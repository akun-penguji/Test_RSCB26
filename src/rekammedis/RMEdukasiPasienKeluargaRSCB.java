package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import widget.Button;
import widget.CekBox;
import widget.ComboBox;
import widget.InternalFrame;
import widget.Label;
import widget.PanelBiasa;
import widget.panelisi;
import widget.ScrollPane;
import widget.Tanggal;
import widget.TextBox;

public final class RMEdukasiPasienKeluargaRSCB extends JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private String finger = "";
    private StringBuilder htmlContent;

    private InternalFrame internalFrame1;
    private PanelBiasa FormInput;
    private ScrollPane Scroll;
    private JTable tbObat;
    private panelisi panelGlass8;
    private Label LCount;
    private TextBox TNoRw, TNoRM, TPasien, TglLahir, JK, NIP, NamaPetugas, TCari;
    private Tanggal Tanggal, DTPCari1, DTPCari2;
    private ComboBox Jam, Menit, Detik, TinggalBersama, Hambatan, EdukasiKepada, KemampuanBahasa, PerluPenerjemah, BacaTulis, KesediaanMenerimaEdukasi, CaraEdukasi;
    private CekBox ChkKejadian, HambatanPendengaran, HambatanEmosi, HambatanPenglihatan, HambatanBicara, MotivasiKurang, MemoriHilang, FisikLemah, Alkoholik, TidakMampuBelajar, Perokok;
    private CekBox KebPartisipasi, KebPenunjang, KebKondisi, KebInformedConcent, KebDiet, KebObat, KebAlatMedis, KebNyeri, KebRehabilitasi, KebCuciTangan, KebMerokok, KebRujukan;
    private TextBox BahasaDaerah, BahasaAsing, KepercayaanLainnya, KebLainLain;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnAll, BtnCari, BtnKeluar, btnPetugas;
    private Label jLabel4, jLabel16, jLabel18, jLabel8, jLabelTinggal, jLabelHambatan, jLabelJikaAda, jLabelEdukasiKepada, jLabelKemampuanBahasa, jLabelPerluPenerjemah, jLabelBacaTulis, jLabelKepercayaanLainnya, jLabelKesediaanMenerimaEdukasi, jLabelCaraEdukasi, jLabelKebutuhanEdukasi, jLabelKebLainLain, jLabel19, jLabel21, jLabel6;

    public RMEdukasiPasienKeluargaRSCB(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(850, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Tinggal Bersama", "Hambatan", "Gnggn Pendengaran", "Gnggn Emosi", "Gnggn Penglihatan", "Gnggn Bicara", "Motivasi Kurang", "Memori Hilang", "Fisik Lemah", "Alkoholik", "Tdk Mampu Belajar", "Perokok", "Edukasi Kpd", "Bhs", "Daerah", "Asing", "Penerjemah", "Baca Tulis", "Kepercayaan", "Sedia Edukasi", "Cara Edukasi", "Partisipasi", "Pemeriksaan", "Kondisi Kesehatan", "Informed Concent", "Diet", "Obat", "Alat Medis", "Manajemen Nyeri", "Rehabilitasi", "Cuci Tangan", "Merokok", "Lain-lain", "Rujukan", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 42; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(65);
            else if (i == 2) column.setPreferredWidth(160);
            else if (i == 3) column.setPreferredWidth(65);
            else if (i == 4) column.setPreferredWidth(25);
            else if (i == 5) column.setPreferredWidth(122);
            else column.setPreferredWidth(100);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        btnPetugas.addActionListener(e -> {
            petugas.emptTeks();
            petugas.isCek();
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1);
            petugas.setVisible(true);
        });

        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        BtnSimpan.addActionListener(e -> simpan());
        BtnBatal.addActionListener(e -> emptTeks());
        BtnHapus.addActionListener(e -> hapus());
        BtnEdit.addActionListener(e -> ganti());
        BtnKeluar.addActionListener(e -> dispose());
        BtnCari.addActionListener(e -> tampil());
        BtnAll.addActionListener(e -> {
            TCari.setText("");
            tampil();
        });

        tbObat.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) { if(tabMode.getRowCount()!=0) getData(); }
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
    }
    
    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                "select p.no_rkm_medis, p.nm_pasien, p.jk, p.tgl_lahir, r.tgl_registrasi " +
                "from reg_periksa r inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "where r.no_rawat=?"
            );
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    JK.setText(rs.getString("jk"));
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

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        FormInput = new PanelBiasa();
        Scroll = new ScrollPane();
        tbObat = new JTable();
        panelGlass8 = new panelisi();
        LCount = new Label();
        jLabel19 = new Label(); jLabel21 = new Label(); jLabel6 = new Label();
        
        TNoRw = new TextBox(); TNoRM = new TextBox(); TPasien = new TextBox(); TglLahir = new TextBox(); JK = new TextBox(); NIP = new TextBox(); NamaPetugas = new TextBox(); TCari = new TextBox();
        Tanggal = new Tanggal(); DTPCari1 = new Tanggal(); DTPCari2 = new Tanggal();
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        Jam = new ComboBox(); Menit = new ComboBox(); Detik = new ComboBox(); TinggalBersama = new ComboBox(); Hambatan = new ComboBox(); EdukasiKepada = new ComboBox(); KemampuanBahasa = new ComboBox(); PerluPenerjemah = new ComboBox(); BacaTulis = new ComboBox(); KesediaanMenerimaEdukasi = new ComboBox(); CaraEdukasi = new ComboBox();
        ChkKejadian = new CekBox(); HambatanPendengaran = new CekBox(); HambatanEmosi = new CekBox(); HambatanPenglihatan = new CekBox(); HambatanBicara = new CekBox(); MotivasiKurang = new CekBox(); MemoriHilang = new CekBox(); FisikLemah = new CekBox(); Alkoholik = new CekBox(); TidakMampuBelajar = new CekBox(); Perokok = new CekBox();
        KebPartisipasi = new CekBox(); KebPenunjang = new CekBox(); KebKondisi = new CekBox(); KebInformedConcent = new CekBox(); KebDiet = new CekBox(); KebObat = new CekBox(); KebAlatMedis = new CekBox(); KebNyeri = new CekBox(); KebRehabilitasi = new CekBox(); KebCuciTangan = new CekBox(); KebMerokok = new CekBox(); KebRujukan = new CekBox();
        BahasaDaerah = new TextBox(); BahasaAsing = new TextBox(); KepercayaanLainnya = new TextBox(); KebLainLain = new TextBox();
        BtnSimpan = new Button(); BtnBatal = new Button(); BtnHapus = new Button(); BtnEdit = new Button(); BtnPrint = new Button(); BtnAll = new Button(); BtnCari = new Button(); BtnKeluar = new Button(); btnPetugas = new Button();
        jLabel4 = new Label(); jLabel16 = new Label(); jLabel18 = new Label(); jLabel8 = new Label(); jLabelTinggal = new Label(); jLabelHambatan = new Label(); jLabelJikaAda = new Label(); jLabelEdukasiKepada = new Label(); jLabelKemampuanBahasa = new Label(); jLabelPerluPenerjemah = new Label(); jLabelBacaTulis = new Label(); jLabelKepercayaanLainnya = new Label(); jLabelKesediaanMenerimaEdukasi = new Label(); jLabelCaraEdukasi = new Label(); jLabelKebutuhanEdukasi = new Label(); jLabelKebLainLain = new Label();
        jLabel19 = new Label(); jLabel21 = new Label(); jLabel6 = new Label();

        internalFrame1.setLayout(new BorderLayout());
        FormInput.setPreferredSize(new Dimension(850, 550));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :"); jLabel4.setBounds(0, 10, 80, 23); FormInput.add(jLabel4);
        TNoRw.setBounds(84, 10, 136, 23); FormInput.add(TNoRw);
        TNoRM.setEditable(false); TNoRM.setBounds(222, 10, 100, 23); FormInput.add(TNoRM);
        TPasien.setEditable(false); TPasien.setBounds(324, 10, 250, 23); FormInput.add(TPasien);
        jLabel8.setText("Tgl.Lahir :"); jLabel8.setBounds(580, 10, 60, 23); FormInput.add(jLabel8);
        TglLahir.setEditable(false); TglLahir.setBounds(644, 10, 100, 23); FormInput.add(TglLahir);
        
        jLabel16.setText("Tanggal :"); jLabel16.setBounds(0, 40, 80, 23); FormInput.add(jLabel16);
        Tanggal.setBounds(84, 40, 90, 23); FormInput.add(Tanggal);
        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"})); Jam.setBounds(178, 40, 50, 23); FormInput.add(Jam);
        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"})); Menit.setBounds(230, 40, 50, 23); FormInput.add(Menit);
        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"})); Detik.setBounds(282, 40, 50, 23); FormInput.add(Detik);
        
        jLabel18.setText("Petugas :"); jLabel18.setBounds(340, 40, 60, 23); FormInput.add(jLabel18);
        NIP.setEditable(false); NIP.setBounds(404, 40, 100, 23); FormInput.add(NIP);
        NamaPetugas.setEditable(false); NamaPetugas.setBounds(506, 40, 180, 23); FormInput.add(NamaPetugas);
        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); btnPetugas.setBounds(688, 40, 28, 23); FormInput.add(btnPetugas);

        jLabelTinggal.setText("Tinggal Bersama :"); jLabelTinggal.setBounds(0, 70, 110, 23); FormInput.add(jLabelTinggal);
        TinggalBersama.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Anak", "Orang Tua", "Sendiri", "Suami/Istri"})); TinggalBersama.setBounds(114, 70, 100, 23); FormInput.add(TinggalBersama);
        jLabelHambatan.setText("Hambatan :"); jLabelHambatan.setBounds(220, 70, 70, 23); FormInput.add(jLabelHambatan);
        Hambatan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ada", "Tidak"})); Hambatan.setBounds(294, 70, 70, 23); FormInput.add(Hambatan);

        jLabelJikaAda.setText("(Jika Ada) :"); jLabelJikaAda.setBounds(0, 100, 110, 23); FormInput.add(jLabelJikaAda);
        HambatanPendengaran.setText("Gangguan Pendengaran"); HambatanPendengaran.setBounds(114, 100, 150, 23); FormInput.add(HambatanPendengaran);
        HambatanEmosi.setText("Gangguan Emosi"); HambatanEmosi.setBounds(270, 100, 120, 23); FormInput.add(HambatanEmosi);
        HambatanPenglihatan.setText("Gangguan Penglihatan"); HambatanPenglihatan.setBounds(400, 100, 150, 23); FormInput.add(HambatanPenglihatan);
        HambatanBicara.setText("Gangguan Bicara"); HambatanBicara.setBounds(560, 100, 120, 23); FormInput.add(HambatanBicara);
        
        MotivasiKurang.setText("Motivasi kurang"); MotivasiKurang.setBounds(114, 130, 150, 23); FormInput.add(MotivasiKurang);
        MemoriHilang.setText("Memori hilang"); MemoriHilang.setBounds(270, 130, 120, 23); FormInput.add(MemoriHilang);
        FisikLemah.setText("Fisik Lemah"); FisikLemah.setBounds(400, 130, 150, 23); FormInput.add(FisikLemah);
        Alkoholik.setText("Alkoholik"); Alkoholik.setBounds(560, 130, 120, 23); FormInput.add(Alkoholik);
        TidakMampuBelajar.setText("Tidak mampu belajar"); TidakMampuBelajar.setBounds(114, 160, 180, 23); FormInput.add(TidakMampuBelajar);
        Perokok.setText("Perokok"); Perokok.setBounds(300, 160, 100, 23); FormInput.add(Perokok);

        jLabelEdukasiKepada.setText("Edukasi diberikan kepada :"); jLabelEdukasiKepada.setBounds(0, 190, 150, 23); FormInput.add(jLabelEdukasiKepada);
        EdukasiKepada.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Pasien",
            "Orang Tua (Ayah)",
            "Orang Tua (Ibu)",
            "Keluarga (Suami)",
            "Keluarga (Istri)",
            "Keluarga (Kakak)",
            "Keluarga (Adik)"
        })); EdukasiKepada.setBounds(154, 190, 200, 23); FormInput.add(EdukasiKepada);

        jLabelKemampuanBahasa.setText("Kemampuan Bahasa :"); jLabelKemampuanBahasa.setBounds(0, 220, 120, 23); FormInput.add(jLabelKemampuanBahasa);
        KemampuanBahasa.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Indonesia", "Daerah", "Asing"})); KemampuanBahasa.setBounds(124, 220, 90, 23); FormInput.add(KemampuanBahasa);
        BahasaDaerah.setToolTipText("Bahasa Daerah"); BahasaDaerah.setBounds(220, 220, 150, 23); FormInput.add(BahasaDaerah);
        BahasaAsing.setToolTipText("Bahasa Asing"); BahasaAsing.setBounds(380, 220, 150, 23); FormInput.add(BahasaAsing);

        jLabelPerluPenerjemah.setText("Perlu Penerjemah :"); jLabelPerluPenerjemah.setBounds(0, 250, 110, 23); FormInput.add(jLabelPerluPenerjemah);
        PerluPenerjemah.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"})); PerluPenerjemah.setBounds(114, 250, 70, 23); FormInput.add(PerluPenerjemah);
        jLabelBacaTulis.setText("Baca & Tulis :"); jLabelBacaTulis.setBounds(200, 250, 80, 23); FormInput.add(jLabelBacaTulis);
        BacaTulis.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Bisa", "Tidak"})); BacaTulis.setBounds(284, 250, 70, 23); FormInput.add(BacaTulis);

        jLabelKepercayaanLainnya.setText("Kepercayaan Lainnya :"); jLabelKepercayaanLainnya.setBounds(0, 280, 120, 23); FormInput.add(jLabelKepercayaanLainnya);
        KepercayaanLainnya.setBounds(124, 280, 250, 23); FormInput.add(KepercayaanLainnya);

        jLabelKesediaanMenerimaEdukasi.setText("Kesediaan Menerima Edukasi :"); jLabelKesediaanMenerimaEdukasi.setBounds(0, 310, 170, 23); FormInput.add(jLabelKesediaanMenerimaEdukasi);
        KesediaanMenerimaEdukasi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"})); KesediaanMenerimaEdukasi.setBounds(124, 310, 70, 23); FormInput.add(KesediaanMenerimaEdukasi);
        jLabelCaraEdukasi.setText("Cara Edukasi :"); jLabelCaraEdukasi.setBounds(210, 310, 80, 23); FormInput.add(jLabelCaraEdukasi);
        CaraEdukasi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Lisan", "Tulisan"})); CaraEdukasi.setBounds(294, 310, 80, 23); FormInput.add(CaraEdukasi);

        jLabelKebutuhanEdukasi.setText("Kebutuhan Edukasi :"); jLabelKebutuhanEdukasi.setBounds(10, 340, 120, 23); FormInput.add(jLabelKebutuhanEdukasi);
        KebPartisipasi.setText("Hak Berpartisipasi"); KebPartisipasi.setBounds(114, 370, 200, 23); FormInput.add(KebPartisipasi);
        KebPenunjang.setText("Proses Penunjang"); KebPenunjang.setBounds(320, 370, 200, 23); FormInput.add(KebPenunjang);
        KebKondisi.setText("Kondisi & Diagnosis"); KebKondisi.setBounds(530, 370, 200, 23); FormInput.add(KebKondisi);
        KebInformedConcent.setText("Informed Concent"); KebInformedConcent.setBounds(114, 400, 200, 23); FormInput.add(KebInformedConcent);
        KebDiet.setText("Diet dan Nutrisi"); KebDiet.setBounds(320, 400, 200, 23); FormInput.add(KebDiet);
        KebObat.setText("Penggunaan Obat"); KebObat.setBounds(530, 400, 200, 23); FormInput.add(KebObat);
        KebAlatMedis.setText("Penggunaan Alat Medis"); KebAlatMedis.setBounds(114, 430, 200, 23); FormInput.add(KebAlatMedis);
        KebNyeri.setText("Manajemen Nyeri"); KebNyeri.setBounds(320, 430, 200, 23); FormInput.add(KebNyeri);
        KebRehabilitasi.setText("Teknik Rehabilitasi"); KebRehabilitasi.setBounds(530, 430, 200, 23); FormInput.add(KebRehabilitasi);
        KebCuciTangan.setText("Cuci Tangan"); KebCuciTangan.setBounds(114, 460, 200, 23); FormInput.add(KebCuciTangan);
        KebMerokok.setText("Bahaya Merokok"); KebMerokok.setBounds(320, 460, 200, 23); FormInput.add(KebMerokok);
        jLabelKebLainLain.setText("Lain-lain :"); jLabelKebLainLain.setBounds(530, 460, 60, 23); FormInput.add(jLabelKebLainLain);
        KebLainLain.setBounds(594, 460, 140, 23); FormInput.add(KebLainLain);
        KebRujukan.setText("Rujukan Edukasi"); KebRujukan.setBounds(114, 490, 200, 23); FormInput.add(KebRujukan);

        Scroll.setViewportView(tbObat);
        internalFrame1.add(FormInput, BorderLayout.PAGE_START);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        jPanel3.setLayout(new BorderLayout());

        panelGlass8.setPreferredSize(new Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30)); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); panelGlass8.add(BtnSimpan);
        BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30)); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); panelGlass8.add(BtnBatal);
        BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30)); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); panelGlass8.add(BtnHapus);
        BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30)); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); panelGlass8.add(BtnEdit);
        BtnPrint.setText("Print"); BtnPrint.setPreferredSize(new Dimension(100, 30)); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); panelGlass8.add(BtnPrint);
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30)); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); panelGlass8.add(BtnKeluar);
        
        panelisi panelGlass9 = new panelisi();
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

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select p.*, r.no_rkm_medis, m.nm_pasien, m.tgl_lahir, m.jk, pet.nama " +
                "from edukasi_pasien_keluarga_rscb p " +
                "inner join reg_periksa r on p.no_rawat=r.no_rawat " +
                "inner join pasien m on r.no_rkm_medis=m.no_rkm_medis " +
                "inner join petugas pet on p.nip=pet.nip " +
                "where p.tanggal between ? and ? and (p.no_rawat like ? or r.no_rkm_medis like ? or m.nm_pasien like ?) order by p.tanggal"
            );
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("tgl_lahir"), rs.getString("jk"), rs.getString("tanggal"),
                    rs.getString("tinggal_bersama"), rs.getString("hambatan"), rs.getString("hambatan_pendengaran"), rs.getString("hambatan_emosi"), rs.getString("hambatan_penglihatan"), rs.getString("hambatan_bicara"),
                    rs.getString("motivasi_kurang"), rs.getString("memori_hilang"), rs.getString("fisik_lemah"), rs.getString("alkoholik"), rs.getString("tidak_mampu_belajar"), rs.getString("perokok"),
                    rs.getString("edukasi_kepada"), rs.getString("kemampuan_bahasa"), rs.getString("bahasa_daerah"), rs.getString("bahasa_asing"), rs.getString("perlu_penerjemah"), rs.getString("baca_tulis"),
                    rs.getString("kepercayaan_lainnya"), rs.getString("kesediaan_menerima_edukasi"), rs.getString("cara_edukasi"),
                    rs.getString("keb_partisipasi"), rs.getString("keb_penunjang"), rs.getString("keb_kondisi"), rs.getString("keb_informed_concent"), rs.getString("keb_diet"), rs.getString("keb_obat"),
                    rs.getString("keb_alat_medis"), rs.getString("keb_nyeri"), rs.getString("keb_rehabilitasi"), rs.getString("keb_cuci_tangan"), rs.getString("keb_merokok"), rs.getString("keb_lain_lain"), rs.getString("keb_rujukan"),
                    rs.getString("nip"), rs.getString("nama")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        } finally {
            try { if(rs!=null) rs.close(); if(ps!=null) ps.close(); } catch(Exception e) {}
        }
    }

    public void emptTeks() {
        TinggalBersama.setSelectedIndex(0);
        Hambatan.setSelectedIndex(0);
        HambatanPendengaran.setSelected(false); HambatanEmosi.setSelected(false); HambatanPenglihatan.setSelected(false); HambatanBicara.setSelected(false);
        MotivasiKurang.setSelected(false); MemoriHilang.setSelected(false); FisikLemah.setSelected(false); Alkoholik.setSelected(false);
        TidakMampuBelajar.setSelected(false); Perokok.setSelected(false);
        EdukasiKepada.setSelectedIndex(0); KemampuanBahasa.setSelectedIndex(0);
        BahasaDaerah.setText(""); BahasaAsing.setText(""); PerluPenerjemah.setSelectedIndex(0); BacaTulis.setSelectedIndex(0);
        KepercayaanLainnya.setText(""); KesediaanMenerimaEdukasi.setSelectedIndex(0); CaraEdukasi.setSelectedIndex(0);
        KebPartisipasi.setSelected(false); KebPenunjang.setSelected(false); KebKondisi.setSelected(false);
        KebInformedConcent.setSelected(false); KebDiet.setSelected(false); KebObat.setSelected(false);
        KebAlatMedis.setSelected(false); KebNyeri.setSelected(false); KebRehabilitasi.setSelected(false);
        KebCuciTangan.setSelected(false); KebMerokok.setSelected(false); KebLainLain.setText(""); KebRujukan.setSelected(false);
        Tanggal.setDate(new Date()); TinggalBersama.requestFocus();
    }

    private void getData() {
        int row = tbObat.getSelectedRow();
        if (row != -1) {
            TNoRw.setText(tabMode.getValueAt(row, 0).toString());
            TNoRM.setText(tabMode.getValueAt(row, 1).toString());
            TPasien.setText(tabMode.getValueAt(row, 2).toString());
            TglLahir.setText(tabMode.getValueAt(row, 3).toString());
            JK.setText(tabMode.getValueAt(row, 4).toString());
            Valid.SetTgl(Tanggal, tabMode.getValueAt(row, 5).toString());
            TinggalBersama.setSelectedItem(tabMode.getValueAt(row, 6).toString());
            Hambatan.setSelectedItem(tabMode.getValueAt(row, 7).toString());
            HambatanPendengaran.setSelected(tabMode.getValueAt(row, 8).toString().equals("Ya"));
            HambatanEmosi.setSelected(tabMode.getValueAt(row, 9).toString().equals("Ya"));
            HambatanPenglihatan.setSelected(tabMode.getValueAt(row, 10).toString().equals("Ya"));
            HambatanBicara.setSelected(tabMode.getValueAt(row, 11).toString().equals("Ya"));
            MotivasiKurang.setSelected(tabMode.getValueAt(row, 12).toString().equals("Ya"));
            MemoriHilang.setSelected(tabMode.getValueAt(row, 13).toString().equals("Ya"));
            FisikLemah.setSelected(tabMode.getValueAt(row, 14).toString().equals("Ya"));
            Alkoholik.setSelected(tabMode.getValueAt(row, 15).toString().equals("Ya"));
            TidakMampuBelajar.setSelected(tabMode.getValueAt(row, 16).toString().equals("Ya"));
            Perokok.setSelected(tabMode.getValueAt(row, 17).toString().equals("Ya"));
            EdukasiKepada.setSelectedItem(tabMode.getValueAt(row, 18).toString());
            KemampuanBahasa.setSelectedItem(tabMode.getValueAt(row, 19).toString());
            BahasaDaerah.setText(tabMode.getValueAt(row, 20).toString());
            BahasaAsing.setText(tabMode.getValueAt(row, 21).toString());
            PerluPenerjemah.setSelectedItem(tabMode.getValueAt(row, 22).toString());
            BacaTulis.setSelectedItem(tabMode.getValueAt(row, 23).toString());
            KepercayaanLainnya.setText(tabMode.getValueAt(row, 24).toString());
            KesediaanMenerimaEdukasi.setSelectedItem(tabMode.getValueAt(row, 25).toString());
            CaraEdukasi.setSelectedItem(tabMode.getValueAt(row, 26).toString());
            KebPartisipasi.setSelected(tabMode.getValueAt(row, 27).toString().equals("Ya"));
            KebPenunjang.setSelected(tabMode.getValueAt(row, 28).toString().equals("Ya"));
            KebKondisi.setSelected(tabMode.getValueAt(row, 29).toString().equals("Ya"));
            KebInformedConcent.setSelected(tabMode.getValueAt(row, 30).toString().equals("Ya"));
            KebDiet.setSelected(tabMode.getValueAt(row, 31).toString().equals("Ya"));
            KebObat.setSelected(tabMode.getValueAt(row, 32).toString().equals("Ya"));
            KebAlatMedis.setSelected(tabMode.getValueAt(row, 33).toString().equals("Ya"));
            KebNyeri.setSelected(tabMode.getValueAt(row, 34).toString().equals("Ya"));
            KebRehabilitasi.setSelected(tabMode.getValueAt(row, 35).toString().equals("Ya"));
            KebCuciTangan.setSelected(tabMode.getValueAt(row, 36).toString().equals("Ya"));
            KebMerokok.setSelected(tabMode.getValueAt(row, 37).toString().equals("Ya"));
            KebLainLain.setText(tabMode.getValueAt(row, 38).toString());
            KebRujukan.setSelected(tabMode.getValueAt(row, 39).toString().equals("Ya"));
            NIP.setText(tabMode.getValueAt(row, 40).toString());
            NamaPetugas.setText(tabMode.getValueAt(row, 41).toString());
        }
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (NIP.getText().trim().equals("") || NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Petugas");
        } else {
            if (Sequel.menyimpantf("edukasi_pasien_keluarga_rscb", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 37, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                NIP.getText(), TinggalBersama.getSelectedItem().toString(), Hambatan.getSelectedItem().toString(),
                HambatanPendengaran.isSelected() ? "Ya" : "Tidak", HambatanEmosi.isSelected() ? "Ya" : "Tidak", HambatanPenglihatan.isSelected() ? "Ya" : "Tidak",
                HambatanBicara.isSelected() ? "Ya" : "Tidak", MotivasiKurang.isSelected() ? "Ya" : "Tidak", MemoriHilang.isSelected() ? "Ya" : "Tidak",
                FisikLemah.isSelected() ? "Ya" : "Tidak", Alkoholik.isSelected() ? "Ya" : "Tidak", TidakMampuBelajar.isSelected() ? "Ya" : "Tidak",
                Perokok.isSelected() ? "Ya" : "Tidak", EdukasiKepada.getSelectedItem().toString(), KemampuanBahasa.getSelectedItem().toString(),
                BahasaDaerah.getText(), BahasaAsing.getText(), PerluPenerjemah.getSelectedItem().toString(), BacaTulis.getSelectedItem().toString(),
                KepercayaanLainnya.getText(), KesediaanMenerimaEdukasi.getSelectedItem().toString(), CaraEdukasi.getSelectedItem().toString(),
                KebPartisipasi.isSelected() ? "Ya" : "Tidak", KebPenunjang.isSelected() ? "Ya" : "Tidak", KebKondisi.isSelected() ? "Ya" : "Tidak",
                KebInformedConcent.isSelected() ? "Ya" : "Tidak", KebDiet.isSelected() ? "Ya" : "Tidak", KebObat.isSelected() ? "Ya" : "Tidak",
                KebAlatMedis.isSelected() ? "Ya" : "Tidak", KebNyeri.isSelected() ? "Ya" : "Tidak", KebRehabilitasi.isSelected() ? "Ya" : "Tidak",
                KebCuciTangan.isSelected() ? "Ya" : "Tidak", KebMerokok.isSelected() ? "Ya" : "Tidak", KebLainLain.getText(), KebRujukan.isSelected() ? "Ya" : "Tidak"
            }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void ganti() {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (NIP.getText().trim().equals("") || NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Petugas");
        } else {
            if (Sequel.mengedittf("edukasi_pasien_keluarga_rscb", "no_rawat=?", "no_rawat=?,tanggal=?,nip=?,tinggal_bersama=?,hambatan=?,hambatan_pendengaran=?,hambatan_emosi=?,hambatan_penglihatan=?,hambatan_bicara=?,motivasi_kurang=?,memori_hilang=?,fisik_lemah=?,alkoholik=?,tidak_mampu_belajar=?,perokok=?,edukasi_kepada=?,kemampuan_bahasa=?,bahasa_daerah=?,bahasa_asing=?,perlu_penerjemah=?,baca_tulis=?,kepercayaan_lainnya=?,kesediaan_menerima_edukasi=?,cara_edukasi=?,keb_partisipasi=?,keb_penunjang=?,keb_kondisi=?,keb_informed_concent=?,keb_diet=?,keb_obat=?,keb_alat_medis=?,keb_nyeri=?,keb_rehabilitasi=?,keb_cuci_tangan=?,keb_merokok=?,keb_lain_lain=?,keb_rujukan=?", 38, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                NIP.getText(), TinggalBersama.getSelectedItem().toString(), Hambatan.getSelectedItem().toString(),
                HambatanPendengaran.isSelected() ? "Ya" : "Tidak", HambatanEmosi.isSelected() ? "Ya" : "Tidak", HambatanPenglihatan.isSelected() ? "Ya" : "Tidak",
                HambatanBicara.isSelected() ? "Ya" : "Tidak", MotivasiKurang.isSelected() ? "Ya" : "Tidak", MemoriHilang.isSelected() ? "Ya" : "Tidak",
                FisikLemah.isSelected() ? "Ya" : "Tidak", Alkoholik.isSelected() ? "Ya" : "Tidak", TidakMampuBelajar.isSelected() ? "Ya" : "Tidak",
                Perokok.isSelected() ? "Ya" : "Tidak", EdukasiKepada.getSelectedItem().toString(), KemampuanBahasa.getSelectedItem().toString(),
                BahasaDaerah.getText(), BahasaAsing.getText(), PerluPenerjemah.getSelectedItem().toString(), BacaTulis.getSelectedItem().toString(),
                KepercayaanLainnya.getText(), KesediaanMenerimaEdukasi.getSelectedItem().toString(), CaraEdukasi.getSelectedItem().toString(),
                KebPartisipasi.isSelected() ? "Ya" : "Tidak", KebPenunjang.isSelected() ? "Ya" : "Tidak", KebKondisi.isSelected() ? "Ya" : "Tidak",
                KebInformedConcent.isSelected() ? "Ya" : "Tidak", KebDiet.isSelected() ? "Ya" : "Tidak", KebObat.isSelected() ? "Ya" : "Tidak",
                KebAlatMedis.isSelected() ? "Ya" : "Tidak", KebNyeri.isSelected() ? "Ya" : "Tidak", KebRehabilitasi.isSelected() ? "Ya" : "Tidak",
                KebCuciTangan.isSelected() ? "Ya" : "Tidak", KebMerokok.isSelected() ? "Ya" : "Tidak", KebLainLain.getText(), KebRujukan.isSelected() ? "Ya" : "Tidak",
                TNoRw.getText()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void hapus() {
        if (tbObat.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from edukasi_pasien_keluarga_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString(), tabMode.getValueAt(tbObat.getSelectedRow(), 5).toString()
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
}
