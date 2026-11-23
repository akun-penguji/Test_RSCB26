/*
 * By Mas Elkhanza
 */
package rekammedis;

import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMStatusAnestesi extends javax.swing.JDialog {

//    private DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int pilihan = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private StringBuilder htmlContent;
    private String finger = "";
    // Di bagian variables declaration, ganti atau tambahkan:
// Hapus atau comment final dari tabMode jika ada, lalu tambahkan:
    private DefaultTableModel tabModeDataAwal;
    private DefaultTableModel tabModePraInduksi;
    private DefaultTableModel tabModePremedikasi;

    private DefaultTableModel tabModeAnestesiUmumDataAwal;
    private DefaultTableModel tabModeAnestesiUmumMonitoring;
    private DefaultTableModel tabModeAnestesiUmumKeterangan;
    private DefaultTableModel tabModeBlokadeRegional;
    // Blokade Regional — sub-tab Monitoring (programatik, tabel catatan_anestesi_monitoring)
    private DefaultTableModel tabModeRegMon;
    private javax.swing.JTable tbRegMon;
    private javax.swing.JTextField rmTD, rmNadi, rmRR, rmSpo2, rmPerdarahan, rmObat;
    private DefaultTableModel tabModeKeadaanOperasi;
    private DefaultTableModel tabModeSectio;

    private DefaultTableModel tabModeMonitoring;
    private DefaultTableModel tabModeCatatan;
    private DefaultTableModel tabModeMasukKamar;
    private DefaultTableModel tabModeObservasiKamar;
    private DefaultTableModel tabModeKeluarKamar;
    private DefaultTableModel tabModeInstruksiPasca;
    private DefaultTableModel tabModePascaAnestesiMasukKamar;
    private DefaultTableModel tabModePascaAnestesiMonitoring;
    // Sub-tab "Score" (Keadaan Pasca Anestesi) — dibangun programatik (buildScorePemulihan),
    // mandiri (tombol sendiri) agar tidak tergantung dispatch tombol global yang indeks tab-nya bergeser.
    private DefaultTableModel tabModeScore;
    private javax.swing.JTable tbScore;
    private javax.swing.JTextField scoreBromage, scoreAldrete, scoreStewart, scoreTglKeluar;
    private javax.swing.JComboBox<String> scorePindah;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMStatusAnestesi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        sembunyikanFieldDihapus();
        buildRegionalMonitoring();
        buildScorePemulihan();

        setupTableDataAwal();

        TabRawat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TabRawatStateChanged(evt);
            }
        });

        CatatanKamarPemulihan.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CatatanKamarPemulihanStateChanged(evt);
            }
        });
        setupTablePraInduksi();
        setupTablePremedikasi();
        setupTableMonitoring();
        setupTableCatatan();
        setupTableMasukKamar();
        setupTableObservasiKamar();
        setupTableKeluarKamar();
        setupTableInstruksiPasca();

        TabRawat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TabRawatStateChanged(evt);
            }
        });

        CatatanKamarPemulihan.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CatatanKamarPemulihanStateChanged(evt);
            }
        });

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        //       Pernapasan.setDocument(new batasInput((byte)5).getKata(Pernapasan));
        //    TCari.setDocument(new batasInput((int)100).getKata(TCari));

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {

                    if (pilihan == 1) {
                        KdDokterAnastesi.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        NmDokterAnestesi.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    } else if (pilihan == 2) {
                        KdDokterBedah.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        NmDokterBedah.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    }

                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    if (pilihan == 1) {
                        KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    } else if (pilihan == 2) {
                        KdAsistenAnestesi.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmAsistenAnestasi.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    } else if (pilihan == 3) {
                        KdAsistenBedah.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmAsistenBeda.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    }

                }
                KdPetugas.requestFocus();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
                + ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"
                + ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"
                + ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"
                + ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"
                + ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
        jam();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jLabel20 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll1 = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        DataAwal = new widget.InternalFrame();
        Scroll1 = new widget.ScrollPane();
        tbAnestesi = new widget.Table();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        label16 = new widget.Label();
        KdDokterAnastesi = new widget.TextBox();
        NmDokterAnestesi = new widget.TextBox();
        BtnDokter2 = new widget.Button();
        jLabel63 = new widget.Label();
        KdAsistenAnestesi = new widget.TextBox();
        NmAsistenAnestasi = new widget.TextBox();
        btnPetugas1 = new widget.Button();
        jLabel65 = new widget.Label();
        DiagnosaPrabedah = new widget.TextBox();
        jLabel66 = new widget.Label();
        MedikasiPrabedah = new widget.TextBox();
        label17 = new widget.Label();
        KdDokterBedah = new widget.TextBox();
        NmDokterBedah = new widget.TextBox();
        BtnDokter3 = new widget.Button();
        jLabel96 = new widget.Label();
        KdAsistenBedah = new widget.TextBox();
        NmAsistenBeda = new widget.TextBox();
        btnPetugas2 = new widget.Button();
        jLabel97 = new widget.Label();
        JenisAnestesi = new widget.TextBox();
        jLabel113 = new widget.Label();
        InformedConsent = new widget.ComboBox();
        jLabel82 = new widget.Label();
        ObatAnestesi = new widget.ComboBox();
        jLabel128 = new widget.Label();
        jLabel129 = new widget.Label();
        TatalaksanaJalanNafas = new widget.ComboBox();
        jLabel130 = new widget.Label();
        MesinAnestesi = new widget.ComboBox();
        jLabel131 = new widget.Label();
        ObatEmergensi = new widget.ComboBox();
        jLabel132 = new widget.Label();
        CheckMonitoring = new widget.ComboBox();
        jLabel133 = new widget.Label();
        SuctionAparatus = new widget.ComboBox();
        ChkInput = new widget.CekBox();
        Prainduksi = new widget.InternalFrame();
        Scroll2 = new widget.ScrollPane();
        tbAnestesi1 = new widget.Table();
        PanelInput2 = new javax.swing.JPanel();
        ChkInput2 = new widget.CekBox();
        Scroll6 = new widget.ScrollPane();
        FormInput10 = new widget.PanelBiasa();
        jLabel68 = new widget.Label();
        jLabel44 = new widget.Label();
        jLabel79 = new widget.Label();
        jLabel60 = new widget.Label();
        Bb = new widget.TextBox();
        jLabel74 = new widget.Label();
        O2 = new widget.TextBox();
        jLabel98 = new widget.Label();
        jLabel61 = new widget.Label();
        Tensi = new widget.TextBox();
        Metabolik = new widget.TextBox();
        jLabel73 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel32 = new widget.Label();
        ASA = new widget.ComboBox();
        jLabel16 = new widget.Label();
        Muskuloskletal = new widget.TextBox();
        jLabel58 = new widget.Label();
        jLabel62 = new widget.Label();
        Gastro = new widget.TextBox();
        jLabel67 = new widget.Label();
        Respirasi = new widget.TextBox();
        jLabel75 = new widget.Label();
        Spo2 = new widget.TextBox();
        jLabel76 = new widget.Label();
        Hmt = new widget.TextBox();
        jLabel77 = new widget.Label();
        Saraf = new widget.TextBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        Lab = new widget.TextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        Ekg = new widget.TextArea();
        jLabel99 = new widget.Label();
        txtKesadaran = new widget.TextBox();
        jLabel134 = new widget.Label();
        GCS = new widget.TextBox();
        jLabel18 = new widget.Label();
        Airway = new widget.ComboBox();
        jLabel135 = new widget.Label();
        Support1 = new widget.TextBox();
        jLabel136 = new widget.Label();
        jLabel137 = new widget.Label();
        Support2 = new widget.TextBox();
        jLabel138 = new widget.Label();
        jLabel19 = new widget.Label();
        cmbRespirasi = new widget.ComboBox();
        jLabel139 = new widget.Label();
        Rr = new widget.TextBox();
        jLabel83 = new widget.Label();
        Kanul = new widget.ComboBox();
        jLabel26 = new widget.Label();
        jLabel34 = new widget.Label();
        jScrollPane3 = new javax.swing.JScrollPane();
        Thorax = new widget.TextArea();
        jLabel35 = new widget.Label();
        jScrollPane4 = new javax.swing.JScrollPane();
        TFP = new widget.TextArea();
        jLabel36 = new widget.Label();
        jScrollPane5 = new javax.swing.JScrollPane();
        PxLain = new widget.TextArea();
        jLabel37 = new widget.Label();
        jLabel84 = new widget.Label();
        jLabel85 = new widget.Label();
        jLabel86 = new widget.Label();
        jLabel87 = new widget.Label();
        Urinaria = new widget.TextBox();
        jLabel88 = new widget.Label();
        Lain = new widget.TextBox();
        jLabel89 = new widget.Label();
        Alergi = new widget.ComboBox();
        Premedikasi = new widget.InternalFrame();
        Scroll3 = new widget.ScrollPane();
        tbAnestesi2 = new widget.Table();
        PanelInput3 = new javax.swing.JPanel();
        FormInput11 = new widget.PanelBiasa();
        jLabel114 = new widget.Label();
        PernapasanMasuk19 = new widget.TextBox();
        Posisi1 = new widget.ComboBox();
        jLabel101 = new widget.Label();
        SirkulasiMasuk15 = new widget.TextBox();
        jLabel103 = new widget.Label();
        SirkulasiMasuk16 = new widget.TextBox();
        jLabel106 = new widget.Label();
        jLabel109 = new widget.Label();
        SirkulasiMasuk17 = new widget.TextBox();
        jLabel110 = new widget.Label();
        PernapasanMasuk20 = new widget.TextBox();
        Pernapasan15 = new widget.ComboBox();
        jLabel119 = new widget.Label();
        NmObatPremedikasi1 = new widget.TextBox();
        jLabel120 = new widget.Label();
        NamaObatAnestesi = new widget.TextBox();
        JenisAnestesiPremedikasi = new widget.ComboBox();
        JenisPremedikasi1 = new widget.ComboBox();
        jLabel122 = new widget.Label();
        jLabel123 = new widget.Label();
        jLabel140 = new widget.Label();
        jLabel141 = new widget.Label();
        DosisPremedikasi = new widget.TextBox();
        ChkInput3 = new widget.CekBox();
        AnestesiUmum = new javax.swing.JTabbedPane();
        MasukKamar1 = new widget.InternalFrame();
        Scroll10 = new widget.ScrollPane();
        tbAnestesi9 = new widget.Table();
        PanelInput10 = new javax.swing.JPanel();
        FormInput18 = new widget.PanelBiasa();
        label15 = new widget.Label();
        Fio2 = new widget.TextBox();
        jLabel57 = new widget.Label();
        PengaturaNafas = new widget.ComboBox();
        jLabel69 = new widget.Label();
        TeknikKhusus = new widget.ComboBox();
        jLabel13 = new widget.Label();
        jLabel90 = new widget.Label();
        InduksiKet = new widget.TextBox();
        jLabel91 = new widget.Label();
        TidalVolum = new widget.TextBox();
        jLabel92 = new widget.Label();
        Rr2 = new widget.TextBox();
        jLabel93 = new widget.Label();
        jLabel94 = new widget.Label();
        Ieratio = new widget.TextBox();
        Peep = new widget.TextBox();
        jLabel142 = new widget.Label();
        TeknikKhususKet = new widget.TextBox();
        Induksi = new widget.ComboBox();
        jLabel143 = new widget.Label();
        jLabel158 = new widget.Label();
        Pip = new widget.TextBox();
        jLabel159 = new widget.Label();
        ChkInput10 = new widget.CekBox();
        ObservasiKamar1 = new widget.InternalFrame();
        Scroll11 = new widget.ScrollPane();
        tbAnestesi10 = new widget.Table();
        PanelInput11 = new javax.swing.JPanel();
        FormInput19 = new widget.PanelBiasa();
        SirkulasiMasuk14 = new widget.TextBox();
        jLabel100 = new widget.Label();
        PernapasanMasuk12 = new widget.TextBox();
        jLabel102 = new widget.Label();
        PernapasanMasuk14 = new widget.TextBox();
        jLabel104 = new widget.Label();
        jLabel105 = new widget.Label();
        PernapasanMasuk16 = new widget.TextBox();
        jLabel107 = new widget.Label();
        jLabel108 = new widget.Label();
        jLabel111 = new widget.Label();
        jLabel112 = new widget.Label();
        jLabel115 = new widget.Label();
        SirkulasiMasuk18 = new widget.TextBox();
        SirkulasiMasuk19 = new widget.TextBox();
        jLabel116 = new widget.Label();
        SirkulasiMasuk21 = new widget.TextBox();
        PemantauanNadi2 = new widget.TextBox();
        jLabel121 = new widget.Label();
        Lainnya2 = new widget.TextBox();
        Selama2 = new widget.TextBox();
        SirkulasiMasuk22 = new widget.TextBox();
        ChkInput11 = new widget.CekBox();
        AnastesiUmumKeterangan = new widget.InternalFrame();
        Scroll12 = new widget.ScrollPane();
        tbAnestesi11 = new widget.Table();
        PanelInput12 = new javax.swing.JPanel();
        FormInput20 = new widget.PanelBiasa();
        jLabel117 = new widget.Label();
        jLabel118 = new widget.Label();
        jLabel124 = new widget.Label();
        jLabel125 = new widget.Label();
        jScrollPane6 = new javax.swing.JScrollPane();
        Tindakan = new widget.TextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        PemberianCairan = new widget.TextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        Medikasi = new widget.TextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        MasalahDuranteOperasi = new widget.TextArea();
        ChkInput12 = new widget.CekBox();
        BlokadeRegional = new javax.swing.JTabbedPane();
        MasukKamar2 = new widget.InternalFrame();
        Scroll13 = new widget.ScrollPane();
        tbAnestesi12 = new widget.Table();
        PanelInput13 = new javax.swing.JPanel();
        FormInput21 = new widget.PanelBiasa();
        LokasiTusukan = new widget.TextBox();
        jLabel126 = new widget.Label();
        AnalgesiSetinggiSegmen = new widget.TextBox();
        jLabel127 = new widget.Label();
        Teknik = new widget.ComboBox();
        jLabel59 = new widget.Label();
        jLabel146 = new widget.Label();
        AnestesiLokal = new widget.TextBox();
        jLabel147 = new widget.Label();
        KonsentrasiJumlah = new widget.TextBox();
        jLabel148 = new widget.Label();
        ObatTambahan = new widget.TextBox();
        jLabel149 = new widget.Label();
        Dosis = new widget.TextBox();
        jLabel150 = new widget.Label();
        Vasokonstriktor = new widget.ComboBox();
        jLabel151 = new widget.Label();
        Konsentrasi = new widget.TextBox();
        jLabel152 = new widget.Label();
        WaktuMulaiSuntikJam = new widget.TextBox();
        jLabel153 = new widget.Label();
        AnalgesiJam = new widget.TextBox();
        Lamanya = new widget.TextBox();
        jLabel155 = new widget.Label();
        jLabel156 = new widget.Label();
        LamanyaMenitAnalgesi = new widget.TextBox();
        jLabel157 = new widget.Label();
        jLabel154 = new widget.Label();
        OperasiJam = new widget.TextBox();
        LamanyajamOperasi = new widget.TextBox();
        jLabel160 = new widget.Label();
        jLabel161 = new widget.Label();
        LamanyaJamMenit = new widget.TextBox();
        jLabel162 = new widget.Label();
        jLabel163 = new widget.Label();
        TindakanAnestesiTambahan = new widget.TextBox();
        ChkInput13 = new widget.CekBox();
        Monitoring = new widget.InternalFrame();
        Scroll4 = new widget.ScrollPane();
        tbAnestesi3 = new widget.Table();
        PanelInput4 = new javax.swing.JPanel();
        FormInput12 = new widget.PanelBiasa();
        jLabel78 = new widget.Label();
        jLabel70 = new widget.Label();
        BalonNoBalon = new widget.ComboBox();
        Ukuran = new widget.TextBox();
        Kristaloid = new widget.TextBox();
        jLabel71 = new widget.Label();
        LamaOperasi = new widget.TextBox();
        jLabel80 = new widget.Label();
        LetakPenderita = new widget.ComboBox();
        jLabel72 = new widget.Label();
        LamaAnestesi = new widget.TextBox();
        Koloid = new widget.TextBox();
        Airway2 = new widget.ComboBox();
        jLabel144 = new widget.Label();
        jLabel145 = new widget.Label();
        jLabel164 = new widget.Label();
        jLabel165 = new widget.Label();
        jLabel166 = new widget.Label();
        jLabel167 = new widget.Label();
        jLabel168 = new widget.Label();
        jLabel169 = new widget.Label();
        jLabel170 = new widget.Label();
        Darah = new widget.TextBox();
        jLabel171 = new widget.Label();
        KomponenDarah = new widget.TextBox();
        jLabel172 = new widget.Label();
        jLabel173 = new widget.Label();
        Perdarahan = new widget.TextBox();
        Diuresis = new widget.TextBox();
        jLabel174 = new widget.Label();
        jLabel175 = new widget.Label();
        jLabel176 = new widget.Label();
        jLabel177 = new widget.Label();
        jLabel178 = new widget.Label();
        jLabel179 = new widget.Label();
        CairanLain = new widget.TextBox();
        jLabel181 = new widget.Label();
        ChkInput4 = new widget.CekBox();
        Catatan = new widget.InternalFrame();
        Scroll5 = new widget.ScrollPane();
        tbAnestesi4 = new widget.Table();
        PanelInput5 = new javax.swing.JPanel();
        FormInput13 = new widget.PanelBiasa();
        jLabel180 = new widget.Label();
        KeadaanBayi = new widget.ComboBox();
        jLabel182 = new widget.Label();
        Apgrar1Menit = new widget.TextBox();
        jLabel184 = new widget.Label();
        JenisKelamin = new widget.ComboBox();
        jLabel185 = new widget.Label();
        Apgar5Menit = new widget.TextBox();
        jLabel186 = new widget.Label();
        Apgar10Menit = new widget.TextBox();
        ChkInput5 = new widget.CekBox();
        CatatanKamarPemulihan = new javax.swing.JTabbedPane();
        MasukKamar = new widget.InternalFrame();
        Scroll7 = new widget.ScrollPane();
        tbAnestesi5 = new widget.Table();
        PanelInput7 = new javax.swing.JPanel();
        FormInput15 = new widget.PanelBiasa();
        label12 = new widget.Label();
        MasukKamarPulih = new widget.Tanggal();
        jLabel31 = new widget.Label();
        Kesadaran = new widget.ComboBox();
        jLabel12 = new widget.Label();
        jLabel183 = new widget.Label();
        TensiMasukKamar = new widget.TextBox();
        jLabel187 = new widget.Label();
        jLabel188 = new widget.Label();
        NadiMasukKamar = new widget.TextBox();
        jLabel189 = new widget.Label();
        jLabel190 = new widget.Label();
        Support3 = new widget.TextBox();
        jLabel191 = new widget.Label();
        jLabel192 = new widget.Label();
        Support4 = new widget.TextBox();
        jLabel193 = new widget.Label();
        jLabel194 = new widget.Label();
        cmbRespirasiMasukKamar = new widget.ComboBox();
        jLabel197 = new widget.Label();
        KanulMasukKamar = new widget.ComboBox();
        jLabel198 = new widget.Label();
        O2MasukKamar = new widget.TextBox();
        jLabel199 = new widget.Label();
        jLabel200 = new widget.Label();
        Spo2MasukKamar = new widget.TextBox();
        RrMasukKamar = new widget.TextBox();
        jLabel201 = new widget.Label();
        jLabel202 = new widget.Label();
        SkorNyeri = new widget.TextBox();
        PenyulitIntraOperatif = new widget.TextBox();
        jLabel203 = new widget.Label();
        jLabel204 = new widget.Label();
        ChkInput7 = new widget.CekBox();
        ObservasiKamar = new widget.InternalFrame();
        Scroll8 = new widget.ScrollPane();
        tbAnestesi6 = new widget.Table();
        PanelInput8 = new javax.swing.JPanel();
        FormInput16 = new widget.PanelBiasa();
        jLabel38 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel39 = new widget.Label();
        Rr4 = new widget.TextBox();
        Nadi2 = new widget.TextBox();
        jLabel40 = new widget.Label();
        jLabel41 = new widget.Label();
        TekananDarah = new widget.TextBox();
        ChkInput8 = new widget.CekBox();
        panelGlass10 = new widget.panelisi();
        jLabel10 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel11 = new widget.Label();
        Jk = new widget.TextBox();
        jLabel24 = new widget.Label();
        DTPTgl = new widget.Tanggal();
        cmbJam = new widget.ComboBox();
        cmbMnt = new widget.ComboBox();
        cmbDtk = new widget.ComboBox();
        ChkJln = new widget.CekBox();
        jLabel9 = new widget.Label();
        Ruangan = new widget.TextBox();
        jLabel95 = new widget.Label();
        TglMasukRS = new widget.TextBox();
        jLabel64 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        btnPetugas3 = new widget.Button();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Status Anestesi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setPreferredSize(new java.awt.Dimension(467, 500));
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

        jLabel20.setText("Tgl.Asuhan :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(jLabel20);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "28-09-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass8.add(DTPCari2);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass8.add(jLabel21);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "28-09-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass8.add(DTPCari1);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass8.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass8.add(TCari);

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
        panelGlass8.add(BtnCari);

        BtnAll1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll1.setMnemonic('M');
        BtnAll1.setToolTipText("Alt+M");
        BtnAll1.setName("BtnAll1"); // NOI18N
        BtnAll1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAll1ActionPerformed(evt);
            }
        });
        BtnAll1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAll1KeyPressed(evt);
            }
        });
        panelGlass8.add(BtnAll1);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.setPreferredSize(new java.awt.Dimension(457, 480));

        DataAwal.setBorder(null);
        DataAwal.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        DataAwal.setName("DataAwal"); // NOI18N
        DataAwal.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);
        Scroll1.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi.setAutoCreateRowSorter(true);
        tbAnestesi.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi.setName("tbAnestesi"); // NOI18N
        tbAnestesi.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 400));
        tbAnestesi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesiMouseClicked(evt);
            }
        });
        tbAnestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesiKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbAnestesi);

        DataAwal.add(Scroll1, java.awt.BorderLayout.CENTER);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 280));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput.setLayout(null);

        label16.setText("Dokter Anestesi :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label16);
        label16.setBounds(0, 10, 90, 23);

        KdDokterAnastesi.setEditable(false);
        KdDokterAnastesi.setName("KdDokterAnastesi"); // NOI18N
        KdDokterAnastesi.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(KdDokterAnastesi);
        KdDokterAnastesi.setBounds(100, 10, 90, 23);

        NmDokterAnestesi.setEditable(false);
        NmDokterAnestesi.setName("NmDokterAnestesi"); // NOI18N
        NmDokterAnestesi.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokterAnestesi);
        NmDokterAnestesi.setBounds(200, 10, 180, 23);

        BtnDokter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter2.setMnemonic('2');
        BtnDokter2.setToolTipText("Alt+2");
        BtnDokter2.setName("BtnDokter2"); // NOI18N
        BtnDokter2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter2ActionPerformed(evt);
            }
        });
        BtnDokter2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokter2KeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter2);
        BtnDokter2.setBounds(390, 10, 28, 23);

        jLabel63.setText("Penata Anestesi:");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(-10, 40, 110, 23);

        KdAsistenAnestesi.setEditable(false);
        KdAsistenAnestesi.setHighlighter(null);
        KdAsistenAnestesi.setName("KdAsistenAnestesi"); // NOI18N
        KdAsistenAnestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdAsistenAnestesiKeyPressed(evt);
            }
        });
        FormInput.add(KdAsistenAnestesi);
        KdAsistenAnestesi.setBounds(100, 40, 94, 23);

        NmAsistenAnestasi.setEditable(false);
        NmAsistenAnestasi.setName("NmAsistenAnestasi"); // NOI18N
        FormInput.add(NmAsistenAnestasi);
        NmAsistenAnestasi.setBounds(200, 40, 180, 23);

        btnPetugas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas1.setMnemonic('2');
        btnPetugas1.setToolTipText("ALt+2");
        btnPetugas1.setName("btnPetugas1"); // NOI18N
        btnPetugas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugas1ActionPerformed(evt);
            }
        });
        btnPetugas1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugas1KeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas1);
        btnPetugas1.setBounds(390, 40, 28, 23);

        jLabel65.setText("Diagnosa Prabedah:");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(0, 100, 110, 23);

        DiagnosaPrabedah.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah.setName("DiagnosaPrabedah"); // NOI18N
        DiagnosaPrabedah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedahActionPerformed(evt);
            }
        });
        DiagnosaPrabedah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedahKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaPrabedah);
        DiagnosaPrabedah.setBounds(110, 100, 280, 23);

        jLabel66.setText("Checklist Persiapan Anestesi:");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(10, 140, 150, 23);

        MedikasiPrabedah.setFocusTraversalPolicyProvider(true);
        MedikasiPrabedah.setName("MedikasiPrabedah"); // NOI18N
        MedikasiPrabedah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MedikasiPrabedahActionPerformed(evt);
            }
        });
        MedikasiPrabedah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MedikasiPrabedahKeyPressed(evt);
            }
        });
        FormInput.add(MedikasiPrabedah);
        MedikasiPrabedah.setBounds(110, 70, 280, 23);

        label17.setText("Dokter Bedah:");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label17);
        label17.setBounds(460, 10, 90, 23);

        KdDokterBedah.setEditable(false);
        KdDokterBedah.setName("KdDokterBedah"); // NOI18N
        KdDokterBedah.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(KdDokterBedah);
        KdDokterBedah.setBounds(560, 10, 90, 23);

        NmDokterBedah.setEditable(false);
        NmDokterBedah.setName("NmDokterBedah"); // NOI18N
        NmDokterBedah.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokterBedah);
        NmDokterBedah.setBounds(660, 10, 180, 23);

        BtnDokter3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter3.setMnemonic('2');
        BtnDokter3.setToolTipText("Alt+2");
        BtnDokter3.setName("BtnDokter3"); // NOI18N
        BtnDokter3.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter3ActionPerformed(evt);
            }
        });
        BtnDokter3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokter3KeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter3);
        BtnDokter3.setBounds(850, 10, 28, 23);

        jLabel96.setText("Asisten Bedah:");
        jLabel96.setName("jLabel96"); // NOI18N
        FormInput.add(jLabel96);
        jLabel96.setBounds(450, 40, 110, 23);

        KdAsistenBedah.setEditable(false);
        KdAsistenBedah.setHighlighter(null);
        KdAsistenBedah.setName("KdAsistenBedah"); // NOI18N
        KdAsistenBedah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdAsistenBedahKeyPressed(evt);
            }
        });
        FormInput.add(KdAsistenBedah);
        KdAsistenBedah.setBounds(560, 40, 94, 23);

        NmAsistenBeda.setEditable(false);
        NmAsistenBeda.setName("NmAsistenBeda"); // NOI18N
        FormInput.add(NmAsistenBeda);
        NmAsistenBeda.setBounds(660, 40, 180, 23);

        btnPetugas2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas2.setMnemonic('2');
        btnPetugas2.setToolTipText("ALt+2");
        btnPetugas2.setName("btnPetugas2"); // NOI18N
        btnPetugas2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugas2ActionPerformed(evt);
            }
        });
        btnPetugas2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugas2KeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas2);
        btnPetugas2.setBounds(850, 40, 28, 23);

        jLabel97.setText("Jenis Anestesi:");
        jLabel97.setName("jLabel97"); // NOI18N
        FormInput.add(jLabel97);
        jLabel97.setBounds(450, 70, 100, 23);

        JenisAnestesi.setFocusTraversalPolicyProvider(true);
        JenisAnestesi.setName("JenisAnestesi"); // NOI18N
        JenisAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JenisAnestesiActionPerformed(evt);
            }
        });
        JenisAnestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisAnestesiKeyPressed(evt);
            }
        });
        FormInput.add(JenisAnestesi);
        JenisAnestesi.setBounds(560, 70, 280, 23);

        jLabel113.setText("Informed Consent:");
        jLabel113.setName("jLabel113"); // NOI18N
        FormInput.add(jLabel113);
        jLabel113.setBounds(20, 160, 100, 23);

        InformedConsent.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        InformedConsent.setName("InformedConsent"); // NOI18N
        FormInput.add(InformedConsent);
        InformedConsent.setBounds(130, 160, 140, 20);

        jLabel82.setText("Medikasi Prabedah:");
        jLabel82.setName("jLabel82"); // NOI18N
        FormInput.add(jLabel82);
        jLabel82.setBounds(0, 70, 100, 23);

        ObatAnestesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        ObatAnestesi.setName("ObatAnestesi"); // NOI18N
        FormInput.add(ObatAnestesi);
        ObatAnestesi.setBounds(430, 160, 140, 20);

        jLabel128.setText("Obat-obatan Anestesi:");
        jLabel128.setName("jLabel128"); // NOI18N
        FormInput.add(jLabel128);
        jLabel128.setBounds(290, 160, 130, 23);

        jLabel129.setText("Tata laksana jalan nafas:");
        jLabel129.setName("jLabel129"); // NOI18N
        FormInput.add(jLabel129);
        jLabel129.setBounds(580, 160, 130, 23);

        TatalaksanaJalanNafas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        TatalaksanaJalanNafas.setName("TatalaksanaJalanNafas"); // NOI18N
        FormInput.add(TatalaksanaJalanNafas);
        TatalaksanaJalanNafas.setBounds(720, 160, 140, 20);

        jLabel130.setText("Mesin Anestesi:");
        jLabel130.setName("jLabel130"); // NOI18N
        FormInput.add(jLabel130);
        jLabel130.setBounds(-10, 190, 130, 23);

        MesinAnestesi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        MesinAnestesi.setName("MesinAnestesi"); // NOI18N
        FormInput.add(MesinAnestesi);
        MesinAnestesi.setBounds(130, 190, 140, 20);

        jLabel131.setText("Obat-obatan Emergensi:");
        jLabel131.setName("jLabel131"); // NOI18N
        FormInput.add(jLabel131);
        jLabel131.setBounds(290, 190, 130, 23);

        ObatEmergensi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        ObatEmergensi.setName("ObatEmergensi"); // NOI18N
        FormInput.add(ObatEmergensi);
        ObatEmergensi.setBounds(430, 190, 140, 20);

        jLabel132.setText("Monitoring:");
        jLabel132.setName("jLabel132"); // NOI18N
        FormInput.add(jLabel132);
        jLabel132.setBounds(580, 190, 130, 23);

        CheckMonitoring.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        CheckMonitoring.setName("CheckMonitoring"); // NOI18N
        FormInput.add(CheckMonitoring);
        CheckMonitoring.setBounds(720, 190, 140, 20);

        jLabel133.setText("Suction Aparatus:");
        jLabel133.setName("jLabel133"); // NOI18N
        FormInput.add(jLabel133);
        jLabel133.setBounds(0, 220, 130, 23);

        SuctionAparatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        SuctionAparatus.setName("SuctionAparatus"); // NOI18N
        FormInput.add(SuctionAparatus);
        SuctionAparatus.setBounds(130, 220, 140, 20);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        DataAwal.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        TabRawat.addTab("Data Awal ", DataAwal);

        Prainduksi.setBorder(null);
        Prainduksi.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        Prainduksi.setName("Prainduksi"); // NOI18N
        Prainduksi.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);
        Scroll2.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi1.setAutoCreateRowSorter(true);
        tbAnestesi1.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi1.setName("tbAnestesi1"); // NOI18N
        tbAnestesi1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi1MouseClicked(evt);
            }
        });
        tbAnestesi1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi1KeyPressed(evt);
            }
        });
        Scroll2.setViewportView(tbAnestesi1);

        Prainduksi.add(Scroll2, java.awt.BorderLayout.CENTER);

        PanelInput2.setName("PanelInput2"); // NOI18N
        PanelInput2.setOpaque(false);
        PanelInput2.setPreferredSize(new java.awt.Dimension(192, 400));
        PanelInput2.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput2.setMnemonic('I');
        ChkInput2.setText(".: Input Data");
        ChkInput2.setToolTipText("Alt+I");
        ChkInput2.setBorderPainted(true);
        ChkInput2.setBorderPaintedFlat(true);
        ChkInput2.setFocusable(false);
        ChkInput2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput2.setName("ChkInput2"); // NOI18N
        ChkInput2.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput2.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput2ActionPerformed(evt);
            }
        });
        PanelInput2.add(ChkInput2, java.awt.BorderLayout.PAGE_END);

        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);
        Scroll6.setPreferredSize(new java.awt.Dimension(452, 400));

        FormInput10.setBackground(new java.awt.Color(250, 255, 245));
        FormInput10.setName("FormInput10"); // NOI18N
        FormInput10.setPreferredSize(new java.awt.Dimension(100, 600));
        FormInput10.setLayout(null);

        jLabel68.setText("kg");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput10.add(jLabel68);
        jLabel68.setBounds(110, 100, 20, 14);

        jLabel44.setText("x/menit ");
        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput10.add(jLabel44);
        jLabel44.setBounds(850, 10, 60, 23);

        jLabel79.setText("mmHg");
        jLabel79.setName("jLabel79"); // NOI18N
        FormInput10.add(jLabel79);
        jLabel79.setBounds(710, 10, 30, 23);

        jLabel60.setText("BB:");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput10.add(jLabel60);
        jLabel60.setBounds(20, 100, 20, 23);

        Bb.setFocusTraversalPolicyProvider(true);
        Bb.setName("Bb"); // NOI18N
        Bb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BbKeyPressed(evt);
            }
        });
        FormInput10.add(Bb);
        Bb.setBounds(50, 100, 60, 23);

        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel74.setText("l/mnt");
        jLabel74.setName("jLabel74"); // NOI18N
        FormInput10.add(jLabel74);
        jLabel74.setBounds(690, 70, 40, 23);

        O2.setFocusTraversalPolicyProvider(true);
        O2.setName("O2"); // NOI18N
        O2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                O2KeyPressed(evt);
            }
        });
        FormInput10.add(O2);
        O2.setBounds(630, 70, 50, 23);

        jLabel98.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel98.setText("O2:");
        jLabel98.setName("jLabel98"); // NOI18N
        FormInput10.add(jLabel98);
        jLabel98.setBounds(600, 70, 50, 23);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel61.setText("TD:");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput10.add(jLabel61);
        jLabel61.setBounds(590, 10, 50, 23);

        Tensi.setFocusTraversalPolicyProvider(true);
        Tensi.setName("Tensi"); // NOI18N
        Tensi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TensiKeyPressed(evt);
            }
        });
        FormInput10.add(Tensi);
        Tensi.setBounds(620, 10, 80, 23);

        Metabolik.setFocusTraversalPolicyProvider(true);
        Metabolik.setName("Metabolik"); // NOI18N
        Metabolik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MetabolikKeyPressed(evt);
            }
        });
        FormInput10.add(Metabolik);
        Metabolik.setBounds(560, 490, 340, 23);

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel73.setText("Nadi:");
        jLabel73.setName("jLabel73"); // NOI18N
        FormInput10.add(jLabel73);
        jLabel73.setBounds(750, 10, 40, 23);

        Nadi.setFocusTraversalPolicyProvider(true);
        Nadi.setName("Nadi"); // NOI18N
        Nadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiKeyPressed(evt);
            }
        });
        FormInput10.add(Nadi);
        Nadi.setBounds(780, 10, 70, 23);

        jLabel32.setText("Laboratorium:");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput10.add(jLabel32);
        jLabel32.setBounds(0, 130, 90, 23);

        ASA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "E" }));
        ASA.setName("ASA"); // NOI18N
        FormInput10.add(ASA);
        ASA.setBounds(570, 520, 50, 20);

        jLabel16.setText("Status fisik ASA:");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput10.add(jLabel16);
        jLabel16.setBounds(480, 520, 80, 23);

        Muskuloskletal.setFocusTraversalPolicyProvider(true);
        Muskuloskletal.setName("Muskuloskletal"); // NOI18N
        Muskuloskletal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MuskuloskletalKeyPressed(evt);
            }
        });
        FormInput10.add(Muskuloskletal);
        Muskuloskletal.setBounds(130, 490, 330, 23);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel58.setText("EKG:");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput10.add(jLabel58);
        jLabel58.setBounds(490, 130, 40, 23);

        jLabel62.setText("Jam Pra Operasi");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput10.add(jLabel62);
        jLabel62.setBounds(260, 100, 90, 23);

        Gastro.setFocusTraversalPolicyProvider(true);
        Gastro.setName("Gastro"); // NOI18N
        Gastro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GastroKeyPressed(evt);
            }
        });
        FormInput10.add(Gastro);
        Gastro.setBounds(130, 460, 330, 23);

        jLabel67.setText("Sistem metabolik:");
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput10.add(jLabel67);
        jLabel67.setBounds(460, 490, 100, 23);

        Respirasi.setFocusTraversalPolicyProvider(true);
        Respirasi.setName("Respirasi"); // NOI18N
        Respirasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RespirasiActionPerformed(evt);
            }
        });
        Respirasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RespirasiKeyPressed(evt);
            }
        });
        FormInput10.add(Respirasi);
        Respirasi.setBounds(560, 420, 340, 23);

        jLabel75.setText("SPO2:");
        jLabel75.setName("jLabel75"); // NOI18N
        FormInput10.add(jLabel75);
        jLabel75.setBounds(740, 70, 30, 23);

        Spo2.setFocusTraversalPolicyProvider(true);
        Spo2.setName("Spo2"); // NOI18N
        Spo2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Spo2KeyPressed(evt);
            }
        });
        FormInput10.add(Spo2);
        Spo2.setBounds(780, 70, 50, 23);

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel76.setText("Puasa:");
        jLabel76.setName("jLabel76"); // NOI18N
        FormInput10.add(jLabel76);
        jLabel76.setBounds(150, 100, 40, 23);

        Hmt.setFocusTraversalPolicyProvider(true);
        Hmt.setName("Hmt"); // NOI18N
        Hmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HmtKeyPressed(evt);
            }
        });
        FormInput10.add(Hmt);
        Hmt.setBounds(200, 100, 60, 23);

        jLabel77.setText("Sistem Saraf:");
        jLabel77.setName("jLabel77"); // NOI18N
        FormInput10.add(jLabel77);
        jLabel77.setBounds(0, 420, 100, 23);

        Saraf.setFocusTraversalPolicyProvider(true);
        Saraf.setName("Saraf"); // NOI18N
        Saraf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SarafActionPerformed(evt);
            }
        });
        Saraf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SarafKeyPressed(evt);
            }
        });
        FormInput10.add(Saraf);
        Saraf.setBounds(130, 420, 330, 23);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        Lab.setColumns(20);
        Lab.setRows(5);
        Lab.setName("Lab"); // NOI18N
        jScrollPane1.setViewportView(Lab);

        FormInput10.add(jScrollPane1);
        jScrollPane1.setBounds(100, 130, 380, 82);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        Ekg.setColumns(20);
        Ekg.setRows(5);
        Ekg.setName("Ekg"); // NOI18N
        jScrollPane2.setViewportView(Ekg);

        FormInput10.add(jScrollPane2);
        jScrollPane2.setBounds(540, 130, 350, 82);

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel99.setText("Kesadaran:");
        jLabel99.setName("jLabel99"); // NOI18N
        FormInput10.add(jLabel99);
        jLabel99.setBounds(10, 10, 80, 23);

        txtKesadaran.setFocusTraversalPolicyProvider(true);
        txtKesadaran.setName("txtKesadaran"); // NOI18N
        txtKesadaran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKesadaranKeyPressed(evt);
            }
        });
        FormInput10.add(txtKesadaran);
        txtKesadaran.setBounds(70, 10, 160, 23);

        jLabel134.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel134.setText("GCS:");
        jLabel134.setName("jLabel134"); // NOI18N
        FormInput10.add(jLabel134);
        jLabel134.setBounds(250, 10, 50, 23);

        GCS.setFocusTraversalPolicyProvider(true);
        GCS.setName("GCS"); // NOI18N
        GCS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GCSKeyPressed(evt);
            }
        });
        FormInput10.add(GCS);
        GCS.setBounds(280, 10, 80, 23);

        jLabel18.setText("Airway:");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput10.add(jLabel18);
        jLabel18.setBounds(380, 10, 50, 23);

        Airway.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Terintubasi", "Tidak Terintubasi" }));
        Airway.setName("Airway"); // NOI18N
        FormInput10.add(Airway);
        Airway.setBounds(440, 10, 140, 20);

        jLabel135.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel135.setText("Mg/kgBB/mnt");
        jLabel135.setName("jLabel135"); // NOI18N
        FormInput10.add(jLabel135);
        jLabel135.setBounds(290, 40, 80, 23);

        Support1.setFocusTraversalPolicyProvider(true);
        Support1.setName("Support1"); // NOI18N
        Support1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Support1KeyPressed(evt);
            }
        });
        FormInput10.add(Support1);
        Support1.setBounds(70, 40, 210, 23);

        jLabel136.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel136.setText("Support 1:");
        jLabel136.setName("jLabel136"); // NOI18N
        FormInput10.add(jLabel136);
        jLabel136.setBounds(10, 40, 80, 23);

        jLabel137.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel137.setText("Support 2:");
        jLabel137.setName("jLabel137"); // NOI18N
        FormInput10.add(jLabel137);
        jLabel137.setBounds(380, 40, 80, 23);

        Support2.setFocusTraversalPolicyProvider(true);
        Support2.setName("Support2"); // NOI18N
        Support2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Support2KeyPressed(evt);
            }
        });
        FormInput10.add(Support2);
        Support2.setBounds(440, 40, 250, 23);

        jLabel138.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel138.setText("Mg/kgBB/mnt");
        jLabel138.setName("jLabel138"); // NOI18N
        FormInput10.add(jLabel138);
        jLabel138.setBounds(700, 40, 80, 23);

        jLabel19.setText("Respirasi:");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput10.add(jLabel19);
        jLabel19.setBounds(10, 70, 50, 23);

        cmbRespirasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spontan", "Asist", "Kontrol" }));
        cmbRespirasi.setName("cmbRespirasi"); // NOI18N
        FormInput10.add(cmbRespirasi);
        cmbRespirasi.setBounds(70, 70, 140, 20);

        jLabel139.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel139.setText("RR:");
        jLabel139.setName("jLabel139"); // NOI18N
        FormInput10.add(jLabel139);
        jLabel139.setBounds(220, 70, 50, 23);

        Rr.setFocusTraversalPolicyProvider(true);
        Rr.setName("Rr"); // NOI18N
        Rr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RrKeyPressed(evt);
            }
        });
        FormInput10.add(Rr);
        Rr.setBounds(250, 70, 50, 23);

        jLabel83.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel83.setText("x/menit");
        jLabel83.setName("jLabel83"); // NOI18N
        FormInput10.add(jLabel83);
        jLabel83.setBounds(310, 70, 60, 23);

        Kanul.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SM", "SMNR" }));
        Kanul.setName("Kanul"); // NOI18N
        FormInput10.add(Kanul);
        Kanul.setBounds(440, 70, 140, 20);

        jLabel26.setText("Kanul Nasal:");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput10.add(jLabel26);
        jLabel26.setBounds(350, 70, 80, 23);

        jLabel34.setText("Thorax foto:");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput10.add(jLabel34);
        jLabel34.setBounds(0, 220, 90, 23);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        Thorax.setColumns(20);
        Thorax.setRows(5);
        Thorax.setName("Thorax"); // NOI18N
        jScrollPane3.setViewportView(Thorax);

        FormInput10.add(jScrollPane3);
        jScrollPane3.setBounds(100, 220, 380, 82);

        jLabel35.setText("TFP:");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput10.add(jLabel35);
        jLabel35.setBounds(440, 220, 90, 23);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        TFP.setColumns(20);
        TFP.setRows(5);
        TFP.setName("TFP"); // NOI18N
        jScrollPane4.setViewportView(TFP);

        FormInput10.add(jScrollPane4);
        jScrollPane4.setBounds(540, 220, 350, 82);

        jLabel36.setText("Penyakit Penyerta:");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput10.add(jLabel36);
        jLabel36.setBounds(0, 400, 110, 23);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        PxLain.setColumns(20);
        PxLain.setRows(5);
        PxLain.setName("PxLain"); // NOI18N
        jScrollPane5.setViewportView(PxLain);

        FormInput10.add(jScrollPane5);
        jScrollPane5.setBounds(100, 310, 380, 82);

        jLabel37.setText("Pemeriksaan Lainnya:");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput10.add(jLabel37);
        jLabel37.setBounds(-10, 310, 110, 23);

        jLabel84.setText("Sistem urinaria:");
        jLabel84.setName("jLabel84"); // NOI18N
        FormInput10.add(jLabel84);
        jLabel84.setBounds(470, 460, 90, 23);

        jLabel85.setText("Sistem Respirasi:");
        jLabel85.setName("jLabel85"); // NOI18N
        FormInput10.add(jLabel85);
        jLabel85.setBounds(470, 420, 90, 23);

        jLabel86.setText("Sistem Gastrointestinal:");
        jLabel86.setName("jLabel86"); // NOI18N
        FormInput10.add(jLabel86);
        jLabel86.setBounds(0, 460, 120, 23);

        jLabel87.setText("Sistem muskoloskeletal:");
        jLabel87.setName("jLabel87"); // NOI18N
        FormInput10.add(jLabel87);
        jLabel87.setBounds(0, 490, 120, 23);

        Urinaria.setFocusTraversalPolicyProvider(true);
        Urinaria.setName("Urinaria"); // NOI18N
        Urinaria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UrinariaKeyPressed(evt);
            }
        });
        FormInput10.add(Urinaria);
        Urinaria.setBounds(560, 460, 340, 23);

        jLabel88.setText("Lain-lain");
        jLabel88.setName("jLabel88"); // NOI18N
        FormInput10.add(jLabel88);
        jLabel88.setBounds(30, 520, 100, 23);

        Lain.setFocusTraversalPolicyProvider(true);
        Lain.setName("Lain"); // NOI18N
        Lain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LainKeyPressed(evt);
            }
        });
        FormInput10.add(Lain);
        Lain.setBounds(130, 520, 340, 23);

        jLabel89.setText("Alergi:");
        jLabel89.setName("jLabel89"); // NOI18N
        FormInput10.add(jLabel89);
        jLabel89.setBounds(640, 520, 80, 23);

        Alergi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Alergi.setName("Alergi"); // NOI18N
        FormInput10.add(Alergi);
        Alergi.setBounds(730, 520, 80, 20);

        Scroll6.setViewportView(FormInput10);

        PanelInput2.add(Scroll6, java.awt.BorderLayout.PAGE_START);

        Prainduksi.add(PanelInput2, java.awt.BorderLayout.PAGE_START);

        // TabRawat.addTab("Pra Induksi", Prainduksi); // dihapus (samakan web)

        Premedikasi.setBorder(null);
        Premedikasi.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        Premedikasi.setName("Premedikasi"); // NOI18N
        Premedikasi.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);
        Scroll3.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi2.setAutoCreateRowSorter(true);
        tbAnestesi2.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi2.setName("tbAnestesi2"); // NOI18N
        tbAnestesi2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi2MouseClicked(evt);
            }
        });
        tbAnestesi2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi2KeyPressed(evt);
            }
        });
        Scroll3.setViewportView(tbAnestesi2);

        Premedikasi.add(Scroll3, java.awt.BorderLayout.CENTER);

        PanelInput3.setName("PanelInput3"); // NOI18N
        PanelInput3.setOpaque(false);
        PanelInput3.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput11.setBackground(new java.awt.Color(250, 255, 245));
        FormInput11.setName("FormInput11"); // NOI18N
        FormInput11.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput11.setLayout(null);

        jLabel114.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel114.setText("Trakeostomi:");
        jLabel114.setName("jLabel114"); // NOI18N
        FormInput11.add(jLabel114);
        jLabel114.setBounds(420, 340, 80, 23);

        PernapasanMasuk19.setFocusTraversalPolicyProvider(true);
        PernapasanMasuk19.setName("PernapasanMasuk19"); // NOI18N
        PernapasanMasuk19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PernapasanMasuk19KeyPressed(evt);
            }
        });
        FormInput11.add(PernapasanMasuk19);
        PernapasanMasuk19.setBounds(490, 340, 100, 23);

        Posisi1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Terlentang", "Lithotomi", "Prone", "Lateral kanan", "Lateral kiri", "Lain-lain", "Perlindungan mata" }));
        Posisi1.setName("Posisi1"); // NOI18N
        FormInput11.add(Posisi1);
        Posisi1.setBounds(140, 280, 140, 20);

        jLabel101.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel101.setText("Face Mask:");
        jLabel101.setName("jLabel101"); // NOI18N
        FormInput11.add(jLabel101);
        jLabel101.setBounds(70, 340, 70, 23);

        SirkulasiMasuk15.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk15.setName("SirkulasiMasuk15"); // NOI18N
        SirkulasiMasuk15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk15KeyPressed(evt);
            }
        });
        FormInput11.add(SirkulasiMasuk15);
        SirkulasiMasuk15.setBounds(140, 340, 60, 23);

        jLabel103.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel103.setText("LM:");
        jLabel103.setName("jLabel103"); // NOI18N
        FormInput11.add(jLabel103);
        jLabel103.setBounds(320, 340, 30, 23);

        SirkulasiMasuk16.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk16.setName("SirkulasiMasuk16"); // NOI18N
        SirkulasiMasuk16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk16KeyPressed(evt);
            }
        });
        FormInput11.add(SirkulasiMasuk16);
        SirkulasiMasuk16.setBounds(340, 340, 70, 23);

        jLabel106.setText("Intubasi:");
        jLabel106.setName("jLabel106"); // NOI18N
        FormInput11.add(jLabel106);
        jLabel106.setBounds(60, 370, 70, 23);

        jLabel109.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel109.setText("ETT:");
        jLabel109.setName("jLabel109"); // NOI18N
        FormInput11.add(jLabel109);
        jLabel109.setBounds(210, 340, 40, 23);

        SirkulasiMasuk17.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk17.setName("SirkulasiMasuk17"); // NOI18N
        SirkulasiMasuk17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk17KeyPressed(evt);
            }
        });
        FormInput11.add(SirkulasiMasuk17);
        SirkulasiMasuk17.setBounds(240, 340, 70, 23);

        jLabel110.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel110.setText("Lain-Lain:");
        jLabel110.setName("jLabel110"); // NOI18N
        FormInput11.add(jLabel110);
        jLabel110.setBounds(610, 340, 50, 23);

        PernapasanMasuk20.setFocusTraversalPolicyProvider(true);
        PernapasanMasuk20.setName("PernapasanMasuk20"); // NOI18N
        PernapasanMasuk20.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PernapasanMasuk20KeyPressed(evt);
            }
        });
        FormInput11.add(PernapasanMasuk20);
        PernapasanMasuk20.setBounds(660, 340, 90, 23);

        Pernapasan15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sesudah tidur", "Oral", "Fiber optik", "Preksigenasi", "Mudah mask ventilasi", "Mudah intubasi", "Sulit intubasi", "Dengan stilet", "Tekanan luar laring", "Pack", "Blind", "Trakeostomi", "Nasal kanan", "Nasal kiri" }));
        Pernapasan15.setName("Pernapasan15"); // NOI18N
        FormInput11.add(Pernapasan15);
        Pernapasan15.setBounds(130, 370, 140, 20);

        jLabel119.setText("Posisi:");
        jLabel119.setName("jLabel119"); // NOI18N
        FormInput11.add(jLabel119);
        jLabel119.setBounds(50, 280, 60, 23);

        NmObatPremedikasi1.setFocusTraversalPolicyProvider(true);
        NmObatPremedikasi1.setName("NmObatPremedikasi1"); // NOI18N
        NmObatPremedikasi1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmObatPremedikasi1KeyPressed(evt);
            }
        });
        FormInput11.add(NmObatPremedikasi1);
        NmObatPremedikasi1.setBounds(260, 10, 170, 23);

        jLabel120.setText("Jalan Nafas:");
        jLabel120.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel120.setName("jLabel120"); // NOI18N
        FormInput11.add(jLabel120);
        jLabel120.setBounds(70, 310, 70, 23);

        NamaObatAnestesi.setFocusTraversalPolicyProvider(true);
        NamaObatAnestesi.setName("NamaObatAnestesi"); // NOI18N
        NamaObatAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NamaObatAnestesiActionPerformed(evt);
            }
        });
        NamaObatAnestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaObatAnestesiKeyPressed(evt);
            }
        });
        FormInput11.add(NamaObatAnestesi);
        NamaObatAnestesi.setBounds(230, 40, 440, 23);

        JenisAnestesiPremedikasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Anestesi umum", "Spinal", "Kaudal", "Blok perifer", "Epidural", "Lain-lain" }));
        JenisAnestesiPremedikasi.setName("JenisAnestesiPremedikasi"); // NOI18N
        FormInput11.add(JenisAnestesiPremedikasi);
        JenisAnestesiPremedikasi.setBounds(90, 40, 120, 20);

        JenisPremedikasi1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Oral", "IM", "IV", "Rektal" }));
        JenisPremedikasi1.setName("JenisPremedikasi1"); // NOI18N
        FormInput11.add(JenisPremedikasi1);
        JenisPremedikasi1.setBounds(90, 10, 100, 20);

        jLabel122.setText("Obat:");
        jLabel122.setName("jLabel122"); // NOI18N
        FormInput11.add(jLabel122);
        jLabel122.setBounds(210, 10, 40, 23);

        jLabel123.setText("Jenis Anastesi:");
        jLabel123.setName("jLabel123"); // NOI18N
        FormInput11.add(jLabel123);
        jLabel123.setBounds(-20, 40, 90, 23);

        jLabel140.setText("Premedikasi:");
        jLabel140.setName("jLabel140"); // NOI18N
        FormInput11.add(jLabel140);
        jLabel140.setBounds(10, 10, 70, 23);

        jLabel141.setText("Dosis:");
        jLabel141.setName("jLabel141"); // NOI18N
        FormInput11.add(jLabel141);
        jLabel141.setBounds(450, 10, 40, 23);

        DosisPremedikasi.setFocusTraversalPolicyProvider(true);
        DosisPremedikasi.setName("DosisPremedikasi"); // NOI18N
        DosisPremedikasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DosisPremedikasiKeyPressed(evt);
            }
        });
        FormInput11.add(DosisPremedikasi);
        DosisPremedikasi.setBounds(500, 10, 170, 23);

        PanelInput3.add(FormInput11, java.awt.BorderLayout.CENTER);

        ChkInput3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput3.setMnemonic('I');
        ChkInput3.setText(".: Input Data");
        ChkInput3.setToolTipText("Alt+I");
        ChkInput3.setBorderPainted(true);
        ChkInput3.setBorderPaintedFlat(true);
        ChkInput3.setFocusable(false);
        ChkInput3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput3.setName("ChkInput3"); // NOI18N
        ChkInput3.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput3.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput3ActionPerformed(evt);
            }
        });
        PanelInput3.add(ChkInput3, java.awt.BorderLayout.PAGE_END);

        Premedikasi.add(PanelInput3, java.awt.BorderLayout.PAGE_START);

        // TabRawat.addTab("Premedikasi", Premedikasi); // dihapus (samakan web)

        AnestesiUmum.setBackground(new java.awt.Color(254, 255, 254));
        AnestesiUmum.setForeground(new java.awt.Color(50, 50, 50));
        AnestesiUmum.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        AnestesiUmum.setName("AnestesiUmum"); // NOI18N
        AnestesiUmum.setPreferredSize(new java.awt.Dimension(457, 480));

        MasukKamar1.setBorder(null);
        MasukKamar1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        MasukKamar1.setName("MasukKamar1"); // NOI18N
        MasukKamar1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll10.setName("Scroll10"); // NOI18N
        Scroll10.setOpaque(true);
        Scroll10.setPreferredSize(new java.awt.Dimension(452, 200));
        Scroll10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Scroll10MouseClicked(evt);
            }
        });

        tbAnestesi9.setAutoCreateRowSorter(true);
        tbAnestesi9.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi9.setName("tbAnestesi9"); // NOI18N
        tbAnestesi9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi9MouseClicked(evt);
            }
        });
        tbAnestesi9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi9KeyPressed(evt);
            }
        });
        Scroll10.setViewportView(tbAnestesi9);

        MasukKamar1.add(Scroll10, java.awt.BorderLayout.CENTER);

        PanelInput10.setName("PanelInput10"); // NOI18N
        PanelInput10.setOpaque(false);
        PanelInput10.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput18.setBackground(new java.awt.Color(250, 255, 245));
        FormInput18.setName("FormInput18"); // NOI18N
        FormInput18.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput18.setLayout(null);

        label15.setText("%");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput18.add(label15);
        label15.setBounds(190, 110, 30, 23);

        Fio2.setName("Fio2"); // NOI18N
        Fio2.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput18.add(Fio2);
        Fio2.setBounds(120, 110, 70, 23);

        jLabel57.setText("Induksi:");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput18.add(jLabel57);
        jLabel57.setBounds(10, 10, 70, 23);

        PengaturaNafas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spontan", "Assist", "Kontrol" }));
        PengaturaNafas.setName("PengaturaNafas"); // NOI18N
        FormInput18.add(PengaturaNafas);
        PengaturaNafas.setBounds(580, 10, 130, 20);

        jLabel69.setText("Ventilator:");
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput18.add(jLabel69);
        jLabel69.setBounds(30, 50, 70, 23);

        TeknikKhusus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hipotermi", "Hipotensi", "Bypass", "Ventilasi satu paru" }));
        TeknikKhusus.setName("TeknikKhusus"); // NOI18N
        FormInput18.add(TeknikKhusus);
        TeknikKhusus.setBounds(330, 110, 160, 20);

        jLabel13.setText("x/mnt");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput18.add(jLabel13);
        jLabel13.setBounds(370, 70, 30, 23);

        jLabel90.setText("ml");
        jLabel90.setName("jLabel90"); // NOI18N
        FormInput18.add(jLabel90);
        jLabel90.setBounds(200, 70, 20, 23);

        InduksiKet.setFocusTraversalPolicyProvider(true);
        InduksiKet.setName("InduksiKet"); // NOI18N
        InduksiKet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InduksiKetKeyPressed(evt);
            }
        });
        FormInput18.add(InduksiKet);
        InduksiKet.setBounds(230, 10, 240, 23);

        jLabel91.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel91.setText("Teknik Khusus:");
        jLabel91.setName("jLabel91"); // NOI18N
        FormInput18.add(jLabel91);
        jLabel91.setBounds(250, 110, 90, 23);

        TidalVolum.setFocusTraversalPolicyProvider(true);
        TidalVolum.setName("TidalVolum"); // NOI18N
        TidalVolum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TidalVolumKeyPressed(evt);
            }
        });
        FormInput18.add(TidalVolum);
        TidalVolum.setBounds(120, 70, 70, 23);

        jLabel92.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel92.setText("RR:");
        jLabel92.setName("jLabel92"); // NOI18N
        FormInput18.add(jLabel92);
        jLabel92.setBounds(240, 70, 40, 23);

        Rr2.setFocusTraversalPolicyProvider(true);
        Rr2.setName("Rr2"); // NOI18N
        Rr2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Rr2KeyPressed(evt);
            }
        });
        FormInput18.add(Rr2);
        Rr2.setBounds(270, 70, 90, 23);

        jLabel93.setText("I : E Ratio:");
        jLabel93.setName("jLabel93"); // NOI18N
        FormInput18.add(jLabel93);
        jLabel93.setBounds(420, 70, 70, 23);

        jLabel94.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel94.setText("PEEP:");
        jLabel94.setName("jLabel94"); // NOI18N
        FormInput18.add(jLabel94);
        jLabel94.setBounds(550, 70, 50, 23);

        Ieratio.setFocusTraversalPolicyProvider(true);
        Ieratio.setName("Ieratio"); // NOI18N
        Ieratio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IeratioKeyPressed(evt);
            }
        });
        FormInput18.add(Ieratio);
        Ieratio.setBounds(500, 70, 40, 23);

        Peep.setFocusTraversalPolicyProvider(true);
        Peep.setName("Peep"); // NOI18N
        Peep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PeepKeyPressed(evt);
            }
        });
        FormInput18.add(Peep);
        Peep.setBounds(590, 70, 50, 23);

        jLabel142.setText("FiO2:");
        jLabel142.setName("jLabel142"); // NOI18N
        FormInput18.add(jLabel142);
        jLabel142.setBounds(70, 110, 40, 23);

        TeknikKhususKet.setFocusTraversalPolicyProvider(true);
        TeknikKhususKet.setName("TeknikKhususKet"); // NOI18N
        TeknikKhususKet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TeknikKhususKetKeyPressed(evt);
            }
        });
        FormInput18.add(TeknikKhususKet);
        TeknikKhususKet.setBounds(500, 110, 230, 23);

        Induksi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sempurna", "Eksitas", "Muntah", "Batuk", "Spasme", "Lainnya" }));
        Induksi.setName("Induksi"); // NOI18N
        FormInput18.add(Induksi);
        Induksi.setBounds(90, 10, 130, 20);

        jLabel143.setText("Pengaturan nafas:");
        jLabel143.setName("jLabel143"); // NOI18N
        FormInput18.add(jLabel143);
        jLabel143.setBounds(480, 10, 90, 23);

        jLabel158.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel158.setText("Tidal Volum:");
        jLabel158.setName("jLabel158"); // NOI18N
        FormInput18.add(jLabel158);
        jLabel158.setBounds(50, 70, 70, 23);

        Pip.setFocusTraversalPolicyProvider(true);
        Pip.setName("Pip"); // NOI18N
        Pip.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PipKeyPressed(evt);
            }
        });
        FormInput18.add(Pip);
        Pip.setBounds(700, 70, 64, 23);

        jLabel159.setText("PIP:");
        jLabel159.setName("jLabel159"); // NOI18N
        FormInput18.add(jLabel159);
        jLabel159.setBounds(650, 70, 40, 23);

        PanelInput10.add(FormInput18, java.awt.BorderLayout.CENTER);

        ChkInput10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput10.setMnemonic('I');
        ChkInput10.setText(".: Input Data");
        ChkInput10.setToolTipText("Alt+I");
        ChkInput10.setBorderPainted(true);
        ChkInput10.setBorderPaintedFlat(true);
        ChkInput10.setFocusable(false);
        ChkInput10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput10.setName("ChkInput10"); // NOI18N
        ChkInput10.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput10.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput10.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput10.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput10ActionPerformed(evt);
            }
        });
        PanelInput10.add(ChkInput10, java.awt.BorderLayout.PAGE_END);

        MasukKamar1.add(PanelInput10, java.awt.BorderLayout.PAGE_START);

        AnestesiUmum.addTab("Data Awal", MasukKamar1);

        ObservasiKamar1.setBorder(null);
        ObservasiKamar1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        ObservasiKamar1.setName("ObservasiKamar1"); // NOI18N
        ObservasiKamar1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll11.setName("Scroll11"); // NOI18N
        Scroll11.setOpaque(true);
        Scroll11.setPreferredSize(new java.awt.Dimension(452, 200));
        Scroll11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Scroll11MouseClicked(evt);
            }
        });

        tbAnestesi10.setAutoCreateRowSorter(true);
        tbAnestesi10.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi10.setName("tbAnestesi10"); // NOI18N
        tbAnestesi10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi10MouseClicked(evt);
            }
        });
        tbAnestesi10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi10KeyPressed(evt);
            }
        });
        Scroll11.setViewportView(tbAnestesi10);

        ObservasiKamar1.add(Scroll11, java.awt.BorderLayout.CENTER);

        PanelInput11.setName("PanelInput11"); // NOI18N
        PanelInput11.setOpaque(false);
        PanelInput11.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput19.setBackground(new java.awt.Color(250, 255, 245));
        FormInput19.setName("FormInput19"); // NOI18N
        FormInput19.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput19.setLayout(null);

        SirkulasiMasuk14.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk14.setName("SirkulasiMasuk14"); // NOI18N
        SirkulasiMasuk14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk14KeyPressed(evt);
            }
        });
        FormInput19.add(SirkulasiMasuk14);
        SirkulasiMasuk14.setBounds(40, 40, 100, 23);

        jLabel100.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel100.setText("O2:");
        jLabel100.setName("jLabel100"); // NOI18N
        FormInput19.add(jLabel100);
        jLabel100.setBounds(10, 10, 50, 23);

        PernapasanMasuk12.setFocusTraversalPolicyProvider(true);
        PernapasanMasuk12.setName("PernapasanMasuk12"); // NOI18N
        PernapasanMasuk12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PernapasanMasuk12KeyPressed(evt);
            }
        });
        FormInput19.add(PernapasanMasuk12);
        PernapasanMasuk12.setBounds(40, 10, 60, 23);

        jLabel102.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel102.setText("N2O:");
        jLabel102.setName("jLabel102"); // NOI18N
        FormInput19.add(jLabel102);
        jLabel102.setBounds(120, 10, 50, 23);

        PernapasanMasuk14.setFocusTraversalPolicyProvider(true);
        PernapasanMasuk14.setName("PernapasanMasuk14"); // NOI18N
        PernapasanMasuk14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PernapasanMasuk14KeyPressed(evt);
            }
        });
        FormInput19.add(PernapasanMasuk14);
        PernapasanMasuk14.setBounds(150, 10, 60, 23);

        jLabel104.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel104.setText("Air:");
        jLabel104.setName("jLabel104"); // NOI18N
        FormInput19.add(jLabel104);
        jLabel104.setBounds(220, 10, 30, 23);

        jLabel105.setText("Spo2:");
        jLabel105.setName("jLabel105"); // NOI18N
        FormInput19.add(jLabel105);
        jLabel105.setBounds(10, 40, 30, 23);

        PernapasanMasuk16.setFocusTraversalPolicyProvider(true);
        PernapasanMasuk16.setName("PernapasanMasuk16"); // NOI18N
        PernapasanMasuk16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PernapasanMasuk16KeyPressed(evt);
            }
        });
        FormInput19.add(PernapasanMasuk16);
        PernapasanMasuk16.setBounds(250, 10, 70, 23);

        jLabel107.setText("Nadi:");
        jLabel107.setName("jLabel107"); // NOI18N
        FormInput19.add(jLabel107);
        jLabel107.setBounds(0, 70, 40, 23);

        jLabel108.setText("Tensi:");
        jLabel108.setName("jLabel108"); // NOI18N
        FormInput19.add(jLabel108);
        jLabel108.setBounds(520, 40, 50, 23);

        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel111.setText("Respirasi:");
        jLabel111.setName("jLabel111"); // NOI18N
        FormInput19.add(jLabel111);
        jLabel111.setBounds(420, 40, 50, 23);

        jLabel112.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel112.setText("EKG:");
        jLabel112.setName("jLabel112"); // NOI18N
        FormInput19.add(jLabel112);
        jLabel112.setBounds(520, 10, 60, 23);

        jLabel115.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel115.setText("Infus:");
        jLabel115.setName("jLabel115"); // NOI18N
        FormInput19.add(jLabel115);
        jLabel115.setBounds(150, 40, 70, 23);

        SirkulasiMasuk18.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk18.setName("SirkulasiMasuk18"); // NOI18N
        SirkulasiMasuk18.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk18KeyPressed(evt);
            }
        });
        FormInput19.add(SirkulasiMasuk18);
        SirkulasiMasuk18.setBounds(210, 40, 100, 23);

        SirkulasiMasuk19.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk19.setName("SirkulasiMasuk19"); // NOI18N
        SirkulasiMasuk19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk19KeyPressed(evt);
            }
        });
        FormInput19.add(SirkulasiMasuk19);
        SirkulasiMasuk19.setBounds(580, 10, 60, 23);

        jLabel116.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel116.setText("Suhu:");
        jLabel116.setName("jLabel116"); // NOI18N
        FormInput19.add(jLabel116);
        jLabel116.setBounds(320, 40, 30, 23);

        SirkulasiMasuk21.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk21.setName("SirkulasiMasuk21"); // NOI18N
        SirkulasiMasuk21.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk21KeyPressed(evt);
            }
        });
        FormInput19.add(SirkulasiMasuk21);
        SirkulasiMasuk21.setBounds(350, 40, 60, 23);

        PemantauanNadi2.setFocusTraversalPolicyProvider(true);
        PemantauanNadi2.setName("PemantauanNadi2"); // NOI18N
        PemantauanNadi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PemantauanNadi2ActionPerformed(evt);
            }
        });
        PemantauanNadi2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemantauanNadi2KeyPressed(evt);
            }
        });
        FormInput19.add(PemantauanNadi2);
        PemantauanNadi2.setBounds(580, 40, 70, 23);

        jLabel121.setText("Volatile:");
        jLabel121.setName("jLabel121"); // NOI18N
        FormInput19.add(jLabel121);
        jLabel121.setBounds(330, 10, 60, 23);

        Lainnya2.setFocusTraversalPolicyProvider(true);
        Lainnya2.setName("Lainnya2"); // NOI18N
        Lainnya2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Lainnya2ActionPerformed(evt);
            }
        });
        Lainnya2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Lainnya2KeyPressed(evt);
            }
        });
        FormInput19.add(Lainnya2);
        Lainnya2.setBounds(400, 10, 100, 23);

        Selama2.setFocusTraversalPolicyProvider(true);
        Selama2.setName("Selama2"); // NOI18N
        Selama2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Selama2ActionPerformed(evt);
            }
        });
        Selama2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Selama2KeyPressed(evt);
            }
        });
        FormInput19.add(Selama2);
        Selama2.setBounds(40, 70, 70, 23);

        SirkulasiMasuk22.setFocusTraversalPolicyProvider(true);
        SirkulasiMasuk22.setName("SirkulasiMasuk22"); // NOI18N
        SirkulasiMasuk22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SirkulasiMasuk22KeyPressed(evt);
            }
        });
        FormInput19.add(SirkulasiMasuk22);
        SirkulasiMasuk22.setBounds(470, 40, 60, 23);

        PanelInput11.add(FormInput19, java.awt.BorderLayout.CENTER);

        ChkInput11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput11.setMnemonic('I');
        ChkInput11.setText(".: Input Data");
        ChkInput11.setToolTipText("Alt+I");
        ChkInput11.setBorderPainted(true);
        ChkInput11.setBorderPaintedFlat(true);
        ChkInput11.setFocusable(false);
        ChkInput11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput11.setName("ChkInput11"); // NOI18N
        ChkInput11.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput11.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput11.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput11.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput11ActionPerformed(evt);
            }
        });
        PanelInput11.add(ChkInput11, java.awt.BorderLayout.PAGE_END);

        ObservasiKamar1.add(PanelInput11, java.awt.BorderLayout.PAGE_START);

        // AnestesiUmum.addTab("Monitoring", ObservasiKamar1); // jadi tab top-level (di bawah)

        AnastesiUmumKeterangan.setBorder(null);
        AnastesiUmumKeterangan.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        AnastesiUmumKeterangan.setName("AnastesiUmumKeterangan"); // NOI18N
        AnastesiUmumKeterangan.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll12.setName("Scroll12"); // NOI18N
        Scroll12.setOpaque(true);
        Scroll12.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi11.setAutoCreateRowSorter(true);
        tbAnestesi11.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi11.setName("tbAnestesi11"); // NOI18N
        tbAnestesi11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi11MouseClicked(evt);
            }
        });
        tbAnestesi11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi11KeyPressed(evt);
            }
        });
        Scroll12.setViewportView(tbAnestesi11);

        AnastesiUmumKeterangan.add(Scroll12, java.awt.BorderLayout.CENTER);

        PanelInput12.setName("PanelInput12"); // NOI18N
        PanelInput12.setOpaque(false);
        PanelInput12.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput20.setBackground(new java.awt.Color(250, 255, 245));
        FormInput20.setName("FormInput20"); // NOI18N
        FormInput20.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput20.setLayout(null);

        jLabel117.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel117.setText("Pemberian Cairan:");
        jLabel117.setName("jLabel117"); // NOI18N
        FormInput20.add(jLabel117);
        jLabel117.setBounds(450, 10, 90, 23);

        jLabel118.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel118.setText("Masalah Durante Operasi:");
        jLabel118.setName("jLabel118"); // NOI18N
        FormInput20.add(jLabel118);
        jLabel118.setBounds(20, 110, 150, 23);

        jLabel124.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel124.setText("Medikasi:");
        jLabel124.setName("jLabel124"); // NOI18N
        FormInput20.add(jLabel124);
        jLabel124.setBounds(20, 10, 60, 23);

        jLabel125.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel125.setText("Tindakan:");
        jLabel125.setName("jLabel125"); // NOI18N
        FormInput20.add(jLabel125);
        jLabel125.setBounds(460, 130, 80, 23);

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        Tindakan.setColumns(20);
        Tindakan.setRows(5);
        Tindakan.setName("Tindakan"); // NOI18N
        jScrollPane6.setViewportView(Tindakan);

        FormInput20.add(jScrollPane6);
        jScrollPane6.setBounds(550, 130, 350, 82);

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        PemberianCairan.setColumns(20);
        PemberianCairan.setRows(5);
        PemberianCairan.setName("PemberianCairan"); // NOI18N
        jScrollPane7.setViewportView(PemberianCairan);

        FormInput20.add(jScrollPane7);
        jScrollPane7.setBounds(550, 10, 340, 82);

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        Medikasi.setColumns(20);
        Medikasi.setRows(5);
        Medikasi.setName("Medikasi"); // NOI18N
        jScrollPane8.setViewportView(Medikasi);

        FormInput20.add(jScrollPane8);
        jScrollPane8.setBounds(70, 10, 350, 82);

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        MasalahDuranteOperasi.setColumns(20);
        MasalahDuranteOperasi.setRows(5);
        MasalahDuranteOperasi.setName("MasalahDuranteOperasi"); // NOI18N
        jScrollPane9.setViewportView(MasalahDuranteOperasi);

        FormInput20.add(jScrollPane9);
        jScrollPane9.setBounds(70, 130, 350, 82);

        PanelInput12.add(FormInput20, java.awt.BorderLayout.CENTER);

        ChkInput12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput12.setMnemonic('I');
        ChkInput12.setText(".: Input Data");
        ChkInput12.setToolTipText("Alt+I");
        ChkInput12.setBorderPainted(true);
        ChkInput12.setBorderPaintedFlat(true);
        ChkInput12.setFocusable(false);
        ChkInput12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput12.setName("ChkInput12"); // NOI18N
        ChkInput12.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput12.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput12.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput12.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput12ActionPerformed(evt);
            }
        });
        PanelInput12.add(ChkInput12, java.awt.BorderLayout.PAGE_END);

        AnastesiUmumKeterangan.add(PanelInput12, java.awt.BorderLayout.PAGE_START);

        // AnestesiUmum.addTab("Keterangan", AnastesiUmumKeterangan); // dihapus (samakan web)

        TabRawat.addTab("Anestesi Umum", AnestesiUmum);
        TabRawat.addTab("Monitoring", ObservasiKamar1); // Monitoring jadi tab tersendiri (dipakai umum & regional)

        BlokadeRegional.setBackground(new java.awt.Color(254, 255, 254));
        BlokadeRegional.setForeground(new java.awt.Color(50, 50, 50));
        BlokadeRegional.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BlokadeRegional.setName("BlokadeRegional"); // NOI18N
        BlokadeRegional.setPreferredSize(new java.awt.Dimension(457, 480));

        MasukKamar2.setBorder(null);
        MasukKamar2.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        MasukKamar2.setName("MasukKamar2"); // NOI18N
        MasukKamar2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll13.setName("Scroll13"); // NOI18N
        Scroll13.setOpaque(true);
        Scroll13.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi12.setAutoCreateRowSorter(true);
        tbAnestesi12.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi12.setName("tbAnestesi12"); // NOI18N
        tbAnestesi12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi12MouseClicked(evt);
            }
        });
        tbAnestesi12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi12KeyPressed(evt);
            }
        });
        Scroll13.setViewportView(tbAnestesi12);

        MasukKamar2.add(Scroll13, java.awt.BorderLayout.CENTER);

        PanelInput13.setName("PanelInput13"); // NOI18N
        PanelInput13.setPreferredSize(new java.awt.Dimension(192, 300));
        PanelInput13.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput21.setBackground(new java.awt.Color(250, 255, 245));
        FormInput21.setName("FormInput21"); // NOI18N
        FormInput21.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput21.setLayout(null);

        LokasiTusukan.setFocusTraversalPolicyProvider(true);
        LokasiTusukan.setName("LokasiTusukan"); // NOI18N
        LokasiTusukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LokasiTusukanKeyPressed(evt);
            }
        });
        FormInput21.add(LokasiTusukan);
        LokasiTusukan.setBounds(290, 10, 90, 23);

        jLabel126.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel126.setText("Lokasi Tusukan:");
        jLabel126.setName("jLabel126"); // NOI18N
        FormInput21.add(jLabel126);
        jLabel126.setBounds(200, 10, 90, 23);

        AnalgesiSetinggiSegmen.setFocusTraversalPolicyProvider(true);
        AnalgesiSetinggiSegmen.setName("AnalgesiSetinggiSegmen"); // NOI18N
        AnalgesiSetinggiSegmen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnalgesiSetinggiSegmenKeyPressed(evt);
            }
        });
        FormInput21.add(AnalgesiSetinggiSegmen);
        AnalgesiSetinggiSegmen.setBounds(530, 10, 90, 23);

        jLabel127.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel127.setText("Analgesia setinggi segmen:");
        jLabel127.setName("jLabel127"); // NOI18N
        FormInput21.add(jLabel127);
        jLabel127.setBounds(400, 10, 130, 23);

        Teknik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Caudal", "Saddle block", "Intravenous regional", "Epidural", "Spinal" }));
        Teknik.setName("Teknik"); // NOI18N
        FormInput21.add(Teknik);
        Teknik.setBounds(60, 10, 130, 20);

        jLabel59.setText("Teknik:");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput21.add(jLabel59);
        jLabel59.setBounds(10, 10, 40, 23);

        jLabel146.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel146.setText("Anastesi Lokal:");
        jLabel146.setName("jLabel146"); // NOI18N
        FormInput21.add(jLabel146);
        jLabel146.setBounds(20, 40, 80, 23);

        AnestesiLokal.setFocusTraversalPolicyProvider(true);
        AnestesiLokal.setName("AnestesiLokal"); // NOI18N
        AnestesiLokal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnestesiLokalKeyPressed(evt);
            }
        });
        FormInput21.add(AnestesiLokal);
        AnestesiLokal.setBounds(100, 40, 210, 23);

        jLabel147.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel147.setText("Konsentrasi Jumlah:");
        jLabel147.setName("jLabel147"); // NOI18N
        FormInput21.add(jLabel147);
        jLabel147.setBounds(330, 40, 130, 23);

        KonsentrasiJumlah.setFocusTraversalPolicyProvider(true);
        KonsentrasiJumlah.setName("KonsentrasiJumlah"); // NOI18N
        KonsentrasiJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KonsentrasiJumlahKeyPressed(evt);
            }
        });
        FormInput21.add(KonsentrasiJumlah);
        KonsentrasiJumlah.setBounds(460, 40, 90, 23);

        jLabel148.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel148.setText("Obat Tambahan:");
        jLabel148.setName("jLabel148"); // NOI18N
        FormInput21.add(jLabel148);
        jLabel148.setBounds(10, 70, 90, 23);

        ObatTambahan.setFocusTraversalPolicyProvider(true);
        ObatTambahan.setName("ObatTambahan"); // NOI18N
        ObatTambahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ObatTambahanKeyPressed(evt);
            }
        });
        FormInput21.add(ObatTambahan);
        ObatTambahan.setBounds(100, 70, 210, 23);

        jLabel149.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel149.setText("Dosis:");
        jLabel149.setName("jLabel149"); // NOI18N
        FormInput21.add(jLabel149);
        jLabel149.setBounds(330, 70, 60, 23);

        Dosis.setFocusTraversalPolicyProvider(true);
        Dosis.setName("Dosis"); // NOI18N
        Dosis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DosisKeyPressed(evt);
            }
        });
        FormInput21.add(Dosis);
        Dosis.setBounds(380, 70, 90, 23);

        jLabel150.setText("Vasokonstriktor:");
        jLabel150.setName("jLabel150"); // NOI18N
        FormInput21.add(jLabel150);
        jLabel150.setBounds(0, 100, 90, 23);

        Vasokonstriktor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adrenalin", "Noradrenalin", "Tidak pakai" }));
        Vasokonstriktor.setName("Vasokonstriktor"); // NOI18N
        FormInput21.add(Vasokonstriktor);
        Vasokonstriktor.setBounds(100, 100, 130, 20);

        jLabel151.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel151.setText("Konsentrasi:");
        jLabel151.setName("jLabel151"); // NOI18N
        FormInput21.add(jLabel151);
        jLabel151.setBounds(240, 100, 90, 23);

        Konsentrasi.setFocusTraversalPolicyProvider(true);
        Konsentrasi.setName("Konsentrasi"); // NOI18N
        Konsentrasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KonsentrasiKeyPressed(evt);
            }
        });
        FormInput21.add(Konsentrasi);
        Konsentrasi.setBounds(330, 100, 90, 23);

        jLabel152.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel152.setText("Waktu mulai suntikan jam:");
        jLabel152.setName("jLabel152"); // NOI18N
        FormInput21.add(jLabel152);
        jLabel152.setBounds(10, 130, 140, 23);

        WaktuMulaiSuntikJam.setFocusTraversalPolicyProvider(true);
        WaktuMulaiSuntikJam.setName("WaktuMulaiSuntikJam"); // NOI18N
        WaktuMulaiSuntikJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                WaktuMulaiSuntikJamKeyPressed(evt);
            }
        });
        FormInput21.add(WaktuMulaiSuntikJam);
        WaktuMulaiSuntikJam.setBounds(140, 130, 80, 23);

        jLabel153.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel153.setText("Analgesia jam:");
        jLabel153.setName("jLabel153"); // NOI18N
        FormInput21.add(jLabel153);
        jLabel153.setBounds(60, 160, 90, 23);

        AnalgesiJam.setFocusTraversalPolicyProvider(true);
        AnalgesiJam.setName("AnalgesiJam"); // NOI18N
        AnalgesiJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnalgesiJamKeyPressed(evt);
            }
        });
        FormInput21.add(AnalgesiJam);
        AnalgesiJam.setBounds(140, 160, 80, 23);

        Lamanya.setFocusTraversalPolicyProvider(true);
        Lamanya.setName("Lamanya"); // NOI18N
        Lamanya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamanyaKeyPressed(evt);
            }
        });
        FormInput21.add(Lamanya);
        Lamanya.setBounds(290, 160, 80, 23);

        jLabel155.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel155.setText("Lamanya:");
        jLabel155.setName("jLabel155"); // NOI18N
        FormInput21.add(jLabel155);
        jLabel155.setBounds(230, 160, 60, 23);

        jLabel156.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel156.setText("jam");
        jLabel156.setName("jLabel156"); // NOI18N
        FormInput21.add(jLabel156);
        jLabel156.setBounds(380, 160, 60, 23);

        LamanyaMenitAnalgesi.setFocusTraversalPolicyProvider(true);
        LamanyaMenitAnalgesi.setName("LamanyaMenitAnalgesi"); // NOI18N
        LamanyaMenitAnalgesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamanyaMenitAnalgesiKeyPressed(evt);
            }
        });
        FormInput21.add(LamanyaMenitAnalgesi);
        LamanyaMenitAnalgesi.setBounds(420, 160, 80, 23);

        jLabel157.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel157.setText("menit");
        jLabel157.setName("jLabel157"); // NOI18N
        FormInput21.add(jLabel157);
        jLabel157.setBounds(510, 160, 60, 23);

        jLabel154.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel154.setText("Operasi jam:");
        jLabel154.setName("jLabel154"); // NOI18N
        FormInput21.add(jLabel154);
        jLabel154.setBounds(60, 190, 90, 23);

        OperasiJam.setFocusTraversalPolicyProvider(true);
        OperasiJam.setName("OperasiJam"); // NOI18N
        OperasiJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                OperasiJamKeyPressed(evt);
            }
        });
        FormInput21.add(OperasiJam);
        OperasiJam.setBounds(140, 190, 80, 23);

        LamanyajamOperasi.setFocusTraversalPolicyProvider(true);
        LamanyajamOperasi.setName("LamanyajamOperasi"); // NOI18N
        LamanyajamOperasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamanyajamOperasiKeyPressed(evt);
            }
        });
        FormInput21.add(LamanyajamOperasi);
        LamanyajamOperasi.setBounds(290, 190, 80, 23);

        jLabel160.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel160.setText("Lamanya:");
        jLabel160.setName("jLabel160"); // NOI18N
        FormInput21.add(jLabel160);
        jLabel160.setBounds(230, 190, 60, 23);

        jLabel161.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel161.setText("jam");
        jLabel161.setName("jLabel161"); // NOI18N
        FormInput21.add(jLabel161);
        jLabel161.setBounds(380, 190, 60, 23);

        LamanyaJamMenit.setFocusTraversalPolicyProvider(true);
        LamanyaJamMenit.setName("LamanyaJamMenit"); // NOI18N
        LamanyaJamMenit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamanyaJamMenitKeyPressed(evt);
            }
        });
        FormInput21.add(LamanyaJamMenit);
        LamanyaJamMenit.setBounds(420, 190, 80, 23);

        jLabel162.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel162.setText("menit");
        jLabel162.setName("jLabel162"); // NOI18N
        FormInput21.add(jLabel162);
        jLabel162.setBounds(510, 190, 60, 23);

        jLabel163.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel163.setText("Tindakan anestesi tambahan:");
        jLabel163.setName("jLabel163"); // NOI18N
        FormInput21.add(jLabel163);
        jLabel163.setBounds(0, 220, 150, 23);

        TindakanAnestesiTambahan.setFocusTraversalPolicyProvider(true);
        TindakanAnestesiTambahan.setName("TindakanAnestesiTambahan"); // NOI18N
        TindakanAnestesiTambahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TindakanAnestesiTambahanKeyPressed(evt);
            }
        });
        FormInput21.add(TindakanAnestesiTambahan);
        TindakanAnestesiTambahan.setBounds(140, 220, 490, 23);

        PanelInput13.add(FormInput21, java.awt.BorderLayout.CENTER);

        ChkInput13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput13.setMnemonic('I');
        ChkInput13.setText(".: Input Data");
        ChkInput13.setToolTipText("Alt+I");
        ChkInput13.setBorderPainted(true);
        ChkInput13.setBorderPaintedFlat(true);
        ChkInput13.setFocusable(false);
        ChkInput13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput13.setName("ChkInput13"); // NOI18N
        ChkInput13.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput13.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput13.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput13.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput13ActionPerformed(evt);
            }
        });
        PanelInput13.add(ChkInput13, java.awt.BorderLayout.PAGE_END);

        MasukKamar2.add(PanelInput13, java.awt.BorderLayout.PAGE_START);

        BlokadeRegional.addTab("Data Awal", MasukKamar2);

        TabRawat.addTab("Blokade Regional", BlokadeRegional);

        Monitoring.setBorder(null);
        Monitoring.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        Monitoring.setName("Monitoring"); // NOI18N
        Monitoring.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);
        Scroll4.setPreferredSize(new java.awt.Dimension(452, 200));
        Scroll4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Scroll4MouseClicked(evt);
            }
        });

        tbAnestesi3.setAutoCreateRowSorter(true);
        tbAnestesi3.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi3.setName("tbAnestesi3"); // NOI18N
        tbAnestesi3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi3MouseClicked(evt);
            }
        });
        tbAnestesi3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi3KeyPressed(evt);
            }
        });
        Scroll4.setViewportView(tbAnestesi3);

        Monitoring.add(Scroll4, java.awt.BorderLayout.CENTER);

        PanelInput4.setName("PanelInput4"); // NOI18N
        PanelInput4.setOpaque(false);
        PanelInput4.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput12.setBackground(new java.awt.Color(250, 255, 245));
        FormInput12.setName("FormInput12"); // NOI18N
        FormInput12.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput12.setLayout(null);

        jLabel78.setText("Ukuran:");
        jLabel78.setName("jLabel78"); // NOI18N
        FormInput12.add(jLabel78);
        jLabel78.setBounds(480, 10, 60, 23);

        jLabel70.setText("Airway:");
        jLabel70.setName("jLabel70"); // NOI18N
        FormInput12.add(jLabel70);
        jLabel70.setBounds(250, 10, 90, 23);

        BalonNoBalon.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Balon", "Tanpa balon" }));
        BalonNoBalon.setName("BalonNoBalon"); // NOI18N
        FormInput12.add(BalonNoBalon);
        BalonNoBalon.setBounds(650, 10, 110, 20);

        Ukuran.setFocusTraversalPolicyProvider(true);
        Ukuran.setName("Ukuran"); // NOI18N
        Ukuran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UkuranActionPerformed(evt);
            }
        });
        Ukuran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UkuranKeyPressed(evt);
            }
        });
        FormInput12.add(Ukuran);
        Ukuran.setBounds(550, 10, 90, 23);

        Kristaloid.setFocusTraversalPolicyProvider(true);
        Kristaloid.setName("Kristaloid"); // NOI18N
        Kristaloid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KristaloidActionPerformed(evt);
            }
        });
        Kristaloid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KristaloidKeyPressed(evt);
            }
        });
        FormInput12.add(Kristaloid);
        Kristaloid.setBounds(90, 110, 360, 23);

        jLabel71.setText("jam");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput12.add(jLabel71);
        jLabel71.setBounds(440, 40, 30, 23);

        LamaOperasi.setFocusTraversalPolicyProvider(true);
        LamaOperasi.setName("LamaOperasi"); // NOI18N
        LamaOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LamaOperasiActionPerformed(evt);
            }
        });
        LamaOperasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamaOperasiKeyPressed(evt);
            }
        });
        FormInput12.add(LamaOperasi);
        LamaOperasi.setBounds(100, 40, 80, 23);

        jLabel80.setText("Letak Penderita:");
        jLabel80.setName("jLabel80"); // NOI18N
        FormInput12.add(jLabel80);
        jLabel80.setBounds(10, 10, 80, 23);

        LetakPenderita.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Supine", "Prone", "Litotomi", "Semi sitting", "Flowler", "Lateral dekubitus", "Right dekubitus", "Left dekubitus", "Trendelenberg", "Knee-chest", "Jack-knife" }));
        LetakPenderita.setName("LetakPenderita"); // NOI18N
        LetakPenderita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LetakPenderitaActionPerformed(evt);
            }
        });
        FormInput12.add(LetakPenderita);
        LetakPenderita.setBounds(100, 10, 130, 20);

        jLabel72.setText("Lama Anestesi:");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput12.add(jLabel72);
        jLabel72.setBounds(270, 40, 80, 23);

        LamaAnestesi.setFocusTraversalPolicyProvider(true);
        LamaAnestesi.setName("LamaAnestesi"); // NOI18N
        LamaAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LamaAnestesiActionPerformed(evt);
            }
        });
        LamaAnestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamaAnestesiKeyPressed(evt);
            }
        });
        FormInput12.add(LamaAnestesi);
        LamaAnestesi.setBounds(360, 40, 80, 23);

        Koloid.setFocusTraversalPolicyProvider(true);
        Koloid.setName("Koloid"); // NOI18N
        Koloid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KoloidActionPerformed(evt);
            }
        });
        Koloid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KoloidKeyPressed(evt);
            }
        });
        FormInput12.add(Koloid);
        Koloid.setBounds(90, 140, 360, 23);

        Airway2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Face mask", "LMA", "Single lumen ETT", "Single lumen spiral ETT", "Double lumen ETT R", "Double lumen ETT L", "Nasal kanul" }));
        Airway2.setName("Airway2"); // NOI18N
        FormInput12.add(Airway2);
        Airway2.setBounds(350, 10, 110, 20);

        jLabel144.setText("2. Koloid");
        jLabel144.setName("jLabel144"); // NOI18N
        FormInput12.add(jLabel144);
        jLabel144.setBounds(10, 140, 70, 23);

        jLabel145.setText("jam");
        jLabel145.setName("jLabel145"); // NOI18N
        FormInput12.add(jLabel145);
        jLabel145.setBounds(180, 40, 40, 23);

        jLabel164.setText("Lama Operasi:");
        jLabel164.setName("jLabel164"); // NOI18N
        FormInput12.add(jLabel164);
        jLabel164.setBounds(10, 40, 80, 23);

        jLabel165.setText("Cairan:");
        jLabel165.setName("jLabel165"); // NOI18N
        FormInput12.add(jLabel165);
        jLabel165.setBounds(10, 70, 80, 23);

        jLabel166.setText("Total Asupan Cairan:");
        jLabel166.setName("jLabel166"); // NOI18N
        FormInput12.add(jLabel166);
        jLabel166.setBounds(10, 90, 110, 23);

        jLabel167.setText("ml");
        jLabel167.setName("jLabel167"); // NOI18N
        FormInput12.add(jLabel167);
        jLabel167.setBounds(440, 140, 30, 23);

        jLabel168.setText("1. Kristaloid");
        jLabel168.setName("jLabel168"); // NOI18N
        FormInput12.add(jLabel168);
        jLabel168.setBounds(10, 110, 70, 23);

        jLabel169.setText("ml");
        jLabel169.setName("jLabel169"); // NOI18N
        FormInput12.add(jLabel169);
        jLabel169.setBounds(440, 110, 30, 23);

        jLabel170.setText("3. Darah");
        jLabel170.setName("jLabel170"); // NOI18N
        FormInput12.add(jLabel170);
        jLabel170.setBounds(10, 170, 70, 23);

        Darah.setFocusTraversalPolicyProvider(true);
        Darah.setName("Darah"); // NOI18N
        Darah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DarahActionPerformed(evt);
            }
        });
        Darah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DarahKeyPressed(evt);
            }
        });
        FormInput12.add(Darah);
        Darah.setBounds(90, 170, 360, 23);

        jLabel171.setText("4. Komponen Darah");
        jLabel171.setName("jLabel171"); // NOI18N
        FormInput12.add(jLabel171);
        jLabel171.setBounds(-20, 200, 100, 23);

        KomponenDarah.setFocusTraversalPolicyProvider(true);
        KomponenDarah.setName("KomponenDarah"); // NOI18N
        KomponenDarah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KomponenDarahActionPerformed(evt);
            }
        });
        KomponenDarah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KomponenDarahKeyPressed(evt);
            }
        });
        FormInput12.add(KomponenDarah);
        KomponenDarah.setBounds(90, 200, 360, 23);

        jLabel172.setText("ml");
        jLabel172.setName("jLabel172"); // NOI18N
        FormInput12.add(jLabel172);
        jLabel172.setBounds(440, 170, 30, 23);

        jLabel173.setText("ml");
        jLabel173.setName("jLabel173"); // NOI18N
        FormInput12.add(jLabel173);
        jLabel173.setBounds(440, 200, 30, 23);

        Perdarahan.setFocusTraversalPolicyProvider(true);
        Perdarahan.setName("Perdarahan"); // NOI18N
        Perdarahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PerdarahanActionPerformed(evt);
            }
        });
        Perdarahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PerdarahanKeyPressed(evt);
            }
        });
        FormInput12.add(Perdarahan);
        Perdarahan.setBounds(590, 110, 360, 23);

        Diuresis.setFocusTraversalPolicyProvider(true);
        Diuresis.setName("Diuresis"); // NOI18N
        Diuresis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiuresisActionPerformed(evt);
            }
        });
        Diuresis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiuresisKeyPressed(evt);
            }
        });
        FormInput12.add(Diuresis);
        Diuresis.setBounds(590, 140, 360, 23);

        jLabel174.setText("2. Diuresis");
        jLabel174.setName("jLabel174"); // NOI18N
        FormInput12.add(jLabel174);
        jLabel174.setBounds(510, 140, 70, 23);

        jLabel175.setText("Total Keluaran Cairan:");
        jLabel175.setName("jLabel175"); // NOI18N
        FormInput12.add(jLabel175);
        jLabel175.setBounds(510, 90, 110, 23);

        jLabel176.setText("ml");
        jLabel176.setName("jLabel176"); // NOI18N
        FormInput12.add(jLabel176);
        jLabel176.setBounds(940, 140, 30, 23);

        jLabel177.setText("1. Perdarahan");
        jLabel177.setName("jLabel177"); // NOI18N
        FormInput12.add(jLabel177);
        jLabel177.setBounds(510, 110, 70, 23);

        jLabel178.setText("ml");
        jLabel178.setName("jLabel178"); // NOI18N
        FormInput12.add(jLabel178);
        jLabel178.setBounds(940, 110, 30, 23);

        jLabel179.setText("3. Cairan Lain:");
        jLabel179.setName("jLabel179"); // NOI18N
        FormInput12.add(jLabel179);
        jLabel179.setBounds(510, 170, 70, 23);

        CairanLain.setFocusTraversalPolicyProvider(true);
        CairanLain.setName("CairanLain"); // NOI18N
        CairanLain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CairanLainActionPerformed(evt);
            }
        });
        CairanLain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CairanLainKeyPressed(evt);
            }
        });
        FormInput12.add(CairanLain);
        CairanLain.setBounds(590, 170, 360, 23);

        jLabel181.setText("ml");
        jLabel181.setName("jLabel181"); // NOI18N
        FormInput12.add(jLabel181);
        jLabel181.setBounds(940, 170, 30, 23);

        PanelInput4.add(FormInput12, java.awt.BorderLayout.CENTER);

        ChkInput4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput4.setMnemonic('I');
        ChkInput4.setText(".: Input Data");
        ChkInput4.setToolTipText("Alt+I");
        ChkInput4.setBorderPainted(true);
        ChkInput4.setBorderPaintedFlat(true);
        ChkInput4.setFocusable(false);
        ChkInput4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput4.setName("ChkInput4"); // NOI18N
        ChkInput4.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput4.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput4.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput4ActionPerformed(evt);
            }
        });
        PanelInput4.add(ChkInput4, java.awt.BorderLayout.PAGE_END);

        Monitoring.add(PanelInput4, java.awt.BorderLayout.PAGE_START);

        TabRawat.addTab("Keadaan Selama Operasi", Monitoring);

        Catatan.setBorder(null);
        Catatan.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        Catatan.setName("Catatan"); // NOI18N
        Catatan.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);
        Scroll5.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi4.setAutoCreateRowSorter(true);
        tbAnestesi4.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi4.setName("tbAnestesi4"); // NOI18N
        tbAnestesi4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi4MouseClicked(evt);
            }
        });
        tbAnestesi4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi4KeyPressed(evt);
            }
        });
        Scroll5.setViewportView(tbAnestesi4);

        Catatan.add(Scroll5, java.awt.BorderLayout.CENTER);

        PanelInput5.setName("PanelInput5"); // NOI18N
        PanelInput5.setOpaque(false);
        PanelInput5.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput13.setBackground(new java.awt.Color(250, 255, 245));
        FormInput13.setName("FormInput13"); // NOI18N
        FormInput13.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput13.setLayout(null);

        jLabel180.setText("Keadaan Bayi:");
        jLabel180.setName("jLabel180"); // NOI18N
        FormInput13.add(jLabel180);
        jLabel180.setBounds(10, 10, 80, 23);

        KeadaanBayi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hidup", "Mati dalam kandungan" }));
        KeadaanBayi.setName("KeadaanBayi"); // NOI18N
        FormInput13.add(KeadaanBayi);
        KeadaanBayi.setBounds(100, 10, 130, 20);

        jLabel182.setText("Apgar Skor 1 Menit:");
        jLabel182.setName("jLabel182"); // NOI18N
        FormInput13.add(jLabel182);
        jLabel182.setBounds(10, 40, 100, 23);

        Apgrar1Menit.setFocusTraversalPolicyProvider(true);
        Apgrar1Menit.setName("Apgrar1Menit"); // NOI18N
        Apgrar1Menit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Apgrar1MenitActionPerformed(evt);
            }
        });
        Apgrar1Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Apgrar1MenitKeyPressed(evt);
            }
        });
        FormInput13.add(Apgrar1Menit);
        Apgrar1Menit.setBounds(120, 40, 60, 23);

        jLabel184.setText("Jenis Kelamin:");
        jLabel184.setName("jLabel184"); // NOI18N
        FormInput13.add(jLabel184);
        jLabel184.setBounds(250, 10, 80, 23);

        JenisKelamin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "L", "P" }));
        JenisKelamin.setName("JenisKelamin"); // NOI18N
        FormInput13.add(JenisKelamin);
        JenisKelamin.setBounds(340, 10, 50, 20);

        jLabel185.setText("Apgar Skor 5 Menit:");
        jLabel185.setName("jLabel185"); // NOI18N
        FormInput13.add(jLabel185);
        jLabel185.setBounds(190, 40, 100, 23);

        Apgar5Menit.setFocusTraversalPolicyProvider(true);
        Apgar5Menit.setName("Apgar5Menit"); // NOI18N
        Apgar5Menit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Apgar5MenitActionPerformed(evt);
            }
        });
        Apgar5Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Apgar5MenitKeyPressed(evt);
            }
        });
        FormInput13.add(Apgar5Menit);
        Apgar5Menit.setBounds(300, 40, 60, 23);

        jLabel186.setText("Apgar Skor 10 Menit:");
        jLabel186.setName("jLabel186"); // NOI18N
        FormInput13.add(jLabel186);
        jLabel186.setBounds(370, 40, 110, 23);

        Apgar10Menit.setFocusTraversalPolicyProvider(true);
        Apgar10Menit.setName("Apgar10Menit"); // NOI18N
        Apgar10Menit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Apgar10MenitActionPerformed(evt);
            }
        });
        Apgar10Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Apgar10MenitKeyPressed(evt);
            }
        });
        FormInput13.add(Apgar10Menit);
        Apgar10Menit.setBounds(480, 40, 60, 23);

        PanelInput5.add(FormInput13, java.awt.BorderLayout.CENTER);

        ChkInput5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput5.setMnemonic('I');
        ChkInput5.setText(".: Input Data");
        ChkInput5.setToolTipText("Alt+I");
        ChkInput5.setBorderPainted(true);
        ChkInput5.setBorderPaintedFlat(true);
        ChkInput5.setFocusable(false);
        ChkInput5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput5.setName("ChkInput5"); // NOI18N
        ChkInput5.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput5.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput5.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput5.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput5ActionPerformed(evt);
            }
        });
        PanelInput5.add(ChkInput5, java.awt.BorderLayout.PAGE_END);

        Catatan.add(PanelInput5, java.awt.BorderLayout.PAGE_START);

        TabRawat.addTab("Sectio Caesaria", Catatan);

        CatatanKamarPemulihan.setBackground(new java.awt.Color(254, 255, 254));
        CatatanKamarPemulihan.setForeground(new java.awt.Color(50, 50, 50));
        CatatanKamarPemulihan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        CatatanKamarPemulihan.setName("CatatanKamarPemulihan"); // NOI18N
        CatatanKamarPemulihan.setPreferredSize(new java.awt.Dimension(457, 480));

        MasukKamar.setBorder(null);
        MasukKamar.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        MasukKamar.setName("MasukKamar"); // NOI18N
        MasukKamar.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);
        Scroll7.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi5.setAutoCreateRowSorter(true);
        tbAnestesi5.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi5.setName("tbAnestesi5"); // NOI18N
        tbAnestesi5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi5MouseClicked(evt);
            }
        });
        tbAnestesi5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi5KeyPressed(evt);
            }
        });
        Scroll7.setViewportView(tbAnestesi5);

        MasukKamar.add(Scroll7, java.awt.BorderLayout.CENTER);

        PanelInput7.setName("PanelInput7"); // NOI18N
        PanelInput7.setOpaque(false);
        PanelInput7.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput15.setBackground(new java.awt.Color(250, 255, 245));
        FormInput15.setName("FormInput15"); // NOI18N
        FormInput15.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput15.setLayout(null);

        label12.setText("Masuk kamar pulih jam:");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput15.add(label12);
        label12.setBounds(0, 10, 150, 23);

        MasukKamarPulih.setForeground(new java.awt.Color(50, 70, 50));
        MasukKamarPulih.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "28-09-2025 12:28:13" }));
        MasukKamarPulih.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        MasukKamarPulih.setName("MasukKamarPulih"); // NOI18N
        MasukKamarPulih.setOpaque(false);
        MasukKamarPulih.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MasukKamarPulihKeyPressed(evt);
            }
        });
        FormInput15.add(MasukKamarPulih);
        MasukKamarPulih.setBounds(150, 10, 130, 23);

        jLabel31.setText("Kesadaran:");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput15.add(jLabel31);
        jLabel31.setBounds(290, 10, 70, 23);

        Kesadaran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sadar", "Belum sadar", "Tidur dalam" }));
        Kesadaran.setName("Kesadaran"); // NOI18N
        FormInput15.add(Kesadaran);
        Kesadaran.setBounds(370, 10, 130, 20);

        jLabel12.setText("Tanda Vital:");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput15.add(jLabel12);
        jLabel12.setBounds(20, 40, 80, 23);

        jLabel183.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel183.setText("TD:");
        jLabel183.setName("jLabel183"); // NOI18N
        FormInput15.add(jLabel183);
        jLabel183.setBounds(50, 60, 50, 23);

        TensiMasukKamar.setFocusTraversalPolicyProvider(true);
        TensiMasukKamar.setName("TensiMasukKamar"); // NOI18N
        TensiMasukKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TensiMasukKamarKeyPressed(evt);
            }
        });
        FormInput15.add(TensiMasukKamar);
        TensiMasukKamar.setBounds(80, 60, 80, 23);

        jLabel187.setText("mmHg");
        jLabel187.setName("jLabel187"); // NOI18N
        FormInput15.add(jLabel187);
        jLabel187.setBounds(170, 60, 30, 23);

        jLabel188.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel188.setText("RR:");
        jLabel188.setName("jLabel188"); // NOI18N
        FormInput15.add(jLabel188);
        jLabel188.setBounds(410, 60, 30, 23);

        NadiMasukKamar.setFocusTraversalPolicyProvider(true);
        NadiMasukKamar.setName("NadiMasukKamar"); // NOI18N
        NadiMasukKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiMasukKamarKeyPressed(evt);
            }
        });
        FormInput15.add(NadiMasukKamar);
        NadiMasukKamar.setBounds(240, 60, 70, 23);

        jLabel189.setText("x/menit ");
        jLabel189.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel189.setName("jLabel189"); // NOI18N
        FormInput15.add(jLabel189);
        jLabel189.setBounds(310, 60, 60, 23);

        jLabel190.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel190.setText("Support 1:");
        jLabel190.setName("jLabel190"); // NOI18N
        FormInput15.add(jLabel190);
        jLabel190.setBounds(20, 90, 80, 23);

        Support3.setFocusTraversalPolicyProvider(true);
        Support3.setName("Support3"); // NOI18N
        Support3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Support3KeyPressed(evt);
            }
        });
        FormInput15.add(Support3);
        Support3.setBounds(80, 90, 210, 23);

        jLabel191.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel191.setText("Mg/kgBB/mnt");
        jLabel191.setName("jLabel191"); // NOI18N
        FormInput15.add(jLabel191);
        jLabel191.setBounds(300, 90, 80, 23);

        jLabel192.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel192.setText("Support 2:");
        jLabel192.setName("jLabel192"); // NOI18N
        FormInput15.add(jLabel192);
        jLabel192.setBounds(390, 90, 80, 23);

        Support4.setFocusTraversalPolicyProvider(true);
        Support4.setName("Support4"); // NOI18N
        Support4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Support4KeyPressed(evt);
            }
        });
        FormInput15.add(Support4);
        Support4.setBounds(450, 90, 250, 23);

        jLabel193.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel193.setText("Mg/kgBB/mnt");
        jLabel193.setName("jLabel193"); // NOI18N
        FormInput15.add(jLabel193);
        jLabel193.setBounds(710, 90, 80, 23);

        jLabel194.setText("Respirasi:");
        jLabel194.setName("jLabel194"); // NOI18N
        FormInput15.add(jLabel194);
        jLabel194.setBounds(20, 120, 50, 23);

        cmbRespirasiMasukKamar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spontan", "Asist", "Kontrol" }));
        cmbRespirasiMasukKamar.setName("cmbRespirasiMasukKamar"); // NOI18N
        FormInput15.add(cmbRespirasiMasukKamar);
        cmbRespirasiMasukKamar.setBounds(80, 120, 140, 20);

        jLabel197.setText("Kanul Nasal:");
        jLabel197.setName("jLabel197"); // NOI18N
        FormInput15.add(jLabel197);
        jLabel197.setBounds(360, 120, 80, 23);

        KanulMasukKamar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SM", "SMNR" }));
        KanulMasukKamar.setName("KanulMasukKamar"); // NOI18N
        FormInput15.add(KanulMasukKamar);
        KanulMasukKamar.setBounds(450, 120, 140, 20);

        jLabel198.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel198.setText("O2:");
        jLabel198.setName("jLabel198"); // NOI18N
        FormInput15.add(jLabel198);
        jLabel198.setBounds(610, 120, 50, 23);

        O2MasukKamar.setFocusTraversalPolicyProvider(true);
        O2MasukKamar.setName("O2MasukKamar"); // NOI18N
        O2MasukKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                O2MasukKamarKeyPressed(evt);
            }
        });
        FormInput15.add(O2MasukKamar);
        O2MasukKamar.setBounds(640, 120, 50, 23);

        jLabel199.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel199.setText("l/mnt");
        jLabel199.setName("jLabel199"); // NOI18N
        FormInput15.add(jLabel199);
        jLabel199.setBounds(700, 120, 40, 23);

        jLabel200.setText("SPO2:");
        jLabel200.setName("jLabel200"); // NOI18N
        FormInput15.add(jLabel200);
        jLabel200.setBounds(750, 120, 30, 23);

        Spo2MasukKamar.setFocusTraversalPolicyProvider(true);
        Spo2MasukKamar.setName("Spo2MasukKamar"); // NOI18N
        Spo2MasukKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Spo2MasukKamarKeyPressed(evt);
            }
        });
        FormInput15.add(Spo2MasukKamar);
        Spo2MasukKamar.setBounds(790, 120, 50, 23);

        RrMasukKamar.setFocusTraversalPolicyProvider(true);
        RrMasukKamar.setName("RrMasukKamar"); // NOI18N
        RrMasukKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RrMasukKamarKeyPressed(evt);
            }
        });
        FormInput15.add(RrMasukKamar);
        RrMasukKamar.setBounds(440, 60, 50, 23);

        jLabel201.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel201.setText("x/menit");
        jLabel201.setName("jLabel201"); // NOI18N
        FormInput15.add(jLabel201);
        jLabel201.setBounds(500, 60, 60, 23);

        jLabel202.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel202.setText("Skor Nyeri:");
        jLabel202.setName("jLabel202"); // NOI18N
        FormInput15.add(jLabel202);
        jLabel202.setBounds(20, 150, 80, 23);

        SkorNyeri.setFocusTraversalPolicyProvider(true);
        SkorNyeri.setName("SkorNyeri"); // NOI18N
        SkorNyeri.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SkorNyeriKeyPressed(evt);
            }
        });
        FormInput15.add(SkorNyeri);
        SkorNyeri.setBounds(80, 150, 330, 23);

        PenyulitIntraOperatif.setFocusTraversalPolicyProvider(true);
        PenyulitIntraOperatif.setName("PenyulitIntraOperatif"); // NOI18N
        PenyulitIntraOperatif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenyulitIntraOperatifKeyPressed(evt);
            }
        });
        FormInput15.add(PenyulitIntraOperatif);
        PenyulitIntraOperatif.setBounds(550, 150, 260, 23);

        jLabel203.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel203.setText("Penyulit Intraoperatif:");
        jLabel203.setName("jLabel203"); // NOI18N
        FormInput15.add(jLabel203);
        jLabel203.setBounds(420, 150, 130, 23);

        jLabel204.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel204.setText("Nadi:");
        jLabel204.setName("jLabel204"); // NOI18N
        FormInput15.add(jLabel204);
        jLabel204.setBounds(210, 60, 40, 23);

        PanelInput7.add(FormInput15, java.awt.BorderLayout.CENTER);

        ChkInput7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput7.setMnemonic('I');
        ChkInput7.setText(".: Input Data");
        ChkInput7.setToolTipText("Alt+I");
        ChkInput7.setBorderPainted(true);
        ChkInput7.setBorderPaintedFlat(true);
        ChkInput7.setFocusable(false);
        ChkInput7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput7.setName("ChkInput7"); // NOI18N
        ChkInput7.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput7.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput7.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput7.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput7ActionPerformed(evt);
            }
        });
        PanelInput7.add(ChkInput7, java.awt.BorderLayout.PAGE_END);

        MasukKamar.add(PanelInput7, java.awt.BorderLayout.PAGE_START);

        CatatanKamarPemulihan.addTab("Masuk Kamar", MasukKamar);

        ObservasiKamar.setBorder(null);
        ObservasiKamar.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        ObservasiKamar.setName("ObservasiKamar"); // NOI18N
        ObservasiKamar.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);
        Scroll8.setPreferredSize(new java.awt.Dimension(452, 200));

        tbAnestesi6.setAutoCreateRowSorter(true);
        tbAnestesi6.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi6.setName("tbAnestesi6"); // NOI18N
        tbAnestesi6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi6MouseClicked(evt);
            }
        });
        tbAnestesi6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi6KeyPressed(evt);
            }
        });
        Scroll8.setViewportView(tbAnestesi6);

        ObservasiKamar.add(Scroll8, java.awt.BorderLayout.CENTER);

        PanelInput8.setName("PanelInput8"); // NOI18N
        PanelInput8.setOpaque(false);
        PanelInput8.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput16.setBackground(new java.awt.Color(250, 255, 245));
        FormInput16.setName("FormInput16"); // NOI18N
        FormInput16.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput16.setLayout(null);

        jLabel38.setText("S:");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput16.add(jLabel38);
        jLabel38.setBounds(10, 10, 50, 23);

        Suhu.setFocusTraversalPolicyProvider(true);
        Suhu.setName("Suhu"); // NOI18N
        Suhu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuKeyPressed(evt);
            }
        });
        FormInput16.add(Suhu);
        Suhu.setBounds(70, 10, 50, 23);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel39.setText("RR:");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput16.add(jLabel39);
        jLabel39.setBounds(130, 10, 50, 23);

        Rr4.setFocusTraversalPolicyProvider(true);
        Rr4.setName("Rr4"); // NOI18N
        Rr4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Rr4KeyPressed(evt);
            }
        });
        FormInput16.add(Rr4);
        Rr4.setBounds(180, 10, 70, 23);

        Nadi2.setFocusTraversalPolicyProvider(true);
        Nadi2.setName("Nadi2"); // NOI18N
        Nadi2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Nadi2KeyPressed(evt);
            }
        });
        FormInput16.add(Nadi2);
        Nadi2.setBounds(300, 10, 70, 23);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("Nadi:");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput16.add(jLabel40);
        jLabel40.setBounds(270, 10, 70, 23);

        jLabel41.setText("Tekanan Darah:");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput16.add(jLabel41);
        jLabel41.setBounds(380, 10, 90, 23);

        TekananDarah.setFocusTraversalPolicyProvider(true);
        TekananDarah.setName("TekananDarah"); // NOI18N
        TekananDarah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TekananDarahKeyPressed(evt);
            }
        });
        FormInput16.add(TekananDarah);
        TekananDarah.setBounds(480, 10, 40, 23);

        PanelInput8.add(FormInput16, java.awt.BorderLayout.CENTER);

        ChkInput8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput8.setMnemonic('I');
        ChkInput8.setText(".: Input Data");
        ChkInput8.setToolTipText("Alt+I");
        ChkInput8.setBorderPainted(true);
        ChkInput8.setBorderPaintedFlat(true);
        ChkInput8.setFocusable(false);
        ChkInput8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput8.setName("ChkInput8"); // NOI18N
        ChkInput8.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput8.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput8.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput8.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInput8ActionPerformed(evt);
            }
        });
        PanelInput8.add(ChkInput8, java.awt.BorderLayout.PAGE_END);

        ObservasiKamar.add(PanelInput8, java.awt.BorderLayout.PAGE_START);

        CatatanKamarPemulihan.addTab("Monitoring", ObservasiKamar);

        TabRawat.addTab("Keadaan Pasca Anastesi di Ruang Pemulihan", CatatanKamarPemulihan);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(60, 80));
        panelGlass10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        panelGlass10.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 13, -1, -1));

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        panelGlass10.add(TNoRw, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 8, 95, -1));

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        panelGlass10.add(TNoRM, new org.netbeans.lib.awtextra.AbsoluteConstraints(183, 8, -1, -1));

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(TPasien, new org.netbeans.lib.awtextra.AbsoluteConstraints(252, 8, -1, -1));

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        panelGlass10.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(457, 13, -1, -1));

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        panelGlass10.add(TglLahir, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 8, -1, -1));

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        panelGlass10.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(579, 13, -1, -1));

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        panelGlass10.add(Jk, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 8, -1, -1));

        jLabel24.setText("Tanggal :");
        jLabel24.setName("jLabel24"); // NOI18N
        panelGlass10.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 13, -1, -1));

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "28-09-2025" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        panelGlass10.add(DTPTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 8, -1, -1));

        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam.setName("cmbJam"); // NOI18N
        cmbJam.setPreferredSize(new java.awt.Dimension(62, 28));
        cmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJamKeyPressed(evt);
            }
        });
        panelGlass10.add(cmbJam, new org.netbeans.lib.awtextra.AbsoluteConstraints(845, 6, -1, -1));

        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt.setName("cmbMnt"); // NOI18N
        cmbMnt.setPreferredSize(new java.awt.Dimension(62, 28));
        cmbMnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMntKeyPressed(evt);
            }
        });
        panelGlass10.add(cmbMnt, new org.netbeans.lib.awtextra.AbsoluteConstraints(912, 6, -1, -1));

        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDtk.setName("cmbDtk"); // NOI18N
        cmbDtk.setPreferredSize(new java.awt.Dimension(62, 28));
        cmbDtk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbDtkKeyPressed(evt);
            }
        });
        panelGlass10.add(cmbDtk, new org.netbeans.lib.awtextra.AbsoluteConstraints(979, 6, -1, -1));

        ChkJln.setBorder(null);
        ChkJln.setSelected(true);
        ChkJln.setBorderPainted(true);
        ChkJln.setBorderPaintedFlat(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkJln.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkJln.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkJln.setName("ChkJln"); // NOI18N
        ChkJln.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkJlnActionPerformed(evt);
            }
        });
        panelGlass10.add(ChkJln, new org.netbeans.lib.awtextra.AbsoluteConstraints(1046, 11, -1, -1));

        jLabel9.setText("Ruangan:");
        jLabel9.setName("jLabel9"); // NOI18N
        panelGlass10.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 51, 55, -1));

        Ruangan.setEditable(false);
        Ruangan.setHighlighter(null);
        Ruangan.setName("Ruangan"); // NOI18N
        Ruangan.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(Ruangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 46, -1, -1));

        jLabel95.setText("Tgl Masuk RS:");
        jLabel95.setName("jLabel95"); // NOI18N
        panelGlass10.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 51, -1, -1));

        TglMasukRS.setEditable(false);
        TglMasukRS.setHighlighter(null);
        TglMasukRS.setName("TglMasukRS"); // NOI18N
        TglMasukRS.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(TglMasukRS, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 46, 112, -1));

        jLabel64.setText("Petugas:");
        jLabel64.setName("jLabel64"); // NOI18N
        panelGlass10.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(504, 51, -1, -1));

        KdPetugas.setEditable(false);
        KdPetugas.setHighlighter(null);
        KdPetugas.setName("KdPetugas"); // NOI18N
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasKeyPressed(evt);
            }
        });
        panelGlass10.add(KdPetugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(553, 46, 105, -1));

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        NmPetugas.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(NmPetugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 46, 243, -1));

        btnPetugas3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas3.setMnemonic('2');
        btnPetugas3.setToolTipText("ALt+2");
        btnPetugas3.setName("btnPetugas3"); // NOI18N
        btnPetugas3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugas3ActionPerformed(evt);
            }
        });
        btnPetugas3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugas3KeyPressed(evt);
            }
        });
        panelGlass10.add(btnPetugas3, new org.netbeans.lib.awtextra.AbsoluteConstraints(929, 46, -1, -1));

        internalFrame1.add(panelGlass10, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            //    Valid.pindah(evt,TCari,BtnDokter);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        // Dispatch diselaraskan ke 7 tab runtime (Pra Induksi & Premedikasi sudah di-comment).
        switch (TabRawat.getSelectedIndex()) {
            case 0: // Data Awal
                simpanDataAwal();
                break;
            case 1: // Anestesi Umum
                simpanAnestesiUmumDataAwal();
                break;
            case 2: // Monitoring
                simpanAnestesiUmumMonitoring();
                break;
            case 3: // Blokade Regional
                simpanBlokadeRegional();
                break;
            case 4: // Keadaan Selama Operasi
                simpanKeadaanOperasi();
                break;
            case 5: // Sectio Caesaria
                simpanSectio();
                break;
            case 6: // Keadaan Pasca Anestesi di Ruang Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0: // Masuk Kamar
                        simpanMasukKamarPemulihan();
                        break;
                    case 1: // Monitoring
                        simpanMonitoringPemulihan();
                        break;
                    case 2: // Score
                        simpanScore();
                        break;
                }
                break;
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        /*    if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
           Valid.pindah(evt,CatatanKhusus,BtnBatal);
        } */
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        switch (TabRawat.getSelectedIndex()) {
            case 0: // Data Awal
                hapusDataAwal();
                break;
            case 1: // Anestesi Umum
                hapusAnestesiUmumDataAwal();
                break;
            case 2: // Monitoring
                hapusAnestesiUmumMonitoring();
                break;
            case 3: // Blokade Regional
                hapusBlokadeRegional();
                break;
            case 4: // Keadaan Selama Operasi
                hapusKeadaanOperasi();
                break;
            case 5: // Sectio Caesaria
                hapusSectio();
                break;
            case 6: // Keadaan Pasca Anestesi di Ruang Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        hapusMasukKamarPemulihan();
                        break;
                    case 1:
                        hapusMonitoringPemulihan();
                        break;
                    case 2: // Score
                        hapusScore();
                        break;
                }
                break;
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        switch (TabRawat.getSelectedIndex()) {
            case 0: // Data Awal
                gantiDataAwal();
                break;
            case 1: // Anestesi Umum
                gantiAnestesiUmumDataAwal();
                break;
            case 2: // Monitoring
                gantiAnestesiUmumMonitoring();
                break;
            case 3: // Blokade Regional
                gantiBlokadeRegional();
                break;
            case 4: // Keadaan Selama Operasi
                gantiKeadaanOperasi();
                break;
            case 5: // Sectio Caesaria
                gantiSectio();
                break;
            case 6: // Keadaan Pasca Anestesi di Ruang Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        gantiMasukKamarPemulihan();
                        break;
                    case 1:
                        gantiMonitoringPemulihan();
                        break;
                    case 2: // Score
                        ubahScore();
                        break;
                }
                break;
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        /*     if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);} */
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        try {
            // Validasi data yang akan dicetak
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
                return;
            }

            // Tampilkan menu pilihan format cetak
            String[] options = {"Cetak PDF (Jasper)", "Laporan HTML (Browser)"};
            int choice = JOptionPane.showOptionDialog(
                null,
                "Pilih format laporan:",
                "Format Laporan",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice == -1) {
                return; // User cancel
            }

            // Tentukan tab yang aktif
            int currentTabIndex = TabRawat.getSelectedIndex();

            if (choice == 0) {
                // Cetak PDF dengan Jasper (existing)
                switch (currentTabIndex) {
                    case 0:
                        cetakDataAwal();
                        break;
                    case 1: // Anestesi Umum
                        cetakAnestesiUmum();
                        break;
                    case 2: // Monitoring
                        cetakAnestesiUmum();
                        break;
                    case 3: // Blokade Regional
                        cetakBlokadeRegional();
                        break;
                    case 4: // Keadaan Selama Operasi
                        cetakKeadaanOperasi();
                        break;
                    case 5: // Sectio Caesaria
                        cetakSectio();
                        break;
                    case 6: // Keadaan Pasca Anestesi di Ruang Pemulihan
                        cetakPascaAnestesi();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Pilih tab yang akan dicetak!");
                        break;
                }
            } else if (choice == 1) {
                // Generate HTML LENGKAP (semua tab) dan buka di browser
                cetakHTML();
            }

        } catch (Exception e) {
            System.out.println("Error BtnPrintActionPerformed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat mencetak: " + e.getMessage());
        }
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        //    TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        /*    if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        } */
}//GEN-LAST:event_BtnAllKeyPressed

    private void MasukKamarPulihKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MasukKamarPulihKeyPressed
        //       Valid.pindah(evt,RencanaTindakan,TB);
    }//GEN-LAST:event_MasukKamarPulihKeyPressed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        //   Valid.pindah(evt,BtnSeekDokter,cmbJam);
    }//GEN-LAST:event_DTPTglKeyPressed

    private void cmbJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJamKeyPressed
        Valid.pindah(evt, DTPTgl, cmbMnt);
    }//GEN-LAST:event_cmbJamKeyPressed

    private void cmbMntKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMntKeyPressed
        Valid.pindah(evt, cmbJam, cmbDtk);
    }//GEN-LAST:event_cmbMntKeyPressed

    private void cmbDtkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbDtkKeyPressed
        //   Valid.pindah(evt,cmbMnt,TCari);
    }//GEN-LAST:event_cmbDtkKeyPressed

    private void ChkJlnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkJlnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkJlnActionPerformed

    private void SuhuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuhuKeyPressed

    private void Rr4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Rr4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Rr4KeyPressed

    private void Nadi2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Nadi2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Nadi2KeyPressed

    private void TekananDarahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TekananDarahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TekananDarahKeyPressed

    private void BtnDokter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter2ActionPerformed
        pilihan = 1;
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokter2ActionPerformed

    private void BtnDokter2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokter2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDokter2KeyPressed

    private void MuskuloskletalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MuskuloskletalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_MuskuloskletalKeyPressed

    private void MetabolikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MetabolikKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_MetabolikKeyPressed

    private void BbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BbKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BbKeyPressed

    private void TensiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TensiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TensiKeyPressed

    private void GastroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GastroKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_GastroKeyPressed

    private void KdAsistenAnestesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdAsistenAnestesiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdAsistenAnestesiKeyPressed

    private void btnPetugas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugas1ActionPerformed
        pilihan = 2;
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugas1ActionPerformed

    private void btnPetugas1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugas1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPetugas1KeyPressed

    private void DiagnosaPrabedahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiagnosaPrabedahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiagnosaPrabedahActionPerformed

    private void DiagnosaPrabedahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaPrabedahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiagnosaPrabedahKeyPressed

    private void MedikasiPrabedahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MedikasiPrabedahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MedikasiPrabedahActionPerformed

    private void MedikasiPrabedahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MedikasiPrabedahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_MedikasiPrabedahKeyPressed

    private void RespirasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RespirasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RespirasiActionPerformed

    private void RespirasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RespirasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_RespirasiKeyPressed

    private void BtnDokter3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter3ActionPerformed
        pilihan = 2;
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokter3ActionPerformed

    private void BtnDokter3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokter3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDokter3KeyPressed

    private void NadiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NadiKeyPressed

    private void O2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_O2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_O2KeyPressed

    private void Spo2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Spo2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Spo2KeyPressed

    private void HmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HmtKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_HmtKeyPressed

    private void SarafActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SarafActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SarafActionPerformed

    private void SarafKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SarafKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SarafKeyPressed

    private void KdAsistenBedahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdAsistenBedahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdAsistenBedahKeyPressed

    private void btnPetugas2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugas2ActionPerformed
        pilihan = 3;
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugas2ActionPerformed

    private void btnPetugas2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugas2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPetugas2KeyPressed

    private void JenisAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JenisAnestesiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_JenisAnestesiActionPerformed

    private void JenisAnestesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JenisAnestesiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JenisAnestesiKeyPressed

    private void PernapasanMasuk19KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PernapasanMasuk19KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PernapasanMasuk19KeyPressed

    private void SirkulasiMasuk14KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk14KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk14KeyPressed

    private void PernapasanMasuk12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PernapasanMasuk12KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PernapasanMasuk12KeyPressed

    private void SirkulasiMasuk15KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk15KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk15KeyPressed

    private void PernapasanMasuk14KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PernapasanMasuk14KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PernapasanMasuk14KeyPressed

    private void SirkulasiMasuk16KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk16KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk16KeyPressed

    private void PernapasanMasuk16KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PernapasanMasuk16KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PernapasanMasuk16KeyPressed

    private void SirkulasiMasuk17KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk17KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk17KeyPressed

    private void PernapasanMasuk20KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PernapasanMasuk20KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PernapasanMasuk20KeyPressed

    private void SirkulasiMasuk18KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk18KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk18KeyPressed

    private void SirkulasiMasuk19KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk19KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk19KeyPressed

    private void SirkulasiMasuk21KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk21KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk21KeyPressed

    private void NmObatPremedikasi1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NmObatPremedikasi1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NmObatPremedikasi1KeyPressed

    private void NamaObatAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NamaObatAnestesiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NamaObatAnestesiActionPerformed

    private void NamaObatAnestesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NamaObatAnestesiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NamaObatAnestesiKeyPressed

    private void PemantauanNadi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PemantauanNadi2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PemantauanNadi2ActionPerformed

    private void PemantauanNadi2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PemantauanNadi2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PemantauanNadi2KeyPressed

    private void Lainnya2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lainnya2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Lainnya2ActionPerformed

    private void Lainnya2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Lainnya2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Lainnya2KeyPressed

    private void Selama2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Selama2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Selama2ActionPerformed

    private void Selama2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Selama2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Selama2KeyPressed

    private void SirkulasiMasuk22KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SirkulasiMasuk22KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SirkulasiMasuk22KeyPressed

    private void LokasiTusukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LokasiTusukanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LokasiTusukanKeyPressed

    private void AnalgesiSetinggiSegmenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnalgesiSetinggiSegmenKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AnalgesiSetinggiSegmenKeyPressed

    private void KdPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPetugasKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdPetugasKeyPressed

    private void btnPetugas3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugas3ActionPerformed
        pilihan = 1;
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugas3ActionPerformed

    private void btnPetugas3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugas3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPetugas3KeyPressed

    private void UkuranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UkuranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UkuranActionPerformed

    private void UkuranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UkuranKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_UkuranKeyPressed

    private void KristaloidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KristaloidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KristaloidActionPerformed

    private void KristaloidKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KristaloidKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KristaloidKeyPressed

    private void LamaAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamaAnestesiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamaAnestesiActionPerformed

    private void LamaAnestesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamaAnestesiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamaAnestesiKeyPressed

    private void LamaOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamaOperasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamaOperasiActionPerformed

    private void LamaOperasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamaOperasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamaOperasiKeyPressed

    private void KoloidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KoloidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KoloidActionPerformed

    private void KoloidKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KoloidKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KoloidKeyPressed

    private void tbAnestesiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesiMouseClicked
        if (tabModeDataAwal.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesiMouseClicked

    private void tbAnestesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesiKeyPressed
        if (tabModeDataAwal.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbAnestesiKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void tbAnestesi1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi1MouseClicked
        if (tabModePraInduksi.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi1MouseClicked

    private void tbAnestesi1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi1KeyPressed

    private void ChkInput2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput2ActionPerformed

    private void tbAnestesi2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi2MouseClicked
        if (tabModePremedikasi.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi2MouseClicked

    private void tbAnestesi2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi2KeyPressed

    private void ChkInput3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput3ActionPerformed

    private void tbAnestesi3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi3MouseClicked
        if (tabModeKeadaanOperasi.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi3MouseClicked

    private void tbAnestesi3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi3KeyPressed

    private void ChkInput4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput4ActionPerformed

    private void tbAnestesi4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi4MouseClicked
        if (tabModeSectio.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi4MouseClicked

    private void tbAnestesi4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi4KeyPressed

    private void ChkInput5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput5ActionPerformed

    private void tbAnestesi5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi5MouseClicked
        if (tabModeDataAwal.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi5MouseClicked

    private void tbAnestesi5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi5KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi5KeyPressed

    private void ChkInput7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput7ActionPerformed

    private void tbAnestesi6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi6MouseClicked
        if (tabModePascaAnestesiMasukKamar.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi6MouseClicked

    private void tbAnestesi6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi6KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi6KeyPressed

    private void ChkInput8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput8ActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAll1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAll1ActionPerformed
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAll1ActionPerformed

    private void BtnAll1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAll1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            tampil();
            TCari.setText("");
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }//GEN-LAST:event_BtnAll1KeyPressed

    private void txtKesadaranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKesadaranKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKesadaranKeyPressed

    private void GCSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GCSKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_GCSKeyPressed

    private void Support1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Support1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Support1KeyPressed

    private void Support2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Support2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Support2KeyPressed

    private void RrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RrKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_RrKeyPressed

    private void UrinariaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UrinariaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_UrinariaKeyPressed

    private void LainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LainKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LainKeyPressed

    private void DosisPremedikasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DosisPremedikasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DosisPremedikasiKeyPressed

    private void tbAnestesi9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi9MouseClicked

        if (tabModeAnestesiUmumDataAwal.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi9MouseClicked

    private void tbAnestesi9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi9KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi9KeyPressed

    private void InduksiKetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InduksiKetKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_InduksiKetKeyPressed

    private void TidalVolumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TidalVolumKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TidalVolumKeyPressed

    private void Rr2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Rr2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Rr2KeyPressed

    private void IeratioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IeratioKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_IeratioKeyPressed

    private void PeepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PeepKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PeepKeyPressed

    private void TeknikKhususKetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TeknikKhususKetKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TeknikKhususKetKeyPressed

    private void ChkInput10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput10ActionPerformed

    private void tbAnestesi10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi10MouseClicked
        if (tabModeAnestesiUmumMonitoring.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi10MouseClicked

    private void tbAnestesi10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi10KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi10KeyPressed

    private void ChkInput11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput11ActionPerformed

    private void PipKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PipKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PipKeyPressed

    private void tbAnestesi11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi11MouseClicked
        if (tabModeAnestesiUmumKeterangan.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi11MouseClicked

    private void tbAnestesi11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi11KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi11KeyPressed

    private void ChkInput12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput12ActionPerformed

    private void tbAnestesi12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnestesi12MouseClicked
        if (tabModeBlokadeRegional.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbAnestesi12MouseClicked

    private void tbAnestesi12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAnestesi12KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbAnestesi12KeyPressed

    private void ChkInput13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInput13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInput13ActionPerformed

    private void AnestesiLokalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnestesiLokalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AnestesiLokalKeyPressed

    private void KonsentrasiJumlahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KonsentrasiJumlahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KonsentrasiJumlahKeyPressed

    private void ObatTambahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ObatTambahanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_ObatTambahanKeyPressed

    private void DosisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DosisKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DosisKeyPressed

    private void KonsentrasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KonsentrasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KonsentrasiKeyPressed

    private void WaktuMulaiSuntikJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_WaktuMulaiSuntikJamKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_WaktuMulaiSuntikJamKeyPressed

    private void AnalgesiJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnalgesiJamKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AnalgesiJamKeyPressed

    private void LamanyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamanyaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamanyaKeyPressed

    private void LamanyaMenitAnalgesiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamanyaMenitAnalgesiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamanyaMenitAnalgesiKeyPressed

    private void OperasiJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_OperasiJamKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_OperasiJamKeyPressed

    private void LamanyajamOperasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamanyajamOperasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamanyajamOperasiKeyPressed

    private void LamanyaJamMenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamanyaJamMenitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamanyaJamMenitKeyPressed

    private void TindakanAnestesiTambahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TindakanAnestesiTambahanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TindakanAnestesiTambahanKeyPressed

    private void DarahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DarahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DarahActionPerformed

    private void DarahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DarahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DarahKeyPressed

    private void KomponenDarahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KomponenDarahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KomponenDarahActionPerformed

    private void KomponenDarahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KomponenDarahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KomponenDarahKeyPressed

    private void PerdarahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PerdarahanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PerdarahanActionPerformed

    private void PerdarahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PerdarahanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PerdarahanKeyPressed

    private void DiuresisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiuresisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiuresisActionPerformed

    private void DiuresisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiuresisKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiuresisKeyPressed

    private void CairanLainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CairanLainActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CairanLainActionPerformed

    private void CairanLainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CairanLainKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CairanLainKeyPressed

    private void Apgrar1MenitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Apgrar1MenitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Apgrar1MenitActionPerformed

    private void Apgrar1MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Apgrar1MenitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Apgrar1MenitKeyPressed

    private void Apgar5MenitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Apgar5MenitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Apgar5MenitActionPerformed

    private void Apgar5MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Apgar5MenitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Apgar5MenitKeyPressed

    private void Apgar10MenitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Apgar10MenitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Apgar10MenitActionPerformed

    private void Apgar10MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Apgar10MenitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Apgar10MenitKeyPressed

    private void TensiMasukKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TensiMasukKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TensiMasukKamarKeyPressed

    private void NadiMasukKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiMasukKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NadiMasukKamarKeyPressed

    private void Support3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Support3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Support3KeyPressed

    private void Support4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Support4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Support4KeyPressed

    private void O2MasukKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_O2MasukKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_O2MasukKamarKeyPressed

    private void Spo2MasukKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Spo2MasukKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Spo2MasukKamarKeyPressed

    private void RrMasukKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RrMasukKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_RrMasukKamarKeyPressed

    private void SkorNyeriKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SkorNyeriKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SkorNyeriKeyPressed

    private void PenyulitIntraOperatifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenyulitIntraOperatifKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PenyulitIntraOperatifKeyPressed

    private void Scroll10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Scroll10MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Scroll10MouseClicked

    private void Scroll11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Scroll11MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Scroll11MouseClicked

    private void LetakPenderitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LetakPenderitaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LetakPenderitaActionPerformed

    private void Scroll4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Scroll4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_Scroll4MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMStatusAnestesi dialog = new RMStatusAnestesi(new javax.swing.JFrame(), true);
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
    private widget.ComboBox ASA;
    private widget.ComboBox Airway;
    private widget.ComboBox Airway2;
    private widget.ComboBox Alergi;
    private widget.TextBox AnalgesiJam;
    private widget.TextBox AnalgesiSetinggiSegmen;
    private widget.InternalFrame AnastesiUmumKeterangan;
    private widget.TextBox AnestesiLokal;
    private javax.swing.JTabbedPane AnestesiUmum;
    private widget.TextBox Apgar10Menit;
    private widget.TextBox Apgar5Menit;
    private widget.TextBox Apgrar1Menit;
    private widget.ComboBox BalonNoBalon;
    private widget.TextBox Bb;
    private javax.swing.JTabbedPane BlokadeRegional;
    private widget.Button BtnAll;
    private widget.Button BtnAll1;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter2;
    private widget.Button BtnDokter3;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.TextBox CairanLain;
    private widget.InternalFrame Catatan;
    private javax.swing.JTabbedPane CatatanKamarPemulihan;
    private widget.ComboBox CheckMonitoring;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkInput10;
    private widget.CekBox ChkInput11;
    private widget.CekBox ChkInput12;
    private widget.CekBox ChkInput13;
    private widget.CekBox ChkInput2;
    private widget.CekBox ChkInput3;
    private widget.CekBox ChkInput4;
    private widget.CekBox ChkInput5;
    private widget.CekBox ChkInput7;
    private widget.CekBox ChkInput8;
    private widget.CekBox ChkJln;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPTgl;
    private widget.TextBox Darah;
    private widget.InternalFrame DataAwal;
    private widget.TextBox DiagnosaPrabedah;
    private widget.TextBox Diuresis;
    private widget.TextBox Dosis;
    private widget.TextBox DosisPremedikasi;
    private widget.TextArea Ekg;
    private widget.TextBox Fio2;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormInput10;
    private widget.PanelBiasa FormInput11;
    private widget.PanelBiasa FormInput12;
    private widget.PanelBiasa FormInput13;
    private widget.PanelBiasa FormInput15;
    private widget.PanelBiasa FormInput16;
    private widget.PanelBiasa FormInput18;
    private widget.PanelBiasa FormInput19;
    private widget.PanelBiasa FormInput20;
    private widget.PanelBiasa FormInput21;
    private widget.TextBox GCS;
    private widget.TextBox Gastro;
    private widget.TextBox Hmt;
    private widget.TextBox Ieratio;
    private widget.ComboBox Induksi;
    private widget.TextBox InduksiKet;
    private widget.ComboBox InformedConsent;
    private widget.TextBox JenisAnestesi;
    private widget.ComboBox JenisAnestesiPremedikasi;
    private widget.ComboBox JenisKelamin;
    private widget.ComboBox JenisPremedikasi1;
    private widget.TextBox Jk;
    private widget.ComboBox Kanul;
    private widget.ComboBox KanulMasukKamar;
    private widget.TextBox KdAsistenAnestesi;
    private widget.TextBox KdAsistenBedah;
    private widget.TextBox KdDokterAnastesi;
    private widget.TextBox KdDokterBedah;
    private widget.TextBox KdPetugas;
    private widget.ComboBox KeadaanBayi;
    private widget.ComboBox Kesadaran;
    private widget.TextBox Koloid;
    private widget.TextBox KomponenDarah;
    private widget.TextBox Konsentrasi;
    private widget.TextBox KonsentrasiJumlah;
    private widget.TextBox Kristaloid;
    private widget.TextArea Lab;
    private widget.TextBox Lain;
    private widget.TextBox Lainnya2;
    private widget.TextBox LamaAnestesi;
    private widget.TextBox LamaOperasi;
    private widget.TextBox Lamanya;
    private widget.TextBox LamanyaJamMenit;
    private widget.TextBox LamanyaMenitAnalgesi;
    private widget.TextBox LamanyajamOperasi;
    private widget.ComboBox LetakPenderita;
    private widget.editorpane LoadHTML;
    private widget.TextBox LokasiTusukan;
    private widget.TextArea MasalahDuranteOperasi;
    private widget.InternalFrame MasukKamar;
    private widget.InternalFrame MasukKamar1;
    private widget.InternalFrame MasukKamar2;
    private widget.Tanggal MasukKamarPulih;
    private widget.TextArea Medikasi;
    private widget.TextBox MedikasiPrabedah;
    private widget.ComboBox MesinAnestesi;
    private widget.TextBox Metabolik;
    private widget.InternalFrame Monitoring;
    private widget.TextBox Muskuloskletal;
    private widget.TextBox Nadi;
    private widget.TextBox Nadi2;
    private widget.TextBox NadiMasukKamar;
    private widget.TextBox NamaObatAnestesi;
    private widget.TextBox NmAsistenAnestasi;
    private widget.TextBox NmAsistenBeda;
    private widget.TextBox NmDokterAnestesi;
    private widget.TextBox NmDokterBedah;
    private widget.TextBox NmObatPremedikasi1;
    private widget.TextBox NmPetugas;
    private widget.TextBox O2;
    private widget.TextBox O2MasukKamar;
    private widget.ComboBox ObatAnestesi;
    private widget.ComboBox ObatEmergensi;
    private widget.TextBox ObatTambahan;
    private widget.InternalFrame ObservasiKamar;
    private widget.InternalFrame ObservasiKamar1;
    private widget.TextBox OperasiJam;
    private javax.swing.JPanel PanelInput;
    private javax.swing.JPanel PanelInput10;
    private javax.swing.JPanel PanelInput11;
    private javax.swing.JPanel PanelInput12;
    private javax.swing.JPanel PanelInput13;
    private javax.swing.JPanel PanelInput2;
    private javax.swing.JPanel PanelInput3;
    private javax.swing.JPanel PanelInput4;
    private javax.swing.JPanel PanelInput5;
    private javax.swing.JPanel PanelInput7;
    private javax.swing.JPanel PanelInput8;
    private widget.TextBox Peep;
    private widget.TextBox PemantauanNadi2;
    private widget.TextArea PemberianCairan;
    private widget.ComboBox PengaturaNafas;
    private widget.TextBox PenyulitIntraOperatif;
    private widget.TextBox Perdarahan;
    private widget.ComboBox Pernapasan15;
    private widget.TextBox PernapasanMasuk12;
    private widget.TextBox PernapasanMasuk14;
    private widget.TextBox PernapasanMasuk16;
    private widget.TextBox PernapasanMasuk19;
    private widget.TextBox PernapasanMasuk20;
    private widget.TextBox Pip;
    private widget.ComboBox Posisi1;
    private widget.InternalFrame Prainduksi;
    private widget.InternalFrame Premedikasi;
    private widget.TextArea PxLain;
    private widget.TextBox Respirasi;
    private widget.TextBox Rr;
    private widget.TextBox Rr2;
    private widget.TextBox Rr4;
    private widget.TextBox RrMasukKamar;
    private widget.TextBox Ruangan;
    private widget.TextBox Saraf;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll10;
    private widget.ScrollPane Scroll11;
    private widget.ScrollPane Scroll12;
    private widget.ScrollPane Scroll13;
    private widget.ScrollPane Scroll2;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.TextBox Selama2;
    private widget.TextBox SirkulasiMasuk14;
    private widget.TextBox SirkulasiMasuk15;
    private widget.TextBox SirkulasiMasuk16;
    private widget.TextBox SirkulasiMasuk17;
    private widget.TextBox SirkulasiMasuk18;
    private widget.TextBox SirkulasiMasuk19;
    private widget.TextBox SirkulasiMasuk21;
    private widget.TextBox SirkulasiMasuk22;
    private widget.TextBox SkorNyeri;
    private widget.TextBox Spo2;
    private widget.TextBox Spo2MasukKamar;
    private widget.ComboBox SuctionAparatus;
    private widget.TextBox Suhu;
    private widget.TextBox Support1;
    private widget.TextBox Support2;
    private widget.TextBox Support3;
    private widget.TextBox Support4;
    private widget.TextBox TCari;
    private widget.TextArea TFP;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.ComboBox TatalaksanaJalanNafas;
    private widget.TextBox TekananDarah;
    private widget.ComboBox Teknik;
    private widget.ComboBox TeknikKhusus;
    private widget.TextBox TeknikKhususKet;
    private widget.TextBox Tensi;
    private widget.TextBox TensiMasukKamar;
    private widget.TextBox TglLahir;
    private widget.TextBox TglMasukRS;
    private widget.TextArea Thorax;
    private widget.TextBox TidalVolum;
    private widget.TextArea Tindakan;
    private widget.TextBox TindakanAnestesiTambahan;
    private widget.TextBox Ukuran;
    private widget.TextBox Urinaria;
    private widget.ComboBox Vasokonstriktor;
    private widget.TextBox WaktuMulaiSuntikJam;
    private widget.Button btnPetugas1;
    private widget.Button btnPetugas2;
    private widget.Button btnPetugas3;
    private widget.ComboBox cmbDtk;
    private widget.ComboBox cmbJam;
    private widget.ComboBox cmbMnt;
    private widget.ComboBox cmbRespirasi;
    private widget.ComboBox cmbRespirasiMasukKamar;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel100;
    private widget.Label jLabel101;
    private widget.Label jLabel102;
    private widget.Label jLabel103;
    private widget.Label jLabel104;
    private widget.Label jLabel105;
    private widget.Label jLabel106;
    private widget.Label jLabel107;
    private widget.Label jLabel108;
    private widget.Label jLabel109;
    private widget.Label jLabel11;
    private widget.Label jLabel110;
    private widget.Label jLabel111;
    private widget.Label jLabel112;
    private widget.Label jLabel113;
    private widget.Label jLabel114;
    private widget.Label jLabel115;
    private widget.Label jLabel116;
    private widget.Label jLabel117;
    private widget.Label jLabel118;
    private widget.Label jLabel119;
    private widget.Label jLabel12;
    private widget.Label jLabel120;
    private widget.Label jLabel121;
    private widget.Label jLabel122;
    private widget.Label jLabel123;
    private widget.Label jLabel124;
    private widget.Label jLabel125;
    private widget.Label jLabel126;
    private widget.Label jLabel127;
    private widget.Label jLabel128;
    private widget.Label jLabel129;
    private widget.Label jLabel13;
    private widget.Label jLabel130;
    private widget.Label jLabel131;
    private widget.Label jLabel132;
    private widget.Label jLabel133;
    private widget.Label jLabel134;
    private widget.Label jLabel135;
    private widget.Label jLabel136;
    private widget.Label jLabel137;
    private widget.Label jLabel138;
    private widget.Label jLabel139;
    private widget.Label jLabel140;
    private widget.Label jLabel141;
    private widget.Label jLabel142;
    private widget.Label jLabel143;
    private widget.Label jLabel144;
    private widget.Label jLabel145;
    private widget.Label jLabel146;
    private widget.Label jLabel147;
    private widget.Label jLabel148;
    private widget.Label jLabel149;
    private widget.Label jLabel150;
    private widget.Label jLabel151;
    private widget.Label jLabel152;
    private widget.Label jLabel153;
    private widget.Label jLabel154;
    private widget.Label jLabel155;
    private widget.Label jLabel156;
    private widget.Label jLabel157;
    private widget.Label jLabel158;
    private widget.Label jLabel159;
    private widget.Label jLabel16;
    private widget.Label jLabel160;
    private widget.Label jLabel161;
    private widget.Label jLabel162;
    private widget.Label jLabel163;
    private widget.Label jLabel164;
    private widget.Label jLabel165;
    private widget.Label jLabel166;
    private widget.Label jLabel167;
    private widget.Label jLabel168;
    private widget.Label jLabel169;
    private widget.Label jLabel170;
    private widget.Label jLabel171;
    private widget.Label jLabel172;
    private widget.Label jLabel173;
    private widget.Label jLabel174;
    private widget.Label jLabel175;
    private widget.Label jLabel176;
    private widget.Label jLabel177;
    private widget.Label jLabel178;
    private widget.Label jLabel179;
    private widget.Label jLabel18;
    private widget.Label jLabel180;
    private widget.Label jLabel181;
    private widget.Label jLabel182;
    private widget.Label jLabel183;
    private widget.Label jLabel184;
    private widget.Label jLabel185;
    private widget.Label jLabel186;
    private widget.Label jLabel187;
    private widget.Label jLabel188;
    private widget.Label jLabel189;
    private widget.Label jLabel19;
    private widget.Label jLabel190;
    private widget.Label jLabel191;
    private widget.Label jLabel192;
    private widget.Label jLabel193;
    private widget.Label jLabel194;
    private widget.Label jLabel197;
    private widget.Label jLabel198;
    private widget.Label jLabel199;
    private widget.Label jLabel20;
    private widget.Label jLabel200;
    private widget.Label jLabel201;
    private widget.Label jLabel202;
    private widget.Label jLabel203;
    private widget.Label jLabel204;
    private widget.Label jLabel21;
    private widget.Label jLabel24;
    private widget.Label jLabel26;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel44;
    private widget.Label jLabel57;
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
    private widget.Label jLabel82;
    private widget.Label jLabel83;
    private widget.Label jLabel84;
    private widget.Label jLabel85;
    private widget.Label jLabel86;
    private widget.Label jLabel87;
    private widget.Label jLabel88;
    private widget.Label jLabel89;
    private widget.Label jLabel9;
    private widget.Label jLabel90;
    private widget.Label jLabel91;
    private widget.Label jLabel92;
    private widget.Label jLabel93;
    private widget.Label jLabel94;
    private widget.Label jLabel95;
    private widget.Label jLabel96;
    private widget.Label jLabel97;
    private widget.Label jLabel98;
    private widget.Label jLabel99;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private widget.Label label12;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label17;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbAnestesi;
    private widget.Table tbAnestesi1;
    private widget.Table tbAnestesi10;
    private widget.Table tbAnestesi11;
    private widget.Table tbAnestesi12;
    private widget.Table tbAnestesi2;
    private widget.Table tbAnestesi3;
    private widget.Table tbAnestesi4;
    private widget.Table tbAnestesi5;
    private widget.Table tbAnestesi6;
    private widget.Table tbAnestesi9;
    private widget.TextBox txtKesadaran;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        setupTableBasedOnActiveTab();

        switch (TabRawat.getSelectedIndex()) {
            case 0:
                tampilDataAwal();
                break;
            case 1: // Anestesi Umum
                tampilAnestesiUmumDataAwal();
                break;
            case 2: // Monitoring
                tampilAnestesiUmumMonitoring();
                break;
            case 3: // Blokade Regional
                tampilBlokadeRegional();
                break;
            case 4: // Keadaan Selama Operasi
                tampilKeadaanOperasi();
                break;
            case 5: // Sectio Caesaria
                tampilSectio();
                break;
            case 6: // Keadaan Pasca Anestesi di Ruang Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        tampilPascaAnestesiMasukKamar();
                        break;
                    case 1:
                        tampilMonitoringPemulihan();
                        break;
                    case 2: // Score
                        tampilScore();
                        break;
                }
                break;
        }
    }

    private void tampilDataAwal() {
        if (tabModeDataAwal == null) {
            setupTableDataAwal();
        }

        Valid.tabelKosong(tabModeDataAwal);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oda.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oda.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, rp.tgl_registrasi, "
                    + "oda.kd_petugas, p3.nama as nm_petugas, "
                    + "oda.kd_dokter_anestesi, d1.nm_dokter as nm_dokter_anestesi, "
                    + "oda.kd_asisten_anestesi, p1.nama as nm_asisten_anestesi, "
                    + "oda.kd_dokter_bedah, d2.nm_dokter as nm_dokter_bedah, "
                    + "oda.kd_asisten_bedah, p2.nama as nm_asisten_bedah, "
                    + "oda.medikasi_prabedah, oda.jenis_anestesi, oda.diagnosa_prabedah, "
                    + "oda.informed_consent, oda.obat_anestesi, oda.tatalaksana_jalan_nafas, "
                    + "oda.mesin_anestesi, oda.obat_emergensi, oda.check_monitoring, "
                    + "oda.suction_aparatus "
                    + "FROM catatan_anestesi_data_awal oda "
                    + "INNER JOIN reg_periksa rp ON oda.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN dokter d1 ON oda.kd_dokter_anestesi = d1.kd_dokter "
                    + "LEFT JOIN petugas p1 ON oda.kd_asisten_anestesi = p1.nip "
                    + "LEFT JOIN dokter d2 ON oda.kd_dokter_bedah = d2.kd_dokter "
                    + "LEFT JOIN petugas p2 ON oda.kd_asisten_bedah = p2.nip "
                    + "LEFT JOIN petugas p3 ON oda.kd_petugas = p3.nip "
                    + "WHERE oda.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oda.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("tgl_registrasi"), // 7
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 8
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 9
                    rs.getString("kd_dokter_anestesi") != null ? rs.getString("kd_dokter_anestesi") : "", // 10
                    rs.getString("nm_dokter_anestesi") != null ? rs.getString("nm_dokter_anestesi") : "", // 11
                    rs.getString("kd_asisten_anestesi") != null ? rs.getString("kd_asisten_anestesi") : "", // 12
                    rs.getString("nm_asisten_anestesi") != null ? rs.getString("nm_asisten_anestesi") : "", // 13
                    rs.getString("kd_dokter_bedah") != null ? rs.getString("kd_dokter_bedah") : "", // 14
                    rs.getString("nm_dokter_bedah") != null ? rs.getString("nm_dokter_bedah") : "", // 15
                    rs.getString("kd_asisten_bedah") != null ? rs.getString("kd_asisten_bedah") : "", // 16
                    rs.getString("nm_asisten_bedah") != null ? rs.getString("nm_asisten_bedah") : "", // 17
                    rs.getString("medikasi_prabedah") != null ? rs.getString("medikasi_prabedah") : "", // 18
                    rs.getString("jenis_anestesi") != null ? rs.getString("jenis_anestesi") : "", // 19
                    rs.getString("diagnosa_prabedah") != null ? rs.getString("diagnosa_prabedah") : "", // 20
                    rs.getString("informed_consent") != null ? rs.getString("informed_consent") : "", // 21
                    rs.getString("obat_anestesi") != null ? rs.getString("obat_anestesi") : "", // 22
                    rs.getString("tatalaksana_jalan_nafas") != null ? rs.getString("tatalaksana_jalan_nafas") : "", // 23
                    rs.getString("mesin_anestesi") != null ? rs.getString("mesin_anestesi") : "", // 24
                    rs.getString("obat_emergensi") != null ? rs.getString("obat_emergensi") : "", // 25
                    rs.getString("check_monitoring") != null ? rs.getString("check_monitoring") : "", // 26
                    rs.getString("suction_aparatus") != null ? rs.getString("suction_aparatus") : "" // 27
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilDataAwal: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilPraInduksi() {
        if (tabModePraInduksi == null) {
            setupTablePraInduksi();
        }

        Valid.tabelKosong(tabModePraInduksi);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT opi.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, opi.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "opi.kd_petugas, p.nama as nm_petugas, "
                    + "opi.kesadaran, opi.gcs, opi.airway, opi.tensi, opi.nadi, "
                    + "opi.support1, opi.support2, opi.respirasi, opi.rr, "
                    + "opi.kanul_nasal, opi.o2, opi.spo2, opi.bb, opi.puasa_jam, "
                    + "opi.laboratorium, opi.ekg, opi.thorax_foto, opi.tfp, "
                    + "opi.pemeriksaan_lain, opi.sistem_saraf, opi.sistem_respirasi, "
                    + "opi.sistem_gastro, opi.sistem_urinaria, opi.sistem_muskuloskeletal, "
                    + "opi.sistem_metabolik, opi.lain_lain, opi.asa, opi.alergi "
                    + "FROM catatan_anestesi_pra_induksi opi "
                    + "INNER JOIN reg_periksa rp ON opi.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON opi.kd_petugas = p.nip "
                    + "WHERE opi.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY opi.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModePraInduksi.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("kesadaran") != null ? rs.getString("kesadaran") : "", // 9
                    rs.getString("gcs") != null ? rs.getString("gcs") : "", // 10
                    rs.getString("airway") != null ? rs.getString("airway") : "", // 11
                    rs.getString("tensi") != null ? rs.getString("tensi") : "", // 12
                    rs.getString("nadi") != null ? rs.getString("nadi") : "", // 13
                    rs.getString("support1") != null ? rs.getString("support1") : "", // 14
                    rs.getString("support2") != null ? rs.getString("support2") : "", // 15
                    rs.getString("respirasi") != null ? rs.getString("respirasi") : "", // 16
                    rs.getString("rr") != null ? rs.getString("rr") : "", // 17
                    rs.getString("kanul_nasal") != null ? rs.getString("kanul_nasal") : "", // 18
                    rs.getString("o2") != null ? rs.getString("o2") : "", // 19
                    rs.getString("spo2") != null ? rs.getString("spo2") : "", // 20
                    rs.getString("bb") != null ? rs.getString("bb") : "", // 21
                    rs.getString("puasa_jam") != null ? rs.getString("puasa_jam") : "", // 22
                    rs.getString("laboratorium") != null ? rs.getString("laboratorium") : "", // 23
                    rs.getString("ekg") != null ? rs.getString("ekg") : "", // 24
                    rs.getString("thorax_foto") != null ? rs.getString("thorax_foto") : "", // 25
                    rs.getString("tfp") != null ? rs.getString("tfp") : "", // 26
                    rs.getString("pemeriksaan_lain") != null ? rs.getString("pemeriksaan_lain") : "", // 27
                    rs.getString("sistem_saraf") != null ? rs.getString("sistem_saraf") : "", // 28
                    rs.getString("sistem_respirasi") != null ? rs.getString("sistem_respirasi") : "", // 29
                    rs.getString("sistem_gastro") != null ? rs.getString("sistem_gastro") : "", // 30
                    rs.getString("sistem_urinaria") != null ? rs.getString("sistem_urinaria") : "", // 31
                    rs.getString("sistem_muskuloskeletal") != null ? rs.getString("sistem_muskuloskeletal") : "", // 32
                    rs.getString("sistem_metabolik") != null ? rs.getString("sistem_metabolik") : "", // 33
                    rs.getString("lain_lain") != null ? rs.getString("lain_lain") : "", // 34
                    rs.getString("asa") != null ? rs.getString("asa") : "", // 35
                    rs.getString("alergi") != null ? rs.getString("alergi") : "" // 36
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilPraInduksi: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilPremedikasi() {
        if (tabModePremedikasi == null) {
            setupTablePremedikasi();
        }

        Valid.tabelKosong(tabModePremedikasi);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oap.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oap.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oap.kd_petugas, p.nama as nm_petugas, "
                    + "oap.jenis_premedikasi, oap.nm_obat_premedikasi, oap.dosis, "
                    + "oap.jenis_anestesi, oap.nama_obat_anestesi "
                    + "FROM catatan_anestesi_premedikasi oap "
                    + "INNER JOIN reg_periksa rp ON oap.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oap.kd_petugas = p.nip "
                    + "WHERE oap.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oap.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModePremedikasi.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("jenis_premedikasi") != null ? rs.getString("jenis_premedikasi") : "", // 9
                    rs.getString("nm_obat_premedikasi") != null ? rs.getString("nm_obat_premedikasi") : "", // 10
                    rs.getString("dosis") != null ? rs.getString("dosis") : "", // 11
                    rs.getString("jenis_anestesi") != null ? rs.getString("jenis_anestesi") : "", // 12
                    rs.getString("nama_obat_anestesi") != null ? rs.getString("nama_obat_anestesi") : "" // 13
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilPremedikasi: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilMonitoring() {
        if (tabModeDataAwal == null) {
            setupTableMonitoring();
        }

        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oam.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oam.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oam.o2, oam.n2o, oam.air, oam.isofluran, oam.lain_lain, oam.sevofluran, "
                    + "oam.infus, oam.barbiturat, oam.relaksan_depol, oam.relaksan_nin_dipol, "
                    + "oam.analgetika, oam.neuroleptik, oam.lain_lain_obat, "
                    + "oam.respirasi, oam.nadi, oam.tekanan_darah, oam.perdarahan, oam.spo2, "
                    + "oam.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_monitoring oam "
                    + "INNER JOIN reg_periksa rp ON oam.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oam.kd_petugas = p.nip "
                    + "WHERE oam.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oam.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("o2"),
                    rs.getString("n2o"),
                    rs.getString("air"),
                    rs.getString("isofluran"),
                    rs.getString("lain_lain"),
                    rs.getString("sevofluran"),
                    rs.getString("infus"),
                    rs.getString("barbiturat"),
                    rs.getString("relaksan_depol"),
                    rs.getString("relaksan_nin_dipol"),
                    rs.getString("analgetika"),
                    rs.getString("neuroleptik"),
                    rs.getString("lain_lain_obat"),
                    rs.getString("respirasi"),
                    rs.getString("nadi"),
                    rs.getString("tekanan_darah"),
                    rs.getString("perdarahan"),
                    rs.getString("spo2"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilCatatan() {
        if (tabModeDataAwal == null) {
            setupTableCatatan();
        }

        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oac.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oac.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oac.diagnosa_pasca_bedah, oac.lama_operasi, oac.lama_anestesi, "
                    + "oac.cairan_masuk, oac.jumlah_cairan_masuk, oac.cairan_keluar, oac.jumlah_cairan_keluar, "
                    + "oac.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_catatan oac "
                    + "INNER JOIN reg_periksa rp ON oac.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oac.kd_petugas = p.nip "
                    + "WHERE oac.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oac.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("diagnosa_pasca_bedah"),
                    rs.getString("lama_operasi"),
                    rs.getString("lama_anestesi"),
                    rs.getString("cairan_masuk"),
                    rs.getString("jumlah_cairan_masuk"),
                    rs.getString("cairan_keluar"),
                    rs.getString("jumlah_cairan_keluar"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilMasukKamar() {
        if (tabModeDataAwal == null) {
            setupTableMasukKamar();
        }

        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oamk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oamk.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oamk.jam_masuk, oamk.jalan_nafas, oamk.pernapasan, oamk.bila_spontan, oamk.kesadaran, "
                    + "oamk.aktifitas_masuk, oamk.sirkulasi_masuk, oamk.pernapasan_masuk, oamk.kesadaran_masuk, "
                    + "oamk.warna_kulit_masuk, oamk.total_masuk, oamk.skor_bromage, "
                    + "oamk.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_masuk_kamar oamk "
                    + "INNER JOIN reg_periksa rp ON oamk.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oamk.kd_petugas = p.nip "
                    + "WHERE oamk.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oamk.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("jam_masuk"),
                    rs.getString("jalan_nafas"),
                    rs.getString("pernapasan"),
                    rs.getString("bila_spontan"),
                    rs.getString("kesadaran"),
                    rs.getString("aktifitas_masuk"),
                    rs.getString("sirkulasi_masuk"),
                    rs.getString("pernapasan_masuk"),
                    rs.getString("kesadaran_masuk"),
                    rs.getString("warna_kulit_masuk"),
                    rs.getString("total_masuk"),
                    rs.getString("skor_bromage"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilObservasiKamar() {
        if (tabModeDataAwal == null) {
            setupTableObservasiKamar();
        }

        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oaok.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oaok.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oaok.tvs, oaok.rr, oaok.nadi, oaok.tekanan_darah, oaok.vas, "
                    + "oaok.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_observasi_kamar oaok "
                    + "INNER JOIN reg_periksa rp ON oaok.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oaok.kd_petugas = p.nip "
                    + "WHERE oaok.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oaok.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("tvs"),
                    rs.getString("rr"),
                    rs.getString("nadi"),
                    rs.getString("tekanan_darah"),
                    rs.getString("vas"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilKeluarKamar() {
        if (tabModeDataAwal == null) {
            setupTableKeluarKamar();
        }

        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oakk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oakk.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oakk.jam_keluar, oakk.aktifitas_keluar, oakk.sirkulasi_keluar, oakk.pernapasan_keluar, "
                    + "oakk.kesadaran_keluar, oakk.warna_kulit_keluar, oakk.total_keluar, oakk.skor_bromage_keluar, "
                    + "oakk.pindah_ke, oakk.catatan_khusus, oakk.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_keluar_kamar oakk "
                    + "INNER JOIN reg_periksa rp ON oakk.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oakk.kd_petugas = p.nip "
                    + "WHERE oakk.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oakk.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("jam_keluar"),
                    rs.getString("aktifitas_keluar"),
                    rs.getString("sirkulasi_keluar"),
                    rs.getString("pernapasan_keluar"),
                    rs.getString("kesadaran_keluar"),
                    rs.getString("warna_kulit_keluar"),
                    rs.getString("total_keluar"),
                    rs.getString("skor_bromage_keluar"),
                    rs.getString("pindah_ke"),
                    rs.getString("catatan_khusus"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilInstruksiPasca() {
        if (tabModeDataAwal == null) {
            setupTableInstruksiPasca();
        }

        Valid.tabelKosong(tabModeDataAwal);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oaip.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oaip.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oaip.bila_kesakitan, oaip.bila_mual_muntah, oaip.antibiotika, oaip.obat_lain, "
                    + "oaip.infus, oaip.minum, oaip.pemantauan_nadi, oaip.selama, "
                    + "oaip.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_instruksi_pasca oaip "
                    + "INNER JOIN reg_periksa rp ON oaip.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oaip.kd_petugas = p.nip "
                    + "WHERE oaip.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oaip.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("bila_kesakitan"),
                    rs.getString("bila_mual_muntah"),
                    rs.getString("antibiotika"),
                    rs.getString("obat_lain"),
                    rs.getString("infus"),
                    rs.getString("minum"),
                    rs.getString("pemantauan_nadi"),
                    rs.getString("selama"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void emptTeks() {
        KdPetugas.setText("");
        NmPetugas.setText("");
        KdDokterAnastesi.setText("");
        NmDokterAnestesi.setText("");
        KdAsistenAnestesi.setText("");
        NmAsistenAnestasi.setText("");
        KdDokterBedah.setText("");
        NmDokterBedah.setText("");
        KdAsistenBedah.setText("");
        NmAsistenBeda.setText("");
        MedikasiPrabedah.setText("");
        JenisAnestesi.setText("");
        DiagnosaPrabedah.setText("");

        // Reset ComboBox ke index 0
        if (InformedConsent.getItemCount() > 0) {
            InformedConsent.setSelectedIndex(0);
        }
        if (ObatAnestesi.getItemCount() > 0) {
            ObatAnestesi.setSelectedIndex(0);
        }
        if (TatalaksanaJalanNafas.getItemCount() > 0) {
            TatalaksanaJalanNafas.setSelectedIndex(0);
        }
        if (MesinAnestesi.getItemCount() > 0) {
            MesinAnestesi.setSelectedIndex(0);
        }
        if (ObatEmergensi.getItemCount() > 0) {
            ObatEmergensi.setSelectedIndex(0);
        }
        if (CheckMonitoring.getItemCount() > 0) {
            CheckMonitoring.setSelectedIndex(0);
        }
        if (SuctionAparatus.getItemCount() > 0) {
            SuctionAparatus.setSelectedIndex(0);
        }

        // Set tanggal ke hari ini
        DTPTgl.setDate(new Date());
        cmbJam.setSelectedItem(Sequel.cariIsi("select time(now())").substring(0, 2));
        cmbMnt.setSelectedItem(Sequel.cariIsi("select time(now())").substring(3, 5));
        cmbDtk.setSelectedItem(Sequel.cariIsi("select time(now())").substring(6, 8));

        // Pra Induksi
        txtKesadaran.setText("");
        GCS.setText("");
        Airway.setSelectedIndex(0);
        Support1.setText("");
        Support2.setText("");
        cmbRespirasi.setSelectedIndex(0);
        Rr.setText("");
        Kanul.setSelectedIndex(0);
        O2.setText("");
        Bb.setText("");
        Hmt.setText("");
        Tensi.setText("");
        Nadi.setText("");
        Spo2.setText("");
        Lab.setText("");
        Ekg.setText("");
        Thorax.setText("");
        TFP.setText("");
        PxLain.setText("");
        Saraf.setText("");
        Respirasi.setText("");
        Gastro.setText("");
        Metabolik.setText("");
        Urinaria.setText("");
        Muskuloskletal.setText("");
        Lain.setText("");
        ASA.setSelectedIndex(0);
        Alergi.setSelectedIndex(0);

        // Premedikasi
        JenisPremedikasi1.setSelectedIndex(0);
        NmObatPremedikasi1.setText("");
        DosisPremedikasi.setText("");
        JenisAnestesiPremedikasi.setSelectedIndex(0);
        NamaObatAnestesi.setText("");
        Posisi1.setSelectedIndex(0);
        SirkulasiMasuk15.setText("");
        SirkulasiMasuk16.setText("");
        SirkulasiMasuk17.setText("");
        PernapasanMasuk19.setText("");
        PernapasanMasuk20.setText("");
        Pernapasan15.setSelectedIndex(0);

        // Anestesi Umum - Data Awal
        Induksi.setSelectedIndex(0);
        InduksiKet.setText("");
        PengaturaNafas.setSelectedIndex(0);
        TidalVolum.setText("");
        Rr2.setText("");
        Ieratio.setText("");
        Peep.setText("");
        Pip.setText("");
        Fio2.setText("");
        if (TeknikKhusus.getItemCount() > 0) {
            TeknikKhusus.setSelectedIndex(0);
        }
        TeknikKhususKet.setText("");

        // Anestesi Umum - Monitoring
        PernapasanMasuk12.setText("");
        PernapasanMasuk14.setText("");
        PernapasanMasuk16.setText("");
        Lainnya2.setText("");
        SirkulasiMasuk19.setText("");
        SirkulasiMasuk14.setText("");
        SirkulasiMasuk18.setText("");
        SirkulasiMasuk21.setText("");
        SirkulasiMasuk22.setText("");
        PemantauanNadi2.setText("");
        Selama2.setText("");

        // Anestesi Umum - Keterangan
        Medikasi.setText("");
        PemberianCairan.setText("");
        MasalahDuranteOperasi.setText("");
        Tindakan.setText("");

        // Blokade Regional
        Teknik.setSelectedIndex(0);
        LokasiTusukan.setText("");
        AnalgesiSetinggiSegmen.setText("");
        AnestesiLokal.setText("");
        KonsentrasiJumlah.setText("");
        ObatTambahan.setText("");
        Dosis.setText("");
        Vasokonstriktor.setSelectedIndex(0);
        Konsentrasi.setText("");
        WaktuMulaiSuntikJam.setText("");
        AnalgesiJam.setText("");
        Lamanya.setText("");
        LamanyaMenitAnalgesi.setText("");
        OperasiJam.setText("");
        LamanyajamOperasi.setText("");
        LamanyaJamMenit.setText("");
        TindakanAnestesiTambahan.setText("");

        // Keadaan Selama Operasi
        LetakPenderita.setSelectedIndex(0);
        Airway2.setSelectedIndex(0);
        Ukuran.setText("");
        Kristaloid.setText("");
        LamaAnestesi.setText("");
        LamaOperasi.setText("");
        Koloid.setText("");
        Darah.setText("");
        KomponenDarah.setText("");
        Perdarahan.setText("");
        Diuresis.setText("");
        CairanLain.setText("");

        // Sectio Caesaria
        KeadaanBayi.setSelectedIndex(0);
        JenisKelamin.setSelectedIndex(0);
        Apgrar1Menit.setText("");
        Apgar5Menit.setText("");
        Apgar10Menit.setText("");

        // Masuk Kamar Pemulihan
        MasukKamarPulih.setDate(new Date());
        Kesadaran.setSelectedIndex(0);
        TensiMasukKamar.setText("");
        NadiMasukKamar.setText("");
        Support3.setText("");
        Support4.setText("");
        cmbRespirasiMasukKamar.setSelectedIndex(0);

        KanulMasukKamar.setSelectedIndex(0);
        O2MasukKamar.setText("");
        Spo2MasukKamar.setText("");
        RrMasukKamar.setText("");
        SkorNyeri.setText("");
        PenyulitIntraOperatif.setText("");

        // Monitoring Pemulihan
        Suhu.setText("");
        Rr4.setText("");
        Nadi2.setText("");
        TekananDarah.setText("");

        // Reset tanggal
        DTPTgl.setDate(new Date());
    }

    private void getData() {
        int currentTabIndex = TabRawat.getSelectedIndex();

        try {
            // Data Awal - Tab 0
            if (currentTabIndex == 0 && tbAnestesi.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi.getSelectedRow();

                // Data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi, selectedRow, 6));
                TglMasukRS.setText(getValueSafely(tbAnestesi, selectedRow, 7));

                // Clear fields yang tidak digunakan
                clearPraInduksiFields();
                clearPremedikasFields();
                clearAnestesiUmumDataAwalFields();
                clearAnestesiUmumMonitoringFields();
                clearAnestesiUmumKeteranganFields();
                clearBlokadeRegionalFields();
                clearKeadaanOperasiFields();
                clearSectioFields();
                clearMasukKamarFields();
                clearMonitoringPemulihanFields();

                // Data spesifik Data Awal
                KdPetugas.setText(getValueSafely(tbAnestesi, selectedRow, 8));
                NmPetugas.setText(getValueSafely(tbAnestesi, selectedRow, 9));
                KdDokterAnastesi.setText(getValueSafely(tbAnestesi, selectedRow, 10));
                NmDokterAnestesi.setText(getValueSafely(tbAnestesi, selectedRow, 11));
                KdAsistenAnestesi.setText(getValueSafely(tbAnestesi, selectedRow, 12));
                NmAsistenAnestasi.setText(getValueSafely(tbAnestesi, selectedRow, 13));
                KdDokterBedah.setText(getValueSafely(tbAnestesi, selectedRow, 14));
                NmDokterBedah.setText(getValueSafely(tbAnestesi, selectedRow, 15));
                KdAsistenBedah.setText(getValueSafely(tbAnestesi, selectedRow, 16));
                NmAsistenBeda.setText(getValueSafely(tbAnestesi, selectedRow, 17));
                MedikasiPrabedah.setText(getValueSafely(tbAnestesi, selectedRow, 18));
                JenisAnestesi.setText(getValueSafely(tbAnestesi, selectedRow, 19));
                DiagnosaPrabedah.setText(getValueSafely(tbAnestesi, selectedRow, 20));

                // Set ComboBox values
                setComboBoxSafely(InformedConsent, getValueSafely(tbAnestesi, selectedRow, 21));
                setComboBoxSafely(ObatAnestesi, getValueSafely(tbAnestesi, selectedRow, 22));
                setComboBoxSafely(TatalaksanaJalanNafas, getValueSafely(tbAnestesi, selectedRow, 23));
                setComboBoxSafely(MesinAnestesi, getValueSafely(tbAnestesi, selectedRow, 24));
                setComboBoxSafely(ObatEmergensi, getValueSafely(tbAnestesi, selectedRow, 25));
                setComboBoxSafely(CheckMonitoring, getValueSafely(tbAnestesi, selectedRow, 26));
                setComboBoxSafely(SuctionAparatus, getValueSafely(tbAnestesi, selectedRow, 27));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi, selectedRow, 5));
                String tanggalString = getValueSafely(tbAnestesi, selectedRow, 5);
                setTimeFromDateString(tanggalString);

                // Pra Induksi - Tab 1  
            } else if (currentTabIndex == 1 && tbAnestesi1.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi1.getSelectedRow();

                // Data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi1, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi1, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi1, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi1, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi1, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi1, selectedRow, 6));

                // Clear fields yang tidak digunakan
                clearDataAwalFields();
                clearPremedikasFields();
                clearAnestesiUmumDataAwalFields();
                clearAnestesiUmumMonitoringFields();
                clearAnestesiUmumKeteranganFields();
                clearBlokadeRegionalFields();
                clearKeadaanOperasiFields();
                clearSectioFields();
                clearMasukKamarFields();
                clearMonitoringPemulihanFields();

                // Data petugas
                KdPetugas.setText(getValueSafely(tbAnestesi1, selectedRow, 7));
                NmPetugas.setText(getValueSafely(tbAnestesi1, selectedRow, 8));

                // Data spesifik Pra Induksi (sesuai urutan tabel)
                txtKesadaran.setText(getValueSafely(tbAnestesi1, selectedRow, 9));
                GCS.setText(getValueSafely(tbAnestesi1, selectedRow, 10));
                setComboBoxSafely(Airway, getValueSafely(tbAnestesi1, selectedRow, 11));
                Tensi.setText(getValueSafely(tbAnestesi1, selectedRow, 12));
                Nadi.setText(getValueSafely(tbAnestesi1, selectedRow, 13));
                Support1.setText(getValueSafely(tbAnestesi1, selectedRow, 14));
                Support2.setText(getValueSafely(tbAnestesi1, selectedRow, 15));
                setComboBoxSafely(cmbRespirasi, getValueSafely(tbAnestesi1, selectedRow, 16));
                Rr.setText(getValueSafely(tbAnestesi1, selectedRow, 17));
                setComboBoxSafely(Kanul, getValueSafely(tbAnestesi1, selectedRow, 18));
                O2.setText(getValueSafely(tbAnestesi1, selectedRow, 19));
                Spo2.setText(getValueSafely(tbAnestesi1, selectedRow, 20));
                Bb.setText(getValueSafely(tbAnestesi1, selectedRow, 21));
                Hmt.setText(getValueSafely(tbAnestesi1, selectedRow, 22)); // Puasa Jam

                // Pemeriksaan penunjang
                Lab.setText(getValueSafely(tbAnestesi1, selectedRow, 23));
                Ekg.setText(getValueSafely(tbAnestesi1, selectedRow, 24));
                Thorax.setText(getValueSafely(tbAnestesi1, selectedRow, 25));
                TFP.setText(getValueSafely(tbAnestesi1, selectedRow, 26));
                PxLain.setText(getValueSafely(tbAnestesi1, selectedRow, 27));

                // Sistem evaluasi
                Saraf.setText(getValueSafely(tbAnestesi1, selectedRow, 28));
                Respirasi.setText(getValueSafely(tbAnestesi1, selectedRow, 29));
                Gastro.setText(getValueSafely(tbAnestesi1, selectedRow, 30));
                Urinaria.setText(getValueSafely(tbAnestesi1, selectedRow, 31));
                Muskuloskletal.setText(getValueSafely(tbAnestesi1, selectedRow, 32));
                Metabolik.setText(getValueSafely(tbAnestesi1, selectedRow, 33));
                Lain.setText(getValueSafely(tbAnestesi1, selectedRow, 34));

                // Classification
                setComboBoxSafely(ASA, getValueSafely(tbAnestesi1, selectedRow, 35));
                setComboBoxSafely(Alergi, getValueSafely(tbAnestesi1, selectedRow, 36));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi1, selectedRow, 5));
                String tanggalString = getValueSafely(tbAnestesi1, selectedRow, 5);
                setTimeFromDateString(tanggalString);

                // Premedikasi - Tab 2
            } else if (currentTabIndex == 2 && tbAnestesi2.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi2.getSelectedRow();

                // Data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi2, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi2, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi2, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi2, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi2, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi2, selectedRow, 6));

                // Clear fields yang tidak digunakan
                clearDataAwalFields();
                clearPraInduksiFields();
                clearAnestesiUmumDataAwalFields();
                clearAnestesiUmumMonitoringFields();
                clearAnestesiUmumKeteranganFields();
                clearBlokadeRegionalFields();
                clearKeadaanOperasiFields();
                clearSectioFields();
                clearMasukKamarFields();
                clearMonitoringPemulihanFields();

                // Data petugas
                KdPetugas.setText(getValueSafely(tbAnestesi2, selectedRow, 7));
                NmPetugas.setText(getValueSafely(tbAnestesi2, selectedRow, 8));

                // Data spesifik Premedikasi
                setComboBoxSafely(JenisPremedikasi1, getValueSafely(tbAnestesi2, selectedRow, 9));
                NmObatPremedikasi1.setText(getValueSafely(tbAnestesi2, selectedRow, 10));
                Dosis.setText(getValueSafely(tbAnestesi2, selectedRow, 11));
                setComboBoxSafely(JenisAnestesiPremedikasi, getValueSafely(tbAnestesi2, selectedRow, 12));
                NamaObatAnestesi.setText(getValueSafely(tbAnestesi2, selectedRow, 13));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi2, selectedRow, 5));
                String tanggalString = getValueSafely(tbAnestesi2, selectedRow, 5);
                setTimeFromDateString(tanggalString);

                // Anestesi Umum - Tab 3
            } else if (currentTabIndex == 1 || currentTabIndex == 2) { // Anestesi Umum (1) & Monitoring (2)
                int anestesiUmumTabIndex = AnestesiUmum.getSelectedIndex();

                if (currentTabIndex == 1 && tbAnestesi9.getSelectedRow() != -1) {
                    // Anestesi Umum Data Awal
                    int selectedRow = tbAnestesi9.getSelectedRow();

                    // Data umum pasien
                    TNoRw.setText(getValueSafely(tbAnestesi9, selectedRow, 0));
                    TNoRM.setText(getValueSafely(tbAnestesi9, selectedRow, 1));
                    TPasien.setText(getValueSafely(tbAnestesi9, selectedRow, 2));
                    TglLahir.setText(getValueSafely(tbAnestesi9, selectedRow, 3));
                    Jk.setText(getValueSafely(tbAnestesi9, selectedRow, 4));
                    Ruangan.setText(getValueSafely(tbAnestesi9, selectedRow, 6));

                    // Clear fields yang tidak digunakan
                    clearDataAwalFields();
                    clearPraInduksiFields();
                    clearPremedikasFields();
                    clearAnestesiUmumMonitoringFields();
                    clearAnestesiUmumKeteranganFields();
                    clearBlokadeRegionalFields();
                    clearKeadaanOperasiFields();
                    clearSectioFields();
                    clearMasukKamarFields();
                    clearMonitoringPemulihanFields();

                    // Data petugas
                    KdPetugas.setText(getValueSafely(tbAnestesi9, selectedRow, 7));
                    NmPetugas.setText(getValueSafely(tbAnestesi9, selectedRow, 8));

                    // Data spesifik Anestesi Umum Data Awal
                    setComboBoxSafely(Induksi, getValueSafely(tbAnestesi9, selectedRow, 9));
                    InduksiKet.setText(getValueSafely(tbAnestesi9, selectedRow, 10));
                    setComboBoxSafely(PengaturaNafas, getValueSafely(tbAnestesi9, selectedRow, 11));
                    TidalVolum.setText(getValueSafely(tbAnestesi9, selectedRow, 12));
                    Rr2.setText(getValueSafely(tbAnestesi9, selectedRow, 13));
                    Ieratio.setText(getValueSafely(tbAnestesi9, selectedRow, 14));
                    Peep.setText(getValueSafely(tbAnestesi9, selectedRow, 15));
                    Pip.setText(getValueSafely(tbAnestesi9, selectedRow, 16));
                    Fio2.setText(getValueSafely(tbAnestesi9, selectedRow, 17));
                    setComboBoxSafely(TeknikKhusus, getValueSafely(tbAnestesi9, selectedRow, 18));
                    TeknikKhususKet.setText(getValueSafely(tbAnestesi9, selectedRow, 19));

                    // Set tanggal
                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi9, selectedRow, 5));
                    String tanggalString = getValueSafely(tbAnestesi9, selectedRow, 5);
                    setTimeFromDateString(tanggalString);

                } else if (currentTabIndex == 2 && tbAnestesi10.getSelectedRow() != -1) {
                    // Anestesi Umum Monitoring
                    int selectedRow = tbAnestesi10.getSelectedRow();

                    // Data umum pasien
                    TNoRw.setText(getValueSafely(tbAnestesi10, selectedRow, 0));
                    TNoRM.setText(getValueSafely(tbAnestesi10, selectedRow, 1));
                    TPasien.setText(getValueSafely(tbAnestesi10, selectedRow, 2));
                    TglLahir.setText(getValueSafely(tbAnestesi10, selectedRow, 3));
                    Jk.setText(getValueSafely(tbAnestesi10, selectedRow, 4));
                    Ruangan.setText(getValueSafely(tbAnestesi10, selectedRow, 6));

                    // Clear fields yang tidak digunakan
                    clearDataAwalFields();
                    clearPraInduksiFields();
                    clearPremedikasFields();
                    clearAnestesiUmumDataAwalFields();
                    clearAnestesiUmumKeteranganFields();
                    clearBlokadeRegionalFields();
                    clearKeadaanOperasiFields();
                    clearSectioFields();
                    clearMasukKamarFields();
                    clearMonitoringPemulihanFields();

                    // Data petugas
                    KdPetugas.setText(getValueSafely(tbAnestesi10, selectedRow, 7));
                    NmPetugas.setText(getValueSafely(tbAnestesi10, selectedRow, 8));

                    // Data spesifik Monitoring (sesuai urutan kolom tabel)
                    PernapasanMasuk12.setText(getValueSafely(tbAnestesi10, selectedRow, 9));  // O2
                    PernapasanMasuk14.setText(getValueSafely(tbAnestesi10, selectedRow, 10)); // N2O
                    PernapasanMasuk16.setText(getValueSafely(tbAnestesi10, selectedRow, 11)); // Air
                    Lainnya2.setText(getValueSafely(tbAnestesi10, selectedRow, 12));          // Volatile
                    // Field EKG (kolom 13)
                    SirkulasiMasuk18.setText(getValueSafely(tbAnestesi10, selectedRow, 14));  // Spo2
                    SirkulasiMasuk14.setText(getValueSafely(tbAnestesi10, selectedRow, 15));  // Infus
                    SirkulasiMasuk21.setText(getValueSafely(tbAnestesi10, selectedRow, 16));  // Suhu
                    SirkulasiMasuk22.setText(getValueSafely(tbAnestesi10, selectedRow, 17));  // Respirasi
                    Selama2.setText(getValueSafely(tbAnestesi10, selectedRow, 18));           // Tensi
                    PemantauanNadi2.setText(getValueSafely(tbAnestesi10, selectedRow, 19));   // Nadi

                    // Set tanggal
                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi10, selectedRow, 5));
                    String tanggalString = getValueSafely(tbAnestesi10, selectedRow, 5);
                    setTimeFromDateString(tanggalString);

                } else if (anestesiUmumTabIndex == 2 && tbAnestesi11.getSelectedRow() != -1) {
                    // Anestesi Umum Keterangan
                    int selectedRow = tbAnestesi11.getSelectedRow();

                    // Data umum pasien
                    TNoRw.setText(getValueSafely(tbAnestesi11, selectedRow, 0));
                    TNoRM.setText(getValueSafely(tbAnestesi11, selectedRow, 1));
                    TPasien.setText(getValueSafely(tbAnestesi11, selectedRow, 2));
                    TglLahir.setText(getValueSafely(tbAnestesi11, selectedRow, 3));
                    Jk.setText(getValueSafely(tbAnestesi11, selectedRow, 4));
                    Ruangan.setText(getValueSafely(tbAnestesi11, selectedRow, 6));

                    // Clear fields yang tidak digunakan
                    clearDataAwalFields();
                    clearPraInduksiFields();
                    clearPremedikasFields();
                    clearAnestesiUmumDataAwalFields();
                    clearAnestesiUmumMonitoringFields();
                    clearBlokadeRegionalFields();
                    clearKeadaanOperasiFields();
                    clearSectioFields();
                    clearMasukKamarFields();
                    clearMonitoringPemulihanFields();

                    // Data petugas
                    KdPetugas.setText(getValueSafely(tbAnestesi11, selectedRow, 7));
                    NmPetugas.setText(getValueSafely(tbAnestesi11, selectedRow, 8));

                    // Data spesifik Keterangan
                    Medikasi.setText(getValueSafely(tbAnestesi11, selectedRow, 9));
                    PemberianCairan.setText(getValueSafely(tbAnestesi11, selectedRow, 10));
                    MasalahDuranteOperasi.setText(getValueSafely(tbAnestesi11, selectedRow, 11));
                    Tindakan.setText(getValueSafely(tbAnestesi11, selectedRow, 12));

                    // Set tanggal
                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi11, selectedRow, 5));
                    String tanggalString = getValueSafely(tbAnestesi11, selectedRow, 5);
                    setTimeFromDateString(tanggalString);
                }

                // Blokade Regional - Tab 4
            } else if (currentTabIndex == 3 && tbAnestesi12.getSelectedRow() != -1) { // Blokade Regional
                int selectedRow = tbAnestesi12.getSelectedRow();

                // Data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi12, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi12, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi12, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi12, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi12, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi12, selectedRow, 6));

                // Clear fields yang tidak digunakan
                clearDataAwalFields();
                clearPraInduksiFields();
                clearPremedikasFields();
                clearAnestesiUmumDataAwalFields();
                clearAnestesiUmumMonitoringFields();
                clearAnestesiUmumKeteranganFields();
                clearKeadaanOperasiFields();
                clearSectioFields();
                clearMasukKamarFields();
                clearMonitoringPemulihanFields();

                // Data petugas
                KdPetugas.setText(getValueSafely(tbAnestesi12, selectedRow, 7));
                NmPetugas.setText(getValueSafely(tbAnestesi12, selectedRow, 8));

                // Data spesifik Blokade Regional
                setComboBoxSafely(Teknik, getValueSafely(tbAnestesi12, selectedRow, 9));
                LokasiTusukan.setText(getValueSafely(tbAnestesi12, selectedRow, 10));
                AnalgesiSetinggiSegmen.setText(getValueSafely(tbAnestesi12, selectedRow, 11));
                AnestesiLokal.setText(getValueSafely(tbAnestesi12, selectedRow, 12));
                KonsentrasiJumlah.setText(getValueSafely(tbAnestesi12, selectedRow, 13));
                ObatTambahan.setText(getValueSafely(tbAnestesi12, selectedRow, 14));
                Dosis.setText(getValueSafely(tbAnestesi12, selectedRow, 15));
                setComboBoxSafely(Vasokonstriktor, getValueSafely(tbAnestesi12, selectedRow, 16));
                Konsentrasi.setText(getValueSafely(tbAnestesi12, selectedRow, 17));
                WaktuMulaiSuntikJam.setText(getValueSafely(tbAnestesi12, selectedRow, 18));
                AnalgesiJam.setText(getValueSafely(tbAnestesi12, selectedRow, 19));
                Lamanya.setText(getValueSafely(tbAnestesi12, selectedRow, 20));
                LamanyaMenitAnalgesi.setText(getValueSafely(tbAnestesi12, selectedRow, 21));
                OperasiJam.setText(getValueSafely(tbAnestesi12, selectedRow, 22));
                LamanyajamOperasi.setText(getValueSafely(tbAnestesi12, selectedRow, 23));
                LamanyaJamMenit.setText(getValueSafely(tbAnestesi12, selectedRow, 24));
                TindakanAnestesiTambahan.setText(getValueSafely(tbAnestesi12, selectedRow, 25));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi12, selectedRow, 5));
                String tanggalString = getValueSafely(tbAnestesi12, selectedRow, 5);
                setTimeFromDateString(tanggalString);

                // Keadaan Selama Operasi - Tab 5
            } else if (currentTabIndex == 4 && tbAnestesi3.getSelectedRow() != -1) { // Keadaan Selama Operasi
                int selectedRow = tbAnestesi3.getSelectedRow();

                // Data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi3, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi3, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi3, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi3, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi3, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi3, selectedRow, 6));

                // Clear fields yang tidak digunakan
                clearDataAwalFields();
                clearPraInduksiFields();
                clearPremedikasFields();
                clearAnestesiUmumDataAwalFields();
                clearAnestesiUmumMonitoringFields();
                clearAnestesiUmumKeteranganFields();
                clearBlokadeRegionalFields();
                clearSectioFields();
                clearMasukKamarFields();
                clearMonitoringPemulihanFields();

                // Data petugas
                KdPetugas.setText(getValueSafely(tbAnestesi3, selectedRow, 7));
                NmPetugas.setText(getValueSafely(tbAnestesi3, selectedRow, 8));

                // Data spesifik Keadaan Operasi
                setComboBoxSafely(LetakPenderita, getValueSafely(tbAnestesi3, selectedRow, 9));
                setComboBoxSafely(Airway2, getValueSafely(tbAnestesi3, selectedRow, 10));
                Ukuran.setText(getValueSafely(tbAnestesi3, selectedRow, 11));
                setComboBoxSafely(BalonNoBalon, getValueSafely(tbAnestesi3, selectedRow, 12));
                LamaOperasi.setText(getValueSafely(tbAnestesi3, selectedRow, 13));
                LamaAnestesi.setText(getValueSafely(tbAnestesi3, selectedRow, 14));
                Kristaloid.setText(getValueSafely(tbAnestesi3, selectedRow, 15));
                Perdarahan.setText(getValueSafely(tbAnestesi3, selectedRow, 16));
                Koloid.setText(getValueSafely(tbAnestesi3, selectedRow, 17));
                Diuresis.setText(getValueSafely(tbAnestesi3, selectedRow, 18));
                Darah.setText(getValueSafely(tbAnestesi3, selectedRow, 19));
                CairanLain.setText(getValueSafely(tbAnestesi3, selectedRow, 20));
                KomponenDarah.setText(getValueSafely(tbAnestesi3, selectedRow, 21));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi3, selectedRow, 5));
                String tanggalString = getValueSafely(tbAnestesi3, selectedRow, 5);
                setTimeFromDateString(tanggalString);

                // Sectio Caesaria - Tab 6
            } else if (currentTabIndex == 5 && tbAnestesi4.getSelectedRow() != -1) { // Sectio Caesaria
                int selectedRow = tbAnestesi4.getSelectedRow();

                // Data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi4, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi4, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi4, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi4, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi4, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi4, selectedRow, 6));

                // Clear fields yang tidak digunakan
                clearDataAwalFields();
                clearPraInduksiFields();
                clearPremedikasFields();
                clearAnestesiUmumDataAwalFields();
                clearAnestesiUmumMonitoringFields();
                clearAnestesiUmumKeteranganFields();
                clearBlokadeRegionalFields();
                clearKeadaanOperasiFields();
                clearMasukKamarFields();
                clearMonitoringPemulihanFields();

                // Data petugas
                KdPetugas.setText(getValueSafely(tbAnestesi4, selectedRow, 7));
                NmPetugas.setText(getValueSafely(tbAnestesi4, selectedRow, 8));

                // Data spesifik Section Caesaria
                setComboBoxSafely(KeadaanBayi, getValueSafely(tbAnestesi4, selectedRow, 9));
                setComboBoxSafely(JenisKelamin, getValueSafely(tbAnestesi4, selectedRow, 10));
                Apgrar1Menit.setText(getValueSafely(tbAnestesi4, selectedRow, 11));
                Apgar5Menit.setText(getValueSafely(tbAnestesi4, selectedRow, 12));
                Apgar10Menit.setText(getValueSafely(tbAnestesi4, selectedRow, 13));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi4, selectedRow, 5));
                String tanggalString = getValueSafely(tbAnestesi4, selectedRow, 5);
                setTimeFromDateString(tanggalString);

                // Keadaan Pasca Anestesi di Ruang Pemulihan - Tab 7
            } else if (currentTabIndex == 6) { // Keadaan Pasca Anestesi
                int catatanTabIndex = CatatanKamarPemulihan.getSelectedIndex();

                if (catatanTabIndex == 0 && tbAnestesi5.getSelectedRow() != -1) {
                    // Masuk Kamar Pemulihan
                    int selectedRow = tbAnestesi5.getSelectedRow();

                    // Data umum pasien
                    TNoRw.setText(getValueSafely(tbAnestesi5, selectedRow, 0));
                    TNoRM.setText(getValueSafely(tbAnestesi5, selectedRow, 1));
                    TPasien.setText(getValueSafely(tbAnestesi5, selectedRow, 2));
                    TglLahir.setText(getValueSafely(tbAnestesi5, selectedRow, 3));
                    Jk.setText(getValueSafely(tbAnestesi5, selectedRow, 4));
                    Ruangan.setText(getValueSafely(tbAnestesi5, selectedRow, 6));

                    // Clear fields yang tidak digunakan
                    clearDataAwalFields();
                    clearPraInduksiFields();
                    clearPremedikasFields();
                    clearAnestesiUmumDataAwalFields();
                    clearAnestesiUmumMonitoringFields();
                    clearAnestesiUmumKeteranganFields();
                    clearBlokadeRegionalFields();
                    clearKeadaanOperasiFields();
                    clearSectioFields();
                    clearMonitoringPemulihanFields();

                    // Data petugas
                    KdPetugas.setText(getValueSafely(tbAnestesi5, selectedRow, 7));
                    NmPetugas.setText(getValueSafely(tbAnestesi5, selectedRow, 8));

                    // Data spesifik Masuk Kamar Pemulihan
                    String jamMasuk = getValueSafely(tbAnestesi5, selectedRow, 9);
                    if (!jamMasuk.isEmpty()) {
                        try {
                            Valid.SetTgl2(MasukKamarPulih, jamMasuk);
                        } catch (Exception e) {
                            System.out.println("Error setting jam masuk: " + e.getMessage());
                        }
                    }

                    setComboBoxSafely(Kesadaran, getValueSafely(tbAnestesi5, selectedRow, 10));
                    TensiMasukKamar.setText(getValueSafely(tbAnestesi5, selectedRow, 11));
                    NadiMasukKamar.setText(getValueSafely(tbAnestesi5, selectedRow, 12));
                    RrMasukKamar.setText(getValueSafely(tbAnestesi5, selectedRow, 13));
                    Support3.setText(getValueSafely(tbAnestesi5, selectedRow, 14));
                    Support4.setText(getValueSafely(tbAnestesi5, selectedRow, 15));
                    setComboBoxSafely(cmbRespirasiMasukKamar, getValueSafely(tbAnestesi5, selectedRow, 16));
                    setComboBoxSafely(KanulMasukKamar, getValueSafely(tbAnestesi5, selectedRow, 17));
                    O2MasukKamar.setText(getValueSafely(tbAnestesi5, selectedRow, 18));
                    Spo2MasukKamar.setText(getValueSafely(tbAnestesi5, selectedRow, 19));
                    SkorNyeri.setText(getValueSafely(tbAnestesi5, selectedRow, 20));
                    PenyulitIntraOperatif.setText(getValueSafely(tbAnestesi5, selectedRow, 21));

                    // Set tanggal
                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi5, selectedRow, 5));
                    String tanggalString = getValueSafely(tbAnestesi5, selectedRow, 5);
                    setTimeFromDateString(tanggalString);

                } else if (catatanTabIndex == 1 && tbAnestesi6.getSelectedRow() != -1) {
                    // Monitoring Pemulihan
                    int selectedRow = tbAnestesi6.getSelectedRow();

                    // Data umum pasien
                    TNoRw.setText(getValueSafely(tbAnestesi6, selectedRow, 0));
                    TNoRM.setText(getValueSafely(tbAnestesi6, selectedRow, 1));
                    TPasien.setText(getValueSafely(tbAnestesi6, selectedRow, 2));
                    TglLahir.setText(getValueSafely(tbAnestesi6, selectedRow, 3));
                    Jk.setText(getValueSafely(tbAnestesi6, selectedRow, 4));
                    Ruangan.setText(getValueSafely(tbAnestesi6, selectedRow, 6));

                    // Clear fields yang tidak digunakan
                    clearDataAwalFields();
                    clearPraInduksiFields();
                    clearPremedikasFields();
                    clearAnestesiUmumDataAwalFields();
                    clearAnestesiUmumMonitoringFields();
                    clearAnestesiUmumKeteranganFields();
                    clearBlokadeRegionalFields();
                    clearKeadaanOperasiFields();
                    clearSectioFields();
                    clearMasukKamarFields();

                    // Data petugas
                    KdPetugas.setText(getValueSafely(tbAnestesi6, selectedRow, 7));
                    NmPetugas.setText(getValueSafely(tbAnestesi6, selectedRow, 8));

                    // Data spesifik Monitoring Pemulihan
                    Suhu.setText(getValueSafely(tbAnestesi6, selectedRow, 9));
                    Rr4.setText(getValueSafely(tbAnestesi6, selectedRow, 10));
                    Nadi2.setText(getValueSafely(tbAnestesi6, selectedRow, 11));
                    TekananDarah.setText(getValueSafely(tbAnestesi6, selectedRow, 12));

                    // Set tanggal
                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi6, selectedRow, 5));
                    String tanggalString = getValueSafely(tbAnestesi6, selectedRow, 5);
                    setTimeFromDateString(tanggalString);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in getData(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearDataAwalFields() {
        // Clear fields yang hanya ada di Data Awal
        KdDokterAnastesi.setText("");
        NmDokterAnestesi.setText("");
        KdAsistenAnestesi.setText("");
        NmAsistenAnestasi.setText("");
        KdDokterBedah.setText("");
        NmDokterBedah.setText("");
        KdAsistenBedah.setText("");
        NmAsistenBeda.setText("");
        MedikasiPrabedah.setText("");
        JenisAnestesi.setText("");
        DiagnosaPrabedah.setText("");

        if (InformedConsent.getItemCount() > 0) {
            InformedConsent.setSelectedIndex(0);
        }
        if (ObatAnestesi.getItemCount() > 0) {
            ObatAnestesi.setSelectedIndex(0);
        }
        if (TatalaksanaJalanNafas.getItemCount() > 0) {
            TatalaksanaJalanNafas.setSelectedIndex(0);
        }
        if (MesinAnestesi.getItemCount() > 0) {
            MesinAnestesi.setSelectedIndex(0);
        }
        if (ObatEmergensi.getItemCount() > 0) {
            ObatEmergensi.setSelectedIndex(0);
        }
        if (CheckMonitoring.getItemCount() > 0) {
            CheckMonitoring.setSelectedIndex(0);
        }
        if (SuctionAparatus.getItemCount() > 0) {
            SuctionAparatus.setSelectedIndex(0);
        }
    }

    private void clearPraInduksiFields() {
        // Clear fields yang hanya ada di Pra Induksi
        txtKesadaran.setText("");
        GCS.setText("");
        if (Airway.getItemCount() > 0) {
            Airway.setSelectedIndex(0);
        }
        Tensi.setText("");
        Nadi.setText("");
        Support1.setText("");
        Support2.setText("");
        if (cmbRespirasi.getItemCount() > 0) {
            cmbRespirasi.setSelectedIndex(0);
        }
        Rr.setText("");
        O2.setText("");
        Spo2.setText("");
        Bb.setText("");
        Hmt.setText("");

        // Pemeriksaan penunjang
        Lab.setText("");
        Ekg.setText("");
        Thorax.setText("");
        TFP.setText("");
        PxLain.setText("");

        // Sistem evaluasi
        Saraf.setText("");
        Respirasi.setText("");
        Gastro.setText("");
        Urinaria.setText("");
        Muskuloskletal.setText("");
        Metabolik.setText("");
        Lain.setText("");

        if (ASA.getItemCount() > 0) {
            ASA.setSelectedIndex(0);
        }
        if (Alergi.getItemCount() > 0) {
            Alergi.setSelectedIndex(0);
        }
    }

    private void clearPremedikasFields() {
        // Clear fields yang hanya ada di Premedikasi
        if (JenisPremedikasi1.getItemCount() > 0) {
            JenisPremedikasi1.setSelectedIndex(0);
        }
        NmObatPremedikasi1.setText("");
        Dosis.setText("");
        if (JenisAnestesiPremedikasi.getItemCount() > 0) {
            JenisAnestesiPremedikasi.setSelectedIndex(0);
        }
        NamaObatAnestesi.setText("");
    }

    private void clearAnestesiUmumDataAwalFields() {
        // Clear fields yang hanya ada di Anestesi Umum Data Awal
        if (Induksi.getItemCount() > 0) {
            Induksi.setSelectedIndex(0);
        }
        InduksiKet.setText("");
        if (PengaturaNafas.getItemCount() > 0) {
            PengaturaNafas.setSelectedIndex(0);
        }
        TidalVolum.setText("");
        Rr2.setText("");
        Ieratio.setText("");
        Peep.setText("");
        Pip.setText("");
        Fio2.setText("");
        if (TeknikKhusus.getItemCount() > 0) {
            TeknikKhusus.setSelectedIndex(0);
        }
        TeknikKhususKet.setText("");
    }

    private void clearAnestesiUmumMonitoringFields() {
        // Clear fields yang hanya ada di Anestesi Umum Monitoring
        PernapasanMasuk12.setText(""); // O2
        PernapasanMasuk14.setText(""); // N2O
        PernapasanMasuk16.setText(""); // Air
        Lainnya2.setText("");          // Volatile
        SirkulasiMasuk19.setText("");  // Sevofluran (jika ada)
        SirkulasiMasuk14.setText("");  // Infus
        SirkulasiMasuk21.setText("");  // Suhu
        SirkulasiMasuk22.setText("");  // Respirasi
        Selama2.setText("");           // Tensi
        PemantauanNadi2.setText("");   // Nadi
        SirkulasiMasuk18.setText("");  // SpO2
    }

    private void clearAnestesiUmumKeteranganFields() {
        // Clear fields yang hanya ada di Anestesi Umum Keterangan
        Medikasi.setText("");
        PemberianCairan.setText("");
        MasalahDuranteOperasi.setText("");
        Tindakan.setText("");
    }

    private void clearBlokadeRegionalFields() {
        // Clear fields yang hanya ada di Blokade Regional
        if (Teknik.getItemCount() > 0) {
            Teknik.setSelectedIndex(0);
        }
        LokasiTusukan.setText("");
        AnalgesiSetinggiSegmen.setText("");
        AnestesiLokal.setText("");
        KonsentrasiJumlah.setText("");
        ObatTambahan.setText("");
        if (Vasokonstriktor.getItemCount() > 0) {
            Vasokonstriktor.setSelectedIndex(0);
        }
        Konsentrasi.setText("");
        WaktuMulaiSuntikJam.setText("");
        AnalgesiJam.setText("");
        Lamanya.setText("");
        LamanyaMenitAnalgesi.setText("");
        OperasiJam.setText("");
        LamanyajamOperasi.setText("");
        LamanyaJamMenit.setText("");
        TindakanAnestesiTambahan.setText("");
    }

    private void clearKeadaanOperasiFields() {
        // Clear fields yang hanya ada di Keadaan Operasi
        if (LetakPenderita.getItemCount() > 0) {
            LetakPenderita.setSelectedIndex(0);
        }
        if (Airway2.getItemCount() > 0) {
            Airway2.setSelectedIndex(0);
        }
        Ukuran.setText("");
        if (BalonNoBalon.getItemCount() > 0) {
            BalonNoBalon.setSelectedIndex(0);
        }
        LamaOperasi.setText("");
        LamaAnestesi.setText("");
        Kristaloid.setText("");
        Perdarahan.setText("");
        Koloid.setText("");
        Diuresis.setText("");
        Darah.setText("");
        CairanLain.setText("");
        KomponenDarah.setText("");
    }

    private void clearSectioFields() {
        // Clear fields yang hanya ada di Sectio Caesaria
        if (KeadaanBayi.getItemCount() > 0) {
            KeadaanBayi.setSelectedIndex(0);
        }
        if (JenisKelamin.getItemCount() > 0) {
            JenisKelamin.setSelectedIndex(0);
        }
        Apgrar1Menit.setText("");
        Apgar5Menit.setText("");
        Apgar10Menit.setText("");
    }

    private void clearMasukKamarFields() {
        // Clear fields yang hanya ada di Masuk Kamar Pemulihan
        try {
            MasukKamarPulih.setSelectedItem(new Date());
        } catch (Exception e) {
            // Ignore
        }
        if (Kesadaran.getItemCount() > 0) {
            Kesadaran.setSelectedIndex(0);
        }
        TensiMasukKamar.setText("");
        NadiMasukKamar.setText("");
        RrMasukKamar.setText("");
        Support3.setText("");
        Support4.setText("");
        if (cmbRespirasiMasukKamar.getItemCount() > 0) {
            cmbRespirasiMasukKamar.setSelectedIndex(0);
        }
        if (KanulMasukKamar.getItemCount() > 0) {
            KanulMasukKamar.setSelectedIndex(0);
        }
        if (Kanul.getItemCount() > 0) {
            Kanul.setSelectedIndex(0);
        }
        O2MasukKamar.setText("");
        Spo2MasukKamar.setText("");
        SkorNyeri.setText("");
        PenyulitIntraOperatif.setText("");
    }

    private void clearMonitoringPemulihanFields() {
        // Clear fields yang hanya ada di Monitoring Pemulihan
        Suhu.setText("");
        Rr4.setText("");
        Nadi2.setText("");
        TekananDarah.setText("");
    }

    private void setCommonPatientData(JTable table, int selectedRow) {
        // Data umum pasien
        TNoRw.setText(getValueSafely(table, selectedRow, 0));
        TNoRM.setText(getValueSafely(table, selectedRow, 1));
        TPasien.setText(getValueSafely(table, selectedRow, 2));
        TglLahir.setText(getValueSafely(table, selectedRow, 3));
        Jk.setText(getValueSafely(table, selectedRow, 4));
        Ruangan.setText(getValueSafely(table, selectedRow, 6));

        // Data petugas
        KdPetugas.setText(getValueSafely(table, selectedRow, 7));
        NmPetugas.setText(getValueSafely(table, selectedRow, 8));

        // Set tanggal
        Valid.SetTgl2(DTPTgl, getValueSafely(table, selectedRow, 5));

        // Set waktu dari tanggal
        String tanggalString = getValueSafely(table, selectedRow, 5);
        setTimeFromDateString(tanggalString);
    }

    private void setTimeFromDateString(String tanggalString) {
        if (!tanggalString.isEmpty() && tanggalString.length() >= 19) {
            try {
                String[] dateTimeParts = tanggalString.split(" ");
                if (dateTimeParts.length >= 2) {
                    String[] timeParts = dateTimeParts[1].split(":");
                    if (timeParts.length >= 3) {
                        cmbJam.setSelectedItem(timeParts[0]);
                        cmbMnt.setSelectedItem(timeParts[1]);
                        cmbDtk.setSelectedItem(timeParts[2]);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error parsing time: " + e.getMessage());
            }
        }
    }

// Helper method untuk set data pasien umum
    private void setCommonPatientData(widget.Table table, int selectedRow) {
        TNoRw.setText(getValueSafely(table, selectedRow, 0));
        TNoRM.setText(getValueSafely(table, selectedRow, 1));
        TPasien.setText(getValueSafely(table, selectedRow, 2));
        TglLahir.setText(getValueSafely(table, selectedRow, 3));
        Jk.setText(getValueSafely(table, selectedRow, 4));
        Ruangan.setText(getValueSafely(table, selectedRow, 6));
    }

// Helper method untuk mengambil nilai dengan aman
    private String getValueSafely(JTable table, int row, int column) {
        try {
            Object value = table.getValueAt(row, column);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

// Helper method untuk set ComboBox dengan aman
    private void setComboBoxSafely(javax.swing.JComboBox comboBox, String value) {
        try {
            if (value != null && !value.trim().isEmpty()) {
                for (int i = 0; i < comboBox.getItemCount(); i++) {
                    if (comboBox.getItemAt(i).toString().equals(value)) {
                        comboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Jika error, biarkan pada index default
            if (comboBox.getItemCount() > 0) {
                comboBox.setSelectedIndex(0);
            }
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "SELECT rp.no_rkm_medis, p.nm_pasien, IF(p.jk='L','Laki-Laki','Perempuan') as jk, "
                    + "p.tgl_lahir, rp.tgl_registrasi, b.nm_bangsal "
                    + "FROM reg_periksa rp "
                    + "INNER JOIN pasien p ON rp.no_rkm_medis = p.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal b ON k.kd_bangsal = b.kd_bangsal "
                    + "WHERE rp.no_rawat = ?"
            );
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    TglMasukRS.setText(rs.getString("tgl_registrasi"));
                    Ruangan.setText(rs.getString("nm_bangsal") != null ? rs.getString("nm_bangsal") : "-");
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    // ── Blokade Regional : sub-tab Monitoring (TTV) → catatan_anestesi_monitoring ──
    private void buildRegionalMonitoring() {
        try {
            tabModeRegMon = new DefaultTableModel(null, new Object[]{"Jam", "TD", "Nadi", "RR", "SpO2", "Perdarahan", "Obat/Infus"}) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            tbRegMon = new javax.swing.JTable(tabModeRegMon);
            javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(2, 2));
            javax.swing.JPanel in = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));
            rmTD = new javax.swing.JTextField(6); rmNadi = new javax.swing.JTextField(4); rmRR = new javax.swing.JTextField(4);
            rmSpo2 = new javax.swing.JTextField(4); rmPerdarahan = new javax.swing.JTextField(8); rmObat = new javax.swing.JTextField(12);
            javax.swing.JButton bAdd = new javax.swing.JButton("Tambah");
            javax.swing.JButton bDel = new javax.swing.JButton("Hapus");
            bAdd.addActionListener(e -> simpanRegMon());
            bDel.addActionListener(e -> hapusRegMon());
            in.add(new javax.swing.JLabel("TD")); in.add(rmTD);
            in.add(new javax.swing.JLabel("Nadi")); in.add(rmNadi);
            in.add(new javax.swing.JLabel("RR")); in.add(rmRR);
            in.add(new javax.swing.JLabel("SpO2")); in.add(rmSpo2);
            in.add(new javax.swing.JLabel("Perdarahan")); in.add(rmPerdarahan);
            in.add(new javax.swing.JLabel("Obat/Infus")); in.add(rmObat);
            in.add(bAdd); in.add(bDel);
            panel.add(in, java.awt.BorderLayout.PAGE_START);
            panel.add(new javax.swing.JScrollPane(tbRegMon), java.awt.BorderLayout.CENTER);
            // BlokadeRegional.addTab("Monitoring", panel); // dihapus (samakan web)
            // BlokadeRegional.addChangeListener(e -> { if (BlokadeRegional.getSelectedIndex() == 1) tampilRegMon(); });
        } catch (Throwable ex) { System.out.println("Notif buildRegionalMonitoring : " + ex); }
    }

    // ================= Sub-tab "Score" (Keadaan Pasca Anestesi di Ruang Pemulihan) =================
    // Konsolidasi: Bromage / Modified Aldrete / Stewart + Tanggal&Jam Keluar Ruang Pemulihan + Pindah ke.
    // Skor ditulis ke tabel lama (skor_bromage/aldrette/steward_pasca_anestesi); keluar+pindah ke
    // tabel catatan_anestesi_keluar_pemulihan (anchor). Dibangun programatik + tombol mandiri agar
    // tidak terpengaruh pergeseran indeks dispatch tombol global.
    private void buildScorePemulihan() {
        try {
            tabModeScore = new DefaultTableModel(null, new Object[]{
                "No.Rawat", "No.RM", "Nama Pasien", "Tanggal", "Bromage", "Modified Aldrete",
                "Stewart", "Jam Keluar", "Pindah Ke", "Petugas"
            }) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            tbScore = new javax.swing.JTable(tabModeScore);
            tbScore.setAutoCreateRowSorter(true);
            tbScore.setToolTipText("Klik baris untuk mengubah/menghapus data Score");
            tbScore.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int r = tbScore.getSelectedRow();
                    if (r != -1) isiFieldScore(tbScore.convertRowIndexToModel(r));
                }
            });

            scoreBromage  = new javax.swing.JTextField(4);
            scoreAldrete  = new javax.swing.JTextField(4);
            scoreStewart  = new javax.swing.JTextField(4);
            scoreTglKeluar = new javax.swing.JTextField(16);
            scorePindah = new javax.swing.JComboBox<>(new String[]{"", "Ruang Rawat", "ICU", "HCU", "NICU", "PICU"});

            javax.swing.JButton bSimpan = new javax.swing.JButton("Simpan");
            javax.swing.JButton bUbah   = new javax.swing.JButton("Ubah");
            javax.swing.JButton bHapus  = new javax.swing.JButton("Hapus");
            javax.swing.JButton bBaru   = new javax.swing.JButton("Baru");
            bSimpan.addActionListener(e -> simpanScore());
            bUbah.addActionListener(e -> ubahScore());
            bHapus.addActionListener(e -> hapusScore());
            bBaru.addActionListener(e -> emptScore());

            javax.swing.JPanel in = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 4));
            in.add(new javax.swing.JLabel("Bromage Score:"));          in.add(scoreBromage);
            in.add(new javax.swing.JLabel("Modified Aldrete Score:")); in.add(scoreAldrete);
            in.add(new javax.swing.JLabel("Stewart Score:"));          in.add(scoreStewart);
            in.add(new javax.swing.JLabel("Jam Keluar Pemulihan:")); in.add(scoreTglKeluar);
            in.add(new javax.swing.JLabel("Pindah ke:"));              in.add(scorePindah);
            in.add(bSimpan); in.add(bUbah); in.add(bHapus); in.add(bBaru);
            emptScore();

            javax.swing.JPanel p = new javax.swing.JPanel(new java.awt.BorderLayout(2, 2));
            p.add(in, java.awt.BorderLayout.PAGE_START);
            p.add(new javax.swing.JScrollPane(tbScore), java.awt.BorderLayout.CENTER);

            CatatanKamarPemulihan.addTab("Score", p);
            // Refresh daftar saat sub-tab Score dibuka (dispatch tampil() global tak menjangkau indeks ini).
            CatatanKamarPemulihan.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    int i = CatatanKamarPemulihan.getSelectedIndex();
                    if (i != -1 && "Score".equals(CatatanKamarPemulihan.getTitleAt(i))) tampilScore();
                }
            });
        } catch (Throwable ex) { System.out.println("Notif buildScorePemulihan : " + ex); }
    }

    private void emptScore() {
        if (scoreBromage == null) return;
        scoreBromage.setText(""); scoreAldrete.setText(""); scoreStewart.setText("");
        scoreTglKeluar.setText(new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
        scorePindah.setSelectedIndex(0);
        if (tbScore != null) tbScore.clearSelection();
    }

    private void isiFieldScore(int r) {
        try {
            TNoRw.setText(nvScore(r, 0));
            TNoRM.setText(nvScore(r, 1));
            scoreBromage.setText(nvScore(r, 4));
            scoreAldrete.setText(nvScore(r, 5));
            scoreStewart.setText(nvScore(r, 6));
            scoreTglKeluar.setText(nvScore(r, 7));
            scorePindah.setSelectedItem(nvScore(r, 8));
        } catch (Exception e) { System.out.println("Notif isiFieldScore : " + e); }
    }

    private String nvScore(int r, int c) {
        Object v = tabModeScore.getValueAt(r, c);
        return v == null ? "" : v.toString();
    }

    private void simpanScore() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "No.Rawat"); return; }
        String tgl = Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " "
                + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem();
        if (doSaveScore(TNoRw.getText(), tgl)) { tampilScore(); emptScore(); }
    }

    private void ubahScore() {
        int r = tbScore.getSelectedRow();
        if (r == -1) { javax.swing.JOptionPane.showMessageDialog(this, "Pilih dulu baris Score yang mau diubah."); return; }
        r = tbScore.convertRowIndexToModel(r);
        String noRawat = nvScore(r, 0);
        String tgl = nvScore(r, 3);
        if (doSaveScore(noRawat, tgl)) { tampilScore(); emptScore(); }
    }

    private void hapusScore() {
        int r = tbScore.getSelectedRow();
        if (r == -1) { javax.swing.JOptionPane.showMessageDialog(this, "Pilih dulu baris Score yang mau dihapus."); return; }
        r = tbScore.convertRowIndexToModel(r);
        String noRawat = nvScore(r, 0);
        String tgl = nvScore(r, 3);
        Sequel.queryu2tf("delete from skor_bromage_pasca_anestesi where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        Sequel.queryu2tf("delete from skor_aldrette_pasca_anestesi where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        Sequel.queryu2tf("delete from skor_steward_pasca_anestesi where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        Sequel.queryu2tf("delete from catatan_anestesi_keluar_kamar where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        tampilScore(); emptScore();
    }

    // Upsert (hapus+insert) ke-4 tabel pada key (no_rawat, tanggal). Dipakai Simpan (tgl header) & Ubah (tgl baris).
    private boolean doSaveScore(String noRawat, String tgl) {
        String kddok = KdDokterAnastesi.getText().trim();
        String nip   = KdPetugas.getText().trim();
        String br = scoreBromage.getText().trim();
        String al = scoreAldrete.getText().trim();
        String st = scoreStewart.getText().trim();
        String jam = scoreTglKeluar.getText().trim();
        if (jam.contains(" ")) jam = jam.substring(jam.indexOf(' ') + 1); // ambil bagian jam bila diketik datetime penuh
        String pindah = scorePindah.getSelectedItem() == null ? "" : scorePindah.getSelectedItem().toString();

        boolean adaSkor = !br.isEmpty() || !al.isEmpty() || !st.isEmpty();
        if (adaSkor && (kddok.isEmpty() || nip.isEmpty())) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Lengkapi Dokter Anestesi (tab Data Awal) dan Petugas dulu sebelum mengisi nilai skor.");
            return false;
        }
        if ((!br.isEmpty() && !br.matches("\\d+")) || (!al.isEmpty() && !al.matches("\\d+")) || (!st.isEmpty() && !st.matches("\\d+"))) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nilai skor harus berupa angka.");
            return false;
        }

        Sequel.queryu2tf("delete from skor_bromage_pasca_anestesi where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        if (!br.isEmpty())
            Sequel.queryu2tf("insert into skor_bromage_pasca_anestesi (no_rawat,tanggal,penilaian_nilai1,kd_dokter,nip) values (?,?,?,?,?)",
                    5, new String[]{noRawat, tgl, br, kddok, nip});

        Sequel.queryu2tf("delete from skor_aldrette_pasca_anestesi where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        if (!al.isEmpty())
            Sequel.queryu2tf("insert into skor_aldrette_pasca_anestesi (no_rawat,tanggal,penilaian_totalnilai,kd_dokter,nip) values (?,?,?,?,?)",
                    5, new String[]{noRawat, tgl, al, kddok, nip});

        Sequel.queryu2tf("delete from skor_steward_pasca_anestesi where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
        if (!st.isEmpty())
            Sequel.queryu2tf("insert into skor_steward_pasca_anestesi (no_rawat,tanggal,penilaian_totalnilai,kd_dokter,nip) values (?,?,?,?,?)",
                    5, new String[]{noRawat, tgl, st, kddok, nip});

        // Keluar + Pindah -> catatan_anestesi_keluar_kamar (SAMA dengan web e-Nakes). Upsert
        // hanya kolom jam_keluar/pindah_ke/kd_petugas agar kolom lain (seksi Keluar Kamar
        // intra-op) tidak terhapus. kd_petugas FK ke pegawai(nik) -> hanya diset bila terisi.
        if (!jam.isEmpty() || !pindah.isEmpty()) {
            java.util.List<String> cols = new java.util.ArrayList<>();
            java.util.List<String> vals = new java.util.ArrayList<>();
            java.util.List<String> upd  = new java.util.ArrayList<>();
            cols.add("no_rawat"); vals.add(noRawat);
            cols.add("tanggal");  vals.add(tgl);
            if (!jam.isEmpty())    { cols.add("jam_keluar"); vals.add(jam);    upd.add("jam_keluar=values(jam_keluar)"); }
            if (!pindah.isEmpty()) { cols.add("pindah_ke");  vals.add(pindah); upd.add("pindah_ke=values(pindah_ke)"); }
            if (!nip.isEmpty())    { cols.add("kd_petugas"); vals.add(nip);    upd.add("kd_petugas=values(kd_petugas)"); }
            StringBuilder sql = new StringBuilder("insert into catatan_anestesi_keluar_kamar (");
            StringBuilder ph = new StringBuilder();
            for (int i = 0; i < cols.size(); i++) {
                if (i > 0) { sql.append(","); ph.append(","); }
                sql.append(cols.get(i)); ph.append("?");
            }
            sql.append(") values (").append(ph).append(") on duplicate key update ").append(String.join(",", upd));
            Sequel.queryu2tf(sql.toString(), vals.size(), vals.toArray(new String[0]));
        }
        return true;
    }

    private void tampilScore() {
        if (tabModeScore == null) return;
        Valid.tabelKosong(tabModeScore);
        try {
            // Anchor = gabungan key (no_rawat,tanggal) dari 4 tabel, agar entri skor-saja
            // maupun keluar-saja tetap muncul. Keluar+pindah dibaca dari catatan_anestesi_keluar_kamar
            // (sama dengan web e-Nakes).
            ps = koneksi.prepareStatement(
                    "SELECT k.no_rawat, rp.no_rkm_medis, pas.nm_pasien, k.tanggal, "
                    + "b.penilaian_nilai1 AS bromage, a.penilaian_totalnilai AS aldrette, s.penilaian_totalnilai AS steward, "
                    + "kk.jam_keluar, kk.pindah_ke, pg.nama AS nm_petugas "
                    + "FROM ( "
                    + "  SELECT no_rawat,tanggal FROM skor_bromage_pasca_anestesi WHERE tanggal BETWEEN ? AND ? "
                    + "  UNION SELECT no_rawat,tanggal FROM skor_aldrette_pasca_anestesi WHERE tanggal BETWEEN ? AND ? "
                    + "  UNION SELECT no_rawat,tanggal FROM skor_steward_pasca_anestesi WHERE tanggal BETWEEN ? AND ? "
                    + "  UNION SELECT no_rawat,tanggal FROM catatan_anestesi_keluar_kamar WHERE tanggal BETWEEN ? AND ? "
                    + ") k "
                    + "INNER JOIN reg_periksa rp ON k.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN skor_bromage_pasca_anestesi b ON b.no_rawat = k.no_rawat AND b.tanggal = k.tanggal "
                    + "LEFT JOIN skor_aldrette_pasca_anestesi a ON a.no_rawat = k.no_rawat AND a.tanggal = k.tanggal "
                    + "LEFT JOIN skor_steward_pasca_anestesi s ON s.no_rawat = k.no_rawat AND s.tanggal = k.tanggal "
                    + "LEFT JOIN catatan_anestesi_keluar_kamar kk ON kk.no_rawat = k.no_rawat AND kk.tanggal = k.tanggal "
                    + "LEFT JOIN pegawai pg ON pg.nik = kk.kd_petugas "
                    + (TCari.getText().trim().isEmpty() ? "" : "WHERE (k.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY k.tanggal DESC");
            String d0 = Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00";
            String d1 = Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59";
            for (int i = 1; i <= 8; i++) ps.setString(i, (i % 2 == 1) ? d0 : d1);
            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(9, "%" + TCari.getText() + "%");
                ps.setString(10, "%" + TCari.getText() + "%");
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeScore.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tanggal"),
                    rs.getString("bromage") != null ? rs.getString("bromage") : "",
                    rs.getString("aldrette") != null ? rs.getString("aldrette") : "",
                    rs.getString("steward") != null ? rs.getString("steward") : "",
                    rs.getString("jam_keluar") != null ? rs.getString("jam_keluar") : "",
                    rs.getString("pindah_ke") != null ? rs.getString("pindah_ke") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilScore: " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    // Samakan dengan web e-Nakes: sembunyikan field yang dihapus dari form.
    private void sembunyikanFieldDihapus() {
        try {
            // Data Awal: Medikasi Prabedah & Jenis Anestesi
            MedikasiPrabedah.setVisible(false); jLabel82.setVisible(false);
            JenisAnestesi.setVisible(false); jLabel97.setVisible(false);
            // Blokade Regional: vasokonstriktor & konsentrasi (tak ada di form fisik)
            Vasokonstriktor.setVisible(false); jLabel150.setVisible(false);
            Konsentrasi.setVisible(false); jLabel151.setVisible(false);
            // Blokade Regional: waktu suntik / analgesia / operasi / lamanya / tindakan tambahan
            WaktuMulaiSuntikJam.setVisible(false); jLabel152.setVisible(false);
            AnalgesiJam.setVisible(false); jLabel153.setVisible(false);
            Lamanya.setVisible(false); jLabel155.setVisible(false); jLabel156.setVisible(false);
            LamanyaMenitAnalgesi.setVisible(false); jLabel157.setVisible(false);
            OperasiJam.setVisible(false); jLabel154.setVisible(false);
            LamanyajamOperasi.setVisible(false); jLabel160.setVisible(false); jLabel161.setVisible(false);
            LamanyaJamMenit.setVisible(false); jLabel162.setVisible(false);
            TindakanAnestesiTambahan.setVisible(false); jLabel163.setVisible(false);
            // Keadaan Selama Operasi: total asupan & keluaran cairan
            Kristaloid.setVisible(false); jLabel168.setVisible(false);
            Koloid.setVisible(false); jLabel144.setVisible(false);
            Darah.setVisible(false); jLabel170.setVisible(false);
            KomponenDarah.setVisible(false);
            Perdarahan.setVisible(false); jLabel177.setVisible(false);
            Diuresis.setVisible(false); jLabel174.setVisible(false);
            CairanLain.setVisible(false); jLabel179.setVisible(false);
            jLabel165.setVisible(false); jLabel166.setVisible(false); jLabel175.setVisible(false);
        } catch (Throwable ex) { System.out.println("sembunyikanFieldDihapus : " + ex); }
    }

    private void simpanRegMon() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "No.Rawat"); return; }
        try {
            ps = koneksi.prepareStatement("insert into catatan_anestesi_monitoring (no_rawat,tanggal,tekanan_darah,nadi,respirasi,spo2,perdarahan,infus,kd_petugas) values (?,now(),?,?,?,?,?,?,?)");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, rmTD.getText()); ps.setString(3, rmNadi.getText()); ps.setString(4, rmRR.getText());
            ps.setString(5, rmSpo2.getText()); ps.setString(6, rmPerdarahan.getText()); ps.setString(7, rmObat.getText());
            ps.setString(8, KdPetugas.getText());
            ps.executeUpdate();
            rmTD.setText(""); rmNadi.setText(""); rmRR.setText(""); rmSpo2.setText(""); rmPerdarahan.setText(""); rmObat.setText("");
            tampilRegMon();
        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Gagal simpan monitoring: " + e); }
        finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void tampilRegMon() {
        if (tabModeRegMon == null) return;
        tabModeRegMon.setRowCount(0);
        if (TNoRw.getText().trim().equals("")) return;
        try {
            ps = koneksi.prepareStatement("select tanggal,tekanan_darah,nadi,respirasi,spo2,perdarahan,infus from catatan_anestesi_monitoring where no_rawat=? order by tanggal");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeRegMon.addRow(new Object[]{ rs.getString("tanggal"), rs.getString("tekanan_darah"), rs.getString("nadi"),
                    rs.getString("respirasi"), rs.getString("spo2"), rs.getString("perdarahan"), rs.getString("infus") });
            }
        } catch (Exception e) { System.out.println("Notif RegMon : " + e); }
        finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void hapusRegMon() {
        if (tbRegMon.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih baris dulu..!!"); return; }
        try {
            ps = koneksi.prepareStatement("delete from catatan_anestesi_monitoring where no_rawat=? and tanggal=?");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, tabModeRegMon.getValueAt(tbRegMon.getSelectedRow(), 0).toString());
            ps.executeUpdate();
            tampilRegMon();
        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Gagal hapus: " + e); }
        finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        //TCari.setText(norwt);
        //DTPCari2.setDate(tgl2);    
        isRawat();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpenilaian_pre_anestesi());
        BtnHapus.setEnabled(akses.getpenilaian_pre_anestesi());
        BtnEdit.setEnabled(akses.getpenilaian_pre_anestesi());
        BtnEdit.setEnabled(akses.getpenilaian_pre_anestesi());

    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
    }

    private void hapus() {
        /*    if(Sequel.queryu2tf("delete from penilaian_pre_anestesi where no_rawat=? and tanggal=?",2,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),7).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(""+tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        } */
    }

    private void ganti() {
        /*    if(Sequel.mengedittf("penilaian_pre_anestesi","no_rawat=? and tanggal=?","no_rawat=?,tanggal=?,kd_dokter=?,tanggal_operasi=?,diagnosa=?,rencana_tindakan=?,tb=?,bb=?,td=?,io2=?,nadi=?,"+
                "pernapasan=?,suhu=?,fisik_cardiovasculer=?,fisik_paru=?,fisik_abdomen=?,fisik_extrimitas=?,fisik_endokrin=?,fisik_ginjal=?,fisik_obatobatan=?,fisik_laborat=?,fisik_penunjang=?,"+
                "riwayat_penyakit_alergiobat=?,riwayat_penyakit_alergilainnya=?,riwayat_penyakit_terapi=?,riwayat_kebiasaan_merokok=?,riwayat_kebiasaan_ket_merokok=?,riwayat_kebiasaan_alkohol=?,"+
                "riwayat_kebiasaan_ket_alkohol=?,riwayat_kebiasaan_obat=?,riwayat_kebiasaan_ket_obat=?,riwayat_medis_cardiovasculer=?,riwayat_medis_respiratory=?,riwayat_medis_endocrine=?,"+
                "riwayat_medis_lainnya=?,asa=?,puasa=?,rencana_anestesi=?,rencana_perawatan=?,catatan_khusus=?",42,new String[]{
                TNoRw.getText(),Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),KdDokter.getText(),
                Valid.SetTgl(TglOperasi.getSelectedItem()+"")+" "+TglOperasi.getSelectedItem().toString().substring(11,19),Diagnosa.getText(),RencanaTindakan.getText(), 
                TB.getText(),BB.getText(),TD.getText(),IO2.getText(),Nadi.getText(),Pernapasan.getText(),Suhu.getText(),FisikCardio.getText(),FisikParu.getText(),
                FisikAbdomen.getText(),FisikExtrimitas.getText(),FisikEndokrin.getText(),FisikGinjal.getText(),FisikObat.getText(),FisikLaborat.getText(), 
                FisikPenunjang.getText(),PenyakitAlergiObat.getText(),PenyakitAlergiLainnya.getText(),PenyakitTerapi.getText(),PenyakitKebiasaanMerokok.getSelectedItem().toString(), 
                PenyakitKebiasaanJumlahRokok.getText(),PenyakitKebiasaanAlkohol.getSelectedItem().toString(),PenyakitKebiasaanJumlahAlkohol.getText(),
                PenyakitKebiasaanObat.getSelectedItem().toString(),PenyakitKebiasaanObatDiminum.getText(),MedisCardio.getText(),MedisRespiratory.getText(),MedisEndocrine.getText(),
                MedisLainnya.getText(),AngkaASA.getSelectedItem().toString(),Valid.SetTgl(TglPuasa.getSelectedItem()+"")+" "+TglPuasa.getSelectedItem().toString().substring(11,19), 
                RencanaAnestesi.getSelectedItem().toString(),RencanaPerawatan.getText(),CatatanKhusus.getText(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(),7).toString() 
            })==true){
                tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
                tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
                tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
                tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
                tbObat.setValueAt(Jk.getText(),tbObat.getSelectedRow(),4);
                tbObat.setValueAt(KdDokter.getText(),tbObat.getSelectedRow(),5);
                tbObat.setValueAt(NmDokter.getText(),tbObat.getSelectedRow(),6);
                tbObat.setValueAt(Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),7);
                tbObat.setValueAt(Valid.SetTgl(TglOperasi.getSelectedItem()+"")+" "+TglOperasi.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),8);
                tbObat.setValueAt(Diagnosa.getText(),tbObat.getSelectedRow(),9);
                tbObat.setValueAt(RencanaTindakan.getText(),tbObat.getSelectedRow(),10);
                tbObat.setValueAt(TB.getText(),tbObat.getSelectedRow(),11);
                tbObat.setValueAt(BB.getText(),tbObat.getSelectedRow(),12);
                tbObat.setValueAt(TD.getText(),tbObat.getSelectedRow(),13);
                tbObat.setValueAt(IO2.getText(),tbObat.getSelectedRow(),14);
                tbObat.setValueAt(Nadi.getText(),tbObat.getSelectedRow(),15);
                tbObat.setValueAt(Pernapasan.getText(),tbObat.getSelectedRow(),16);
                tbObat.setValueAt(Suhu.getText(),tbObat.getSelectedRow(),17);
                tbObat.setValueAt(FisikCardio.getText(),tbObat.getSelectedRow(),18);
                tbObat.setValueAt(FisikParu.getText(),tbObat.getSelectedRow(),19);
                tbObat.setValueAt(FisikAbdomen.getText(),tbObat.getSelectedRow(),20);
                tbObat.setValueAt(FisikExtrimitas.getText(),tbObat.getSelectedRow(),21);
                tbObat.setValueAt(FisikEndokrin.getText(),tbObat.getSelectedRow(),22);
                tbObat.setValueAt(FisikGinjal.getText(),tbObat.getSelectedRow(),23);
                tbObat.setValueAt(FisikObat.getText(),tbObat.getSelectedRow(),24);
                tbObat.setValueAt(FisikLaborat.getText(),tbObat.getSelectedRow(),25);
                tbObat.setValueAt(FisikPenunjang.getText(),tbObat.getSelectedRow(),26);
                tbObat.setValueAt(PenyakitAlergiObat.getText(),tbObat.getSelectedRow(),27);
                tbObat.setValueAt(PenyakitAlergiLainnya.getText(),tbObat.getSelectedRow(),28);
                tbObat.setValueAt(PenyakitTerapi.getText(),tbObat.getSelectedRow(),29);
                tbObat.setValueAt(PenyakitKebiasaanMerokok.getSelectedItem().toString(),tbObat.getSelectedRow(),30);
                tbObat.setValueAt(PenyakitKebiasaanJumlahRokok.getText(),tbObat.getSelectedRow(),31);
                tbObat.setValueAt(PenyakitKebiasaanAlkohol.getSelectedItem().toString(),tbObat.getSelectedRow(),32);
                tbObat.setValueAt(PenyakitKebiasaanJumlahAlkohol.getText(),tbObat.getSelectedRow(),33);
                tbObat.setValueAt(PenyakitKebiasaanObat.getSelectedItem().toString(),tbObat.getSelectedRow(),34);
                tbObat.setValueAt(PenyakitKebiasaanObatDiminum.getText(),tbObat.getSelectedRow(),35);
                tbObat.setValueAt(MedisCardio.getText(),tbObat.getSelectedRow(),36);
                tbObat.setValueAt(MedisRespiratory.getText(),tbObat.getSelectedRow(),37);
                tbObat.setValueAt(MedisEndocrine.getText(),tbObat.getSelectedRow(),38);
                tbObat.setValueAt(MedisLainnya.getText(),tbObat.getSelectedRow(),39);
                tbObat.setValueAt(AngkaASA.getSelectedItem().toString(),tbObat.getSelectedRow(),40);
                tbObat.setValueAt(Valid.SetTgl(TglPuasa.getSelectedItem()+"")+" "+TglPuasa.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),41);
                tbObat.setValueAt(RencanaAnestesi.getSelectedItem().toString(),tbObat.getSelectedRow(),42);
                tbObat.setValueAt(RencanaPerawatan.getText(),tbObat.getSelectedRow(),43);
                tbObat.setValueAt(CatatanKhusus.getText(),tbObat.getSelectedRow(),44);
                emptTeks();
                TabRawat.setSelectedIndex(1);
        } */
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;

            @Override
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                // Membuat Date
                //Date dt = new Date();
                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if (ChkJln.isSelected() == true) {
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                } else if (ChkJln.isSelected() == false) {
                    nilai_jam = cmbJam.getSelectedIndex();
                    nilai_menit = cmbMnt.getSelectedIndex();
                    nilai_detik = cmbDtk.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                cmbJam.setSelectedItem(jam);
                cmbMnt.setSelectedItem(menit);
                cmbDtk.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 280));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    private void simpanDataAwal() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdDokterAnastesi.getText().trim().equals("")) {
            Valid.textKosong(KdDokterAnastesi, "Dokter Anestesi");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_data_awal",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data Awal", 17, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        KdDokterAnastesi.getText(),
                        KdDokterBedah.getText(),
                        KdAsistenAnestesi.getText(),
                        KdAsistenBedah.getText(),
                        MedikasiPrabedah.getText(),
                        JenisAnestesi.getText(),
                        DiagnosaPrabedah.getText(),
                        InformedConsent.getSelectedItem().toString(),
                        ObatAnestesi.getSelectedItem().toString(),
                        TatalaksanaJalanNafas.getSelectedItem().toString(),
                        MesinAnestesi.getSelectedItem().toString(),
                        ObatEmergensi.getSelectedItem().toString(),
                        CheckMonitoring.getSelectedItem().toString(),
                        SuctionAparatus.getSelectedItem().toString()
                    }) == true) {
                emptTeks();
                tampilDataAwal();
            }
        }
    }

    private void gantiDataAwal() {
        if (tbAnestesi.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else if (KdDokterAnastesi.getText().trim().equals("")) {
                Valid.textKosong(KdDokterAnastesi, "Dokter Anestesi");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_data_awal",
                        "no_rawat=? and tanggal=?",
                        "no_rawat=?,tanggal=?,kd_petugas=?,kd_dokter_anestesi=?,kd_dokter_bedah=?,kd_asisten_anestesi=?,kd_asisten_bedah=?,medikasi_prabedah=?,jenis_anestesi=?,diagnosa_prabedah=?,informed_consent=?,obat_anestesi=?,tatalaksana_jalan_nafas=?,mesin_anestesi=?,obat_emergensi=?,check_monitoring=?,suction_aparatus=?",
                        19, new String[]{
                            TNoRw.getText(),
                            Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                            KdPetugas.getText(),
                            KdDokterAnastesi.getText(),
                            KdDokterBedah.getText(),
                            KdAsistenAnestesi.getText(),
                            KdAsistenBedah.getText(),
                            MedikasiPrabedah.getText(),
                            JenisAnestesi.getText(),
                            DiagnosaPrabedah.getText(),
                            InformedConsent.getSelectedItem().toString(),
                            ObatAnestesi.getSelectedItem().toString(),
                            TatalaksanaJalanNafas.getSelectedItem().toString(),
                            MesinAnestesi.getSelectedItem().toString(),
                            ObatEmergensi.getSelectedItem().toString(),
                            CheckMonitoring.getSelectedItem().toString(),
                            SuctionAparatus.getSelectedItem().toString(),
                            tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 0).toString(), // no_rawat lama
                            tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 5).toString() // tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilDataAwal();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

// ===== CRUD PRA INDUKSI =====
    private void simpanPraInduksi() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_pra_induksi",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Pra Induksi", 31, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        txtKesadaran.getText(),
                        GCS.getText(),
                        Airway.getSelectedItem().toString(),
                        Tensi.getText(),
                        Nadi.getText(),
                        Support1.getText(),
                        Support2.getText(),
                        cmbRespirasi.getSelectedItem().toString(),
                        Rr.getText(),
                        Kanul.getSelectedItem().toString(),
                        O2.getText(),
                        Spo2.getText(),
                        Bb.getText(),
                        Hmt.getText(), // Puasa Jam
                        Lab.getText(), // Laboratorium
                        Ekg.getText(), // EKG
                        Thorax.getText(), // Thorax Foto
                        TFP.getText(), // TFP
                        PxLain.getText(), // Pemeriksaan Lain
                        Saraf.getText(), // Sistem Saraf
                        Respirasi.getText(), // Sistem Respirasi
                        Gastro.getText(), // Sistem Gastro
                        Urinaria.getText(), // Sistem Urinaria
                        Muskuloskletal.getText(), // Sistem Muskuloskeletal
                        Metabolik.getText(), // Sistem Metabolik
                        Lain.getText(), // Lain-lain
                        ASA.getSelectedItem().toString(),
                        Alergi.getSelectedItem().toString()
                    }) == true) {
                emptTeks();
                tampilPraInduksi();
            }
        }
    }

    private void gantiPraInduksi() {
        if (tbAnestesi1.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else if (KdPetugas.getText().trim().equals("")) {
                Valid.textKosong(KdPetugas, "Petugas");
            } else {
                // Debug: print nilai yang akan di-update
                System.out.println("No Rawat Lama: " + tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 0).toString());
                System.out.println("Tanggal Lama: " + tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 5).toString());
                System.out.println("Airway: " + Airway.getSelectedItem().toString());
                System.out.println("Respirasi: " + cmbRespirasi.getSelectedItem().toString());
                System.out.println("Kanul: " + Kanul.getSelectedItem().toString());
                System.out.println("ASA: " + ASA.getSelectedItem().toString());
                System.out.println("Alergi: " + Alergi.getSelectedItem().toString());

                if (Sequel.mengedittf("catatan_anestesi_pra_induksi",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,kesadaran=?,gcs=?,airway=?,tensi=?,nadi=?,support1=?,support2=?,"
                        + "respirasi=?,rr=?,kanul_nasal=?,o2=?,spo2=?,bb=?,puasa_jam=?,laboratorium=?,"
                        + "ekg=?,thorax_foto=?,tfp=?,pemeriksaan_lain=?,sistem_saraf=?,sistem_respirasi=?,"
                        + "sistem_gastro=?,sistem_urinaria=?,sistem_muskuloskeletal=?,sistem_metabolik=?,"
                        + "lain_lain=?,asa=?,alergi=?",
                        31, new String[]{
                            KdPetugas.getText(), // 1
                            txtKesadaran.getText(), // 2
                            GCS.getText(), // 3
                            validateEnumAirway(Airway.getSelectedItem().toString()), // 4
                            Tensi.getText(), // 5
                            Nadi.getText(), // 6
                            Support1.getText(), // 7
                            Support2.getText(), // 8
                            validateEnumRespirasi(cmbRespirasi.getSelectedItem().toString()), // 9
                            Rr.getText(), // 10
                            validateEnumKanul(Kanul.getSelectedItem().toString()), // 11
                            O2.getText(), // 12
                            Spo2.getText(), // 13
                            Bb.getText(), // 14
                            Hmt.getText(), // 15 (puasa_jam)
                            Lab.getText(), // 16
                            Ekg.getText(), // 17
                            Thorax.getText(), // 18
                            TFP.getText(), // 19
                            PxLain.getText(), // 20
                            Saraf.getText(), // 21
                            Respirasi.getText(), // 22
                            Gastro.getText(), // 23
                            Urinaria.getText(), // 24
                            Muskuloskletal.getText(), // 25
                            Metabolik.getText(), // 26
                            Lain.getText(), // 27
                            validateEnumASA(ASA.getSelectedItem().toString()), // 28
                            validateEnumAlergi(Alergi.getSelectedItem().toString()), // 29
                            // WHERE condition parameters:
                            tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 0).toString(), // 30 - no_rawat lama
                            tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 5).toString() // 31 - tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilPraInduksi();
                    System.out.println("Update berhasil!");
                } else {
                    System.out.println("Update gagal!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private String validateEnumAirway(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Terintubasi";
        }
        if (value.equals("Terintubasi") || value.equals("Tidak Terintubasi")) {
            return value;
        }
        return "Terintubasi"; // Default
    }

    private String validateEnumRespirasi(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Spontan";
        }
        if (value.equals("Spontan") || value.equals("Bantuan") || value.equals("Terkontrol")) {
            return value;
        }
        return "Spontan"; // Default
    }

    private String validateEnumKanul(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "SM";
        }
        if (value.equals("SM") || value.equals("SMNR")) {
            return value;
        }
        return "SM"; // Default
    }

    private String validateEnumASA(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "I";
        }
        if (value.equals("I") || value.equals("II") || value.equals("III")
                || value.equals("IV") || value.equals("V") || value.equals("E")) {
            return value;
        }
        return "I"; // Default
    }

    private String validateEnumAlergi(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Tidak";
        }
        if (value.equals("Ya") || value.equals("Tidak") || value.equals("Tidak Diketahui")) {
            return value;
        }
        return "Tidak"; // Default
    }

    private void simpanPremedikasi() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else if (NamaObatAnestesi.getText().trim().equals("")) {
            Valid.textKosong(NamaObatAnestesi, "Nama Obat Anestesi");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_premedikasi",
                    "?,?,?,?,?,?,?,?", "Premedikasi", 8, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        JenisPremedikasi1.getSelectedItem().toString(),
                        NmObatPremedikasi1.getText(),
                        DosisPremedikasi.getText(),
                        JenisAnestesiPremedikasi.getSelectedItem().toString(),
                        NamaObatAnestesi.getText()
                    }) == true) {
                emptTeks();
                tampilPremedikasi();
            }
        }
    }

    private void simpanAnestesiUmumDataAwal() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_umum_data_awal",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Anestesi Umum Data Awal", 14, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        Induksi.getSelectedItem().toString(),
                        InduksiKet.getText(),
                        PengaturaNafas.getSelectedItem().toString(),
                        TidalVolum.getText(),
                        Rr2.getText(),
                        Ieratio.getText(),
                        Peep.getText(),
                        Pip.getText(),
                        Fio2.getText(),
                        TeknikKhusus.getSelectedItem() != null ? TeknikKhusus.getSelectedItem().toString() : "",
                        TeknikKhususKet.getText()
                    }) == true) {
                emptTeks();
                tampilAnestesiUmumDataAwal();
            }
        }
    }

    private void gantiAnestesiUmumDataAwal() {
        if (tbAnestesi9.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else if (KdPetugas.getText().trim().equals("")) {
                Valid.textKosong(KdPetugas, "Petugas");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_umum_data_awal",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,induksi=?,induksi_ket=?,pengaturan_nafas=?,tidal_volum=?,"
                        + "rr=?,ie_ratio=?,peep=?,pip=?,fio2=?,teknik_khusus=?,teknik_khusus_ket=?",
                        14, new String[]{
                            KdPetugas.getText(),
                            Induksi.getSelectedItem().toString(),
                            InduksiKet.getText(),
                            PengaturaNafas.getSelectedItem().toString(),
                            TidalVolum.getText(),
                            Rr2.getText(),
                            Ieratio.getText(),
                            Peep.getText(),
                            Pip.getText(),
                            Fio2.getText(),
                            TeknikKhusus.getSelectedItem() != null ? TeknikKhusus.getSelectedItem().toString() : "",
                            TeknikKhususKet.getText(),
                            tbAnestesi9.getValueAt(tbAnestesi9.getSelectedRow(), 0).toString(),
                            tbAnestesi9.getValueAt(tbAnestesi9.getSelectedRow(), 5).toString()
                        }) == true) {
                    emptTeks();
                    tampilAnestesiUmumDataAwal();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private void hapusAnestesiUmumDataAwal() {
        if (tbAnestesi9.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_umum_data_awal where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi9.getValueAt(tbAnestesi9.getSelectedRow(), 0).toString(),
                tbAnestesi9.getValueAt(tbAnestesi9.getSelectedRow(), 5).toString()
            }) == true) {
                tampilAnestesiUmumDataAwal();
                emptTeks();
            }
        }
    }

    private void simpanAnestesiUmumKeterangan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_umum_keterangan",
                    "?,?,?,?,?,?,?", "Anestesi Umum Keterangan", 7, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        Medikasi.getText(),
                        PemberianCairan.getText(),
                        MasalahDuranteOperasi.getText(),
                        Tindakan.getText()
                    }) == true) {
                emptTeks();
                tampilAnestesiUmumKeterangan();
            }
        }
    }

    private void simpanBlokadeRegional() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_blokade_regional",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Blokade Regional", 20, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        Teknik.getSelectedItem().toString(),
                        LokasiTusukan.getText(),
                        AnalgesiSetinggiSegmen.getText(),
                        AnestesiLokal.getText(),
                        KonsentrasiJumlah.getText(),
                        ObatTambahan.getText(),
                        Dosis.getText(),
                        Vasokonstriktor.getSelectedItem().toString(),
                        Konsentrasi.getText(),
                        WaktuMulaiSuntikJam.getText(),
                        AnalgesiJam.getText(),
                        Lamanya.getText(),
                        LamanyaMenitAnalgesi.getText(),
                        OperasiJam.getText(),
                        LamanyajamOperasi.getText(),
                        LamanyaJamMenit.getText(),
                        TindakanAnestesiTambahan.getText()
                    }) == true) {
                emptTeks();
                tampilBlokadeRegional();
            }
        }
    }

    private void simpanKeadaanOperasi() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_keadaan_operasi",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Keadaan Operasi", 16, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        LetakPenderita.getSelectedItem().toString(),
                        Airway2.getSelectedItem().toString(),
                        Ukuran.getText(),
                        BalonNoBalon.getSelectedItem().toString(),
                        LamaOperasi.getText(),
                        LamaAnestesi.getText(),
                        Kristaloid.getText(),
                        Perdarahan.getText(),
                        Koloid.getText(),
                        Diuresis.getText(),
                        Darah.getText(),
                        CairanLain.getText(),
                        KomponenDarah.getText(),}) == true) {
                emptTeks();
                tampilKeadaanOperasi();
            }
        }
    }

    private void simpanSectio() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_sectio_caesaria",
                    "?,?,?,?,?,?,?,?", "Sectio Caesaria", 8, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        KeadaanBayi.getSelectedItem().toString(),
                        JenisKelamin.getSelectedItem().toString(),
                        Apgrar1Menit.getText(),
                        Apgar5Menit.getText(),
                        Apgar10Menit.getText()
                    }) == true) {
                emptTeks();
                tampilSectio();
            }
        }
    }

    private void gantiPremedikasi() {
        if (tbAnestesi2.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else if (KdPetugas.getText().trim().equals("")) {
                Valid.textKosong(KdPetugas, "Petugas");
            } else if (NamaObatAnestesi.getText().trim().equals("")) {
                Valid.textKosong(NamaObatAnestesi, "Nama Obat Anestesi");
            } else {
                // Debug: print nilai yang akan di-update
                System.out.println("No Rawat Lama: " + tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 0).toString());
                System.out.println("Tanggal Lama: " + tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 5).toString());
                System.out.println("Jenis Premedikasi: " + JenisPremedikasi1.getSelectedItem().toString());
                System.out.println("Jenis Anestesi: " + JenisAnestesiPremedikasi.getSelectedItem().toString());

                if (Sequel.mengedittf("catatan_anestesi_premedikasi",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,jenis_premedikasi=?,nm_obat_premedikasi=?,dosis=?,"
                        + "jenis_anestesi=?,nama_obat_anestesi=?",
                        8, new String[]{ // DIPERBAIKI: parameter count = 8
                            KdPetugas.getText(), // 1
                            JenisPremedikasi1.getSelectedItem().toString(), // 2
                            NmObatPremedikasi1.getText(), // 3
                            DosisPremedikasi.getText(), // 4
                            JenisAnestesiPremedikasi.getSelectedItem().toString(), // 5
                            NamaObatAnestesi.getText(), // 6
                            // WHERE condition parameters:
                            tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 0).toString(), // 7 - no_rawat lama
                            tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 5).toString() // 8 - tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilPremedikasi();
                    System.out.println("Update premedikasi berhasil!");
                } else {
                    System.out.println("Update premedikasi gagal!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private void simpanMonitoring() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            // 21 parameter untuk 21 kolom manual
            if (Sequel.menyimpantf("catatan_anestesi_monitoring",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Monitoring", 21, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        PernapasanMasuk12.getText(), // o2
                        PernapasanMasuk14.getText(), // n2o
                        PernapasanMasuk16.getText(), // air
                        Lainnya2.getText(), // isofluran
                        "", // lain_lain
                        SirkulasiMasuk19.getText(), // sevofluran
                        SirkulasiMasuk14.getText(), // infus
                        SirkulasiMasuk18.getText(), // barbiturat
                        SirkulasiMasuk22.getText(), // relaksan_depol
                        SirkulasiMasuk21.getText(), // relaksan_nin_dipol
                        PemantauanNadi2.getText(), // analgetika
                        Selama2.getText(), // neuroleptik
                        "", // lain_lain_obat
                        Tindakan.getText(), // respirasi
                        Tindakan.getText(), // nadi
                        Tindakan.getText(), // tekanan_darah
                        LokasiTusukan.getText(), // perdarahan
                        AnalgesiSetinggiSegmen.getText(), // spo2
                        KdPetugas.getText() // kd_petugas
                    }) == true) {
                emptTeks();
                tampilMonitoring();
            }
        }
    }

    private void gantiMonitoring() {
        if (tbAnestesi3.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_monitoring", "no_rawat=? and tanggal=?",
                    "o2=?,n2o=?,air=?,isofluran=?,lain_lain=?,sevofluran=?,infus=?,barbiturat=?,"
                    + "relaksan_depol=?,relaksan_nin_dipol=?,analgetika=?,neuroleptik=?,lain_lain_obat=?,"
                    + "respirasi=?,nadi=?,tekanan_darah=?,perdarahan=?,spo2=?",
                    20, new String[]{
                        PernapasanMasuk12.getText(), // o2
                        PernapasanMasuk14.getText(), // n2o
                        PernapasanMasuk16.getText(), // air
                        Lainnya2.getText(), // isofluran
                        "", // lain_lain (kosong)
                        SirkulasiMasuk19.getText(), // sevofluran
                        SirkulasiMasuk14.getText(), // infus
                        SirkulasiMasuk18.getText(), // barbiturat
                        SirkulasiMasuk22.getText(), // relaksan_depol
                        SirkulasiMasuk21.getText(), // relaksan_nin_dipol
                        PemantauanNadi2.getText(), // analgetika
                        Selama2.getText(), // neuroleptik
                        PemberianCairan.getText(), // lain_lain_obat
                        Tindakan.getText(), // respirasi
                        Tindakan.getText(), // nadi
                        Tindakan.getText(), // tekanan_darah
                        LokasiTusukan.getText(), // perdarahan
                        AnalgesiSetinggiSegmen.getText(), // spo2
                        tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 0).toString(),
                        tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilMonitoring();
                emptTeks();
            }
        }
    }

// ===== CRUD CATATAN =====
    private void simpanCatatan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            // 10 parameter untuk 10 kolom manual
            if (Sequel.menyimpantf("catatan_anestesi_catatan",
                    "?,?,?,?,?,?,?,?,?,?", "Catatan", 10, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Ukuran.getText(), // diagnosa_pasca_bedah
                        LamaOperasi.getText(), // lama_operasi
                        LamaAnestesi.getText(), // lama_anestesi
                        BalonNoBalon.getSelectedItem().toString(), // cairan_masuk
                        Kristaloid.getText(), // jumlah_cairan_masuk
                        LetakPenderita.getSelectedItem().toString(), // cairan_keluar
                        Koloid.getText(), // jumlah_cairan_keluar
                        KdPetugas.getText() // kd_petugas
                    }) == true) {
                emptTeks();
                tampilCatatan();
            }
        }
    }

    private void gantiCatatan() {
        if (tbAnestesi4.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_catatan", "no_rawat=? and tanggal=?",
                    "diagnosa_pasca_bedah=?,lama_operasi=?,lama_anestesi=?,cairan_masuk=?,"
                    + "jumlah_cairan_masuk=?,cairan_keluar=?,jumlah_cairan_keluar=?",
                    9, new String[]{
                        Ukuran.getText(),
                        LamaOperasi.getText(),
                        LamaAnestesi.getText(),
                        BalonNoBalon.getSelectedItem().toString(),
                        Kristaloid.getText(),
                        LetakPenderita.getSelectedItem().toString(),
                        Koloid.getText(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 1).toString()
                    }) == true) {
                tampilCatatan();
                emptTeks();
            }
        }
    }

    private void simpanObservasiKamar() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            // 8 parameter untuk 8 kolom manual
            if (Sequel.menyimpantf("catatan_anestesi_observasi_kamar",
                    "?,?,?,?,?,?,?,?", "Observasi Kamar", 8, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Suhu.getText(), // tvs
                        Rr4.getText(), // rr
                        Nadi2.getText(), // nadi
                        TekananDarah.getText(), // tekanan_darah
                        "", // vas
                        KdPetugas.getText() // kd_petugas
                    }) == true) {
                emptTeks();
                tampilObservasiKamar();
            }
        }
    }

    private void gantiObservasiKamar() {
        if (tbAnestesi6.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_observasi_kamar", "no_rawat=? and tanggal=?",
                    "tvs=?,rr=?,nadi=?,tekanan_darah=?,vas=?",
                    7, new String[]{
                        Suhu.getText(), // tvs
                        Rr4.getText(), // rr
                        Nadi2.getText(), // nadi
                        TekananDarah.getText(), // tekanan_darah
                        "", // vas
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(), // no_rawat
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 1).toString() // tanggal
                    }) == true) {
                tampilObservasiKamar();
                emptTeks();
            }
        }
    }

    // Method untuk handle tab change
    private void TabRawatStateChanged(javax.swing.event.ChangeEvent evt) {
        setupTableBasedOnActiveTab();
        tampil();
    }

    private void CatatanKamarPemulihanStateChanged(javax.swing.event.ChangeEvent evt) {
        if (TabRawat.getSelectedIndex() == 6) { // Keadaan Pasca Anestesi = index 6
            setupTableBasedOnActiveTab();
            tampil();
        }
    }

    private void AnestesiUmumStateChanged(javax.swing.event.ChangeEvent evt) {
        if (TabRawat.getSelectedIndex() == 1) { // Anestesi Umum = index 1
            setupTableBasedOnActiveTab();
            tampil();
        }
    }

    private void hapusDataAwal() {
        if (tbAnestesi.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_data_awal where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 0).toString(),
                        tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilDataAwal();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusPraInduksi() {
        if (tbAnestesi1.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_pra_induksi where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 0).toString(),
                        tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilPraInduksi();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusPremedikasi() {
        if (tbAnestesi2.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_premedikasi where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 0).toString(),
                tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 5).toString()
            }) == true) {
                tampilPremedikasi();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusMonitoring() {
        if (tbAnestesi3.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_monitoring where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 0).toString(),
                tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 5).toString()
            }) == true) {
                tampilMonitoring();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusCatatan() {
        if (tbAnestesi4.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_catatan where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(),
                tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 5).toString()
            }) == true) {
                tampilCatatan();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusObservasiKamar() {
        if (tbAnestesi6.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_observasi_kamar where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(),
                tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString()
            }) == true) {
                tampilObservasiKamar();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void setupTablePraInduksi() {
        tabModePraInduksi = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Kesadaran", "GCS", "Airway", "Tensi", "Nadi",
            "Support 1", "Support 2", "Respirasi", "RR", "Kanul Nasal",
            "O2", "SpO2", "BB", "Puasa Jam", "Laboratorium", "EKG",
            "Thorax Foto", "TFP", "Pemeriksaan Lain", "Sistem Saraf",
            "Sistem Respirasi", "Sistem Gastro", "Sistem Urinaria",
            "Sistem Muskuloskeletal", "Sistem Metabolik", "Lain-lain",
            "ASA", "Alergi"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class
                    getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi1.setModel(tabModePraInduksi);

        // Atur lebar kolom
        if (tbAnestesi1.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi1.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi1.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi1.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi1.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi1.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi1.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi1.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi1.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi1.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom pemeriksaan fisik
            tbAnestesi1.getColumnModel().getColumn(9).setPreferredWidth(100);  // Kesadaran
            tbAnestesi1.getColumnModel().getColumn(10).setPreferredWidth(60);  // GCS
            tbAnestesi1.getColumnModel().getColumn(11).setPreferredWidth(120); // Airway
            tbAnestesi1.getColumnModel().getColumn(12).setPreferredWidth(80);  // Tensi
            tbAnestesi1.getColumnModel().getColumn(13).setPreferredWidth(60);  // Nadi
            tbAnestesi1.getColumnModel().getColumn(14).setPreferredWidth(100); // Support 1
            tbAnestesi1.getColumnModel().getColumn(15).setPreferredWidth(100); // Support 2

            // Kolom respirasi
            tbAnestesi1.getColumnModel().getColumn(16).setPreferredWidth(80);  // Respirasi
            tbAnestesi1.getColumnModel().getColumn(17).setPreferredWidth(60);  // RR
            tbAnestesi1.getColumnModel().getColumn(18).setPreferredWidth(80);  // Kanul Nasal
            tbAnestesi1.getColumnModel().getColumn(19).setPreferredWidth(60);  // O2
            tbAnestesi1.getColumnModel().getColumn(20).setPreferredWidth(60);  // SpO2
            tbAnestesi1.getColumnModel().getColumn(21).setPreferredWidth(60);  // BB
            tbAnestesi1.getColumnModel().getColumn(22).setPreferredWidth(80);  // Puasa Jam

            // Kolom pemeriksaan penunjang
            tbAnestesi1.getColumnModel().getColumn(23).setPreferredWidth(120); // Laboratorium
            tbAnestesi1.getColumnModel().getColumn(24).setPreferredWidth(80);  // EKG
            tbAnestesi1.getColumnModel().getColumn(25).setPreferredWidth(100); // Thorax Foto
            tbAnestesi1.getColumnModel().getColumn(26).setPreferredWidth(80);  // TFP
            tbAnestesi1.getColumnModel().getColumn(27).setPreferredWidth(120); // Pemeriksaan Lain

            // Kolom sistem evaluasi
            tbAnestesi1.getColumnModel().getColumn(28).setPreferredWidth(100); // Sistem Saraf
            tbAnestesi1.getColumnModel().getColumn(29).setPreferredWidth(120); // Sistem Respirasi
            tbAnestesi1.getColumnModel().getColumn(30).setPreferredWidth(100); // Sistem Gastro
            tbAnestesi1.getColumnModel().getColumn(31).setPreferredWidth(120); // Sistem Urinaria
            tbAnestesi1.getColumnModel().getColumn(32).setPreferredWidth(150); // Sistem Muskuloskeletal
            tbAnestesi1.getColumnModel().getColumn(33).setPreferredWidth(120); // Sistem Metabolik
            tbAnestesi1.getColumnModel().getColumn(34).setPreferredWidth(100); // Lain-lain

            // Kolom classification
            tbAnestesi1.getColumnModel().getColumn(35).setPreferredWidth(60);  // ASA
            tbAnestesi1.getColumnModel().getColumn(36).setPreferredWidth(100); // Alergi
        }

        // Set table styling
        setTableStyling(tbAnestesi1);
    }

    private void setupTablePremedikasi() {
        tabModePremedikasi = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Jenis Premedikasi", "Nama Obat Premedikasi", "Dosis",
            "Jenis Anestesi", "Nama Obat Anestesi"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class
                    getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi2.setModel(tabModePremedikasi);

        // Atur lebar kolom
        if (tbAnestesi2.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi2.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi2.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi2.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi2.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi2.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi2.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi2.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi2.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi2.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik premedikasi
            tbAnestesi2.getColumnModel().getColumn(9).setPreferredWidth(120);  // Jenis Premedikasi
            tbAnestesi2.getColumnModel().getColumn(10).setPreferredWidth(150); // Nama Obat Premedikasi
            tbAnestesi2.getColumnModel().getColumn(11).setPreferredWidth(80);  // Dosis
            tbAnestesi2.getColumnModel().getColumn(12).setPreferredWidth(120); // Jenis Anestesi
            tbAnestesi2.getColumnModel().getColumn(13).setPreferredWidth(150); // Nama Obat Anestesi
        }

        // Set table styling
        setTableStyling(tbAnestesi2);
    }

// Setup Table Monitoring
    private void setupTableMonitoring() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal Jam", "Ruangan",
            "O2", "N2O", "Air", "Isofluran", "Lain-lain", "Sevofluran",
            "Infus", "Barbiturat", "Relaksan Depol", "Relaksan Non Depol",
            "Analgetika", "Neuroleptik", "Lain-lain Obat",
            "Respirasi", "Nadi", "Tekanan Darah", "Perdarahan", "SpO2",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi3.setModel(tabModeDataAwal);

        if (tbAnestesi3.getColumnModel().getColumnCount() > 0) {
            for (int i = 0; i < tbAnestesi3.getColumnModel().getColumnCount(); i++) {
                if (i < 7) {
                    tbAnestesi3.getColumnModel().getColumn(i).setPreferredWidth(100);
                } else {
                    tbAnestesi3.getColumnModel().getColumn(i).setPreferredWidth(80);
                }
            }
        }
        setTableStyling(tbAnestesi3);
    }

// Setup Table Catatan
    private void setupTableCatatan() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Diagnosa Pasca Bedah", "Lama Operasi", "Lama Anestesi",
            "Cairan Masuk", "Jumlah Cairan Masuk", "Cairan Keluar", "Jumlah Cairan Keluar",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi4.setModel(tabModeDataAwal);

        if (tbAnestesi4.getColumnModel().getColumnCount() > 0) {
            tbAnestesi4.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi4.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi4.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi4.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi4.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi4.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi4.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi4.getColumnModel().getColumn(7).setPreferredWidth(200);
            tbAnestesi4.getColumnModel().getColumn(8).setPreferredWidth(100);
            tbAnestesi4.getColumnModel().getColumn(9).setPreferredWidth(100);
            tbAnestesi4.getColumnModel().getColumn(10).setPreferredWidth(100);
            tbAnestesi4.getColumnModel().getColumn(11).setPreferredWidth(150);
            tbAnestesi4.getColumnModel().getColumn(12).setPreferredWidth(100);
            tbAnestesi4.getColumnModel().getColumn(13).setPreferredWidth(150);
            tbAnestesi4.getColumnModel().getColumn(14).setPreferredWidth(80);
            tbAnestesi4.getColumnModel().getColumn(15).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi4);
    }

// Setup Table Masuk Kamar
    private void setupTableMasukKamar() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Jam Masuk", "Jalan Nafas", "Pernapasan", "Bila Spontan", "Kesadaran",
            "Aktifitas", "Sirkulasi", "Pernapasan Score", "Kesadaran Score",
            "Warna Kulit", "Total Score", "Skor Bromage", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi5.setModel(tabModeDataAwal);

        if (tbAnestesi5.getColumnModel().getColumnCount() > 0) {
            tbAnestesi5.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi5.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi5.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi5.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi5.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(7).setPreferredWidth(130);
            tbAnestesi5.getColumnModel().getColumn(8).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(9).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(10).setPreferredWidth(120);
            tbAnestesi5.getColumnModel().getColumn(11).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(12).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(13).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(14).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(15).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(16).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(17).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(18).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(19).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(20).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi5);
    }

// Setup Table Observasi Kamar
    private void setupTableObservasiKamar() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "TVS", "RR", "Nadi", "Tekanan Darah", "VAS", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi6.setModel(tabModeDataAwal);

        if (tbAnestesi6.getColumnModel().getColumnCount() > 0) {
            tbAnestesi6.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi6.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi6.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi6.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi6.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(7).setPreferredWidth(60);
            tbAnestesi6.getColumnModel().getColumn(8).setPreferredWidth(60);
            tbAnestesi6.getColumnModel().getColumn(9).setPreferredWidth(60);
            tbAnestesi6.getColumnModel().getColumn(10).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(11).setPreferredWidth(60);
            tbAnestesi6.getColumnModel().getColumn(12).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(13).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi6);
    }

// Setup Table Keluar Kamar
    private void setupTableKeluarKamar() {
        /*    tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Jam Keluar", "Aktifitas", "Sirkulasi", "Pernapasan", "Kesadaran",
            "Warna Kulit", "Total Score", "Skor Bromage", "Pindah Ke", "Catatan Khusus",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi8.setModel(tabMode);

        if (tbAnestesi8.getColumnModel().getColumnCount() > 0) {
            tbAnestesi8.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi8.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi8.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi8.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi8.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi8.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi8.getColumnModel().getColumn(7).setPreferredWidth(130);
            tbAnestesi8.getColumnModel().getColumn(8).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(9).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(10).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(11).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(12).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(13).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(14).setPreferredWidth(200);
            tbAnestesi8.getColumnModel().getColumn(15).setPreferredWidth(100);
            tbAnestesi8.getColumnModel().getColumn(16).setPreferredWidth(200);
            tbAnestesi8.getColumnModel().getColumn(17).setPreferredWidth(80);
            tbAnestesi8.getColumnModel().getColumn(18).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi8); */
    }

// Setup Table Instruksi Pasca
    private void setupTableInstruksiPasca() {
        /*    tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Bila Kesakitan", "Bila Mual/Muntah", "Antibiotika", "Obat Lain",
            "Infus", "Minum", "Pemantauan Nadi", "Selama", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi5.setModel(tabMode);

        if (tbAnestesi5.getColumnModel().getColumnCount() > 0) {
            tbAnestesi5.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi5.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi5.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi5.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi5.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(7).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(8).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(9).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(10).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(11).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(12).setPreferredWidth(200);
            tbAnestesi5.getColumnModel().getColumn(13).setPreferredWidth(150);
            tbAnestesi5.getColumnModel().getColumn(14).setPreferredWidth(100);
            tbAnestesi5.getColumnModel().getColumn(15).setPreferredWidth(80);
            tbAnestesi5.getColumnModel().getColumn(16).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi5); */
    }

// Method untuk mengatur styling table
    private void setTableStyling(widget.Table table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer untuk zebra striping
        table
                .setDefaultRenderer(Object.class,
                        new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        java.awt.Component c = super.getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);

                        if (!isSelected) {
                            if (row % 2 == 0) {
                                c.setBackground(new java.awt.Color(255, 255, 255)); // Putih
                            } else {
                                c.setBackground(new java.awt.Color(250, 250, 250)); // Abu-abu muda
                            }
                            c.setForeground(new java.awt.Color(50, 50, 50));
                        } else {
                            c.setBackground(new java.awt.Color(184, 207, 229)); // Biru muda saat dipilih
                            c.setForeground(new java.awt.Color(0, 0, 0));
                        }
                        c.setFont(new java.awt.Font("Tahoma", 0, 11));
                        return c;
                    }
                }
                );
    }

// Method untuk setup table berdasarkan tab aktif
    private void setupTableBasedOnActiveTab() {
        switch (TabRawat.getSelectedIndex()) {
            case 0:
                setupTableDataAwal();
                break;
            case 1: // Anestesi Umum
                setupTableAnestesiUmumDataAwal();
                break;
            case 2: // Monitoring
                setupTableAnestesiUmumMonitoring();
                break;
            case 3: // Blokade Regional
                setupTableBlokadeRegional();
                break;
            case 4: // Keadaan Selama Operasi
                setupTableKeadaanOperasi();
                break;
            case 5: // Sectio Caesaria
                setupTableSectio();
                break;
            case 6: // Keadaan Pasca Anestesi di Ruang Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        setupTableMasukKamarPemulihan();
                        break;
                    case 1:
                        setupTableMonitoringPemulihan();
                        break;
                    case 2: // Score (model dibangun di buildScorePemulihan)
                        break;
                    default:
                        setupTableMasukKamarPemulihan();
                        break;
                }
                break;
            default:
                setupTableDataAwal();
                break;
        }
    }

    private void simpanMasukKamarPemulihan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_pasca_anestesi_masuk_kamar",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Pasca Anestesi Masuk Kamar", 16, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        Valid.SetTgl(MasukKamarPulih.getSelectedItem() + "") + " " + MasukKamarPulih.getSelectedItem().toString().substring(11, 19),
                        Kesadaran.getSelectedItem().toString(),
                        TensiMasukKamar.getText(),
                        NadiMasukKamar.getText(),
                        RrMasukKamar.getText(),
                        Support3.getText(),
                        Support4.getText(),
                        cmbRespirasiMasukKamar.getSelectedItem().toString(),
                        KanulMasukKamar.getSelectedItem().toString(),
                        O2MasukKamar.getText(),
                        Spo2MasukKamar.getText(),
                        SkorNyeri.getText(),
                        PenyulitIntraOperatif.getText()
                    }) == true) {
                emptTeks();
                tampilPascaAnestesiMasukKamar();
            }
        }
    }

    private void simpanMonitoringPemulihan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_pasca_anestesi_monitoring",
                    "?,?,?,?,?,?,?", "Pasca Anestesi Monitoring", 7, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        Suhu.getText(),
                        Rr4.getText(),
                        Nadi2.getText(),
                        TekananDarah.getText()
                    }) == true) {
                emptTeks();
                tampilPascaAnestesiMonitoring();
            }
        }
    }

    // ===== MISSING METHOD IMPLEMENTATIONS =====
// ===== ANESTESI UMUM METHODS =====
    private void tampilAnestesiUmumDataAwal() {
        if (tabModeAnestesiUmumDataAwal == null) {
            setupTableAnestesiUmumDataAwal();
        }

        Valid.tabelKosong(tabModeAnestesiUmumDataAwal);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oauda.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oauda.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oauda.kd_petugas, p.nama as nm_petugas, "
                    + "oauda.induksi, oauda.induksi_ket, oauda.pengaturan_nafas, "
                    + "oauda.tidal_volum, oauda.rr, oauda.ie_ratio, oauda.peep, "
                    + "oauda.pip, oauda.fio2, oauda.teknik_khusus, oauda.teknik_khusus_ket "
                    + "FROM catatan_anestesi_umum_data_awal oauda "
                    + "INNER JOIN reg_periksa rp ON oauda.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oauda.kd_petugas = p.nip "
                    + "WHERE oauda.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oauda.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeAnestesiUmumDataAwal.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("induksi") != null ? rs.getString("induksi") : "", // 9
                    rs.getString("induksi_ket") != null ? rs.getString("induksi_ket") : "", // 10
                    rs.getString("pengaturan_nafas") != null ? rs.getString("pengaturan_nafas") : "", // 11
                    rs.getString("tidal_volum") != null ? rs.getString("tidal_volum") : "", // 12
                    rs.getString("rr") != null ? rs.getString("rr") : "", // 13
                    rs.getString("ie_ratio") != null ? rs.getString("ie_ratio") : "", // 14 - DIPERBAIKI
                    rs.getString("peep") != null ? rs.getString("peep") : "", // 15
                    rs.getString("pip") != null ? rs.getString("pip") : "", // 16
                    rs.getString("fio2") != null ? rs.getString("fio2") : "", // 17
                    rs.getString("teknik_khusus") != null ? rs.getString("teknik_khusus") : "", // 18
                    rs.getString("teknik_khusus_ket") != null ? rs.getString("teknik_khusus_ket") : "" // 19
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilAnestesiUmumDataAwal: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilAnestesiUmumMonitoring() {
        if (tabModeAnestesiUmumMonitoring == null) {
            setupTableAnestesiUmumMonitoring();
        }

        Valid.tabelKosong(tabModeAnestesiUmumMonitoring);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oaum.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oaum.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oaum.kd_petugas, p.nama as nm_petugas, "
                    + "oaum.o2, oaum.n2o, oaum.air, oaum.volatile, oaum.ekg, oaum.spo2, "
                    + "oaum.infus, oaum.suhu, oaum.respirasi, oaum.tekanan_darah, oaum.nadi "
                    + "FROM catatan_anestesi_umum_monitoring oaum "
                    + "INNER JOIN reg_periksa rp ON oaum.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oaum.kd_petugas = p.nip "
                    + "WHERE oaum.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oaum.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeAnestesiUmumMonitoring.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("o2") != null ? rs.getString("o2") : "", // 9 - O2
                    rs.getString("n2o") != null ? rs.getString("n2o") : "", // 10 - N2O
                    rs.getString("air") != null ? rs.getString("air") : "", // 11 - Air
                    rs.getString("volatile") != null ? rs.getString("volatile") : "", // 12 - Volatile
                    rs.getString("ekg") != null ? rs.getString("ekg") : "", // 13 - EKG
                    rs.getString("spo2") != null ? rs.getString("spo2") : "", // 14 - Spo2
                    rs.getString("infus") != null ? rs.getString("infus") : "", // 15 - Infus
                    rs.getString("suhu") != null ? rs.getString("suhu") : "", // 16 - Suhu
                    rs.getString("respirasi") != null ? rs.getString("respirasi") : "", // 17 - Respirasi
                    rs.getString("tekanan_darah") != null ? rs.getString("tekanan_darah") : "", // 18 - Tensi
                    rs.getString("nadi") != null ? rs.getString("nadi") : "" // 19 - Nadi
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilAnestesiUmumMonitoring: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilAnestesiUmumKeterangan() {
        if (tabModeAnestesiUmumKeterangan == null) {
            setupTableAnestesiUmumKeterangan();
        }

        Valid.tabelKosong(tabModeAnestesiUmumKeterangan);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oauk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oauk.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oauk.kd_petugas, p.nama as nm_petugas, "
                    + "oauk.medikasi, oauk.pemberian_cairan, oauk.masalah_durante_operasi, oauk.tindakan "
                    + "FROM catatan_anestesi_umum_keterangan oauk "
                    + "INNER JOIN reg_periksa rp ON oauk.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oauk.kd_petugas = p.nip "
                    + "WHERE oauk.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oauk.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeAnestesiUmumKeterangan.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("medikasi") != null ? rs.getString("medikasi") : "", // 9
                    rs.getString("pemberian_cairan") != null ? rs.getString("pemberian_cairan") : "", // 10
                    rs.getString("masalah_durante_operasi") != null ? rs.getString("masalah_durante_operasi") : "", // 11
                    rs.getString("tindakan") != null ? rs.getString("tindakan") : "" // 12
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilAnestesiUmumKeterangan: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void setupTableAnestesiUmumKeterangan() {
        tabModeAnestesiUmumKeterangan = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Medikasi", "Pemberian Cairan", "Masalah Durante Operasi", "Tindakan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class
                    getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi11.setModel(tabModeAnestesiUmumKeterangan);

        // Atur lebar kolom
        if (tbAnestesi11.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi11.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi11.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi11.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi11.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi11.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi11.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi11.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi11.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi11.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik anestesi umum keterangan
            tbAnestesi11.getColumnModel().getColumn(9).setPreferredWidth(200);  // Medikasi
            tbAnestesi11.getColumnModel().getColumn(10).setPreferredWidth(200); // Pemberian Cairan
            tbAnestesi11.getColumnModel().getColumn(11).setPreferredWidth(250); // Masalah Durante Operasi
            tbAnestesi11.getColumnModel().getColumn(12).setPreferredWidth(200); // Tindakan
        }

        // Set table styling
        setTableStyling(tbAnestesi11);
    }

    private void tampilBlokadeRegional() {
        if (tabModeBlokadeRegional == null) {
            setupTableBlokadeRegional();
        }

        Valid.tabelKosong(tabModeBlokadeRegional);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT obr.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, obr.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "obr.kd_petugas, p.nama as nm_petugas, "
                    + "obr.teknik, obr.lokasi_tusukan, obr.analgesia_setinggi_segmen, "
                    + "obr.anestesi_lokal, obr.konsentrasi_jumlah, obr.obat_tambahan, "
                    + "obr.dosis, obr.vasokonstriktor, obr.konsentrasi, "
                    + "obr.waktu_mulai_suntik_jam, obr.analgesia_jam, obr.lamanya, "
                    + "obr.lamanya_menit_analgesi, obr.operasi_jam, obr.lamanya_jam_operasi, "
                    + "obr.lamanya_jam_menit, obr.tindakan_anestesi_tambahan "
                    + "FROM catatan_anestesi_blokade_regional obr "
                    + "INNER JOIN reg_periksa rp ON obr.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON obr.kd_petugas = p.nip "
                    + "WHERE obr.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY obr.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeBlokadeRegional.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("teknik") != null ? rs.getString("teknik") : "", // 9
                    rs.getString("lokasi_tusukan") != null ? rs.getString("lokasi_tusukan") : "", // 10
                    rs.getString("analgesia_setinggi_segmen") != null ? rs.getString("analgesia_setinggi_segmen") : "", // 11
                    rs.getString("anestesi_lokal") != null ? rs.getString("anestesi_lokal") : "", // 12
                    rs.getString("konsentrasi_jumlah") != null ? rs.getString("konsentrasi_jumlah") : "", // 13
                    rs.getString("obat_tambahan") != null ? rs.getString("obat_tambahan") : "", // 14
                    rs.getString("dosis") != null ? rs.getString("dosis") : "", // 15
                    rs.getString("vasokonstriktor") != null ? rs.getString("vasokonstriktor") : "", // 16
                    rs.getString("konsentrasi") != null ? rs.getString("konsentrasi") : "", // 17
                    rs.getString("waktu_mulai_suntik_jam") != null ? rs.getString("waktu_mulai_suntik_jam") : "", // 18
                    rs.getString("analgesia_jam") != null ? rs.getString("analgesia_jam") : "", // 19
                    rs.getString("lamanya") != null ? rs.getString("lamanya") : "", // 20
                    rs.getString("lamanya_menit_analgesi") != null ? rs.getString("lamanya_menit_analgesi") : "", // 21
                    rs.getString("operasi_jam") != null ? rs.getString("operasi_jam") : "", // 22
                    rs.getString("lamanya_jam_operasi") != null ? rs.getString("lamanya_jam_operasi") : "", // 23
                    rs.getString("lamanya_jam_menit") != null ? rs.getString("lamanya_jam_menit") : "", // 24
                    rs.getString("tindakan_anestesi_tambahan") != null ? rs.getString("tindakan_anestesi_tambahan") : "" // 25
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilBlokadeRegional: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilKeadaanOperasi() {
        if (tabModeKeadaanOperasi == null) {
            setupTableKeadaanOperasi();
        }

        Valid.tabelKosong(tabModeKeadaanOperasi);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oko.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oko.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oko.kd_petugas, p.nama as nm_petugas, "
                    + "oko.letak_penderita, oko.airway, oko.ukuran, oko.balon, "
                    + "oko.lama_operasi, oko.lama_anestesi, oko.kristaloid, oko.perdarahan, "
                    + "oko.koloid, oko.diuresis, oko.darah, oko.cairan_lain, oko.komponen_darah "
                    + "FROM catatan_anestesi_keadaan_operasi oko "
                    + "INNER JOIN reg_periksa rp ON oko.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oko.kd_petugas = p.nip "
                    + "WHERE oko.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oko.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeKeadaanOperasi.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("letak_penderita") != null ? rs.getString("letak_penderita") : "", // 9
                    rs.getString("airway") != null ? rs.getString("airway") : "", // 10
                    rs.getString("ukuran") != null ? rs.getString("ukuran") : "", // 11
                    rs.getString("balon") != null ? rs.getString("balon") : "", // 12
                    rs.getString("lama_operasi") != null ? rs.getString("lama_operasi") : "", // 13
                    rs.getString("lama_anestesi") != null ? rs.getString("lama_anestesi") : "", // 14
                    rs.getString("kristaloid") != null ? rs.getString("kristaloid") : "", // 15
                    rs.getString("perdarahan") != null ? rs.getString("perdarahan") : "", // 16
                    rs.getString("koloid") != null ? rs.getString("koloid") : "", // 17
                    rs.getString("diuresis") != null ? rs.getString("diuresis") : "", // 18
                    rs.getString("darah") != null ? rs.getString("darah") : "", // 19
                    rs.getString("cairan_lain") != null ? rs.getString("cairan_lain") : "", // 20
                    rs.getString("komponen_darah") != null ? rs.getString("komponen_darah") : "" // 21
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilKeadaanOperasi: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void setupTableKeadaanOperasi() {
        tabModeKeadaanOperasi = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Letak Penderita", "Airway", "Ukuran", "Balon",
            "Lama Operasi", "Lama Anestesi", "Kristaloid", "Perdarahan",
            "Koloid", "Diuresis", "Darah", "Cairan Lain", "Komponen Darah"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi3.setModel(tabModeKeadaanOperasi);

        // Atur lebar kolom
        if (tbAnestesi3.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi3.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi3.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi3.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi3.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi3.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi3.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi3.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi3.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi3.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik keadaan operasi
            tbAnestesi3.getColumnModel().getColumn(9).setPreferredWidth(120);  // Letak Penderita
            tbAnestesi3.getColumnModel().getColumn(10).setPreferredWidth(120); // Airway
            tbAnestesi3.getColumnModel().getColumn(11).setPreferredWidth(80);  // Ukuran
            tbAnestesi3.getColumnModel().getColumn(12).setPreferredWidth(100); // Balon
            tbAnestesi3.getColumnModel().getColumn(13).setPreferredWidth(100); // Lama Operasi
            tbAnestesi3.getColumnModel().getColumn(14).setPreferredWidth(100); // Lama Anestesi
            tbAnestesi3.getColumnModel().getColumn(15).setPreferredWidth(80);  // Kristaloid
            tbAnestesi3.getColumnModel().getColumn(16).setPreferredWidth(80);  // Perdarahan
            tbAnestesi3.getColumnModel().getColumn(17).setPreferredWidth(80);  // Koloid
            tbAnestesi3.getColumnModel().getColumn(18).setPreferredWidth(80);  // Diuresis
            tbAnestesi3.getColumnModel().getColumn(19).setPreferredWidth(80);  // Darah
            tbAnestesi3.getColumnModel().getColumn(20).setPreferredWidth(100); // Cairan Lain
            tbAnestesi3.getColumnModel().getColumn(21).setPreferredWidth(120); // Komponen Darah
        }

        // Set table styling
        setTableStyling(tbAnestesi3);
    }

    private void tampilSectio() {
        if (tabModeSectio == null) {
            setupTableSectio();
        }

        Valid.tabelKosong(tabModeSectio);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oasc.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oasc.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oasc.kd_petugas, p.nama as nm_petugas, "
                    + "oasc.keadaan_bayi, oasc.jenis_kelamin, oasc.apgar_1_menit, "
                    + "oasc.apgar_5_menit, oasc.apgar_10_menit "
                    + "FROM catatan_anestesi_sectio_caesaria oasc "
                    + "INNER JOIN reg_periksa rp ON oasc.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oasc.kd_petugas = p.nip "
                    + "WHERE oasc.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oasc.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModeSectio.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("keadaan_bayi") != null ? rs.getString("keadaan_bayi") : "", // 9
                    rs.getString("jenis_kelamin") != null ? rs.getString("jenis_kelamin") : "", // 10
                    rs.getString("apgar_1_menit") != null ? rs.getString("apgar_1_menit") : "", // 11
                    rs.getString("apgar_5_menit") != null ? rs.getString("apgar_5_menit") : "", // 12
                    rs.getString("apgar_10_menit") != null ? rs.getString("apgar_10_menit") : "" // 13
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilSectio: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void tampilPascaAnestesiMasukKamar() {
        System.out.println("=== tampilPascaAnestesiMasukKamar dipanggil ===");
        if (tabModePascaAnestesiMasukKamar == null) {
            setupTablePascaAnestesiMasukKamar();
        }

        Valid.tabelKosong(tabModePascaAnestesiMasukKamar);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oasmk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oasmk.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oasmk.kd_petugas, p.nama as nm_petugas, "
                    + "oasmk.jam_masuk_kamar_pulih, oasmk.kesadaran, oasmk.tensi, "
                    + "oasmk.nadi, oasmk.rr, oasmk.support1, oasmk.support2, "
                    + "oasmk.respirasi, oasmk.kanul_nasal, oasmk.o2, "
                    + "oasmk.spo2, oasmk.skor_nyeri, oasmk.penyulit_intra_operasi "
                    + "FROM catatan_anestesi_pasca_anestesi_masuk_kamar oasmk "
                    + "INNER JOIN reg_periksa rp ON oasmk.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oasmk.kd_petugas = p.nip "
                    + "WHERE oasmk.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oasmk.tanggal DESC"
            );

            String tglAwal = Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00";
            String tglAkhir = Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59";
            System.out.println("Filter Tanggal: " + tglAwal + " sampai " + tglAkhir);

            ps.setString(1, tglAwal);
            ps.setString(2, tglAkhir);

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                System.out.println("Filter Cari: " + TCari.getText());
            }

            rs = ps.executeQuery();

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                tabModePascaAnestesiMasukKamar.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("jam_masuk_kamar_pulih") != null ? rs.getString("jam_masuk_kamar_pulih") : "", // 9
                    rs.getString("kesadaran") != null ? rs.getString("kesadaran") : "", // 10
                    rs.getString("tensi") != null ? rs.getString("tensi") : "", // 11
                    rs.getString("nadi") != null ? rs.getString("nadi") : "", // 12
                    rs.getString("rr") != null ? rs.getString("rr") : "", // 13
                    rs.getString("support1") != null ? rs.getString("support1") : "", // 14
                    rs.getString("support2") != null ? rs.getString("support2") : "", // 15
                    rs.getString("respirasi") != null ? rs.getString("respirasi") : "", // 16
                    rs.getString("kanul_nasal") != null ? rs.getString("kanul_nasal") : "", // 17
                    rs.getString("o2") != null ? rs.getString("o2") : "", // 18
                    rs.getString("spo2") != null ? rs.getString("spo2") : "", // 19
                    rs.getString("skor_nyeri") != null ? rs.getString("skor_nyeri") : "", // 20
                    rs.getString("penyulit_intra_operasi") != null ? rs.getString("penyulit_intra_operasi") : "" // 21
                });
            }

            System.out.println("Total data ditemukan: " + rowCount);

        } catch (Exception e) {
            System.out.println("Error tampilPascaAnestesiMasukKamar: " + e);
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void setupTablePascaAnestesiMasukKamar() {
        tabModePascaAnestesiMasukKamar = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Jam Masuk", "Kesadaran", "Tensi", "Nadi", "RR",
            "Support 1", "Support 2", "Respirasi", "Kanul Nasal",
            "O2", "SpO2", "Skor Nyeri", "Penyulit Intraoperatif"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi5.setModel(tabModePascaAnestesiMasukKamar);

        // Atur lebar kolom
        if (tbAnestesi5.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi5.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi5.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi5.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi5.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi5.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi5.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi5.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi5.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi5.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik masuk kamar pemulihan
            tbAnestesi5.getColumnModel().getColumn(9).setPreferredWidth(130);  // Jam Masuk
            tbAnestesi5.getColumnModel().getColumn(10).setPreferredWidth(100); // Kesadaran
            tbAnestesi5.getColumnModel().getColumn(11).setPreferredWidth(80);  // Tensi
            tbAnestesi5.getColumnModel().getColumn(12).setPreferredWidth(80);  // Nadi
            tbAnestesi5.getColumnModel().getColumn(13).setPreferredWidth(60);  // RR
            tbAnestesi5.getColumnModel().getColumn(14).setPreferredWidth(100); // Support 1
            tbAnestesi5.getColumnModel().getColumn(15).setPreferredWidth(100); // Support 2
            tbAnestesi5.getColumnModel().getColumn(16).setPreferredWidth(100); // Respirasi
            tbAnestesi5.getColumnModel().getColumn(17).setPreferredWidth(100); // Kanul Nasal
            tbAnestesi5.getColumnModel().getColumn(18).setPreferredWidth(60);  // O2
            tbAnestesi5.getColumnModel().getColumn(19).setPreferredWidth(60);  // SpO2
            tbAnestesi5.getColumnModel().getColumn(20).setPreferredWidth(80);  // Skor Nyeri
            tbAnestesi5.getColumnModel().getColumn(21).setPreferredWidth(200); // Penyulit Intraoperatif
        }

        // Set table styling
        setTableStyling(tbAnestesi5);
    }

    private void tampilMonitoringPemulihan() {
        if (tabModePascaAnestesiMonitoring == null) {
            setupTablePascaAnestesiMonitoring();
        }
        tbAnestesi6.setModel(tabModePascaAnestesiMonitoring);

        Valid.tabelKosong(tabModePascaAnestesiMonitoring);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oasm.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oasm.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oasm.kd_petugas, p.nama as nm_petugas, "
                    + "oasm.suhu, oasm.rr, oasm.nadi, oasm.tekanan_darah "
                    + "FROM catatan_anestesi_pasca_anestesi_monitoring oasm "
                    + "INNER JOIN reg_periksa rp ON oasm.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oasm.kd_petugas = p.nip "
                    + "WHERE oasm.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oasm.tanggal DESC"
            );

            // Set parameter tanggal
            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            // Set parameter pencarian jika ada
            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            // Loop untuk menampilkan data
            while (rs.next()) {
                tabModePascaAnestesiMonitoring.addRow(new String[]{
                    rs.getString("no_rawat"), // 0 - No.Rawat
                    rs.getString("no_rkm_medis"), // 1 - No.RM
                    rs.getString("nm_pasien"), // 2 - Nama Pasien
                    rs.getString("tgl_lahir"), // 3 - Tgl.Lahir
                    rs.getString("jk"), // 4 - J.K.
                    rs.getString("tanggal"), // 5 - Tanggal
                    rs.getString("nm_bangsal"), // 6 - Ruangan
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7 - Kode Petugas
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8 - Nama Petugas
                    rs.getString("suhu") != null ? rs.getString("suhu") : "", // 9 - Suhu
                    rs.getString("rr") != null ? rs.getString("rr") : "", // 10 - RR
                    rs.getString("nadi") != null ? rs.getString("nadi") : "", // 11 - Nadi
                    rs.getString("tekanan_darah") != null ? rs.getString("tekanan_darah") : "" // 12 - Tekanan Darah
                });
            }

            // Set status label (opsional)
            //    LCount.setText("" + tabModePascaAnestesiMonitoring.getRowCount());
        } catch (Exception e) {
            System.out.println("Error tampilMonitoringPemulihan: " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saat menampilkan data monitoring pemulihan: " + e.getMessage());
        } finally {
            // Cleanup resources
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    System.out.println("Error closing ResultSet: " + e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    private void setupTableAnestesiUmumDataAwal() {
        tabModeAnestesiUmumDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Induksi", "Induksi Ket", "Pengaturan Nafas", "Tidal Volum",
            "RR", "IE Ratio", "PEEP", "PIP", "FiO2",
            "Teknik Khusus", "Teknik Khusus Ket"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbAnestesi9.setModel(tabModeAnestesiUmumDataAwal);
        setTableStyling(tbAnestesi9);
    }

    private void setupTableAnestesiUmumMonitoring() {
        tabModeAnestesiUmumMonitoring = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "O2", "N2O", "Air", "Volatile", "EKG", "Spo2", "Infus",
            "Suhu", "Respirasi", "Tensi", "Nadi"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class
                    getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi10.setModel(tabModeAnestesiUmumMonitoring);

        // Atur lebar kolom
        if (tbAnestesi10.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi10.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi10.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi10.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi10.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi10.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi10.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi10.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi10.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi10.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom monitoring sesuai urutan baru
            tbAnestesi10.getColumnModel().getColumn(9).setPreferredWidth(60);   // O2
            tbAnestesi10.getColumnModel().getColumn(10).setPreferredWidth(60);  // N2O
            tbAnestesi10.getColumnModel().getColumn(11).setPreferredWidth(60);  // Air
            tbAnestesi10.getColumnModel().getColumn(12).setPreferredWidth(80);  // Volatile
            tbAnestesi10.getColumnModel().getColumn(13).setPreferredWidth(80);  // EKG
            tbAnestesi10.getColumnModel().getColumn(14).setPreferredWidth(60);  // Spo2
            tbAnestesi10.getColumnModel().getColumn(15).setPreferredWidth(80);  // Infus
            tbAnestesi10.getColumnModel().getColumn(16).setPreferredWidth(60);  // Suhu
            tbAnestesi10.getColumnModel().getColumn(17).setPreferredWidth(80);  // Respirasi
            tbAnestesi10.getColumnModel().getColumn(18).setPreferredWidth(80);  // Tensi
            tbAnestesi10.getColumnModel().getColumn(19).setPreferredWidth(60);  // Nadi
        }

        // Set table styling
        setTableStyling(tbAnestesi10);
    }

    private void setupTableBlokadeRegional() {
        tabModeBlokadeRegional = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Teknik", "Lokasi Tusukan", "Analgesia Segmen", "Anestesi Lokal",
            "Konsentrasi Jumlah", "Obat Tambahan", "Dosis", "Vasokonstriktor",
            "Konsentrasi", "Waktu Mulai", "Analgesia Jam", "Lamanya",
            "Lamanya Menit", "Operasi Jam", "Lamanya Jam", "Lamanya Menit",
            "Tindakan Tambahan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class
                    getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi12.setModel(tabModeBlokadeRegional);

        // Atur lebar kolom
        if (tbAnestesi12.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi12.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi12.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi12.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi12.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi12.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi12.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi12.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi12.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi12.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik blokade regional
            tbAnestesi12.getColumnModel().getColumn(9).setPreferredWidth(100);  // Teknik
            tbAnestesi12.getColumnModel().getColumn(10).setPreferredWidth(120); // Lokasi Tusukan
            tbAnestesi12.getColumnModel().getColumn(11).setPreferredWidth(120); // Analgesia Segmen
            tbAnestesi12.getColumnModel().getColumn(12).setPreferredWidth(120); // Anestesi Lokal
            tbAnestesi12.getColumnModel().getColumn(13).setPreferredWidth(120); // Konsentrasi Jumlah
            tbAnestesi12.getColumnModel().getColumn(14).setPreferredWidth(120); // Obat Tambahan
            tbAnestesi12.getColumnModel().getColumn(15).setPreferredWidth(80);  // Dosis
            tbAnestesi12.getColumnModel().getColumn(16).setPreferredWidth(100); // Vasokonstriktor
            tbAnestesi12.getColumnModel().getColumn(17).setPreferredWidth(80);  // Konsentrasi
            tbAnestesi12.getColumnModel().getColumn(18).setPreferredWidth(100); // Waktu Mulai
            tbAnestesi12.getColumnModel().getColumn(19).setPreferredWidth(100); // Analgesia Jam
            tbAnestesi12.getColumnModel().getColumn(20).setPreferredWidth(80);  // Lamanya
            tbAnestesi12.getColumnModel().getColumn(21).setPreferredWidth(100); // Lamanya Menit
            tbAnestesi12.getColumnModel().getColumn(22).setPreferredWidth(100); // Operasi Jam
            tbAnestesi12.getColumnModel().getColumn(23).setPreferredWidth(100); // Lamanya Jam
            tbAnestesi12.getColumnModel().getColumn(24).setPreferredWidth(100); // Lamanya Menit
            tbAnestesi12.getColumnModel().getColumn(25).setPreferredWidth(150); // Tindakan Tambahan
        }

        // Set table styling
        setTableStyling(tbAnestesi12);
    }

    private void setupTableSectio() {
        tabModeSectio = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Keadaan Bayi", "Jenis Kelamin", "Apgar 1 Menit",
            "Apgar 5 Menit", "Apgar 10 Menit"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi4.setModel(tabModeSectio);

        // Atur lebar kolom
        if (tbAnestesi4.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi4.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi4.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi4.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi4.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi4.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi4.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi4.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi4.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi4.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik section caesaria
            tbAnestesi4.getColumnModel().getColumn(9).setPreferredWidth(100);  // Keadaan Bayi
            tbAnestesi4.getColumnModel().getColumn(10).setPreferredWidth(100); // Jenis Kelamin
            tbAnestesi4.getColumnModel().getColumn(11).setPreferredWidth(100); // Apgar 1 Menit
            tbAnestesi4.getColumnModel().getColumn(12).setPreferredWidth(100); // Apgar 5 Menit
            tbAnestesi4.getColumnModel().getColumn(13).setPreferredWidth(100); // Apgar 10 Menit
        }

        // Set table styling
        setTableStyling(tbAnestesi4);
    }

    private void setupTableMasukKamarPemulihan() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Jam Masuk", "Kesadaran", "Tensi", "Nadi", "Support 1", "Support 2",
            "Respirasi", "RR", "Kanul Nasal", "O2", "SpO2", "Skor Nyeri",
            "Penyulit Intraoperatif", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi5.setModel(tabModeDataAwal);
        setTableStyling(tbAnestesi5);
    }

    private void setupTableMonitoringPemulihan() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Suhu", "RR", "Nadi", "Tekanan Darah", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi6.setModel(tabModeDataAwal);
        setTableStyling(tbAnestesi6);
    }

// ===== CRUD GANTI/HAPUS METHODS =====
    private void gantiAnestesiUmumMonitoring() {
        if (tbAnestesi10.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_umum_monitoring", "no_rawat=? and tanggal=?",
                    "o2=?,n2o=?,air=?,volatile=?,sevofluran=?,infus=?,suhu=?,"
                    + "respirasi=?,nadi=?,tekanan_darah=?,spo2=?,ekg=?,lain_lain=?",
                    15, new String[]{
                        PernapasanMasuk12.getText(), // o2
                        PernapasanMasuk14.getText(), // n2o
                        PernapasanMasuk16.getText(), // air
                        Lainnya2.getText(), // volatile
                        SirkulasiMasuk19.getText(), // sevofluran
                        SirkulasiMasuk14.getText(), // infus
                        SirkulasiMasuk21.getText(), // suhu
                        SirkulasiMasuk22.getText(), // respirasi
                        PemantauanNadi2.getText(), // nadi
                        Selama2.getText(), // tekanan_darah
                        SirkulasiMasuk18.getText(), // spo2
                        "", // ekg
                        "", // lain_lain
                        tbAnestesi10.getValueAt(tbAnestesi10.getSelectedRow(), 0).toString(),
                        tbAnestesi10.getValueAt(tbAnestesi10.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilAnestesiUmumMonitoring();
                emptTeks();
            }
        }
    }

    private void gantiAnestesiUmumKeterangan() {
        if (tbAnestesi11.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_umum_keterangan", "no_rawat=? and tanggal=?",
                    "medikasi=?,pemberian_cairan=?,masalah_durante_operasi=?,tindakan=?",
                    6, new String[]{
                        Medikasi.getText(),
                        PemberianCairan.getText(),
                        MasalahDuranteOperasi.getText(),
                        Tindakan.getText(),
                        tbAnestesi11.getValueAt(tbAnestesi11.getSelectedRow(), 0).toString(),
                        tbAnestesi11.getValueAt(tbAnestesi11.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilAnestesiUmumKeterangan();
                emptTeks();
            }
        }
    }

    private void gantiBlokadeRegional() {
        if (tbAnestesi12.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else if (KdPetugas.getText().trim().equals("")) {
                Valid.textKosong(KdPetugas, "Petugas");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_blokade_regional",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,teknik=?,lokasi_tusukan=?,analgesia_setinggi_segmen=?,anestesi_lokal=?,"
                        + "konsentrasi_jumlah=?,obat_tambahan=?,dosis=?,vasokonstriktor=?,konsentrasi=?,"
                        + "waktu_mulai_suntik_jam=?,analgesia_jam=?,lamanya=?,lamanya_menit_analgesi=?,"
                        + "operasi_jam=?,lamanya_jam_operasi=?,lamanya_jam_menit=?,tindakan_anestesi_tambahan=?",
                        20, new String[]{
                            KdPetugas.getText(),
                            Teknik.getSelectedItem().toString(),
                            LokasiTusukan.getText(),
                            AnalgesiSetinggiSegmen.getText(),
                            AnestesiLokal.getText(),
                            KonsentrasiJumlah.getText(),
                            ObatTambahan.getText(),
                            Dosis.getText(),
                            Vasokonstriktor.getSelectedItem().toString(),
                            Konsentrasi.getText(),
                            WaktuMulaiSuntikJam.getText(),
                            AnalgesiJam.getText(),
                            Lamanya.getText(),
                            LamanyaMenitAnalgesi.getText(),
                            OperasiJam.getText(),
                            LamanyajamOperasi.getText(),
                            LamanyaJamMenit.getText(),
                            TindakanAnestesiTambahan.getText(),
                            tbAnestesi12.getValueAt(tbAnestesi12.getSelectedRow(), 0).toString(),
                            tbAnestesi12.getValueAt(tbAnestesi12.getSelectedRow(), 5).toString()
                        }) == true) {
                    emptTeks();
                    tampilBlokadeRegional();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private void gantiKeadaanOperasi() {
        if (tbAnestesi3.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_keadaan_operasi",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,letak_penderita=?,airway=?,ukuran=?,balon=?,"
                        + "lama_operasi=?,lama_anestesi=?,kristaloid=?,perdarahan=?,"
                        + "koloid=?,diuresis=?,darah=?,cairan_lain=?,komponen_darah=?",
                        16, new String[]{
                            KdPetugas.getText(),
                            LetakPenderita.getSelectedItem().toString(),
                            Airway2.getSelectedItem().toString(),
                            Ukuran.getText(),
                            BalonNoBalon.getSelectedItem().toString(),
                            LamaOperasi.getText(),
                            LamaAnestesi.getText(),
                            Kristaloid.getText(),
                            Perdarahan.getText(),
                            Koloid.getText(),
                            Diuresis.getText(),
                            Darah.getText(),
                            CairanLain.getText(),
                            KomponenDarah.getText(),
                            tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 0).toString(), // no_rawat lama
                            tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 5).toString() // tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilKeadaanOperasi();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private void gantiSectio() {
        if (tbAnestesi4.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_sectio_caesaria",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,keadaan_bayi=?,jenis_kelamin=?,apgar_1_menit=?,apgar_5_menit=?,apgar_10_menit=?",
                        8, new String[]{
                            KdPetugas.getText(),
                            KeadaanBayi.getSelectedItem().toString(),
                            JenisKelamin.getSelectedItem().toString(),
                            Apgrar1Menit.getText(),
                            Apgar5Menit.getText(),
                            Apgar10Menit.getText(),
                            tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(), // no_rawat lama
                            tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 5).toString() // tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilSectio();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private void gantiMasukKamarPemulihan() {
        if (tbAnestesi5.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_pasca_anestesi_masuk_kamar",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,jam_masuk_kamar_pulih=?,kesadaran=?,tensi=?,nadi=?,"
                        + "rr=?,support1=?,support2=?,respirasi=?,kanul_nasal=?,"
                        + "o2=?,spo2=?,skor_nyeri=?,penyulit_intra_operasi=?",
                        16, new String[]{
                            KdPetugas.getText(),
                            Valid.SetTgl(MasukKamarPulih.getSelectedItem() + "") + " " + MasukKamarPulih.getSelectedItem().toString().substring(11, 19),
                            Kesadaran.getSelectedItem().toString(),
                            TensiMasukKamar.getText(),
                            NadiMasukKamar.getText(),
                            RrMasukKamar.getText(),
                            Support3.getText(),
                            Support4.getText(),
                            cmbRespirasiMasukKamar.getSelectedItem().toString(),
                            KanulMasukKamar.getSelectedItem().toString(),
                            O2MasukKamar.getText(),
                            Spo2MasukKamar.getText(),
                            SkorNyeri.getText(),
                            PenyulitIntraOperatif.getText(),
                            tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 0).toString(), // no_rawat lama
                            tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 5).toString() // tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilPascaAnestesiMasukKamar();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

    private void gantiMonitoringPemulihan() {
        if (tbAnestesi6.getSelectedRow() != -1) {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "No.Rawat");
            } else {
                if (Sequel.mengedittf("catatan_anestesi_pasca_anestesi_monitoring",
                        "no_rawat=? and tanggal=?",
                        "kd_petugas=?,suhu=?,rr=?,nadi=?,tekanan_darah=?",
                        7, new String[]{
                            KdPetugas.getText(),
                            Suhu.getText(),
                            Rr4.getText(),
                            Nadi2.getText(),
                            TekananDarah.getText(),
                            tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(), // no_rawat lama
                            tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString() // tanggal lama
                        }) == true) {
                    emptTeks();
                    tampilPascaAnestesiMonitoring();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diedit..!!");
        }
    }

// ===== HAPUS METHODS =====
    private void hapusAnestesiUmumMonitoring() {
        if (tbAnestesi10.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_umum_monitoring where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi10.getValueAt(tbAnestesi10.getSelectedRow(), 0).toString(),
                tbAnestesi10.getValueAt(tbAnestesi10.getSelectedRow(), 5).toString()
            }) == true) {
                tampilAnestesiUmumMonitoring();
                emptTeks();
            }
        }
    }

    private void hapusAnestesiUmumKeterangan() {
        if (tbAnestesi11.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_umum_keterangan where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi11.getValueAt(tbAnestesi11.getSelectedRow(), 0).toString(),
                tbAnestesi11.getValueAt(tbAnestesi11.getSelectedRow(), 5).toString()
            }) == true) {
                tampilAnestesiUmumKeterangan();
                emptTeks();
            }
        }
    }

    private void hapusBlokadeRegional() {
        if (tbAnestesi12.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_blokade_regional where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi12.getValueAt(tbAnestesi12.getSelectedRow(), 0).toString(),
                        tbAnestesi12.getValueAt(tbAnestesi12.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilBlokadeRegional();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusKeadaanOperasi() {
        if (tbAnestesi3.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_keadaan_operasi where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 0).toString(),
                        tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilKeadaanOperasi();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusSectio() {
        if (tbAnestesi4.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_sectio_caesaria where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilSectio();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusMasukKamarPemulihan() {
        if (tbAnestesi5.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_pasca_anestesi_masuk_kamar where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 0).toString(),
                        tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilPascaAnestesiMasukKamar();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusMonitoringPemulihan() {
        if (tbAnestesi6.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_pasca_anestesi_monitoring where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(),
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilPascaAnestesiMonitoring();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void tampilPascaAnestesiMonitoring() {
        if (tabModePascaAnestesiMonitoring == null) {
            setupTablePascaAnestesiMonitoring();
        }
        tbAnestesi6.setModel(tabModePascaAnestesiMonitoring);

        Valid.tabelKosong(tabModePascaAnestesiMonitoring);

        try {
            ps = koneksi.prepareStatement(
                    "SELECT oasm.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oasm.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oasm.kd_petugas, p.nama as nm_petugas, "
                    + "oasm.suhu, oasm.rr, oasm.nadi, oasm.tekanan_darah "
                    + "FROM catatan_anestesi_pasca_anestesi_monitoring oasm "
                    + "INNER JOIN reg_periksa rp ON oasm.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oasm.kd_petugas = p.nip "
                    + "WHERE oasm.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? ""
                    : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
                    + "ORDER BY oasm.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");

            if (!TCari.getText().trim().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                tabModePascaAnestesiMonitoring.addRow(new String[]{
                    rs.getString("no_rawat"), // 0
                    rs.getString("no_rkm_medis"), // 1
                    rs.getString("nm_pasien"), // 2
                    rs.getString("tgl_lahir"), // 3
                    rs.getString("jk"), // 4
                    rs.getString("tanggal"), // 5
                    rs.getString("nm_bangsal"), // 6
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "", // 7
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "", // 8
                    rs.getString("suhu") != null ? rs.getString("suhu") : "", // 9
                    rs.getString("rr") != null ? rs.getString("rr") : "", // 10
                    rs.getString("nadi") != null ? rs.getString("nadi") : "", // 11
                    rs.getString("tekanan_darah") != null ? rs.getString("tekanan_darah") : "" // 12
                });
            }

        } catch (Exception e) {
            System.out.println("Error tampilPascaAnestesiMonitoring: " + e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void setupTablePascaAnestesiMonitoring() {
        tabModePascaAnestesiMonitoring = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Kode Petugas", "Nama Petugas",
            "Suhu", "RR", "Nadi", "Tekanan Darah"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi6.setModel(tabModePascaAnestesiMonitoring);

        // Atur lebar kolom
        if (tbAnestesi6.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi6.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi6.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi6.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi6.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi6.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi6.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi6.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi6.getColumnModel().getColumn(7).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi6.getColumnModel().getColumn(8).setPreferredWidth(150);  // Nama Petugas

            // Kolom spesifik monitoring pemulihan
            tbAnestesi6.getColumnModel().getColumn(9).setPreferredWidth(80);   // Suhu
            tbAnestesi6.getColumnModel().getColumn(10).setPreferredWidth(80);  // RR
            tbAnestesi6.getColumnModel().getColumn(11).setPreferredWidth(80);  // Nadi
            tbAnestesi6.getColumnModel().getColumn(12).setPreferredWidth(120); // Tekanan Darah
        }

        // Set table styling
        setTableStyling(tbAnestesi6);
    }

    private void simpanAnestesiUmumMonitoring() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_umum_monitoring",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Anestesi Umum Monitoring", 16, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdPetugas.getText(),
                        PernapasanMasuk12.getText(), // O2
                        PernapasanMasuk14.getText(), // N2O
                        PernapasanMasuk16.getText(), // Air
                        Lainnya2.getText(), // Volatile
                        SirkulasiMasuk19.getText(), // Sevofluran
                        SirkulasiMasuk14.getText(), // Infus
                        SirkulasiMasuk21.getText(), // Suhu
                        SirkulasiMasuk22.getText(), // Respirasi
                        PemantauanNadi2.getText(), // Nadi
                        Selama2.getText(), // Tekanan Darah
                        SirkulasiMasuk18.getText(), // SpO2
                        "", // EKG
                        "" // Lain-lain
                    }) == true) {
                emptTeks();
                tampilAnestesiUmumMonitoring();
            }
        }
    }

// contoh untuk satu tab, ulangi untuk semua tab sesuai struktur table Anda
    private void setupTableDataAwal() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.",
            "Tanggal", "Ruangan", "Tgl.Masuk RS",
            "Kode Petugas", "Nama Petugas",
            "Kode Dokter Anestesi", "Nama Dokter Anestesi",
            "Kode Asisten Anestesi", "Nama Asisten Anestesi",
            "Kode Dokter Bedah", "Nama Dokter Bedah",
            "Kode Asisten Bedah", "Nama Asisten Bedah",
            "Medikasi Prabedah", "Jenis Anestesi", "Diagnosa Prabedah",
            "Informed Consent", "Obat Anestesi", "Tatalaksana Jalan Nafas",
            "Mesin Anestesi", "Obat Emergensi", "Check Monitoring",
            "Suction Aparatus"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }

            @Override
            public Class
                    getColumnClass(int column) {
                return String.class;
            }
        };

        tbAnestesi.setModel(tabModeDataAwal);

        // Atur lebar kolom dengan detail
        if (tbAnestesi.getColumnModel().getColumnCount() > 0) {
            // Kolom utama pasien
            tbAnestesi.getColumnModel().getColumn(0).setPreferredWidth(105);  // No.Rawat
            tbAnestesi.getColumnModel().getColumn(1).setPreferredWidth(75);   // No.RM
            tbAnestesi.getColumnModel().getColumn(2).setPreferredWidth(200);  // Nama Pasien
            tbAnestesi.getColumnModel().getColumn(3).setPreferredWidth(80);   // Tgl.Lahir
            tbAnestesi.getColumnModel().getColumn(4).setPreferredWidth(60);   // J.K.
            tbAnestesi.getColumnModel().getColumn(5).setPreferredWidth(130);  // Tanggal
            tbAnestesi.getColumnModel().getColumn(6).setPreferredWidth(120);  // Ruangan
            tbAnestesi.getColumnModel().getColumn(7).setPreferredWidth(100);  // Tgl.Masuk RS

            // Kolom petugas dan dokter
            tbAnestesi.getColumnModel().getColumn(8).setPreferredWidth(80);   // Kode Petugas
            tbAnestesi.getColumnModel().getColumn(9).setPreferredWidth(150);  // Nama Petugas
            tbAnestesi.getColumnModel().getColumn(10).setPreferredWidth(100); // Kode Dokter Anestesi
            tbAnestesi.getColumnModel().getColumn(11).setPreferredWidth(150); // Nama Dokter Anestesi
            tbAnestesi.getColumnModel().getColumn(12).setPreferredWidth(100); // Kode Asisten Anestesi
            tbAnestesi.getColumnModel().getColumn(13).setPreferredWidth(150); // Nama Asisten Anestesi
            tbAnestesi.getColumnModel().getColumn(14).setPreferredWidth(100); // Kode Dokter Bedah
            tbAnestesi.getColumnModel().getColumn(15).setPreferredWidth(150); // Nama Dokter Bedah
            tbAnestesi.getColumnModel().getColumn(16).setPreferredWidth(100); // Kode Asisten Bedah
            tbAnestesi.getColumnModel().getColumn(17).setPreferredWidth(150); // Nama Asisten Bedah

            // Kolom data medis
            tbAnestesi.getColumnModel().getColumn(18).setPreferredWidth(150); // Medikasi Prabedah
            tbAnestesi.getColumnModel().getColumn(19).setPreferredWidth(120); // Jenis Anestesi
            tbAnestesi.getColumnModel().getColumn(20).setPreferredWidth(150); // Diagnosa Prabedah

            // Kolom pilihan/kombo
            tbAnestesi.getColumnModel().getColumn(21).setPreferredWidth(100); // Informed Consent
            tbAnestesi.getColumnModel().getColumn(22).setPreferredWidth(100); // Obat Anestesi
            tbAnestesi.getColumnModel().getColumn(23).setPreferredWidth(150); // Tatalaksana Jalan Nafas
            tbAnestesi.getColumnModel().getColumn(24).setPreferredWidth(120); // Mesin Anestesi
            tbAnestesi.getColumnModel().getColumn(25).setPreferredWidth(120); // Obat Emergensi
            tbAnestesi.getColumnModel().getColumn(26).setPreferredWidth(120); // Check Monitoring
            tbAnestesi.getColumnModel().getColumn(27).setPreferredWidth(120); // Suction Aparatus
        }

        // Set table styling
        setTableStyling(tbAnestesi);
    }

    // Method untuk debug dan testing
    private void debugDatabase() {
        try {
            // Test koneksi database
            ps = koneksi.prepareStatement("SELECT COUNT(*) as total FROM catatan_anestesi_data_awal");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Total data awal: " + rs.getInt("total"));
            }
        } catch (Exception e) {
            System.out.println("Error debug database: " + e.getMessage());
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
            if (ps != null) try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }

// Method untuk validasi data sebelum simpan
    private boolean validateDataAwal() {
        if (TNoRw.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No. Rawat harus diisi!");
            return false;
        }
        if (KdDokterAnastesi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dokter Anestesi harus dipilih!");
            return false;
        }
        return true;
    }

    private void TabRawatStateChanged() {
        int idx = TabRawat.getSelectedIndex();
        switch (idx) {
            case 0:
                tbAnestesi.setModel(tabModeDataAwal);
                tampilDataAwal();
                break;
            case 1:
                tbAnestesi1.setModel(tabModePraInduksi);
                tampilPraInduksi();
                break;
            case 2:
                tbAnestesi2.setModel(tabModePremedikasi);
                tampilPremedikasi();
                break;
            case 3:
                tbAnestesi3.setModel(tabModeMonitoring);
                tampilMonitoring();
                break;
            case 4:
                tbAnestesi4.setModel(tabModeCatatan);
                tampilCatatan();
                break;
            // dst sesuai urutan tab
        }
    }

    private void cetakDataAwal() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "SELECT ca.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, ca.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, ca.tgl_masuk_rs, "
                    + "ca.kd_petugas, p1.nama as nm_petugas, "
                    + "ca.kd_dokter_anestesi, d1.nm_dokter as nm_dokter_anestesi, "
                    + "ca.kd_asisten_anestesi, p2.nama as nm_asisten_anestesi, "
                    + "ca.kd_dokter_bedah, d2.nm_dokter as nm_dokter_bedah, "
                    + "ca.kd_asisten_bedah, p3.nama as nm_asisten_bedah, "
                    + "ca.medikasi_prabedah, ca.jenis_anestesi, ca.diagnosa_prabedah, "
                    + "ca.informed_consent, ca.obat_anestesi, ca.tatalaksana_jalan_nafas, "
                    + "ca.mesin_anestesi, ca.obat_emergensi, ca.check_monitoring, "
                    + "ca.suction_apparatus "
                    + "FROM catatan_anestesi_data_awal ca "
                    + "INNER JOIN reg_periksa rp ON ca.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p1 ON ca.kd_petugas = p1.nip "
                    + "LEFT JOIN dokter d1 ON ca.kd_dokter_anestesi = d1.kd_dokter "
                    + "LEFT JOIN petugas p2 ON ca.kd_asisten_anestesi = p2.nip "
                    + "LEFT JOIN dokter d2 ON ca.kd_dokter_bedah = d2.kd_dokter "
                    + "LEFT JOIN petugas p3 ON ca.kd_asisten_bedah = p3.nip "
                    + "WHERE ca.no_rawat = '" + TNoRw.getText() + "' "
                    + "ORDER BY ca.tanggal DESC LIMIT 1";

            Valid.MyReportqry("rptCatatanAnestesiDataAwal.jasper", "report",
                    "::[ Laporan Catatan Anestesi Data Awal ]::", query, param);

        } catch (Exception e) {
            System.out.println("Error cetakDataAwal: " + e.getMessage());
        }
    }

    private void cetakPraInduksi() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "SELECT capi.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, capi.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "capi.kd_petugas, p.nama as nm_petugas, "
                    + "capi.kesadaran, capi.gcs, capi.airway, capi.tensi, capi.nadi, "
                    + "capi.support1, capi.support2, capi.respirasi, capi.rr, "
                    + "capi.kanul_nasal, capi.o2, capi.spo2, capi.bb, capi.puasa_jam, "
                    + "capi.lab, capi.ekg, capi.thorax, capi.tfp, capi.px_lain, "
                    + "capi.saraf, capi.respirasi_sistem, capi.gastro, capi.urinaria, "
                    + "capi.muskuloskletal, capi.metabolik, capi.lain, capi.asa, capi.alergi "
                    + "FROM catatan_anestesi_pra_induksi capi "
                    + "INNER JOIN reg_periksa rp ON capi.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON capi.kd_petugas = p.nip "
                    + "WHERE capi.no_rawat = '" + TNoRw.getText() + "' "
                    + "ORDER BY capi.tanggal DESC LIMIT 1";

            Valid.MyReportqry("rptCatatanAnestesiPraInduksi.jasper", "report",
                    "::[ Laporan Catatan Anestesi Pra Induksi ]::", query, param);

        } catch (Exception e) {
            System.out.println("Error cetakPraInduksi: " + e.getMessage());
        }
    }

    private void cetakPremedikasi() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "SELECT cap.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, cap.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "cap.kd_petugas, p.nama as nm_petugas, "
                    + "cap.jenis_premedikasi, cap.nm_obat_premedikasi, cap.dosis, "
                    + "cap.jenis_anestesi, cap.nama_obat_anestesi "
                    + "FROM catatan_anestesi_premedikasi cap "
                    + "INNER JOIN reg_periksa rp ON cap.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON cap.kd_petugas = p.nip "
                    + "WHERE cap.no_rawat = '" + TNoRw.getText() + "' "
                    + "ORDER BY cap.tanggal DESC LIMIT 1";

            Valid.MyReportqry("rptCatatanAnestesiPremedikasi.jasper", "report",
                    "::[ Laporan Catatan Anestesi Premedikasi ]::", query, param);

        } catch (Exception e) {
            System.out.println("Error cetakPremedikasi: " + e.getMessage());
        }
    }

    private void cetakAnestesiUmum() {
        try {
            // Tentukan sub-tab yang aktif
            int anestesiUmumTabIndex = AnestesiUmum.getSelectedIndex();

            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "";
            String reportName = "";
            String judul = "";

            switch (anestesiUmumTabIndex) {
                case 0: // Data Awal
                    query = "SELECT cauda.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                            + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, cauda.tanggal, "
                            + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                            + "cauda.kd_petugas, p.nama as nm_petugas, "
                            + "cauda.induksi, cauda.induksi_ket, cauda.pengaturan_nafas, "
                            + "cauda.tidal_volum, cauda.rr, cauda.ie_ratio, cauda.peep, "
                            + "cauda.pip, cauda.fio2, cauda.teknik_khusus, cauda.teknik_khusus_ket "
                            + "FROM catatan_anestesi_umum_data_awal cauda "
                            + "INNER JOIN reg_periksa rp ON cauda.no_rawat = rp.no_rawat "
                            + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                            + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                            + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                            + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                            + "LEFT JOIN petugas p ON cauda.kd_petugas = p.nip "
                            + "WHERE cauda.no_rawat = '" + TNoRw.getText() + "' "
                            + "ORDER BY cauda.tanggal DESC LIMIT 1";
                    reportName = "rptCatatanAnestesiUmumDataAwal.jasper";
                    judul = "::[ Laporan Catatan Anestesi Umum Data Awal ]::";
                    break;

                case 1: // Monitoring
                    query = "SELECT caum.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                            + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, caum.tanggal, "
                            + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                            + "caum.kd_petugas, p.nama as nm_petugas, "
                            + "caum.o2, caum.n2o, caum.air, caum.volatile, caum.ekg, "
                            + "caum.spo2, caum.infus, caum.suhu, caum.respirasi, "
                            + "caum.tekanan_darah, caum.nadi "
                            + "FROM catatan_anestesi_umum_monitoring caum "
                            + "INNER JOIN reg_periksa rp ON caum.no_rawat = rp.no_rawat "
                            + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                            + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                            + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                            + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                            + "LEFT JOIN petugas p ON caum.kd_petugas = p.nip "
                            + "WHERE caum.no_rawat = '" + TNoRw.getText() + "' "
                            + "ORDER BY caum.tanggal DESC LIMIT 1";
                    reportName = "rptCatatanAnestesiUmumMonitoring.jasper";
                    judul = "::[ Laporan Catatan Anestesi Umum Monitoring ]::";
                    break;

                case 2: // Keterangan
                    query = "SELECT cauk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                            + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, cauk.tanggal, "
                            + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                            + "cauk.kd_petugas, p.nama as nm_petugas, "
                            + "cauk.medikasi, cauk.pemberian_cairan, "
                            + "cauk.masalah_durante_operasi, cauk.tindakan "
                            + "FROM catatan_anestesi_umum_keterangan cauk "
                            + "INNER JOIN reg_periksa rp ON cauk.no_rawat = rp.no_rawat "
                            + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                            + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                            + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                            + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                            + "LEFT JOIN petugas p ON cauk.kd_petugas = p.nip "
                            + "WHERE cauk.no_rawat = '" + TNoRw.getText() + "' "
                            + "ORDER BY cauk.tanggal DESC LIMIT 1";
                    reportName = "rptCatatanAnestesiUmumKeterangan.jasper";
                    judul = "::[ Laporan Catatan Anestesi Umum Keterangan ]::";
                    break;
            }

            Valid.MyReportqry(reportName, "report", judul, query, param);

        } catch (Exception e) {
            System.out.println("Error cetakAnestesiUmum: " + e.getMessage());
        }
    }

    private void cetakPascaAnestesi() {
        try {
            // Tentukan sub-tab yang aktif
            int catatanTabIndex = CatatanKamarPemulihan.getSelectedIndex();

            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "";
            String reportName = "";
            String judul = "";

            switch (catatanTabIndex) {
                case 0: // Masuk Kamar
                    query = "SELECT capamk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                            + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, capamk.tanggal, "
                            + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                            + "capamk.kd_petugas, p.nama as nm_petugas, "
                            + "capamk.jam_masuk_kamar_pulih, capamk.kesadaran, capamk.tensi, "
                            + "capamk.nadi, capamk.rr, capamk.support1, capamk.support2, "
                            + "capamk.respirasi, capamk.kanul_nasal, capamk.kanul_o2, "
                            + "capamk.o2, capamk.spo2, capamk.skor_nyeri, capamk.penyulit_intraoperatif "
                            + "FROM catatan_anestesi_pasca_anestesi_masuk_kamar capamk "
                            + "INNER JOIN reg_periksa rp ON capamk.no_rawat = rp.no_rawat "
                            + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                            + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                            + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                            + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                            + "LEFT JOIN petugas p ON capamk.kd_petugas = p.nip "
                            + "WHERE capamk.no_rawat = '" + TNoRw.getText() + "' "
                            + "ORDER BY capamk.tanggal DESC LIMIT 1";
                    reportName = "rptCatatanAnestesiMasukKamar.jasper";
                    judul = "::[ Laporan Catatan Anestesi Masuk Kamar Pemulihan ]::";
                    break;

                case 1: // Monitoring
                    query = "SELECT capam.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                            + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, capam.tanggal, "
                            + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                            + "capam.kd_petugas, p.nama as nm_petugas, "
                            + "capam.suhu, capam.rr, capam.nadi, capam.tekanan_darah "
                            + "FROM catatan_anestesi_pasca_anestesi_monitoring capam "
                            + "INNER JOIN reg_periksa rp ON capam.no_rawat = rp.no_rawat "
                            + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                            + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                            + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                            + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                            + "LEFT JOIN petugas p ON capam.kd_petugas = p.nip "
                            + "WHERE capam.no_rawat = '" + TNoRw.getText() + "' "
                            + "ORDER BY capam.tanggal DESC LIMIT 1";
                    reportName = "rptCatatanAnestesiMonitoringPemulihan.jasper";
                    judul = "::[ Laporan Catatan Anestesi Monitoring Pemulihan ]::";
                    break;
            }

            Valid.MyReportqry(reportName, "report", judul, query, param);

        } catch (Exception e) {
            System.out.println("Error cetakPascaAnestesi: " + e.getMessage());
        }
    }

    private void cetakBlokadeRegional() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "SELECT cabr.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, cabr.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "cabr.kd_petugas, p.nama as nm_petugas, "
                    + "cabr.teknik, cabr.lokasi_tusukan, cabr.analgesia_setinggi_segmen, "
                    + "cabr.anestesi_lokal, cabr.konsentrasi_jumlah, cabr.obat_tambahan, "
                    + "cabr.dosis, cabr.vasokonstriktor, cabr.konsentrasi, "
                    + "cabr.waktu_mulai_suntik_jam, cabr.analgesia_jam, cabr.lamanya, "
                    + "cabr.lamanya_menit_analgesi, cabr.operasi_jam, cabr.lamanya_jam_operasi, "
                    + "cabr.lamanya_jam_menit, cabr.tindakan_anestesi_tambahan "
                    + "FROM catatan_anestesi_blokade_regional cabr "
                    + "INNER JOIN reg_periksa rp ON cabr.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON cabr.kd_petugas = p.nip "
                    + "WHERE cabr.no_rawat = '" + TNoRw.getText() + "' "
                    + "ORDER BY cabr.tanggal DESC LIMIT 1";

            Valid.MyReportqry("rptCatatanAnestesiBlokadeRegional.jasper", "report",
                    "::[ Laporan Catatan Anestesi Blokade Regional ]::", query, param);

        } catch (Exception e) {
            System.out.println("Error cetakBlokadeRegional: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat mencetak Blokade Regional: " + e.getMessage());
        }
    }

    private void cetakKeadaanOperasi() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "SELECT cako.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, cako.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "cako.kd_petugas, p.nama as nm_petugas, "
                    + "cako.letak_penderita, cako.airway, cako.ukuran, cako.balon, "
                    + "cako.lama_operasi, cako.lama_anestesi, cako.kristaloid, "
                    + "cako.perdarahan, cako.koloid, cako.diuresis, cako.darah, "
                    + "cako.cairan_lain, cako.komponen_darah "
                    + "FROM catatan_anestesi_keadaan_operasi cako "
                    + "INNER JOIN reg_periksa rp ON cako.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON cako.kd_petugas = p.nip "
                    + "WHERE cako.no_rawat = '" + TNoRw.getText() + "' "
                    + "ORDER BY cako.tanggal DESC LIMIT 1";

            Valid.MyReportqry("rptCatatanAnestesiKeadaanOperasi.jasper", "report",
                    "::[ Laporan Catatan Anestesi Keadaan Selama Operasi ]::", query, param);

        } catch (Exception e) {
            System.out.println("Error cetakKeadaanOperasi: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat mencetak Keadaan Operasi: " + e.getMessage());
        }
    }

    private void cetakSectio() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select logo from setting"));
            param.put("no_rawat", TNoRw.getText());

            String query = "SELECT casc.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, casc.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "casc.kd_petugas, p.nama as nm_petugas, "
                    + "casc.keadaan_bayi, casc.jenis_kelamin, casc.apgar_1_menit, "
                    + "casc.apgar_5_menit, casc.apgar_10_menit "
                    + "FROM catatan_anestesi_sectio_caesaria casc "
                    + "INNER JOIN reg_periksa rp ON casc.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON casc.kd_petugas = p.nip "
                    + "WHERE casc.no_rawat = '" + TNoRw.getText() + "' "
                    + "ORDER BY casc.tanggal DESC LIMIT 1";

            Valid.MyReportqry("rptCatatanAnestesiSectio.jasper", "report",
                    "::[ Laporan Catatan Anestesi Sectio Caesaria ]::", query, param);

        } catch (Exception e) {
            System.out.println("Error cetakSectio: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saat mencetak Sectio Caesaria: " + e.getMessage());
        }
    }

    // ===== METHOD CETAK HTML =====
    private void cetakHTML() {
        try {
            StringBuilder html = new StringBuilder();

            // Header HTML
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n<head>\n");
            html.append("<meta charset='UTF-8'>\n");
            html.append("<title>Laporan Status Anestesi</title>\n");
            html.append("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>\n");
            html.append("<style>\n");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
            html.append(".header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 10px; }\n");
            html.append(".info-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }\n");
            html.append(".info-table td { padding: 5px; border: 1px solid #ddd; }\n");
            html.append(".info-table td:first-child { width: 200px; background-color: #f0f0f0; font-weight: bold; }\n");
            html.append(".section { margin-top: 30px; margin-bottom: 30px; page-break-inside: avoid; border: 1px solid #ddd; padding: 15px; border-radius: 5px; background-color: #fafafa; }\n");
            html.append(".section-title { background-color: #4CAF50; color: white; padding: 10px; font-size: 16px; font-weight: bold; margin: -15px -15px 15px -15px; border-radius: 5px 5px 0 0; }\n");
            html.append("table.data-table { width: 100%; border-collapse: collapse; margin-top: 10px; background-color: white; }\n");
            html.append("table.data-table th, table.data-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
            html.append("table.data-table th { background-color: #4CAF50; color: white; }\n");
            html.append("table.data-table tr:nth-child(even) { background-color: #f2f2f2; }\n");
            html.append(".print-button { background-color: #008CBA; color: white; padding: 10px 20px; border: none; cursor: pointer; margin: 10px 0; font-size: 16px; border-radius: 5px; }\n");
            html.append(".print-button:hover { background-color: #005f73; }\n");
            html.append(".chart-container { margin: 20px 0; max-width: 100%; height: 400px; background-color: white; padding: 10px; border-radius: 5px; }\n");
            html.append("@media print { .print-button { display: none; } canvas { max-height: 300px; } .section { page-break-after: auto; } }\n");
            html.append("</style>\n");
            html.append("</head>\n<body>\n");

            // Tombol Print
            html.append("<button class='print-button' onclick='window.print()'>🖨️ Cetak Laporan</button>\n");

            // Header Rumah Sakit
            html.append("<div class='header'>\n");
            html.append("<h2>").append(akses.getnamars()).append("</h2>\n");
            html.append("<p>").append(akses.getalamatrs()).append("<br>\n");
            html.append(akses.getkabupatenrs()).append(", ").append(akses.getpropinsirs()).append("<br>\n");
            html.append("Telp: ").append(akses.getkontakrs()).append(" | Email: ").append(akses.getemailrs()).append("</p>\n");
            html.append("<h3>LAPORAN STATUS ANESTESI LENGKAP</h3>\n");
            html.append("<p style='font-size: 14px; color: #666;'>Laporan ini berisi semua data anestesi yang tersedia untuk pasien</p>\n");
            html.append("</div>\n");

            // Data Pasien
            html.append(generateDataPasienHTML());

            // Debug info
            System.out.println("=== Cetak HTML - SEMUA TAB YANG MEMILIKI DATA ===");

            // Generate SEMUA konten yang ada datanya
            // 1. Data Awal
            html.append(generateDataAwalHTML());

            // 2. Pra Induksi
            html.append(generatePraInduksiHTML());

            // 3. Premedikasi
            html.append(generatePremedikasiHTML());

            // 4. Blokade Regional
            html.append(generateBlokadeRegionalHTML());

            // 5. Keadaan Operasi
            html.append(generateKeadaanOperasiHTML());

            // 6. Pasca Anestesi (Masuk Kamar + Monitoring + Grafik)
            html.append(generatePascaAnestesiHTML());

            // Footer
            html.append("<div style='margin-top: 30px; text-align: right;'>\n");
            html.append("<p>Dicetak pada: ").append(new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date())).append("</p>\n");
            html.append("</div>\n");

            html.append("</body>\n</html>");

            // Simpan ke file temp dan buka di browser
            saveTempHTMLAndOpen(html.toString());

        } catch (Exception e) {
            System.out.println("Error cetakHTML: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saat membuat laporan HTML: " + e.getMessage());
        }
    }

    private String generateDataPasienHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            String query = "SELECT rp.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "pas.jk, pas.alamat, rp.tgl_registrasi, pol.nm_poli "
                    + "FROM reg_periksa rp "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN poliklinik pol ON rp.kd_poli = pol.kd_poli "
                    + "WHERE rp.no_rawat = ?";

            psLocal = koneksi.prepareStatement(query);
            psLocal.setString(1, TNoRw.getText());
            rsLocal = psLocal.executeQuery();

            if (rsLocal.next()) {
                html.append("<div class='section'>\n");
                html.append("<div class='section-title'>INFORMASI PASIEN</div>\n");
                html.append("<table class='info-table'>\n");
                html.append("<tr><td>No. Rawat</td><td>").append(rsLocal.getString("no_rawat")).append("</td></tr>\n");
                html.append("<tr><td>No. Rekam Medis</td><td>").append(rsLocal.getString("no_rkm_medis")).append("</td></tr>\n");
                html.append("<tr><td>Nama Pasien</td><td>").append(rsLocal.getString("nm_pasien")).append("</td></tr>\n");
                html.append("<tr><td>Tanggal Lahir</td><td>").append(rsLocal.getString("tgl_lahir")).append("</td></tr>\n");
                html.append("<tr><td>Jenis Kelamin</td><td>").append(rsLocal.getString("jk").equals("L") ? "Laki-laki" : "Perempuan").append("</td></tr>\n");
                html.append("<tr><td>Alamat</td><td>").append(rsLocal.getString("alamat")).append("</td></tr>\n");
                html.append("<tr><td>Tanggal Registrasi</td><td>").append(rsLocal.getString("tgl_registrasi")).append("</td></tr>\n");
                html.append("<tr><td>Poliklinik</td><td>").append(rsLocal.getString("nm_poli") != null ? rsLocal.getString("nm_poli") : "-").append("</td></tr>\n");
                html.append("</table>\n");
                html.append("</div>\n");
            }
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error mengambil data pasien: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsLocal != null) try { rsLocal.close(); } catch (Exception e) {}
            if (psLocal != null) try { psLocal.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generateDataAwalHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>DATA AWAL</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Parameter</th><th>Nilai</th></tr>\n");

            String query = "SELECT * FROM catatan_anestesi_data_awal WHERE no_rawat = ? ORDER BY tanggal DESC LIMIT 1";
            psLocal = koneksi.prepareStatement(query);
            psLocal.setString(1, TNoRw.getText());
            rsLocal = psLocal.executeQuery();

            if (rsLocal.next()) {
                html.append("<tr><td>Tanggal</td><td>").append(rsLocal.getString("tanggal")).append("</td></tr>\n");
                html.append("<tr><td>Dokter Anestesi</td><td>").append(nvl(rsLocal.getString("kd_dokter_anestesi"))).append("</td></tr>\n");
                html.append("<tr><td>Dokter Bedah</td><td>").append(nvl(rsLocal.getString("kd_dokter_bedah"))).append("</td></tr>\n");
                html.append("<tr><td>Medikasi Prabedah</td><td>").append(nvl(rsLocal.getString("medikasi_prabedah"))).append("</td></tr>\n");
                html.append("<tr><td>Jenis Anestesi</td><td>").append(nvl(rsLocal.getString("jenis_anestesi"))).append("</td></tr>\n");
                html.append("<tr><td>Diagnosa Prabedah</td><td>").append(nvl(rsLocal.getString("diagnosa_prabedah"))).append("</td></tr>\n");
                html.append("<tr><td>Informed Consent</td><td>").append(nvl(rsLocal.getString("informed_consent"))).append("</td></tr>\n");
                html.append("<tr><td>Obat Anestesi</td><td>").append(nvl(rsLocal.getString("obat_anestesi"))).append("</td></tr>\n");
                html.append("<tr><td>Tatalaksana Jalan Nafas</td><td>").append(nvl(rsLocal.getString("tatalaksana_jalan_nafas"))).append("</td></tr>\n");
                html.append("<tr><td>Mesin Anestesi</td><td>").append(nvl(rsLocal.getString("mesin_anestesi"))).append("</td></tr>\n");
                html.append("<tr><td>Obat Emergensi</td><td>").append(nvl(rsLocal.getString("obat_emergensi"))).append("</td></tr>\n");
                html.append("<tr><td>Check Monitoring</td><td>").append(nvl(rsLocal.getString("check_monitoring"))).append("</td></tr>\n");
                html.append("<tr><td>Suction Aparatus</td><td>").append(nvl(rsLocal.getString("suction_aparatus"))).append("</td></tr>\n");
            } else {
                html.append("<tr><td colspan='2' style='text-align: center;'>Tidak ada data</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</div>\n");
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsLocal != null) try { rsLocal.close(); } catch (Exception e) {}
            if (psLocal != null) try { psLocal.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generatePraInduksiHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>PRA INDUKSI</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Parameter</th><th>Nilai</th></tr>\n");

            String query = "SELECT * FROM catatan_anestesi_pra_induksi WHERE no_rawat = ? ORDER BY tanggal DESC LIMIT 1";
            psLocal = koneksi.prepareStatement(query);
            psLocal.setString(1, TNoRw.getText());
            rsLocal = psLocal.executeQuery();

            if (rsLocal.next()) {
                html.append("<tr><td>Tanggal</td><td>").append(rsLocal.getString("tanggal")).append("</td></tr>\n");
                html.append("<tr><td>Kesadaran</td><td>").append(nvl(rsLocal.getString("kesadaran"))).append("</td></tr>\n");
                html.append("<tr><td>GCS</td><td>").append(nvl(rsLocal.getString("gcs"))).append("</td></tr>\n");
                html.append("<tr><td>Airway</td><td>").append(nvl(rsLocal.getString("airway"))).append("</td></tr>\n");
                html.append("<tr><td>Tensi</td><td>").append(nvl(rsLocal.getString("tensi"))).append("</td></tr>\n");
                html.append("<tr><td>Nadi</td><td>").append(nvl(rsLocal.getString("nadi"))).append("</td></tr>\n");
                html.append("<tr><td>Respirasi</td><td>").append(nvl(rsLocal.getString("respirasi"))).append("</td></tr>\n");
                html.append("<tr><td>RR</td><td>").append(nvl(rsLocal.getString("rr"))).append("</td></tr>\n");
                html.append("<tr><td>SpO2</td><td>").append(nvl(rsLocal.getString("spo2"))).append("</td></tr>\n");
            } else {
                html.append("<tr><td colspan='2' style='text-align: center;'>Tidak ada data</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</div>\n");
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsLocal != null) try { rsLocal.close(); } catch (Exception e) {}
            if (psLocal != null) try { psLocal.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generatePremedikasiHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>PREMEDIKASI</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Tanggal</th><th>Obat</th><th>Dosis</th><th>Rute</th></tr>\n");

            String query = "SELECT * FROM catatan_anestesi_premedikasi WHERE no_rawat = ? ORDER BY tanggal DESC";
            psLocal = koneksi.prepareStatement(query);
            psLocal.setString(1, TNoRw.getText());
            rsLocal = psLocal.executeQuery();

            boolean hasData = false;
            while (rsLocal.next()) {
                hasData = true;
                html.append("<tr>");
                html.append("<td>").append(rsLocal.getString("tanggal")).append("</td>");
                html.append("<td>").append(nvl(rsLocal.getString("nm_obat_premedikasi"))).append("</td>");
                html.append("<td>").append(nvl(rsLocal.getString("dosis"))).append("</td>");
                html.append("<td>").append(nvl(rsLocal.getString("jenis_premedikasi"))).append("</td>");
                html.append("</tr>\n");
            }

            if (!hasData) {
                html.append("<tr><td colspan='4' style='text-align: center;'>Tidak ada data</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</div>\n");
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsLocal != null) try { rsLocal.close(); } catch (Exception e) {}
            if (psLocal != null) try { psLocal.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generateAnestesiUmumHTML() {
        StringBuilder html = new StringBuilder();

        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>ANESTESI UMUM</div>\n");
        html.append("<div style='background-color: #fff3cd; padding: 15px; margin: 10px 0; border-left: 4px solid #ffc107;'>\n");
        html.append("<p><strong>ℹ️ Informasi:</strong></p>\n");
        html.append("<p>Tab Anestesi Umum memiliki beberapa sub-kategori yang kompleks:</p>\n");
        html.append("<ul>\n");
        html.append("<li>Data Awal</li>\n");
        html.append("<li>Monitoring</li>\n");
        html.append("<li>Keterangan</li>\n");
        html.append("</ul>\n");
        html.append("<p><strong>Untuk mencetak data Pasca Anestesi dengan grafik, silakan pilih tab \"Keadaan Pasca Anestesi\" terlebih dahulu sebelum mencetak.</strong></p>\n");
        html.append("</div>\n");
        html.append("</div>\n");

        return html.toString();
    }

    private String generateBlokadeRegionalHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>BLOKADE REGIONAL</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Parameter</th><th>Nilai</th></tr>\n");

            String query = "SELECT * FROM catatan_anestesi_blokade_regional WHERE no_rawat = ? ORDER BY tanggal DESC LIMIT 1";
            psLocal = koneksi.prepareStatement(query);
            psLocal.setString(1, TNoRw.getText());
            rsLocal = psLocal.executeQuery();

            if (rsLocal.next()) {
                html.append("<tr><td>Tanggal</td><td>").append(rsLocal.getString("tanggal")).append("</td></tr>\n");
                html.append("<tr><td>Teknik</td><td>").append(nvl(rsLocal.getString("teknik"))).append("</td></tr>\n");
                html.append("<tr><td>Lokasi Tusukan</td><td>").append(nvl(rsLocal.getString("lokasi_tusukan"))).append("</td></tr>\n");
                html.append("<tr><td>Analgesia Setinggi Segmen</td><td>").append(nvl(rsLocal.getString("analgesia_setinggi_segmen"))).append("</td></tr>\n");
                html.append("<tr><td>Anestesi Lokal</td><td>").append(nvl(rsLocal.getString("anestesi_lokal"))).append("</td></tr>\n");
                html.append("<tr><td>Konsentrasi/Jumlah</td><td>").append(nvl(rsLocal.getString("konsentrasi_jumlah"))).append("</td></tr>\n");
                html.append("<tr><td>Obat Tambahan</td><td>").append(nvl(rsLocal.getString("obat_tambahan"))).append("</td></tr>\n");
                html.append("<tr><td>Dosis</td><td>").append(nvl(rsLocal.getString("dosis"))).append("</td></tr>\n");
                html.append("<tr><td>Vasokonstriktor</td><td>").append(nvl(rsLocal.getString("vasokonstriktor"))).append("</td></tr>\n");
            } else {
                html.append("<tr><td colspan='2' style='text-align: center;'>Tidak ada data</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</div>\n");
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsLocal != null) try { rsLocal.close(); } catch (Exception e) {}
            if (psLocal != null) try { psLocal.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generateKeadaanOperasiHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>KEADAAN SELAMA OPERASI</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Parameter</th><th>Nilai</th></tr>\n");

            String query = "SELECT * FROM catatan_anestesi_keadaan_operasi WHERE no_rawat = ? ORDER BY tanggal DESC LIMIT 1";
            psLocal = koneksi.prepareStatement(query);
            psLocal.setString(1, TNoRw.getText());
            rsLocal = psLocal.executeQuery();

            if (rsLocal.next()) {
                html.append("<tr><td>Tanggal</td><td>").append(rsLocal.getString("tanggal")).append("</td></tr>\n");
                html.append("<tr><td>Letak Penderita</td><td>").append(nvl(rsLocal.getString("letak_penderita"))).append("</td></tr>\n");
                html.append("<tr><td>Airway</td><td>").append(nvl(rsLocal.getString("airway"))).append("</td></tr>\n");
                html.append("<tr><td>Ukuran</td><td>").append(nvl(rsLocal.getString("ukuran"))).append("</td></tr>\n");
                html.append("<tr><td>Balon</td><td>").append(nvl(rsLocal.getString("balon"))).append("</td></tr>\n");
                html.append("<tr><td>Lama Operasi</td><td>").append(nvl(rsLocal.getString("lama_operasi"))).append("</td></tr>\n");
                html.append("<tr><td>Lama Anestesi</td><td>").append(nvl(rsLocal.getString("lama_anestesi"))).append("</td></tr>\n");
                html.append("<tr><td>Kristaloid</td><td>").append(nvl(rsLocal.getString("kristaloid"))).append("</td></tr>\n");
                html.append("<tr><td>Perdarahan</td><td>").append(nvl(rsLocal.getString("perdarahan"))).append("</td></tr>\n");
                html.append("<tr><td>Diuresis</td><td>").append(nvl(rsLocal.getString("diuresis"))).append("</td></tr>\n");
            } else {
                html.append("<tr><td colspan='2' style='text-align: center;'>Tidak ada data</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</div>\n");
        } catch (Exception e) {
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsLocal != null) try { rsLocal.close(); } catch (Exception e) {}
            if (psLocal != null) try { psLocal.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generateSectioHTML() {
        StringBuilder html = new StringBuilder();

        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>SECTIO CAESARIA</div>\n");
        html.append("<p style='text-align: center;'>Data sectio caesaria tersedia di tab khusus</p>\n");
        html.append("</div>\n");

        return html.toString();
    }

    private String generatePascaAnestesiHTML() {
        StringBuilder html = new StringBuilder();
        PreparedStatement psMasukKamar = null;
        ResultSet rsMasukKamar = null;
        PreparedStatement psMonitoring = null;
        ResultSet rsMonitoring = null;

        try {
            System.out.println("=== generatePascaAnestesiHTML dipanggil ===");
            System.out.println("No Rawat: " + TNoRw.getText());

            // Masuk Kamar Pemulihan
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>PASCA ANESTESI - MASUK KAMAR PEMULIHAN</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Parameter</th><th>Nilai</th></tr>\n");

            String query = "SELECT * FROM catatan_anestesi_pasca_anestesi_masuk_kamar WHERE no_rawat = ? ORDER BY tanggal DESC LIMIT 1";
            psMasukKamar = koneksi.prepareStatement(query);
            psMasukKamar.setString(1, TNoRw.getText());
            System.out.println("Query Masuk Kamar: " + query.replace("?", "'" + TNoRw.getText() + "'"));
            rsMasukKamar = psMasukKamar.executeQuery();

            if (rsMasukKamar.next()) {
                System.out.println("Data Masuk Kamar ditemukan!");
                html.append("<tr><td>Tanggal</td><td>").append(rsMasukKamar.getString("tanggal")).append("</td></tr>\n");
                html.append("<tr><td>Jam Masuk Kamar Pulih</td><td>").append(nvl(rsMasukKamar.getString("jam_masuk_kamar_pulih"))).append("</td></tr>\n");
                html.append("<tr><td>Kesadaran</td><td>").append(nvl(rsMasukKamar.getString("kesadaran"))).append("</td></tr>\n");
                html.append("<tr><td>Tensi</td><td>").append(nvl(rsMasukKamar.getString("tensi"))).append("</td></tr>\n");
                html.append("<tr><td>Nadi</td><td>").append(nvl(rsMasukKamar.getString("nadi"))).append("</td></tr>\n");
                html.append("<tr><td>RR</td><td>").append(nvl(rsMasukKamar.getString("rr"))).append("</td></tr>\n");
                html.append("<tr><td>SpO2</td><td>").append(nvl(rsMasukKamar.getString("spo2"))).append("</td></tr>\n");
                html.append("<tr><td>Skor Nyeri</td><td>").append(nvl(rsMasukKamar.getString("skor_nyeri"))).append("</td></tr>\n");
            } else {
                System.out.println("TIDAK ada data Masuk Kamar!");
                html.append("<tr><td colspan='2' style='text-align: center;'>Tidak ada data</td></tr>\n");
            }

            html.append("</table>\n");
            html.append("</div>\n");

            // Monitoring Pemulihan - Tabel
            html.append("<div class='section'>\n");
            html.append("<div class='section-title'>PASCA ANESTESI - MONITORING PEMULIHAN (Tabel)</div>\n");
            html.append("<table class='data-table'>\n");
            html.append("<tr><th>Tanggal</th><th>Suhu (°C)</th><th>RR (x/mnt)</th><th>Nadi (x/mnt)</th><th>Tekanan Darah (mmHg)</th></tr>\n");

            query = "SELECT * FROM catatan_anestesi_pasca_anestesi_monitoring WHERE no_rawat = ? ORDER BY tanggal ASC";
            psMonitoring = koneksi.prepareStatement(query);
            psMonitoring.setString(1, TNoRw.getText());
            System.out.println("Query Monitoring: " + query.replace("?", "'" + TNoRw.getText() + "'"));
            rsMonitoring = psMonitoring.executeQuery();

            // Untuk grafik
            java.util.List<String> labels = new java.util.ArrayList<>();
            java.util.List<String> suhuData = new java.util.ArrayList<>();
            java.util.List<String> rrData = new java.util.ArrayList<>();
            java.util.List<String> nadiData = new java.util.ArrayList<>();
            java.util.List<String> sistolikData = new java.util.ArrayList<>();
            java.util.List<String> diastolikData = new java.util.ArrayList<>();

            boolean hasData = false;
            int rowCount = 0;
            while (rsMonitoring.next()) {
                hasData = true;
                rowCount++;
                String tanggal = rsMonitoring.getString("tanggal");
                String suhu = nvl(rsMonitoring.getString("suhu"));
                String rr = nvl(rsMonitoring.getString("rr"));
                String nadi = nvl(rsMonitoring.getString("nadi"));
                String td = nvl(rsMonitoring.getString("tekanan_darah"));

                html.append("<tr>");
                html.append("<td>").append(tanggal).append("</td>");
                html.append("<td>").append(suhu).append("</td>");
                html.append("<td>").append(rr).append("</td>");
                html.append("<td>").append(nadi).append("</td>");
                html.append("<td>").append(td).append("</td>");
                html.append("</tr>\n");

                // Simpan untuk grafik
                try {
                    labels.add(tanggal.substring(11, 16)); // HH:MM
                    suhuData.add(suhu.equals("-") ? "0" : suhu);
                    rrData.add(rr.equals("-") ? "0" : rr);
                    nadiData.add(nadi.equals("-") ? "0" : nadi);

                    // Parse tekanan darah (format: 110/80)
                    if (!td.equals("-") && td.contains("/")) {
                        String[] tdParts = td.split("/");
                        sistolikData.add(tdParts[0].trim());
                        diastolikData.add(tdParts[1].trim());
                    } else {
                        sistolikData.add("0");
                        diastolikData.add("0");
                    }
                } catch (Exception e) {
                    // Skip jika parsing error
                    sistolikData.add("0");
                    diastolikData.add("0");
                }
            }

            if (!hasData) {
                System.out.println("TIDAK ada data Monitoring!");
                html.append("<tr><td colspan='5' style='text-align: center;'>Tidak ada data</td></tr>\n");
            } else {
                System.out.println("Data Monitoring ditemukan: " + rowCount + " rows");
            }

            html.append("</table>\n");
            html.append("</div>\n");

            // Grafik Monitoring (jika ada data)
            if (hasData && labels.size() > 0) {
                System.out.println("Membuat grafik dengan " + labels.size() + " data points");
                html.append(generateMonitoringChart(labels, suhuData, rrData, nadiData, sistolikData, diastolikData));
            } else {
                System.out.println("Grafik TIDAK dibuat (hasData=" + hasData + ", labels=" + labels.size() + ")");
            }

        } catch (Exception e) {
            System.out.println("Error generatePascaAnestesiHTML: " + e.getMessage());
            e.printStackTrace();
            html.append("<p style='color: red;'>Error: ").append(e.getMessage()).append("</p>\n");
        } finally {
            if (rsMasukKamar != null) try { rsMasukKamar.close(); } catch (Exception e) {}
            if (psMasukKamar != null) try { psMasukKamar.close(); } catch (Exception e) {}
            if (rsMonitoring != null) try { rsMonitoring.close(); } catch (Exception e) {}
            if (psMonitoring != null) try { psMonitoring.close(); } catch (Exception e) {}
        }

        return html.toString();
    }

    private String generateMonitoringChart(java.util.List<String> labels,
                                          java.util.List<String> suhuData,
                                          java.util.List<String> rrData,
                                          java.util.List<String> nadiData,
                                          java.util.List<String> sistolikData,
                                          java.util.List<String> diastolikData) {
        StringBuilder html = new StringBuilder();

        html.append("<div class='section'>\n");
        html.append("<div class='section-title'>GRAFIK MONITORING PEMULIHAN</div>\n");

        // Chart Suhu
        html.append("<div class='chart-container'>\n");
        html.append("<canvas id='chartSuhu'></canvas>\n");
        html.append("</div>\n");

        // Chart Respirasi & Nadi
        html.append("<div class='chart-container'>\n");
        html.append("<canvas id='chartVitalSign'></canvas>\n");
        html.append("</div>\n");

        // Chart Tekanan Darah
        html.append("<div class='chart-container'>\n");
        html.append("<canvas id='chartTekananDarah'></canvas>\n");
        html.append("</div>\n");

        html.append("</div>\n");

        // JavaScript untuk Chart
        html.append("<script>\n");

        // Chart Suhu
        html.append("const ctxSuhu = document.getElementById('chartSuhu').getContext('2d');\n");
        html.append("new Chart(ctxSuhu, {\n");
        html.append("  type: 'line',\n");
        html.append("  data: {\n");
        html.append("    labels: ").append(toJSArray(labels)).append(",\n");
        html.append("    datasets: [{\n");
        html.append("      label: 'Suhu (°C)',\n");
        html.append("      data: ").append(toJSArray(suhuData)).append(",\n");
        html.append("      borderColor: 'rgb(255, 99, 132)',\n");
        html.append("      backgroundColor: 'rgba(255, 99, 132, 0.1)',\n");
        html.append("      tension: 0.4,\n");
        html.append("      fill: true\n");
        html.append("    }]\n");
        html.append("  },\n");
        html.append("  options: {\n");
        html.append("    responsive: true,\n");
        html.append("    maintainAspectRatio: false,\n");
        html.append("    plugins: {\n");
        html.append("      title: { display: true, text: 'Monitoring Suhu Tubuh' }\n");
        html.append("    },\n");
        html.append("    scales: {\n");
        html.append("      y: { beginAtZero: false, min: 35, max: 40 }\n");
        html.append("    }\n");
        html.append("  }\n");
        html.append("});\n\n");

        // Chart Vital Sign (RR & Nadi)
        html.append("const ctxVital = document.getElementById('chartVitalSign').getContext('2d');\n");
        html.append("new Chart(ctxVital, {\n");
        html.append("  type: 'line',\n");
        html.append("  data: {\n");
        html.append("    labels: ").append(toJSArray(labels)).append(",\n");
        html.append("    datasets: [\n");
        html.append("      {\n");
        html.append("        label: 'Respirasi (x/menit)',\n");
        html.append("        data: ").append(toJSArray(rrData)).append(",\n");
        html.append("        borderColor: 'rgb(54, 162, 235)',\n");
        html.append("        backgroundColor: 'rgba(54, 162, 235, 0.1)',\n");
        html.append("        tension: 0.4,\n");
        html.append("        yAxisID: 'y'\n");
        html.append("      },\n");
        html.append("      {\n");
        html.append("        label: 'Nadi (x/menit)',\n");
        html.append("        data: ").append(toJSArray(nadiData)).append(",\n");
        html.append("        borderColor: 'rgb(75, 192, 192)',\n");
        html.append("        backgroundColor: 'rgba(75, 192, 192, 0.1)',\n");
        html.append("        tension: 0.4,\n");
        html.append("        yAxisID: 'y'\n");
        html.append("      }\n");
        html.append("    ]\n");
        html.append("  },\n");
        html.append("  options: {\n");
        html.append("    responsive: true,\n");
        html.append("    maintainAspectRatio: false,\n");
        html.append("    plugins: {\n");
        html.append("      title: { display: true, text: 'Monitoring Vital Sign (Respirasi & Nadi)' }\n");
        html.append("    },\n");
        html.append("    scales: {\n");
        html.append("      y: { beginAtZero: true, max: 150 }\n");
        html.append("    }\n");
        html.append("  }\n");
        html.append("});\n\n");

        // Chart Tekanan Darah (Sistolik & Diastolik)
        html.append("const ctxTD = document.getElementById('chartTekananDarah').getContext('2d');\n");
        html.append("new Chart(ctxTD, {\n");
        html.append("  type: 'line',\n");
        html.append("  data: {\n");
        html.append("    labels: ").append(toJSArray(labels)).append(",\n");
        html.append("    datasets: [\n");
        html.append("      {\n");
        html.append("        label: 'Sistolik (mmHg)',\n");
        html.append("        data: ").append(toJSArray(sistolikData)).append(",\n");
        html.append("        borderColor: 'rgb(255, 99, 132)',\n");
        html.append("        backgroundColor: 'rgba(255, 99, 132, 0.1)',\n");
        html.append("        tension: 0.4,\n");
        html.append("        fill: false,\n");
        html.append("        yAxisID: 'y'\n");
        html.append("      },\n");
        html.append("      {\n");
        html.append("        label: 'Diastolik (mmHg)',\n");
        html.append("        data: ").append(toJSArray(diastolikData)).append(",\n");
        html.append("        borderColor: 'rgb(153, 102, 255)',\n");
        html.append("        backgroundColor: 'rgba(153, 102, 255, 0.1)',\n");
        html.append("        tension: 0.4,\n");
        html.append("        fill: false,\n");
        html.append("        yAxisID: 'y'\n");
        html.append("      }\n");
        html.append("    ]\n");
        html.append("  },\n");
        html.append("  options: {\n");
        html.append("    responsive: true,\n");
        html.append("    maintainAspectRatio: false,\n");
        html.append("    plugins: {\n");
        html.append("      title: { display: true, text: 'Monitoring Tekanan Darah' }\n");
        html.append("    },\n");
        html.append("    scales: {\n");
        html.append("      y: { beginAtZero: false, min: 40, max: 200 }\n");
        html.append("    }\n");
        html.append("  }\n");
        html.append("});\n");

        html.append("</script>\n");

        return html.toString();
    }

    private String toJSArray(java.util.List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("'").append(list.get(i).replace("'", "\\'")).append("'");
        }
        sb.append("]");
        return sb.toString();
    }

    private String nvl(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }

    private void saveTempHTMLAndOpen(String htmlContent) {
        try {
            // Create temp file
            java.io.File tempFile = java.io.File.createTempFile("laporan_anestesi_", ".html");
            tempFile.deleteOnExit();

            // Write HTML content
            java.io.FileWriter writer = new java.io.FileWriter(tempFile);
            writer.write(htmlContent);
            writer.close();

            // Open in browser
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(tempFile.toURI());
                JOptionPane.showMessageDialog(null, "Laporan HTML berhasil dibuat dan dibuka di browser!");
            } else {
                JOptionPane.showMessageDialog(null, "File disimpan di: " + tempFile.getAbsolutePath());
            }

        } catch (Exception e) {
            System.out.println("Error saveTempHTMLAndOpen: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error menyimpan file HTML: " + e.getMessage());
        }
    }

}
