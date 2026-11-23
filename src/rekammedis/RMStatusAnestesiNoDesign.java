/*
 * By Mas Elkhanza
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMStatusAnestesiNoDesign extends javax.swing.JDialog {

    private DefaultTableModel tabMode;
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
    private DefaultTableModel tabModeMonitoring;
    private DefaultTableModel tabModeCatatan;
    private DefaultTableModel tabModeMasukKamar;
    private DefaultTableModel tabModeObservasiKamar;
    private DefaultTableModel tabModeKeluarKamar;
    private DefaultTableModel tabModeInstruksiPasca;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMStatusAnestesiNoDesign(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

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

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Dokter", "Nama Dokter", "Tanggal", "Tgl.Operasi", "Diagnosa", "Rencana Tindakan",
            "TB", "BB", "TD", "IO2", "Nadi", "Pernapasan", "Suhu", "Asesmen Fisik Cardiovasculer", "Asesmen Fisik Paru", "Asesmen Fisik Abdomen",
            "Asesmen Fisik Extrimitas", "Asesmen Fisik Endokrin", "Asesmen Fisik Ginjal", "Asesmen Fisik Obat-obatan", "Asesmen Fisik Laborat",
            "Asesmen Fisik Penunjang", "Riwayat Penyakit Alergi Obat", "Riwayat Penyakit Alergi Lainnya", "Riwayat Penyakit Terapi",
            "Kebiasaan Merokok", "Jml.Rokok", "Kebiasaan Alkohol", "Jml.Alko", "Penggunaan Obat", "Obat Dikonsumsi", "Riwayat Medis Cardiovasculer",
            "Riwayat Medis Respiratory", "Riwayat Medis Endocrine", "Riwayat Medis Lainnya", "Angka ASA", "Mulai Puasa", "Rencana Anestesi",
            "Rencana Perawatan", "Catatan Khusus"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

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
                        KdAsistenAnestesi1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmAsistenAnestasi1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    } else if (pilihan == 2) {
                        KdAsistenAnestesi.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmAsistenAnestasi.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    } else if (pilihan == 3) {
                        KdAsistenBedah.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmAsistenBeda.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    }

                }
                KdAsistenAnestesi1.requestFocus();
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
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
        JenisPembedahan = new widget.TextBox();
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
        ChkInput2 = new widget.CekBox();
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
        NamaObatInduksi1 = new widget.TextBox();
        Induksi1 = new widget.ComboBox();
        JenisPremedikasi1 = new widget.ComboBox();
        jLabel122 = new widget.Label();
        jLabel123 = new widget.Label();
        jLabel140 = new widget.Label();
        jLabel141 = new widget.Label();
        NmObatPremedikasi2 = new widget.TextBox();
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
        Pernapasan11 = new widget.ComboBox();
        DiagnosaPrabedah1 = new widget.TextBox();
        DiagnosaPrabedah2 = new widget.TextBox();
        jLabel71 = new widget.Label();
        DiagnosaPrabedah4 = new widget.TextBox();
        jLabel80 = new widget.Label();
        Pernapasan13 = new widget.ComboBox();
        jLabel72 = new widget.Label();
        DiagnosaPrabedah3 = new widget.TextBox();
        DiagnosaPrabedah5 = new widget.TextBox();
        Pernapasan12 = new widget.ComboBox();
        jLabel144 = new widget.Label();
        jLabel145 = new widget.Label();
        jLabel164 = new widget.Label();
        jLabel165 = new widget.Label();
        jLabel166 = new widget.Label();
        jLabel167 = new widget.Label();
        jLabel168 = new widget.Label();
        jLabel169 = new widget.Label();
        jLabel170 = new widget.Label();
        DiagnosaPrabedah6 = new widget.TextBox();
        jLabel171 = new widget.Label();
        DiagnosaPrabedah7 = new widget.TextBox();
        jLabel172 = new widget.Label();
        jLabel173 = new widget.Label();
        DiagnosaPrabedah8 = new widget.TextBox();
        DiagnosaPrabedah9 = new widget.TextBox();
        jLabel174 = new widget.Label();
        jLabel175 = new widget.Label();
        jLabel176 = new widget.Label();
        jLabel177 = new widget.Label();
        jLabel178 = new widget.Label();
        jLabel179 = new widget.Label();
        DiagnosaPrabedah10 = new widget.TextBox();
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
        tbAnestesi6 = new widget.Table();
        PanelInput7 = new javax.swing.JPanel();
        FormInput15 = new widget.PanelBiasa();
        label12 = new widget.Label();
        MasukKamarPulih = new widget.Tanggal();
        jLabel31 = new widget.Label();
        Kesadaran = new widget.ComboBox();
        jLabel12 = new widget.Label();
        jLabel183 = new widget.Label();
        Tensi1 = new widget.TextBox();
        jLabel187 = new widget.Label();
        jLabel188 = new widget.Label();
        Nadi1 = new widget.TextBox();
        jLabel189 = new widget.Label();
        jLabel190 = new widget.Label();
        Support3 = new widget.TextBox();
        jLabel191 = new widget.Label();
        jLabel192 = new widget.Label();
        Support4 = new widget.TextBox();
        jLabel193 = new widget.Label();
        jLabel194 = new widget.Label();
        cmbRespirasi1 = new widget.ComboBox();
        jLabel195 = new widget.Label();
        Rr1 = new widget.TextBox();
        jLabel196 = new widget.Label();
        jLabel197 = new widget.Label();
        Kanul1 = new widget.ComboBox();
        jLabel198 = new widget.Label();
        O3 = new widget.TextBox();
        jLabel199 = new widget.Label();
        jLabel200 = new widget.Label();
        Spo3 = new widget.TextBox();
        Rr3 = new widget.TextBox();
        jLabel201 = new widget.Label();
        jLabel202 = new widget.Label();
        Support5 = new widget.TextBox();
        Support6 = new widget.TextBox();
        jLabel203 = new widget.Label();
        ChkInput7 = new widget.CekBox();
        ObservasiKamar = new widget.InternalFrame();
        Scroll8 = new widget.ScrollPane();
        tbAnestesi7 = new widget.Table();
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
        KdAsistenAnestesi1 = new widget.TextBox();
        NmAsistenAnestasi1 = new widget.TextBox();
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

        JenisPembedahan.setFocusTraversalPolicyProvider(true);
        JenisPembedahan.setName("JenisPembedahan"); // NOI18N
        JenisPembedahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JenisPembedahanActionPerformed(evt);
            }
        });
        JenisPembedahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisPembedahanKeyPressed(evt);
            }
        });
        FormInput.add(JenisPembedahan);
        JenisPembedahan.setBounds(110, 70, 280, 23);

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
        PanelInput2.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput10.setBackground(new java.awt.Color(250, 255, 245));
        FormInput10.setName("FormInput10"); // NOI18N
        FormInput10.setPreferredSize(new java.awt.Dimension(100, 225));
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

        PanelInput2.add(FormInput10, java.awt.BorderLayout.CENTER);

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

        Prainduksi.add(PanelInput2, java.awt.BorderLayout.PAGE_START);

        TabRawat.addTab("Pra Induksi", Prainduksi);

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

        NamaObatInduksi1.setFocusTraversalPolicyProvider(true);
        NamaObatInduksi1.setName("NamaObatInduksi1"); // NOI18N
        NamaObatInduksi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NamaObatInduksi1ActionPerformed(evt);
            }
        });
        NamaObatInduksi1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaObatInduksi1KeyPressed(evt);
            }
        });
        FormInput11.add(NamaObatInduksi1);
        NamaObatInduksi1.setBounds(230, 40, 440, 23);

        Induksi1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Anestesi umum", "Spinal", "Kaudal", "Blok perifer", "Epidural", "Lain-lain" }));
        Induksi1.setName("Induksi1"); // NOI18N
        FormInput11.add(Induksi1);
        Induksi1.setBounds(90, 40, 120, 20);

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

        NmObatPremedikasi2.setFocusTraversalPolicyProvider(true);
        NmObatPremedikasi2.setName("NmObatPremedikasi2"); // NOI18N
        NmObatPremedikasi2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NmObatPremedikasi2KeyPressed(evt);
            }
        });
        FormInput11.add(NmObatPremedikasi2);
        NmObatPremedikasi2.setBounds(500, 10, 170, 23);

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

        TabRawat.addTab("Premedikasi", Premedikasi);

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

        Fio2.setEditable(false);
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

        AnestesiUmum.addTab("Monitoring", ObservasiKamar1);

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

        AnestesiUmum.addTab("Keterangan", AnastesiUmumKeterangan);

        TabRawat.addTab("Anestesi Umum", AnestesiUmum);

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
        PanelInput13.setOpaque(false);
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

        Pernapasan11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Balon", "Tanpa balon" }));
        Pernapasan11.setName("Pernapasan11"); // NOI18N
        FormInput12.add(Pernapasan11);
        Pernapasan11.setBounds(650, 10, 110, 20);

        DiagnosaPrabedah1.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah1.setName("DiagnosaPrabedah1"); // NOI18N
        DiagnosaPrabedah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah1ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah1KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah1);
        DiagnosaPrabedah1.setBounds(550, 10, 90, 23);

        DiagnosaPrabedah2.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah2.setName("DiagnosaPrabedah2"); // NOI18N
        DiagnosaPrabedah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah2ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah2KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah2);
        DiagnosaPrabedah2.setBounds(90, 110, 360, 23);

        jLabel71.setText("jam");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput12.add(jLabel71);
        jLabel71.setBounds(440, 40, 30, 23);

        DiagnosaPrabedah4.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah4.setName("DiagnosaPrabedah4"); // NOI18N
        DiagnosaPrabedah4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah4ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah4KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah4);
        DiagnosaPrabedah4.setBounds(100, 40, 80, 23);

        jLabel80.setText("Letak Penderita:");
        jLabel80.setName("jLabel80"); // NOI18N
        FormInput12.add(jLabel80);
        jLabel80.setBounds(10, 10, 80, 23);

        Pernapasan13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Supine", "Prone", "Litotomi", "Semi sitting", "Flowler", "Lateral dekubitus", "Right dekubitus", "Left dekubitus", "Trendelenberg", "Knee-chest", "Jack-knife" }));
        Pernapasan13.setName("Pernapasan13"); // NOI18N
        FormInput12.add(Pernapasan13);
        Pernapasan13.setBounds(100, 10, 130, 20);

        jLabel72.setText("Lama Anestesi:");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput12.add(jLabel72);
        jLabel72.setBounds(270, 40, 80, 23);

        DiagnosaPrabedah3.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah3.setName("DiagnosaPrabedah3"); // NOI18N
        DiagnosaPrabedah3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah3ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah3KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah3);
        DiagnosaPrabedah3.setBounds(360, 40, 80, 23);

        DiagnosaPrabedah5.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah5.setName("DiagnosaPrabedah5"); // NOI18N
        DiagnosaPrabedah5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah5ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah5KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah5);
        DiagnosaPrabedah5.setBounds(90, 140, 360, 23);

        Pernapasan12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Face mask", "LMA", "Single lumen ETT", "Single lumen spiral ETT", "Double lumen ETT R", "Double Lumen ETT L", "Nasal kanul" }));
        Pernapasan12.setName("Pernapasan12"); // NOI18N
        FormInput12.add(Pernapasan12);
        Pernapasan12.setBounds(350, 10, 110, 20);

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

        DiagnosaPrabedah6.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah6.setName("DiagnosaPrabedah6"); // NOI18N
        DiagnosaPrabedah6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah6ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah6KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah6);
        DiagnosaPrabedah6.setBounds(90, 170, 360, 23);

        jLabel171.setText("4. Komponen Darah");
        jLabel171.setName("jLabel171"); // NOI18N
        FormInput12.add(jLabel171);
        jLabel171.setBounds(-20, 200, 100, 23);

        DiagnosaPrabedah7.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah7.setName("DiagnosaPrabedah7"); // NOI18N
        DiagnosaPrabedah7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah7ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah7KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah7);
        DiagnosaPrabedah7.setBounds(90, 200, 360, 23);

        jLabel172.setText("ml");
        jLabel172.setName("jLabel172"); // NOI18N
        FormInput12.add(jLabel172);
        jLabel172.setBounds(440, 170, 30, 23);

        jLabel173.setText("ml");
        jLabel173.setName("jLabel173"); // NOI18N
        FormInput12.add(jLabel173);
        jLabel173.setBounds(440, 200, 30, 23);

        DiagnosaPrabedah8.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah8.setName("DiagnosaPrabedah8"); // NOI18N
        DiagnosaPrabedah8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah8ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah8KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah8);
        DiagnosaPrabedah8.setBounds(590, 110, 360, 23);

        DiagnosaPrabedah9.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah9.setName("DiagnosaPrabedah9"); // NOI18N
        DiagnosaPrabedah9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah9ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah9KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah9);
        DiagnosaPrabedah9.setBounds(590, 140, 360, 23);

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

        DiagnosaPrabedah10.setFocusTraversalPolicyProvider(true);
        DiagnosaPrabedah10.setName("DiagnosaPrabedah10"); // NOI18N
        DiagnosaPrabedah10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaPrabedah10ActionPerformed(evt);
            }
        });
        DiagnosaPrabedah10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaPrabedah10KeyPressed(evt);
            }
        });
        FormInput12.add(DiagnosaPrabedah10);
        DiagnosaPrabedah10.setBounds(590, 170, 360, 23);

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
        Scroll7.setViewportView(tbAnestesi6);

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
        MasukKamarPulih.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-09-2025 19:35:59" }));
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

        Tensi1.setFocusTraversalPolicyProvider(true);
        Tensi1.setName("Tensi1"); // NOI18N
        Tensi1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Tensi1KeyPressed(evt);
            }
        });
        FormInput15.add(Tensi1);
        Tensi1.setBounds(80, 60, 80, 23);

        jLabel187.setText("mmHg");
        jLabel187.setName("jLabel187"); // NOI18N
        FormInput15.add(jLabel187);
        jLabel187.setBounds(170, 60, 30, 23);

        jLabel188.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel188.setText("Nadi:");
        jLabel188.setName("jLabel188"); // NOI18N
        FormInput15.add(jLabel188);
        jLabel188.setBounds(390, 60, 40, 23);

        Nadi1.setFocusTraversalPolicyProvider(true);
        Nadi1.setName("Nadi1"); // NOI18N
        Nadi1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Nadi1KeyPressed(evt);
            }
        });
        FormInput15.add(Nadi1);
        Nadi1.setBounds(240, 60, 70, 23);

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

        cmbRespirasi1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spontan", "Asist", "Kontrol" }));
        cmbRespirasi1.setName("cmbRespirasi1"); // NOI18N
        FormInput15.add(cmbRespirasi1);
        cmbRespirasi1.setBounds(80, 120, 140, 20);

        jLabel195.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel195.setText("RR:");
        jLabel195.setName("jLabel195"); // NOI18N
        FormInput15.add(jLabel195);
        jLabel195.setBounds(230, 120, 50, 23);

        Rr1.setFocusTraversalPolicyProvider(true);
        Rr1.setName("Rr1"); // NOI18N
        Rr1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Rr1KeyPressed(evt);
            }
        });
        FormInput15.add(Rr1);
        Rr1.setBounds(260, 120, 50, 23);

        jLabel196.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel196.setText("x/menit");
        jLabel196.setName("jLabel196"); // NOI18N
        FormInput15.add(jLabel196);
        jLabel196.setBounds(320, 120, 60, 23);

        jLabel197.setText("Kanul Nasal:");
        jLabel197.setName("jLabel197"); // NOI18N
        FormInput15.add(jLabel197);
        jLabel197.setBounds(360, 120, 80, 23);

        Kanul1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SM", "SMNR" }));
        Kanul1.setName("Kanul1"); // NOI18N
        FormInput15.add(Kanul1);
        Kanul1.setBounds(450, 120, 140, 20);

        jLabel198.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel198.setText("O2:");
        jLabel198.setName("jLabel198"); // NOI18N
        FormInput15.add(jLabel198);
        jLabel198.setBounds(610, 120, 50, 23);

        O3.setFocusTraversalPolicyProvider(true);
        O3.setName("O3"); // NOI18N
        O3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                O3KeyPressed(evt);
            }
        });
        FormInput15.add(O3);
        O3.setBounds(640, 120, 50, 23);

        jLabel199.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel199.setText("l/mnt");
        jLabel199.setName("jLabel199"); // NOI18N
        FormInput15.add(jLabel199);
        jLabel199.setBounds(700, 120, 40, 23);

        jLabel200.setText("SPO2:");
        jLabel200.setName("jLabel200"); // NOI18N
        FormInput15.add(jLabel200);
        jLabel200.setBounds(750, 120, 30, 23);

        Spo3.setFocusTraversalPolicyProvider(true);
        Spo3.setName("Spo3"); // NOI18N
        Spo3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Spo3KeyPressed(evt);
            }
        });
        FormInput15.add(Spo3);
        Spo3.setBounds(790, 120, 50, 23);

        Rr3.setFocusTraversalPolicyProvider(true);
        Rr3.setName("Rr3"); // NOI18N
        Rr3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Rr3KeyPressed(evt);
            }
        });
        FormInput15.add(Rr3);
        Rr3.setBounds(420, 60, 50, 23);

        jLabel201.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel201.setText("x/menit");
        jLabel201.setName("jLabel201"); // NOI18N
        FormInput15.add(jLabel201);
        jLabel201.setBounds(480, 60, 60, 23);

        jLabel202.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel202.setText("Skor Nyeri:");
        jLabel202.setName("jLabel202"); // NOI18N
        FormInput15.add(jLabel202);
        jLabel202.setBounds(20, 150, 80, 23);

        Support5.setFocusTraversalPolicyProvider(true);
        Support5.setName("Support5"); // NOI18N
        Support5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Support5KeyPressed(evt);
            }
        });
        FormInput15.add(Support5);
        Support5.setBounds(80, 150, 330, 23);

        Support6.setFocusTraversalPolicyProvider(true);
        Support6.setName("Support6"); // NOI18N
        Support6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Support6KeyPressed(evt);
            }
        });
        FormInput15.add(Support6);
        Support6.setBounds(550, 150, 260, 23);

        jLabel203.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel203.setText("Penyulit Intraoperatif:");
        jLabel203.setName("jLabel203"); // NOI18N
        FormInput15.add(jLabel203);
        jLabel203.setBounds(420, 150, 130, 23);

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

        tbAnestesi7.setAutoCreateRowSorter(true);
        tbAnestesi7.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbAnestesi7.setName("tbAnestesi7"); // NOI18N
        tbAnestesi7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnestesi7MouseClicked(evt);
            }
        });
        tbAnestesi7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAnestesi7KeyPressed(evt);
            }
        });
        Scroll8.setViewportView(tbAnestesi7);

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

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        panelGlass10.add(jLabel10);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        panelGlass10.add(TNoRw);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        panelGlass10.add(TNoRM);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(TPasien);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        panelGlass10.add(jLabel8);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        panelGlass10.add(TglLahir);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        panelGlass10.add(jLabel11);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        panelGlass10.add(Jk);

        jLabel24.setText("Tanggal :");
        jLabel24.setName("jLabel24"); // NOI18N
        panelGlass10.add(jLabel24);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-09-2025" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        panelGlass10.add(DTPTgl);

        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam.setName("cmbJam"); // NOI18N
        cmbJam.setPreferredSize(new java.awt.Dimension(62, 28));
        cmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJamKeyPressed(evt);
            }
        });
        panelGlass10.add(cmbJam);

        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt.setName("cmbMnt"); // NOI18N
        cmbMnt.setPreferredSize(new java.awt.Dimension(62, 28));
        cmbMnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMntKeyPressed(evt);
            }
        });
        panelGlass10.add(cmbMnt);

        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDtk.setName("cmbDtk"); // NOI18N
        cmbDtk.setPreferredSize(new java.awt.Dimension(62, 28));
        cmbDtk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbDtkKeyPressed(evt);
            }
        });
        panelGlass10.add(cmbDtk);

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
        panelGlass10.add(ChkJln);

        jLabel9.setText("Ruangan:");
        jLabel9.setName("jLabel9"); // NOI18N
        panelGlass10.add(jLabel9);

        Ruangan.setEditable(false);
        Ruangan.setHighlighter(null);
        Ruangan.setName("Ruangan"); // NOI18N
        Ruangan.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(Ruangan);

        jLabel95.setText("Tgl Masuk RS:");
        jLabel95.setName("jLabel95"); // NOI18N
        panelGlass10.add(jLabel95);

        TglMasukRS.setEditable(false);
        TglMasukRS.setHighlighter(null);
        TglMasukRS.setName("TglMasukRS"); // NOI18N
        TglMasukRS.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(TglMasukRS);

        jLabel64.setText("Petugas:");
        jLabel64.setName("jLabel64"); // NOI18N
        panelGlass10.add(jLabel64);

        KdAsistenAnestesi1.setEditable(false);
        KdAsistenAnestesi1.setHighlighter(null);
        KdAsistenAnestesi1.setName("KdAsistenAnestesi1"); // NOI18N
        KdAsistenAnestesi1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdAsistenAnestesi1KeyPressed(evt);
            }
        });
        panelGlass10.add(KdAsistenAnestesi1);

        NmAsistenAnestasi1.setEditable(false);
        NmAsistenAnestasi1.setName("NmAsistenAnestasi1"); // NOI18N
        NmAsistenAnestasi1.setPreferredSize(new java.awt.Dimension(200, 24));
        panelGlass10.add(NmAsistenAnestasi1);

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
        panelGlass10.add(btnPetugas3);

        internalFrame1.add(panelGlass10, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {                                 
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            //    Valid.pindah(evt,TCari,BtnDokter);
        }
}                                

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                          
        switch (TabRawat.getSelectedIndex()) {
            case 0: // Data Awal
                simpanDataAwal();
                break;
            case 1: // Pra Induksi
                simpanPraInduksi();
                break;
            case 2: // Premedikasi
                simpanPremedikasi();
                break;
            case 3: // Anestesi Umum
                switch (AnestesiUmum.getSelectedIndex()) {
                    case 0: // Data Awal
                        simpanAnestesiUmumDataAwal();
                        break;
                    case 1: // Monitoring
                        simpanAnestesiUmumMonitoring();
                        break;
                    case 2: // Keterangan
                        simpanAnestesiUmumKeterangan();
                        break;
                }
                break;
            case 4: // Blokade Regional
                simpanBlokadeRegional();
                break;
            case 5: // Keadaan Selama Operasi
                simpanKeadaanOperasi();
                break;
            case 6: // Sectio Caesaria
                simpanSectio();
                break;
            case 7: // Catatan Kamar Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0: // Masuk Kamar
                        simpanMasukKamarPemulihan();
                        break;
                    case 1: // Monitoring
                        simpanMonitoringPemulihan();
                        break;
                }
                break;
        }
}                                         

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {                                     
        /*    if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
           Valid.pindah(evt,CatatanKhusus,BtnBatal);
        } */
}                                    

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {                                         
        emptTeks();
}                                        

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
}                                   

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {                                         
        switch (TabRawat.getSelectedIndex()) {
            case 0: // Data Awal
                hapusDataAwal();
                break;
            case 1: // Pra Induksi
                hapusPraInduksi();
                break;
            case 2: // Premedikasi
                hapusPremedikasi();
                break;
            case 3: // Anestesi Umum
                switch (AnestesiUmum.getSelectedIndex()) {
                    case 0:
                        hapusAnestesiUmumDataAwal();
                        break;
                    case 1:
                        hapusAnestesiUmumMonitoring();
                        break;
                    case 2:
                        hapusAnestesiUmumKeterangan();
                        break;
                }
                break;
            case 4: // Blokade Regional
                hapusBlokadeRegional();
                break;
            case 5: // Keadaan Selama Operasi
                hapusKeadaanOperasi();
                break;
            case 6: // Sectio Caesaria
                hapusSectio();
                break;
            case 7: // Catatan Kamar Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        hapusMasukKamarPemulihan();
                        break;
                    case 1:
                        hapusMonitoringPemulihan();
                        break;
                }
                break;
        }
}                                        

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}                                   

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {                                        
        switch (TabRawat.getSelectedIndex()) {
            case 0: // Data Awal
                gantiDataAwal();
                break;
            case 1: // Pra Induksi
                gantiPraInduksi();
                break;
            case 2: // Premedikasi
                gantiPremedikasi();
                break;
            case 3: // Anestesi Umum
                switch (AnestesiUmum.getSelectedIndex()) {
                    case 0:
                        gantiAnestesiUmumDataAwal();
                        break;
                    case 1:
                        gantiAnestesiUmumMonitoring();
                        break;
                    case 2:
                        gantiAnestesiUmumKeterangan();
                        break;
                }
                break;
            case 4: // Blokade Regional
                gantiBlokadeRegional();
                break;
            case 5: // Keadaan Selama Operasi
                gantiKeadaanOperasi();
                break;
            case 6: // Sectio Caesaria
                gantiSectio();
                break;
            case 7: // Catatan Kamar Pemulihan
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        gantiMasukKamarPemulihan();
                        break;
                    case 1:
                        gantiMonitoringPemulihan();
                        break;
                }
                break;
        }
}                                       

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}                                  

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dispose();
}                                         

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {                                     
        /*     if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);} */
}                                    

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {                                         
        
}                                        

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}                                   

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {                                       
        //    TCari.setText("");
        tampil();
}                                      

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {                                  
        /*    if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        } */
}                                 

    private void MasukKamarPulihKeyPressed(java.awt.event.KeyEvent evt) {                                           
        //       Valid.pindah(evt,RencanaTindakan,TB);
    }                                          

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {                                  
        //   Valid.pindah(evt,BtnSeekDokter,cmbJam);
    }                                 

    private void cmbJamKeyPressed(java.awt.event.KeyEvent evt) {                                  
        Valid.pindah(evt, DTPTgl, cmbMnt);
    }                                 

    private void cmbMntKeyPressed(java.awt.event.KeyEvent evt) {                                  
        Valid.pindah(evt, cmbJam, cmbDtk);
    }                                 

    private void cmbDtkKeyPressed(java.awt.event.KeyEvent evt) {                                  
        //   Valid.pindah(evt,cmbMnt,TCari);
    }                                 

    private void ChkJlnActionPerformed(java.awt.event.ActionEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void SuhuKeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void Rr4KeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void Nadi2KeyPressed(java.awt.event.KeyEvent evt) {                                 
        // TODO add your handling code here:
    }                                

    private void TekananDarahKeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void BtnDokter2ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        pilihan = 1;
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }                                          

    private void BtnDokter2KeyPressed(java.awt.event.KeyEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void MuskuloskletalKeyPressed(java.awt.event.KeyEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void MetabolikKeyPressed(java.awt.event.KeyEvent evt) {                                     
        // TODO add your handling code here:
    }                                    

    private void BbKeyPressed(java.awt.event.KeyEvent evt) {                              
        // TODO add your handling code here:
    }                             

    private void TensiKeyPressed(java.awt.event.KeyEvent evt) {                                 
        // TODO add your handling code here:
    }                                

    private void GastroKeyPressed(java.awt.event.KeyEvent evt) {                                  
        // TODO add your handling code here:
    }                                 

    private void KdAsistenAnestesiKeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void btnPetugas1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        pilihan = 2;
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }                                           

    private void btnPetugas1KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void DiagnosaPrabedahActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    private void DiagnosaPrabedahKeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void JenisPembedahanActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
    }                                               

    private void JenisPembedahanKeyPressed(java.awt.event.KeyEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void RespirasiActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void RespirasiKeyPressed(java.awt.event.KeyEvent evt) {                                     
        // TODO add your handling code here:
    }                                    

    private void BtnDokter3ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        pilihan = 2;
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }                                          

    private void BtnDokter3KeyPressed(java.awt.event.KeyEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void NadiKeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void O2KeyPressed(java.awt.event.KeyEvent evt) {                              
        // TODO add your handling code here:
    }                             

    private void Spo2KeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void HmtKeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void SarafActionPerformed(java.awt.event.ActionEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void SarafKeyPressed(java.awt.event.KeyEvent evt) {                                 
        // TODO add your handling code here:
    }                                

    private void KdAsistenBedahKeyPressed(java.awt.event.KeyEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void btnPetugas2ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        pilihan = 3;
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }                                           

    private void btnPetugas2KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void JenisAnestesiActionPerformed(java.awt.event.ActionEvent evt) {                                              
        // TODO add your handling code here:
    }                                             

    private void JenisAnestesiKeyPressed(java.awt.event.KeyEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void PernapasanMasuk19KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void SirkulasiMasuk14KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void PernapasanMasuk12KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void SirkulasiMasuk15KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void PernapasanMasuk14KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void SirkulasiMasuk16KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void PernapasanMasuk16KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void SirkulasiMasuk17KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void PernapasanMasuk20KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void SirkulasiMasuk18KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void SirkulasiMasuk19KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void SirkulasiMasuk21KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void NmObatPremedikasi1KeyPressed(java.awt.event.KeyEvent evt) {                                              
        // TODO add your handling code here:
    }                                             

    private void NamaObatInduksi1ActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    private void NamaObatInduksi1KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void PemantauanNadi2ActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
    }                                               

    private void PemantauanNadi2KeyPressed(java.awt.event.KeyEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void Lainnya2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void Lainnya2KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void Selama2ActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void Selama2KeyPressed(java.awt.event.KeyEvent evt) {                                   
        // TODO add your handling code here:
    }                                  

    private void SirkulasiMasuk22KeyPressed(java.awt.event.KeyEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void LokasiTusukanKeyPressed(java.awt.event.KeyEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void AnalgesiSetinggiSegmenKeyPressed(java.awt.event.KeyEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void KdAsistenAnestesi1KeyPressed(java.awt.event.KeyEvent evt) {                                              
        // TODO add your handling code here:
    }                                             

    private void btnPetugas3ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        pilihan = 1;
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }                                           

    private void btnPetugas3KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void DiagnosaPrabedah1ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah1KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah2ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah2KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah3ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah3KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah4ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah4KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah5ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah5KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void tbAnestesiMouseClicked(java.awt.event.MouseEvent evt) {                                        
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                       

    private void tbAnestesiKeyPressed(java.awt.event.KeyEvent evt) {                                      
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }                                     

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {                                         
        isForm();
    }                                        

    private void tbAnestesi1MouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                        

    private void tbAnestesi1KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void ChkInput2ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi2MouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                        

    private void tbAnestesi2KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void ChkInput3ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi3MouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                        

    private void tbAnestesi3KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void ChkInput4ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi4MouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                        

    private void tbAnestesi4KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void ChkInput5ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi6MouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                        

    private void tbAnestesi6KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void ChkInput7ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi7MouseClicked(java.awt.event.MouseEvent evt) {                                         
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                        

    private void tbAnestesi7KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void ChkInput8ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {                                 
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }                                

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {                                        
        tampil();
    }                                       

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }                                  

    private void BtnAll1ActionPerformed(java.awt.event.ActionEvent evt) {                                        
        TCari.setText("");
        tampil();
    }                                       

    private void BtnAll1KeyPressed(java.awt.event.KeyEvent evt) {                                   
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            tampil();
            TCari.setText("");
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }                                  

    private void txtKesadaranKeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void GCSKeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void Support1KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void Support2KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void RrKeyPressed(java.awt.event.KeyEvent evt) {                              
        // TODO add your handling code here:
    }                             

    private void UrinariaKeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void LainKeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void NmObatPremedikasi2KeyPressed(java.awt.event.KeyEvent evt) {                                              
        // TODO add your handling code here:
    }                                             

    private void tbAnestesi9MouseClicked(java.awt.event.MouseEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void tbAnestesi9KeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void InduksiKetKeyPressed(java.awt.event.KeyEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void TidalVolumKeyPressed(java.awt.event.KeyEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void Rr2KeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void IeratioKeyPressed(java.awt.event.KeyEvent evt) {                                   
        // TODO add your handling code here:
    }                                  

    private void PeepKeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void TeknikKhususKetKeyPressed(java.awt.event.KeyEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void ChkInput10ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void tbAnestesi10MouseClicked(java.awt.event.MouseEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi10KeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void ChkInput11ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void PipKeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void tbAnestesi11MouseClicked(java.awt.event.MouseEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi11KeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void ChkInput12ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void tbAnestesi12MouseClicked(java.awt.event.MouseEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void tbAnestesi12KeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void ChkInput13ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void AnestesiLokalKeyPressed(java.awt.event.KeyEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void KonsentrasiJumlahKeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void ObatTambahanKeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void DosisKeyPressed(java.awt.event.KeyEvent evt) {                                 
        // TODO add your handling code here:
    }                                

    private void KonsentrasiKeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void WaktuMulaiSuntikJamKeyPressed(java.awt.event.KeyEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void AnalgesiJamKeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void LamanyaKeyPressed(java.awt.event.KeyEvent evt) {                                   
        // TODO add your handling code here:
    }                                  

    private void LamanyaMenitAnalgesiKeyPressed(java.awt.event.KeyEvent evt) {                                                
        // TODO add your handling code here:
    }                                               

    private void OperasiJamKeyPressed(java.awt.event.KeyEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void LamanyajamOperasiKeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void LamanyaJamMenitKeyPressed(java.awt.event.KeyEvent evt) {                                           
        // TODO add your handling code here:
    }                                          

    private void TindakanAnestesiTambahanKeyPressed(java.awt.event.KeyEvent evt) {                                                    
        // TODO add your handling code here:
    }                                                   

    private void DiagnosaPrabedah6ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah6KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah7ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah7KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah8ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah8KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah9ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }                                                 

    private void DiagnosaPrabedah9KeyPressed(java.awt.event.KeyEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void DiagnosaPrabedah10ActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        // TODO add your handling code here:
    }                                                  

    private void DiagnosaPrabedah10KeyPressed(java.awt.event.KeyEvent evt) {                                              
        // TODO add your handling code here:
    }                                             

    private void Apgrar1MenitActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void Apgrar1MenitKeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void Apgar5MenitActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void Apgar5MenitKeyPressed(java.awt.event.KeyEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void Apgar10MenitActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
    }                                            

    private void Apgar10MenitKeyPressed(java.awt.event.KeyEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void Tensi1KeyPressed(java.awt.event.KeyEvent evt) {                                  
        // TODO add your handling code here:
    }                                 

    private void Nadi1KeyPressed(java.awt.event.KeyEvent evt) {                                 
        // TODO add your handling code here:
    }                                

    private void Support3KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void Support4KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void Rr1KeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void O3KeyPressed(java.awt.event.KeyEvent evt) {                              
        // TODO add your handling code here:
    }                             

    private void Spo3KeyPressed(java.awt.event.KeyEvent evt) {                                
        // TODO add your handling code here:
    }                               

    private void Rr3KeyPressed(java.awt.event.KeyEvent evt) {                               
        // TODO add your handling code here:
    }                              

    private void Support5KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    private void Support6KeyPressed(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
    }                                   

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMStatusAnestesiNoDesign dialog = new RMStatusAnestesiNoDesign(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private widget.ComboBox ASA;
    private widget.ComboBox Airway;
    private widget.ComboBox Alergi;
    private widget.TextBox AnalgesiJam;
    private widget.TextBox AnalgesiSetinggiSegmen;
    private widget.InternalFrame AnastesiUmumKeterangan;
    private widget.TextBox AnestesiLokal;
    private javax.swing.JTabbedPane AnestesiUmum;
    private widget.TextBox Apgar10Menit;
    private widget.TextBox Apgar5Menit;
    private widget.TextBox Apgrar1Menit;
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
    private widget.Tanggal DTPTgl;
    private widget.InternalFrame DataAwal;
    private widget.TextBox DiagnosaPrabedah;
    private widget.TextBox DiagnosaPrabedah1;
    private widget.TextBox DiagnosaPrabedah10;
    private widget.TextBox DiagnosaPrabedah2;
    private widget.TextBox DiagnosaPrabedah3;
    private widget.TextBox DiagnosaPrabedah4;
    private widget.TextBox DiagnosaPrabedah5;
    private widget.TextBox DiagnosaPrabedah6;
    private widget.TextBox DiagnosaPrabedah7;
    private widget.TextBox DiagnosaPrabedah8;
    private widget.TextBox DiagnosaPrabedah9;
    private widget.TextBox Dosis;
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
    private widget.ComboBox Induksi1;
    private widget.TextBox InduksiKet;
    private widget.ComboBox InformedConsent;
    private widget.TextBox JenisAnestesi;
    private widget.ComboBox JenisKelamin;
    private widget.TextBox JenisPembedahan;
    private widget.ComboBox JenisPremedikasi1;
    private widget.TextBox Jk;
    private widget.ComboBox Kanul;
    private widget.ComboBox Kanul1;
    private widget.TextBox KdAsistenAnestesi;
    private widget.TextBox KdAsistenAnestesi1;
    private widget.TextBox KdAsistenBedah;
    private widget.TextBox KdDokterAnastesi;
    private widget.TextBox KdDokterBedah;
    private widget.ComboBox KeadaanBayi;
    private widget.ComboBox Kesadaran;
    private widget.TextBox Konsentrasi;
    private widget.TextBox KonsentrasiJumlah;
    private widget.TextArea Lab;
    private widget.TextBox Lain;
    private widget.TextBox Lainnya2;
    private widget.TextBox Lamanya;
    private widget.TextBox LamanyaJamMenit;
    private widget.TextBox LamanyaMenitAnalgesi;
    private widget.TextBox LamanyajamOperasi;
    private widget.editorpane LoadHTML;
    private widget.TextBox LokasiTusukan;
    private widget.TextArea MasalahDuranteOperasi;
    private widget.InternalFrame MasukKamar;
    private widget.InternalFrame MasukKamar1;
    private widget.InternalFrame MasukKamar2;
    private widget.Tanggal MasukKamarPulih;
    private widget.TextArea Medikasi;
    private widget.ComboBox MesinAnestesi;
    private widget.TextBox Metabolik;
    private widget.InternalFrame Monitoring;
    private widget.TextBox Muskuloskletal;
    private widget.TextBox Nadi;
    private widget.TextBox Nadi1;
    private widget.TextBox Nadi2;
    private widget.TextBox NamaObatInduksi1;
    private widget.TextBox NmAsistenAnestasi;
    private widget.TextBox NmAsistenAnestasi1;
    private widget.TextBox NmAsistenBeda;
    private widget.TextBox NmDokterAnestesi;
    private widget.TextBox NmDokterBedah;
    private widget.TextBox NmObatPremedikasi1;
    private widget.TextBox NmObatPremedikasi2;
    private widget.TextBox O2;
    private widget.TextBox O3;
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
    private widget.ComboBox Pernapasan11;
    private widget.ComboBox Pernapasan12;
    private widget.ComboBox Pernapasan13;
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
    private widget.TextBox Rr1;
    private widget.TextBox Rr2;
    private widget.TextBox Rr3;
    private widget.TextBox Rr4;
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
    private widget.TextBox Spo2;
    private widget.TextBox Spo3;
    private widget.ComboBox SuctionAparatus;
    private widget.TextBox Suhu;
    private widget.TextBox Support1;
    private widget.TextBox Support2;
    private widget.TextBox Support3;
    private widget.TextBox Support4;
    private widget.TextBox Support5;
    private widget.TextBox Support6;
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
    private widget.TextBox Tensi1;
    private widget.TextBox TglLahir;
    private widget.TextBox TglMasukRS;
    private widget.TextArea Thorax;
    private widget.TextBox TidalVolum;
    private widget.TextArea Tindakan;
    private widget.TextBox TindakanAnestesiTambahan;
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
    private widget.ComboBox cmbRespirasi1;
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
    private widget.Label jLabel195;
    private widget.Label jLabel196;
    private widget.Label jLabel197;
    private widget.Label jLabel198;
    private widget.Label jLabel199;
    private widget.Label jLabel200;
    private widget.Label jLabel201;
    private widget.Label jLabel202;
    private widget.Label jLabel203;
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
    private widget.Table tbAnestesi6;
    private widget.Table tbAnestesi7;
    private widget.Table tbAnestesi9;
    private widget.TextBox txtKesadaran;
    // End of variables declaration                   

    public void tampil() {
        setupTableBasedOnActiveTab();

        switch (TabRawat.getSelectedIndex()) {
            case 0:
                tampilDataAwal();
                break;
            case 1:
                tampilPraInduksi();
                break;
            case 2:
                tampilPremedikasi();
                break;
            case 3:
                switch (AnestesiUmum.getSelectedIndex()) {
                    case 0:
                        tampilAnestesiUmumDataAwal();
                        break;
                    case 1:
                        tampilAnestesiUmumMonitoring();
                        break;
                    case 2:
                        tampilAnestesiUmumKeterangan();
                        break;
                }
                break;
            case 4:
                tampilBlokadeRegional();
                break;
            case 5:
                tampilKeadaanOperasi();
                break;
            case 6:
                tampilSectio();
                break;
            case 7:
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        tampilMasukKamarPemulihan();
                        break;
                    case 1:
                        tampilMonitoringPemulihan();
                        break;
                }
                break;
        }
    }

    private void tampilDataAwal() {
        if (tabMode == null) {
            setupTableDataAwal();
        }

        Valid.tabelKosong(tabMode);
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
                    + "oda.jenis_pembedahan, oda.jenis_anestesi, oda.diagnosa_prabedah, "
                    + "oda.informed_consent, oda.obat_anestesi, oda.tatalaksana_jalan_nafas, "
                    + "oda.mesin_anestesi, oda.obat_emergensi, oda.check_monitoring, oda.suction_aparatus "
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
                    + (TCari.getText().trim().isEmpty() ? "" : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
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
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("tgl_registrasi"),
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : "",
                    rs.getString("kd_dokter_anestesi") != null ? rs.getString("kd_dokter_anestesi") : "",
                    rs.getString("nm_dokter_anestesi") != null ? rs.getString("nm_dokter_anestesi") : "",
                    rs.getString("kd_asisten_anestesi") != null ? rs.getString("kd_asisten_anestesi") : "",
                    rs.getString("nm_asisten_anestesi") != null ? rs.getString("nm_asisten_anestesi") : "",
                    rs.getString("kd_dokter_bedah") != null ? rs.getString("kd_dokter_bedah") : "",
                    rs.getString("nm_dokter_bedah") != null ? rs.getString("nm_dokter_bedah") : "",
                    rs.getString("kd_asisten_bedah") != null ? rs.getString("kd_asisten_bedah") : "",
                    rs.getString("nm_asisten_bedah") != null ? rs.getString("nm_asisten_bedah") : "",
                    rs.getString("jenis_pembedahan") != null ? rs.getString("jenis_pembedahan") : "",
                    rs.getString("jenis_anestesi") != null ? rs.getString("jenis_anestesi") : "",
                    rs.getString("diagnosa_prabedah") != null ? rs.getString("diagnosa_prabedah") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilDataAwal: " + e);
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

    private void tampilPraInduksi() {
        if (tabMode == null) {
            setupTablePraInduksi();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT opi.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, opi.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "opi.kesadaran, opi.gcs, opi.airway, opi.support1, opi.support2, "
                    + "opi.respirasi, opi.rr, opi.kanul_nasal, opi.o2, opi.bb, opi.puasa_jam, "
                    + "opi.tensi, opi.nadi, opi.spo2, opi.laboratorium, opi.ekg, opi.thorax_foto, "
                    + "opi.tfp, opi.pemeriksaan_lain, opi.sistem_saraf, opi.sistem_respirasi, "
                    + "opi.sistem_gastro, opi.sistem_metabolik, opi.sistem_urinaria, "
                    + "opi.sistem_muskuloskeletal, opi.lain_lain, opi.asa, opi.alergi, "
                    + "opi.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_pra_induksi opi "
                    + "INNER JOIN reg_periksa rp ON opi.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON opi.kd_petugas = p.nip "
                    + "WHERE opi.tanggal BETWEEN ? AND ? "
                    + (TCari.getText().trim().isEmpty() ? "" : "AND (rp.no_rawat LIKE ? OR pas.nm_pasien LIKE ?) ")
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
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("kesadaran") != null ? rs.getString("kesadaran") : "",
                    rs.getString("gcs") != null ? rs.getString("gcs") : "",
                    rs.getString("airway") != null ? rs.getString("airway") : "",
                    rs.getString("support1") != null ? rs.getString("support1") : "",
                    rs.getString("support2") != null ? rs.getString("support2") : "",
                    rs.getString("respirasi") != null ? rs.getString("respirasi") : "",
                    rs.getString("rr") != null ? rs.getString("rr") : "",
                    rs.getString("kanul_nasal") != null ? rs.getString("kanul_nasal") : "",
                    rs.getString("o2") != null ? rs.getString("o2") : "",
                    rs.getString("bb") != null ? rs.getString("bb") : "",
                    rs.getString("puasa_jam") != null ? rs.getString("puasa_jam") : "",
                    rs.getString("tensi") != null ? rs.getString("tensi") : "",
                    rs.getString("nadi") != null ? rs.getString("nadi") : "",
                    rs.getString("spo2") != null ? rs.getString("spo2") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilPraInduksi: " + e);
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

    private void tampilPremedikasi() {
        if (tabMode == null) {
            setupTablePremedikasi();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oap.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oap.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oap.jenis_premedikasi, oap.nm_obat_premedikasi, oap.induksi, oap.nama_obat_induksi, "
                    + "oap.posisi, oap.face_mask, oap.ett, oap.lm, oap.trakeostomi, oap.lain_lain, oap.intubasi, "
                    + "oap.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_premedikasi oap "
                    + "INNER JOIN reg_periksa rp ON oap.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oap.kd_petugas = p.nip "
                    + "WHERE oap.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oap.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("jenis_premedikasi"),
                    rs.getString("nm_obat_premedikasi"),
                    rs.getString("induksi"),
                    rs.getString("nama_obat_induksi"),
                    rs.getString("posisi"),
                    rs.getString("face_mask"),
                    rs.getString("ett"),
                    rs.getString("lm"),
                    rs.getString("trakeostomi"),
                    rs.getString("lain_lain"),
                    rs.getString("intubasi"),
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

    private void tampilMonitoring() {
        if (tabMode == null) {
            setupTableMonitoring();
        }

        Valid.tabelKosong(tabMode);
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
                tabMode.addRow(new String[]{
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
        if (tabMode == null) {
            setupTableCatatan();
        }

        Valid.tabelKosong(tabMode);
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
                tabMode.addRow(new String[]{
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
        if (tabMode == null) {
            setupTableMasukKamar();
        }

        Valid.tabelKosong(tabMode);
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
                tabMode.addRow(new String[]{
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
        if (tabMode == null) {
            setupTableObservasiKamar();
        }

        Valid.tabelKosong(tabMode);
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
                tabMode.addRow(new String[]{
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
        if (tabMode == null) {
            setupTableKeluarKamar();
        }

        Valid.tabelKosong(tabMode);
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
                tabMode.addRow(new String[]{
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
        if (tabMode == null) {
            setupTableInstruksiPasca();
        }

        Valid.tabelKosong(tabMode);
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
                tabMode.addRow(new String[]{
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
        // Data Awal
        KdDokterAnastesi.setText("");
        NmDokterAnestesi.setText("");
        KdAsistenAnestesi.setText("");
        NmAsistenAnestasi.setText("");
        DiagnosaPrabedah.setText("");
        JenisPembedahan.setText("");
        KdDokterBedah.setText("");
        NmDokterBedah.setText("");
        KdAsistenBedah.setText("");
        NmAsistenBeda.setText("");
        JenisAnestesi.setText("");
        KdAsistenAnestesi1.setText("");
        NmAsistenAnestasi1.setText("");
        InformedConsent.setSelectedIndex(0);
        ObatAnestesi.setSelectedIndex(0);
        TatalaksanaJalanNafas.setSelectedIndex(0);
        MesinAnestesi.setSelectedIndex(0);
        ObatEmergensi.setSelectedIndex(0);
        CheckMonitoring.setSelectedIndex(0);
        SuctionAparatus.setSelectedIndex(0);

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
        NmObatPremedikasi2.setText("");
        Induksi1.setSelectedIndex(0);
        NamaObatInduksi1.setText("");
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
        Pernapasan13.setSelectedIndex(0);
        Pernapasan12.setSelectedIndex(0);
        DiagnosaPrabedah1.setText("");
        DiagnosaPrabedah2.setText("");
        DiagnosaPrabedah3.setText("");
        DiagnosaPrabedah4.setText("");
        DiagnosaPrabedah5.setText("");
        DiagnosaPrabedah6.setText("");
        DiagnosaPrabedah7.setText("");
        DiagnosaPrabedah8.setText("");
        DiagnosaPrabedah9.setText("");
        DiagnosaPrabedah10.setText("");

        // Sectio Caesaria
        KeadaanBayi.setSelectedIndex(0);
        JenisKelamin.setSelectedIndex(0);
        Apgrar1Menit.setText("");
        Apgar5Menit.setText("");
        Apgar10Menit.setText("");

        // Masuk Kamar Pemulihan
        MasukKamarPulih.setDate(new Date());
        Kesadaran.setSelectedIndex(0);
        Tensi1.setText("");
        Nadi1.setText("");
        Support3.setText("");
        Support4.setText("");
        cmbRespirasi1.setSelectedIndex(0);
        Rr1.setText("");
        Kanul1.setSelectedIndex(0);
        O3.setText("");
        Spo3.setText("");
        Rr3.setText("");
        Support5.setText("");
        Support6.setText("");

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

                // Set data umum pasien
                TNoRw.setText(getValueSafely(tbAnestesi, selectedRow, 0));
                TNoRM.setText(getValueSafely(tbAnestesi, selectedRow, 1));
                TPasien.setText(getValueSafely(tbAnestesi, selectedRow, 2));
                TglLahir.setText(getValueSafely(tbAnestesi, selectedRow, 3));
                Jk.setText(getValueSafely(tbAnestesi, selectedRow, 4));
                Ruangan.setText(getValueSafely(tbAnestesi, selectedRow, 6));
                TglMasukRS.setText(getValueSafely(tbAnestesi, selectedRow, 7));

                // Data spesifik Data Awal
                KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi, selectedRow, 8));
                NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi, selectedRow, 9));
                KdDokterAnastesi.setText(getValueSafely(tbAnestesi, selectedRow, 10));
                NmDokterAnestesi.setText(getValueSafely(tbAnestesi, selectedRow, 11));
                KdAsistenAnestesi.setText(getValueSafely(tbAnestesi, selectedRow, 12));
                NmAsistenAnestasi.setText(getValueSafely(tbAnestesi, selectedRow, 13));
                KdDokterBedah.setText(getValueSafely(tbAnestesi, selectedRow, 14));
                NmDokterBedah.setText(getValueSafely(tbAnestesi, selectedRow, 15));
                KdAsistenBedah.setText(getValueSafely(tbAnestesi, selectedRow, 16));
                NmAsistenBeda.setText(getValueSafely(tbAnestesi, selectedRow, 17));
                JenisPembedahan.setText(getValueSafely(tbAnestesi, selectedRow, 18));
                JenisAnestesi.setText(getValueSafely(tbAnestesi, selectedRow, 19));
                DiagnosaPrabedah.setText(getValueSafely(tbAnestesi, selectedRow, 20));

                // Set tanggal
                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi, selectedRow, 5));

                // Pra Induksi - Tab 1  
            } else if (currentTabIndex == 1 && tbAnestesi1.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi1.getSelectedRow();

                // Set data umum
                setCommonPatientData(tbAnestesi1, selectedRow);

                // Data spesifik Pra Induksi
                txtKesadaran.setText(getValueSafely(tbAnestesi1, selectedRow, 7));
                GCS.setText(getValueSafely(tbAnestesi1, selectedRow, 8));
                setComboBoxSafely(Airway, getValueSafely(tbAnestesi1, selectedRow, 9));
                Support1.setText(getValueSafely(tbAnestesi1, selectedRow, 10));
                Support2.setText(getValueSafely(tbAnestesi1, selectedRow, 11));
                setComboBoxSafely(cmbRespirasi, getValueSafely(tbAnestesi1, selectedRow, 12));
                Rr.setText(getValueSafely(tbAnestesi1, selectedRow, 13));
                setComboBoxSafely(Kanul, getValueSafely(tbAnestesi1, selectedRow, 14));
                O2.setText(getValueSafely(tbAnestesi1, selectedRow, 15));
                Bb.setText(getValueSafely(tbAnestesi1, selectedRow, 16));
                Hmt.setText(getValueSafely(tbAnestesi1, selectedRow, 17));
                Tensi.setText(getValueSafely(tbAnestesi1, selectedRow, 18));
                Nadi.setText(getValueSafely(tbAnestesi1, selectedRow, 19));
                Spo2.setText(getValueSafely(tbAnestesi1, selectedRow, 20));

                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi1, selectedRow, 5));

                // Premedikasi - Tab 2
            } else if (currentTabIndex == 2 && tbAnestesi2.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi2.getSelectedRow();

                setCommonPatientData(tbAnestesi2, selectedRow);

                // Data spesifik Premedikasi
                setComboBoxSafely(JenisPremedikasi1, getValueSafely(tbAnestesi2, selectedRow, 7));
                NmObatPremedikasi1.setText(getValueSafely(tbAnestesi2, selectedRow, 8));
                setComboBoxSafely(Induksi1, getValueSafely(tbAnestesi2, selectedRow, 9));
                NamaObatInduksi1.setText(getValueSafely(tbAnestesi2, selectedRow, 10));
                setComboBoxSafely(Posisi1, getValueSafely(tbAnestesi2, selectedRow, 11));
                SirkulasiMasuk15.setText(getValueSafely(tbAnestesi2, selectedRow, 12));
                SirkulasiMasuk17.setText(getValueSafely(tbAnestesi2, selectedRow, 13));
                SirkulasiMasuk16.setText(getValueSafely(tbAnestesi2, selectedRow, 14));
                PernapasanMasuk19.setText(getValueSafely(tbAnestesi2, selectedRow, 15));
                PernapasanMasuk20.setText(getValueSafely(tbAnestesi2, selectedRow, 16));
                setComboBoxSafely(Pernapasan15, getValueSafely(tbAnestesi2, selectedRow, 17));
                KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi2, selectedRow, 18));
                NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi2, selectedRow, 19));

                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi2, selectedRow, 5));

                // Anestesi Umum - Tab 3
            } else if (currentTabIndex == 3) {
                int anestesiUmumTabIndex = AnestesiUmum.getSelectedIndex();

                if (anestesiUmumTabIndex == 0 && tbAnestesi9.getSelectedRow() != -1) {
                    // Anestesi Umum Data Awal
                    int selectedRow = tbAnestesi9.getSelectedRow();
                    setCommonPatientData(tbAnestesi9, selectedRow);

                    setComboBoxSafely(Induksi, getValueSafely(tbAnestesi9, selectedRow, 7));
                    InduksiKet.setText(getValueSafely(tbAnestesi9, selectedRow, 8));
                    setComboBoxSafely(PengaturaNafas, getValueSafely(tbAnestesi9, selectedRow, 9));
                    TidalVolum.setText(getValueSafely(tbAnestesi9, selectedRow, 10));
                    Rr2.setText(getValueSafely(tbAnestesi9, selectedRow, 11));
                    Ieratio.setText(getValueSafely(tbAnestesi9, selectedRow, 12));
                    Peep.setText(getValueSafely(tbAnestesi9, selectedRow, 13));
                    Pip.setText(getValueSafely(tbAnestesi9, selectedRow, 14));
                    Fio2.setText(getValueSafely(tbAnestesi9, selectedRow, 15));
                    setComboBoxSafely(TeknikKhusus, getValueSafely(tbAnestesi9, selectedRow, 16));
                    TeknikKhususKet.setText(getValueSafely(tbAnestesi9, selectedRow, 17));
                    KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi9, selectedRow, 18));
                    NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi9, selectedRow, 19));

                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi9, selectedRow, 5));

                } else if (anestesiUmumTabIndex == 1 && tbAnestesi10.getSelectedRow() != -1) {
                    // Anestesi Umum Monitoring
                    int selectedRow = tbAnestesi10.getSelectedRow();
                    setCommonPatientData(tbAnestesi10, selectedRow);

                    PernapasanMasuk12.setText(getValueSafely(tbAnestesi10, selectedRow, 7));
                    PernapasanMasuk14.setText(getValueSafely(tbAnestesi10, selectedRow, 8));
                    PernapasanMasuk16.setText(getValueSafely(tbAnestesi10, selectedRow, 9));
                    Lainnya2.setText(getValueSafely(tbAnestesi10, selectedRow, 10));
                    SirkulasiMasuk19.setText(getValueSafely(tbAnestesi10, selectedRow, 11));
                    SirkulasiMasuk14.setText(getValueSafely(tbAnestesi10, selectedRow, 12));
                    SirkulasiMasuk21.setText(getValueSafely(tbAnestesi10, selectedRow, 13));
                    SirkulasiMasuk22.setText(getValueSafely(tbAnestesi10, selectedRow, 14));
                    PemantauanNadi2.setText(getValueSafely(tbAnestesi10, selectedRow, 15));
                    Selama2.setText(getValueSafely(tbAnestesi10, selectedRow, 16));
                    SirkulasiMasuk18.setText(getValueSafely(tbAnestesi10, selectedRow, 17));
                    KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi10, selectedRow, 20));
                    NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi10, selectedRow, 21));

                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi10, selectedRow, 5));

                } else if (anestesiUmumTabIndex == 2 && tbAnestesi11.getSelectedRow() != -1) {
                    // Anestesi Umum Keterangan
                    int selectedRow = tbAnestesi11.getSelectedRow();
                    setCommonPatientData(tbAnestesi11, selectedRow);

                    Medikasi.setText(getValueSafely(tbAnestesi11, selectedRow, 7));
                    PemberianCairan.setText(getValueSafely(tbAnestesi11, selectedRow, 8));
                    MasalahDuranteOperasi.setText(getValueSafely(tbAnestesi11, selectedRow, 9));
                    Tindakan.setText(getValueSafely(tbAnestesi11, selectedRow, 10));
                    KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi11, selectedRow, 11));
                    NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi11, selectedRow, 12));

                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi11, selectedRow, 5));
                }

                // Blokade Regional - Tab 4
            } else if (currentTabIndex == 4 && tbAnestesi12.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi12.getSelectedRow();
                setCommonPatientData(tbAnestesi12, selectedRow);

                setComboBoxSafely(Teknik, getValueSafely(tbAnestesi12, selectedRow, 7));
                LokasiTusukan.setText(getValueSafely(tbAnestesi12, selectedRow, 8));
                AnalgesiSetinggiSegmen.setText(getValueSafely(tbAnestesi12, selectedRow, 9));
                AnestesiLokal.setText(getValueSafely(tbAnestesi12, selectedRow, 10));
                KonsentrasiJumlah.setText(getValueSafely(tbAnestesi12, selectedRow, 11));
                ObatTambahan.setText(getValueSafely(tbAnestesi12, selectedRow, 12));
                Dosis.setText(getValueSafely(tbAnestesi12, selectedRow, 13));
                setComboBoxSafely(Vasokonstriktor, getValueSafely(tbAnestesi12, selectedRow, 14));
                Konsentrasi.setText(getValueSafely(tbAnestesi12, selectedRow, 15));
                WaktuMulaiSuntikJam.setText(getValueSafely(tbAnestesi12, selectedRow, 16));
                AnalgesiJam.setText(getValueSafely(tbAnestesi12, selectedRow, 17));
                Lamanya.setText(getValueSafely(tbAnestesi12, selectedRow, 18));
                LamanyaMenitAnalgesi.setText(getValueSafely(tbAnestesi12, selectedRow, 19));
                OperasiJam.setText(getValueSafely(tbAnestesi12, selectedRow, 20));
                LamanyajamOperasi.setText(getValueSafely(tbAnestesi12, selectedRow, 21));
                LamanyaJamMenit.setText(getValueSafely(tbAnestesi12, selectedRow, 22));
                TindakanAnestesiTambahan.setText(getValueSafely(tbAnestesi12, selectedRow, 23));
                KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi12, selectedRow, 24));
                NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi12, selectedRow, 25));

                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi12, selectedRow, 5));

                // Keadaan Selama Operasi - Tab 5
            } else if (currentTabIndex == 5 && tbAnestesi3.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi3.getSelectedRow();
                setCommonPatientData(tbAnestesi3, selectedRow);

                setComboBoxSafely(Pernapasan13, getValueSafely(tbAnestesi3, selectedRow, 7));
                setComboBoxSafely(Pernapasan12, getValueSafely(tbAnestesi3, selectedRow, 8));
                DiagnosaPrabedah1.setText(getValueSafely(tbAnestesi3, selectedRow, 9));
                DiagnosaPrabedah4.setText(getValueSafely(tbAnestesi3, selectedRow, 10));
                DiagnosaPrabedah3.setText(getValueSafely(tbAnestesi3, selectedRow, 11));
                DiagnosaPrabedah2.setText(getValueSafely(tbAnestesi3, selectedRow, 12));
                DiagnosaPrabedah5.setText(getValueSafely(tbAnestesi3, selectedRow, 13));
                DiagnosaPrabedah6.setText(getValueSafely(tbAnestesi3, selectedRow, 14));
                DiagnosaPrabedah7.setText(getValueSafely(tbAnestesi3, selectedRow, 15));
                DiagnosaPrabedah8.setText(getValueSafely(tbAnestesi3, selectedRow, 16));
                DiagnosaPrabedah9.setText(getValueSafely(tbAnestesi3, selectedRow, 17));
                DiagnosaPrabedah10.setText(getValueSafely(tbAnestesi3, selectedRow, 18));
                KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi3, selectedRow, 22));
                NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi3, selectedRow, 23));

                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi3, selectedRow, 5));

                // Sectio Caesaria - Tab 6
            } else if (currentTabIndex == 6 && tbAnestesi4.getSelectedRow() != -1) {
                int selectedRow = tbAnestesi4.getSelectedRow();
                setCommonPatientData(tbAnestesi4, selectedRow);

                setComboBoxSafely(KeadaanBayi, getValueSafely(tbAnestesi4, selectedRow, 7));
                setComboBoxSafely(JenisKelamin, getValueSafely(tbAnestesi4, selectedRow, 8));
                Apgrar1Menit.setText(getValueSafely(tbAnestesi4, selectedRow, 9));
                Apgar5Menit.setText(getValueSafely(tbAnestesi4, selectedRow, 10));
                Apgar10Menit.setText(getValueSafely(tbAnestesi4, selectedRow, 11));
                KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi4, selectedRow, 12));
                NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi4, selectedRow, 13));

                Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi4, selectedRow, 5));

                // Catatan Kamar Pemulihan - Tab 7
            } else if (currentTabIndex == 7) {
                int catatanTabIndex = CatatanKamarPemulihan.getSelectedIndex();

                if (catatanTabIndex == 0 && tbAnestesi6.getSelectedRow() != -1) {
                    // Masuk Kamar
                    int selectedRow = tbAnestesi6.getSelectedRow();
                    setCommonPatientData(tbAnestesi6, selectedRow);

                    Valid.SetTgl2(MasukKamarPulih, getValueSafely(tbAnestesi6, selectedRow, 7));
                    setComboBoxSafely(Kesadaran, getValueSafely(tbAnestesi6, selectedRow, 8));
                    Tensi1.setText(getValueSafely(tbAnestesi6, selectedRow, 9));
                    Nadi1.setText(getValueSafely(tbAnestesi6, selectedRow, 10));
                    Support3.setText(getValueSafely(tbAnestesi6, selectedRow, 11));
                    Support4.setText(getValueSafely(tbAnestesi6, selectedRow, 12));
                    setComboBoxSafely(cmbRespirasi1, getValueSafely(tbAnestesi6, selectedRow, 13));
                    Rr1.setText(getValueSafely(tbAnestesi6, selectedRow, 14));
                    setComboBoxSafely(Kanul1, getValueSafely(tbAnestesi6, selectedRow, 15));
                    O3.setText(getValueSafely(tbAnestesi6, selectedRow, 16));
                    Spo3.setText(getValueSafely(tbAnestesi6, selectedRow, 17));
                    Support5.setText(getValueSafely(tbAnestesi6, selectedRow, 18));
                    Support6.setText(getValueSafely(tbAnestesi6, selectedRow, 19));
                    KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi6, selectedRow, 20));
                    NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi6, selectedRow, 21));

                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi6, selectedRow, 5));

                } else if (catatanTabIndex == 1 && tbAnestesi7.getSelectedRow() != -1) {
                    // Monitoring Pemulihan
                    int selectedRow = tbAnestesi7.getSelectedRow();
                    setCommonPatientData(tbAnestesi7, selectedRow);

                    Suhu.setText(getValueSafely(tbAnestesi7, selectedRow, 7));
                    Rr4.setText(getValueSafely(tbAnestesi7, selectedRow, 8));
                    Nadi2.setText(getValueSafely(tbAnestesi7, selectedRow, 9));
                    TekananDarah.setText(getValueSafely(tbAnestesi7, selectedRow, 10));
                    KdAsistenAnestesi1.setText(getValueSafely(tbAnestesi7, selectedRow, 11));
                    NmAsistenAnestasi1.setText(getValueSafely(tbAnestesi7, selectedRow, 12));

                    Valid.SetTgl2(DTPTgl, getValueSafely(tbAnestesi7, selectedRow, 5));
                }
            }

        } catch (Exception e) {
            System.out.println("Error in getData(): " + e.getMessage());
            e.printStackTrace();
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
    private String getValueSafely(widget.Table table, int row, int col) {
        try {
            if (row >= 0 && row < table.getRowCount() && col >= 0 && col < table.getColumnCount()) {
                Object value = table.getValueAt(row, col);
                return value != null ? value.toString().trim() : "";
            }
            return "";
        } catch (Exception e) {
            System.out.println("Error getting value at row " + row + ", col " + col + ": " + e.getMessage());
            return "";
        }
    }

// Helper method untuk set ComboBox dengan aman
    private void setComboBoxSafely(javax.swing.JComboBox comboBox, String value) {
        if (comboBox != null && value != null && !value.trim().isEmpty()) {
            try {
                // Cari item yang cocok dalam ComboBox
                for (int i = 0; i < comboBox.getItemCount(); i++) {
                    if (comboBox.getItemAt(i).toString().equals(value.trim())) {
                        comboBox.setSelectedIndex(i);
                        return;
                    }
                }
                // Jika tidak ditemukan, set ke index 0 (default)
                if (comboBox.getItemCount() > 0) {
                    comboBox.setSelectedIndex(0);
                }
            } catch (Exception e) {
                System.out.println("Error setting combo box: " + e.getMessage());
                // Set ke default jika error
                if (comboBox.getItemCount() > 0) {
                    comboBox.setSelectedIndex(0);
                }
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
            PanelInput.setPreferredSize(new Dimension(WIDTH, 246));
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
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Data Awal", 17, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdDokterAnastesi.getText(),
                        KdAsistenAnestesi.getText(),
                        DiagnosaPrabedah.getText(),
                        JenisPembedahan.getText(),
                        KdDokterBedah.getText(),
                        KdAsistenBedah.getText(),
                        JenisAnestesi.getText(),
                        KdAsistenAnestesi1.getText(),
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
        if (tbAnestesi.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_data_awal", "no_rawat=? and tanggal=?",
                    "no_rawat=?,tanggal=?,kd_dokter_anestesi=?,kd_asisten_anestesi=?,diagnosa_prabedah=?,"
                    + "jenis_pembedahan=?,kd_dokter_bedah=?,kd_asisten_bedah=?,jenis_anestesi=?,kd_petugas=?,"
                    + "informed_consent=?,obat_anestesi=?,tatalaksana_jalan_nafas=?,mesin_anestesi=?,"
                    + "obat_emergensi=?,check_monitoring=?,suction_aparatus=?",
                    19, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdDokterAnastesi.getText(),
                        KdAsistenAnestesi.getText(),
                        DiagnosaPrabedah.getText(),
                        JenisPembedahan.getText(),
                        KdDokterBedah.getText(),
                        KdAsistenBedah.getText(),
                        JenisAnestesi.getText(),
                        KdAsistenAnestesi1.getText(),
                        InformedConsent.getSelectedItem().toString(),
                        ObatAnestesi.getSelectedItem().toString(),
                        TatalaksanaJalanNafas.getSelectedItem().toString(),
                        MesinAnestesi.getSelectedItem().toString(),
                        ObatEmergensi.getSelectedItem().toString(),
                        CheckMonitoring.getSelectedItem().toString(),
                        SuctionAparatus.getSelectedItem().toString(),
                        tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 0).toString(),
                        tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilDataAwal();
                emptTeks();
            }
        }
    }

// ===== CRUD PRA INDUKSI =====
    private void simpanPraInduksi() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_pra_induksi",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null",
                    "Pra Induksi", 31, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        txtKesadaran.getText(),
                        GCS.getText(),
                        Airway.getSelectedItem().toString(),
                        Support1.getText(),
                        Support2.getText(),
                        cmbRespirasi.getSelectedItem().toString(),
                        Rr.getText(),
                        Kanul.getSelectedItem().toString(),
                        O2.getText(),
                        Bb.getText(),
                        Hmt.getText(),
                        Tensi.getText(),
                        Nadi.getText(),
                        Spo2.getText(),
                        Lab.getText(),
                        Ekg.getText(),
                        Thorax.getText(),
                        TFP.getText(),
                        PxLain.getText(),
                        Saraf.getText(),
                        Respirasi.getText(),
                        Gastro.getText(),
                        Metabolik.getText(),
                        Urinaria.getText(),
                        Muskuloskletal.getText(),
                        Lain.getText(),
                        ASA.getSelectedItem().toString(),
                        Alergi.getSelectedItem().toString(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilPraInduksi();
            }
        }
    }

    private void gantiPraInduksi() {
        if (tbAnestesi1.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_pra_induksi", "no_rawat=? and tanggal=?",
                    "kesadaran=?,gcs=?,airway=?,support1=?,support2=?,respirasi=?,rr=?,kanul_nasal=?,"
                    + "o2=?,bb=?,puasa_jam=?,tensi=?,nadi=?,spo2=?,laboratorium=?,ekg=?,thorax_foto=?,"
                    + "tfp=?,pemeriksaan_lain=?,sistem_saraf=?,sistem_respirasi=?,sistem_gastro=?,"
                    + "sistem_metabolik=?,sistem_urinaria=?,sistem_muskuloskeletal=?,lain_lain=?,"
                    + "asa=?,alergi=?",
                    30, new String[]{
                        txtKesadaran.getText(),
                        GCS.getText(),
                        Airway.getSelectedItem().toString(),
                        Support1.getText(),
                        Support2.getText(),
                        cmbRespirasi.getSelectedItem().toString(),
                        Rr.getText(),
                        Kanul.getSelectedItem().toString(),
                        O2.getText(),
                        Bb.getText(),
                        Hmt.getText(),
                        Tensi.getText(),
                        Nadi.getText(),
                        Spo2.getText(),
                        Lab.getText(),
                        Ekg.getText(),
                        Thorax.getText(),
                        TFP.getText(),
                        PxLain.getText(),
                        Saraf.getText(),
                        Respirasi.getText(),
                        Gastro.getText(),
                        Metabolik.getText(),
                        Urinaria.getText(),
                        Muskuloskletal.getText(),
                        Lain.getText(),
                        ASA.getSelectedItem().toString(),
                        Alergi.getSelectedItem().toString(),
                        tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 0).toString(),
                        tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilPraInduksi();
                emptTeks();
            }
        }
    }

    private void simpanPremedikasi() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_premedikasi",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Premedikasi", 16, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        JenisPremedikasi1.getSelectedItem().toString(),
                        NmObatPremedikasi1.getText(),
                        NmObatPremedikasi2.getText(),
                        NmObatPremedikasi2.getText(), // dosis_premedikasi2
                        Induksi1.getSelectedItem().toString(),
                        NamaObatInduksi1.getText(),
                        Posisi1.getSelectedItem().toString(),
                        SirkulasiMasuk15.getText(),
                        SirkulasiMasuk17.getText(),
                        SirkulasiMasuk16.getText(),
                        PernapasanMasuk19.getText(),
                        PernapasanMasuk20.getText(),
                        Pernapasan15.getSelectedItem().toString(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilPremedikasi();
            }
        }
    }

    private void simpanAnestesiUmumDataAwal() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_umum_data_awal",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Anestesi Umum Data Awal", 14, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
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
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilAnestesiUmumDataAwal();
            }
        }
    }

    private void gantiAnestesiUmumDataAwal() {
        if (tbAnestesi9.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_umum_data_awal", "no_rawat=? and tanggal=?",
                    "induksi=?,induksi_ket=?,pengaturan_nafas=?,tidal_volum=?,rr=?,"
                    + "i_e_ratio=?,peep=?,pip=?,fio2=?,teknik_khusus=?,teknik_khusus_ket=?",
                    13, new String[]{
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
                tampilAnestesiUmumDataAwal();
                emptTeks();
            }
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



// ===== CRUD ANESTESI UMUM KETERANGAN =====
    private void simpanAnestesiUmumKeterangan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_umum_keterangan",
                    "null,?,?,?,?,?,?,null,null", "Anestesi Umum Keterangan", 6, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Medikasi.getText(),
                        PemberianCairan.getText(),
                        MasalahDuranteOperasi.getText(),
                        Tindakan.getText(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilAnestesiUmumKeterangan();
            }
        }
    }

