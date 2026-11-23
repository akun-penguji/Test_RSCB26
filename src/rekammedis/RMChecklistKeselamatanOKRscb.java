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
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

/**
 * Checklist Keselamatan Operasi (WHO Surgical Safety Checklist) — 3 fase terpisah
 * (Sign In / Time Out / Sign Out) dalam 3 tab, MASING-MASING disimpan ke tabel
 * Khanza native yang SAMA dengan e-Nakes:
 *   - signin_sebelum_anestesi (+ signin_sebelum_anestesi_implan)
 *   - timeout_sebelum_insisi
 *   - signout_sebelum_menutup_luka
 * Sehingga data desktop &amp; e-Nakes (HP) saling nyambung.
 */
public class RMChecklistKeselamatanOKRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;
    private TabPane TabRawat;

    private widget.Table tbData;
    private DefaultTableModel tabMode;

    // ── SIGN IN ──
    private widget.Tanggal TglSignIn;
    private TextBox SiTindakan, SiKdBedah, SiNmBedah, SiKdAnestesi, SiNmAnestesi, SiKdPerawat, SiNmPerawat, SiJenisImplan;
    private CekBox SiIdentitas, SiPenandaan, SiMesin, SiPulse, SiAlergi, SiNapas, SiPerdarahan, SiImplan;
    private Button SiBtnBedah, SiBtnAnestesi, SiBtnPerawat;

    // ── TIME OUT ──
    private widget.Tanggal TglTimeOut;
    private TextBox ToTindakan, ToKdBedah, ToNmBedah, ToKdAnestesi, ToNmAnestesi, ToKdPerawat, ToNmPerawat;
    private TextBox ToTimAsisten, ToTimInstrumen, ToTimSirkuler, ToTimPenata, ToProfilaksisObat, ToLama, ToKasa, ToInstrumen, ToJarum;
    private CekBox ToPerkenalan, ToKonfirmasi, ToAntibiotik, ToHalKhusus, ToPerdarahan, ToKekhawatiran, ToSteril, ToPeralatan, ToRadiologi;
    private Button ToBtnBedah, ToBtnAnestesi, ToBtnPerawat;

    // ── SIGN OUT ──
    private widget.Tanggal TglSignOut;
    private TextBox SoTindakan, SoKdBedah, SoNmBedah, SoKdAnestesi, SoNmAnestesi, SoKdPerawat, SoNmPerawat, SoKasa, SoInstrumen, SoJarum, SoPerhatian;
    private CekBox SoVerbalTindakan, SoVerbalKasa, SoVerbalInstrumen, SoVerbalTajam, SoSpecimen;
    private Button SoBtnBedah, SoBtnAnestesi, SoBtnPerawat;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private int pilihan = 0; // 11/12/13 signin bedah/anestesi/perawat, 21/22/23 timeout, 31/32/33 signout
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    public RMChecklistKeselamatanOKRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(950, 720);
        setLocationRelativeTo(null);
    }

    private CekBox chk(String txt) {
        CekBox c = new CekBox(); c.setText(txt); c.setHorizontalAlignment(SwingConstants.LEFT); return c;
    }
    private void addDokterPicker(PanelBiasa p, int y, Label lbl, TextBox kd, TextBox nm, Button btn, int mode) {
        lbl.setBounds(10, y, 120, 23); p.add(lbl);
        kd.setBounds(135, y, 80, 23); p.add(kd);
        nm.setBounds(217, y, 220, 23); p.add(nm);
        btn.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        btn.setBounds(439, y, 28, 23); p.add(btn);
        btn.addActionListener(evt -> { pilihan = mode;
            if (mode % 10 == 3) { petugas.emptTeks(); petugas.isCek(); petugas.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20); petugas.setLocationRelativeTo(internalFrame1); petugas.setVisible(true); }
            else { dokter.emptTeks(); dokter.isCek(); dokter.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20); dokter.setLocationRelativeTo(internalFrame1); dokter.setVisible(true); }
        });
    }
    private ScrollPane wrap(PanelBiasa p, int h) {
        p.setPreferredSize(new Dimension(900, h)); p.setLayout(null);
        ScrollPane s = new ScrollPane(); s.setViewportView(p); return s;
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Checklist Keselamatan Operasi ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS (No.Rawat bersama)
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23)); TNoRw.setEditable(false);
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23)); TNoRM.setEditable(false);
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(220, 23)); TPasien.setEditable(false);
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // ── TAB SIGN IN ──
        PanelBiasa pSi = new PanelBiasa(); int y = 12;
        TglSignIn = new widget.Tanggal(); TglSignIn.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        Label lS0 = new Label(); lS0.setText("Tanggal/Jam :"); lS0.setBounds(10, y, 120, 23); pSi.add(lS0);
        TglSignIn.setBounds(135, y, 150, 23); pSi.add(TglSignIn);
        y += 28; Label lS1 = new Label(); lS1.setText("Nama Tindakan :"); lS1.setBounds(10, y, 120, 23); pSi.add(lS1);
        SiTindakan = new TextBox(); SiTindakan.setBounds(135, y, 400, 23); pSi.add(SiTindakan);
        y += 32;
        JLabel hS = new JLabel("SIGN IN — Sebelum Induksi Anestesi"); hS.setFont(new java.awt.Font("Tahoma", 1, 12)); hS.setBounds(10, y, 500, 20); pSi.add(hS);
        JSeparator spS = new JSeparator(); spS.setBounds(10, y+22, 870, 2); pSi.add(spS);
        y += 30; SiIdentitas = chk("Identitas pasien telah dikonfirmasi"); SiIdentitas.setBounds(30, y, 500, 22); pSi.add(SiIdentitas);
        y += 24; SiPenandaan = chk("Sisi/area operasi telah ditandai"); SiPenandaan.setBounds(30, y, 500, 22); pSi.add(SiPenandaan);
        y += 24; SiMesin = chk("Mesin anestesi & obat telah diperiksa"); SiMesin.setBounds(30, y, 500, 22); pSi.add(SiMesin);
        y += 24; SiPulse = chk("Pulse oximeter terpasang & berfungsi"); SiPulse.setBounds(30, y, 500, 22); pSi.add(SiPulse);
        y += 24; SiAlergi = chk("Alergi diketahui"); SiAlergi.setBounds(30, y, 500, 22); pSi.add(SiAlergi);
        y += 24; SiNapas = chk("Risiko gangguan pernapasan / aspirasi"); SiNapas.setBounds(30, y, 500, 22); pSi.add(SiNapas);
        y += 24; SiPerdarahan = chk("Risiko perdarahan >500ml (7ml/kg pd anak)"); SiPerdarahan.setBounds(30, y, 500, 22); pSi.add(SiPerdarahan);
        y += 26; SiImplan = chk("Perlu peralatan khusus / implan"); SiImplan.setBounds(30, y, 260, 22); pSi.add(SiImplan);
        Label lSj = new Label(); lSj.setText("Jenis :"); lSj.setBounds(295, y, 45, 23); pSi.add(lSj);
        SiJenisImplan = new TextBox(); SiJenisImplan.setBounds(342, y, 250, 23); pSi.add(SiJenisImplan);
        y += 34;
        SiKdBedah = new TextBox(); SiNmBedah = new TextBox(); SiBtnBedah = new Button();
        addDokterPicker(pSi, y, mkLbl("Dokter Bedah :"), SiKdBedah, SiNmBedah, SiBtnBedah, 11); y += 28;
        SiKdAnestesi = new TextBox(); SiNmAnestesi = new TextBox(); SiBtnAnestesi = new Button();
        addDokterPicker(pSi, y, mkLbl("Dokter Anestesi :"), SiKdAnestesi, SiNmAnestesi, SiBtnAnestesi, 12); y += 28;
        SiKdPerawat = new TextBox(); SiNmPerawat = new TextBox(); SiBtnPerawat = new Button();
        addDokterPicker(pSi, y, mkLbl("Perawat OK :"), SiKdPerawat, SiNmPerawat, SiBtnPerawat, 13);
        ScrollPane scSi = wrap(pSi, y + 60);

        // ── TAB TIME OUT ──
        PanelBiasa pTo = new PanelBiasa(); y = 12;
        TglTimeOut = new widget.Tanggal(); TglTimeOut.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        Label lT0 = new Label(); lT0.setText("Tanggal/Jam :"); lT0.setBounds(10, y, 120, 23); pTo.add(lT0);
        TglTimeOut.setBounds(135, y, 150, 23); pTo.add(TglTimeOut);
        y += 28; Label lT1 = new Label(); lT1.setText("Nama Tindakan :"); lT1.setBounds(10, y, 120, 23); pTo.add(lT1);
        ToTindakan = new TextBox(); ToTindakan.setBounds(135, y, 400, 23); pTo.add(ToTindakan);
        y += 32; JLabel hT = new JLabel("TIME OUT — Sebelum Insisi"); hT.setFont(new java.awt.Font("Tahoma", 1, 12)); hT.setBounds(10, y, 500, 20); pTo.add(hT);
        JSeparator spT = new JSeparator(); spT.setBounds(10, y+22, 870, 2); pTo.add(spT);
        y += 30; ToPerkenalan = chk("Semua tim memperkenalkan diri (nama & peran)"); ToPerkenalan.setBounds(30, y, 520, 22); pTo.add(ToPerkenalan);
        y += 24; ToKonfirmasi = chk("Konfirmasi nama pasien, tindakan, area insisi"); ToKonfirmasi.setBounds(30, y, 520, 22); pTo.add(ToKonfirmasi);
        y += 24; ToAntibiotik = chk("Profilaksis antibiotik <60 menit terakhir"); ToAntibiotik.setBounds(30, y, 360, 22); pTo.add(ToAntibiotik);
        Label lTo = new Label(); lTo.setText("Obat :"); lTo.setBounds(395, y, 40, 23); pTo.add(lTo);
        ToProfilaksisObat = new TextBox(); ToProfilaksisObat.setBounds(437, y, 200, 23); pTo.add(ToProfilaksisObat);
        y += 24; ToHalKhusus = chk("dr. Bedah: hal khusus / langkah kritis"); ToHalKhusus.setBounds(30, y, 360, 22); pTo.add(ToHalKhusus);
        Label lTl = new Label(); lTl.setText("Lama operasi :"); lTl.setBounds(395, y, 90, 23); pTo.add(lTl);
        ToLama = new TextBox(); ToLama.setBounds(487, y, 150, 23); pTo.add(ToLama);
        y += 24; ToPerdarahan = chk("dr. Bedah: antisipasi perdarahan"); ToPerdarahan.setBounds(30, y, 520, 22); pTo.add(ToPerdarahan);
        y += 24; ToKekhawatiran = chk("dr. Anestesi: ada kekhawatiran khusus"); ToKekhawatiran.setBounds(30, y, 520, 22); pTo.add(ToKekhawatiran);
        y += 24; ToSteril = chk("Perawat: kesterilan alat dipastikan (termasuk hasil indikator)"); ToSteril.setBounds(30, y, 520, 22); pTo.add(ToSteril);
        y += 24; ToPeralatan = chk("Perawat: kesiapan / kondisi peralatan"); ToPeralatan.setBounds(30, y, 520, 22); pTo.add(ToPeralatan);
        y += 24; ToRadiologi = chk("Perawat: hasil radiologi tersedia"); ToRadiologi.setBounds(30, y, 520, 22); pTo.add(ToRadiologi);
        y += 28; Label lTt = new Label(); lTt.setText("Tim — Asisten:"); lTt.setBounds(30, y, 90, 23); pTo.add(lTt);
        ToTimAsisten = new TextBox(); ToTimAsisten.setBounds(122, y, 140, 23); pTo.add(ToTimAsisten);
        Label lTi = new Label(); lTi.setText("Instrumen:"); lTi.setBounds(270, y, 70, 23); pTo.add(lTi);
        ToTimInstrumen = new TextBox(); ToTimInstrumen.setBounds(342, y, 140, 23); pTo.add(ToTimInstrumen);
        y += 26; Label lTs = new Label(); lTs.setText("Sirkuler:"); lTs.setBounds(30, y, 90, 23); pTo.add(lTs);
        ToTimSirkuler = new TextBox(); ToTimSirkuler.setBounds(122, y, 140, 23); pTo.add(ToTimSirkuler);
        Label lTp = new Label(); lTp.setText("Penata:"); lTp.setBounds(270, y, 70, 23); pTo.add(lTp);
        ToTimPenata = new TextBox(); ToTimPenata.setBounds(342, y, 140, 23); pTo.add(ToTimPenata);
        y += 28; Label lTk = new Label(); lTk.setText("Jml Kasa:"); lTk.setBounds(30, y, 70, 23); pTo.add(lTk);
        ToKasa = new TextBox(); ToKasa.setBounds(102, y, 70, 23); pTo.add(ToKasa);
        Label lTn = new Label(); lTn.setText("Instrumen:"); lTn.setBounds(185, y, 70, 23); pTo.add(lTn);
        ToInstrumen = new TextBox(); ToInstrumen.setBounds(257, y, 70, 23); pTo.add(ToInstrumen);
        Label lTj = new Label(); lTj.setText("Jarum:"); lTj.setBounds(340, y, 50, 23); pTo.add(lTj);
        ToJarum = new TextBox(); ToJarum.setBounds(392, y, 70, 23); pTo.add(ToJarum);
        y += 34;
        ToKdBedah = new TextBox(); ToNmBedah = new TextBox(); ToBtnBedah = new Button();
        addDokterPicker(pTo, y, mkLbl("Dokter Bedah :"), ToKdBedah, ToNmBedah, ToBtnBedah, 21); y += 28;
        ToKdAnestesi = new TextBox(); ToNmAnestesi = new TextBox(); ToBtnAnestesi = new Button();
        addDokterPicker(pTo, y, mkLbl("Dokter Anestesi :"), ToKdAnestesi, ToNmAnestesi, ToBtnAnestesi, 22); y += 28;
        ToKdPerawat = new TextBox(); ToNmPerawat = new TextBox(); ToBtnPerawat = new Button();
        addDokterPicker(pTo, y, mkLbl("Perawat OK :"), ToKdPerawat, ToNmPerawat, ToBtnPerawat, 23);
        ScrollPane scTo = wrap(pTo, y + 60);

        // ── TAB SIGN OUT ──
        PanelBiasa pSo = new PanelBiasa(); y = 12;
        TglSignOut = new widget.Tanggal(); TglSignOut.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        Label lO0 = new Label(); lO0.setText("Tanggal/Jam :"); lO0.setBounds(10, y, 120, 23); pSo.add(lO0);
        TglSignOut.setBounds(135, y, 150, 23); pSo.add(TglSignOut);
        y += 28; Label lO1 = new Label(); lO1.setText("Nama Tindakan :"); lO1.setBounds(10, y, 120, 23); pSo.add(lO1);
        SoTindakan = new TextBox(); SoTindakan.setBounds(135, y, 400, 23); pSo.add(SoTindakan);
        y += 32; JLabel hO = new JLabel("SIGN OUT — Sebelum Menutup Luka"); hO.setFont(new java.awt.Font("Tahoma", 1, 12)); hO.setBounds(10, y, 500, 20); pSo.add(hO);
        JSeparator spO = new JSeparator(); spO.setBounds(10, y+22, 870, 2); pSo.add(spO);
        y += 30; SoVerbalTindakan = chk("Konfirmasi nama tindakan"); SoVerbalTindakan.setBounds(30, y, 500, 22); pSo.add(SoVerbalTindakan);
        y += 24; SoVerbalKasa = chk("Kelengkapan jumlah kasa"); SoVerbalKasa.setBounds(30, y, 500, 22); pSo.add(SoVerbalKasa);
        y += 24; SoVerbalInstrumen = chk("Kelengkapan instrumen"); SoVerbalInstrumen.setBounds(30, y, 500, 22); pSo.add(SoVerbalInstrumen);
        y += 24; SoVerbalTajam = chk("Kelengkapan alat tajam / jarum"); SoVerbalTajam.setBounds(30, y, 500, 22); pSo.add(SoVerbalTajam);
        y += 24; SoSpecimen = chk("Pelabelan spesimen (nama pasien)"); SoSpecimen.setBounds(30, y, 500, 22); pSo.add(SoSpecimen);
        y += 28; Label lOk = new Label(); lOk.setText("Jml Kasa:"); lOk.setBounds(30, y, 70, 23); pSo.add(lOk);
        SoKasa = new TextBox(); SoKasa.setBounds(102, y, 70, 23); pSo.add(SoKasa);
        Label lOn = new Label(); lOn.setText("Instrumen:"); lOn.setBounds(185, y, 70, 23); pSo.add(lOn);
        SoInstrumen = new TextBox(); SoInstrumen.setBounds(257, y, 70, 23); pSo.add(SoInstrumen);
        Label lOj = new Label(); lOj.setText("Jarum:"); lOj.setBounds(340, y, 50, 23); pSo.add(lOj);
        SoJarum = new TextBox(); SoJarum.setBounds(392, y, 70, 23); pSo.add(SoJarum);
        y += 28; Label lOp = new Label(); lOp.setText("Perhatian utama fase pemulihan :"); lOp.setBounds(30, y, 210, 23); pSo.add(lOp);
        SoPerhatian = new TextBox(); SoPerhatian.setBounds(242, y, 395, 23); pSo.add(SoPerhatian);
        y += 34;
        SoKdBedah = new TextBox(); SoNmBedah = new TextBox(); SoBtnBedah = new Button();
        addDokterPicker(pSo, y, mkLbl("Dokter Bedah :"), SoKdBedah, SoNmBedah, SoBtnBedah, 31); y += 28;
        SoKdAnestesi = new TextBox(); SoNmAnestesi = new TextBox(); SoBtnAnestesi = new Button();
        addDokterPicker(pSo, y, mkLbl("Dokter Anestesi :"), SoKdAnestesi, SoNmAnestesi, SoBtnAnestesi, 32); y += 28;
        SoKdPerawat = new TextBox(); SoNmPerawat = new TextBox(); SoBtnPerawat = new Button();
        addDokterPicker(pSo, y, mkLbl("Perawat OK :"), SoKdPerawat, SoNmPerawat, SoBtnPerawat, 33);
        ScrollPane scSo = wrap(pSo, y + 60);

        TabRawat = new TabPane();
        TabRawat.addTab("Sign In (Sebelum Induksi)", scSi);
        TabRawat.addTab("Time Out (Sebelum Insisi)", scTo);
        TabRawat.addTab("Sign Out (Sebelum Menutup Luka)", scSo);
        TabRawat.addChangeListener(e -> tampil());

        // TABLE data
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Dokter Bedah","Dokter Anestesi","Perawat OK"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData = new widget.Table(); tbData.setModel(tabMode);
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); } });
        ScrollPane scrollData = new ScrollPane(); scrollData.setViewportView(tbData);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, TabRawat, scrollData);
        split.setResizeWeight(0.62); split.setDividerLocation(430);

        // PANEL BAWAH
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = mkBtn("Simpan", 'S', "/picture/save-16x16.png", 100);
        BtnBatal = mkBtn("Baru", 'B', "/picture/Cancel-2-16x16.png", 100);
        BtnHapus = mkBtn("Hapus", 'H', "/picture/stop_f2.png", 100);
        BtnKeluar = mkBtn("Keluar", 'K', "/picture/exit.png", 100);
        Label lblTglC = new Label(); lblTglC.setText("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));
        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglC); panelBawah.add(DTPCari1); panelBawah.add(lblSd); panelBawah.add(DTPCari2);
        panelBawah.add(lblKey); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil(); } });
        BtnSimpan.addActionListener(evt -> simpan());
        BtnHapus.addActionListener(evt -> hapus());

        dokter.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokter.getTable().getSelectedRow() == -1) return;
                String kd = dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString();
                String nm = dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString();
                switch (pilihan) {
                    case 11: SiKdBedah.setText(kd); SiNmBedah.setText(nm); break;
                    case 12: SiKdAnestesi.setText(kd); SiNmAnestesi.setText(nm); break;
                    case 21: ToKdBedah.setText(kd); ToNmBedah.setText(nm); break;
                    case 22: ToKdAnestesi.setText(kd); ToNmAnestesi.setText(nm); break;
                    case 31: SoKdBedah.setText(kd); SoNmBedah.setText(nm); break;
                    case 32: SoKdAnestesi.setText(kd); SoNmAnestesi.setText(nm); break;
                }
            } });
        petugas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugas.getTable().getSelectedRow() == -1) return;
                String kd = petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString();
                String nm = petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString();
                switch (pilihan) {
                    case 13: SiKdPerawat.setText(kd); SiNmPerawat.setText(nm); break;
                    case 23: ToKdPerawat.setText(kd); ToNmPerawat.setText(nm); break;
                    case 33: SoKdPerawat.setText(kd); SoNmPerawat.setText(nm); break;
                }
            } });

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(split, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private Label mkLbl(String t) { Label l = new Label(); l.setText(t); return l; }
    private Button mkBtn(String t, char m, String ic, int w) {
        Button b = new Button(); b.setIcon(new ImageIcon(getClass().getResource(ic)));
        b.setMnemonic(m); b.setText(t); b.setPreferredSize(new Dimension(w, 30)); return b;
    }
    private String yn(CekBox c) { return c.isSelected() ? "Ya" : "Tidak"; }
    private String dt(widget.Tanggal t) {
        String s = t.getSelectedItem().toString();
        return Valid.SetTgl(s) + " " + (s.length() >= 19 ? s.substring(11, 19) : "00:00:00");
    }

    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Maaf, pilih pasien dulu..!!"); return; }
        int idx = TabRawat.getSelectedIndex();
        if (idx == 0) { // SIGN IN
            if (SiKdPerawat.getText().trim().isEmpty()) { Valid.textKosong(SiKdPerawat, "Perawat OK"); return; }
            String tgl = dt(TglSignIn);
            Sequel.queryu2tf("delete from signin_sebelum_anestesi where no_rawat=? and tanggal=?", 2, new String[]{TNoRw.getText(), tgl});
            Sequel.queryu2tf("delete from signin_sebelum_anestesi_implan where no_rawat=? and tanggal=?", 2, new String[]{TNoRw.getText(), tgl});
            if (Sequel.menyimpantf("signin_sebelum_anestesi", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
                TNoRw.getText(), tgl, SiTindakan.getText(), SiKdBedah.getText(), SiKdAnestesi.getText(),
                yn(SiIdentitas), yn(SiPenandaan), yn(SiMesin), yn(SiPulse), yn(SiAlergi), yn(SiNapas), yn(SiPerdarahan),
                SiKdPerawat.getText() })) {
                if (SiImplan.isSelected())
                    Sequel.menyimpantf("signin_sebelum_anestesi_implan", "?,?,?,?", "Data", 4, new String[]{
                        TNoRw.getText(), tgl, "Ya", SiJenisImplan.getText() });
                tampil(); emptTeks();
            }
        } else if (idx == 1) { // TIME OUT
            if (ToKdPerawat.getText().trim().isEmpty()) { Valid.textKosong(ToKdPerawat, "Perawat OK"); return; }
            String tgl = dt(TglTimeOut);
            Sequel.queryu2tf("delete from timeout_sebelum_insisi where no_rawat=? and tanggal=?", 2, new String[]{TNoRw.getText(), tgl});
            if (Sequel.menyimpantf("timeout_sebelum_insisi", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 24, new String[]{
                TNoRw.getText(), tgl, ToTindakan.getText(), ToKdBedah.getText(), ToKdAnestesi.getText(),
                yn(ToPerkenalan), ToTimAsisten.getText(), ToTimInstrumen.getText(), ToTimSirkuler.getText(), ToTimPenata.getText(),
                yn(ToKonfirmasi), yn(ToAntibiotik), ToProfilaksisObat.getText(), yn(ToHalKhusus), ToLama.getText(),
                yn(ToPerdarahan), yn(ToKekhawatiran), yn(ToSteril), yn(ToPeralatan), yn(ToRadiologi),
                ToKdPerawat.getText(), ToKasa.getText(), ToInstrumen.getText(), ToJarum.getText() })) {
                tampil(); emptTeks();
            }
        } else { // SIGN OUT
            if (SoKdPerawat.getText().trim().isEmpty()) { Valid.textKosong(SoKdPerawat, "Perawat OK"); return; }
            String tgl = dt(TglSignOut);
            Sequel.queryu2tf("delete from signout_sebelum_menutup_luka where no_rawat=? and tanggal=?", 2, new String[]{TNoRw.getText(), tgl});
            if (Sequel.menyimpantf("signout_sebelum_menutup_luka", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 15, new String[]{
                TNoRw.getText(), tgl, SoTindakan.getText(), SoKdBedah.getText(), SoKdAnestesi.getText(),
                yn(SoVerbalTindakan), yn(SoVerbalKasa), SoKasa.getText(), yn(SoVerbalInstrumen), SoInstrumen.getText(),
                SoJarum.getText(), yn(SoVerbalTajam), yn(SoSpecimen), SoPerhatian.getText(), SoKdPerawat.getText() })) {
                tampil(); emptTeks();
            }
        }
    }

    private void hapus() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, pilih data yang mau dihapus..!!"); return; }
        String tbl = new String[]{"signin_sebelum_anestesi","timeout_sebelum_insisi","signout_sebelum_menutup_luka"}[TabRawat.getSelectedIndex()];
        String norw = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tgl = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        if (Sequel.queryu2tf("delete from " + tbl + " where no_rawat=? and tanggal=?", 2, new String[]{norw, tgl})) {
            if (TabRawat.getSelectedIndex() == 0)
                Sequel.queryu2tf("delete from signin_sebelum_anestesi_implan where no_rawat=? and tanggal=?", 2, new String[]{norw, tgl});
            tampil(); emptTeks();
        }
    }

    public void tampil() {
        if (tabMode == null) return;
        Valid.tabelKosong(tabMode);
        String tbl = new String[]{"signin_sebelum_anestesi","timeout_sebelum_insisi","signout_sebelum_menutup_luka"}[TabRawat.getSelectedIndex()];
        try {
            ps = koneksi.prepareStatement(
                "select h.no_rawat, r.no_rkm_medis, p.nm_pasien, h.tanggal, " +
                "db.nm_dokter as bedah, da.nm_dokter as anestesi, pt.nama as perawat " +
                "from " + tbl + " h " +
                "inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join dokter db on h.kd_dokter_bedah=db.kd_dokter " +
                "left join dokter da on h.kd_dokter_anestesi=da.kd_dokter " +
                "left join petugas pt on h.nip_perawat_ok=pt.nip " +
                "where h.tanggal between ? and ? and (h.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by h.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) tabMode.addRow(new String[]{
                rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                rs.getString("tanggal"), rs.getString("bedah"), rs.getString("anestesi"), rs.getString("perawat") });
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Checklist Keselamatan OK : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        TNoRw.setText(tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
        TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
        TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
        String norw = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tgl = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        int idx = TabRawat.getSelectedIndex();
        String tbl = new String[]{"signin_sebelum_anestesi","timeout_sebelum_insisi","signout_sebelum_menutup_luka"}[idx];
        try {
            java.sql.PreparedStatement p2 = koneksi.prepareStatement("select * from " + tbl + " where no_rawat=? and tanggal=?");
            p2.setString(1, norw); p2.setString(2, tgl);
            java.sql.ResultSet r2 = p2.executeQuery();
            if (r2.next()) {
                Date d = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tgl);
                if (idx == 0) {
                    TglSignIn.setDate(d); SiTindakan.setText(r2.getString("tindakan"));
                    SiKdBedah.setText(r2.getString("kd_dokter_bedah")); SiKdAnestesi.setText(r2.getString("kd_dokter_anestesi"));
                    SiKdPerawat.setText(r2.getString("nip_perawat_ok"));
                    SiIdentitas.setSelected("Ya".equals(r2.getString("identitas")));
                    SiPenandaan.setSelected("Ya".equals(r2.getString("penandaan_area_operasi")));
                    SiMesin.setSelected("Ya".equals(r2.getString("mesin_anestesi")));
                    SiPulse.setSelected("Ya".equals(r2.getString("pulse_oximetri")));
                    SiAlergi.setSelected("Ya".equals(r2.getString("alergi")));
                    SiNapas.setSelected("Ya".equals(r2.getString("gangguan_pernapasan")));
                    SiPerdarahan.setSelected("Ya".equals(r2.getString("resiko_perdarahan")));
                    SiNmBedah.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 4)));
                    SiNmAnestesi.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 5)));
                    SiNmPerawat.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 6)));
                    String imp = Sequel.cariIsi("select jenis from signin_sebelum_anestesi_implan where no_rawat=? and tanggal='" + tgl + "'", norw);
                    SiImplan.setSelected(imp != null && !imp.isEmpty()); SiJenisImplan.setText(imp == null ? "" : imp);
                } else if (idx == 1) {
                    TglTimeOut.setDate(d); ToTindakan.setText(r2.getString("tindakan"));
                    ToKdBedah.setText(r2.getString("kd_dokter_bedah")); ToKdAnestesi.setText(r2.getString("kd_dokter_anestesi"));
                    ToKdPerawat.setText(r2.getString("nip_perawat_ok"));
                    ToPerkenalan.setSelected("Ya".equals(r2.getString("identitas_perkenalan")));
                    ToKonfirmasi.setSelected("Ya".equals(r2.getString("nama_pasien_tindakan_area_insisi")));
                    ToAntibiotik.setSelected("Ya".equals(r2.getString("profilaksis_antibiotik")));
                    ToHalKhusus.setSelected("Ya".equals(r2.getString("hal_khusus_bedah")));
                    ToPerdarahan.setSelected("Ya".equals(r2.getString("antisipasi_perdarahan")));
                    ToKekhawatiran.setSelected("Ya".equals(r2.getString("kekhawatiran_anestesi")));
                    ToSteril.setSelected("Ya".equals(r2.getString("kesterilan_alat")));
                    ToPeralatan.setSelected("Ya".equals(r2.getString("kesiapan_peralatan")));
                    ToRadiologi.setSelected("Ya".equals(r2.getString("hasil_radiologi")));
                    ToProfilaksisObat.setText(nz(r2.getString("profilaksis_obat"))); ToLama.setText(nz(r2.getString("lama_tindakan")));
                    ToTimAsisten.setText(nz(r2.getString("tim_asisten"))); ToTimInstrumen.setText(nz(r2.getString("tim_instrumen")));
                    ToTimSirkuler.setText(nz(r2.getString("tim_sirkuler"))); ToTimPenata.setText(nz(r2.getString("tim_penata")));
                    ToKasa.setText(nz(r2.getString("jumlah_kasa"))); ToInstrumen.setText(nz(r2.getString("jumlah_instrumen"))); ToJarum.setText(nz(r2.getString("jumlah_jarum")));
                    ToNmBedah.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 4)));
                    ToNmAnestesi.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 5)));
                    ToNmPerawat.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 6)));
                } else {
                    TglSignOut.setDate(d); SoTindakan.setText(r2.getString("tindakan"));
                    SoKdBedah.setText(r2.getString("kd_dokter_bedah")); SoKdAnestesi.setText(r2.getString("kd_dokter_anestesi"));
                    SoKdPerawat.setText(r2.getString("nip_perawat_ok"));
                    SoVerbalTindakan.setSelected("Ya".equals(r2.getString("verbal_tindakan")));
                    SoVerbalKasa.setSelected("Ya".equals(r2.getString("verbal_kelengkapan_kasa")));
                    SoVerbalInstrumen.setSelected("Ya".equals(r2.getString("verbal_instrumen")));
                    SoVerbalTajam.setSelected("Ya".equals(r2.getString("verbal_alat_tajam")));
                    SoSpecimen.setSelected("Ya".equals(r2.getString("kelengkapan_specimen_label")));
                    SoKasa.setText(nz(r2.getString("jumlah_kasa"))); SoInstrumen.setText(nz(r2.getString("jumlah_instrumen"))); SoJarum.setText(nz(r2.getString("jumlah_jarum")));
                    SoPerhatian.setText(nz(r2.getString("perhatian_utama_fase_pemulihan")));
                    SoNmBedah.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 4)));
                    SoNmAnestesi.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 5)));
                    SoNmPerawat.setText(nz(tbData.getValueAt(tbData.getSelectedRow(), 6)));
                }
            }
            r2.close(); p2.close();
        } catch (Exception e) { System.out.println("Notif getData Checklist : " + e); }
    }

    private String nz(Object o) { return o == null ? "" : o.toString(); }

    public void emptTeks() {
        TglSignIn.setDate(new Date()); TglTimeOut.setDate(new Date()); TglSignOut.setDate(new Date());
        for (CekBox c : new CekBox[]{SiIdentitas,SiPenandaan,SiMesin,SiPulse,SiAlergi,SiNapas,SiPerdarahan,SiImplan,
            ToPerkenalan,ToKonfirmasi,ToAntibiotik,ToHalKhusus,ToPerdarahan,ToKekhawatiran,ToSteril,ToPeralatan,ToRadiologi,
            SoVerbalTindakan,SoVerbalKasa,SoVerbalInstrumen,SoVerbalTajam,SoSpecimen}) c.setSelected(false);
        for (TextBox t : new TextBox[]{SiTindakan,SiKdBedah,SiNmBedah,SiKdAnestesi,SiNmAnestesi,SiKdPerawat,SiNmPerawat,SiJenisImplan,
            ToTindakan,ToKdBedah,ToNmBedah,ToKdAnestesi,ToNmAnestesi,ToKdPerawat,ToNmPerawat,ToTimAsisten,ToTimInstrumen,ToTimSirkuler,ToTimPenata,ToProfilaksisObat,ToLama,ToKasa,ToInstrumen,ToJarum,
            SoTindakan,SoKdBedah,SoNmBedah,SoKdAnestesi,SoNmAnestesi,SoKdPerawat,SoNmPerawat,SoKasa,SoInstrumen,SoJarum,SoPerhatian}) t.setText("");
    }

    public void setNoRm(String norwt, String norm, String nmpas) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nmpas);
    }
    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglSignIn.setDate(tgl); TglTimeOut.setDate(tgl); TglSignOut.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
    }
}
