package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.*;
import widget.*;

/**
 * Form Triase IGD — Permenkes No. 47 Tahun 2018
 * Sistem 4 warna: MERAH / KUNING / HIJAU / HITAM
 * KMK HK.01.07/MENKES/1423/2022 — variabel wajib IGD
 */
public class RMTriaseIGDPermenkes extends JDialog {

    // DB
    private java.sql.Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps; private ResultSet rs;
    private sekuel Sequel = new sekuel(); private validasi Valid = new validasi();

    // Panels
    private panelisi panelAtas, panelBawah, PanelInput;
    private PanelBiasa FormInput;
    private widget.ScrollPane ScrollInput, Scroll;
    private InternalFrame internalFrame1;
    private CekBox ChkInput;

    // Identitas
    private TextBox TNoRw, TNoRM, TPasien;
    private widget.Tanggal DTPWaktuDatang, DTPWaktuTriase;

    // Cara datang
    private ComboBox CbCaraDatang, CbJenisKeluhan;
    private CekBox ChkDOA;
    private widget.Tanggal DTPJamDOA;
    private TextBox TKriteriaDOA;

    // Tanda vital
    private TextBox TTdSistolik, TTdDiastolik, TNadi, TRR, TSpO2, TSuhu;
    private TextBox TGCSE, TGCSV, TGCSM, TGCSTotal;
    private TextBox TSkalaNyeri, TBeratBadan;
    private TextBox TPupilKiri, TPupilKanan, TReflexCahaya, TAkral;

    // ABCDE - Airway
    private CekBox chkAwBebas, chkAwSumbatan, chkAwSnoring, chkAwGargling, chkAwStridor;
    // Breathing
    private CekBox chkBrNormal, chkBrTakipnea, chkBrBradipnea, chkBrApnea, chkBrSpO2Turun;
    // Circulation
    private CekBox chkCiNormal, chkCiTakikardi, chkCiBradikardi, chkCiHipotensi, chkCiSyok;
    // Disability
    private CekBox chkDiSadar, chkDiSomnolen, chkDiSopor, chkDiKoma, chkDiKejang;
    // Exposure
    private CekBox chkExNormal, chkExLuka, chkExEdema, chkExRuam;

    // Keluhan
    private widget.TextArea TKeluhanUtama, TRiwayatPenyakit, TRiwayatAlergi;
    private TextBox TImunisasi;
    private CekBox ChkGelisah, ChkRisikoBunuhDiri;

    // CDS & Kategori
    private widget.Label LblSaranSistem, LblAlasanSaran;
    private JRadioButton RdMerah, RdKuning, RdHijau, RdHitam;
    private ButtonGroup BgKategori;
    private widget.TextArea TOverrideAlasan;
    private widget.Label LblOverrideWarn;

    // Disposisi
    private ComboBox CbAreaPenempatan, CbStatusKeluar;

    // Petugas
    private TextBox KdPetugas, NmPetugas;
    private widget.Button BtnPetugas;
    private DlgCariPetugas dlgPetugas = new DlgCariPetugas(null, false);

    // Table
    private JTable tbData;
    private javax.swing.table.DefaultTableModel tabMode;
    // Retriase log table
    private JTable tbRetriaseLog;
    private javax.swing.table.DefaultTableModel tabRetriaseLog;
    private widget.ScrollPane ScrollRetriaseLog;
    private panelisi PanelRetriaseLog;

    // Buttons
    private widget.Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnRetriase;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private widget.Label LCount;

    // State
    private Long currentId = null;
    private String saranSistem = null;
    private boolean isRetriase = false;
    private Long retriaseId = null;
    private String katLamaRetriase = null;
    private widget.Label LblRetriaseBanner;
    private widget.Label LblAlasanRetriaseLbl;
    private widget.TextArea TAlasanRetriase;
    private javax.swing.JScrollPane scrAlasanRetriase;

    public RMTriaseIGDPermenkes(Frame parent) {
        super(parent, false);
        initComponents();
        isCek();
        setSize(1150, 750);
        setLocationRelativeTo(parent);
    }

    private CekBox createCheckBox(String label) {
        CekBox c = new CekBox(); c.setText(label);
        c.setBackground(Color.WHITE); c.setFont(new Font("Tahoma", Font.PLAIN, 11));
        return c;
    }

    private JRadioButton createRadio(String label, Color fg) {
        JRadioButton r = new JRadioButton(label);
        r.setBackground(Color.WHITE); r.setFont(new Font("Tahoma", Font.BOLD, 12));
        r.setForeground(fg); return r;
    }

    private widget.Label makeLabel(String text, int x, int y, int w, int h) {
        widget.Label l = new widget.Label(); l.setText(text); l.setBounds(x, y, w, h); return l;
    }

    private void addField(String labelText, java.awt.Component field, int lx, int ly, int lw, int fx, int fy, int fw, int fh) {
        FormInput.add(makeLabel(labelText, lx, ly, lw, 20));
        field.setBounds(fx, fy, fw, fh); FormInput.add(field);
    }