// ===== CRUD BLOKADE REGIONAL =====
    private void simpanBlokadeRegional() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_blokade_regional",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Blokade Regional", 20, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
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
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilBlokadeRegional();
            }
        }
    }

// ===== CRUD KEADAAN SELAMA OPERASI =====
    private void simpanKeadaanOperasi() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_keadaan_operasi",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Keadaan Operasi", 17, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Pernapasan13.getSelectedItem().toString(),
                        Pernapasan12.getSelectedItem().toString(),
                        DiagnosaPrabedah1.getText(),
                        DiagnosaPrabedah4.getText(),
                        "",
                        DiagnosaPrabedah3.getText(),
                        "",
                        DiagnosaPrabedah2.getText(),
                        DiagnosaPrabedah5.getText(),
                        DiagnosaPrabedah6.getText(),
                        DiagnosaPrabedah7.getText(),
                        DiagnosaPrabedah8.getText(),
                        DiagnosaPrabedah9.getText(),
                        DiagnosaPrabedah10.getText(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilKeadaanOperasi();
            }
        }
    }

// ===== CRUD SECTIO CAESARIA =====
    private void simpanSectio() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_sectio_caesaria",
                    "null,?,?,?,?,?,?,?,null,null", "Sectio Caesaria", 7, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KeadaanBayi.getSelectedItem().toString(),
                        JenisKelamin.getSelectedItem().toString(),
                        Apgrar1Menit.getText(),
                        Apgar5Menit.getText(),
                        Apgar10Menit.getText(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilSectio();
            }
        }
    }

    private void gantiPremedikasi() {
        if (tbAnestesi2.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_premedikasi", "no_rawat=? and tanggal=?",
                    "jenis_premedikasi=?,nm_obat_premedikasi=?,induksi=?,nama_obat_induksi=?,posisi=?,"
                    + "face_mask=?,ett=?,lm=?,trakeostomi=?,lain_lain=?,intubasi=?",
                    13, new String[]{
                        JenisPremedikasi1.getSelectedItem().toString(),
                        NmObatPremedikasi1.getText(),
                        Induksi1.getSelectedItem().toString(),
                        NamaObatInduksi1.getText(),
                        Posisi1.getSelectedItem().toString(),
                        SirkulasiMasuk15.getText(),
                        SirkulasiMasuk17.getText(),
                        SirkulasiMasuk16.getText(),
                        PernapasanMasuk19.getText(),
                        PernapasanMasuk20.getText(),
                        Pernapasan15.getSelectedItem().toString(),
                        tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 0).toString(), // no_rawat
                        tbAnestesi2.getValueAt(tbAnestesi2.getSelectedRow(), 5).toString() // tanggal
                    }) == true) {
                tampilPremedikasi();
                emptTeks();
            }
        }
    }

    private void simpanMonitoring() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            // 21 parameter untuk 21 kolom manual
            if (Sequel.menyimpantf("catatan_anestesi_monitoring",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Monitoring", 21, new String[]{
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
                        KdAsistenAnestesi1.getText() // kd_petugas
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
                    "null,?,?,?,?,?,?,?,?,?,?,null,null", "Catatan", 10, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        DiagnosaPrabedah1.getText(), // diagnosa_pasca_bedah
                        DiagnosaPrabedah4.getText(), // lama_operasi
                        DiagnosaPrabedah3.getText(), // lama_anestesi
                        Pernapasan11.getSelectedItem().toString(), // cairan_masuk
                        DiagnosaPrabedah2.getText(), // jumlah_cairan_masuk
                        Pernapasan13.getSelectedItem().toString(), // cairan_keluar
                        DiagnosaPrabedah5.getText(), // jumlah_cairan_keluar
                        KdAsistenAnestesi1.getText() // kd_petugas
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
                        DiagnosaPrabedah1.getText(),
                        DiagnosaPrabedah4.getText(),
                        DiagnosaPrabedah3.getText(),
                        Pernapasan11.getSelectedItem().toString(),
                        DiagnosaPrabedah2.getText(),
                        Pernapasan13.getSelectedItem().toString(),
                        DiagnosaPrabedah5.getText(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 1).toString()
                    }) == true) {
                tampilCatatan();
                emptTeks();
            }
        }
    }

    private void simpanMasukKamar() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_masuk_kamar",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Masuk Kamar", 15, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Valid.SetTgl(MasukKamarPulih.getSelectedItem() + "") + " " + MasukKamarPulih.getSelectedItem().toString().substring(11, 19),
                        Kesadaran.getSelectedItem().toString(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilMasukKamar();
            }
        }
    }

    private void gantiMasukKamar() {
        if (tbAnestesi6.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_masuk_kamar", "no_rawat=? and tanggal=?",
                    "jam_masuk=?,jalan_nafas=?,pernapasan=?,bila_spontan=?,kesadaran=?,"
                    + "aktifitas_masuk=?,sirkulasi_masuk=?,pernapasan_masuk=?,kesadaran_masuk=?,"
                    + "warna_kulit_masuk=?,total_masuk=?,skor_bromage=?",
                    14, new String[]{
                        Valid.SetTgl(MasukKamarPulih.getSelectedItem() + "") + " " + MasukKamarPulih.getSelectedItem().toString().substring(11, 19),
                        cmbRespirasi.getSelectedItem().toString(),
                        Airway.getSelectedItem().toString(),
                        Support1.getText(),
                        Kesadaran.getSelectedItem().toString(),
                        Support2.getText(),
                        Tensi1.getText(),
                        Rr1.getText(),
                        Nadi1.getText(),
                        Support3.getText(),
                        Support4.getText(),
                        ASA.getSelectedItem().toString(),
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(),
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilMasukKamar();
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
                    "null,?,?,?,?,?,?,?,?,null,null", "Observasi Kamar", 8, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Suhu.getText(), // tvs
                        Rr4.getText(), // rr
                        Nadi2.getText(), // nadi
                        TekananDarah.getText(), // tekanan_darah

                        KdAsistenAnestesi1.getText() // kd_petugas
                    }) == true) {
                emptTeks();
                tampilObservasiKamar();
            }
        }
    }

    private void gantiObservasiKamar() {
        if (tbAnestesi7.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_observasi_kamar", "no_rawat=? and tanggal=?",
                    "tvs=?,rr=?,nadi=?,tekanan_darah=?,vas=?",
                    7, new String[]{
                        Suhu.getText(), // tvs
                        Rr4.getText(), // rr
                        Nadi2.getText(), // nadi
                        TekananDarah.getText(), // tekanan_darah

                        tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 0).toString(), // no_rawat
                        tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 1).toString() // tanggal
                    }) == true) {
                tampilObservasiKamar();
                emptTeks();
            }
        }
    }

    private void simpanKeluarKamar() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            // 13 parameter untuk 13 kolom manual
            if (Sequel.menyimpantf("catatan_anestesi_keluar_kamar",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Keluar Kamar", 13, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        KdAsistenAnestesi1.getText() // kd_petugas
                    }) == true) {
                emptTeks();
                tampilKeluarKamar();
            }
        }
    }

    private void gantiKeluarKamar() {
    /*    if (tbAnestesi8.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_keluar_kamar", "no_rawat=? and tanggal=?",
                    "jam_keluar=?,aktifitas_keluar=?,sirkulasi_keluar=?,pernapasan_keluar=?,"
                    + "kesadaran_keluar=?,warna_kulit_keluar=?,total_keluar=?,skor_bromage_keluar=?,"
                    + "pindah_ke=?,catatan_khusus=?",
                    12, new String[]{}) == true) {
                tampilKeluarKamar();
                emptTeks();
            }
        } */
    }

    private void simpanInstruksiPasca() {
    /*    if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            // 11 parameter untuk 11 kolom manual
            if (Sequel.menyimpantf("catatan_anestesi_instruksi_pasca",
                    "null,?,?,?,?,?,?,?,?,?,?,?,null,null", "Instruksi Pasca", 11, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Kesakitan.getText(),
                        MualMuntah.getText(),
                        MualMuntah1.getText(),
                        Obat.getText(),
                        Infus.getText(),
                        Lainnya.getText(),
                        PemantauanNadi.getText(),
                        Selama.getText(),
                        KdAsistenAnestesi1.getText() // kd_petugas
                    }) == true) {
                emptTeks();
                tampilInstruksiPasca();
            } 
        } */
    }

    private void gantiInstruksiPasca() {
    /*    if (tbAnestesi5.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_instruksi_pasca", "no_rawat=? and tanggal=?",
                    "bila_kesakitan=?,bila_mual_muntah=?,antibiotika=?,obat_lain=?,infus=?,"
                    + "minum=?,pemantauan_nadi=?,selama=?",
                    10, new String[]{
                        Kesakitan.getText(), // bila_kesakitan
                        MualMuntah.getText(), // bila_mual_muntah
                        MualMuntah1.getText(), // antibiotika
                        Obat.getText(), // obat_lain
                        Infus.getText(), // infus
                        Lainnya.getText(), // minum
                        PemantauanNadi.getText(), // pemantauan_nadi
                        Selama.getText(), // selama
                        tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 0).toString(), // no_rawat
                        tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 1).toString() // tanggal
                    }) == true) {
                tampilInstruksiPasca();
                emptTeks();
            }
        } */
    }

    // Method untuk handle tab change
    private void TabRawatStateChanged(javax.swing.event.ChangeEvent evt) {
        setupTableBasedOnActiveTab();
        tampil();
    }

    private void CatatanKamarPemulihanStateChanged(javax.swing.event.ChangeEvent evt) {
        if (TabRawat.getSelectedIndex() == 7) {
            setupTableBasedOnActiveTab();
            tampil();
        }
    }

    private void AnestesiUmumStateChanged(javax.swing.event.ChangeEvent evt) {
        if (TabRawat.getSelectedIndex() == 3) {
            setupTableBasedOnActiveTab();
            tampil();
        }
    }

    private void hapusDataAwal() {
        if (tbAnestesi.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_data_awal where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 0).toString(),
                tbAnestesi.getValueAt(tbAnestesi.getSelectedRow(), 5).toString()
            }) == true) {
                tampilDataAwal();
                emptTeks();
            }
        }
    }

    private void hapusPraInduksi() {
        if (tbAnestesi1.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_pra_induksi where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 0).toString(),
                tbAnestesi1.getValueAt(tbAnestesi1.getSelectedRow(), 5).toString()
            }) == true) {
                tampilPraInduksi();
                emptTeks();
            }
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

    private void hapusMasukKamar() {
        if (tbAnestesi6.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_masuk_kamar where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(),
                tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString()
            }) == true) {
                tampilMasukKamar();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        }
    }

    private void hapusObservasiKamar() {
        if (tbAnestesi7.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_observasi_kamar where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 0).toString(),
                tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 5).toString()
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

    private void hapusKeluarKamar() {
    /*    if (tbAnestesi8.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_keluar_kamar where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi8.getValueAt(tbAnestesi8.getSelectedRow(), 0).toString(),
                tbAnestesi8.getValueAt(tbAnestesi8.getSelectedRow(), 5).toString()
            }) == true) {
                tampilKeluarKamar();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        } */
    }

    private void hapusInstruksiPasca() {
    /*    if (tbAnestesi5.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_instruksi_pasca where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 0).toString(),
                tbAnestesi5.getValueAt(tbAnestesi5.getSelectedRow(), 5).toString()
            }) == true) {
                tampilInstruksiPasca();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang akan dihapus..!!");
        } */
    }

   

