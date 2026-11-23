package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMRekamMedikGigi extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar;
    private TextBox TNoRw, TNoRM, TPasien;
    private JTabbedPane tabs;

    // Table per-tab
    private widget.Table tbMedik;
    private DefaultTableModel tabMedikMode;

    // Panel Atas - Viewer dari tabel pasien (readonly)
    private TextBox TSukuRas, TPekerjaanKantor, TTelpSeluler, TGoldar;

    // Tab A - Data Pasien & Medik
    private TextBox TTdSistolik, TTdDiastolik;
    private ComboBox CmbStatusTd;
    private ComboBox CmbJantung, CmbDiabetes, CmbHaemopilia, CmbHepatitis, CmbGastritis;
    private ComboBox CmbPenyakitLain, CmbAlergiObat, CmbAlergiMakanan;
    private TextArea TKetPenyakitLain, TKetAlergiObat, TKetAlergiMakanan;
    private widget.Tanggal TglPencatatan;
    private TextBox NIP, NamaPetugas;
    private Button BtnPetugas;

    // Tab B - Odontogram
    private ComboBox CmbOcclusi, CmbTorusPalatinus, CmbTorusMandibularis, CmbPalatum;
    private ComboBox CmbDiastema, CmbGigiAnomali;
    private TextArea TKetDiastema, TKetGigiAnomali, TLainLain;
    private TextBox TDMF_D, TDMF_M, TDMF_F, TJmlPhoto, TJmlRontgen;
    private widget.Tanggal TglPeriksa;
    private TextBox NIPPemeriksa, NamaPemeriksa;
    private Button BtnPemeriksa;
    private widget.Table tbGigi;
    private DefaultTableModel tabGigiMode;

    // Tab Visual Odontogram
    private PanelOdontogramFDI panelOdonto;

    // Tab C - Tabel Perawatan
    private widget.Table tbPerawatan;
    private DefaultTableModel tabPerawatanMode;
    private widget.Tanggal TglPerawatan;
    private TextBox TKodeGigi, TKodeICD;
    private TextArea TKeluhanDiagnosa, TPerawatan, TKetPerawatan;
    private TextBox NIPDokter, NamaDokter;
    private Button BtnDokter;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas pemeriksDlg = new DlgCariPetugas(null, false);
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private DlgCariDokter dokterPencatatDlg = new DlgCariDokter(null, false);

    public RMRekamMedikGigi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        buatTabelJikaBelumAda();
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
    }

    private void buatTabelJikaBelumAda() {
        try {
            java.sql.Statement st = koneksi.createStatement();
            // rekam_medik_gigi: 33 kolom tanpa id (menyimpantf INSERT VALUES)
            // suku_ras, pekerjaan_kantor, alamat_kantor, telp_seluler, goldar diambil dari tabel pasien
            st.execute(
                "CREATE TABLE IF NOT EXISTS `rekam_medik_gigi` (" +
                " `no_rawat` VARCHAR(17) NOT NULL UNIQUE," +
                " `td_sistolik` VARCHAR(10)," +
                " `td_diastolik` VARCHAR(10)," +
                " `status_td` VARCHAR(20) DEFAULT 'Normal'," +
                " `penyakit_jantung` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `diabetes` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `haemopilia` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `hepatitis` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `gastritis` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `penyakit_lainnya` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_penyakit_lainnya` TEXT," +
                " `alergi_obat` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_alergi_obat` TEXT," +
                " `alergi_makanan` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_alergi_makanan` TEXT," +
                " `tgl_pencatatan` DATE," +
                " `nip` VARCHAR(20)," +
                " `occlusi` VARCHAR(20) DEFAULT 'Normal Bite'," +
                " `torus_palatinus` VARCHAR(20) DEFAULT 'Tidak Ada'," +
                " `torus_mandibularis` VARCHAR(20) DEFAULT 'Tidak Ada'," +
                " `palatum` VARCHAR(20) DEFAULT 'Sedang'," +
                " `diastema` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_diastema` TEXT," +
                " `gigi_anomali` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_gigi_anomali` TEXT," +
                " `lain_lain` TEXT," +
                " `dmf_d` TINYINT DEFAULT 0," +
                " `dmf_m` TINYINT DEFAULT 0," +
                " `dmf_f` TINYINT DEFAULT 0," +
                " `jml_photo` TINYINT DEFAULT 0," +
                " `jml_rontgen` TINYINT DEFAULT 0," +
                " `tgl_periksa` DATE," +
                " `nip_pemeriksa` VARCHAR(20)," +
                " KEY `idx_no_rawat` (`no_rawat`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            // perawatan_gigi: 8 kolom tanpa id
            st.execute(
                "CREATE TABLE IF NOT EXISTS `perawatan_gigi` (" +
                " `no_rawat` VARCHAR(17) NOT NULL," +
                " `tanggal` DATE," +
                " `kode_gigi` VARCHAR(10)," +
                " `keluhan_diagnosa` TEXT," +
                " `kode_icd` VARCHAR(20)," +
                " `perawatan` TEXT," +
                " `nip_dokter` VARCHAR(20)," +
                " `keterangan` TEXT," +
                " KEY `idx_no_rawat` (`no_rawat`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            // odontogram_gigi: 4 kolom tanpa id
            st.execute(
                "CREATE TABLE IF NOT EXISTS `odontogram_gigi` (" +
                " `no_rawat` VARCHAR(17) NOT NULL," +
                " `nomor_gigi` SMALLINT NOT NULL," +
                " `kondisi` VARCHAR(10) NOT NULL DEFAULT 'N'," +
                " `keterangan` TEXT," +
                " KEY `idx_no_rawat` (`no_rawat`)," +
                " KEY `idx_nomor_gigi` (`nomor_gigi`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            // Hapus kolom id jika ada (tabel lama mungkin punya AUTO_INCREMENT id)
            try { st.execute("ALTER TABLE rekam_medik_gigi DROP COLUMN id"); } catch (Exception ex) {}
            try { st.execute("ALTER TABLE perawatan_gigi DROP COLUMN id"); } catch (Exception ex) {}
            try { st.execute("ALTER TABLE odontogram_gigi DROP COLUMN id"); } catch (Exception ex) {}
            // Hapus kolom yang dipindah ke panel atas (viewer dari pasien)
            try { st.execute("ALTER TABLE rekam_medik_gigi DROP COLUMN suku_ras"); } catch (Exception ex) {}
            try { st.execute("ALTER TABLE rekam_medik_gigi DROP COLUMN pekerjaan_kantor"); } catch (Exception ex) {}
            try { st.execute("ALTER TABLE rekam_medik_gigi DROP COLUMN alamat_kantor"); } catch (Exception ex) {}
            try { st.execute("ALTER TABLE rekam_medik_gigi DROP COLUMN telp_seluler"); } catch (Exception ex) {}
            try { st.execute("ALTER TABLE rekam_medik_gigi DROP COLUMN goldar"); } catch (Exception ex) {}
        } catch (Exception e) {
            System.out.println("Notif buatTabel gigi: " + e);
        }
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Rekam Medik Kedokteran Gigi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new Color(50,50,50)));
        internalFrame1.setLayout(new BorderLayout(1,1));

        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT,5,4));
        panelAtas.setPreferredSize(new Dimension(1024,58));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130,23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100,23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200,23));
        // Viewer fields - readonly, data dari tabel pasien
        TSukuRas = new TextBox(); TSukuRas.setPreferredSize(new Dimension(130,23)); TSukuRas.setEditable(false);
        TTelpSeluler = new TextBox(); TTelpSeluler.setPreferredSize(new Dimension(120,23)); TTelpSeluler.setEditable(false);
        TPekerjaanKantor = new TextBox(); TPekerjaanKantor.setPreferredSize(new Dimension(170,23)); TPekerjaanKantor.setEditable(false);
        TGoldar = new TextBox(); TGoldar.setPreferredSize(new Dimension(50,23)); TGoldar.setEditable(false);
        Label lblSuku = new Label(); lblSuku.setText("Suku/Ras :"); lblSuku.setPreferredSize(new Dimension(65,23));
        Label lblTelp = new Label(); lblTelp.setText("Telp :"); lblTelp.setPreferredSize(new Dimension(35,23));
        Label lblPek = new Label(); lblPek.setText("Pekerjaan :"); lblPek.setPreferredSize(new Dimension(65,23));
        Label lblGD = new Label(); lblGD.setText("Gol.Darah :"); lblGD.setPreferredSize(new Dimension(65,23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);
        panelAtas.add(lblSuku); panelAtas.add(TSukuRas);
        panelAtas.add(lblTelp); panelAtas.add(TTelpSeluler);
        panelAtas.add(lblPek); panelAtas.add(TPekerjaanKantor);
        panelAtas.add(lblGD); panelAtas.add(TGoldar);

        tabs = new JTabbedPane();

        // ===== TAB A - DATA PASIEN & MEDIK =====
        PanelBiasa tabA = new PanelBiasa();
        tabA.setLayout(null);
        tabA.setPreferredSize(new Dimension(860,650));

        int y = 10;
        JLabel hA = new JLabel("RM.PGI.01.002.A.2022  -  REKAM MEDIK KEDOKTERAN GIGI");
        hA.setFont(new java.awt.Font("Tahoma",1,11)); hA.setBounds(10,y,500,20); tabA.add(hA);

        y += 22;
        JLabel hDM = new JLabel("DATA MEDIK YANG PERLU DIPERHATIKAN");
        hDM.setFont(new java.awt.Font("Tahoma",1,10)); hDM.setForeground(new Color(0,0,160));
        hDM.setBounds(10,y,300,18); tabA.add(hDM);

        y += 26;
        Label ltd = new Label(); ltd.setText("2. Tekanan Darah :"); ltd.setBounds(10,y,125,23); tabA.add(ltd);
        TTdSistolik = new TextBox(); TTdSistolik.setBounds(140,y,50,23); tabA.add(TTdSistolik);
        Label lsl = new Label(); lsl.setText("/"); lsl.setBounds(193,y,15,23); tabA.add(lsl);
        TTdDiastolik = new TextBox(); TTdDiastolik.setBounds(210,y,50,23); tabA.add(TTdDiastolik);
        CmbStatusTd = new ComboBox(); CmbStatusTd.setModel(new DefaultComboBoxModel(new String[]{"Normal","Hypertensi","Hypotensi"}));
        CmbStatusTd.setBounds(270,y,110,23); tabA.add(CmbStatusTd);

        String[] tiadaAda = {"Tidak Ada","Ada"};

        y += 26;
        Label ljt = new Label(); ljt.setText("3. Penyakit Jantung :"); ljt.setBounds(10,y,135,23); tabA.add(ljt);
        CmbJantung = new ComboBox(); CmbJantung.setModel(new DefaultComboBoxModel(tiadaAda)); CmbJantung.setBounds(150,y,90,23); tabA.add(CmbJantung);
        Label ldb = new Label(); ldb.setText("4. Diabetes :"); ldb.setBounds(255,y,85,23); tabA.add(ldb);
        CmbDiabetes = new ComboBox(); CmbDiabetes.setModel(new DefaultComboBoxModel(tiadaAda)); CmbDiabetes.setBounds(345,y,90,23); tabA.add(CmbDiabetes);

        y += 26;
        Label lhm = new Label(); lhm.setText("5. Haemopilia :"); lhm.setBounds(10,y,105,23); tabA.add(lhm);
        CmbHaemopilia = new ComboBox(); CmbHaemopilia.setModel(new DefaultComboBoxModel(tiadaAda)); CmbHaemopilia.setBounds(120,y,90,23); tabA.add(CmbHaemopilia);
        Label lhp = new Label(); lhp.setText("6. Hepatitis :"); lhp.setBounds(225,y,90,23); tabA.add(lhp);
        CmbHepatitis = new ComboBox(); CmbHepatitis.setModel(new DefaultComboBoxModel(tiadaAda)); CmbHepatitis.setBounds(320,y,90,23); tabA.add(CmbHepatitis);

        y += 26;
        Label lgs = new Label(); lgs.setText("7. Gastritis :"); lgs.setBounds(10,y,90,23); tabA.add(lgs);
        CmbGastritis = new ComboBox(); CmbGastritis.setModel(new DefaultComboBoxModel(tiadaAda)); CmbGastritis.setBounds(105,y,90,23); tabA.add(CmbGastritis);

        y += 26;
        Label lpl = new Label(); lpl.setText("8. Penyakit Lainnya :"); lpl.setBounds(10,y,135,23); tabA.add(lpl);
        CmbPenyakitLain = new ComboBox(); CmbPenyakitLain.setModel(new DefaultComboBoxModel(tiadaAda)); CmbPenyakitLain.setBounds(150,y,90,23); tabA.add(CmbPenyakitLain);

        y += 26;
        Label lketpl = new Label(); lketpl.setText("  Keterangan :"); lketpl.setBounds(10,y,100,23); tabA.add(lketpl);
        TKetPenyakitLain = new TextArea(); TKetPenyakitLain.setBounds(115,y,430,40);
        TKetPenyakitLain.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabA.add(TKetPenyakitLain);

        y += 45;
        Label lao = new Label(); lao.setText("9. Alergi Obat :"); lao.setBounds(10,y,110,23); tabA.add(lao);
        CmbAlergiObat = new ComboBox(); CmbAlergiObat.setModel(new DefaultComboBoxModel(tiadaAda)); CmbAlergiObat.setBounds(125,y,90,23); tabA.add(CmbAlergiObat);

        y += 26;
        Label lketao = new Label(); lketao.setText("  Keterangan :"); lketao.setBounds(10,y,100,23); tabA.add(lketao);
        TKetAlergiObat = new TextArea(); TKetAlergiObat.setBounds(115,y,430,40);
        TKetAlergiObat.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabA.add(TKetAlergiObat);

        y += 45;
        Label lam = new Label(); lam.setText("10. Alergi Makanan :"); lam.setBounds(10,y,135,23); tabA.add(lam);
        CmbAlergiMakanan = new ComboBox(); CmbAlergiMakanan.setModel(new DefaultComboBoxModel(tiadaAda)); CmbAlergiMakanan.setBounds(150,y,90,23); tabA.add(CmbAlergiMakanan);

        y += 26;
        Label lketam = new Label(); lketam.setText("  Keterangan :"); lketam.setBounds(10,y,100,23); tabA.add(lketam);
        TKetAlergiMakanan = new TextArea(); TKetAlergiMakanan.setBounds(115,y,430,40);
        TKetAlergiMakanan.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabA.add(TKetAlergiMakanan);

        y += 50;
        Label ltpc = new Label(); ltpc.setText("Tgl. Pencatatan :"); ltpc.setBounds(10,y,115,23); tabA.add(ltpc);
        TglPencatatan = new widget.Tanggal(); TglPencatatan.setDisplayFormat("dd-MM-yyyy"); TglPencatatan.setBounds(130,y,120,23); tabA.add(TglPencatatan);

        y += 28;
        Label lnip = new Label(); lnip.setText("Dokter :"); lnip.setBounds(10,y,55,23); tabA.add(lnip);
        NIP = new TextBox(); NIP.setBounds(70,y,80,23); tabA.add(NIP);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(155,y,220,23); tabA.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(380,y,28,23); tabA.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{dokterPencatatDlg.emptTeks();dokterPencatatDlg.isCek();dokterPencatatDlg.setSize(800,500);dokterPencatatDlg.setLocationRelativeTo(null);dokterPencatatDlg.setVisible(true);});

        y += 38;
        JLabel hTblMedik = new JLabel("Ringkasan Data Medik :");
        hTblMedik.setFont(new java.awt.Font("Tahoma",1,10)); hTblMedik.setForeground(new Color(0,0,160));
        hTblMedik.setBounds(10,y,200,18); tabA.add(hTblMedik);
        y += 20;
        tabMedikMode = new DefaultTableModel(new Object[][]{},
            new String[]{"TD Sis","TD Dia","Status TD","Jantung","DM","Hmf","Hpt","Gst","P.Lain","A.Obat","A.Makanan","Tgl Catat","Dokter"}
        ){ @Override public boolean isCellEditable(int r,int c){return false;} };
        tbMedik = new widget.Table(); tbMedik.setModel(tabMedikMode);
        ScrollPane scrollMedik = new ScrollPane(); scrollMedik.setViewportView(tbMedik);
        scrollMedik.setBounds(10,y,840,120); tabA.add(scrollMedik);
        dokterPencatatDlg.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosed(java.awt.event.WindowEvent e){
                if(dokterPencatatDlg.getTable().getSelectedRow()!=-1){
                    NIP.setText(dokterPencatatDlg.getTable().getValueAt(dokterPencatatDlg.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(dokterPencatatDlg.getTable().getValueAt(dokterPencatatDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        ScrollPane scrollA = new ScrollPane(); scrollA.setViewportView(tabA);
        tabs.addTab("A. Data Pasien & Medik", scrollA);

        // ===== TAB B - ODONTOGRAM =====
        PanelBiasa tabB = new PanelBiasa();
        tabB.setLayout(null);
        tabB.setPreferredSize(new Dimension(860,600));

        y = 10;
        JLabel hB = new JLabel("RM.PGI.01.002.B.2022  -  FORMULIR PEMERIKSAAN ODONTOGRAM");
        hB.setFont(new java.awt.Font("Tahoma",1,11)); hB.setBounds(10,y,550,20); tabB.add(hB);

        y += 30;
        JLabel hOcl = new JLabel("Kondisi Umum Rongga Mulut :");
        hOcl.setFont(new java.awt.Font("Tahoma",1,10)); hOcl.setForeground(new Color(0,0,160));
        hOcl.setBounds(10,y,230,18); tabB.add(hOcl);

        y += 22;
        Label locl = new Label(); locl.setText("Occlusi :"); locl.setBounds(10,y,65,23); tabB.add(locl);
        CmbOcclusi = new ComboBox(); CmbOcclusi.setModel(new DefaultComboBoxModel(new String[]{"Normal Bite","Cross Bite","Steep Bite"}));
        CmbOcclusi.setBounds(80,y,120,23); tabB.add(CmbOcclusi);

        y += 26;
        Label ltp = new Label(); ltp.setText("Torus Palatinus :"); ltp.setBounds(10,y,115,23); tabB.add(ltp);
        CmbTorusPalatinus = new ComboBox(); CmbTorusPalatinus.setModel(new DefaultComboBoxModel(new String[]{"Tidak Ada","Kecil","Sedang","Besar","Multiple"}));
        CmbTorusPalatinus.setBounds(130,y,110,23); tabB.add(CmbTorusPalatinus);

        y += 26;
        Label ltm = new Label(); ltm.setText("Torus Mandibularis :"); ltm.setBounds(10,y,135,23); tabB.add(ltm);
        CmbTorusMandibularis = new ComboBox(); CmbTorusMandibularis.setModel(new DefaultComboBoxModel(new String[]{"Tidak Ada","Sisi Kiri","Sisi Kanan","Kedua Sisi"}));
        CmbTorusMandibularis.setBounds(150,y,110,23); tabB.add(CmbTorusMandibularis);
        Label lpal = new Label(); lpal.setText("Palatum :"); lpal.setBounds(275,y,65,23); tabB.add(lpal);
        CmbPalatum = new ComboBox(); CmbPalatum.setModel(new DefaultComboBoxModel(new String[]{"Dalam","Sedang","Rendah"}));
        CmbPalatum.setBounds(345,y,100,23); tabB.add(CmbPalatum);

        y += 26;
        Label ldst = new Label(); ldst.setText("Diastema :"); ldst.setBounds(10,y,75,23); tabB.add(ldst);
        CmbDiastema = new ComboBox(); CmbDiastema.setModel(new DefaultComboBoxModel(new String[]{"Tidak Ada","Ada"}));
        CmbDiastema.setBounds(90,y,80,23); tabB.add(CmbDiastema);
        Label lketdst = new Label(); lketdst.setText("(Keterangan) :"); lketdst.setBounds(180,y,100,23); tabB.add(lketdst);
        TKetDiastema = new TextArea(); TKetDiastema.setBounds(285,y,340,46);
        TKetDiastema.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabB.add(TKetDiastema);

        y += 52;
        Label lga = new Label(); lga.setText("Gigi Anomali :"); lga.setBounds(10,y,100,23); tabB.add(lga);
        CmbGigiAnomali = new ComboBox(); CmbGigiAnomali.setModel(new DefaultComboBoxModel(new String[]{"Tidak Ada","Ada"}));
        CmbGigiAnomali.setBounds(115,y,80,23); tabB.add(CmbGigiAnomali);
        Label lketga = new Label(); lketga.setText("(Keterangan) :"); lketga.setBounds(205,y,100,23); tabB.add(lketga);
        TKetGigiAnomali = new TextArea(); TKetGigiAnomali.setBounds(310,y,315,46);
        TKetGigiAnomali.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabB.add(TKetGigiAnomali);

        y += 52;
        Label lll = new Label(); lll.setText("Lain-lain :"); lll.setBounds(10,y,75,23); tabB.add(lll);
        TLainLain = new TextArea(); TLainLain.setBounds(90,y,535,46);
        TLainLain.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabB.add(TLainLain);

        y += 52;
        Label ldmfd = new Label(); ldmfd.setText("D :"); ldmfd.setBounds(10,y,25,23); tabB.add(ldmfd);
        TDMF_D = new TextBox(); TDMF_D.setBounds(35,y,45,23); tabB.add(TDMF_D);
        Label ldmfm = new Label(); ldmfm.setText("M :"); ldmfm.setBounds(90,y,25,23); tabB.add(ldmfm);
        TDMF_M = new TextBox(); TDMF_M.setBounds(118,y,45,23); tabB.add(TDMF_M);
        Label ldmff = new Label(); ldmff.setText("F :"); ldmff.setBounds(173,y,25,23); tabB.add(ldmff);
        TDMF_F = new TextBox(); TDMF_F.setBounds(198,y,45,23); tabB.add(TDMF_F);

        Label ljp = new Label(); ljp.setText("Jml.Photo :"); ljp.setBounds(260,y,80,23); tabB.add(ljp);
        TJmlPhoto = new TextBox(); TJmlPhoto.setBounds(345,y,60,23); tabB.add(TJmlPhoto);
        Label ljr = new Label(); ljr.setText("Jml.Rontgen :"); ljr.setBounds(415,y,95,23); tabB.add(ljr);
        TJmlRontgen = new TextBox(); TJmlRontgen.setBounds(515,y,110,23); tabB.add(TJmlRontgen);

        y += 32;
        Label ltpr = new Label(); ltpr.setText("Tgl. Periksa :"); ltpr.setBounds(10,y,90,23); tabB.add(ltpr);
        TglPeriksa = new widget.Tanggal(); TglPeriksa.setDisplayFormat("dd-MM-yyyy"); TglPeriksa.setBounds(105,y,120,23); tabB.add(TglPeriksa);

        y += 28;
        Label lnipper = new Label(); lnipper.setText("Pemeriksa :"); lnipper.setBounds(10,y,80,23); tabB.add(lnipper);
        NIPPemeriksa = new TextBox(); NIPPemeriksa.setBounds(95,y,80,23); tabB.add(NIPPemeriksa);
        NamaPemeriksa = new TextBox(); NamaPemeriksa.setBounds(180,y,220,23); tabB.add(NamaPemeriksa);
        BtnPemeriksa = new Button(); BtnPemeriksa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPemeriksa.setBounds(405,y,28,23); tabB.add(BtnPemeriksa);
        BtnPemeriksa.addActionListener(ev->{pemeriksDlg.emptTeks();pemeriksDlg.isCek();pemeriksDlg.setSize(800,500);pemeriksDlg.setLocationRelativeTo(null);pemeriksDlg.setVisible(true);});
        pemeriksDlg.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosed(java.awt.event.WindowEvent e){
                if(pemeriksDlg.getTable().getSelectedRow()!=-1){
                    NIPPemeriksa.setText(pemeriksDlg.getTable().getValueAt(pemeriksDlg.getTable().getSelectedRow(),0).toString());
                    NamaPemeriksa.setText(pemeriksDlg.getTable().getValueAt(pemeriksDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        // ===== ODONTOGRAM VISUAL (atas) + form input (bawah) dalam satu tab =====
        panelOdonto = new PanelOdontogramFDI();
        panelOdonto.setOnChangeCallback(() -> {
            int[] dmft = panelOdonto.getDMFT();
            TDMF_D.setText(String.valueOf(dmft[0]));
            TDMF_M.setText(String.valueOf(dmft[1]));
            TDMF_F.setText(String.valueOf(dmft[2]));
            syncOdontoToTable();
        });
        JPanel combinedB = new JPanel();
        combinedB.setLayout(new BoxLayout(combinedB, BoxLayout.PAGE_AXIS));
        combinedB.setOpaque(false);
        combinedB.add(panelOdonto);
        combinedB.add(tabB);
        ScrollPane scrollB = new ScrollPane(); scrollB.setViewportView(combinedB);
        tabs.addTab("B. Odontogram", scrollB);

        // ===== TAB B2 - DATA ODONTOGRAM TERSIMPAN =====
        tabGigiMode = new DefaultTableModel(new Object[][]{}, new String[]{"No.Gigi","Kondisi","Keterangan"}){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tbGigi = new widget.Table(); tbGigi.setModel(tabGigiMode);
        ScrollPane scrollB2 = new ScrollPane(); scrollB2.setViewportView(tbGigi);
        tabs.addTab("B2. Data Odontogram", scrollB2);

        // ===== TAB C - TABEL PERAWATAN =====
        PanelBiasa tabC = new PanelBiasa();
        tabC.setLayout(null);
        tabC.setPreferredSize(new Dimension(860,600));

        y = 10;
        JLabel hC = new JLabel("RM.PGI.01.002.C.2022  -  TABEL PERAWATAN");
        hC.setFont(new java.awt.Font("Tahoma",1,11)); hC.setBounds(10,y,450,20); tabC.add(hC);

        y += 30;
        JLabel hForm = new JLabel("Input Perawatan :");
        hForm.setFont(new java.awt.Font("Tahoma",1,10)); hForm.setForeground(new Color(0,0,160));
        hForm.setBounds(10,y,180,18); tabC.add(hForm);

        y += 22;
        Label ltpw = new Label(); ltpw.setText("Tanggal :"); ltpw.setBounds(10,y,65,23); tabC.add(ltpw);
        TglPerawatan = new widget.Tanggal(); TglPerawatan.setDisplayFormat("dd-MM-yyyy"); TglPerawatan.setBounds(80,y,120,23); tabC.add(TglPerawatan);
        Label lkg = new Label(); lkg.setText("Gigi :"); lkg.setBounds(215,y,45,23); tabC.add(lkg);
        TKodeGigi = new TextBox(); TKodeGigi.setBounds(265,y,60,23); tabC.add(TKodeGigi);
        Label licd = new Label(); licd.setText("Kode ICD :"); licd.setBounds(340,y,75,23); tabC.add(licd);
        TKodeICD = new TextBox(); TKodeICD.setBounds(420,y,80,23); tabC.add(TKodeICD);

        y += 28;
        Label lkd = new Label(); lkd.setText("Keluhan/Diagnosa :"); lkd.setBounds(10,y,130,23); tabC.add(lkd);
        TKeluhanDiagnosa = new TextArea(); TKeluhanDiagnosa.setBounds(145,y,480,50);
        TKeluhanDiagnosa.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabC.add(TKeluhanDiagnosa);

        y += 56;
        Label lpw = new Label(); lpw.setText("Perawatan :"); lpw.setBounds(10,y,80,23); tabC.add(lpw);
        TPerawatan = new TextArea(); TPerawatan.setBounds(95,y,530,50);
        TPerawatan.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabC.add(TPerawatan);

        y += 56;
        Label ldktr = new Label(); ldktr.setText("Paraf (Dokter) :"); ldktr.setBounds(10,y,110,23); tabC.add(ldktr);
        NIPDokter = new TextBox(); NIPDokter.setBounds(125,y,80,23); tabC.add(NIPDokter);
        NamaDokter = new TextBox(); NamaDokter.setBounds(210,y,200,23); tabC.add(NamaDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(415,y,28,23); tabC.add(BtnDokter);
        BtnDokter.addActionListener(ev->{dokterDlg.emptTeks();dokterDlg.isCek();dokterDlg.setSize(800,500);dokterDlg.setLocationRelativeTo(null);dokterDlg.setVisible(true);});
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosed(java.awt.event.WindowEvent e){
                if(dokterDlg.getTable().getSelectedRow()!=-1){
                    NIPDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),0).toString());
                    NamaDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        y += 28;
        Label lketpw = new Label(); lketpw.setText("Keterangan :"); lketpw.setBounds(10,y,90,23); tabC.add(lketpw);
        TKetPerawatan = new TextArea(); TKetPerawatan.setBounds(105,y,520,40);
        TKetPerawatan.setBorder(BorderFactory.createLineBorder(Color.GRAY)); tabC.add(TKetPerawatan);

        y += 48;
        JLabel hTbl = new JLabel("Daftar Perawatan :");
        hTbl.setFont(new java.awt.Font("Tahoma",1,10)); hTbl.setForeground(new Color(0,0,160));
        hTbl.setBounds(10,y,180,18); tabC.add(hTbl);

        y += 22;
        ScrollPane scrollPerawatan = new ScrollPane();
        tbPerawatan = new widget.Table();
        tabPerawatanMode = new DefaultTableModel(new Object[][]{},
            new String[]{"Tanggal","Gigi","Keluhan/Diagnosa","Kode ICD","Perawatan","NIP Dokter","Nama Dokter","Keterangan"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbPerawatan.setModel(tabPerawatanMode);
        scrollPerawatan.setViewportView(tbPerawatan);
        scrollPerawatan.setBounds(10,y,840,180); tabC.add(scrollPerawatan);

        ScrollPane scrollC = new ScrollPane(); scrollC.setViewportView(tabC);
        tabs.addTab("C. Tabel Perawatan", scrollC);

        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44,54)); panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        BtnSimpan=new Button();BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));BtnSimpan.setText("Simpan");BtnSimpan.setPreferredSize(new Dimension(100,30));
        BtnBatal=new Button();BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));BtnBatal.setText("Baru");BtnBatal.setPreferredSize(new Dimension(100,30));
        BtnHapus=new Button();BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));BtnHapus.setText("Hapus");BtnHapus.setPreferredSize(new Dimension(100,30));
        BtnEdit=new Button();BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));BtnEdit.setText("Ganti");BtnEdit.setPreferredSize(new Dimension(100,30));
        BtnPrint=new Button();BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));BtnPrint.setText("Cetak");BtnPrint.setPreferredSize(new Dimension(100,30));
        BtnKeluar=new Button();BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));BtnKeluar.setText("Keluar");BtnKeluar.setPreferredSize(new Dimension(100,30));
        panelBawah.add(BtnSimpan);panelBawah.add(BtnBatal);panelBawah.add(BtnHapus);panelBawah.add(BtnEdit);panelBawah.add(BtnPrint);panelBawah.add(BtnKeluar);

        BtnKeluar.addActionListener(ev->dispose());
        BtnBatal.addActionListener(ev->emptTeks());

        BtnSimpan.addActionListener(ev->{
            if(tabs.getSelectedIndex() == 3) {
                // Tab C: tambah baris perawatan
                tabPerawatanMode.addRow(new Object[]{
                    Valid.SetTgl(TglPerawatan.getSelectedItem()+""),
                    TKodeGigi.getText(), TKeluhanDiagnosa.getText(),
                    TKodeICD.getText(), TPerawatan.getText(),
                    NIPDokter.getText(), NamaDokter.getText(), TKetPerawatan.getText()
                });
                TKodeGigi.setText(""); TKeluhanDiagnosa.setText(""); TKodeICD.setText("");
                TPerawatan.setText(""); TKetPerawatan.setText("");
            } else {
                if(TNoRw.getText().trim().isEmpty()){Valid.textKosong(TNoRw,"Pasien");return;}
                if(NIP.getText().trim().isEmpty()){Valid.textKosong(NIP,"Petugas");return;}
                simpanData();
            }
        });

        BtnHapus.addActionListener(ev->{
            if(tabs.getSelectedIndex() == 3) {
                // Tab C: hapus baris perawatan terpilih
                if(tbPerawatan.getSelectedRow()!=-1) tabPerawatanMode.removeRow(tbPerawatan.getSelectedRow());
            } else {
                if(TNoRw.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
                String nr=TNoRw.getText().trim();
                if(JOptionPane.showConfirmDialog(null,"Hapus data rekam medik gigi untuk "+nr+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)==0){
                    Sequel.queryu2tf("delete from rekam_medik_gigi where no_rawat=?",1,new String[]{nr});
                    Sequel.queryu2tf("delete from perawatan_gigi where no_rawat=?",1,new String[]{nr});
                    Sequel.queryu2tf("delete from odontogram_gigi where no_rawat=?",1,new String[]{nr});
                    emptTeks();
                }
            }
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            String nr=TNoRw.getText().trim();
            Sequel.queryu2tf("delete from rekam_medik_gigi where no_rawat=?",1,new String[]{nr});
            Sequel.queryu2tf("delete from perawatan_gigi where no_rawat=?",1,new String[]{nr});
            Sequel.queryu2tf("delete from odontogram_gigi where no_rawat=?",1,new String[]{nr});
            tabs.setSelectedIndex(0); BtnSimpan.doClick();
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            java.util.Map<String,Object> p=new java.util.HashMap<>();
            p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("kotars",akses.getkabupatenrs());p.put("propinsirs",akses.getpropinsirs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            p.put("no_rawat",TNoRw.getText());
            p.put("odontogram_img", panelOdonto != null ? panelOdonto.renderToStream() : null);
            Valid.MyReportqry("rptRekamMedikGigi.jasper","report","::[ Rekam Medik Kedokteran Gigi ]::",
                "select r.no_rawat,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.jk," +
                "p.alamat,p.no_tlp as tlp," +
                "sb.nama_suku_bangsa as suku_ras,p.pekerjaan as pekerjaan_kantor,p.gol_darah as goldar," +
                "m.td_sistolik,m.td_diastolik,m.status_td," +
                "m.penyakit_jantung,m.diabetes,m.haemopilia,m.hepatitis,m.gastritis," +
                "m.penyakit_lainnya,m.ket_penyakit_lainnya," +
                "m.alergi_obat,m.ket_alergi_obat,m.alergi_makanan,m.ket_alergi_makanan," +
                "m.tgl_pencatatan," +
                "m.occlusi,m.torus_palatinus,m.torus_mandibularis,m.palatum," +
                "m.diastema,m.ket_diastema,m.gigi_anomali,m.ket_gigi_anomali," +
                "m.lain_lain,m.dmf_d,m.dmf_m,m.dmf_f," +
                "m.jml_photo,m.jml_rontgen,m.tgl_periksa,dk2.nm_dokter as nm_petugas " +
                "from reg_periksa r " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join suku_bangsa sb on p.suku_bangsa=sb.id " +
                "left join rekam_medik_gigi m on r.no_rawat=m.no_rawat " +
                "left join dokter dk2 on m.nip_pemeriksa=dk2.kd_dokter " +
                "where r.no_rawat='"+TNoRw.getText()+"'",p);
        });

        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout(0,2));ifd.setBackground(new Color(255,255,255));
        ifd.add(tabs, BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    /** Sinkronisasi kondisi dari PanelOdontogramFDI ke tabGigiMode */
    private void syncOdontoToTable() {
        Valid.tabelKosong(tabGigiMode);
        Map<Integer,String> kondisi    = panelOdonto.getKondisiMap();
        Map<Integer,String> keterangan = panelOdonto.getKeteranganMap();
        for (Map.Entry<Integer,String> e : kondisi.entrySet()) {
            if (!"N".equals(e.getValue())) { // hanya yang bukan Normal
                tabGigiMode.addRow(new String[]{
                    String.valueOf(e.getKey()),
                    e.getValue(),
                    keterangan.getOrDefault(e.getKey(), "")
                });
            }
        }
    }

    /** Muat data dari tabGigiMode ke PanelOdontogramFDI */
    private void syncTableToOdonto() {
        Map<Integer,String> kondisi    = new java.util.HashMap<>();
        Map<Integer,String> keterangan = new java.util.HashMap<>();
        for (int i = 0; i < tabGigiMode.getRowCount(); i++) {
            try {
                int   num  = Integer.parseInt(tabGigiMode.getValueAt(i,0).toString().trim());
                String kd  = tabGigiMode.getValueAt(i,1) != null ? tabGigiMode.getValueAt(i,1).toString() : "N";
                String ket = tabGigiMode.getValueAt(i,2) != null ? tabGigiMode.getValueAt(i,2).toString() : "";
                kondisi.put(num, kd);
                keterangan.put(num, ket);
            } catch (Exception ex) { /* skip baris tidak valid */ }
        }
        panelOdonto.loadData(kondisi, keterangan);
        int[] dmft = panelOdonto.getDMFT();
        TDMF_D.setText(String.valueOf(dmft[0]));
        TDMF_M.setText(String.valueOf(dmft[1]));
        TDMF_F.setText(String.valueOf(dmft[2]));
    }

    /** Konversi string ke integer string; kembalikan "0" jika kosong atau bukan angka */
    private String toInt(String s) {
        try { return String.valueOf(Integer.parseInt(s == null ? "" : s.trim())); }
        catch (Exception e) { return "0"; }
    }

    private void simpanData(){
        String tglPen = Valid.SetTgl(TglPencatatan.getSelectedItem()+"");
        String tglPer = Valid.SetTgl(TglPeriksa.getSelectedItem()+"");
        // Save rekam_medik_gigi (33 fields - suku_ras/pekerjaan/goldar/telp_seluler/alamat_kantor diambil dari pasien)
        if(Sequel.menyimpantf("rekam_medik_gigi",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
            "Data",33,new String[]{
                TNoRw.getText(),
                TTdSistolik.getText(), TTdDiastolik.getText(),
                CmbStatusTd.getSelectedItem().toString(),
                CmbJantung.getSelectedItem().toString(), CmbDiabetes.getSelectedItem().toString(),
                CmbHaemopilia.getSelectedItem().toString(), CmbHepatitis.getSelectedItem().toString(),
                CmbGastritis.getSelectedItem().toString(), CmbPenyakitLain.getSelectedItem().toString(),
                TKetPenyakitLain.getText(), CmbAlergiObat.getSelectedItem().toString(), TKetAlergiObat.getText(),
                CmbAlergiMakanan.getSelectedItem().toString(), TKetAlergiMakanan.getText(),
                tglPen, NIP.getText(),
                CmbOcclusi.getSelectedItem().toString(), CmbTorusPalatinus.getSelectedItem().toString(),
                CmbTorusMandibularis.getSelectedItem().toString(), CmbPalatum.getSelectedItem().toString(),
                CmbDiastema.getSelectedItem().toString(), TKetDiastema.getText(),
                CmbGigiAnomali.getSelectedItem().toString(), TKetGigiAnomali.getText(),
                TLainLain.getText(), toInt(TDMF_D.getText()), toInt(TDMF_M.getText()), toInt(TDMF_F.getText()),
                toInt(TJmlPhoto.getText()), toInt(TJmlRontgen.getText()), tglPer, NIPPemeriksa.getText()
            })){
            // Save odontogram detail
            for(int i=0;i<tabGigiMode.getRowCount();i++){
                Sequel.menyimpantf("odontogram_gigi","?,?,?,?","",4,new String[]{
                    TNoRw.getText(),
                    tabGigiMode.getValueAt(i,0)!=null?tabGigiMode.getValueAt(i,0).toString():"",
                    tabGigiMode.getValueAt(i,1)!=null?tabGigiMode.getValueAt(i,1).toString():"",
                    tabGigiMode.getValueAt(i,2)!=null?tabGigiMode.getValueAt(i,2).toString():""
                });
            }
            // Save perawatan
            for(int i=0;i<tabPerawatanMode.getRowCount();i++){
                Sequel.menyimpantf("perawatan_gigi","?,?,?,?,?,?,?,?","",8,new String[]{
                    TNoRw.getText(),
                    tabPerawatanMode.getValueAt(i,0)!=null?tabPerawatanMode.getValueAt(i,0).toString():"",
                    tabPerawatanMode.getValueAt(i,1)!=null?tabPerawatanMode.getValueAt(i,1).toString():"",
                    tabPerawatanMode.getValueAt(i,2)!=null?tabPerawatanMode.getValueAt(i,2).toString():"",
                    tabPerawatanMode.getValueAt(i,3)!=null?tabPerawatanMode.getValueAt(i,3).toString():"",
                    tabPerawatanMode.getValueAt(i,4)!=null?tabPerawatanMode.getValueAt(i,4).toString():"",
                    tabPerawatanMode.getValueAt(i,5)!=null?tabPerawatanMode.getValueAt(i,5).toString():"",
                    tabPerawatanMode.getValueAt(i,7)!=null?tabPerawatanMode.getValueAt(i,7).toString():""
                });
            }
            emptTeks();
        }
    }

    public void getData(){
        String nr=TNoRw.getText().trim();
        if(nr.isEmpty()) return;

        // Load viewer fields dari pasien (readonly)
        try{
            ps=koneksi.prepareStatement(
                "select sb.nama_suku_bangsa,p.pekerjaan,p.gol_darah,p.no_tlp "+
                "from pasien p "+
                "left join suku_bangsa sb on p.suku_bangsa=sb.id "+
                "inner join reg_periksa r on p.no_rkm_medis=r.no_rkm_medis "+
                "where r.no_rawat=?");
            ps.setString(1,nr); rs=ps.executeQuery();
            if(rs.next()){
                TSukuRas.setText(rs.getString("nama_suku_bangsa")!=null?rs.getString("nama_suku_bangsa"):"");
                TPekerjaanKantor.setText(rs.getString("pekerjaan")!=null?rs.getString("pekerjaan"):"");
                TGoldar.setText(rs.getString("gol_darah")!=null?rs.getString("gol_darah"):"");
                TTelpSeluler.setText(rs.getString("no_tlp")!=null?rs.getString("no_tlp"):"");
            }
        }catch(Exception ex){System.out.println("Notif getData pasien viewer: "+ex);}
        finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception ex){}}

        try{
            ps=koneksi.prepareStatement(
                "select g.*,dk.nm_dokter as nm_petugas,pt2.nama as nm_pemeriksa "+
                "from rekam_medik_gigi g "+
                "left join dokter dk on g.nip=dk.kd_dokter "+
                "left join petugas pt2 on g.nip_pemeriksa=pt2.nip "+
                "where g.no_rawat=?");
            ps.setString(1,nr); rs=ps.executeQuery();
            if(rs.next()){
                TTdSistolik.setText(rs.getString("td_sistolik"));
                TTdDiastolik.setText(rs.getString("td_diastolik"));
                if(rs.getString("status_td")!=null) CmbStatusTd.setSelectedItem(rs.getString("status_td"));
                if(rs.getString("penyakit_jantung")!=null) CmbJantung.setSelectedItem(rs.getString("penyakit_jantung"));
                if(rs.getString("diabetes")!=null) CmbDiabetes.setSelectedItem(rs.getString("diabetes"));
                if(rs.getString("haemopilia")!=null) CmbHaemopilia.setSelectedItem(rs.getString("haemopilia"));
                if(rs.getString("hepatitis")!=null) CmbHepatitis.setSelectedItem(rs.getString("hepatitis"));
                if(rs.getString("gastritis")!=null) CmbGastritis.setSelectedItem(rs.getString("gastritis"));
                if(rs.getString("penyakit_lainnya")!=null) CmbPenyakitLain.setSelectedItem(rs.getString("penyakit_lainnya"));
                TKetPenyakitLain.setText(rs.getString("ket_penyakit_lainnya"));
                if(rs.getString("alergi_obat")!=null) CmbAlergiObat.setSelectedItem(rs.getString("alergi_obat"));
                TKetAlergiObat.setText(rs.getString("ket_alergi_obat"));
                if(rs.getString("alergi_makanan")!=null) CmbAlergiMakanan.setSelectedItem(rs.getString("alergi_makanan"));
                TKetAlergiMakanan.setText(rs.getString("ket_alergi_makanan"));
                try{if(rs.getString("tgl_pencatatan")!=null) TglPencatatan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tgl_pencatatan")));}catch(Exception ex){}
                NIP.setText(rs.getString("nip")); NamaPetugas.setText(rs.getString("nm_petugas"));
                // Odontogram
                if(rs.getString("occlusi")!=null) CmbOcclusi.setSelectedItem(rs.getString("occlusi"));
                if(rs.getString("torus_palatinus")!=null) CmbTorusPalatinus.setSelectedItem(rs.getString("torus_palatinus"));
                if(rs.getString("torus_mandibularis")!=null) CmbTorusMandibularis.setSelectedItem(rs.getString("torus_mandibularis"));
                if(rs.getString("palatum")!=null) CmbPalatum.setSelectedItem(rs.getString("palatum"));
                if(rs.getString("diastema")!=null) CmbDiastema.setSelectedItem(rs.getString("diastema"));
                TKetDiastema.setText(rs.getString("ket_diastema"));
                if(rs.getString("gigi_anomali")!=null) CmbGigiAnomali.setSelectedItem(rs.getString("gigi_anomali"));
                TKetGigiAnomali.setText(rs.getString("ket_gigi_anomali"));
                TLainLain.setText(rs.getString("lain_lain"));
                TDMF_D.setText(rs.getString("dmf_d")); TDMF_M.setText(rs.getString("dmf_m")); TDMF_F.setText(rs.getString("dmf_f"));
                TJmlPhoto.setText(rs.getString("jml_photo")); TJmlRontgen.setText(rs.getString("jml_rontgen"));
                try{if(rs.getString("tgl_periksa")!=null) TglPeriksa.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tgl_periksa")));}catch(Exception ex){}
                NIPPemeriksa.setText(rs.getString("nip_pemeriksa")); NamaPemeriksa.setText(rs.getString("nm_pemeriksa"));
                // Populate ringkasan Tab A
                Valid.tabelKosong(tabMedikMode);
                tabMedikMode.addRow(new Object[]{
                    rs.getString("td_sistolik"), rs.getString("td_diastolik"), rs.getString("status_td"),
                    rs.getString("penyakit_jantung"), rs.getString("diabetes"), rs.getString("haemopilia"),
                    rs.getString("hepatitis"), rs.getString("gastritis"), rs.getString("penyakit_lainnya"),
                    rs.getString("alergi_obat"), rs.getString("alergi_makanan"),
                    rs.getString("tgl_pencatatan"), rs.getString("nm_petugas")
                });
            }
            if(rs!=null)rs.close(); if(ps!=null)ps.close();
        }catch(Exception e){System.out.println("Notif getData: "+e);}

        // Load odontogram detail
        Valid.tabelKosong(tabGigiMode);
        try{
            ps=koneksi.prepareStatement("select nomor_gigi,kondisi,keterangan from odontogram_gigi where no_rawat=? order by nomor_gigi");
            ps.setString(1,nr); rs=ps.executeQuery();
            while(rs.next()) tabGigiMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3)});
        }catch(Exception e){System.out.println("Notif getData gigi: "+e);}
        finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        // Sync ke visual odontogram
        if (panelOdonto != null) syncTableToOdonto();

        // Load perawatan
        Valid.tabelKosong(tabPerawatanMode);
        try{
            ps=koneksi.prepareStatement(
                "select pg.tanggal,pg.kode_gigi,pg.keluhan_diagnosa,pg.kode_icd,pg.perawatan,pg.nip_dokter,d.nm_dokter,pg.keterangan "+
                "from perawatan_gigi pg left join dokter d on pg.nip_dokter=d.kd_dokter "+
                "where pg.no_rawat=? order by pg.tanggal");
            ps.setString(1,nr); rs=ps.executeQuery();
            while(rs.next()) tabPerawatanMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)});
        }catch(Exception e){System.out.println("Notif getData perawatan: "+e);}
        finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void emptTeks(){
        TglPencatatan.setDate(new Date()); TglPeriksa.setDate(new Date()); TglPerawatan.setDate(new Date());
        TSukuRas.setText(""); TPekerjaanKantor.setText(""); TTelpSeluler.setText(""); TGoldar.setText("");
        TTdSistolik.setText(""); TTdDiastolik.setText("");
        CmbStatusTd.setSelectedIndex(0); CmbJantung.setSelectedIndex(0); CmbDiabetes.setSelectedIndex(0);
        CmbHaemopilia.setSelectedIndex(0); CmbHepatitis.setSelectedIndex(0); CmbGastritis.setSelectedIndex(0);
        CmbPenyakitLain.setSelectedIndex(0); CmbAlergiObat.setSelectedIndex(0); CmbAlergiMakanan.setSelectedIndex(0);
        TKetPenyakitLain.setText(""); TKetAlergiObat.setText(""); TKetAlergiMakanan.setText("");
        NIP.setText(""); NamaPetugas.setText("");
        CmbOcclusi.setSelectedIndex(0); CmbTorusPalatinus.setSelectedIndex(0);
        CmbTorusMandibularis.setSelectedIndex(0); CmbPalatum.setSelectedIndex(0);
        CmbDiastema.setSelectedIndex(0); TKetDiastema.setText(""); CmbGigiAnomali.setSelectedIndex(0); TKetGigiAnomali.setText("");
        TLainLain.setText(""); TDMF_D.setText(""); TDMF_M.setText(""); TDMF_F.setText("");
        TJmlPhoto.setText(""); TJmlRontgen.setText(""); NIPPemeriksa.setText(""); NamaPemeriksa.setText("");
        Valid.tabelKosong(tabGigiMode); Valid.tabelKosong(tabMedikMode);
        if (panelOdonto != null) panelOdonto.resetAll();
        TKodeGigi.setText(""); TKeluhanDiagnosa.setText(""); TKodeICD.setText("");
        TPerawatan.setText(""); NIPDokter.setText(""); NamaDokter.setText(""); TKetPerawatan.setText("");
        Valid.tabelKosong(tabPerawatanMode);
    }

    public void setNoRm(String n, String m, String p){
        TNoRw.setText(n); TNoRM.setText(m); TPasien.setText(p);
        getData();
    }

    public void isCek(){
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
