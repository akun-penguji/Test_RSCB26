package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

/**
 * Form "Anestesi Sedasi" (RM.IBS.11.005.A/B.2019 REV.01) — RSUD Cabang Bungin.
 * Dipecah per FASE (waktu berbeda) ke tab & tabel masing-masing:
 *  - Pra Sedasi   -> catatan_sedasi_pra   (identitas + A checklist + B pra sedasi)
 *  - Selama Sedasi-> catatan_sedasi_selama (C)
 *  - Pasca Sedasi -> catatan_sedasi_pasca  (D + E + instruksi + perawat)
 *  - Monitoring   -> catatan_sedasi_monitoring (MULTI-BARIS, diinput berkali)
 * Tiap tab punya tanggal/jam sendiri, tabel daftar record, & CRUD sendiri.
 * Dibuka dari DlgBookingOperasi. Gaya UI Khanza.
 */
public class RMCatatanSedasiAnestesiRscb extends JDialog {

    // Identitas pasien (atas)
    private TextBox TNoRw, TNoRM, TPasien, TTglLahir, TJK;

    // ── Tab Pra Sedasi ──
    private widget.Tanggal PtTgl;
    private TextBox PtJamAwal, PtDiagnosis, PtTindakan;
    private ComboBox cmbPtJenis;
    private TextBox KdDokter, NmDokter, KdPenata, NmPenata, KdAsisten, NmAsisten;
    private Button BtnDokter, BtnPenata, BtnAsisten;
    private CekBox chkIC, chkObat, chkTata, chkMesin, chkEmer, chkMon, chkSuc;
    private TextBox PtKesadaran, PtGcsE, PtGcsV, PtGcsM, PtTd, PtNadi, PtRr, PtO2, PtSpo2;
    private ComboBox cmbPtAirway, cmbPtNadiReg, cmbPtKanul;
    private widget.Table tbPra; private DefaultTableModel mdPra;

    // ── Tab Selama Sedasi ──
    private widget.Tanggal SlTgl;
    private TextBox SlLamaSedasi, SlLamaTindakan, SlJamAkhir, SlMedikasi;
    private ComboBox cmbSlLetak, cmbSlAirway;
    private widget.Table tbSel; private DefaultTableModel mdSel;

    // ── Tab Pasca Sedasi ──
    private widget.Tanggal PsTgl;
    private TextBox KdPerawat, NmPerawat;
    private Button BtnPerawat;
    private TextBox PsKesadaran, PsTd, PsNadi, PsRr, PsO2, PsSpo2, PsAldrete, PsKeluarJam;
    private ComboBox cmbPsAirway, cmbPsNadiReg, cmbPsKanul, cmbPsPindah;
    private TextBox PsInsPantau, PsInsPosisi, PsInsNyeri, PsInsMual, PsInsDiet, PsInsObat, PsInsLain;
    private widget.Table tbPsc; private DefaultTableModel mdPsc;

    // ── Tab Monitoring (2 form terpisah: sedasi & pasca) ──
    private MonTab mon1, mon2;

    // Umum
    private JTabbedPane Tabs;
    private widget.Tanggal DTPCari1, DTPCari2;
    private Button BtnCetak, BtnKeluar;
    private InternalFrame internalFrame1;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private kepegawaian.DlgCariPetugas petugasPenata = new kepegawaian.DlgCariPetugas(null, false);
    private kepegawaian.DlgCariPetugas petugasAsisten = new kepegawaian.DlgCariPetugas(null, false);
    private kepegawaian.DlgCariPetugas petugasPerawat = new kepegawaian.DlgCariPetugas(null, false);

    public RMCatatanSedasiAnestesiRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(980, 730);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)), "::[ Anestesi Sedasi ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS (identitas pasien)
        panelisi atas = new panelisi();
        atas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 4));
        atas.setPreferredSize(new Dimension(1024, 33));
        atas.add(mkLabel("No.Rawat :"));
        TNoRw = ro(130); atas.add(TNoRw);
        TNoRM = ro(90); atas.add(TNoRM);
        TPasien = ro(200); atas.add(TPasien);
        atas.add(mkLabel("Tgl Lahir :")); TTglLahir = ro(90); atas.add(TTglLahir);
        atas.add(mkLabel("JK :")); TJK = ro(30); atas.add(TJK);

        mon1 = new MonTab("catatan_sedasi_monitoring", "Monitoring", true);
        mon2 = new MonTab("catatan_sedasi_monitoring_pasca", "Monitoring Pasca", false);
        Tabs = new JTabbedPane();
        // Urutan mengikuti formulir fisik: Pra Sedasi -> Monitoring -> Selama ->
        // Pasca Sedasi -> Monitoring Pasca.
        Tabs.addTab("Pra Sedasi", buildPra());
        Tabs.addTab("Monitoring", mon1.build());
        Tabs.addTab("Selama Sedasi", buildSelama());
        Tabs.addTab("Pasca Sedasi", buildPasca());
        Tabs.addTab("Monitoring Pasca", mon2.build());

        // PANEL BAWAH umum (filter tanggal + cetak + keluar)
        panelisi bawah = new panelisi();
        bawah.setPreferredSize(new Dimension(44, 44));
        bawah.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 8));
        Label l1 = new Label(); l1.setText("Tanggal :"); l1.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label l2 = new Label(); l2.setText("s.d."); l2.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Button BtnCari = new Button(); try { BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png"))); } catch (Exception e) {}
        BtnCari.setText("Tampil"); BtnCari.setPreferredSize(new Dimension(90, 30));
        BtnCari.addActionListener(evt -> tampilSemua());
        BtnCetak = btn("Cetak", 'T', "/picture/b_print.png");
        BtnCetak.addActionListener(evt -> cetak());
        BtnKeluar = btn("Keluar", 'K', "/picture/exit.png");
        BtnKeluar.addActionListener(evt -> dispose());
        bawah.add(l1); bawah.add(DTPCari1); bawah.add(l2); bawah.add(DTPCari2); bawah.add(BtnCari);
        bawah.add(BtnCetak); bawah.add(BtnKeluar);

        internalFrame1.add(atas, BorderLayout.PAGE_START);
        internalFrame1.add(Tabs, BorderLayout.CENTER);
        internalFrame1.add(bawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    // ══════════════════ TAB PRA SEDASI ══════════════════
    private JPanel buildPra() {
        PanelBiasa f = new PanelBiasa(); f.setPreferredSize(new Dimension(950, 470)); f.setLayout(null);
        int y = 8;
        f.add(sec("IDENTITAS & PRA SEDASI (A + B)", y)); y += 24;
        f.add(lbl("Tanggal & jam :", 10, y, 110)); PtTgl = dt(); PtTgl.setBounds(125, y, 150, 23); f.add(PtTgl);
        f.add(lbl("Jenis Sedasi :", 300, y, 90)); cmbPtJenis = combo(new String[]{"", "Ringan", "Sedang", "Dalam"}, 392, y, 120, f);
        f.add(lbl("Jam Awal :", 540, y, 65)); PtJamAwal = tb(70); PtJamAwal.setBounds(607, y, 70, 23); f.add(PtJamAwal);
        y += 26;
        f.add(lbl("Diagnosis :", 10, y, 110)); PtDiagnosis = tb(300); PtDiagnosis.setBounds(125, y, 300, 23); f.add(PtDiagnosis);
        f.add(lbl("Tindakan :", 435, y, 65)); PtTindakan = tb(300); PtTindakan.setBounds(502, y, 300, 23); f.add(PtTindakan);
        y += 26;
        f.add(lbl("Dokter :", 10, y, 110)); KdDokter = ro(70); KdDokter.setBounds(125, y, 70, 23); f.add(KdDokter);
        NmDokter = ro(220); NmDokter.setBounds(197, y, 220, 23); f.add(NmDokter);
        BtnDokter = klip(); BtnDokter.setBounds(419, y, 28, 23); f.add(BtnDokter); bindDokter();
        y += 26;
        f.add(lbl("Asisten :", 10, y, 110)); KdAsisten = ro(70); KdAsisten.setBounds(125, y, 70, 23); f.add(KdAsisten);
        NmAsisten = ro(220); NmAsisten.setBounds(197, y, 220, 23); f.add(NmAsisten);
        BtnAsisten = klip(); BtnAsisten.setBounds(419, y, 28, 23); f.add(BtnAsisten); bindPetugas(BtnAsisten, petugasAsisten, KdAsisten, NmAsisten);
        y += 26;
        f.add(lbl("Penata Anestesi :", 10, y, 110)); KdPenata = ro(70); KdPenata.setBounds(125, y, 70, 23); f.add(KdPenata);
        NmPenata = ro(220); NmPenata.setBounds(197, y, 220, 23); f.add(NmPenata);
        BtnPenata = klip(); BtnPenata.setBounds(419, y, 28, 23); f.add(BtnPenata); bindPetugas(BtnPenata, petugasPenata, KdPenata, NmPenata);

        y += 30; f.add(sec("A. CHECKLIST PERSIAPAN SEDASI", y)); y += 22;
        chkIC = ck("Informed Consent", 15, y, 160, f); chkObat = ck("Obat-obatan Anestesi", 195, y, 175, f); chkTata = ck("Tatalaksana Jalan Napas", 385, y, 190, f);
        y += 22; chkMesin = ck("Mesin Anestesi", 15, y, 160, f); chkEmer = ck("Obat-obatan Emergensi", 195, y, 175, f); chkMon = ck("Monitoring", 385, y, 190, f);
        y += 22; chkSuc = ck("Suction Aparatus", 15, y, 160, f);

        y += 30; f.add(sec("B. PENILAIAN PRA SEDASI", y)); y += 22;
        f.add(lbl("Kesadaran :", 10, y, 110)); PtKesadaran = tb(230); PtKesadaran.setBounds(125, y, 230, 23); f.add(PtKesadaran);
        f.add(lbl("GCS E:", 370, y, 42)); PtGcsE = tb(35); PtGcsE.setBounds(412, y, 35, 23); f.add(PtGcsE);
        f.add(lbl("V:", 452, y, 16)); PtGcsV = tb(35); PtGcsV.setBounds(468, y, 35, 23); f.add(PtGcsV);
        f.add(lbl("M:", 508, y, 16)); PtGcsM = tb(35); PtGcsM.setBounds(524, y, 35, 23); f.add(PtGcsM);
        y += 26; f.add(lbl("Airway :", 10, y, 110)); cmbPtAirway = combo(new String[]{"", "Kanul Nasal", "Simple Mask", "NRM", "RM"}, 125, y, 150, f);
        y += 26; f.add(lbl("Tekanan Darah :", 10, y, 110)); PtTd = tb(90); PtTd.setBounds(125, y, 90, 23); f.add(PtTd); f.add(lbl("mmHg", 218, y, 40));
        f.add(lbl("Nadi :", 270, y, 40)); PtNadi = tb(60); PtNadi.setBounds(312, y, 60, 23); f.add(PtNadi); cmbPtNadiReg = combo(new String[]{"", "reg", "ireg"}, 378, y, 80, f);
        y += 26; f.add(lbl("Respirasi RR :", 10, y, 110)); PtRr = tb(60); PtRr.setBounds(125, y, 60, 23); f.add(PtRr);
        f.add(lbl("Kanul :", 190, y, 45)); cmbPtKanul = combo(new String[]{"", "Nasal", "SM", "RM", "NRM"}, 237, y, 85, f);
        f.add(lbl("O2 :", 330, y, 28)); PtO2 = tb(45); PtO2.setBounds(358, y, 45, 23); f.add(PtO2);
        f.add(lbl("L/mnt SPO2 :", 408, y, 80)); PtSpo2 = tb(45); PtSpo2.setBounds(490, y, 45, 23); f.add(PtSpo2); f.add(lbl("%", 538, y, 15));

        mdPra = model(new String[]{"No.Rawat", "Tanggal", "Dokter", "Penata", "Asisten", "Diagnosis", "Tindakan", "Jam Awal", "Jenis Sedasi",
            "IC", "Obat Anes", "Tatalaksana", "Mesin", "Emergensi", "Monitoring", "Suction",
            "Kesadaran", "GCS", "Airway", "TD", "Nadi", "Reg", "RR", "Kanul", "O2", "SPO2"});
        tbPra = tabel(mdPra, evt -> getDataPra());
        return tabWrap(f, tbPra, () -> simpanPra(), () -> emptPra(), () -> hapusPra(), () -> gantiPra());
    }

    // ══════════════════ TAB SELAMA SEDASI ══════════════════
    private JPanel buildSelama() {
        PanelBiasa f = new PanelBiasa(); f.setPreferredSize(new Dimension(950, 220)); f.setLayout(null);
        int y = 8;
        f.add(sec("C. KEADAAN SELAMA SEDASI", y)); y += 24;
        f.add(lbl("Tanggal & jam :", 10, y, 110)); SlTgl = dt(); SlTgl.setBounds(125, y, 150, 23); f.add(SlTgl);
        f.add(lbl("Jam Akhir :", 300, y, 70)); SlJamAkhir = tb(70); SlJamAkhir.setBounds(372, y, 70, 23); f.add(SlJamAkhir);
        y += 26; f.add(lbl("Letak penderita :", 10, y, 110)); cmbSlLetak = combo(new String[]{"", "Supine", "Litotomi", "Lateral dekubitus (R)", "Lateral dekubitus (L)"}, 125, y, 200, f);
        y += 26; f.add(lbl("Airway :", 10, y, 110)); cmbSlAirway = combo(new String[]{"", "Kanul Nasal", "Face Mask", "Oropharyngeal Airway"}, 125, y, 200, f);
        y += 26; f.add(lbl("Lama Sedasi :", 10, y, 110)); SlLamaSedasi = tb(80); SlLamaSedasi.setBounds(125, y, 80, 23); f.add(SlLamaSedasi); f.add(lbl("menit", 208, y, 40));
        f.add(lbl("Lama Tindakan :", 270, y, 100)); SlLamaTindakan = tb(80); SlLamaTindakan.setBounds(372, y, 80, 23); f.add(SlLamaTindakan); f.add(lbl("menit", 455, y, 40));
        y += 26; f.add(lbl("Medikasi :", 10, y, 110)); SlMedikasi = tb(660); SlMedikasi.setBounds(125, y, 660, 23); f.add(SlMedikasi);

        mdSel = model(new String[]{"No.Rawat", "Tanggal", "Letak", "Airway", "Lama Sedasi", "Lama Tindakan", "Jam Akhir", "Medikasi"});
        tbSel = tabel(mdSel, evt -> getDataSel());
        return tabWrap(f, tbSel, () -> simpanSel(), () -> emptSel(), () -> hapusSel(), () -> gantiSel());
    }

    // ══════════════════ TAB PASCA SEDASI ══════════════════
    private JPanel buildPasca() {
        PanelBiasa f = new PanelBiasa(); f.setPreferredSize(new Dimension(950, 430)); f.setLayout(null);
        int y = 8;
        f.add(sec("D. KEADAAN PASCA SEDASI", y)); y += 24;
        f.add(lbl("Tanggal & jam :", 10, y, 110)); PsTgl = dt(); PsTgl.setBounds(125, y, 150, 23); f.add(PsTgl);
        y += 26; f.add(lbl("Kesadaran :", 10, y, 110)); PsKesadaran = tb(230); PsKesadaran.setBounds(125, y, 230, 23); f.add(PsKesadaran);
        f.add(lbl("Airway :", 370, y, 50)); cmbPsAirway = combo(new String[]{"", "Kanul Nasal", "Simple Mask", "NRM", "RM"}, 422, y, 140, f);
        y += 26; f.add(lbl("Tekanan Darah :", 10, y, 110)); PsTd = tb(90); PsTd.setBounds(125, y, 90, 23); f.add(PsTd); f.add(lbl("mmHg", 218, y, 40));
        f.add(lbl("Nadi :", 270, y, 40)); PsNadi = tb(60); PsNadi.setBounds(312, y, 60, 23); f.add(PsNadi); cmbPsNadiReg = combo(new String[]{"", "reg", "ireg"}, 378, y, 80, f);
        y += 26; f.add(lbl("Respirasi RR :", 10, y, 110)); PsRr = tb(60); PsRr.setBounds(125, y, 60, 23); f.add(PsRr);
        f.add(lbl("Kanul :", 190, y, 45)); cmbPsKanul = combo(new String[]{"", "Nasal", "SM", "RM", "NRM"}, 237, y, 85, f);
        f.add(lbl("O2 :", 330, y, 28)); PsO2 = tb(45); PsO2.setBounds(358, y, 45, 23); f.add(PsO2);
        f.add(lbl("L/mnt SPO2 :", 408, y, 80)); PsSpo2 = tb(45); PsSpo2.setBounds(490, y, 45, 23); f.add(PsSpo2); f.add(lbl("%", 538, y, 15));
        y += 26; f.add(lbl("ALDRETE Score :", 10, y, 110)); PsAldrete = tb(90); PsAldrete.setBounds(125, y, 90, 23); f.add(PsAldrete);

        y += 30; f.add(sec("E. JAM KELUAR RUANG PEMULIHAN", y)); y += 22;
        f.add(lbl("Jam keluar :", 10, y, 110)); PsKeluarJam = tb(90); PsKeluarJam.setBounds(125, y, 90, 23); f.add(PsKeluarJam);
        f.add(lbl("Pindah ke :", 240, y, 70)); cmbPsPindah = combo(new String[]{"", "Ruang Rawat", "ICU", "HCU", "NICU", "PICU"}, 312, y, 140, f);
        y += 28; f.add(sec("INSTRUKSI PASCA SEDASI", y)); y += 22;
        f.add(lbl("Pemantauan tiap :", 10, y, 140)); PsInsPantau = tb(640); PsInsPantau.setBounds(150, y, 640, 23); f.add(PsInsPantau);
        y += 24; f.add(lbl("Posisi pasien :", 10, y, 140)); PsInsPosisi = tb(640); PsInsPosisi.setBounds(150, y, 640, 23); f.add(PsInsPosisi);
        y += 24; f.add(lbl("Pengelolaan nyeri :", 10, y, 140)); PsInsNyeri = tb(640); PsInsNyeri.setBounds(150, y, 640, 23); f.add(PsInsNyeri);
        y += 24; f.add(lbl("Penanganan mual muntah :", 10, y, 140)); PsInsMual = tb(640); PsInsMual.setBounds(150, y, 640, 23); f.add(PsInsMual);
        y += 24; f.add(lbl("Diet dan nutrisi :", 10, y, 140)); PsInsDiet = tb(640); PsInsDiet.setBounds(150, y, 640, 23); f.add(PsInsDiet);
        y += 24; f.add(lbl("Obat-obat lain :", 10, y, 140)); PsInsObat = tb(640); PsInsObat.setBounds(150, y, 640, 23); f.add(PsInsObat);
        y += 24; f.add(lbl("Lain-lain :", 10, y, 140)); PsInsLain = tb(640); PsInsLain.setBounds(150, y, 640, 23); f.add(PsInsLain);
        y += 28; f.add(lbl("Perawat Ruangan :", 10, y, 110)); KdPerawat = ro(70); KdPerawat.setBounds(125, y, 70, 23); f.add(KdPerawat);
        NmPerawat = ro(220); NmPerawat.setBounds(197, y, 220, 23); f.add(NmPerawat);
        BtnPerawat = klip(); BtnPerawat.setBounds(419, y, 28, 23); f.add(BtnPerawat); bindPetugas(BtnPerawat, petugasPerawat, KdPerawat, NmPerawat);

        mdPsc = model(new String[]{"No.Rawat", "Tanggal", "Perawat", "Kesadaran", "Airway", "TD", "Nadi", "Reg", "RR", "Kanul", "O2", "SPO2", "ALDRETE",
            "Jam Keluar", "Pindah ke", "Pemantauan", "Posisi", "Nyeri", "Mual Muntah", "Diet", "Obat Lain", "Lain-lain"});
        tbPsc = tabel(mdPsc, evt -> getDataPsc());
        return tabWrap(f, tbPsc, () -> simpanPsc(), () -> emptPsc(), () -> hapusPsc(), () -> gantiPsc());
    }

    // ══════════════════ CRUD PRA ══════════════════
    private void simpanPra() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (KdDokter.getText().trim().equals("")) { Valid.textKosong(KdDokter, "Dokter"); return; }
        if (Sequel.menyimpantf("catatan_sedasi_pra", ph(29), "Pra Sedasi", 29, new String[]{
            TNoRw.getText(), jam(PtTgl), akses.getkode(), KdDokter.getText(), KdPenata.getText(), NmAsisten.getText(),
            PtDiagnosis.getText(), PtTindakan.getText(), PtJamAwal.getText(), cmb(cmbPtJenis),
            yn(chkIC), yn(chkObat), yn(chkTata), yn(chkMesin), yn(chkEmer), yn(chkMon), yn(chkSuc),
            PtKesadaran.getText(), PtGcsE.getText(), PtGcsV.getText(), PtGcsM.getText(), cmb(cmbPtAirway),
            PtTd.getText(), PtNadi.getText(), cmb(cmbPtNadiReg), PtRr.getText(), cmb(cmbPtKanul), PtO2.getText(), PtSpo2.getText()
        })) { tampilPra(); emptPra(); }
    }
    private void gantiPra() {
        if (tbPra.getSelectedRow() == -1) { pilih(); return; }
        if (Sequel.queryu2tf("delete from catatan_sedasi_pra where no_rawat=? and tanggal=?", 2, key(tbPra))) simpanPra();
    }
    private void hapusPra() {
        if (tbPra.getSelectedRow() == -1) { pilih(); return; }
        if (!konfirm()) return;
        if (Sequel.queryu2tf("delete from catatan_sedasi_pra where no_rawat=? and tanggal=?", 2, key(tbPra))) { tampilPra(); emptPra(); }
    }
    private void tampilPra() {
        Valid.tabelKosong(mdPra);
        try {
            ps = koneksi.prepareStatement("select s.no_rawat, s.tanggal, IFNULL(d.nm_dokter,'') dokter, IFNULL(pn.nama,'') penata, s.asisten, s.diagnosis, s.tindakan, s.jam_awal, s.jenis_sedasi, "
                + "s.cl_informed_consent, s.cl_obat_anestesi, s.cl_tatalaksana_napas, s.cl_mesin_anestesi, s.cl_obat_emergensi, s.cl_monitoring, s.cl_suction, "
                + "s.kesadaran, concat_ws('/',s.gcs_e,s.gcs_v,s.gcs_m) gcs, s.airway, s.td, s.nadi, s.nadi_reg, s.rr, s.kanul, s.o2, s.spo2 "
                + "from catatan_sedasi_pra s left join dokter d on s.kd_dokter=d.kd_dokter left join petugas pn on s.kd_penata=pn.nip "
                + "where s.no_rawat=? and s.tanggal between ? and ? order by s.tanggal desc");
            ps.setString(1, TNoRw.getText()); ps.setString(2, cari1()); ps.setString(3, cari2());
            rs = ps.executeQuery();
            while (rs.next()) mdPra.addRow(row(rs, 26));
        } catch (Exception e) { System.out.println("Notif Pra Sedasi : " + e); } finally { tutup(); }
    }
    private void getDataPra() {
        if (tbPra.getSelectedRow() == -1) return;
        try { PtTgl.setDate(fmt().parse(tbPra.getValueAt(tbPra.getSelectedRow(), 1).toString())); } catch (Exception e) {}
        try {
            ps = koneksi.prepareStatement("select * from catatan_sedasi_pra where no_rawat=? and tanggal=?");
            ps.setString(1, tbPra.getValueAt(tbPra.getSelectedRow(), 0).toString());
            ps.setString(2, tbPra.getValueAt(tbPra.getSelectedRow(), 1).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                setDok(rs.getString("kd_dokter"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(nmPtg(rs.getString("kd_penata")));
                NmAsisten.setText(rs.getString("asisten")); KdAsisten.setText(nipByNama(rs.getString("asisten")));
                PtDiagnosis.setText(rs.getString("diagnosis")); PtTindakan.setText(rs.getString("tindakan"));
                PtJamAwal.setText(rs.getString("jam_awal")); cmbPtJenis.setSelectedItem(rs.getString("jenis_sedasi"));
                chkIC.setSelected(ya(rs, "cl_informed_consent")); chkObat.setSelected(ya(rs, "cl_obat_anestesi"));
                chkTata.setSelected(ya(rs, "cl_tatalaksana_napas")); chkMesin.setSelected(ya(rs, "cl_mesin_anestesi"));
                chkEmer.setSelected(ya(rs, "cl_obat_emergensi")); chkMon.setSelected(ya(rs, "cl_monitoring")); chkSuc.setSelected(ya(rs, "cl_suction"));
                PtKesadaran.setText(rs.getString("kesadaran")); PtGcsE.setText(rs.getString("gcs_e")); PtGcsV.setText(rs.getString("gcs_v")); PtGcsM.setText(rs.getString("gcs_m"));
                cmbPtAirway.setSelectedItem(rs.getString("airway")); PtTd.setText(rs.getString("td")); PtNadi.setText(rs.getString("nadi"));
                cmbPtNadiReg.setSelectedItem(rs.getString("nadi_reg")); PtRr.setText(rs.getString("rr")); cmbPtKanul.setSelectedItem(rs.getString("kanul"));
                PtO2.setText(rs.getString("o2")); PtSpo2.setText(rs.getString("spo2"));
            }
        } catch (Exception e) { System.out.println("Notif getData Pra : " + e); } finally { tutup(); }
    }
    private void emptPra() {
        PtTgl.setDate(new Date()); PtJamAwal.setText(""); PtDiagnosis.setText(""); PtTindakan.setText(""); cmbPtJenis.setSelectedIndex(0);
        KdDokter.setText(""); NmDokter.setText(""); KdPenata.setText(""); NmPenata.setText(""); KdAsisten.setText(""); NmAsisten.setText("");
        chkIC.setSelected(false); chkObat.setSelected(false); chkTata.setSelected(false); chkMesin.setSelected(false);
        chkEmer.setSelected(false); chkMon.setSelected(false); chkSuc.setSelected(false);
        PtKesadaran.setText(""); PtGcsE.setText(""); PtGcsV.setText(""); PtGcsM.setText(""); cmbPtAirway.setSelectedIndex(0);
        PtTd.setText(""); PtNadi.setText(""); cmbPtNadiReg.setSelectedIndex(0); PtRr.setText(""); cmbPtKanul.setSelectedIndex(0); PtO2.setText(""); PtSpo2.setText("");
        // tindakan bedah auto dari booking bila kosong
        String nm = Sequel.cariIsi("select po.nm_perawatan from booking_operasi bo left join paket_operasi po on bo.kode_paket=po.kode_paket where bo.no_rawat=? order by bo.tanggal desc limit 1", TNoRw.getText());
        if (nm != null && !nm.trim().equals("")) PtTindakan.setText(nm);
    }

    // ══════════════════ CRUD SELAMA ══════════════════
    private void simpanSel() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (Sequel.menyimpantf("catatan_sedasi_selama", ph(9), "Selama Sedasi", 9, new String[]{
            TNoRw.getText(), jam(SlTgl), akses.getkode(), cmb(cmbSlLetak), cmb(cmbSlAirway),
            SlLamaSedasi.getText(), SlLamaTindakan.getText(), SlJamAkhir.getText(), SlMedikasi.getText()
        })) { tampilSel(); emptSel(); }
    }
    private void gantiSel() {
        if (tbSel.getSelectedRow() == -1) { pilih(); return; }
        if (Sequel.queryu2tf("delete from catatan_sedasi_selama where no_rawat=? and tanggal=?", 2, key(tbSel))) simpanSel();
    }
    private void hapusSel() {
        if (tbSel.getSelectedRow() == -1) { pilih(); return; }
        if (!konfirm()) return;
        if (Sequel.queryu2tf("delete from catatan_sedasi_selama where no_rawat=? and tanggal=?", 2, key(tbSel))) { tampilSel(); emptSel(); }
    }
    private void tampilSel() {
        Valid.tabelKosong(mdSel);
        try {
            ps = koneksi.prepareStatement("select no_rawat, tanggal, letak, airway, lama_sedasi, lama_tindakan, jam_akhir, medikasi from catatan_sedasi_selama where no_rawat=? and tanggal between ? and ? order by tanggal desc");
            ps.setString(1, TNoRw.getText()); ps.setString(2, cari1()); ps.setString(3, cari2());
            rs = ps.executeQuery();
            while (rs.next()) mdSel.addRow(row(rs, 8));
        } catch (Exception e) { System.out.println("Notif Selama : " + e); } finally { tutup(); }
    }
    private void getDataSel() {
        if (tbSel.getSelectedRow() == -1) return;
        try { SlTgl.setDate(fmt().parse(tbSel.getValueAt(tbSel.getSelectedRow(), 1).toString())); } catch (Exception e) {}
        try {
            ps = koneksi.prepareStatement("select * from catatan_sedasi_selama where no_rawat=? and tanggal=?");
            ps.setString(1, tbSel.getValueAt(tbSel.getSelectedRow(), 0).toString());
            ps.setString(2, tbSel.getValueAt(tbSel.getSelectedRow(), 1).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                cmbSlLetak.setSelectedItem(rs.getString("letak")); cmbSlAirway.setSelectedItem(rs.getString("airway"));
                SlLamaSedasi.setText(rs.getString("lama_sedasi")); SlLamaTindakan.setText(rs.getString("lama_tindakan"));
                SlJamAkhir.setText(rs.getString("jam_akhir")); SlMedikasi.setText(rs.getString("medikasi"));
            }
        } catch (Exception e) { System.out.println("Notif getData Selama : " + e); } finally { tutup(); }
    }
    private void emptSel() {
        SlTgl.setDate(new Date()); cmbSlLetak.setSelectedIndex(0); cmbSlAirway.setSelectedIndex(0);
        SlLamaSedasi.setText(""); SlLamaTindakan.setText(""); SlJamAkhir.setText(""); SlMedikasi.setText("");
    }

    // ══════════════════ CRUD PASCA ══════════════════
    private void simpanPsc() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (Sequel.menyimpantf("catatan_sedasi_pasca", ph(23), "Pasca Sedasi", 23, new String[]{
            TNoRw.getText(), jam(PsTgl), akses.getkode(), KdPerawat.getText(), PsKesadaran.getText(), cmb(cmbPsAirway),
            PsTd.getText(), PsNadi.getText(), cmb(cmbPsNadiReg), PsRr.getText(), cmb(cmbPsKanul), PsO2.getText(), PsSpo2.getText(),
            PsAldrete.getText(), PsKeluarJam.getText(), cmb(cmbPsPindah),
            PsInsPantau.getText(), PsInsPosisi.getText(), PsInsNyeri.getText(), PsInsMual.getText(), PsInsDiet.getText(), PsInsObat.getText(), PsInsLain.getText()
        })) { tampilPsc(); emptPsc(); }
    }
    private void gantiPsc() {
        if (tbPsc.getSelectedRow() == -1) { pilih(); return; }
        if (Sequel.queryu2tf("delete from catatan_sedasi_pasca where no_rawat=? and tanggal=?", 2, key(tbPsc))) simpanPsc();
    }
    private void hapusPsc() {
        if (tbPsc.getSelectedRow() == -1) { pilih(); return; }
        if (!konfirm()) return;
        if (Sequel.queryu2tf("delete from catatan_sedasi_pasca where no_rawat=? and tanggal=?", 2, key(tbPsc))) { tampilPsc(); emptPsc(); }
    }
    private void tampilPsc() {
        Valid.tabelKosong(mdPsc);
        try {
            ps = koneksi.prepareStatement("select s.no_rawat, s.tanggal, IFNULL(pr.nama,'') perawat, s.kesadaran, s.airway, s.td, s.nadi, s.nadi_reg, s.rr, s.kanul, s.o2, s.spo2, s.aldrete, "
                + "s.keluar_jam, s.pindah_ke, s.ins_pemantauan, s.ins_posisi, s.ins_nyeri, s.ins_mual_muntah, s.ins_diet, s.ins_obat_lain, s.ins_lain "
                + "from catatan_sedasi_pasca s left join petugas pr on s.kd_perawat_ruangan=pr.nip where s.no_rawat=? and s.tanggal between ? and ? order by s.tanggal desc");
            ps.setString(1, TNoRw.getText()); ps.setString(2, cari1()); ps.setString(3, cari2());
            rs = ps.executeQuery();
            while (rs.next()) mdPsc.addRow(row(rs, 22));
        } catch (Exception e) { System.out.println("Notif Pasca : " + e); } finally { tutup(); }
    }
    private void getDataPsc() {
        if (tbPsc.getSelectedRow() == -1) return;
        try { PsTgl.setDate(fmt().parse(tbPsc.getValueAt(tbPsc.getSelectedRow(), 1).toString())); } catch (Exception e) {}
        try {
            ps = koneksi.prepareStatement("select * from catatan_sedasi_pasca where no_rawat=? and tanggal=?");
            ps.setString(1, tbPsc.getValueAt(tbPsc.getSelectedRow(), 0).toString());
            ps.setString(2, tbPsc.getValueAt(tbPsc.getSelectedRow(), 1).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                KdPerawat.setText(rs.getString("kd_perawat_ruangan")); NmPerawat.setText(nmPtg(rs.getString("kd_perawat_ruangan")));
                PsKesadaran.setText(rs.getString("kesadaran")); cmbPsAirway.setSelectedItem(rs.getString("airway"));
                PsTd.setText(rs.getString("td")); PsNadi.setText(rs.getString("nadi")); cmbPsNadiReg.setSelectedItem(rs.getString("nadi_reg"));
                PsRr.setText(rs.getString("rr")); cmbPsKanul.setSelectedItem(rs.getString("kanul")); PsO2.setText(rs.getString("o2")); PsSpo2.setText(rs.getString("spo2"));
                PsAldrete.setText(rs.getString("aldrete")); PsKeluarJam.setText(rs.getString("keluar_jam")); cmbPsPindah.setSelectedItem(rs.getString("pindah_ke"));
                PsInsPantau.setText(rs.getString("ins_pemantauan")); PsInsPosisi.setText(rs.getString("ins_posisi")); PsInsNyeri.setText(rs.getString("ins_nyeri"));
                PsInsMual.setText(rs.getString("ins_mual_muntah")); PsInsDiet.setText(rs.getString("ins_diet")); PsInsObat.setText(rs.getString("ins_obat_lain")); PsInsLain.setText(rs.getString("ins_lain"));
            }
        } catch (Exception e) { System.out.println("Notif getData Pasca : " + e); } finally { tutup(); }
    }
    private void emptPsc() {
        PsTgl.setDate(new Date()); KdPerawat.setText(""); NmPerawat.setText(""); PsKesadaran.setText(""); cmbPsAirway.setSelectedIndex(0);
        PsTd.setText(""); PsNadi.setText(""); cmbPsNadiReg.setSelectedIndex(0); PsRr.setText(""); cmbPsKanul.setSelectedIndex(0); PsO2.setText(""); PsSpo2.setText("");
        PsAldrete.setText(""); PsKeluarJam.setText(""); cmbPsPindah.setSelectedIndex(0);
        PsInsPantau.setText(""); PsInsPosisi.setText(""); PsInsNyeri.setText(""); PsInsMual.setText(""); PsInsDiet.setText(""); PsInsObat.setText(""); PsInsLain.setText("");
    }

    // ══════════════════ MONITORING (inner class, dipakai 2×) ══════════════════
    private class MonTab {
        final String table, judul; final boolean full;
        widget.Tanggal tgl;
        TextBox jamMon; // jam (HH:mm:ss) yang bisa diedit petugas
        TextBox o2, spo2, infus, sistol, diastol, nadi, rr, suhu, ket, medikasi, perdarahan, urine;
        widget.Table grid; DefaultTableModel md;
        MonTab(String table, String judul, boolean full) { this.table = table; this.judul = judul; this.full = full; }
        JPanel build() {
            PanelBiasa f = new PanelBiasa(); f.setPreferredSize(new Dimension(950, full ? 150 : 90)); f.setLayout(null);
            // Semua textbox dibuat (dipakai simpan positional); yg tak relevan utk
            // Monitoring Pasca tidak ditampilkan (nilainya kosong).
            o2 = tb(45); spo2 = tb(45); infus = tb(120); sistol = tb(50); diastol = tb(50);
            nadi = tb(50); rr = tb(45); suhu = tb(45); ket = tb(510); medikasi = tb(300); perdarahan = tb(80); urine = tb(80);
            int y = 8;
            f.add(sec(judul.toUpperCase() + " (input berkali-kali)", y)); y += 24;
            f.add(lbl("Tanggal :", 10, y, 110)); tgl = dt(); tgl.setBounds(125, y, 150, 23); f.add(tgl);
            f.add(lbl("Jam :", 285, y, 32)); jamMon = tb(70); jamMon.setBounds(320, y, 70, 23); jamMon.setText(jamNow()); f.add(jamMon);
            y += 26;
            // Baris vital dasar (selalu): Tensi (Sistol/Diastol), Nadi, RR, Suhu
            f.add(lbl("Tensi — Sistol :", 10, y, 85)); sistol.setBounds(95, y, 50, 23); f.add(sistol);
            f.add(lbl("Diastol :", 155, y, 50)); diastol.setBounds(205, y, 50, 23); f.add(diastol);
            f.add(lbl("Nadi :", 265, y, 40)); nadi.setBounds(305, y, 50, 23); f.add(nadi);
            f.add(lbl("RR :", 365, y, 30)); rr.setBounds(395, y, 45, 23); f.add(rr);
            f.add(lbl("Suhu :", 450, y, 40)); suhu.setBounds(490, y, 45, 23); f.add(suhu);
            if (full) {
                f.add(lbl("SPO2 :", 545, y, 42)); spo2.setBounds(587, y, 45, 23); f.add(spo2);
                f.add(lbl("O2 :", 642, y, 28)); o2.setBounds(670, y, 45, 23); f.add(o2);
                y += 26;
                f.add(lbl("Infus :", 10, y, 55)); infus.setBounds(65, y, 120, 23); f.add(infus);
                f.add(lbl("Keterangan :", 200, y, 75)); ket.setBounds(275, y, 510, 23); f.add(ket);
                y += 26;
                f.add(lbl("Medikasi :", 10, y, 55)); medikasi.setBounds(65, y, 300, 23); f.add(medikasi);
                f.add(lbl("Keluaran — Perdarahan :", 375, y, 130)); perdarahan.setBounds(505, y, 80, 23); f.add(perdarahan);
                f.add(lbl("Urine :", 595, y, 40)); urine.setBounds(635, y, 80, 23); f.add(urine);
                md = model(new String[]{"No.Rawat", "Tanggal", "TD", "Nadi", "RR", "SPO2", "O2", "Suhu", "Infus", "Keterangan", "Medikasi", "Perdarahan", "Urine"});
            } else {
                md = model(new String[]{"No.Rawat", "Tanggal", "Tensi", "Nadi", "RR", "Suhu"});
            }
            grid = tabel(md, evt -> getData());
            return tabWrap(f, grid, this::simpan, this::empt, this::hapus, this::ganti);
        }
        void simpan() {
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
            if (Sequel.menyimpantf(table, ph(15), judul, 15, new String[]{
                TNoRw.getText(), jamEdit(tgl, jamMon), akses.getkode(), o2.getText(), spo2.getText(), infus.getText(),
                sistol.getText(), diastol.getText(), nadi.getText(), rr.getText(), suhu.getText(), ket.getText(),
                medikasi.getText(), perdarahan.getText(), urine.getText()
            })) { tampil(); empt(); }
        }
        void ganti() { if (grid.getSelectedRow() == -1) { pilih(); return; } if (Sequel.queryu2tf("delete from " + table + " where no_rawat=? and tanggal=?", 2, key(grid))) simpan(); }
        void hapus() { if (grid.getSelectedRow() == -1) { pilih(); return; } if (!konfirm()) return; if (Sequel.queryu2tf("delete from " + table + " where no_rawat=? and tanggal=?", 2, key(grid))) { tampil(); empt(); } }
        void tampil() {
            Valid.tabelKosong(md);
            try {
                ps = koneksi.prepareStatement("select no_rawat, tanggal, sistol, diastol, nadi, rr, spo2, o2, suhu, infus, keterangan, medikasi, perdarahan, urine from " + table + " where no_rawat=? and tanggal between ? and ? order by tanggal");
                ps.setString(1, TNoRw.getText()); ps.setString(2, cari1()); ps.setString(3, cari2());
                rs = ps.executeQuery();
                while (rs.next()) {
                    String td = rs.getString(3) + "/" + rs.getString(4);
                    if (full) md.addRow(new String[]{ rs.getString(1), rs.getString(2), td, rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14) });
                    else md.addRow(new String[]{ rs.getString(1), rs.getString(2), td, rs.getString(5), rs.getString(6), rs.getString(9) });
                }
            } catch (Exception e) { System.out.println("Notif " + judul + " : " + e); } finally { tutup(); }
        }
        void getData() {
            if (grid.getSelectedRow() == -1) return;
            String tglStr = grid.getValueAt(grid.getSelectedRow(), 1).toString();
            try { tgl.setDate(fmt().parse(tglStr)); } catch (Exception e) {}
            try { jamMon.setText(tglStr.length() >= 19 ? tglStr.substring(11, 19) : jamNow()); } catch (Exception e) { jamMon.setText(jamNow()); }
            try {
                ps = koneksi.prepareStatement("select * from " + table + " where no_rawat=? and tanggal=?");
                ps.setString(1, grid.getValueAt(grid.getSelectedRow(), 0).toString());
                ps.setString(2, grid.getValueAt(grid.getSelectedRow(), 1).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    o2.setText(rs.getString("o2")); spo2.setText(rs.getString("spo2")); infus.setText(rs.getString("infus"));
                    sistol.setText(rs.getString("sistol")); diastol.setText(rs.getString("diastol")); nadi.setText(rs.getString("nadi"));
                    rr.setText(rs.getString("rr")); suhu.setText(rs.getString("suhu")); ket.setText(rs.getString("keterangan"));
                    medikasi.setText(rs.getString("medikasi")); perdarahan.setText(rs.getString("perdarahan")); urine.setText(rs.getString("urine"));
                }
            } catch (Exception e) { System.out.println("Notif getData " + judul + " : " + e); } finally { tutup(); }
        }
        void empt() {
            tgl.setDate(new Date()); jamMon.setText(jamNow()); o2.setText(""); spo2.setText(""); infus.setText(""); sistol.setText("");
            diastol.setText(""); nadi.setText(""); rr.setText(""); suhu.setText(""); ket.setText("");
            medikasi.setText(""); perdarahan.setText(""); urine.setText("");
        }
    }

    // ══════════════════ UMUM ══════════════════
    private void tampilSemua() { if (!TNoRw.getText().trim().equals("")) { tampilPra(); tampilSel(); tampilPsc(); mon1.tampil(); mon2.tampil(); } }

    private void cetak() {
        if (TNoRw.getText().trim().equals("")) { JOptionPane.showMessageDialog(null, "Belum ada pasien..!!"); return; }
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
        param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("no_rawat", TNoRw.getText());
        Valid.MyReport("rptAnestesiSedasiRscb.jasper", "report", "::[ Anestesi Sedasi ]::", param);
    }

    public void setNoRm(String noRawat, String noRM, String nama) {
        TNoRw.setText(noRawat); TNoRM.setText(noRM); TPasien.setText(nama);
        TTglLahir.setText(Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", noRM));
        TJK.setText(Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", noRM));
        emptPra(); emptSel(); emptPsc(); mon1.empt(); mon2.empt();
        DTPCari1.setDate(new Date()); DTPCari2.setDate(new Date());
        tampilSemua();
    }

    public void isCek() { /* akses mengikuti tombol pemanggil */ }

    // ---- helper widget ----
    private TextBox ro(int w) { TextBox t = new TextBox(); t.setPreferredSize(new Dimension(w, 23)); t.setEditable(false); return t; }
    private TextBox tb(int w) { TextBox t = new TextBox(); t.setPreferredSize(new Dimension(w, 23)); return t; }
    private widget.Tanggal dt() { widget.Tanggal t = new widget.Tanggal(); t.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); t.setDate(new Date()); return t; }
    private Label mkLabel(String t) { Label l = new Label(); l.setText(t); return l; }
    private Label lbl(String t, int x, int y, int w) { Label l = new Label(); l.setText(t); l.setBounds(x, y, w, 23); return l; }
    private JLabel sec(String t, int y) { JLabel l = new JLabel(t); l.setFont(new java.awt.Font("Tahoma", 1, 11)); l.setBounds(10, y, 520, 20); return l; }
    private CekBox ck(String t, int x, int y, int w, PanelBiasa f) { CekBox c = new CekBox(); c.setText(t); c.setBounds(x, y, w, 23); c.setSelected(false); c.setOpaque(false); f.add(c); return c; }
    private ComboBox combo(String[] opts, int x, int y, int w, PanelBiasa f) { ComboBox c = new ComboBox(); c.setModel(new DefaultComboBoxModel(opts)); c.setBounds(x, y, w, 23); f.add(c); return c; }
    private Button klip() { Button b = new Button(); try { b.setIcon(new ImageIcon(getClass().getResource("/picture/attachment.png"))); } catch (Exception e) {} return b; }
    private Button btn(String teks, char mn, String ikon) {
        Button b = new Button(); b.setText(teks); b.setMnemonic(mn); b.setPreferredSize(new Dimension(100, 30));
        try { b.setIcon(new ImageIcon(getClass().getResource(ikon))); } catch (Exception e) {}
        return b;
    }
    private DefaultTableModel model(String[] cols) { return new DefaultTableModel(new Object[][]{}, cols) { @Override public boolean isCellEditable(int r, int c) { return false; } }; }
    private widget.Table tabel(DefaultTableModel md, java.util.function.Consumer<java.awt.event.MouseEvent> onClick) {
        widget.Table t = new widget.Table(); t.setModel(md); t.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        t.addMouseListener(new java.awt.event.MouseAdapter() { @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (md.getRowCount() != 0) onClick.accept(e); } });
        return t;
    }
    /** Bungkus 1 tab: input (scroll) NORTH, tabel CENTER, tombol CRUD SOUTH. */
    private JPanel tabWrap(PanelBiasa input, widget.Table tabel, Runnable simpan, Runnable baru, Runnable hapus, Runnable ganti) {
        JPanel p = new JPanel(new BorderLayout());
        ScrollPane sIn = new ScrollPane(); sIn.setViewportView(input);
        sIn.setPreferredSize(new Dimension(950, input.getPreferredSize().height + 8));
        ScrollPane sTb = new ScrollPane(); sTb.setViewportView(tabel);
        panelisi tb = new panelisi(); tb.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6)); tb.setPreferredSize(new Dimension(44, 42));
        Button bS = btn("Simpan", 'S', "/picture/save-16x16.png"); bS.addActionListener(e -> simpan.run());
        Button bB = btn("Baru", 'B', "/picture/Cancel-2-16x16.png"); bB.addActionListener(e -> baru.run());
        Button bG = btn("Ganti", 'G', "/picture/inventaris.png"); bG.addActionListener(e -> ganti.run());
        Button bH = btn("Hapus", 'H', "/picture/stop_f2.png"); bH.addActionListener(e -> hapus.run());
        tb.add(bS); tb.add(bB); tb.add(bG); tb.add(bH);
        p.add(sIn, BorderLayout.NORTH); p.add(sTb, BorderLayout.CENTER); p.add(tb, BorderLayout.SOUTH);
        return p;
    }

    // ---- helper data ----
    private String ph(int n) { StringBuilder sb = new StringBuilder(); for (int i = 0; i < n; i++) sb.append(i == 0 ? "?" : ",?"); return sb.toString(); }
    private String jam(widget.Tanggal t) { return Valid.SetTgl(t.getSelectedItem() + "") + " " + t.getSelectedItem().toString().substring(11, 19); }
    private String jamNow() { return new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()); }
    // Gabung tanggal (dari picker) + jam yang bisa diedit petugas (TextBox HH:mm:ss).
    private String jamEdit(widget.Tanggal t, TextBox jamBox) {
        String j = jamBox.getText().trim();
        if (j.isEmpty()) j = jamNow();
        return Valid.SetTgl(t.getSelectedItem() + "") + " " + j;
    }
    private String cmb(ComboBox c) { return c.getSelectedItem() == null ? "" : c.getSelectedItem().toString(); }
    private String yn(CekBox c) { return c.isSelected() ? "Ya" : "Tidak"; }
    private boolean ya(java.sql.ResultSet r, String col) throws Exception { return "Ya".equals(r.getString(col)); }
    private String[] key(widget.Table t) { return new String[]{ t.getValueAt(t.getSelectedRow(), 0).toString(), t.getValueAt(t.getSelectedRow(), 1).toString() }; }
    private String cari1() { return Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00"; }
    private String cari2() { return Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59"; }
    private java.text.SimpleDateFormat fmt() { return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); }
    private String[] row(java.sql.ResultSet r, int n) throws Exception { String[] a = new String[n]; for (int i = 0; i < n; i++) a[i] = r.getString(i + 1); return a; }
    private String nmPtg(String nip) { return Sequel.cariIsi("select nama from petugas where nip=?", nip); }
    private String nipByNama(String nama) { return Sequel.cariIsi("select nip from petugas where nama=? limit 1", nama); }
    private void setDok(String kd) { KdDokter.setText(kd); NmDokter.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kd)); }
    private void pilih() { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data pada tabel...!!!!"); }
    private boolean konfirm() { return JOptionPane.showConfirmDialog(this, "Hapus data terpilih?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; }
    private void tutup() { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

    private void bindDokter() {
        BtnDokter.addActionListener(evt -> {
            dokterDlg.emptTeks(); dokterDlg.isCek();
            dokterDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokterDlg.setLocationRelativeTo(internalFrame1); dokterDlg.setVisible(true);
        });
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDlg.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }
    private void bindPetugas(Button b, final kepegawaian.DlgCariPetugas dlg, final TextBox kd, final TextBox nm) {
        b.addActionListener(evt -> { dlg.setLocationRelativeTo(null); dlg.setVisible(true); });
        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dlg.getTable().getSelectedRow() != -1) {
                    kd.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                    nm.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }
}