// Setup Table Pra Induksi (Updated)
    private void setupTablePraInduksi() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal Jam", "Ruangan",
            "Kesadaran", "GCS", "Airway", "Support 1", "Support 2", "Respirasi", "RR",
            "Kanul Nasal", "O2", "BB", "Puasa Jam", "Tensi", "Nadi", "SpO2",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi1.setModel(tabMode);
        setTableStyling(tbAnestesi1);
    }

// Setup Table Premedikasi
    private void setupTablePremedikasi() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal Jam", "Ruangan",
            "Jenis Premedikasi", "Nama Obat Premedikasi", "Induksi", "Nama Obat Induksi",
            "Posisi", "Face Mask", "ETT", "LM", "Trakeostomi", "Lain-lain", "Intubasi",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi2.setModel(tabMode);

        if (tbAnestesi2.getColumnModel().getColumnCount() > 0) {
            tbAnestesi2.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi2.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi2.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi2.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi2.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi2.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi2.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi2.getColumnModel().getColumn(7).setPreferredWidth(100);
            tbAnestesi2.getColumnModel().getColumn(8).setPreferredWidth(200);
            tbAnestesi2.getColumnModel().getColumn(9).setPreferredWidth(80);
            tbAnestesi2.getColumnModel().getColumn(10).setPreferredWidth(200);
            tbAnestesi2.getColumnModel().getColumn(11).setPreferredWidth(100);
            tbAnestesi2.getColumnModel().getColumn(12).setPreferredWidth(80);
            tbAnestesi2.getColumnModel().getColumn(13).setPreferredWidth(80);
            tbAnestesi2.getColumnModel().getColumn(14).setPreferredWidth(80);
            tbAnestesi2.getColumnModel().getColumn(15).setPreferredWidth(100);
            tbAnestesi2.getColumnModel().getColumn(16).setPreferredWidth(100);
            tbAnestesi2.getColumnModel().getColumn(17).setPreferredWidth(120);
            tbAnestesi2.getColumnModel().getColumn(18).setPreferredWidth(80);
            tbAnestesi2.getColumnModel().getColumn(19).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi2);
    }

