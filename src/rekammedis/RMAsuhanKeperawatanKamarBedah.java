package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import widget.Button;
import widget.InternalFrame;
import widget.Label;
import widget.TextBox;
import widget.panelisi;
import widget.CekBox;
import widget.PanelBiasa;
import widget.ScrollPane;

public class RMAsuhanKeperawatanKamarBedah extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelAtas;
    private panelisi panelBawah;
    private JTabbedPane tabPane;
    
    private Button BtnSimpan;
    private Button BtnBatal;
    private Button BtnHapus;
    private Button BtnEdit;
    private Button BtnPrint;
    private Button BtnKeluar;

    private fungsi.validasi Valid = new fungsi.validasi();
    private java.sql.Connection koneksi = fungsi.koneksiDB.condb();
    private fungsi.sekuel Sequel = new fungsi.sekuel();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;

    private TextBox TNoRw;
    private TextBox TNoRM;
    private TextBox TPasien;
    private widget.Tanggal TglAsuhan;
    private TextBox TUmur;
    private TextBox TKamar;
    private TextBox TTglMasuk;
    private TextBox TAlamat;
    private TextBox KdPetugas;
    private TextBox NmPetugas;
    private Button BtnPetugas;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    
    // Lookups for Data Awal
    private kepegawaian.DlgCariDokter dokterBedah = new kepegawaian.DlgCariDokter(null, false);
    private kepegawaian.DlgCariDokter dokterBedah2 = new kepegawaian.DlgCariDokter(null, false);
    private kepegawaian.DlgCariDokter dokterAnestesi = new kepegawaian.DlgCariDokter(null, false);
    private DlgCariPetugas asistenBedah = new DlgCariPetugas(null, false);
    private DlgCariPetugas asistenAnestesi = new DlgCariPetugas(null, false);
    private DlgCariPetugas instrumenDlg = new DlgCariPetugas(null, false);
    private DlgCariPetugas penataDlg = new DlgCariPetugas(null, false);
    private DlgCariPetugas perawatRuangPulihDlg = new DlgCariPetugas(null, false);
    private DlgCariPetugas perawatRuanganDlg = new DlgCariPetugas(null, false);
    private DlgCariPetugas sirkulasi = new DlgCariPetugas(null, false);

    private widget.InternalFrame internalFrameDataAwal, internalFramePre, internalFrameMasalah, internalFrameIntra, internalFramePost, internalFrameDiagnosa;

    // Tab Components
    private widget.CekBox ChkInputDataAwal, ChkInputPre, ChkInputMasalah, ChkInputIntra, ChkInputPost, ChkInputDiagnosa;
    private widget.PanelBiasa FormInputDataAwal, FormInputPre, FormInputMasalah, FormInputIntra, FormInputPost, FormInputDiagnosa;
    private widget.ScrollPane ScrollInputDataAwal, ScrollInputPre, ScrollInputMasalah, ScrollInputIntra, ScrollInputPost, ScrollInputDiagnosa;
    private javax.swing.JPanel PanelInputDataAwal, PanelInputPre, PanelInputMasalah, PanelInputIntra, PanelInputPost, PanelInputDiagnosa;
    private widget.Table tbDataAwal, tbPre, tbMasalah, tbIntra, tbPost, tbDiagnosa;
    private widget.ScrollPane ScrollDataAwal, ScrollPre, ScrollMasalah, ScrollIntra, ScrollPost, ScrollDiagnosa;
    private javax.swing.table.DefaultTableModel tabModeDataAwal, tabModePre, tabModeMasalah, tabModeIntra, tabModePost, tabModeDiagnosa;

    // Fields for Data Awal
    private widget.Tanggal TglMasukOK;
    private widget.TextBox KdDokterBedah, NmDokterBedah, KdDokterBedah2, NmDokterBedah2, KdAsistenBedah, NmAsistenBedah;
    private widget.TextBox KdDokterAnestesi, NmDokterAnestesi, KdAsistenAnestesi, NmAsistenAnestesi;
    private widget.TextBox KdInstrumen, NmInstrumen, KdPenata, NmPenata;
    private widget.TextBox KdSirkulasi, NmSirkulasi;
    private widget.Button BtnDokterBedah, BtnDokterBedah2, BtnAsistenBedah, BtnDokterAnestesi, BtnAsistenAnestesi, BtnSirkulasi, BtnInstrumen, BtnPenata;
    private widget.TextBox TDiagnosaPreOp, TTindakan, TJenisAnestesi, TCatatan, TKamarOperasi;
    private widget.Tanggal TWaktuOperasiMulai, TWaktuOperasiSelesai;
    private widget.Tanggal TKeluarRRpk;

    private widget.CekBox ChkIdentitas, ChkInformend, ChkVerifikasiIsolasi, ChkVerifikasiAlergi, ChkVerifikasiTindakan,
            ChkLevamen, ChkMarkes, ChkDingin, ChkHangat, ChkKering, ChkLembab, ChkUtuh,
            ChkCompos, ChkApatis, ChkSomnolen, ChkSoporcoma, ChkComa,
            ChkSensorikTidak, ChkSensorikDengar, ChkSensorikBahasa, ChkSensorikBicara,
            ChkMuskuloTidak, ChkMuskuloLumpuh, ChkMuskuloTraksi,
            ChkHearing, ChkKacamata, ChkAstma, ChkEKG, ChkThorax,
            ChkHepatitis, ChkRilek, ChkCemas, ChkBanyakBicara, ChkMenangis,
            ChkCemasRingan, ChkCemasSedang, ChkCemasBerat;
            
    private widget.Tanggal TPuasaJam, TPengukuran;
    private widget.TextBox TPuasa, TGCSM, TGCSV, TGCSE, TKesimpulan,
            TKekuatanOtot, TProtese, TEdema, TPernapasan, TLainPenyakit;
            
    // Masalah Keperawatan
    private widget.CekBox ChkKajiKecemasan, ChkJalinTrust, ChkObservasiTTV, ChkDampingi, ChkAjakUngkap, ChkCiptaLingkungan, ChkDiskusikan, ChkKolabObatPenenang, ChkEvaluasiRespon, ChkKolabTerapi;
    private widget.TextBox TNyeriBD, TKolabTerapi, TKajiTTVMenit;
    private widget.CekBox ChkKajiTingkatNyeri, ChkAjarkanTeknikRelaksasi, ChkKajiTTV;
    private widget.TextBox TAnestesiLama, TPembedahanLama;

    private widget.TextArea TUSG, TCTScan, TMRI, TLainPendukung, TLab;
    private widget.ScrollPane ScrollUSG, ScrollCTScan, ScrollMRI, ScrollLainPendukung, ScrollLab;
    
    private widget.CekBox ChkIntraHipo1, ChkIntraHipo2, ChkIntraHipo3, ChkIntraHipo4,
            ChkIntraKulitPosisiSupine, ChkIntraKulitPosisiProne, ChkIntraKulitPosisiLithotomy, ChkIntraKulitPosisiLateral, ChkIntraKulitPosisiLateralKiri, ChkIntraKulitPosisiLateralKanan,
            ChkIntraBantal, ChkIntraPasir, ChkIntraPenyangga, ChkIntraPaddingSiku, ChkIntraPaddingPoplitea,
            ChkIntraSabuk, ChkIntraPlate, ChkIntraKulitUtuh, ChkIntraKulitMerah, ChkIntraKulitLecet, ChkIntraKulitLuka,
            ChkIntraTorniquet, ChkIntraKassa1, ChkIntraKassa2, ChkIntraKassa3, ChkIntraJarum1, ChkIntraJarum2, ChkIntraJarum3, ChkIntraInstrumen1, ChkIntraInstrumen2, ChkIntraInstrumen3,
            ChkIntraSpinal, ChkIntraPengeluaran;
    private widget.TextBox TIntraSuhu, TIntraOlehHipo, TIntraPosisiLain, TIntraBantuLain, TIntraPlateOleh, TIntraCouterSetting, TIntraCuttingSetting, TIntraTorniquetOleh, TIntraInflate, TIntraDeflate, TIntraTekanan,
            TIntraInfus, TIntraUrine, TIntraInfusJenis, TIntraCairanLain, TIntraSabukPada, TIntraPlatePada, TIntraTorniquetPada;
    private widget.CekBox ChkIntraHitungXRayYa, ChkIntraHitungXRayTidak, ChkIntraPemberianDarahYa, ChkIntraPemberianDarahTidak;
    private widget.TextBox TIntraHitungXRayKet;
            
    // Intra Operasi - Resti Infeksi
    private widget.CekBox ChkKulitUtuhPreOp, ChkLainRestiInfeksi, ChkAseptik, ChkLukaBersih, ChkLukaBersihTercemar, ChkLukaTercemar, ChkLukaKotor, ChkPasangKateterUrine, ChkDrain, ChkTampon, ChKGips;
    private widget.TextBox TLainRestiInfeksiOleh, TAseptikDg, TAseptikOleh, TKateterNo, TKateterUrineCc, TKateterOleh, TDrainJenis, TDrainCairanCc, TTamponJenis, TTamponLokasi, TGipsJenis, TGipsLokasi, TIntraNGT, TIntraDarahYa, TIntraDarahTidak;

    // Post Operasi
    private widget.TextBox TPostMasukRR;
    private widget.CekBox ChkPostUmum, ChkPostSpinalRegional, ChkPostBlokPerifer;
    private widget.CekBox ChkPostDingin, ChkPostHangat, ChkPostKering, ChkPostUtuh, ChkPostLembab, ChkPostPink;
    private widget.CekBox ChkPostIntubasi, ChkPostEkstubasi, ChkPostSpontan, ChkPostSpasme, ChkPostSekret;
    private widget.CekBox ChkPostBangun, ChkPostSadar;
    private widget.CekBox ChkPostTerlentang, ChkPostLateralKiriKanan, ChkPostEkstremitasBawah;
    private widget.TextBox TPostTD, TPostHR, TPostRR, TPostSpO2;
    private widget.TextBox TPostDiagnosa, TPostDrain, TPostTampon, TPostPA, TPostResp;
    
    // Diagnosa Post Operasi
    private widget.CekBox ChkDiagNafas, ChkNafasSekret, ChkNafasAnastesi;
    private widget.CekBox ChkSuctionYa, ChkSuctionTidak;
    private widget.CekBox ChkNapasSpontanO2Nasal, ChkNapasSpontanO2Oral, ChkNapasDibantuBagging, ChkNapasDibantuTriple, ChkNapasDibantuO2Mask;
    private widget.TextBox TSpontanO2Nasal, TSpontanO2Oral, TKolabPemberianObat;
    private widget.CekBox ChkDiagHipotermi, ChkHipoLingkungan, ChkHipoAnastesi;
    private widget.CekBox ChkHipoSelimut, ChkHipoElevasi, ChkHipoKolab, ChkHipoLain;
    private widget.TextBox THipoKolab, THipoLain;
    private widget.TextBox KdPerawatRuangPulih, NmPerawatRuangPulih, KdPerawatRuangan, NmPerawatRuangan;
    private widget.Button BtnPerawatRuangPulih, BtnPerawatRuangan;
    
    // Pencarian
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.TextBox TCari;
    private widget.Button BtnCari, BtnAll;
    private widget.Label LCount;
    
    // Tabel untuk memenuhi dependensi dari form lain
    public widget.Table tbAnestesi, tbAnestesi1, tbAnestesi2, tbAnestesi3, tbAnestesi4;
    public widget.Table tbAnestesi5, tbAnestesi6, tbAnestesi9, tbAnestesi10, tbAnestesi11, tbAnestesi12;

    public widget.editorpane LoadHTML;

    public RMAsuhanKeperawatanKamarBedah(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Inisialisasi variabel dummy agar tidak NPE dari form lain
        tbAnestesi = new widget.Table();
        tbAnestesi1 = new widget.Table();
        tbAnestesi2 = new widget.Table();
        tbAnestesi3 = new widget.Table();
        tbAnestesi4 = new widget.Table();
        tbAnestesi5 = new widget.Table();
        tbAnestesi6 = new widget.Table();
        tbAnestesi9 = new widget.Table();
        tbAnestesi10 = new widget.Table();
        tbAnestesi11 = new widget.Table();
        tbAnestesi12 = new widget.Table();

        LoadHTML = new widget.editorpane();

        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)), 
            "::[ Asuhan Keperawatan Kamar Bedah ]::", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new java.awt.Font("Tahoma", 0, 11), 
            new Color(50, 50, 50)
        ));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS (Header Pasien + Tim Operasi)
        panelAtas = new panelisi();
        panelAtas.setLayout(null);
        panelAtas.setPreferredSize(new Dimension(1024, 215));

        // Row 1: No.Rawat, No.RM, Nama, Umur, Kelas/Kamar
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :"); lblNoRw.setBounds(5, 5, 70, 23);
        TNoRw = new TextBox(); TNoRw.setBounds(80, 5, 130, 23);
        TNoRM = new TextBox(); TNoRM.setBounds(215, 5, 90, 23);
        TPasien = new TextBox(); TPasien.setBounds(310, 5, 180, 23);
        Label lblUmur = new Label(); lblUmur.setText("Umur :"); lblUmur.setBounds(500, 5, 40, 23);
        TUmur = new TextBox(); TUmur.setBounds(545, 5, 60, 23);
        Label lblKamar = new Label(); lblKamar.setText("Kelas/Kamar :"); lblKamar.setBounds(615, 5, 80, 23);
        TKamar = new TextBox(); TKamar.setBounds(700, 5, 160, 23);

        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);
        panelAtas.add(lblUmur); panelAtas.add(TUmur); panelAtas.add(lblKamar); panelAtas.add(TKamar);

        // Row 2: Tanggal, Tgl Masuk, Alamat, Petugas
        Label lblTanggal = new Label(); lblTanggal.setText("Tanggal :"); lblTanggal.setBounds(5, 30, 70, 23);
        TglAsuhan = new widget.Tanggal();
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(80, 30, 130, 23);
        Label lblTglMasuk = new Label(); lblTglMasuk.setText("Tgl.Masuk :"); lblTglMasuk.setBounds(215, 30, 70, 23);
        TTglMasuk = new TextBox(); TTglMasuk.setBounds(290, 30, 90, 23);
        Label lblAlamat = new Label(); lblAlamat.setText("Alamat :"); lblAlamat.setBounds(390, 30, 50, 23);
        TAlamat = new TextBox(); TAlamat.setBounds(445, 30, 160, 23);
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(615, 30, 55, 23);
        KdPetugas = new TextBox(); KdPetugas.setBounds(675, 30, 70, 23);
        NmPetugas = new TextBox(); NmPetugas.setBounds(747, 30, 130, 23);
        BtnPetugas = new Button();
        java.net.URL petugasURL = getClass().getResource("/picture/011.png");
        if (petugasURL != null) { BtnPetugas.setIcon(new javax.swing.ImageIcon(petugasURL)); }
        BtnPetugas.setBounds(879, 30, 28, 23);
        BtnPetugas.addActionListener(evt -> {
            petugas.emptTeks(); petugas.isCek();
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1); petugas.setVisible(true);
        });
        petugas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        panelAtas.add(lblTanggal); panelAtas.add(TglAsuhan); panelAtas.add(lblTglMasuk); panelAtas.add(TTglMasuk);
        panelAtas.add(lblAlamat); panelAtas.add(TAlamat); panelAtas.add(lblPetugas);
        panelAtas.add(KdPetugas); panelAtas.add(NmPetugas); panelAtas.add(BtnPetugas);

        // Row 3-6: Tim Operasi (dari tabel operasi)
        int tyL = 58; // y start for tim kiri
        int tyR = 58; // y start for tim kanan

        Label lblDB1 = new Label(); lblDB1.setText("Dokter Bedah 1 :"); lblDB1.setBounds(5, tyL, 100, 23);
        KdDokterBedah = new TextBox(); KdDokterBedah.setBounds(110, tyL, 70, 23);
        NmDokterBedah = new TextBox(); NmDokterBedah.setBounds(182, tyL, 150, 23);
        BtnDokterBedah = new Button(); BtnDokterBedah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterBedah.setBounds(334, tyL, 28, 23);
        panelAtas.add(lblDB1); panelAtas.add(KdDokterBedah); panelAtas.add(NmDokterBedah); panelAtas.add(BtnDokterBedah);

        Label lblDB2 = new Label(); lblDB2.setText("Dokter Bedah 2 :"); lblDB2.setBounds(5, tyL+25, 100, 23);
        KdDokterBedah2 = new TextBox(); KdDokterBedah2.setBounds(110, tyL+25, 70, 23);
        NmDokterBedah2 = new TextBox(); NmDokterBedah2.setBounds(182, tyL+25, 150, 23);
        BtnDokterBedah2 = new Button(); BtnDokterBedah2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterBedah2.setBounds(334, tyL+25, 28, 23);
        panelAtas.add(lblDB2); panelAtas.add(KdDokterBedah2); panelAtas.add(NmDokterBedah2); panelAtas.add(BtnDokterBedah2);

        Label lblAst = new Label(); lblAst.setText("Asisten :"); lblAst.setBounds(5, tyL+50, 100, 23);
        KdAsistenBedah = new TextBox(); KdAsistenBedah.setBounds(110, tyL+50, 70, 23);
        NmAsistenBedah = new TextBox(); NmAsistenBedah.setBounds(182, tyL+50, 150, 23);
        BtnAsistenBedah = new Button(); BtnAsistenBedah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnAsistenBedah.setBounds(334, tyL+50, 28, 23);
        panelAtas.add(lblAst); panelAtas.add(KdAsistenBedah); panelAtas.add(NmAsistenBedah); panelAtas.add(BtnAsistenBedah);

        Label lblIns = new Label(); lblIns.setText("Instrumen :"); lblIns.setBounds(5, tyL+75, 100, 23);
        KdInstrumen = new TextBox(); KdInstrumen.setBounds(110, tyL+75, 70, 23);
        NmInstrumen = new TextBox(); NmInstrumen.setBounds(182, tyL+75, 150, 23);
        BtnInstrumen = new Button(); BtnInstrumen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnInstrumen.setBounds(334, tyL+75, 28, 23);
        panelAtas.add(lblIns); panelAtas.add(KdInstrumen); panelAtas.add(NmInstrumen); panelAtas.add(BtnInstrumen);

        // Kolom kanan
        int xR = 380;
        Label lblDA = new Label(); lblDA.setText("Dokter Anestesi :"); lblDA.setBounds(xR, tyR, 100, 23);
        KdDokterAnestesi = new TextBox(); KdDokterAnestesi.setBounds(xR+105, tyR, 70, 23);
        NmDokterAnestesi = new TextBox(); NmDokterAnestesi.setBounds(xR+177, tyR, 150, 23);
        BtnDokterAnestesi = new Button(); BtnDokterAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterAnestesi.setBounds(xR+329, tyR, 28, 23);
        panelAtas.add(lblDA); panelAtas.add(KdDokterAnestesi); panelAtas.add(NmDokterAnestesi); panelAtas.add(BtnDokterAnestesi);

        Label lblPen = new Label(); lblPen.setText("Penata :"); lblPen.setBounds(xR, tyR+25, 100, 23);
        KdPenata = new TextBox(); KdPenata.setBounds(xR+105, tyR+25, 70, 23);
        NmPenata = new TextBox(); NmPenata.setBounds(xR+177, tyR+25, 150, 23);
        BtnPenata = new Button(); BtnPenata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPenata.setBounds(xR+329, tyR+25, 28, 23);
        panelAtas.add(lblPen); panelAtas.add(KdPenata); panelAtas.add(NmPenata); panelAtas.add(BtnPenata);

        Label lblAA = new Label(); lblAA.setText("Asisten Anestesi :"); lblAA.setBounds(xR, tyR+50, 100, 23);
        KdAsistenAnestesi = new TextBox(); KdAsistenAnestesi.setBounds(xR+105, tyR+50, 70, 23);
        NmAsistenAnestesi = new TextBox(); NmAsistenAnestesi.setBounds(xR+177, tyR+50, 150, 23);
        BtnAsistenAnestesi = new Button(); BtnAsistenAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnAsistenAnestesi.setBounds(xR+329, tyR+50, 28, 23);
        panelAtas.add(lblAA); panelAtas.add(KdAsistenAnestesi); panelAtas.add(NmAsistenAnestesi); panelAtas.add(BtnAsistenAnestesi);

        Label lblSirk = new Label(); lblSirk.setText("Sirkulasi :"); lblSirk.setBounds(xR, tyR+75, 100, 23);
        KdSirkulasi = new TextBox(); KdSirkulasi.setBounds(xR+105, tyR+75, 70, 23);
        NmSirkulasi = new TextBox(); NmSirkulasi.setBounds(xR+177, tyR+75, 150, 23);
        BtnSirkulasi = new Button(); BtnSirkulasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnSirkulasi.setBounds(xR+329, tyR+75, 28, 23);
        panelAtas.add(lblSirk); panelAtas.add(KdSirkulasi); panelAtas.add(NmSirkulasi); panelAtas.add(BtnSirkulasi);

        // Kamar Operasi & Waktu Operasi (dari booking_operasi)
        Label lblKmrOp2 = new Label(); lblKmrOp2.setText("Kamar Operasi :"); lblKmrOp2.setBounds(5, tyR+100, 100, 23);
        TKamarOperasi = new TextBox(); TKamarOperasi.setBounds(110, tyR+100, 150, 23);
        Label lblWktOp2 = new Label(); lblWktOp2.setText("Waktu Operasi :"); lblWktOp2.setBounds(280, tyR+100, 95, 23);
        TWaktuOperasiMulai = new widget.Tanggal(); TWaktuOperasiMulai.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TWaktuOperasiMulai.setBounds(380, tyR+100, 130, 23);
        JLabel lblSdAtas = new JLabel("s/d"); lblSdAtas.setBounds(515, tyR+100, 25, 23);
        TWaktuOperasiSelesai = new widget.Tanggal(); TWaktuOperasiSelesai.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TWaktuOperasiSelesai.setBounds(545, tyR+100, 130, 23);
        // Keluar RR (date picker)
        Label lblKeluarRR2 = new Label(); lblKeluarRR2.setText("Keluar RR pk :"); lblKeluarRR2.setBounds(530, tyR+125, 80, 23);
        TKeluarRRpk = new widget.Tanggal();
        TKeluarRRpk.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TKeluarRRpk.setBounds(615, tyR+125, 130, 23);
        panelAtas.add(lblKeluarRR2); panelAtas.add(TKeluarRRpk);

        // Dx Pre-Op (dari laporan_operasi)
        Label lblDxPre = new Label(); lblDxPre.setText("Dx.Pre-Op :"); lblDxPre.setBounds(5, tyR+125, 100, 23);
        TDiagnosaPreOp = new TextBox(); TDiagnosaPreOp.setBounds(110, tyR+125, 400, 23);
        panelAtas.add(lblDxPre); panelAtas.add(TDiagnosaPreOp);

        Label lblMasukOK = new Label(); lblMasukOK.setText("Masuk OK pk :"); lblMasukOK.setBounds(530, tyR+100, 80, 23);
        TglMasukOK = new widget.Tanggal();
        TglMasukOK.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglMasukOK.setBounds(615, tyR+100, 130, 23);

        panelAtas.add(lblKmrOp2); panelAtas.add(TKamarOperasi);
        panelAtas.add(lblWktOp2); panelAtas.add(TWaktuOperasiMulai); panelAtas.add(lblSdAtas); panelAtas.add(TWaktuOperasiSelesai);
        panelAtas.add(lblMasukOK); panelAtas.add(TglMasukOK);

        // PANEL TENGAH (TABBED PANE)
        tabPane = new JTabbedPane();
        tabPane.setBackground(new Color(255, 255, 255));
        
        initTabDataAwal();
        initTabPreOp();
        initTabMasalah();
        initTabIntraOp();
        initTabPostOp();
        initTabDiagnosa();
        
        tabPane.addTab("Data Awal", internalFrameDataAwal);
        tabPane.addTab("Pre Op", internalFramePre);
        tabPane.addTab("Masalah, Intervensi dan Implementasi", internalFrameMasalah);
        tabPane.addTab("Intra Operasi", internalFrameIntra);
        tabPane.addTab("Post Operasi", internalFramePost);
        tabPane.addTab("Diagnosa, Intervensi dan Implementasi", internalFrameDiagnosa);

        // Auto-load tabel tiap tab saat berpindah (sebelumnya hanya tab Data Awal
        // yang ter-isi; tab lain kosong sampai klik Cari/Semua → tampak "belum ada
        // tabel" & getData tak jalan karena tak ada baris).
        tabPane.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                if (DTPCari1 == null) return; // form belum selesai diinisialisasi
                switch (tabPane.getSelectedIndex()) {
                    case 0: tampilDataAwal(); break;
                    case 1: tampilPre();      break;
                    case 2: tampilMasalah();  break;
                    case 3: tampilIntra();    break;
                    case 4: tampilPost();     break;
                    case 5: tampilDiagnosa(); break;
                }
            }
        });

        // PANEL BAWAH (TOMBOL-TOMBOL)
        panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        BtnSimpan = new Button();
        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new Dimension(100, 30));

        BtnBatal = new Button();
        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setPreferredSize(new Dimension(100, 30));

        BtnHapus = new Button();
        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new Dimension(100, 30));

        BtnEdit = new Button();
        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setPreferredSize(new Dimension(100, 30));

        BtnPrint = new Button();
        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setPreferredSize(new Dimension(100, 30));

        BtnKeluar = new Button();
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));

        // Search Section in panelBawah
        widget.Label lblTgl = new widget.Label();
        lblTgl.setText("Tanggal :");
        lblTgl.setPreferredSize(new Dimension(55, 23));

        DTPCari1 = new widget.Tanggal();
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new Dimension(90, 23));

        widget.Label lblSampai = new widget.Label();
        lblSampai.setText("s.d.");
        lblSampai.setPreferredSize(new Dimension(25, 23));

        DTPCari2 = new widget.Tanggal();
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setPreferredSize(new Dimension(90, 23));

        widget.Label lblKeyword = new widget.Label();
        lblKeyword.setText("Key Word :");
        lblKeyword.setPreferredSize(new Dimension(70, 23));

        TCari = new widget.TextBox();
        TCari.setName("TCari");
        TCari.setPreferredSize(new Dimension(150, 23));

        BtnCari = new widget.Button();
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('4');
        BtnCari.setToolTipText("Alt+4");
        BtnCari.setPreferredSize(new Dimension(28, 23));

        BtnAll = new widget.Button();
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setPreferredSize(new Dimension(28, 23));

        LCount = new widget.Label();
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("Record : 0");
        LCount.setPreferredSize(new Dimension(100, 23));

        BtnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emptTeks();
            }
        });

        panelBawah.add(BtnSimpan);
        panelBawah.add(BtnBatal);
        panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit);
        panelBawah.add(BtnPrint);
        panelBawah.add(BtnKeluar);
        panelBawah.add(lblTgl);
        panelBawah.add(DTPCari1);
        panelBawah.add(lblSampai);
        panelBawah.add(DTPCari2);
        panelBawah.add(lblKeyword);
        panelBawah.add(TCari);
        panelBawah.add(BtnCari);
        panelBawah.add(BtnAll);
        panelBawah.add(LCount);

        BtnKeluar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        BtnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabPane.getSelectedIndex()==0) {
                    tampilDataAwal();
                } else if(tabPane.getSelectedIndex()==1) {
                    tampilPre();
                } else if(tabPane.getSelectedIndex()==2) {
                    tampilMasalah();
                } else if(tabPane.getSelectedIndex()==3) {
                    tampilIntra();
                } else if(tabPane.getSelectedIndex()==4) {
                    tampilPost();
                } else if(tabPane.getSelectedIndex()==5) {
                    tampilDiagnosa();
                }
            }
        });

        BtnAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TCari.setText("");
                if(tabPane.getSelectedIndex()==0) {
                    tampilDataAwal();
                } else if(tabPane.getSelectedIndex()==1) {
                    tampilPre();
                } else if(tabPane.getSelectedIndex()==2) {
                    tampilMasalah();
                } else if(tabPane.getSelectedIndex()==3) {
                    tampilIntra();
                } else if(tabPane.getSelectedIndex()==4) {
                    tampilPost();
                } else if(tabPane.getSelectedIndex()==5) {
                    tampilDiagnosa();
                }
            }
        });

        BtnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TNoRw.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
                } else {
                    Map<String, Object> param = new HashMap<>();
                    param.put("namars", akses.getnamars());
                    param.put("alamatrs", akses.getalamatrs());
                    param.put("propinsirs", akses.getpropinsirs());
                    param.put("kabupatenrs", akses.getkabupatenrs());
                    param.put("kontakrs", akses.getkontakrs());
                    param.put("emailrs", akses.getemailrs());
                    param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                    String tglCetak = Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19);
                    Valid.MyReportqry("rptAskepKamarBedah.jasper", "report", "::[ Laporan Asuhan Keperawatan Kamar Bedah ]::",
                            "select da.no_rawat, da.tanggal, r.no_rkm_medis, p.nm_pasien, p.alamat, concat(r.umurdaftar,' ',r.sttsumur) as umur, "
                            + "da.tindakan, da.jenis_anestesi, da.catatan, pt.nama as nm_petugas, "
                            + "pre.puasa, pre.kondisi_kulit, pre.preop_kesadaran, pre.gcs, pre.kesimpulan, pre.gangguan_sensorik, "
                            + "pre.muskuloskeletal, pre.protesa, pre.edema, pre.respirasi, pre.kondisi_psikologis, pre.laboratorium, pre.data_penunjang, "
                            + "ms.masalah_preop_kecemasan, ms.cemas_tingkat, ms.masalah_preop_nyeri, "
                            + "io.klasifikasi_luka, io.posisi_operasi, io.aseptik_antiseptik, "
                            + "po.jam_masuk_rr, po.td, po.hr, po.spo2, po.rr, po.jenis_anestesi_postop, po.kulit_postop, po.respirasi_postop, "
                            + "po.kesadaran, po.posisi_pasien, po.lama_anestesi, po.lama_operasi, po.diagnosa_postop, po.drain, po.tampon, po.pa, "
                            + "dg.efektifitas_jalan_nafas, dg.hipotermi_postop "
                            + "from asuhan_keperawatan_kamar_bedah_data_awal da "
                            + "inner join reg_periksa r on da.no_rawat=r.no_rawat "
                            + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
                            + "left join petugas pt on da.nip=pt.nip "
                            + "left join askep_kb_preop pre on da.no_rawat=pre.no_rawat and da.tanggal=pre.tanggal "
                            + "left join askep_kb_masalah ms on da.no_rawat=ms.no_rawat and da.tanggal=ms.tanggal "
                            + "left join askep_kb_intraop io on da.no_rawat=io.no_rawat and da.tanggal=io.tanggal "
                            + "left join askep_kb_postop po on da.no_rawat=po.no_rawat and da.tanggal=po.tanggal "
                            + "left join askep_kb_diagnosa dg on da.no_rawat=dg.no_rawat and da.tanggal=dg.tanggal "
                            + "where da.no_rawat='" + TNoRw.getText() + "' and da.tanggal='" + tglCetak + "'", param);
                }
            }
        });

        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER){
                    if(tabPane.getSelectedIndex()==0) {
                        tampilDataAwal();
                    } else if(tabPane.getSelectedIndex()==1) {
                        tampilPre();
                    } else if(tabPane.getSelectedIndex()==2) {
                        tampilMasalah();
                    } else if(tabPane.getSelectedIndex()==3) {
                        tampilIntra();
                    } else if(tabPane.getSelectedIndex()==4) {
                        tampilPost();
                    } else if(tabPane.getSelectedIndex()==5) {
                        tampilDiagnosa();
                    }
                }else if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_PAGE_UP){
                    BtnKeluar.requestFocus();
                }else if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_PAGE_DOWN){
                    BtnSimpan.requestFocus();
                }
            }
        });

        BtnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TNoRw.getText().trim().equals("") || TNoRM.getText().trim().equals("")) {
                    Valid.textKosong(TNoRw, "Pasien");
                } else if (KdPetugas.getText().trim().equals("")) {
                    Valid.textKosong(KdPetugas, "Petugas");
                } else {
                    String tglSimpan = Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19);
                    if(tabPane.getSelectedIndex()==0) {
                        if (Sequel.menyimpantf("asuhan_keperawatan_kamar_bedah_data_awal", "?,?,?,?,?,?", "Data", 6, new String[]{
                            TNoRw.getText(), tglSimpan, TTindakan.getText(), TJenisAnestesi.getText(), TCatatan.getText(), KdPetugas.getText()
                        })) { tampilDataAwal(); emptTeksDataAwal(); }
                    } else if(tabPane.getSelectedIndex()==1) {
                        String kondisiKulit = (ChkDingin.isSelected()?"Dingin ":"")+(ChkHangat.isSelected()?"Hangat ":"")+(ChkKering.isSelected()?"Kering ":"")+(ChkLembab.isSelected()?"Lembab ":"")+(ChkUtuh.isSelected()?"Utuh ":"");
                        String kesadaran = (ChkCompos.isSelected()?"Compos mentis ":"")+(ChkApatis.isSelected()?"Apatis ":"")+(ChkSomnolen.isSelected()?"Somnolen ":"")+(ChkSoporcoma.isSelected()?"Soporcoma ":"")+(ChkComa.isSelected()?"Coma ":"");
                        String sensorik = (ChkSensorikTidak.isSelected()?"Tidak ada ":"")+(ChkSensorikDengar.isSelected()?"Pendengaran ":"")+(ChkSensorikBahasa.isSelected()?"Bahasa ":"")+(ChkSensorikBicara.isSelected()?"Bicara ":"");
                        String muskulo = (ChkMuskuloTidak.isSelected()?"Tidak ada ":"")+(ChkMuskuloLumpuh.isSelected()?"Lumpuh ":"")+(ChkMuskuloTraksi.isSelected()?"Traksi ":"")+(TKekuatanOtot.getText().isEmpty()?"":":"+TKekuatanOtot.getText());
                        String psikologi = (ChkRilek.isSelected()?"Rilek ":"")+(ChkCemas.isSelected()?"Cemas ":"")+(ChkBanyakBicara.isSelected()?"Banyak Bicara ":"")+(ChkMenangis.isSelected()?"Menangis ":"");
                        if (Sequel.menyimpantf("askep_kb_preop", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 29, new String[]{
                            TNoRw.getText(), tglSimpan,
                            TPuasaJam.getSelectedItem().toString().substring(11, 19), ChkInformend.isSelected()?"Ya":"Tidak", ChkIdentitas.isSelected()?"Ya":"Tidak",
                            ChkLevamen.isSelected()?"Ya":"Tidak", ChkVerifikasiIsolasi.isSelected()?"Ya":"Tidak", ChkMarkes.isSelected()?"Ya":"Tidak",
                            ChkVerifikasiAlergi.isSelected()?"Ya":"", TPengukuran.getSelectedItem().toString().substring(11, 19), ChkVerifikasiTindakan.isSelected()?"Ya":"Tidak",
                            kondisiKulit, kesadaran, TGCSM.getText()+","+TGCSV.getText()+","+TGCSE.getText(),
                            sensorik, muskulo, TProtese.getText(), TEdema.getText(), ChkAstma.isSelected()?"Ya":"Tidak",
                            TPernapasan.getText(), ChkEKG.isSelected()?"Ya":"Tidak", ChkThorax.isSelected()?"Ya":"Tidak",
                            ChkHepatitis.isSelected()?"Ya":"Tidak", TLainPenyakit.getText(), TKesimpulan.getText(), psikologi,
                            TUSG.getText()+"|"+TCTScan.getText()+"|"+TMRI.getText()+"|"+TLainPendukung.getText(), TLab.getText(), KdPetugas.getText()
                        })) { tampilPre(); emptTeks(); }
                    } else if(tabPane.getSelectedIndex()==2) {
                        String cemasTingkat = (ChkCemasRingan.isSelected()?"Ringan ":"")+(ChkCemasSedang.isSelected()?"Sedang ":"")+(ChkCemasBerat.isSelected()?"Berat ":"");
                        String cemasIntervensi = (ChkKajiKecemasan.isSelected()?"Kaji tingkat kecemasan,":"")+(ChkJalinTrust.isSelected()?"Jalin trust dengan pasien,":"")+(ChkObservasiTTV.isSelected()?"Observasi tanda-tanda vital,":"")+(ChkDampingi.isSelected()?"Dampingi pasien,":"")+(ChkAjakUngkap.isSelected()?"Ajak pasien mengungkapkan,":"")+(ChkCiptaLingkungan.isSelected()?"Ciptakan lingkungan nyaman,":"")+(ChkDiskusikan.isSelected()?"Diskusikan situasi,":"")+(ChkKolabObatPenenang.isSelected()?"Kolaborasi obat penenang,":"")+(ChkEvaluasiRespon.isSelected()?"Evaluasi respon,":"");
                        String nyeriIntervensi = (ChkKajiTingkatNyeri.isSelected()?"Kaji tingkat nyeri pada lokasi,":"")+(ChkAjarkanTeknikRelaksasi.isSelected()?"Ajarkan teknik relaksasi,":"")+(ChkKolabTerapi.isSelected()?"Kolaborasi terapi "+TKolabTerapi.getText()+",":"")+(ChkKajiTTV.isSelected()?"Kaji TTV tiap "+TKajiTTVMenit.getText()+" menit,":"");
                        if (Sequel.menyimpantf("askep_kb_masalah", "?,?,?,?,?,?,?,?,?", "Data", 9, new String[]{
                            TNoRw.getText(), tglSimpan, TKolabTerapi.getText(), cemasTingkat, cemasIntervensi,
                            TNyeriBD.getText(), nyeriIntervensi, TKajiTTVMenit.getText(), KdPetugas.getText()
                        })) { tampilMasalah(); emptTeks(); }
                    } else if(tabPane.getSelectedIndex()==3) {
                        String posisiOp = (ChkIntraKulitPosisiSupine.isSelected()?"Supine ":"")+(ChkIntraKulitPosisiProne.isSelected()?"Prone ":"")+(ChkIntraKulitPosisiLithotomy.isSelected()?"Lithotomy ":"")+(ChkIntraKulitPosisiLateral.isSelected()?"Lateral ":"")+(ChkIntraKulitPosisiLateralKiri.isSelected()?"Lateral Kiri ":"")+(ChkIntraKulitPosisiLateralKanan.isSelected()?"Lateral Kanan ":"");
                        String alatPosisi = (ChkIntraBantal.isSelected()?"Bantal/guling ":"")+(ChkIntraPasir.isSelected()?"Bantal/Pasir ":"")+(ChkIntraPenyangga.isSelected()?"Penyangga Kaki ":"");
                        String padding = (ChkIntraPaddingSiku.isSelected()?"Siku ":"")+(ChkIntraPaddingPoplitea.isSelected()?"Poplitea ":"");
                        String klasifikasiLuka = (ChkLukaBersih.isSelected()?"bersih ":"")+(ChkLukaBersihTercemar.isSelected()?"Bersih tercemar ":"")+(ChkLukaTercemar.isSelected()?"Tercemar ":"")+(ChkLukaKotor.isSelected()?"Kotor ":"");
                        String kulitPlate = (ChkIntraKulitUtuh.isSelected()?"Utuh ":"")+(ChkIntraKulitMerah.isSelected()?"kemerahan ":"")+(ChkIntraKulitLecet.isSelected()?"Lecet ":"")+(ChkIntraKulitLuka.isSelected()?"Luka ":"");
                        if (Sequel.menyimpantf("askep_kb_intraop",
                            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                            "Data", 56, new String[]{
                            TNoRw.getText(), tglSimpan,
                            TAseptikDg.getText(), TAseptikOleh.getText(), TLainRestiInfeksiOleh.getText(), klasifikasiLuka,
                            ChkKulitUtuhPreOp.isSelected()?"Ya":"Tidak", ChkPasangKateterUrine.isSelected()?"Ya":"Tidak", TKateterNo.getText(), TKateterUrineCc.getText(), TKateterOleh.getText(),
                            ChkDrain.isSelected()?"Ya":"Tidak", TDrainJenis.getText(), TDrainCairanCc.getText(),
                            ChkTampon.isSelected()?"Ya":"Tidak", TTamponJenis.getText(), TTamponLokasi.getText(),
                            ChKGips.isSelected()?"Ya":"Tidak", TGipsJenis.getText(), TGipsLokasi.getText(),
                            TIntraSuhu.getText(), TIntraOlehHipo.getText(), ChkIntraHipo2.isSelected()?"Ya":"Tidak", ChkIntraHipo3.isSelected()?"Ya":"Tidak", ChkIntraHipo4.isSelected()?"Ya":"Tidak",
                            posisiOp, TIntraPosisiLain.getText(), alatPosisi, TIntraBantuLain!=null?TIntraBantuLain.getText():"",
                            padding, ChkIntraSabuk.isSelected()?"Ya":"Tidak", TIntraSabukPada.getText(),
                            ChkIntraPlate.isSelected()?"Ya":"Tidak", TIntraPlatePada.getText(), TIntraPlateOleh.getText(), kulitPlate,
                            TIntraCouterSetting.getText(), TIntraCuttingSetting.getText(),
                            ChkIntraTorniquet.isSelected()?"Ya":"Tidak", TIntraTekanan.getText(), TIntraTorniquetPada.getText(), TIntraTorniquetOleh.getText(),
                            TIntraInflate.getText(), TIntraDeflate.getText(),
                            ChkIntraHitungXRayYa.isSelected()?"Ya":"Tidak", TIntraHitungXRayKet.getText(),
                            TIntraInfus.getText(), TIntraInfusJenis.getText(), TIntraUrine.getText(), TIntraNGT.getText(), TIntraNGT.getText(), TIntraCairanLain.getText(),
                            ChkIntraPemberianDarahYa.isSelected()?"Ya":"Tidak",
                            ChkIntraSpinal.isSelected()?"Ya":"Tidak", ChkIntraPengeluaran.isSelected()?"Ya":"Tidak", KdPetugas.getText()
                        })) { tampilIntra(); emptTeks(); }
                    } else if(tabPane.getSelectedIndex()==4) {
                        String kulitPostop = (ChkPostDingin.isSelected()?"Dingin ":"")+(ChkPostHangat.isSelected()?"Hangat ":"")+(ChkPostKering.isSelected()?"kering ":"")+(ChkPostUtuh.isSelected()?"Utuh ":"")+(ChkPostLembab.isSelected()?"Lembab ":"")+(ChkPostPink.isSelected()?"Pink ":"");
                        String napasPostop = (ChkPostIntubasi.isSelected()?"Intubasi ":"")+(ChkPostEkstubasi.isSelected()?"Ekstubasi ":"")+(ChkPostSpontan.isSelected()?"Spontan ":"")+(ChkPostSpasme.isSelected()?"Spasme ":"")+(ChkPostSekret.isSelected()?"Penumpukan sekret ":"");
                        String sadarPostop = (ChkPostBangun.isSelected()?"bangun ":"")+(ChkPostSadar.isSelected()?"Sadar ":"");
                        String posisiPostop = (ChkPostTerlentang.isSelected()?"Terlentang ":"")+(ChkPostLateralKiriKanan.isSelected()?"lateral kiri/kanan ":"")+(ChkPostEkstremitasBawah.isSelected()?"Ektremitas bawah addukdi/Abduksi/elevasi ":"");
                        String jenisAnestesiPostop = (ChkPostUmum.isSelected()?"Umum ":"")+(ChkPostSpinalRegional.isSelected()?"Spinal/Regional ":"")+(ChkPostBlokPerifer.isSelected()?"Blok Perifer ":"");
                        if (Sequel.menyimpantf("askep_kb_postop", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 19, new String[]{
                            TNoRw.getText(), tglSimpan,
                            TPostMasukRR.getText(), TPostTD.getText(), TPostHR.getText(), TPostSpO2.getText(), TPostRR.getText(),
                            jenisAnestesiPostop, kulitPostop, napasPostop, sadarPostop, posisiPostop,
                            TAnestesiLama.getText(), TPembedahanLama.getText(),
                            TPostDiagnosa.getText(), TPostDrain.getText(), TPostTampon.getText(), TPostPA.getText(), KdPetugas.getText()
                        })) { tampilPost(); emptTeks(); }
                    } else if(tabPane.getSelectedIndex()==5) {
                        String efektifitasNafas = (ChkDiagNafas.isSelected()?"Ya":"Tidak")+"|"+(ChkNafasSekret.isSelected()?"penumpukan sekret ":"")+(ChkNafasAnastesi.isSelected()?"efek anastesi ":"")+"|"+(ChkSuctionYa.isSelected()?"Suction Ya ":"")+(ChkSuctionTidak.isSelected()?"Suction Tidak ":"")+"|Spontan:"+(ChkNapasSpontanO2Nasal.isSelected()?"O2 nasal "+TSpontanO2Nasal.getText()+"l/mnt ":"")+(ChkNapasSpontanO2Oral.isSelected()?"O2 oral "+TSpontanO2Oral.getText()+"l/mnt ":"")+"|Dibantu:"+(ChkNapasDibantuBagging.isSelected()?"Bagging ":"")+(ChkNapasDibantuTriple.isSelected()?"Triple manuver ":"")+(ChkNapasDibantuO2Mask.isSelected()?"O2 face mask ":"")+"|Kolaborasi:"+TKolabPemberianObat.getText();
                        String hipotermiPostop = (ChkDiagHipotermi.isSelected()?"Ya":"Tidak")+"|"+(ChkHipoLingkungan.isSelected()?"Lingkungan OK ":"")+(ChkHipoAnastesi.isSelected()?"Efek Anastesi ":"")+"|"+(ChkHipoSelimut.isSelected()?"Beri selimut hangat,":"")+(ChkHipoElevasi.isSelected()?"Elevasi tungkai,":"")+(ChkHipoKolab.isSelected()?"Kolaborasi pemberian obat "+THipoKolab.getText()+",":"")+(ChkHipoLain.isSelected()?"Lain-lain "+THipoLain.getText():"");
                        if (Sequel.menyimpantf("askep_kb_diagnosa", "?,?,?,?,?,?,?", "Data", 7, new String[]{
                            TNoRw.getText(), tglSimpan, efektifitasNafas, hipotermiPostop,
                            KdPerawatRuangPulih.getText(), KdPerawatRuangan.getText(), KdPetugas.getText()
                        })) { tampilDiagnosa(); emptTeks(); }
                    }
                }
            }
        });

        BtnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabPane.getSelectedIndex()==0) {
                    if (tbDataAwal.getSelectedRow() != -1) {
                        if(Sequel.queryu2tf("delete from asuhan_keperawatan_kamar_bedah_data_awal where no_rawat=? and tanggal=?", 
                            2, new String[]{tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),0).toString(), tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),3).toString()})==true){
                            tampilDataAwal();
                            emptTeksDataAwal();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data yang mau dihapus...!!!!");
                    }
                } else {
                    String[] tblNames = {"","askep_kb_preop","askep_kb_masalah","askep_kb_intraop","askep_kb_postop","askep_kb_diagnosa"};
                    widget.Table tb = (tabPane.getSelectedIndex()==1?tbPre:(tabPane.getSelectedIndex()==2?tbMasalah:(tabPane.getSelectedIndex()==3?tbIntra:(tabPane.getSelectedIndex()==4?tbPost:tbDiagnosa))));
                    if (tb.getSelectedRow() != -1) {
                        if(Sequel.queryu2tf("delete from "+tblNames[tabPane.getSelectedIndex()]+" where no_rawat=? and tanggal=?",
                            2, new String[]{tb.getValueAt(tb.getSelectedRow(),0).toString(), tb.getValueAt(tb.getSelectedRow(),3).toString()})==true){
                            if(tabPane.getSelectedIndex()==1) tampilPre();
                            else if(tabPane.getSelectedIndex()==2) tampilMasalah();
                            else if(tabPane.getSelectedIndex()==3) tampilIntra();
                            else if(tabPane.getSelectedIndex()==4) tampilPost();
                            else if(tabPane.getSelectedIndex()==5) tampilDiagnosa();
                            emptTeks();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,"Maaf, silahkan pilik data yang mau dihapus...!!!!");
                    }
                }
            }
        });

        BtnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TNoRw.getText().trim().equals("") || TNoRM.getText().trim().equals("")) {
                    Valid.textKosong(TNoRw, "Pasien");
                } else if (KdPetugas.getText().trim().equals("")) {
                    Valid.textKosong(KdPetugas, "Petugas");
                } else {
                    widget.Table tb = (tabPane.getSelectedIndex()==0?tbDataAwal:(tabPane.getSelectedIndex()==1?tbPre:(tabPane.getSelectedIndex()==2?tbMasalah:(tabPane.getSelectedIndex()==3?tbIntra:(tabPane.getSelectedIndex()==4?tbPost:tbDiagnosa)))));
                    if (tb.getSelectedRow() == -1) {
                        JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data yang mau diganti...!!!!");
                    } else {
                        String noRwEdit = tb.getValueAt(tb.getSelectedRow(),0).toString();
                        String tglEdit = tb.getValueAt(tb.getSelectedRow(),3).toString();
                        if(tabPane.getSelectedIndex()==0) {
                            if (Sequel.mengedittf("asuhan_keperawatan_kamar_bedah_data_awal","no_rawat=? and tanggal=?",
                                "tindakan=?,jenis_anestesi=?,catatan=?,nip=?",6,new String[]{
                                TTindakan.getText(), TJenisAnestesi.getText(), TCatatan.getText(), KdPetugas.getText(), noRwEdit, tglEdit
                            })) { tampilDataAwal(); emptTeksDataAwal(); }
                        } else if(tabPane.getSelectedIndex()==1) {
                            String kondisiKulit = (ChkDingin.isSelected()?"Dingin ":"")+(ChkHangat.isSelected()?"Hangat ":"")+(ChkKering.isSelected()?"Kering ":"")+(ChkLembab.isSelected()?"Lembab ":"")+(ChkUtuh.isSelected()?"Utuh ":"");
                            String kesadaran = (ChkCompos.isSelected()?"Compos mentis ":"")+(ChkApatis.isSelected()?"Apatis ":"")+(ChkSomnolen.isSelected()?"Somnolen ":"")+(ChkSoporcoma.isSelected()?"Soporcoma ":"")+(ChkComa.isSelected()?"Coma ":"");
                            String sensorik = (ChkSensorikTidak.isSelected()?"Tidak ada ":"")+(ChkSensorikDengar.isSelected()?"Pendengaran ":"")+(ChkSensorikBahasa.isSelected()?"Bahasa ":"")+(ChkSensorikBicara.isSelected()?"Bicara ":"");
                            String muskulo = (ChkMuskuloTidak.isSelected()?"Tidak ada ":"")+(ChkMuskuloLumpuh.isSelected()?"Lumpuh ":"")+(ChkMuskuloTraksi.isSelected()?"Traksi ":"")+(TKekuatanOtot.getText().isEmpty()?"":":"+TKekuatanOtot.getText());
                            String psikologi = (ChkRilek.isSelected()?"Rilek ":"")+(ChkCemas.isSelected()?"Cemas ":"")+(ChkBanyakBicara.isSelected()?"Banyak Bicara ":"")+(ChkMenangis.isSelected()?"Menangis ":"");
                            if (Sequel.mengedittf("askep_kb_preop","no_rawat=? and tanggal=?",
                                "puasa=?,informed_consent=?,identitas_pasien=?,lavamen=?,isolasi=?,marking=?,alergi=?,waktu_pengukuran=?,verifikasi_tindakan=?,kondisi_kulit=?,"+
                                "preop_kesadaran=?,gcs=?,gangguan_sensorik=?,muskuloskeletal=?,protesa=?,edema=?,riwayat_asma=?,respirasi=?,ekg=?,foto_thorax=?,"+
                                "riwayat_hepatitis=?,penyakit_lain=?,kesimpulan=?,kondisi_psikologis=?,data_penunjang=?,laboratorium=?,nip=?",29,new String[]{
                                TPuasaJam.getSelectedItem().toString().substring(11, 19), ChkInformend.isSelected()?"Ya":"Tidak", ChkIdentitas.isSelected()?"Ya":"Tidak",
                                ChkLevamen.isSelected()?"Ya":"Tidak", ChkVerifikasiIsolasi.isSelected()?"Ya":"Tidak", ChkMarkes.isSelected()?"Ya":"Tidak",
                                ChkVerifikasiAlergi.isSelected()?"Ya":"", TPengukuran.getSelectedItem().toString().substring(11, 19), ChkVerifikasiTindakan.isSelected()?"Ya":"Tidak",
                                kondisiKulit, kesadaran, TGCSM.getText()+","+TGCSV.getText()+","+TGCSE.getText(),
                                sensorik, muskulo, TProtese.getText(), TEdema.getText(), ChkAstma.isSelected()?"Ya":"Tidak",
                                TPernapasan.getText(), ChkEKG.isSelected()?"Ya":"Tidak", ChkThorax.isSelected()?"Ya":"Tidak",
                                ChkHepatitis.isSelected()?"Ya":"Tidak", TLainPenyakit.getText(), TKesimpulan.getText(), psikologi,
                                TUSG.getText()+"|"+TCTScan.getText()+"|"+TMRI.getText()+"|"+TLainPendukung.getText(), TLab.getText(), KdPetugas.getText(), noRwEdit, tglEdit
                            })) { tampilPre(); emptTeks(); }
                        } else if(tabPane.getSelectedIndex()==2) {
                            String cemasTingkat = (ChkCemasRingan.isSelected()?"Ringan ":"")+(ChkCemasSedang.isSelected()?"Sedang ":"")+(ChkCemasBerat.isSelected()?"Berat ":"");
                            String cemasIntervensi = (ChkKajiKecemasan.isSelected()?"Kaji tingkat kecemasan,":"")+(ChkJalinTrust.isSelected()?"Jalin trust dengan pasien,":"")+(ChkObservasiTTV.isSelected()?"Observasi tanda-tanda vital,":"")+(ChkDampingi.isSelected()?"Dampingi pasien,":"")+(ChkAjakUngkap.isSelected()?"Ajak pasien mengungkapkan,":"")+(ChkCiptaLingkungan.isSelected()?"Ciptakan lingkungan nyaman,":"")+(ChkDiskusikan.isSelected()?"Diskusikan situasi,":"")+(ChkKolabObatPenenang.isSelected()?"Kolaborasi obat penenang,":"")+(ChkEvaluasiRespon.isSelected()?"Evaluasi respon,":"");
                            String nyeriIntervensi = (ChkKajiTingkatNyeri.isSelected()?"Kaji tingkat nyeri pada lokasi,":"")+(ChkAjarkanTeknikRelaksasi.isSelected()?"Ajarkan teknik relaksasi,":"")+(ChkKolabTerapi.isSelected()?"Kolaborasi terapi "+TKolabTerapi.getText()+",":"")+(ChkKajiTTV.isSelected()?"Kaji TTV tiap "+TKajiTTVMenit.getText()+" menit,":"");
                            if (Sequel.mengedittf("askep_kb_masalah","no_rawat=? and tanggal=?",
                                "masalah_preop_kecemasan=?,cemas_tingkat=?,intervensi_cemas=?,masalah_preop_nyeri=?,intervensi_nyeri=?,kaji_ttv_menit=?,nip=?",9,new String[]{
                                TKolabTerapi.getText(), cemasTingkat, cemasIntervensi, TNyeriBD.getText(), nyeriIntervensi, TKajiTTVMenit.getText(), KdPetugas.getText(), noRwEdit, tglEdit
                            })) { tampilMasalah(); emptTeks(); }
                        } else if(tabPane.getSelectedIndex()==3) {
                            // Sama dengan save intra - terlalu panjang, pakai hapus+simpan ulang
                            if(Sequel.queryu2tf("delete from askep_kb_intraop where no_rawat=? and tanggal=?",2,new String[]{noRwEdit,tglEdit})){
                                BtnSimpan.doClick(); // re-save
                            }
                        } else if(tabPane.getSelectedIndex()==4) {
                            String kulitPostop = (ChkPostDingin.isSelected()?"Dingin ":"")+(ChkPostHangat.isSelected()?"Hangat ":"")+(ChkPostKering.isSelected()?"kering ":"")+(ChkPostUtuh.isSelected()?"Utuh ":"")+(ChkPostLembab.isSelected()?"Lembab ":"")+(ChkPostPink.isSelected()?"Pink ":"");
                            String napasPostop = (ChkPostIntubasi.isSelected()?"Intubasi ":"")+(ChkPostEkstubasi.isSelected()?"Ekstubasi ":"")+(ChkPostSpontan.isSelected()?"Spontan ":"")+(ChkPostSpasme.isSelected()?"Spasme ":"")+(ChkPostSekret.isSelected()?"Penumpukan sekret ":"");
                            String sadarPostop = (ChkPostBangun.isSelected()?"bangun ":"")+(ChkPostSadar.isSelected()?"Sadar ":"");
                            String posisiPostop = (ChkPostTerlentang.isSelected()?"Terlentang ":"")+(ChkPostLateralKiriKanan.isSelected()?"lateral kiri/kanan ":"")+(ChkPostEkstremitasBawah.isSelected()?"Ektremitas bawah addukdi/Abduksi/elevasi ":"");
                            String jenisAnestesiPostop = (ChkPostUmum.isSelected()?"Umum ":"")+(ChkPostSpinalRegional.isSelected()?"Spinal/Regional ":"")+(ChkPostBlokPerifer.isSelected()?"Blok Perifer ":"");
                            if (Sequel.mengedittf("askep_kb_postop","no_rawat=? and tanggal=?",
                                "jam_masuk_rr=?,td=?,hr=?,spo2=?,rr=?,jenis_anestesi_postop=?,kulit_postop=?,respirasi_postop=?,kesadaran=?,posisi_pasien=?,"+
                                "lama_anestesi=?,lama_operasi=?,diagnosa_postop=?,drain=?,tampon=?,pa=?,nip=?",19,new String[]{
                                TPostMasukRR.getText(), TPostTD.getText(), TPostHR.getText(), TPostSpO2.getText(), TPostRR.getText(),
                                jenisAnestesiPostop, kulitPostop, napasPostop, sadarPostop, posisiPostop,
                                TAnestesiLama.getText(), TPembedahanLama.getText(), TPostDiagnosa.getText(), TPostDrain.getText(), TPostTampon.getText(), TPostPA.getText(), KdPetugas.getText(),
                                noRwEdit, tglEdit
                            })) { tampilPost(); emptTeks(); }
                        } else if(tabPane.getSelectedIndex()==5) {
                            String efektifitasNafas = (ChkDiagNafas.isSelected()?"Ya":"Tidak")+"|"+(ChkNafasSekret.isSelected()?"penumpukan sekret ":"")+(ChkNafasAnastesi.isSelected()?"efek anastesi ":"")+"|"+(ChkSuctionYa.isSelected()?"Suction Ya ":"")+(ChkSuctionTidak.isSelected()?"Suction Tidak ":"")+"|Spontan:"+(ChkNapasSpontanO2Nasal.isSelected()?"O2 nasal "+TSpontanO2Nasal.getText()+"l/mnt ":"")+(ChkNapasSpontanO2Oral.isSelected()?"O2 oral "+TSpontanO2Oral.getText()+"l/mnt ":"")+"|Dibantu:"+(ChkNapasDibantuBagging.isSelected()?"Bagging ":"")+(ChkNapasDibantuTriple.isSelected()?"Triple manuver ":"")+(ChkNapasDibantuO2Mask.isSelected()?"O2 face mask ":"")+"|Kolaborasi:"+TKolabPemberianObat.getText();
                            String hipotermiPostop = (ChkDiagHipotermi.isSelected()?"Ya":"Tidak")+"|"+(ChkHipoLingkungan.isSelected()?"Lingkungan OK ":"")+(ChkHipoAnastesi.isSelected()?"Efek Anastesi ":"")+"|"+(ChkHipoSelimut.isSelected()?"Beri selimut hangat,":"")+(ChkHipoElevasi.isSelected()?"Elevasi tungkai,":"")+(ChkHipoKolab.isSelected()?"Kolaborasi pemberian obat "+THipoKolab.getText()+",":"")+(ChkHipoLain.isSelected()?"Lain-lain "+THipoLain.getText():"");
                            if (Sequel.mengedittf("askep_kb_diagnosa","no_rawat=? and tanggal=?",
                                "efektifitas_jalan_nafas=?,hipotermi_postop=?,nip_perawat_rr=?,nip_perawat_ruangan=?,nip=?",7,new String[]{
                                efektifitasNafas, hipotermiPostop, KdPerawatRuangPulih.getText(), KdPerawatRuangan.getText(), KdPetugas.getText(), noRwEdit, tglEdit
                            })) { tampilDiagnosa(); emptTeks(); }
                        }
                    }
                }
            }
        });

        // ADD TO INTERNAL FRAME
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(tabPane, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    public void tampilPre() {
        Valid.tabelKosong(tabModePre);
        try {
            ps = koneksi.prepareStatement("select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, pt.nama, a.kesimpulan, a.gcs " +
                    "from askep_kb_preop a " +
                    "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join petugas pt on a.nip=pt.nip " +
                    "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ? or pt.nama like ?) " +
                    "order by a.tanggal");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%" );
                ps.setString(4, "%" + TCari.getText().trim() + "%" );
                ps.setString(5, "%" + TCari.getText().trim() + "%" );
                ps.setString(6, "%" + TCari.getText().trim() + "%" );
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModePre.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("nama"), rs.getString("kesimpulan"), rs.getString("gcs")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Pre Op : " + e);
            } finally {
                if (rs != null) { rs.close(); }
                if (ps != null) { ps.close(); }
            }
        } catch (Exception e) {
            System.out.println("Notif Pre Op : " + e);
        }
    }

    public void tampilMasalah() {
        Valid.tabelKosong(tabModeMasalah);
        try {
            ps = koneksi.prepareStatement("select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.masalah_preop_kecemasan, a.masalah_preop_nyeri " +
                    "from askep_kb_masalah a " +
                    "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                    "order by a.tanggal");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%" );
                ps.setString(4, "%" + TCari.getText().trim() + "%" );
                ps.setString(5, "%" + TCari.getText().trim() + "%" );
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeMasalah.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("masalah_preop_kecemasan"), rs.getString("masalah_preop_nyeri")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Masalah : " + e);
            } finally {
                if (rs != null) { rs.close(); }
                if (ps != null) { ps.close(); }
            }
        } catch (Exception e) {
            System.out.println("Notif Masalah : " + e);
        }
    }

    public void tampilIntra() {
        Valid.tabelKosong(tabModeIntra);
        try {
            ps = koneksi.prepareStatement("select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.klasifikasi_luka, a.posisi_operasi, a.kateter " +
                    "from askep_kb_intraop a " +
                    "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                    "order by a.tanggal");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%" );
                ps.setString(4, "%" + TCari.getText().trim() + "%" );
                ps.setString(5, "%" + TCari.getText().trim() + "%" );
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeIntra.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("klasifikasi_luka"), rs.getString("posisi_operasi"), rs.getString("kateter")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Intra : " + e);
            } finally {
                if (rs != null) { rs.close(); }
                if (ps != null) { ps.close(); }
            }
        } catch (Exception e) {
            System.out.println("Notif Intra : " + e);
        }
    }

    public void tampilPost() {
        Valid.tabelKosong(tabModePost);
        try {
            ps = koneksi.prepareStatement("select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.jam_masuk_rr, a.kulit_postop, a.kesadaran " +
                    "from askep_kb_postop a " +
                    "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                    "order by a.tanggal");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%" );
                ps.setString(4, "%" + TCari.getText().trim() + "%" );
                ps.setString(5, "%" + TCari.getText().trim() + "%" );
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModePost.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("jam_masuk_rr"), rs.getString("kulit_postop"), rs.getString("kesadaran")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Post : " + e);
            } finally {
                if (rs != null) { rs.close(); }
                if (ps != null) { ps.close(); }
            }
        } catch (Exception e) {
            System.out.println("Notif Post : " + e);
        }
    }

    public void tampilDiagnosa() {
        Valid.tabelKosong(tabModeDiagnosa);
        try {
            ps = koneksi.prepareStatement("select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.efektifitas_jalan_nafas, a.hipotermi_postop " +
                    "from askep_kb_diagnosa a " +
                    "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                    "order by a.tanggal");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%" );
                ps.setString(4, "%" + TCari.getText().trim() + "%" );
                ps.setString(5, "%" + TCari.getText().trim() + "%" );
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeDiagnosa.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("efektifitas_jalan_nafas"), rs.getString("hipotermi_postop")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Diagnosa : " + e);
            } finally {
                if (rs != null) { rs.close(); }
                if (ps != null) { ps.close(); }
            }
        } catch (Exception e) {
            System.out.println("Notif Diagnosa : " + e);
        }
    }

    public void emptTeks() {
        emptTeksDataAwal();
        TglAsuhan.setDate(new Date());
        KdPetugas.setText("");
        NmPetugas.setText("");
        
        // Pre Op
        ChkIdentitas.setSelected(false);
        ChkInformend.setSelected(false);
        ChkVerifikasiIsolasi.setSelected(false);
        ChkVerifikasiAlergi.setSelected(false);
        ChkVerifikasiTindakan.setSelected(false);
        TPuasaJam.setDate(new Date());
        ChkLevamen.setSelected(false);
        ChkMarkes.setSelected(false);
        TPengukuran.setDate(new Date());
        ChkDingin.setSelected(false);
        ChkHangat.setSelected(false);
        ChkKering.setSelected(false);
        ChkLembab.setSelected(false);
        ChkUtuh.setSelected(false);
        ChkCompos.setSelected(false);
        ChkApatis.setSelected(false);
        ChkSomnolen.setSelected(false);
        ChkSoporcoma.setSelected(false);
        ChkComa.setSelected(false);
        TGCSM.setText("");
        TGCSV.setText("");
        TGCSE.setText("");
        TKesimpulan.setText("");
        ChkSensorikTidak.setSelected(false);
        ChkSensorikDengar.setSelected(false);
        ChkSensorikBahasa.setSelected(false);
        ChkSensorikBicara.setSelected(false);
        ChkMuskuloTidak.setSelected(false);
        ChkMuskuloLumpuh.setSelected(false);
        ChkMuskuloTraksi.setSelected(false);
        TKekuatanOtot.setText("");
        ChkHearing.setSelected(false);
        ChkKacamata.setSelected(false);
        TProtese.setText("");
        TEdema.setText("");
        ChkAstma.setSelected(false);
        TPernapasan.setText("");
        ChkEKG.setSelected(false);
        ChkThorax.setSelected(false);
        ChkHepatitis.setSelected(false);
        TLainPenyakit.setText("");
        ChkRilek.setSelected(false);
        ChkCemas.setSelected(false);
        ChkBanyakBicara.setSelected(false);
        ChkMenangis.setSelected(false);
        TUSG.setText("");
        TCTScan.setText("");
        TMRI.setText("");
        TLainPendukung.setText("");
        TLab.setText("");

        // Masalah
        ChkKajiKecemasan.setSelected(false);
        ChkJalinTrust.setSelected(false);
        ChkObservasiTTV.setSelected(false);
        ChkDampingi.setSelected(false);
        ChkAjakUngkap.setSelected(false);
        ChkCiptaLingkungan.setSelected(false);
        ChkDiskusikan.setSelected(false);
        ChkKolabObatPenenang.setSelected(false);
        ChkEvaluasiRespon.setSelected(false);
        TNyeriBD.setText("");
        ChkKajiTingkatNyeri.setSelected(false);
        ChkAjarkanTeknikRelaksasi.setSelected(false);
        ChkKajiTTV.setSelected(false);
        TKajiTTVMenit.setText("");
        ChkKolabTerapi.setSelected(false);
        TKolabTerapi.setText("");

        // Intra Op
        TIntraSuhu.setText("");
        ChkIntraHipo1.setSelected(false);
        ChkIntraHipo2.setSelected(false);
        ChkIntraHipo3.setSelected(false);
        ChkIntraHipo4.setSelected(false);
        TIntraOlehHipo.setText("");
        ChkIntraKulitPosisiSupine.setSelected(false);
        ChkIntraKulitPosisiProne.setSelected(false);
        ChkIntraKulitPosisiLithotomy.setSelected(false);
        ChkIntraKulitPosisiLateral.setSelected(false);
        ChkIntraKulitPosisiLateralKiri.setSelected(false);
        ChkIntraKulitPosisiLateralKanan.setSelected(false);
        TIntraPosisiLain.setText("");
        ChkIntraBantal.setSelected(false);
        ChkIntraPasir.setSelected(false);
        ChkIntraPenyangga.setSelected(false);
        ChkIntraPaddingSiku.setSelected(false);
        ChkIntraPaddingPoplitea.setSelected(false);
        TIntraBantuLain.setText("");
        ChkIntraSabuk.setSelected(false);
        ChkIntraPlate.setSelected(false);
        TIntraPlateOleh.setText("");
        ChkIntraKulitUtuh.setSelected(false);
        ChkIntraKulitMerah.setSelected(false);
        ChkIntraKulitLecet.setSelected(false);
        ChkIntraKulitLuka.setSelected(false);
        TIntraCouterSetting.setText("");
        TIntraCuttingSetting.setText("");
        ChkIntraTorniquet.setSelected(false);
        TIntraTorniquetOleh.setText("");
        TIntraInflate.setText("");
        TIntraDeflate.setText("");
        TIntraTekanan.setText("");
        ChkIntraKassa1.setSelected(false);
        ChkIntraKassa2.setSelected(false);
        ChkIntraKassa3.setSelected(false);
        ChkIntraJarum1.setSelected(false);
        ChkIntraJarum2.setSelected(false);
        ChkIntraJarum3.setSelected(false);
        ChkIntraInstrumen1.setSelected(false);
        ChkIntraInstrumen2.setSelected(false);
        ChkIntraInstrumen3.setSelected(false);
        TIntraInfus.setText("");
        TIntraUrine.setText("");
        TIntraNGT.setText("");
        TIntraInfusJenis.setText("");
        TIntraCairanLain.setText("");
        TIntraSabukPada.setText("");
        TIntraPlatePada.setText("");
        TIntraTorniquetPada.setText("");
        ChkIntraSpinal.setSelected(false);
        ChkIntraPengeluaran.setSelected(false);
        ChkIntraHitungXRayYa.setSelected(false);
        ChkIntraHitungXRayTidak.setSelected(false);
        TIntraHitungXRayKet.setText("");
        ChkIntraPemberianDarahYa.setSelected(false);
        ChkIntraPemberianDarahTidak.setSelected(false);

        // Intra - Resti Infeksi
        ChkKulitUtuhPreOp.setSelected(false);
        ChkLainRestiInfeksi.setSelected(false);
        TLainRestiInfeksiOleh.setText("");
        ChkAseptik.setSelected(false);
        TAseptikDg.setText("");
        TAseptikOleh.setText("");
        ChkLukaBersih.setSelected(false);
        ChkLukaBersihTercemar.setSelected(false);
        ChkLukaTercemar.setSelected(false);
        ChkLukaKotor.setSelected(false);
        ChkPasangKateterUrine.setSelected(false);
        TKateterNo.setText("");
        TKateterUrineCc.setText("");
        TKateterOleh.setText("");
        ChkDrain.setSelected(false);
        TDrainJenis.setText("");
        TDrainCairanCc.setText("");
        ChkTampon.setSelected(false);
        TTamponJenis.setText("");
        TTamponLokasi.setText("");
        ChKGips.setSelected(false);
        TGipsJenis.setText("");
        TGipsLokasi.setText("");

        // Post Op
        TPostMasukRR.setText("");
        ChkPostUmum.setSelected(false);
        ChkPostSpinalRegional.setSelected(false);
        ChkPostBlokPerifer.setSelected(false);
        ChkPostDingin.setSelected(false);
        ChkPostHangat.setSelected(false);
        ChkPostKering.setSelected(false);
        ChkPostUtuh.setSelected(false);
        ChkPostLembab.setSelected(false);
        ChkPostPink.setSelected(false);
        ChkPostIntubasi.setSelected(false);
        ChkPostEkstubasi.setSelected(false);
        ChkPostSpontan.setSelected(false);
        ChkPostSpasme.setSelected(false);
        ChkPostSekret.setSelected(false);
        ChkPostBangun.setSelected(false);
        ChkPostSadar.setSelected(false);
        ChkPostTerlentang.setSelected(false);
        ChkPostLateralKiriKanan.setSelected(false);
        ChkPostEkstremitasBawah.setSelected(false);
        TPostTD.setText("");
        TPostHR.setText("");
        TPostRR.setText("");
        TPostSpO2.setText("");
        TAnestesiLama.setText("");
        TPembedahanLama.setText("");
        TPostDiagnosa.setText("");
        TPostDrain.setText("");
        TPostTampon.setText("");
        TPostPA.setText("");

        // Diagnosa Post
        ChkDiagNafas.setSelected(false);
        ChkNafasSekret.setSelected(false);
        ChkNafasAnastesi.setSelected(false);
        ChkSuctionYa.setSelected(false);
        ChkSuctionTidak.setSelected(false);
        ChkNapasSpontanO2Nasal.setSelected(false);
        TSpontanO2Nasal.setText("");
        ChkNapasSpontanO2Oral.setSelected(false);
        TSpontanO2Oral.setText("");
        ChkNapasDibantuBagging.setSelected(false);
        ChkNapasDibantuTriple.setSelected(false);
        ChkNapasDibantuO2Mask.setSelected(false);
        TKolabPemberianObat.setText("");
        ChkDiagHipotermi.setSelected(false);
        ChkHipoLingkungan.setSelected(false);
        ChkHipoAnastesi.setSelected(false);
        ChkHipoSelimut.setSelected(false);
        ChkHipoElevasi.setSelected(false);
        ChkHipoKolab.setSelected(false);
        THipoKolab.setText("");
        ChkHipoLain.setSelected(false);
        THipoLain.setText("");
        
        KdPerawatRuangPulih.setText(""); NmPerawatRuangPulih.setText("");
        KdPerawatRuangan.setText(""); NmPerawatRuangan.setText("");
    }

    public void getDataPre() {
        if (tbPre.getSelectedRow() != -1) {
            TNoRw.setText(tbPre.getValueAt(tbPre.getSelectedRow(), 0).toString());
            TNoRM.setText(tbPre.getValueAt(tbPre.getSelectedRow(), 1).toString());
            TPasien.setText(tbPre.getValueAt(tbPre.getSelectedRow(), 2).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbPre.getValueAt(tbPre.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            // Fetch the rest from DB
            try {
                ps = koneksi.prepareStatement("select * from askep_kb_preop where no_rawat=? and tanggal=?");
                ps.setString(1, TNoRw.getText());
                ps.setString(2, tbPre.getValueAt(tbPre.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    // Populate everything
                    fillFields(rs);
                }
            } catch (Exception e) {
                System.out.println("Notif Get Data : " + e);
            } finally {
                if (rs != null) { try { rs.close(); } catch (Exception e) {} }
                if (ps != null) { try { ps.close(); } catch (Exception e) {} }
            }
        }
    }

    public void getDataMasalah() {
        if (tbMasalah.getSelectedRow() != -1) {
            TNoRw.setText(tbMasalah.getValueAt(tbMasalah.getSelectedRow(), 0).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbMasalah.getValueAt(tbMasalah.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from askep_kb_masalah where no_rawat=? and tanggal=?");
                ps.setString(1, TNoRw.getText());
                ps.setString(2, tbMasalah.getValueAt(tbMasalah.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    fillFieldsMasalah(rs);
                }
            } catch (Exception e) {
                System.out.println("Notif Get Data : " + e);
            } finally {
                if (rs != null) { try { rs.close(); } catch (Exception e) {} }
                if (ps != null) { try { ps.close(); } catch (Exception e) {} }
            }
        }
    }

    public void getDataIntra() {
        if (tbIntra.getSelectedRow() != -1) {
            TNoRw.setText(tbIntra.getValueAt(tbIntra.getSelectedRow(), 0).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbIntra.getValueAt(tbIntra.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from askep_kb_intraop where no_rawat=? and tanggal=?");
                ps.setString(1, TNoRw.getText());
                ps.setString(2, tbIntra.getValueAt(tbIntra.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    fillFieldsIntra(rs);
                }
            } catch (Exception e) {
                System.out.println("Notif Get Data : " + e);
            } finally {
                if (rs != null) { try { rs.close(); } catch (Exception e) {} }
                if (ps != null) { try { ps.close(); } catch (Exception e) {} }
            }
        }
    }

    public void getDataPost() {
        if (tbPost.getSelectedRow() != -1) {
            TNoRw.setText(tbPost.getValueAt(tbPost.getSelectedRow(), 0).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbPost.getValueAt(tbPost.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from askep_kb_postop where no_rawat=? and tanggal=?");
                ps.setString(1, TNoRw.getText());
                ps.setString(2, tbPost.getValueAt(tbPost.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    fillFieldsPost(rs);
                }
            } catch (Exception e) {
                System.out.println("Notif Get Data : " + e);
            } finally {
                if (rs != null) { try { rs.close(); } catch (Exception e) {} }
                if (ps != null) { try { ps.close(); } catch (Exception e) {} }
            }
        }
    }

    public void getDataDiagnosa() {
        if (tbDiagnosa.getSelectedRow() != -1) {
            TNoRw.setText(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(), 0).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from askep_kb_diagnosa where no_rawat=? and tanggal=?");
                ps.setString(1, TNoRw.getText());
                ps.setString(2, tbDiagnosa.getValueAt(tbDiagnosa.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    fillFieldsDiagnosa(rs);
                }
            } catch (Exception e) {
                System.out.println("Notif Get Data : " + e);
            } finally {
                if (rs != null) { try { rs.close(); } catch (Exception e) {} }
                if (ps != null) { try { ps.close(); } catch (Exception e) {} }
            }
        }
    }

    private void fillFieldsMasalah(java.sql.ResultSet rs) throws java.sql.SQLException {
        // askep_kb_masalah: masalah_preop_kecemasan, cemas_tingkat, intervensi_cemas,
        //                   masalah_preop_nyeri, intervensi_nyeri, kaji_ttv_menit, nip
        TKolabTerapi.setText(rs.getString("masalah_preop_kecemasan"));
        TNyeriBD.setText(rs.getString("masalah_preop_nyeri"));
        TKajiTTVMenit.setText(rs.getString("kaji_ttv_menit"));

        String cemasTingkat = rs.getString("cemas_tingkat");
        if (cemasTingkat != null) {
            ChkCemasRingan.setSelected(cemasTingkat.contains("Ringan"));
            ChkCemasSedang.setSelected(cemasTingkat.contains("Sedang"));
            ChkCemasBerat.setSelected(cemasTingkat.contains("Berat"));
        }

        String cemasIntervensi = rs.getString("intervensi_cemas");
        if (cemasIntervensi != null) {
            ChkKajiKecemasan.setSelected(cemasIntervensi.contains("Kaji tingkat kecemasan"));
            ChkJalinTrust.setSelected(cemasIntervensi.contains("Jalin trust"));
            ChkObservasiTTV.setSelected(cemasIntervensi.contains("Observasi tanda-tanda vital"));
            ChkDampingi.setSelected(cemasIntervensi.contains("Dampingi pasien"));
            ChkAjakUngkap.setSelected(cemasIntervensi.contains("Ajak pasien mengungkapkan"));
            ChkCiptaLingkungan.setSelected(cemasIntervensi.contains("Ciptakan lingkungan"));
            ChkDiskusikan.setSelected(cemasIntervensi.contains("Diskusikan situasi"));
            ChkKolabObatPenenang.setSelected(cemasIntervensi.contains("Kolaborasi obat penenang"));
            ChkEvaluasiRespon.setSelected(cemasIntervensi.contains("Evaluasi respon"));
        }

        String nyeriIntervensi = rs.getString("intervensi_nyeri");
        if (nyeriIntervensi != null) {
            ChkKajiTingkatNyeri.setSelected(nyeriIntervensi.contains("Kaji tingkat nyeri"));
            ChkAjarkanTeknikRelaksasi.setSelected(nyeriIntervensi.contains("Ajarkan teknik relaksasi"));
            ChkKolabTerapi.setSelected(nyeriIntervensi.contains("Kolaborasi terapi"));
            ChkKajiTTV.setSelected(nyeriIntervensi.contains("Kaji TTV tiap"));
        }

        KdPetugas.setText(rs.getString("nip"));
        NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
    }

    private void fillFieldsIntra(java.sql.ResultSet rs) throws java.sql.SQLException {
        // askep_kb_intraop - Resti Infeksi
        String aseptik = rs.getString("aseptik_antiseptik");
        if(aseptik!=null){ TAseptikDg.setText(aseptik); }
        String aseptikOleh = rs.getString("aseptik_oleh");
        if(aseptikOleh!=null){ TAseptikOleh.setText(aseptikOleh); }
        ChkKulitUtuhPreOp.setSelected(rs.getString("kulit_preop_utuh").equals("Ya"));
        String klasLuka = rs.getString("klasifikasi_luka");
        if(klasLuka!=null){
            ChkLukaBersih.setSelected(klasLuka.contains("bersih") && !klasLuka.contains("tercemar"));
            ChkLukaBersihTercemar.setSelected(klasLuka.contains("Bersih tercemar"));
            ChkLukaTercemar.setSelected(klasLuka.contains("Tercemar") && !klasLuka.contains("Bersih"));
            ChkLukaKotor.setSelected(klasLuka.contains("Kotor"));
        }
        ChkPasangKateterUrine.setSelected(rs.getString("kateter").equals("Ya"));
        TKateterNo.setText(rs.getString("kateter_no"));
        TKateterUrineCc.setText(rs.getString("kateter_urine_cc"));
        TKateterOleh.setText(rs.getString("kateter_oleh"));
        ChkDrain.setSelected(rs.getString("drain_intraop").equals("Ya"));
        TDrainJenis.setText(rs.getString("drain_intraop_jenis"));
        TDrainCairanCc.setText(rs.getString("drain_intraop_cc"));
        ChkTampon.setSelected(rs.getString("tampon_intraop").equals("Ya"));
        TTamponJenis.setText(rs.getString("tampon_intraop_jenis"));
        TTamponLokasi.setText(rs.getString("tampon_intraop_lokasi"));
        ChKGips.setSelected(rs.getString("gips").equals("Ya"));
        TGipsJenis.setText(rs.getString("gips_jenis"));
        TGipsLokasi.setText(rs.getString("gips_lokasi"));

        // INTRA OP - Hipotermi
        TIntraSuhu.setText(rs.getString("matras_penghangat"));
        TIntraOlehHipo.setText(rs.getString("hipo_oleh"));
        ChkIntraHipo2.setSelected(rs.getString("cairan_iv_hangat").equals("Ya"));
        ChkIntraHipo3.setSelected(rs.getString("irigasi_hangat").equals("Ya"));

        // INTRA OP - Integritas Kulit
        String posOp = rs.getString("posisi_operasi");
        if(posOp!=null){
            ChkIntraKulitPosisiSupine.setSelected(posOp.contains("Supine"));
            ChkIntraKulitPosisiProne.setSelected(posOp.contains("Prone"));
            ChkIntraKulitPosisiLithotomy.setSelected(posOp.contains("Lithotomy"));
            ChkIntraKulitPosisiLateral.setSelected(posOp.contains("Lateral") && !posOp.contains("Lateral Kiri") && !posOp.contains("Lateral Kanan"));
            ChkIntraKulitPosisiLateralKiri.setSelected(posOp.contains("Lateral Kiri"));
            ChkIntraKulitPosisiLateralKanan.setSelected(posOp.contains("Lateral Kanan"));
        }
        TIntraPosisiLain.setText(rs.getString("posisi_lain"));
        String alatPos = rs.getString("alat_posisi");
        if(alatPos!=null){
            ChkIntraBantal.setSelected(alatPos.contains("Bantal/guling"));
            ChkIntraPasir.setSelected(alatPos.contains("Bantal/Pasir"));
            ChkIntraPenyangga.setSelected(alatPos.contains("Penyangga Kaki"));
        }
        String pad = rs.getString("bantalan");
        if(pad!=null){
            ChkIntraPaddingSiku.setSelected(pad.contains("Siku"));
            ChkIntraPaddingPoplitea.setSelected(pad.contains("Poplitea"));
        }

        // INTRA OP - Injuri
        ChkIntraSabuk.setSelected(rs.getString("sabuk_pengaman").equals("Ya"));
        TIntraSabukPada.setText(rs.getString("sabuk_pada"));
        ChkIntraPlate.setSelected(rs.getString("plat_grounding").equals("Ya"));
        TIntraPlatePada.setText(rs.getString("plate_pada"));
        TIntraPlateOleh.setText(rs.getString("plate_oleh"));
        TIntraCouterSetting.setText(rs.getString("elektrokauter_koagulasi"));
        TIntraCuttingSetting.setText(rs.getString("elektrokauter_cutting"));
        String kulitP = rs.getString("kulit_setelah_plate");
        if(kulitP!=null){
            ChkIntraKulitUtuh.setSelected(kulitP.contains("Utuh"));
            ChkIntraKulitMerah.setSelected(kulitP.contains("kemerahan"));
            ChkIntraKulitLecet.setSelected(kulitP.contains("Lecet"));
            ChkIntraKulitLuka.setSelected(kulitP.contains("Luka"));
        }
        ChkIntraTorniquet.setSelected(rs.getString("tourniquet_waktu").equals("Ya"));
        TIntraTorniquetPada.setText(rs.getString("tourniquet_pada"));
        TIntraTorniquetOleh.setText(rs.getString("tourniquet_oleh"));
        TIntraInflate.setText(rs.getString("tourniquet_inflate"));
        TIntraDeflate.setText(rs.getString("tourniquet_deflate"));
        TIntraTekanan.setText(rs.getString("tourniquet_tekanan"));
        ChkIntraHitungXRayYa.setSelected(rs.getString("hitung_xray").equals("Ya"));
        ChkIntraHitungXRayTidak.setSelected(rs.getString("hitung_xray").equals("Tidak"));
        TIntraHitungXRayKet.setText(rs.getString("hitung_xray_keterangan"));

        // INTRA OP - Keseimbangan cairan
        TIntraInfus.setText(rs.getString("cairan_infus"));
        TIntraInfusJenis.setText(rs.getString("cairan_infus_jenis"));
        TIntraUrine.setText(rs.getString("urine"));
        TIntraNGT.setText(rs.getString("ngt"));
        TIntraCairanLain.setText(rs.getString("cairan_lain_cc"));
        ChkIntraPemberianDarahYa.setSelected(rs.getString("pemberian_darah").equals("Ya"));
        ChkIntraPemberianDarahTidak.setSelected(rs.getString("pemberian_darah").equals("Tidak"));

        KdPetugas.setText(rs.getString("nip"));
        NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
    }

    private void fillFieldsPost(java.sql.ResultSet rs) throws java.sql.SQLException {
        // askep_kb_postop
        TPostMasukRR.setText(rs.getString("jam_masuk_rr"));
        TPostTD.setText(rs.getString("td"));
        TPostHR.setText(rs.getString("hr"));
        TPostRR.setText(rs.getString("rr"));
        TPostSpO2.setText(rs.getString("spo2"));
        TPostDiagnosa.setText(rs.getString("diagnosa_postop"));
        TPostDrain.setText(rs.getString("drain"));
        TPostTampon.setText(rs.getString("tampon"));
        TPostPA.setText(rs.getString("pa"));
        TPostResp.setText(rs.getString("respirasi_postop"));
        TAnestesiLama.setText(rs.getString("lama_anestesi"));
        TPembedahanLama.setText(rs.getString("lama_operasi"));
        String jenisAnesPostop = rs.getString("jenis_anestesi_postop");
        if(jenisAnesPostop!=null){
            ChkPostUmum.setSelected(jenisAnesPostop.contains("Umum"));
            ChkPostSpinalRegional.setSelected(jenisAnesPostop.contains("Spinal"));
            ChkPostBlokPerifer.setSelected(jenisAnesPostop.contains("Blok"));
        }
        String kulitPost = rs.getString("kulit_postop");
        if(kulitPost!=null){
            ChkPostDingin.setSelected(kulitPost.contains("Dingin"));
            ChkPostHangat.setSelected(kulitPost.contains("Hangat"));
            ChkPostKering.setSelected(kulitPost.contains("kering"));
            ChkPostUtuh.setSelected(kulitPost.contains("Utuh"));
            ChkPostLembab.setSelected(kulitPost.contains("Lembab"));
            ChkPostPink.setSelected(kulitPost.contains("Pink"));
        }
        String napPost = rs.getString("respirasi_postop");
        if(napPost!=null){
            ChkPostIntubasi.setSelected(napPost.contains("Intubasi"));
            ChkPostEkstubasi.setSelected(napPost.contains("Ekstubasi"));
            ChkPostSpontan.setSelected(napPost.contains("Spontan"));
            ChkPostSpasme.setSelected(napPost.contains("Spasme"));
            ChkPostSekret.setSelected(napPost.contains("sekret"));
        }
        String sadarPost = rs.getString("kesadaran");
        if(sadarPost!=null){
            ChkPostBangun.setSelected(sadarPost.contains("bangun"));
            ChkPostSadar.setSelected(sadarPost.contains("Sadar"));
        }
        String posPost = rs.getString("posisi_pasien");
        if(posPost!=null){
            ChkPostTerlentang.setSelected(posPost.contains("Terlentang"));
            ChkPostLateralKiriKanan.setSelected(posPost.contains("lateral"));
            ChkPostEkstremitasBawah.setSelected(posPost.contains("Ektremitas"));
        }

        KdPetugas.setText(rs.getString("nip"));
        NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
    }

    private void fillFieldsDiagnosa(java.sql.ResultSet rs) throws java.sql.SQLException {
        // askep_kb_diagnosa: efektifitas_jalan_nafas, hipotermi_postop,
        //                    nip_perawat_rr, nip_perawat_ruangan, nip
        String ef = rs.getString("efektifitas_jalan_nafas");
        if(ef!=null){
            ChkDiagNafas.setSelected(ef.startsWith("Ya"));
            ChkNafasSekret.setSelected(ef.contains("penumpukan sekret"));
            ChkNafasAnastesi.setSelected(ef.contains("efek anastesi"));
            ChkSuctionYa.setSelected(ef.contains("Suction Ya"));
            ChkSuctionTidak.setSelected(ef.contains("Suction Tidak"));
            ChkNapasSpontanO2Nasal.setSelected(ef.contains("O2 nasal"));
            ChkNapasSpontanO2Oral.setSelected(ef.contains("O2 oral"));
            ChkNapasDibantuBagging.setSelected(ef.contains("Bagging"));
            ChkNapasDibantuTriple.setSelected(ef.contains("Triple manuver"));
            ChkNapasDibantuO2Mask.setSelected(ef.contains("O2 face mask"));
            if(ef.contains("O2 nasal ")){
                String s = ef.substring(ef.indexOf("O2 nasal ") + "O2 nasal ".length());
                if(s.contains("l/mnt")) TSpontanO2Nasal.setText(s.substring(0, s.indexOf("l/mnt")).trim());
            }
            if(ef.contains("O2 oral ")){
                String s = ef.substring(ef.indexOf("O2 oral ") + "O2 oral ".length());
                if(s.contains("l/mnt")) TSpontanO2Oral.setText(s.substring(0, s.indexOf("l/mnt")).trim());
            }
            if(ef.contains("Kolaborasi:")){
                TKolabPemberianObat.setText(ef.substring(ef.indexOf("Kolaborasi:") + "Kolaborasi:".length()).trim());
            }
        }

        String hipo = rs.getString("hipotermi_postop");
        if(hipo!=null){
            ChkDiagHipotermi.setSelected(hipo.startsWith("Ya"));
            ChkHipoLingkungan.setSelected(hipo.contains("Lingkungan OK"));
            ChkHipoAnastesi.setSelected(hipo.contains("Efek Anastesi"));
            ChkHipoSelimut.setSelected(hipo.contains("Beri selimut hangat"));
            ChkHipoElevasi.setSelected(hipo.contains("Elevasi tungkai"));
            ChkHipoKolab.setSelected(hipo.contains("Kolaborasi pemberian obat"));
            ChkHipoLain.setSelected(hipo.contains("Lain-lain"));
            if(hipo.contains("Kolaborasi pemberian obat ")){
                String s = hipo.substring(hipo.indexOf("Kolaborasi pemberian obat ") + "Kolaborasi pemberian obat ".length());
                if(s.contains(",")) THipoKolab.setText(s.substring(0, s.indexOf(",")).trim());
            }
            if(hipo.contains("Lain-lain ")){
                THipoLain.setText(hipo.substring(hipo.indexOf("Lain-lain ") + "Lain-lain ".length()).trim());
            }
        }

        KdPerawatRuangPulih.setText(rs.getString("nip_perawat_rr"));
        NmPerawatRuangPulih.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_perawat_rr")));
        KdPerawatRuangan.setText(rs.getString("nip_perawat_ruangan"));
        NmPerawatRuangan.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_perawat_ruangan")));
        KdPetugas.setText(rs.getString("nip"));
        NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
    }

    private void fillFields(java.sql.ResultSet rs) throws java.sql.SQLException {
        // PRE OP
        ChkIdentitas.setSelected(rs.getString("identitas_pasien").equals("Ya"));
        ChkInformend.setSelected(rs.getString("informed_consent").equals("Ya"));
        ChkVerifikasiIsolasi.setSelected(rs.getString("isolasi").equals("Ya"));
        ChkVerifikasiAlergi.setSelected(!rs.getString("alergi").trim().isEmpty()); 
        ChkVerifikasiTindakan.setSelected(rs.getString("verifikasi_tindakan").equals("Ya"));
        String puasaStr = rs.getString("puasa");
        if(puasaStr!=null && !puasaStr.isEmpty()){
            try { TPuasaJam.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(puasaStr)); } catch(Exception ex){}
        }
        ChkLevamen.setSelected(rs.getString("lavamen").equals("Ya"));
        ChkMarkes.setSelected(rs.getString("marking").equals("Ya"));
        String pengukuranStr = rs.getString("waktu_pengukuran");
        if(pengukuranStr!=null && !pengukuranStr.isEmpty()){
            try { TPengukuran.setDate(new java.text.SimpleDateFormat("HH:mm:ss").parse(pengukuranStr)); } catch(Exception ex){}
        }

        String kondisiKulit = rs.getString("kondisi_kulit");
        if (kondisiKulit != null) {
            ChkDingin.setSelected(kondisiKulit.contains("Dingin"));
            ChkHangat.setSelected(kondisiKulit.contains("Hangat"));
            ChkKering.setSelected(kondisiKulit.contains("Kering"));
            ChkLembab.setSelected(kondisiKulit.contains("Lembab"));
            ChkUtuh.setSelected(kondisiKulit.contains("Utuh"));
        }

        String kesadaran = rs.getString("preop_kesadaran");
        if (kesadaran != null) {
            ChkCompos.setSelected(kesadaran.contains("Compos"));
            ChkApatis.setSelected(kesadaran.contains("Apatis"));
            ChkSomnolen.setSelected(kesadaran.contains("Somnolen"));
            ChkSoporcoma.setSelected(kesadaran.contains("Soporcoma"));
            ChkComa.setSelected(kesadaran.contains("Coma"));
        }

        String gcs = rs.getString("gcs");
        if(gcs != null && gcs.contains(",")){
            String[] gcsParts = gcs.split(",");
            if(gcsParts.length>=3){
                TGCSM.setText(gcsParts[0]);
                TGCSV.setText(gcsParts[1]);
                TGCSE.setText(gcsParts[2]);
            }
        }
        
        TKesimpulan.setText(rs.getString("kesimpulan"));

        String sensorik = rs.getString("gangguan_sensorik");
        if (sensorik != null) {
            ChkSensorikTidak.setSelected(sensorik.contains("Tidak"));
            ChkSensorikDengar.setSelected(sensorik.contains("Dengar"));
            ChkSensorikBahasa.setSelected(sensorik.contains("Bahasa"));
            ChkSensorikBicara.setSelected(sensorik.contains("Bicara"));
        }

        String muskulo = rs.getString("muskuloskeletal");
        if (muskulo != null) {
            ChkMuskuloTidak.setSelected(muskulo.contains("Tidak"));
            ChkMuskuloLumpuh.setSelected(muskulo.contains("Lumpuh"));
            ChkMuskuloTraksi.setSelected(muskulo.contains("Traksi"));
            if (muskulo.contains(":")) TKekuatanOtot.setText(muskulo.split(":")[1]);
        }

        TProtese.setText(rs.getString("protesa"));
        TEdema.setText(rs.getString("edema"));
        ChkAstma.setSelected(rs.getString("riwayat_asma").equals("Ya"));
        TPernapasan.setText(rs.getString("respirasi"));
        ChkEKG.setSelected(rs.getString("ekg").equals("Ya"));
        ChkThorax.setSelected(rs.getString("foto_thorax").equals("Ya"));
        ChkHepatitis.setSelected(rs.getString("riwayat_hepatitis").equals("Ya"));
        TLainPenyakit.setText(rs.getString("penyakit_lain"));

        String psikologi = rs.getString("kondisi_psikologis");
        if (psikologi != null) {
            ChkRilek.setSelected(psikologi.contains("Rilek"));
            ChkCemas.setSelected(psikologi.contains("Cemas"));
            ChkBanyakBicara.setSelected(psikologi.contains("Banyak Bicara"));
            ChkMenangis.setSelected(psikologi.contains("Menangis"));
        }

        TLab.setText(rs.getString("laboratorium"));
        
        // MASALAH - Use actual column names from decription
        TNyeriBD.setText(rs.getString("masalah_preop_nyeri"));
        TKolabTerapi.setText(rs.getString("masalah_preop_kecemasan")); 

        // INTRA OP - Resti Infeksi
        String aseptik = rs.getString("aseptik_antiseptik");
        if(aseptik!=null){ TAseptikDg.setText(aseptik); }
        String aseptikOleh = rs.getString("aseptik_oleh");
        if(aseptikOleh!=null){ TAseptikOleh.setText(aseptikOleh); }
        ChkKulitUtuhPreOp.setSelected(rs.getString("kulit_preop_utuh").equals("Ya"));
        String klasLuka = rs.getString("klasifikasi_luka");
        if(klasLuka!=null){
            ChkLukaBersih.setSelected(klasLuka.contains("bersih") && !klasLuka.contains("tercemar"));
            ChkLukaBersihTercemar.setSelected(klasLuka.contains("Bersih tercemar"));
            ChkLukaTercemar.setSelected(klasLuka.contains("Tercemar") && !klasLuka.contains("Bersih"));
            ChkLukaKotor.setSelected(klasLuka.contains("Kotor"));
        }
        ChkPasangKateterUrine.setSelected(rs.getString("kateter").equals("Ya"));
        TKateterNo.setText(rs.getString("kateter_no"));
        TKateterUrineCc.setText(rs.getString("kateter_urine_cc"));
        TKateterOleh.setText(rs.getString("kateter_oleh"));
        ChkDrain.setSelected(rs.getString("drain_intraop").equals("Ya"));
        TDrainJenis.setText(rs.getString("drain_intraop_jenis"));
        TDrainCairanCc.setText(rs.getString("drain_intraop_cc"));
        ChkTampon.setSelected(rs.getString("tampon_intraop").equals("Ya"));
        TTamponJenis.setText(rs.getString("tampon_intraop_jenis"));
        TTamponLokasi.setText(rs.getString("tampon_intraop_lokasi"));
        ChKGips.setSelected(rs.getString("gips").equals("Ya"));
        TGipsJenis.setText(rs.getString("gips_jenis"));
        TGipsLokasi.setText(rs.getString("gips_lokasi"));

        // INTRA OP - Hipotermi
        TIntraSuhu.setText(rs.getString("matras_penghangat"));
        TIntraOlehHipo.setText(rs.getString("hipo_oleh"));
        ChkIntraHipo2.setSelected(rs.getString("cairan_iv_hangat").equals("Ya"));
        ChkIntraHipo3.setSelected(rs.getString("irigasi_hangat").equals("Ya"));

        // INTRA OP - Integritas Kulit
        String posOp = rs.getString("posisi_operasi");
        if(posOp!=null){
            ChkIntraKulitPosisiSupine.setSelected(posOp.contains("Supine"));
            ChkIntraKulitPosisiProne.setSelected(posOp.contains("Prone"));
            ChkIntraKulitPosisiLithotomy.setSelected(posOp.contains("Lithotomy"));
            ChkIntraKulitPosisiLateral.setSelected(posOp.contains("Lateral") && !posOp.contains("Lateral Kiri") && !posOp.contains("Lateral Kanan"));
            ChkIntraKulitPosisiLateralKiri.setSelected(posOp.contains("Lateral Kiri"));
            ChkIntraKulitPosisiLateralKanan.setSelected(posOp.contains("Lateral Kanan"));
        }
        TIntraPosisiLain.setText(rs.getString("posisi_lain"));
        String alatPos = rs.getString("alat_posisi");
        if(alatPos!=null){
            ChkIntraBantal.setSelected(alatPos.contains("Bantal/guling"));
            ChkIntraPasir.setSelected(alatPos.contains("Bantal/Pasir"));
            ChkIntraPenyangga.setSelected(alatPos.contains("Penyangga Kaki"));
        }
        String pad = rs.getString("bantalan");
        if(pad!=null){
            ChkIntraPaddingSiku.setSelected(pad.contains("Siku"));
            ChkIntraPaddingPoplitea.setSelected(pad.contains("Poplitea"));
        }

        // INTRA OP - Injuri
        ChkIntraSabuk.setSelected(rs.getString("sabuk_pengaman").equals("Ya"));
        TIntraSabukPada.setText(rs.getString("sabuk_pada"));
        ChkIntraPlate.setSelected(rs.getString("plat_grounding").equals("Ya"));
        TIntraPlatePada.setText(rs.getString("plate_pada"));
        TIntraPlateOleh.setText(rs.getString("plate_oleh"));
        TIntraCouterSetting.setText(rs.getString("elektrokauter_koagulasi"));
        TIntraCuttingSetting.setText(rs.getString("elektrokauter_cutting"));
        String kulitP = rs.getString("kulit_setelah_plate");
        if(kulitP!=null){
            ChkIntraKulitUtuh.setSelected(kulitP.contains("Utuh"));
            ChkIntraKulitMerah.setSelected(kulitP.contains("kemerahan"));
            ChkIntraKulitLecet.setSelected(kulitP.contains("Lecet"));
            ChkIntraKulitLuka.setSelected(kulitP.contains("Luka"));
        }
        ChkIntraTorniquet.setSelected(rs.getString("tourniquet_waktu").equals("Ya"));
        TIntraTorniquetPada.setText(rs.getString("tourniquet_pada"));
        TIntraTorniquetOleh.setText(rs.getString("tourniquet_oleh"));
        TIntraInflate.setText(rs.getString("tourniquet_inflate"));
        TIntraDeflate.setText(rs.getString("tourniquet_deflate"));
        TIntraTekanan.setText(rs.getString("tourniquet_tekanan"));
        ChkIntraHitungXRayYa.setSelected(rs.getString("hitung_xray").equals("Ya"));
        ChkIntraHitungXRayTidak.setSelected(rs.getString("hitung_xray").equals("Tidak"));
        TIntraHitungXRayKet.setText(rs.getString("hitung_xray_keterangan"));

        // INTRA OP - Keseimbangan cairan
        TIntraInfus.setText(rs.getString("cairan_infus"));
        TIntraInfusJenis.setText(rs.getString("cairan_infus_jenis"));
        TIntraUrine.setText(rs.getString("urine"));
        TIntraNGT.setText(rs.getString("ngt"));
        TIntraCairanLain.setText(rs.getString("cairan_lain_cc"));
        ChkIntraPemberianDarahYa.setSelected(rs.getString("pemberian_darah").equals("Ya"));
        ChkIntraPemberianDarahTidak.setSelected(rs.getString("pemberian_darah").equals("Tidak"));
        ChkIntraSpinal.setSelected(false);
        ChkIntraPengeluaran.setSelected(false);

        // POST OP
        TPostMasukRR.setText(rs.getString("jam_masuk_rr"));
        TPostTD.setText(rs.getString("td"));
        TPostHR.setText(rs.getString("hr"));
        TPostRR.setText(rs.getString("rr"));
        TPostSpO2.setText(rs.getString("spo2"));
        TPostDiagnosa.setText(rs.getString("diagnosa_postop"));
        TPostDrain.setText(rs.getString("drain"));
        TPostTampon.setText(rs.getString("tampon"));
        TPostPA.setText(rs.getString("pa"));
        TPostResp.setText(rs.getString("respirasi_postop"));
        TAnestesiLama.setText(rs.getString("lama_anestesi"));
        TPembedahanLama.setText(rs.getString("lama_operasi"));
        String jenisAnesPostop = rs.getString("jenis_anestesi_postop");
        if(jenisAnesPostop!=null){
            ChkPostUmum.setSelected(jenisAnesPostop.contains("Umum"));
            ChkPostSpinalRegional.setSelected(jenisAnesPostop.contains("Spinal"));
            ChkPostBlokPerifer.setSelected(jenisAnesPostop.contains("Blok"));
        }
        String kulitPost = rs.getString("kulit_postop");
        if(kulitPost!=null){
            ChkPostDingin.setSelected(kulitPost.contains("Dingin"));
            ChkPostHangat.setSelected(kulitPost.contains("Hangat"));
            ChkPostKering.setSelected(kulitPost.contains("kering"));
            ChkPostUtuh.setSelected(kulitPost.contains("Utuh"));
            ChkPostLembab.setSelected(kulitPost.contains("Lembab"));
            ChkPostPink.setSelected(kulitPost.contains("Pink"));
        }
        String napPost = rs.getString("respirasi_postop");
        if(napPost!=null){
            ChkPostIntubasi.setSelected(napPost.contains("Intubasi"));
            ChkPostEkstubasi.setSelected(napPost.contains("Ekstubasi"));
            ChkPostSpontan.setSelected(napPost.contains("Spontan"));
            ChkPostSpasme.setSelected(napPost.contains("Spasme"));
            ChkPostSekret.setSelected(napPost.contains("sekret"));
        }
        String sadarPost = rs.getString("kesadaran");
        if(sadarPost!=null){
            ChkPostBangun.setSelected(sadarPost.contains("bangun"));
            ChkPostSadar.setSelected(sadarPost.contains("Sadar"));
        }
        String posPost = rs.getString("posisi_pasien");
        if(posPost!=null){
            ChkPostTerlentang.setSelected(posPost.contains("Terlentang"));
            ChkPostLateralKiriKanan.setSelected(posPost.contains("lateral"));
            ChkPostEkstremitasBawah.setSelected(posPost.contains("Ektremitas"));
        }

        KdPetugas.setText(rs.getString("nip_perawat_ok"));
        NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_perawat_ok")));
        KdPerawatRuangPulih.setText(rs.getString("nip_perawat_rr"));
        NmPerawatRuangPulih.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_perawat_rr")));
        KdPerawatRuangan.setText(rs.getString("nip_perawat_ruangan"));
        NmPerawatRuangan.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_perawat_ruangan")));
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(namapasien);
    }
    

    // Method overload for DlgBookingOperasi
    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        TUmur.setText(Sequel.cariIsi("select concat(umurdaftar,' ',sttsumur) from reg_periksa where no_rawat=?", norwt));
        TKamar.setText(Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? order by kamar_inap.tgl_masuk desc limit 1", norwt));
        TTglMasuk.setText(Sequel.cariIsi("select tgl_registrasi from reg_periksa where no_rawat=?", norwt));
        TAlamat.setText(Sequel.cariIsi("select almt_pj from reg_periksa where no_rawat=?", norwt));
        loadTimOperasi(norwt);
    }

    private void loadTimOperasi(String norwt) {
        // Load kamar operasi & waktu operasi dari booking_operasi
        TKamarOperasi.setText(Sequel.cariIsi(
            "select r.nm_ruang_ok from booking_operasi b inner join ruang_ok r on b.kd_ruang_ok=r.kd_ruang_ok where b.no_rawat=? order by b.tanggal desc limit 1", norwt));
        String jamMulai = Sequel.cariIsi("select concat(b.tanggal,' ',b.jam_mulai) from booking_operasi b where b.no_rawat=? order by b.tanggal desc limit 1", norwt);
        if(jamMulai!=null && !jamMulai.isEmpty()){
            try { TWaktuOperasiMulai.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jamMulai)); } catch(Exception e){}
        }
        String jamSelesai = Sequel.cariIsi("select concat(b.tanggal,' ',b.jam_selesai) from booking_operasi b where b.no_rawat=? order by b.tanggal desc limit 1", norwt);
        if(jamSelesai!=null && !jamSelesai.isEmpty()){
            try { TWaktuOperasiSelesai.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jamSelesai)); } catch(Exception e){}
        }
        // Load tanggal masuk OK dari booking_operasi
        String tglOp = Sequel.cariIsi("select concat(b.tanggal,' ',b.jam_mulai) from booking_operasi b where b.no_rawat=? order by b.tanggal desc limit 1", norwt);
        if (tglOp != null && !tglOp.isEmpty()) {
            try { TglMasukOK.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tglOp)); } catch (Exception e) {}
        }
        // Load Dx Pre-Op dari laporan_operasi
        TDiagnosaPreOp.setText(Sequel.cariIsi(
            "select diagnosa_preop from laporan_operasi where no_rawat=? order by tanggal desc limit 1", norwt));

        try {
            ps = koneksi.prepareStatement(
                "select o.operator1, d1.nm_dokter as nm_operator1, " +
                "o.operator2, d2.nm_dokter as nm_operator2, " +
                "o.asisten_operator1, p1.nama as nm_asisten1, " +
                "o.instrumen, p2.nama as nm_instrumen, " +
                "o.dokter_anestesi, d3.nm_dokter as nm_anestesi, " +
                "o.asisten_anestesi, p3.nama as nm_asisten_anestesi, " +
                "o.omloop, p4.nama as nm_sirkulasi, " +
                "o.perawaat_resusitas, p5.nama as nm_penata " +
                "from operasi o " +
                "left join dokter d1 on o.operator1=d1.kd_dokter " +
                "left join dokter d2 on o.operator2=d2.kd_dokter " +
                "left join petugas p1 on o.asisten_operator1=p1.nip " +
                "left join petugas p2 on o.instrumen=p2.nip " +
                "left join dokter d3 on o.dokter_anestesi=d3.kd_dokter " +
                "left join petugas p3 on o.asisten_anestesi=p3.nip " +
                "left join petugas p4 on o.omloop=p4.nip " +
                "left join petugas p5 on o.perawaat_resusitas=p5.nip " +
                "where o.no_rawat=? order by o.tgl_operasi desc limit 1");
            try {
                ps.setString(1, norwt);
                rs = ps.executeQuery();
                if (rs.next()) {
                    KdDokterBedah.setText(rs.getString("operator1"));
                    NmDokterBedah.setText(rs.getString("nm_operator1"));
                    KdDokterBedah2.setText(rs.getString("operator2"));
                    NmDokterBedah2.setText(rs.getString("nm_operator2"));
                    KdAsistenBedah.setText(rs.getString("asisten_operator1"));
                    NmAsistenBedah.setText(rs.getString("nm_asisten1"));
                    KdInstrumen.setText(rs.getString("instrumen"));
                    NmInstrumen.setText(rs.getString("nm_instrumen"));
                    KdDokterAnestesi.setText(rs.getString("dokter_anestesi"));
                    NmDokterAnestesi.setText(rs.getString("nm_anestesi"));
                    KdAsistenAnestesi.setText(rs.getString("asisten_anestesi"));
                    NmAsistenAnestesi.setText(rs.getString("nm_asisten_anestesi"));
                    KdSirkulasi.setText(rs.getString("omloop"));
                    NmSirkulasi.setText(rs.getString("nm_sirkulasi"));
                    KdPenata.setText(rs.getString("perawaat_resusitas"));
                    NmPenata.setText(rs.getString("nm_penata"));
                }
            } catch (Exception e) {
                System.out.println("Notif loadTimOperasi : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notif loadTimOperasi : " + e);
        }
    }
    
    public void emptTeksDataAwal() {
        TTindakan.setText(""); TJenisAnestesi.setText(""); TCatatan.setText("");
        TKeluarRRpk.setDate(new Date()); TglMasukOK.setDate(new Date());
    }

    public void tampilDataAwal() {
        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement("select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.nip, pt.nama " +
                    "from asuhan_keperawatan_kamar_bedah_data_awal a " +
                    "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join petugas pt on a.nip=pt.nip " +
                    "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ? or pt.nama like ?) " +
                    "order by a.tanggal");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%" );
                ps.setString(4, "%" + TCari.getText().trim() + "%" );
                ps.setString(5, "%" + TCari.getText().trim() + "%" );
                ps.setString(6, "%" + TCari.getText().trim() + "%" );
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeDataAwal.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("nama"), "Tersimpan"
                    });
                }
                LCount.setText("Record : " + tabModeDataAwal.getRowCount());
            } catch (Exception e) {
                System.out.println("Notif Data Awal : " + e);
            } finally {
                if (rs != null) { rs.close(); }
                if (ps != null) { ps.close(); }
            }
        } catch (Exception e) {
            System.out.println("Notif Data Awal : " + e);
        }
    }
    
    public void getDataDataAwal() {
        if(tbDataAwal.getSelectedRow()!= -1){
            TNoRw.setText(tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),0).toString());
            TNoRM.setText(tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),1).toString());
            TPasien.setText(tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),2).toString());
            Valid.SetTgl(TglMasukOK, tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),3).toString());
            // Load tim operasi, kamar, waktu, dx dari tabel operasi/booking/laporan
            loadTimOperasi(TNoRw.getText());
            try {
                ps = koneksi.prepareStatement("select a.tindakan, a.jenis_anestesi, a.catatan, a.nip, pt.nama " +
                        "from asuhan_keperawatan_kamar_bedah_data_awal a " +
                        "left join petugas pt on a.nip=pt.nip " +
                        "where a.no_rawat=? and a.tanggal=?");
                try {
                    ps.setString(1, tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),0).toString());
                    ps.setString(2, tbDataAwal.getValueAt(tbDataAwal.getSelectedRow(),3).toString());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        TTindakan.setText(rs.getString("tindakan"));
                        TJenisAnestesi.setText(rs.getString("jenis_anestesi"));
                        TCatatan.setText(rs.getString("catatan"));
                        KdPetugas.setText(rs.getString("nip"));
                        NmPetugas.setText(rs.getString("nama"));
                    }
                } catch (Exception e) {
                    System.out.println("Notif Data Awal GetData : " + e);
                } finally {
                    if (rs != null) { rs.close(); }
                    if (ps != null) { ps.close(); }
                }
            } catch (Exception e) {
                System.out.println("Notif Data Awal GetData : " + e);
            }
        }
    }
    
    
    private void initTabDataAwal() {
        internalFrameDataAwal = new widget.InternalFrame();
        internalFrameDataAwal.setLayout(new BorderLayout());
        internalFrameDataAwal.setBackground(new Color(255, 255, 255));
        
        ScrollDataAwal = new widget.ScrollPane();
        tbDataAwal = new widget.Table();
        tabModeDataAwal = new javax.swing.table.DefaultTableModel(new Object[][]{}, 
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tgl. Asuhan", "Petugas", "Ceklist"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbDataAwal.setModel(tabModeDataAwal);
        tbDataAwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabPane.getSelectedIndex() == 0) {
                    getDataDataAwal();
                }
            }
        });
        tbDataAwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (tabPane.getSelectedIndex() == 0) {
                    if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                        getDataDataAwal();
                    }
                }
            }
        });
        ScrollDataAwal.setViewportView(tbDataAwal);
        
        FormInputDataAwal = new widget.PanelBiasa();
        FormInputDataAwal.setPreferredSize(new Dimension(800, 130));
        FormInputDataAwal.setLayout(null);

        JLabel lblDataAwal = new JLabel("PENGKAJIAN DATA AWAL");
        lblDataAwal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDataAwal.setBounds(10, 10, 300, 23);
        FormInputDataAwal.add(lblDataAwal);

        widget.Label lblTind = new widget.Label(); lblTind.setText("Tindakan :"); lblTind.setBounds(10, 35, 120, 23); FormInputDataAwal.add(lblTind);
        TTindakan = new TextBox(); TTindakan.setBounds(135, 35, 640, 23); FormInputDataAwal.add(TTindakan);

        widget.Label lblJenis = new widget.Label(); lblJenis.setText("Jenis Anestesi :"); lblJenis.setBounds(10, 60, 120, 23); FormInputDataAwal.add(lblJenis);
        TJenisAnestesi = new TextBox(); TJenisAnestesi.setBounds(135, 60, 640, 23); FormInputDataAwal.add(TJenisAnestesi);

        widget.Label lblCat = new widget.Label(); lblCat.setText("Catatan :"); lblCat.setBounds(10, 85, 120, 23); FormInputDataAwal.add(lblCat);
        TCatatan = new TextBox(); TCatatan.setBounds(135, 85, 640, 23); FormInputDataAwal.add(TCatatan);
        
        // Listeners for Buttons
        BtnDokterBedah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dokterBedah.emptTeks();
                dokterBedah.isCek();
                dokterBedah.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                dokterBedah.setLocationRelativeTo(internalFrame1);
                dokterBedah.setVisible(true);
            }
        });
        dokterBedah.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(dokterBedah.getTable().getSelectedRow() != -1){
                    KdDokterBedah.setText(dokterBedah.getTable().getValueAt(dokterBedah.getTable().getSelectedRow(),0).toString());
                    NmDokterBedah.setText(dokterBedah.getTable().getValueAt(dokterBedah.getTable().getSelectedRow(),1).toString());
                }
            }
        });
        
        BtnAsistenBedah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asistenBedah.emptTeks();
                asistenBedah.isCek();
                asistenBedah.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                asistenBedah.setLocationRelativeTo(internalFrame1);
                asistenBedah.setVisible(true);
            }
        });
        asistenBedah.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(asistenBedah.getTable().getSelectedRow() != -1){
                    KdAsistenBedah.setText(asistenBedah.getTable().getValueAt(asistenBedah.getTable().getSelectedRow(),0).toString());
                    NmAsistenBedah.setText(asistenBedah.getTable().getValueAt(asistenBedah.getTable().getSelectedRow(),1).toString());
                }
            }
        });
        
        BtnDokterAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dokterAnestesi.emptTeks();
                dokterAnestesi.isCek();
                dokterAnestesi.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                dokterAnestesi.setLocationRelativeTo(internalFrame1);
                dokterAnestesi.setVisible(true);
            }
        });
        dokterAnestesi.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(dokterAnestesi.getTable().getSelectedRow() != -1){
                    KdDokterAnestesi.setText(dokterAnestesi.getTable().getValueAt(dokterAnestesi.getTable().getSelectedRow(),0).toString());
                    NmDokterAnestesi.setText(dokterAnestesi.getTable().getValueAt(dokterAnestesi.getTable().getSelectedRow(),1).toString());
                }
            }
        });
        
        BtnAsistenAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asistenAnestesi.emptTeks();
                asistenAnestesi.isCek();
                asistenAnestesi.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                asistenAnestesi.setLocationRelativeTo(internalFrame1);
                asistenAnestesi.setVisible(true);
            }
        });
        asistenAnestesi.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(asistenAnestesi.getTable().getSelectedRow() != -1){
                    KdAsistenAnestesi.setText(asistenAnestesi.getTable().getValueAt(asistenAnestesi.getTable().getSelectedRow(),0).toString());
                    NmAsistenAnestesi.setText(asistenAnestesi.getTable().getValueAt(asistenAnestesi.getTable().getSelectedRow(),1).toString());
                }
            }
        });
        
        BtnDokterBedah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dokterBedah2.emptTeks();
                dokterBedah2.isCek();
                dokterBedah2.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                dokterBedah2.setLocationRelativeTo(internalFrame1);
                dokterBedah2.setVisible(true);
            }
        });
        dokterBedah2.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(dokterBedah2.getTable().getSelectedRow() != -1){
                    KdDokterBedah2.setText(dokterBedah2.getTable().getValueAt(dokterBedah2.getTable().getSelectedRow(),0).toString());
                    NmDokterBedah2.setText(dokterBedah2.getTable().getValueAt(dokterBedah2.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        BtnInstrumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instrumenDlg.emptTeks();
                instrumenDlg.isCek();
                instrumenDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                instrumenDlg.setLocationRelativeTo(internalFrame1);
                instrumenDlg.setVisible(true);
            }
        });
        instrumenDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(instrumenDlg.getTable().getSelectedRow() != -1){
                    KdInstrumen.setText(instrumenDlg.getTable().getValueAt(instrumenDlg.getTable().getSelectedRow(),0).toString());
                    NmInstrumen.setText(instrumenDlg.getTable().getValueAt(instrumenDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        BtnPenata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                penataDlg.emptTeks();
                penataDlg.isCek();
                penataDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                penataDlg.setLocationRelativeTo(internalFrame1);
                penataDlg.setVisible(true);
            }
        });
        penataDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(penataDlg.getTable().getSelectedRow() != -1){
                    KdPenata.setText(penataDlg.getTable().getValueAt(penataDlg.getTable().getSelectedRow(),0).toString());
                    NmPenata.setText(penataDlg.getTable().getValueAt(penataDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        BtnSirkulasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sirkulasi.emptTeks();
                sirkulasi.isCek();
                sirkulasi.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                sirkulasi.setLocationRelativeTo(internalFrame1);
                sirkulasi.setVisible(true);
            }
        });
        sirkulasi.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(sirkulasi.getTable().getSelectedRow() != -1){
                    KdSirkulasi.setText(sirkulasi.getTable().getValueAt(sirkulasi.getTable().getSelectedRow(),0).toString());
                    NmSirkulasi.setText(sirkulasi.getTable().getValueAt(sirkulasi.getTable().getSelectedRow(),1).toString());
                }
            }
        });
        
        ChkInputDataAwal = new widget.CekBox();
        ChkInputDataAwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputDataAwal.setMnemonic('1');
        ChkInputDataAwal.setText(".: Input Data");
        ChkInputDataAwal.setToolTipText("Alt+1");
        ChkInputDataAwal.setBorderPainted(true);
        ChkInputDataAwal.setBorderPaintedFlat(true);
        ChkInputDataAwal.setFocusable(false);
        ChkInputDataAwal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInputDataAwal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInputDataAwal.setName("ChkInputDataAwal");
        ChkInputDataAwal.setPreferredSize(new Dimension(192, 20));
        ChkInputDataAwal.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputDataAwal.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputDataAwal.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputDataAwal.setSelected(true);
        ChkInputDataAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isFormDataAwal();
            }
        });
        
        ScrollInputDataAwal = new widget.ScrollPane();
        ScrollInputDataAwal.setViewportView(FormInputDataAwal);
        ScrollInputDataAwal.setPreferredSize(new Dimension(800, 130));

        PanelInputDataAwal = new JPanel(new BorderLayout());
        PanelInputDataAwal.setOpaque(false);
        PanelInputDataAwal.setPreferredSize(new Dimension(WIDTH, 150));
        PanelInputDataAwal.add(ScrollInputDataAwal, BorderLayout.CENTER);
        PanelInputDataAwal.add(ChkInputDataAwal, BorderLayout.PAGE_END);
        
        ScrollDataAwal.setPreferredSize(new java.awt.Dimension(WIDTH, 150));
        internalFrameDataAwal.add(ScrollDataAwal, BorderLayout.NORTH);
        internalFrameDataAwal.add(PanelInputDataAwal, BorderLayout.CENTER);
    }

    private void isFormDataAwal() {
        if (ChkInputDataAwal.isSelected() == true) {
            ChkInputDataAwal.setVisible(false);
            PanelInputDataAwal.setPreferredSize(new Dimension(WIDTH, 150));
            FormInputDataAwal.setVisible(true);
            ChkInputDataAwal.setVisible(true);
        } else {
            ChkInputDataAwal.setVisible(false);
            PanelInputDataAwal.setPreferredSize(new Dimension(WIDTH, 20));
            FormInputDataAwal.setVisible(false);
            ChkInputDataAwal.setVisible(true);
        }
    }

    private void initTabPreOp() {
        internalFramePre = new widget.InternalFrame();
        internalFramePre.setLayout(new BorderLayout());
        internalFramePre.setBackground(new Color(255, 255, 255));
        
        ScrollPre = new widget.ScrollPane();
        tbPre = new widget.Table();
        tabModePre = new javax.swing.table.DefaultTableModel(new Object[][]{}, 
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tgl. Asuhan", "Petugas", "Kesimpulan", "GCS"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbPre.setModel(tabModePre);
        ScrollPre.setViewportView(tbPre);
        tbPre.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(tabModePre.getRowCount()!=0){
                    try {
                        getDataPre();
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        FormInputPre = new widget.PanelBiasa();
        FormInputPre.setPreferredSize(new Dimension(880, 680));
        FormInputPre.setLayout(null);

        int y = 10;
        JLabel lblPreOp = new JLabel("PENGKAJIAN PRE OPERASI");
        lblPreOp.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblPreOp.setBounds(10, y, 300, 23); FormInputPre.add(lblPreOp);

        // --- Checklist Pre-Op ---
        y += 32;
        widget.Label lblCeklist = new widget.Label(); lblCeklist.setText("Evaluasi / review ceklist pre - op :"); lblCeklist.setBounds(10, y, 210, 23); FormInputPre.add(lblCeklist);
        ChkIdentitas = new CekBox(); ChkIdentitas.setText("Identitas pasien"); ChkIdentitas.setBounds(225, y, 130, 23); FormInputPre.add(ChkIdentitas);
        ChkInformend = new CekBox(); ChkInformend.setText("Informend consent"); ChkInformend.setBounds(360, y, 140, 23); FormInputPre.add(ChkInformend);
        ChkVerifikasiIsolasi = new CekBox(); ChkVerifikasiIsolasi.setText("Verifikasi isolasi"); ChkVerifikasiIsolasi.setBounds(505, y, 125, 23); FormInputPre.add(ChkVerifikasiIsolasi);
        ChkVerifikasiAlergi = new CekBox(); ChkVerifikasiAlergi.setText("Verifikasi alergi"); ChkVerifikasiAlergi.setBounds(635, y, 125, 23); FormInputPre.add(ChkVerifikasiAlergi);
        ChkVerifikasiTindakan = new CekBox(); ChkVerifikasiTindakan.setText("Verifikasi tindakan"); ChkVerifikasiTindakan.setBounds(765, y, 135, 23); FormInputPre.add(ChkVerifikasiTindakan);

        // --- Puasa, Levamen, Markes, Pengukuran ---
        y += 32;
        widget.Label lblPuasa = new widget.Label(); lblPuasa.setText("Puasa jam :"); lblPuasa.setBounds(10, y, 75, 23); FormInputPre.add(lblPuasa);
        TPuasaJam = new widget.Tanggal(); TPuasaJam.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TPuasaJam.setBounds(90, y, 130, 23); FormInputPre.add(TPuasaJam);
        ChkLevamen = new CekBox(); ChkLevamen.setText("Levamen"); ChkLevamen.setBounds(230, y, 85, 23); FormInputPre.add(ChkLevamen);
        ChkMarkes = new CekBox(); ChkMarkes.setText("Markes"); ChkMarkes.setBounds(320, y, 80, 23); FormInputPre.add(ChkMarkes);
        widget.Label lblPengukuran = new widget.Label(); lblPengukuran.setText("Pengukuran jam :"); lblPengukuran.setBounds(410, y, 110, 23); FormInputPre.add(lblPengukuran);
        TPengukuran = new widget.Tanggal(); TPengukuran.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TPengukuran.setBounds(525, y, 130, 23); FormInputPre.add(TPengukuran);

        // --- Kulit ---
        y += 32;
        JLabel lblKulit = new JLabel("Kulit :"); lblKulit.setBounds(10, y, 45, 23); FormInputPre.add(lblKulit);
        ChkDingin = new CekBox(); ChkDingin.setText("Dingin"); ChkDingin.setBounds(60, y, 70, 23); FormInputPre.add(ChkDingin);
        ChkHangat = new CekBox(); ChkHangat.setText("Hangat"); ChkHangat.setBounds(135, y, 75, 23); FormInputPre.add(ChkHangat);
        ChkKering = new CekBox(); ChkKering.setText("Kering"); ChkKering.setBounds(215, y, 70, 23); FormInputPre.add(ChkKering);
        ChkLembab = new CekBox(); ChkLembab.setText("Lembab"); ChkLembab.setBounds(290, y, 80, 23); FormInputPre.add(ChkLembab);
        ChkUtuh = new CekBox(); ChkUtuh.setText("Utuh"); ChkUtuh.setBounds(375, y, 60, 23); FormInputPre.add(ChkUtuh);

        // --- Tingkat Kesadaran ---
        y += 32;
        JLabel lblKesadaran = new JLabel("Tingkat kesadaran :"); lblKesadaran.setBounds(10, y, 130, 23); FormInputPre.add(lblKesadaran);
        ChkCompos = new CekBox(); ChkCompos.setText("Compos mentis"); ChkCompos.setBounds(145, y, 120, 23); FormInputPre.add(ChkCompos);
        ChkApatis = new CekBox(); ChkApatis.setText("Apatis"); ChkApatis.setBounds(270, y, 70, 23); FormInputPre.add(ChkApatis);
        ChkSomnolen = new CekBox(); ChkSomnolen.setText("Somnolen"); ChkSomnolen.setBounds(345, y, 90, 23); FormInputPre.add(ChkSomnolen);
        ChkSoporcoma = new CekBox(); ChkSoporcoma.setText("Soporcoma"); ChkSoporcoma.setBounds(440, y, 100, 23); FormInputPre.add(ChkSoporcoma);
        ChkComa = new CekBox(); ChkComa.setText("Coma"); ChkComa.setBounds(545, y, 70, 23); FormInputPre.add(ChkComa);

        // --- GCS ---
        y += 32;
        JLabel lblGCS = new JLabel("GCS  M :"); lblGCS.setBounds(10, y, 60, 23); FormInputPre.add(lblGCS);
        TGCSM = new TextBox(); TGCSM.setBounds(75, y, 40, 23); FormInputPre.add(TGCSM);
        JLabel lblGCSV = new JLabel("V :"); lblGCSV.setBounds(125, y, 20, 23); FormInputPre.add(lblGCSV);
        TGCSV = new TextBox(); TGCSV.setBounds(150, y, 40, 23); FormInputPre.add(TGCSV);
        JLabel lblGCSE = new JLabel("E :"); lblGCSE.setBounds(200, y, 20, 23); FormInputPre.add(lblGCSE);
        TGCSE = new TextBox(); TGCSE.setBounds(225, y, 40, 23); FormInputPre.add(TGCSE);
        JLabel lblKesimpulan = new JLabel("Kesimpulan :"); lblKesimpulan.setBounds(280, y, 85, 23); FormInputPre.add(lblKesimpulan);
        TKesimpulan = new TextBox(); TKesimpulan.setBounds(370, y, 250, 23); FormInputPre.add(TKesimpulan);

        // --- Gangguan Sensorik ---
        y += 32;
        JLabel lblSensorik = new JLabel("Gangguan Sensorik :"); lblSensorik.setBounds(10, y, 135, 23); FormInputPre.add(lblSensorik);
        ChkSensorikTidak = new CekBox(); ChkSensorikTidak.setText("Tidak ada"); ChkSensorikTidak.setBounds(150, y, 90, 23); FormInputPre.add(ChkSensorikTidak);
        ChkSensorikDengar = new CekBox(); ChkSensorikDengar.setText("Pendengaran"); ChkSensorikDengar.setBounds(245, y, 110, 23); FormInputPre.add(ChkSensorikDengar);
        ChkSensorikBahasa = new CekBox(); ChkSensorikBahasa.setText("Bahasa"); ChkSensorikBahasa.setBounds(360, y, 75, 23); FormInputPre.add(ChkSensorikBahasa);
        ChkSensorikBicara = new CekBox(); ChkSensorikBicara.setText("Bicara"); ChkSensorikBicara.setBounds(440, y, 75, 23); FormInputPre.add(ChkSensorikBicara);

        // --- Muskuloskeletal ---
        y += 32;
        JLabel lblMuskulo = new JLabel("Muskuloskeletal :"); lblMuskulo.setBounds(10, y, 120, 23); FormInputPre.add(lblMuskulo);
        ChkMuskuloTidak = new CekBox(); ChkMuskuloTidak.setText("Tidak ada"); ChkMuskuloTidak.setBounds(135, y, 90, 23); FormInputPre.add(ChkMuskuloTidak);
        ChkMuskuloLumpuh = new CekBox(); ChkMuskuloLumpuh.setText("Lumpuh"); ChkMuskuloLumpuh.setBounds(230, y, 80, 23); FormInputPre.add(ChkMuskuloLumpuh);
        ChkMuskuloTraksi = new CekBox(); ChkMuskuloTraksi.setText("Traksi"); ChkMuskuloTraksi.setBounds(315, y, 70, 23); FormInputPre.add(ChkMuskuloTraksi);
        JLabel lblKekuatan = new JLabel("Kekuatan otot :"); lblKekuatan.setBounds(390, y, 100, 23); FormInputPre.add(lblKekuatan);
        TKekuatanOtot = new TextBox(); TKekuatanOtot.setBounds(495, y, 130, 23); FormInputPre.add(TKekuatanOtot);

        // --- Protase/alat bantu ---
        y += 32;
        JLabel lblProtase = new JLabel("Protase/alat bantu :"); lblProtase.setBounds(10, y, 130, 23); FormInputPre.add(lblProtase);
        ChkHearing = new CekBox(); ChkHearing.setText("Hearing ead"); ChkHearing.setBounds(145, y, 100, 23); FormInputPre.add(ChkHearing);
        ChkKacamata = new CekBox(); ChkKacamata.setText("kacamata"); ChkKacamata.setBounds(250, y, 90, 23); FormInputPre.add(ChkKacamata);
        JLabel lblProtese = new JLabel("Protese :"); lblProtese.setBounds(345, y, 65, 23); FormInputPre.add(lblProtese);
        TProtese = new TextBox(); TProtese.setBounds(415, y, 210, 23); FormInputPre.add(TProtese);

        // --- Cardiopulmonal ---
        y += 32;
        JLabel lblCardio = new JLabel("Cardiopulmonal :"); lblCardio.setBounds(10, y, 115, 23); FormInputPre.add(lblCardio);
        JLabel lblEdema = new JLabel("Edema, lokasi :"); lblEdema.setBounds(130, y, 100, 23); FormInputPre.add(lblEdema);
        TEdema = new TextBox(); TEdema.setBounds(235, y, 180, 23); FormInputPre.add(TEdema);
        ChkAstma = new CekBox(); ChkAstma.setText("Riwayat astma"); ChkAstma.setBounds(425, y, 120, 23); FormInputPre.add(ChkAstma);

        y += 32;
        JLabel lblPernapasan = new JLabel("Pernapasan, sebutkan :"); lblPernapasan.setBounds(10, y, 150, 23); FormInputPre.add(lblPernapasan);
        TPernapasan = new TextBox(); TPernapasan.setBounds(165, y, 230, 23); FormInputPre.add(TPernapasan);
        ChkEKG = new CekBox(); ChkEKG.setText("EKG"); ChkEKG.setBounds(405, y, 55, 23); FormInputPre.add(ChkEKG);
        ChkThorax = new CekBox(); ChkThorax.setText("Thorax Foto"); ChkThorax.setBounds(465, y, 105, 23); FormInputPre.add(ChkThorax);

        // --- Riwayat penyakit ---
        y += 32;
        JLabel lblRiwayat = new JLabel("Riwayat penyakit :"); lblRiwayat.setBounds(10, y, 120, 23); FormInputPre.add(lblRiwayat);
        ChkHepatitis = new CekBox(); ChkHepatitis.setText("Hepatitis"); ChkHepatitis.setBounds(135, y, 90, 23); FormInputPre.add(ChkHepatitis);
        JLabel lblLainPenyakit = new JLabel("Lain-lain :"); lblLainPenyakit.setBounds(230, y, 70, 23); FormInputPre.add(lblLainPenyakit);
        TLainPenyakit = new TextBox(); TLainPenyakit.setBounds(305, y, 230, 23); FormInputPre.add(TLainPenyakit);

        // --- Status Psiko-sosial ---
        y += 32;
        JLabel lblPsiko = new JLabel("Status Psiko-sosial :"); lblPsiko.setBounds(10, y, 130, 23); FormInputPre.add(lblPsiko);
        ChkRilek = new CekBox(); ChkRilek.setText("Rilek"); ChkRilek.setBounds(145, y, 65, 23); FormInputPre.add(ChkRilek);
        ChkCemas = new CekBox(); ChkCemas.setText("Cemas"); ChkCemas.setBounds(215, y, 75, 23); FormInputPre.add(ChkCemas);
        ChkBanyakBicara = new CekBox(); ChkBanyakBicara.setText("Banyak bicara"); ChkBanyakBicara.setBounds(295, y, 115, 23); FormInputPre.add(ChkBanyakBicara);
        ChkMenangis = new CekBox(); ChkMenangis.setText("Menangis"); ChkMenangis.setBounds(415, y, 90, 23); FormInputPre.add(ChkMenangis);

        // --- Data Pendukung ---
        y += 32;
        JLabel lblPendukung = new JLabel("Data pendukung :"); lblPendukung.setBounds(10, y, 120, 23); FormInputPre.add(lblPendukung);

        JLabel lblUSG = new JLabel("USG :"); lblUSG.setBounds(10, y+25, 40, 23); FormInputPre.add(lblUSG);
        TUSG = new widget.TextArea(); TUSG.setLineWrap(true);
        ScrollUSG = new widget.ScrollPane(); ScrollUSG.setViewportView(TUSG);
        ScrollUSG.setBounds(55, y+25, 165, 43); FormInputPre.add(ScrollUSG);

        JLabel lblCT = new JLabel("CT Scan :"); lblCT.setBounds(230, y+25, 65, 23); FormInputPre.add(lblCT);
        TCTScan = new widget.TextArea(); TCTScan.setLineWrap(true);
        ScrollCTScan = new widget.ScrollPane(); ScrollCTScan.setViewportView(TCTScan);
        ScrollCTScan.setBounds(300, y+25, 165, 43); FormInputPre.add(ScrollCTScan);

        JLabel lblMRI = new JLabel("MRI :"); lblMRI.setBounds(475, y+25, 40, 23); FormInputPre.add(lblMRI);
        TMRI = new widget.TextArea(); TMRI.setLineWrap(true);
        ScrollMRI = new widget.ScrollPane(); ScrollMRI.setViewportView(TMRI);
        ScrollMRI.setBounds(520, y+25, 165, 43); FormInputPre.add(ScrollMRI);

        JLabel lblLainD = new JLabel("Lain-lain :"); lblLainD.setBounds(695, y+25, 65, 23); FormInputPre.add(lblLainD);
        TLainPendukung = new widget.TextArea(); TLainPendukung.setLineWrap(true);
        ScrollLainPendukung = new widget.ScrollPane(); ScrollLainPendukung.setViewportView(TLainPendukung);
        ScrollLainPendukung.setBounds(765, y+25, 115, 43); FormInputPre.add(ScrollLainPendukung);

        y += 85;
        JLabel lblLab = new JLabel("Laboratorium :"); lblLab.setBounds(10, y, 100, 23); FormInputPre.add(lblLab);
        TLab = new widget.TextArea(); TLab.setLineWrap(true);
        ScrollLab = new widget.ScrollPane(); ScrollLab.setViewportView(TLab);
        ScrollLab.setBounds(115, y, 765, 43); FormInputPre.add(ScrollLab);

        ChkInputPre = new widget.CekBox();
        ChkInputPre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputPre.setMnemonic('2');
        ChkInputPre.setText(".: Input Data");
        ChkInputPre.setToolTipText("Alt+2");
        ChkInputPre.setBorderPainted(true);
        ChkInputPre.setBorderPaintedFlat(true);
        ChkInputPre.setFocusable(false);
        ChkInputPre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInputPre.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInputPre.setName("ChkInputPre");
        ChkInputPre.setPreferredSize(new Dimension(192, 20));
        ChkInputPre.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputPre.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputPre.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputPre.setSelected(true);
        ChkInputPre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isFormPreOp();
            }
        });
        
        ScrollInputPre = new widget.ScrollPane();
        ScrollInputPre.setViewportView(FormInputPre);
        ScrollInputPre.setPreferredSize(new Dimension(900, 430));

        PanelInputPre = new JPanel(new BorderLayout());
        PanelInputPre.setOpaque(false);
        PanelInputPre.setPreferredSize(new Dimension(WIDTH, 450));
        PanelInputPre.add(ScrollInputPre, BorderLayout.CENTER);
        PanelInputPre.add(ChkInputPre, BorderLayout.PAGE_END);
        
        ScrollPre.setPreferredSize(new java.awt.Dimension(WIDTH, 150));
        internalFramePre.add(ScrollPre, BorderLayout.NORTH);
        internalFramePre.add(PanelInputPre, BorderLayout.CENTER);
    }

    private void isFormPreOp() {
        if (ChkInputPre.isSelected() == true) {
            ChkInputPre.setVisible(false);
            PanelInputPre.setPreferredSize(new Dimension(WIDTH, 450));
            FormInputPre.setVisible(true);
            ChkInputPre.setVisible(true);
        } else {
            ChkInputPre.setVisible(false);
            PanelInputPre.setPreferredSize(new Dimension(WIDTH, 20));
            FormInputPre.setVisible(false);
            ChkInputPre.setVisible(true);
        }
    }

    private void initTabMasalah() {
        internalFrameMasalah = new widget.InternalFrame();
        internalFrameMasalah.setLayout(new BorderLayout());
        internalFrameMasalah.setBackground(new Color(255, 255, 255));
        
        ScrollMasalah = new widget.ScrollPane();
        tbMasalah = new widget.Table();
        tabModeMasalah = new javax.swing.table.DefaultTableModel(new Object[][]{}, 
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tgl. Asuhan", "Masalah Kecemasan", "Masalah Nyeri"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbMasalah.setModel(tabModeMasalah);
        ScrollMasalah.setViewportView(tbMasalah);
        tbMasalah.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(tabModeMasalah.getRowCount()!=0){
                    try {
                        getDataMasalah();
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        FormInputMasalah = new widget.PanelBiasa();
        FormInputMasalah.setPreferredSize(new Dimension(800, 480));
        FormInputMasalah.setLayout(null);
        
        JLabel lblMasalahHeader = new JLabel("MASALAH, INTERVENSI DAN IMPLEMENTASI KEPERAWATAN");
        lblMasalahHeader.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblMasalahHeader.setBounds(10, 10, 400, 23);
        FormInputMasalah.add(lblMasalahHeader);
        
        JLabel lblCemasTxt = new JLabel("Cemas :"); lblCemasTxt.setBounds(10, 35, 60, 23); FormInputMasalah.add(lblCemasTxt);
        ChkCemasRingan = new CekBox(); ChkCemasRingan.setText("Ringan"); ChkCemasRingan.setBounds(70, 35, 70, 23); FormInputMasalah.add(ChkCemasRingan);
        ChkCemasSedang = new CekBox(); ChkCemasSedang.setText("Sedang"); ChkCemasSedang.setBounds(150, 35, 80, 23); FormInputMasalah.add(ChkCemasSedang);
        ChkCemasBerat = new CekBox(); ChkCemasBerat.setText("Berat"); ChkCemasBerat.setBounds(240, 35, 70, 23); FormInputMasalah.add(ChkCemasBerat);
        
        JLabel lblHYDCemas = new JLabel("HYD Kecemasan berkurang/hilang"); lblHYDCemas.setBounds(400, 35, 250, 23); FormInputMasalah.add(lblHYDCemas);
        
        ChkKajiKecemasan = new CekBox(); ChkKajiKecemasan.setText("Kaji tingkat kecemasan"); ChkKajiKecemasan.setBounds(10, 60, 180, 23); FormInputMasalah.add(ChkKajiKecemasan);
        ChkJalinTrust = new CekBox(); ChkJalinTrust.setText("Jalin trust dengan pasien"); ChkJalinTrust.setBounds(200, 60, 190, 23); FormInputMasalah.add(ChkJalinTrust);
        ChkObservasiTTV = new CekBox(); ChkObservasiTTV.setText("Observasi tanda-tanda vital"); ChkObservasiTTV.setBounds(400, 60, 200, 23); FormInputMasalah.add(ChkObservasiTTV);
        ChkDampingi = new CekBox(); ChkDampingi.setText("Dampingi pasien"); ChkDampingi.setBounds(620, 60, 150, 23); FormInputMasalah.add(ChkDampingi);
        
        ChkAjakUngkap = new CekBox(); ChkAjakUngkap.setText("Ajak pasien mengungkapkan tingkat kecemasannya"); ChkAjakUngkap.setBounds(10, 85, 330, 23); FormInputMasalah.add(ChkAjakUngkap);
        ChkCiptaLingkungan = new CekBox(); ChkCiptaLingkungan.setText("Ciptakan lingkungan yang nyaman"); ChkCiptaLingkungan.setBounds(400, 85, 250, 23); FormInputMasalah.add(ChkCiptaLingkungan);
        
        ChkDiskusikan = new CekBox(); ChkDiskusikan.setText("Diskusikan situasi dan hal-hal yang akan dialami"); ChkDiskusikan.setBounds(10, 110, 350, 23); FormInputMasalah.add(ChkDiskusikan);
        ChkKolabObatPenenang = new CekBox(); ChkKolabObatPenenang.setText("Kolaborasi pemberian obat-obat penenang"); ChkKolabObatPenenang.setBounds(370, 110, 280, 23); FormInputMasalah.add(ChkKolabObatPenenang);
        ChkEvaluasiRespon = new CekBox(); ChkEvaluasiRespon.setText("Evaluasi respon"); ChkEvaluasiRespon.setBounds(660, 110, 130, 23); FormInputMasalah.add(ChkEvaluasiRespon);

        // --- NYERI ---
        JLabel lblNyeriTxt = new JLabel("<html><u>Nyeri</u> b.d</html>"); lblNyeriTxt.setBounds(10, 150, 70, 23); FormInputMasalah.add(lblNyeriTxt);
        TNyeriBD = new TextBox(); TNyeriBD.setBounds(85, 150, 200, 23); FormInputMasalah.add(TNyeriBD);
        JLabel lblHYDNyeri = new JLabel("HYD : Kecemasan berkurang / hilang"); lblHYDNyeri.setBounds(400, 150, 250, 23); FormInputMasalah.add(lblHYDNyeri);
        
        ChkKajiTingkatNyeri = new CekBox(); ChkKajiTingkatNyeri.setText("Kaji tingkat nyeri pada lokasi"); ChkKajiTingkatNyeri.setBounds(10, 175, 200, 23); FormInputMasalah.add(ChkKajiTingkatNyeri);
        ChkAjarkanTeknikRelaksasi = new CekBox(); ChkAjarkanTeknikRelaksasi.setText("Ajarkan teknik relaksasi"); ChkAjarkanTeknikRelaksasi.setBounds(220, 175, 170, 23); FormInputMasalah.add(ChkAjarkanTeknikRelaksasi);
        ChkKolabTerapi = new CekBox(); ChkKolabTerapi.setText("Kolaborasi terapi"); ChkKolabTerapi.setBounds(400, 175, 120, 23); FormInputMasalah.add(ChkKolabTerapi);
        TKolabTerapi = new TextBox(); TKolabTerapi.setBounds(525, 175, 230, 23); FormInputMasalah.add(TKolabTerapi);
        
        ChkKajiTTV = new CekBox(); ChkKajiTTV.setText("Kaji TTV tiap"); ChkKajiTTV.setBounds(10, 200, 100, 23); FormInputMasalah.add(ChkKajiTTV);
        TKajiTTVMenit = new TextBox(); TKajiTTVMenit.setBounds(115, 200, 50, 23); FormInputMasalah.add(TKajiTTVMenit);
        JLabel lblMenit = new JLabel("menit"); lblMenit.setBounds(175, 200, 50, 23); FormInputMasalah.add(lblMenit);
        
        ChkInputMasalah = new widget.CekBox();
        ChkInputMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputMasalah.setMnemonic('3');
        ChkInputMasalah.setText(".: Input Data");
        ChkInputMasalah.setToolTipText("Alt+3");
        ChkInputMasalah.setBorderPainted(true);
        ChkInputMasalah.setBorderPaintedFlat(true);
        ChkInputMasalah.setFocusable(false);
        ChkInputMasalah.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInputMasalah.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInputMasalah.setName("ChkInputMasalah");
        ChkInputMasalah.setPreferredSize(new Dimension(192, 20));
        ChkInputMasalah.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputMasalah.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputMasalah.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputMasalah.setSelected(true);
        ChkInputMasalah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isFormMasalah();
            }
        });
        
        ScrollInputMasalah = new widget.ScrollPane();
        ScrollInputMasalah.setViewportView(FormInputMasalah);
        ScrollInputMasalah.setPreferredSize(new Dimension(800, 260));
        
        PanelInputMasalah = new JPanel(new BorderLayout());
        PanelInputMasalah.setOpaque(false);
        PanelInputMasalah.setPreferredSize(new Dimension(WIDTH, 280));
        PanelInputMasalah.add(ScrollInputMasalah, BorderLayout.CENTER);
        PanelInputMasalah.add(ChkInputMasalah, BorderLayout.PAGE_END);
        
        ScrollMasalah.setPreferredSize(new java.awt.Dimension(WIDTH, 150));
        internalFrameMasalah.add(ScrollMasalah, BorderLayout.NORTH);
        internalFrameMasalah.add(PanelInputMasalah, BorderLayout.CENTER);
    }

    private void isFormMasalah() {
        if (ChkInputMasalah.isSelected() == true) {
            ChkInputMasalah.setVisible(false);
            PanelInputMasalah.setPreferredSize(new Dimension(WIDTH, 280));
            FormInputMasalah.setVisible(true);
            ChkInputMasalah.setVisible(true);
        } else {
            ChkInputMasalah.setVisible(false);
            PanelInputMasalah.setPreferredSize(new Dimension(WIDTH, 20));
            FormInputMasalah.setVisible(false);
            ChkInputMasalah.setVisible(true);
        }
    }

    private void initTabIntraOp() {
        internalFrameIntra = new widget.InternalFrame();
        internalFrameIntra.setLayout(new BorderLayout());
        internalFrameIntra.setBackground(new Color(255, 255, 255));
        
        ScrollIntra = new widget.ScrollPane();
        tbIntra = new widget.Table();
        // tabModeIntra initialization
        tabModeIntra = new javax.swing.table.DefaultTableModel(new Object[][]{}, 
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tgl. Asuhan", "Tgl. Operasi", "Jam Masuk", "Posisi"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbIntra.setModel(tabModeIntra);
        ScrollIntra.setViewportView(tbIntra);
        tbIntra.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(tabModeIntra.getRowCount()!=0){
                    try {
                        getDataIntra();
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        // Initialize TextBox fields that are declared but not placed on form
        TLainRestiInfeksiOleh = new TextBox();
        TIntraDarahYa = new TextBox();
        TIntraDarahTidak = new TextBox();
        TPostResp = new TextBox();
        TPuasa = new TextBox();

        FormInputIntra = new widget.PanelBiasa();
        FormInputIntra.setPreferredSize(new Dimension(800, 1100));
        FormInputIntra.setLayout(null);
        
        // --- SECTION 1: Resti Hipotermi ---
        JLabel lblIntra1 = new JLabel("PENGKAJIAN PERAWATAN INTRA OPERASI");
        lblIntra1.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblIntra1.setBounds(10, 10, 300, 23);
        FormInputIntra.add(lblIntra1);
        
        JLabel lblMasalahIntra = new JLabel("MASALAH, INTERVENSI DAN IMPLEMENTASI KEPERAWATAN"); lblMasalahIntra.setFont(new java.awt.Font("Tahoma", 1, 11)); lblMasalahIntra.setBounds(10, 35, 400, 23); FormInputIntra.add(lblMasalahIntra);
        JLabel lblRestiInf = new JLabel("<html><u>Resti infeksi</u></html>"); lblRestiInf.setBounds(10, 60, 100, 23); FormInputIntra.add(lblRestiInf);
        JLabel lblHYDInfeksi = new JLabel("HYD : Infeksi tidak terjadi"); lblHYDInfeksi.setBounds(400, 60, 200, 23); FormInputIntra.add(lblHYDInfeksi);
        
        ChkKulitUtuhPreOp = new CekBox(); ChkKulitUtuhPreOp.setText("kulit sebelum operasi utuh"); ChkKulitUtuhPreOp.setBounds(10, 85, 200, 23); FormInputIntra.add(ChkKulitUtuhPreOp);
        ChkLainRestiInfeksi = new CekBox(); ChkLainRestiInfeksi.setText("Lain-lain"); ChkLainRestiInfeksi.setBounds(400, 85, 100, 23); FormInputIntra.add(ChkLainRestiInfeksi);
        
        ChkAseptik = new CekBox(); ChkAseptik.setText("Aseptik, antiseptik dg"); ChkAseptik.setBounds(10, 110, 150, 23); FormInputIntra.add(ChkAseptik);
        TAseptikDg = new TextBox(); TAseptikDg.setBounds(165, 110, 200, 23); FormInputIntra.add(TAseptikDg);
        JLabel lblOlehAseptik = new JLabel("Oleh"); lblOlehAseptik.setBounds(400, 110, 40, 23); FormInputIntra.add(lblOlehAseptik);
        TAseptikOleh = new TextBox(); TAseptikOleh.setBounds(445, 110, 200, 23); FormInputIntra.add(TAseptikOleh);
        
        JLabel lblKlasifikasiLuka = new JLabel("Klasifikasi luka"); lblKlasifikasiLuka.setBounds(10, 135, 100, 23); FormInputIntra.add(lblKlasifikasiLuka);
        ChkLukaBersih = new CekBox(); ChkLukaBersih.setText("bersih"); ChkLukaBersih.setBounds(120, 135, 70, 23); FormInputIntra.add(ChkLukaBersih);
        ChkLukaBersihTercemar = new CekBox(); ChkLukaBersihTercemar.setText("Bersih tercemar"); ChkLukaBersihTercemar.setBounds(200, 135, 120, 23); FormInputIntra.add(ChkLukaBersihTercemar);
        ChkLukaTercemar = new CekBox(); ChkLukaTercemar.setText("Tercemar"); ChkLukaTercemar.setBounds(335, 135, 90, 23); FormInputIntra.add(ChkLukaTercemar);
        ChkLukaKotor = new CekBox(); ChkLukaKotor.setText("Kotor"); ChkLukaKotor.setBounds(440, 135, 70, 23); FormInputIntra.add(ChkLukaKotor);
        
        ChkPasangKateterUrine = new CekBox(); ChkPasangKateterUrine.setText("Pemasangan kateter urine no"); ChkPasangKateterUrine.setBounds(10, 160, 200, 23); FormInputIntra.add(ChkPasangKateterUrine);
        TKateterNo = new TextBox(); TKateterNo.setBounds(215, 160, 80, 23); FormInputIntra.add(TKateterNo);
        JLabel lblUrineC = new JLabel("Urine"); lblUrineC.setBounds(310, 160, 40, 23); FormInputIntra.add(lblUrineC);
        TKateterUrineCc = new TextBox(); TKateterUrineCc.setBounds(355, 160, 50, 23); FormInputIntra.add(TKateterUrineCc);
        JLabel lblUrineCc = new JLabel("cc"); lblUrineCc.setBounds(410, 160, 30, 23); FormInputIntra.add(lblUrineCc);
        JLabel lblOlehK = new JLabel("Oleh"); lblOlehK.setBounds(450, 160, 40, 23); FormInputIntra.add(lblOlehK);
        TKateterOleh = new TextBox(); TKateterOleh.setBounds(495, 160, 200, 23); FormInputIntra.add(TKateterOleh);
        
        ChkDrain = new CekBox(); ChkDrain.setText("Drain, jenis"); ChkDrain.setBounds(10, 185, 100, 23); FormInputIntra.add(ChkDrain);
        TDrainJenis = new TextBox(); TDrainJenis.setBounds(115, 185, 180, 23); FormInputIntra.add(TDrainJenis);
        JLabel lblCairanD = new JLabel("Cairan drain"); lblCairanD.setBounds(310, 185, 80, 23); FormInputIntra.add(lblCairanD);
        TDrainCairanCc = new TextBox(); TDrainCairanCc.setBounds(395, 185, 60, 23); FormInputIntra.add(TDrainCairanCc);
        JLabel lblCairanDCc = new JLabel("cc"); lblCairanDCc.setBounds(460, 185, 30, 23); FormInputIntra.add(lblCairanDCc);

        ChkTampon = new CekBox(); ChkTampon.setText("tampon, Jenis"); ChkTampon.setBounds(10, 210, 110, 23); FormInputIntra.add(ChkTampon);
        TTamponJenis = new TextBox(); TTamponJenis.setBounds(125, 210, 170, 23); FormInputIntra.add(TTamponJenis);
        JLabel lblLokTampon = new JLabel("Lokasi"); lblLokTampon.setBounds(310, 210, 50, 23); FormInputIntra.add(lblLokTampon);
        TTamponLokasi = new TextBox(); TTamponLokasi.setBounds(365, 210, 330, 23); FormInputIntra.add(TTamponLokasi);

        ChKGips = new CekBox(); ChKGips.setText("Gips, jenis"); ChKGips.setBounds(10, 235, 100, 23); FormInputIntra.add(ChKGips);
        TGipsJenis = new TextBox(); TGipsJenis.setBounds(115, 235, 180, 23); FormInputIntra.add(TGipsJenis);
        JLabel lblLokGips = new JLabel("Lokasi"); lblLokGips.setBounds(310, 235, 50, 23); FormInputIntra.add(lblLokGips);
        TGipsLokasi = new TextBox(); TGipsLokasi.setBounds(365, 235, 330, 23); FormInputIntra.add(TGipsLokasi);

        // --- SECTION 1: Resti Hipotermi ---        
        JLabel lblHipo = new JLabel("Resti Hipotermi :"); lblHipo.setBounds(10, 270, 120, 23); FormInputIntra.add(lblHipo);
        
        ChkIntraHipo1 = new CekBox(); ChkIntraHipo1.setText("Pemasangan kasur penghangat, suhu :"); ChkIntraHipo1.setBounds(20, 295, 230, 23); FormInputIntra.add(ChkIntraHipo1);
        TIntraSuhu = new TextBox(); TIntraSuhu.setBounds(255, 295, 40, 23); FormInputIntra.add(TIntraSuhu);
        JLabel lblDerajat = new JLabel("°C"); lblDerajat.setBounds(300, 295, 20, 23); FormInputIntra.add(lblDerajat);
        JLabel lblOlehH = new JLabel("Oleh :"); lblOlehH.setBounds(330, 295, 40, 23); FormInputIntra.add(lblOlehH);
        TIntraOlehHipo = new TextBox(); TIntraOlehHipo.setBounds(375, 295, 150, 23); FormInputIntra.add(TIntraOlehHipo);
        
        ChkIntraHipo2 = new CekBox(); ChkIntraHipo2.setText("Pemberian cairan IV hangat"); ChkIntraHipo2.setBounds(20, 320, 180, 23); FormInputIntra.add(ChkIntraHipo2);
        ChkIntraHipo3 = new CekBox(); ChkIntraHipo3.setText("Irigasi cairan hangat"); ChkIntraHipo3.setBounds(210, 320, 150, 23); FormInputIntra.add(ChkIntraHipo3);
        ChkIntraHipo4 = new CekBox(); ChkIntraHipo4.setText("Lain-lain"); ChkIntraHipo4.setBounds(370, 320, 100, 23); FormInputIntra.add(ChkIntraHipo4);
        
        // --- SECTION 2: Resti gangguan Integritas Kulit ---
        JLabel lblKulitI = new JLabel("Resti gangguan Integritas Kulit :"); lblKulitI.setBounds(10, 350, 200, 23); FormInputIntra.add(lblKulitI);
        JLabel lblPosisi = new JLabel("Posisi operasi :"); lblPosisi.setBounds(20, 375, 100, 23); FormInputIntra.add(lblPosisi);
        ChkIntraKulitPosisiSupine = new CekBox(); ChkIntraKulitPosisiSupine.setText("Supine"); ChkIntraKulitPosisiSupine.setBounds(120, 375, 70, 23); FormInputIntra.add(ChkIntraKulitPosisiSupine);
        ChkIntraKulitPosisiProne = new CekBox(); ChkIntraKulitPosisiProne.setText("Prone"); ChkIntraKulitPosisiProne.setBounds(195, 375, 70, 23); FormInputIntra.add(ChkIntraKulitPosisiProne);
        ChkIntraKulitPosisiLithotomy = new CekBox(); ChkIntraKulitPosisiLithotomy.setText("Lithotomy"); ChkIntraKulitPosisiLithotomy.setBounds(270, 375, 90, 23); FormInputIntra.add(ChkIntraKulitPosisiLithotomy);
        ChkIntraKulitPosisiLateral = new CekBox(); ChkIntraKulitPosisiLateral.setText("Lateral"); ChkIntraKulitPosisiLateral.setBounds(365, 375, 70, 23); FormInputIntra.add(ChkIntraKulitPosisiLateral);
        ChkIntraKulitPosisiLateralKiri = new CekBox(); ChkIntraKulitPosisiLateralKiri.setText("Lateral Kiri"); ChkIntraKulitPosisiLateralKiri.setBounds(440, 375, 90, 23); FormInputIntra.add(ChkIntraKulitPosisiLateralKiri);
        ChkIntraKulitPosisiLateralKanan = new CekBox(); ChkIntraKulitPosisiLateralKanan.setText("Lateral Kanan"); ChkIntraKulitPosisiLateralKanan.setBounds(535, 375, 100, 23); FormInputIntra.add(ChkIntraKulitPosisiLateralKanan);
        JLabel lblLainPos = new JLabel("Lain-lain :"); lblLainPos.setBounds(640, 375, 70, 23); FormInputIntra.add(lblLainPos);
        TIntraPosisiLain = new TextBox(); TIntraPosisiLain.setBounds(710, 375, 100, 23); FormInputIntra.add(TIntraPosisiLain);

        JLabel lblBantuP = new JLabel("Alat bantu untuk posisi :"); lblBantuP.setBounds(20, 400, 140, 23); FormInputIntra.add(lblBantuP);
        ChkIntraBantal = new CekBox(); ChkIntraBantal.setText("Bantal/guling"); ChkIntraBantal.setBounds(160, 400, 100, 23); FormInputIntra.add(ChkIntraBantal);
        ChkIntraPasir = new CekBox(); ChkIntraPasir.setText("Bantal/Pasir"); ChkIntraPasir.setBounds(265, 400, 100, 23); FormInputIntra.add(ChkIntraPasir);
        ChkIntraPenyangga = new CekBox(); ChkIntraPenyangga.setText("Penyangga Kaki"); ChkIntraPenyangga.setBounds(370, 400, 110, 23); FormInputIntra.add(ChkIntraPenyangga);
        JLabel lblLainBPos = new JLabel("Lain-lain :"); lblLainBPos.setBounds(485, 400, 70, 23); FormInputIntra.add(lblLainBPos);
        TIntraBantuLain = new TextBox(); TIntraBantuLain.setBounds(555, 400, 100, 23); FormInputIntra.add(TIntraBantuLain);
        
        JLabel lblPadding = new JLabel("Pemasangan Padding :"); lblPadding.setBounds(20, 425, 130, 23); FormInputIntra.add(lblPadding);
        ChkIntraPaddingSiku = new CekBox(); ChkIntraPaddingSiku.setText("Siku"); ChkIntraPaddingSiku.setBounds(150, 425, 60, 23); FormInputIntra.add(ChkIntraPaddingSiku);
        ChkIntraPaddingPoplitea = new CekBox(); ChkIntraPaddingPoplitea.setText("Poplitea"); ChkIntraPaddingPoplitea.setBounds(215, 425, 80, 23); FormInputIntra.add(ChkIntraPaddingPoplitea);
        
        // --- SECTION 3: Resti Injuri b.d pemasangan alat ---
        JLabel lblInjuri = new JLabel("Resti Injuri b.d pemasangan alat :"); lblInjuri.setBounds(10, 455, 250, 23); FormInputIntra.add(lblInjuri);
        ChkIntraSabuk = new CekBox(); ChkIntraSabuk.setText("Pemasangan sabuk pengaman pada :"); ChkIntraSabuk.setBounds(20, 475, 230, 23); FormInputIntra.add(ChkIntraSabuk);
        TIntraSabukPada = new TextBox(); TIntraSabukPada.setBounds(255, 475, 200, 23); FormInputIntra.add(TIntraSabukPada);
        ChkIntraPlate = new CekBox(); ChkIntraPlate.setText("Pemasangan plate pada :"); ChkIntraPlate.setBounds(20, 500, 180, 23); FormInputIntra.add(ChkIntraPlate);
        TIntraPlatePada = new TextBox(); TIntraPlatePada.setBounds(205, 500, 150, 23); FormInputIntra.add(TIntraPlatePada);
        JLabel lblPOle = new JLabel("Oleh :"); lblPOle.setBounds(365, 500, 40, 23); FormInputIntra.add(lblPOle);
        TIntraPlateOleh = new TextBox(); TIntraPlateOleh.setBounds(410, 500, 150, 23); FormInputIntra.add(TIntraPlateOleh);
        
        JLabel lblCouter = new JLabel("Elektro Couter type :"); lblCouter.setBounds(20, 525, 130, 23); FormInputIntra.add(lblCouter);
        JLabel lblSetC = new JLabel("Setting : Coagulasi :"); lblSetC.setBounds(155, 525, 110, 23); FormInputIntra.add(lblSetC);
        TIntraCouterSetting = new TextBox(); TIntraCouterSetting.setBounds(270, 525, 50, 23); FormInputIntra.add(TIntraCouterSetting);
        JLabel lblSetCut = new JLabel("Cutting :"); lblSetCut.setBounds(330, 525, 60, 23); FormInputIntra.add(lblSetCut);
        TIntraCuttingSetting = new TextBox(); TIntraCuttingSetting.setBounds(395, 525, 50, 23); FormInputIntra.add(TIntraCuttingSetting);
        
        JLabel lblKulitS = new JLabel("Kondisi Kulit setelah pemasangan patien plate :"); lblKulitS.setBounds(20, 550, 280, 23); FormInputIntra.add(lblKulitS);
        ChkIntraKulitUtuh = new CekBox(); ChkIntraKulitUtuh.setText("Utuh"); ChkIntraKulitUtuh.setBounds(305, 550, 60, 23); FormInputIntra.add(ChkIntraKulitUtuh);
        ChkIntraKulitMerah = new CekBox(); ChkIntraKulitMerah.setText("kemerahan"); ChkIntraKulitMerah.setBounds(370, 550, 90, 23); FormInputIntra.add(ChkIntraKulitMerah);
        ChkIntraKulitLecet = new CekBox(); ChkIntraKulitLecet.setText("Lecet"); ChkIntraKulitLecet.setBounds(465, 550, 70, 23); FormInputIntra.add(ChkIntraKulitLecet);
        ChkIntraKulitLuka = new CekBox(); ChkIntraKulitLuka.setText("Luka"); ChkIntraKulitLuka.setBounds(540, 550, 60, 23); FormInputIntra.add(ChkIntraKulitLuka);
        
        ChkIntraTorniquet = new CekBox(); ChkIntraTorniquet.setText("Pemasangan torniquet pada :"); ChkIntraTorniquet.setBounds(20, 575, 200, 23); FormInputIntra.add(ChkIntraTorniquet);
        TIntraTorniquetPada = new TextBox(); TIntraTorniquetPada.setBounds(225, 575, 150, 23); FormInputIntra.add(TIntraTorniquetPada);
        JLabel lblTOle = new JLabel("Oleh :"); lblTOle.setBounds(385, 575, 40, 23); FormInputIntra.add(lblTOle);
        TIntraTorniquetOleh = new TextBox(); TIntraTorniquetOleh.setBounds(430, 575, 150, 23); FormInputIntra.add(TIntraTorniquetOleh);
        
        JLabel lblInflate = new JLabel("Inflate pk :"); lblInflate.setBounds(20, 600, 80, 23); FormInputIntra.add(lblInflate);
        TIntraInflate = new TextBox(); TIntraInflate.setBounds(105, 600, 60, 23); FormInputIntra.add(TIntraInflate);
        JLabel lblDeflate = new JLabel("Deflate pk :"); lblDeflate.setBounds(175, 600, 80, 23); FormInputIntra.add(lblDeflate);
        TIntraDeflate = new TextBox(); TIntraDeflate.setBounds(260, 600, 60, 23); FormInputIntra.add(TIntraDeflate);
        JLabel lblTekanan = new JLabel("Tekanan :"); lblTekanan.setBounds(330, 600, 70, 23); FormInputIntra.add(lblTekanan);
        TIntraTekanan = new TextBox(); TIntraTekanan.setBounds(405, 600, 60, 23); FormInputIntra.add(TIntraTekanan);
        
        // Penghitungan Kassa, Jarum, Instrumen
        JLabel lblHitung = new JLabel("Penghitungan :"); lblHitung.setBounds(20, 625, 100, 23); FormInputIntra.add(lblHitung);
        JLabel lblHKassa = new JLabel("Kassa"); lblHKassa.setBounds(160, 625, 80, 23); FormInputIntra.add(lblHKassa);
        JLabel lblHJarum = new JLabel("Jarum"); lblHJarum.setBounds(280, 625, 80, 23); FormInputIntra.add(lblHJarum);
        JLabel lblHInstr = new JLabel("Instrumen"); lblHInstr.setBounds(400, 625, 80, 23); FormInputIntra.add(lblHInstr);

        JLabel lblH1 = new JLabel("Pertama sebelum Op"); lblH1.setBounds(20, 650, 130, 23); FormInputIntra.add(lblH1);
        ChkIntraKassa1 = new CekBox(); ChkIntraKassa1.setText("lengkap"); ChkIntraKassa1.setBounds(155, 650, 100, 23); FormInputIntra.add(ChkIntraKassa1);
        ChkIntraJarum1 = new CekBox(); ChkIntraJarum1.setText("lengkap"); ChkIntraJarum1.setBounds(275, 650, 100, 23); FormInputIntra.add(ChkIntraJarum1);
        ChkIntraInstrumen1 = new CekBox(); ChkIntraInstrumen1.setText("lengkap"); ChkIntraInstrumen1.setBounds(395, 650, 100, 23); FormInputIntra.add(ChkIntraInstrumen1);

        JLabel lblH2 = new JLabel("Kedua sebelum tutup"); lblH2.setBounds(20, 675, 130, 23); FormInputIntra.add(lblH2);
        ChkIntraKassa2 = new CekBox(); ChkIntraKassa2.setText("lengkap"); ChkIntraKassa2.setBounds(155, 675, 100, 23); FormInputIntra.add(ChkIntraKassa2);
        ChkIntraJarum2 = new CekBox(); ChkIntraJarum2.setText("lengkap"); ChkIntraJarum2.setBounds(275, 675, 100, 23); FormInputIntra.add(ChkIntraJarum2);
        ChkIntraInstrumen2 = new CekBox(); ChkIntraInstrumen2.setText("lengkap"); ChkIntraInstrumen2.setBounds(395, 675, 100, 23); FormInputIntra.add(ChkIntraInstrumen2);

        JLabel lblH3 = new JLabel("Ketiga selesai Op"); lblH3.setBounds(20, 700, 130, 23); FormInputIntra.add(lblH3);
        ChkIntraKassa3 = new CekBox(); ChkIntraKassa3.setText("lengkap"); ChkIntraKassa3.setBounds(155, 700, 100, 23); FormInputIntra.add(ChkIntraKassa3);
        ChkIntraJarum3 = new CekBox(); ChkIntraJarum3.setText("lengkap"); ChkIntraJarum3.setBounds(275, 700, 100, 23); FormInputIntra.add(ChkIntraJarum3);
        ChkIntraInstrumen3 = new CekBox(); ChkIntraInstrumen3.setText("lengkap"); ChkIntraInstrumen3.setBounds(395, 700, 100, 23); FormInputIntra.add(ChkIntraInstrumen3);

        JLabel lblXRay = new JLabel("Operator diberitahu, jika tidak sesuai, dilakukan X-Ray :"); lblXRay.setBounds(20, 725, 320, 23); FormInputIntra.add(lblXRay);
        ChkIntraHitungXRayYa = new CekBox(); ChkIntraHitungXRayYa.setText("Ya"); ChkIntraHitungXRayYa.setBounds(345, 725, 50, 23); FormInputIntra.add(ChkIntraHitungXRayYa);
        ChkIntraHitungXRayTidak = new CekBox(); ChkIntraHitungXRayTidak.setText("Tidak"); ChkIntraHitungXRayTidak.setBounds(400, 725, 70, 23); FormInputIntra.add(ChkIntraHitungXRayTidak);
        JLabel lblBilaTdk = new JLabel("Bila tidak, jelaskan :"); lblBilaTdk.setBounds(480, 725, 120, 23); FormInputIntra.add(lblBilaTdk);
        TIntraHitungXRayKet = new TextBox(); TIntraHitungXRayKet.setBounds(605, 725, 200, 23); FormInputIntra.add(TIntraHitungXRayKet);

        // --- SECTION 4: Resti gangguan Keseimbangan cairan & elektrolit ---
        JLabel lblElek = new JLabel("Resti gangguan Keseimbangan cairan dan elektrolit b.d :"); lblElek.setBounds(10, 760, 350, 23); FormInputIntra.add(lblElek);
        ChkIntraSpinal = new CekBox(); ChkIntraSpinal.setText("Spinal Anastesi"); ChkIntraSpinal.setBounds(370, 760, 130, 23); FormInputIntra.add(ChkIntraSpinal);
        ChkIntraPengeluaran = new CekBox(); ChkIntraPengeluaran.setText("Pengeluaran cairan / darah berlebih"); ChkIntraPengeluaran.setBounds(510, 760, 250, 23); FormInputIntra.add(ChkIntraPengeluaran);

        JLabel lblCairanM = new JLabel("Cairan Masuk :"); lblCairanM.setBounds(20, 785, 100, 23); FormInputIntra.add(lblCairanM);
        JLabel lblInfus = new JLabel("infus"); lblInfus.setBounds(125, 785, 40, 23); FormInputIntra.add(lblInfus);
        TIntraInfus = new TextBox(); TIntraInfus.setBounds(170, 785, 60, 23); FormInputIntra.add(TIntraInfus);
        JLabel lblCcI = new JLabel("cc"); lblCcI.setBounds(235, 785, 20, 23); FormInputIntra.add(lblCcI);
        JLabel lblJenisI = new JLabel("Jenis"); lblJenisI.setBounds(265, 785, 40, 23); FormInputIntra.add(lblJenisI);
        TIntraInfusJenis = new TextBox(); TIntraInfusJenis.setBounds(310, 785, 150, 23); FormInputIntra.add(TIntraInfusJenis);

        JLabel lblCairanK = new JLabel("Cairan Keluar :"); lblCairanK.setBounds(20, 810, 100, 23); FormInputIntra.add(lblCairanK);
        JLabel lblUrK = new JLabel("Urine"); lblUrK.setBounds(125, 810, 40, 23); FormInputIntra.add(lblUrK);
        TIntraUrine = new TextBox(); TIntraUrine.setBounds(170, 810, 60, 23); FormInputIntra.add(TIntraUrine);
        JLabel lblCcU = new JLabel("cc"); lblCcU.setBounds(235, 810, 20, 23); FormInputIntra.add(lblCcU);
        JLabel lblNGTK = new JLabel("Cairan NGT"); lblNGTK.setBounds(265, 810, 80, 23); FormInputIntra.add(lblNGTK);
        TIntraNGT = new TextBox(); TIntraNGT.setBounds(350, 810, 60, 23); FormInputIntra.add(TIntraNGT);
        JLabel lblCcN = new JLabel("cc"); lblCcN.setBounds(415, 810, 20, 23); FormInputIntra.add(lblCcN);
        JLabel lblLainK = new JLabel("Lain-lain"); lblLainK.setBounds(445, 810, 60, 23); FormInputIntra.add(lblLainK);
        TIntraCairanLain = new TextBox(); TIntraCairanLain.setBounds(510, 810, 60, 23); FormInputIntra.add(TIntraCairanLain);
        JLabel lblCcL = new JLabel("cc"); lblCcL.setBounds(575, 810, 20, 23); FormInputIntra.add(lblCcL);

        JLabel lblDarah = new JLabel("Pemberian darah :"); lblDarah.setBounds(20, 835, 120, 23); FormInputIntra.add(lblDarah);
        ChkIntraPemberianDarahYa = new CekBox(); ChkIntraPemberianDarahYa.setText("Ya"); ChkIntraPemberianDarahYa.setBounds(145, 835, 50, 23); FormInputIntra.add(ChkIntraPemberianDarahYa);
        ChkIntraPemberianDarahTidak = new CekBox(); ChkIntraPemberianDarahTidak.setText("Tidak"); ChkIntraPemberianDarahTidak.setBounds(200, 835, 70, 23); FormInputIntra.add(ChkIntraPemberianDarahTidak);
        
        ChkInputIntra = new widget.CekBox();
        ChkInputIntra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputIntra.setMnemonic('M');
        ChkInputIntra.setText(".: Input Data");
        ChkInputIntra.setToolTipText("Alt+M");
        ChkInputIntra.setBorderPainted(true);
        ChkInputIntra.setBorderPaintedFlat(true);
        ChkInputIntra.setFocusable(false);
        ChkInputIntra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInputIntra.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInputIntra.setName("ChkInputIntra");
        ChkInputIntra.setPreferredSize(new Dimension(192, 20));
        ChkInputIntra.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputIntra.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputIntra.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputIntra.setSelected(true);
        ChkInputIntra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isFormIntra();
            }
        });
        
        ScrollInputIntra = new widget.ScrollPane();
        ScrollInputIntra.setViewportView(FormInputIntra);
        ScrollInputIntra.setPreferredSize(new Dimension(800, 360));
        
        PanelInputIntra = new JPanel(new BorderLayout());
        PanelInputIntra.setOpaque(false);
        PanelInputIntra.setPreferredSize(new Dimension(WIDTH, 380));
        PanelInputIntra.add(ScrollInputIntra, BorderLayout.CENTER);
        PanelInputIntra.add(ChkInputIntra, BorderLayout.PAGE_END);
        
        ScrollIntra.setPreferredSize(new java.awt.Dimension(WIDTH, 150));
        internalFrameIntra.add(ScrollIntra, BorderLayout.NORTH);
        internalFrameIntra.add(PanelInputIntra, BorderLayout.CENTER);
    }

    private void isFormIntra() {
        if (ChkInputIntra.isSelected() == true) {
            ChkInputIntra.setVisible(false);
            PanelInputIntra.setPreferredSize(new Dimension(WIDTH, 380));
            FormInputIntra.setVisible(true);
            ChkInputIntra.setVisible(true);
        } else if (ChkInputIntra.isSelected() == false) {
            ChkInputIntra.setVisible(false);
            PanelInputIntra.setPreferredSize(new Dimension(WIDTH, 20));
            FormInputIntra.setVisible(false);
            ChkInputIntra.setVisible(true);
        }
    }

    private void initTabPostOp() {
        internalFramePost = new widget.InternalFrame();
        internalFramePost.setLayout(new BorderLayout());
        internalFramePost.setBackground(new Color(255, 255, 255));
        
        ScrollPost = new widget.ScrollPane();
        tbPost = new widget.Table();
        // tabModePost initialization
        tabModePost = new javax.swing.table.DefaultTableModel(new Object[][]{}, 
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tgl. Asuhan", "Petugas", "Suhu", "Pernapasan", "Kesadaran", "TD", "HR", "RR", "SpO2"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbPost.setModel(tabModePost);
        ScrollPost.setViewportView(tbPost);
        tbPost.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(tabModePost.getRowCount()!=0){
                    try {
                        getDataPost();
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        FormInputPost = new widget.PanelBiasa();
        FormInputPost.setPreferredSize(new Dimension(800, 420));
        FormInputPost.setLayout(null);
        
        JLabel lblMasukRR = new JLabel("Masuk RR pk :"); lblMasukRR.setBounds(10, 10, 100, 23); FormInputPost.add(lblMasukRR);
        TPostMasukRR = new TextBox(); TPostMasukRR.setBounds(115, 10, 80, 23); FormInputPost.add(TPostMasukRR);
        
        JLabel lblTV = new JLabel("Tanda - tanda vital : TD"); lblTV.setBounds(10, 35, 140, 23); FormInputPost.add(lblTV);
        TPostTD = new TextBox(); TPostTD.setBounds(155, 35, 60, 23); FormInputPost.add(TPostTD);
        JLabel lblMmhg = new JLabel("mmHg"); lblMmhg.setBounds(220, 35, 40, 23); FormInputPost.add(lblMmhg);
        JLabel lblHR = new JLabel("HR"); lblHR.setBounds(270, 35, 30, 23); FormInputPost.add(lblHR);
        TPostHR = new TextBox(); TPostHR.setBounds(305, 35, 60, 23); FormInputPost.add(TPostHR);
        JLabel lblXmenit1 = new JLabel("x/menit"); lblXmenit1.setBounds(370, 35, 50, 23); FormInputPost.add(lblXmenit1);
        JLabel lblSp = new JLabel("SpO2"); lblSp.setBounds(430, 35, 40, 23); FormInputPost.add(lblSp);
        TPostSpO2 = new TextBox(); TPostSpO2.setBounds(475, 35, 50, 23); FormInputPost.add(TPostSpO2);
        JLabel lblPersen = new JLabel("%"); lblPersen.setBounds(530, 35, 20, 23); FormInputPost.add(lblPersen);
        JLabel lblRRp = new JLabel("RR"); lblRRp.setBounds(560, 35, 30, 23); FormInputPost.add(lblRRp);
        TPostRR = new TextBox(); TPostRR.setBounds(595, 35, 60, 23); FormInputPost.add(TPostRR);
        JLabel lblXmenit2 = new JLabel("x/menit"); lblXmenit2.setBounds(660, 35, 50, 23); FormInputPost.add(lblXmenit2);
        
        JLabel lblJenisA = new JLabel("Jenis anestesi"); lblJenisA.setBounds(10, 60, 100, 23); FormInputPost.add(lblJenisA);
        ChkPostUmum = new CekBox(); ChkPostUmum.setText("Umum"); ChkPostUmum.setBounds(115, 60, 70, 23); FormInputPost.add(ChkPostUmum);
        ChkPostSpinalRegional = new CekBox(); ChkPostSpinalRegional.setText("Spinal/Regional"); ChkPostSpinalRegional.setBounds(190, 60, 110, 23); FormInputPost.add(ChkPostSpinalRegional);
        ChkPostBlokPerifer = new CekBox(); ChkPostBlokPerifer.setBounds(310, 60, 100, 23); FormInputPost.add(ChkPostBlokPerifer);
        
        JLabel lblKondKon = new JLabel("Kondisi Kulit"); lblKondKon.setBounds(10, 85, 100, 23); FormInputPost.add(lblKondKon);
        ChkPostDingin = new CekBox(); ChkPostDingin.setText("Dingin"); ChkPostDingin.setBounds(115, 85, 70, 23); FormInputPost.add(ChkPostDingin);
        ChkPostHangat = new CekBox(); ChkPostHangat.setText("Hangat"); ChkPostHangat.setBounds(190, 85, 70, 23); FormInputPost.add(ChkPostHangat);
        ChkPostKering = new CekBox(); ChkPostKering.setText("kering"); ChkPostKering.setBounds(310, 85, 70, 23); FormInputPost.add(ChkPostKering);
        ChkPostUtuh = new CekBox(); ChkPostUtuh.setText("Utuh"); ChkPostUtuh.setBounds(430, 85, 60, 23); FormInputPost.add(ChkPostUtuh);
        ChkPostLembab = new CekBox(); ChkPostLembab.setText("Lembab"); ChkPostLembab.setBounds(510, 85, 70, 23); FormInputPost.add(ChkPostLembab);
        ChkPostPink = new CekBox(); ChkPostPink.setText("Pink"); ChkPostPink.setBounds(610, 85, 60, 23); FormInputPost.add(ChkPostPink);
        
        JLabel lblNapasP = new JLabel("Pernapasan"); lblNapasP.setBounds(10, 110, 100, 23); FormInputPost.add(lblNapasP);
        ChkPostIntubasi = new CekBox(); ChkPostIntubasi.setText("Intubasi"); ChkPostIntubasi.setBounds(115, 110, 80, 23); FormInputPost.add(ChkPostIntubasi);
        ChkPostEkstubasi = new CekBox(); ChkPostEkstubasi.setText("Ekstubasi"); ChkPostEkstubasi.setBounds(310, 110, 80, 23); FormInputPost.add(ChkPostEkstubasi);
        ChkPostSpontan = new CekBox(); ChkPostSpontan.setText("Spontan"); ChkPostSpontan.setBounds(430, 110, 80, 23); FormInputPost.add(ChkPostSpontan);
        ChkPostSpasme = new CekBox(); ChkPostSpasme.setText("Spasme"); ChkPostSpasme.setBounds(530, 110, 80, 23); FormInputPost.add(ChkPostSpasme);
        ChkPostSekret = new CekBox(); ChkPostSekret.setText("Penumpukan sekret"); ChkPostSekret.setBounds(620, 110, 130, 23); FormInputPost.add(ChkPostSekret);
        
        JLabel lblSadarP = new JLabel("Kesadaran"); lblSadarP.setBounds(115, 135, 70, 23); FormInputPost.add(lblSadarP);
        ChkPostBangun = new CekBox(); ChkPostBangun.setText("bangun"); ChkPostBangun.setBounds(190, 135, 70, 23); FormInputPost.add(ChkPostBangun);
        ChkPostSadar = new CekBox(); ChkPostSadar.setText("Sadar"); ChkPostSadar.setBounds(310, 135, 70, 23); FormInputPost.add(ChkPostSadar);
        
        JLabel lblPosP = new JLabel("Posisi pasien :"); lblPosP.setBounds(10, 160, 100, 23); FormInputPost.add(lblPosP);
        ChkPostTerlentang = new CekBox(); ChkPostTerlentang.setText("Terlentang"); ChkPostTerlentang.setBounds(115, 160, 100, 23); FormInputPost.add(ChkPostTerlentang);
        ChkPostLateralKiriKanan = new CekBox(); ChkPostLateralKiriKanan.setText("lateral kiri/kana"); ChkPostLateralKiriKanan.setBounds(230, 160, 130, 23); FormInputPost.add(ChkPostLateralKiriKanan);
        ChkPostEkstremitasBawah = new CekBox(); ChkPostEkstremitasBawah.setText("Ektremitas bawah addukdi/Abduksi/elevasi"); ChkPostEkstremitasBawah.setBounds(390, 160, 250, 23); FormInputPost.add(ChkPostEkstremitasBawah);
        
        JLabel lblLamaA = new JLabel("Lama Anestesi"); lblLamaA.setBounds(10, 185, 100, 23); FormInputPost.add(lblLamaA);
        TAnestesiLama = new TextBox(); TAnestesiLama.setBounds(115, 185, 130, 23); FormInputPost.add(TAnestesiLama);
        JLabel lblLamaP = new JLabel("Lama Pembedahan"); lblLamaP.setBounds(260, 185, 120, 23); FormInputPost.add(lblLamaP);
        TPembedahanLama = new TextBox(); TPembedahanLama.setBounds(390, 185, 130, 23); FormInputPost.add(TPembedahanLama);
        
        JLabel lblDPO = new JLabel("Diagnosa Post Op :"); lblDPO.setBounds(10, 210, 120, 23); FormInputPost.add(lblDPO);
        TPostDiagnosa = new TextBox(); TPostDiagnosa.setBounds(140, 210, 380, 23); FormInputPost.add(TPostDiagnosa);
        
        JLabel lblD = new JLabel("Drain :"); lblD.setBounds(10, 235, 120, 23); FormInputPost.add(lblD);
        TPostDrain = new TextBox(); TPostDrain.setBounds(140, 235, 380, 23); FormInputPost.add(TPostDrain);
        
        JLabel lblTampo = new JLabel("Tampon :"); lblTampo.setBounds(10, 260, 120, 23); FormInputPost.add(lblTampo);
        TPostTampon = new TextBox(); TPostTampon.setBounds(140, 260, 380, 23); FormInputPost.add(TPostTampon);
        
        JLabel lblPA = new JLabel("PA :"); lblPA.setBounds(10, 285, 120, 23); FormInputPost.add(lblPA);
        TPostPA = new TextBox(); TPostPA.setBounds(140, 285, 380, 23); FormInputPost.add(TPostPA);
        
        ScrollInputPost = new widget.ScrollPane();
        ScrollInputPost.setViewportView(FormInputPost);
        ScrollInputPost.setPreferredSize(new Dimension(800, 360));
        
        ChkInputPost = new widget.CekBox();
        ChkInputPost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputPost.setMnemonic('L');
        ChkInputPost.setText(".: Input Data");
        ChkInputPost.setToolTipText("Alt+L");
        ChkInputPost.setBorderPainted(true);
        ChkInputPost.setBorderPaintedFlat(true);
        ChkInputPost.setFocusable(false);
        ChkInputPost.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInputPost.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInputPost.setName("ChkInputPost");
        ChkInputPost.setPreferredSize(new Dimension(192, 20));
        ChkInputPost.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputPost.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputPost.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputPost.setSelected(true);
        ChkInputPost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isFormPost();
            }
        });
        
        PanelInputPost = new JPanel(new BorderLayout());
        PanelInputPost.setOpaque(false);
        PanelInputPost.setPreferredSize(new Dimension(WIDTH, 380));
        PanelInputPost.add(ScrollInputPost, BorderLayout.CENTER);
        PanelInputPost.add(ChkInputPost, BorderLayout.PAGE_END);
        
        ScrollPost.setPreferredSize(new java.awt.Dimension(WIDTH, 150));
        internalFramePost.add(ScrollPost, BorderLayout.NORTH);
        internalFramePost.add(PanelInputPost, BorderLayout.CENTER);
    }

    private void isFormPost() {
        if (ChkInputPost.isSelected() == true) {
            ChkInputPost.setVisible(false);
            PanelInputPost.setPreferredSize(new Dimension(WIDTH, 380));
            FormInputPost.setVisible(true);
            ChkInputPost.setVisible(true);
        } else {
            ChkInputPost.setVisible(false);
            PanelInputPost.setPreferredSize(new Dimension(WIDTH, 20));
            FormInputPost.setVisible(false);
            ChkInputPost.setVisible(true);
        }
    }

    private void initTabDiagnosa() {
        internalFrameDiagnosa = new widget.InternalFrame();
        internalFrameDiagnosa.setLayout(new BorderLayout());
        internalFrameDiagnosa.setBackground(new Color(255, 255, 255));
        
        ScrollDiagnosa = new widget.ScrollPane();
        tbDiagnosa = new widget.Table();
        tabModeDiagnosa = new javax.swing.table.DefaultTableModel(new Object[][]{}, 
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tgl. Asuhan", "Jalan Nafas", "Hipotermi"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbDiagnosa.setModel(tabModeDiagnosa);
        ScrollDiagnosa.setViewportView(tbDiagnosa);
        tbDiagnosa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(tabModeDiagnosa.getRowCount()!=0){
                    try {
                        getDataDiagnosa();
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        FormInputDiagnosa = new widget.PanelBiasa();
        FormInputDiagnosa.setPreferredSize(new Dimension(800, 480));
        FormInputDiagnosa.setLayout(null);
        
        JLabel lblDiagHeader = new JLabel("DIAGNOSA, INTERVENSI DAN IMPLEMENTASI KEPERAWATAN (POST-OP)");
        lblDiagHeader.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDiagHeader.setBounds(10, 10, 450, 23);
        FormInputDiagnosa.add(lblDiagHeader);
        
        // --- Ketidakefektifan jalan nafas ---
        ChkDiagNafas = new CekBox(); ChkDiagNafas.setText("Ketidakefektifan jalan nafas b.d"); ChkDiagNafas.setFont(new java.awt.Font("Tahoma", 1, 11)); ChkDiagNafas.setBounds(10, 35, 220, 23); FormInputDiagnosa.add(ChkDiagNafas);
        ChkNafasSekret = new CekBox(); ChkNafasSekret.setText("penumpukan sekret"); ChkNafasSekret.setBounds(230, 35, 140, 23); FormInputDiagnosa.add(ChkNafasSekret);
        ChkNafasAnastesi = new CekBox(); ChkNafasAnastesi.setText("efek anastesi"); ChkNafasAnastesi.setBounds(380, 35, 120, 23); FormInputDiagnosa.add(ChkNafasAnastesi);
        
        JLabel lblSuc = new JLabel("Suction"); lblSuc.setBounds(10, 60, 80, 23); FormInputDiagnosa.add(lblSuc);
        ChkSuctionYa = new CekBox(); ChkSuctionYa.setText("Ya"); ChkSuctionYa.setBounds(100, 60, 50, 23); FormInputDiagnosa.add(ChkSuctionYa);
        ChkSuctionTidak = new CekBox(); ChkSuctionTidak.setText("Tidak"); ChkSuctionTidak.setBounds(160, 60, 70, 23); FormInputDiagnosa.add(ChkSuctionTidak);
        
        JLabel lblNapDiag = new JLabel("Pernapasan :"); lblNapDiag.setBounds(10, 85, 80, 23); FormInputDiagnosa.add(lblNapDiag);
        JLabel lblSpon = new JLabel("Spontan"); lblSpon.setBounds(100, 85, 70, 23); FormInputDiagnosa.add(lblSpon);
        ChkNapasSpontanO2Nasal = new CekBox(); ChkNapasSpontanO2Nasal.setText("O2 nasal :"); ChkNapasSpontanO2Nasal.setBounds(180, 85, 90, 23); FormInputDiagnosa.add(ChkNapasSpontanO2Nasal);
        TSpontanO2Nasal = new TextBox(); TSpontanO2Nasal.setBounds(275, 85, 50, 23); FormInputDiagnosa.add(TSpontanO2Nasal);
        JLabel lblLm1 = new JLabel("l/mnt"); lblLm1.setBounds(335, 85, 40, 23); FormInputDiagnosa.add(lblLm1);
        
        ChkNapasSpontanO2Oral = new CekBox(); ChkNapasSpontanO2Oral.setText("O2 oral"); ChkNapasSpontanO2Oral.setBounds(390, 85, 80, 23); FormInputDiagnosa.add(ChkNapasSpontanO2Oral);
        TSpontanO2Oral = new TextBox(); TSpontanO2Oral.setBounds(475, 85, 50, 23); FormInputDiagnosa.add(TSpontanO2Oral);
        JLabel lblLm2 = new JLabel("l/mnt"); lblLm2.setBounds(535, 85, 40, 23); FormInputDiagnosa.add(lblLm2);
        
        JLabel lblDib = new JLabel("Dibantu"); lblDib.setBounds(100, 110, 70, 23); FormInputDiagnosa.add(lblDib);
        ChkNapasDibantuBagging = new CekBox(); ChkNapasDibantuBagging.setText("Bagging"); ChkNapasDibantuBagging.setBounds(180, 110, 120, 23); FormInputDiagnosa.add(ChkNapasDibantuBagging);
        ChkNapasDibantuTriple = new CekBox(); ChkNapasDibantuTriple.setText("Triple manuver"); ChkNapasDibantuTriple.setBounds(330, 110, 120, 23); FormInputDiagnosa.add(ChkNapasDibantuTriple);
        ChkNapasDibantuO2Mask = new CekBox(); ChkNapasDibantuO2Mask.setText("O2 face mask"); ChkNapasDibantuO2Mask.setBounds(470, 110, 120, 23); FormInputDiagnosa.add(ChkNapasDibantuO2Mask);
        
        JLabel lblKolDiag = new JLabel("Kolaborasi :"); lblKolDiag.setBounds(10, 135, 80, 23); FormInputDiagnosa.add(lblKolDiag);
        JLabel lblPemObat = new JLabel("Pemberian obat :"); lblPemObat.setBounds(100, 135, 120, 23); FormInputDiagnosa.add(lblPemObat);
        TKolabPemberianObat = new TextBox(); TKolabPemberianObat.setBounds(225, 135, 300, 23); FormInputDiagnosa.add(TKolabPemberianObat);
        
        // --- Resti Hipotermi ---
        ChkDiagHipotermi = new CekBox(); ChkDiagHipotermi.setText("Resti Hipotermi b.d"); ChkDiagHipotermi.setFont(new java.awt.Font("Tahoma", 1, 11)); ChkDiagHipotermi.setBounds(10, 165, 150, 23); FormInputDiagnosa.add(ChkDiagHipotermi);
        ChkHipoLingkungan = new CekBox(); ChkHipoLingkungan.setText("Lingkungan OK"); ChkHipoLingkungan.setBounds(210, 165, 120, 23); FormInputDiagnosa.add(ChkHipoLingkungan);
        ChkHipoAnastesi = new CekBox(); ChkHipoAnastesi.setText("Efek Anastesi"); ChkHipoAnastesi.setBounds(350, 165, 120, 23); FormInputDiagnosa.add(ChkHipoAnastesi);
        
        ChkHipoSelimut = new CekBox(); ChkHipoSelimut.setText("Beri selimut hangat"); ChkHipoSelimut.setBounds(10, 190, 150, 23); FormInputDiagnosa.add(ChkHipoSelimut);
        ChkHipoElevasi = new CekBox(); ChkHipoElevasi.setText("Elevasi tungkai"); ChkHipoElevasi.setBounds(10, 215, 150, 23); FormInputDiagnosa.add(ChkHipoElevasi);
        ChkHipoKolab = new CekBox(); ChkHipoKolab.setText("Kolaborasi, pemberian obat"); ChkHipoKolab.setBounds(10, 240, 180, 23); FormInputDiagnosa.add(ChkHipoKolab);
        THipoKolab = new TextBox(); THipoKolab.setBounds(195, 240, 300, 23); FormInputDiagnosa.add(THipoKolab);
        ChkHipoLain = new CekBox(); ChkHipoLain.setText("Lain - lain"); ChkHipoLain.setBounds(10, 265, 90, 23); FormInputDiagnosa.add(ChkHipoLain);
        THipoLain = new TextBox(); THipoLain.setBounds(100, 265, 395, 23); FormInputDiagnosa.add(THipoLain);
        
        // Serah Terima Label
        JLabel lblSerahTerima = new JLabel("Serah Terima"); lblSerahTerima.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSerahTerima.setBounds(330, 290, 150, 23); FormInputDiagnosa.add(lblSerahTerima);
        
        // Perawat Ruang Pulih
        JLabel lblPerawatPulih = new JLabel("Perawat Ruang Pulih"); lblPerawatPulih.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); lblPerawatPulih.setBounds(80, 310, 130, 23); FormInputDiagnosa.add(lblPerawatPulih);
        KdPerawatRuangPulih = new TextBox(); KdPerawatRuangPulih.setBounds(10, 335, 70, 23); FormInputDiagnosa.add(KdPerawatRuangPulih);
        NmPerawatRuangPulih = new TextBox(); NmPerawatRuangPulih.setBounds(82, 335, 170, 23); FormInputDiagnosa.add(NmPerawatRuangPulih);
        BtnPerawatRuangPulih = new widget.Button(); BtnPerawatRuangPulih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPerawatRuangPulih.setMnemonic('3'); BtnPerawatRuangPulih.setToolTipText("Alt+3"); BtnPerawatRuangPulih.setBounds(254, 335, 28, 23); FormInputDiagnosa.add(BtnPerawatRuangPulih);
        BtnPerawatRuangPulih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perawatRuangPulihDlg.emptTeks();
                perawatRuangPulihDlg.isCek();
                perawatRuangPulihDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                perawatRuangPulihDlg.setLocationRelativeTo(internalFrame1);
                perawatRuangPulihDlg.setVisible(true);
            }
        });
        perawatRuangPulihDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(perawatRuangPulihDlg.getTable().getSelectedRow() != -1){
                    KdPerawatRuangPulih.setText(perawatRuangPulihDlg.getTable().getValueAt(perawatRuangPulihDlg.getTable().getSelectedRow(),0).toString());
                    NmPerawatRuangPulih.setText(perawatRuangPulihDlg.getTable().getValueAt(perawatRuangPulihDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        // Perawat Ruangan
        JLabel lblPerawatRuang = new JLabel("Perawat Ruangan"); lblPerawatRuang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); lblPerawatRuang.setBounds(460, 310, 130, 23); FormInputDiagnosa.add(lblPerawatRuang);
        KdPerawatRuangan = new TextBox(); KdPerawatRuangan.setBounds(380, 335, 70, 23); FormInputDiagnosa.add(KdPerawatRuangan);
        NmPerawatRuangan = new TextBox(); NmPerawatRuangan.setBounds(452, 335, 170, 23); FormInputDiagnosa.add(NmPerawatRuangan);
        BtnPerawatRuangan = new widget.Button(); BtnPerawatRuangan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPerawatRuangan.setMnemonic('4'); BtnPerawatRuangan.setToolTipText("Alt+4"); BtnPerawatRuangan.setBounds(624, 335, 28, 23); FormInputDiagnosa.add(BtnPerawatRuangan);
        BtnPerawatRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perawatRuanganDlg.emptTeks();
                perawatRuanganDlg.isCek();
                perawatRuanganDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                perawatRuanganDlg.setLocationRelativeTo(internalFrame1);
                perawatRuanganDlg.setVisible(true);
            }
        });
        perawatRuanganDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if(perawatRuanganDlg.getTable().getSelectedRow() != -1){
                    KdPerawatRuangan.setText(perawatRuanganDlg.getTable().getValueAt(perawatRuanganDlg.getTable().getSelectedRow(),0).toString());
                    NmPerawatRuangan.setText(perawatRuanganDlg.getTable().getValueAt(perawatRuanganDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });
        
        ScrollInputDiagnosa = new widget.ScrollPane();
        ScrollInputDiagnosa.setViewportView(FormInputDiagnosa);
        ScrollInputDiagnosa.setPreferredSize(new Dimension(800, 440));
        
        ChkInputDiagnosa = new widget.CekBox();
        ChkInputDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputDiagnosa.setMnemonic('6');
        ChkInputDiagnosa.setText(".: Input Data");
        ChkInputDiagnosa.setToolTipText("Alt+6");
        ChkInputDiagnosa.setBorderPainted(true);
        ChkInputDiagnosa.setBorderPaintedFlat(true);
        ChkInputDiagnosa.setFocusable(false);
        ChkInputDiagnosa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInputDiagnosa.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInputDiagnosa.setName("ChkInputDiagnosa");
        ChkInputDiagnosa.setPreferredSize(new Dimension(192, 20));
        ChkInputDiagnosa.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputDiagnosa.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputDiagnosa.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputDiagnosa.setSelected(true);
        ChkInputDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isFormDiagnosa();
            }
        });
        
        PanelInputDiagnosa = new JPanel(new BorderLayout());
        PanelInputDiagnosa.setOpaque(false);
        PanelInputDiagnosa.setPreferredSize(new Dimension(WIDTH, 460));
        PanelInputDiagnosa.add(ScrollInputDiagnosa, BorderLayout.CENTER);
        PanelInputDiagnosa.add(ChkInputDiagnosa, BorderLayout.PAGE_END);
        
        ScrollDiagnosa.setPreferredSize(new java.awt.Dimension(WIDTH, 150));
        internalFrameDiagnosa.add(ScrollDiagnosa, BorderLayout.NORTH);
        internalFrameDiagnosa.add(PanelInputDiagnosa, BorderLayout.CENTER);
    }
    
    private void isFormDiagnosa() {
        if (ChkInputDiagnosa.isSelected() == true) {
            ChkInputDiagnosa.setVisible(false);
            PanelInputDiagnosa.setPreferredSize(new Dimension(WIDTH, 460));
            FormInputDiagnosa.setVisible(true);
            ChkInputDiagnosa.setVisible(true);
        } else {
            ChkInputDiagnosa.setVisible(false);
            PanelInputDiagnosa.setPreferredSize(new Dimension(WIDTH, 20));
            FormInputDiagnosa.setVisible(false);
            ChkInputDiagnosa.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getoperasi());
        BtnHapus.setEnabled(akses.getoperasi());
        BtnEdit.setEnabled(akses.getoperasi());
        BtnPrint.setEnabled(akses.getoperasi());
    }
}
