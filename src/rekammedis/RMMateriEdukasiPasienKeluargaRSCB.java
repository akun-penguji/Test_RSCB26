package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.File;
import kepegawaian.DlgCariPetugas;
import widget.TextBox;
import widget.panelisi;
import widget.InternalFrame;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.Label;
import widget.ComboBox;
import widget.Tanggal;
import widget.Button;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public final class RMMateriEdukasiPasienKeluargaRSCB extends JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    private InternalFrame internalFrame1;
    private PanelBiasa FormInput;
    private ScrollPane Scroll;
    private JTable tbObat;
    private panelisi panelGlass8, panelGlass9;
    private Label LCount;
    private TextBox TNoRw, TNoRM, TPasien, NIP, NamaPetugas, TCari, PenerimaEdukasi;
    private TextBox MateriEdukasi, KodeLeaflet, LamaEdukasi;
    private ComboBox Jam, Menit, Detik, HasilVerifikasi, Hubungan;
    private Tanggal Tanggal, TglReEdukasi, DTPCari1, DTPCari2;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnAll, BtnCari, BtnKeluar, btnPetugas;
    private Label jLabel4, jLabel16, jLabel18, jLabel19, jLabel21, jLabel6;
    private Label jLabelMateri, jLabelKode, jLabelLama, jLabelHasil, jLabelTglRe, jLabelPenerima, jLabelHubungan;

    private widget.PanelBiasa FormPhoto = new widget.PanelBiasa();
    private widget.CekBox ChkAccor = new widget.CekBox();
    private widget.PanelBiasa PanelAccor = new widget.PanelBiasa();
    private widget.Button btnAmbil = new widget.Button();
    private widget.Button BtnRefreshPhoto1 = new widget.Button();
    private widget.ScrollPane Scroll5 = new widget.ScrollPane();
    private widget.editorpane LoadHTML2 = new widget.editorpane();
    private String lokasifile="";

    public RMMateriEdukasiPasienKeluargaRSCB(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(850, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl Edukasi", "Jam", "Hubungan", "Penerima Edukasi", "Materi Edukasi", 
            "Kode Leaflet", "Lama(Mnt)", "Hasil Verifikasi", "Tgl Re-Edukasi", "NIP", "Edukator"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 12; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(70);
            else if (i == 2) column.setPreferredWidth(150);
            else if (i == 3) column.setPreferredWidth(75);
            else if (i == 4) column.setPreferredWidth(50);
            else if (i == 5) column.setPreferredWidth(200);
            else if (i == 6) column.setPreferredWidth(80);
            else if (i == 7) column.setPreferredWidth(70);
            else if (i == 8) column.setPreferredWidth(120);
            else if (i == 9) column.setPreferredWidth(90);
            else if (i == 10) column.setPreferredWidth(90);
            else if (i == 11) column.setPreferredWidth(150);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte)20).getKata(NIP));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        MateriEdukasi.setDocument(new batasInput((int)100).getKata(MateriEdukasi));
        KodeLeaflet.setDocument(new batasInput((byte)50).getKata(KodeLeaflet));
        LamaEdukasi.setDocument(new batasInput((byte)10).getKata(LamaEdukasi));

        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
            });
        }

        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){
                    NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                }
                NIP.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getData(); }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { 
                if(evt.getKeyCode() == KeyEvent.VK_SPACE) getData();
            }
        });

        BtnSimpan.addActionListener(e -> simpan());
        BtnBatal.addActionListener(e -> emptTeks());
        BtnHapus.addActionListener(e -> hapus());
        BtnEdit.addActionListener(e -> ganti());
        BtnKeluar.addActionListener(e -> dispose());
        BtnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabMode.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Silahkan refresh data..!!");
                    TCari.requestFocus();
                } else if (tbObat.getSelectedRow() <= -1) {
                    JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dicetak..!!");
                } else {
                    Map<String, Object> param = new HashMap<>();
                    param.put("namars", akses.getnamars());
                    param.put("alamatrs", akses.getalamatrs());
                    param.put("kotars", akses.getkabupatenrs());
                    param.put("propinsirs", akses.getpropinsirs());
                    param.put("kontakrs", akses.getkontakrs());
                    param.put("emailrs", akses.getemailrs());
                    param.put("logo", Sequel.cariGambar("select logo from setting"));
                    
                    // photo & tandatangan diambil dari edukasipasien_ttd (tempat web menyimpan:
                    // url_image 'foto/xxx.jpg' & 'ttd/xxx.jpg', file di folder pelaksanaanedukasi/,
                    // sama dengan yang dibangun report). Fallback ke bukti_pelaksanaan_informasi_edukasi
                    // untuk record lama/native. Report membangun URL {webapps}/pelaksanaanedukasi/{photo}.
                    String sql = "select m.*, r.no_rkm_medis, p.nm_pasien, p.jk, p.tgl_lahir, r.umurdaftar, r.sttsumur, r.tgl_registrasi, r.jam_reg, pet.nama as nama_petugas, " +
                                 "coalesce((select url_image from edukasipasien_ttd where no_rawat=m.no_rawat and url_image like 'foto/%' order by tanggal desc, jam desc limit 1),(select photo from bukti_pelaksanaan_informasi_edukasi where no_rawat=m.no_rawat and tanggal=concat(m.tanggal,' ',m.jam) limit 1)) as photo, " +
                                 "coalesce((select url_image from edukasipasien_ttd where no_rawat=m.no_rawat and url_image like 'ttd/%' order by tanggal desc, jam desc limit 1),(select tandatangan from bukti_pelaksanaan_informasi_edukasi where no_rawat=m.no_rawat and tanggal=concat(m.tanggal,' ',m.jam) limit 1)) as tandatangan " +
                                 "from materi_edukasi_pasien_keluarga_rscb m " +
                                 "inner join reg_periksa r on m.no_rawat=r.no_rawat " +
                                 "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                                 "inner join pegawai pet on m.nip=pet.nik " +
                                 "where m.no_rawat='" + tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' " +
                                 "and m.tanggal='" + tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString() + "' " +
                                 "and m.jam='" + tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString() + "' ";
                    
                    Valid.MyReportqry("rptMateriEdukasiPasienKeluargaRSCB.jasper", "report", "::[ Formulir Edukasi Pasien ]::", sql, param);
                }
            }
        });
        BtnCari.addActionListener(e -> tampil());
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        btnPetugas.addActionListener(e -> {
            petugas.isCek();
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1);
            petugas.setVisible(true);
        });

        ChkInput.setSelected(false);
        isForm();

        HTMLEditorKit kit = new HTMLEditorKit();
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
        LoadHTML2.setDocument(doc);

        ChkAccor.setSelected(false);
        isPhoto();
        jam();
    }

    private widget.CekBox ChkKejadian = new widget.CekBox();
    private widget.CekBox ChkInput = new widget.CekBox();

    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 170));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }

    private javax.swing.JPanel PanelInput;

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        FormInput = new PanelBiasa();
        Scroll = new ScrollPane();
        tbObat = new JTable();
        panelGlass8 = new panelisi();
        panelGlass9 = new panelisi();
        LCount = new Label();
        
        TNoRw = new TextBox(); TNoRM = new TextBox(); TPasien = new TextBox(); NIP = new TextBox(); NamaPetugas = new TextBox(); TCari = new TextBox();
        MateriEdukasi = new TextBox(); KodeLeaflet = new TextBox(); LamaEdukasi = new TextBox();
        Tanggal = new Tanggal(); TglReEdukasi = new Tanggal(); DTPCari1 = new Tanggal(); DTPCari2 = new Tanggal();
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        TglReEdukasi.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setDisplayFormat("dd-MM-yyyy");

        PenerimaEdukasi = new TextBox();
        Hubungan = new ComboBox();
        Hubungan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Pasien", "Keluarga", "Lain-lain"}));

        Jam = new ComboBox(); Menit = new ComboBox(); Detik = new ComboBox(); 
        HasilVerifikasi = new ComboBox();
        
        BtnSimpan = new Button(); BtnBatal = new Button(); BtnHapus = new Button(); BtnEdit = new Button(); BtnPrint = new Button(); BtnAll = new Button(); BtnCari = new Button(); BtnKeluar = new Button(); btnPetugas = new Button();
        jLabel4 = new Label(); jLabel16 = new Label(); jLabel18 = new Label(); jLabel19 = new Label(); jLabel21 = new Label(); jLabel6 = new Label();
        jLabelMateri = new Label(); jLabelKode = new Label(); jLabelLama = new Label(); jLabelHasil = new Label(); jLabelTglRe = new Label();
        jLabelPenerima = new Label(); jLabelHubungan = new Label();

        internalFrame1.setLayout(new BorderLayout());
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Materi Edukasi Pasien dan Keluarga Terintegrasi RSCB ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));

        PanelInput = new javax.swing.JPanel();
        PanelInput.setLayout(new BorderLayout(1, 1));
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(850, 200));

        FormInput.setPreferredSize(new Dimension(850, 160));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :"); jLabel4.setBounds(0, 30, 80, 23); FormInput.add(jLabel4);
        TNoRw.setBounds(84, 30, 136, 23); FormInput.add(TNoRw);
        TNoRM.setEditable(false); TNoRM.setBounds(222, 30, 100, 23); FormInput.add(TNoRM);
        TPasien.setEditable(false); TPasien.setBounds(324, 30, 250, 23); FormInput.add(TPasien);
        
        jLabel16.setText("Tgl Edukasi :"); jLabel16.setBounds(0, 60, 80, 23); FormInput.add(jLabel16);
        Tanggal.setBounds(84, 60, 90, 23); FormInput.add(Tanggal);
        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"})); Jam.setBounds(178, 60, 50, 23); FormInput.add(Jam);
        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"})); Menit.setBounds(230, 60, 50, 23); FormInput.add(Menit);
        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"})); Detik.setBounds(282, 60, 50, 23); FormInput.add(Detik);
        ChkKejadian.setBorder(null); ChkKejadian.setSelected(true); ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER); ChkKejadian.setName("ChkKejadian"); FormInput.add(ChkKejadian); ChkKejadian.setBounds(335, 60, 23, 23);
        
        jLabel18.setText("Edukator :"); jLabel18.setBounds(362, 60, 60, 23); FormInput.add(jLabel18);
        NIP.setEditable(false); NIP.setBounds(424, 60, 100, 23); FormInput.add(NIP);
        NamaPetugas.setEditable(false); NamaPetugas.setBounds(526, 60, 160, 23); FormInput.add(NamaPetugas);
        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); btnPetugas.setBounds(688, 60, 28, 23); FormInput.add(btnPetugas);

        jLabelHubungan.setText("Hubungan :"); jLabelHubungan.setBounds(0, 90, 80, 23); FormInput.add(jLabelHubungan);
        Hubungan.setBounds(84, 90, 110, 23); FormInput.add(Hubungan);
        jLabelPenerima.setText("Penerima Edukasi :"); jLabelPenerima.setBounds(200, 90, 110, 23); FormInput.add(jLabelPenerima);
        PenerimaEdukasi.setBounds(314, 90, 260, 23); FormInput.add(PenerimaEdukasi);

        jLabelMateri.setText("Materi Edukasi :"); jLabelMateri.setBounds(0, 120, 100, 23); FormInput.add(jLabelMateri);
        MateriEdukasi.setBounds(104, 120, 300, 23); FormInput.add(MateriEdukasi);

        jLabelKode.setText("Kode Leaflet :"); jLabelKode.setBounds(410, 120, 80, 23); FormInput.add(jLabelKode);
        KodeLeaflet.setBounds(494, 120, 100, 23); FormInput.add(KodeLeaflet);

        jLabelLama.setText("Lama(Mnt) :"); jLabelLama.setBounds(600, 120, 70, 23); FormInput.add(jLabelLama);
        LamaEdukasi.setBounds(674, 120, 50, 23); FormInput.add(LamaEdukasi);

        jLabelHasil.setText("Hasil Verifikasi :"); jLabelHasil.setBounds(0, 150, 100, 23); FormInput.add(jLabelHasil);
        HasilVerifikasi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Sudah Mengerti", "Re - Edukasi", "Re - Demonstrasi"})); HasilVerifikasi.setBounds(104, 150, 130, 23); FormInput.add(HasilVerifikasi);

        jLabelTglRe.setText("Tgl Re-Edukasi :"); jLabelTglRe.setBounds(240, 150, 100, 23); FormInput.add(jLabelTglRe);
        TglReEdukasi.setBounds(344, 150, 90, 23); FormInput.add(TglReEdukasi);

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isForm();
            }
        });

        PanelInput.add(FormInput, BorderLayout.CENTER);
        PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        Scroll.setViewportView(tbObat);
        internalFrame1.add(PanelInput, BorderLayout.PAGE_START);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor");
        PanelAccor.setPreferredSize(new java.awt.Dimension(250, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout(1, 1));

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkAccor.setMnemonic('S');
        ChkAccor.setText(".: Photo Pembuat Pernyataan");
        ChkAccor.setToolTipText("Alt+S");
        ChkAccor.setBorderPainted(true);
        ChkAccor.setBorderPaintedFlat(true);
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkAccor.setName("ChkAccor");
        ChkAccor.setPreferredSize(new java.awt.Dimension(22, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkAccor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isPhoto();
            }
        });
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.PAGE_START);

        FormPhoto.setBackground(new java.awt.Color(255, 255, 255));
        FormPhoto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), " Bukti Pengambilan Photo & Tanda Tangan : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        FormPhoto.setName("FormPhoto");
        FormPhoto.setPreferredSize(new java.awt.Dimension(115, 73));
        FormPhoto.setLayout(new java.awt.BorderLayout());

        widget.PanelBiasa FormPass3 = new widget.PanelBiasa();
        FormPass3.setBackground(new java.awt.Color(255, 255, 255));
        FormPass3.setBorder(null);
        FormPass3.setName("FormPass3");
        FormPass3.setPreferredSize(new java.awt.Dimension(115, 40));
        FormPass3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 9));

        btnAmbil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png")));
        btnAmbil.setMnemonic('U');
        btnAmbil.setText("Ambil");
        btnAmbil.setToolTipText("Alt+U");
        btnAmbil.setName("btnAmbil");
        btnAmbil.setPreferredSize(new java.awt.Dimension(100, 30));
        btnAmbil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmbilActionPerformed(evt);
            }
        });
        FormPass3.add(btnAmbil);

        BtnRefreshPhoto1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        BtnRefreshPhoto1.setMnemonic('U');
        BtnRefreshPhoto1.setText("Refresh");
        BtnRefreshPhoto1.setToolTipText("Alt+U");
        BtnRefreshPhoto1.setName("BtnRefreshPhoto1");
        BtnRefreshPhoto1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnRefreshPhoto1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panggilPhoto();
            }
        });
        FormPass3.add(BtnRefreshPhoto1);

        FormPhoto.add(FormPass3, java.awt.BorderLayout.PAGE_END);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll5.setName("Scroll5");
        Scroll5.setOpaque(true);
        Scroll5.setPreferredSize(new java.awt.Dimension(200, 200));

        LoadHTML2.setBorder(null);
        LoadHTML2.setName("LoadHTML2");
        Scroll5.setViewportView(LoadHTML2);

        FormPhoto.add(Scroll5, java.awt.BorderLayout.CENTER);

        PanelAccor.add(FormPhoto, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.LINE_END);

        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        jPanel3.setLayout(new BorderLayout());

        panelGlass8.setPreferredSize(new Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30)); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); panelGlass8.add(BtnSimpan);
        BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30)); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); panelGlass8.add(BtnBatal);
        BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30)); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); panelGlass8.add(BtnHapus);
        BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30)); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); panelGlass8.add(BtnEdit);
        BtnPrint.setText("Print"); BtnPrint.setPreferredSize(new Dimension(100, 30)); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); BtnPrint.setVisible(true); panelGlass8.add(BtnPrint);
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30)); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); panelGlass8.add(BtnKeluar);
        
        panelGlass9.setPreferredSize(new Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        jLabel19.setText("Tanggal :"); jLabel19.setPreferredSize(new Dimension(67, 23)); panelGlass9.add(jLabel19);
        DTPCari1.setPreferredSize(new Dimension(90, 23)); panelGlass9.add(DTPCari1);
        jLabel21.setText("s.d."); jLabel21.setPreferredSize(new Dimension(23, 23)); panelGlass9.add(jLabel21);
        DTPCari2.setPreferredSize(new Dimension(90, 23)); panelGlass9.add(DTPCari2);
        jLabel6.setText("Key Word :"); jLabel6.setPreferredSize(new Dimension(70, 23)); panelGlass9.add(jLabel6);
        TCari.setPreferredSize(new Dimension(190, 23)); panelGlass9.add(TCari);
        BtnCari.setPreferredSize(new Dimension(26, 23)); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); panelGlass9.add(BtnCari);
        BtnAll.setPreferredSize(new Dimension(26, 23)); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); panelGlass9.add(BtnAll);
        LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23)); panelGlass9.add(LCount);

        jPanel3.add(panelGlass8, BorderLayout.CENTER);
        jPanel3.add(panelGlass9, BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, BorderLayout.CENTER);
        pack();
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                "select p.no_rkm_medis, p.nm_pasien, r.tgl_registrasi " +
                "from reg_periksa r inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "where r.no_rawat=?"
            );
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        BtnHapus.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        BtnEdit.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        BtnPrint.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false);
            btnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?", NamaPetugas, NIP.getText());
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select m.*, r.no_rkm_medis, p.nm_pasien, pet.nama " +
                "from materi_edukasi_pasien_keluarga_rscb m " +
                "inner join reg_periksa r on m.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "inner join pegawai pet on m.nip=pet.nik " +
                "where m.tanggal between ? and ? and (m.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) order by m.tanggal, m.jam"
            );
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), 
                    rs.getString("tanggal"), rs.getString("jam"), rs.getString("diberikan_pada"), 
                    rs.getString("penerima_edukasi"), rs.getString("materi_edukasi"),
                    rs.getString("kode_leaflet"), rs.getString("lama_edukasi"), 
                    rs.getString("hasil_verifikasi"), rs.getString("tgl_re_edukasi"),
                    rs.getString("nip"), rs.getString("nama")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        } finally {
            try { if(rs!=null) rs.close(); if(ps!=null) ps.close(); } catch(Exception e) {}
        }
        LCount.setText("Record : " + tabMode.getRowCount());
    }

    public void emptTeks() {
        PenerimaEdukasi.setText("");
        Hubungan.setSelectedIndex(0);
        MateriEdukasi.setText("");
        KodeLeaflet.setText("");
        LamaEdukasi.setText("");
        HasilVerifikasi.setSelectedIndex(0);
        Tanggal.setDate(new Date());
        TglReEdukasi.setDate(new Date());
        PenerimaEdukasi.requestFocus();
    }

    private void getData() {
        int row = tbObat.getSelectedRow();
        if (row != -1) {
            TNoRw.setText(tabMode.getValueAt(row, 0).toString());
            TNoRM.setText(tabMode.getValueAt(row, 1).toString());
            TPasien.setText(tabMode.getValueAt(row, 2).toString());
            Valid.SetTgl(Tanggal, tabMode.getValueAt(row, 3).toString());
            Jam.setSelectedItem(tabMode.getValueAt(row, 4).toString().substring(0,2));
            Menit.setSelectedItem(tabMode.getValueAt(row, 4).toString().substring(3,5));
            Detik.setSelectedItem(tabMode.getValueAt(row, 4).toString().substring(6,8));
            Hubungan.setSelectedItem(tabMode.getValueAt(row, 5).toString());
            PenerimaEdukasi.setText(tabMode.getValueAt(row, 6).toString());
            MateriEdukasi.setText(tabMode.getValueAt(row, 7).toString());
            KodeLeaflet.setText(tabMode.getValueAt(row, 8).toString());
            LamaEdukasi.setText(tabMode.getValueAt(row, 9).toString());
            HasilVerifikasi.setSelectedItem(tabMode.getValueAt(row, 10).toString());
            Valid.SetTgl(TglReEdukasi, tabMode.getValueAt(row, 11).toString());
            NIP.setText(tabMode.getValueAt(row, 12).toString());
            NamaPetugas.setText(tabMode.getValueAt(row, 13).toString());
            panggilPhoto();
        }
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (PenerimaEdukasi.getText().trim().equals("")) {
            Valid.textKosong(PenerimaEdukasi, "Penerima Edukasi");
        } else if (MateriEdukasi.getText().trim().equals("")) {
            Valid.textKosong(MateriEdukasi, "Materi Edukasi");
        } else if (NIP.getText().trim().equals("") || NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Edukator");
        } else {
            if (Sequel.menyimpantf("materi_edukasi_pasien_keluarga_rscb", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                Hubungan.getSelectedItem().toString(), PenerimaEdukasi.getText(),
                MateriEdukasi.getText(), KodeLeaflet.getText(), LamaEdukasi.getText(), HasilVerifikasi.getSelectedItem().toString(), 
                Valid.SetTgl(TglReEdukasi.getSelectedItem() + ""), NIP.getText()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void ganti() {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (PenerimaEdukasi.getText().trim().equals("")) {
            Valid.textKosong(PenerimaEdukasi, "Penerima Edukasi");
        } else if (MateriEdukasi.getText().trim().equals("")) {
            Valid.textKosong(MateriEdukasi, "Materi Edukasi");
        } else if (NIP.getText().trim().equals("") || NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Edukator");
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.mengedittf("materi_edukasi_pasien_keluarga_rscb", "no_rawat=? and tanggal=? and jam=?", "no_rawat=?,tanggal=?,jam=?,diberikan_pada=?,penerima_edukasi=?,materi_edukasi=?,kode_leaflet=?,lama_edukasi=?,hasil_verifikasi=?,tgl_re_edukasi=?,nip=?", 14, new String[]{
                    TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                    Hubungan.getSelectedItem().toString(), PenerimaEdukasi.getText(),
                    MateriEdukasi.getText(), KodeLeaflet.getText(), LamaEdukasi.getText(), HasilVerifikasi.getSelectedItem().toString(), 
                    Valid.SetTgl(TglReEdukasi.getSelectedItem() + ""), NIP.getText(),
                    tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString(), tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString(), tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString()
                }) == true) {
                    tampil();
                    emptTeks();
                }
            }
        }
    }

    private void hapus() {
        if (tbObat.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from materi_edukasi_pasien_keluarga_rscb where no_rawat=? and tanggal=? and jam=?", 3, new String[]{
                tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString(), tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString(), tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString()
            }) == true) {
                tampil();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data..!!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data terlebih dahulu..!!");
        }
    }

    private void btnAmbilActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() > -1) {
                Sequel.queryu("delete from antripelaksanaanedukasi");
                String tglVal = tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString();
                String jamVal = tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString();
                Sequel.queryu("insert into antripelaksanaanedukasi(no_rawat,tanggal) values('" + tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString() + "','" + tglVal + " " + jamVal + "')");
                JOptionPane.showMessageDialog(rootPane, "Data antrian sudah disiapkan.\nSilahkan buka browser dan akses halaman Pelaksanaan Edukasi untuk mengambil photo & tanda tangan.");
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                Date now = Calendar.getInstance().getTime();
                if (ChkKejadian.isSelected() == true) {
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                } else {
                    nilai_jam = Jam.getSelectedIndex();
                    nilai_menit = Menit.getSelectedIndex();
                    nilai_detik = Detik.getSelectedIndex();
                }
                if (nilai_jam <= 9) { nol_jam = "0"; }
                if (nilai_menit <= 9) { nol_menit = "0"; }
                if (nilai_detik <= 9) { nol_detik = "0"; }
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                Jam.setSelectedItem(jam);
                Menit.setSelectedItem(menit);
                Detik.setSelectedItem(detik);
            }
        };
        new Timer(1000, taskPerformer).start();
    }

    private void isPhoto() {
        if (ChkAccor.isSelected() == true) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new java.awt.Dimension(250, 43));
            FormPhoto.setVisible(true);
            ChkAccor.setVisible(true);
            panggilPhoto();
        } else if (ChkAccor.isSelected() == false) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new java.awt.Dimension(22, 43));
            FormPhoto.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }

    private void panggilPhoto() {
        if (FormPhoto.isVisible() == true) {
            lokasifile = "";
            try {
                // Fetch photo path from DB
                ps = koneksi.prepareStatement("select photo, tandatangan from bukti_pelaksanaan_informasi_edukasi where no_rawat=? and tanggal=?");
                try {
                    if (tbObat.getSelectedRow() > -1) {
                        ps.setString(1, tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString());
                        ps.setString(2, tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString() + " " + tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            String photo = rs.getString("photo");
                            String signature = rs.getString("tandatangan");
                            String html = "<html><body><center>";

                            if (photo.equals("") || photo.equals("-")) {
                                html += "<img src='file:./picture/blank.png' width='200' height='200' />";
                            } else {
                                html += "<img src='http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/pelaksanaanedukasi/" + photo + "' width='200' height='200' />";
                            }

                            html += "<br>Tanda Tangan:<br>";
                            if (signature == null || signature.equals("") || signature.equals("-")) {
                                html += "<img src='file:./picture/blank.png' width='200' height='100' />";
                            } else {
                                html += "<img src='http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/pelaksanaanedukasi/" + signature + "' width='200' height='100' />";
                            }

                            html += "</center></body></html>";
                            LoadHTML2.setText(html);
                        } else {
                            LoadHTML2.setText("<html><body><center><img src='file:./picture/blank.png' width='200' height='200' /></center></body></html>");
                        }
                    }
                } finally {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }
}
