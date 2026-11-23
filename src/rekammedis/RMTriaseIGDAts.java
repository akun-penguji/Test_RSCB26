package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import widget.*;

public class RMTriaseIGDAts extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari, KdPetugas, NmPetugas;
    private TextArea TKeluhanUtama;
    private TextBox TTekananDarah, TNadi, TNafas, TSpO2, TSuhu;
    private TextBox TGCSE, TGCSV, TGCSM;
    private TextBox TReflexCahaya, TPupilKiri, TPupilKanan, TAkral;
    private TextBox TAlergiObat, TAlergiMakanan, TAlergiLain;
    private Label LCount;

    // ATS 1 - Resusitasi (8)
    private CekBox chkAts1HentiJantung, chkAts1HentiNafas, chkAts1RisikoSumbatanNapas, chkAts1RRKurang10;
    private CekBox chkAts1RespirasiDistress, chkAts1TekSistolik, chkAts1GCSKurang9, chkAts1KejangTerus;

    // ATS 2 (22)
    private CekBox chkAts2StridorBerat, chkAts2HRExtreme, chkAts2KulitLembab, chkAts2PerdarahanBerat;
    private CekBox chkAts2OverdosisObat, chkAts2GangguanPerilaku;
    private CekBox chkAts2PernafasanDangkal, chkAts2SaO2Kurang90, chkAts2SesakBerat;
    private CekBox chkAts2PenurunanKesadaran, chkAts2HemiparaseAkut, chkAts2NyeriDadaKardiak;
    private CekBox chkAts2DemamKelemahan, chkAts2HemiparesiDisfagia, chkAts2MataAlkaliAsam;
    private CekBox chkAts2MultipleTraumaMayor, chkAts2TraumaBerat, chkAts2MinumSedative;
    private CekBox chkAts2KenaBisaBinatang, chkAts2NyeriHebat, chkAts2GaduhGelisah;

    // ATS 3 Obs Respirasi (8)
    private CekBox chkAts3BatukDemamSesak, chkAts3BatukNyeriSesak, chkAts3BatukDarah, chkAts3SesakAsma;
    private CekBox chkAts3SesakTumor, chkAts3SesakPPOK, chkAts3SesakTB, chkAts3SesakSpO2;
    // ATS 3 Non Respirasi (14)
    private CekBox chkAts3HipertensiBerat, chkAts3PerdarahanSedang, chkAts3RiwayatKejang, chkAts3MuntahMenetap;
    private CekBox chkAts3Dehidrasi, chkAts3CederaKepala, chkAts3NyeriSedangBerat, chkAts3NyeriNonKardiak;
    private CekBox chkAts3SakitPerut, chkAts3TraumaExtremitas, chkAts3ExtremitasTidakSensasi;
    private CekBox chkAts3TraumaRisikoTinggi, chkAts3StableNeonatus, chkAts3StressBerat;

    // ATS 4 (10)
    private CekBox chkAts4PerdarahanRingan, chkAts4AspirasiBendaAsing, chkAts4CKR, chkAts4IritasiMata;
    private CekBox chkAts4Keseleo, chkAts4FrakturRingan, chkAts4BalutanKetat, chkAts4SendiBengkak;
    private CekBox chkAts4SukarMenelan, chkAts4MasalahMental;

    // ATS 5 (10)
    private CekBox chkAts5NyeriRingan, chkAts5NyeriSedangMual, chkAts5NyeriPerutNonSpesifik, chkAts5TraumaDada;
    private CekBox chkAts5RiwayatRisikoRendah, chkAts5GejalaRingan, chkAts5LukaKecil, chkAts5KontrolLuka;
    private CekBox chkAts5Imunisasi, chkAts5PerilakuPsikiatrik;

    // Kategori ATS
    private JRadioButton RdKategori1, RdKategori2, RdKategori3, RdKategori4, RdKategori5;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMTriaseIGDAts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JPopupMenu jPopupMenu1;
    private JMenuItem MnCetak;

    private CekBox createCheckBox(String text) {
        CekBox cb = new CekBox();
        cb.setText(text);
        cb.setOpaque(false);
        cb.setSelected(false);
        cb.addActionListener(ev -> autoSelectKategori());
        return cb;
    }

    private void autoSelectKategori() {
        // Auto-select kategori berdasarkan checkbox ATS yang tercentang
        if (chkAts1HentiJantung==null) return; // belum init
        boolean ada1 = chkAts1HentiJantung.isSelected()||chkAts1HentiNafas.isSelected()||chkAts1RisikoSumbatanNapas.isSelected()||
            chkAts1RRKurang10.isSelected()||chkAts1RespirasiDistress.isSelected()||chkAts1TekSistolik.isSelected()||
            chkAts1GCSKurang9.isSelected()||chkAts1KejangTerus.isSelected();
        boolean ada2 = chkAts2StridorBerat.isSelected()||chkAts2HRExtreme.isSelected()||chkAts2KulitLembab.isSelected()||
            chkAts2PerdarahanBerat.isSelected()||chkAts2OverdosisObat.isSelected()||chkAts2GangguanPerilaku.isSelected()||
            chkAts2PernafasanDangkal.isSelected()||chkAts2SaO2Kurang90.isSelected()||chkAts2SesakBerat.isSelected()||
            chkAts2PenurunanKesadaran.isSelected()||chkAts2HemiparaseAkut.isSelected()||chkAts2NyeriDadaKardiak.isSelected()||
            chkAts2DemamKelemahan.isSelected()||chkAts2HemiparesiDisfagia.isSelected()||chkAts2MataAlkaliAsam.isSelected()||
            chkAts2MultipleTraumaMayor.isSelected()||chkAts2TraumaBerat.isSelected()||chkAts2MinumSedative.isSelected()||
            chkAts2KenaBisaBinatang.isSelected()||chkAts2NyeriHebat.isSelected()||chkAts2GaduhGelisah.isSelected();
        boolean ada3 = chkAts3BatukDemamSesak.isSelected()||chkAts3BatukNyeriSesak.isSelected()||chkAts3BatukDarah.isSelected()||
            chkAts3SesakAsma.isSelected()||chkAts3SesakTumor.isSelected()||chkAts3SesakPPOK.isSelected()||
            chkAts3SesakTB.isSelected()||chkAts3SesakSpO2.isSelected()||
            chkAts3HipertensiBerat.isSelected()||chkAts3PerdarahanSedang.isSelected()||chkAts3RiwayatKejang.isSelected()||
            chkAts3MuntahMenetap.isSelected()||chkAts3Dehidrasi.isSelected()||chkAts3CederaKepala.isSelected()||
            chkAts3NyeriSedangBerat.isSelected()||chkAts3NyeriNonKardiak.isSelected()||chkAts3SakitPerut.isSelected()||
            chkAts3TraumaExtremitas.isSelected()||chkAts3ExtremitasTidakSensasi.isSelected()||chkAts3TraumaRisikoTinggi.isSelected()||
            chkAts3StableNeonatus.isSelected()||chkAts3StressBerat.isSelected();
        boolean ada4 = chkAts4PerdarahanRingan.isSelected()||chkAts4AspirasiBendaAsing.isSelected()||chkAts4CKR.isSelected()||
            chkAts4IritasiMata.isSelected()||chkAts4Keseleo.isSelected()||chkAts4FrakturRingan.isSelected()||
            chkAts4BalutanKetat.isSelected()||chkAts4SendiBengkak.isSelected()||chkAts4SukarMenelan.isSelected()||
            chkAts4MasalahMental.isSelected();
        boolean ada5 = chkAts5NyeriRingan.isSelected()||chkAts5NyeriSedangMual.isSelected()||chkAts5NyeriPerutNonSpesifik.isSelected()||
            chkAts5TraumaDada.isSelected()||chkAts5RiwayatRisikoRendah.isSelected()||chkAts5GejalaRingan.isSelected()||
            chkAts5LukaKecil.isSelected()||chkAts5KontrolLuka.isSelected()||chkAts5Imunisasi.isSelected()||
            chkAts5PerilakuPsikiatrik.isSelected();
        // Pilih kategori tertinggi (paling darurat)
        if (ada1) RdKategori1.setSelected(true);
        else if (ada2) RdKategori2.setSelected(true);
        else if (ada3) RdKategori3.setSelected(true);
        else if (ada4) RdKategori4.setSelected(true);
        else if (ada5) RdKategori5.setSelected(true);
    }

    private void loadAlergiPasien() {
        if (TNoRM.getText().trim().isEmpty()) return;
        String catatan = Sequel.cariIsi("select catatan from catatan_pasien_alergi where no_rkm_medis=?", TNoRM.getText());
        if (!catatan.isEmpty()) {
            TAlergiObat.setText(catatan);
        }
    }

    private void cetakPerBaris() {
        if (tbObat.getSelectedRow() > -1) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> p = new HashMap<>();
            p.put("namars", akses.getnamars()); p.put("alamatrs", akses.getalamatrs());
            p.put("propinsirs", akses.getpropinsirs()); p.put("kabupatenrs", akses.getkabupatenrs());
            p.put("kontakrs", akses.getkontakrs()); p.put("emailrs", akses.getemailrs());
            p.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            String nipp = Sequel.cariIsi("select nip from triase_igd_ats where no_rawat='" +
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' and tanggal='" +
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString() + "'");
            String nmPet = tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString();
            String finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", nipp);
            p.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() +
                "\nDitandatangani secara elektronik oleh " + nmPet + "\nID " + (finger.isEmpty() ? nipp : finger) +
                "\n" + tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Valid.MyReportqry("rptTriaseIGDATS.jasper", "report", "::[ Triase IGD ATS ]::",
                "select s.*,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.no_ktp,pt.nama as nm_petugas " +
                "from triase_igd_ats s inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip " +
                "where s.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' " +
                "and s.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString() + "'", p);
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!");
        }
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Triase IGD ATS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new Color(50,50,50)));
        internalFrame1.setLayout(new BorderLayout(1,1));

        // PANEL ATAS
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT,5,4));
        panelAtas.setPreferredSize(new Dimension(1024,33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130,23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100,23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200,23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Kategori","Keluhan","Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });

        jPopupMenu1 = new JPopupMenu();
        MnCetak = new JMenuItem();
        MnCetak.setBackground(new Color(255,255,254));
        MnCetak.setFont(new java.awt.Font("Tahoma",0,11));
        MnCetak.setForeground(new Color(50,50,50));
        MnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnCetak.setText("Cetak");
        MnCetak.setPreferredSize(new Dimension(220,26));
        MnCetak.addActionListener(ev -> cetakPerBaris());
        jPopupMenu1.add(MnCetak);
        tbObat.setComponentPopupMenu(jPopupMenu1);

        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(1100, 1500));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("TRIASE IGD - AUSTRALASIAN TRIAGE SCALE (ATS)");
        lblHeader.setFont(new java.awt.Font("Tahoma",1,13)); lblHeader.setBounds(10,y,500,20); FormInput.add(lblHeader);

        // Tanggal & Keluhan Utama
        y += 30;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10,y,80,23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        y += 28;
        Label lblKeluhan = new Label(); lblKeluhan.setText("Keluhan Utama :"); lblKeluhan.setBounds(10,y,100,23); FormInput.add(lblKeluhan);
        TKeluhanUtama = new TextArea(); TKeluhanUtama.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TKeluhanUtama.setBounds(115,y,400,40); FormInput.add(TKeluhanUtama);

        // Tanda Vital (right side)
        int xv = 560, yv = 10;
        JLabel lblTV = new JLabel("TANDA VITAL");
        lblTV.setFont(new java.awt.Font("Tahoma",1,11)); lblTV.setBounds(xv,yv,200,20); FormInput.add(lblTV);

        yv += 28;
        Label lblTD = new Label(); lblTD.setText("Tekanan Darah :"); lblTD.setBounds(xv,yv,100,23); FormInput.add(lblTD);
        TTekananDarah = new TextBox(); TTekananDarah.setBounds(xv+105,yv,80,23); FormInput.add(TTekananDarah);
        JLabel lblTDu = new JLabel("mmHg"); lblTDu.setBounds(xv+190,yv,40,23); FormInput.add(lblTDu);

        yv += 30;
        Label lblNadi = new Label(); lblNadi.setText("Nadi :"); lblNadi.setBounds(xv,yv,100,23); FormInput.add(lblNadi);
        TNadi = new TextBox(); TNadi.setBounds(xv+105,yv,80,23); FormInput.add(TNadi);
        JLabel lblNu = new JLabel("x/menit"); lblNu.setBounds(xv+190,yv,50,23); FormInput.add(lblNu);

        yv += 30;
        Label lblNafas = new Label(); lblNafas.setText("Nafas :"); lblNafas.setBounds(xv,yv,100,23); FormInput.add(lblNafas);
        TNafas = new TextBox(); TNafas.setBounds(xv+105,yv,80,23); FormInput.add(TNafas);
        JLabel lblNfu = new JLabel("x/menit"); lblNfu.setBounds(xv+190,yv,50,23); FormInput.add(lblNfu);

        yv += 30;
        Label lblSpO2 = new Label(); lblSpO2.setText("SpO2 :"); lblSpO2.setBounds(xv,yv,100,23); FormInput.add(lblSpO2);
        TSpO2 = new TextBox(); TSpO2.setBounds(xv+105,yv,80,23); FormInput.add(TSpO2);
        JLabel lblSu = new JLabel("%"); lblSu.setBounds(xv+190,yv,30,23); FormInput.add(lblSu);

        yv += 30;
        Label lblSuhu = new Label(); lblSuhu.setText("Suhu :"); lblSuhu.setBounds(xv,yv,100,23); FormInput.add(lblSuhu);
        TSuhu = new TextBox(); TSuhu.setBounds(xv+105,yv,80,23); FormInput.add(TSuhu);
        JLabel lblShu = new JLabel("\u00B0C"); lblShu.setBounds(xv+190,yv,30,23); FormInput.add(lblShu);

        yv += 30;
        Label lblGCS = new Label(); lblGCS.setText("GCS :"); lblGCS.setBounds(xv,yv,100,23); FormInput.add(lblGCS);
        JLabel lblE = new JLabel("E"); lblE.setBounds(xv+105,yv,15,23); FormInput.add(lblE);
        TGCSE = new TextBox(); TGCSE.setBounds(xv+118,yv,30,23); FormInput.add(TGCSE);
        JLabel lblV = new JLabel("V"); lblV.setBounds(xv+155,yv,15,23); FormInput.add(lblV);
        TGCSV = new TextBox(); TGCSV.setBounds(xv+168,yv,30,23); FormInput.add(TGCSV);
        JLabel lblM = new JLabel("M"); lblM.setBounds(xv+205,yv,15,23); FormInput.add(lblM);
        TGCSM = new TextBox(); TGCSM.setBounds(xv+218,yv,30,23); FormInput.add(TGCSM);

        yv += 30;
        Label lblRC = new Label(); lblRC.setText("Reflex Cahaya :"); lblRC.setBounds(xv,yv,100,23); FormInput.add(lblRC);
        TReflexCahaya = new TextBox(); TReflexCahaya.setBounds(xv+105,yv,80,23); FormInput.add(TReflexCahaya);

        yv += 30;
        Label lblPupil = new Label(); lblPupil.setText("Pupil :"); lblPupil.setBounds(xv,yv,100,23); FormInput.add(lblPupil);
        TPupilKiri = new TextBox(); TPupilKiri.setBounds(xv+105,yv,40,23); FormInput.add(TPupilKiri);
        JLabel lblPmm = new JLabel("mm /"); lblPmm.setBounds(xv+148,yv,30,23); FormInput.add(lblPmm);
        TPupilKanan = new TextBox(); TPupilKanan.setBounds(xv+180,yv,40,23); FormInput.add(TPupilKanan);
        JLabel lblPmm2 = new JLabel("mm"); lblPmm2.setBounds(xv+223,yv,25,23); FormInput.add(lblPmm2);

        yv += 30;
        Label lblAkral = new Label(); lblAkral.setText("Akral :"); lblAkral.setBounds(xv,yv,100,23); FormInput.add(lblAkral);
        TAkral = new TextBox(); TAkral.setBounds(xv+105,yv,130,23); FormInput.add(TAkral);

        yv += 36;
        Label lblAO = new Label(); lblAO.setText("Alergi Obat :"); lblAO.setBounds(xv,yv,100,23); FormInput.add(lblAO);
        TAlergiObat = new TextBox(); TAlergiObat.setBounds(xv+105,yv,130,23); FormInput.add(TAlergiObat);

        yv += 30;
        Label lblAM = new Label(); lblAM.setText("Alergi Makanan :"); lblAM.setBounds(xv,yv,100,23); FormInput.add(lblAM);
        TAlergiMakanan = new TextBox(); TAlergiMakanan.setBounds(xv+105,yv,130,23); FormInput.add(TAlergiMakanan);

        yv += 30;
        Label lblAL = new Label(); lblAL.setText("Alergi Lainnya :"); lblAL.setBounds(xv,yv,100,23); FormInput.add(lblAL);
        TAlergiLain = new TextBox(); TAlergiLain.setBounds(xv+105,yv,130,23); FormInput.add(TAlergiLain);

        // ==================== ATS 1 - RESUSITASI ====================
        y = 120;
        JLabel lblAts1 = new JLabel("ATS 1 - RESUSITASI (Segera)");
        lblAts1.setFont(new java.awt.Font("Tahoma",1,11)); lblAts1.setForeground(new Color(200,0,0));
        lblAts1.setBounds(10,y,300,20); FormInput.add(lblAts1);

        y += 22;
        chkAts1HentiJantung = createCheckBox("Henti jantung"); chkAts1HentiJantung.setBounds(20,y,200,20); FormInput.add(chkAts1HentiJantung);
        chkAts1HentiNafas = createCheckBox("Henti nafas"); chkAts1HentiNafas.setBounds(250,y,200,20); FormInput.add(chkAts1HentiNafas);

        y += 22;
        chkAts1RisikoSumbatanNapas = createCheckBox("Risiko sumbatan jalan napas"); chkAts1RisikoSumbatanNapas.setBounds(20,y,250,20); FormInput.add(chkAts1RisikoSumbatanNapas);
        chkAts1RRKurang10 = createCheckBox("RR < 10"); chkAts1RRKurang10.setBounds(250,y,200,20); FormInput.add(chkAts1RRKurang10);

        y += 22;
        chkAts1RespirasiDistress = createCheckBox("Respirasi distress sangat berat"); chkAts1RespirasiDistress.setBounds(20,y,250,20); FormInput.add(chkAts1RespirasiDistress);
        chkAts1TekSistolik = createCheckBox("Tek sistolik < 80"); chkAts1TekSistolik.setBounds(250,y,200,20); FormInput.add(chkAts1TekSistolik);

        y += 22;
        chkAts1GCSKurang9 = createCheckBox("GCS < 9"); chkAts1GCSKurang9.setBounds(20,y,200,20); FormInput.add(chkAts1GCSKurang9);
        chkAts1KejangTerus = createCheckBox("Kejang terus menerus"); chkAts1KejangTerus.setBounds(250,y,200,20); FormInput.add(chkAts1KejangTerus);

        // ==================== ATS 2 ====================
        y += 32;
        JLabel lblAts2 = new JLabel("ATS 2 - EMERGENCY (10 menit)");
        lblAts2.setFont(new java.awt.Font("Tahoma",1,11)); lblAts2.setForeground(new Color(200,180,0));
        lblAts2.setBounds(10,y,300,20); FormInput.add(lblAts2);

        // Left column - Resusitasi
        y += 22;
        JLabel lblA2R = new JLabel("Resusitasi :"); lblA2R.setFont(new java.awt.Font("Tahoma",2,10)); lblA2R.setBounds(20,y,100,18); FormInput.add(lblA2R);
        y += 20;
        chkAts2StridorBerat = createCheckBox("Stridor berat"); chkAts2StridorBerat.setBounds(20,y,170,20); FormInput.add(chkAts2StridorBerat);
        y += 22;
        chkAts2HRExtreme = createCheckBox("HR <50 / >150"); chkAts2HRExtreme.setBounds(20,y,170,20); FormInput.add(chkAts2HRExtreme);
        y += 22;
        chkAts2KulitLembab = createCheckBox("Kulit lembab hipotensi"); chkAts2KulitLembab.setBounds(20,y,190,20); FormInput.add(chkAts2KulitLembab);
        y += 22;
        chkAts2PerdarahanBerat = createCheckBox("Perdarahan berat"); chkAts2PerdarahanBerat.setBounds(20,y,170,20); FormInput.add(chkAts2PerdarahanBerat);
        y += 22;
        chkAts2OverdosisObat = createCheckBox("Overdosis obat"); chkAts2OverdosisObat.setBounds(20,y,170,20); FormInput.add(chkAts2OverdosisObat);
        y += 22;
        chkAts2GangguanPerilaku = createCheckBox("Gangguan perilaku berat"); chkAts2GangguanPerilaku.setBounds(20,y,200,20); FormInput.add(chkAts2GangguanPerilaku);

        // Middle column - Obs Respirasi (positioned at saved y offsets)
        int y2mid = y - (22*5) - 20; // go back to start of ATS2 items
        JLabel lblA2OR = new JLabel("Obs Respirasi :"); lblA2OR.setFont(new java.awt.Font("Tahoma",2,10)); lblA2OR.setBounds(230,y2mid,120,18); FormInput.add(lblA2OR);
        y2mid += 20;
        chkAts2PernafasanDangkal = createCheckBox("Pernafasan dangkal"); chkAts2PernafasanDangkal.setBounds(230,y2mid,190,20); FormInput.add(chkAts2PernafasanDangkal);
        y2mid += 22;
        chkAts2SaO2Kurang90 = createCheckBox("SaO2 < 90"); chkAts2SaO2Kurang90.setBounds(230,y2mid,170,20); FormInput.add(chkAts2SaO2Kurang90);
        y2mid += 22;
        chkAts2SesakBerat = createCheckBox("Sesak nafas berat"); chkAts2SesakBerat.setBounds(230,y2mid,170,20); FormInput.add(chkAts2SesakBerat);

        // Right column - Non Respirasi
        int y2r = y - (22*5) - 20;
        JLabel lblA2NR = new JLabel("Non Respirasi :"); lblA2NR.setFont(new java.awt.Font("Tahoma",2,10)); lblA2NR.setBounds(430,y2r,120,18); FormInput.add(lblA2NR);
        y2r += 20;
        chkAts2PenurunanKesadaran = createCheckBox("Penurunan Kesadaran"); chkAts2PenurunanKesadaran.setBounds(430,y2r,200,20); FormInput.add(chkAts2PenurunanKesadaran);
        y2r += 22;
        chkAts2HemiparaseAkut = createCheckBox("Hemiparese akut"); chkAts2HemiparaseAkut.setBounds(430,y2r,200,20); FormInput.add(chkAts2HemiparaseAkut);
        y2r += 22;
        chkAts2NyeriDadaKardiak = createCheckBox("Nyeri dada kardiak"); chkAts2NyeriDadaKardiak.setBounds(430,y2r,200,20); FormInput.add(chkAts2NyeriDadaKardiak);
        y2r += 22;
        chkAts2DemamKelemahan = createCheckBox("Demam dengan kelemahan"); chkAts2DemamKelemahan.setBounds(430,y2r,220,20); FormInput.add(chkAts2DemamKelemahan);
        y2r += 22;
        chkAts2HemiparesiDisfagia = createCheckBox("Hemipareisi/disfagia"); chkAts2HemiparesiDisfagia.setBounds(430,y2r,200,20); FormInput.add(chkAts2HemiparesiDisfagia);
        y2r += 22;
        chkAts2MataAlkaliAsam = createCheckBox("Mata kena alkali/asam"); chkAts2MataAlkaliAsam.setBounds(430,y2r,200,20); FormInput.add(chkAts2MataAlkaliAsam);
        y2r += 22;
        chkAts2MultipleTraumaMayor = createCheckBox("Multiple trauma Mayor"); chkAts2MultipleTraumaMayor.setBounds(430,y2r,200,20); FormInput.add(chkAts2MultipleTraumaMayor);
        y2r += 22;
        chkAts2TraumaBerat = createCheckBox("Trauma berat"); chkAts2TraumaBerat.setBounds(430,y2r,200,20); FormInput.add(chkAts2TraumaBerat);
        y2r += 22;
        chkAts2MinumSedative = createCheckBox("Minum sedative"); chkAts2MinumSedative.setBounds(430,y2r,200,20); FormInput.add(chkAts2MinumSedative);
        y2r += 22;
        chkAts2KenaBisaBinatang = createCheckBox("Kena bisa binatang"); chkAts2KenaBisaBinatang.setBounds(430,y2r,200,20); FormInput.add(chkAts2KenaBisaBinatang);
        y2r += 22;
        chkAts2NyeriHebat = createCheckBox("Nyeri hebat"); chkAts2NyeriHebat.setBounds(430,y2r,200,20); FormInput.add(chkAts2NyeriHebat);
        y2r += 22;
        chkAts2GaduhGelisah = createCheckBox("Gaduh gelisah"); chkAts2GaduhGelisah.setBounds(430,y2r,200,20); FormInput.add(chkAts2GaduhGelisah);

        // Advance y past the tallest column (right column has 12 items + label)
        y = Math.max(y, y2r);

        // ==================== ATS 3 ====================
        y += 30;
        JLabel lblAts3 = new JLabel("ATS 3 - URGENT (30 menit)");
        lblAts3.setFont(new java.awt.Font("Tahoma",1,11)); lblAts3.setForeground(new Color(220,120,0));
        lblAts3.setBounds(10,y,300,20); FormInput.add(lblAts3);

        // Obs Respirasi
        y += 22;
        JLabel lblA3OR = new JLabel("Obs Respirasi :"); lblA3OR.setFont(new java.awt.Font("Tahoma",2,10)); lblA3OR.setBounds(20,y,120,18); FormInput.add(lblA3OR);
        int y3nr = y; // save for non respirasi column
        y += 20;
        chkAts3BatukDemamSesak = createCheckBox("Batuk berdahak+demam+sesak"); chkAts3BatukDemamSesak.setBounds(20,y,250,20); FormInput.add(chkAts3BatukDemamSesak);
        y += 22;
        chkAts3BatukNyeriSesak = createCheckBox("Batuk+nyeri dada+sesak"); chkAts3BatukNyeriSesak.setBounds(20,y,250,20); FormInput.add(chkAts3BatukNyeriSesak);
        y += 22;
        chkAts3BatukDarah = createCheckBox("Batuk darah"); chkAts3BatukDarah.setBounds(20,y,200,20); FormInput.add(chkAts3BatukDarah);
        y += 22;
        chkAts3SesakAsma = createCheckBox("Sesak+Asma"); chkAts3SesakAsma.setBounds(20,y,200,20); FormInput.add(chkAts3SesakAsma);
        y += 22;
        chkAts3SesakTumor = createCheckBox("Sesak+Tumor"); chkAts3SesakTumor.setBounds(20,y,200,20); FormInput.add(chkAts3SesakTumor);
        y += 22;
        chkAts3SesakPPOK = createCheckBox("Sesak+PPOK"); chkAts3SesakPPOK.setBounds(20,y,200,20); FormInput.add(chkAts3SesakPPOK);
        y += 22;
        chkAts3SesakTB = createCheckBox("Sesak+TB"); chkAts3SesakTB.setBounds(20,y,200,20); FormInput.add(chkAts3SesakTB);
        y += 22;
        chkAts3SesakSpO2 = createCheckBox("Sesak+SpO2 90-95%"); chkAts3SesakSpO2.setBounds(20,y,200,20); FormInput.add(chkAts3SesakSpO2);

        // ATS3 Non Respirasi (right column)
        JLabel lblA3NR = new JLabel("Non Respirasi :"); lblA3NR.setFont(new java.awt.Font("Tahoma",2,10)); lblA3NR.setBounds(320,y3nr,120,18); FormInput.add(lblA3NR);
        int y3nri = y3nr + 20;
        chkAts3HipertensiBerat = createCheckBox("Hipertensi berat"); chkAts3HipertensiBerat.setBounds(320,y3nri,200,20); FormInput.add(chkAts3HipertensiBerat);
        y3nri += 22;
        chkAts3PerdarahanSedang = createCheckBox("Perdarahan sedang"); chkAts3PerdarahanSedang.setBounds(320,y3nri,200,20); FormInput.add(chkAts3PerdarahanSedang);
        y3nri += 22;
        chkAts3RiwayatKejang = createCheckBox("Riwayat kejang"); chkAts3RiwayatKejang.setBounds(320,y3nri,200,20); FormInput.add(chkAts3RiwayatKejang);
        y3nri += 22;
        chkAts3MuntahMenetap = createCheckBox("Muntah menetap"); chkAts3MuntahMenetap.setBounds(320,y3nri,200,20); FormInput.add(chkAts3MuntahMenetap);
        y3nri += 22;
        chkAts3Dehidrasi = createCheckBox("Dehidrasi"); chkAts3Dehidrasi.setBounds(320,y3nri,200,20); FormInput.add(chkAts3Dehidrasi);
        y3nri += 22;
        chkAts3CederaKepala = createCheckBox("Cedera kepala+pingsan"); chkAts3CederaKepala.setBounds(320,y3nri,200,20); FormInput.add(chkAts3CederaKepala);
        y3nri += 22;
        chkAts3NyeriSedangBerat = createCheckBox("Nyeri sedang-berat"); chkAts3NyeriSedangBerat.setBounds(320,y3nri,200,20); FormInput.add(chkAts3NyeriSedangBerat);
        y3nri += 22;
        chkAts3NyeriNonKardiak = createCheckBox("Nyeri non kardiak"); chkAts3NyeriNonKardiak.setBounds(320,y3nri,200,20); FormInput.add(chkAts3NyeriNonKardiak);
        y3nri += 22;
        chkAts3SakitPerut = createCheckBox("Sakit perut"); chkAts3SakitPerut.setBounds(320,y3nri,200,20); FormInput.add(chkAts3SakitPerut);
        y3nri += 22;
        chkAts3TraumaExtremitas = createCheckBox("Trauma extremitas/laserasi"); chkAts3TraumaExtremitas.setBounds(320,y3nri,230,20); FormInput.add(chkAts3TraumaExtremitas);
        y3nri += 22;
        chkAts3ExtremitasTidakSensasi = createCheckBox("Extremitas tidak sensasi"); chkAts3ExtremitasTidakSensasi.setBounds(320,y3nri,220,20); FormInput.add(chkAts3ExtremitasTidakSensasi);
        y3nri += 22;
        chkAts3TraumaRisikoTinggi = createCheckBox("Trauma risiko tinggi"); chkAts3TraumaRisikoTinggi.setBounds(320,y3nri,200,20); FormInput.add(chkAts3TraumaRisikoTinggi);
        y3nri += 22;
        chkAts3StableNeonatus = createCheckBox("Stable neonatus"); chkAts3StableNeonatus.setBounds(320,y3nri,200,20); FormInput.add(chkAts3StableNeonatus);
        y3nri += 22;
        chkAts3StressBerat = createCheckBox("Stress berat"); chkAts3StressBerat.setBounds(320,y3nri,200,20); FormInput.add(chkAts3StressBerat);

        y = Math.max(y, y3nri);

        // ==================== ATS 4 ====================
        y += 30;
        JLabel lblAts4 = new JLabel("ATS 4 - SEMI DARURAT (60 menit)");
        lblAts4.setFont(new java.awt.Font("Tahoma",1,11)); lblAts4.setForeground(new Color(100,100,200));
        lblAts4.setBounds(10,y,300,20); FormInput.add(lblAts4);

        y += 22;
        chkAts4PerdarahanRingan = createCheckBox("Perdarahan ringan"); chkAts4PerdarahanRingan.setBounds(20,y,200,20); FormInput.add(chkAts4PerdarahanRingan);
        chkAts4AspirasiBendaAsing = createCheckBox("Aspirasi benda asing"); chkAts4AspirasiBendaAsing.setBounds(250,y,200,20); FormInput.add(chkAts4AspirasiBendaAsing);
        y += 22;
        chkAts4CKR = createCheckBox("CKR"); chkAts4CKR.setBounds(20,y,200,20); FormInput.add(chkAts4CKR);
        chkAts4IritasiMata = createCheckBox("Iritasi mata"); chkAts4IritasiMata.setBounds(250,y,200,20); FormInput.add(chkAts4IritasiMata);
        y += 22;
        chkAts4Keseleo = createCheckBox("Keseleo"); chkAts4Keseleo.setBounds(20,y,200,20); FormInput.add(chkAts4Keseleo);
        chkAts4FrakturRingan = createCheckBox("Fraktur luka ringan"); chkAts4FrakturRingan.setBounds(250,y,200,20); FormInput.add(chkAts4FrakturRingan);
        y += 22;
        chkAts4BalutanKetat = createCheckBox("Balutan ketat"); chkAts4BalutanKetat.setBounds(20,y,200,20); FormInput.add(chkAts4BalutanKetat);
        chkAts4SendiBengkak = createCheckBox("Sendi bengkak"); chkAts4SendiBengkak.setBounds(250,y,200,20); FormInput.add(chkAts4SendiBengkak);
        y += 22;
        chkAts4SukarMenelan = createCheckBox("Sukar menelan"); chkAts4SukarMenelan.setBounds(20,y,200,20); FormInput.add(chkAts4SukarMenelan);
        chkAts4MasalahMental = createCheckBox("Masalah kesehatan mental"); chkAts4MasalahMental.setBounds(250,y,220,20); FormInput.add(chkAts4MasalahMental);

        // ==================== ATS 5 ====================
        y += 30;
        JLabel lblAts5 = new JLabel("ATS 5 - TIDAK DARURAT (120 menit)");
        lblAts5.setFont(new java.awt.Font("Tahoma",1,11)); lblAts5.setForeground(new Color(0,150,0));
        lblAts5.setBounds(10,y,300,20); FormInput.add(lblAts5);

        y += 22;
        chkAts5NyeriRingan = createCheckBox("Nyeri ringan"); chkAts5NyeriRingan.setBounds(20,y,200,20); FormInput.add(chkAts5NyeriRingan);
        chkAts5NyeriSedangMual = createCheckBox("Nyeri sedang mual/diare"); chkAts5NyeriSedangMual.setBounds(250,y,220,20); FormInput.add(chkAts5NyeriSedangMual);
        y += 22;
        chkAts5NyeriPerutNonSpesifik = createCheckBox("Nyeri perut non spesifik"); chkAts5NyeriPerutNonSpesifik.setBounds(20,y,220,20); FormInput.add(chkAts5NyeriPerutNonSpesifik);
        chkAts5TraumaDada = createCheckBox("Trauma dada"); chkAts5TraumaDada.setBounds(250,y,200,20); FormInput.add(chkAts5TraumaDada);
        y += 22;
        chkAts5RiwayatRisikoRendah = createCheckBox("Riwayat risiko rendah"); chkAts5RiwayatRisikoRendah.setBounds(20,y,200,20); FormInput.add(chkAts5RiwayatRisikoRendah);
        chkAts5GejalaRingan = createCheckBox("Gejala ringan"); chkAts5GejalaRingan.setBounds(250,y,200,20); FormInput.add(chkAts5GejalaRingan);
        y += 22;
        chkAts5LukaKecil = createCheckBox("Luka kecil/lecet"); chkAts5LukaKecil.setBounds(20,y,200,20); FormInput.add(chkAts5LukaKecil);
        chkAts5KontrolLuka = createCheckBox("Kontrol luka"); chkAts5KontrolLuka.setBounds(250,y,200,20); FormInput.add(chkAts5KontrolLuka);
        y += 22;
        chkAts5Imunisasi = createCheckBox("Imunisasi"); chkAts5Imunisasi.setBounds(20,y,200,20); FormInput.add(chkAts5Imunisasi);
        chkAts5PerilakuPsikiatrik = createCheckBox("Perilaku psikiatrik"); chkAts5PerilakuPsikiatrik.setBounds(250,y,200,20); FormInput.add(chkAts5PerilakuPsikiatrik);

        // ==================== KATEGORI ATS ====================
        y += 32;
        JLabel lblKat = new JLabel("KATEGORI ATS :");
        lblKat.setFont(new java.awt.Font("Tahoma",1,11)); lblKat.setBounds(10,y,200,20); FormInput.add(lblKat);

        y += 22;
        RdKategori1 = new JRadioButton("Kategori 1 - Segera - Resusitasi"); RdKategori1.setBounds(20,y,280,20); RdKategori1.setOpaque(false); FormInput.add(RdKategori1);
        y += 22;
        RdKategori2 = new JRadioButton("Kategori 2 - 10 menit - Emergency"); RdKategori2.setBounds(20,y,280,20); RdKategori2.setOpaque(false); FormInput.add(RdKategori2);
        y += 22;
        RdKategori3 = new JRadioButton("Kategori 3 - 30 menit - Urgent"); RdKategori3.setBounds(20,y,280,20); RdKategori3.setOpaque(false); RdKategori3.setSelected(true); FormInput.add(RdKategori3);
        y += 22;
        RdKategori4 = new JRadioButton("Kategori 4 - 60 menit - Semi Darurat"); RdKategori4.setBounds(20,y,300,20); RdKategori4.setOpaque(false); FormInput.add(RdKategori4);
        y += 22;
        RdKategori5 = new JRadioButton("Kategori 5 - 120 menit - Tidak Darurat"); RdKategori5.setBounds(20,y,300,20); RdKategori5.setOpaque(false); FormInput.add(RdKategori5);
        ButtonGroup bgKat = new ButtonGroup(); bgKat.add(RdKategori1); bgKat.add(RdKategori2); bgKat.add(RdKategori3); bgKat.add(RdKategori4); bgKat.add(RdKategori5);

        // Petugas
        y += 32;
        Label lp = new Label(); lp.setText("Petugas :"); lp.setBounds(10,y,65,23); FormInput.add(lp);
        KdPetugas = new TextBox(); KdPetugas.setBounds(80,y,80,23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(162,y,165,23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPetugas.setBounds(329,y,28,23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{petugasDlg.emptTeks();petugasDlg.isCek();petugasDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);petugasDlg.setLocationRelativeTo(internalFrame1);petugasDlg.setVisible(true);});
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(petugasDlg.getTable().getSelectedRow()!=-1){KdPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),0).toString());NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),1).toString());}}});

        // Input toggle
        ChkInput = new CekBox(); ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setPreferredSize(new Dimension(192,20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev->isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.setPreferredSize(new Dimension(1100,440));
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH,460));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // Bottom panel
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44,54)); panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        BtnSimpan=new Button();BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));BtnSimpan.setText("Simpan");BtnSimpan.setPreferredSize(new Dimension(100,30));
        BtnBatal=new Button();BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));BtnBatal.setText("Baru");BtnBatal.setPreferredSize(new Dimension(100,30));
        BtnHapus=new Button();BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));BtnHapus.setText("Hapus");BtnHapus.setPreferredSize(new Dimension(100,30));
        BtnEdit=new Button();BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));BtnEdit.setText("Ganti");BtnEdit.setPreferredSize(new Dimension(100,30));
        BtnPrint=new Button();BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));BtnPrint.setText("Cetak");BtnPrint.setPreferredSize(new Dimension(100,30));
        BtnKeluar=new Button();BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));BtnKeluar.setText("Keluar");BtnKeluar.setPreferredSize(new Dimension(100,30));
        Label lc=new Label();lc.setText("Tanggal :");lc.setPreferredSize(new Dimension(55,23));
        DTPCari1=new widget.Tanggal();DTPCari1.setDisplayFormat("dd-MM-yyyy");DTPCari1.setPreferredSize(new Dimension(90,23));
        Label sd=new Label();sd.setText("s.d.");sd.setPreferredSize(new Dimension(25,23));
        DTPCari2=new widget.Tanggal();DTPCari2.setDisplayFormat("dd-MM-yyyy");DTPCari2.setPreferredSize(new Dimension(90,23));
        Label lk=new Label();lk.setText("Key Word :");lk.setPreferredSize(new Dimension(70,23));
        TCari=new TextBox();TCari.setPreferredSize(new Dimension(150,23));
        BtnCari=new Button();BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));BtnCari.setPreferredSize(new Dimension(28,23));
        BtnAll=new Button();BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));BtnAll.setPreferredSize(new Dimension(28,23));
        LCount=new Label();LCount.setText("Record : 0");LCount.setPreferredSize(new Dimension(100,23));
        panelBawah.add(BtnSimpan);panelBawah.add(BtnBatal);panelBawah.add(BtnHapus);panelBawah.add(BtnEdit);panelBawah.add(BtnPrint);panelBawah.add(BtnKeluar);
        panelBawah.add(lc);panelBawah.add(DTPCari1);panelBawah.add(sd);panelBawah.add(DTPCari2);panelBawah.add(lk);panelBawah.add(TCari);panelBawah.add(BtnCari);panelBawah.add(BtnAll);panelBawah.add(LCount);

        // ACTIONS
        BtnKeluar.addActionListener(ev->dispose());
        BtnBatal.addActionListener(ev->emptTeks());
        BtnCari.addActionListener(ev->tampil());
        BtnAll.addActionListener(ev->{TCari.setText("");tampil();});
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){@Override public void keyPressed(java.awt.event.KeyEvent e){if(e.getKeyCode()==KeyEvent.VK_ENTER)tampil();}});

        BtnSimpan.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){Valid.textKosong(TNoRw,"Pasien");return;}
            if(KdPetugas.getText().trim().equals("")){Valid.textKosong(KdPetugas,"Petugas");return;}
            String kategori = getKategoriATS();
            String tgl = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            // 87 columns sesuai urutan tabel DB triase_igd_ats
            if(Sequel.menyimpantf("triase_igd_ats",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Data",88,new String[]{
                TNoRw.getText(), tgl, TKeluhanUtama.getText(), kategori,
                TTekananDarah.getText(), TNadi.getText(), TNafas.getText(), TSpO2.getText(), TSuhu.getText(),
                TGCSE.getText()+"/"+TGCSV.getText()+"/"+TGCSM.getText(),
                TReflexCahaya.getText(), TPupilKiri.getText()+"/"+TPupilKanan.getText(), TAkral.getText(),
                TAlergiObat.getText(), TAlergiMakanan.getText(), TAlergiLain.getText(),
                // ATS1 (8) - sesuai DB: henti_jantung, henti_nafas, sumbatan_jalan_napas, rr_kurang10, resp_distress, tek_sistolik, gcs_kurang9, kejang
                ynCek(chkAts1HentiJantung), ynCek(chkAts1HentiNafas), ynCek(chkAts1RisikoSumbatanNapas), ynCek(chkAts1RRKurang10),
                ynCek(chkAts1RespirasiDistress), ynCek(chkAts1TekSistolik), ynCek(chkAts1GCSKurang9), ynCek(chkAts1KejangTerus),
                // ATS2 (21) - sesuai DB: stridor, hr, kulit_lembab, perdarahan_berat, overdosis, gangguan_perilaku, pernafasan_dangkal, sao2, sesak_berat, penurunan_kesadaran, hemiparese, nyeri_dada, demam, hemipareisi, mata_alkali, multiple_trauma, trauma_berat, minum_sedative, bisa_binatang, nyeri_hebat, gaduh_gelisah
                ynCek(chkAts2StridorBerat), ynCek(chkAts2HRExtreme), ynCek(chkAts2KulitLembab), ynCek(chkAts2PerdarahanBerat),
                ynCek(chkAts2OverdosisObat), ynCek(chkAts2GangguanPerilaku),
                ynCek(chkAts2PernafasanDangkal), ynCek(chkAts2SaO2Kurang90), ynCek(chkAts2SesakBerat),
                ynCek(chkAts2PenurunanKesadaran), ynCek(chkAts2HemiparaseAkut), ynCek(chkAts2NyeriDadaKardiak),
                ynCek(chkAts2DemamKelemahan), ynCek(chkAts2HemiparesiDisfagia), ynCek(chkAts2MataAlkaliAsam),
                ynCek(chkAts2MultipleTraumaMayor), ynCek(chkAts2TraumaBerat), ynCek(chkAts2MinumSedative),
                ynCek(chkAts2KenaBisaBinatang), ynCek(chkAts2NyeriHebat), ynCek(chkAts2GaduhGelisah),
                // ATS3 Obs (8) - sesuai DB: batuk_dahak, batuk_nyeri, batuk_darah, sesak_asma, sesak_tumor, sesak_ppok, sesak_tb, sesak_spo2
                ynCek(chkAts3BatukDemamSesak), ynCek(chkAts3BatukNyeriSesak), ynCek(chkAts3BatukDarah), ynCek(chkAts3SesakAsma),
                ynCek(chkAts3SesakTumor), ynCek(chkAts3SesakPPOK), ynCek(chkAts3SesakTB), ynCek(chkAts3SesakSpO2),
                // ATS3 Non (14) - sesuai DB: hipertensi, perdarahan_sedang, riwayat_kejang, muntah, dehidrasi, cedera_kepala, nyeri_sedang, nyeri_non_kardiak, sakit_perut, trauma_extremitas, extremitas_sensasi, trauma_risiko, stable_neonatus, stress_berat
                ynCek(chkAts3HipertensiBerat), ynCek(chkAts3PerdarahanSedang), ynCek(chkAts3RiwayatKejang), ynCek(chkAts3MuntahMenetap),
                ynCek(chkAts3Dehidrasi), ynCek(chkAts3CederaKepala), ynCek(chkAts3NyeriSedangBerat), ynCek(chkAts3NyeriNonKardiak),
                ynCek(chkAts3SakitPerut), ynCek(chkAts3TraumaExtremitas), ynCek(chkAts3ExtremitasTidakSensasi),
                ynCek(chkAts3TraumaRisikoTinggi), ynCek(chkAts3StableNeonatus), ynCek(chkAts3StressBerat),
                // ATS4 (10) - sesuai DB: perdarahan_ringan, aspirasi, ckr, iritasi_mata, keseleo, fraktur_ringan, balutan_ketat, sendi_bengkak, sukar_menelan, kesehatan_mental
                ynCek(chkAts4PerdarahanRingan), ynCek(chkAts4AspirasiBendaAsing), ynCek(chkAts4CKR), ynCek(chkAts4IritasiMata),
                ynCek(chkAts4Keseleo), ynCek(chkAts4FrakturRingan), ynCek(chkAts4BalutanKetat), ynCek(chkAts4SendiBengkak),
                ynCek(chkAts4SukarMenelan), ynCek(chkAts4MasalahMental),
                // ATS5 (10) - sesuai DB: nyeri_ringan, nyeri_sedang_mual, nyeri_perut, trauma_dada, riwayat_rendah, gejala_ringan, luka_kecil, kontrol_luka, imunisasi, psikiatrik
                ynCek(chkAts5NyeriRingan), ynCek(chkAts5NyeriSedangMual), ynCek(chkAts5NyeriPerutNonSpesifik), ynCek(chkAts5TraumaDada),
                ynCek(chkAts5RiwayatRisikoRendah), ynCek(chkAts5GejalaRingan), ynCek(chkAts5LukaKecil), ynCek(chkAts5KontrolLuka),
                ynCek(chkAts5Imunisasi), ynCek(chkAts5PerilakuPsikiatrik),
                // NIP
                KdPetugas.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from triase_igd_ats where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from triase_igd_ats where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            String nipp=KdPetugas.getText().isEmpty()?Sequel.cariIsi("select nip from triase_igd_ats where no_rawat=?",TNoRw.getText()):KdPetugas.getText();
            String finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",nipp);
            p.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+NmPetugas.getText()+"\nID "+(finger.isEmpty()?nipp:finger));
            Valid.MyReportqry("rptTriaseIGDATS.jasper","report","::[ Triase IGD ATS ]::",
                "select s.*,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.no_ktp,pt.nama as nm_petugas "+
                "from triase_igd_ats s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip "+
                "where s.no_rawat='"+TNoRw.getText()+"'",p);
        });

        // ASSEMBLY
        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout());ifd.setBackground(new Color(255,255,255));
        ifd.add(PanelInput,BorderLayout.NORTH);ifd.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    private String ynCek(CekBox cb) {
        return cb.isSelected() ? "Ya" : "Tidak";
    }

    private void setCek(CekBox cb, String val) {
        cb.setSelected("Ya".equals(val));
    }

    private String getKategoriATS() {
        if (RdKategori1.isSelected()) return "1";
        if (RdKategori2.isSelected()) return "2";
        if (RdKategori3.isSelected()) return "3";
        if (RdKategori4.isSelected()) return "4";
        if (RdKategori5.isSelected()) return "5";
        return "3";
    }

    private void setKategoriATS(String kat) {
        if (kat==null) { RdKategori3.setSelected(true); return; }
        if (kat.equals("1")) RdKategori1.setSelected(true);
        else if (kat.equals("2")) RdKategori2.setSelected(true);
        else if (kat.equals("3")) RdKategori3.setSelected(true);
        else if (kat.equals("4")) RdKategori4.setSelected(true);
        else if (kat.equals("5")) RdKategori5.setSelected(true);
        else RdKategori3.setSelected(true);
    }

    private void isForm(){if(ChkInput.isSelected()){ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,460));FormInput.setVisible(true);ChkInput.setVisible(true);}else{ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);}}

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                "select s.no_rawat,r.no_rkm_medis,p.nm_pasien,s.tanggal,s.kategori_ats,s.keluhan_utama,pt.nama as nm_petugas "+
                "from triase_igd_ats s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip "+
                "where s.tanggal between ? and ? and (s.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by s.tanggal desc");
            ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3,"%"+TCari.getText().trim()+"%");
            ps.setString(4,"%"+TCari.getText().trim()+"%");
            ps.setString(5,"%"+TCari.getText().trim()+"%");
            rs=ps.executeQuery();
            while(rs.next()){
                String kat=rs.getString("kategori_ats");
                String katLabel=kat.equals("1")?"ATS 1 - Resusitasi":(kat.equals("2")?"ATS 2 - Emergency":(kat.equals("3")?"ATS 3 - Urgent":(kat.equals("4")?"ATS 4 - Semi Darurat":(kat.equals("5")?"ATS 5 - Tidak Darurat":kat))));
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                    rs.getString("tanggal"),katLabel,rs.getString("keluhan_utama"),
                    rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : "+tabMode.getRowCount());
        }catch(Exception e){System.out.println("Notif Triase IGD ATS : "+e);
        }finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()!=-1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            try{
                ps=koneksi.prepareStatement(
                    "select * from triase_igd_ats where no_rawat=? and tanggal=?");
                ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
                ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
                rs=ps.executeQuery();
                if(rs.next()){
                    TKeluhanUtama.setText(rs.getString("keluhan_utama"));
                    setKategoriATS(rs.getString("kategori_ats"));
                    TTekananDarah.setText(rs.getString("tekanan_darah"));
                    TNadi.setText(rs.getString("nadi"));
                    TNafas.setText(rs.getString("nafas"));
                    TSpO2.setText(rs.getString("spo2"));
                    TSuhu.setText(rs.getString("suhu"));
                    String gcs=rs.getString("gcs");
                    if(gcs!=null&&gcs.contains("/")){String[]g=gcs.split("/");if(g.length>=3){TGCSE.setText(g[0]);TGCSV.setText(g[1]);TGCSM.setText(g[2]);}}
                    TReflexCahaya.setText(rs.getString("reflex_cahaya"));
                    String pupil=rs.getString("pupil");
                    if(pupil!=null&&pupil.contains("/")){String[]pp=pupil.split("/");if(pp.length>=2){TPupilKiri.setText(pp[0]);TPupilKanan.setText(pp[1]);}}
                    TAkral.setText(rs.getString("akral"));
                    TAlergiObat.setText(rs.getString("alergi_obat"));
                    TAlergiMakanan.setText(rs.getString("alergi_makanan"));
                    TAlergiLain.setText(rs.getString("alergi_lain"));
                    // ATS1
                    setCek(chkAts1HentiJantung,rs.getString("ats1_henti_jantung"));
                    setCek(chkAts1HentiNafas,rs.getString("ats1_henti_nafas"));
                    setCek(chkAts1RisikoSumbatanNapas,rs.getString("ats1_sumbatan_jalan_napas"));
                    setCek(chkAts1RRKurang10,rs.getString("ats1_rr_kurang10"));
                    setCek(chkAts1RespirasiDistress,rs.getString("ats1_resp_distress"));
                    setCek(chkAts1TekSistolik,rs.getString("ats1_tek_sistolik"));
                    setCek(chkAts1GCSKurang9,rs.getString("ats1_gcs_kurang9"));
                    setCek(chkAts1KejangTerus,rs.getString("ats1_kejang"));
                    // ATS2
                    setCek(chkAts2StridorBerat,rs.getString("ats2_stridor"));
                    setCek(chkAts2HRExtreme,rs.getString("ats2_hr"));
                    setCek(chkAts2KulitLembab,rs.getString("ats2_kulit_lembab"));
                    setCek(chkAts2PerdarahanBerat,rs.getString("ats2_perdarahan_berat"));
                    setCek(chkAts2OverdosisObat,rs.getString("ats2_overdosis"));
                    setCek(chkAts2GangguanPerilaku,rs.getString("ats2_gangguan_perilaku"));
                    setCek(chkAts2PernafasanDangkal,rs.getString("ats2_pernafasan_dangkal"));
                    setCek(chkAts2SaO2Kurang90,rs.getString("ats2_sao2"));
                    setCek(chkAts2SesakBerat,rs.getString("ats2_sesak_berat"));
                    setCek(chkAts2PenurunanKesadaran,rs.getString("ats2_penurunan_kesadaran"));
                    setCek(chkAts2HemiparaseAkut,rs.getString("ats2_hemiparese"));
                    setCek(chkAts2NyeriDadaKardiak,rs.getString("ats2_nyeri_dada"));
                    setCek(chkAts2DemamKelemahan,rs.getString("ats2_demam"));
                    setCek(chkAts2HemiparesiDisfagia,rs.getString("ats2_hemipareisi"));
                    setCek(chkAts2MataAlkaliAsam,rs.getString("ats2_mata_alkali"));
                    setCek(chkAts2MultipleTraumaMayor,rs.getString("ats2_multiple_trauma"));
                    setCek(chkAts2TraumaBerat,rs.getString("ats2_trauma_berat"));
                    setCek(chkAts2MinumSedative,rs.getString("ats2_minum_sedative"));
                    setCek(chkAts2KenaBisaBinatang,rs.getString("ats2_bisa_binatang"));
                    setCek(chkAts2NyeriHebat,rs.getString("ats2_nyeri_hebat"));
                    setCek(chkAts2GaduhGelisah,rs.getString("ats2_gaduh_gelisah"));
                    // ATS3 Obs Respirasi
                    setCek(chkAts3BatukDemamSesak,rs.getString("ats3_batuk_dahak"));
                    setCek(chkAts3BatukNyeriSesak,rs.getString("ats3_batuk_nyeri"));
                    setCek(chkAts3BatukDarah,rs.getString("ats3_batuk_darah"));
                    setCek(chkAts3SesakAsma,rs.getString("ats3_sesak_asma"));
                    setCek(chkAts3SesakTumor,rs.getString("ats3_sesak_tumor"));
                    setCek(chkAts3SesakPPOK,rs.getString("ats3_sesak_ppok"));
                    setCek(chkAts3SesakTB,rs.getString("ats3_sesak_tb"));
                    setCek(chkAts3SesakSpO2,rs.getString("ats3_sesak_spo2"));
                    // ATS3 Non Respirasi
                    setCek(chkAts3HipertensiBerat,rs.getString("ats3_hipertensi"));
                    setCek(chkAts3PerdarahanSedang,rs.getString("ats3_perdarahan_sedang"));
                    setCek(chkAts3RiwayatKejang,rs.getString("ats3_riwayat_kejang"));
                    setCek(chkAts3MuntahMenetap,rs.getString("ats3_muntah"));
                    setCek(chkAts3Dehidrasi,rs.getString("ats3_dehidrasi"));
                    setCek(chkAts3CederaKepala,rs.getString("ats3_cedera_kepala"));
                    setCek(chkAts3NyeriSedangBerat,rs.getString("ats3_nyeri_sedang"));
                    setCek(chkAts3NyeriNonKardiak,rs.getString("ats3_nyeri_non_kardiak"));
                    setCek(chkAts3SakitPerut,rs.getString("ats3_sakit_perut"));
                    setCek(chkAts3TraumaExtremitas,rs.getString("ats3_trauma_extremitas"));
                    setCek(chkAts3ExtremitasTidakSensasi,rs.getString("ats3_extremitas_sensasi"));
                    setCek(chkAts3TraumaRisikoTinggi,rs.getString("ats3_trauma_risiko"));
                    setCek(chkAts3StableNeonatus,rs.getString("ats3_stable_neonatus"));
                    setCek(chkAts3StressBerat,rs.getString("ats3_stress_berat"));
                    // ATS4
                    setCek(chkAts4PerdarahanRingan,rs.getString("ats4_perdarahan_ringan"));
                    setCek(chkAts4AspirasiBendaAsing,rs.getString("ats4_aspirasi"));
                    setCek(chkAts4CKR,rs.getString("ats4_ckr"));
                    setCek(chkAts4IritasiMata,rs.getString("ats4_iritasi_mata"));
                    setCek(chkAts4Keseleo,rs.getString("ats4_keseleo"));
                    setCek(chkAts4FrakturRingan,rs.getString("ats4_fraktur_ringan"));
                    setCek(chkAts4BalutanKetat,rs.getString("ats4_balutan_ketat"));
                    setCek(chkAts4SendiBengkak,rs.getString("ats4_sendi_bengkak"));
                    setCek(chkAts4SukarMenelan,rs.getString("ats4_sukar_menelan"));
                    setCek(chkAts4MasalahMental,rs.getString("ats4_kesehatan_mental"));
                    // ATS5
                    setCek(chkAts5NyeriRingan,rs.getString("ats5_nyeri_ringan"));
                    setCek(chkAts5NyeriSedangMual,rs.getString("ats5_nyeri_sedang_mual"));
                    setCek(chkAts5NyeriPerutNonSpesifik,rs.getString("ats5_nyeri_perut"));
                    setCek(chkAts5TraumaDada,rs.getString("ats5_trauma_dada"));
                    setCek(chkAts5RiwayatRisikoRendah,rs.getString("ats5_riwayat_rendah"));
                    setCek(chkAts5GejalaRingan,rs.getString("ats5_gejala_ringan"));
                    setCek(chkAts5LukaKecil,rs.getString("ats5_luka_kecil"));
                    setCek(chkAts5KontrolLuka,rs.getString("ats5_kontrol_luka"));
                    setCek(chkAts5Imunisasi,rs.getString("ats5_imunisasi"));
                    setCek(chkAts5PerilakuPsikiatrik,rs.getString("ats5_psikiatrik"));
                    // Petugas
                    KdPetugas.setText(rs.getString("nip"));
                    NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?",rs.getString("nip")));
                }
            }catch(Exception e){System.out.println("Notif getData Triase : "+e);
            }finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){
        TglAsuhan.setDate(new Date());
        TKeluhanUtama.setText("");
        TTekananDarah.setText(""); TNadi.setText(""); TNafas.setText(""); TSpO2.setText(""); TSuhu.setText("");
        TGCSE.setText(""); TGCSV.setText(""); TGCSM.setText("");
        TReflexCahaya.setText(""); TPupilKiri.setText(""); TPupilKanan.setText(""); TAkral.setText("");
        TAlergiObat.setText(""); TAlergiMakanan.setText(""); TAlergiLain.setText("");
        // ATS1
        chkAts1HentiJantung.setSelected(false); chkAts1HentiNafas.setSelected(false);
        chkAts1RisikoSumbatanNapas.setSelected(false); chkAts1RRKurang10.setSelected(false);
        chkAts1RespirasiDistress.setSelected(false); chkAts1TekSistolik.setSelected(false);
        chkAts1GCSKurang9.setSelected(false); chkAts1KejangTerus.setSelected(false);
        // ATS2
        chkAts2StridorBerat.setSelected(false); chkAts2HRExtreme.setSelected(false);
        chkAts2KulitLembab.setSelected(false); chkAts2PerdarahanBerat.setSelected(false);
        chkAts2OverdosisObat.setSelected(false); chkAts2GangguanPerilaku.setSelected(false);
        chkAts2PernafasanDangkal.setSelected(false); chkAts2SaO2Kurang90.setSelected(false); chkAts2SesakBerat.setSelected(false);
        chkAts2PenurunanKesadaran.setSelected(false); chkAts2HemiparaseAkut.setSelected(false); chkAts2NyeriDadaKardiak.setSelected(false);
        chkAts2DemamKelemahan.setSelected(false); chkAts2HemiparesiDisfagia.setSelected(false); chkAts2MataAlkaliAsam.setSelected(false);
        chkAts2MultipleTraumaMayor.setSelected(false); chkAts2TraumaBerat.setSelected(false); chkAts2MinumSedative.setSelected(false);
        chkAts2KenaBisaBinatang.setSelected(false); chkAts2NyeriHebat.setSelected(false); chkAts2GaduhGelisah.setSelected(false);
        // ATS3
        chkAts3BatukDemamSesak.setSelected(false); chkAts3BatukNyeriSesak.setSelected(false);
        chkAts3BatukDarah.setSelected(false); chkAts3SesakAsma.setSelected(false);
        chkAts3SesakTumor.setSelected(false); chkAts3SesakPPOK.setSelected(false);
        chkAts3SesakTB.setSelected(false); chkAts3SesakSpO2.setSelected(false);
        chkAts3HipertensiBerat.setSelected(false); chkAts3PerdarahanSedang.setSelected(false);
        chkAts3RiwayatKejang.setSelected(false); chkAts3MuntahMenetap.setSelected(false);
        chkAts3Dehidrasi.setSelected(false); chkAts3CederaKepala.setSelected(false);
        chkAts3NyeriSedangBerat.setSelected(false); chkAts3NyeriNonKardiak.setSelected(false);
        chkAts3SakitPerut.setSelected(false); chkAts3TraumaExtremitas.setSelected(false);
        chkAts3ExtremitasTidakSensasi.setSelected(false); chkAts3TraumaRisikoTinggi.setSelected(false);
        chkAts3StableNeonatus.setSelected(false); chkAts3StressBerat.setSelected(false);
        // ATS4
        chkAts4PerdarahanRingan.setSelected(false); chkAts4AspirasiBendaAsing.setSelected(false);
        chkAts4CKR.setSelected(false); chkAts4IritasiMata.setSelected(false);
        chkAts4Keseleo.setSelected(false); chkAts4FrakturRingan.setSelected(false);
        chkAts4BalutanKetat.setSelected(false); chkAts4SendiBengkak.setSelected(false);
        chkAts4SukarMenelan.setSelected(false); chkAts4MasalahMental.setSelected(false);
        // ATS5
        chkAts5NyeriRingan.setSelected(false); chkAts5NyeriSedangMual.setSelected(false);
        chkAts5NyeriPerutNonSpesifik.setSelected(false); chkAts5TraumaDada.setSelected(false);
        chkAts5RiwayatRisikoRendah.setSelected(false); chkAts5GejalaRingan.setSelected(false);
        chkAts5LukaKecil.setSelected(false); chkAts5KontrolLuka.setSelected(false);
        chkAts5Imunisasi.setSelected(false); chkAts5PerilakuPsikiatrik.setSelected(false);
        // Kategori & Petugas
        RdKategori3.setSelected(true);
        KdPetugas.setText(""); NmPetugas.setText("");
    }

    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}

    public void setNoRm(String n,Date t){
        TNoRw.setText(n);TglAsuhan.setDate(t);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));
        loadAlergiPasien();
    }

    public void isCek(){
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