    private void initComponents() {
        // ========== TOP PANEL ==========
        panelAtas = new panelisi(); panelAtas.setPreferredSize(new Dimension(44, 33)); panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        widget.Label judul = new widget.Label(); judul.setText(":: TRIASE IGD — PERMENKES 47/2018 ::"); judul.setFont(new Font("Tahoma", Font.BOLD, 13));
        panelAtas.add(judul);

        // ========== FORM INPUT ==========
        FormInput = new PanelBiasa(); FormInput.setLayout(null); FormInput.setBackground(Color.WHITE);
        FormInput.setPreferredSize(new Dimension(1100, 1800));

        int lw = 130, fw = 160, fh = 22, row = 5, gap = 26;

        // --- IDENTITAS ---
        FormInput.add(makeLabel("─── IDENTITAS PASIEN ───", 5, row, 400, 18)); row += 22;
        TNoRw = new TextBox(); TNoRw.setEditable(false);
        addField("No. Rawat", TNoRw, 5, row, lw, 140, row, 200, fh);
        TNoRM = new TextBox(); TNoRM.setEditable(false);
        addField("No. RM", TNoRM, 350, row, 60, 415, row, 140, fh);
        row += gap;
        TPasien = new TextBox(); TPasien.setEditable(false);
        addField("Nama Pasien", TPasien, 5, row, lw, 140, row, 500, fh);
        row += gap;

        // --- WAKTU ---
        DTPWaktuDatang = new widget.Tanggal(); DTPWaktuDatang.setDisplayFormat("dd-MM-yyyy HH:mm");
        addField("Waktu Datang", DTPWaktuDatang, 5, row, lw, 140, row, 180, fh);
        DTPWaktuTriase = new widget.Tanggal(); DTPWaktuTriase.setDisplayFormat("dd-MM-yyyy HH:mm");
        addField("Waktu Triase", DTPWaktuTriase, 340, row, 120, 465, row, 180, fh);
        row += gap;

        // --- CARA DATANG ---
        FormInput.add(makeLabel("─── CARA DATANG ───", 5, row, 400, 18)); row += 22;
        CbCaraDatang = new ComboBox();
        CbCaraDatang.addItem("Sendiri"); CbCaraDatang.addItem("Diantar Keluarga");
        CbCaraDatang.addItem("Ambulans RS"); CbCaraDatang.addItem("Ambulans 118");
        CbCaraDatang.addItem("Polisi"); CbCaraDatang.addItem("Rujukan");
        addField("Cara Datang", CbCaraDatang, 5, row, lw, 140, row, 180, fh);
        CbJenisKeluhan = new ComboBox();
        CbJenisKeluhan.addItem("Non Trauma"); CbJenisKeluhan.addItem("Trauma"); CbJenisKeluhan.addItem("Obstetri");
        addField("Jenis Keluhan", CbJenisKeluhan, 340, row, 120, 465, row, 150, fh);
        row += gap;

        ChkDOA = createCheckBox("DOA (Dead On Arrival)");
        ChkDOA.setBounds(5, row, 175, fh); FormInput.add(ChkDOA);
        DTPJamDOA = new widget.Tanggal(); DTPJamDOA.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        addField("Tgl/Jam DOA", DTPJamDOA, 183, row, 85, 273, row, 165, fh);
        TKriteriaDOA = new TextBox();
        addField("Kriteria DOA", TKriteriaDOA, 443, row, 90, 538, row, 220, fh);
        row += gap + 5;

        // --- TANDA VITAL ---
        FormInput.add(makeLabel("─── TANDA VITAL ───", 5, row, 400, 18)); row += 22;

        TTdSistolik = new TextBox();
        addField("TD Sistolik", TTdSistolik, 5, row, lw, 140, row, 70, fh);
        TTdDiastolik = new TextBox();
        addField("Diastolik", TTdDiastolik, 220, row, 65, 290, row, 70, fh);
        TNadi = new TextBox();
        addField("Nadi (x/m)", TNadi, 375, row, 80, 460, row, 70, fh);
        row += gap;

        TRR = new TextBox();
        addField("RR (x/m)", TRR, 5, row, lw, 140, row, 70, fh);
        TSpO2 = new TextBox();
        addField("SpO2 (%)", TSpO2, 220, row, 65, 290, row, 70, fh);
        TSuhu = new TextBox();
        addField("Suhu (°C)", TSuhu, 375, row, 65, 445, row, 70, fh);
        row += gap;

        TGCSE = new TextBox(); TGCSV = new TextBox(); TGCSM = new TextBox(); TGCSTotal = new TextBox(); TGCSTotal.setEditable(false);
        addField("GCS E", TGCSE, 5, row, lw, 140, row, 50, fh);
        addField("V", TGCSV, 200, row, 18, 222, row, 50, fh);
        addField("M", TGCSM, 280, row, 18, 302, row, 50, fh);
        addField("Total", TGCSTotal, 362, row, 40, 407, row, 50, fh);
        row += gap;

        TSkalaNyeri = new TextBox();
        addField("Skala Nyeri (0-10)", TSkalaNyeri, 5, row, lw, 140, row, 50, fh);
        TBeratBadan = new TextBox();
        addField("BB (kg)", TBeratBadan, 220, row, 65, 290, row, 70, fh);
        row += gap;

        TPupilKiri = new TextBox(); TPupilKanan = new TextBox(); TReflexCahaya = new TextBox(); TAkral = new TextBox();
        addField("Pupil Kiri", TPupilKiri, 5, row, lw, 140, row, 80, fh);
        addField("Pupil Kanan", TPupilKanan, 235, row, 80, 320, row, 80, fh);
        addField("Reflex Cahaya", TReflexCahaya, 415, row, 100, 520, row, 100, fh);
        addField("Akral", TAkral, 635, row, 45, 685, row, 100, fh);
        row += gap + 5;

        // Auto-hitung GCS total
        KeyAdapter gcsListener = new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungGCS(); }
        };
        TGCSE.addKeyListener(gcsListener); TGCSV.addKeyListener(gcsListener); TGCSM.addKeyListener(gcsListener);

        // Auto-CDS saat vital berubah
        KeyAdapter vitalListener = new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updateSaranCDS(); }
        };
        TTdSistolik.addKeyListener(vitalListener); TNadi.addKeyListener(vitalListener);
        TRR.addKeyListener(vitalListener); TSpO2.addKeyListener(vitalListener);
        TGCSE.addKeyListener(vitalListener); TGCSV.addKeyListener(vitalListener); TGCSM.addKeyListener(vitalListener);

        // --- ABCDE ASSESSMENT ---
        FormInput.add(makeLabel("─── ABCDE ASSESSMENT ───", 5, row, 400, 18)); row += 22;

        // Airway
        FormInput.add(makeLabel("Airway:", 5, row, 60, fh));
        chkAwBebas = createCheckBox("Bebas"); chkAwBebas.setBounds(70, row, 90, fh); FormInput.add(chkAwBebas);
        chkAwSumbatan = createCheckBox("Sumbatan"); chkAwSumbatan.setBounds(165, row, 90, fh); FormInput.add(chkAwSumbatan);
        chkAwSnoring = createCheckBox("Snoring"); chkAwSnoring.setBounds(260, row, 80, fh); FormInput.add(chkAwSnoring);
        chkAwGargling = createCheckBox("Gargling"); chkAwGargling.setBounds(345, row, 80, fh); FormInput.add(chkAwGargling);
        chkAwStridor = createCheckBox("Stridor"); chkAwStridor.setBounds(430, row, 80, fh); FormInput.add(chkAwStridor);
        row += gap;

        // Breathing
        FormInput.add(makeLabel("Breathing:", 5, row, 70, fh));
        chkBrNormal = createCheckBox("Normal"); chkBrNormal.setBounds(80, row, 80, fh); FormInput.add(chkBrNormal);
        chkBrTakipnea = createCheckBox("Takipnea"); chkBrTakipnea.setBounds(165, row, 90, fh); FormInput.add(chkBrTakipnea);
        chkBrBradipnea = createCheckBox("Bradipnea"); chkBrBradipnea.setBounds(260, row, 90, fh); FormInput.add(chkBrBradipnea);
        chkBrApnea = createCheckBox("Apnea"); chkBrApnea.setBounds(355, row, 75, fh); FormInput.add(chkBrApnea);
        chkBrSpO2Turun = createCheckBox("SpO2 Turun"); chkBrSpO2Turun.setBounds(435, row, 100, fh); FormInput.add(chkBrSpO2Turun);
        row += gap;

        // Circulation
        FormInput.add(makeLabel("Circulation:", 5, row, 75, fh));
        chkCiNormal = createCheckBox("Normal"); chkCiNormal.setBounds(85, row, 80, fh); FormInput.add(chkCiNormal);
        chkCiTakikardi = createCheckBox("Takikardi"); chkCiTakikardi.setBounds(170, row, 85, fh); FormInput.add(chkCiTakikardi);
        chkCiBradikardi = createCheckBox("Bradikardi"); chkCiBradikardi.setBounds(260, row, 90, fh); FormInput.add(chkCiBradikardi);
        chkCiHipotensi = createCheckBox("Hipotensi"); chkCiHipotensi.setBounds(355, row, 85, fh); FormInput.add(chkCiHipotensi);
        chkCiSyok = createCheckBox("Syok"); chkCiSyok.setBounds(445, row, 65, fh); FormInput.add(chkCiSyok);
        row += gap;

        // Disability
        FormInput.add(makeLabel("Disability:", 5, row, 68, fh));
        chkDiSadar = createCheckBox("Sadar"); chkDiSadar.setBounds(78, row, 75, fh); FormInput.add(chkDiSadar);
        chkDiSomnolen = createCheckBox("Somnolen"); chkDiSomnolen.setBounds(158, row, 90, fh); FormInput.add(chkDiSomnolen);
        chkDiSopor = createCheckBox("Sopor"); chkDiSopor.setBounds(253, row, 70, fh); FormInput.add(chkDiSopor);
        chkDiKoma = createCheckBox("Koma"); chkDiKoma.setBounds(328, row, 65, fh); FormInput.add(chkDiKoma);
        chkDiKejang = createCheckBox("Kejang"); chkDiKejang.setBounds(398, row, 75, fh); FormInput.add(chkDiKejang);
        row += gap;

        // Exposure
        FormInput.add(makeLabel("Exposure:", 5, row, 65, fh));
        chkExNormal = createCheckBox("Normal"); chkExNormal.setBounds(75, row, 80, fh); FormInput.add(chkExNormal);
        chkExLuka = createCheckBox("Luka/Lecet"); chkExLuka.setBounds(160, row, 95, fh); FormInput.add(chkExLuka);
        chkExEdema = createCheckBox("Edema"); chkExEdema.setBounds(260, row, 75, fh); FormInput.add(chkExEdema);
        chkExRuam = createCheckBox("Ruam/Memar"); chkExRuam.setBounds(340, row, 110, fh); FormInput.add(chkExRuam);
        row += gap + 5;

        // --- KELUHAN & RIWAYAT ---
        FormInput.add(makeLabel("─── KELUHAN & RIWAYAT ───", 5, row, 400, 18)); row += 22;

        TKeluhanUtama = new widget.TextArea(); TKeluhanUtama.setLineWrap(true);
        addField("Keluhan Utama", new JScrollPane(TKeluhanUtama), 5, row, lw, 140, row, 500, 50);
        row += 58;

        TRiwayatPenyakit = new widget.TextArea(); TRiwayatPenyakit.setLineWrap(true);
        addField("Riwayat Penyakit", new JScrollPane(TRiwayatPenyakit), 5, row, lw, 140, row, 500, 50);
        row += 58;

        TRiwayatAlergi = new widget.TextArea(); TRiwayatAlergi.setLineWrap(true);
        addField("Riwayat Alergi", new JScrollPane(TRiwayatAlergi), 5, row, lw, 140, row, 500, 40);
        row += 48;

        TImunisasi = new TextBox();
        addField("Imunisasi", TImunisasi, 5, row, lw, 140, row, 300, fh);
        row += gap;

        ChkGelisah = createCheckBox("Gelisah"); ChkGelisah.setBounds(140, row, 90, fh); FormInput.add(ChkGelisah);
        ChkRisikoBunuhDiri = createCheckBox("Risiko Bunuh Diri"); ChkRisikoBunuhDiri.setBounds(240, row, 160, fh); FormInput.add(ChkRisikoBunuhDiri);
        row += gap + 5;

        // --- CDS & KATEGORI TRIASE ---
        FormInput.add(makeLabel("─── KATEGORI TRIASE (Permenkes 47/2018) ───", 5, row, 500, 18)); row += 22;

        // Saran sistem (CDS)
        FormInput.add(makeLabel("Saran Sistem:", 5, row, 90, fh));
        LblSaranSistem = new widget.Label(); LblSaranSistem.setText("—");
        LblSaranSistem.setFont(new Font("Tahoma", Font.BOLD, 13));
        LblSaranSistem.setBounds(100, row, 150, fh); FormInput.add(LblSaranSistem);
        LblAlasanSaran = new widget.Label(); LblAlasanSaran.setText("");
        LblAlasanSaran.setFont(new Font("Tahoma", Font.PLAIN, 10));
        LblAlasanSaran.setBounds(260, row, 400, fh); FormInput.add(LblAlasanSaran);
        row += gap;

        // Radio kategori
        FormInput.add(makeLabel("Kategori:", 5, row, 65, fh));
        BgKategori = new ButtonGroup();
        RdMerah = createRadio("■ MERAH (P1/Resusitasi)", new Color(180, 0, 0));
        RdMerah.setBounds(75, row, 200, fh); FormInput.add(RdMerah); BgKategori.add(RdMerah);
        RdKuning = createRadio("■ KUNING (P2/Tindakan)", new Color(160, 120, 0));
        RdKuning.setBounds(280, row, 200, fh); FormInput.add(RdKuning); BgKategori.add(RdKuning);
        RdHijau = createRadio("■ HIJAU (P3/Observasi)", new Color(0, 120, 0));
        RdHijau.setBounds(485, row, 195, fh); FormInput.add(RdHijau); BgKategori.add(RdHijau);
        RdHitam = createRadio("■ HITAM (P0/Fatal)", new Color(30, 30, 30));
        RdHitam.setBounds(685, row, 170, fh); FormInput.add(RdHitam); BgKategori.add(RdHitam);
        RdHijau.setSelected(true);
        row += gap;

        // Override alasan (muncul jika berbeda dari saran)
        LblOverrideWarn = new widget.Label();
        LblOverrideWarn.setText("⚠ Kategori berbeda dari saran sistem — wajib isi alasan override:");
        LblOverrideWarn.setFont(new Font("Tahoma", Font.BOLD, 11));
        LblOverrideWarn.setForeground(new Color(180, 0, 0));
        LblOverrideWarn.setBounds(5, row, 600, fh); LblOverrideWarn.setVisible(false);
        FormInput.add(LblOverrideWarn); row += gap;

        TOverrideAlasan = new widget.TextArea(); TOverrideAlasan.setLineWrap(true);
        TOverrideAlasan.setBackground(new Color(255, 248, 220));
        final JScrollPane scrOverride = new JScrollPane(TOverrideAlasan);
        scrOverride.setBounds(5, row, 650, 45); scrOverride.setVisible(false);
        FormInput.add(scrOverride); row += 53;

        // Listener untuk deteksi override + auto-suggest area penempatan (Permenkes 47/2018 Pasal 4d)
        ActionListener rdListener = e -> {
            boolean override = saranSistem != null && !saranSistem.equals(getKategoriTriase());
            LblOverrideWarn.setVisible(override); scrOverride.setVisible(override);
            updateKategoriColor();
            // auto-suggest area sesuai Permenkes 47/2018
            switch (getKategoriTriase()) {
                case "MERAH":  CbAreaPenempatan.setSelectedItem("Ruang Resusitasi"); break;
                case "KUNING": CbAreaPenempatan.setSelectedItem("Ruang Tindakan");   break;
                case "HIJAU":  CbAreaPenempatan.setSelectedItem("Ruang Observasi");  break;
                case "HITAM":  CbAreaPenempatan.setSelectedItem("Kamar Jenazah");    break;
            }
        };
        RdMerah.addActionListener(rdListener); RdKuning.addActionListener(rdListener);
        RdHijau.addActionListener(rdListener); RdHitam.addActionListener(rdListener);

        // --- DISPOSISI ---
        FormInput.add(makeLabel("─── DISPOSISI ───", 5, row, 400, 18)); row += 22;

        CbAreaPenempatan = new ComboBox();
        CbAreaPenempatan.addItem(""); CbAreaPenempatan.addItem("Ruang Resusitasi");
        CbAreaPenempatan.addItem("Ruang Tindakan"); CbAreaPenempatan.addItem("Ruang Observasi");
        CbAreaPenempatan.addItem("Rawat Jalan"); CbAreaPenempatan.addItem("Kamar Jenazah");
        addField("Area Penempatan", CbAreaPenempatan, 5, row, lw, 140, row, 200, fh);

        CbStatusKeluar = new ComboBox();
        CbStatusKeluar.addItem(""); CbStatusKeluar.addItem("Rawat Inap");
        CbStatusKeluar.addItem("Rawat Jalan"); CbStatusKeluar.addItem("Rujuk");
        CbStatusKeluar.addItem("Pulang APS"); CbStatusKeluar.addItem("Meninggal"); CbStatusKeluar.addItem("DOA");
        addField("Status Keluar", CbStatusKeluar, 360, row, 100, 465, row, 160, fh);
        row += gap + 5;

        // --- PETUGAS ---
        FormInput.add(makeLabel("─── PETUGAS ───", 5, row, 400, 18)); row += 22;

        KdPetugas = new TextBox();
        addField("NIP Petugas", KdPetugas, 5, row, lw, 140, row, 130, fh);
        BtnPetugas = new widget.Button();
        java.net.URL urlClip = getClass().getResource("/picture/011.png");
        if (urlClip != null) BtnPetugas.setIcon(new javax.swing.ImageIcon(urlClip));
        BtnPetugas.setBounds(275, row, 28, fh); FormInput.add(BtnPetugas);
        NmPetugas = new TextBox(); NmPetugas.setEditable(false);
        addField("Nama Petugas", NmPetugas, 308, row, 100, 413, row, 250, fh);
        KdPetugas.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String nm = Sequel.cariIsi("select nama from petugas where nip=?", KdPetugas.getText().trim());
                NmPetugas.setText(nm);
            }
        });
        BtnPetugas.addActionListener(e -> {
            dlgPetugas.setSize(600, 400);
            dlgPetugas.setLocationRelativeTo(BtnPetugas);
            dlgPetugas.setVisible(true);
            if (dlgPetugas.getTable().getSelectedRow() != -1) {
                KdPetugas.setText(dlgPetugas.getTable().getValueAt(dlgPetugas.getTable().getSelectedRow(), 0).toString());
                NmPetugas.setText(dlgPetugas.getTable().getValueAt(dlgPetugas.getTable().getSelectedRow(), 1).toString());
            }
        });

        // --- RE-TRIASE BANNER (hidden by default) ---
        row += gap + 8;
        LblRetriaseBanner = new widget.Label();
        LblRetriaseBanner.setText("  \u26a0  MODE RE-TRIASE AKTIF \u2014 Ubah data sesuai kondisi terbaru pasien, lalu klik Simpan Re-Triase");
        LblRetriaseBanner.setFont(new Font("Tahoma", Font.BOLD, 12));
        LblRetriaseBanner.setOpaque(true);
        LblRetriaseBanner.setBackground(new Color(180, 0, 0));
        LblRetriaseBanner.setForeground(Color.WHITE);
        LblRetriaseBanner.setBounds(5, row, 750, 26);
        LblRetriaseBanner.setVisible(false);
        FormInput.add(LblRetriaseBanner); row += 30;

        LblAlasanRetriaseLbl = new widget.Label(); LblAlasanRetriaseLbl.setText("Alasan Re-Triase *");
        LblAlasanRetriaseLbl.setBounds(5, row, 135, 20); LblAlasanRetriaseLbl.setVisible(false);
        FormInput.add(LblAlasanRetriaseLbl);
        TAlasanRetriase = new widget.TextArea(); TAlasanRetriase.setLineWrap(true);
        TAlasanRetriase.setBackground(new Color(255, 245, 245));
        scrAlasanRetriase = new javax.swing.JScrollPane(TAlasanRetriase);
        scrAlasanRetriase.setBounds(140, row, 570, 45); scrAlasanRetriase.setVisible(false);
        FormInput.add(scrAlasanRetriase);

        // === SCROLL FORM ===
        ScrollInput = new widget.ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.getVerticalScrollBar().setUnitIncrement(16);
        PanelInput = new panelisi(); PanelInput.setLayout(new BorderLayout());
        PanelInput.setPreferredSize(new Dimension(1100, 460));
        ChkInput = new CekBox(); ChkInput.setText("Input Data"); ChkInput.setSelected(true);
        ScrollInput.setPreferredSize(new Dimension(1100, 430));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // === TABLE ===
        tabMode = new javax.swing.table.DefaultTableModel(new String[]{"ID","No.Rawat","Nama Pasien","Waktu Triase","Kategori","Keluhan","Petugas"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new JTable(tabMode); tbData.setRowHeight(20); tbData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbData.getColumnModel().getColumn(0).setPreferredWidth(0); tbData.getColumnModel().getColumn(0).setMinWidth(0); tbData.getColumnModel().getColumn(0).setMaxWidth(0);
        tbData.getColumnModel().getColumn(1).setPreferredWidth(100);
        tbData.getColumnModel().getColumn(2).setPreferredWidth(160);
        tbData.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbData.getColumnModel().getColumn(4).setPreferredWidth(80);
        tbData.getColumnModel().getColumn(5).setPreferredWidth(220);
        tbData.getColumnModel().getColumn(6).setPreferredWidth(120);
        tbData.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) getData(); });

        // Right-click popup menu on table
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem miCetak = new JMenuItem("Cetak Laporan");
        miCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        miCetak.addActionListener(e -> cetak());
        JMenuItem miHapus = new JMenuItem("Hapus Data");
        miHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        miHapus.addActionListener(e -> hapus());
        JMenuItem miRetriase = new JMenuItem("Re-Triase");
        miRetriase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        miRetriase.addActionListener(e -> retriase());
        popupMenu.add(miCetak); popupMenu.addSeparator(); popupMenu.add(miRetriase); popupMenu.addSeparator(); popupMenu.add(miHapus);
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) { checkPopup(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { checkPopup(e); }
            private void checkPopup(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tbData.rowAtPoint(e.getPoint());
                    if (row >= 0) tbData.setRowSelectionInterval(row, row);
                    popupMenu.show(tbData, e.getX(), e.getY());
                }
            }
        });

        Scroll = new widget.ScrollPane(); Scroll.setViewportView(tbData);

        // === BOTTOM PANEL ===
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54)); panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new widget.Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal  = new widget.Button(); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus  = new widget.Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit   = new widget.Button(); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnRetriase = new widget.Button(); BtnRetriase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); BtnRetriase.setText("Re-Triase"); BtnRetriase.setPreferredSize(new Dimension(100, 30));
        BtnPrint  = new widget.Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new widget.Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));

        widget.Label lc = new widget.Label(); lc.setText("Tanggal :"); lc.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        widget.Label sd = new widget.Label(); sd.setText("s.d."); sd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        widget.Label lk = new widget.Label(); lk.setText("Cari :"); lk.setPreferredSize(new Dimension(35, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new widget.Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll  = new widget.Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount  = new widget.Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnRetriase); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lc); panelBawah.add(DTPCari1); panelBawah.add(sd); panelBawah.add(DTPCari2);
        panelBawah.add(lk); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        // === ACTIONS ===
        ChkInput.addActionListener(e -> isForm());
        BtnKeluar.addActionListener(e -> dispose());
        BtnBatal.addActionListener(e -> emptTeks());
        BtnCari.addActionListener(e -> tampil());
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });

        BtnSimpan.addActionListener(e -> simpan());
        BtnHapus.addActionListener(e -> hapus());
        BtnEdit.addActionListener(e -> edit());
        BtnRetriase.addActionListener(e -> retriase());
        BtnPrint.addActionListener(e -> cetak());

        // === RETRIASE LOG TABLE ===
        tabRetriaseLog = new javax.swing.table.DefaultTableModel(
            new String[]{"Waktu Re-Triase","Kategori Lama","Kategori Baru","Alasan","Petugas"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbRetriaseLog = new JTable(tabRetriaseLog);
        tbRetriaseLog.setRowHeight(18);
        tbRetriaseLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRetriaseLog.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbRetriaseLog.getColumnModel().getColumn(1).setPreferredWidth(90);
        tbRetriaseLog.getColumnModel().getColumn(2).setPreferredWidth(90);
        tbRetriaseLog.getColumnModel().getColumn(3).setPreferredWidth(380);
        tbRetriaseLog.getColumnModel().getColumn(4).setPreferredWidth(130);
        // Color kategori baru column renderer
        tbRetriaseLog.getColumnModel().getColumn(1).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) setForeground(getWarnaKategori(v != null ? v.toString() : ""));
                return this;
            }
        });
        tbRetriaseLog.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) setForeground(getWarnaKategori(v != null ? v.toString() : ""));
                return this;
            }
        });
        ScrollRetriaseLog = new widget.ScrollPane(); ScrollRetriaseLog.setViewportView(tbRetriaseLog);
        PanelRetriaseLog = new panelisi(); PanelRetriaseLog.setLayout(new BorderLayout());
        PanelRetriaseLog.setPreferredSize(new Dimension(1100, 100));
        widget.Label lblLogTitle = new widget.Label();
        lblLogTitle.setText("  Riwayat Re-Triase:");
        lblLogTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblLogTitle.setOpaque(true); lblLogTitle.setBackground(new Color(220, 220, 220));
        PanelRetriaseLog.add(lblLogTitle, BorderLayout.NORTH);
        PanelRetriaseLog.add(ScrollRetriaseLog, BorderLayout.CENTER);

        // === ASSEMBLY ===
        internalFrame1 = new InternalFrame(); internalFrame1.setLayout(new BorderLayout()); internalFrame1.setBackground(Color.WHITE);
        InternalFrame ifd = new InternalFrame(); ifd.setLayout(new BorderLayout()); ifd.setBackground(Color.WHITE);
        ifd.add(PanelInput, BorderLayout.NORTH); ifd.add(Scroll, BorderLayout.CENTER);
        ifd.add(PanelRetriaseLog, BorderLayout.SOUTH);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(ifd, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);
    }

    // ========== CDS ENGINE ==========
    private void hitungGCS() {
        try {
            int e = Integer.parseInt(TGCSE.getText().trim());
            int v = Integer.parseInt(TGCSV.getText().trim());
            int m = Integer.parseInt(TGCSM.getText().trim());
            TGCSTotal.setText(String.valueOf(e + v + m));
        } catch (Exception ex) { TGCSTotal.setText(""); }
    }

    private void updateSaranCDS() {
        try {
            int sis = parseIntSafe(TTdSistolik.getText());
            int nadi = parseIntSafe(TNadi.getText());
            int rr = parseIntSafe(TRR.getText());
            int spo2 = parseIntSafe(TSpO2.getText());
            int gcsTot = parseIntSafe(TGCSTotal.getText());

            String saran; String alasan;
            if ((gcsTot > 0 && gcsTot <= 8) || (sis > 0 && sis < 80) || (spo2 > 0 && spo2 < 80)
                    || (rr > 0 && (rr < 10 || rr > 30)) || (nadi > 0 && (nadi > 150 || nadi < 40))) {
                saran = "MERAH";
                alasan = buildAlasanCDS(gcsTot, sis, spo2, rr, nadi, saran);
            } else if ((gcsTot >= 9 && gcsTot <= 13) || (sis > 0 && (sis >= 180 || (sis >= 80 && sis <= 89)))
                    || (spo2 > 0 && spo2 >= 80 && spo2 <= 89) || (rr > 0 && (rr >= 25 && rr <= 30))
                    || (nadi > 0 && (nadi >= 100 && nadi <= 150))) {
                saran = "KUNING";
                alasan = buildAlasanCDS(gcsTot, sis, spo2, rr, nadi, saran);
            } else if (gcsTot == 14 || gcsTot == 15 || sis >= 90) {
                saran = "HIJAU";
                alasan = "Kondisi stabil";
            } else {
                saran = null; alasan = "";
            }

            saranSistem = saran;
            if (saran != null) {
                LblSaranSistem.setText(saran);
                LblSaranSistem.setForeground(getWarnaKategori(saran));
                LblAlasanSaran.setText(alasan);
            } else {
                LblSaranSistem.setText("—"); LblAlasanSaran.setText("");
                LblSaranSistem.setForeground(Color.DARK_GRAY);
            }
        } catch (Exception ex) { /* ignore */ }
    }

    private String buildAlasanCDS(int gcs, int sis, int spo2, int rr, int nadi, String kat) {
        StringBuilder sb = new StringBuilder();
        if ("MERAH".equals(kat)) {
            if (gcs > 0 && gcs <= 8) sb.append("GCS=").append(gcs).append(" ");
            if (sis > 0 && sis < 80) sb.append("TD=").append(sis).append(" ");
            if (spo2 > 0 && spo2 < 80) sb.append("SpO2=").append(spo2).append("% ");
            if (rr > 0 && (rr < 10 || rr > 30)) sb.append("RR=").append(rr).append(" ");
            if (nadi > 0 && (nadi > 150 || nadi < 40)) sb.append("Nadi=").append(nadi).append(" ");
        } else {
            if (gcs >= 9 && gcs <= 13) sb.append("GCS=").append(gcs).append(" ");
            if (sis >= 180 || (sis >= 80 && sis <= 89)) sb.append("TD=").append(sis).append(" ");
            if (spo2 >= 80 && spo2 <= 89) sb.append("SpO2=").append(spo2).append("% ");
            if (rr >= 25 && rr <= 30) sb.append("RR=").append(rr).append(" ");
            if (nadi >= 100 && nadi <= 150) sb.append("Nadi=").append(nadi).append(" ");
        }
        return sb.toString().trim();
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private Color getWarnaKategori(String k) {
        if (k == null) return Color.DARK_GRAY;
        switch (k) {
            case "MERAH": return new Color(180, 0, 0);
            case "KUNING": return new Color(160, 120, 0);
            case "HIJAU": return new Color(0, 120, 0);
            case "HITAM": return new Color(30, 30, 30);
            default: return Color.DARK_GRAY;
        }
    }

    private void updateKategoriColor() {
        String k = getKategoriTriase();
        Color warna = getWarnaKategori(k);
        RdMerah.setForeground("MERAH".equals(k) ? new Color(180,0,0) : new Color(180,0,0));
        // warna sudah diset di createRadio, tidak perlu ubah
    }

    // ========== HELPERS ==========
    private String getKategoriTriase() {
        if (RdMerah.isSelected()) return "MERAH";
        if (RdKuning.isSelected()) return "KUNING";
        if (RdHijau.isSelected()) return "HIJAU";
        if (RdHitam.isSelected()) return "HITAM";
        return "HIJAU";
    }

    private void setKategoriTriase(String k) {
        if (k == null) { RdHijau.setSelected(true); return; }
        switch (k) {
            case "MERAH": RdMerah.setSelected(true); break;
            case "KUNING": RdKuning.setSelected(true); break;
            case "HIJAU": RdHijau.setSelected(true); break;
            case "HITAM": RdHitam.setSelected(true); break;
            default: RdHijau.setSelected(true);
        }
    }

    private boolean b(ResultSet rset, String col) throws SQLException {
        return rset.getInt(col) == 1;
    }

    private int toInt(boolean b) { return b ? 1 : 0; }

    private void isForm() {
        ChkInput.setVisible(false);
        boolean show = ChkInput.isSelected();
        PanelInput.setPreferredSize(new Dimension(1100, show ? 460 : 20));
        ScrollInput.setVisible(show);
        ChkInput.setVisible(true);
    }

    // ========== SIMPAN ==========
    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (TKeluhanUtama.getText().trim().isEmpty()) { Valid.textKosong(TKeluhanUtama, "Keluhan Utama"); return; }
        if (KdPetugas.getText().trim().isEmpty()) { Valid.textKosong(KdPetugas, "NIP Petugas"); return; }

        boolean overrideFlag = saranSistem != null && !saranSistem.equals(getKategoriTriase());
        if (overrideFlag && TOverrideAlasan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kategori berbeda dari saran sistem.\nWajib mengisi alasan override!", "Override Wajib", JOptionPane.WARNING_MESSAGE);
            TOverrideAlasan.requestFocus(); return;
        }
        if (isRetriase && TAlasanRetriase.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Alasan re-triase wajib diisi!", "Re-Triase", JOptionPane.WARNING_MESSAGE);
            TAlasanRetriase.requestFocus(); return;
        }

        String noRawat = TNoRw.getText().trim();
        String waktuDatang = Valid.SetTgl(DTPWaktuDatang.getSelectedItem() + "");
        String waktuTriase = Valid.SetTgl(DTPWaktuTriase.getSelectedItem() + "");
        String kategori = getKategoriTriase();
        String nip = KdPetugas.getText().trim();
        // Konversi Tgl/Jam DOA dari "dd-MM-yyyy HH:mm:ss" ke "yyyy-MM-dd HH:mm:ss"
        String doaJamMysql = null;
        if (ChkDOA.isSelected()) {
            String s = DTPJamDOA.getSelectedItem().toString().trim();
            if (s.length() >= 19) doaJamMysql = Valid.SetTgl(s.substring(0, 10)) + " " + s.substring(11);
        }

        if (isRetriase) {
            // ===== RE-TRIASE: UPDATE triase_igd + INSERT retriase_log =====
            try {
                ps = koneksi.prepareStatement(
                    "UPDATE triase_igd SET waktu_triase=?,cara_datang=?,jenis_keluhan=?,doa_flag=?,doa_jam=?,doa_kriteria=?," +
                    "td_sistolik=?,td_diastolik=?,nadi=?,rr=?,spo2=?,suhu=?," +
                    "gcs_eye=?,gcs_verbal=?,gcs_motor=?,gcs_total=?,skala_nyeri=?,berat_badan=?," +
                    "pupil_kiri=?,pupil_kanan=?,reflex_cahaya=?,akral=?," +
                    "airway_bebas=?,airway_sumbatan=?,airway_snoring=?,airway_gargling=?,airway_stridor=?," +
                    "breathing_normal=?,breathing_takipnea=?,breathing_bradipnea=?,breathing_apnea=?,breathing_spo2_turun=?," +
                    "circulation_normal=?,circulation_takikardi=?,circulation_bradikardi=?,circulation_hipotensi=?,circulation_syok=?," +
                    "disability_sadar=?,disability_somnolen=?,disability_sopor=?,disability_koma=?,disability_kejang=?," +
                    "exposure_normal=?,exposure_luka=?,exposure_edema=?,exposure_ruam=?," +
                    "keluhan_utama=?,riwayat_penyakit=?,riwayat_alergi=?,imunisasi=?,gelisah=?,risiko_bunuh_diri=?," +
                    "saran_sistem=?,alasan_saran=?,kategori_triase=?,override_flag=?,override_alasan=?," +
                    "area_penempatan=?,status_keluar=?,nip_petugas=?,updated_at=NOW() WHERE id=?");
                int i = 1;
                ps.setString(i++, waktuTriase + " 00:00:00");
                ps.setString(i++, CbCaraDatang.getSelectedItem().toString());
                ps.setString(i++, CbJenisKeluhan.getSelectedItem().toString());
                ps.setInt(i++, ChkDOA.isSelected() ? 1 : 0);
                ps.setString(i++, doaJamMysql);
                ps.setString(i++, TKriteriaDOA.getText().trim().isEmpty() ? null : TKriteriaDOA.getText().trim());
                ps.setObject(i++, parseInt(TTdSistolik)); ps.setObject(i++, parseInt(TTdDiastolik));
                ps.setObject(i++, parseInt(TNadi)); ps.setObject(i++, parseInt(TRR));
                ps.setObject(i++, parseInt(TSpO2)); ps.setObject(i++, parseDecimal(TSuhu));
                ps.setObject(i++, parseInt(TGCSE)); ps.setObject(i++, parseInt(TGCSV));
                ps.setObject(i++, parseInt(TGCSM)); ps.setObject(i++, parseInt(TGCSTotal));
                ps.setObject(i++, parseInt(TSkalaNyeri)); ps.setObject(i++, parseDecimal(TBeratBadan));
                ps.setString(i++, TPupilKiri.getText().trim()); ps.setString(i++, TPupilKanan.getText().trim());
                ps.setString(i++, TReflexCahaya.getText().trim()); ps.setString(i++, TAkral.getText().trim());
                ps.setInt(i++, toInt(chkAwBebas.isSelected())); ps.setInt(i++, toInt(chkAwSumbatan.isSelected()));
                ps.setInt(i++, toInt(chkAwSnoring.isSelected())); ps.setInt(i++, toInt(chkAwGargling.isSelected()));
                ps.setInt(i++, toInt(chkAwStridor.isSelected()));
                ps.setInt(i++, toInt(chkBrNormal.isSelected())); ps.setInt(i++, toInt(chkBrTakipnea.isSelected()));
                ps.setInt(i++, toInt(chkBrBradipnea.isSelected())); ps.setInt(i++, toInt(chkBrApnea.isSelected()));
                ps.setInt(i++, toInt(chkBrSpO2Turun.isSelected()));
                ps.setInt(i++, toInt(chkCiNormal.isSelected())); ps.setInt(i++, toInt(chkCiTakikardi.isSelected()));
                ps.setInt(i++, toInt(chkCiBradikardi.isSelected())); ps.setInt(i++, toInt(chkCiHipotensi.isSelected()));
                ps.setInt(i++, toInt(chkCiSyok.isSelected()));
                ps.setInt(i++, toInt(chkDiSadar.isSelected())); ps.setInt(i++, toInt(chkDiSomnolen.isSelected()));
                ps.setInt(i++, toInt(chkDiSopor.isSelected())); ps.setInt(i++, toInt(chkDiKoma.isSelected()));
                ps.setInt(i++, toInt(chkDiKejang.isSelected()));
                ps.setInt(i++, toInt(chkExNormal.isSelected())); ps.setInt(i++, toInt(chkExLuka.isSelected()));
                ps.setInt(i++, toInt(chkExEdema.isSelected())); ps.setInt(i++, toInt(chkExRuam.isSelected()));
                ps.setString(i++, TKeluhanUtama.getText().trim());
                ps.setString(i++, TRiwayatPenyakit.getText().trim().isEmpty() ? null : TRiwayatPenyakit.getText().trim());
                ps.setString(i++, TRiwayatAlergi.getText().trim().isEmpty() ? null : TRiwayatAlergi.getText().trim());
                ps.setString(i++, TImunisasi.getText().trim().isEmpty() ? null : TImunisasi.getText().trim());
                ps.setInt(i++, toInt(ChkGelisah.isSelected())); ps.setInt(i++, toInt(ChkRisikoBunuhDiri.isSelected()));
                ps.setString(i++, saranSistem);
                ps.setString(i++, LblAlasanSaran.getText().trim().isEmpty() ? null : LblAlasanSaran.getText().trim());
                ps.setString(i++, kategori);
                ps.setInt(i++, overrideFlag ? 1 : 0);
                ps.setString(i++, overrideFlag ? TOverrideAlasan.getText().trim() : null);
                String area = CbAreaPenempatan.getSelectedItem().toString();
                ps.setString(i++, area.isEmpty() ? null : area);
                String statusKeluar = CbStatusKeluar.getSelectedItem().toString();
                ps.setString(i++, statusKeluar.isEmpty() ? null : statusKeluar);
                ps.setString(i++, nip);
                ps.setLong(i++, retriaseId);
                ps.executeUpdate(); ps.close(); ps = null;

                // INSERT retriase_log
                ps = koneksi.prepareStatement(
                    "INSERT INTO retriase_log (triase_id,no_rawat,kategori_lama,kategori_baru,alasan,nip_petugas) VALUES (?,?,?,?,?,?)");
                ps.setLong(1, retriaseId); ps.setString(2, noRawat);
                ps.setString(3, katLamaRetriase); ps.setString(4, kategori);
                ps.setString(5, TAlasanRetriase.getText().trim()); ps.setString(6, nip);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Re-triase berhasil disimpan. Riwayat tersimpan di log.");
                tampil(); emptTeks();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Gagal re-triase: " + ex.getMessage());
                System.out.println("Error re-triase: " + ex);
            } finally { try { if (ps != null) ps.close(); } catch (Exception ex) {} }

        } else {
            // ===== TRIASE BARU: INSERT =====
            try {
                ps = koneksi.prepareStatement(
                    "INSERT INTO triase_igd (no_rawat,waktu_datang,waktu_triase," +
                    "cara_datang,jenis_keluhan,doa_flag,doa_jam,doa_kriteria," +
                    "td_sistolik,td_diastolik,nadi,rr,spo2,suhu," +
                    "gcs_eye,gcs_verbal,gcs_motor,gcs_total,skala_nyeri,berat_badan," +
                    "pupil_kiri,pupil_kanan,reflex_cahaya,akral," +
                    "airway_bebas,airway_sumbatan,airway_snoring,airway_gargling,airway_stridor," +
                    "breathing_normal,breathing_takipnea,breathing_bradipnea,breathing_apnea,breathing_spo2_turun," +
                    "circulation_normal,circulation_takikardi,circulation_bradikardi,circulation_hipotensi,circulation_syok," +
                    "disability_sadar,disability_somnolen,disability_sopor,disability_koma,disability_kejang," +
                    "exposure_normal,exposure_luka,exposure_edema,exposure_ruam," +
                    "keluhan_utama,riwayat_penyakit,riwayat_alergi,imunisasi,gelisah,risiko_bunuh_diri," +
                    "saran_sistem,alasan_saran,kategori_triase,override_flag,override_alasan," +
                    "area_penempatan,status_keluar,nip_petugas) VALUES (" +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                int i = 1;
                ps.setString(i++, noRawat);
                ps.setString(i++, waktuDatang + " 00:00:00");
                ps.setString(i++, waktuTriase + " 00:00:00");
                ps.setString(i++, CbCaraDatang.getSelectedItem().toString());
                ps.setString(i++, CbJenisKeluhan.getSelectedItem().toString());
                ps.setInt(i++, ChkDOA.isSelected() ? 1 : 0);
                ps.setString(i++, doaJamMysql);
                ps.setString(i++, TKriteriaDOA.getText().trim().isEmpty() ? null : TKriteriaDOA.getText().trim());
                ps.setObject(i++, parseInt(TTdSistolik)); ps.setObject(i++, parseInt(TTdDiastolik));
                ps.setObject(i++, parseInt(TNadi)); ps.setObject(i++, parseInt(TRR));
                ps.setObject(i++, parseInt(TSpO2)); ps.setObject(i++, parseDecimal(TSuhu));
                ps.setObject(i++, parseInt(TGCSE)); ps.setObject(i++, parseInt(TGCSV));
                ps.setObject(i++, parseInt(TGCSM)); ps.setObject(i++, parseInt(TGCSTotal));
                ps.setObject(i++, parseInt(TSkalaNyeri)); ps.setObject(i++, parseDecimal(TBeratBadan));
                ps.setString(i++, TPupilKiri.getText().trim()); ps.setString(i++, TPupilKanan.getText().trim());
                ps.setString(i++, TReflexCahaya.getText().trim()); ps.setString(i++, TAkral.getText().trim());
                ps.setInt(i++, toInt(chkAwBebas.isSelected())); ps.setInt(i++, toInt(chkAwSumbatan.isSelected()));
                ps.setInt(i++, toInt(chkAwSnoring.isSelected())); ps.setInt(i++, toInt(chkAwGargling.isSelected()));
                ps.setInt(i++, toInt(chkAwStridor.isSelected()));
                ps.setInt(i++, toInt(chkBrNormal.isSelected())); ps.setInt(i++, toInt(chkBrTakipnea.isSelected()));
                ps.setInt(i++, toInt(chkBrBradipnea.isSelected())); ps.setInt(i++, toInt(chkBrApnea.isSelected()));
                ps.setInt(i++, toInt(chkBrSpO2Turun.isSelected()));
                ps.setInt(i++, toInt(chkCiNormal.isSelected())); ps.setInt(i++, toInt(chkCiTakikardi.isSelected()));
                ps.setInt(i++, toInt(chkCiBradikardi.isSelected())); ps.setInt(i++, toInt(chkCiHipotensi.isSelected()));
                ps.setInt(i++, toInt(chkCiSyok.isSelected()));
                ps.setInt(i++, toInt(chkDiSadar.isSelected())); ps.setInt(i++, toInt(chkDiSomnolen.isSelected()));
                ps.setInt(i++, toInt(chkDiSopor.isSelected())); ps.setInt(i++, toInt(chkDiKoma.isSelected()));
                ps.setInt(i++, toInt(chkDiKejang.isSelected()));
                ps.setInt(i++, toInt(chkExNormal.isSelected())); ps.setInt(i++, toInt(chkExLuka.isSelected()));
                ps.setInt(i++, toInt(chkExEdema.isSelected())); ps.setInt(i++, toInt(chkExRuam.isSelected()));
                ps.setString(i++, TKeluhanUtama.getText().trim());
                ps.setString(i++, TRiwayatPenyakit.getText().trim().isEmpty() ? null : TRiwayatPenyakit.getText().trim());
                ps.setString(i++, TRiwayatAlergi.getText().trim().isEmpty() ? null : TRiwayatAlergi.getText().trim());
                ps.setString(i++, TImunisasi.getText().trim().isEmpty() ? null : TImunisasi.getText().trim());
                ps.setInt(i++, toInt(ChkGelisah.isSelected())); ps.setInt(i++, toInt(ChkRisikoBunuhDiri.isSelected()));
                ps.setString(i++, saranSistem);
                ps.setString(i++, LblAlasanSaran.getText().trim().isEmpty() ? null : LblAlasanSaran.getText().trim());
                ps.setString(i++, kategori);
                ps.setInt(i++, overrideFlag ? 1 : 0);
                ps.setString(i++, overrideFlag ? TOverrideAlasan.getText().trim() : null);
                String area = CbAreaPenempatan.getSelectedItem().toString();
                ps.setString(i++, area.isEmpty() ? null : area);
                String statusKeluar = CbStatusKeluar.getSelectedItem().toString();
                ps.setString(i++, statusKeluar.isEmpty() ? null : statusKeluar);
                ps.setString(i++, nip);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data triase berhasil disimpan.");
                tampil(); emptTeks();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan: " + ex.getMessage());
                System.out.println("Error simpan triase: " + ex);
            } finally { try { if (ps != null) ps.close(); } catch (Exception ex) {} }
        }
    }

    private Integer parseInt(TextBox tb) {
        try { return Integer.parseInt(tb.getText().trim()); } catch (Exception e) { return null; }
    }

    private Object parseDecimal(TextBox tb) {
        try { return new java.math.BigDecimal(tb.getText().trim()); } catch (Exception e) { return null; }
    }

    // ========== HAPUS (soft delete) ==========
    private void hapus() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu!"); return; }
        int confirm = JOptionPane.showConfirmDialog(null, "Hapus data triase ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Long id = Long.parseLong(tabMode.getValueAt(tbData.getSelectedRow(), 0).toString());
        try {
            ps = koneksi.prepareStatement("UPDATE triase_igd SET deleted_at=NOW() WHERE id=?");
            ps.setLong(1, id); ps.executeUpdate();
            tampil(); emptTeks();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal hapus: " + ex.getMessage());
        } finally { try { if (ps != null) ps.close(); } catch (Exception ex) {} }
    }

    // ========== EDIT (hapus lalu simpan ulang) ==========
    private void edit() {
        if (TNoRw.getText().trim().isEmpty() || tbData.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data dulu!"); return;
        }
        Long id = Long.parseLong(tabMode.getValueAt(tbData.getSelectedRow(), 0).toString());
        try {
            ps = koneksi.prepareStatement("UPDATE triase_igd SET deleted_at=NOW() WHERE id=?");
            ps.setLong(1, id); ps.executeUpdate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal edit: " + ex.getMessage()); return;
        } finally { try { if (ps != null) ps.close(); } catch (Exception ex) {} }
        simpan();
    }

    // ========== RE-TRIASE (comprehensive mode) ==========
    private void retriase() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu!"); return; }
        retriaseId = Long.parseLong(tabMode.getValueAt(tbData.getSelectedRow(), 0).toString());
        katLamaRetriase = tabMode.getValueAt(tbData.getSelectedRow(), 4).toString();
        // Load existing triase data into form
        getData();
        // Activate re-triase mode UI
        isRetriase = true;
        TAlasanRetriase.setText("");
        LblRetriaseBanner.setVisible(true);
        LblAlasanRetriaseLbl.setVisible(true);
        scrAlasanRetriase.setVisible(true);
        BtnSimpan.setText("Simpan Re-Triase");
        javax.swing.SwingUtilities.invokeLater(() -> ScrollInput.getVerticalScrollBar().setValue(0));
        JOptionPane.showMessageDialog(null,
            "Data triase pasien telah dimuat ke form.\n" +
            "Sesuaikan data dengan kondisi terbaru pasien,\nlalu klik tombol 'Simpan Re-Triase'.",
            "Mode Re-Triase Aktif", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========== CETAK ==========
    private void cetak() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih data dulu!"); return; }
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu!"); return; }
        Long id = Long.parseLong(tabMode.getValueAt(tbData.getSelectedRow(), 0).toString());
        Map<String, Object> p = new HashMap<>();
        p.put("namars", akses.getnamars()); p.put("alamatrs", akses.getalamatrs());
        p.put("propinsirs", akses.getpropinsirs()); p.put("kabupatenrs", akses.getkabupatenrs());
        p.put("kontakrs", akses.getkontakrs()); p.put("emailrs", akses.getemailrs());
        p.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        String nipp = KdPetugas.getText().trim();
        String nmPet = NmPetugas.getText().trim();
        String finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", nipp);
        p.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() +
            "\nDitandatangani secara elektronik oleh " + nmPet + "\nID " + (finger.isEmpty() ? nipp : finger));
        Valid.MyReportqry("rptTriaseIGDPermenkes.jasper", "report", ":: Triase IGD Permenkes ::",
            "select t.*,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,pt.nama as nm_petugas " +
            "from triase_igd t inner join reg_periksa r on t.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join petugas pt on t.nip_petugas=pt.nip " +
            "where t.id=" + id, p);
    }

    // ========== TAMPIL (load table) ==========
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select t.id,t.no_rawat,r.no_rkm_medis,p.nm_pasien,t.waktu_triase,t.kategori_triase,t.keluhan_utama,pt.nama " +
                "from triase_igd t inner join reg_periksa r on t.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join petugas pt on t.nip_petugas=pt.nip " +
                "where t.deleted_at IS NULL " +
                "and t.waktu_datang between ? and ? " +
                "and (t.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by t.waktu_datang desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            String key = "%" + TCari.getText().trim() + "%";
            ps.setString(3, key); ps.setString(4, key); ps.setString(5, key);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("id"), rs.getString("no_rawat"), rs.getString("nm_pasien"),
                    rs.getString("waktu_triase"), rs.getString("kategori_triase"),
                    rs.getString("keluhan_utama"), rs.getString("nama")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) {
            System.out.println("Error tampil triase: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    // ========== GET DATA (dari table ke form) ==========
    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        Long id = Long.parseLong(tabMode.getValueAt(tbData.getSelectedRow(), 0).toString());
        currentId = id;
        try {
            ps = koneksi.prepareStatement("select * from triase_igd where id=?");
            ps.setLong(1, id); rs = ps.executeQuery();
            if (rs.next()) {
                TNoRw.setText(rs.getString("no_rawat"));
                TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", rs.getString("no_rawat")));
                TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
                // cara datang
                CbCaraDatang.setSelectedItem(rs.getString("cara_datang"));
                CbJenisKeluhan.setSelectedItem(rs.getString("jenis_keluhan"));
                ChkDOA.setSelected(rs.getInt("doa_flag") == 1);
                String doaJamDb = rs.getString("doa_jam");
                if (doaJamDb != null && !doaJamDb.isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        DTPJamDOA.setDate(sdf.parse(doaJamDb));
                    } catch (Exception ex) { DTPJamDOA.setDate(new java.util.Date()); }
                } else { DTPJamDOA.setDate(new java.util.Date()); }
                TKriteriaDOA.setText(nvl(rs.getString("doa_kriteria")));
                // vital
                TTdSistolik.setText(nvl(rs.getString("td_sistolik")));
                TTdDiastolik.setText(nvl(rs.getString("td_diastolik")));
                TNadi.setText(nvl(rs.getString("nadi")));
                TRR.setText(nvl(rs.getString("rr")));
                TSpO2.setText(nvl(rs.getString("spo2")));
                TSuhu.setText(nvl(rs.getString("suhu")));
                TGCSE.setText(nvl(rs.getString("gcs_eye")));
                TGCSV.setText(nvl(rs.getString("gcs_verbal")));
                TGCSM.setText(nvl(rs.getString("gcs_motor")));
                TGCSTotal.setText(nvl(rs.getString("gcs_total")));
                TSkalaNyeri.setText(nvl(rs.getString("skala_nyeri")));
                TBeratBadan.setText(nvl(rs.getString("berat_badan")));
                TPupilKiri.setText(nvl(rs.getString("pupil_kiri")));
                TPupilKanan.setText(nvl(rs.getString("pupil_kanan")));
                TReflexCahaya.setText(nvl(rs.getString("reflex_cahaya")));
                TAkral.setText(nvl(rs.getString("akral")));
                // ABCDE
                chkAwBebas.setSelected(b(rs,"airway_bebas")); chkAwSumbatan.setSelected(b(rs,"airway_sumbatan"));
                chkAwSnoring.setSelected(b(rs,"airway_snoring")); chkAwGargling.setSelected(b(rs,"airway_gargling"));
                chkAwStridor.setSelected(b(rs,"airway_stridor"));
                chkBrNormal.setSelected(b(rs,"breathing_normal")); chkBrTakipnea.setSelected(b(rs,"breathing_takipnea"));
                chkBrBradipnea.setSelected(b(rs,"breathing_bradipnea")); chkBrApnea.setSelected(b(rs,"breathing_apnea"));
                chkBrSpO2Turun.setSelected(b(rs,"breathing_spo2_turun"));
                chkCiNormal.setSelected(b(rs,"circulation_normal")); chkCiTakikardi.setSelected(b(rs,"circulation_takikardi"));
                chkCiBradikardi.setSelected(b(rs,"circulation_bradikardi")); chkCiHipotensi.setSelected(b(rs,"circulation_hipotensi"));
                chkCiSyok.setSelected(b(rs,"circulation_syok"));
                chkDiSadar.setSelected(b(rs,"disability_sadar")); chkDiSomnolen.setSelected(b(rs,"disability_somnolen"));
                chkDiSopor.setSelected(b(rs,"disability_sopor")); chkDiKoma.setSelected(b(rs,"disability_koma"));
                chkDiKejang.setSelected(b(rs,"disability_kejang"));
                chkExNormal.setSelected(b(rs,"exposure_normal")); chkExLuka.setSelected(b(rs,"exposure_luka"));
                chkExEdema.setSelected(b(rs,"exposure_edema")); chkExRuam.setSelected(b(rs,"exposure_ruam"));
                // keluhan
                TKeluhanUtama.setText(nvl(rs.getString("keluhan_utama")));
                TRiwayatPenyakit.setText(nvl(rs.getString("riwayat_penyakit")));
                TRiwayatAlergi.setText(nvl(rs.getString("riwayat_alergi")));
                TImunisasi.setText(nvl(rs.getString("imunisasi")));
                ChkGelisah.setSelected(b(rs,"gelisah")); ChkRisikoBunuhDiri.setSelected(b(rs,"risiko_bunuh_diri"));
                // CDS
                saranSistem = rs.getString("saran_sistem");
                if (saranSistem != null) {
                    LblSaranSistem.setText(saranSistem); LblSaranSistem.setForeground(getWarnaKategori(saranSistem));
                }
                LblAlasanSaran.setText(nvl(rs.getString("alasan_saran")));
                setKategoriTriase(rs.getString("kategori_triase"));
                TOverrideAlasan.setText(nvl(rs.getString("override_alasan")));
                // disposisi
                CbAreaPenempatan.setSelectedItem(nvl(rs.getString("area_penempatan")));
                CbStatusKeluar.setSelectedItem(nvl(rs.getString("status_keluar")));
                // petugas
                KdPetugas.setText(nvl(rs.getString("nip_petugas")));
                NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_petugas")));
            }
        } catch (Exception e) {
            System.out.println("Error getData triase: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        // Load retriase log
        Valid.tabelKosong(tabRetriaseLog);
        java.sql.PreparedStatement ps2 = null; java.sql.ResultSet rs2 = null;
        try {
            ps2 = koneksi.prepareStatement(
                "SELECT rl.waktu_retriase, rl.kategori_lama, rl.kategori_baru, rl.alasan, pt.nama " +
                "FROM retriase_log rl LEFT JOIN petugas pt ON rl.nip_petugas=pt.nip " +
                "WHERE rl.triase_id=? ORDER BY rl.waktu_retriase");
            ps2.setLong(1, id); rs2 = ps2.executeQuery();
            while (rs2.next()) {
                tabRetriaseLog.addRow(new String[]{
                    nvl(rs2.getString("waktu_retriase")), nvl(rs2.getString("kategori_lama")),
                    nvl(rs2.getString("kategori_baru")), nvl(rs2.getString("alasan")),
                    nvl(rs2.getString("nama"))
                });
            }
        } catch (Exception e) {
            System.out.println("Error load retriase_log: " + e);
        } finally { try { if (rs2 != null) rs2.close(); if (ps2 != null) ps2.close(); } catch (Exception e) {} }
    }

    private String nvl(String s) { return s == null ? "" : s; }

    // ========== EMPTY / RESET ==========
    public void emptTeks() {
        currentId = null; saranSistem = null;
        isRetriase = false; retriaseId = null; katLamaRetriase = null;
        LblRetriaseBanner.setVisible(false);
        LblAlasanRetriaseLbl.setVisible(false);
        scrAlasanRetriase.setVisible(false);
        TAlasanRetriase.setText("");
        BtnSimpan.setText("Simpan");
        TNoRw.setText(""); TNoRM.setText(""); TPasien.setText("");
        DTPWaktuDatang.setDate(new Date()); DTPWaktuTriase.setDate(new Date());
        CbCaraDatang.setSelectedIndex(0); CbJenisKeluhan.setSelectedIndex(0);
        ChkDOA.setSelected(false); TKriteriaDOA.setText("");
        TTdSistolik.setText(""); TTdDiastolik.setText(""); TNadi.setText(""); TRR.setText("");
        TSpO2.setText(""); TSuhu.setText(""); TGCSE.setText(""); TGCSV.setText(""); TGCSM.setText(""); TGCSTotal.setText("");
        TSkalaNyeri.setText(""); TBeratBadan.setText(""); TPupilKiri.setText(""); TPupilKanan.setText("");
        TReflexCahaya.setText(""); TAkral.setText("");
        chkAwBebas.setSelected(false); chkAwSumbatan.setSelected(false); chkAwSnoring.setSelected(false);
        chkAwGargling.setSelected(false); chkAwStridor.setSelected(false);
        chkBrNormal.setSelected(false); chkBrTakipnea.setSelected(false); chkBrBradipnea.setSelected(false);
        chkBrApnea.setSelected(false); chkBrSpO2Turun.setSelected(false);
        chkCiNormal.setSelected(false); chkCiTakikardi.setSelected(false); chkCiBradikardi.setSelected(false);
        chkCiHipotensi.setSelected(false); chkCiSyok.setSelected(false);
        chkDiSadar.setSelected(false); chkDiSomnolen.setSelected(false); chkDiSopor.setSelected(false);
        chkDiKoma.setSelected(false); chkDiKejang.setSelected(false);
        chkExNormal.setSelected(false); chkExLuka.setSelected(false); chkExEdema.setSelected(false); chkExRuam.setSelected(false);
        TKeluhanUtama.setText(""); TRiwayatPenyakit.setText(""); TRiwayatAlergi.setText("");
        TImunisasi.setText(""); ChkGelisah.setSelected(false); ChkRisikoBunuhDiri.setSelected(false);
        LblSaranSistem.setText("—"); LblSaranSistem.setForeground(Color.DARK_GRAY); LblAlasanSaran.setText("");
        RdHijau.setSelected(true); TOverrideAlasan.setText("");
        LblOverrideWarn.setVisible(false);
        CbAreaPenempatan.setSelectedIndex(0); CbStatusKeluar.setSelectedIndex(0);
        KdPetugas.setText(akses.getkode()); NmPetugas.setText(akses.getnamauser());
        Valid.tabelKosong(tabRetriaseLog);
    }

    public void setNoRm(String noRawat, String noRm, String namaPasien) {
        TNoRw.setText(noRawat); TNoRM.setText(noRm); TPasien.setText(namaPasien);
        DTPWaktuDatang.setDate(new Date()); DTPWaktuTriase.setDate(new Date());
        KdPetugas.setText(akses.getkode()); NmPetugas.setText(akses.getnamauser());
        // auto-load alergi pasien
        String alergi = Sequel.cariIsi(
            "select concat_ws(', ',ket_alergi_obat,ket_alergi_makanan) from rekam_medik_gigi where no_rawat=?", noRawat);
        if (!alergi.isEmpty()) TRiwayatAlergi.setText(alergi);
        tampil();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnRetriase.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(true);
    }
}