// Setup Table Monitoring
    private void setupTableMonitoring() {
        tabMode = new DefaultTableModel(null, new Object[]{
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
        tbAnestesi3.setModel(tabMode);

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
        tabMode = new DefaultTableModel(null, new Object[]{
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
        tbAnestesi4.setModel(tabMode);

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
        tabMode = new DefaultTableModel(null, new Object[]{
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
        tbAnestesi6.setModel(tabMode);

        if (tbAnestesi6.getColumnModel().getColumnCount() > 0) {
            tbAnestesi6.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi6.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi6.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi6.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi6.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(7).setPreferredWidth(130);
            tbAnestesi6.getColumnModel().getColumn(8).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(9).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(10).setPreferredWidth(120);
            tbAnestesi6.getColumnModel().getColumn(11).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(12).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(13).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(14).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(15).setPreferredWidth(100);
            tbAnestesi6.getColumnModel().getColumn(16).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(17).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(18).setPreferredWidth(200);
            tbAnestesi6.getColumnModel().getColumn(19).setPreferredWidth(80);
            tbAnestesi6.getColumnModel().getColumn(20).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi6);
    }

// Setup Table Observasi Kamar
    private void setupTableObservasiKamar() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "TVS", "RR", "Nadi", "Tekanan Darah", "VAS", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi7.setModel(tabMode);

        if (tbAnestesi7.getColumnModel().getColumnCount() > 0) {
            tbAnestesi7.getColumnModel().getColumn(0).setPreferredWidth(100);
            tbAnestesi7.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbAnestesi7.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbAnestesi7.getColumnModel().getColumn(3).setPreferredWidth(80);
            tbAnestesi7.getColumnModel().getColumn(4).setPreferredWidth(50);
            tbAnestesi7.getColumnModel().getColumn(5).setPreferredWidth(130);
            tbAnestesi7.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbAnestesi7.getColumnModel().getColumn(7).setPreferredWidth(60);
            tbAnestesi7.getColumnModel().getColumn(8).setPreferredWidth(60);
            tbAnestesi7.getColumnModel().getColumn(9).setPreferredWidth(60);
            tbAnestesi7.getColumnModel().getColumn(10).setPreferredWidth(100);
            tbAnestesi7.getColumnModel().getColumn(11).setPreferredWidth(60);
            tbAnestesi7.getColumnModel().getColumn(12).setPreferredWidth(80);
            tbAnestesi7.getColumnModel().getColumn(13).setPreferredWidth(150);
        }
        setTableStyling(tbAnestesi7);
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

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new java.awt.Color(255, 255, 255));
                    } else {
                        c.setBackground(new java.awt.Color(250, 250, 250));
                    }
                    c.setForeground(new java.awt.Color(50, 50, 50));
                } else {
                    c.setBackground(new java.awt.Color(255, 255, 255));
                    c.setForeground(new java.awt.Color(255, 0, 0));
                }

                c.setFont(new java.awt.Font("Tahoma", 0, 11));
                return c;
            }
        });
    }

