package rekammedis;

import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JOptionPane;
import kepegawaian.DlgCariPetugas;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import java.util.HashMap;
import java.util.Map;


/**
 * Form Partograf
 * @author rs
 */
public final class RMPartograf extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, ps2;
    private ResultSet rs, rs2;
    private DefaultTableModel tabMode, tabModeObs, tabModeHalaman2, tabModeCatatanPersalinan, tabModeKala1, tabModeKala2, tabModeKala3, tabModeBBL, tabModeMasalahKala4;

    public RMPartograf(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Batas input
        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        TKodePetugas.setDocument(new batasInput((byte) 20).getKata(TKodePetugas));
        TG.setDocument(new batasInput((byte) 3).getKata(TG));
        TP.setDocument(new batasInput((byte) 3).getKata(TP));
        TA.setDocument(new batasInput((byte) 3).getKata(TA));
        TKetubanPecah.setDocument(new batasInput((byte) 20).getKata(TKetubanPecah));
        TDJJ.setDocument(new batasInput((byte) 3).getOnlyAngka(TDJJ));
        TPembukaan.setDocument(new batasInput((byte) 2).getOnlyAngka(TPembukaan));
        TPenurunan.setDocument(new batasInput((byte) 3).getKata(TPenurunan));
        TKontraksiJml.setDocument(new batasInput((byte) 1).getOnlyAngka(TKontraksiJml));
        TSistole.setDocument(new batasInput((byte) 3).getOnlyAngka(TSistole));
        TDiastole.setDocument(new batasInput((byte) 3).getOnlyAngka(TDiastole));
        TNadi.setDocument(new batasInput((byte) 3).getOnlyAngka(TNadi));
        TSuhu.setDocument(new batasInput((byte) 5).getOnlyAngka(TSuhu));
        TUrineVol.setDocument(new batasInput((byte) 5).getOnlyAngka(TUrineVol));

        DTPCari1.setDate(new java.util.Date());
        DTPCari2.setDate(new java.util.Date());

        TCari.setDocument(new batasInput((int) 100).getKata(TCari));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { if (TCari.getText().length() > 2) { tampil(); tampil_observasi(); } }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { if (TCari.getText().length() > 2) { tampil(); tampil_observasi(); } }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { if (TCari.getText().length() > 2) { tampil(); tampil_observasi(); } }
            });
        }

        tabMode = new javax.swing.table.DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "Umur", "Tanggal", "G", "P", "A", "Ketuban Pecah", "Mules Sejak", "NIP", "Nama Petugas", "ID"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbPartograf.setModel(tabMode);
        tbPartograf.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 500));
        tbPartograf.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 13; i++) {
            javax.swing.table.TableColumn column = tbPartograf.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(70);
            else if (i == 2) column.setPreferredWidth(150);
            else if (i == 3) column.setPreferredWidth(65);
            else if (i == 4) column.setPreferredWidth(40);
            else if (i == 5) column.setPreferredWidth(115);
            else if (i == 6) column.setPreferredWidth(25);
            else if (i == 7) column.setPreferredWidth(25);
            else if (i == 8) column.setPreferredWidth(25);
            else if (i == 9) column.setPreferredWidth(100);
            else if (i == 10) column.setPreferredWidth(115);
            else if (i == 11) column.setPreferredWidth(80);
            else if (i == 12) column.setPreferredWidth(150);
            else if (i == 13) column.setPreferredWidth(30);
        }
        tbPartograf.setDefaultRenderer(Object.class, new widget.Table().getDefaultRenderer(Object.class));

        tabModeObs = new javax.swing.table.DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Jam", "DJJ", "Air Ketuban", "Penyusupan", "Pembukaan", "Penurunan", "Kontraksi", "Durasi", "Oksitosin", "Tetes", "Obat/Cairan", "Sistole", "Diastole", "Nadi", "Suhu", "Urine Vol", "Protein", "Aseton", "NIP", "Nama Petugas", "ID"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObservasi.setModel(tabModeObs);
        tbObservasi.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 500));
        tbObservasi.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 21; i++) {
            javax.swing.table.TableColumn column = tbObservasi.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(115);
            else if (i == 2) column.setPreferredWidth(40);
            else if (i == 3) column.setPreferredWidth(70);
            else if (i == 4) column.setPreferredWidth(70);
            else if (i == 5) column.setPreferredWidth(60);
            else if (i == 6) column.setPreferredWidth(60);
            else if (i == 7) column.setPreferredWidth(60);
            else if (i == 8) column.setPreferredWidth(70);
            else if (i == 9) column.setPreferredWidth(60);
            else if (i == 10) column.setPreferredWidth(40);
            else if (i == 11) column.setPreferredWidth(150);
            else if (i == 12) column.setPreferredWidth(50);
            else if (i == 13) column.setPreferredWidth(50);
            else if (i == 14) column.setPreferredWidth(50);
            else if (i == 15) column.setPreferredWidth(40);
            else if (i == 16) column.setPreferredWidth(60);
            else if (i == 17) column.setPreferredWidth(60);
            else if (i == 18) column.setPreferredWidth(60);
            else if (i == 19) column.setPreferredWidth(80);
            else if (i == 20) column.setPreferredWidth(150);
            else if (i == 21) column.setPreferredWidth(30);
        }
        tbObservasi.setDefaultRenderer(Object.class, new widget.Table().getDefaultRenderer(Object.class));

        tabModeObs = createObsModel();
        tbObservasi.setModel(tabModeObs);
        configObsTable(tbObservasi);

        tabModeHalaman2 = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Lahir Anak", "Jam", "J.K.", "BB", "PB", "Apgar", "Kala III", "Placenta", "Oxytocin", "Monitoring Kala IV", "Keterangan", "NIP"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbHalaman2.setModel(tabModeHalaman2);
        tbHalaman2.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbHalaman2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 13; i++) {
            TableColumn column = tbHalaman2.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(80);
            else if (i == 2) column.setPreferredWidth(50);
            else if (i == 3) column.setPreferredWidth(40);
            else if (i == 4) column.setPreferredWidth(40);
            else if (i == 5) column.setPreferredWidth(40);
            else if (i == 6) column.setPreferredWidth(60);
            else if (i == 7) column.setPreferredWidth(115);
            else if (i == 8) column.setPreferredWidth(60);
            else if (i == 9) column.setPreferredWidth(60);
            else if (i == 10) column.setPreferredWidth(200);
            else if (i == 11) column.setPreferredWidth(200);
            else column.setPreferredWidth(80);
        }
        tbHalaman2.setDefaultRenderer(Object.class, new widget.Table().getDefaultRenderer(Object.class));
    }

    private DefaultTableModel createObsModel() {
        return new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Jam", "DJJ", "Air Ketuban", "Penyusupan", "Pembukaan", "Penurunan", "Kontraksi", "Durasi", "Oksitosin", "Tetes", "Obat/Cairan", "Sistole", "Diastole", "Nadi", "Suhu", "Urine Vol", "Protein", "Aseton", "NIP", "Nama Petugas", "ID"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
    }

    private void configObsTable(widget.Table table) {
        table.setPreferredScrollableViewportSize(new Dimension(500, 500));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 21; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(115);
            else column.setPreferredWidth(60);
        }
        table.setDefaultRenderer(Object.class, new widget.Table().getDefaultRenderer(Object.class));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (table.getSelectedRow() != -1) {
                    // Logic to load data from table to fields
                    loadDataObs(table);
                }
            }
        });
    }

    private void loadDataObs(widget.Table table) {
        int row = table.getSelectedRow();
        if (row != -1) {
            TNoRw.setText(table.getValueAt(row, 0).toString());
            Valid.SetTgl2(TTanggal, table.getValueAt(row, 1).toString());
            TDJJ.setText(table.getValueAt(row, 2).toString());
            CAirKetuban.setSelectedItem(table.getValueAt(row, 3).toString());
            CPenyusupan.setSelectedItem(table.getValueAt(row, 4).toString());
            TPembukaan.setText(table.getValueAt(row, 5).toString());
            TPenurunan.setText(table.getValueAt(row, 6).toString());
            TKontraksiJml.setText(table.getValueAt(row, 7).toString());
            CKontraksiDurasi.setSelectedItem(table.getValueAt(row, 8).toString());
            TOksitosinUnit.setText(table.getValueAt(row, 9).toString());
            TOksitosinTetes.setText(table.getValueAt(row, 10).toString());
            TObatCairan.setText(table.getValueAt(row, 11).toString());
            TSistole.setText(table.getValueAt(row, 12).toString());
            TDiastole.setText(table.getValueAt(row, 13).toString());
            TNadi.setText(table.getValueAt(row, 14).toString());
            TSuhu.setText(table.getValueAt(row, 15).toString());
            TUrineVol.setText(table.getValueAt(row, 16).toString());
            TUrineProt.setText(table.getValueAt(row, 17).toString());
            TUrineAseton.setText(table.getValueAt(row, 18).toString());
            TKodePetugas.setText(table.getValueAt(row, 19).toString());
            TNamaPetugas.setText(table.getValueAt(row, 20).toString());
        }
    }

    /** This method is called from within the constructor to initialize the form. */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnKeluar = new widget.Button();

        // Panel Atas
        panelAtas = new widget.panelisi();
        lNoRw = new widget.Label();
        TNoRw = new widget.TextBox();
        lNoRM = new widget.Label();
        TNoRM = new widget.TextBox();
        lNamaPasien = new widget.Label();
        TNamaPasien = new widget.TextBox();
        lTglLahir = new widget.Label();
        TTglLahir = new widget.TextBox();
        lUmur = new widget.Label();
        TUmur = new widget.TextBox();
        lTanggal = new widget.Label();
        TTanggal = new widget.Tanggal();
        lKodePetugas = new widget.Label();
        TKodePetugas = new widget.TextBox();
        BtnCariPetugas = new widget.Button();
        lNamaPetugas = new widget.Label();
        TNamaPetugas = new widget.TextBox();

        // Tab pane
        TabPartograf = new javax.swing.JTabbedPane();

        // Tab 1 - Info Persalinan
        panelTab1 = new widget.panelisi();
        scollTab1 = new widget.ScrollPane();
        formTab1 = new widget.PanelBiasa();
        ScrollPartograf = new widget.ScrollPane();
        tbPartograf = new widget.Table();

        // Tab 2 - DJJ & Janin
        panelTab2 = new widget.panelisi();
        panelTab2.setLayout(new java.awt.BorderLayout());
        scrollTab2 = new widget.ScrollPane();
        // Combine all observation UI elements into formTab2
        formTab2 = new widget.PanelBiasa();
        formTab2.setPreferredSize(new java.awt.Dimension(800, 160));
        formTab2.setLayout(null);
        lDJJ = new widget.Label();
        TDJJ = new widget.TextBox();
        lAirKetuban = new widget.Label();
        CAirKetuban = new widget.ComboBox();
        lPenyusupan = new widget.Label();
        CPenyusupan = new widget.ComboBox();
        lPembukaan = new widget.Label();
        TPembukaan = new widget.TextBox();
        lPenurunan = new widget.Label();
        TPenurunan = new widget.TextBox();
        lKontraksiJml = new widget.Label();
        TKontraksiJml = new widget.TextBox();
        lKontraksiDurasi = new widget.Label();
        CKontraksiDurasi = new widget.ComboBox();
        lSistole = new widget.Label();
        TSistole = new widget.TextBox();
        lDiastole = new widget.Label();
        TDiastole = new widget.TextBox();
        lNadi = new widget.Label();
        TNadi = new widget.TextBox();
        lSuhu = new widget.Label();
        TSuhu = new widget.TextBox();
        lOksitosinUnit = new widget.Label();
        TOksitosinUnit = new widget.TextBox();
        lOksitosinTetes = new widget.Label();
        TOksitosinTetes = new widget.TextBox();
        lObatCairan = new widget.Label();
        TObatCairan = new widget.TextBox();
        lUrineVol = new widget.Label();
        TUrineVol = new widget.TextBox();
        lUrineProt = new widget.Label();
        TUrineProt = new widget.TextBox();
        lUrineAseton = new widget.Label();
        TUrineAseton = new widget.TextBox();

        // Row 1
        lDJJ.setText("DJJ (x/menit) :");
        formTab2.add(lDJJ);
        lDJJ.setBounds(0, 10, 85, 23);
        TDJJ.setName("TDJJ");
        formTab2.add(TDJJ);
        TDJJ.setBounds(89, 10, 60, 23);

        lAirKetuban.setText("Air Ketuban :");
        formTab2.add(lAirKetuban);
        lAirKetuban.setBounds(155, 10, 80, 23);
        CAirKetuban.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "U", "J", "M", "D", "K" }));
        CAirKetuban.setName("CAirKetuban");
        formTab2.add(CAirKetuban);
        CAirKetuban.setBounds(239, 10, 60, 23);

        lPenyusupan.setText("Penyusupan :");
        formTab2.add(lPenyusupan);
        lPenyusupan.setBounds(305, 10, 80, 23);
        CPenyusupan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3" }));
        CPenyusupan.setName("CPenyusupan");
        formTab2.add(CPenyusupan);
        CPenyusupan.setBounds(389, 10, 60, 23);

        lPembukaan.setText("Pembukaan (cm) :");
        formTab2.add(lPembukaan);
        lPembukaan.setBounds(455, 10, 100, 23);
        TPembukaan.setName("TPembukaan");
        formTab2.add(TPembukaan);
        TPembukaan.setBounds(559, 10, 60, 23);

        lPenurunan.setText("Penurunan :");
        formTab2.add(lPenurunan);
        lPenurunan.setBounds(625, 10, 80, 23);
        TPenurunan.setName("TPenurunan");
        formTab2.add(TPenurunan);
        TPenurunan.setBounds(709, 10, 60, 23);

        // Row 2
        lKontraksiJml.setText("Kontraksi/10m :");
        formTab2.add(lKontraksiJml);
        lKontraksiJml.setBounds(0, 40, 85, 23);
        TKontraksiJml.setName("TKontraksiJml");
        formTab2.add(TKontraksiJml);
        TKontraksiJml.setBounds(89, 40, 60, 23);

        lKontraksiDurasi.setText("Durasi (detik) :");
        formTab2.add(lKontraksiDurasi);
        lKontraksiDurasi.setBounds(155, 40, 80, 23);
        CKontraksiDurasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "< 20", "20 - 40", "> 40" }));
        CKontraksiDurasi.setName("CKontraksiDurasi");
        formTab2.add(CKontraksiDurasi);
        CKontraksiDurasi.setBounds(239, 40, 75, 23);

        lOksitosinUnit.setText("Oksitosin U :");
        formTab2.add(lOksitosinUnit);
        lOksitosinUnit.setBounds(305, 40, 80, 23);
        TOksitosinUnit.setName("TOksitosinUnit");
        formTab2.add(TOksitosinUnit);
        TOksitosinUnit.setBounds(389, 40, 60, 23);

        lOksitosinTetes.setText("Tetes/m :");
        formTab2.add(lOksitosinTetes);
        lOksitosinTetes.setBounds(455, 40, 100, 23);
        TOksitosinTetes.setName("TOksitosinTetes");
        formTab2.add(TOksitosinTetes);
        TOksitosinTetes.setBounds(559, 40, 60, 23);

        lObatCairan.setText("Obat/Cairan :");
        formTab2.add(lObatCairan);
        lObatCairan.setBounds(625, 40, 80, 23);
        TObatCairan.setName("TObatCairan");
        formTab2.add(TObatCairan);
        TObatCairan.setBounds(709, 40, 60, 23);

        // Row 3
        lSistole.setText("Sistole :");
        formTab2.add(lSistole);
        lSistole.setBounds(0, 70, 85, 23);
        TSistole.setName("TSistole");
        formTab2.add(TSistole);
        TSistole.setBounds(89, 70, 60, 23);

        lDiastole.setText("Diastole :");
        formTab2.add(lDiastole);
        lDiastole.setBounds(155, 70, 80, 23);
        TDiastole.setName("TDiastole");
        formTab2.add(TDiastole);
        TDiastole.setBounds(239, 70, 60, 23);

        lNadi.setText("Nadi :");
        formTab2.add(lNadi);
        lNadi.setBounds(305, 70, 80, 23);
        TNadi.setName("TNadi");
        formTab2.add(TNadi);
        TNadi.setBounds(389, 70, 60, 23);

        lSuhu.setText("Suhu :");
        formTab2.add(lSuhu);
        lSuhu.setBounds(455, 70, 100, 23);
        TSuhu.setName("TSuhu");
        formTab2.add(TSuhu);
        TSuhu.setBounds(559, 70, 60, 23);

        // Row 4
        lUrineVol.setText("Urine Vol :");
        formTab2.add(lUrineVol);
        lUrineVol.setBounds(0, 100, 85, 23);
        TUrineVol.setName("TUrineVol");
        formTab2.add(TUrineVol);
        TUrineVol.setBounds(89, 100, 60, 23);

        lUrineProt.setText("Protein :");
        formTab2.add(lUrineProt);
        lUrineProt.setBounds(155, 100, 80, 23);
        TUrineProt.setName("TUrineProt");
        formTab2.add(TUrineProt);
        TUrineProt.setBounds(239, 100, 60, 23);

        lUrineAseton.setText("Aseton :");
        formTab2.add(lUrineAseton);
        lUrineAseton.setBounds(305, 100, 80, 23);
        TUrineAseton.setName("TUrineAseton");
        formTab2.add(TUrineAseton);
        TUrineAseton.setBounds(389, 100, 60, 23);

        scrollTab2.setViewportView(formTab2);

        // Halaman 2 - Catatan Persalinan
        // panelHalaman2 = new widget.panelisi(); // Removed - not used
        // panelHalaman2.setLayout(new java.awt.BorderLayout()); // Removed
        scrollHalaman2 = new widget.ScrollPane();
        formHalaman2 = new widget.PanelBiasa();
        formHalaman2.setPreferredSize(new java.awt.Dimension(800, 180));
        formHalaman2.setLayout(null);

        lTglPersalinan = new widget.Label();
        lTglPersalinan.setText("Tgl.Persalinan :");
        formHalaman2.add(lTglPersalinan);
        lTglPersalinan.setBounds(0, 10, 80, 23);

        TTglPersalinan = new widget.Tanggal();
        TTglPersalinan.setDisplayFormat("dd-MM-yyyy");
        TTglPersalinan.setName("TTglPersalinan");
        formHalaman2.add(TTglPersalinan);
        TTglPersalinan.setBounds(84, 10, 100, 23);

        lRujukan = new widget.Label();
        lRujukan.setText("Catatan Rujukan Kala :");
        formHalaman2.add(lRujukan);
        lRujukan.setBounds(0, 40, 130, 23);

        CKala1 = new widget.CekBox();
        CKala1.setText("I");
        formHalaman2.add(CKala1);
        CKala1.setBounds(134, 40, 35, 23);

        CKala2 = new widget.CekBox();
        CKala2.setText("II");
        formHalaman2.add(CKala2);
        CKala2.setBounds(174, 40, 35, 23);

        CKala3 = new widget.CekBox();
        CKala3.setText("III");
        formHalaman2.add(CKala3);
        CKala3.setBounds(214, 40, 40, 23);

        CKala4 = new widget.CekBox();
        CKala4.setText("IV");
        formHalaman2.add(CKala4);
        CKala4.setBounds(258, 40, 40, 23);

        lAlasanMerujuk = new widget.Label();
        lAlasanMerujuk.setText("Alasan Merujuk :");
        formHalaman2.add(lAlasanMerujuk);
        lAlasanMerujuk.setBounds(0, 70, 110, 23);

        TAlasanMerujuk = new widget.TextBox();
        TAlasanMerujuk.setName("TAlasanMerujuk");
        formHalaman2.add(TAlasanMerujuk);
        TAlasanMerujuk.setBounds(114, 70, 400, 23);

        lTempatRujukan = new widget.Label();
        lTempatRujukan.setText("Tempat Rujukan :");
        formHalaman2.add(lTempatRujukan);
        lTempatRujukan.setBounds(0, 100, 110, 23);

        TTempatRujukan = new widget.TextBox();
        TTempatRujukan.setName("TTempatRujukan");
        formHalaman2.add(TTempatRujukan);
        TTempatRujukan.setBounds(114, 100, 400, 23);

        lPendamping = new widget.Label();
        lPendamping.setText("Pendamping saat merujuk :");
        formHalaman2.add(lPendamping);
        lPendamping.setBounds(0, 130, 150, 23);

        CPendampingBidan = new widget.CekBox();
        CPendampingBidan.setText("Bidan");
        formHalaman2.add(CPendampingBidan);
        CPendampingBidan.setBounds(154, 130, 60, 23);

        CPendampingKeluarga = new widget.CekBox();
        CPendampingKeluarga.setText("Keluarga");
        formHalaman2.add(CPendampingKeluarga);
        CPendampingKeluarga.setBounds(214, 130, 75, 23);

        CPendampingTeman = new widget.CekBox();
        CPendampingTeman.setText("Teman");
        formHalaman2.add(CPendampingTeman);
        CPendampingTeman.setBounds(289, 130, 65, 23);

        CPendampingSuami = new widget.CekBox();
        CPendampingSuami.setText("Suami");
        formHalaman2.add(CPendampingSuami);
        CPendampingSuami.setBounds(354, 130, 60, 23);

        CPendampingDukun = new widget.CekBox();
        CPendampingDukun.setText("Dukun");
        formHalaman2.add(CPendampingDukun);
        CPendampingDukun.setBounds(414, 130, 65, 23);

        CPendampingTidakAda = new widget.CekBox();
        CPendampingTidakAda.setText("Tidak Ada");
        formHalaman2.add(CPendampingTidakAda);
        CPendampingTidakAda.setBounds(479, 130, 85, 23);

        formHalaman2.setPreferredSize(new Dimension(800, 180));
        scrollHalaman2.setViewportView(formHalaman2);

        // panelHalaman2.add(scrollHalaman2, java.awt.BorderLayout.PAGE_START); // Removed - scrollHalaman2 now used in panelCatatan

        // ScrollHalaman2 removed - tbHalaman2 used directly
        tbHalaman2 = new widget.Table();
        tbHalaman2.setName("tbHalaman2");
        // panelHalaman2.add(ScrollHalaman2, java.awt.BorderLayout.CENTER); // Removed - panelHalaman2 not used

        // Removed TabPartograf.addTab("Halaman 2", panelHalaman2); from here
        // Tab 6 is removed, its contents moved to Tab 2

        lG = new widget.Label();
        TG = new widget.TextBox();
        lP = new widget.Label();
        TP = new widget.TextBox();
        lA = new widget.Label();
        TA = new widget.TextBox();
        sep1 = new javax.swing.JSeparator();
        lKetubanPecah = new widget.Label();
        TKetubanPecah = new widget.TextBox();
        lMulesSejak = new widget.Label();
        TMulesSejak = new widget.Tanggal();

        // ===== Setup Form =====
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Partograf ]:::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ===== Tombol Aksi =====
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 40));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(evt -> BtnSimpanActionPerformed(evt));
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(evt -> BtnBatalActionPerformed(evt));
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
        panelGlass8.add(BtnEdit);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(evt -> BtnKeluarActionPerformed(evt));
        BtnPrint = new widget.Button();
        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint");
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPrint);
        
        BtnGrafik = new widget.Button();
        BtnGrafik.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnGrafik.setMnemonic('G');
        BtnGrafik.setText("Grafik");
        BtnGrafik.setToolTipText("Alt+G");
        BtnGrafik.setName("BtnGrafik");
        BtnGrafik.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnGrafik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGrafikActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnGrafik);

        BtnHistori = new widget.Button();
        BtnHistori.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        BtnHistori.setMnemonic('I');
        BtnHistori.setText("Histori");
        BtnHistori.setToolTipText("Alt+I");
        BtnHistori.setName("BtnHistori");
        BtnHistori.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHistori.addActionListener(e -> tampilHistory());
        panelGlass8.add(BtnHistori);

        panelGlass8.add(BtnKeluar);

        panelGlass9 = new widget.panelisi();
        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));

        LFilterTanggal = new widget.Label();
        LFilterTanggal.setText("Tanggal :");
        LFilterTanggal.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(LFilterTanggal);

        DTPCari1 = new widget.Tanggal();
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        LSampaiTanggal = new widget.Label();
        LSampaiTanggal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LSampaiTanggal.setText("s.d.");
        LSampaiTanggal.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass9.add(LSampaiTanggal);

        DTPCari2 = new widget.Tanggal();
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        LKeyword = new widget.Label();
        LKeyword.setText("Key Word :");
        LKeyword.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LKeyword);

        TCari = new widget.TextBox();
        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    tampil();
                }
            }
        });
        panelGlass9.add(TCari);



        BtnCari = new widget.Button();
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(evt -> tampil());
        panelGlass9.add(BtnCari);

        BtnAll = new widget.Button();
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(evt -> {
            TCari.setText("");
            tampil();
        });
        panelGlass9.add(BtnAll);

        javax.swing.JPanel PanelBawah = new javax.swing.JPanel();
        PanelBawah.setName("PanelBawah");
        PanelBawah.setLayout(new java.awt.BorderLayout(0, 0));
        PanelBawah.add(panelGlass8, java.awt.BorderLayout.PAGE_START);
        PanelBawah.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelBawah, java.awt.BorderLayout.PAGE_END);

        // ===== Panel Tengah (Panel Atas + Tab) =====
        javax.swing.JPanel panelTengah = new javax.swing.JPanel();
        panelTengah.setBackground(new java.awt.Color(255, 255, 255));
        panelTengah.setLayout(new java.awt.BorderLayout(2, 2));

        // ===== Panel Atas =====
        panelAtas.setBackground(new java.awt.Color(245, 250, 240));
        panelAtas.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 220, 195)));
        panelAtas.setPreferredSize(new java.awt.Dimension(800, 90));
        panelAtas.setLayout(null);

        // Baris 1: No Rawat | No RM | Nama Pasien
        lNoRw.setText("No.Rawat :");
        lNoRw.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lNoRw);
        lNoRw.setBounds(5, 8, 65, 23);

        TNoRw.setEditable(false);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw");
        panelAtas.add(TNoRw);
        TNoRw.setBounds(73, 8, 120, 23);

        lNoRM.setText("No.RM :");
        lNoRM.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lNoRM);
        lNoRM.setBounds(200, 8, 55, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM");
        panelAtas.add(TNoRM);
        TNoRM.setBounds(258, 8, 90, 23);

        lNamaPasien.setText("Nama Pasien :");
        lNamaPasien.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lNamaPasien);
        lNamaPasien.setBounds(355, 8, 85, 23);

        TNamaPasien.setEditable(false);
        TNamaPasien.setHighlighter(null);
        TNamaPasien.setName("TNamaPasien");
        panelAtas.add(TNamaPasien);
        TNamaPasien.setBounds(443, 8, 200, 23);

        // Baris 2: Tgl Lahir | Umur
        lTglLahir.setText("Tgl.Lahir :");
        lTglLahir.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lTglLahir);
        lTglLahir.setBounds(5, 36, 65, 23);

        TTglLahir.setEditable(false);
        TTglLahir.setHighlighter(null);
        TTglLahir.setName("TTglLahir");
        panelAtas.add(TTglLahir);
        TTglLahir.setBounds(73, 36, 90, 23);

        lUmur.setText("Umur :");
        lUmur.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lUmur);
        lUmur.setBounds(170, 36, 45, 23);

        TUmur.setEditable(false);
        TUmur.setHighlighter(null);
        TUmur.setName("TUmur");
        panelAtas.add(TUmur);
        TUmur.setBounds(218, 36, 70, 23);

        // Baris 3: Tanggal (DateTimePicker) | Kode Petugas | Nama Petugas
        lTanggal.setText("Tanggal :");
        lTanggal.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lTanggal);
        lTanggal.setBounds(5, 64, 65, 23);

        TTanggal.setForeground(new java.awt.Color(50, 70, 50));
        TTanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"06-02-2026 13:40:35"}));
        TTanggal.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TTanggal.setName("TTanggal");
        TTanggal.setOpaque(false);
        panelAtas.add(TTanggal);
        TTanggal.setBounds(73, 64, 160, 23);

        lKodePetugas.setText("Kode Petugas :");
        lKodePetugas.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lKodePetugas);
        lKodePetugas.setBounds(240, 64, 90, 23);

        TKodePetugas.setName("TKodePetugas");
        panelAtas.add(TKodePetugas);
        TKodePetugas.setBounds(333, 64, 100, 23);

        BtnCariPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnCariPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariPetugas.setName("BtnCariPetugas");
        BtnCariPetugas.addActionListener(evt -> BtnCariPetugasActionPerformed(evt));
        panelAtas.add(BtnCariPetugas);
        BtnCariPetugas.setBounds(436, 64, 28, 23);

        lNamaPetugas.setText("Nama Petugas :");
        lNamaPetugas.setFont(new java.awt.Font("Tahoma", 0, 11));
        panelAtas.add(lNamaPetugas);
        lNamaPetugas.setBounds(470, 64, 90, 23);

        TNamaPetugas.setEditable(false);
        TNamaPetugas.setHighlighter(null);
        TNamaPetugas.setName("TNamaPetugas");
        panelAtas.add(TNamaPetugas);
        TNamaPetugas.setBounds(563, 64, 200, 23);

        panelTengah.add(panelAtas, java.awt.BorderLayout.PAGE_START);

        // ===== Tab 1: Info Persalinan =====
        panelTab1.setBackground(new java.awt.Color(255, 255, 255));
        panelTab1.setBorder(null);
        panelTab1.setLayout(new java.awt.BorderLayout());

        scollTab1.setName("scollTab1");
        formTab1.setBackground(new java.awt.Color(255, 255, 255));
        formTab1.setPreferredSize(new java.awt.Dimension(800, 300));
        formTab1.setLayout(null);

        // G P A
        lG.setText("G :");
        lG.setFont(new java.awt.Font("Tahoma", 1, 11));
        formTab1.add(lG);
        lG.setBounds(10, 15, 30, 23);

        TG.setName("TG");
        TG.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        formTab1.add(TG);
        TG.setBounds(43, 15, 45, 23);

        lP.setText("P :");
        lP.setFont(new java.awt.Font("Tahoma", 1, 11));
        formTab1.add(lP);
        lP.setBounds(100, 15, 30, 23);

        TP.setName("TP");
        TP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        formTab1.add(TP);
        TP.setBounds(130, 15, 45, 23);

        lA.setText("A :");
        lA.setFont(new java.awt.Font("Tahoma", 1, 11));
        formTab1.add(lA);
        lA.setBounds(185, 15, 30, 23);

        TA.setName("TA");
        TA.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        formTab1.add(TA);
        TA.setBounds(215, 15, 45, 23);

        // Separator
        sep1.setBackground(new java.awt.Color(200, 220, 195));
        sep1.setForeground(new java.awt.Color(200, 220, 195));
        formTab1.add(sep1);
        sep1.setBounds(0, 48, 800, 2);

        // Ketuban Pecah Sejak Pukul
        lKetubanPecah.setText("Ketuban Pecah Sejak Pukul :");
        lKetubanPecah.setFont(new java.awt.Font("Tahoma", 0, 11));
        formTab1.add(lKetubanPecah);
        lKetubanPecah.setBounds(10, 60, 165, 23);

        TKetubanPecah.setName("TKetubanPecah");
        formTab1.add(TKetubanPecah);
        TKetubanPecah.setBounds(178, 60, 120, 23);

        lMulesSejak.setText("Mules Sejak Pukul :");
        lMulesSejak.setFont(new java.awt.Font("Tahoma", 0, 11));
        formTab1.add(lMulesSejak);
        lMulesSejak.setBounds(10, 93, 120, 23);

        TMulesSejak.setForeground(new java.awt.Color(50, 70, 50));
        TMulesSejak.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"06-02-2026 13:40:35"}));
        TMulesSejak.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TMulesSejak.setName("TMulesSejak");
        TMulesSejak.setOpaque(false);
        formTab1.add(TMulesSejak);
        TMulesSejak.setBounds(133, 93, 160, 23);

        scollTab1.setViewportView(formTab1);
        panelTab1.add(scollTab1, java.awt.BorderLayout.PAGE_START);

        ScrollPartograf.setName("ScrollPartograf");
        tbPartograf.setName("tbPartograf");
        tbPartograf.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPartografMouseClicked(evt);
            }
        });
        ScrollPartograf.setViewportView(tbPartograf);
        panelTab1.add(ScrollPartograf, java.awt.BorderLayout.CENTER);


        // ===== Tab Partograf Setup =====

        TabPartograf.setBackground(new java.awt.Color(254, 255, 254));
        TabPartograf.setForeground(new java.awt.Color(50, 50, 50));
        TabPartograf.setFont(new java.awt.Font("Tahoma", 0, 11));
        TabPartograf.setName("TabPartograf");
        TabPartograf.addTab("Data Awal", panelTab1);
        TabPartograf.addTab("Observasi", panelTab2);

        // Initialize table models and tables
        scrollCatatanPersalinan = new widget.ScrollPane();
        tabModeCatatanPersalinan = new DefaultTableModel(null, new Object[]{"No.Rawat", "Tgl.Persalinan", "Rujukan", "Alasan", "Tempat", "Pendamping"}) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbCatatanPersalinan = new widget.Table();
        tbCatatanPersalinan.setModel(tabModeCatatanPersalinan);
        tbCatatanPersalinan.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 500));
        tbCatatanPersalinan.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 6; i++) {
            javax.swing.table.TableColumn column = tbCatatanPersalinan.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(80);
            else if (i == 2) column.setPreferredWidth(60);
            else if (i == 3) column.setPreferredWidth(150);
            else if (i == 4) column.setPreferredWidth(150);
            else if (i == 5) column.setPreferredWidth(150);
        }
        tbCatatanPersalinan.setDefaultRenderer(Object.class, new widget.Table().getDefaultRenderer(Object.class));
        scrollCatatanPersalinan.setViewportView(tbCatatanPersalinan);
        scrollCatatanPersalinan.setMinimumSize(new java.awt.Dimension(400, 200));

        scrollKala1 = new widget.ScrollPane();
        tabModeKala1 = new DefaultTableModel(null, new Object[]{"No.Rawat", "Tgl.Persalinan", "Waspada", "Masalah", "Tindakan", "Hasil"}) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbKala1 = new widget.Table();
        tbKala1.setModel(tabModeKala1);
        scrollKala1.setViewportView(tbKala1);

        scrollKala2 = new widget.ScrollPane();
        tabModeKala2 = new DefaultTableModel(null, new Object[]{"No.Rawat", "Tgl.Persalinan", "Episiotomi", "Gawat Janin", "Distosia", "Masalah", "Tindakan", "Hasil"}) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbKala2 = new widget.Table();
        tbKala2.setModel(tabModeKala2);
        scrollKala2.setViewportView(tbKala2);

        scrollKala3 = new widget.ScrollPane();
        tabModeKala3 = new DefaultTableModel(null, new Object[]{"No.Rawat", "Tgl.Persalinan", "Lama", "Oksitosin", "Masase", "Plasenta Lengkap"}) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbKala3 = new widget.Table();
        tbKala3.setModel(tabModeKala3);
        scrollKala3.setViewportView(tbKala3);

        scrollBBL = new widget.ScrollPane();
        tabModeBBL = new DefaultTableModel(null, new Object[]{"No.Rawat", "Tgl.Persalinan", "JK", "BB", "PB", "Lahir", "Penilaian", "Masalah", "Hasil"}) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbBBL = new widget.Table();
        tbBBL.setModel(tabModeBBL);
        scrollBBL.setViewportView(tbBBL);

        scrollMasalahKala4 = new widget.ScrollPane();
        tabModeMasalahKala4 = new DefaultTableModel(null, new Object[]{"No.Rawat", "Tgl.Persalinan", "Masalah", "Tindakan", "Hasil"}) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbMasalahKala4 = new widget.Table();
        tbMasalahKala4.setModel(tabModeMasalahKala4);
        scrollMasalahKala4.setViewportView(tbMasalahKala4);

        // Halaman 2 Sub-Tabs Initialization
        TabHalaman2 = new javax.swing.JTabbedPane();
        
        panelCatatan = new widget.panelisi();
        panelCatatan.setName("panelCatatan");
        panelCatatan.setLayout(new java.awt.BorderLayout(1, 1));
        panelCatatan.add(scrollHalaman2, java.awt.BorderLayout.PAGE_START);
        panelCatatan.add(scrollCatatanPersalinan, java.awt.BorderLayout.CENTER);
        
        panelKala1 = new widget.panelisi();
        panelKala1.setName("panelKala1");
        
        panelKala2 = new widget.panelisi();
        panelKala2.setName("panelKala2");
        
        panelKala3 = new widget.panelisi();
        panelKala3.setName("panelKala3");
        
        panelBBL = new widget.panelisi();
        panelBBL.setName("panelBBL");
        
        panelKala4 = new widget.panelisi();
        panelKala4.setName("panelKala4");
        
        panelMasalahKala4 = new widget.panelisi();
        panelMasalahKala4.setName("panelMasalahKala4");
        
        TabHalaman2.addTab("Catatan Persalinan", panelCatatan);
        
        // KALA I Initialization
        panelKala1.setLayout(new java.awt.BorderLayout());
        widget.ScrollPane scrollFormKala1 = new widget.ScrollPane();
        widget.PanelBiasa formKala1 = new widget.PanelBiasa();
        formKala1.setPreferredSize(new java.awt.Dimension(800, 200));
        formKala1.setLayout(null);
        
        lKala1Waspada = new widget.Label();
        lKala1Waspada.setText("9. Partograf melewati garis waspada :");
        formKala1.add(lKala1Waspada);
        lKala1Waspada.setBounds(0, 10, 210, 23);
        CKala1Waspada = new widget.ComboBox();
        CKala1Waspada.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        formKala1.add(CKala1Waspada);
        CKala1Waspada.setBounds(214, 10, 60, 23);
        
        lKala1Masalah = new widget.Label();
        lKala1Masalah.setText("10. Masalah lain, sebutkan :");
        formKala1.add(lKala1Masalah);
        lKala1Masalah.setBounds(0, 40, 160, 23);
        TKala1Masalah = new widget.TextBox();
        formKala1.add(TKala1Masalah);
        TKala1Masalah.setBounds(164, 40, 400, 23);
        
        lKala1Tindakan = new widget.Label();
        lKala1Tindakan.setText("11. Penatalaksanaan masalah tersebut :");
        formKala1.add(lKala1Tindakan);
        lKala1Tindakan.setBounds(0, 70, 220, 23);
        TKala1Tindakan = new widget.TextBox();
        formKala1.add(TKala1Tindakan);
        TKala1Tindakan.setBounds(224, 70, 400, 23);
        
        lKala1Hasil = new widget.Label();
        lKala1Hasil.setText("12. Hasilnya :");
        formKala1.add(lKala1Hasil);
        lKala1Hasil.setBounds(0, 100, 80, 23);
        TKala1Hasil = new widget.TextBox();
        formKala1.add(TKala1Hasil);
        TKala1Hasil.setBounds(84, 100, 400, 23);
        
        scrollFormKala1.setViewportView(formKala1);
        JSplitPane splitKala1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitKala1.setTopComponent(scrollFormKala1);
        splitKala1.setBottomComponent(this.scrollKala1);
        splitKala1.setDividerLocation(150);
        splitKala1.setResizeWeight(0.5);
        panelKala1.add(splitKala1, java.awt.BorderLayout.CENTER);
        TabHalaman2.addTab("Kala I", panelKala1);
        
        // KALA II Initialization
        panelKala2.setLayout(new java.awt.BorderLayout());
        widget.ScrollPane scrollFormKala2 = new widget.ScrollPane();
        widget.PanelBiasa formKala2 = new widget.PanelBiasa();
        formKala2.setPreferredSize(new java.awt.Dimension(800, 300));
        formKala2.setLayout(null);
        
        lKala2Episiotomi = new widget.Label();
        lKala2Episiotomi.setText("13. Episiotomi :");
        formKala2.add(lKala2Episiotomi);
        lKala2Episiotomi.setBounds(0, 10, 100, 23);
        CKala2Episiotomi = new widget.ComboBox();
        CKala2Episiotomi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        formKala2.add(CKala2Episiotomi);
        CKala2Episiotomi.setBounds(104, 10, 100, 23);

        lKala2EpisiotomiAlasan = new widget.Label();
        lKala2EpisiotomiAlasan.setText("Indikasi :");
        formKala2.add(lKala2EpisiotomiAlasan);
        lKala2EpisiotomiAlasan.setBounds(210, 10, 60, 23);
        lKala2EpisiotomiAlasan.setVisible(false);
        TKala2EpisiotomiAlasan = new widget.TextBox();
        formKala2.add(TKala2EpisiotomiAlasan);
        TKala2EpisiotomiAlasan.setBounds(274, 10, 200, 23);
        TKala2EpisiotomiAlasan.setVisible(false);

        CKala2Episiotomi.addItemListener(new java.awt.event.ItemListener() {
            @Override public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(CKala2Episiotomi.getSelectedItem().toString().equals("Ya")){
                    lKala2EpisiotomiAlasan.setVisible(true);
                    TKala2EpisiotomiAlasan.setVisible(true);
                }else{
                    lKala2EpisiotomiAlasan.setVisible(false);
                    TKala2EpisiotomiAlasan.setVisible(false);
                    TKala2EpisiotomiAlasan.setText("");
                }
            }
        });
        
        lKala2Pendamping = new widget.Label();
        lKala2Pendamping.setText("14. Pendamping saat persalinan :");
        formKala2.add(lKala2Pendamping);
        lKala2Pendamping.setBounds(0, 40, 180, 23);
        CKala2PendampingSuami = new widget.CekBox();
        CKala2PendampingSuami.setText("Suami");
        formKala2.add(CKala2PendampingSuami);
        CKala2PendampingSuami.setBounds(184, 40, 60, 23);
        CKala2PendampingKeluarga = new widget.CekBox();
        CKala2PendampingKeluarga.setText("Keluarga");
        formKala2.add(CKala2PendampingKeluarga);
        CKala2PendampingKeluarga.setBounds(244, 40, 75, 23);
        CKala2PendampingTeman = new widget.CekBox();
        CKala2PendampingTeman.setText("Teman");
        formKala2.add(CKala2PendampingTeman);
        CKala2PendampingTeman.setBounds(319, 40, 65, 23);
        CKala2PendampingDukun = new widget.CekBox();
        CKala2PendampingDukun.setText("Dukun");
        formKala2.add(CKala2PendampingDukun);
        CKala2PendampingDukun.setBounds(384, 40, 60, 23);
        CKala2PendampingTidakAda = new widget.CekBox();
        CKala2PendampingTidakAda.setText("Tidak Ada");
        formKala2.add(CKala2PendampingTidakAda);
        CKala2PendampingTidakAda.setBounds(444, 40, 85, 23);
        
        lKala2GawatJanin = new widget.Label();
        lKala2GawatJanin.setText("15. Gawat janin :");
        formKala2.add(lKala2GawatJanin);
        lKala2GawatJanin.setBounds(0, 70, 100, 23);
        CKala2GawatJanin = new widget.ComboBox();
        CKala2GawatJanin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        formKala2.add(CKala2GawatJanin);
        CKala2GawatJanin.setBounds(104, 70, 100, 23);

        lKala2GawatJaninTindakan = new widget.Label();
        lKala2GawatJaninTindakan.setText("Tindakan :");
        formKala2.add(lKala2GawatJaninTindakan);
        lKala2GawatJaninTindakan.setBounds(210, 70, 70, 23);
        lKala2GawatJaninTindakan.setVisible(false);
        TKala2GawatJaninTindakan = new widget.TextBox();
        formKala2.add(TKala2GawatJaninTindakan);
        TKala2GawatJaninTindakan.setBounds(284, 70, 200, 23);
        TKala2GawatJaninTindakan.setVisible(false);

        CKala2GawatJanin.addItemListener(new java.awt.event.ItemListener() {
            @Override public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(CKala2GawatJanin.getSelectedItem().toString().equals("Ya")){
                    lKala2GawatJaninTindakan.setVisible(true);
                    TKala2GawatJaninTindakan.setVisible(true);
                }else{
                    lKala2GawatJaninTindakan.setVisible(false);
                    TKala2GawatJaninTindakan.setVisible(false);
                    TKala2GawatJaninTindakan.setText("");
                }
            }
        });
        
        lKala2Distosia = new widget.Label();
        lKala2Distosia.setText("16. Distosia bahu :");
        formKala2.add(lKala2Distosia);
        lKala2Distosia.setBounds(0, 100, 100, 23);
        CKala2Distosia = new widget.ComboBox();
        CKala2Distosia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        formKala2.add(CKala2Distosia);
        CKala2Distosia.setBounds(104, 100, 100, 23);

        lKala2DistosiaTindakan = new widget.Label();
        lKala2DistosiaTindakan.setText("Tindakan :");
        formKala2.add(lKala2DistosiaTindakan);
        lKala2DistosiaTindakan.setBounds(210, 100, 70, 23);
        lKala2DistosiaTindakan.setVisible(false);
        TKala2DistosiaTindakan = new widget.TextBox();
        formKala2.add(TKala2DistosiaTindakan);
        TKala2DistosiaTindakan.setBounds(284, 100, 200, 23);
        TKala2DistosiaTindakan.setVisible(false);

        CKala2Distosia.addItemListener(new java.awt.event.ItemListener() {
            @Override public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(CKala2Distosia.getSelectedItem().toString().equals("Ya")){
                    lKala2DistosiaTindakan.setVisible(true);
                    TKala2DistosiaTindakan.setVisible(true);
                }else{
                    lKala2DistosiaTindakan.setVisible(false);
                    TKala2DistosiaTindakan.setVisible(false);
                    TKala2DistosiaTindakan.setText("");
                }
            }
        });
        
        lKala2Masalah = new widget.Label();
        lKala2Masalah.setText("17. Masalah lain :");
        formKala2.add(lKala2Masalah);
        lKala2Masalah.setBounds(0, 130, 100, 23);
        TKala2Masalah = new widget.TextBox();
        formKala2.add(TKala2Masalah);
        TKala2Masalah.setBounds(104, 130, 300, 23);
        
        lKala2Tindakan = new widget.Label();
        lKala2Tindakan.setText("18. Penatalaksanaan :");
        formKala2.add(lKala2Tindakan);
        lKala2Tindakan.setBounds(0, 160, 100, 23);
        TKala2Tindakan = new widget.TextBox();
        formKala2.add(TKala2Tindakan);
        TKala2Tindakan.setBounds(104, 160, 300, 23);
        
        lKala2Hasil = new widget.Label();
        lKala2Hasil.setText("19. Hasilnya :");
        formKala2.add(lKala2Hasil);
        lKala2Hasil.setBounds(0, 190, 100, 23);
        TKala2Hasil = new widget.TextBox();
        formKala2.add(TKala2Hasil);
        TKala2Hasil.setBounds(104, 190, 300, 23);
        
        scrollFormKala2.setViewportView(formKala2);
        JSplitPane splitKala2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitKala2.setTopComponent(scrollFormKala2);
        splitKala2.setBottomComponent(this.scrollKala2);
        splitKala2.setDividerLocation(150);
        splitKala2.setResizeWeight(0.5);
        panelKala2.add(splitKala2, java.awt.BorderLayout.CENTER);
        TabHalaman2.addTab("Kala II", panelKala2);
        
        // KALA III Initialization
        panelKala3.setLayout(new java.awt.BorderLayout());
        widget.ScrollPane scrollFormKala3 = new widget.ScrollPane();
        widget.PanelBiasa formKala3 = new widget.PanelBiasa();
        formKala3.setPreferredSize(new java.awt.Dimension(800, 450));
        formKala3.setLayout(null);
        
        lKala3Lama = new widget.Label();
        lKala3Lama.setText("20. Lama Kala III (mnt) :");
        formKala3.add(lKala3Lama);
        lKala3Lama.setBounds(0, 10, 130, 23);
        TKala3Lama = new widget.TextBox();
        formKala3.add(TKala3Lama);
        TKala3Lama.setBounds(134, 10, 50, 23);
        
        lKala3Oksitosin = new widget.Label();
        lKala3Oksitosin.setText("21. Pemberian Oksitosin 10U :");
        formKala3.add(lKala3Oksitosin);
        lKala3Oksitosin.setBounds(0, 45, 160, 23);
        CKala3Oksitosin = new widget.ComboBox();
        CKala3Oksitosin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak, alasan:" }));
        formKala3.add(CKala3Oksitosin);
        CKala3Oksitosin.setBounds(164, 45, 120, 23);
        TKala3OksitosinAlasan = new widget.TextBox();
        formKala3.add(TKala3OksitosinAlasan);
        TKala3OksitosinAlasan.setBounds(286, 45, 300, 23);
        
        lKala3TaliPusat = new widget.Label();
        lKala3TaliPusat.setText("22. Penegangan Tali Pusat :");
        formKala3.add(lKala3TaliPusat);
        lKala3TaliPusat.setBounds(0, 80, 150, 23);
        CKala3TaliPusat = new widget.ComboBox();
        CKala3TaliPusat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak, alasan:" }));
        formKala3.add(CKala3TaliPusat);
        CKala3TaliPusat.setBounds(154, 80, 120, 23);
        TKala3TaliPusatAlasan = new widget.TextBox();
        formKala3.add(TKala3TaliPusatAlasan);
        TKala3TaliPusatAlasan.setBounds(276, 80, 250, 23);

        lKala3Masase = new widget.Label();
        lKala3Masase.setText("24. Masase Fundus Uteri :");
        formKala3.add(lKala3Masase);
        lKala3Masase.setBounds(0, 115, 140, 23);
        CKala3Masase = new widget.ComboBox();
        CKala3Masase.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak, alasan:" }));
        formKala3.add(CKala3Masase);
        CKala3Masase.setBounds(144, 115, 120, 23);
        TKala3MasaseAlasan = new widget.TextBox();
        formKala3.add(TKala3MasaseAlasan);
        TKala3MasaseAlasan.setBounds(266, 115, 300, 23);

        lKala3PlasentaLengkap = new widget.Label();
        lKala3PlasentaLengkap.setText("25. Plasenta Lahir Lengkap :");
        formKala3.add(lKala3PlasentaLengkap);
        lKala3PlasentaLengkap.setBounds(0, 150, 150, 23);
        CKala3PlasentaLengkap = new widget.ComboBox();
        CKala3PlasentaLengkap.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak, Tindakan:" }));
        formKala3.add(CKala3PlasentaLengkap);
        CKala3PlasentaLengkap.setBounds(154, 150, 120, 23);
        TKala3PlasentaTindakan = new widget.TextBox();
        formKala3.add(TKala3PlasentaTindakan);
        TKala3PlasentaTindakan.setBounds(276, 150, 300, 23);
        
        lKala3PlasentaTdkLahir = new widget.Label();
        lKala3PlasentaTdkLahir.setText("26. Plasenta tdk lahir >30 mnt :");
        formKala3.add(lKala3PlasentaTdkLahir);
        lKala3PlasentaTdkLahir.setBounds(0, 185, 170, 23);
        CKala3PlasentaTdkLahir = new widget.ComboBox();
        CKala3PlasentaTdkLahir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        formKala3.add(CKala3PlasentaTdkLahir);
        CKala3PlasentaTdkLahir.setBounds(174, 185, 80, 23);
        TKala3PlasentaTdkLahirTindakan = new widget.TextBox();
        formKala3.add(TKala3PlasentaTdkLahirTindakan);
        TKala3PlasentaTdkLahirTindakan.setBounds(258, 185, 300, 23);
        
        lKala3Laserasi = new widget.Label();
        lKala3Laserasi.setText("27. Laserasi :");
        formKala3.add(lKala3Laserasi);
        lKala3Laserasi.setBounds(0, 220, 100, 23);
        CKala3Laserasi = new widget.ComboBox();
        CKala3Laserasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya, Lokasi:" }));
        formKala3.add(CKala3Laserasi);
        CKala3Laserasi.setBounds(104, 220, 120, 23);
        TKala3LaserasiLokasi = new widget.TextBox();
        formKala3.add(TKala3LaserasiLokasi);
        TKala3LaserasiLokasi.setBounds(228, 220, 200, 23);
        
        lKala3LaserasiDerajat = new widget.Label();
        lKala3LaserasiDerajat.setText("28. Jika Ya, Derajat :");
        formKala3.add(lKala3LaserasiDerajat);
        lKala3LaserasiDerajat.setBounds(430, 220, 110, 23);
        CKala3LaserasiDerajat = new widget.ComboBox();
        CKala3LaserasiDerajat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4" }));
        formKala3.add(CKala3LaserasiDerajat);
        CKala3LaserasiDerajat.setBounds(544, 220, 60, 23);
        
        lKala3Atonia = new widget.Label();
        lKala3Atonia.setText("29. Atonia Uteri :");
        formKala3.add(lKala3Atonia);
        lKala3Atonia.setBounds(0, 255, 100, 23);
        CKala3Atonia = new widget.ComboBox();
        CKala3Atonia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya, Tindakan:" }));
        formKala3.add(CKala3Atonia);
        CKala3Atonia.setBounds(104, 255, 120, 23);
        TKala3AtoniaTindakan = new widget.TextBox();
        formKala3.add(TKala3AtoniaTindakan);
        TKala3AtoniaTindakan.setBounds(228, 255, 300, 23);
        
        lKala3Pendarahan = new widget.Label();
        lKala3Pendarahan.setText("30. Jumlah Pendarahan (ml) :");
        formKala3.add(lKala3Pendarahan);
        lKala3Pendarahan.setBounds(0, 290, 160, 23);
        TKala3Pendarahan = new widget.TextBox();
        formKala3.add(TKala3Pendarahan);
        TKala3Pendarahan.setBounds(164, 290, 80, 23);
        
        lKala3Masalah = new widget.Label();
        lKala3Masalah.setText("31. Masalah lain :");
        formKala3.add(lKala3Masalah);
        lKala3Masalah.setBounds(0, 325, 100, 23);
        TKala3Masalah = new widget.TextBox();
        formKala3.add(TKala3Masalah);
        TKala3Masalah.setBounds(104, 325, 400, 23);
        
        lKala3Tindakan = new widget.Label();
        lKala3Tindakan.setText("32. Penatalaksanaan :");
        formKala3.add(lKala3Tindakan);
        lKala3Tindakan.setBounds(0, 360, 110, 23);
        TKala3Tindakan = new widget.TextBox();
        formKala3.add(TKala3Tindakan);
        TKala3Tindakan.setBounds(114, 360, 400, 23);
        
        lKala3Hasil = new widget.Label();
        lKala3Hasil.setText("33. Hasilnya :");
        formKala3.add(lKala3Hasil);
        lKala3Hasil.setBounds(0, 395, 80, 23);
        TKala3Hasil = new widget.TextBox();
        formKala3.add(TKala3Hasil);
        TKala3Hasil.setBounds(84, 395, 400, 23);
        
        scrollFormKala3.setViewportView(formKala3);
        JSplitPane splitKala3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitKala3.setTopComponent(scrollFormKala3);
        splitKala3.setBottomComponent(this.scrollKala3);
        splitKala3.setDividerLocation(150);
        splitKala3.setResizeWeight(0.5);
        panelKala3.add(splitKala3, java.awt.BorderLayout.CENTER);
        TabHalaman2.addTab("Kala III", panelKala3);

        // BAYI BARU LAHIR Initialization
        panelBBL.setLayout(new java.awt.BorderLayout());
        widget.ScrollPane scrollFormBBL = new widget.ScrollPane();
        widget.PanelBiasa formBBL = new widget.PanelBiasa();
        formBBL.setPreferredSize(new java.awt.Dimension(800, 300));
        formBBL.setLayout(null);
        
        lBBLBB = new widget.Label();
        lBBLBB.setText("34. Berat Badan (gr) :");
        formBBL.add(lBBLBB);
        lBBLBB.setBounds(0, 10, 120, 23);
        TBBLBB = new widget.TextBox();
        formBBL.add(TBBLBB);
        TBBLBB.setBounds(124, 10, 60, 23);
        
        lBBLPB = new widget.Label();
        lBBLPB.setText("35. Panjang (cm) :");
        formBBL.add(lBBLPB);
        lBBLPB.setBounds(200, 10, 100, 23);
        TBBLPB = new widget.TextBox();
        formBBL.add(TBBLPB);
        TBBLPB.setBounds(304, 10, 60, 23);
        
        lBBLJK = new widget.Label();
        lBBLJK.setText("36. Jenis Kelamin :");
        formBBL.add(lBBLJK);
        lBBLJK.setBounds(400, 10, 100, 23);
        CBBLJK = new widget.ComboBox();
        CBBLJK.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "L", "P" }));
        formBBL.add(CBBLJK);
        CBBLJK.setBounds(504, 10, 50, 23);
        
        CBBLPenilaian = new widget.ComboBox();
        CBBLPenilaian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Baik", "Ada Penyulit" }));
        formBBL.add(CBBLPenilaian);
        CBBLPenilaian.setBounds(124, 40, 100, 23);
        
        lBBLPenilaian = new widget.Label();
        lBBLPenilaian.setText("37. Penilaian :");
        formBBL.add(lBBLPenilaian);
        lBBLPenilaian.setBounds(0, 40, 100, 23);

        CBBLBayiLahir = new widget.ComboBox();
        CBBLBayiLahir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Aspiksia", "Cacat", "Hipotermi" }));
        formBBL.add(CBBLBayiLahir);
        CBBLBayiLahir.setBounds(124, 70, 100, 23);
        
        lBBLBayiLahir = new widget.Label();
        lBBLBayiLahir.setText("38. Bayi Lahir :");
        formBBL.add(lBBLBayiLahir);
        lBBLBayiLahir.setBounds(0, 70, 100, 23);

        lBBLASI = new widget.Label();
        lBBLASI.setText("39. Menyusui :");
        formBBL.add(lBBLASI);
        lBBLASI.setBounds(250, 40, 100, 23);
        CBBLASI = new widget.ComboBox();
        CBBLASI.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        formBBL.add(CBBLASI);
        CBBLASI.setBounds(354, 40, 60, 23);

        lBBLWaktuASI = new widget.Label();
        lBBLWaktuASI.setText("Waktu ASI :");
        formBBL.add(lBBLWaktuASI);
        lBBLWaktuASI.setBounds(250, 70, 100, 23);
        TBBLASITglJam = new widget.TextBox();
        formBBL.add(TBBLASITglJam);
        TBBLASITglJam.setBounds(354, 70, 100, 23);

        lBBLTindakan = new widget.Label();
        lBBLTindakan.setText("Tindakan :");
        formBBL.add(lBBLTindakan);
        lBBLTindakan.setBounds(450, 40, 100, 23);
        TBBLNormalTindakan = new widget.TextBox();
        formBBL.add(TBBLNormalTindakan);
        TBBLNormalTindakan.setBounds(554, 40, 200, 23);

        lBBLMasalah = new widget.Label();
        lBBLMasalah.setText("40. Masalah :");
        formBBL.add(lBBLMasalah);
        lBBLMasalah.setBounds(0, 100, 100, 23);
        TBBLMasalah = new widget.TextBox();
        formBBL.add(TBBLMasalah);
        TBBLMasalah.setBounds(104, 100, 400, 23);

        lBBLHasil = new widget.Label();
        lBBLHasil.setText("41. Hasil :");
        formBBL.add(lBBLHasil);
        lBBLHasil.setBounds(0, 130, 100, 23);
        TBBLHasil = new widget.TextBox();
        formBBL.add(TBBLHasil);
        TBBLHasil.setBounds(104, 130, 400, 23);
        
        scrollFormBBL.setViewportView(formBBL);
        JSplitPane splitBBL = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitBBL.setTopComponent(scrollFormBBL);
        splitBBL.setBottomComponent(this.scrollBBL);
        splitBBL.setDividerLocation(150);
        splitBBL.setResizeWeight(0.5);
        panelBBL.add(splitBBL, java.awt.BorderLayout.CENTER);
        TabHalaman2.addTab("Bayi Baru Lahir", panelBBL);
        
        // KALA IV Initialization
        panelKala4.setLayout(new java.awt.BorderLayout());
        widget.PanelBiasa formKala4 = new widget.PanelBiasa();
        formKala4.setPreferredSize(new java.awt.Dimension(800, 150));
        formKala4.setLayout(null);

        lKala4Jam = new widget.Label();
        lKala4Jam.setText("Jam ke :");
        formKala4.add(lKala4Jam);
        lKala4Jam.setBounds(0, 10, 60, 23);
        TKala4Jam = new widget.TextBox();
        formKala4.add(TKala4Jam);
        TKala4Jam.setBounds(64, 10, 40, 23);

        lKala4Waktu = new widget.Label();
        lKala4Waktu.setText("Waktu :");
        formKala4.add(lKala4Waktu);
        lKala4Waktu.setBounds(110, 10, 50, 23);
        TKala4Waktu = new widget.TextBox();
        formKala4.add(TKala4Waktu);
        TKala4Waktu.setBounds(164, 10, 60, 23);

        lKala4TD = new widget.Label();
        lKala4TD.setText("TD :");
        formKala4.add(lKala4TD);
        lKala4TD.setBounds(230, 10, 30, 23);
        TKala4TD = new widget.TextBox();
        formKala4.add(TKala4TD);
        TKala4TD.setBounds(264, 10, 60, 23);

        lKala4Nadi = new widget.Label();
        lKala4Nadi.setText("Nadi :");
        formKala4.add(lKala4Nadi);
        lKala4Nadi.setBounds(330, 10, 40, 23);
        TKala4Nadi = new widget.TextBox();
        formKala4.add(TKala4Nadi);
        TKala4Nadi.setBounds(374, 10, 40, 23);

        lKala4Suhu = new widget.Label();
        lKala4Suhu.setText("Suhu :");
        formKala4.add(lKala4Suhu);
        lKala4Suhu.setBounds(420, 10, 40, 23);
        TKala4Suhu = new widget.TextBox();
        formKala4.add(TKala4Suhu);
        TKala4Suhu.setBounds(464, 10, 40, 23);

        lKala4TFU = new widget.Label();
        lKala4TFU.setText("TFU :");
        formKala4.add(lKala4TFU);
        lKala4TFU.setBounds(0, 40, 60, 23);
        TKala4TFU = new widget.TextBox();
        formKala4.add(TKala4TFU);
        TKala4TFU.setBounds(64, 40, 100, 23);

        lKala4Kontraksi = new widget.Label();
        lKala4Kontraksi.setText("Kontraksi :");
        formKala4.add(lKala4Kontraksi);
        lKala4Kontraksi.setBounds(174, 40, 70, 23);
        TKala4Kontraksi = new widget.TextBox();
        formKala4.add(TKala4Kontraksi);
        TKala4Kontraksi.setBounds(248, 40, 100, 23);

        lKala4Kandung = new widget.Label();
        lKala4Kandung.setText("Kandung Kemih :");
        formKala4.add(lKala4Kandung);
        lKala4Kandung.setBounds(358, 40, 100, 23);
        TKala4Kandung = new widget.TextBox();
        formKala4.add(TKala4Kandung);
        TKala4Kandung.setBounds(462, 40, 100, 23);

        lKala4Pendarahan = new widget.Label();
        lKala4Pendarahan.setText("Pendarahan (ml) :");
        formKala4.add(lKala4Pendarahan);
        lKala4Pendarahan.setBounds(0, 70, 110, 23);
        TKala4Pendarahan = new widget.TextBox();
        formKala4.add(TKala4Pendarahan);
        TKala4Pendarahan.setBounds(114, 70, 60, 23);


        widget.ScrollPane scrollFormKala4 = new widget.ScrollPane();
        scrollFormKala4.setViewportView(formKala4);
        
        ScrollKala4 = new widget.ScrollPane();
        tbKala4 = new widget.Table();
        tabModeKala4 = new DefaultTableModel(null, new Object[]{
            "Jam ke", "Waktu", "TD", "Nadi", "Suhu", "TFU", "Kontraksi", "Kandung Kemih", "Pendarahan", "id"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbKala4.setModel(tabModeKala4);
        tbKala4.getColumnModel().getColumn(9).setMinWidth(0);
        tbKala4.getColumnModel().getColumn(9).setMaxWidth(0);
        tbKala4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { getDataKala4(); }
        });
        ScrollKala4.setViewportView(tbKala4);
        
        JSplitPane splitKala4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitKala4.setTopComponent(scrollFormKala4);
        splitKala4.setBottomComponent(ScrollKala4);
        splitKala4.setDividerLocation(120);
        splitKala4.setResizeWeight(0.5);
        
        panelKala4.add(splitKala4, java.awt.BorderLayout.CENTER);
        TabHalaman2.addTab("Kala IV", panelKala4);
        
        // MASALAH KALA IV Initialization
        panelMasalahKala4.setLayout(new java.awt.BorderLayout());
        widget.ScrollPane scrollFormMasalahKala4 = new widget.ScrollPane();
        widget.PanelBiasa formMasalahKala4 = new widget.PanelBiasa();
        formMasalahKala4.setPreferredSize(new java.awt.Dimension(800, 200));
        formMasalahKala4.setLayout(null);
        
        lMasalahKala4 = new widget.Label();
        lMasalahKala4.setText("Masalah Kala IV :");
        formMasalahKala4.add(lMasalahKala4);
        lMasalahKala4.setBounds(0, 10, 120, 23);
        TMasalahKala4 = new widget.TextBox();
        formMasalahKala4.add(TMasalahKala4);
        TMasalahKala4.setBounds(124, 10, 400, 23);
        
        lMasalahKala4Tindakan = new widget.Label();
        lMasalahKala4Tindakan.setText("Tindakan :");
        formMasalahKala4.add(lMasalahKala4Tindakan);
        lMasalahKala4Tindakan.setBounds(0, 40, 120, 23);
        TMasalahKala4Tindakan = new widget.TextBox();
        formMasalahKala4.add(TMasalahKala4Tindakan);
        TMasalahKala4Tindakan.setBounds(124, 40, 400, 23);
        
        lMasalahKala4Hasil = new widget.Label();
        lMasalahKala4Hasil.setText("Hasil :");
        formMasalahKala4.add(lMasalahKala4Hasil);
        lMasalahKala4Hasil.setBounds(0, 70, 120, 23);
        TMasalahKala4Hasil = new widget.TextBox();
        formMasalahKala4.add(TMasalahKala4Hasil);
        TMasalahKala4Hasil.setBounds(124, 70, 400, 23);
        
        scrollFormMasalahKala4.setViewportView(formMasalahKala4);
        JSplitPane splitMasalahKala4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitMasalahKala4.setTopComponent(scrollFormMasalahKala4);
        splitMasalahKala4.setBottomComponent(this.scrollMasalahKala4);
        splitMasalahKala4.setDividerLocation(120);
        splitMasalahKala4.setResizeWeight(0.5);
        panelMasalahKala4.add(splitMasalahKala4, java.awt.BorderLayout.CENTER);
        TabHalaman2.addTab("Masalah Kala IV", panelMasalahKala4);

        // Setup panelTab2 (Observasi)
        panelTab2.setLayout(new java.awt.BorderLayout(1, 1));
        panelTab2.removeAll();
        panelTab2.add(scrollTab2, java.awt.BorderLayout.NORTH);
        
        ScrollObservasi = new widget.ScrollPane();
        ScrollObservasi.setName("ScrollObservasi");
        tbObservasi = new widget.Table();
        tbObservasi.setName("tbObservasi");
        tbObservasi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObservasiMouseClicked(evt);
            }
        });
        ScrollObservasi.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)), "Data Observasi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        ScrollObservasi.setViewportView(tbObservasi);
        panelTab2.add(ScrollObservasi, java.awt.BorderLayout.CENTER);

        TabPartograf.addTab("Catatan Persalinan", TabHalaman2);

        panelTengah.add(TabPartograf, java.awt.BorderLayout.CENTER);
        internalFrame1.add(panelTengah, java.awt.BorderLayout.CENTER);

        // ===== Layout Utama =====
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(internalFrame1, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(internalFrame1, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
        );

        pack();
    }

    // ===== Event Handlers =====

    private void BtnCariPetugasActionPerformed(java.awt.event.ActionEvent evt) {
        DlgCariPetugas petugas = new DlgCariPetugas(null, false);
        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    TKodePetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    TNamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(600, 400);
        petugas.setLocationRelativeTo(this);
        petugas.setVisible(true);
    }

    private long selisihMenit(String tgl1, String tgl2) {
        try {
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = df.parse(tgl1);
            Date d2 = df.parse(tgl2);
            return (d2.getTime() - d1.getTime()) / (1000 * 60);
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean cekInterval() {
        if (TNoRw.getText().equals("")) return false;
        String tgl_jam_baru = Valid.SetTgl(TTanggal.getSelectedItem().toString()) + " " + TTanggal.getSelectedItem().toString().substring(11, 19);

        // Cari tgl_jam terbaru dari tabel UI (tabModeObs), lewati baris yang sedang dipilih (mode edit)
        int selectedRow = tbObservasi.getSelectedRow();
        String tgl_jam_lama = null;
        for (int i = 0; i < tabModeObs.getRowCount(); i++) {
            if (i == selectedRow) continue; // skip baris yang sedang diedit
            Object val = tabModeObs.getValueAt(i, 1); // kolom tgl_jam
            if (val != null && !val.toString().trim().isEmpty()) {
                tgl_jam_lama = val.toString().trim();
                break; // tabModeObs urutan DESC, baris pertama yang bukan selectedRow adalah terbaru
            }
        }

        if (tgl_jam_lama != null) {
            long minutes = selisihMenit(tgl_jam_lama, tgl_jam_baru);
            if (minutes != -1) {
                if (minutes < 30) {
                    JOptionPane.showMessageDialog(null, "Eitss, interval observasi minimal 30 menit! (Baru saja " + minutes + " menit yang lalu)");
                    return false;
                } else if (minutes > 240) {
                    JOptionPane.showMessageDialog(null, "Waduh, interval observasi sudah lebih dari 4 jam! (Sudah " + minutes + " menit berlalu)");
                    return false;
                }
            }
        }
        return true;
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (TKodePetugas.getText().trim().equals("")) {
            Valid.textKosong(TKodePetugas, "Petugas");
        } else {
            if (TabPartograf.getSelectedIndex() == 0) {
                if (TG.getText().trim().equals("")) {
                    Valid.textKosong(TG, "G");
                } else {
                    try {
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf (no_rawat, tgl_jam, g, p, a, ketuban_pecah, mules_sejak, nip, status, created_by, created_at) " +
                            "VALUES (?,?,?,?,?,?,?,?,'aktif',?,NOW())");
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, Valid.SetTgl(TTanggal.getSelectedItem().toString()) + " " + TTanggal.getSelectedItem().toString().substring(11, 19));
                        ps.setString(3, TG.getText());
                        ps.setString(4, TP.getText());
                        ps.setString(5, TA.getText());
                        ps.setString(6, TKetubanPecah.getText());
                        ps.setString(7, Valid.SetTgl(TMulesSejak.getSelectedItem().toString()) + " " + TMulesSejak.getSelectedItem().toString().substring(11, 19));
                        ps.setString(8, TKodePetugas.getText());
                        ps.setString(9, akses.getkode());
                        ps.executeUpdate();
                        tampil();
                        emptTeks();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan: " + ex.getMessage());
                    }
                }
             } else if (TabPartograf.getSelectedIndex() == 1) {
                // Validasi WHO: fase aktif persalinan dimulai saat pembukaan >= 4 cm
                int pembukaanVal = 0;
                try { pembukaanVal = Integer.parseInt(TPembukaan.getText().trim()); } catch (NumberFormatException nfe) {}
                if (pembukaanVal < 4) {
                    JOptionPane.showMessageDialog(null,
                        "<html><b>Pembukaan serviks harus ≥ 4 cm</b><br>" +
                        "Pencatatan observasi partograf hanya dapat dilakukan<br>" +
                        "pada fase aktif persalinan (pembukaan ≥ 4 cm).<br>" +
                        "<small>Sumber: WHO Partograph Guidelines</small></html>",
                        "Validasi Partograf", JOptionPane.WARNING_MESSAGE);
                    TPembukaan.requestFocus();
                    return;
                }
                if (cekInterval()) {
                    pengingatIntervalParameter();   // pengingat lunak interval WHO (tidak memblokir)
                    try {
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_observasi (no_rawat, tgl_jam, djj, air_ketuban, penyusupan, pembukaan, penurunan, kontraksi_jml, kontraksi_durasi, oksitosin_unit, oksitosin_tetes, obat_cairan, sistole, diastole, nadi, suhu, urine_vol, urine_prot, urine_aseton, nip, status, created_by, created_at) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'aktif',?,NOW())");
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, Valid.SetTgl(TTanggal.getSelectedItem().toString()) + " " + TTanggal.getSelectedItem().toString().substring(11, 19));
                        ps.setString(3, TDJJ.getText());
                        ps.setString(4, CAirKetuban.getSelectedItem().toString().substring(0, 1));
                        ps.setString(5, CPenyusupan.getSelectedItem().toString());
                        ps.setString(6, TPembukaan.getText());
                        ps.setString(7, TPenurunan.getText());
                        ps.setString(8, TKontraksiJml.getText());
                        ps.setString(9, CKontraksiDurasi.getSelectedItem().toString());
                        ps.setString(10, TOksitosinUnit.getText());
                        ps.setString(11, TOksitosinTetes.getText());
                        ps.setString(12, TObatCairan.getText());
                        ps.setString(13, TSistole.getText());
                        ps.setString(14, TDiastole.getText());
                        ps.setString(15, TNadi.getText());
                        ps.setString(16, TSuhu.getText());
                        ps.setString(17, TUrineVol.getText());
                        ps.setString(18, TUrineProt.getText());
                        ps.setString(19, TUrineAseton.getText());
                        ps.setString(20, TKodePetugas.getText());
                        ps.setString(21, akses.getkode());
                        ps.executeUpdate();
                        tampil_observasi();   // memanggil deteksiLewatGarisWaspada() -> set lewatGarisStatus
                        emptTeks();
                        if (lewatGarisStatus == 2) {
                            JOptionPane.showMessageDialog(null,
                                "<html><b>PERHATIAN: Partograf melewati garis BERTINDAK!</b><br>"
                                + "Kemajuan persalinan sangat lambat. Lakukan penilaian menyeluruh<br>"
                                + "dan ambil keputusan tindakan segera.<br>"
                                + "<small>Checklist Kala I “melewati garis waspada” otomatis = Ya.</small></html>",
                                "Partograf - Garis Bertindak", JOptionPane.WARNING_MESSAGE);
                        } else if (lewatGarisStatus == 1) {
                            JOptionPane.showMessageDialog(null,
                                "<html><b>Perhatian: Partograf melewati garis WASPADA.</b><br>"
                                + "Pantau lebih ketat dan pertimbangkan rujukan bila progres tetap lambat.<br>"
                                + "<small>Checklist Kala I “melewati garis waspada” otomatis = Ya.</small></html>",
                                "Partograf - Garis Waspada", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan observasi: " + ex.getMessage());
                    }
                }
            } else if (TabPartograf.getSelectedIndex() == 2) {
                // Implementation for Halaman 2 (Catatan Persalinan) saving logic
                // Check which sub-tab is selected in TabHalaman2
                int selectedSubTab = TabHalaman2.getSelectedIndex();
                
                if (selectedSubTab == 0) {
                    // Catatan Persalinan sub-tab
                    try {
                        if (TTglPersalinan.getSelectedItem() == null || TTglPersalinan.getSelectedItem().toString().trim().equals("")) {
                            Valid.textKosong(TTglPersalinan, "Tanggal Persalinan");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        String rujukan = "";
                        if (CKala1.isSelected()) rujukan += "I ";
                        if (CKala2.isSelected()) rujukan += "II ";
                        if (CKala3.isSelected()) rujukan += "III ";
                        if (CKala4.isSelected()) rujukan += "IV ";
                        if (rujukan.trim().equals("")) rujukan = "Tidak";
                        
                        String pendamping = "";
                        if (CPendampingBidan.isSelected()) pendamping += "Bidan ";
                        if (CPendampingSuami.isSelected()) pendamping += "Suami ";
                        if (CPendampingKeluarga.isSelected()) pendamping += "Keluarga ";
                        if (CPendampingDukun.isSelected()) pendamping += "Dukun ";
                        if (CPendampingTeman.isSelected()) pendamping += "Teman ";
                        if (CPendampingTidakAda.isSelected()) pendamping = "Tidak Ada";

                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_halaman2 (no_rawat, cp_merujuk_kala, cp_alasan_merujuk, cp_tempat_rujukan, cp_pendamping, created_by, created_at, status) " +
                            "VALUES (?,?,?,?,?, ?,NOW(),'aktif')");
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, rujukan.trim());
                        ps.setString(3, TAlasanMerujuk.getText());
                        ps.setString(4, TTempatRujukan.getText());
                        ps.setString(5, pendamping.trim());
                        ps.setString(6, TKodePetugas.getText());
                        ps.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampil_halaman2();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data Catatan Persalinan berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan Catatan Persalinan: " + ex.getMessage());
                    }
                } else if (selectedSubTab == 1) {
                    // Kala I sub-tab
                    try {
                        if (TTglPersalinan.getSelectedItem() == null || TTglPersalinan.getSelectedItem().toString().trim().equals("")) {
                            Valid.textKosong(TTglPersalinan, "Tanggal Persalinan");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_halaman2 (no_rawat, k1_garis_waspada, k1_masalah, k1_penatalaksanaan, k1_hasil, created_by, created_at, status) " +
                            "VALUES (?, ?,?,?,?, ?,NOW(),'aktif')");
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, CKala1Waspada.getSelectedItem().toString());
                        ps.setString(3, TKala1Masalah.getText());
                        ps.setString(4, TKala1Tindakan.getText());
                        ps.setString(5, TKala1Hasil.getText());
                        ps.setString(6, TKodePetugas.getText());
                        ps.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampil_halaman2();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data Kala I berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan Kala I: " + ex.getMessage());
                    }
                } else if (selectedSubTab == 2) {
                    // Kala II sub-tab
                    try {
                        if (TTglPersalinan.getSelectedItem() == null || TTglPersalinan.getSelectedItem().toString().trim().equals("")) {
                            Valid.textKosong(TTglPersalinan, "Tanggal Persalinan");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_halaman2 (no_rawat, k2_episiotomi, k2_indikasi_episiotomi, k2_gawat_janin, k2_tindakan_gawat_janin, k2_distosia_bahu, k2_tindakan_distosia_bahu, k2_masalah, k2_penatalaksanaan, k2_hasil, created_by, created_at, status) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW(),'aktif')");
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, CKala2Episiotomi.getSelectedItem().toString());
                        ps.setString(3, TKala2EpisiotomiAlasan.getText());
                        ps.setString(4, CKala2GawatJanin.getSelectedItem().toString());
                        ps.setString(5, TKala2GawatJaninTindakan.getText());
                        ps.setString(6, CKala2Distosia.getSelectedItem().toString());
                        ps.setString(7, TKala2DistosiaTindakan.getText());
                        ps.setString(8, TKala2Masalah.getText());
                        ps.setString(9, TKala2Tindakan.getText());
                        ps.setString(10, TKala2Hasil.getText());
                        ps.setString(11, TKodePetugas.getText());
                        ps.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampil_halaman2();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data Kala II berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan Kala II: " + ex.getMessage());
                    }
                } else if (selectedSubTab == 3) {
                    // Kala III sub-tab
                    try {
                        if (TTglPersalinan.getSelectedItem() == null || TTglPersalinan.getSelectedItem().toString().trim().equals("")) {
                            Valid.textKosong(TTglPersalinan, "Tanggal Persalinan");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_halaman2 (no_rawat, k3_lama, k3_oksitosin, k3_alasan_oksitosin, k3_tali_pusat, k3_alasan_tali_pusat, k3_masase_fundus, k3_alasan_masase_fundus, k3_plasenta_lengkap, k3_tindakan_plasenta_tidak_lengkap, created_by, created_at, status) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW(),'aktif')");
                        ps.setString(1, TNoRw.getText());
                        ps.setInt(2, TKala3Lama.getText().trim().equals("") ? 0 : Integer.parseInt(TKala3Lama.getText()));
                        ps.setString(3, CKala3Oksitosin.getSelectedItem().toString());
                        ps.setString(4, TKala3OksitosinAlasan.getText());
                        ps.setString(5, CKala3TaliPusat.getSelectedItem().toString());
                        ps.setString(6, TKala3TaliPusatAlasan.getText());
                        ps.setString(7, CKala3Masase.getSelectedItem().toString());
                        ps.setString(8, TKala3MasaseAlasan.getText());
                        ps.setString(9, CKala3PlasentaLengkap.getSelectedItem().toString());
                        ps.setString(10, TKala3PlasentaTindakan.getText());
                        ps.setString(11, TKodePetugas.getText());
                        ps.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampil_halaman2();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data Kala III berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan Kala III: " + ex.getMessage());
                    }
                } else if (selectedSubTab == 4) {
                    // BBL sub-tab
                    try {
                        if (TTglPersalinan.getSelectedItem() == null || TTglPersalinan.getSelectedItem().toString().trim().equals("")) {
                            Valid.textKosong(TTglPersalinan, "Tanggal Persalinan");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_halaman2 (no_rawat, bbl_bb, bbl_pb, bbl_jk, bbl_penilaian, bbl_lahir, bbl_tindakan, bbl_masalah, bbl_hasil, bbl_asi, bbl_waktu_asi, created_by, created_at, status) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'aktif')");
                        ps.setString(1, TNoRw.getText());
                        ps.setInt(2, TBBLBB.getText().trim().equals("") ? 0 : Integer.parseInt(TBBLBB.getText()));
                        ps.setInt(3, TBBLPB.getText().trim().equals("") ? 0 : Integer.parseInt(TBBLPB.getText()));
                        ps.setString(4, CBBLJK.getSelectedItem().toString());
                        ps.setString(5, CBBLPenilaian.getSelectedItem().toString());
                        ps.setString(6, CBBLBayiLahir.getSelectedItem().toString());
                        ps.setString(7, TBBLNormalTindakan.getText());
                        ps.setString(8, TBBLMasalah.getText());
                        ps.setString(9, TBBLHasil.getText());
                        ps.setString(10, CBBLASI.getSelectedItem().toString());
                        ps.setString(11, TBBLASITglJam.getText());
                        ps.setString(12, TKodePetugas.getText());
                        ps.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampil_halaman2();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data BBL berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan BBL: " + ex.getMessage());
                    }
                } else if (selectedSubTab == 5) {
                    // Kala IV sub-tab
                    try {
                        if (TKala4Jam.getText().trim().equals("")) {
                            Valid.textKosong(TKala4Jam, "Jam Ke");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        
                        ps2 = koneksi.prepareStatement("INSERT INTO partograf_halaman2_kala4 (no_rawat, jam_ke, waktu, td, nadi, suhu, tfu, kontraksi, kandung_kemih, pendarahan, created_by, created_at, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW(),'aktif')");
                        ps2.setString(1, TNoRw.getText());
                        ps2.setString(2, TKala4Jam.getText());
                        ps2.setString(3, TKala4Waktu.getText());
                        ps2.setString(4, TKala4TD.getText());
                        ps2.setString(5, TKala4Nadi.getText());
                        ps2.setString(6, TKala4Suhu.getText());
                        ps2.setString(7, TKala4TFU.getText());
                        ps2.setString(8, TKala4Kontraksi.getText());
                        ps2.setString(9, TKala4Kandung.getText());
                        ps2.setString(10, TKala4Pendarahan.getText());
                        ps2.setString(11, TKodePetugas.getText());
                        ps2.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampilKala4();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data Kala IV berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan Kala IV: " + ex.getMessage());
                    }
                } else if (selectedSubTab == 6) {
                    // Masalah Kala IV sub-tab
                    try {
                        if (TTglPersalinan.getSelectedItem() == null || TTglPersalinan.getSelectedItem().toString().trim().equals("")) {
                            Valid.textKosong(TTglPersalinan, "Tanggal Persalinan");
                            return;
                        }
                        koneksi.setAutoCommit(false);
                        
                        ps = koneksi.prepareStatement(
                            "INSERT INTO partograf_halaman2 (no_rawat, k4_masalah, k4_penatalaksanaan, k4_hasil, created_by, created_at, status) " +
                            "VALUES (?,?,?,?,?,NOW(),'aktif')");
                        ps.setString(1, TNoRw.getText());
                        ps.setString(2, TMasalahKala4.getText());
                        ps.setString(3, TMasalahKala4Tindakan.getText());
                        ps.setString(4, TMasalahKala4Hasil.getText());
                        ps.setString(5, TKodePetugas.getText());
                        ps.executeUpdate();

                        koneksi.commit();
                        koneksi.setAutoCommit(true);
                        tampil_halaman2();
                        emptTeks();
                        JOptionPane.showMessageDialog(null, "Data Masalah Kala IV berhasil disimpan");
                    } catch (Exception ex) {
                        try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                        JOptionPane.showMessageDialog(null, "Gagal menyimpan Masalah Kala IV: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            cetakCatatanPersalinan();
        }
    }

    private void cetakHTMLObservasi() {
        try {
            String nmPasien = TNamaPasien.getText();
            String noRw = TNoRw.getText();
            
            // Ambil data observasi
            StringBuilder tableRows = new StringBuilder();
            try {
                ps = koneksi.prepareStatement(
                    "SELECT DATE_FORMAT(tgl_jam,'%d/%m/%Y %H:%i'), djj, air_ketuban, penyusupan, pembukaan, penurunan, " +
                    "kontraksi_jml, kontraksi_durasi, oksitosin_unit, oksitosin_tetes, obat_cairan, " +
                    "sistole, diastole, nadi, suhu, urine_vol, urine_prot, urine_aseton " +
                    "FROM partograf_observasi WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam");
                ps.setString(1, noRw);
                rs = ps.executeQuery();
                while (rs.next()) {
                    tableRows.append("<tr style='font-size:11px'>")
                        .append("<td>").append(n(rs,1)).append("</td>")
                        .append("<td>").append(n(rs,2)).append("</td>")
                        .append("<td>").append(n(rs,3)).append("</td>")
                        .append("<td>").append(n(rs,4)).append("</td>")
                        .append("<td>").append(n(rs,5)).append("</td>")
                        .append("<td>").append(n(rs,6)).append("</td>")
                        .append("<td>").append(n(rs,7)).append("x/").append(n(rs,8)).append("</td>")
                        .append("<td>").append(n(rs,9)).append("</td>")
                        .append("<td>").append(n(rs,10)).append("</td>")
                        .append("<td>").append(n(rs,11)).append("</td>")
                        .append("<td>").append(n(rs,12)).append("/").append(n(rs,13)).append("</td>")
                        .append("<td>").append(n(rs,14)).append("</td>")
                        .append("<td>").append(n(rs,15)).append("</td>")
                        .append("<td>").append(n(rs,16)).append("</td>")
                        .append("<td>").append(n(rs,17)).append("</td>")
                        .append("<td>").append(n(rs,18)).append("</td>")
                        .append("</tr>");
                }
            } finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }

            String html = "<html><head><style>" +
                "table{border-collapse:collapse;width:100%} th,td{border:1px solid #333;padding:4px;text-align:center} th{background:#eee}" +
                "</style></head><body>" +
                "<h2 style='text-align:center'>OBSERVASI PARTOGRAF</h2>" +
                "<p>No. Rawat: " + noRw + " | Nama: " + nmPasien + "</p>" +
                "<table><thead><tr>" +
                "<th>Waktu</th><th>DJJ</th><th>Air</th><th>Penyusapan</th><th>Pembukaan</th><th>Penurunan</th>" +
                "<th>Kontraksi</th><th>Oksit Uni</th><th>Oksit Tet</th><th>Obat/Cairan</th>" +
                "<th>TD</th><th>Nadi</th><th>Suhu</th><th>Urin Vol</th><th>Prot</th><th>Aset</th>" +
                "</tr></thead><tbody>" + tableRows + "</tbody></table>" +
                "</body></html>";
            openHtmlInBrowser(html, "partograf_observasi");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal cetak HTML: " + e.getMessage());
        }
    }

    private void BtnGrafikActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            if (dlgGrafik == null) {
                dlgGrafik = new DlgPartografGrafik(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), false);
            }

            dlgGrafik.setSize(this.getWidth(), this.getHeight());
            dlgGrafik.setLocationRelativeTo(this);
            dlgGrafik.setData(TNoRw.getText());
            dlgGrafik.setVisible(true);
        }
    }

    private void cetakJasperCatatanPersalinan() {
        Map<String, Object> param = new HashMap<>();
        param.put("REPORT_CONNECTION", koneksi);
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select logo from setting"));
        param.put("no_rawat", TNoRw.getText());
        
        // Parameter finger untuk QR TTE
        String tte = "Dikeluarkan di " + akses.getnamars() + "\n" +
                     "Ditandatangani secara elektronik oleh\n" +
                     TNamaPetugas.getText() + "\n" +
                     "NIP: " + TKodePetugas.getText() + "\n" +
                     "Tanggal: " + new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
        param.put("finger", tte);
        param.put("finger2", tte);
        
        Valid.MyReportqry("rptCetakCatatanPersalinan.jrxml", "report", "::[ Catatan Persalinan ]::",
                "SELECT rp.no_rawat, p.no_rkm_medis, p.nm_pasien, IF(p.jk='L','Laki-Laki','Perempuan') AS jk, p.tgl_lahir, " +
                "h.cp_merujuk_kala, h.cp_alasan_merujuk, h.cp_tempat_rujukan, h.cp_pendamping, " +
                "h.k1_garis_waspada, h.k1_masalah, h.k1_penatalaksanaan, h.k1_hasil, " +
                "h.k2_episiotomi, h.k2_indikasi_episiotomi, h.k2_gawat_janin, h.k2_tindakan_gawat_janin, h.k2_distosia_bahu, h.k2_tindakan_distosia_bahu, h.k2_masalah, h.k2_penatalaksanaan, h.k2_hasil, " +
                "h.k3_lama, h.k3_oksitosin, h.k3_alasan_oksitosin, h.k3_tali_pusat, h.k3_alasan_tali_pusat, h.k3_masase_fundus, h.k3_alasan_masase_fundus, h.k3_plasenta_lengkap, h.k3_tindakan_plasenta_tidak_lengkap, " +
                "h.bbl_bb, h.bbl_pb, h.bbl_jk, h.bbl_penilaian, h.bbl_lahir, h.bbl_tindakan, h.bbl_masalah, h.bbl_hasil, h.bbl_asi, h.bbl_waktu_asi, " +
                "h.k4_masalah, h.k4_penatalaksanaan, h.k4_hasil, pt.nama AS nm_petugas " +
                "FROM partograf_halaman2 h INNER JOIN reg_periksa rp ON h.no_rawat = rp.no_rawat " +
                "INNER JOIN pasien p ON rp.no_rkm_medis = p.no_rkm_medis " +
                "INNER JOIN petugas pt ON h.created_by = pt.nip " +
                "WHERE h.no_rawat = '" + TNoRw.getText() + "' AND h.status='aktif' ORDER BY h.created_at DESC LIMIT 1", param);
    }

    private void cetakJasperObservasi() {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select logo from setting"));
        param.put("no_rawat", TNoRw.getText());
        param.put("REPORT_CONNECTION", koneksi);
        
        // Add Partograf Graph Snapshot
        if (dlgGrafik == null) {
            dlgGrafik = new DlgPartografGrafik((java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), false);
        }
        dlgGrafik.setData(TNoRw.getText());
        param.put("contoh_grafik", dlgGrafik.getSnapshot());
        
        String tte = "Dikeluarkan di " + akses.getnamars() + "\n" +
                     "Ditandatangani secara elektronik oleh\n" +
                     TNamaPetugas.getText() + "\n" +
                     "NIP: " + TKodePetugas.getText() + "\n" +
                     "Tanggal: " + new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
        param.put("finger", tte);
        param.put("finger2", tte);
        
        Valid.MyReportqry("rptPartografObservasi.jasper", "report", "::[ Laporan Observasi Partograf ]::",
                "SELECT p.no_rkm_medis, p.nm_pasien, TIMESTAMPDIFF(YEAR, p.tgl_lahir, CURDATE()) AS umur, " +
                "pt.g, pt.p, pt.a, DATE_FORMAT(pt.mules_sejak,'%d/%m/%Y %H:%i') AS mules_sejak, pt.ketuban_pecah, " +
                "DATE_FORMAT(po.tgl_jam,'%d/%m %H:%i') AS waktu, IFNULL(po.djj,'-') AS djj, IFNULL(po.air_ketuban,'-') AS air_ketuban, " +
                "IFNULL(po.penyusupan,'-') AS penyusupan, IFNULL(po.pembukaan,'-') AS pembukaan, IFNULL(po.penurunan,'-') AS penurunan, " +
                "CONCAT(IFNULL(po.kontraksi_jml,'-'),'x/',IFNULL(po.kontraksi_durasi,'-'),'dt') AS kontraksi, " +
                "IFNULL(po.oksitosin_unit,'-') AS oksitosin_ul, IFNULL(po.oksitosin_tetes,'-') AS oksitosin_tpm, " +
                "IFNULL(po.obat_cairan,'-') AS obat_cairan, CONCAT(IFNULL(po.sistole,'-'),'/',IFNULL(po.diastole,'-')) AS td, " +
                "IFNULL(po.nadi,'-') AS nadi, IFNULL(po.suhu,'-') AS suhu, IFNULL(po.urine_vol,'-') AS urine_vol, " +
                "IFNULL(po.urine_prot,'-') AS urine_prot, IFNULL(po.urine_aseton,'-') AS urine_aseton, rp.no_rawat " +
                "FROM partograf_observasi po INNER JOIN partograf pt ON po.no_rawat = pt.no_rawat AND pt.status='aktif' " +
                "INNER JOIN reg_periksa rp ON po.no_rawat = rp.no_rawat " +
                "INNER JOIN pasien p ON rp.no_rkm_medis = p.no_rkm_medis " +
                "WHERE po.no_rawat = '" + TNoRw.getText() + "' AND po.status = 'aktif' ORDER BY po.tgl_jam", param);
    }

    private void openHtmlInBrowser(String html, String filename) {
        try {
            java.io.File tmpFile = java.io.File.createTempFile(filename, ".html");
            tmpFile.deleteOnExit();
            java.io.FileWriter fw = new java.io.FileWriter(tmpFile);
            fw.write(html);
            fw.close();
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(tmpFile.toURI());
            } else {
                String[] browsers = {"x-www-browser", "firefox", "chromium", "google-chrome", "epiphany"};
                for (String b : browsers) {
                    try { Runtime.getRuntime().exec(new String[]{b, tmpFile.getAbsolutePath()}); break; } catch (Exception ignore) {}
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal membuka browser: " + ex.getMessage());
        }
    }

    
    private void cetakGrafikPartograf() {
        try {
            // Ambil data pasien
            String nmPasien = TNamaPasien.getText();
            String noRw = TNoRw.getText();
            String umur = TUmur.getText();

            // Ambil data partograf (G,P,A,mules_sejak)
            String tglPartograf = "", g = "", p = "", a = "", mulesSejak = "", ketubanPecah = "";
            try {
                ps = koneksi.prepareStatement("SELECT DATE_FORMAT(tgl_jam,'%d/%m/%Y %H:%i') , g,p,a, DATE_FORMAT(mules_sejak,'%d/%m/%Y %H:%i'), ketuban_pecah FROM partograf WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam DESC LIMIT 1");
                ps.setString(1, noRw);
                rs = ps.executeQuery();
                if (rs.next()) { tglPartograf=n(rs,1); g=n(rs,2); p=n(rs,3); a=n(rs,4); mulesSejak=n(rs,5); ketubanPecah=n(rs,6); }
            } finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }

            // Ambil data observasi
            java.util.List<String[]> obsRows = new java.util.ArrayList<>();
            try {
                ps2 = koneksi.prepareStatement(
                    "SELECT DATE_FORMAT(tgl_jam,'%H:%i'), djj, air_ketuban, penyusupan, pembukaan, penurunan, " +
                    "kontraksi_jml, kontraksi_durasi, oksitosin_unit, oksitosin_tetes, obat_cairan, " +
                    "sistole, diastole, nadi, suhu, urine_vol, urine_prot, urine_aseton, " +
                    "TIMESTAMPDIFF(MINUTE, MIN(tgl_jam) OVER(), tgl_jam)/60.0 AS jam_ke " +
                    "FROM partograf_observasi WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam");
                ps2.setString(1, noRw);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    obsRows.add(new String[]{n(rs2,1),n(rs2,2),n(rs2,3),n(rs2,4),n(rs2,5),n(rs2,6),
                        n(rs2,7),n(rs2,8),n(rs2,9),n(rs2,10),n(rs2,11),n(rs2,12),n(rs2,13),n(rs2,14),n(rs2,15),n(rs2,16),n(rs2,17),n(rs2,18),n(rs2,19)});
                }
            } finally { try{if(rs2!=null)rs2.close();if(ps2!=null)ps2.close();}catch(Exception e){} }

            // Build data arrays for JavaScript
            StringBuilder jsWaktu=new StringBuilder(), jsDJJ=new StringBuilder(), jsPembukaan=new StringBuilder();
            StringBuilder jsPenurunan=new StringBuilder(), jsKontraksi=new StringBuilder(), jsSistole=new StringBuilder();
            StringBuilder jsDiastole=new StringBuilder(), jsNadi=new StringBuilder(), jsSuhu=new StringBuilder();
            StringBuilder jsJamKe=new StringBuilder();
            // Table row data
            StringBuilder tableRows = new StringBuilder();
            boolean firstObs = true;
            for (String[] r : obsRows) {
                String sep = firstObs?"":",";
                jsWaktu.append(sep).append("'").append(r[0]).append("'");
                String djjV=r[1].isEmpty()?"null":r[1]; String pemV=r[4].isEmpty()?"null":r[4];
                String penV=r[5].isEmpty()?"null":r[5]; String konV=r[6].isEmpty()?"null":r[6];
                String sisV=r[11].isEmpty()?"null":r[11]; String diaV=r[12].isEmpty()?"null":r[12];
                String nadiV=r[13].isEmpty()?"null":r[13]; String suhuV=r[14].isEmpty()?"null":r[14];
                String jamV=r[18].isEmpty()?"0":r[18];
                jsDJJ.append(sep).append(djjV); jsPembukaan.append(sep).append(pemV);
                jsPenurunan.append(sep).append(penV); jsKontraksi.append(sep).append(konV);
                jsSistole.append(sep).append(sisV); jsDiastole.append(sep).append(diaV);
                jsNadi.append(sep).append(nadiV); jsSuhu.append(sep).append(suhuV);
                jsJamKe.append(sep).append(jamV);
                tableRows.append("<tr style='font-size:10px'>")
                    .append("<td>").append(r[0]).append("</td>")
                    .append("<td>").append(r[1]).append("</td>")
                    .append("<td>").append(r[2]).append("</td>")
                    .append("<td>").append(r[3]).append("</td>")
                    .append("<td>").append(r[4]).append("</td>")
                    .append("<td>").append(r[5]).append("</td>")
                    .append("<td>").append(r[6]).append("x/10m</td>")
                    .append("<td>").append(r[7]).append("</td>")
                    .append("<td>").append(r[8]).append("</td>")
                    .append("<td>").append(r[9]).append("</td>")
                    .append("<td>").append(r[10]).append("</td>")
                    .append("<td>").append(r[11]).append("/").append(r[12]).append("</td>")
                    .append("<td>").append(r[13]).append("</td>")
                    .append("<td>").append(r[14]).append("</td>")
                    .append("<td>").append(r[15]).append("</td>")
                    .append("<td>").append(r[16]).append("</td>")
                    .append("<td>").append(r[17]).append("</td>")
                    .append("</tr>");
                firstObs = false;
            }

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<title>PARTOGRAF - " + noRw + "</title>" +
                "<script src='https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js'></script>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;font-size:11px;margin:10px;background:#fff;}" +
                ".header-box{border:2px solid #000;padding:6px;margin-bottom:4px;}" +
                ".header-title{font-size:18px;font-weight:bold;text-align:center;margin-bottom:4px;}" +
                ".row-info{display:flex;gap:20px;margin:2px 0;}" +
                ".row-info span{white-space:nowrap;}" +
                ".chart-section{margin:4px 0;}" +
                ".section-label{font-size:9px;font-weight:bold;}" +
                "canvas{border:1px solid #999;display:block;}" +
                "table.data-table{border-collapse:collapse;width:100%;margin-top:8px;font-size:10px;}" +
                "table.data-table th,table.data-table td{border:1px solid #333;padding:2px 3px;text-align:center;}" +
                "table.data-table th{background:#e0e0e0;}" +
                "@media print{body{margin:0;}button{display:none;}}" +
                "</style>" +
                "</head><body>" +
                "<button onclick='window.print()' style='position:fixed;top:8px;right:8px;padding:6px 16px;font-size:13px;'>&#128438; Cetak</button>" +

                "<div class='header-box'>" +
                "<div class='header-title'>PARTOGRAF</div>" +
                "<div style='display:flex;gap:30px'>" +
                "<div>" +
                "<div>No. Register &nbsp;: <b>" + noRw + "</b></div>" +
                "<div>Nama Ibu &nbsp;&nbsp;&nbsp;&nbsp;: <b>" + nmPasien + "</b></div>" +
                "<div>Tanggal &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: " + tglPartograf + "</div>" +
                "</div>" +
                "<div>" +
                "<div>Umur : <b>" + umur + "</b></div>" +
                "<div>G: <b>" + g + "</b> &nbsp; P: <b>" + p + "</b> &nbsp; A: <b>" + a + "</b></div>" +
                "<div>Ketuban Pecah: " + ketubanPecah + "</div>" +
                "<div>Mulas Sejak: " + mulesSejak + "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +

                // DJJ Chart (80-200)
                "<div class='chart-section'>" +
                "<div style='display:flex;align-items:center;gap:4px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:9px;font-weight:bold;text-align:center'>Denyut Jantung Janin (/menit)</div>" +
                "<canvas id='chartDJJ' height='120'></canvas>" +
                "</div></div>" +

                // Air Ketuban & Penyusupan
                "<canvas id='chartAirKetuban' height='30'></canvas>" +

                // Pembukaan + garis waspada/bertindak
                "<div class='chart-section'>" +
                "<div style='display:flex;align-items:center;gap:4px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:9px;font-weight:bold;text-align:center'>Pembukaan serviks (cm)<br>Penurunan kepala</div>" +
                "<canvas id='chartPembukaan' height='200'></canvas>" +
                "</div></div>" +

                // Kontraksi
                "<div class='chart-section'>" +
                "<div style='display:flex;align-items:center;gap:4px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:9px;font-weight:bold;text-align:center'>Kontraksi /10m</div>" +
                "<canvas id='chartKontraksi' height='70'></canvas>" +
                "</div></div>" +

                // Oksitosin (label baris)
                "<canvas id='chartOksitosin' height='25'></canvas>" +

                // TD/Nadi 60-180
                "<div class='chart-section'>" +
                "<div style='display:flex;align-items:center;gap:4px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:9px;font-weight:bold;text-align:center'>TD/Nadi</div>" +
                "<canvas id='chartTD' height='140'></canvas>" +
                "</div></div>" +

                // Suhu
                "<canvas id='chartSuhu' height='25'></canvas>" +

                // Urin
                "<canvas id='chartUrin' height='25'></canvas>" +

                // Data table
                "<table class='data-table' style='margin-top:12px'><thead><tr>" +
                "<th>Waktu</th><th>DJJ</th><th>Air Ketuban</th><th>Penyusupan</th><th>Pembukaan</th><th>Penurunan</th>" +
                "<th>Kontraksi</th><th>Durasi</th><th>Oksitosin</th><th>Tetes</th><th>Obat/Cairan</th>" +
                "<th>TD</th><th>Nadi</th><th>Suhu</th><th>Urine Vol</th><th>Protein</th><th>Aseton</th>" +
                "</tr></thead><tbody>" + tableRows + "</tbody></table>" +

                "<script>" +
                "const waktuLbl=[" + jsWaktu + "];" +
                "const djjDt=[" + jsDJJ + "];" +
                "const pemDt=[" + jsPembukaan + "];" +
                "const penDt=[" + jsPenurunan + "];" +
                "const konDt=[" + jsKontraksi + "];" +
                "const sisDt=[" + jsSistole + "];" +
                "const diaDt=[" + jsDiastole + "];" +
                "const nadiDt=[" + jsNadi + "];" +
                "const suhuDt=[" + jsSuhu + "];" +
                "const jamKe=[" + jsJamKe + "];" +
                "const gridOpts={color:'rgba(0,0,0,0.2)'};" +
                "const lineStyle={tension:0,pointRadius:5,pointStyle:'circle',fill:false,borderWidth:2};" +
                "let jamWaspada=-1; for(let i=0;i<pemDt.length;i++){ if(pemDt[i]>=4){ jamWaspada=jamKe[i]; break; } }" +
                "const waspadaPts=[]; const bertindakPts=[];" +
                "if(jamWaspada!==-1){ for(let y=4;y<=10;y++){ waspadaPts.push({x:jamWaspada+(y-4),y:y}); bertindakPts.push({x:jamWaspada+4+(y-4),y:y}); } }" +
                "const pemPts=waktuLbl.map((l,i)=>({x:jamKe[i],y:pemDt[i]}));" +
                "const penPts=waktuLbl.map((l,i)=>({x:jamKe[i],y:penDt[i]}));" +

                // DJJ chart
                "new Chart(document.getElementById('chartDJJ').getContext('2d'),{" +
                "  type:'line', data:{labels:waktuLbl, datasets:[{label:'DJJ',data:djjDt,borderColor:'#000',...lineStyle,pointBackgroundColor:'#000'}]}," +
                "  options:{scales:{x:{grid:gridOpts},y:{min:80,max:200,ticks:{stepSize:10},grid:gridOpts}}," +
                "    plugins:{legend:{display:false}},animation:false}" +
                "});" +

                "new Chart(document.getElementById('chartPembukaan').getContext('2d'),{" +
                "  type:'line', data:{datasets:[" +
                "    {label:'WASPADA',data:waspadaPts,borderColor:'rgba(0,0,0,0.5)',borderDash:[5,5],pointRadius:0,fill:false}," +
                "    {label:'BERTINDAK',data:bertindakPts,borderColor:'rgba(255,0,0,0.5)',borderDash:[2,2],pointRadius:0,fill:false}," +
                "    {label:'Pembukaan (X)',data:pemPts,borderColor:'red',pointBackgroundColor:'red',pointStyle:'crossRot',...lineStyle}," +
                "    {label:'Penurunan (O)',data:penPts,borderColor:'blue',pointBackgroundColor:'blue',pointStyle:'circle',...lineStyle}" +
                "  ]}," +
                "  options:{scales:{" +
                "    x:{type:'linear',min:0,max:24,ticks:{stepSize:1},title:{display:true,text:'Waktu (jam)'},grid:gridOpts}," +
                "    y:{min:0,max:10,ticks:{stepSize:1},grid:gridOpts}" +
                "  },plugins:{legend:{position:'top'}},animation:false}" +
                "});" +

                // Kontraksi chart (stacked bars 1-5)
                "new Chart(document.getElementById('chartKontraksi').getContext('2d'),{" +
                "  type:'bar', data:{labels:waktuLbl, datasets:[{label:'Kontraksi/10m',data:konDt,backgroundColor:'rgba(0,0,0,0.5)',borderColor:'#000',borderWidth:1}]}," +
                "  options:{scales:{x:{grid:gridOpts},y:{min:0,max:5,ticks:{stepSize:1},grid:gridOpts}}," +
                "    plugins:{legend:{display:false}},animation:false}" +
                "});" +

                // TD/Nadi chart
                "new Chart(document.getElementById('chartTD').getContext('2d'),{" +
                "  type:'line', data:{labels:waktuLbl, datasets:[" +
                "    {label:'Sistole',data:sisDt,borderColor:'#000',...lineStyle,pointStyle:'rect',pointBackgroundColor:'#000'}," +
                "    {label:'Diastole',data:diaDt,borderColor:'#555',...lineStyle,pointStyle:'rect',pointBackgroundColor:'#555'}," +
                "    {label:'Nadi (o)',data:nadiDt,borderColor:'#000',...lineStyle,pointStyle:'circle',pointBackgroundColor:'#fff',borderDash:[2,2]}" +
                "  ]}," +
                "  options:{scales:{x:{grid:gridOpts},y:{min:60,max:180,ticks:{stepSize:10},grid:gridOpts}}," +
                "    plugins:{legend:{position:'top'}},animation:false}" +
                "});" +

                "</script>" +
                "<div style='margin-top:16px;border-top:1px solid #999;padding-top:8px;display:flex;justify-content:flex-end'>" +
                "<div style='text-align:center'>" +
                "<img src='https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=" +
                java.net.URLEncoder.encode("Dikeluarkan di " + akses.getnamars() + "\nDitandatangani secara elektronik oleh\n" + TNamaPetugas.getText() + "\nNIP: " + TKodePetugas.getText() + "\nTanggal: " + new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date()), "UTF-8") +
                "' width='100' height='100' style='display:block' alt='QR TTE'/>" +
                "<div style='font-size:8px;margin-top:2px'>Tanda Tangan Elektronik</div>" +
                "<div style='font-size:9px;font-weight:bold'>" + TNamaPetugas.getText() + "</div>" +
                "<div style='font-size:8px'>NIP: " + TKodePetugas.getText() + "</div>" +
                "</div></div>" +
                "</body></html>";

            openHtmlInBrowser(html, "partograf_grafik");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menampilkan grafik: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String n(java.sql.ResultSet r, int col) {
        try { String v = r.getString(col); return v == null ? "" : v; } catch (Exception e) { return ""; }
    }

    private void cetakCatatanPersalinan() {
        try {
            String noRw = TNoRw.getText();
            String nmPasien = TNamaPasien.getText();
            String noRM = TNoRM.getText();
            String namars = akses.getnamars();
            String alamatrs = akses.getalamatrs();
            String kotars = akses.getkabupatenrs();
            String kontakrs = akses.getkontakrs();

            // ====== QUERY GRAFIK DATA (Halaman 1) ======
            String tglPartograf="", gpa_g="", gpa_p="", gpa_a="", mulesSejak="", ketubanPecah="", umurPasien=TUmur.getText();
            try {
                ps = koneksi.prepareStatement("SELECT DATE_FORMAT(tgl_jam,'%d/%m/%Y %H:%i'),g,p,a,DATE_FORMAT(mules_sejak,'%d/%m/%Y %H:%i'),ketuban_pecah FROM partograf WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam DESC LIMIT 1");
                ps.setString(1, noRw);
                rs = ps.executeQuery();
                if (rs.next()) { tglPartograf=n(rs,1); gpa_g=n(rs,2); gpa_p=n(rs,3); gpa_a=n(rs,4); mulesSejak=n(rs,5); ketubanPecah=n(rs,6); }
            } finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }

            // Observasi data untuk grafik
            java.util.List<String[]> obsRows = new java.util.ArrayList<>();
            try {
                ps2 = koneksi.prepareStatement(
                    "SELECT DATE_FORMAT(tgl_jam,'%H:%i'),djj,air_ketuban,penyusupan,pembukaan,penurunan," +
                    "kontraksi_jml,kontraksi_durasi,oksitosin_unit,oksitosin_tetes,obat_cairan," +
                    "sistole,diastole,nadi,suhu,urine_vol,urine_prot,urine_aseton," +
                    "TIMESTAMPDIFF(MINUTE,MIN(tgl_jam) OVER(),tgl_jam)/60.0 " +
                    "FROM partograf_observasi WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam");
                ps2.setString(1, noRw);
                rs2 = ps2.executeQuery();
                while(rs2.next()) {
                    obsRows.add(new String[]{n(rs2,1),n(rs2,2),n(rs2,3),n(rs2,4),n(rs2,5),n(rs2,6),n(rs2,7),n(rs2,8),n(rs2,9),n(rs2,10),n(rs2,11),n(rs2,12),n(rs2,13),n(rs2,14),n(rs2,15),n(rs2,16),n(rs2,17),n(rs2,18),n(rs2,19)});
                }
            } finally { try{if(rs2!=null)rs2.close();if(ps2!=null)ps2.close();}catch(Exception e){} }

            // Build JS arrays untuk Chart.js
            StringBuilder jsWaktu=new StringBuilder(),jsDJJ=new StringBuilder(),jsPem=new StringBuilder(),jsPen=new StringBuilder();
            StringBuilder jsKon=new StringBuilder(),jsKonDur=new StringBuilder(),jsSis=new StringBuilder(),jsDia=new StringBuilder(),jsNadi=new StringBuilder(),jsSuhu=new StringBuilder(),jsJam=new StringBuilder();
            StringBuilder obsTableRows = new StringBuilder();
            boolean first=true;
            for (String[] r : obsRows) {
                String sep=first?"":",";
                jsWaktu.append(sep).append("'").append(r[0]).append("'");
                jsDJJ.append(sep).append(r[1].isEmpty()?"null":r[1]);
                jsPem.append(sep).append(r[4].isEmpty()?"null":r[4]);
                jsPen.append(sep).append(r[5].isEmpty()?"null":r[5]);
                jsKon.append(sep).append(r[6].isEmpty()?"null":r[6]);
                jsKonDur.append(sep).append("'").append(r[7].replace("'","")).append("'");
                jsSis.append(sep).append(r[11].isEmpty()?"null":r[11]);
                jsDia.append(sep).append(r[12].isEmpty()?"null":r[12]);
                jsNadi.append(sep).append(r[13].isEmpty()?"null":r[13]);
                jsSuhu.append(sep).append(r[14].isEmpty()?"null":r[14]);
                jsJam.append(sep).append(r[18].isEmpty()?"0":r[18]);
                obsTableRows.append("<tr style='font-size:8px'>")
                    .append("<td>").append(r[0]).append("</td><td>").append(r[1]).append("</td><td>").append(r[2]).append("</td><td>").append(r[3])
                    .append("</td><td>").append(r[4]).append("</td><td>").append(r[5]).append("</td><td>").append(r[6]).append("</td><td>").append(r[7])
                    .append("</td><td>").append(r[11]).append("/").append(r[12]).append("</td><td>").append(r[13]).append("</td><td>").append(r[14])
                    .append("</td></tr>");
                first=false;
            }

            // ====== QUERY HALAMAN 2 (Catatan Persalinan) ======
            String[] h = new String[60];
            for (int i=0; i<60; i++) h[i]="";
            try {
                ps = koneksi.prepareStatement(
                    "SELECT cp_merujuk_kala,cp_alasan_merujuk,cp_tempat_rujukan,cp_pendamping," +
                    "k1_garis_waspada,k1_masalah,k1_penatalaksanaan,k1_hasil," +
                    "k2_episiotomi,k2_indikasi_episiotomi,k2_gawat_janin,k2_tindakan_gawat_janin," +
                    "k2_distosia_bahu,k2_tindakan_distosia_bahu,k2_masalah,k2_penatalaksanaan,k2_hasil," +
                    "k3_lama,k3_oksitosin,k3_alasan_oksitosin," +
                    "k3_tali_pusat,k3_alasan_tali_pusat,k3_masase_fundus,k3_alasan_masase_fundus," +
                    "k3_plasenta_lengkap,k3_tindakan_plasenta_tidak_lengkap," +
                    "bbl_bb,bbl_pb,bbl_jk,bbl_penilaian,bbl_lahir,bbl_tindakan,bbl_asi,bbl_waktu_asi,bbl_masalah,bbl_hasil," +
                    "k4_masalah,k4_penatalaksanaan,k4_hasil " +
                    "FROM partograf_halaman2 WHERE no_rawat=? AND status='aktif' ORDER BY created_at DESC LIMIT 1");
                ps.setString(1, noRw);
                rs = ps.executeQuery();
                if (rs.next()) { for (int i=0;i<39;i++) h[i]=n(rs,i+1); }
            } finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }

            // Kala IV rows
            java.util.List<String[]> k4List = new java.util.ArrayList<>();
            try {
                ps2 = koneksi.prepareStatement("SELECT jam_ke,waktu,td,nadi,suhu,tfu,kontraksi,kandung_kemih,pendarahan,id FROM partograf_halaman2_kala4 WHERE no_rawat=? AND status='aktif' ORDER BY created_at");
                ps2.setString(1, noRw);
                rs2 = ps2.executeQuery();
                while(rs2.next()) { k4List.add(new String[]{n(rs2,1),n(rs2,2),n(rs2,3),n(rs2,4),n(rs2,5),n(rs2,6),n(rs2,7),n(rs2,8),n(rs2,9)}); }
            } finally { try{if(rs2!=null)rs2.close();if(ps2!=null)ps2.close();}catch(Exception e){} }

            // Query semua petugas yang terlibat
            java.util.LinkedHashMap<String,String> petugasMap = new java.util.LinkedHashMap<>();
            try {
                ps = koneksi.prepareStatement(
                    "SELECT DISTINCT p.nip, ifnull(pt.nama,p.nip) as nama FROM (" +
                    "  SELECT nip FROM partograf WHERE no_rawat=? AND status='aktif' " +
                    "  UNION SELECT nip FROM partograf_observasi WHERE no_rawat=? AND status='aktif' " +
                    "  UNION SELECT created_by as nip FROM partograf_halaman2 WHERE no_rawat=? AND status='aktif'" +
                    ") p LEFT JOIN petugas pt ON p.nip=pt.nip WHERE p.nip IS NOT NULL AND p.nip<>''");
                ps.setString(1, noRw); ps.setString(2, noRw); ps.setString(3, noRw);
                rs = ps.executeQuery();
                while(rs.next()) { petugasMap.put(rs.getString(1), rs.getString(2)); }
            } finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }

            // Helpers
            String cb = " style='display:inline-block;width:11px;height:11px;border:1px solid #000;margin-right:3px;vertical-align:middle'";
            String cbC = " style='display:inline-block;width:11px;height:11px;border:1px solid #000;background:#000;margin-right:3px;vertical-align:middle'";
            java.util.function.Function<Boolean,String> box = (v) -> "<span" + (v?cbC:cb) + "></span>";
            java.util.function.Function<String,String> ln = (v) -> "<span style='border-bottom:1px dotted #000;display:inline-block;min-width:120px;padding:0 4px'>" + v + "</span>";

            // Kala IV table rows
            StringBuilder k4Rows = new StringBuilder();
            for (String[] kr : k4List) { k4Rows.append("<tr>"); for (String v : kr) k4Rows.append("<td>").append(v).append("</td>"); k4Rows.append("</tr>"); }
            for (int i=k4List.size(); i<6; i++) k4Rows.append("<tr><td>&nbsp;</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>");

            // QR codes per petugas
            StringBuilder qrHtml = new StringBuilder();
            String tglCetak = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
            for (java.util.Map.Entry<String,String> entry : petugasMap.entrySet()) {
                String nip = entry.getKey(); String nama = entry.getValue();
                String qrData = "Dikeluarkan di " + namars + ", " + kotars + "\nDitandatangani secara elektronik oleh " + nama + "\nNIP: " + nip + "\nTanggal: " + tglCetak;
                qrHtml.append("<div style='text-align:center;min-width:100px'>")
                    .append("<img src='https://api.qrserver.com/v1/create-qr-code/?size=80x80&data=").append(java.net.URLEncoder.encode(qrData, "UTF-8"))
                    .append("' width='70' height='70' style='display:block;margin:0 auto'/>")
                    .append("<div style='font-size:7px;margin-top:1px'>Tanda Tangan Elektronik</div>")
                    .append("<div style='font-size:8px;font-weight:bold'>").append(nama).append("</div>")
                    .append("<div style='font-size:7px'>NIP: ").append(nip).append("</div></div>");
            }

            String merujuk = h[0]; String pendamping = h[3];

            String css =
                "@page{size:A4;margin:8mm}" +
                "body{font-family:Arial,sans-serif;font-size:9px;margin:0;padding:6px;line-height:1.4;color:#000}" +
                ".header{text-align:center;margin-bottom:4px}" +
                ".header h3{margin:0;font-size:13px}" +
                ".header p{margin:0;font-size:9px}" +
                ".page-break{page-break-before:always}" +
                ".section{font-weight:bold;font-size:10px;margin:4px 0 2px;text-decoration:underline}" +
                ".two-col{display:grid;grid-template-columns:1fr 1fr;gap:0 12px}" +
                ".item{margin:1px 0}" +
                "table.grid{border-collapse:collapse;width:100%;margin-top:3px}" +
                "table.grid td,table.grid th{border:1px solid #000;padding:1px 3px;font-size:8px}" +
                "table.grid th{background:#eee;text-align:center;font-weight:bold}" +
                "table.obs{border-collapse:collapse;width:100%;font-size:8px;margin-top:4px}" +
                "table.obs td,table.obs th{border:1px solid #333;padding:1px 2px;text-align:center}" +
                "table.obs th{background:#e0e0e0}" +
                ".chart-wrap{margin:2px 0}" +
                "canvas{display:block;width:100%!important}" +
                ".ttd-wrap{display:flex;justify-content:space-around;flex-wrap:wrap;gap:8px;margin-top:10px;padding-top:6px;border-top:1px solid #000}" +
                "@media print{.no-print{display:none}}";

            String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Partograf - " + noRw + "</title>" +
                "<script src='https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js'></script>" +
                "<style>" + css + "</style></head><body>" +
                "<button class='no-print' onclick='window.print()' style='position:fixed;top:6px;right:6px;padding:5px 14px;font-size:11px;cursor:pointer'>Cetak</button>" +

                // ==================== HALAMAN 1: GRAFIK PARTOGRAF ====================
                "<div class='header'><h3>" + namars + "</h3><p>" + alamatrs + " Telp: " + kontakrs + "</p></div>" +
                "<div style='text-align:center;font-weight:bold;font-size:14px;border-top:2px solid #000;border-bottom:1px solid #000;padding:3px 0;margin:3px 0'>PARTOGRAF</div>" +
                "<div style='display:flex;gap:20px;font-size:9px;margin:3px 0'>" +
                "<div><div>No. Register: <b>" + noRw + "</b></div><div>No. RM: <b>" + noRM + "</b></div><div>Ketuban Pecah: " + ketubanPecah + "</div></div>" +
                "<div><div>Nama Ibu: <b>" + nmPasien + "</b></div><div>Tanggal: " + tglPartograf + "</div><div>Mulas sejak pukul: " + mulesSejak + "</div></div>" +
                "<div><div>Umur: <b>" + umurPasien + "</b></div><div>G: <b>" + gpa_g + "</b> P: <b>" + gpa_p + "</b> A: <b>" + gpa_a + "</b></div></div></div>" +

                // Grafik DJJ
                "<div class='chart-wrap'><div style='display:flex;align-items:center;gap:2px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:8px;font-weight:bold'>Denyut Jantung Janin (/menit)</div>" +
                "<canvas id='cDJJ' height='100'></canvas></div></div>" +

                // Grafik Pembukaan+Penurunan
                "<div class='chart-wrap'><div style='display:flex;align-items:center;gap:2px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:8px;font-weight:bold'>Pembukaan serviks (cm) / Penurunan kepala</div>" +
                "<canvas id='cPem' height='140'></canvas></div></div>" +

                // Grafik Kontraksi
                "<div class='chart-wrap'><div style='display:flex;align-items:center;gap:2px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:8px;font-weight:bold'>Kontraksi /10 mnt</div>" +
                "<canvas id='cKon' height='50'></canvas></div></div>" +

                // Grafik TD/Nadi
                "<div class='chart-wrap'><div style='display:flex;align-items:center;gap:2px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:8px;font-weight:bold'>Tekanan Darah / Nadi</div>" +
                "<canvas id='cTD' height='100'></canvas></div></div>" +

                // Grafik Suhu
                "<div class='chart-wrap'><div style='display:flex;align-items:center;gap:2px'>" +
                "<div style='writing-mode:vertical-rl;transform:rotate(180deg);font-size:8px;font-weight:bold'>Suhu (°C)</div>" +
                "<canvas id='cSuhu' height='50'></canvas></div></div>" +

                // Tabel observasi ringkas
                "<table class='obs'><thead><tr><th>Waktu</th><th>DJJ</th><th>A.Ketuban</th><th>Penyusupan</th><th>Pembukaan</th><th>Penurunan</th>" +
                "<th>Kontraksi</th><th>Durasi</th><th>TD</th><th>Nadi</th><th>Suhu</th></tr></thead><tbody>" + obsTableRows + "</tbody></table>" +

                // ==================== HALAMAN 2: CATATAN PERSALINAN ====================
                "<div class='page-break'></div>" +
                "<div class='header'><h3>" + namars + "</h3><p>" + alamatrs + " Telp: " + kontakrs + "</p></div>" +
                "<div style='text-align:center;font-weight:bold;font-size:13px;border-top:2px solid #000;border-bottom:1px solid #000;padding:3px 0;margin:3px 0'>CATATAN PERSALINAN</div>" +
                "<div style='font-size:9px;margin:3px 0'><b>No. Rawat:</b> " + noRw + " &nbsp;&nbsp; <b>No. RM:</b> " + noRM + " &nbsp;&nbsp; <b>Nama:</b> " + nmPasien + "</div>" +

                "<div class='two-col'><div>" +
                "<div class='item'><b>1. Tanggal:</b> " + ln.apply(tglCetak) + "</div>" +
                "<div class='item'><b>2. Catatan, Rujukan Kala:</b> " +
                    box.apply(merujuk.contains("I"))+"I &nbsp;"+box.apply(merujuk.contains("II"))+"II &nbsp;"+box.apply(merujuk.contains("III"))+"III &nbsp;"+box.apply(merujuk.contains("IV"))+"IV</div>" +
                "<div class='item'><b>3. Alasan Merujuk:</b> " + ln.apply(h[1]) + "</div>" +
                "<div class='item'><b>4. Tempat Rujukan:</b> " + ln.apply(h[2]) + "</div>" +
                "<div class='item'><b>5. Pendamping saat merujuk:</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(pendamping.contains("Bidan"))+"Bidan "+box.apply(pendamping.contains("Suami"))+"Suami "+box.apply(pendamping.contains("Dukun"))+"Dukun</div>" +
                "<div style='padding-left:10px'>"+box.apply(pendamping.contains("Keluarga"))+"Keluarga "+box.apply(pendamping.contains("Teman"))+"Teman "+box.apply(pendamping.contains("Tidak Ada"))+"Tidak Ada</div>" +

                "<div class='section'>KALA I</div>" +
                "<div class='item'><b>6. Partograf melewati garis waspada:</b> "+box.apply(h[4].equals("Ya"))+"Y "+box.apply(!h[4].equals("Ya"))+"T</div>" +
                "<div class='item'><b>7. Masalah lain:</b> " + ln.apply(h[5]) + "</div>" +
                "<div class='item'><b>8. Penatalaksanaan:</b> " + ln.apply(h[6]) + "</div>" +
                "<div class='item'><b>9. Hasilnya:</b> " + ln.apply(h[7]) + "</div>" +

                "<div class='section'>KALA II</div>" +
                "<div class='item'><b>10. Episiotomi:</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[8].equals("Ya"))+"Ya, Indikasi: "+ln.apply(h[9])+"</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[8].equals("Tidak"))+"Tidak</div>" +
                "<div class='item'><b>11. Gawat janin:</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[10].equals("Ya"))+"Ya, Tindakan: "+ln.apply(h[11])+"</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[10].equals("Tidak"))+"Tidak</div>" +
                "<div class='item'><b>12. Distosia bahu:</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[12].equals("Ya"))+"Ya, Tindakan: "+ln.apply(h[13])+"</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[12].equals("Tidak"))+"Tidak</div>" +
                "<div class='item'><b>13. Masalah lain:</b> " + ln.apply(h[14]) + "</div>" +
                "<div class='item'><b>14. Penatalaksanaan:</b> " + ln.apply(h[15]) + "</div>" +
                "<div class='item'><b>15. Hasilnya:</b> " + ln.apply(h[16]) + "</div>" +

                "<div class='section'>KALA III</div>" +
                "<div class='item'><b>16. Lama kala III:</b> " + ln.apply(h[17]) + " menit</div>" +
                "<div class='item'><b>17. Pemberian Oksitosin 100 M</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[18].equals("Ya"))+"Ya, waktu: "+ln.apply("")+" menit sesudah persalinan</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[18].equals("Tidak"))+"Tidak, alasan: "+ln.apply(h[19])+"</div>" +

                "</div><div>" + // RIGHT COLUMN

                "<div class='item'><b>18. Penegangan tali pusat terkendali</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[20].equals("Ya"))+"Ya</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[20].equals("Tidak"))+"Tidak, alasan: "+ln.apply(h[21])+"</div>" +
                "<div class='item'><b>19. Masase Fundus Uteri?</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[22].equals("Ya"))+"Ya</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[22].equals("Tidak"))+"Tidak, alasan: "+ln.apply(h[23])+"</div>" +
                "<div class='item'><b>20. Plasenta lahir lengkap (intek):</b> "+box.apply(h[24].equals("Ya"))+"Ya "+box.apply(h[24].equals("Tidak"))+"Tidak</div>" +
                "<div style='padding-left:10px'>Jika tidak lengkap, tindakan: "+ln.apply(h[25])+"</div>" +

                "<div class='section'>BAYI BARU LAHIR</div>" +
                "<div class='item'><b>21. Berat badan:</b> " + ln.apply(h[26]) + " Gram</div>" +
                "<div class='item'><b>22. Panjang:</b> " + ln.apply(h[27]) + " cm</div>" +
                "<div class='item'><b>23. Jenis Kelamin:</b> "+box.apply("L".equals(h[28]))+"L "+box.apply("P".equals(h[28]))+"P</div>" +
                "<div class='item'><b>24. Penilaian BBL:</b> "+box.apply("Baik".equals(h[29]))+"Baik "+box.apply("Ada Penyulit".equals(h[29]))+"Ada penyulit</div>" +
                "<div class='item'><b>25. Bayi lahir</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[30].equals("Normal"))+"Normal, tindakan: "+ln.apply(h[31])+"</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[30].equals("Aspiksia"))+"Aspiksia ringan/pucat/biru/lemas</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[30].equals("Cacat"))+"Cacat bawaan</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[30].equals("Hipotermi"))+"Hipotermi</div>" +
                "<div class='item'><b>26. Pemberian ASI</b></div>" +
                "<div style='padding-left:10px'>"+box.apply(h[32].equals("Ya"))+"Ya, waktu: "+ln.apply(h[33])+" jam setelah bayi lahir</div>" +
                "<div style='padding-left:10px'>"+box.apply(h[32].equals("Tidak"))+"Tidak</div>" +
                "<div class='item'><b>27. Masalah lain:</b> " + ln.apply(h[34]) + "</div>" +
                "<div class='item'><b>&nbsp;&nbsp;&nbsp;Hasilnya:</b> " + ln.apply(h[35]) + "</div>" +

                "</div></div>" + // END two-col

                "<div class='section'>PEMANTAUAN PERSALINAN KALA IV</div>" +
                "<table class='grid'><thead><tr><th>Jam ke-</th><th>Waktu</th><th>Tekanan Darah</th><th>Nadi</th><th>Suhu</th>" +
                "<th>Tinggi Fundus Uteri</th><th>Kontraksi Uterus</th><th>Kandung Kemih</th><th>Pendarahan</th></tr></thead><tbody>" + k4Rows + "</tbody></table>" +
                "<div style='margin-top:4px'>" +
                "<div><b>Masalah KALA IV:</b> "+ln.apply(h[36])+"</div>" +
                "<div><b>Penatalaksanaan:</b> "+ln.apply(h[37])+"</div>" +
                "<div><b>Hasilnya:</b> "+ln.apply(h[38])+"</div></div>" +

                // QR CODES
                "<div class='ttd-wrap'>" + qrHtml + "</div>" +

                // ==================== JAVASCRIPT GRAFIK ====================
                "<script>" +
                "const w=["+jsWaktu+"],djj=["+jsDJJ+"],pem=["+jsPem+"],pen=["+jsPen+"],kon=["+jsKon+"],konDur=["+jsKonDur+"],sis=["+jsSis+"],dia=["+jsDia+"],nad=["+jsNadi+"],shu=["+jsSuhu+"],jk=["+jsJam+"];" +
                "const go={color:'rgba(0,0,0,0.12)'};const ls={tension:0.1,pointRadius:4,fill:false,borderWidth:2};" +
                // Hitung max jam untuk sumbu X pembukaan
                "let mxJ=Math.ceil(Math.max(...jk))+2; if(mxJ<12)mxJ=12;" +
                // Garis waspada & bertindak
                "let jw=-1;for(let i=0;i<pem.length;i++){if(pem[i]>=4){jw=jk[i];break;}}" +
                "const wp=[],bt=[];if(jw!==-1){for(let y=4;y<=10;y++){wp.push({x:jw+(y-4),y:y});bt.push({x:jw+4+(y-4),y:y});}}" +
                "const pp=w.map((l,i)=>({x:jk[i],y:pem[i]})),pn=w.map((l,i)=>({x:jk[i],y:pen[i]}));" +

                // ZONA NORMAL sebagai annotation background
                "const zoneDJJ={id:'zoneDJJ',beforeDraw(c){const{ctx,chartArea:a,scales:{y}}=c;if(!a)return;" +
                "const y1=y.getPixelForValue(160),y2=y.getPixelForValue(120);" +
                "ctx.save();ctx.fillStyle='rgba(144,238,144,0.2)';ctx.fillRect(a.left,y1,a.right-a.left,y2-y1);ctx.restore();}};" +

                // DJJ Chart - skala 80-200, zona hijau 120-160
                "new Chart(document.getElementById('cDJJ'),{type:'line',plugins:[zoneDJJ]," +
                "data:{labels:w,datasets:[{label:'DJJ',data:djj,borderColor:'#000',...ls,pointBackgroundColor:djj.map(v=>(v<120||v>160)?'red':'#000'),pointRadius:5}]}," +
                "options:{scales:{x:{grid:go,ticks:{font:{size:9}}},y:{min:80,max:200,ticks:{stepSize:10,font:{size:8}},grid:go}}," +
                "plugins:{legend:{display:false},tooltip:{enabled:true}},animation:false,maintainAspectRatio:false}});" +

                // Pembukaan + Penurunan - dual Y axis
                "new Chart(document.getElementById('cPem'),{type:'line',data:{datasets:[" +
                "{label:'WASPADA',data:wp,borderColor:'rgba(200,100,0,0.7)',borderWidth:2,borderDash:[6,3],pointRadius:0,fill:false}," +
                "{label:'BERTINDAK',data:bt,borderColor:'rgba(200,0,0,0.7)',borderWidth:2,borderDash:[3,3],pointRadius:0,fill:false}," +
                "{label:'Pembukaan (X)',data:pp,borderColor:'red',pointBackgroundColor:'red',pointStyle:'crossRot',pointRadius:6,...ls,borderWidth:2}," +
                "{label:'Penurunan (O)',data:pn,borderColor:'blue',pointBackgroundColor:'rgba(0,0,255,0.3)',pointStyle:'circle',pointRadius:6,...ls,borderWidth:2}" +
                "]}," +
                "options:{scales:{x:{type:'linear',min:0,max:mxJ,ticks:{stepSize:1,font:{size:8}},title:{display:true,text:'Waktu (jam)',font:{size:9}},grid:go}," +
                "y:{min:0,max:10,ticks:{stepSize:1,font:{size:8}},grid:go}}," +
                "plugins:{legend:{position:'top',labels:{font:{size:8},usePointStyle:true,pointStyle:'circle'}}},animation:false,maintainAspectRatio:false}});" +

                // Kontraksi - WHO standard: <20s=titik(ringan), 20-40s=diagonal(sedang), >40s=solid(kuat)
                "function konPattern(dur){" +
                "  const c=document.createElement('canvas');c.width=10;c.height=10;const x=c.getContext('2d');" +
                "  if(dur.includes('>40')||dur.includes('>40')){" +  // solid = kuat
                "    x.fillStyle='#000';x.fillRect(0,0,10,10);" +
                "  }else if(dur.includes('20')){" +  // diagonal = sedang (20-40)
                "    x.strokeStyle='#000';x.lineWidth=1.5;" +
                "    for(let i=-10;i<20;i+=4){x.beginPath();x.moveTo(i,0);x.lineTo(i+10,10);x.stroke();}" +
                "  }else{" +  // dots = ringan (<20)
                "    x.fillStyle='#fff';x.fillRect(0,0,10,10);" +
                "    x.fillStyle='#000';for(let px=1;px<10;px+=3)for(let py=1;py<10;py+=3)x.fillRect(px,py,1.5,1.5);" +
                "  }" +
                "  return x.createPattern(c,'repeat');" +
                "}" +
                "const konBg=konDur.map(d=>konPattern(d));" +
                "new Chart(document.getElementById('cKon'),{type:'bar',data:{labels:w,datasets:[{label:'Kontraksi',data:kon," +
                "backgroundColor:konBg,borderWidth:1,borderColor:'#000'}]}," +
                "options:{scales:{x:{grid:go,ticks:{font:{size:8}}},y:{min:0,max:5,ticks:{stepSize:1,font:{size:8}},grid:go}}," +
                "plugins:{legend:{display:false}},animation:false,maintainAspectRatio:false}});" +

                // TD/Nadi - zona normal shading
                "const zoneTD={id:'zoneTD',beforeDraw(c){const{ctx,chartArea:a,scales:{y}}=c;if(!a)return;" +
                "const y1=y.getPixelForValue(140),y2=y.getPixelForValue(90);" +
                "ctx.save();ctx.fillStyle='rgba(144,200,255,0.15)';ctx.fillRect(a.left,y1,a.right-a.left,y2-y1);ctx.restore();}};" +
                "new Chart(document.getElementById('cTD'),{type:'line',plugins:[zoneTD],data:{labels:w,datasets:[" +
                "{label:'Sistole',data:sis,borderColor:'#c00',...ls,pointStyle:'triangle',pointBackgroundColor:'#c00',pointRadius:5}," +
                "{label:'Diastole',data:dia,borderColor:'#00c',...ls,pointStyle:'triangle',pointBackgroundColor:'#00c',pointRadius:5,pointRotation:180}," +
                "{label:'Nadi',data:nad,borderColor:'#000',...ls,pointStyle:'circle',pointBackgroundColor:'#000',borderDash:[4,2]}" +
                "]}," +
                "options:{scales:{x:{grid:go,ticks:{font:{size:8}}},y:{min:60,max:180,ticks:{stepSize:10,font:{size:8}},grid:go}}," +
                "plugins:{legend:{position:'top',labels:{font:{size:8},usePointStyle:true}}},animation:false,maintainAspectRatio:false}});" +

                // Suhu - skala 35-40
                "const zoneSuhu={id:'zoneSuhu',beforeDraw(c){const{ctx,chartArea:a,scales:{y}}=c;if(!a)return;" +
                "const yl=y.getPixelForValue(37.5);" +
                "ctx.save();ctx.strokeStyle='rgba(255,0,0,0.4)';ctx.setLineDash([4,2]);ctx.beginPath();ctx.moveTo(a.left,yl);ctx.lineTo(a.right,yl);ctx.stroke();ctx.restore();}};" +
                "new Chart(document.getElementById('cSuhu'),{type:'line',plugins:[zoneSuhu],data:{labels:w,datasets:[" +
                "{label:'Suhu',data:shu,borderColor:'#c00',...ls,pointBackgroundColor:shu.map(v=>v>=37.5?'red':'#c00'),pointRadius:5}" +
                "]}," +
                "options:{scales:{x:{grid:go,ticks:{font:{size:8}}},y:{min:35,max:40,ticks:{stepSize:0.5,font:{size:8}},grid:go}}," +
                "plugins:{legend:{display:false}},animation:false,maintainAspectRatio:false}});" +
                "</script></body></html>";

            openHtmlInBrowser(html, "partograf_lengkap");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal mencetak: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        String ket = JOptionPane.showInputDialog(null, "Keterangan penghapusan data:", "Hapus Partograf", JOptionPane.QUESTION_MESSAGE);
        if (ket != null && !ket.trim().isEmpty()) {
            if (TabPartograf.getSelectedIndex() == 0) {
                if (tbPartograf.getSelectedRow() != -1) {
                    try {
                        ps = koneksi.prepareStatement("UPDATE partograf SET status='dibatalkan', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                        ps.setString(1, akses.getkode());
                        ps.setString(2, ket);
                        ps.setString(3, tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 13).toString());
                        ps.executeUpdate();
                        tampil();
                        emptTeks();
                    } catch (Exception ex) { System.out.println("Hapus Partograf: " + ex); }
                } else {
                    JOptionPane.showMessageDialog(null, "Silahkan pilih data di tabel keterangan!");
                }
            } else if (TabPartograf.getSelectedIndex() == 1) {
                if (tbObservasi.getSelectedRow() != -1) {
                    try {
                        ps = koneksi.prepareStatement("UPDATE partograf_observasi SET status='dibatalkan', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                        ps.setString(1, akses.getkode());
                        ps.setString(2, ket);
                        ps.setString(3, tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 21).toString());
                        ps.executeUpdate();
                        tampil_observasi();
                        emptTeks();
                    } catch (Exception ex) { System.out.println("Hapus Observasi: " + ex); }
                } else {
                    JOptionPane.showMessageDialog(null, "Silahkan pilih data di tabel observasi!");
                }
            } else if (TabPartograf.getSelectedIndex() == 2) {
                 int selectedSubTab = TabHalaman2.getSelectedIndex();
                 if (selectedSubTab == 5) {
                     // Hapus baris Kala IV
                     if (tbKala4.getSelectedRow() != -1) {
                         try {
                             String ket4 = JOptionPane.showInputDialog(null, "Keterangan penghapusan:", "Hapus Baris Kala IV", JOptionPane.QUESTION_MESSAGE);
                             if (ket4 != null && !ket4.trim().isEmpty()) {
                                 String idKala4 = tabModeKala4.getValueAt(tbKala4.getSelectedRow(), 9).toString();
                                 ps = koneksi.prepareStatement("UPDATE partograf_halaman2_kala4 SET status='dibatalkan', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                                 ps.setString(1, akses.getkode());
                                 ps.setString(2, ket4);
                                 ps.setString(3, idKala4);
                                 ps.executeUpdate();
                                 tampilKala4();
                                 emptTeks();
                             }
                         } catch (Exception ex) { System.out.println("Hapus Kala IV: " + ex); }
                     } else {
                         JOptionPane.showMessageDialog(null, "Silahkan pilih baris Kala IV di tabel!");
                     }
                 } else {
                     // Hapus Halaman 2 lainnya (soft delete)
                     if (tbHalaman2.getSelectedRow() != -1) {
                         try {
                             String ket2 = JOptionPane.showInputDialog(null, "Keterangan penghapusan data:", "Hapus Partograf Halaman 2", JOptionPane.QUESTION_MESSAGE);
                             if (ket2 != null && !ket2.trim().isEmpty()) {
                                 ps = koneksi.prepareStatement("UPDATE partograf_halaman2 SET status='dibatalkan', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                                 ps.setString(1, akses.getkode());
                                 ps.setString(2, ket2);
                                 ps.setString(3, tbHalaman2.getValueAt(tbHalaman2.getSelectedRow(), 12).toString());
                                 ps.executeUpdate();
                                 tampil_halaman2();
                                 emptTeks();
                             }
                         } catch (Exception ex) { System.out.println("Hapus Halaman 2: " + ex); }
                     } else {
                         JOptionPane.showMessageDialog(null, "Silahkan pilih data di tabel!");
                     }
                 }
            }
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (TKodePetugas.getText().trim().equals("")) {
            Valid.textKosong(TKodePetugas, "Petugas");
        } else {
            String ket = JOptionPane.showInputDialog(null, "Keterangan perubahan data:", "Edit Partograf", JOptionPane.QUESTION_MESSAGE);
            if (ket != null && !ket.trim().isEmpty()) {
                if (TabPartograf.getSelectedIndex() == 0) {
                    if (tbPartograf.getSelectedRow() != -1) {
                        try {
                            ps = koneksi.prepareStatement("UPDATE partograf SET status='revisi', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                            ps.setString(1, akses.getkode());
                            ps.setString(2, ket);
                            ps.setString(3, tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 13).toString());
                            ps.executeUpdate();
                            
                            ps = koneksi.prepareStatement(
                                "INSERT INTO partograf (no_rawat, tgl_jam, g, p, a, ketuban_pecah, mules_sejak, nip, status, created_by, created_at) " +
                                "VALUES (?,?,?,?,?,?,?,?,'aktif',?,NOW())");
                            ps.setString(1, TNoRw.getText());
                            ps.setString(2, Valid.SetTgl(TTanggal.getSelectedItem().toString()) + " " + TTanggal.getSelectedItem().toString().substring(11, 19));
                            ps.setString(3, TG.getText());
                            ps.setString(4, TP.getText());
                            ps.setString(5, TA.getText());
                            ps.setString(6, TKetubanPecah.getText());
                            ps.setString(7, Valid.SetTgl(TMulesSejak.getSelectedItem().toString()) + " " + TMulesSejak.getSelectedItem().toString().substring(11, 19));
                            ps.setString(8, TKodePetugas.getText());
                            ps.setString(9, akses.getkode());
                            ps.executeUpdate();
                            
                            tampil();
                            emptTeks();
                        } catch (Exception ex) { System.out.println("Edit Partograf: " + ex); }
                    }
                } else if (TabPartograf.getSelectedIndex() == 1) {
                     if (tbObservasi.getSelectedRow() != -1) {
                        try {
                            ps = koneksi.prepareStatement("UPDATE partograf_observasi SET status='revisi', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                            ps.setString(1, akses.getkode());
                            ps.setString(2, ket);
                            ps.setString(3, tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 21).toString());
                            ps.executeUpdate();
                            
                            ps = koneksi.prepareStatement(
                                "INSERT INTO partograf_observasi (no_rawat, tgl_jam, djj, air_ketuban, penyusupan, pembukaan, penurunan, kontraksi_jml, kontraksi_durasi, oksitosin_unit, oksitosin_tetes, obat_cairan, sistole, diastole, nadi, suhu, urine_vol, urine_prot, urine_aseton, nip, status, created_by, created_at) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'aktif',?,NOW())");
                            ps.setString(1, TNoRw.getText());
                            ps.setString(2, Valid.SetTgl(TTanggal.getSelectedItem().toString()) + " " + TTanggal.getSelectedItem().toString().substring(11, 19));
                            ps.setString(3, TDJJ.getText());
                            ps.setString(4, CAirKetuban.getSelectedItem().toString().substring(0, 1));
                            ps.setString(5, CPenyusupan.getSelectedItem().toString());
                            ps.setString(6, TPembukaan.getText());
                            ps.setString(7, TPenurunan.getText());
                            ps.setString(8, TKontraksiJml.getText());
                            ps.setString(9, CKontraksiDurasi.getSelectedItem().toString());
                            ps.setString(10, TOksitosinUnit.getText());
                            ps.setString(11, TOksitosinTetes.getText());
                            ps.setString(12, TObatCairan.getText());
                            ps.setString(13, TSistole.getText());
                            ps.setString(14, TDiastole.getText());
                            ps.setString(15, TNadi.getText());
                            ps.setString(16, TSuhu.getText());
                            ps.setString(17, TUrineVol.getText());
                            ps.setString(18, TUrineProt.getText());
                            ps.setString(19, TUrineAseton.getText());
                            ps.setString(20, TKodePetugas.getText());
                            ps.setString(21, akses.getkode());
                            ps.executeUpdate();
                            
                            tampil_observasi();
                            emptTeks();
                        } catch (Exception ex) { System.out.println("Edit Observasi: " + ex); }
                     }
                } else if (TabPartograf.getSelectedIndex() == 2) {
                     if (tbHalaman2.getSelectedRow() != -1) {
                         try {
                             koneksi.setAutoCommit(false);
                             // Set current to revision
                             ps = koneksi.prepareStatement("UPDATE partograf_halaman2 SET status='revisi', updated_by=?, updated_at=NOW(), keterangan_edit=? WHERE id=?");
                             ps.setString(1, akses.getkode());
                             ps.setString(2, ket);
                             ps.setString(3, tbHalaman2.getValueAt(tbHalaman2.getSelectedRow(), 12).toString());
                             ps.executeUpdate();

                             String rujukan = "";
                             if (CKala1.isSelected()) rujukan += "I ";
                             if (CKala2.isSelected()) rujukan += "II ";
                             if (CKala3.isSelected()) rujukan += "III ";
                             if (CKala4.isSelected()) rujukan += "IV ";
                             if (rujukan.trim().equals("")) rujukan = "Tidak";
                             
                             String pendamping = "";
                             if (CPendampingBidan.isSelected()) pendamping += "Bidan ";
                             if (CPendampingSuami.isSelected()) pendamping += "Suami ";
                             if (CPendampingKeluarga.isSelected()) pendamping += "Keluarga ";
                             if (CPendampingDukun.isSelected()) pendamping += "Dukun ";
                             if (CPendampingTeman.isSelected()) pendamping += "Teman ";
                             if (CPendampingTidakAda.isSelected()) pendamping = "Tidak Ada";

                             // Insert new active record
                             ps = koneksi.prepareStatement(
                                 "INSERT INTO partograf_halaman2 (no_rawat, cp_merujuk_kala, cp_alasan_merujuk, cp_tempat_rujukan, cp_pendamping, k1_garis_waspada, k1_masalah, k1_penatalaksanaan, k1_hasil, k2_episiotomi, k2_indikasi_episiotomi, k2_gawat_janin, k2_tindakan_gawat_janin, k2_distosia_bahu, k2_tindakan_distosia_bahu, k2_masalah, k2_penatalaksanaan, k2_hasil, k3_lama, k3_oksitosin, k3_alasan_oksitosin, k3_tali_pusat, k3_alasan_tali_pusat, k3_masase_fundus, k3_alasan_masase_fundus, k3_plasenta_lengkap, k3_tindakan_plasenta_tidak_lengkap, bbl_bb, bbl_pb, bbl_jk, bbl_penilaian, bbl_lahir, bbl_tindakan, bbl_masalah, bbl_hasil, bbl_asi, bbl_waktu_asi, k4_masalah, k4_penatalaksanaan, k4_hasil, created_by, created_at, status) " +
                                 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'aktif')");
                             ps.setString(1, TNoRw.getText());
                             ps.setString(2, rujukan.trim());
                             ps.setString(3, TAlasanMerujuk.getText());
                             ps.setString(4, TTempatRujukan.getText());
                             ps.setString(5, pendamping.trim());
                             ps.setString(6, CKala1Waspada.getSelectedItem().toString());
                             ps.setString(7, TKala1Masalah.getText());
                             ps.setString(8, TKala1Tindakan.getText());
                             ps.setString(9, TKala1Hasil.getText());
                             ps.setString(10, CKala2Episiotomi.getSelectedItem().toString());
                             ps.setString(11, TKala2EpisiotomiAlasan.getText());
                             ps.setString(12, CKala2GawatJanin.getSelectedItem().toString());
                             ps.setString(13, TKala2GawatJaninTindakan.getText());
                             ps.setString(14, CKala2Distosia.getSelectedItem().toString());
                             ps.setString(15, TKala2DistosiaTindakan.getText());
                             ps.setString(16, TKala2Masalah.getText());
                             ps.setString(17, TKala2Tindakan.getText());
                             ps.setString(18, TKala2Hasil.getText());
                             ps.setInt(19, TKala3Lama.getText().trim().equals("") ? 0 : Integer.parseInt(TKala3Lama.getText()));
                             ps.setString(20, CKala3Oksitosin.getSelectedItem().toString());
                             ps.setString(21, TKala3OksitosinAlasan.getText());
                             ps.setString(22, CKala3TaliPusat.getSelectedItem().toString());
                             ps.setString(23, TKala3TaliPusatAlasan.getText());
                             ps.setString(24, CKala3Masase.getSelectedItem().toString());
                             ps.setString(25, TKala3MasaseAlasan.getText());
                             ps.setString(26, CKala3PlasentaLengkap.getSelectedItem().toString());
                             ps.setString(27, TKala3PlasentaTindakan.getText());
                             ps.setInt(28, TBBLBB.getText().trim().equals("") ? 0 : Integer.parseInt(TBBLBB.getText()));
                             ps.setInt(29, TBBLPB.getText().trim().equals("") ? 0 : Integer.parseInt(TBBLPB.getText()));
                             ps.setString(30, CBBLJK.getSelectedItem().toString());
                             ps.setString(31, CBBLPenilaian.getSelectedItem().toString());
                             ps.setString(32, CBBLBayiLahir.getSelectedItem().toString());
                             ps.setString(33, TBBLNormalTindakan.getText());
                             ps.setString(34, TBBLMasalah.getText());
                             ps.setString(35, TBBLHasil.getText());
                             ps.setString(36, CBBLASI.getSelectedItem().toString());
                             ps.setString(37, TBBLASITglJam.getText());
                             ps.setString(38, TMasalahKala4.getText());
                             ps.setString(39, TMasalahKala4Tindakan.getText());
                             ps.setString(40, TMasalahKala4Hasil.getText());
                             ps.setString(41, TKodePetugas.getText());
                             ps.executeUpdate();

                             // Update Kala4 rows Status to revision
                             ps2 = koneksi.prepareStatement("UPDATE partograf_halaman2_kala4 SET status='revisi' WHERE no_rawat=?");
                             ps2.setString(1, TNoRw.getText());
                             ps2.executeUpdate();

                             // Kala IV dikelola per baris via BtnSimpan/BtnHapus Kala IV, tidak perlu insert ulang di sini

                             koneksi.commit();
                             koneksi.setAutoCommit(true);
                             tampil_halaman2();
                             emptTeks();
                         } catch (Exception ex) {
                             try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception e) {}
                             System.out.println("Edit Halaman 2: " + ex);
                         }
                     }
                }
            }
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void tbPartografMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (Exception e) {}
        }
    }

    private void tbHalaman2MouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeHalaman2.getRowCount() != 0) {
            try {
                getDataHalaman2();
            } catch (Exception e) {}
        }
    }

    private void tbObservasiMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeObs.getRowCount() != 0) {
            try {
                getDataObs();
            } catch (Exception e) {}
        }
    }

    // ===== Public Methods =====

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String sql = "SELECT p.no_rawat, r.no_rkm_medis, pa.nm_pasien, pa.tgl_lahir, " +
                "TIMESTAMPDIFF(YEAR, pa.tgl_lahir, CURDATE()) AS umur, p.tgl_jam, " +
                "p.g, p.p, p.a, p.ketuban_pecah, p.mules_sejak, p.nip, ifnull(pt.nama,p.nip) as nama, p.id " +
                "FROM partograf p INNER JOIN reg_periksa r ON p.no_rawat=r.no_rawat " +
                "INNER JOIN pasien pa ON r.no_rkm_medis=pa.no_rkm_medis " +
                "LEFT JOIN petugas pt ON p.nip=pt.nip " +
                "WHERE p.tgl_jam between ? and ? and p.no_rawat like ? AND p.status='aktif' " +
                (TCari.getText().trim().equals("") ? "" :
                    "and (p.no_rawat like ? or r.no_rkm_medis like ? or pa.nm_pasien like ?) ") +
                "ORDER BY p.tgl_jam DESC";
            ps = koneksi.prepareStatement(sql);
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TNoRw.getText() + "%");
                if (!TCari.getText().trim().equals("")) {
                    ps.setString(4, "%" + TCari.getText().trim() + "%");
                    ps.setString(5, "%" + TCari.getText().trim() + "%");
                    ps.setString(6, "%" + TCari.getText().trim() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5) + " th", rs.getString(6), rs.getString(7),
                        rs.getString(8), rs.getString(9), rs.getString(10),
                        rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14)
                    });
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

    public void tampil_observasi() {
        Valid.tabelKosong(tabModeObs);
        try {
            String sql = "SELECT p.no_rawat, p.tgl_jam, p.djj, p.air_ketuban, p.penyusupan, p.pembukaan, p.penurunan, " +
                "p.kontraksi_jml, p.kontraksi_durasi, p.oksitosin_unit, p.oksitosin_tetes, p.obat_cairan, " +
                "p.sistole, p.diastole, p.nadi, p.suhu, p.urine_vol, p.urine_prot, p.urine_aseton, p.nip, ifnull(pt.nama,p.nip) as nama, p.id " +
                "FROM partograf_observasi p LEFT JOIN petugas pt ON p.nip=pt.nip " +
                "WHERE p.tgl_jam between ? and ? and p.no_rawat like ? AND p.status='aktif' " +
                (TCari.getText().trim().equals("") ? "" :
                    "and (p.no_rawat like ?) ") +
                "ORDER BY p.tgl_jam DESC";
            ps2 = koneksi.prepareStatement(sql);
            try {
                ps2.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps2.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps2.setString(3, "%" + TNoRw.getText() + "%");
                if (!TCari.getText().trim().equals("")) {
                    ps2.setString(4, "%" + TCari.getText().trim() + "%");
                }
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    Object[] row = new Object[]{
                        rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4),
                        rs2.getString(5), rs2.getString(6), rs2.getString(7), rs2.getString(8),
                        rs2.getString(9), rs2.getString(10), rs2.getString(11), rs2.getString(12),
                        rs2.getString(13), rs2.getString(14), rs2.getString(15), rs2.getString(16),
                        rs2.getString(17), rs2.getString(18), rs2.getString(19), rs2.getString(20), rs2.getString(21), rs2.getString(22)
                    };
                    tabModeObs.addRow(row);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs2 != null) rs2.close();
                if (ps2 != null) ps2.close();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        deteksiLewatGarisWaspada();   // auto-isi checklist "melewati garis waspada" dari data observasi
    }

    // ====== Cherry-pick dari rsthb_ehd_adistia: deteksi garis waspada WHO + pengingat interval ======
    private int lewatGarisStatus = 0;

    /** Hitung apakah grafik pembukaan melewati garis waspada/bertindak dari partograf_observasi,
     *  set {@code lewatGarisStatus} (0=tidak,1=waspada,2=bertindak) & auto-isi checklist Kala I. */
    private int deteksiLewatGarisWaspada() {
        lewatGarisStatus = 0;
        if (TNoRw.getText().trim().equals("")) { CKala1Waspada.setSelectedIndex(0); return 0; }
        java.util.List<Double> jamList = new java.util.ArrayList<>();
        java.util.List<Double> pemList = new java.util.ArrayList<>();
        try {
            ps = koneksi.prepareStatement(
                "SELECT TIMESTAMPDIFF(MINUTE, MIN(tgl_jam) OVER(), tgl_jam)/60.0, IFNULL(pembukaan,0) "
                + "FROM partograf_observasi WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                jamList.add(rs.getDouble(1));
                double pem = 0;
                try { pem = Double.parseDouble(rs.getString(2)); } catch (Exception e) {}
                pemList.add(pem);
            }
        } catch (Exception e) {
            System.out.println("Deteksi waspada: " + e);
            return 0;
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
        double[] jamKe = new double[jamList.size()], pembukaan = new double[pemList.size()];
        for (int i = 0; i < jamKe.length; i++) { jamKe[i] = jamList.get(i); pembukaan[i] = pemList.get(i); }
        lewatGarisStatus = PartografData.statusLewatGaris(jamKe, pembukaan);   // sumber tunggal
        CKala1Waspada.setSelectedIndex(lewatGarisStatus >= 1 ? 1 : 0);          // 0=Tidak, 1=Ya
        return lewatGarisStatus;
    }

    // Pengingat lunak interval WHO (tidak memblokir simpan): ingatkan bila grup parameter
    // sudah jatuh tempo dan input grup tsb sekarang kosong.
    //   - Periksa dalam/VT (pembukaan, penurunan, air ketuban, penyusupan) & Tekanan Darah: tiap 4 jam
    //   - Suhu: tiap 2 jam ; DJJ/kontraksi/nadi: tiap 30 menit (sudah dijaga cekInterval()).
    private void pengingatIntervalParameter() {
        String tglBaru = Valid.SetTgl(TTanggal.getSelectedItem().toString()) + " " + TTanggal.getSelectedItem().toString().substring(11, 19);
        StringBuilder sb = new StringBuilder();
        cekIntervalGrup("pembukaan", 240, "Periksa dalam (pembukaan/penurunan/air ketuban/penyusupan)", TPembukaan.getText().trim().isEmpty(), tglBaru, sb);
        cekIntervalGrup("sistole",   240, "Tekanan darah", TSistole.getText().trim().isEmpty(), tglBaru, sb);
        cekIntervalGrup("suhu",      120, "Suhu", TSuhu.getText().trim().isEmpty(), tglBaru, sb);
        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(null,
                "<html><b>Pengingat interval WHO</b> (boleh tetap disimpan):<br>" + sb
                + "<small>DJJ/kontraksi/nadi dicatat tiap 30 menit.</small></html>",
                "Pengingat Interval Partograf", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cekIntervalGrup(String field, int intervalMnt, String label, boolean inputKosong, String tglBaru, StringBuilder sb) {
        if (!inputKosong) return;   // grup ini sedang diisi -> tidak perlu diingatkan
        try {
            ps = koneksi.prepareStatement(
                "SELECT TIMESTAMPDIFF(MINUTE, MAX(tgl_jam), ?) FROM partograf_observasi "
                + "WHERE no_rawat=? AND status='aktif' AND IFNULL(" + field + ",'') NOT IN ('','0')");
            ps.setString(1, tglBaru);
            ps.setString(2, TNoRw.getText());
            rs = ps.executeQuery();
            if (rs.next() && rs.getObject(1) != null) {
                int menit = rs.getInt(1);
                if (menit >= intervalMnt) {
                    sb.append("• ").append(label).append(" sudah ").append(menit)
                      .append(" menit (tiap ").append(intervalMnt / 60).append(" jam) — mohon diperbarui.<br>");
                }
            }
        } catch (Exception e) {
            System.out.println("Pengingat interval: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    /** True bila pasien sudah punya minimal satu observasi fase aktif (pembukaan &ge; 4 cm). */
    private boolean sudahAdaObservasiAktif() {
        boolean ada = false;
        try {
            ps = koneksi.prepareStatement(
                "SELECT 1 FROM partograf_observasi WHERE no_rawat=? AND status='aktif' AND pembukaan>=4 LIMIT 1");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            ada = rs.next();
        } catch (Exception e) {
            System.out.println("Cek observasi aktif: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
        return ada;
    }

    public void tampil_halaman2() {
        Valid.tabelKosong(tabModeHalaman2);
        try {
            ps = koneksi.prepareStatement(
                "SELECT h.no_rawat, h.created_at, TIME(h.created_at), h.bbl_jk, h.bbl_bb, h.bbl_pb, h.bbl_hasil, h.k3_lama, h.k3_plasenta_lengkap, h.k3_oksitosin, h.k4_masalah, h.k4_hasil, h.created_by " +
                "FROM partograf_halaman2 h WHERE h.no_rawat=? AND h.status<>'dibatalkan' ORDER BY h.created_at DESC");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    Object[] row = new Object[]{
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13)
                    };
                    tabModeHalaman2.addRow(row);
                }

                // Fill specific history tables
                ps2 = koneksi.prepareStatement("SELECT * FROM partograf_halaman2 WHERE no_rawat=? AND status<>'dibatalkan' ORDER BY created_at DESC");
                ps2.setString(1, TNoRw.getText());
                rs2 = ps2.executeQuery();
                Valid.tabelKosong(tabModeCatatanPersalinan);
                Valid.tabelKosong(tabModeKala1);
                Valid.tabelKosong(tabModeKala2);
                Valid.tabelKosong(tabModeKala3);
                Valid.tabelKosong(tabModeBBL);
                Valid.tabelKosong(tabModeMasalahKala4);
                while (rs2.next()) {
                    tabModeCatatanPersalinan.addRow(new Object[]{
                        rs2.getString("no_rawat"), rs2.getString("created_at"), 
                        rs2.getString("cp_merujuk_kala"), rs2.getString("cp_alasan_merujuk"),
                        rs2.getString("cp_tempat_rujukan"), rs2.getString("cp_pendamping")
                    });
                    tabModeKala1.addRow(new Object[]{
                        rs2.getString("no_rawat"), rs2.getString("created_at"), 
                        rs2.getString("k1_garis_waspada"), rs2.getString("k1_masalah"),
                        rs2.getString("k1_penatalaksanaan"), rs2.getString("k1_hasil")
                    });
                    tabModeKala2.addRow(new Object[]{
                        rs2.getString("no_rawat"), rs2.getString("created_at"), 
                        rs2.getString("k2_episiotomi"), rs2.getString("k2_gawat_janin"),
                        rs2.getString("k2_distosia_bahu"), rs2.getString("k2_masalah"),
                        rs2.getString("k2_penatalaksanaan"), rs2.getString("k2_hasil")
                    });
                    tabModeKala3.addRow(new Object[]{
                        rs2.getString("no_rawat"), rs2.getString("created_at"), 
                        rs2.getString("k3_lama"), rs2.getString("k3_oksitosin"),
                        rs2.getString("k3_masase_fundus"), rs2.getString("k3_plasenta_lengkap")
                    });
                    tabModeBBL.addRow(new Object[]{
                        rs2.getString("no_rawat"), rs2.getString("created_at"), 
                        rs2.getString("bbl_jk"), rs2.getString("bbl_bb"),
                        rs2.getString("bbl_pb"), rs2.getString("bbl_lahir"),
                        rs2.getString("bbl_penilaian"), rs2.getString("bbl_masalah"),
                        rs2.getString("bbl_hasil")
                    });
                    tabModeMasalahKala4.addRow(new Object[]{
                        rs2.getString("no_rawat"), rs2.getString("created_at"), 
                        rs2.getString("k4_masalah"), rs2.getString("k4_penatalaksanaan"),
                        rs2.getString("k4_hasil")
                    });
                }

                // Load Kala IV rows
                tampilKala4();
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (rs2 != null) rs2.close();
                if (ps2 != null) ps2.close();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void tampilKala4() {
        if (TNoRw.getText().trim().isEmpty()) return;
        Valid.tabelKosong(tabModeKala4);
        try {
            ps = koneksi.prepareStatement(
                "SELECT jam_ke, waktu, td, nadi, suhu, tfu, kontraksi, kandung_kemih, pendarahan, id " +
                "FROM partograf_halaman2_kala4 WHERE no_rawat=? AND status='aktif' ORDER BY created_at");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeKala4.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)
                });
            }
        } catch (Exception e) {
            System.out.println("tampilKala4: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    private void getDataKala4() {
        if (tbKala4.getSelectedRow() != -1) {
            int row = tbKala4.getSelectedRow();
            TKala4Jam.setText(tabModeKala4.getValueAt(row, 0).toString());
            TKala4Waktu.setText(tabModeKala4.getValueAt(row, 1).toString());
            TKala4TD.setText(tabModeKala4.getValueAt(row, 2).toString());
            TKala4Nadi.setText(tabModeKala4.getValueAt(row, 3).toString());
            TKala4Suhu.setText(tabModeKala4.getValueAt(row, 4).toString());
            TKala4TFU.setText(tabModeKala4.getValueAt(row, 5).toString());
            TKala4Kontraksi.setText(tabModeKala4.getValueAt(row, 6).toString());
            TKala4Kandung.setText(tabModeKala4.getValueAt(row, 7).toString());
            TKala4Pendarahan.setText(tabModeKala4.getValueAt(row, 8).toString());
        }
    }

    private void getData() {
        if (tbPartograf.getSelectedRow() != -1) {
            TNoRw.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 0).toString());
            TNoRM.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 1).toString());
            TNamaPasien.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 2).toString());
            TTglLahir.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 3).toString());
            TUmur.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 4).toString());
            Valid.SetTgl2(TTanggal, tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 5).toString());
            TG.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 6).toString());
            TP.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 7).toString());
            TA.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 8).toString());
            TKetubanPecah.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 9).toString());
            Valid.SetTgl2(TMulesSejak, tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 10).toString());
            TKodePetugas.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 11).toString());
            TNamaPetugas.setText(tbPartograf.getValueAt(tbPartograf.getSelectedRow(), 12).toString());
        }
    }

    private void getDataObs() {
        if (tbObservasi.getSelectedRow() != -1) {
            TNoRw.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 0).toString());
            Valid.SetTgl2(TTanggal, tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 1).toString());
            TDJJ.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 2).toString());
            // Map back combobox values
            String ak = tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 3).toString();
            for(int i=0; i<CAirKetuban.getItemCount(); i++) {
                if(CAirKetuban.getItemAt(i).toString().startsWith(ak)) {
                    CAirKetuban.setSelectedIndex(i);
                    break;
                }
            }
            CPenyusupan.setSelectedItem(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 4).toString());
            TPembukaan.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 5).toString());
            TPenurunan.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 6).toString());
            TKontraksiJml.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 7).toString());
            CKontraksiDurasi.setSelectedItem(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 8).toString());
            TOksitosinUnit.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 9).toString());
            TOksitosinTetes.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 10).toString());
            TObatCairan.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 11).toString());
            TSistole.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 12).toString());
            TDiastole.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 13).toString());
            TNadi.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 14).toString());
            TSuhu.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 15).toString());
            TUrineVol.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 16).toString());
            TUrineProt.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 17).toString());
            TUrineAseton.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 18).toString());
            TKodePetugas.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 19).toString());
            TNamaPetugas.setText(tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 20).toString());
        }
    }

    public void emptTeks() {
        TG.setText("");
        TP.setText("");
        TA.setText("");
        TKetubanPecah.setText("");
        TMulesSejak.setDate(new Date());

        TDJJ.setText("");
        CAirKetuban.setSelectedIndex(0);
        CPenyusupan.setSelectedIndex(0);
        TPembukaan.setText("");
        TPenurunan.setText("");
        TKontraksiJml.setText("");
        CKontraksiDurasi.setSelectedIndex(0);
        TSistole.setText("");
        TDiastole.setText("");
        TNadi.setText("");
        TSuhu.setText("");
        TOksitosinUnit.setText("");
        TOksitosinTetes.setText("");
        TObatCairan.setText("");
        TUrineVol.setText("");
        TUrineProt.setText("");
        TUrineAseton.setText("");
        
        // Catatan Persalinan Restructured
        TTglPersalinan.setDate(new Date());
        CKala1.setSelected(false);
        CKala2.setSelected(false);
        CKala3.setSelected(false);
        CKala4.setSelected(false);
        TAlasanMerujuk.setText("");
        TTempatRujukan.setText("");
        CPendampingBidan.setSelected(false);
        CPendampingSuami.setSelected(false);
        CPendampingKeluarga.setSelected(false);
        CPendampingDukun.setSelected(false);
        CPendampingTeman.setSelected(false);
        CPendampingTidakAda.setSelected(false);

        // KALA I
        CKala1Waspada.setSelectedIndex(0);
        TKala1Masalah.setText("");
        TKala1Tindakan.setText("");
        TKala1Hasil.setText("");

        // KALA II
        CKala2Episiotomi.setSelectedIndex(0);
        CKala2PendampingSuami.setSelected(false);
        CKala2PendampingKeluarga.setSelected(false);
        CKala2PendampingTeman.setSelected(false);
        CKala2PendampingDukun.setSelected(false);
        CKala2PendampingTidakAda.setSelected(false);
        CKala2GawatJanin.setSelectedIndex(0);
        CKala2Distosia.setSelectedIndex(0);
        TKala2Masalah.setText("");
        TKala2Tindakan.setText("");
        TKala2Hasil.setText("");

        // KALA III
        TKala3Lama.setText("");
        CKala3Oksitosin.setSelectedIndex(0);
        TKala3OksitosinAlasan.setText("");
        CKala3Masase.setSelectedIndex(0);
        TKala3MasaseAlasan.setText("");
        CKala3PlasentaLengkap.setSelectedIndex(0);
        TKala3PlasentaTindakan.setText("");

        // BAYI BARU LAHIR
        TBBLBB.setText("");
        TBBLPB.setText("");
        CBBLJK.setSelectedIndex(0);
        CBBLPenilaian.setSelectedIndex(0);
        CBBLBayiLahir.setSelectedIndex(0);
        TBBLMasalah.setText("");
        TBBLHasil.setText("");

        // KALA IV (field inputs only - table diisi oleh tampilKala4())
        TKala4Jam.setText("");
        TKala4Waktu.setText("");
        TKala4TD.setText("");
        TKala4Nadi.setText("");
        TKala4Suhu.setText("");
        TKala4TFU.setText("");
        TKala4Kontraksi.setText("");
        TKala4Kandung.setText("");
        TKala4Pendarahan.setText("");

        // MASALAH KALA IV
        TMasalahKala4.setText("");
        TMasalahKala4Tindakan.setText("");
        TMasalahKala4Hasil.setText("");

        TTanggal.setDate(new Date());
        TG.requestFocus();
    }

    public void isCek() {
        // Auto-fill petugas jika user login adalah petugas
        if (akses.getjml2() >= 1) {
            TKodePetugas.setEditable(false);
            BtnCariPetugas.setEnabled(false);
            TKodePetugas.setText(akses.getkode());
            TNamaPetugas.setText(Sequel.CariPetugas(TKodePetugas.getText()));
            if (TNamaPetugas.getText().equals("")) {
                TKodePetugas.setText("");
            }
        }
    }


    private void getDataHalaman2() {
        if (tbHalaman2.getSelectedRow() != -1) {
            try {
                ps = koneksi.prepareStatement("SELECT * FROM partograf_halaman2 WHERE id=?");
                ps.setString(1, tbHalaman2.getValueAt(tbHalaman2.getSelectedRow(), 12).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TTglPersalinan.setDate(rs.getTimestamp("created_at"));

                    String rujukan = rs.getString("cp_merujuk_kala");
                    CKala1.setSelected(rujukan.contains("I"));
                    CKala2.setSelected(rujukan.contains("II"));
                    CKala3.setSelected(rujukan.contains("III"));
                    CKala4.setSelected(rujukan.contains("IV"));

                    TAlasanMerujuk.setText(rs.getString("cp_alasan_merujuk"));
                    TTempatRujukan.setText(rs.getString("cp_tempat_rujukan"));

                    String pendamping = rs.getString("cp_pendamping");
                    CPendampingBidan.setSelected(pendamping.contains("Bidan"));
                    CPendampingSuami.setSelected(pendamping.contains("Suami"));
                    CPendampingKeluarga.setSelected(pendamping.contains("Keluarga"));
                    CPendampingDukun.setSelected(pendamping.contains("Dukun"));
                    CPendampingTeman.setSelected(pendamping.contains("Teman"));
                    CPendampingTidakAda.setSelected(pendamping.contains("Tidak Ada"));

                    CKala1Waspada.setSelectedItem(rs.getString("k1_garis_waspada"));
                    TKala1Masalah.setText(rs.getString("k1_masalah"));
                    TKala1Tindakan.setText(rs.getString("k1_penatalaksanaan"));
                    TKala1Hasil.setText(rs.getString("k1_hasil"));

                    CKala2Episiotomi.setSelectedItem(rs.getString("k2_episiotomi"));
                    TKala2EpisiotomiAlasan.setText(rs.getString("k2_indikasi_episiotomi"));
                    CKala2GawatJanin.setSelectedItem(rs.getString("k2_gawat_janin"));
                    TKala2GawatJaninTindakan.setText(rs.getString("k2_tindakan_gawat_janin"));
                    CKala2Distosia.setSelectedItem(rs.getString("k2_distosia_bahu"));
                    TKala2DistosiaTindakan.setText(rs.getString("k2_tindakan_distosia_bahu"));
                    TKala2Masalah.setText(rs.getString("k2_masalah"));
                    TKala2Tindakan.setText(rs.getString("k2_penatalaksanaan"));
                    TKala2Hasil.setText(rs.getString("k2_hasil"));

                    TKala3Lama.setText(rs.getString("k3_lama"));
                    CKala3Oksitosin.setSelectedItem(rs.getString("k3_oksitosin"));
                    TKala3OksitosinAlasan.setText(rs.getString("k3_alasan_oksitosin"));
                    CKala3TaliPusat.setSelectedItem(rs.getString("k3_tali_pusat"));
                    TKala3TaliPusatAlasan.setText(rs.getString("k3_alasan_tali_pusat"));
                    CKala3Masase.setSelectedItem(rs.getString("k3_masase_fundus"));
                    TKala3MasaseAlasan.setText(rs.getString("k3_alasan_masase_fundus"));
                    CKala3PlasentaLengkap.setSelectedItem(rs.getString("k3_plasenta_lengkap"));
                    TKala3PlasentaTindakan.setText(rs.getString("k3_tindakan_plasenta_tidak_lengkap"));

                    TBBLBB.setText(rs.getString("bbl_bb"));
                    TBBLPB.setText(rs.getString("bbl_pb"));
                    CBBLJK.setSelectedItem(rs.getString("bbl_jk"));
                    CBBLPenilaian.setSelectedItem(rs.getString("bbl_penilaian"));
                    CBBLBayiLahir.setSelectedItem(rs.getString("bbl_lahir"));
                    TBBLNormalTindakan.setText(rs.getString("bbl_tindakan"));
                    TBBLMasalah.setText(rs.getString("bbl_masalah"));
                    TBBLHasil.setText(rs.getString("bbl_hasil"));
                    CBBLASI.setSelectedItem(rs.getString("bbl_asi"));
                    TBBLASITglJam.setText(rs.getString("bbl_waktu_asi"));

                    TMasalahKala4.setText(rs.getString("k4_masalah"));
                    TMasalahKala4Tindakan.setText(rs.getString("k4_penatalaksanaan"));
                    TMasalahKala4Hasil.setText(rs.getString("k4_hasil"));
                    
                    // Fetch Kala IV rows
                    Valid.tabelKosong(tabModeKala4);
                    ps2 = koneksi.prepareStatement("SELECT jam_ke, waktu, td, nadi, suhu, tfu, kontraksi, kandung_kemih, pendarahan, id FROM partograf_halaman2_kala4 WHERE no_rawat=? AND status='aktif' ORDER BY created_at");
                    ps2.setString(1, TNoRw.getText());
                    rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        tabModeKala4.addRow(new Object[]{
                            rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4),
                            rs2.getString(5), rs2.getString(6), rs2.getString(7), rs2.getString(8), rs2.getString(9), rs2.getString(10)
                        });
                    }
                }
            } catch (Exception e) {
                System.out.println("getDataHalaman2 : " + e);
            }
        }
    }

    private void tampilHistory() {
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
            return;
        }
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        try {
            javax.swing.JDialog dlg = new javax.swing.JDialog((java.awt.Frame) null, "Histori Partograf - " + TNoRw.getText(), true);
            dlg.setSize(this.getWidth(), this.getHeight());
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(new java.awt.BorderLayout());

            javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();

            // Color renderer berdasarkan kolom status
            class StatusRenderer extends javax.swing.table.DefaultTableCellRenderer {
                int statusCol;
                StatusRenderer(int statusCol) { this.statusCol = statusCol; }
                @Override
                public java.awt.Component getTableCellRendererComponent(javax.swing.JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                    java.awt.Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                    if (!sel) {
                        String st = tbl.getModel().getValueAt(row, statusCol) != null ? tbl.getModel().getValueAt(row, statusCol).toString() : "";
                        if ("revisi".equals(st)) c.setBackground(new java.awt.Color(255, 255, 180));
                        else if ("dibatalkan".equals(st)) c.setBackground(new java.awt.Color(255, 200, 200));
                        else c.setBackground(java.awt.Color.WHITE);
                    }
                    return c;
                }
            }

            // ===== Tab 1: Partograf Header =====
            String[] colP = {"No.Rawat","Tgl/Jam","G","P","A","Ketuban Pecah","Mules Sejak","NIP","Status","Dibuat Oleh","Dibuat","Diubah Oleh","Diubah","Keterangan"};
            int[] wP = {120,130,30,30,30,100,130,80,80,80,130,80,130,200};
            javax.swing.table.DefaultTableModel mP = new javax.swing.table.DefaultTableModel(null, colP) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            javax.swing.JTable tblP = new javax.swing.JTable(mP);
            tblP.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            for (int k = 0; k < wP.length; k++) tblP.getColumnModel().getColumn(k).setPreferredWidth(wP[k]);
            tblP.setDefaultRenderer(Object.class, new StatusRenderer(8));
            try {
                ps = koneksi.prepareStatement(
                    "SELECT no_rawat, tgl_jam, g, p, a, ketuban_pecah, mules_sejak, nip, status, created_by, created_at, updated_by, updated_at, keterangan_edit " +
                    "FROM partograf WHERE no_rawat=? ORDER BY tgl_jam DESC, created_at DESC");
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    mP.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)});
                }
            } catch (Exception e) { System.out.println("Histori Partograf: " + e); }
            finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }
            tabs.addTab("Partograf (" + mP.getRowCount() + ")", new javax.swing.JScrollPane(tblP));

            // ===== Tab 2: Observasi =====
            String[] colO = {"No.Rawat","Tgl/Jam","DJJ","Air Ketuban","Penyusupan","Pembukaan","Penurunan","Kontraksi Jml","Durasi","Sistole","Diastole","Nadi","Suhu","NIP","Status","Dibuat Oleh","Dibuat","Diubah Oleh","Diubah","Keterangan"};
            int[] wO = {120,130,40,70,70,65,65,70,60,50,55,40,40,80,80,80,130,80,130,200};
            javax.swing.table.DefaultTableModel mO = new javax.swing.table.DefaultTableModel(null, colO) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            javax.swing.JTable tblO = new javax.swing.JTable(mO);
            tblO.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            for (int k = 0; k < wO.length; k++) tblO.getColumnModel().getColumn(k).setPreferredWidth(wO[k]);
            tblO.setDefaultRenderer(Object.class, new StatusRenderer(14));
            try {
                ps = koneksi.prepareStatement(
                    "SELECT no_rawat, tgl_jam, djj, air_ketuban, penyusupan, pembukaan, penurunan, kontraksi_jml, kontraksi_durasi, sistole, diastole, nadi, suhu, nip, status, created_by, created_at, updated_by, updated_at, keterangan_edit " +
                    "FROM partograf_observasi WHERE no_rawat=? ORDER BY tgl_jam DESC, created_at DESC");
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    mO.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20)});
                }
            } catch (Exception e) { System.out.println("Histori Observasi: " + e); }
            finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }
            tabs.addTab("Observasi (" + mO.getRowCount() + ")", new javax.swing.JScrollPane(tblO));

            // ===== Tab 3: Halaman 2 =====
            String[] colH = {"No.Rawat","Rujukan Kala","Alasan","Tempat","Pendamping","K1 Waspada","K2 Episiotomi","K3 Lama","K3 Plasenta","BBL BB","BBL PB","BBL JK","K4 Masalah","Status","Dibuat Oleh","Dibuat","Diubah Oleh","Diubah","Keterangan"};
            int[] wH = {120,80,100,100,80,70,70,50,70,50,50,30,100,80,80,130,80,130,200};
            javax.swing.table.DefaultTableModel mH = new javax.swing.table.DefaultTableModel(null, colH) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            javax.swing.JTable tblH = new javax.swing.JTable(mH);
            tblH.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            for (int k = 0; k < wH.length; k++) tblH.getColumnModel().getColumn(k).setPreferredWidth(wH[k]);
            tblH.setDefaultRenderer(Object.class, new StatusRenderer(13));
            try {
                ps = koneksi.prepareStatement(
                    "SELECT no_rawat, cp_merujuk_kala, cp_alasan_merujuk, cp_tempat_rujukan, cp_pendamping, k1_garis_waspada, k2_episiotomi, k3_lama, k3_plasenta_lengkap, bbl_bb, bbl_pb, bbl_jk, k4_masalah, status, created_by, created_at, updated_by, updated_at, keterangan_edit " +
                    "FROM partograf_halaman2 WHERE no_rawat=? ORDER BY created_at DESC");
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    mH.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19)});
                }
            } catch (Exception e) { System.out.println("Histori Halaman2: " + e); }
            finally { try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }
            tabs.addTab("Halaman 2 (" + mH.getRowCount() + ")", new javax.swing.JScrollPane(tblH));

            // Bottom panel
            widget.Button btnTutup = new widget.Button();
            btnTutup.setText("Tutup");
            btnTutup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
            btnTutup.setPreferredSize(new java.awt.Dimension(100, 30));
            btnTutup.addActionListener(e -> dlg.dispose());
            javax.swing.JPanel pBottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
            pBottom.add(btnTutup);

            dlg.add(tabs, java.awt.BorderLayout.CENTER);
            dlg.add(pBottom, java.awt.BorderLayout.SOUTH);
            dlg.setVisible(true);
        } catch (Exception e) {
            System.out.println("tampilHistory: " + e);
        } finally {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                "SELECT r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, " +
                "TIMESTAMPDIFF(YEAR, p.tgl_lahir, CURDATE()) AS umur " +
                "FROM reg_periksa r INNER JOIN pasien p ON r.no_rkm_medis=p.no_rkm_medis " +
                "WHERE r.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TNamaPasien.setText(rs.getString("nm_pasien"));
                    TTglLahir.setText(rs.getString("tgl_lahir"));
                    TUmur.setText(rs.getString("umur") + " th");
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

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TTanggal.setDate(new Date());
        TMulesSejak.setDate(new Date());
        // Set filter tanggal: dari tanggal masuk rawat inap s.d. hari ini
        String tglMasuk = Sequel.cariIsi("select min(tgl_masuk) from kamar_inap where no_rawat=?", norwt);
        if (!tglMasuk.equals("")) {
            try {
                DTPCari1.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglMasuk));
            } catch (Exception e) {
                DTPCari1.setDate(new Date());
            }
        } else {
            DTPCari1.setDate(new Date());
        }
        DTPCari2.setDate(new Date());
        isRawat();
        tampil();
        tampil_observasi();
        tampil_halaman2();
    }

    // ===== Komponen =====
    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelGlass8;
    private widget.Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnGrafik, BtnHistori, BtnKeluar;
    private DlgPartografGrafik dlgGrafik;

    // Panel Atas
    private widget.panelisi panelAtas;
    private widget.Label lNoRw, lNoRM, lNamaPasien, lTglLahir, lUmur;
    private widget.Label lTanggal, lKodePetugas, lNamaPetugas;
    private widget.TextBox TNoRw, TNoRM, TNamaPasien, TTglLahir, TUmur;
    private widget.Tanggal TTanggal;
    private widget.TextBox TKodePetugas, TNamaPetugas;
    private widget.Button BtnCariPetugas;

    // Tab
    private javax.swing.JTabbedPane TabPartograf;

    // Tab 1
    private widget.panelisi panelTab1;
    private widget.ScrollPane scollTab1, ScrollPartograf;
    private widget.PanelBiasa formTab1;
    private widget.Table tbPartograf;
    private widget.Label lG, lP, lA, lKetubanPecah, lMulesSejak;
    private widget.TextBox TG, TP, TA, TKetubanPecah;
    private widget.Tanggal TMulesSejak;
    private javax.swing.JSeparator sep1;

    // Tab 2: Observasi
    private widget.panelisi panelTab2;
    private widget.ScrollPane scrollTab2;
    private widget.PanelBiasa formTab2;
    private widget.Label lDJJ, lAirKetuban, lPenyusupan, lPembukaan, lPenurunan, lKontraksiJml, lKontraksiDurasi, 
                         lSistole, lDiastole, lNadi, lSuhu, lOksitosinUnit, lOksitosinTetes, lObatCairan, lUrineVol, lUrineProt, lUrineAseton;
    private widget.TextBox TDJJ, TPembukaan, TPenurunan, TKontraksiJml, TSistole, TDiastole, TNadi, TSuhu, TOksitosinUnit, TOksitosinTetes, TObatCairan, TUrineVol, TUrineProt, TUrineAseton;
    private widget.ComboBox CAirKetuban, CPenyusupan, CKontraksiDurasi;
    private widget.Table tbObservasi;
    private widget.ScrollPane ScrollObservasi;

    // Halaman 2
    private widget.ScrollPane scrollHalaman2;
    private widget.PanelBiasa formHalaman2;
    private widget.Label lTglLahirAnak, lGenderAnak, lBBAnak, lPBAnak, lApgarAnak, lKala3, lPlacenta, lOxytocin, lMonitoring4, lKetPersalinan;
    private widget.Tanggal TTglLahirAnak, TKala3;
    private widget.TextBox TBBAnak, TPBAnak, TApgarAnak, TMonitoring4, TKetPersalinan;
    private widget.ComboBox CGenderAnak, CPlacenta, COxytocin;
    private javax.swing.JTabbedPane TabHalaman2;
    private widget.panelisi panelCatatan, panelKala1, panelKala2, panelKala3, panelBBL, panelKala4, panelMasalahKala4;

    private widget.Table tbHalaman2, tbCatatanPersalinan, tbKala1, tbKala2, tbKala3, tbBBL, tbMasalahKala4;
    private widget.ScrollPane scrollCatatanPersalinan, scrollKala1, scrollKala2, scrollKala3, scrollBBL, scrollMasalahKala4;
    private widget.ScrollPane ScrollObservasi_old;

    // Halaman 2: Catatan Persalinan (General)
    private widget.Label lTglPersalinan, lRujukan, lAlasanMerujuk, lTempatRujukan, lPendamping;
    private widget.Tanggal TTglPersalinan;
    private widget.TextBox TAlasanMerujuk, TTempatRujukan;
    private widget.CekBox CKala1, CKala2, CKala3, CKala4, CPendampingBidan, CPendampingKeluarga, CPendampingTeman, CPendampingSuami, CPendampingDukun, CPendampingTidakAda;

    // KALA I
    private widget.Label lKala1Waspada, lKala1Masalah, lKala1Tindakan, lKala1Hasil;
    private widget.ComboBox CKala1Waspada;
    private widget.TextBox TKala1Masalah, TKala1Tindakan, TKala1Hasil;

    // KALA II
    private widget.Label lKala2Episiotomi, lKala2EpisiotomiAlasan, lKala2Pendamping, lKala2GawatJanin, lKala2GawatJaninTindakan, lKala2Distosia, lKala2DistosiaTindakan, lKala2Masalah, lKala2Tindakan, lKala2Hasil;
    private widget.ComboBox CKala2Episiotomi, CKala2GawatJanin, CKala2Distosia;
    private widget.TextBox TKala2EpisiotomiAlasan, TKala2GawatJaninTindakan, TKala2DistosiaTindakan, TKala2Masalah, TKala2Tindakan, TKala2Hasil;
    private widget.CekBox CKala2PendampingSuami, CKala2PendampingKeluarga, CKala2PendampingTeman, CKala2PendampingDukun, CKala2PendampingTidakAda;

    // KALA III
    private widget.Label lKala3Lama, lKala3Oksitosin, lKala3TaliPusat, lKala3Masase, lKala3PlasentaLengkap, lKala3PlasentaTdkLahir, lKala3Laserasi, lKala3LaserasiDerajat, lKala3Atonia, lKala3Pendarahan, lKala3Masalah, lKala3Tindakan, lKala3Hasil;
    private widget.TextBox TKala3Lama, TKala3OksitosinAlasan, TKala3TaliPusatAlasan, TKala3MasaseAlasan, TKala3PlasentaTindakan, TKala3PlasentaTdkLahirTindakan, TKala3LaserasiLokasi, TKala3AtoniaTindakan, TKala3Pendarahan, TKala3Masalah, TKala3Tindakan, TKala3Hasil;
    private widget.ComboBox CKala3Oksitosin, CKala3TaliPusat, CKala3Masase, CKala3PlasentaLengkap, CKala3PlasentaTdkLahir, CKala3Laserasi, CKala3LaserasiDerajat, CKala3Atonia;

    // BAYI BARU LAHIR
    private widget.Label lBBLBB, lBBLPB, lBBLJK, lBBLPenilaian, lBBLBayiLahir, lBBLTindakan, lBBLWaktuASI, lBBLASI, lBBLMasalah, lBBLHasil;
    private widget.TextBox TBBLBB, TBBLPB, TBBLNormalTindakan, TBBLAsfiksiaTindakan, TBBLCacat, TBBLHipotermi, TBBLASITglJam, TBBLMasalah, TBBLHasil;
    private widget.ComboBox CBBLJK, CBBLPenilaian, CBBLBayiLahir, CBBLASI;

    // KALA IV
    private widget.Table tbKala4;
    private widget.ScrollPane ScrollKala4;
    private DefaultTableModel tabModeKala4;
    private widget.Label lKala4Jam, lKala4Waktu, lKala4TD, lKala4Nadi, lKala4Suhu, lKala4TFU, lKala4Kontraksi, lKala4Kandung, lKala4Pendarahan;
    private widget.TextBox TKala4Jam, TKala4Waktu, TKala4TD, TKala4Nadi, TKala4Suhu, TKala4TFU, TKala4Kontraksi, TKala4Kandung, TKala4Pendarahan;

    // MASALAH KALA IV
    private widget.Label lMasalahKala4, lMasalahKala4Tindakan, lMasalahKala4Hasil;
    private widget.TextBox TMasalahKala4, TMasalahKala4Tindakan, TMasalahKala4Hasil;

    // Search and Filter
    private widget.Label LKeyword, LFilterTanggal, LSampaiTanggal;
    private widget.TextBox TCari;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.Button BtnCari, BtnAll;
    private widget.panelisi panelGlass9;
}
