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
import java.util.Date;
import javax.swing.*;
import widget.*;

public class RMPetugasBankDarahRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnKeluar, BtnPetugas;

    // Header info (read-only)
    private TextBox TNoRawat, TTanggal, TNmPasien, TRuangRawat;

    // Tanggal / jam periksa
    private widget.Tanggal TglPeriksa;
    private TextBox TJamPeriksa;

    // Ada/Tidak comboboxes
    private ComboBox CmbContohSebelum, CmbContohSetelah, CmbSisaKantong, CmbLamaWaktu, CmbTransportasi;

    // Kategori & verifikasi comboboxes
    private ComboBox CmbKategoriReaksi;
    private ComboBox CmbIdentitasPasien, CmbGolonganDarah, CmbJenisKomponen, CmbLabelDarah;

    // Bank Darah Sebelum
    private TextBox TBdSblIdentitas, TBdSblGoldarPasien, TBdSblGoldarDonor;
    private TextBox TBdSblSkriningAb, TBdSblMayor, TBdSblDct, TBdSblPetugas;

    // Bank Darah Setelah
    private TextBox TBdStlIdentitas, TBdStlGoldarPasien, TBdStlGoldarDonor;
    private TextBox TBdStlSkriningAb, TBdStlMayor, TBdStlDct, TBdStlPetugas;

    // UDD PMI
    private TextBox TUddSampel, TUddGoldarPasien, TUddGoldarDonor;
    private TextBox TUddSkriningAb, TUddMayor, TUddDct, TUddPetugas;

    // Petugas bank darah
    private TextBox NIP, NamaPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private ScrollPane ScrollInput;
    private PanelBiasa FormInput;

    private String noRawatSave = "";
    private String tanggalSave = "";

    public RMPetugasBankDarahRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Form Petugas Bank Darah ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(720, 1050));
        FormInput.setLayout(null);

        int y = 10;

        // === HEADER INFO ===
        JLabel hdr = new JLabel("Form Petugas Bank Darah");
        hdr.setFont(new java.awt.Font("Tahoma", 1, 12));
        hdr.setBounds(10, y, 400, 20);
        FormInput.add(hdr);

        y += 28;
        Label lnr = new Label(); lnr.setText("No. Rawat :"); lnr.setBounds(10, y, 80, 23); FormInput.add(lnr);
        TNoRawat = new TextBox(); TNoRawat.setBounds(95, y, 130, 23); TNoRawat.setEditable(false); FormInput.add(TNoRawat);
        Label ltgl = new Label(); ltgl.setText("Tanggal :"); ltgl.setBounds(240, y, 60, 23); FormInput.add(ltgl);
        TTanggal = new TextBox(); TTanggal.setBounds(305, y, 160, 23); TTanggal.setEditable(false); FormInput.add(TTanggal);

        y += 28;
        Label lnm = new Label(); lnm.setText("Nama Pasien :"); lnm.setBounds(10, y, 85, 23); FormInput.add(lnm);
        TNmPasien = new TextBox(); TNmPasien.setBounds(100, y, 250, 23); TNmPasien.setEditable(false); FormInput.add(TNmPasien);
        Label lrr = new Label(); lrr.setText("Ruang Rawat :"); lrr.setBounds(360, y, 90, 23); FormInput.add(lrr);
        TRuangRawat = new TextBox(); TRuangRawat.setBounds(455, y, 155, 23); TRuangRawat.setEditable(false); FormInput.add(TRuangRawat);

        // separator
        JSeparator sep0 = new JSeparator(); sep0.setBounds(10, y + 28, 700, 2); FormInput.add(sep0);

        y += 38;
        JLabel hs1 = new JLabel("Data Pemeriksaan :"); hs1.setFont(new java.awt.Font("Tahoma", 1, 11)); hs1.setBounds(10, y, 250, 20); FormInput.add(hs1);

        y += 25;
        Label ltp = new Label(); ltp.setText("Tgl Periksa :"); ltp.setBounds(10, y, 80, 23); FormInput.add(ltp);
        TglPeriksa = new widget.Tanggal(); TglPeriksa.setDisplayFormat("dd-MM-yyyy"); TglPeriksa.setBounds(95, y, 120, 23); FormInput.add(TglPeriksa);
        Label ljp = new Label(); ljp.setText("Jam :"); ljp.setBounds(230, y, 40, 23); FormInput.add(ljp);
        TJamPeriksa = new TextBox(); TJamPeriksa.setBounds(275, y, 80, 23); FormInput.add(TJamPeriksa);

        y += 28;
        Label lcs = new Label(); lcs.setText("Contoh Darah Sebelum :"); lcs.setBounds(10, y, 140, 23); FormInput.add(lcs);
        CmbContohSebelum = new ComboBox(); CmbContohSebelum.addItem("Ada"); CmbContohSebelum.addItem("Tidak");
        CmbContohSebelum.setBounds(155, y, 90, 23); FormInput.add(CmbContohSebelum);
        Label lct = new Label(); lct.setText("Contoh Darah Setelah :"); lct.setBounds(260, y, 140, 23); FormInput.add(lct);
        CmbContohSetelah = new ComboBox(); CmbContohSetelah.addItem("Ada"); CmbContohSetelah.addItem("Tidak");
        CmbContohSetelah.setBounds(405, y, 90, 23); FormInput.add(CmbContohSetelah);

        y += 28;
        Label lsk = new Label(); lsk.setText("Sisa Kantong :"); lsk.setBounds(10, y, 90, 23); FormInput.add(lsk);
        CmbSisaKantong = new ComboBox(); CmbSisaKantong.addItem("Ada"); CmbSisaKantong.addItem("Tidak");
        CmbSisaKantong.setBounds(105, y, 90, 23); FormInput.add(CmbSisaKantong);
        Label llw = new Label(); llw.setText("Lama Waktu :"); llw.setBounds(210, y, 85, 23); FormInput.add(llw);
        CmbLamaWaktu = new ComboBox(); CmbLamaWaktu.addItem("Ada"); CmbLamaWaktu.addItem("Tidak");
        CmbLamaWaktu.setBounds(300, y, 90, 23); FormInput.add(CmbLamaWaktu);
        Label ltr = new Label(); ltr.setText("Transportasi :"); ltr.setBounds(405, y, 85, 23); FormInput.add(ltr);
        CmbTransportasi = new ComboBox(); CmbTransportasi.addItem("Ada"); CmbTransportasi.addItem("Tidak");
        CmbTransportasi.setBounds(495, y, 90, 23); FormInput.add(CmbTransportasi);

        y += 28;
        Label lkr = new Label(); lkr.setText("Kategori Reaksi :"); lkr.setBounds(10, y, 105, 23); FormInput.add(lkr);
        CmbKategoriReaksi = new ComboBox(); CmbKategoriReaksi.addItem("I"); CmbKategoriReaksi.addItem("II"); CmbKategoriReaksi.addItem("III");
        CmbKategoriReaksi.setBounds(120, y, 80, 23); FormInput.add(CmbKategoriReaksi);

        y += 28;
        Label lip = new Label(); lip.setText("Identitas Pasien :"); lip.setBounds(10, y, 105, 23); FormInput.add(lip);
        CmbIdentitasPasien = new ComboBox(); CmbIdentitasPasien.addItem("Sesuai"); CmbIdentitasPasien.addItem("Tidak");
        CmbIdentitasPasien.setBounds(120, y, 90, 23); FormInput.add(CmbIdentitasPasien);
        Label lgd = new Label(); lgd.setText("Golongan Darah :"); lgd.setBounds(225, y, 105, 23); FormInput.add(lgd);
        CmbGolonganDarah = new ComboBox(); CmbGolonganDarah.addItem("Sesuai"); CmbGolonganDarah.addItem("Tidak");
        CmbGolonganDarah.setBounds(335, y, 90, 23); FormInput.add(CmbGolonganDarah);
        Label ljk = new Label(); ljk.setText("Jenis Komponen :"); ljk.setBounds(440, y, 105, 23); FormInput.add(ljk);
        CmbJenisKomponen = new ComboBox(); CmbJenisKomponen.addItem("Sesuai"); CmbJenisKomponen.addItem("Tidak");
        CmbJenisKomponen.setBounds(550, y, 90, 23); FormInput.add(CmbJenisKomponen);

        y += 28;
        Label lld = new Label(); lld.setText("Label Darah :"); lld.setBounds(10, y, 85, 23); FormInput.add(lld);
        CmbLabelDarah = new ComboBox(); CmbLabelDarah.addItem("Sesuai"); CmbLabelDarah.addItem("Tidak");
        CmbLabelDarah.setBounds(100, y, 90, 23); FormInput.add(CmbLabelDarah);

        // === BANK DARAH SEBELUM ===
        JSeparator sep1 = new JSeparator(); sep1.setBounds(10, y + 28, 700, 2); FormInput.add(sep1);
        y += 38;
        JLabel hs2 = new JLabel("Bank Darah - Sebelum Transfusi :"); hs2.setFont(new java.awt.Font("Tahoma", 1, 11)); hs2.setBounds(10, y, 350, 20); FormInput.add(hs2);

        // Header kolom
        y += 22;
        int col1=10, col2=200, col3=355, col4=545;
        JLabel hc1 = new JLabel("Pemeriksaan"); hc1.setFont(new java.awt.Font("Tahoma",1,9)); hc1.setBounds(col1,y,140,18); FormInput.add(hc1);
        JLabel hc2 = new JLabel("Goldar Pasien"); hc2.setFont(new java.awt.Font("Tahoma",1,9)); hc2.setBounds(col2,y,140,18); FormInput.add(hc2);
        JLabel hc3 = new JLabel("Goldar Donor"); hc3.setFont(new java.awt.Font("Tahoma",1,9)); hc3.setBounds(col3,y,140,18); FormInput.add(hc3);
        JLabel hc4 = new JLabel("Skrining AB"); hc4.setFont(new java.awt.Font("Tahoma",1,9)); hc4.setBounds(col4,y,100,18); FormInput.add(hc4);

        y += 20;
        Label lbsi = new Label(); lbsi.setText("Identitas :"); lbsi.setBounds(col1,y,85,23); FormInput.add(lbsi);
        TBdSblIdentitas = new TextBox(); TBdSblIdentitas.setBounds(col1+90,y,100,23); FormInput.add(TBdSblIdentitas);
        Label lbsgp = new Label(); lbsgp.setText("Goldar Pasien :"); lbsgp.setBounds(col2,y,100,23); FormInput.add(lbsgp);
        TBdSblGoldarPasien = new TextBox(); TBdSblGoldarPasien.setBounds(col2+105,y,80,23); FormInput.add(TBdSblGoldarPasien);
        Label lbsgd = new Label(); lbsgd.setText("Goldar Donor :"); lbsgd.setBounds(col3,y,100,23); FormInput.add(lbsgd);
        TBdSblGoldarDonor = new TextBox(); TBdSblGoldarDonor.setBounds(col3+105,y,80,23); FormInput.add(TBdSblGoldarDonor);
        Label lbssa = new Label(); lbssa.setText("Skrining AB :"); lbssa.setBounds(col4,y,85,23); FormInput.add(lbssa);
        TBdSblSkriningAb = new TextBox(); TBdSblSkriningAb.setBounds(col4+90,y,80,23); FormInput.add(TBdSblSkriningAb);

        y += 28;
        Label lbsm = new Label(); lbsm.setText("Mayor :"); lbsm.setBounds(col1,y,60,23); FormInput.add(lbsm);
        TBdSblMayor = new TextBox(); TBdSblMayor.setBounds(col1+65,y,90,23); FormInput.add(TBdSblMayor);
        Label lbsd = new Label(); lbsd.setText("DCT :"); lbsd.setBounds(col2,y,50,23); FormInput.add(lbsd);
        TBdSblDct = new TextBox(); TBdSblDct.setBounds(col2+55,y,90,23); FormInput.add(TBdSblDct);
        Label lbsp = new Label(); lbsp.setText("Petugas :"); lbsp.setBounds(col3,y,65,23); FormInput.add(lbsp);
        TBdSblPetugas = new TextBox(); TBdSblPetugas.setBounds(col3+70,y,200,23); FormInput.add(TBdSblPetugas);

        // === BANK DARAH SETELAH ===
        JSeparator sep2 = new JSeparator(); sep2.setBounds(10, y+28, 700, 2); FormInput.add(sep2);
        y += 38;
        JLabel hs3 = new JLabel("Bank Darah - Setelah Transfusi :"); hs3.setFont(new java.awt.Font("Tahoma", 1, 11)); hs3.setBounds(10, y, 350, 20); FormInput.add(hs3);

        y += 25;
        Label lbti = new Label(); lbti.setText("Identitas :"); lbti.setBounds(col1,y,85,23); FormInput.add(lbti);
        TBdStlIdentitas = new TextBox(); TBdStlIdentitas.setBounds(col1+90,y,100,23); FormInput.add(TBdStlIdentitas);
        Label lbtgp = new Label(); lbtgp.setText("Goldar Pasien :"); lbtgp.setBounds(col2,y,100,23); FormInput.add(lbtgp);
        TBdStlGoldarPasien = new TextBox(); TBdStlGoldarPasien.setBounds(col2+105,y,80,23); FormInput.add(TBdStlGoldarPasien);
        Label lbtgd = new Label(); lbtgd.setText("Goldar Donor :"); lbtgd.setBounds(col3,y,100,23); FormInput.add(lbtgd);
        TBdStlGoldarDonor = new TextBox(); TBdStlGoldarDonor.setBounds(col3+105,y,80,23); FormInput.add(TBdStlGoldarDonor);
        Label lbtsa = new Label(); lbtsa.setText("Skrining AB :"); lbtsa.setBounds(col4,y,85,23); FormInput.add(lbtsa);
        TBdStlSkriningAb = new TextBox(); TBdStlSkriningAb.setBounds(col4+90,y,80,23); FormInput.add(TBdStlSkriningAb);

        y += 28;
        Label lbtm = new Label(); lbtm.setText("Mayor :"); lbtm.setBounds(col1,y,60,23); FormInput.add(lbtm);
        TBdStlMayor = new TextBox(); TBdStlMayor.setBounds(col1+65,y,90,23); FormInput.add(TBdStlMayor);
        Label lbtd = new Label(); lbtd.setText("DCT :"); lbtd.setBounds(col2,y,50,23); FormInput.add(lbtd);
        TBdStlDct = new TextBox(); TBdStlDct.setBounds(col2+55,y,90,23); FormInput.add(TBdStlDct);
        Label lbtp = new Label(); lbtp.setText("Petugas :"); lbtp.setBounds(col3,y,65,23); FormInput.add(lbtp);
        TBdStlPetugas = new TextBox(); TBdStlPetugas.setBounds(col3+70,y,200,23); FormInput.add(TBdStlPetugas);

        // === UDD PMI ===
        JSeparator sep3 = new JSeparator(); sep3.setBounds(10, y+28, 700, 2); FormInput.add(sep3);
        y += 38;
        JLabel hs4 = new JLabel("UDD PMI Kab. Bekasi :"); hs4.setFont(new java.awt.Font("Tahoma", 1, 11)); hs4.setBounds(10, y, 350, 20); FormInput.add(hs4);

        y += 25;
        Label lusp = new Label(); lusp.setText("Sampel :"); lusp.setBounds(col1,y,65,23); FormInput.add(lusp);
        TUddSampel = new TextBox(); TUddSampel.setBounds(col1+70,y,110,23); FormInput.add(TUddSampel);
        Label lusgp = new Label(); lusgp.setText("Goldar Pasien :"); lusgp.setBounds(col2,y,100,23); FormInput.add(lusgp);
        TUddGoldarPasien = new TextBox(); TUddGoldarPasien.setBounds(col2+105,y,80,23); FormInput.add(TUddGoldarPasien);
        Label lusgd = new Label(); lusgd.setText("Goldar Donor :"); lusgd.setBounds(col3,y,100,23); FormInput.add(lusgd);
        TUddGoldarDonor = new TextBox(); TUddGoldarDonor.setBounds(col3+105,y,80,23); FormInput.add(TUddGoldarDonor);
        Label lussa = new Label(); lussa.setText("Skrining AB :"); lussa.setBounds(col4,y,85,23); FormInput.add(lussa);
        TUddSkriningAb = new TextBox(); TUddSkriningAb.setBounds(col4+90,y,80,23); FormInput.add(TUddSkriningAb);

        y += 28;
        Label lusm = new Label(); lusm.setText("Mayor :"); lusm.setBounds(col1,y,60,23); FormInput.add(lusm);
        TUddMayor = new TextBox(); TUddMayor.setBounds(col1+65,y,90,23); FormInput.add(TUddMayor);
        Label lusd = new Label(); lusd.setText("DCT :"); lusd.setBounds(col2,y,50,23); FormInput.add(lusd);
        TUddDct = new TextBox(); TUddDct.setBounds(col2+55,y,90,23); FormInput.add(TUddDct);
        Label luspe = new Label(); luspe.setText("Petugas :"); luspe.setBounds(col3,y,65,23); FormInput.add(luspe);
        TUddPetugas = new TextBox(); TUddPetugas.setBounds(col3+70,y,200,23); FormInput.add(TUddPetugas);

        // === PETUGAS BANK DARAH ===
        JSeparator sep4 = new JSeparator(); sep4.setBounds(10, y+28, 700, 2); FormInput.add(sep4);
        y += 38;
        JLabel hs5 = new JLabel("Petugas Bank Darah :"); hs5.setFont(new java.awt.Font("Tahoma", 1, 11)); hs5.setBounds(10, y, 250, 20); FormInput.add(hs5);

        y += 25;
        Label lnip = new Label(); lnip.setText("NIP :"); lnip.setBounds(10, y, 40, 23); FormInput.add(lnip);
        NIP = new TextBox(); NIP.setBounds(55, y, 120, 23); FormInput.add(NIP);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(185, y, 250, 23); NamaPetugas.setEditable(false); FormInput.add(NamaPetugas);
        BtnPetugas = new Button();
        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnPetugas.setBounds(445, y, 28, 23); FormInput.add(BtnPetugas);

        // Panel bottom buttons
        panelBawah = new panelisi();
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelBawah.setPreferredSize(new Dimension(720, 40));

        BtnSimpan = new Button();
        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));

        BtnBatal = new Button();
        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));

        BtnKeluar = new Button();
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnKeluar);

        // ScrollPane for FormInput
        ScrollInput = new ScrollPane();
        ScrollInput.setViewportView(FormInput);

        internalFrame1.add(ScrollInput, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);

        // === ACTION LISTENERS ===
        BtnKeluar.addActionListener(ev -> dispose());
        BtnBatal.addActionListener(ev -> emptTeks());

        BtnPetugas.addActionListener(ev -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(600, 500);
            petugasDlg.setLocationRelativeTo(this);
            petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String nm = Sequel.cariIsi("select nama from petugas where nip=?", NIP.getText().trim());
                    NamaPetugas.setText(nm);
                }
            }
        });

        BtnSimpan.addActionListener(ev -> {
            if (noRawatSave.trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Data pasien belum dipilih...!!!!");
                return;
            }
            if (NIP.getText().trim().equals("")) {
                Valid.textKosong(NIP, "NIP Petugas");
                return;
            }
            String tglP = Valid.SetTgl(TglPeriksa.getSelectedItem() + "");
            String tglFull = tglP;
            // DELETE + INSERT pattern
            Sequel.queryu2tf(
                "delete from petugas_bank_darah_rscb where no_rawat=? and tanggal=?",
                2, new String[]{noRawatSave, tanggalSave}
            );
            if (Sequel.menyimpantf(
                "petugas_bank_darah_rscb",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Data", 36,
                new String[]{
                    noRawatSave, tanggalSave,
                    tglFull,
                    TJamPeriksa.getText(),
                    CmbContohSebelum.getSelectedItem() != null ? CmbContohSebelum.getSelectedItem().toString() : "Tidak",
                    CmbContohSetelah.getSelectedItem() != null ? CmbContohSetelah.getSelectedItem().toString() : "Tidak",
                    CmbSisaKantong.getSelectedItem() != null ? CmbSisaKantong.getSelectedItem().toString() : "Tidak",
                    CmbLamaWaktu.getSelectedItem() != null ? CmbLamaWaktu.getSelectedItem().toString() : "Tidak",
                    CmbTransportasi.getSelectedItem() != null ? CmbTransportasi.getSelectedItem().toString() : "Tidak",
                    CmbKategoriReaksi.getSelectedItem() != null ? CmbKategoriReaksi.getSelectedItem().toString() : "I",
                    CmbIdentitasPasien.getSelectedItem() != null ? CmbIdentitasPasien.getSelectedItem().toString() : "Sesuai",
                    CmbGolonganDarah.getSelectedItem() != null ? CmbGolonganDarah.getSelectedItem().toString() : "Sesuai",
                    CmbJenisKomponen.getSelectedItem() != null ? CmbJenisKomponen.getSelectedItem().toString() : "Sesuai",
                    CmbLabelDarah.getSelectedItem() != null ? CmbLabelDarah.getSelectedItem().toString() : "Sesuai",
                    TBdSblIdentitas.getText(),
                    TBdSblGoldarPasien.getText(),
                    TBdSblGoldarDonor.getText(),
                    TBdSblSkriningAb.getText(),
                    TBdSblMayor.getText(),
                    TBdSblDct.getText(),
                    TBdSblPetugas.getText(),
                    TBdStlIdentitas.getText(),
                    TBdStlGoldarPasien.getText(),
                    TBdStlGoldarDonor.getText(),
                    TBdStlSkriningAb.getText(),
                    TBdStlMayor.getText(),
                    TBdStlDct.getText(),
                    TBdStlPetugas.getText(),
                    TUddSampel.getText(),
                    TUddGoldarPasien.getText(),
                    TUddGoldarDonor.getText(),
                    TUddSkriningAb.getText(),
                    TUddMayor.getText(),
                    TUddDct.getText(),
                    TUddPetugas.getText(),
                    NIP.getText()
                }
            )) {
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
            }
        });
    }

    public void setData(String noRawat, String tanggal, String nmPasien, String ruangRawat) {
        this.noRawatSave = noRawat;
        this.tanggalSave = tanggal;
        TNoRawat.setText(noRawat);
        TTanggal.setText(tanggal);
        TNmPasien.setText(nmPasien);
        TRuangRawat.setText(ruangRawat);
    }

    public void getData() {
        if (noRawatSave.trim().equals("")) return;
        try {
            ps = koneksi.prepareStatement(
                "select * from petugas_bank_darah_rscb where no_rawat=? and tanggal=?");
            ps.setString(1, noRawatSave);
            ps.setString(2, tanggalSave);
            rs = ps.executeQuery();
            if (rs.next()) {
                try {
                    if (rs.getString("tanggal_periksa") != null) {
                        String tp = rs.getString("tanggal_periksa");
                        TglPeriksa.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tp));
                    }
                } catch (Exception ex) {}
                TJamPeriksa.setText(rs.getString("jam_periksa"));
                CmbContohSebelum.setSelectedItem(rs.getString("contoh_darah_sebelum"));
                CmbContohSetelah.setSelectedItem(rs.getString("contoh_darah_setelah"));
                CmbSisaKantong.setSelectedItem(rs.getString("sisa_kantong"));
                CmbLamaWaktu.setSelectedItem(rs.getString("lama_waktu"));
                CmbTransportasi.setSelectedItem(rs.getString("transportasi"));
                CmbKategoriReaksi.setSelectedItem(rs.getString("kategori_reaksi"));
                CmbIdentitasPasien.setSelectedItem(rs.getString("identitas_pasien"));
                CmbGolonganDarah.setSelectedItem(rs.getString("golongan_darah"));
                CmbJenisKomponen.setSelectedItem(rs.getString("jenis_komponen"));
                CmbLabelDarah.setSelectedItem(rs.getString("label_darah"));
                TBdSblIdentitas.setText(rs.getString("bd_sbl_identitas"));
                TBdSblGoldarPasien.setText(rs.getString("bd_sbl_goldar_pasien"));
                TBdSblGoldarDonor.setText(rs.getString("bd_sbl_goldar_donor"));
                TBdSblSkriningAb.setText(rs.getString("bd_sbl_skrining_ab"));
                TBdSblMayor.setText(rs.getString("bd_sbl_mayor"));
                TBdSblDct.setText(rs.getString("bd_sbl_dct"));
                TBdSblPetugas.setText(rs.getString("bd_sbl_petugas"));
                TBdStlIdentitas.setText(rs.getString("bd_stl_identitas"));
                TBdStlGoldarPasien.setText(rs.getString("bd_stl_goldar_pasien"));
                TBdStlGoldarDonor.setText(rs.getString("bd_stl_goldar_donor"));
                TBdStlSkriningAb.setText(rs.getString("bd_stl_skrining_ab"));
                TBdStlMayor.setText(rs.getString("bd_stl_mayor"));
                TBdStlDct.setText(rs.getString("bd_stl_dct"));
                TBdStlPetugas.setText(rs.getString("bd_stl_petugas"));
                TUddSampel.setText(rs.getString("udd_sampel"));
                TUddGoldarPasien.setText(rs.getString("udd_goldar_pasien"));
                TUddGoldarDonor.setText(rs.getString("udd_goldar_donor"));
                TUddSkriningAb.setText(rs.getString("udd_skrining_ab"));
                TUddMayor.setText(rs.getString("udd_mayor"));
                TUddDct.setText(rs.getString("udd_dct"));
                TUddPetugas.setText(rs.getString("udd_petugas"));
                NIP.setText(rs.getString("nip"));
                if (rs.getString("nip") != null && !rs.getString("nip").trim().equals("")) {
                    NamaPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif getData PetugasBankDarah : " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    public void emptTeks() {
        TglPeriksa.setDate(new Date());
        TJamPeriksa.setText("");
        CmbContohSebelum.setSelectedIndex(0);
        CmbContohSetelah.setSelectedIndex(0);
        CmbSisaKantong.setSelectedIndex(0);
        CmbLamaWaktu.setSelectedIndex(0);
        CmbTransportasi.setSelectedIndex(0);
        CmbKategoriReaksi.setSelectedIndex(0);
        CmbIdentitasPasien.setSelectedIndex(0);
        CmbGolonganDarah.setSelectedIndex(0);
        CmbJenisKomponen.setSelectedIndex(0);
        CmbLabelDarah.setSelectedIndex(0);
        TBdSblIdentitas.setText(""); TBdSblGoldarPasien.setText(""); TBdSblGoldarDonor.setText("");
        TBdSblSkriningAb.setText(""); TBdSblMayor.setText(""); TBdSblDct.setText(""); TBdSblPetugas.setText("");
        TBdStlIdentitas.setText(""); TBdStlGoldarPasien.setText(""); TBdStlGoldarDonor.setText("");
        TBdStlSkriningAb.setText(""); TBdStlMayor.setText(""); TBdStlDct.setText(""); TBdStlPetugas.setText("");
        TUddSampel.setText(""); TUddGoldarPasien.setText(""); TUddGoldarDonor.setText("");
        TUddSkriningAb.setText(""); TUddMayor.setText(""); TUddDct.setText(""); TUddPetugas.setText("");
        NIP.setText(""); NamaPetugas.setText("");
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        getData();
    }
}