// Method untuk setup table berdasarkan tab aktif
    private void setupTableBasedOnActiveTab() {
        switch (TabRawat.getSelectedIndex()) {
            case 0:
                setupTableDataAwal();
                break;
            case 1:
                setupTablePraInduksi();
                break;
            case 2:
                setupTablePremedikasi();
                break;
            case 3:
                switch (AnestesiUmum.getSelectedIndex()) {
                    case 0:
                        setupTableAnestesiUmumDataAwal();
                        break;
                    case 1:
                        setupTableAnestesiUmumMonitoring();
                        break;
                    case 2:
                        setupTableAnestesiUmumKeterangan();
                        break;
                    default:
                        setupTableAnestesiUmumDataAwal();
                        break;
                }
                break;
            case 4:
                setupTableBlokadeRegional();
                break;
            case 5:
                setupTableKeadaanOperasi();
                break;
            case 6:
                setupTableSectio();
                break;
            case 7:
                switch (CatatanKamarPemulihan.getSelectedIndex()) {
                    case 0:
                        setupTableMasukKamarPemulihan();
                        break;
                    case 1:
                        setupTableMonitoringPemulihan();
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
            if (Sequel.menyimpantf("catatan_anestesi_masuk_kamar_pemulihan",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Masuk Kamar Pemulihan", 16, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Valid.SetTgl(MasukKamarPulih.getSelectedItem() + "") + " " + MasukKamarPulih.getSelectedItem().toString().substring(11, 19),
                        Kesadaran.getSelectedItem().toString(),
                        Tensi1.getText(),
                        Nadi1.getText(),
                        Support3.getText(),
                        Support4.getText(),
                        cmbRespirasi1.getSelectedItem().toString(),
                        Rr1.getText(),
                        Kanul1.getSelectedItem().toString(),
                        O3.getText(),
                        Spo3.getText(),
                        Support5.getText(),
                        Support6.getText(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilMasukKamarPemulihan();
            }
        }
    }

// ===== CRUD MONITORING PEMULIHAN =====
    private void simpanMonitoringPemulihan() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_monitoring_pemulihan",
                    "null,?,?,?,?,?,?,?,null,null", "Monitoring Pemulihan", 7, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        Suhu.getText(),
                        Rr4.getText(),
                        Nadi2.getText(),
                        TekananDarah.getText(),
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilMonitoringPemulihan();
            }
        }
    }

    // ===== MISSING METHOD IMPLEMENTATIONS =====
// ===== ANESTESI UMUM METHODS =====
    private void tampilAnestesiUmumDataAwal() {
        if (tabMode == null) {
            setupTableAnestesiUmumDataAwal();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oauda.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oauda.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oauda.induksi, oauda.induksi_ket, oauda.pengaturan_nafas, oauda.tidal_volum, "
                    + "oauda.rr, oauda.i_e_ratio, oauda.peep, oauda.pip, oauda.fio2, "
                    + "oauda.teknik_khusus, oauda.teknik_khusus_ket, "
                    + "oauda.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_umum_data_awal oauda "
                    + "INNER JOIN reg_periksa rp ON oauda.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oauda.kd_petugas = p.nip "
                    + "WHERE oauda.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oauda.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("induksi") != null ? rs.getString("induksi") : "",
                    rs.getString("induksi_ket") != null ? rs.getString("induksi_ket") : "",
                    rs.getString("pengaturan_nafas") != null ? rs.getString("pengaturan_nafas") : "",
                    rs.getString("tidal_volum") != null ? rs.getString("tidal_volum") : "",
                    rs.getString("rr") != null ? rs.getString("rr") : "",
                    rs.getString("i_e_ratio") != null ? rs.getString("i_e_ratio") : "",
                    rs.getString("peep") != null ? rs.getString("peep") : "",
                    rs.getString("pip") != null ? rs.getString("pip") : "",
                    rs.getString("fio2") != null ? rs.getString("fio2") : "",
                    rs.getString("teknik_khusus") != null ? rs.getString("teknik_khusus") : "",
                    rs.getString("teknik_khusus_ket") != null ? rs.getString("teknik_khusus_ket") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilAnestesiUmumDataAwal: " + e);
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

    private void tampilAnestesiUmumMonitoring() {
        if (tabMode == null) {
            setupTableAnestesiUmumMonitoring();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oaum.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oaum.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oaum.o2, oaum.n2o, oaum.air, oaum.volatile, oaum.sevofluran, "
                    + "oaum.infus, oaum.suhu, oaum.respirasi, oaum.nadi, oaum.tekanan_darah, "
                    + "oaum.spo2, oaum.ekg, oaum.lain_lain, "
                    + "oaum.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_umum_monitoring oaum "
                    + "INNER JOIN reg_periksa rp ON oaum.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oaum.kd_petugas = p.nip "
                    + "WHERE oaum.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oaum.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("o2") != null ? rs.getString("o2") : "",
                    rs.getString("n2o") != null ? rs.getString("n2o") : "",
                    rs.getString("air") != null ? rs.getString("air") : "",
                    rs.getString("volatile") != null ? rs.getString("volatile") : "",
                    rs.getString("sevofluran") != null ? rs.getString("sevofluran") : "",
                    rs.getString("infus") != null ? rs.getString("infus") : "",
                    rs.getString("suhu") != null ? rs.getString("suhu") : "",
                    rs.getString("respirasi") != null ? rs.getString("respirasi") : "",
                    rs.getString("nadi") != null ? rs.getString("nadi") : "",
                    rs.getString("tekanan_darah") != null ? rs.getString("tekanan_darah") : "",
                    rs.getString("spo2") != null ? rs.getString("spo2") : "",
                    rs.getString("ekg") != null ? rs.getString("ekg") : "",
                    rs.getString("lain_lain") != null ? rs.getString("lain_lain") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilAnestesiUmumMonitoring: " + e);
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

    private void tampilAnestesiUmumKeterangan() {
        if (tabMode == null) {
            setupTableAnestesiUmumKeterangan();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oauk.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oauk.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oauk.medikasi, oauk.pemberian_cairan, oauk.masalah_durante_operasi, "
                    + "oauk.tindakan, oauk.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_umum_keterangan oauk "
                    + "INNER JOIN reg_periksa rp ON oauk.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oauk.kd_petugas = p.nip "
                    + "WHERE oauk.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oauk.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("medikasi") != null ? rs.getString("medikasi") : "",
                    rs.getString("pemberian_cairan") != null ? rs.getString("pemberian_cairan") : "",
                    rs.getString("masalah_durante_operasi") != null ? rs.getString("masalah_durante_operasi") : "",
                    rs.getString("tindakan") != null ? rs.getString("tindakan") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilAnestesiUmumKeterangan: " + e);
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

// ===== BLOKADE REGIONAL METHODS =====
    private void tampilBlokadeRegional() {
        if (tabMode == null) {
            setupTableBlokadeRegional();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oabr.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oabr.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oabr.teknik, oabr.lokasi_tusukan, oabr.analgesia_setinggi_segmen, "
                    + "oabr.anestesi_lokal, oabr.konsentrasi_jumlah, oabr.obat_tambahan, "
                    + "oabr.dosis, oabr.vasokonstriktor, oabr.konsentrasi, "
                    + "oabr.waktu_mulai_suntik_jam, oabr.analgesia_jam, oabr.lamanya, "
                    + "oabr.lamanya_menit_analgesi, oabr.operasi_jam, oabr.lamanya_jam_operasi, "
                    + "oabr.lamanya_jam_menit, oabr.tindakan_anestesi_tambahan, "
                    + "oabr.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_blokade_regional oabr "
                    + "INNER JOIN reg_periksa rp ON oabr.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oabr.kd_petugas = p.nip "
                    + "WHERE oabr.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oabr.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("teknik") != null ? rs.getString("teknik") : "",
                    rs.getString("lokasi_tusukan") != null ? rs.getString("lokasi_tusukan") : "",
                    rs.getString("analgesia_setinggi_segmen") != null ? rs.getString("analgesia_setinggi_segmen") : "",
                    rs.getString("anestesi_lokal") != null ? rs.getString("anestesi_lokal") : "",
                    rs.getString("konsentrasi_jumlah") != null ? rs.getString("konsentrasi_jumlah") : "",
                    rs.getString("obat_tambahan") != null ? rs.getString("obat_tambahan") : "",
                    rs.getString("dosis") != null ? rs.getString("dosis") : "",
                    rs.getString("vasokonstriktor") != null ? rs.getString("vasokonstriktor") : "",
                    rs.getString("konsentrasi") != null ? rs.getString("konsentrasi") : "",
                    rs.getString("waktu_mulai_suntik_jam") != null ? rs.getString("waktu_mulai_suntik_jam") : "",
                    rs.getString("analgesia_jam") != null ? rs.getString("analgesia_jam") : "",
                    rs.getString("lamanya") != null ? rs.getString("lamanya") : "",
                    rs.getString("lamanya_menit_analgesi") != null ? rs.getString("lamanya_menit_analgesi") : "",
                    rs.getString("operasi_jam") != null ? rs.getString("operasi_jam") : "",
                    rs.getString("lamanya_jam_operasi") != null ? rs.getString("lamanya_jam_operasi") : "",
                    rs.getString("lamanya_jam_menit") != null ? rs.getString("lamanya_jam_menit") : "",
                    rs.getString("tindakan_anestesi_tambahan") != null ? rs.getString("tindakan_anestesi_tambahan") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilBlokadeRegional: " + e);
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

// ===== KEADAAN OPERASI METHODS =====
    private void tampilKeadaanOperasi() {
        if (tabMode == null) {
            setupTableKeadaanOperasi();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oako.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oako.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oako.letak_penderita, oako.airway, oako.ukuran, oako.balon, "
                    + "oako.lama_operasi_jam, oako.lama_operasi_menit, oako.lama_anestesi_jam, "
                    + "oako.lama_anestesi_menit, oako.kristaloid, oako.koloid, oako.darah, "
                    + "oako.komponen_darah, oako.perdarahan, oako.diuresis, oako.cairan_lain, "
                    + "oako.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_keadaan_operasi oako "
                    + "INNER JOIN reg_periksa rp ON oako.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oako.kd_petugas = p.nip "
                    + "WHERE oako.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oako.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("letak_penderita") != null ? rs.getString("letak_penderita") : "",
                    rs.getString("airway") != null ? rs.getString("airway") : "",
                    rs.getString("ukuran") != null ? rs.getString("ukuran") : "",
                    rs.getString("balon") != null ? rs.getString("balon") : "",
                    rs.getString("lama_operasi_jam") != null ? rs.getString("lama_operasi_jam") : "",
                    rs.getString("lama_operasi_menit") != null ? rs.getString("lama_operasi_menit") : "",
                    rs.getString("lama_anestesi_jam") != null ? rs.getString("lama_anestesi_jam") : "",
                    rs.getString("lama_anestesi_menit") != null ? rs.getString("lama_anestesi_menit") : "",
                    rs.getString("kristaloid") != null ? rs.getString("kristaloid") : "",
                    rs.getString("koloid") != null ? rs.getString("koloid") : "",
                    rs.getString("darah") != null ? rs.getString("darah") : "",
                    rs.getString("komponen_darah") != null ? rs.getString("komponen_darah") : "",
                    rs.getString("perdarahan") != null ? rs.getString("perdarahan") : "",
                    rs.getString("diuresis") != null ? rs.getString("diuresis") : "",
                    rs.getString("cairan_lain") != null ? rs.getString("cairan_lain") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilKeadaanOperasi: " + e);
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

// ===== SECTIO METHODS =====
    private void tampilSectio() {
        if (tabMode == null) {
            setupTableSectio();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oasc.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oasc.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oasc.keadaan_bayi, oasc.jenis_kelamin, oasc.apgar_1_menit, "
                    + "oasc.apgar_5_menit, oasc.apgar_10_menit, "
                    + "oasc.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_sectio_caesaria oasc "
                    + "INNER JOIN reg_periksa rp ON oasc.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oasc.kd_petugas = p.nip "
                    + "WHERE oasc.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oasc.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("keadaan_bayi") != null ? rs.getString("keadaan_bayi") : "",
                    rs.getString("jenis_kelamin") != null ? rs.getString("jenis_kelamin") : "",
                    rs.getString("apgar_1_menit") != null ? rs.getString("apgar_1_menit") : "",
                    rs.getString("apgar_5_menit") != null ? rs.getString("apgar_5_menit") : "",
                    rs.getString("apgar_10_menit") != null ? rs.getString("apgar_10_menit") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilSectio: " + e);
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

// ===== KAMAR PEMULIHAN METHODS =====
    private void tampilMasukKamarPemulihan() {
        if (tabMode == null) {
            setupTableMasukKamarPemulihan();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oamkp.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oamkp.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oamkp.jam_masuk, oamkp.kesadaran, oamkp.tensi, oamkp.nadi, "
                    + "oamkp.support1, oamkp.support2, oamkp.respirasi, oamkp.rr, "
                    + "oamkp.kanul_nasal, oamkp.o2, oamkp.spo2, oamkp.skor_nyeri, "
                    + "oamkp.penyulit_intraoperatif, "
                    + "oamkp.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_masuk_kamar_pemulihan oamkp "
                    + "INNER JOIN reg_periksa rp ON oamkp.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oamkp.kd_petugas = p.nip "
                    + "WHERE oamkp.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oamkp.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("jam_masuk") != null ? rs.getString("jam_masuk") : "",
                    rs.getString("kesadaran") != null ? rs.getString("kesadaran") : "",
                    rs.getString("tensi") != null ? rs.getString("tensi") : "",
                    rs.getString("nadi") != null ? rs.getString("nadi") : "",
                    rs.getString("support1") != null ? rs.getString("support1") : "",
                    rs.getString("support2") != null ? rs.getString("support2") : "",
                    rs.getString("respirasi") != null ? rs.getString("respirasi") : "",
                    rs.getString("rr") != null ? rs.getString("rr") : "",
                    rs.getString("kanul_nasal") != null ? rs.getString("kanul_nasal") : "",
                    rs.getString("o2") != null ? rs.getString("o2") : "",
                    rs.getString("spo2") != null ? rs.getString("spo2") : "",
                    rs.getString("skor_nyeri") != null ? rs.getString("skor_nyeri") : "",
                    rs.getString("penyulit_intraoperatif") != null ? rs.getString("penyulit_intraoperatif") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilMasukKamarPemulihan: " + e);
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

    private void tampilMonitoringPemulihan() {
        if (tabMode == null) {
            setupTableMonitoringPemulihan();
        }

        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "SELECT oamp.no_rawat, rp.no_rkm_medis, pas.nm_pasien, pas.tgl_lahir, "
                    + "IF(pas.jk='L','Laki-Laki','Perempuan') as jk, oamp.tanggal, "
                    + "COALESCE(ruang.nm_bangsal, '-') as nm_bangsal, "
                    + "oamp.suhu, oamp.rr, oamp.nadi, oamp.tekanan_darah, "
                    + "oamp.kd_petugas, p.nama as nm_petugas "
                    + "FROM catatan_anestesi_monitoring_pemulihan oamp "
                    + "INNER JOIN reg_periksa rp ON oamp.no_rawat = rp.no_rawat "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "LEFT JOIN kamar_inap ki ON rp.no_rawat = ki.no_rawat AND ki.stts_pulang = '-' "
                    + "LEFT JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                    + "LEFT JOIN bangsal ruang ON k.kd_bangsal = ruang.kd_bangsal "
                    + "LEFT JOIN petugas p ON oamp.kd_petugas = p.nip "
                    + "WHERE oamp.tanggal BETWEEN ? AND ? "
                    + "ORDER BY oamp.tanggal DESC"
            );

            ps.setString(1, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " 23:59:59");
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"),
                    rs.getString("jk"),
                    rs.getString("tanggal"),
                    rs.getString("nm_bangsal"),
                    rs.getString("suhu") != null ? rs.getString("suhu") : "",
                    rs.getString("rr") != null ? rs.getString("rr") : "",
                    rs.getString("nadi") != null ? rs.getString("nadi") : "",
                    rs.getString("tekanan_darah") != null ? rs.getString("tekanan_darah") : "",
                    rs.getString("kd_petugas") != null ? rs.getString("kd_petugas") : "",
                    rs.getString("nm_petugas") != null ? rs.getString("nm_petugas") : ""
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilMonitoringPemulihan: " + e);
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

// ===== SETUP TABLE METHODS =====
    private void setupTableAnestesiUmumDataAwal() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Induksi", "Induksi Ket", "Pengaturan Nafas", "Tidal Volum", "RR",
            "I:E Ratio", "PEEP", "PIP", "FiO2", "Teknik Khusus", "Teknik Khusus Ket",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi9.setModel(tabMode);
        setTableStyling(tbAnestesi9);
    }

    private void setupTableAnestesiUmumMonitoring() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "O2", "N2O", "Air", "Volatile", "Sevofluran", "Infus", "Suhu",
            "Respirasi", "Nadi", "Tekanan Darah", "SpO2", "EKG", "Lain-lain",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi10.setModel(tabMode);
        setTableStyling(tbAnestesi10);
    }

    private void setupTableAnestesiUmumKeterangan() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Medikasi", "Pemberian Cairan", "Masalah Durante Operasi", "Tindakan",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi11.setModel(tabMode);
        setTableStyling(tbAnestesi11);
    }

    private void setupTableBlokadeRegional() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Teknik", "Lokasi Tusukan", "Analgesia Segmen", "Anestesi Lokal",
            "Konsentrasi Jumlah", "Obat Tambahan", "Dosis", "Vasokonstriktor",
            "Konsentrasi", "Waktu Mulai", "Analgesia Jam", "Lamanya",
            "Lamanya Menit", "Operasi Jam", "Lamanya Jam", "Lamanya Menit",
            "Tindakan Tambahan", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi12.setModel(tabMode);
        setTableStyling(tbAnestesi12);
    }

    private void setupTableKeadaanOperasi() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Letak Penderita", "Airway", "Ukuran", "Balon", "Lama Op Jam", "Lama Op Menit",
            "Lama Anest Jam", "Lama Anest Menit", "Kristaloid", "Koloid", "Darah",
            "Komponen Darah", "Perdarahan", "Diuresis", "Cairan Lain",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi3.setModel(tabMode);
        setTableStyling(tbAnestesi3);
    }

    private void setupTableSectio() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Keadaan Bayi", "Jenis Kelamin", "Apgar 1 Menit", "Apgar 5 Menit", "Apgar 10 Menit",
            "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi4.setModel(tabMode);
        setTableStyling(tbAnestesi4);
    }

    private void setupTableMasukKamarPemulihan() {
        tabMode = new DefaultTableModel(null, new Object[]{
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
        tbAnestesi6.setModel(tabMode);
        setTableStyling(tbAnestesi6);
    }

    private void setupTableMonitoringPemulihan() {
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Suhu", "RR", "Nadi", "Tekanan Darah", "Kode Petugas", "Nama Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi7.setModel(tabMode);
        setTableStyling(tbAnestesi7);
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
        if (tbAnestesi12.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_blokade_regional", "no_rawat=? and tanggal=?",
                    "teknik=?,lokasi_tusukan=?,analgesia_setinggi_segmen=?,anestesi_lokal=?,"
                    + "konsentrasi_jumlah=?,obat_tambahan=?,dosis=?,vasokonstriktor=?,"
                    + "konsentrasi=?,waktu_mulai_suntik_jam=?,analgesia_jam=?,lamanya=?,"
                    + "lamanya_menit_analgesi=?,operasi_jam=?,lamanya_jam_operasi=?,"
                    + "lamanya_jam_menit=?,tindakan_anestesi_tambahan=?",
                    19, new String[]{
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
                tampilBlokadeRegional();
                emptTeks();
            }
        }
    }

    private void gantiKeadaanOperasi() {
        if (tbAnestesi3.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_keadaan_operasi", "no_rawat=? and tanggal=?",
                    "letak_penderita=?,airway=?,ukuran=?,balon=?,lama_operasi_jam=?,"
                    + "lama_operasi_menit=?,lama_anestesi_jam=?,lama_anestesi_menit=?,"
                    + "kristaloid=?,koloid=?,darah=?,komponen_darah=?,perdarahan=?,"
                    + "diuresis=?,cairan_lain=?",
                    17, new String[]{
                        Pernapasan13.getSelectedItem().toString(),
                        Pernapasan12.getSelectedItem().toString(),
                        DiagnosaPrabedah1.getText(),
                        Pernapasan11.getSelectedItem().toString(),
                        DiagnosaPrabedah4.getText(),
                        DiagnosaPrabedah3.getText(),
                        "", // lama_anestesi_jam
                        "", // lama_anestesi_menit
                        DiagnosaPrabedah2.getText(),
                        DiagnosaPrabedah5.getText(),
                        DiagnosaPrabedah6.getText(),
                        DiagnosaPrabedah7.getText(),
                        DiagnosaPrabedah8.getText(),
                        DiagnosaPrabedah9.getText(),
                        DiagnosaPrabedah10.getText(),
                        tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 0).toString(),
                        tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilKeadaanOperasi();
                emptTeks();
            }
        }
    }

    private void gantiSectio() {
        if (tbAnestesi4.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_sectio_caesaria", "no_rawat=? and tanggal=?",
                    "keadaan_bayi=?,jenis_kelamin=?,apgar_1_menit=?,apgar_5_menit=?,apgar_10_menit=?",
                    7, new String[]{
                        KeadaanBayi.getSelectedItem().toString(),
                        JenisKelamin.getSelectedItem().toString(),
                        Apgrar1Menit.getText(),
                        Apgar5Menit.getText(),
                        Apgar10Menit.getText(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(),
                        tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilSectio();
                emptTeks();
            }
        }
    }

    private void gantiMasukKamarPemulihan() {
        if (tbAnestesi6.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_masuk_kamar_pemulihan", "no_rawat=? and tanggal=?",
                    "jam_masuk=?,kesadaran=?,tensi=?,nadi=?,support1=?,support2=?,"
                    + "respirasi=?,rr=?,kanul_nasal=?,o2=?,spo2=?,skor_nyeri=?,"
                    + "penyulit_intraoperatif=?",
                    15, new String[]{
                        Valid.SetTgl(MasukKamarPulih.getSelectedItem() + "") + " " + MasukKamarPulih.getSelectedItem().toString().substring(11, 19),
                        Kesadaran.getSelectedItem().toString(),
                        Tensi1.getText(),
                        Nadi1.getText(),
                        Support3.getText(),
                        Support4.getText(),
                        cmbRespirasi1.getSelectedItem().toString(),
                        Rr1.getText(),
                        Kanul1.getSelectedItem().toString(),
                        O3.getText(),
                        Spo3.getText(),
                        Support5.getText(),
                        Support6.getText(),
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(),
                        tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilMasukKamarPemulihan();
                emptTeks();
            }
        }
    }

    private void gantiMonitoringPemulihan() {
        if (tbAnestesi7.getSelectedRow() > -1) {
            if (Sequel.mengedittf("catatan_anestesi_monitoring_pemulihan", "no_rawat=? and tanggal=?",
                    "suhu=?,rr=?,nadi=?,tekanan_darah=?",
                    6, new String[]{
                        Suhu.getText(),
                        Rr4.getText(),
                        Nadi2.getText(),
                        TekananDarah.getText(),
                        tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 0).toString(),
                        tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 5).toString()
                    }) == true) {
                tampilMonitoringPemulihan();
                emptTeks();
            }
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
        if (tbAnestesi12.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_blokade_regional where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi12.getValueAt(tbAnestesi12.getSelectedRow(), 0).toString(),
                tbAnestesi12.getValueAt(tbAnestesi12.getSelectedRow(), 5).toString()
            }) == true) {
                tampilBlokadeRegional();
                emptTeks();
            }
        }
    }

    private void hapusKeadaanOperasi() {
        if (tbAnestesi3.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_keadaan_operasi where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 0).toString(),
                tbAnestesi3.getValueAt(tbAnestesi3.getSelectedRow(), 5).toString()
            }) == true) {
                tampilKeadaanOperasi();
                emptTeks();
            }
        }
    }

    private void hapusSectio() {
        if (tbAnestesi4.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_sectio_caesaria where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 0).toString(),
                tbAnestesi4.getValueAt(tbAnestesi4.getSelectedRow(), 5).toString()
            }) == true) {
                tampilSectio();
                emptTeks();
            }
        }
    }

    private void hapusMasukKamarPemulihan() {
        if (tbAnestesi6.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_masuk_kamar_pemulihan where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 0).toString(),
                tbAnestesi6.getValueAt(tbAnestesi6.getSelectedRow(), 5).toString()
            }) == true) {
                tampilMasukKamarPemulihan();
                emptTeks();
            }
        }
    }

    private void hapusMonitoringPemulihan() {
        if (tbAnestesi7.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from catatan_anestesi_monitoring_pemulihan where no_rawat=? and tanggal=?", 2, new String[]{
                tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 0).toString(),
                tbAnestesi7.getValueAt(tbAnestesi7.getSelectedRow(), 5).toString()
            }) == true) {
                tampilMonitoringPemulihan();
                emptTeks();
            }
        }
    }

    // Method untuk simpan anestesi umum monitoring yang diperbaiki
    private void simpanAnestesiUmumMonitoring() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (Sequel.menyimpantf("catatan_anestesi_umum_monitoring",
                    "null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,null", "Anestesi Umum Monitoring", 15, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
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
                        KdAsistenAnestesi1.getText()
                    }) == true) {
                emptTeks();
                tampilAnestesiUmumMonitoring();
            }
        }
    }

// Method untuk setup table yang hilang
    private void setupTableDataAwal() {
        tabModeDataAwal = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "JK", "Tanggal", "Ruangan",
            "Kd.Petugas", "Nm.Petugas", "Kd.Dokter Anestesi", "Nm.Dokter Anestesi",
            "Kd.Asisten Anestesi", "Nm.Asisten Anestesi", "Kd.Dokter Bedah", "Nm.Dokter Bedah",
            "Kd.Asisten Bedah", "Nm.Asisten Bedah", "Jenis Pembedahan", "Jenis Anestesi",
            "Diagnosa Prabedah", "Informed Consent", "Obat Anestesi", "Tatalaksana Jalan Nafas",
            "Mesin Anestesi", "Obat Emergensi", "Check Monitoring", "Suction Aparatus"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbAnestesi.setModel(tabModeDataAwal);
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

}