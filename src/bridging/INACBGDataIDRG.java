/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgPasienMati.java
 *
 * Created on Aug 30, 2010, 7:46:01 AM
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import org.json.JSONArray;
import org.json.JSONObject;
import simrskhanza.DlgPasien;

/**
 *
 * @author dosen3
 */
public class INACBGDataIDRG extends javax.swing.JDialog {

    private final DefaultTableModel tabModeDiagnosa, tabModeProsedur, tabModeDiagnosa2, tabModeProsedur2;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private DlgPasien pasien = new DlgPasien(null, false);
    private PreparedStatement ps, pskoders, psprosedur, psdiagnosa;
    private ResultSet rs, rskode, rs2, rs3, rsdiagnosa, rsprosedur, rsgrouper;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private String tgl, finger = "", lama = "", payor_id = "", payor_cd = "", coder = "", dokterkonsul = "", tgl_masuk_ranap = "", tgl_keluar_ranap = "", dpjp_ranap = "", inacbg_diagnosa = "", prosedur_inacbg = "", no_rawat_bayi = "",
            icd9 = "", icd10 = "", icd10INA = "", icd9INA = "";
    public ApiINACBG inacbg = new ApiINACBG();
    private int total_biaya = 0, i = 0, j = 0, jml = 0, index = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter formatter_tanggal = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private boolean isLoading = false;

    private String[] kode, nama, kode2, panjang, acc, asterisk, validcode;
    private boolean[] pilih;

    JTextField[] kd_diag;
    JTextField[] kd_diag2;
    JTextField[] nm_diag;
    JTextField[] nm_diag2;
    JTextField[] multiple;
    JTextField[] kd_psdr;
    JTextField[] kd_psdr2;
    JTextField[] nm_psdr;
    JTextField[] nm_psdr2;
    JCheckBox[] chk_diag;
    JCheckBox[] chk_diag2;
    JButton[] h_diag;
    JButton[] h_diag2;
    JButton[] h_psdr;
    JButton[] h_psdr2;

    /**
     * Creates new form DlgPasienMati
     *
     * @param parent
     * @param modal
     */
    public INACBGDataIDRG(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        pnb.setDocument(new batasInput((byte) 10).getOnlyAngka(pnb));
        rad.setDocument(new batasInput((byte) 10).getOnlyAngka(rad));
        rad.setDocument(new batasInput((byte) 10).getOnlyAngka(rad));
        rehab.setDocument(new batasInput((byte) 10).getOnlyAngka(rehab));
        obat.setDocument(new batasInput((byte) 10).getOnlyAngka(obat));
        alkes.setDocument(new batasInput((byte) 10).getOnlyAngka(alkes));
        pb.setDocument(new batasInput((byte) 10).getOnlyAngka(pb));
        kpr.setDocument(new batasInput((byte) 10).getOnlyAngka(kpr));
        lab.setDocument(new batasInput((byte) 10).getOnlyAngka(lab));
        akomodasi.setDocument(new batasInput((byte) 10).getOnlyAngka(akomodasi));
        kronis.setDocument(new batasInput((byte) 10).getOnlyAngka(kronis));
        bmhp.setDocument(new batasInput((byte) 10).getOnlyAngka(bmhp));
        ksl.setDocument(new batasInput((byte) 10).getOnlyAngka(ksl));
        pnj.setDocument(new batasInput((byte) 10).getOnlyAngka(pnj));
        pl_darah.setDocument(new batasInput((byte) 10).getOnlyAngka(pl_darah));
        ri.setDocument(new batasInput((byte) 10).getOnlyAngka(ri));
        obat_kemo.setDocument(new batasInput((byte) 10).getOnlyAngka(obat_kemo));
        sewa_alat.setDocument(new batasInput((byte) 10).getOnlyAngka(sewa_alat));
        multiple1.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple1));
        multiple2.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple2));
        multiple3.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple3));
        multiple4.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple4));
        multiple5.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple5));
        multiple6.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple6));
        multiple7.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple7));
        multiple8.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple8));
        multiple9.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple9));
        multiple10.setDocument(new batasInput((byte) 1).getOnlyAngka(multiple10));

        h_diag = new JButton[]{
            batal_1, batal_2, batal_3, batal_4, batal_5,
            batal_6, batal_7, batal_8, batal_9, batal_10
        };

        h_diag2 = new JButton[]{
            batal_11, batal_12, batal_13, batal_14, batal_15,
            batal_16, batal_17, batal_18, batal_19, batal_20
        };

        h_psdr = new JButton[]{
            batal_psdr_1, batal_psdr_2, batal_psdr_3, batal_psdr_4, batal_psdr_5,
            batal_psdr_6, batal_psdr_7, batal_psdr_8, batal_psdr_9, batal_psdr_10
        };

        h_psdr2 = new JButton[]{
            batal_psdr_11, batal_psdr_12, batal_psdr_13, batal_psdr_14, batal_psdr_15,
            batal_psdr_16, batal_psdr_17, batal_psdr_18, batal_psdr_19, batal_psdr_20
        };

        kd_diag = new JTextField[]{
            kd_diag_1, kd_diag_2, kd_diag_3, kd_diag_4, kd_diag_5,
            kd_diag_6, kd_diag_7, kd_diag_8, kd_diag_9, kd_diag_10
        };

        kd_diag2 = new JTextField[]{
            kd_diag_11, kd_diag_12, kd_diag_13, kd_diag_14, kd_diag_15,
            kd_diag_16, kd_diag_17, kd_diag_18, kd_diag_19, kd_diag_20
        };

        nm_diag = new JTextField[]{
            nm_diag_1, nm_diag_2, nm_diag_3, nm_diag_4, nm_diag_5,
            nm_diag_6, nm_diag_7, nm_diag_8, nm_diag_9, nm_diag_10
        };

        nm_diag2 = new JTextField[]{
            nm_diag_11, nm_diag_12, nm_diag_13, nm_diag_14, nm_diag_15,
            nm_diag_16, nm_diag_17, nm_diag_18, nm_diag_19, nm_diag_20
        };

        chk_diag = new JCheckBox[]{
            chk_diag_1, chk_diag_2, chk_diag_3, chk_diag_4, chk_diag_5,
            chk_diag_6, chk_diag_7, chk_diag_8, chk_diag_9, chk_diag_10
        };

        chk_diag2 = new JCheckBox[]{
            chk_diag_11, chk_diag_12, chk_diag_13, chk_diag_14, chk_diag_15,
            chk_diag_16, chk_diag_17, chk_diag_18, chk_diag_19, chk_diag_20
        };

        kd_psdr = new JTextField[]{
            kd_psdr_1, kd_psdr_2, kd_psdr_3, kd_psdr_4, kd_psdr_5,
            kd_psdr_6, kd_psdr_7, kd_psdr_8, kd_psdr_9, kd_psdr_10
        };

        kd_psdr2 = new JTextField[]{
            kd_psdr_11, kd_psdr_12, kd_psdr_13, kd_psdr_14, kd_psdr_15,
            kd_psdr_16, kd_psdr_17, kd_psdr_18, kd_psdr_19, kd_psdr_20
        };

        nm_psdr = new JTextField[]{
            nm_psdr_1, nm_psdr_2, nm_psdr_3, nm_psdr_4, nm_psdr_5,
            nm_psdr_6, nm_psdr_7, nm_psdr_8, nm_psdr_9, nm_psdr_10
        };

        nm_psdr2 = new JTextField[]{
            nm_psdr_11, nm_psdr_12, nm_psdr_13, nm_psdr_14, nm_psdr_15,
            nm_psdr_16, nm_psdr_17, nm_psdr_18, nm_psdr_19, nm_psdr_20
        };

        multiple = new JTextField[]{
            multiple1, multiple2, multiple3, multiple4, multiple5,
            multiple6, multiple7, multiple8, multiple9, multiple10
        };

        for (int i = 0; i < chk_diag.length; i++) {
            final int index = i;
            chk_diag[i].addActionListener(e -> {
                for (int j = 0; j < chk_diag.length; j++) {
                    if (j != index) {
                        chk_diag[j].setSelected(false);
                    }
                }

//                if (chk_diag[index].isSelected()) {
//                    cekDiagnosaUtama(index);
//                }
            });
        }

        for (int i = 0; i < chk_diag2.length; i++) {
            final int index = i;
            chk_diag2[i].addActionListener(e -> {
                for (int j = 0; j < chk_diag2.length; j++) {
                    if (j != index) {
                        chk_diag2[j].setSelected(false);
                    }
                }
            });
        }

        tabModeDiagnosa = new DefaultTableModel(null, new Object[]{
            "P", "Kode", "Nama Penyakit", "ACCPDX", "ASTERISK", "VALIDCODE"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbDiagnosa.setModel(tabModeDiagnosa);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbDiagnosa.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDiagnosa.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 6; i++) {
            TableColumn column = tbDiagnosa.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(40);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            } else if (i == 3) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 4) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 5) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbDiagnosa.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeDiagnosa2 = new DefaultTableModel(null, new Object[]{
            "P", "Kode", "Nama Penyakit"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbDiagnosa2.setModel(tabModeDiagnosa2);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbDiagnosa2.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbDiagnosa2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 3; i++) {
            TableColumn column = tbDiagnosa2.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(40);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            }
        }
        tbDiagnosa2.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeProsedur = new DefaultTableModel(null, new Object[]{
            "P", "Kode", "Deskripsi Panjang", "ACCPDX", "ASTERISK", "VALIDCODE"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbProsedur.setModel(tabModeProsedur);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbProsedur.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbProsedur.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 6; i++) {
            TableColumn column = tbProsedur.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(50);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            } else if (i == 3) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 4) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 5) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbProsedur.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeProsedur2 = new DefaultTableModel(null, new Object[]{
            "P", "Kode", "Deskripsi Panjang"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbProsedur2.setModel(tabModeProsedur2);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbProsedur2.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbProsedur2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbProsedur2.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(50);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            }
        }
        tbProsedur2.setDefaultRenderer(Object.class, new WarnaTable());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        no_rawat = new widget.TextBox();
        Scroll9 = new widget.ScrollPane();
        FormDATAKLAIM = new widget.panelisi();
        Panel1 = new javax.swing.JPanel();
        TPasien = new widget.TextBox();
        tgl_masuk = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel25 = new widget.Label();
        jLabel49 = new widget.Label();
        no_kartu = new widget.TextBox();
        cara_masuk = new widget.ComboBox();
        sistole = new widget.TextBox();
        jLabel53 = new widget.Label();
        jenis_rawat = new widget.ComboBox();
        cob = new widget.ComboBox();
        jaminan = new widget.ComboBox();
        jLabel9 = new widget.Label();
        jLabel58 = new widget.Label();
        jLabel61 = new widget.Label();
        jLabel66 = new widget.Label();
        jLabel68 = new widget.Label();
        hak_kelas = new widget.ComboBox();
        jLabel69 = new widget.Label();
        jLabel88 = new widget.Label();
        jam_masuk = new widget.ComboBox();
        menit_masuk = new widget.ComboBox();
        tgl_keluar = new widget.Tanggal();
        jLabel89 = new widget.Label();
        jam_keluar = new widget.ComboBox();
        menit_keluar = new widget.ComboBox();
        rawat_intensif = new javax.swing.JCheckBox();
        jLabel90 = new widget.Label();
        ventilator = new javax.swing.JCheckBox();
        jLabel91 = new widget.Label();
        naik_turun_kelas = new javax.swing.JCheckBox();
        naik_kelas_pelayanan = new widget.ComboBox();
        jLabel216 = new widget.Label();
        lama_naik_kelas = new widget.TextBox();
        jLabel217 = new widget.Label();
        jLabel218 = new widget.Label();
        jLabel219 = new widget.Label();
        chronic = new widget.TextBox();
        sub_acute = new widget.TextBox();
        jLabel220 = new widget.Label();
        berat_bayi = new widget.TextBox();
        jLabel221 = new widget.Label();
        dpjp = new widget.TextBox();
        jLabel222 = new widget.Label();
        validasi_tb = new widget.Button();
        status_pulang = new widget.ComboBox();
        jLabel223 = new widget.Label();
        kelas_eksekutif = new javax.swing.JCheckBox();
        tgl_intubasi = new widget.Tanggal();
        jam_intubasi = new widget.ComboBox();
        menit_intubasi = new widget.ComboBox();
        menit_ekstubasi = new widget.ComboBox();
        jam_ekstubasi = new widget.ComboBox();
        tgl_ekstubasi = new widget.Tanggal();
        jLabel224 = new widget.Label();
        hari_intensif = new widget.TextBox();
        jLabel225 = new widget.Label();
        jLabel226 = new widget.Label();
        distole = new widget.TextBox();
        tarif_eksekutif = new widget.TextBox();
        jLabel227 = new widget.Label();
        tgl_lahir = new widget.TextBox();
        jLabel228 = new widget.Label();
        jenis_kelamin = new widget.TextBox();
        jLabel229 = new widget.Label();
        jam_ventilator = new widget.TextBox();
        jLabel230 = new widget.Label();
        Tsep = new widget.TextBox();
        pasien_tb = new widget.TextBox();
        batal_validasi_tb = new widget.Button();
        pasien_hd = new widget.ComboBox();
        jLabel233 = new widget.Label();
        kt_darah = new widget.TextBox();
        jLabel234 = new widget.Label();
        check_hd = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        Scroll4 = new widget.ScrollPane();
        FormIDRG1 = new widget.panelisi();
        pnb = new widget.TextBox();
        ahli = new widget.TextBox();
        rehab = new widget.TextBox();
        rad = new widget.TextBox();
        alkes = new widget.TextBox();
        obat = new widget.TextBox();
        pb = new widget.TextBox();
        kpr = new widget.TextBox();
        akomodasi = new widget.TextBox();
        lab = new widget.TextBox();
        bmhp = new widget.TextBox();
        kronis = new widget.TextBox();
        ksl = new widget.TextBox();
        pnj = new widget.TextBox();
        ri = new widget.TextBox();
        pl_darah = new widget.TextBox();
        sewa_alat = new widget.TextBox();
        obat_kemo = new widget.TextBox();
        jLabel71 = new widget.Label();
        jLabel72 = new widget.Label();
        jLabel73 = new widget.Label();
        jLabel74 = new widget.Label();
        jLabel75 = new widget.Label();
        jLabel76 = new widget.Label();
        jLabel77 = new widget.Label();
        jLabel78 = new widget.Label();
        jLabel80 = new widget.Label();
        jLabel81 = new widget.Label();
        jLabel82 = new widget.Label();
        jLabel83 = new widget.Label();
        jLabel84 = new widget.Label();
        jLabel85 = new widget.Label();
        jLabel86 = new widget.Label();
        jLabel87 = new widget.Label();
        jLabel231 = new widget.Label();
        jLabel232 = new widget.Label();
        tarifRS = new widget.Label();
        lbltarifrs = new widget.Label();
        TabRawat = new javax.swing.JTabbedPane();
        FormIDRG = new widget.panelisi();
        jLabel111 = new widget.Label();
        nm_diag_1 = new widget.TextBox();
        jLabel121 = new widget.Label();
        kd_diag_1 = new widget.TextBox();
        nm_diag_2 = new widget.TextBox();
        kd_diag_2 = new widget.TextBox();
        nm_diag_3 = new widget.TextBox();
        kd_diag_3 = new widget.TextBox();
        nm_diag_4 = new widget.TextBox();
        kd_diag_4 = new widget.TextBox();
        nm_diag_5 = new widget.TextBox();
        kd_diag_5 = new widget.TextBox();
        nm_psdr_1 = new widget.TextBox();
        kd_psdr_1 = new widget.TextBox();
        nm_psdr_2 = new widget.TextBox();
        kd_psdr_2 = new widget.TextBox();
        nm_psdr_3 = new widget.TextBox();
        kd_psdr_3 = new widget.TextBox();
        nm_psdr_4 = new widget.TextBox();
        kd_psdr_4 = new widget.TextBox();
        nm_psdr_5 = new widget.TextBox();
        kd_psdr_5 = new widget.TextBox();
        chk_diag_1 = new javax.swing.JCheckBox();
        chk_diag_2 = new javax.swing.JCheckBox();
        chk_diag_3 = new javax.swing.JCheckBox();
        chk_diag_4 = new javax.swing.JCheckBox();
        chk_diag_5 = new javax.swing.JCheckBox();
        nm_diag_6 = new widget.TextBox();
        kd_diag_6 = new widget.TextBox();
        nm_diag_7 = new widget.TextBox();
        kd_diag_7 = new widget.TextBox();
        nm_diag_8 = new widget.TextBox();
        kd_diag_8 = new widget.TextBox();
        nm_diag_9 = new widget.TextBox();
        kd_diag_9 = new widget.TextBox();
        nm_diag_10 = new widget.TextBox();
        kd_diag_10 = new widget.TextBox();
        chk_diag_6 = new javax.swing.JCheckBox();
        chk_diag_7 = new javax.swing.JCheckBox();
        chk_diag_8 = new javax.swing.JCheckBox();
        chk_diag_9 = new javax.swing.JCheckBox();
        chk_diag_10 = new javax.swing.JCheckBox();
        nm_psdr_6 = new widget.TextBox();
        kd_psdr_6 = new widget.TextBox();
        nm_psdr_7 = new widget.TextBox();
        kd_psdr_7 = new widget.TextBox();
        nm_psdr_8 = new widget.TextBox();
        kd_psdr_8 = new widget.TextBox();
        nm_psdr_9 = new widget.TextBox();
        kd_psdr_9 = new widget.TextBox();
        nm_psdr_10 = new widget.TextBox();
        kd_psdr_10 = new widget.TextBox();
        multiple1 = new widget.TextBox();
        multiple2 = new widget.TextBox();
        multiple3 = new widget.TextBox();
        multiple4 = new widget.TextBox();
        multiple5 = new widget.TextBox();
        multiple6 = new widget.TextBox();
        multiple7 = new widget.TextBox();
        multiple8 = new widget.TextBox();
        multiple9 = new widget.TextBox();
        multiple10 = new widget.TextBox();
        batal_1 = new widget.Button();
        batal_2 = new widget.Button();
        batal_3 = new widget.Button();
        batal_4 = new widget.Button();
        batal_5 = new widget.Button();
        batal_6 = new widget.Button();
        batal_7 = new widget.Button();
        batal_8 = new widget.Button();
        batal_9 = new widget.Button();
        batal_10 = new widget.Button();
        batal_psdr_1 = new widget.Button();
        batal_psdr_2 = new widget.Button();
        batal_psdr_3 = new widget.Button();
        batal_psdr_4 = new widget.Button();
        batal_psdr_5 = new widget.Button();
        batal_psdr_6 = new widget.Button();
        batal_psdr_7 = new widget.Button();
        batal_psdr_8 = new widget.Button();
        batal_psdr_9 = new widget.Button();
        batal_psdr_10 = new widget.Button();
        jLabel92 = new widget.Label();
        Diagnosa = new widget.TextBox();
        idrgPenyakit = new widget.Button();
        idrgPenyakittambah = new widget.Button();
        Scroll5 = new widget.ScrollPane();
        tbDiagnosa = new widget.Table();
        Scroll6 = new widget.ScrollPane();
        tbProsedur = new widget.Table();
        jLabel93 = new widget.Label();
        Prosedur = new widget.TextBox();
        idrgProsedur = new widget.Button();
        idrgProseduttambah = new widget.Button();
        responIDRG = new widget.Label();
        jLabel127 = new widget.Label();
        jLabel129 = new widget.Label();
        FormINACBG = new widget.panelisi();
        jLabel94 = new widget.Label();
        DiagnosaINACBG = new widget.TextBox();
        INAdiagnosa = new widget.Button();
        INAdiagnosatambah = new widget.Button();
        Scroll7 = new widget.ScrollPane();
        tbDiagnosa2 = new widget.Table();
        Scroll8 = new widget.ScrollPane();
        tbProsedur2 = new widget.Table();
        jLabel95 = new widget.Label();
        ProsedurINACBG = new widget.TextBox();
        INAprosedur = new widget.Button();
        INAprosedurtambah = new widget.Button();
        jLabel183 = new widget.Label();
        jLabel113 = new widget.Label();
        nm_diag_11 = new widget.TextBox();
        jLabel125 = new widget.Label();
        kd_diag_11 = new widget.TextBox();
        nm_diag_12 = new widget.TextBox();
        kd_diag_12 = new widget.TextBox();
        nm_diag_13 = new widget.TextBox();
        kd_diag_13 = new widget.TextBox();
        nm_diag_14 = new widget.TextBox();
        kd_diag_14 = new widget.TextBox();
        nm_diag_15 = new widget.TextBox();
        kd_diag_15 = new widget.TextBox();
        nm_psdr_11 = new widget.TextBox();
        kd_psdr_11 = new widget.TextBox();
        nm_psdr_12 = new widget.TextBox();
        kd_psdr_12 = new widget.TextBox();
        nm_psdr_13 = new widget.TextBox();
        kd_psdr_13 = new widget.TextBox();
        nm_psdr_14 = new widget.TextBox();
        kd_psdr_14 = new widget.TextBox();
        nm_psdr_15 = new widget.TextBox();
        kd_psdr_15 = new widget.TextBox();
        chk_diag_11 = new javax.swing.JCheckBox();
        chk_diag_12 = new javax.swing.JCheckBox();
        chk_diag_13 = new javax.swing.JCheckBox();
        chk_diag_14 = new javax.swing.JCheckBox();
        chk_diag_15 = new javax.swing.JCheckBox();
        jLabel126 = new widget.Label();
        nm_diag_16 = new widget.TextBox();
        kd_diag_16 = new widget.TextBox();
        nm_diag_17 = new widget.TextBox();
        kd_diag_17 = new widget.TextBox();
        nm_diag_18 = new widget.TextBox();
        kd_diag_18 = new widget.TextBox();
        nm_diag_19 = new widget.TextBox();
        kd_diag_19 = new widget.TextBox();
        nm_diag_20 = new widget.TextBox();
        kd_diag_20 = new widget.TextBox();
        chk_diag_16 = new javax.swing.JCheckBox();
        chk_diag_17 = new javax.swing.JCheckBox();
        chk_diag_18 = new javax.swing.JCheckBox();
        chk_diag_19 = new javax.swing.JCheckBox();
        chk_diag_20 = new javax.swing.JCheckBox();
        nm_psdr_16 = new widget.TextBox();
        kd_psdr_16 = new widget.TextBox();
        nm_psdr_17 = new widget.TextBox();
        kd_psdr_17 = new widget.TextBox();
        nm_psdr_18 = new widget.TextBox();
        kd_psdr_18 = new widget.TextBox();
        nm_psdr_19 = new widget.TextBox();
        kd_psdr_19 = new widget.TextBox();
        nm_psdr_20 = new widget.TextBox();
        kd_psdr_20 = new widget.TextBox();
        batal_11 = new widget.Button();
        batal_12 = new widget.Button();
        batal_13 = new widget.Button();
        batal_14 = new widget.Button();
        batal_15 = new widget.Button();
        batal_16 = new widget.Button();
        batal_17 = new widget.Button();
        batal_18 = new widget.Button();
        batal_19 = new widget.Button();
        batal_20 = new widget.Button();
        batal_psdr_11 = new widget.Button();
        batal_psdr_12 = new widget.Button();
        batal_psdr_13 = new widget.Button();
        batal_psdr_14 = new widget.Button();
        batal_psdr_15 = new widget.Button();
        batal_psdr_16 = new widget.Button();
        batal_psdr_17 = new widget.Button();
        batal_psdr_18 = new widget.Button();
        batal_psdr_19 = new widget.Button();
        batal_psdr_20 = new widget.Button();
        Import = new widget.Button();
        scrollRespon1 = new widget.ScrollPane();
        panelGlass9 = new widget.panelisi();
        cmbDrugs = new widget.ComboBox();
        jLabel96 = new widget.Label();
        jLabel97 = new widget.Label();
        jLabel98 = new widget.Label();
        jLabel99 = new widget.Label();
        jLabel100 = new widget.Label();
        jLabel101 = new widget.Label();
        jLabel102 = new widget.Label();
        jLabel103 = new widget.Label();
        jLabel104 = new widget.Label();
        lChronic = new widget.Label();
        jLabel105 = new widget.Label();
        cmbProc = new widget.ComboBox();
        cmbPros = new widget.ComboBox();
        cmbInv = new widget.ComboBox();
        lSubacute = new widget.Label();
        jLabel107 = new widget.Label();
        jLabel108 = new widget.Label();
        jLabel109 = new widget.Label();
        jLabel110 = new widget.Label();
        jLabel112 = new widget.Label();
        jLabel114 = new widget.Label();
        jLabel115 = new widget.Label();
        lblTarifKronis = new widget.Label();
        lblTarifGrup = new widget.Label();
        lblTarifAcute = new widget.Label();
        lbltarifProc = new widget.Label();
        lblTarifPros = new widget.Label();
        lblTarifDrugs = new widget.Label();
        lblTarifInv = new widget.Label();
        lblTarifTotal = new widget.Label();
        jSeparator16 = new javax.swing.JSeparator();
        jSeparator17 = new javax.swing.JSeparator();
        jSeparator18 = new javax.swing.JSeparator();
        jSeparator19 = new javax.swing.JSeparator();
        jSeparator20 = new javax.swing.JSeparator();
        jSeparator21 = new javax.swing.JSeparator();
        jSeparator22 = new javax.swing.JSeparator();
        jSeparator23 = new javax.swing.JSeparator();
        jSeparator24 = new javax.swing.JSeparator();
        jSeparator25 = new javax.swing.JSeparator();
        grupCode = new widget.Label();
        jSeparator26 = new javax.swing.JSeparator();
        jLabel117 = new widget.Label();
        lblTarifRS = new widget.Label();
        jLabel118 = new widget.Label();
        jLabel119 = new widget.Label();
        lblBaseTariff = new widget.Label();
        jSeparator27 = new javax.swing.JSeparator();
        scrollPane1 = new widget.ScrollPane();
        Txtgroup = new widget.TextArea();
        jLabel120 = new widget.Label();
        jLabel122 = new widget.Label();
        lblSelisih = new widget.Label();
        jSeparator28 = new javax.swing.JSeparator();
        lJenis1 = new widget.Label();
        cmbSelisih = new widget.ComboBox();
        jLabel116 = new widget.Label();
        panelBawah = new widget.panelisi();
        hapusKlaim4 = new widget.Button();
        BtnGrouperIDRG = new widget.Button();
        finalklaimIDRG = new widget.Button();
        editKlaimIDRG = new widget.Button();
        BtnGrouperINA = new widget.Button();
        finalINA = new widget.Button();
        editINA = new widget.Button();
        finalklaim = new widget.Button();
        editKlaim = new widget.Button();
        kirimonline = new widget.Button();
        CetakKlaim = new widget.Button();
        BtnKeluar4 = new widget.Button();

        no_rawat.setHighlighter(null);
        no_rawat.setName("no_rawat"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        Scroll9.setName("Scroll9"); // NOI18N
        Scroll9.setOpaque(true);
        Scroll9.setPreferredSize(new java.awt.Dimension(500, 770));

        FormDATAKLAIM.setName("FormDATAKLAIM"); // NOI18N
        FormDATAKLAIM.setPreferredSize(new java.awt.Dimension(1000, 1000));
        FormDATAKLAIM.setLayout(new java.awt.BorderLayout());

        Panel1.setBackground(new java.awt.Color(255, 255, 255));
        Panel1.setName("Panel1"); // NOI18N
        Panel1.setPreferredSize(new java.awt.Dimension(1300, 260));
        Panel1.setLayout(null);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        Panel1.add(TPasien);
        TPasien.setBounds(225, 10, 230, 23);

        tgl_masuk.setEditable(false);
        tgl_masuk.setForeground(new java.awt.Color(50, 70, 50));
        tgl_masuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2025-10-19" }));
        tgl_masuk.setDisplayFormat("yyyy-MM-dd");
        tgl_masuk.setName("tgl_masuk"); // NOI18N
        tgl_masuk.setOpaque(false);
        Panel1.add(tgl_masuk);
        tgl_masuk.setBounds(130, 100, 100, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        Panel1.add(TNoRM);
        TNoRM.setBounds(130, 10, 90, 23);

        jLabel25.setText("Distole : ");
        jLabel25.setName("jLabel25"); // NOI18N
        Panel1.add(jLabel25);
        jLabel25.setBounds(710, 70, 60, 23);

        jLabel49.setText("Intubasi :");
        jLabel49.setName("jLabel49"); // NOI18N
        Panel1.add(jLabel49);
        jLabel49.setBounds(570, 190, 70, 23);

        no_kartu.setEditable(false);
        no_kartu.setHighlighter(null);
        no_kartu.setName("no_kartu"); // NOI18N
        Panel1.add(no_kartu);
        no_kartu.setBounds(500, 10, 120, 23);

        cara_masuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "gp : Rujukan FKTP", "hosp-trans : Rujukan FKRTL", "mp : Rujukan Spesialis", "outp : Dari Rawat Jalan", "inp : Dari Rawat Inap", "emd : Dari Rawat Darurat", "born : Lahir di RS", "nursing : Rujukan Panti Jompo", "psych : Rujukan dari RS Jiwa", "rehab : Rujukan Fasilitas Rehab", "other : Lain-lain" }));
        cara_masuk.setName("cara_masuk"); // NOI18N
        Panel1.add(cara_masuk);
        cara_masuk.setBounds(750, 40, 260, 23);

        sistole.setHighlighter(null);
        sistole.setName("sistole"); // NOI18N
        sistole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sistoleActionPerformed(evt);
            }
        });
        Panel1.add(sistole);
        sistole.setBounds(670, 70, 50, 23);

        jLabel53.setText("Pasien TB : ");
        jLabel53.setName("jLabel53"); // NOI18N
        Panel1.add(jLabel53);
        jLabel53.setBounds(60, 190, 70, 23);

        jenis_rawat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 : RAWAT INAP", "2 : RAWAT JALAN", "3 : IGD" }));
        jenis_rawat.setSelectedIndex(1);
        jenis_rawat.setName("jenis_rawat"); // NOI18N
        jenis_rawat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jenis_rawatItemStateChanged(evt);
            }
        });
        Panel1.add(jenis_rawat);
        jenis_rawat.setBounds(130, 70, 150, 23);

        cob.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "# : TIDAK COB", "0001 : MANDIRI INHEALTH", "0005 : ASURANSI SINAR MAS", "0006 : ASURANSI TUGU MANDIRI", "0007 : ASURANSI MITRA MAPARYA", "0008 : ASURANSI AXA MANDIRI FINANSIAL SERVICE", "0009 : ASURANSI AXA FINANSIAL INDONESIA", "0010 : LIPPO GENERAL INSURANCE", "0011 : ARTHAGRAHA GENERAL INSURANSE", "0012 : TUGU PRATAMA INDONESIA", "0013 : ASURANSI BINA DANA ARTA", "0014 : ASURANSI JIWA SINAR MAS MSIG", "0015 : AVRIST ASSURANCE", "0016 : ASURANSI JIWA SRAYA", "0017 : ASURANSI JIWA CENTRAL ASIA RAYA", "0018 : ASURANSI TAKAFUL KELUARGA", "0019 : ASURANSI JIWA GENERALI INDONESIA", "0020 : ASURANSI ASTRA BUANA", "0021 : ASURANSI UMUM MEGA", "0022 : ASURANSI MULTI ARTHA GUNA", "0023 : ASURANSI AIA INDONESIA", "0024 : ASURANSI JIWA EQUITY LIFE INDONESIA", "0025 : ASURANSI JIWA RECAPITAL", "0026 : GREAT EASTERN LIFE INDONESIA", "0027 : ASURANSI ADISARANA WANAARTHA", "0028 : ASURANSI JIWA BRINGIN JIWA SEJAHTERA", "0029 : BOSOWA ASURANSI", "0030 : MNC LIFE ASSURANCE", "0031 : ASURANSI AVIVA INDONESIA", "0032 : ASURANSI CENTRAL ASIA RAYA", "0033 : ASURANSI ALLIANZ LIFE INDONESIA", "0034 : ASURANSI BINTANG", "0035 : TOKIO MARINE LIFE INSURANCE INDONESIA", "0036 : MALACCA TRUST WUWUNGAN", "0037 : ASURANSI JASA INDONESIA", "0038 : ASURANSI JIWA MANULIFE INDONESIA", "0039 : ASURANSI BANGUN ASKRIDA", "0040 : ASURANSI JIWA SEQUIS FINANCIAL", "0041 : ASURANSI AXA INDONESIA", "0042 : BNI LIFE", "0043 : ACE LIFE INSURANCE", "0044 : CITRA INTERNATIONAL UNDERWRITERS", "0045 : ASURANSI RELIANCE INDONESIA", "0046 : HANWHA LIFE INSURANCE INDONESIA", "0047 : ASURANSI DAYIN MITRA", "0048 : ASURANSI ADIRA DINAMIKA", "0049 : PAN PASIFIC INSURANCE", "0050 : ASURANSI SAMSUNG TUGU", "0051 : ASURANSI UMUM BUMI PUTERA MUDA 1967", "0052 : ASURANSI JIWA KRESNA", "0053 : ASURANSI RAMAYANA", "0054 : VICTORIA INSURANCE", "0055 : ASURANSI JIWA BERSAMA BUMIPUTERA 1912", "0056 : FWD LIFE INDONESIA", "0057 : ASURANSI TAKAFUL KELUARGA", "0058 : ASURANSI TUGU KRESNA PRATAMA", "0059 : SOMPO INSURANCE" }));
        cob.setName("cob"); // NOI18N
        Panel1.add(cob);
        cob.setBounds(750, 10, 260, 23);

        jaminan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3 : JKN ", "71 : JAMINAN COVID-19", "72 : JAMINAN KIPI", "73 : JAMINAN BAYI BARU LAHIR", "74 : JAMINAN PERPANJANGAN MASA RAWAT", "75 : JAMINAN CO-INSIDENSE", "76 : JAMPERSAL", "77 : JAMINAN PEMULIHAN KESEHATAN PRIORITAS" }));
        jaminan.setName("jaminan"); // NOI18N
        Panel1.add(jaminan);
        jaminan.setBounds(130, 40, 200, 23);

        jLabel9.setText("No.Rekam Medik :");
        jLabel9.setName("jLabel9"); // NOI18N
        Panel1.add(jLabel9);
        jLabel9.setBounds(10, 10, 115, 23);

        jLabel58.setText("Jaminan :");
        jLabel58.setName("jLabel58"); // NOI18N
        Panel1.add(jLabel58);
        jLabel58.setBounds(10, 40, 115, 23);

        jLabel61.setText("COB :");
        jLabel61.setName("jLabel61"); // NOI18N
        Panel1.add(jLabel61);
        jLabel61.setBounds(710, 10, 30, 23);

        jLabel66.setText("Kartu : ");
        jLabel66.setName("jLabel66"); // NOI18N
        Panel1.add(jLabel66);
        jLabel66.setBounds(410, 10, 90, 23);

        jLabel68.setText("Tgl Masuk :");
        jLabel68.setName("jLabel68"); // NOI18N
        Panel1.add(jLabel68);
        jLabel68.setBounds(10, 100, 115, 23);

        hak_kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kelas 1", "Kelas 2", "Kelas 3" }));
        hak_kelas.setSelectedIndex(2);
        hak_kelas.setName("hak_kelas"); // NOI18N
        Panel1.add(hak_kelas);
        hak_kelas.setBounds(890, 70, 120, 23);

        jLabel69.setText("Hak Kelas :");
        jLabel69.setName("jLabel69"); // NOI18N
        Panel1.add(jLabel69);
        jLabel69.setBounds(770, 70, 115, 23);

        jLabel88.setText("Jenis Rawat :");
        jLabel88.setName("jLabel88"); // NOI18N
        Panel1.add(jLabel88);
        jLabel88.setBounds(10, 70, 115, 23);

        jam_masuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        jam_masuk.setName("jam_masuk"); // NOI18N
        Panel1.add(jam_masuk);
        jam_masuk.setBounds(240, 100, 62, 23);

        menit_masuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        menit_masuk.setName("menit_masuk"); // NOI18N
        Panel1.add(menit_masuk);
        menit_masuk.setBounds(300, 100, 62, 23);

        tgl_keluar.setEditable(false);
        tgl_keluar.setForeground(new java.awt.Color(50, 70, 50));
        tgl_keluar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2025-10-19" }));
        tgl_keluar.setDisplayFormat("yyyy-MM-dd");
        tgl_keluar.setName("tgl_keluar"); // NOI18N
        tgl_keluar.setOpaque(false);
        Panel1.add(tgl_keluar);
        tgl_keluar.setBounds(430, 100, 100, 23);

        jLabel89.setText("Tgl Keluar :");
        jLabel89.setName("jLabel89"); // NOI18N
        Panel1.add(jLabel89);
        jLabel89.setBounds(310, 100, 115, 23);

        jam_keluar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        jam_keluar.setName("jam_keluar"); // NOI18N
        Panel1.add(jam_keluar);
        jam_keluar.setBounds(540, 100, 62, 23);

        menit_keluar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        menit_keluar.setName("menit_keluar"); // NOI18N
        Panel1.add(menit_keluar);
        menit_keluar.setBounds(600, 100, 62, 23);

        rawat_intensif.setBackground(new java.awt.Color(255, 255, 255));
        rawat_intensif.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        rawat_intensif.setText("Ada Rawat Intensif");
        rawat_intensif.setName("rawat_intensif"); // NOI18N
        rawat_intensif.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rawat_intensifItemStateChanged(evt);
            }
        });
        Panel1.add(rawat_intensif);
        rawat_intensif.setBounds(470, 70, 140, 22);

        jLabel90.setText("ADL Score :");
        jLabel90.setName("jLabel90"); // NOI18N
        Panel1.add(jLabel90);
        jLabel90.setBounds(10, 160, 115, 23);

        ventilator.setBackground(new java.awt.Color(255, 255, 255));
        ventilator.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        ventilator.setText("Ventilator");
        ventilator.setName("ventilator"); // NOI18N
        ventilator.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ventilatorItemStateChanged(evt);
            }
        });
        Panel1.add(ventilator);
        ventilator.setBounds(490, 190, 90, 22);

        jLabel91.setText("SEP : ");
        jLabel91.setName("jLabel91"); // NOI18N
        Panel1.add(jLabel91);
        jLabel91.setBounds(300, 40, 70, 23);

        naik_turun_kelas.setBackground(new java.awt.Color(255, 255, 255));
        naik_turun_kelas.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        naik_turun_kelas.setText("Naik / Turun Kelas");
        naik_turun_kelas.setName("naik_turun_kelas"); // NOI18N
        naik_turun_kelas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                naik_turun_kelasItemStateChanged(evt);
            }
        });
        Panel1.add(naik_turun_kelas);
        naik_turun_kelas.setBounds(320, 70, 140, 22);

        naik_kelas_pelayanan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "kelas_3 : Kelas 3", "kelas_2 : Kelas 2", "kelas_1 : Kelas 1", "vip : Diatas Kelas 1" }));
        naik_kelas_pelayanan.setName("naik_kelas_pelayanan"); // NOI18N
        Panel1.add(naik_kelas_pelayanan);
        naik_kelas_pelayanan.setBounds(890, 100, 120, 23);

        jLabel216.setText("Kelas Pelayanan : ");
        jLabel216.setName("jLabel216"); // NOI18N
        Panel1.add(jLabel216);
        jLabel216.setBounds(790, 100, 100, 23);

        lama_naik_kelas.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        lama_naik_kelas.setHighlighter(null);
        lama_naik_kelas.setName("lama_naik_kelas"); // NOI18N
        Panel1.add(lama_naik_kelas);
        lama_naik_kelas.setBounds(740, 100, 50, 23);

        jLabel217.setText("Lama (hari) : ");
        jLabel217.setName("jLabel217"); // NOI18N
        Panel1.add(jLabel217);
        jLabel217.setBounds(670, 100, 70, 23);

        jLabel218.setText("Chronic : ");
        jLabel218.setName("jLabel218"); // NOI18N
        Panel1.add(jLabel218);
        jLabel218.setBounds(240, 160, 90, 23);

        jLabel219.setText("Gram");
        jLabel219.setName("jLabel219"); // NOI18N
        Panel1.add(jLabel219);
        jLabel219.setBounds(220, 130, 30, 23);

        chronic.setText("-");
        chronic.setHighlighter(null);
        chronic.setName("chronic"); // NOI18N
        Panel1.add(chronic);
        chronic.setBounds(330, 160, 60, 23);

        sub_acute.setText("-");
        sub_acute.setHighlighter(null);
        sub_acute.setName("sub_acute"); // NOI18N
        Panel1.add(sub_acute);
        sub_acute.setBounds(200, 160, 60, 23);

        jLabel220.setText("Sub Acute : ");
        jLabel220.setName("jLabel220"); // NOI18N
        Panel1.add(jLabel220);
        jLabel220.setBounds(110, 160, 90, 23);

        berat_bayi.setHighlighter(null);
        berat_bayi.setName("berat_bayi"); // NOI18N
        Panel1.add(berat_bayi);
        berat_bayi.setBounds(130, 130, 90, 23);

        jLabel221.setText("Berat Lahir Bayi : ");
        jLabel221.setName("jLabel221"); // NOI18N
        Panel1.add(jLabel221);
        jLabel221.setBounds(40, 130, 90, 23);

        dpjp.setHighlighter(null);
        dpjp.setName("dpjp"); // NOI18N
        Panel1.add(dpjp);
        dpjp.setBounds(300, 130, 310, 23);

        jLabel222.setText("DPJP : ");
        jLabel222.setName("jLabel222"); // NOI18N
        Panel1.add(jLabel222);
        jLabel222.setBounds(190, 130, 110, 23);

        validasi_tb.setText("Validasi");
        validasi_tb.setToolTipText("");
        validasi_tb.setGlassColor(new java.awt.Color(0, 255, 153));
        validasi_tb.setName("validasi_tb"); // NOI18N
        validasi_tb.setPreferredSize(new java.awt.Dimension(100, 30));
        validasi_tb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validasi_tbActionPerformed(evt);
            }
        });
        Panel1.add(validasi_tb);
        validasi_tb.setBounds(320, 190, 70, 22);

        status_pulang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 : Atas persetujuan dokter", "2 : Dirujuk", "3 : Atas permintaan sendiri", "4 : Meninggal", "5 : Lain-lain" }));
        status_pulang.setName("status_pulang"); // NOI18N
        Panel1.add(status_pulang);
        status_pulang.setBounds(450, 160, 170, 23);

        jLabel223.setText("Pulang : ");
        jLabel223.setName("jLabel223"); // NOI18N
        Panel1.add(jLabel223);
        jLabel223.setBounds(380, 160, 70, 23);

        kelas_eksekutif.setBackground(new java.awt.Color(255, 255, 255));
        kelas_eksekutif.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        kelas_eksekutif.setText("Kelas Eksekutif");
        kelas_eksekutif.setName("kelas_eksekutif"); // NOI18N
        kelas_eksekutif.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                kelas_eksekutifItemStateChanged(evt);
            }
        });
        Panel1.add(kelas_eksekutif);
        kelas_eksekutif.setBounds(320, 70, 140, 22);

        tgl_intubasi.setEditable(false);
        tgl_intubasi.setForeground(new java.awt.Color(50, 70, 50));
        tgl_intubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2025-10-19" }));
        tgl_intubasi.setDisplayFormat("yyyy-MM-dd");
        tgl_intubasi.setName("tgl_intubasi"); // NOI18N
        tgl_intubasi.setOpaque(false);
        Panel1.add(tgl_intubasi);
        tgl_intubasi.setBounds(650, 190, 100, 23);

        jam_intubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        jam_intubasi.setName("jam_intubasi"); // NOI18N
        Panel1.add(jam_intubasi);
        jam_intubasi.setBounds(760, 190, 62, 23);

        menit_intubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        menit_intubasi.setName("menit_intubasi"); // NOI18N
        Panel1.add(menit_intubasi);
        menit_intubasi.setBounds(820, 190, 62, 23);

        menit_ekstubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        menit_ekstubasi.setName("menit_ekstubasi"); // NOI18N
        Panel1.add(menit_ekstubasi);
        menit_ekstubasi.setBounds(820, 220, 62, 23);

        jam_ekstubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        jam_ekstubasi.setName("jam_ekstubasi"); // NOI18N
        Panel1.add(jam_ekstubasi);
        jam_ekstubasi.setBounds(760, 220, 62, 23);

        tgl_ekstubasi.setEditable(false);
        tgl_ekstubasi.setForeground(new java.awt.Color(50, 70, 50));
        tgl_ekstubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2025-10-19" }));
        tgl_ekstubasi.setDisplayFormat("yyyy-MM-dd");
        tgl_ekstubasi.setName("tgl_ekstubasi"); // NOI18N
        tgl_ekstubasi.setOpaque(false);
        Panel1.add(tgl_ekstubasi);
        tgl_ekstubasi.setBounds(650, 220, 100, 23);

        jLabel224.setText("Ekstubasi :");
        jLabel224.setName("jLabel224"); // NOI18N
        Panel1.add(jLabel224);
        jLabel224.setBounds(570, 220, 70, 23);

        hari_intensif.setEditable(false);
        hari_intensif.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        hari_intensif.setHighlighter(null);
        hari_intensif.setName("hari_intensif"); // NOI18N
        Panel1.add(hari_intensif);
        hari_intensif.setBounds(530, 220, 50, 23);

        jLabel225.setText(" Rawat Intensif (hari) : ");
        jLabel225.setName("jLabel225"); // NOI18N
        Panel1.add(jLabel225);
        jLabel225.setBounds(410, 220, 120, 23);

        jLabel226.setText("Sistole : ");
        jLabel226.setName("jLabel226"); // NOI18N
        Panel1.add(jLabel226);
        jLabel226.setBounds(620, 70, 50, 23);

        distole.setHighlighter(null);
        distole.setName("distole"); // NOI18N
        Panel1.add(distole);
        distole.setBounds(770, 70, 50, 23);

        tarif_eksekutif.setHighlighter(null);
        tarif_eksekutif.setName("tarif_eksekutif"); // NOI18N
        Panel1.add(tarif_eksekutif);
        tarif_eksekutif.setBounds(700, 130, 100, 23);

        jLabel227.setText("Tarif Eksekutif : ");
        jLabel227.setName("jLabel227"); // NOI18N
        Panel1.add(jLabel227);
        jLabel227.setBounds(610, 130, 90, 23);

        tgl_lahir.setEditable(false);
        tgl_lahir.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        tgl_lahir.setEnabled(false);
        tgl_lahir.setHighlighter(null);
        tgl_lahir.setName("tgl_lahir"); // NOI18N
        Panel1.add(tgl_lahir);
        tgl_lahir.setBounds(625, 10, 80, 23);

        jLabel228.setText("Gender : ");
        jLabel228.setName("jLabel228"); // NOI18N
        Panel1.add(jLabel228);
        jLabel228.setBounds(530, 40, 70, 23);

        jenis_kelamin.setEditable(false);
        jenis_kelamin.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jenis_kelamin.setEnabled(false);
        jenis_kelamin.setHighlighter(null);
        jenis_kelamin.setName("jenis_kelamin"); // NOI18N
        Panel1.add(jenis_kelamin);
        jenis_kelamin.setBounds(600, 40, 50, 23);

        jLabel229.setText("Cara Masuk :");
        jLabel229.setName("jLabel229"); // NOI18N
        Panel1.add(jLabel229);
        jLabel229.setBounds(670, 40, 70, 23);

        jam_ventilator.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jam_ventilator.setHighlighter(null);
        jam_ventilator.setName("jam_ventilator"); // NOI18N
        Panel1.add(jam_ventilator);
        jam_ventilator.setBounds(960, 190, 50, 23);

        jLabel230.setText("Berapa Jam  : ");
        jLabel230.setName("jLabel230"); // NOI18N
        Panel1.add(jLabel230);
        jLabel230.setBounds(880, 190, 80, 23);

        Tsep.setHighlighter(null);
        Tsep.setName("Tsep"); // NOI18N
        Tsep.setOpaque(true);
        Panel1.add(Tsep);
        Tsep.setBounds(370, 40, 180, 23);

        pasien_tb.setHighlighter(null);
        pasien_tb.setName("pasien_tb"); // NOI18N
        Panel1.add(pasien_tb);
        pasien_tb.setBounds(130, 190, 190, 23);

        batal_validasi_tb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_validasi_tb.setToolTipText("");
        batal_validasi_tb.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_validasi_tb.setName("batal_validasi_tb"); // NOI18N
        batal_validasi_tb.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_validasi_tb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_validasi_tbActionPerformed(evt);
            }
        });
        Panel1.add(batal_validasi_tb);
        batal_validasi_tb.setBounds(390, 190, 40, 22);

        pasien_hd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MULTIPLE USE", "SINGLE USE" }));
        pasien_hd.setSelectedIndex(1);
        pasien_hd.setName("pasien_hd"); // NOI18N
        Panel1.add(pasien_hd);
        pasien_hd.setBounds(770, 160, 110, 23);

        jLabel233.setText("Dializer : ");
        jLabel233.setName("jLabel233"); // NOI18N
        Panel1.add(jLabel233);
        jLabel233.setBounds(670, 160, 100, 23);

        kt_darah.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kt_darah.setHighlighter(null);
        kt_darah.setName("kt_darah"); // NOI18N
        Panel1.add(kt_darah);
        kt_darah.setBounds(970, 160, 40, 23);

        jLabel234.setText("Kantong Darah : ");
        jLabel234.setName("jLabel234"); // NOI18N
        Panel1.add(jLabel234);
        jLabel234.setBounds(880, 160, 90, 23);

        check_hd.setBackground(new java.awt.Color(255, 255, 255));
        check_hd.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        check_hd.setText("Pasien HD");
        check_hd.setName("check_hd"); // NOI18N
        check_hd.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                check_hdItemStateChanged(evt);
            }
        });
        Panel1.add(check_hd);
        check_hd.setBounds(630, 160, 90, 22);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(null);

        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);
        Scroll4.setPreferredSize(new java.awt.Dimension(300, 300));

        FormIDRG1.setName("FormIDRG1"); // NOI18N
        FormIDRG1.setPreferredSize(new java.awt.Dimension(300, 560));
        FormIDRG1.setLayout(null);

        pnb.setHighlighter(null);
        pnb.setName("pnb"); // NOI18N
        pnb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pnbKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pnbKeyTyped(evt);
            }
        });
        FormIDRG1.add(pnb);
        pnb.setBounds(130, 50, 154, 23);

        ahli.setHighlighter(null);
        ahli.setName("ahli"); // NOI18N
        ahli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ahliKeyReleased(evt);
            }
        });
        FormIDRG1.add(ahli);
        ahli.setBounds(130, 80, 154, 23);

        rehab.setHighlighter(null);
        rehab.setName("rehab"); // NOI18N
        rehab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rehabKeyReleased(evt);
            }
        });
        FormIDRG1.add(rehab);
        rehab.setBounds(130, 140, 154, 23);

        rad.setHighlighter(null);
        rad.setName("rad"); // NOI18N
        rad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                radKeyReleased(evt);
            }
        });
        FormIDRG1.add(rad);
        rad.setBounds(130, 110, 154, 23);

        alkes.setHighlighter(null);
        alkes.setName("alkes"); // NOI18N
        alkes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                alkesKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                alkesKeyTyped(evt);
            }
        });
        FormIDRG1.add(alkes);
        alkes.setBounds(130, 200, 154, 23);

        obat.setHighlighter(null);
        obat.setName("obat"); // NOI18N
        obat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                obatKeyReleased(evt);
            }
        });
        FormIDRG1.add(obat);
        obat.setBounds(130, 170, 154, 23);

        pb.setHighlighter(null);
        pb.setName("pb"); // NOI18N
        pb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pbKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pbKeyReleased(evt);
            }
        });
        FormIDRG1.add(pb);
        pb.setBounds(130, 230, 154, 23);

        kpr.setHighlighter(null);
        kpr.setName("kpr"); // NOI18N
        kpr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kprKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                kprKeyReleased(evt);
            }
        });
        FormIDRG1.add(kpr);
        kpr.setBounds(130, 260, 154, 23);

        akomodasi.setHighlighter(null);
        akomodasi.setName("akomodasi"); // NOI18N
        akomodasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                akomodasiKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                akomodasiKeyReleased(evt);
            }
        });
        FormIDRG1.add(akomodasi);
        akomodasi.setBounds(130, 320, 154, 23);

        lab.setHighlighter(null);
        lab.setName("lab"); // NOI18N
        lab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                labKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                labKeyReleased(evt);
            }
        });
        FormIDRG1.add(lab);
        lab.setBounds(130, 290, 154, 23);

        bmhp.setHighlighter(null);
        bmhp.setName("bmhp"); // NOI18N
        bmhp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                bmhpKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                bmhpKeyReleased(evt);
            }
        });
        FormIDRG1.add(bmhp);
        bmhp.setBounds(130, 380, 154, 23);

        kronis.setHighlighter(null);
        kronis.setName("kronis"); // NOI18N
        kronis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kronisKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                kronisKeyReleased(evt);
            }
        });
        FormIDRG1.add(kronis);
        kronis.setBounds(130, 350, 154, 23);

        ksl.setHighlighter(null);
        ksl.setName("ksl"); // NOI18N
        ksl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kslKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                kslKeyReleased(evt);
            }
        });
        FormIDRG1.add(ksl);
        ksl.setBounds(130, 410, 154, 23);

        pnj.setHighlighter(null);
        pnj.setName("pnj"); // NOI18N
        pnj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pnjKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pnjKeyReleased(evt);
            }
        });
        FormIDRG1.add(pnj);
        pnj.setBounds(130, 440, 154, 23);

        ri.setHighlighter(null);
        ri.setName("ri"); // NOI18N
        ri.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                riKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                riKeyReleased(evt);
            }
        });
        FormIDRG1.add(ri);
        ri.setBounds(130, 500, 154, 23);

        pl_darah.setHighlighter(null);
        pl_darah.setName("pl_darah"); // NOI18N
        pl_darah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pl_darahKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pl_darahKeyReleased(evt);
            }
        });
        FormIDRG1.add(pl_darah);
        pl_darah.setBounds(130, 470, 154, 23);

        sewa_alat.setHighlighter(null);
        sewa_alat.setName("sewa_alat"); // NOI18N
        sewa_alat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sewa_alatKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sewa_alatKeyReleased(evt);
            }
        });
        FormIDRG1.add(sewa_alat);
        sewa_alat.setBounds(130, 560, 154, 23);

        obat_kemo.setHighlighter(null);
        obat_kemo.setName("obat_kemo"); // NOI18N
        obat_kemo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                obat_kemoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                obat_kemoKeyReleased(evt);
            }
        });
        FormIDRG1.add(obat_kemo);
        obat_kemo.setBounds(130, 530, 154, 23);

        jLabel71.setText("BMHP :");
        jLabel71.setName("jLabel71"); // NOI18N
        FormIDRG1.add(jLabel71);
        jLabel71.setBounds(10, 380, 115, 23);

        jLabel72.setText("Prosedur Non Bedah :");
        jLabel72.setName("jLabel72"); // NOI18N
        FormIDRG1.add(jLabel72);
        jLabel72.setBounds(10, 50, 115, 23);

        jLabel73.setText("Tenaga Ahli :");
        jLabel73.setName("jLabel73"); // NOI18N
        FormIDRG1.add(jLabel73);
        jLabel73.setBounds(10, 80, 115, 23);

        jLabel74.setText("Radiologi :");
        jLabel74.setName("jLabel74"); // NOI18N
        FormIDRG1.add(jLabel74);
        jLabel74.setBounds(10, 110, 115, 23);

        jLabel75.setText("Rehabilitasi :");
        jLabel75.setName("jLabel75"); // NOI18N
        FormIDRG1.add(jLabel75);
        jLabel75.setBounds(10, 140, 115, 23);

        jLabel76.setText("Obat :");
        jLabel76.setName("jLabel76"); // NOI18N
        FormIDRG1.add(jLabel76);
        jLabel76.setBounds(10, 170, 115, 23);

        jLabel77.setText("Prosedur Bedah :");
        jLabel77.setName("jLabel77"); // NOI18N
        FormIDRG1.add(jLabel77);
        jLabel77.setBounds(10, 230, 115, 23);

        jLabel78.setText("Keperawatan :");
        jLabel78.setName("jLabel78"); // NOI18N
        FormIDRG1.add(jLabel78);
        jLabel78.setBounds(10, 260, 115, 23);

        jLabel80.setText("Labotatorium :");
        jLabel80.setName("jLabel80"); // NOI18N
        FormIDRG1.add(jLabel80);
        jLabel80.setBounds(10, 290, 115, 23);

        jLabel81.setText("Kamar / Akomodasi :");
        jLabel81.setName("jLabel81"); // NOI18N
        FormIDRG1.add(jLabel81);
        jLabel81.setBounds(10, 320, 115, 23);

        jLabel82.setText("Obat Kronis :");
        jLabel82.setName("jLabel82"); // NOI18N
        FormIDRG1.add(jLabel82);
        jLabel82.setBounds(10, 350, 115, 23);

        jLabel83.setText("Konsultasi :");
        jLabel83.setName("jLabel83"); // NOI18N
        FormIDRG1.add(jLabel83);
        jLabel83.setBounds(10, 410, 115, 23);

        jLabel84.setText("Penunjang :");
        jLabel84.setName("jLabel84"); // NOI18N
        FormIDRG1.add(jLabel84);
        jLabel84.setBounds(10, 440, 115, 23);

        jLabel85.setText("Pelayanan Darah :");
        jLabel85.setName("jLabel85"); // NOI18N
        FormIDRG1.add(jLabel85);
        jLabel85.setBounds(10, 470, 115, 23);

        jLabel86.setText("Rawat Intensif :");
        jLabel86.setName("jLabel86"); // NOI18N
        FormIDRG1.add(jLabel86);
        jLabel86.setBounds(10, 500, 115, 23);

        jLabel87.setText("Obat Kemoterapi :");
        jLabel87.setName("jLabel87"); // NOI18N
        FormIDRG1.add(jLabel87);
        jLabel87.setBounds(10, 530, 115, 23);

        jLabel231.setText("Alkes :");
        jLabel231.setName("jLabel231"); // NOI18N
        FormIDRG1.add(jLabel231);
        jLabel231.setBounds(10, 200, 115, 23);

        jLabel232.setText("Sewa Alat :");
        jLabel232.setName("jLabel232"); // NOI18N
        FormIDRG1.add(jLabel232);
        jLabel232.setBounds(10, 560, 115, 23);

        tarifRS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tarifRS.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tarifRS.setName("tarifRS"); // NOI18N
        FormIDRG1.add(tarifRS);
        tarifRS.setBounds(150, 10, 220, 30);

        lbltarifrs.setText("TARIF RS : Rp. ");
        lbltarifrs.setToolTipText("");
        lbltarifrs.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbltarifrs.setName("lbltarifrs"); // NOI18N
        FormIDRG1.add(lbltarifrs);
        lbltarifrs.setBounds(10, 10, 140, 30);

        Scroll4.setViewportView(FormIDRG1);

        jPanel1.add(Scroll4);
        Scroll4.setBounds(-6, 5, 320, 230);

        Panel1.add(jPanel1);
        jPanel1.setBounds(1020, 0, 350, 240);

        FormDATAKLAIM.add(Panel1, java.awt.BorderLayout.PAGE_START);

        TabRawat.setBackground(new java.awt.Color(255, 255, 254));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFocusCycleRoot(true);
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.setPreferredSize(new java.awt.Dimension(494, 600));
        TabRawat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TabRawatStateChanged(evt);
            }
        });
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        FormIDRG.setName("FormIDRG"); // NOI18N
        FormIDRG.setPreferredSize(new java.awt.Dimension(490, 680));
        FormIDRG.setLayout(null);

        jLabel111.setText("Check Diagnosa Utama");
        jLabel111.setName("jLabel111"); // NOI18N
        FormIDRG.add(jLabel111);
        jLabel111.setBounds(270, 190, 115, 23);

        nm_diag_1.setEditable(false);
        nm_diag_1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_1.setEnabled(false);
        nm_diag_1.setHighlighter(null);
        nm_diag_1.setName("nm_diag_1"); // NOI18N
        FormIDRG.add(nm_diag_1);
        nm_diag_1.setBounds(110, 210, 250, 23);

        jLabel121.setText("DIAGNOSA PASIEN");
        jLabel121.setName("jLabel121"); // NOI18N
        FormIDRG.add(jLabel121);
        jLabel121.setBounds(20, 190, 115, 23);

        kd_diag_1.setEditable(false);
        kd_diag_1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_1.setEnabled(false);
        kd_diag_1.setHighlighter(null);
        kd_diag_1.setName("kd_diag_1"); // NOI18N
        FormIDRG.add(kd_diag_1);
        kd_diag_1.setBounds(40, 210, 70, 23);

        nm_diag_2.setEditable(false);
        nm_diag_2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_2.setEnabled(false);
        nm_diag_2.setHighlighter(null);
        nm_diag_2.setName("nm_diag_2"); // NOI18N
        FormIDRG.add(nm_diag_2);
        nm_diag_2.setBounds(110, 240, 250, 23);

        kd_diag_2.setEditable(false);
        kd_diag_2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_2.setEnabled(false);
        kd_diag_2.setHighlighter(null);
        kd_diag_2.setName("kd_diag_2"); // NOI18N
        FormIDRG.add(kd_diag_2);
        kd_diag_2.setBounds(40, 240, 70, 23);

        nm_diag_3.setEditable(false);
        nm_diag_3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_3.setEnabled(false);
        nm_diag_3.setHighlighter(null);
        nm_diag_3.setName("nm_diag_3"); // NOI18N
        FormIDRG.add(nm_diag_3);
        nm_diag_3.setBounds(110, 270, 250, 23);

        kd_diag_3.setEditable(false);
        kd_diag_3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_3.setEnabled(false);
        kd_diag_3.setHighlighter(null);
        kd_diag_3.setName("kd_diag_3"); // NOI18N
        FormIDRG.add(kd_diag_3);
        kd_diag_3.setBounds(40, 270, 70, 23);

        nm_diag_4.setEditable(false);
        nm_diag_4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_4.setEnabled(false);
        nm_diag_4.setHighlighter(null);
        nm_diag_4.setName("nm_diag_4"); // NOI18N
        FormIDRG.add(nm_diag_4);
        nm_diag_4.setBounds(110, 300, 250, 23);

        kd_diag_4.setEditable(false);
        kd_diag_4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_4.setEnabled(false);
        kd_diag_4.setHighlighter(null);
        kd_diag_4.setName("kd_diag_4"); // NOI18N
        FormIDRG.add(kd_diag_4);
        kd_diag_4.setBounds(40, 300, 70, 23);

        nm_diag_5.setEditable(false);
        nm_diag_5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_5.setEnabled(false);
        nm_diag_5.setHighlighter(null);
        nm_diag_5.setName("nm_diag_5"); // NOI18N
        nm_diag_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nm_diag_5ActionPerformed(evt);
            }
        });
        FormIDRG.add(nm_diag_5);
        nm_diag_5.setBounds(110, 330, 250, 23);

        kd_diag_5.setEditable(false);
        kd_diag_5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_5.setEnabled(false);
        kd_diag_5.setHighlighter(null);
        kd_diag_5.setName("kd_diag_5"); // NOI18N
        FormIDRG.add(kd_diag_5);
        kd_diag_5.setBounds(40, 330, 70, 23);

        nm_psdr_1.setEditable(false);
        nm_psdr_1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_1.setEnabled(false);
        nm_psdr_1.setHighlighter(null);
        nm_psdr_1.setName("nm_psdr_1"); // NOI18N
        FormIDRG.add(nm_psdr_1);
        nm_psdr_1.setBounds(590, 210, 260, 23);

        kd_psdr_1.setEditable(false);
        kd_psdr_1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_1.setEnabled(false);
        kd_psdr_1.setHighlighter(null);
        kd_psdr_1.setName("kd_psdr_1"); // NOI18N
        FormIDRG.add(kd_psdr_1);
        kd_psdr_1.setBounds(520, 210, 70, 23);

        nm_psdr_2.setEditable(false);
        nm_psdr_2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_2.setEnabled(false);
        nm_psdr_2.setHighlighter(null);
        nm_psdr_2.setName("nm_psdr_2"); // NOI18N
        FormIDRG.add(nm_psdr_2);
        nm_psdr_2.setBounds(590, 240, 260, 23);

        kd_psdr_2.setEditable(false);
        kd_psdr_2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_2.setEnabled(false);
        kd_psdr_2.setHighlighter(null);
        kd_psdr_2.setName("kd_psdr_2"); // NOI18N
        FormIDRG.add(kd_psdr_2);
        kd_psdr_2.setBounds(520, 240, 70, 23);

        nm_psdr_3.setEditable(false);
        nm_psdr_3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_3.setEnabled(false);
        nm_psdr_3.setHighlighter(null);
        nm_psdr_3.setName("nm_psdr_3"); // NOI18N
        FormIDRG.add(nm_psdr_3);
        nm_psdr_3.setBounds(590, 270, 260, 23);

        kd_psdr_3.setEditable(false);
        kd_psdr_3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_3.setEnabled(false);
        kd_psdr_3.setHighlighter(null);
        kd_psdr_3.setName("kd_psdr_3"); // NOI18N
        FormIDRG.add(kd_psdr_3);
        kd_psdr_3.setBounds(520, 270, 70, 23);

        nm_psdr_4.setEditable(false);
        nm_psdr_4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_4.setEnabled(false);
        nm_psdr_4.setHighlighter(null);
        nm_psdr_4.setName("nm_psdr_4"); // NOI18N
        FormIDRG.add(nm_psdr_4);
        nm_psdr_4.setBounds(590, 300, 260, 23);

        kd_psdr_4.setEditable(false);
        kd_psdr_4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_4.setEnabled(false);
        kd_psdr_4.setHighlighter(null);
        kd_psdr_4.setName("kd_psdr_4"); // NOI18N
        FormIDRG.add(kd_psdr_4);
        kd_psdr_4.setBounds(520, 300, 70, 23);

        nm_psdr_5.setEditable(false);
        nm_psdr_5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_5.setEnabled(false);
        nm_psdr_5.setHighlighter(null);
        nm_psdr_5.setName("nm_psdr_5"); // NOI18N
        FormIDRG.add(nm_psdr_5);
        nm_psdr_5.setBounds(590, 330, 260, 23);

        kd_psdr_5.setEditable(false);
        kd_psdr_5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_5.setEnabled(false);
        kd_psdr_5.setHighlighter(null);
        kd_psdr_5.setName("kd_psdr_5"); // NOI18N
        FormIDRG.add(kd_psdr_5);
        kd_psdr_5.setBounds(520, 330, 70, 23);

        chk_diag_1.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_1.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_1.setName("chk_diag_1"); // NOI18N
        chk_diag_1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_1ItemStateChanged(evt);
            }
        });
        chk_diag_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_diag_1ActionPerformed(evt);
            }
        });
        FormIDRG.add(chk_diag_1);
        chk_diag_1.setBounds(360, 210, 30, 22);

        chk_diag_2.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_2.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_2.setName("chk_diag_2"); // NOI18N
        chk_diag_2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_2ItemStateChanged(evt);
            }
        });
        chk_diag_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_diag_2ActionPerformed(evt);
            }
        });
        FormIDRG.add(chk_diag_2);
        chk_diag_2.setBounds(360, 240, 30, 22);

        chk_diag_3.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_3.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_3.setName("chk_diag_3"); // NOI18N
        chk_diag_3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_3ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_3);
        chk_diag_3.setBounds(360, 270, 30, 22);

        chk_diag_4.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_4.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_4.setName("chk_diag_4"); // NOI18N
        chk_diag_4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_4ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_4);
        chk_diag_4.setBounds(360, 300, 30, 22);

        chk_diag_5.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_5.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_5.setName("chk_diag_5"); // NOI18N
        chk_diag_5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_5ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_5);
        chk_diag_5.setBounds(360, 330, 30, 22);

        nm_diag_6.setEditable(false);
        nm_diag_6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_6.setEnabled(false);
        nm_diag_6.setHighlighter(null);
        nm_diag_6.setName("nm_diag_6"); // NOI18N
        FormIDRG.add(nm_diag_6);
        nm_diag_6.setBounds(110, 360, 250, 23);

        kd_diag_6.setEditable(false);
        kd_diag_6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_6.setEnabled(false);
        kd_diag_6.setHighlighter(null);
        kd_diag_6.setName("kd_diag_6"); // NOI18N
        FormIDRG.add(kd_diag_6);
        kd_diag_6.setBounds(40, 360, 70, 23);

        nm_diag_7.setEditable(false);
        nm_diag_7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_7.setEnabled(false);
        nm_diag_7.setHighlighter(null);
        nm_diag_7.setName("nm_diag_7"); // NOI18N
        FormIDRG.add(nm_diag_7);
        nm_diag_7.setBounds(110, 390, 250, 23);

        kd_diag_7.setEditable(false);
        kd_diag_7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_7.setEnabled(false);
        kd_diag_7.setHighlighter(null);
        kd_diag_7.setName("kd_diag_7"); // NOI18N
        FormIDRG.add(kd_diag_7);
        kd_diag_7.setBounds(40, 390, 70, 23);

        nm_diag_8.setEditable(false);
        nm_diag_8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_8.setEnabled(false);
        nm_diag_8.setHighlighter(null);
        nm_diag_8.setName("nm_diag_8"); // NOI18N
        FormIDRG.add(nm_diag_8);
        nm_diag_8.setBounds(110, 420, 250, 23);

        kd_diag_8.setEditable(false);
        kd_diag_8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_8.setEnabled(false);
        kd_diag_8.setHighlighter(null);
        kd_diag_8.setName("kd_diag_8"); // NOI18N
        FormIDRG.add(kd_diag_8);
        kd_diag_8.setBounds(40, 420, 70, 23);

        nm_diag_9.setEditable(false);
        nm_diag_9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_9.setEnabled(false);
        nm_diag_9.setHighlighter(null);
        nm_diag_9.setName("nm_diag_9"); // NOI18N
        FormIDRG.add(nm_diag_9);
        nm_diag_9.setBounds(110, 450, 250, 23);

        kd_diag_9.setEditable(false);
        kd_diag_9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_9.setEnabled(false);
        kd_diag_9.setHighlighter(null);
        kd_diag_9.setName("kd_diag_9"); // NOI18N
        FormIDRG.add(kd_diag_9);
        kd_diag_9.setBounds(40, 450, 70, 23);

        nm_diag_10.setEditable(false);
        nm_diag_10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_10.setEnabled(false);
        nm_diag_10.setHighlighter(null);
        nm_diag_10.setName("nm_diag_10"); // NOI18N
        FormIDRG.add(nm_diag_10);
        nm_diag_10.setBounds(110, 480, 250, 23);

        kd_diag_10.setEditable(false);
        kd_diag_10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_10.setEnabled(false);
        kd_diag_10.setHighlighter(null);
        kd_diag_10.setName("kd_diag_10"); // NOI18N
        FormIDRG.add(kd_diag_10);
        kd_diag_10.setBounds(40, 480, 70, 23);

        chk_diag_6.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_6.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_6.setName("chk_diag_6"); // NOI18N
        chk_diag_6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_6ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_6);
        chk_diag_6.setBounds(360, 360, 30, 22);

        chk_diag_7.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_7.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_7.setName("chk_diag_7"); // NOI18N
        chk_diag_7.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_7ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_7);
        chk_diag_7.setBounds(360, 390, 30, 22);

        chk_diag_8.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_8.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_8.setName("chk_diag_8"); // NOI18N
        chk_diag_8.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_8ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_8);
        chk_diag_8.setBounds(360, 420, 30, 22);

        chk_diag_9.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_9.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_9.setName("chk_diag_9"); // NOI18N
        chk_diag_9.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_9ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_9);
        chk_diag_9.setBounds(360, 450, 30, 22);

        chk_diag_10.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_10.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_10.setName("chk_diag_10"); // NOI18N
        chk_diag_10.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_10ItemStateChanged(evt);
            }
        });
        FormIDRG.add(chk_diag_10);
        chk_diag_10.setBounds(360, 480, 30, 22);

        nm_psdr_6.setEditable(false);
        nm_psdr_6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_6.setEnabled(false);
        nm_psdr_6.setHighlighter(null);
        nm_psdr_6.setName("nm_psdr_6"); // NOI18N
        FormIDRG.add(nm_psdr_6);
        nm_psdr_6.setBounds(590, 360, 260, 23);

        kd_psdr_6.setEditable(false);
        kd_psdr_6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_6.setEnabled(false);
        kd_psdr_6.setHighlighter(null);
        kd_psdr_6.setName("kd_psdr_6"); // NOI18N
        FormIDRG.add(kd_psdr_6);
        kd_psdr_6.setBounds(520, 360, 70, 23);

        nm_psdr_7.setEditable(false);
        nm_psdr_7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_7.setEnabled(false);
        nm_psdr_7.setHighlighter(null);
        nm_psdr_7.setName("nm_psdr_7"); // NOI18N
        FormIDRG.add(nm_psdr_7);
        nm_psdr_7.setBounds(590, 390, 260, 23);

        kd_psdr_7.setEditable(false);
        kd_psdr_7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_7.setEnabled(false);
        kd_psdr_7.setHighlighter(null);
        kd_psdr_7.setName("kd_psdr_7"); // NOI18N
        FormIDRG.add(kd_psdr_7);
        kd_psdr_7.setBounds(520, 390, 70, 23);

        nm_psdr_8.setEditable(false);
        nm_psdr_8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_8.setEnabled(false);
        nm_psdr_8.setHighlighter(null);
        nm_psdr_8.setName("nm_psdr_8"); // NOI18N
        FormIDRG.add(nm_psdr_8);
        nm_psdr_8.setBounds(590, 420, 260, 23);

        kd_psdr_8.setEditable(false);
        kd_psdr_8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_8.setEnabled(false);
        kd_psdr_8.setHighlighter(null);
        kd_psdr_8.setName("kd_psdr_8"); // NOI18N
        FormIDRG.add(kd_psdr_8);
        kd_psdr_8.setBounds(520, 420, 70, 23);

        nm_psdr_9.setEditable(false);
        nm_psdr_9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_9.setEnabled(false);
        nm_psdr_9.setHighlighter(null);
        nm_psdr_9.setName("nm_psdr_9"); // NOI18N
        FormIDRG.add(nm_psdr_9);
        nm_psdr_9.setBounds(590, 450, 260, 23);

        kd_psdr_9.setEditable(false);
        kd_psdr_9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_9.setEnabled(false);
        kd_psdr_9.setHighlighter(null);
        kd_psdr_9.setName("kd_psdr_9"); // NOI18N
        FormIDRG.add(kd_psdr_9);
        kd_psdr_9.setBounds(520, 450, 70, 23);

        nm_psdr_10.setEditable(false);
        nm_psdr_10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_10.setEnabled(false);
        nm_psdr_10.setHighlighter(null);
        nm_psdr_10.setName("nm_psdr_10"); // NOI18N
        FormIDRG.add(nm_psdr_10);
        nm_psdr_10.setBounds(590, 480, 260, 23);

        kd_psdr_10.setEditable(false);
        kd_psdr_10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_10.setEnabled(false);
        kd_psdr_10.setHighlighter(null);
        kd_psdr_10.setName("kd_psdr_10"); // NOI18N
        FormIDRG.add(kd_psdr_10);
        kd_psdr_10.setBounds(520, 480, 70, 23);

        multiple1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple1.setHighlighter(null);
        multiple1.setName("multiple1"); // NOI18N
        FormIDRG.add(multiple1);
        multiple1.setBounds(850, 210, 50, 23);

        multiple2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple2.setHighlighter(null);
        multiple2.setName("multiple2"); // NOI18N
        FormIDRG.add(multiple2);
        multiple2.setBounds(850, 240, 50, 23);

        multiple3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple3.setHighlighter(null);
        multiple3.setName("multiple3"); // NOI18N
        FormIDRG.add(multiple3);
        multiple3.setBounds(850, 270, 50, 23);

        multiple4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple4.setHighlighter(null);
        multiple4.setName("multiple4"); // NOI18N
        FormIDRG.add(multiple4);
        multiple4.setBounds(850, 300, 50, 23);

        multiple5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple5.setHighlighter(null);
        multiple5.setName("multiple5"); // NOI18N
        FormIDRG.add(multiple5);
        multiple5.setBounds(850, 330, 50, 23);

        multiple6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple6.setHighlighter(null);
        multiple6.setName("multiple6"); // NOI18N
        FormIDRG.add(multiple6);
        multiple6.setBounds(850, 360, 50, 23);

        multiple7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple7.setHighlighter(null);
        multiple7.setName("multiple7"); // NOI18N
        FormIDRG.add(multiple7);
        multiple7.setBounds(850, 390, 50, 23);

        multiple8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple8.setHighlighter(null);
        multiple8.setName("multiple8"); // NOI18N
        FormIDRG.add(multiple8);
        multiple8.setBounds(850, 420, 50, 23);

        multiple9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple9.setHighlighter(null);
        multiple9.setName("multiple9"); // NOI18N
        FormIDRG.add(multiple9);
        multiple9.setBounds(850, 450, 50, 23);

        multiple10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        multiple10.setHighlighter(null);
        multiple10.setName("multiple10"); // NOI18N
        FormIDRG.add(multiple10);
        multiple10.setBounds(850, 480, 50, 23);

        batal_1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_1.setToolTipText("");
        batal_1.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_1.setName("batal_1"); // NOI18N
        batal_1.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_1ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_1);
        batal_1.setBounds(390, 210, 40, 22);

        batal_2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_2.setToolTipText("");
        batal_2.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_2.setName("batal_2"); // NOI18N
        batal_2.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_2ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_2);
        batal_2.setBounds(390, 240, 40, 22);

        batal_3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_3.setToolTipText("");
        batal_3.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_3.setName("batal_3"); // NOI18N
        batal_3.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_3ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_3);
        batal_3.setBounds(390, 270, 40, 22);

        batal_4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_4.setToolTipText("");
        batal_4.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_4.setName("batal_4"); // NOI18N
        batal_4.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_4ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_4);
        batal_4.setBounds(390, 300, 40, 22);

        batal_5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_5.setToolTipText("");
        batal_5.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_5.setName("batal_5"); // NOI18N
        batal_5.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_5ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_5);
        batal_5.setBounds(390, 330, 40, 22);

        batal_6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_6.setToolTipText("");
        batal_6.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_6.setName("batal_6"); // NOI18N
        batal_6.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_6ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_6);
        batal_6.setBounds(390, 360, 40, 22);

        batal_7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_7.setToolTipText("");
        batal_7.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_7.setName("batal_7"); // NOI18N
        batal_7.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_7ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_7);
        batal_7.setBounds(390, 390, 40, 22);

        batal_8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_8.setToolTipText("");
        batal_8.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_8.setName("batal_8"); // NOI18N
        batal_8.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_8ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_8);
        batal_8.setBounds(390, 420, 40, 22);

        batal_9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_9.setToolTipText("");
        batal_9.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_9.setName("batal_9"); // NOI18N
        batal_9.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_9ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_9);
        batal_9.setBounds(390, 450, 40, 22);

        batal_10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_10.setToolTipText("");
        batal_10.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_10.setName("batal_10"); // NOI18N
        batal_10.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_10ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_10);
        batal_10.setBounds(390, 480, 40, 22);

        batal_psdr_1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_1.setToolTipText("");
        batal_psdr_1.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_1.setName("batal_psdr_1"); // NOI18N
        batal_psdr_1.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_1ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_1);
        batal_psdr_1.setBounds(910, 210, 40, 22);

        batal_psdr_2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_2.setToolTipText("");
        batal_psdr_2.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_2.setName("batal_psdr_2"); // NOI18N
        batal_psdr_2.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_2ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_2);
        batal_psdr_2.setBounds(910, 240, 40, 22);

        batal_psdr_3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_3.setToolTipText("");
        batal_psdr_3.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_3.setName("batal_psdr_3"); // NOI18N
        batal_psdr_3.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_3ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_3);
        batal_psdr_3.setBounds(910, 270, 40, 22);

        batal_psdr_4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_4.setToolTipText("");
        batal_psdr_4.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_4.setName("batal_psdr_4"); // NOI18N
        batal_psdr_4.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_4ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_4);
        batal_psdr_4.setBounds(910, 300, 40, 22);

        batal_psdr_5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_5.setToolTipText("");
        batal_psdr_5.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_5.setName("batal_psdr_5"); // NOI18N
        batal_psdr_5.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_5ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_5);
        batal_psdr_5.setBounds(910, 330, 40, 22);

        batal_psdr_6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_6.setToolTipText("");
        batal_psdr_6.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_6.setName("batal_psdr_6"); // NOI18N
        batal_psdr_6.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_6ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_6);
        batal_psdr_6.setBounds(910, 360, 40, 22);

        batal_psdr_7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_7.setToolTipText("");
        batal_psdr_7.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_7.setName("batal_psdr_7"); // NOI18N
        batal_psdr_7.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_7ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_7);
        batal_psdr_7.setBounds(910, 390, 40, 22);

        batal_psdr_8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_8.setToolTipText("");
        batal_psdr_8.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_8.setName("batal_psdr_8"); // NOI18N
        batal_psdr_8.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_8ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_8);
        batal_psdr_8.setBounds(910, 420, 40, 22);

        batal_psdr_9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_9.setToolTipText("");
        batal_psdr_9.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_9.setName("batal_psdr_9"); // NOI18N
        batal_psdr_9.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_9ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_9);
        batal_psdr_9.setBounds(910, 450, 40, 22);

        batal_psdr_10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_10.setToolTipText("");
        batal_psdr_10.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_10.setName("batal_psdr_10"); // NOI18N
        batal_psdr_10.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_10ActionPerformed(evt);
            }
        });
        FormIDRG.add(batal_psdr_10);
        batal_psdr_10.setBounds(910, 480, 40, 22);

        jLabel92.setText("Diagnosa :");
        jLabel92.setName("jLabel92"); // NOI18N
        FormIDRG.add(jLabel92);
        jLabel92.setBounds(0, 10, 68, 23);

        Diagnosa.setHighlighter(null);
        Diagnosa.setName("Diagnosa"); // NOI18N
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormIDRG.add(Diagnosa);
        Diagnosa.setBounds(70, 10, 370, 24);

        idrgPenyakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        idrgPenyakit.setMnemonic('1');
        idrgPenyakit.setToolTipText("Alt+1");
        idrgPenyakit.setName("idrgPenyakit"); // NOI18N
        idrgPenyakit.setPreferredSize(new java.awt.Dimension(28, 23));
        idrgPenyakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idrgPenyakitActionPerformed(evt);
            }
        });
        FormIDRG.add(idrgPenyakit);
        idrgPenyakit.setBounds(440, 10, 28, 23);

        idrgPenyakittambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        idrgPenyakittambah.setMnemonic('2');
        idrgPenyakittambah.setToolTipText("Alt+2");
        idrgPenyakittambah.setName("idrgPenyakittambah"); // NOI18N
        idrgPenyakittambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idrgPenyakittambahActionPerformed(evt);
            }
        });
        FormIDRG.add(idrgPenyakittambah);
        idrgPenyakittambah.setBounds(10, 40, 28, 23);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);

        tbDiagnosa.setName("tbDiagnosa"); // NOI18N
        tbDiagnosa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDiagnosaMouseClicked(evt);
            }
        });
        Scroll5.setViewportView(tbDiagnosa);

        FormIDRG.add(Scroll5);
        Scroll5.setBounds(40, 40, 400, 150);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbProsedur.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbProsedur.setName("tbProsedur"); // NOI18N
        tbProsedur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProsedurMouseClicked(evt);
            }
        });
        Scroll6.setViewportView(tbProsedur);

        FormIDRG.add(Scroll6);
        Scroll6.setBounds(520, 40, 430, 150);

        jLabel93.setText("Prosedur :");
        jLabel93.setName("jLabel93"); // NOI18N
        FormIDRG.add(jLabel93);
        jLabel93.setBounds(480, 10, 68, 23);

        Prosedur.setHighlighter(null);
        Prosedur.setName("Prosedur"); // NOI18N
        Prosedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurKeyPressed(evt);
            }
        });
        FormIDRG.add(Prosedur);
        Prosedur.setBounds(550, 10, 400, 23);

        idrgProsedur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        idrgProsedur.setMnemonic('1');
        idrgProsedur.setToolTipText("Alt+1");
        idrgProsedur.setName("idrgProsedur"); // NOI18N
        idrgProsedur.setPreferredSize(new java.awt.Dimension(28, 23));
        idrgProsedur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idrgProsedurActionPerformed(evt);
            }
        });
        FormIDRG.add(idrgProsedur);
        idrgProsedur.setBounds(950, 10, 28, 23);

        idrgProseduttambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        idrgProseduttambah.setMnemonic('2');
        idrgProseduttambah.setToolTipText("Alt+2");
        idrgProseduttambah.setName("idrgProseduttambah"); // NOI18N
        idrgProseduttambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idrgProseduttambahActionPerformed(evt);
            }
        });
        FormIDRG.add(idrgProseduttambah);
        idrgProseduttambah.setBounds(490, 40, 28, 23);

        responIDRG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        responIDRG.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        responIDRG.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        responIDRG.setName("responIDRG"); // NOI18N
        FormIDRG.add(responIDRG);
        responIDRG.setBounds(960, 70, 360, 380);

        jLabel127.setText("PROSEDUR PASIEN");
        jLabel127.setName("jLabel127"); // NOI18N
        FormIDRG.add(jLabel127);
        jLabel127.setBounds(500, 190, 115, 23);

        jLabel129.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel129.setText(" HASIL GROUPING iDRG");
        jLabel129.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel129.setName("jLabel129"); // NOI18N
        FormIDRG.add(jLabel129);
        jLabel129.setBounds(960, 40, 200, 23);

        TabRawat.addTab("IDRG", FormIDRG);

        FormINACBG.setName("FormINACBG"); // NOI18N
        FormINACBG.setPreferredSize(new java.awt.Dimension(490, 500));
        FormINACBG.setLayout(null);

        jLabel94.setText("Diagnosa :");
        jLabel94.setName("jLabel94"); // NOI18N
        FormINACBG.add(jLabel94);
        jLabel94.setBounds(0, 10, 68, 23);

        DiagnosaINACBG.setHighlighter(null);
        DiagnosaINACBG.setName("DiagnosaINACBG"); // NOI18N
        DiagnosaINACBG.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaINACBGKeyPressed(evt);
            }
        });
        FormINACBG.add(DiagnosaINACBG);
        DiagnosaINACBG.setBounds(70, 10, 370, 24);

        INAdiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        INAdiagnosa.setMnemonic('1');
        INAdiagnosa.setToolTipText("Alt+1");
        INAdiagnosa.setName("INAdiagnosa"); // NOI18N
        INAdiagnosa.setPreferredSize(new java.awt.Dimension(28, 23));
        INAdiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                INAdiagnosaActionPerformed(evt);
            }
        });
        FormINACBG.add(INAdiagnosa);
        INAdiagnosa.setBounds(440, 10, 28, 23);

        INAdiagnosatambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        INAdiagnosatambah.setMnemonic('2');
        INAdiagnosatambah.setToolTipText("Alt+2");
        INAdiagnosatambah.setName("INAdiagnosatambah"); // NOI18N
        INAdiagnosatambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                INAdiagnosatambahActionPerformed(evt);
            }
        });
        FormINACBG.add(INAdiagnosatambah);
        INAdiagnosatambah.setBounds(10, 40, 28, 23);

        Scroll7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll7.setName("Scroll7"); // NOI18N
        Scroll7.setOpaque(true);

        tbDiagnosa2.setName("tbDiagnosa2"); // NOI18N
        tbDiagnosa2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDiagnosa2MouseClicked(evt);
            }
        });
        Scroll7.setViewportView(tbDiagnosa2);

        FormINACBG.add(Scroll7);
        Scroll7.setBounds(40, 40, 400, 150);

        Scroll8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll8.setName("Scroll8"); // NOI18N
        Scroll8.setOpaque(true);

        tbProsedur2.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbProsedur2.setName("tbProsedur2"); // NOI18N
        tbProsedur2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProsedur2MouseClicked(evt);
            }
        });
        Scroll8.setViewportView(tbProsedur2);

        FormINACBG.add(Scroll8);
        Scroll8.setBounds(520, 40, 430, 150);

        jLabel95.setText("Prosedur :");
        jLabel95.setName("jLabel95"); // NOI18N
        FormINACBG.add(jLabel95);
        jLabel95.setBounds(480, 10, 68, 23);

        ProsedurINACBG.setHighlighter(null);
        ProsedurINACBG.setName("ProsedurINACBG"); // NOI18N
        ProsedurINACBG.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurINACBGKeyPressed(evt);
            }
        });
        FormINACBG.add(ProsedurINACBG);
        ProsedurINACBG.setBounds(550, 10, 400, 23);

        INAprosedur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        INAprosedur.setMnemonic('1');
        INAprosedur.setToolTipText("Alt+1");
        INAprosedur.setName("INAprosedur"); // NOI18N
        INAprosedur.setPreferredSize(new java.awt.Dimension(28, 23));
        INAprosedur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                INAprosedurActionPerformed(evt);
            }
        });
        FormINACBG.add(INAprosedur);
        INAprosedur.setBounds(950, 10, 28, 23);

        INAprosedurtambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        INAprosedurtambah.setMnemonic('2');
        INAprosedurtambah.setToolTipText("Alt+2");
        INAprosedurtambah.setName("INAprosedurtambah"); // NOI18N
        INAprosedurtambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                INAprosedurtambahActionPerformed(evt);
            }
        });
        FormINACBG.add(INAprosedurtambah);
        INAprosedurtambah.setBounds(490, 40, 28, 23);

        jLabel183.setName("jLabel183"); // NOI18N
        jLabel183.setPreferredSize(new java.awt.Dimension(150, 14));
        FormINACBG.add(jLabel183);
        jLabel183.setBounds(980, 160, 150, 14);

        jLabel113.setText("Check Diagnosa Utama");
        jLabel113.setName("jLabel113"); // NOI18N
        FormINACBG.add(jLabel113);
        jLabel113.setBounds(270, 190, 115, 23);

        nm_diag_11.setEditable(false);
        nm_diag_11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_11.setEnabled(false);
        nm_diag_11.setHighlighter(null);
        nm_diag_11.setName("nm_diag_11"); // NOI18N
        FormINACBG.add(nm_diag_11);
        nm_diag_11.setBounds(110, 210, 250, 23);

        jLabel125.setText("DIAGNOSA PASIEN");
        jLabel125.setName("jLabel125"); // NOI18N
        FormINACBG.add(jLabel125);
        jLabel125.setBounds(20, 190, 115, 23);

        kd_diag_11.setEditable(false);
        kd_diag_11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_11.setEnabled(false);
        kd_diag_11.setHighlighter(null);
        kd_diag_11.setName("kd_diag_11"); // NOI18N
        FormINACBG.add(kd_diag_11);
        kd_diag_11.setBounds(40, 210, 70, 23);

        nm_diag_12.setEditable(false);
        nm_diag_12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_12.setEnabled(false);
        nm_diag_12.setHighlighter(null);
        nm_diag_12.setName("nm_diag_12"); // NOI18N
        FormINACBG.add(nm_diag_12);
        nm_diag_12.setBounds(110, 240, 250, 23);

        kd_diag_12.setEditable(false);
        kd_diag_12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_12.setEnabled(false);
        kd_diag_12.setHighlighter(null);
        kd_diag_12.setName("kd_diag_12"); // NOI18N
        FormINACBG.add(kd_diag_12);
        kd_diag_12.setBounds(40, 240, 70, 23);

        nm_diag_13.setEditable(false);
        nm_diag_13.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_13.setEnabled(false);
        nm_diag_13.setHighlighter(null);
        nm_diag_13.setName("nm_diag_13"); // NOI18N
        FormINACBG.add(nm_diag_13);
        nm_diag_13.setBounds(110, 270, 250, 23);

        kd_diag_13.setEditable(false);
        kd_diag_13.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_13.setEnabled(false);
        kd_diag_13.setHighlighter(null);
        kd_diag_13.setName("kd_diag_13"); // NOI18N
        FormINACBG.add(kd_diag_13);
        kd_diag_13.setBounds(40, 270, 70, 23);

        nm_diag_14.setEditable(false);
        nm_diag_14.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_14.setEnabled(false);
        nm_diag_14.setHighlighter(null);
        nm_diag_14.setName("nm_diag_14"); // NOI18N
        FormINACBG.add(nm_diag_14);
        nm_diag_14.setBounds(110, 300, 250, 23);

        kd_diag_14.setEditable(false);
        kd_diag_14.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_14.setEnabled(false);
        kd_diag_14.setHighlighter(null);
        kd_diag_14.setName("kd_diag_14"); // NOI18N
        FormINACBG.add(kd_diag_14);
        kd_diag_14.setBounds(40, 300, 70, 23);

        nm_diag_15.setEditable(false);
        nm_diag_15.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_15.setEnabled(false);
        nm_diag_15.setHighlighter(null);
        nm_diag_15.setName("nm_diag_15"); // NOI18N
        nm_diag_15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nm_diag_15ActionPerformed(evt);
            }
        });
        FormINACBG.add(nm_diag_15);
        nm_diag_15.setBounds(110, 330, 250, 23);

        kd_diag_15.setEditable(false);
        kd_diag_15.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_15.setEnabled(false);
        kd_diag_15.setHighlighter(null);
        kd_diag_15.setName("kd_diag_15"); // NOI18N
        FormINACBG.add(kd_diag_15);
        kd_diag_15.setBounds(40, 330, 70, 23);

        nm_psdr_11.setEditable(false);
        nm_psdr_11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_11.setEnabled(false);
        nm_psdr_11.setHighlighter(null);
        nm_psdr_11.setName("nm_psdr_11"); // NOI18N
        nm_psdr_11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nm_psdr_11ActionPerformed(evt);
            }
        });
        FormINACBG.add(nm_psdr_11);
        nm_psdr_11.setBounds(590, 210, 310, 23);

        kd_psdr_11.setEditable(false);
        kd_psdr_11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_11.setEnabled(false);
        kd_psdr_11.setHighlighter(null);
        kd_psdr_11.setName("kd_psdr_11"); // NOI18N
        FormINACBG.add(kd_psdr_11);
        kd_psdr_11.setBounds(520, 210, 70, 23);

        nm_psdr_12.setEditable(false);
        nm_psdr_12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_12.setEnabled(false);
        nm_psdr_12.setHighlighter(null);
        nm_psdr_12.setName("nm_psdr_12"); // NOI18N
        FormINACBG.add(nm_psdr_12);
        nm_psdr_12.setBounds(590, 240, 310, 23);

        kd_psdr_12.setEditable(false);
        kd_psdr_12.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_12.setEnabled(false);
        kd_psdr_12.setHighlighter(null);
        kd_psdr_12.setName("kd_psdr_12"); // NOI18N
        FormINACBG.add(kd_psdr_12);
        kd_psdr_12.setBounds(520, 240, 70, 23);

        nm_psdr_13.setEditable(false);
        nm_psdr_13.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_13.setEnabled(false);
        nm_psdr_13.setHighlighter(null);
        nm_psdr_13.setName("nm_psdr_13"); // NOI18N
        FormINACBG.add(nm_psdr_13);
        nm_psdr_13.setBounds(590, 270, 310, 23);

        kd_psdr_13.setEditable(false);
        kd_psdr_13.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_13.setEnabled(false);
        kd_psdr_13.setHighlighter(null);
        kd_psdr_13.setName("kd_psdr_13"); // NOI18N
        FormINACBG.add(kd_psdr_13);
        kd_psdr_13.setBounds(520, 270, 70, 23);

        nm_psdr_14.setEditable(false);
        nm_psdr_14.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_14.setEnabled(false);
        nm_psdr_14.setHighlighter(null);
        nm_psdr_14.setName("nm_psdr_14"); // NOI18N
        FormINACBG.add(nm_psdr_14);
        nm_psdr_14.setBounds(590, 300, 310, 23);

        kd_psdr_14.setEditable(false);
        kd_psdr_14.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_14.setEnabled(false);
        kd_psdr_14.setHighlighter(null);
        kd_psdr_14.setName("kd_psdr_14"); // NOI18N
        FormINACBG.add(kd_psdr_14);
        kd_psdr_14.setBounds(520, 300, 70, 23);

        nm_psdr_15.setEditable(false);
        nm_psdr_15.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_15.setEnabled(false);
        nm_psdr_15.setHighlighter(null);
        nm_psdr_15.setName("nm_psdr_15"); // NOI18N
        FormINACBG.add(nm_psdr_15);
        nm_psdr_15.setBounds(590, 330, 310, 23);

        kd_psdr_15.setEditable(false);
        kd_psdr_15.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_15.setEnabled(false);
        kd_psdr_15.setHighlighter(null);
        kd_psdr_15.setName("kd_psdr_15"); // NOI18N
        FormINACBG.add(kd_psdr_15);
        kd_psdr_15.setBounds(520, 330, 70, 23);

        chk_diag_11.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_11.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_11.setName("chk_diag_11"); // NOI18N
        chk_diag_11.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_11ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_11);
        chk_diag_11.setBounds(360, 210, 30, 22);

        chk_diag_12.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_12.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_12.setName("chk_diag_12"); // NOI18N
        chk_diag_12.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_12ItemStateChanged(evt);
            }
        });
        chk_diag_12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chk_diag_12MouseClicked(evt);
            }
        });
        FormINACBG.add(chk_diag_12);
        chk_diag_12.setBounds(360, 240, 30, 22);

        chk_diag_13.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_13.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_13.setName("chk_diag_13"); // NOI18N
        chk_diag_13.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_13ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_13);
        chk_diag_13.setBounds(360, 270, 30, 22);

        chk_diag_14.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_14.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_14.setName("chk_diag_14"); // NOI18N
        chk_diag_14.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_14ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_14);
        chk_diag_14.setBounds(360, 300, 30, 22);

        chk_diag_15.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_15.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_15.setName("chk_diag_15"); // NOI18N
        chk_diag_15.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_15ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_15);
        chk_diag_15.setBounds(360, 330, 30, 22);

        jLabel126.setText("PROSEDUR PASIEN");
        jLabel126.setName("jLabel126"); // NOI18N
        FormINACBG.add(jLabel126);
        jLabel126.setBounds(500, 190, 115, 23);

        nm_diag_16.setEditable(false);
        nm_diag_16.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_16.setEnabled(false);
        nm_diag_16.setHighlighter(null);
        nm_diag_16.setName("nm_diag_16"); // NOI18N
        FormINACBG.add(nm_diag_16);
        nm_diag_16.setBounds(110, 360, 250, 23);

        kd_diag_16.setEditable(false);
        kd_diag_16.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_16.setEnabled(false);
        kd_diag_16.setHighlighter(null);
        kd_diag_16.setName("kd_diag_16"); // NOI18N
        FormINACBG.add(kd_diag_16);
        kd_diag_16.setBounds(40, 360, 70, 23);

        nm_diag_17.setEditable(false);
        nm_diag_17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_17.setEnabled(false);
        nm_diag_17.setHighlighter(null);
        nm_diag_17.setName("nm_diag_17"); // NOI18N
        FormINACBG.add(nm_diag_17);
        nm_diag_17.setBounds(110, 390, 250, 23);

        kd_diag_17.setEditable(false);
        kd_diag_17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_17.setEnabled(false);
        kd_diag_17.setHighlighter(null);
        kd_diag_17.setName("kd_diag_17"); // NOI18N
        FormINACBG.add(kd_diag_17);
        kd_diag_17.setBounds(40, 390, 70, 23);

        nm_diag_18.setEditable(false);
        nm_diag_18.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_18.setEnabled(false);
        nm_diag_18.setHighlighter(null);
        nm_diag_18.setName("nm_diag_18"); // NOI18N
        FormINACBG.add(nm_diag_18);
        nm_diag_18.setBounds(110, 420, 250, 23);

        kd_diag_18.setEditable(false);
        kd_diag_18.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_18.setEnabled(false);
        kd_diag_18.setHighlighter(null);
        kd_diag_18.setName("kd_diag_18"); // NOI18N
        FormINACBG.add(kd_diag_18);
        kd_diag_18.setBounds(40, 420, 70, 23);

        nm_diag_19.setEditable(false);
        nm_diag_19.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_19.setEnabled(false);
        nm_diag_19.setHighlighter(null);
        nm_diag_19.setName("nm_diag_19"); // NOI18N
        FormINACBG.add(nm_diag_19);
        nm_diag_19.setBounds(110, 450, 250, 23);

        kd_diag_19.setEditable(false);
        kd_diag_19.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_19.setEnabled(false);
        kd_diag_19.setHighlighter(null);
        kd_diag_19.setName("kd_diag_19"); // NOI18N
        FormINACBG.add(kd_diag_19);
        kd_diag_19.setBounds(40, 450, 70, 23);

        nm_diag_20.setEditable(false);
        nm_diag_20.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_diag_20.setEnabled(false);
        nm_diag_20.setHighlighter(null);
        nm_diag_20.setName("nm_diag_20"); // NOI18N
        FormINACBG.add(nm_diag_20);
        nm_diag_20.setBounds(110, 480, 250, 23);

        kd_diag_20.setEditable(false);
        kd_diag_20.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_diag_20.setEnabled(false);
        kd_diag_20.setHighlighter(null);
        kd_diag_20.setName("kd_diag_20"); // NOI18N
        FormINACBG.add(kd_diag_20);
        kd_diag_20.setBounds(40, 480, 70, 23);

        chk_diag_16.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_16.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_16.setName("chk_diag_16"); // NOI18N
        chk_diag_16.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_16ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_16);
        chk_diag_16.setBounds(360, 360, 30, 22);

        chk_diag_17.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_17.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_17.setName("chk_diag_17"); // NOI18N
        chk_diag_17.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_17ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_17);
        chk_diag_17.setBounds(360, 390, 30, 22);

        chk_diag_18.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_18.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_18.setName("chk_diag_18"); // NOI18N
        chk_diag_18.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_18ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_18);
        chk_diag_18.setBounds(360, 420, 30, 22);

        chk_diag_19.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_19.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_19.setName("chk_diag_19"); // NOI18N
        chk_diag_19.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_19ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_19);
        chk_diag_19.setBounds(360, 450, 30, 22);

        chk_diag_20.setBackground(new java.awt.Color(255, 255, 255));
        chk_diag_20.setFont(new java.awt.Font("Inter", 1, 12)); // NOI18N
        chk_diag_20.setName("chk_diag_20"); // NOI18N
        chk_diag_20.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_diag_20ItemStateChanged(evt);
            }
        });
        FormINACBG.add(chk_diag_20);
        chk_diag_20.setBounds(360, 480, 30, 22);

        nm_psdr_16.setEditable(false);
        nm_psdr_16.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_16.setEnabled(false);
        nm_psdr_16.setHighlighter(null);
        nm_psdr_16.setName("nm_psdr_16"); // NOI18N
        FormINACBG.add(nm_psdr_16);
        nm_psdr_16.setBounds(590, 360, 310, 23);

        kd_psdr_16.setEditable(false);
        kd_psdr_16.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_16.setEnabled(false);
        kd_psdr_16.setHighlighter(null);
        kd_psdr_16.setName("kd_psdr_16"); // NOI18N
        FormINACBG.add(kd_psdr_16);
        kd_psdr_16.setBounds(520, 360, 70, 23);

        nm_psdr_17.setEditable(false);
        nm_psdr_17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_17.setEnabled(false);
        nm_psdr_17.setHighlighter(null);
        nm_psdr_17.setName("nm_psdr_17"); // NOI18N
        FormINACBG.add(nm_psdr_17);
        nm_psdr_17.setBounds(590, 390, 310, 23);

        kd_psdr_17.setEditable(false);
        kd_psdr_17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_17.setEnabled(false);
        kd_psdr_17.setHighlighter(null);
        kd_psdr_17.setName("kd_psdr_17"); // NOI18N
        FormINACBG.add(kd_psdr_17);
        kd_psdr_17.setBounds(520, 390, 70, 23);

        nm_psdr_18.setEditable(false);
        nm_psdr_18.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_18.setEnabled(false);
        nm_psdr_18.setHighlighter(null);
        nm_psdr_18.setName("nm_psdr_18"); // NOI18N
        FormINACBG.add(nm_psdr_18);
        nm_psdr_18.setBounds(590, 420, 310, 23);

        kd_psdr_18.setEditable(false);
        kd_psdr_18.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_18.setEnabled(false);
        kd_psdr_18.setHighlighter(null);
        kd_psdr_18.setName("kd_psdr_18"); // NOI18N
        FormINACBG.add(kd_psdr_18);
        kd_psdr_18.setBounds(520, 420, 70, 23);

        nm_psdr_19.setEditable(false);
        nm_psdr_19.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_19.setEnabled(false);
        nm_psdr_19.setHighlighter(null);
        nm_psdr_19.setName("nm_psdr_19"); // NOI18N
        FormINACBG.add(nm_psdr_19);
        nm_psdr_19.setBounds(590, 450, 310, 23);

        kd_psdr_19.setEditable(false);
        kd_psdr_19.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_19.setEnabled(false);
        kd_psdr_19.setHighlighter(null);
        kd_psdr_19.setName("kd_psdr_19"); // NOI18N
        FormINACBG.add(kd_psdr_19);
        kd_psdr_19.setBounds(520, 450, 70, 23);

        nm_psdr_20.setEditable(false);
        nm_psdr_20.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        nm_psdr_20.setEnabled(false);
        nm_psdr_20.setHighlighter(null);
        nm_psdr_20.setName("nm_psdr_20"); // NOI18N
        FormINACBG.add(nm_psdr_20);
        nm_psdr_20.setBounds(590, 480, 310, 23);

        kd_psdr_20.setEditable(false);
        kd_psdr_20.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        kd_psdr_20.setEnabled(false);
        kd_psdr_20.setHighlighter(null);
        kd_psdr_20.setName("kd_psdr_20"); // NOI18N
        FormINACBG.add(kd_psdr_20);
        kd_psdr_20.setBounds(520, 480, 70, 23);

        batal_11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_11.setToolTipText("");
        batal_11.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_11.setName("batal_11"); // NOI18N
        batal_11.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_11ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_11);
        batal_11.setBounds(390, 210, 40, 22);

        batal_12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_12.setToolTipText("");
        batal_12.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_12.setName("batal_12"); // NOI18N
        batal_12.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_12ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_12);
        batal_12.setBounds(390, 240, 40, 22);

        batal_13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_13.setToolTipText("");
        batal_13.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_13.setName("batal_13"); // NOI18N
        batal_13.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_13ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_13);
        batal_13.setBounds(390, 270, 40, 22);

        batal_14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_14.setToolTipText("");
        batal_14.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_14.setName("batal_14"); // NOI18N
        batal_14.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_14ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_14);
        batal_14.setBounds(390, 300, 40, 22);

        batal_15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_15.setToolTipText("");
        batal_15.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_15.setName("batal_15"); // NOI18N
        batal_15.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_15ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_15);
        batal_15.setBounds(390, 330, 40, 22);

        batal_16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_16.setToolTipText("");
        batal_16.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_16.setName("batal_16"); // NOI18N
        batal_16.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_16ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_16);
        batal_16.setBounds(390, 360, 40, 22);

        batal_17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_17.setToolTipText("");
        batal_17.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_17.setName("batal_17"); // NOI18N
        batal_17.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_17ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_17);
        batal_17.setBounds(390, 390, 40, 22);

        batal_18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_18.setToolTipText("");
        batal_18.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_18.setName("batal_18"); // NOI18N
        batal_18.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_18ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_18);
        batal_18.setBounds(390, 420, 40, 22);

        batal_19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_19.setToolTipText("");
        batal_19.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_19.setName("batal_19"); // NOI18N
        batal_19.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_19ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_19);
        batal_19.setBounds(390, 450, 40, 22);

        batal_20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_20.setToolTipText("");
        batal_20.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_20.setName("batal_20"); // NOI18N
        batal_20.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_20ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_20);
        batal_20.setBounds(390, 480, 40, 22);

        batal_psdr_11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_11.setToolTipText("");
        batal_psdr_11.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_11.setName("batal_psdr_11"); // NOI18N
        batal_psdr_11.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_11ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_11);
        batal_psdr_11.setBounds(910, 210, 40, 22);

        batal_psdr_12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_12.setToolTipText("");
        batal_psdr_12.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_12.setName("batal_psdr_12"); // NOI18N
        batal_psdr_12.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_12ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_12);
        batal_psdr_12.setBounds(910, 240, 40, 22);

        batal_psdr_13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_13.setToolTipText("");
        batal_psdr_13.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_13.setName("batal_psdr_13"); // NOI18N
        batal_psdr_13.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_13ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_13);
        batal_psdr_13.setBounds(910, 270, 40, 22);

        batal_psdr_14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_14.setToolTipText("");
        batal_psdr_14.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_14.setName("batal_psdr_14"); // NOI18N
        batal_psdr_14.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_14ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_14);
        batal_psdr_14.setBounds(910, 300, 40, 22);

        batal_psdr_15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_15.setToolTipText("");
        batal_psdr_15.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_15.setName("batal_psdr_15"); // NOI18N
        batal_psdr_15.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_15ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_15);
        batal_psdr_15.setBounds(910, 330, 40, 22);

        batal_psdr_16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_16.setToolTipText("");
        batal_psdr_16.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_16.setName("batal_psdr_16"); // NOI18N
        batal_psdr_16.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_16ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_16);
        batal_psdr_16.setBounds(910, 360, 40, 22);

        batal_psdr_17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_17.setToolTipText("");
        batal_psdr_17.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_17.setName("batal_psdr_17"); // NOI18N
        batal_psdr_17.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_17ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_17);
        batal_psdr_17.setBounds(910, 390, 40, 22);

        batal_psdr_18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_18.setToolTipText("");
        batal_psdr_18.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_18.setName("batal_psdr_18"); // NOI18N
        batal_psdr_18.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_18ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_18);
        batal_psdr_18.setBounds(910, 420, 40, 22);

        batal_psdr_19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_19.setToolTipText("");
        batal_psdr_19.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_19.setName("batal_psdr_19"); // NOI18N
        batal_psdr_19.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_19ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_19);
        batal_psdr_19.setBounds(910, 450, 40, 22);

        batal_psdr_20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/101.png"))); // NOI18N
        batal_psdr_20.setToolTipText("");
        batal_psdr_20.setGlassColor(new java.awt.Color(255, 0, 0));
        batal_psdr_20.setName("batal_psdr_20"); // NOI18N
        batal_psdr_20.setPreferredSize(new java.awt.Dimension(100, 30));
        batal_psdr_20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batal_psdr_20ActionPerformed(evt);
            }
        });
        FormINACBG.add(batal_psdr_20);
        batal_psdr_20.setBounds(910, 480, 40, 22);

        Import.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        Import.setText("Import Coding");
        Import.setToolTipText("Alt+S");
        Import.setGlassColor(new java.awt.Color(0, 255, 153));
        Import.setName("Import"); // NOI18N
        Import.setPreferredSize(new java.awt.Dimension(120, 30));
        Import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportActionPerformed(evt);
            }
        });
        FormINACBG.add(Import);
        Import.setBounds(990, 10, 150, 30);

        scrollRespon1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        scrollRespon1.setName("scrollRespon1"); // NOI18N
        scrollRespon1.setPreferredSize(new java.awt.Dimension(50, 50));

        panelGlass9.setBackground(new java.awt.Color(187, 187, 187));
        panelGlass9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 0));
        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(520, 500));
        panelGlass9.setLayout(null);

        cmbDrugs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none : None" }));
        cmbDrugs.setName("cmbDrugs"); // NOI18N
        cmbDrugs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbDrugsItemStateChanged(evt);
            }
        });
        panelGlass9.add(cmbDrugs);
        cmbDrugs.setBounds(120, 260, 160, 23);

        jLabel96.setText("Sub Acute :");
        jLabel96.setName("jLabel96"); // NOI18N
        panelGlass9.add(jLabel96);
        jLabel96.setBounds(10, 110, 100, 23);

        jLabel97.setText("Pembayar Selisih :");
        jLabel97.setName("jLabel97"); // NOI18N
        panelGlass9.add(jLabel97);
        jLabel97.setBounds(10, 410, 100, 23);

        jLabel98.setText("Group :");
        jLabel98.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel98.setName("jLabel98"); // NOI18N
        panelGlass9.add(jLabel98);
        jLabel98.setBounds(10, 290, 100, 23);

        jLabel99.setText("Chronic");
        jLabel99.setName("jLabel99"); // NOI18N
        panelGlass9.add(jLabel99);
        jLabel99.setBounds(10, 140, 100, 23);

        jLabel100.setText("Spec. Procedure :");
        jLabel100.setName("jLabel100"); // NOI18N
        panelGlass9.add(jLabel100);
        jLabel100.setBounds(10, 170, 100, 23);

        jLabel101.setText("Spec. Prosthesis :");
        jLabel101.setName("jLabel101"); // NOI18N
        panelGlass9.add(jLabel101);
        jLabel101.setBounds(10, 200, 100, 23);

        jLabel102.setText("TOTAL KLAIM :");
        jLabel102.setName("jLabel102"); // NOI18N
        panelGlass9.add(jLabel102);
        jLabel102.setBounds(10, 350, 100, 23);

        jLabel103.setText("Spec. Investigation :");
        jLabel103.setName("jLabel103"); // NOI18N
        panelGlass9.add(jLabel103);
        jLabel103.setBounds(10, 230, 100, 23);

        jLabel104.setText("Spec.Drugs :");
        jLabel104.setName("jLabel104"); // NOI18N
        panelGlass9.add(jLabel104);
        jLabel104.setBounds(10, 260, 100, 23);

        lChronic.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lChronic.setName("lChronic"); // NOI18N
        panelGlass9.add(lChronic);
        lChronic.setBounds(120, 140, 80, 23);

        jLabel105.setText("Rp. ");
        jLabel105.setName("jLabel105"); // NOI18N
        panelGlass9.add(jLabel105);
        jLabel105.setBounds(290, 280, 30, 23);

        cmbProc.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none : None" }));
        cmbProc.setName("cmbProc"); // NOI18N
        cmbProc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProcItemStateChanged(evt);
            }
        });
        panelGlass9.add(cmbProc);
        cmbProc.setBounds(120, 170, 160, 23);

        cmbPros.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none : None" }));
        cmbPros.setName("cmbPros"); // NOI18N
        cmbPros.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProsItemStateChanged(evt);
            }
        });
        panelGlass9.add(cmbPros);
        cmbPros.setBounds(120, 200, 160, 23);

        cmbInv.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none : None" }));
        cmbInv.setName("cmbInv"); // NOI18N
        cmbInv.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbInvItemStateChanged(evt);
            }
        });
        panelGlass9.add(cmbInv);
        cmbInv.setBounds(120, 230, 160, 23);

        lSubacute.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lSubacute.setName("lSubacute"); // NOI18N
        panelGlass9.add(lSubacute);
        lSubacute.setBounds(120, 110, 100, 23);

        jLabel107.setText("Rp. ");
        jLabel107.setName("jLabel107"); // NOI18N
        panelGlass9.add(jLabel107);
        jLabel107.setBounds(290, 130, 30, 23);

        jLabel108.setText("Rp. ");
        jLabel108.setName("jLabel108"); // NOI18N
        panelGlass9.add(jLabel108);
        jLabel108.setBounds(290, 100, 30, 23);

        jLabel109.setText("Rp. ");
        jLabel109.setName("jLabel109"); // NOI18N
        panelGlass9.add(jLabel109);
        jLabel109.setBounds(290, 190, 30, 23);

        jLabel110.setText("Rp. ");
        jLabel110.setName("jLabel110"); // NOI18N
        panelGlass9.add(jLabel110);
        jLabel110.setBounds(290, 160, 30, 23);

        jLabel112.setText("Rp. ");
        jLabel112.setName("jLabel112"); // NOI18N
        panelGlass9.add(jLabel112);
        jLabel112.setBounds(290, 250, 30, 23);

        jLabel114.setText("Rp. ");
        jLabel114.setName("jLabel114"); // NOI18N
        panelGlass9.add(jLabel114);
        jLabel114.setBounds(290, 220, 30, 23);

        jLabel115.setText("Rp. ");
        jLabel115.setName("jLabel115"); // NOI18N
        panelGlass9.add(jLabel115);
        jLabel115.setBounds(290, 340, 30, 23);

        lblTarifKronis.setText("0");
        lblTarifKronis.setName("lblTarifKronis"); // NOI18N
        panelGlass9.add(lblTarifKronis);
        lblTarifKronis.setBounds(320, 130, 100, 23);

        lblTarifGrup.setText("0");
        lblTarifGrup.setName("lblTarifGrup"); // NOI18N
        panelGlass9.add(lblTarifGrup);
        lblTarifGrup.setBounds(320, 280, 100, 23);

        lblTarifAcute.setText("0");
        lblTarifAcute.setName("lblTarifAcute"); // NOI18N
        panelGlass9.add(lblTarifAcute);
        lblTarifAcute.setBounds(320, 100, 100, 23);

        lbltarifProc.setText("0");
        lbltarifProc.setName("lbltarifProc"); // NOI18N
        panelGlass9.add(lbltarifProc);
        lbltarifProc.setBounds(320, 160, 100, 23);

        lblTarifPros.setText("0");
        lblTarifPros.setName("lblTarifPros"); // NOI18N
        panelGlass9.add(lblTarifPros);
        lblTarifPros.setBounds(320, 190, 100, 23);

        lblTarifDrugs.setText("0");
        lblTarifDrugs.setName("lblTarifDrugs"); // NOI18N
        panelGlass9.add(lblTarifDrugs);
        lblTarifDrugs.setBounds(320, 250, 100, 23);

        lblTarifInv.setText("0");
        lblTarifInv.setName("lblTarifInv"); // NOI18N
        panelGlass9.add(lblTarifInv);
        lblTarifInv.setBounds(320, 220, 100, 23);

        lblTarifTotal.setText("0");
        lblTarifTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblTarifTotal.setName("lblTarifTotal"); // NOI18N
        panelGlass9.add(lblTarifTotal);
        lblTarifTotal.setBounds(320, 340, 100, 23);

        jSeparator16.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator16.setName("jSeparator16"); // NOI18N
        panelGlass9.add(jSeparator16);
        jSeparator16.setBounds(10, 400, 420, 10);

        jSeparator17.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator17.setName("jSeparator17"); // NOI18N
        panelGlass9.add(jSeparator17);
        jSeparator17.setBounds(10, 100, 420, 10);

        jSeparator18.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator18.setName("jSeparator18"); // NOI18N
        panelGlass9.add(jSeparator18);
        jSeparator18.setBounds(10, 370, 420, 10);

        jSeparator19.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator19.setName("jSeparator19"); // NOI18N
        panelGlass9.add(jSeparator19);
        jSeparator19.setBounds(10, 130, 420, 10);

        jSeparator20.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator20.setName("jSeparator20"); // NOI18N
        panelGlass9.add(jSeparator20);
        jSeparator20.setBounds(10, 160, 420, 10);

        jSeparator21.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator21.setName("jSeparator21"); // NOI18N
        panelGlass9.add(jSeparator21);
        jSeparator21.setBounds(10, 190, 420, 10);

        jSeparator22.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator22.setName("jSeparator22"); // NOI18N
        panelGlass9.add(jSeparator22);
        jSeparator22.setBounds(10, 220, 420, 10);

        jSeparator23.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator23.setName("jSeparator23"); // NOI18N
        panelGlass9.add(jSeparator23);
        jSeparator23.setBounds(10, 250, 420, 10);

        jSeparator24.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator24.setName("jSeparator24"); // NOI18N
        panelGlass9.add(jSeparator24);
        jSeparator24.setBounds(10, 280, 420, 10);

        jSeparator25.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator25.setName("jSeparator25"); // NOI18N
        panelGlass9.add(jSeparator25);
        jSeparator25.setBounds(20, 340, 410, 10);

        grupCode.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        grupCode.setName("grupCode"); // NOI18N
        panelGlass9.add(grupCode);
        grupCode.setBounds(300, 450, 60, 23);

        jSeparator26.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator26.setName("jSeparator26"); // NOI18N
        panelGlass9.add(jSeparator26);
        jSeparator26.setBounds(10, 70, 420, 10);

        jLabel117.setText("Rp. ");
        jLabel117.setName("jLabel117"); // NOI18N
        panelGlass9.add(jLabel117);
        jLabel117.setBounds(290, 40, 30, 23);

        lblTarifRS.setText("0");
        lblTarifRS.setName("lblTarifRS"); // NOI18N
        panelGlass9.add(lblTarifRS);
        lblTarifRS.setBounds(320, 40, 100, 23);

        jLabel118.setText("Base Tarif INACBG :");
        jLabel118.setName("jLabel118"); // NOI18N
        panelGlass9.add(jLabel118);
        jLabel118.setBounds(10, 80, 100, 23);

        jLabel119.setText("Rp. ");
        jLabel119.setName("jLabel119"); // NOI18N
        panelGlass9.add(jLabel119);
        jLabel119.setBounds(290, 70, 30, 23);

        lblBaseTariff.setText("0");
        lblBaseTariff.setName("lblBaseTariff"); // NOI18N
        panelGlass9.add(lblBaseTariff);
        lblBaseTariff.setBounds(320, 70, 100, 23);

        jSeparator27.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator27.setName("jSeparator27"); // NOI18N
        panelGlass9.add(jSeparator27);
        jSeparator27.setBounds(10, 40, 420, 10);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        Txtgroup.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Txtgroup.setColumns(20);
        Txtgroup.setRows(5);
        Txtgroup.setName("Txtgroup"); // NOI18N
        Txtgroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TxtgroupKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(Txtgroup);

        panelGlass9.add(scrollPane1);
        scrollPane1.setBounds(120, 290, 170, 50);

        jLabel120.setText("Selisih :");
        jLabel120.setName("jLabel120"); // NOI18N
        panelGlass9.add(jLabel120);
        jLabel120.setBounds(10, 380, 100, 23);

        jLabel122.setText("Rp. ");
        jLabel122.setName("jLabel122"); // NOI18N
        panelGlass9.add(jLabel122);
        jLabel122.setBounds(290, 370, 30, 23);

        lblSelisih.setText("0");
        lblSelisih.setName("lblSelisih"); // NOI18N
        panelGlass9.add(lblSelisih);
        lblSelisih.setBounds(320, 370, 100, 23);

        jSeparator28.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator28.setName("jSeparator28"); // NOI18N
        panelGlass9.add(jSeparator28);
        jSeparator28.setBounds(10, 430, 420, 10);

        lJenis1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lJenis1.setName("lJenis1"); // NOI18N
        panelGlass9.add(lJenis1);
        lJenis1.setBounds(120, 80, 180, 23);

        cmbSelisih.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Peserta", "Pemberi Kerja", "Asuransi Tambahan" }));
        cmbSelisih.setName("cmbSelisih"); // NOI18N
        cmbSelisih.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSelisihItemStateChanged(evt);
            }
        });
        panelGlass9.add(cmbSelisih);
        cmbSelisih.setBounds(120, 410, 160, 23);

        jLabel116.setText("BILLING RS :");
        jLabel116.setName("jLabel116"); // NOI18N
        panelGlass9.add(jLabel116);
        jLabel116.setBounds(10, 50, 100, 23);

        scrollRespon1.setViewportView(panelGlass9);

        FormINACBG.add(scrollRespon1);
        scrollRespon1.setBounds(970, 50, 470, 450);

        TabRawat.addTab("INACBG", FormINACBG);

        FormDATAKLAIM.add(TabRawat, java.awt.BorderLayout.CENTER);

        Scroll9.setViewportView(FormDATAKLAIM);

        getContentPane().add(Scroll9, java.awt.BorderLayout.CENTER);

        panelBawah.setName("panelBawah"); // NOI18N
        panelBawah.setPreferredSize(new java.awt.Dimension(44, 54));
        panelBawah.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        hapusKlaim4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete_folder.png"))); // NOI18N
        hapusKlaim4.setMnemonic('K');
        hapusKlaim4.setText("Hapus Claim");
        hapusKlaim4.setToolTipText("Alt+K");
        hapusKlaim4.setEnabled(false);
        hapusKlaim4.setGlassColor(new java.awt.Color(255, 102, 102));
        hapusKlaim4.setName("hapusKlaim4"); // NOI18N
        hapusKlaim4.setPreferredSize(new java.awt.Dimension(130, 30));
        hapusKlaim4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusKlaim4ActionPerformed(evt);
            }
        });
        hapusKlaim4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                hapusKlaim4KeyPressed(evt);
            }
        });
        panelBawah.add(hapusKlaim4);

        BtnGrouperIDRG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/users.png"))); // NOI18N
        BtnGrouperIDRG.setMnemonic('B');
        BtnGrouperIDRG.setText("Grouping IDRG");
        BtnGrouperIDRG.setToolTipText("Alt+B");
        BtnGrouperIDRG.setGlassColor(new java.awt.Color(0, 255, 153));
        BtnGrouperIDRG.setName("BtnGrouperIDRG"); // NOI18N
        BtnGrouperIDRG.setPreferredSize(new java.awt.Dimension(150, 30));
        BtnGrouperIDRG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGrouperIDRGActionPerformed(evt);
            }
        });
        panelBawah.add(BtnGrouperIDRG);

        finalklaimIDRG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        finalklaimIDRG.setText("Final IDRG");
        finalklaimIDRG.setToolTipText("Alt+S");
        finalklaimIDRG.setGlassColor(new java.awt.Color(0, 255, 153));
        finalklaimIDRG.setName("finalklaimIDRG"); // NOI18N
        finalklaimIDRG.setPreferredSize(new java.awt.Dimension(120, 30));
        finalklaimIDRG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalklaimIDRGActionPerformed(evt);
            }
        });
        panelBawah.add(finalklaimIDRG);

        editKlaimIDRG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        editKlaimIDRG.setText("Edit IDRG");
        editKlaimIDRG.setToolTipText("Alt+S");
        editKlaimIDRG.setGlassColor(new java.awt.Color(0, 255, 153));
        editKlaimIDRG.setName("editKlaimIDRG"); // NOI18N
        editKlaimIDRG.setPreferredSize(new java.awt.Dimension(120, 30));
        editKlaimIDRG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editKlaimIDRGActionPerformed(evt);
            }
        });
        panelBawah.add(editKlaimIDRG);

        BtnGrouperINA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/users.png"))); // NOI18N
        BtnGrouperINA.setMnemonic('B');
        BtnGrouperINA.setText("Grouping INACBG");
        BtnGrouperINA.setToolTipText("Alt+B");
        BtnGrouperINA.setGlassColor(new java.awt.Color(0, 255, 153));
        BtnGrouperINA.setName("BtnGrouperINA"); // NOI18N
        BtnGrouperINA.setPreferredSize(new java.awt.Dimension(150, 30));
        BtnGrouperINA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGrouperINAActionPerformed(evt);
            }
        });
        BtnGrouperINA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnGrouperINAKeyPressed(evt);
            }
        });
        panelBawah.add(BtnGrouperINA);

        finalINA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        finalINA.setText("Final INACBG");
        finalINA.setToolTipText("Alt+S");
        finalINA.setGlassColor(new java.awt.Color(0, 255, 153));
        finalINA.setName("finalINA"); // NOI18N
        finalINA.setPreferredSize(new java.awt.Dimension(120, 30));
        finalINA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalINAActionPerformed(evt);
            }
        });
        panelBawah.add(finalINA);

        editINA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        editINA.setText("Edit INACBG");
        editINA.setToolTipText("Alt+S");
        editINA.setGlassColor(new java.awt.Color(0, 255, 153));
        editINA.setName("editINA"); // NOI18N
        editINA.setPreferredSize(new java.awt.Dimension(120, 30));
        editINA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editINAActionPerformed(evt);
            }
        });
        panelBawah.add(editINA);

        finalklaim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        finalklaim.setText("Final Klaim");
        finalklaim.setToolTipText("Alt+S");
        finalklaim.setGlassColor(new java.awt.Color(0, 255, 153));
        finalklaim.setName("finalklaim"); // NOI18N
        finalklaim.setPreferredSize(new java.awt.Dimension(120, 30));
        finalklaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalklaimActionPerformed(evt);
            }
        });
        panelBawah.add(finalklaim);

        editKlaim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/EDIT2.png"))); // NOI18N
        editKlaim.setText("Edit Klaim");
        editKlaim.setToolTipText("Alt+S");
        editKlaim.setGlassColor(new java.awt.Color(0, 255, 153));
        editKlaim.setName("editKlaim"); // NOI18N
        editKlaim.setPreferredSize(new java.awt.Dimension(120, 30));
        editKlaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editKlaimActionPerformed(evt);
            }
        });
        panelBawah.add(editKlaim);

        kirimonline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2.png"))); // NOI18N
        kirimonline.setText("Kirim Online");
        kirimonline.setToolTipText("Alt+S");
        kirimonline.setGlassColor(new java.awt.Color(0, 255, 153));
        kirimonline.setName("kirimonline"); // NOI18N
        kirimonline.setPreferredSize(new java.awt.Dimension(120, 30));
        kirimonline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kirimonlineActionPerformed(evt);
            }
        });
        panelBawah.add(kirimonline);

        CetakKlaim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/PrinterSettings.png"))); // NOI18N
        CetakKlaim.setText("Cetak Klaim");
        CetakKlaim.setToolTipText("Alt+S");
        CetakKlaim.setGlassColor(new java.awt.Color(0, 255, 153));
        CetakKlaim.setName("CetakKlaim"); // NOI18N
        CetakKlaim.setPreferredSize(new java.awt.Dimension(120, 30));
        CetakKlaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CetakKlaimActionPerformed(evt);
            }
        });
        panelBawah.add(CetakKlaim);

        BtnKeluar4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar4.setMnemonic('K');
        BtnKeluar4.setText("Keluar");
        BtnKeluar4.setToolTipText("Alt+K");
        BtnKeluar4.setGlassColor(new java.awt.Color(255, 102, 102));
        BtnKeluar4.setName("BtnKeluar4"); // NOI18N
        BtnKeluar4.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluar4ActionPerformed(evt);
            }
        });
        BtnKeluar4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluar4KeyPressed(evt);
            }
        });
        panelBawah.add(BtnKeluar4);

        getContentPane().add(panelBawah, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        Scroll9.getVerticalScrollBar().setUnitIncrement(20);
        Scroll4.getVerticalScrollBar().setUnitIncrement(20);

        TabRawatMouseClicked(null);
    }//GEN-LAST:event_formWindowOpened

    private void klaimbaru() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + " \"metadata\": {\n"
                        + " \"method\": \"new_claim\"\n"
                        + "},\n"
                        + " \"data\": {\n"
                        + "\"nomor_kartu\": \"" + no_kartu.getText().trim() + "\",\n"
                        + "\"nomor_sep\": \"" + Tsep.getText().trim() + "\",\n"
                        + "\"nomor_rm\": \"" + TNoRM.getText().trim() + "\",\n"
                        + "\"nama_pasien\": \"" + TPasien.getText().trim() + "\",\n"
                        + "\"tgl_lahir\": \"" + tgl_lahir.getText().trim() + " 00:00:00\",\n"
                        + "\"gender\": \"" + (jenis_kelamin.getText().trim().equals("L") ? 1 : 2) + "\""
                        + " }\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);

                JSONObject response = inacbg.request(request);

                System.out.println("respon klaimbaru : " + response);

                if (response.has("metadata")) {
                    JSONObject code = response.getJSONObject("metadata");
                    if (code.getInt("code") == 200) {
                        JSONObject data = response.getJSONObject("response");
                        if (data.has("patient_id")) {
                            if (Sequel.menyimpantf2("inacbg_klaim_baru", "?,?,?,?", "EKLAIM", 4, new String[]{
                                Tsep.getText(), data.getInt("patient_id") + "", data.getInt("admission_id") + "", data.getInt("hospital_admission_id") + ""
                            }) == false) {
                                Sequel.mengedit("inacbg_klaim_baru", "no_sep='" + Tsep.getText() + "'", "patient_id='" + data.getInt("patient_id") + "',admission_id='" + data.getInt("admission_id") + "',hospital_admission_id='" + data.getInt("hospital_admission_id") + "'");
                            }
                        }
                        System.out.println("RESPON : " + code.getString("message"));
                    } else {
                        if (!code.getString("message").contains("Duplikasi")) {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void grouper1() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"grouper\",\n"
                        + "\"stage\":\"1\",\n"
                        + "\"grouper\":\"inacbg\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response grouper1 : " + response);

                if (response.has("metadata")) {
                    if (code.getInt("code") == 200) {
                        Sequel.meghapus("inacbg_grouping_stage1", "no_sep", Tsep.getText());
                        Sequel.meghapus("inacbg_grouping_stage2", "no_sep", Tsep.getText());
                        Sequel.meghapus("inacbg_prosedur", "no_sep", Tsep.getText());
                        Sequel.meghapus("inacbg_diagnosa", "no_sep", Tsep.getText());

                        JSONObject data = response.getJSONObject("response_inacbg");
                        JSONObject trf = data.getJSONObject("cbg");
                        if (data.has("tariff")) {
                            Sequel.menyimpantf2("inacbg_grouping_stage1", "?,?,?,?", "GROUPER EKLAIM", 4, new String[]{
                                Tsep.getText(), trf.getString("code"), trf.getString("description"), data.getDouble("tariff") + ""
                            });

                            for (i = 0; i < kd_diag2.length; i++) {
                                if (!kd_diag2[i].getText().trim().equals("")) {
                                    Sequel.menyimpantf2("inacbg_diagnosa", "?,?,?,?", "INA EKLAIM", 4, new String[]{
                                        Tsep.getText(), kd_diag2[i].getText(), nm_diag2[i].getText(), (chk_diag2[i].isSelected() == true ? "1" : "0")
                                    });
                                }
                            }

                            for (i = 0; i < kd_psdr2.length; i++) {
                                if (!kd_psdr2[i].getText().trim().equals("")) {
                                    Sequel.menyimpantf2("inacbg_prosedur", "?,?,?", "INA PSDR EKLAIM", 3, new String[]{
                                        Tsep.getText(), kd_psdr2[i].getText(), nm_psdr2[i].getText()
                                    });
                                }
                            }

                            lblTarifRS.setText(tarifRS.getText());
                            lblBaseTariff.setText(Valid.SetAngka(data.getDouble("tariff")));
                            Txtgroup.setText(trf.getString("description"));
                            lJenis1.setText(trf.getString("code"));
                            lblSelisih.setText("" + Sequel.cariIsi("select biaya from inacbg_selisihbayar where no_sep='" + Tsep.getText().trim() + "'"));

                            JOptionPane.showMessageDialog(this, "RESPON : GROUPER BERHASIL");
                        } else {
                            lblBaseTariff.setText("0");
                            Txtgroup.setText(trf.getString("description"));
                            lJenis1.setText(trf.getString("code"));
                            JOptionPane.showMessageDialog(this, "RESPON : GROUPER GAGAL");
                        }

                        if (data.has("add_payment_amt") && data.optDouble("add_payment_amt", 0.0) > 0) {
                            cmbSelisih.setEnabled(true);
                            lblSelisih.setText(Valid.SetAngka(data.getDouble("add_payment_amt")));
                        } else {
                            cmbSelisih.setEnabled(false);
                            lblSelisih.setText("0");
                        }

                        if (response.has("special_cmg_option")) {
                            isLoading = true;

                            cmbProc.setEnabled(true);
                            cmbDrugs.setEnabled(true);
                            cmbPros.setEnabled(true);
                            cmbInv.setEnabled(true);

                            cmbProc.removeAllItems();
                            cmbPros.removeAllItems();
                            cmbInv.removeAllItems();
                            cmbDrugs.removeAllItems();

                            // tambah default
                            cmbProc.addItem("none : None");
                            cmbPros.addItem("none : None");
                            cmbInv.addItem("none : None");
                            cmbDrugs.addItem("none : None");

                            isLoading = false;

                            JOptionPane.showMessageDialog(this, "PASIEN INI MEMILIKI SPESIAL PROSEDUR");
                            System.out.println("spesial : " + response.getJSONArray("special_cmg_option"));
                            JSONArray spesial = response.getJSONArray("special_cmg_option");
//                            
                            for (int i = 0; i < spesial.length(); i++) {
                                JSONObject cmg = spesial.getJSONObject(i);
                                if ("Special Prosthesis".equals(cmg.getString("type"))) {
                                    cmbPros.addItem(cmg.getString("code") + " : " + cmg.getString("description"));
                                }

                                if ("Special Procedure".equals(cmg.getString("type"))) {
                                    cmbProc.addItem(cmg.getString("code") + " : " + cmg.getString("description"));
                                }

                                if ("Special Investigation".equals(cmg.getString("type"))) {
                                    cmbProc.addItem(cmg.getString("code") + " : " + cmg.getString("description"));
                                }

                                if ("Special Drug".equals(cmg.getString("type"))) {
                                    cmbProc.addItem(cmg.getString("code") + " : " + cmg.getString("description"));
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getCodeOnly(JComboBox combo) {
        if (combo.getSelectedItem() == null) {
            return "";
        }
        String val = combo.getSelectedItem().toString();
        if (val.toLowerCase().contains("none")) {
            return "";
        }
        String[] parts = val.split(":", 2); // ambil sebelum tanda ":"
        return parts[0].trim();
    }

    private String buildSpecialCmg() {
        StringJoiner joiner = new StringJoiner("#");

        String code1 = getCodeOnly(cmbProc);
        String code2 = getCodeOnly(cmbPros);
        String code3 = getCodeOnly(cmbInv);
        String code4 = getCodeOnly(cmbDrugs);

        if (!code1.isEmpty()) {
            joiner.add(code1);
        }
        if (!code2.isEmpty()) {
            joiner.add(code2);
        }
        if (!code3.isEmpty()) {
            joiner.add(code3);
        }
        if (!code4.isEmpty()) {
            joiner.add(code4);
        }

        return joiner.toString();
    }

    private void grouper2() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"grouper\",\n"
                        + "\"stage\":\"2\",\n"
                        + "\"grouper\":\"inacbg\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\",\n"
                        + "\"special_cmg\": \""
                        + buildSpecialCmg()
                        + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response grouper2 : " + response);

                if (response.has("metadata")) {
                    JSONObject meta = response.getJSONObject("metadata");
                    if (meta.getInt("code") == 200) {
                        JSONObject data = response.getJSONObject("response_inacbg");

                        JOptionPane.showMessageDialog(this, "GROUPER SPESIAL BERHASIL");

                        // hapus data lama dulu
                        Sequel.meghapus("inacbg_grouping_stage2", "no_sep", Tsep.getText());

                        // ambil special_cmg kalau ada
                        if (data.has("special_cmg")) {
                            JSONArray trf = data.getJSONArray("special_cmg");
                            System.out.println(trf);
                            for (int i = 0; i < trf.length(); i++) {
                                JSONObject obj = trf.getJSONObject(i);

                                String type = obj.getString("type").toLowerCase();
                                double tarif = obj.getDouble("tariff");

                                if (type.contains("procedure")) {
                                    lbltarifProc.setText(Valid.SetAngka(tarif));
                                    Sequel.menyimpantf2("inacbg_grouping_stage2", "?,?,?,?,?", "GROUPER EKLAIM", 5,
                                            new String[]{
                                                Tsep.getText(),
                                                obj.optString("code", ""),
                                                cmbProc.getSelectedItem().toString(),
                                                tarif + "",
                                                obj.getString("type")
                                            });
                                } else if (type.contains("prosthesis")) {
                                    lblTarifPros.setText(Valid.SetAngka(tarif));
                                    Sequel.menyimpantf2("inacbg_grouping_stage2", "?,?,?,?,?", "GROUPER EKLAIM", 5,
                                            new String[]{
                                                Tsep.getText(),
                                                obj.optString("code", ""),
                                                cmbPros.getSelectedItem().toString(),
                                                tarif + "",
                                                obj.getString("type")
                                            });
                                } else if (type.contains("investigation")) {
                                    lblTarifInv.setText(Valid.SetAngka(tarif));
                                    Sequel.menyimpantf2("inacbg_grouping_stage2", "?,?,?,?,?", "GROUPER EKLAIM", 5,
                                            new String[]{
                                                Tsep.getText(),
                                                obj.optString("code", ""),
                                                cmbInv.getSelectedItem().toString(),
                                                tarif + "",
                                                obj.getString("type")
                                            });
                                } else if (type.contains("drugs")) {
                                    lblTarifDrugs.setText(Valid.SetAngka(tarif));
                                    Sequel.menyimpantf2("inacbg_grouping_stage2", "?,?,?,?,?", "GROUPER EKLAIM", 5,
                                            new String[]{
                                                Tsep.getText(),
                                                obj.optString("code", ""),
                                                cmbDrugs.getSelectedItem().toString(),
                                                tarif + "",
                                                obj.getString("type")
                                            });
                                }
                            }
                        }

                        // set label tarif dasar & total
                        if (data.has("base_tariff")) {
                            lblBaseTariff.setText(Valid.SetAngka(data.getDouble("base_tariff")));
                        }
                        if (data.has("tariff")) {
                            lblTarifTotal.setText(Valid.SetAngka(data.getDouble("tariff")));
                        }

                    } else {
                        System.out.println("response : " + response);
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'response_inacbg' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setdataklaim() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            switch (jaminan.getSelectedIndex()) {
                case 0:
                    payor_id = 3 + "";
                    payor_cd = "JKN";
                    break;
                case 1:
                    payor_id = 71 + "";
                    payor_cd = "COVID-19";
                    break;
                case 2:
                    payor_id = 72 + "";
                    payor_cd = "KIPI";
                    break;
                case 3:
                    payor_id = 73 + "";
                    payor_cd = "BBL";
                    break;
                case 4:
                    payor_id = 74 + "";
                    payor_cd = "PMR";
                    break;
                case 5:
                    payor_id = 75 + "";
                    payor_cd = "CO-INS";
                    break;
                case 6:
                    payor_id = 76 + "";
                    payor_cd = "JPS";
                    break;
                case 7:
                    payor_id = 77 + "";
                    payor_cd = "JPKP";
                    break;
                default:
                    payor_id = 3 + "";
                    payor_cd = "JKN";
                    break;
            }
            try {
//                
                String requestJson = "{\n"
                        + "  \"metadata\": {\n"
                        + "        \"method\": \"set_claim_data\",\n"
                        + "        \"nomor_sep\": \"" + Tsep.getText() + "\"\n"
                        + "    },\n"
                        + "    \"data\": {\n"
                        + "        \"nomor_sep\": \"" + Tsep.getText() + "\",\n"
                        + "        \"nomor_kartu\": \"" + no_kartu.getText() + "\",\n"
                        + "        \"tgl_masuk\": \"" + tgl_masuk.getSelectedItem() + " " + jam_masuk.getSelectedItem() + ":" + menit_masuk.getSelectedItem() + ":00\",\n"
                        + "        \"tgl_pulang\": \"" + tgl_keluar.getSelectedItem() + " " + jam_keluar.getSelectedItem() + ":" + menit_keluar.getSelectedItem() + ":00\",\n"
                        + "        \"cara_masuk\": \"" + cara_masuk.getSelectedItem().toString().split(":")[0].trim() + "\",\n"
                        + "        \"jenis_rawat\": \"" + jenis_rawat.getSelectedItem().toString().split(":")[0].trim() + "\",\n"
                        + "        \"kelas_rawat\": \"" + hak_kelas.getSelectedItem().toString().split(" ")[1].trim() + "\",\n"
                        + "        \"adl_sub_acute\": \"" + sub_acute.getText() + "\",\n"
                        + "        \"adl_chronic\": \"" + chronic.getText() + "\",\n"
                        + "        \"icu_indikator\": \"" + (rawat_intensif.isSelected() == true ? 1 : 0) + "\",\n"
                        + (rawat_intensif.isSelected() == true
                        ? "        \"icu_los\": \"" + hari_intensif.getText() + "\",\n"
                        + "        \"ventilator_hour\": \"" + jam_ventilator.getText() + "\",\n"
                        + "        \"ventilator\": {\n"
                        + "            \"use_ind\": \"" + (ventilator.isSelected() == true ? 1 : 0) + "\",\n"
                        + "            \"start_dttm\": \"" + tgl_intubasi.getSelectedItem() + " " + jam_intubasi.getSelectedItem() + ":" + menit_intubasi.getSelectedItem() + ":00\",\n"
                        + "            \"stop_dttm\": \"" + tgl_ekstubasi.getSelectedItem() + " " + jam_ekstubasi.getSelectedItem() + ":" + menit_ekstubasi.getSelectedItem() + ":00\"\n"
                        + "        },\n"
                        : "")
                        + "        \"upgrade_class_ind\": \"" + (naik_turun_kelas.isSelected() == true ? 1 : 0) + "\",\n"
                        + (naik_turun_kelas.isSelected() == true
                        ? "        \"upgrade_class_class\": \"" + naik_kelas_pelayanan.getSelectedItem().toString().split(":")[0].trim() + "\",\n"
                        + "        \"upgrade_class_los\": \"" + lama_naik_kelas.getText().trim() + "\",\n"
                        + "        \"upgrade_class_payor\": \"peserta\",\n"
                        + "        \"add_payment_pct\": \"35\",\n"
                        : "")
                        + "        \"sistole\": \"" + sistole.getText() + "\",\n"
                        + "        \"diastole\": \"" + distole.getText() + "\",\n"
                        + "        \"discharge_status\": \"" + status_pulang.getSelectedItem().toString().split(":")[0].trim() + "\",\n"
                        + (check_hd.isSelected() == true
                        ? "        \"dializer_single_use\": \"" + pasien_hd.getSelectedIndex() + "\",\n"
                        + "        \"kantong_darah\": \"" + kt_darah.getText().trim() + "\",\n"
                        : "")
                        + "        \"birth_weight\": \"" + berat_bayi.getText() + "\",\n"
                        + //                                        "        \"diagnosa\": \""+icd10.getText()+"\",\n" +
                        //                                        "        \"procedure\": \""+icd9.getText()+"\",\n" +
                        //                                        "        \"diagnosa_inagrouper\": \""+icd10.getText()+"\",\n" +
                        //                                        "        \"procedure_inagrouper\": \""+icd9.getText()+"\",\n" +
                        "        \"tarif_rs\": {\n"
                        + "            \"prosedur_non_bedah\": \"" + pnb.getText() + "\",\n"
                        + "            \"prosedur_bedah\": \"" + pb.getText() + "\",\n"
                        + "            \"konsultasi\": \"" + ksl.getText() + "\",\n"
                        + "            \"tenaga_ahli\": \"" + ahli.getText() + "\",\n"
                        + "            \"keperawatan\": \"" + kpr.getText() + "\",\n"
                        + "            \"penunjang\": \"" + pnj.getText() + "\",\n"
                        + "            \"radiologi\": \"" + rad.getText() + "\",\n"
                        + "            \"laboratorium\": \"" + lab.getText() + "\",\n"
                        + "            \"pelayanan_darah\": \"" + pl_darah.getText() + "\",\n"
                        + "            \"rehabilitasi\": \"" + rehab.getText() + "\",\n"
                        + "            \"kamar\": \"" + akomodasi.getText() + "\",\n"
                        + "            \"rawat_intensif\": \"" + ri.getText() + "\",\n"
                        + "            \"obat\": \"" + obat.getText() + "\",\n"
                        + "            \"obat_kronis\": \"" + kronis.getText() + "\",\n"
                        + "            \"obat_kemoterapi\": \"" + obat_kemo.getText() + "\",\n"
                        + "            \"alkes\": \"" + alkes.getText() + "\",\n"
                        + "            \"bmhp\": \"" + bmhp.getText() + "\",\n"
                        + "            \"sewa_alat\": \"" + sewa_alat.getText() + "\"\n"
                        + "        },\n"
                        + //                                        "        \"pemulasaraan_jenazah\": \"1\",\n" +
                        //                                        "        \"kantong_jenazah\": \"1\",\n" +
                        //                                        "        \"peti_jenazah\": \"1\",\n" +
                        //                                        "        \"plastik_erat\": \"1\",\n" +
                        //                                        "        \"desinfektan_jenazah\": \"1\",\n" +
                        //                                        "        \"mobil_jenazah\": \"0\",\n" +
                        //                                        "        \"desinfektan_mobil_jenazah\": \"0\",\n" +
                        //                                        "        \"covid19_status_cd\": \"1\",\n" +
                        //                                        "        \"nomor_kartu_t\": \"nik\",\n" +
                        //                                        "        \"episodes\": \"1;12#2;3#6;5\",\n" +
                        //                                        "        \"covid19_cc_ind\": \"1\",\n" +
                        //                                        "        \"covid19_rs_darurat_ind\": \"1\",\n" +
                        //                                        "        \"covid19_co_insidense_ind\": \"1\",\n" +
                        //                                        "        \"covid19_penunjang_pengurang\": {\n" +
                        //                                        "            \"lab_asam_laktat\": \"1\",\n" +
                        //                                        "            \"lab_procalcitonin\": \"1\",\n" +
                        //                                        "            \"lab_crp\": \"1\",\n" +
                        //                                        "            \"lab_kultur\": \"1\",\n" +
                        //                                        "            \"lab_d_dimer\": \"1\",\n" +
                        //                                        "            \"lab_pt\": \"1\",\n" +
                        //                                        "            \"lab_aptt\": \"1\",\n" +
                        //                                        "            \"lab_waktu_pendarahan\": \"1\",\n" +
                        //                                        "            \"lab_anti_hiv\": \"1\",\n" +
                        //                                        "            \"lab_analisa_gas\": \"1\",\n" +
                        //                                        "            \"lab_albumin\": \"1\",\n" +
                        //                                        "            \"rad_thorax_ap_pa\": \"0\"\n" +
                        //                                        "        },\n" +
                        //                                        "        \"terapi_konvalesen\": \"1000000\",\n" +
                        //                                        "        \"akses_naat\": \"C\",\n" +
                        //                                        "        \"isoman_ind\": \"0\",\n" +
                        //                                        "        \"bayi_lahir_status_cd\": 1,\n" +
                        //                                        "        \"alteplase_ind\": 0,\n" +
                        //                                        "        \"apgar\": {\n" +
                        //                                        "            \"menit_1\": {\n" +
                        //                                        "                \"appearance\": "+ap_1.getText()+",\n" +
                        //                                        "                \"pulse\": "+ap_2.getText()+",\n" +
                        //                                        "                \"grimace\": "+ap_3.getText()+",\n" +
                        //                                        "                \"activity\": "+ap_4.getText()+",\n" +
                        //                                        "                \"respiration\": "+ap_5.getText()+"\n" +
                        //                                        "            },\n" +
                        //                                        "            \"menit_5\": {\n" +
                        //                                        "                \"appearance\": "+ap_6.getText()+",\n" +
                        //                                        "                \"pulse\": "+ap_7.getText()+",\n" +
                        //                                        "                \"grimace\": "+ap_8.getText()+",\n" +
                        //                                        "                \"activity\": "+ap_9.getText()+",\n" +
                        //                                        "                \"respiration\": "+ap_10.getText()+"\n" +
                        //                                        "            }\n" +
                        //                                        "        },\n" +
                        //                                        "        \"persalinan\": {\n" +
                        //                                        "            \"usia_kehamilan\": \"22\",\n" +
                        //                                        "            \"gravida\": \"2\",\n" +
                        //                                        "            \"partus\": \"4\",\n" +
                        //                                        "            \"abortus\": \"2\",\n" +
                        //                                        "            \"onset_kontraksi\": \"induksi\",\n" +
                        //                                        "            \"delivery\": [\n" +
                        //                                        "                {\n" +
                        //                                        "                    \"delivery_sequence\": \"1\",\n" +
                        //                                        "                    \"delivery_method\": \"vaginal\",\n" +
                        //                                        "                    \"delivery_dttm\": \"2023-01-21 17:01:33\",\n" +
                        //                                        "                    \"letak_janin\": \"kepala\",\n" +
                        //                                        "                    \"kondisi\": \"livebirth\",\n" +
                        //                                        "                    \"use_manual\": \"1\",\n" +
                        //                                        "                    \"use_forcep\": \"0\",\n" +
                        //                                        "                    \"use_vacuum\": \"1\",\n" +
                        //                                        "                    \"shk_spesimen_ambil\": \"ya\",\n" +
                        //                                        "                    \"shk_lokasi\": \"tumit\",\n" +
                        //                                        "                    \"shk_spesimen_dttm\": \"2023-01-21 18:11:33\"\n" +
                        //                                        "                },\n" +
                        //                                        "                {\n" +
                        //                                        "                    \"delivery_sequence\": \"2\",\n" +
                        //                                        "                    \"delivery_method\": \"vaginal\",\n" +
                        //                                        "                    \"delivery_dttm\": \"2023-01-21 17:03:49\",\n" +
                        //                                        "                    \"letak_janin\": \"lintang\",\n" +
                        //                                        "                    \"kondisi\": \"livebirth\",\n" +
                        //                                        "                    \"use_manual\": \"1\",\n" +
                        //                                        "                    \"use_forcep\": \"0\",\n" +
                        //                                        "                    \"use_vacuum\": \"0\",\n" +
                        //                                        "                    \"shk_spesimen_ambil\": \"tidak\",\n" +
                        //                                        "                    \"shk_alasan\": \"akses-sulit\"\n" +
                        //                                        "                }\n" +
                        //                                        "            ]\n" +
                        //                                        "        },\n" +
                        "        \"tarif_poli_eks\": \"" + (tarif_eksekutif.getText().equals(0) ? 0 : tarif_eksekutif.getText()) + "\",\n"
                        + "        \"nama_dokter\": \"" + dpjp.getText() + "\",\n"
                        + "        \"kode_tarif\": \"" + koneksiDB.KELASRSEKLAIM() + "\",\n"
                        + "        \"payor_id\": \"" + payor_id + "\",\n"
                        + "        \"payor_cd\": \"" + payor_cd + "\",\n"
                        + "        \"cob_cd\": \"" + cob.getSelectedItem().toString().split(":")[0].trim() + "\",\n"
                        + "        \"coder_nik\": \"" + coder + "\"\n"
                        + "    \n}"
                        + " }\n";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);

                JSONObject response = inacbg.request(request);
                System.out.println("response data klaim : " + response);

                if (response.has("metadata")) {
                    JSONObject code = response.getJSONObject("metadata");
                    if (code.getInt("code") == 200) {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        Sequel.meghapus("inacbg_data_terkirim", "no_sep", Tsep.getText());
                        Sequel.menyimpantf2("inacbg_data_terkirim", "?,?", "DATA EKLAIM", 2, new String[]{
                            Tsep.getText(), coder
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRMKeyPressed

    private void pnbKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnbKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pnbKeyReleased

    private void pnbKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnbKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_pnbKeyTyped

    private void ahliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ahliKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ahliKeyReleased

    private void jenis_rawatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jenis_rawatItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jenis_rawatItemStateChanged

    private void rehabKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rehabKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rehabKeyReleased

    private void radKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_radKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_radKeyReleased

    private void alkesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_alkesKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_alkesKeyReleased

    private void alkesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_alkesKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_alkesKeyTyped

    private void obatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_obatKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_obatKeyReleased

    private void pbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pbKeyPressed

    private void pbKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pbKeyReleased

    private void kprKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kprKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kprKeyPressed

    private void kprKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kprKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_kprKeyReleased

    private void akomodasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_akomodasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_akomodasiKeyPressed

    private void akomodasiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_akomodasiKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_akomodasiKeyReleased

    private void labKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_labKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_labKeyPressed

    private void labKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_labKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_labKeyReleased

    private void bmhpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bmhpKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_bmhpKeyPressed

    private void bmhpKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bmhpKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_bmhpKeyReleased

    private void kronisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kronisKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kronisKeyPressed

    private void kronisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kronisKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_kronisKeyReleased

    private void kslKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kslKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kslKeyPressed

    private void kslKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kslKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_kslKeyReleased

    private void pnjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnjKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pnjKeyPressed

    private void pnjKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pnjKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pnjKeyReleased

    private void riKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_riKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_riKeyPressed

    private void riKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_riKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_riKeyReleased

    private void pl_darahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pl_darahKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_pl_darahKeyPressed

    private void pl_darahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pl_darahKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_pl_darahKeyReleased

    private void sewa_alatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sewa_alatKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_sewa_alatKeyPressed

    private void sewa_alatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sewa_alatKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_sewa_alatKeyReleased

    private void obat_kemoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_obat_kemoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_obat_kemoKeyPressed

    private void obat_kemoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_obat_kemoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_obat_kemoKeyReleased

    private void rawat_intensifItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rawat_intensifItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_rawat_intensifItemStateChanged

    private void ventilatorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ventilatorItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_ventilatorItemStateChanged

    private void naik_turun_kelasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_naik_turun_kelasItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_naik_turun_kelasItemStateChanged

    private void validasi_tbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validasi_tbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_validasi_tbActionPerformed

    private void kelas_eksekutifItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_kelas_eksekutifItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_kelas_eksekutifItemStateChanged

    private void batal_validasi_tbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_validasi_tbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_batal_validasi_tbActionPerformed

    private void check_hdItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_check_hdItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_check_hdItemStateChanged

    private void chk_diag_1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_1ItemStateChanged

    private void chk_diag_2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_2ItemStateChanged

    private void chk_diag_3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_3ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_3ItemStateChanged

    private void chk_diag_4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_4ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_4ItemStateChanged

    private void chk_diag_5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_5ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_5ItemStateChanged

    private void chk_diag_6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_6ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_6ItemStateChanged

    private void chk_diag_7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_7ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_7ItemStateChanged

    private void chk_diag_8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_8ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_8ItemStateChanged

    private void chk_diag_9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_9ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_9ItemStateChanged

    private void chk_diag_10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_10ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_10ItemStateChanged

    private void batal_10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_10ActionPerformed
        // TODO add your handling code here:
        kd_diag_10.setText("");
        nm_diag_10.setText("");
        chk_diag_10.setSelected(false);
    }//GEN-LAST:event_batal_10ActionPerformed

    private void batal_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_1ActionPerformed
        // TODO add your handling code here:
        kd_diag_1.setText("");
        nm_diag_1.setText("");
        chk_diag_1.setSelected(false);
    }//GEN-LAST:event_batal_1ActionPerformed

    private void batal_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_2ActionPerformed
        // TODO add your handling code here:
        kd_diag_2.setText("");
        nm_diag_2.setText("");
        chk_diag_2.setSelected(false);
    }//GEN-LAST:event_batal_2ActionPerformed

    private void batal_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_3ActionPerformed
        // TODO add your handling code here:
        kd_diag_3.setText("");
        nm_diag_3.setText("");
        chk_diag_3.setSelected(false);
    }//GEN-LAST:event_batal_3ActionPerformed

    private void batal_4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_4ActionPerformed
        // TODO add your handling code here:
        kd_diag_4.setText("");
        nm_diag_4.setText("");
        chk_diag_4.setSelected(false);
    }//GEN-LAST:event_batal_4ActionPerformed

    private void batal_5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_5ActionPerformed
        // TODO add your handling code here:
        kd_diag_5.setText("");
        nm_diag_5.setText("");
        chk_diag_5.setSelected(false);
    }//GEN-LAST:event_batal_5ActionPerformed

    private void batal_6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_6ActionPerformed
        // TODO add your handling code here:
        kd_diag_6.setText("");
        nm_diag_6.setText("");
        chk_diag_6.setSelected(false);
    }//GEN-LAST:event_batal_6ActionPerformed

    private void batal_7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_7ActionPerformed
        // TODO add your handling code here:
        kd_diag_7.setText("");
        nm_diag_7.setText("");
        chk_diag_7.setSelected(false);
    }//GEN-LAST:event_batal_7ActionPerformed

    private void batal_8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_8ActionPerformed
        // TODO add your handling code here:
        kd_diag_8.setText("");
        nm_diag_8.setText("");
        chk_diag_8.setSelected(false);
    }//GEN-LAST:event_batal_8ActionPerformed

    private void batal_9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_9ActionPerformed
        // TODO add your handling code here:
        kd_diag_9.setText("");
        nm_diag_9.setText("");
        chk_diag_9.setSelected(false);
    }//GEN-LAST:event_batal_9ActionPerformed

    private void batal_psdr_10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_10ActionPerformed
        // TODO add your handling code here:
        kd_psdr_10.setText("");
        nm_psdr_10.setText("");
        multiple10.setText("");
    }//GEN-LAST:event_batal_psdr_10ActionPerformed

    private void batal_psdr_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_1ActionPerformed
        // TODO add your handling code here:
        kd_psdr_1.setText("");
        nm_psdr_1.setText("");
        multiple1.setText("");
    }//GEN-LAST:event_batal_psdr_1ActionPerformed

    private void batal_psdr_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_2ActionPerformed
        // TODO add your handling code here:
        kd_psdr_2.setText("");
        nm_psdr_2.setText("");
        multiple2.setText("");
    }//GEN-LAST:event_batal_psdr_2ActionPerformed

    private void batal_psdr_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_3ActionPerformed
        // TODO add your handling code here:
        kd_psdr_3.setText("");
        nm_psdr_3.setText("");
        multiple3.setText("");
    }//GEN-LAST:event_batal_psdr_3ActionPerformed

    private void batal_psdr_4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_4ActionPerformed
        // TODO add your handling code here:
        kd_psdr_4.setText("");
        nm_psdr_4.setText("");
        multiple4.setText("");
    }//GEN-LAST:event_batal_psdr_4ActionPerformed

    private void batal_psdr_5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_5ActionPerformed
        // TODO add your handling code here:
        kd_psdr_5.setText("");
        nm_psdr_5.setText("");
        multiple5.setText("");
    }//GEN-LAST:event_batal_psdr_5ActionPerformed

    private void batal_psdr_6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_6ActionPerformed
        // TODO add your handling code here:
        kd_psdr_6.setText("");
        nm_psdr_6.setText("");
        multiple6.setText("");
    }//GEN-LAST:event_batal_psdr_6ActionPerformed

    private void batal_psdr_7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_7ActionPerformed
        // TODO add your handling code here:
        kd_psdr_7.setText("");
        nm_psdr_7.setText("");
        multiple7.setText("");
    }//GEN-LAST:event_batal_psdr_7ActionPerformed

    private void batal_psdr_8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_8ActionPerformed
        // TODO add your handling code here:
        kd_psdr_8.setText("");
        nm_psdr_8.setText("");
        multiple8.setText("");
    }//GEN-LAST:event_batal_psdr_8ActionPerformed

    private void batal_psdr_9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_9ActionPerformed
        // TODO add your handling code here:
        kd_psdr_9.setText("");
        nm_psdr_9.setText("");
        multiple9.setText("");
    }//GEN-LAST:event_batal_psdr_9ActionPerformed

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampildiagnosa();
        }
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void idrgPenyakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idrgPenyakitActionPerformed
        // TODO add your handling code here:
        tampildiagnosa();
    }//GEN-LAST:event_idrgPenyakitActionPerformed

    private void idrgPenyakittambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idrgPenyakittambahActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tbDiagnosa.getRowCount(); i++) {
            if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                String kode = tbDiagnosa.getValueAt(i, 1).toString();
                String nama = tbDiagnosa.getValueAt(i, 2).toString();
                String acc = tbDiagnosa.getValueAt(i, 3).toString();
                String asteriks = tbDiagnosa.getValueAt(i, 4).toString();
                String valid = tbDiagnosa.getValueAt(i, 5).toString();

                // ✅ Cek duplikasi dulu
                boolean sudahAda = false;
                for (int k = 0; k < 10; k++) {
                    if (kd_diag[k].getText().equals(kode)) {
                        sudahAda = true;
                        tbDiagnosa.setValueAt(false, i, 0);
                        break;
                    }
                }

                // ✅ Jika belum ada, tambah ke slot kosong
                if (!sudahAda) {
                    for (int j = 0; j < 10; j++) {
                        if (valid.trim().equals("0") && acc.trim().equals("Y") && asteriks.trim().equals("0")) {
                            JOptionPane.showMessageDialog(this, "DIAGNOSA INI TIDAK VALID UNTUK GROUPING IDRG");
                            break;
                        } else if (valid.trim().equals("0") && acc.trim().equals("N") && asteriks.trim().equals("0")) {
                            JOptionPane.showMessageDialog(this, "DIAGNOSA INI TIDAK VALID UNTUK GROUPING IDRG");
                            break;
                        } else if (valid.trim().equals("1") && acc.trim().equals("N") && asteriks.trim().equals("0") && kd_diag_1.getText().trim().equals("")) {
                            JOptionPane.showMessageDialog(this, "DIAGNOSA INI TIDAK BISA DI JADIKAN PRIMARY DI IDRG");
                            break;
                        } else if (valid.trim().equals("1") && acc.trim().equals("N") && asteriks.trim().equals("1") && kd_diag_1.getText().trim().equals("")) {
                            JOptionPane.showMessageDialog(this, "DIAGNOSA INI TIDAK BISA DI JADIKAN PRIMARY DI IDRG");
                            break;
                        } else {
                            if (kd_diag[j].getText().trim().equals("")) {
                                kd_diag[j].setText(kode);
                                nm_diag[j].setText(nama);
                                tbDiagnosa.setValueAt(false, i, 0);
                                break;
                            }
                        }
                    }
                } else {
                    // Jika sudah ada, tetap uncheck
                    tbDiagnosa.setValueAt(false, i, 0);
                }
            }
        }

    }//GEN-LAST:event_idrgPenyakittambahActionPerformed

    private void ProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilprosedure();
        }
    }//GEN-LAST:event_ProsedurKeyPressed

    private void idrgProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idrgProsedurActionPerformed
        // TODO add your handling code here:
        tampilprosedure();
    }//GEN-LAST:event_idrgProsedurActionPerformed

    private void idrgProseduttambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idrgProseduttambahActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tbProsedur.getRowCount(); i++) {
            if (tbProsedur.getValueAt(i, 0).toString().equals("true")) {
                String kode = tbProsedur.getValueAt(i, 1).toString();
                String nama = tbProsedur.getValueAt(i, 2).toString();
                String valid = tbProsedur.getValueAt(i, 5).toString();

                // Cek duplikasi
                boolean sudahAda = false;
                for (int k = 0; k < 10; k++) {  // ✅ 0-9 untuk 10 elemen
                    if (kd_psdr[k].getText().equals(kode)) {
                        sudahAda = true;
                        tbProsedur.setValueAt(false, i, 0);
                        break;
                    }
                }

                // Tambah ke slot kosong
                if (!sudahAda) {
                    for (int j = 0; j < 10; j++) {
                        if (valid.trim().equals("0")) {
                            JOptionPane.showMessageDialog(this, "PROSEDUR INI TIDAK VALID UNTUK GROUPING IDRG");
                            break;
                        } else {
                            if (kd_psdr[j].getText().trim().equals("")) {
                                kd_psdr[j].setText(kode);
                                nm_psdr[j].setText(nama);
                                multiple[j].setText("1");
                                tbProsedur.setValueAt(false, i, 0);
                                break;
                            }
                        }
//                        
                    }
                } else {
                    tbProsedur.setValueAt(false, i, 0);
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < kd_psdr.length; i++) {
            String val = kd_psdr[i].getText().trim() + (multiple[i].getText().trim().equals("") ? "" : (multiple[i].getText().trim().equals("1") ? "" : "+" + multiple[i].getText().trim()));
            if (!val.equals("")) {
                if (sb.length() > 0) {
                    sb.append("#"); // separator multiple
                }
                sb.append(val);
            }
        }

        icd9 = sb.toString();
        System.out.println("icd9 : " + icd9);
    }//GEN-LAST:event_idrgProseduttambahActionPerformed

    private void DiagnosaINACBGKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaINACBGKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampildiagnosaINA();
        }
    }//GEN-LAST:event_DiagnosaINACBGKeyPressed

    private void INAdiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INAdiagnosaActionPerformed
        // TODO add your handling code here:
        tampildiagnosaINA();
    }//GEN-LAST:event_INAdiagnosaActionPerformed

    private void INAdiagnosatambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INAdiagnosatambahActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tbDiagnosa2.getRowCount(); i++) {
            if (tbDiagnosa2.getValueAt(i, 0).toString().equals("true")) {
                String kode = tbDiagnosa2.getValueAt(i, 1).toString();
                String nama = tbDiagnosa2.getValueAt(i, 2).toString();

                // Cek duplikasi di slot 11-20
                boolean sudahAda = false;
                for (int k = 0; k < 10; k++) {
                    if (kd_diag2[k].getText().equals(kode)) {
                        sudahAda = true;
                        tbDiagnosa2.setValueAt(false, i, 0);
                        break;
                    }
                }

                // Tambah ke slot kosong jika belum ada
                if (!sudahAda) {
                    for (int j = 0; j < 10; j++) {
                        if (kd_diag2[j].getText().trim().equals("")) {
                            kd_diag2[j].setText(kode);
                            nm_diag2[j].setText(nama);
                            tbDiagnosa2.setValueAt(false, i, 0);
                            break;
                        }
                    }
                } else {
                    tbDiagnosa2.setValueAt(false, i, 0);
                }
            }
        }
    }//GEN-LAST:event_INAdiagnosatambahActionPerformed

    private void ProsedurINACBGKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurINACBGKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampilprosedureINA();
        }
    }//GEN-LAST:event_ProsedurINACBGKeyPressed

    private void INAprosedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INAprosedurActionPerformed
        // TODO add your handling code here:
        tampilprosedureINA();
    }//GEN-LAST:event_INAprosedurActionPerformed

    private void INAprosedurtambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INAprosedurtambahActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tbProsedur2.getRowCount(); i++) {
            if (tbProsedur2.getValueAt(i, 0).toString().equals("true")) {
                String kode = tbProsedur2.getValueAt(i, 1).toString();
                String nama = tbProsedur2.getValueAt(i, 2).toString();

                // Cek duplikasi di slot 11–20
                boolean sudahAda = false;
                for (int k = 0; k < 10; k++) {
                    if (kd_psdr2[k].getText().equals(kode)) {
                        sudahAda = true;
                        tbProsedur2.setValueAt(false, i, 0);
                        break;
                    }
                }

                // Tambah ke slot kosong 11–20
                if (!sudahAda) {
                    for (int j = 0; j < 10; j++) {
                        if (kd_psdr2[j].getText().trim().equals("")) {
                            kd_psdr2[j].setText(kode);
                            nm_psdr2[j].setText(nama);
                            tbProsedur2.setValueAt(false, i, 0);
                            break;
                        }
                    }
                } else {
                    tbProsedur2.setValueAt(false, i, 0);
                }
            }
        }

    }//GEN-LAST:event_INAprosedurtambahActionPerformed

    private void sistoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sistoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sistoleActionPerformed

    private void BtnGrouperIDRGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGrouperIDRGActionPerformed
        // TODO add your handling code here:

        boolean adaIsi = false;
        for (int i = 0; i < kd_diag.length; i++) {
            if (!kd_diag[i].getText().trim().equals("")) {
                adaIsi = true;
                break;
            }
        }

        boolean belumAdaDicentang = true;

        for (JCheckBox chk : chk_diag) {
            if (chk.isSelected()) {
                belumAdaDicentang = false;
                break;
            }
        }

        if (!adaIsi) {
            JOptionPane.showMessageDialog(this, "Belum ada diagnosa yang diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (belumAdaDicentang) {
            JOptionPane.showMessageDialog(this, "Belum ada diagnosa yang dicentang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            getICD9();
            getICD10();

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (Sequel.cariInteger("select count(no_sep) from inacbg_klaim_baru where no_sep='" + Tsep.getText().trim() + "' ") == 0) {
                klaimbaru();
            }
            setdataklaim();
            idrg_diagnosa();
            idrg_prosedur();
            grouperidrg();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            tombolinacbg();
            // setelah rentetan JOptionPane(null,...) fokus pindah ke form induk -> kembalikan form ini ke depan
            bawaKeDepan();
        }
    }//GEN-LAST:event_BtnGrouperIDRGActionPerformed

    /** Paksa dialog ini naik ke depan (toggle alwaysOnTop lebih andal dibanding toFront saja). */
    private void bawaKeDepan() {
        this.setAlwaysOnTop(true);
        this.toFront();
        this.requestFocus();
        this.setAlwaysOnTop(false);
    }

    private void finalklaimIDRGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalklaimIDRGActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(rootPane, "SUDAH YAKIN MAU FINAL IDRG PASIEN " + TPasien.getText() + " ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
            } else {
                // PEMBARUAN API 2026-04: sebelum final, bind id_encounter SatuSehat ke klaim (validasi RME).
                // Non-blocking: kalau belum ada Encounter ter-sync, user dapat warning & bisa lanjut manual.
                satusehatEncounterSet();

                try {
                    // Create a human-readable JSON string
                    String requestJson = "{\n"
                            + "\"metadata\": {\n"
                            + "\"method\":\"idrg_grouper_final\"\n"
                            + "},\n"
                            + "\"data\": {\n"
                            + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                            + "}\n"
                            + "}";

                    // Use the JSON string in your request
                    String request = requestJson;
                    System.out.println("request JSON : " + requestJson);
                    JSONObject response = inacbg.request(request);
                    JSONObject code = response.getJSONObject("metadata");
                    System.out.println("response grouper idrg : " + response);

                    if (response.has("metadata")) {
                        if (code.getInt("code") == 200) {
                            System.out.println("response final idrg : " + code.getString("message"));
                            Sequel.meghapus("inacbg_idrg_final", "no_sep", Tsep.getText());
                            Sequel.meghapus("idrg_prosedur", "no_sep", Tsep.getText());
                            Sequel.meghapus("idrg_diagnosa", "no_sep", Tsep.getText());
                            Sequel.menyimpantf2("inacbg_idrg_final", "?,?", "DATA IDRG", 2, new String[]{
                                Tsep.getText(), coder
                            });

                            BtnGrouperIDRG.setVisible(false);
                            finalklaimIDRG.setVisible(false);
                            editKlaimIDRG.setVisible(true);
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));

                            for (int i = 0; i < kd_psdr.length; i++) {
                                String val = kd_psdr[i].getText().trim();
                                if (!val.equals("")) {
                                    Sequel.menyimpantf2("idrg_prosedur", "?,?,?,?", "SIMPAN PROSEDUR IDRG", 4, new String[]{
                                        Tsep.getText(),
                                        kd_psdr[i].getText().trim(),
                                        nm_psdr[i].getText().trim(),
                                        multiple[i].getText().trim()
                                    });
                                }
                            }

                            for (int i = 0; i < chk_diag.length; i++) {
                                // Pastikan checkbox terpilih dan kode diagnosa tidak kosong
                                if (chk_diag[i].isSelected() && !kd_diag[i].getText().trim().equals("")) {
                                    Sequel.menyimpantf2("idrg_diagnosa", "?,?,?,?", "SIMPAN DIAGNOSA IDRG", 4, new String[]{
                                        Tsep.getText(),
                                        kd_diag[i].getText().trim(),
                                        nm_diag[i].getText().trim(),
                                        "1"
                                    }
                                    );
                                } else if (!kd_diag[i].getText().trim().equals("")) {
                                    Sequel.menyimpantf2("idrg_diagnosa", "?,?,?,?", "SIMPAN DIAGNOSA IDRG", 4, new String[]{
                                        Tsep.getText(),
                                        kd_diag[i].getText().trim(),
                                        nm_diag[i].getText().trim(),
                                        "0"
                                    }
                                    );
                                }
                            }

                            lbltarifProc.setText("0");
                            lblTarifInv.setText("0");
                            lblTarifPros.setText("0");
                            lblTarifDrugs.setText("0");

                            TabRawat.setSelectedIndex(1);
                            FormIDRG.setEnabled(false);
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        System.out.println("response : " + response);
                        System.err.println("\n The 'data' field is missing in the response.");
                    }

                } catch (Exception e) {
                    System.err.println("Error in sitb: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        tombolinacbg();
    }//GEN-LAST:event_finalklaimIDRGActionPerformed

    private void editKlaimIDRGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editKlaimIDRGActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(rootPane, "SUDAH YAKIN MAU EDIT IDRG PASIEN " + TPasien.getText() + " ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
            } else {
                try {
                    // Create a human-readable JSON string
                    String requestJson = "{\n"
                            + "\"metadata\": {\n"
                            + "\"method\":\"idrg_grouper_reedit\"\n"
                            + "},\n"
                            + "\"data\": {\n"
                            + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                            + "}\n"
                            + "}";

                    // Use the JSON string in your request
                    String request = requestJson;
                    System.out.println("request JSON : " + requestJson);
                    JSONObject response = inacbg.request(request);
                    JSONObject code = response.getJSONObject("metadata");
                    System.out.println("response grouper idrg : " + response);

                    if (response.has("metadata")) {
                        if (code.getInt("code") == 200) {
                            System.out.println("response final idrg : " + code.getString("message"));
                            Sequel.meghapus("inacbg_idrg_final", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_final", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_grouping_stage1", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_grouping_stage2", "no_sep", Tsep.getText());
                            Sequel.meghapus("idrg_grouper", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_diagnosa", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_prosedur", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_selisihbayar", "no_sep", Tsep.getText());
                            Txtgroup.setText("");

                            for (i = 0; i < kd_diag2.length; i++) {
                                kd_diag2[i].setText("");
                                nm_diag2[i].setText("");
                                kd_psdr2[i].setText("");
                                nm_psdr2[i].setText("");
                                chk_diag2[i].setSelected(false);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        System.out.println("response : " + response);
                        System.err.println("\n The 'data' field is missing in the response.");
                    }

                } catch (Exception e) {
                    System.err.println("Error in sitb: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        tombolinacbg();
    }//GEN-LAST:event_editKlaimIDRGActionPerformed

    private void prosedurINA() {
        icd9INA = "";

        StringBuilder sb = new StringBuilder();
        for (JTextField field : kd_psdr2) {
            String kode = field.getText().trim();
            if (!kode.equals("")) {
                if (sb.length() > 0) {
                    sb.append("#");
                }
                sb.append(kode);
            }
        }

        icd9INA = sb.toString();
        System.out.println("icd9 INA : " + icd9INA);

        // Validasi input wajib
        if (TNoRM.getText().trim().equals("")
                || Tsep.getText().trim().equals("")
                || no_kartu.getText().trim().equals("")) {

            JOptionPane.showMessageDialog(this,
                    "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");

        } else {
            try {
                // Buat JSON request
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"inacbg_procedure_set\",\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"procedure\":\"" + icd9INA.trim() + "\"\n"
                        + "}\n"
                        + "}";

                System.out.println("request JSON : " + requestJson);

                JSONObject response = inacbg.request(requestJson);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response set prosedur : " + response);

                if (code.getInt("code") == 200) {
                    System.out.println("Prosedur berhasil diset.");
                } else {
                    JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                }

            } catch (Exception e) {
                System.err.println("Error in prosedurINA: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void diagnosaINA() {
        icd10INA = "";

        String primary = "";
        List<String> secondary = new ArrayList<>();

        for (int i = 0; i < kd_diag2.length; i++) {
            String kode = kd_diag2[i].getText().trim();
            if (!kode.equals("")) {
                if (chk_diag2[i].isSelected()) {
                    // Kalau checkbox dicentang → anggap primary
                    primary = kode;
                } else {
                    secondary.add(kode);
                }
            }
        }

        // Susun ICD10 (diagnosa) → primary dulu, baru sisanya
        StringBuilder sb = new StringBuilder();
        if (!primary.equals("")) {
            sb.append(primary);
        }
        for (String kode : secondary) {
            if (sb.length() > 0) {
                sb.append("#");
            }
            sb.append(kode);
        }

        icd10INA = sb.toString();
        System.out.println("icd10 INA : " + icd10INA);

        // Validasi input wajib
        if (TNoRM.getText().trim().equals("")
                || Tsep.getText().trim().equals("")
                || no_kartu.getText().trim().equals("")) {

            JOptionPane.showMessageDialog(this,
                    "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Buat JSON request untuk diagnosa (ICD10)
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"inacbg_diagnosa_set\",\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"diagnosa\":\"" + icd10INA.trim() + "\"\n"
                        + "}\n"
                        + "}";

                System.out.println("request JSON : " + requestJson);

                JSONObject response = inacbg.request(requestJson);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response set diagnosa : " + response);

                if (code.getInt("code") == 200) {
                    System.out.println("Diagnosa berhasil diset.");
                } else {
                    JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                }
            } catch (Exception e) {
                System.err.println("Error in diagnosaINA: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void BtnGrouperINAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGrouperINAActionPerformed
        boolean adaIsi = false;
        for (int i = 0; i < kd_diag2.length; i++) {
            if (!kd_diag2[i].getText().trim().equals("")) {
                adaIsi = true;
                break;
            }
        }

        boolean belumAdaDicentang = true;

        for (JCheckBox chk : chk_diag2) {
            if (chk.isSelected()) {
                belumAdaDicentang = false;
                break;
            }
        }

        if (!adaIsi) {
            JOptionPane.showMessageDialog(this, "Belum ada diagnosa yang diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (belumAdaDicentang) {
            JOptionPane.showMessageDialog(this, "Belum ada diagnosa yang dicentang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            prosedurINA();
            diagnosaINA();

            grouper1();
        }

        tombolinacbg();
        // kembalikan form ini ke depan setelah rentetan JOptionPane(null,...)
        bawaKeDepan();
    }//GEN-LAST:event_BtnGrouperINAActionPerformed

    private void BtnGrouperINAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnGrouperINAKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnGrouperINAKeyPressed

    private void finalINAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalINAActionPerformed
        // TODO add your handling code here:
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"inacbg_grouper_final\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response final inacbg : " + response);

                if (response.has("metadata")) {
                    if (code.getInt("code") == 200) {
                        if (code.getInt("code") == 200) {
                            Sequel.meghapus("inacbg_selisihbayar", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_final", "no_sep", Tsep.getText());

                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                            Sequel.menyimpan("inacbg_final", "'" + Tsep.getText().trim() + "'");

                            Sequel.menyimpantf2("inacbg_selisihbayar", "?,?,?,?", "SELISIH EKLAIM", 4, new String[]{
                                Tsep.getText(), cmbSelisih.getSelectedItem().toString().trim(), lblSelisih.getText(), coder
                            });

//                            for (int i = 0; i < kd_diag2.length; i++) {
//                                String kode = kd_diag2[i].getText().trim();
//                                if (!kode.equals("")) {
//                                    Sequel.menyimpantf2("diagnosa_pasien","?,?,?,?,?","DIAGNOSA",4,new String[]{
//                                        no_rawat.getText(),kode,"","","baru"
//                                    });
//                                }
//                            }
                            tombolinacbg();
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }

        tombolinacbg();
    }//GEN-LAST:event_finalINAActionPerformed

    private void editINAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editINAActionPerformed
        // TODO add your handling code here:
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"inacbg_grouper_reedit\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response edit : " + response);

                if (response.has("metadata")) {
                    if (code.getInt("code") == 200) {
                        if (code.getInt("code") == 200) {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                            Sequel.meghapus("inacbg_final", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_diagnosa", "no_sep", Tsep.getText());
                            Sequel.meghapus("inacbg_prosedur", "no_sep", Tsep.getText());
                            tombolinacbg();
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }

        tombolinacbg();
    }//GEN-LAST:event_editINAActionPerformed

    private void finalklaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalklaimActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(rootPane, "SUDAH YAKIN MAU FINAL KLAIM PASIEN " + TPasien.getText() + " ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
            } else {
                try {
                    // Create a human-readable JSON string
                    String requestJson = "{\n"
                            + "\"metadata\": {\n"
                            + "\"method\":\"claim_final\"\n"
                            + "},\n"
                            + "\"data\": {\n"
                            + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\",\n"
                            + "\"coder_nik\": \"" + coder + "\"\n"
                            + "}\n"
                            + "}";

                    // Use the JSON string in your request
                    String request = requestJson;
                    System.out.println("request JSON : " + requestJson);
                    JSONObject response = inacbg.request(request);
                    JSONObject code = response.getJSONObject("metadata");
                    System.out.println("response final inacbg : " + response);

                    if (response.has("metadata")) {
                        if (code.getInt("code") == 200) {
                            if (code.getInt("code") == 200) {
                                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                                Sequel.menyimpan("inacbg_grouping_final", "'" + Tsep.getText().trim() + "'");

                            } else {
                                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        System.out.println("response : " + response);
                        System.err.println("\n The 'data' field is missing in the response.");
                    }

                } catch (Exception e) {
                    System.err.println("Error in sitb: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        tombolinacbg();
    }//GEN-LAST:event_finalklaimActionPerformed

    private void editKlaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editKlaimActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(rootPane, "SUDAH YAKIN MAU EDIT KLAIM PASIEN " + TPasien.getText() + " ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
            } else {
                try {
                    // Create a human-readable JSON string
                    String requestJson = "{\n"
                            + "\"metadata\": {\n"
                            + "\"method\":\"reedit_claim\"\n"
                            + "},\n"
                            + "\"data\": {\n"
                            + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                            + "}\n"
                            + "}";

                    // Use the JSON string in your request
                    String request = requestJson;
                    System.out.println("request JSON : " + requestJson);
                    JSONObject response = inacbg.request(request);
                    JSONObject code = response.getJSONObject("metadata");
                    System.out.println("response edit : " + response);

                    if (response.has("metadata")) {
                        if (code.getInt("code") == 200) {
                            if (code.getInt("code") == 200) {
                                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                                Sequel.meghapus("inacbg_grouping_final", "no_sep", Tsep.getText());
                            } else {
                                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        System.out.println("response : " + response);
                        System.err.println("\n The 'data' field is missing in the response.");
                    }

                } catch (Exception e) {
                    System.err.println("Error in sitb: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        tombolinacbg();
    }//GEN-LAST:event_editKlaimActionPerformed

    private void kirimonlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kirimonlineActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(rootPane, "SUDAH YAKIN MAU KIRIM ONLINE PASIEN " + TPasien.getText() + " ?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
                } else {
                    try {
                        // Create a human-readable JSON string
                        String requestJson = "{\n"
                                + "\"metadata\": {\n"
                                + "\"method\":\"send_claim_individual\"\n"
                                + "},\n"
                                + "\"data\": {\n"
                                + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                                + "}\n"
                                + "}";

                        // Use the JSON string in your request
                        String request = requestJson;
                        System.out.println("request JSON : " + requestJson);
                        JSONObject response = inacbg.request(request);
                        JSONObject code = response.getJSONObject("metadata");
                        System.out.println("response kirim online : " + response);

                        if (response.has("metadata")) {
                            if (code.getInt("code") == 200) {
                                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                            } else {
                                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                            }
                        } else {
                            System.out.println("response : " + response);
                            System.err.println("\n The 'data' field is missing in the response.");
                        }

                    } catch (Exception e) {
                        System.err.println("Error in sitb: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi Bridging Hapus : " + ex);
            }
        }

        tombolinacbg();
    }//GEN-LAST:event_kirimonlineActionPerformed

    private void hapusKlaim4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusKlaim4ActionPerformed
        if (Tsep.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Nomor SEP belum terisi!");
            return;
        }
        int reply = JOptionPane.showConfirmDialog(this,
                "Hapus klaim (server e-Klaim + lokal) untuk SEP " + Tsep.getText().trim() + " ?\n"
                + "Data grouping iDRG/INACBG akan direset. Grouping berikutnya akan kirim ulang data pasien (termasuk tanggal lahir terbaru).\n\n"
                + "PERHATIAN: setelah klaim dihapus, e-Klaim bisa menolak klaim baru dengan error E2043\n"
                + "(\"Nomor Klaim sudah terpakai\") sehingga perlu bantuan admin e-Klaim. Hapus hanya bila benar-benar perlu.",
                "Konfirmasi Hapus Claim", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) {
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String sep = Tsep.getText().trim();
        try {
            String requestJson = "{\n"
                    + "\"metadata\": {\n"
                    + "\"method\":\"delete_claim\"\n"
                    + "},\n"
                    + "\"data\": {\n"
                    + "\"nomor_sep\":\"" + sep + "\",\n"
                    + "\"coder_nik\":\"" + coder + "\"\n"   // WAJIB, kalau kosong WS balas E2005 "NIK Coder masih kosong"
                    + "}\n"
                    + "}";
            System.out.println("request JSON : " + requestJson);
            JSONObject response = inacbg.request(requestJson);
            System.out.println("response delete_claim : " + response);
            // Klaim final tidak bisa langsung dihapus (E2009 "Klaim sudah final").
            // Buka final via reedit_claim dulu, lalu delete lagi.
            if (response.has("metadata") && response.getJSONObject("metadata").optInt("code", 0) != 200) {
                String reeditJson = "{\n\"metadata\":{\n\"method\":\"reedit_claim\"\n},\n\"data\":{\n"
                        + "\"nomor_sep\":\"" + sep + "\",\n\"coder_nik\":\"" + coder + "\"\n}\n}";
                System.out.println("request JSON : " + reeditJson);
                System.out.println("response reedit_claim : " + inacbg.request(reeditJson));
                response = inacbg.request(requestJson);
                System.out.println("response delete_claim(2) : " + response);
            }
            String pesan = response.has("metadata") ? response.getJSONObject("metadata").optString("message", "") : "";
            // reset data lokal SEP ini -> grouping berikutnya jalankan new_claim ulang dgn DOB terbaru
            for (String tbl : new String[]{"inacbg_klaim_baru", "idrg_grouper", "idrg_diagnosa", "idrg_prosedur",
                "inacbg_grouping_stage1", "inacbg_grouping_final", "inacbg_idrg_final", "inacbg_final",
                "inacbg_diagnosa", "inacbg_prosedur"}) {
                try {
                    Sequel.meghapus(tbl, "no_sep", sep);
                } catch (Exception ex) {
                    System.out.println("hapus " + tbl + " : " + ex);
                }
            }
            responIDRG.setText("");  // bersihkan panel hasil grouping lama biar tidak membingungkan
            JOptionPane.showMessageDialog(this, "Klaim direset. " + pesan + "\nSilakan klik Grouping IDRG lagi.");
            tombolinacbg();
        } catch (Exception e) {
            System.out.println("Notifikasi hapus claim : " + e);
            JOptionPane.showMessageDialog(this, "Gagal hapus klaim : " + e);
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_hapusKlaim4ActionPerformed

    private void hapusKlaim4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hapusKlaim4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_hapusKlaim4KeyPressed

    private void BtnKeluar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluar4ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_BtnKeluar4ActionPerformed

    private void BtnKeluar4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluar4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnKeluar4KeyPressed

    private void nm_diag_5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nm_diag_5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nm_diag_5ActionPerformed

    private void nm_diag_15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nm_diag_15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nm_diag_15ActionPerformed

    private void chk_diag_11ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_11ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_11ItemStateChanged

    private void chk_diag_12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_12ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_12ItemStateChanged

    private void chk_diag_13ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_13ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_13ItemStateChanged

    private void chk_diag_14ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_14ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_14ItemStateChanged

    private void chk_diag_15ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_15ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_15ItemStateChanged

    private void chk_diag_16ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_16ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_16ItemStateChanged

    private void chk_diag_17ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_17ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_17ItemStateChanged

    private void chk_diag_18ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_18ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_18ItemStateChanged

    private void chk_diag_19ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_19ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_19ItemStateChanged

    private void chk_diag_20ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_diag_20ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_20ItemStateChanged

    private void batal_11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_11ActionPerformed
        // TODO add your handling code here:
        kd_diag_11.setText("");
        nm_diag_11.setText("");
        chk_diag_11.setSelected(false);
    }//GEN-LAST:event_batal_11ActionPerformed

    private void batal_12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_12ActionPerformed
        // TODO add your handling code here:
        kd_diag_12.setText("");
        nm_diag_12.setText("");
        chk_diag_12.setSelected(false);
    }//GEN-LAST:event_batal_12ActionPerformed

    private void batal_13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_13ActionPerformed
        // TODO add your handling code here:
        kd_diag_13.setText("");
        nm_diag_13.setText("");
        chk_diag_13.setSelected(false);
    }//GEN-LAST:event_batal_13ActionPerformed

    private void batal_14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_14ActionPerformed
        // TODO add your handling code here:
        kd_diag_14.setText("");
        nm_diag_14.setText("");
        chk_diag_14.setSelected(false);
    }//GEN-LAST:event_batal_14ActionPerformed

    private void batal_15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_15ActionPerformed
        // TODO add your handling code here:
        kd_diag_15.setText("");
        nm_diag_15.setText("");
        chk_diag_15.setSelected(false);
    }//GEN-LAST:event_batal_15ActionPerformed

    private void batal_16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_16ActionPerformed
        // TODO add your handling code here:
        kd_diag_16.setText("");
        nm_diag_16.setText("");
        chk_diag_16.setSelected(false);
    }//GEN-LAST:event_batal_16ActionPerformed

    private void batal_17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_17ActionPerformed
        // TODO add your handling code here:
        kd_diag_17.setText("");
        nm_diag_17.setText("");
        chk_diag_17.setSelected(false);
    }//GEN-LAST:event_batal_17ActionPerformed

    private void batal_18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_18ActionPerformed
        // TODO add your handling code here:
        kd_diag_18.setText("");
        nm_diag_18.setText("");
        chk_diag_18.setSelected(false);
    }//GEN-LAST:event_batal_18ActionPerformed

    private void batal_19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_19ActionPerformed
        // TODO add your handling code here:
        kd_diag_19.setText("");
        nm_diag_19.setText("");
        chk_diag_19.setSelected(false);
    }//GEN-LAST:event_batal_19ActionPerformed

    private void batal_20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_20ActionPerformed
        // TODO add your handling code here:
        kd_diag_20.setText("");
        nm_diag_20.setText("");
        chk_diag_20.setSelected(false);
    }//GEN-LAST:event_batal_20ActionPerformed

    private void batal_psdr_11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_11ActionPerformed
        // TODO add your handling code here:
        kd_psdr_11.setText("");
        nm_psdr_11.setText("");
    }//GEN-LAST:event_batal_psdr_11ActionPerformed

    private void batal_psdr_12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_12ActionPerformed
        // TODO add your handling code here:
        kd_psdr_12.setText("");
        nm_psdr_12.setText("");
    }//GEN-LAST:event_batal_psdr_12ActionPerformed

    private void batal_psdr_13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_13ActionPerformed
        // TODO add your handling code here:
        kd_psdr_13.setText("");
        nm_psdr_13.setText("");
    }//GEN-LAST:event_batal_psdr_13ActionPerformed

    private void batal_psdr_14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_14ActionPerformed
        // TODO add your handling code here:
        kd_psdr_14.setText("");
        nm_psdr_14.setText("");
    }//GEN-LAST:event_batal_psdr_14ActionPerformed

    private void batal_psdr_15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_15ActionPerformed
        // TODO add your handling code here:
        kd_psdr_15.setText("");
        nm_psdr_15.setText("");
    }//GEN-LAST:event_batal_psdr_15ActionPerformed

    private void batal_psdr_16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_16ActionPerformed
        // TODO add your handling code here:
        kd_psdr_16.setText("");
        nm_psdr_16.setText("");
    }//GEN-LAST:event_batal_psdr_16ActionPerformed

    private void batal_psdr_17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_17ActionPerformed
        // TODO add your handling code here:
        kd_psdr_17.setText("");
        nm_psdr_17.setText("");
    }//GEN-LAST:event_batal_psdr_17ActionPerformed

    private void batal_psdr_18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_18ActionPerformed
        // TODO add your handling code here:
        kd_psdr_18.setText("");
        nm_psdr_18.setText("");
    }//GEN-LAST:event_batal_psdr_18ActionPerformed

    private void batal_psdr_19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_19ActionPerformed
        // TODO add your handling code here:
        kd_psdr_19.setText("");
        nm_psdr_19.setText("");
    }//GEN-LAST:event_batal_psdr_19ActionPerformed

    private void batal_psdr_20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batal_psdr_20ActionPerformed
        // TODO add your handling code here:
        kd_psdr_20.setText("");
        nm_psdr_20.setText("");
    }//GEN-LAST:event_batal_psdr_20ActionPerformed

    private void tbDiagnosaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDiagnosaMouseClicked
        // TODO add your handling code here:
        for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
            tbDiagnosa.setValueAt(false, i, 0);
        }
        tabModeDiagnosa.setValueAt(true, tbDiagnosa.getSelectedRow(), 0);
    }//GEN-LAST:event_tbDiagnosaMouseClicked

    private void tbProsedurMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProsedurMouseClicked
        // TODO add your handling code here:
        for (i = 0; i < tbProsedur.getRowCount(); i++) {
            tbProsedur.setValueAt(false, i, 0);
        }
        tabModeProsedur.setValueAt(true, tbProsedur.getSelectedRow(), 0);
    }//GEN-LAST:event_tbProsedurMouseClicked

    private void tbDiagnosa2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDiagnosa2MouseClicked
        // TODO add your handling code here:
        for (i = 0; i < tbDiagnosa2.getRowCount(); i++) {
            tbDiagnosa2.setValueAt(false, i, 0);
        }
        tabModeDiagnosa2.setValueAt(true, tbDiagnosa2.getSelectedRow(), 0);
    }//GEN-LAST:event_tbDiagnosa2MouseClicked

    private void tbProsedur2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProsedur2MouseClicked
        // TODO add your handling code here:
        for (i = 0; i < tbProsedur2.getRowCount(); i++) {
            tbProsedur2.setValueAt(false, i, 0);
        }
        tabModeProsedur2.setValueAt(true, tbProsedur2.getSelectedRow(), 0);
    }//GEN-LAST:event_tbProsedur2MouseClicked

    private void nm_psdr_11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nm_psdr_11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nm_psdr_11ActionPerformed

    private void chk_diag_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_diag_2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_2ActionPerformed

    private void chk_diag_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_diag_1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_1ActionPerformed

    private void chk_diag_12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chk_diag_12MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_diag_12MouseClicked

    private void TabRawatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TabRawatStateChanged
        //        if (TabRawat.getSelectedIndex() == 0) {
        //            tampilDxPx("idrg", Status.getSelectedItem().toString());
        //        } else if (TabRawat.getSelectedIndex() == 1) {
        //            tampilDxPx("inacbg", Status.getSelectedItem().toString());
        //        }
    }//GEN-LAST:event_TabRawatStateChanged

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        if (TabRawat.getSelectedIndex() == 0) {
            tampildiagnosa();
            tampilprosedure();
        } else if (TabRawat.getSelectedIndex() == 1) {
            tampildiagnosaINA();
            tampilprosedureINA();
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void ImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportActionPerformed
        // TODO add your handling code here:
        for (i = 0; i < kd_psdr.length; i++) {
            kd_diag2[i].setText("");
            nm_diag2[i].setText("");
            kd_psdr2[i].setText("");
            nm_psdr2[i].setText("");
            chk_diag2[i].setSelected(false);
        }

        lbltarifProc.setText("0");
        lblTarifInv.setText("0");
        lblTarifPros.setText("0");
        lblTarifDrugs.setText("0");

        idrgtoinacbg();
    }//GEN-LAST:event_ImportActionPerformed

    private void TxtgroupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtgroupKeyPressed
        //        Valid.pindah2(evt,KdPetugas,Diagnosis);
    }//GEN-LAST:event_TxtgroupKeyPressed

    private void cmbProcItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProcItemStateChanged
        // TODO add your handling code here:
        if (isLoading) {
            return;
        }
        if (cmbProc.getSelectedItem() != null && cmbPros.getSelectedItem() != null && cmbInv.getSelectedItem() != null && cmbDrugs.getSelectedItem() != null) {
            if (cmbProc.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbPros.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbDrugs.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbInv.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lbltarifProc.setText("0");
                grouper2();
                System.out.println("1");
            } else if (cmbProc.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lbltarifProc.setText("0");
                handleComboChange();
            } else {
                if (cmbProc.getSelectedIndex() > 0 && evt.getStateChange() == ItemEvent.SELECTED) {
                    handleComboChange();
                }
            }
        }
    }//GEN-LAST:event_cmbProcItemStateChanged

    private void cmbProsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProsItemStateChanged
        // TODO add your handling code here:
        if (isLoading) {
            return;
        }
        if (cmbProc.getSelectedItem() != null && cmbPros.getSelectedItem() != null && cmbInv.getSelectedItem() != null && cmbDrugs.getSelectedItem() != null) {
            if (cmbProc.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbPros.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbDrugs.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbInv.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lblTarifPros.setText("0");
                grouper2();
                System.out.println("2");
            } else if (cmbPros.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lblTarifPros.setText("0");
                handleComboChange();
            } else {
                if (cmbPros.getSelectedIndex() > 0 && evt.getStateChange() == ItemEvent.SELECTED) {
                    handleComboChange();
                }
            }
        }
    }//GEN-LAST:event_cmbProsItemStateChanged

    private void cmbInvItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbInvItemStateChanged
        // TODO add your handling code here:
        if (isLoading) {
            return;
        }
        if (cmbProc.getSelectedItem() != null && cmbPros.getSelectedItem() != null && cmbInv.getSelectedItem() != null && cmbDrugs.getSelectedItem() != null) {
            if (cmbProc.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbPros.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbDrugs.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbInv.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lblTarifInv.setText("0");
                grouper2();
                System.out.println("3");
            } else if (cmbInv.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lblTarifInv.setText("0");
                handleComboChange();
            } else {
                if (cmbInv.getSelectedIndex() > 0 && evt.getStateChange() == ItemEvent.SELECTED) {
                    handleComboChange();
                }
            }
        }
    }//GEN-LAST:event_cmbInvItemStateChanged

    private void cmbDrugsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbDrugsItemStateChanged
        // TODO add your handling code here:
        if (isLoading) {
            return;
        }
        if (cmbProc.getSelectedItem() != null && cmbPros.getSelectedItem() != null && cmbInv.getSelectedItem() != null && cmbDrugs.getSelectedItem() != null) {
            if (cmbProc.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbPros.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbDrugs.getSelectedItem().toString().toLowerCase().contains("none")
                    && cmbInv.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lblTarifDrugs.setText("0");
                grouper2();
                System.out.println("4");
            } else if (cmbDrugs.getSelectedItem().toString().toLowerCase().contains("none") && evt.getStateChange() == ItemEvent.SELECTED) {
                lblTarifDrugs.setText("0");
                handleComboChange();
            } else {
                if (cmbDrugs.getSelectedIndex() > 0 && evt.getStateChange() == ItemEvent.SELECTED) {
                    handleComboChange();
                }
            }
        }
    }//GEN-LAST:event_cmbDrugsItemStateChanged

    private void cmbSelisihItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSelisihItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSelisihItemStateChanged

    // Bulatkan string angka (bisa berdesimal, mis. "4294967.295") ke rupiah bulat.
    // Tanpa ini, TextBox tarif hanya menerima digit (getOnlyAngka) sehingga titik
    // desimal dibuang dan angka membengkak ~1000x (4294967.295 -> 4294967295).
    private String bulat(String v) {
        try {
            if (v == null || v.trim().isEmpty()) return "0";
            return String.valueOf(Math.round(Double.parseDouble(v.trim())));
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    private void CetakKlaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CetakKlaimActionPerformed
        try {

            // Create a human-readable JSON string
            String requestJson = "{\n"
                    + " \"metadata\": {\n"
                    + " \"method\": \"claim_print\"\n"
                    + " },\n"
                    + " \"data\": {\n"
                    + " \"nomor_sep\": \"" + Tsep.getText() + "\"\n"
                    + " }\n"
                    + "}";

            // Use the JSON string in your request
            String request = requestJson;

            JSONObject response = inacbg.request(request);

            // Extract the 'data' field from the response
            if (response.has("data")) {
                String data = response.getString("data");

                try {
                    // Decode the Base64 data
//                    byte[] decodedBytes = Base64.getDecoder().decode(data);

                    byte[] byteArray = Base64.getDecoder().decode(data);
                    InputStream inputStream = new ByteArrayInputStream(byteArray);
                    String Dir = "./berkasklaim/" + Tsep.getText() + ".pdf";
                    File file = new File(Dir);

                    file.delete();

                    Path path = Paths.get(Dir);
                    Files.copy(inputStream, path);
                    System.out.println("PDF saved successfully at " + path);
//                    Desktop desktop = Desktop.getDesktop();
//                    desktop.open(file);

                } catch (Exception e) {
                    System.err.println("Error decoding Base64 data: " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                System.out.println("response : " + response);

                System.err.println("\n The 'data' field is missing in the response.");
            }

        } catch (Exception e) {
            System.err.println("Error in cetakKlaim: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_CetakKlaimActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            INACBGDataIDRG dialog = new INACBGDataIDRG(new javax.swing.JFrame(), true);
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
    private widget.Button BtnGrouperIDRG;
    private widget.Button BtnGrouperINA;
    private widget.Button BtnKeluar4;
    private widget.Button CetakKlaim;
    public widget.TextBox Diagnosa;
    public widget.TextBox DiagnosaINACBG;
    private widget.panelisi FormDATAKLAIM;
    private widget.panelisi FormIDRG;
    private widget.panelisi FormIDRG1;
    private widget.panelisi FormINACBG;
    private widget.Button INAdiagnosa;
    public widget.Button INAdiagnosatambah;
    private widget.Button INAprosedur;
    public widget.Button INAprosedurtambah;
    private widget.Button Import;
    private javax.swing.JPanel Panel1;
    private widget.TextBox Prosedur;
    private widget.TextBox ProsedurINACBG;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.ScrollPane Scroll6;
    private widget.ScrollPane Scroll7;
    private widget.ScrollPane Scroll8;
    private widget.ScrollPane Scroll9;
    private widget.TextBox TNoRM;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.TextBox Tsep;
    private widget.TextArea Txtgroup;
    private widget.TextBox ahli;
    private widget.TextBox akomodasi;
    private widget.TextBox alkes;
    private widget.Button batal_1;
    private widget.Button batal_10;
    private widget.Button batal_11;
    private widget.Button batal_12;
    private widget.Button batal_13;
    private widget.Button batal_14;
    private widget.Button batal_15;
    private widget.Button batal_16;
    private widget.Button batal_17;
    private widget.Button batal_18;
    private widget.Button batal_19;
    private widget.Button batal_2;
    private widget.Button batal_20;
    private widget.Button batal_3;
    private widget.Button batal_4;
    private widget.Button batal_5;
    private widget.Button batal_6;
    private widget.Button batal_7;
    private widget.Button batal_8;
    private widget.Button batal_9;
    private widget.Button batal_psdr_1;
    private widget.Button batal_psdr_10;
    private widget.Button batal_psdr_11;
    private widget.Button batal_psdr_12;
    private widget.Button batal_psdr_13;
    private widget.Button batal_psdr_14;
    private widget.Button batal_psdr_15;
    private widget.Button batal_psdr_16;
    private widget.Button batal_psdr_17;
    private widget.Button batal_psdr_18;
    private widget.Button batal_psdr_19;
    private widget.Button batal_psdr_2;
    private widget.Button batal_psdr_20;
    private widget.Button batal_psdr_3;
    private widget.Button batal_psdr_4;
    private widget.Button batal_psdr_5;
    private widget.Button batal_psdr_6;
    private widget.Button batal_psdr_7;
    private widget.Button batal_psdr_8;
    private widget.Button batal_psdr_9;
    private widget.Button batal_validasi_tb;
    private widget.TextBox berat_bayi;
    private widget.TextBox bmhp;
    private widget.ComboBox cara_masuk;
    private javax.swing.JCheckBox check_hd;
    private javax.swing.JCheckBox chk_diag_1;
    private javax.swing.JCheckBox chk_diag_10;
    private javax.swing.JCheckBox chk_diag_11;
    private javax.swing.JCheckBox chk_diag_12;
    private javax.swing.JCheckBox chk_diag_13;
    private javax.swing.JCheckBox chk_diag_14;
    private javax.swing.JCheckBox chk_diag_15;
    private javax.swing.JCheckBox chk_diag_16;
    private javax.swing.JCheckBox chk_diag_17;
    private javax.swing.JCheckBox chk_diag_18;
    private javax.swing.JCheckBox chk_diag_19;
    private javax.swing.JCheckBox chk_diag_2;
    private javax.swing.JCheckBox chk_diag_20;
    private javax.swing.JCheckBox chk_diag_3;
    private javax.swing.JCheckBox chk_diag_4;
    private javax.swing.JCheckBox chk_diag_5;
    private javax.swing.JCheckBox chk_diag_6;
    private javax.swing.JCheckBox chk_diag_7;
    private javax.swing.JCheckBox chk_diag_8;
    private javax.swing.JCheckBox chk_diag_9;
    private widget.TextBox chronic;
    private widget.ComboBox cmbDrugs;
    private widget.ComboBox cmbInv;
    private widget.ComboBox cmbProc;
    private widget.ComboBox cmbPros;
    private widget.ComboBox cmbSelisih;
    private widget.ComboBox cob;
    private widget.TextBox distole;
    private widget.TextBox dpjp;
    private widget.Button editINA;
    private widget.Button editKlaim;
    private widget.Button editKlaimIDRG;
    private widget.Button finalINA;
    private widget.Button finalklaim;
    private widget.Button finalklaimIDRG;
    private widget.Label grupCode;
    private widget.ComboBox hak_kelas;
    private widget.Button hapusKlaim4;
    private widget.TextBox hari_intensif;
    private widget.Button idrgPenyakit;
    public widget.Button idrgPenyakittambah;
    private widget.Button idrgProsedur;
    public widget.Button idrgProseduttambah;
    private widget.Label jLabel100;
    private widget.Label jLabel101;
    private widget.Label jLabel102;
    private widget.Label jLabel103;
    private widget.Label jLabel104;
    private widget.Label jLabel105;
    private widget.Label jLabel107;
    private widget.Label jLabel108;
    private widget.Label jLabel109;
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
    private widget.Label jLabel120;
    private widget.Label jLabel121;
    private widget.Label jLabel122;
    private widget.Label jLabel125;
    private widget.Label jLabel126;
    private widget.Label jLabel127;
    private widget.Label jLabel129;
    private widget.Label jLabel183;
    private widget.Label jLabel216;
    private widget.Label jLabel217;
    private widget.Label jLabel218;
    private widget.Label jLabel219;
    private widget.Label jLabel220;
    private widget.Label jLabel221;
    private widget.Label jLabel222;
    private widget.Label jLabel223;
    private widget.Label jLabel224;
    private widget.Label jLabel225;
    private widget.Label jLabel226;
    private widget.Label jLabel227;
    private widget.Label jLabel228;
    private widget.Label jLabel229;
    private widget.Label jLabel230;
    private widget.Label jLabel231;
    private widget.Label jLabel232;
    private widget.Label jLabel233;
    private widget.Label jLabel234;
    private widget.Label jLabel25;
    private widget.Label jLabel49;
    private widget.Label jLabel53;
    private widget.Label jLabel58;
    private widget.Label jLabel61;
    private widget.Label jLabel66;
    private widget.Label jLabel68;
    private widget.Label jLabel69;
    private widget.Label jLabel71;
    private widget.Label jLabel72;
    private widget.Label jLabel73;
    private widget.Label jLabel74;
    private widget.Label jLabel75;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel80;
    private widget.Label jLabel81;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSeparator jSeparator21;
    private javax.swing.JSeparator jSeparator22;
    private javax.swing.JSeparator jSeparator23;
    private javax.swing.JSeparator jSeparator24;
    private javax.swing.JSeparator jSeparator25;
    private javax.swing.JSeparator jSeparator26;
    private javax.swing.JSeparator jSeparator27;
    private javax.swing.JSeparator jSeparator28;
    private widget.ComboBox jam_ekstubasi;
    private widget.ComboBox jam_intubasi;
    private widget.ComboBox jam_keluar;
    private widget.ComboBox jam_masuk;
    private widget.TextBox jam_ventilator;
    private widget.ComboBox jaminan;
    private widget.TextBox jenis_kelamin;
    private widget.ComboBox jenis_rawat;
    private widget.TextBox kd_diag_1;
    private widget.TextBox kd_diag_10;
    private widget.TextBox kd_diag_11;
    private widget.TextBox kd_diag_12;
    private widget.TextBox kd_diag_13;
    private widget.TextBox kd_diag_14;
    private widget.TextBox kd_diag_15;
    private widget.TextBox kd_diag_16;
    private widget.TextBox kd_diag_17;
    private widget.TextBox kd_diag_18;
    private widget.TextBox kd_diag_19;
    private widget.TextBox kd_diag_2;
    private widget.TextBox kd_diag_20;
    private widget.TextBox kd_diag_3;
    private widget.TextBox kd_diag_4;
    private widget.TextBox kd_diag_5;
    private widget.TextBox kd_diag_6;
    private widget.TextBox kd_diag_7;
    private widget.TextBox kd_diag_8;
    private widget.TextBox kd_diag_9;
    private widget.TextBox kd_psdr_1;
    private widget.TextBox kd_psdr_10;
    private widget.TextBox kd_psdr_11;
    private widget.TextBox kd_psdr_12;
    private widget.TextBox kd_psdr_13;
    private widget.TextBox kd_psdr_14;
    private widget.TextBox kd_psdr_15;
    private widget.TextBox kd_psdr_16;
    private widget.TextBox kd_psdr_17;
    private widget.TextBox kd_psdr_18;
    private widget.TextBox kd_psdr_19;
    private widget.TextBox kd_psdr_2;
    private widget.TextBox kd_psdr_20;
    private widget.TextBox kd_psdr_3;
    private widget.TextBox kd_psdr_4;
    private widget.TextBox kd_psdr_5;
    private widget.TextBox kd_psdr_6;
    private widget.TextBox kd_psdr_7;
    private widget.TextBox kd_psdr_8;
    private widget.TextBox kd_psdr_9;
    private javax.swing.JCheckBox kelas_eksekutif;
    private widget.Button kirimonline;
    private widget.TextBox kpr;
    private widget.TextBox kronis;
    private widget.TextBox ksl;
    private widget.TextBox kt_darah;
    private widget.Label lChronic;
    private widget.Label lJenis1;
    private widget.Label lSubacute;
    private widget.TextBox lab;
    private widget.TextBox lama_naik_kelas;
    private widget.Label lblBaseTariff;
    private widget.Label lblSelisih;
    private widget.Label lblTarifAcute;
    private widget.Label lblTarifDrugs;
    private widget.Label lblTarifGrup;
    private widget.Label lblTarifInv;
    private widget.Label lblTarifKronis;
    private widget.Label lblTarifPros;
    private widget.Label lblTarifRS;
    private widget.Label lblTarifTotal;
    private widget.Label lbltarifProc;
    private widget.Label lbltarifrs;
    private widget.ComboBox menit_ekstubasi;
    private widget.ComboBox menit_intubasi;
    private widget.ComboBox menit_keluar;
    private widget.ComboBox menit_masuk;
    private widget.TextBox multiple1;
    private widget.TextBox multiple10;
    private widget.TextBox multiple2;
    private widget.TextBox multiple3;
    private widget.TextBox multiple4;
    private widget.TextBox multiple5;
    private widget.TextBox multiple6;
    private widget.TextBox multiple7;
    private widget.TextBox multiple8;
    private widget.TextBox multiple9;
    private widget.ComboBox naik_kelas_pelayanan;
    private javax.swing.JCheckBox naik_turun_kelas;
    private widget.TextBox nm_diag_1;
    private widget.TextBox nm_diag_10;
    private widget.TextBox nm_diag_11;
    private widget.TextBox nm_diag_12;
    private widget.TextBox nm_diag_13;
    private widget.TextBox nm_diag_14;
    private widget.TextBox nm_diag_15;
    private widget.TextBox nm_diag_16;
    private widget.TextBox nm_diag_17;
    private widget.TextBox nm_diag_18;
    private widget.TextBox nm_diag_19;
    private widget.TextBox nm_diag_2;
    private widget.TextBox nm_diag_20;
    private widget.TextBox nm_diag_3;
    private widget.TextBox nm_diag_4;
    private widget.TextBox nm_diag_5;
    private widget.TextBox nm_diag_6;
    private widget.TextBox nm_diag_7;
    private widget.TextBox nm_diag_8;
    private widget.TextBox nm_diag_9;
    private widget.TextBox nm_psdr_1;
    private widget.TextBox nm_psdr_10;
    private widget.TextBox nm_psdr_11;
    private widget.TextBox nm_psdr_12;
    private widget.TextBox nm_psdr_13;
    private widget.TextBox nm_psdr_14;
    private widget.TextBox nm_psdr_15;
    private widget.TextBox nm_psdr_16;
    private widget.TextBox nm_psdr_17;
    private widget.TextBox nm_psdr_18;
    private widget.TextBox nm_psdr_19;
    private widget.TextBox nm_psdr_2;
    private widget.TextBox nm_psdr_20;
    private widget.TextBox nm_psdr_3;
    private widget.TextBox nm_psdr_4;
    private widget.TextBox nm_psdr_5;
    private widget.TextBox nm_psdr_6;
    private widget.TextBox nm_psdr_7;
    private widget.TextBox nm_psdr_8;
    private widget.TextBox nm_psdr_9;
    private widget.TextBox no_kartu;
    private widget.TextBox no_rawat;
    private widget.TextBox obat;
    private widget.TextBox obat_kemo;
    private widget.panelisi panelBawah;
    private widget.panelisi panelGlass9;
    private widget.ComboBox pasien_hd;
    private widget.TextBox pasien_tb;
    private widget.TextBox pb;
    private widget.TextBox pl_darah;
    private widget.TextBox pnb;
    private widget.TextBox pnj;
    private widget.TextBox rad;
    private javax.swing.JCheckBox rawat_intensif;
    private widget.TextBox rehab;
    private widget.Label responIDRG;
    private widget.TextBox ri;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollRespon1;
    private widget.TextBox sewa_alat;
    private widget.TextBox sistole;
    private widget.ComboBox status_pulang;
    private widget.TextBox sub_acute;
    private widget.Label tarifRS;
    private widget.TextBox tarif_eksekutif;
    public widget.Table tbDiagnosa;
    public widget.Table tbDiagnosa2;
    public widget.Table tbProsedur;
    public widget.Table tbProsedur2;
    private widget.Tanggal tgl_ekstubasi;
    private widget.Tanggal tgl_intubasi;
    private widget.Tanggal tgl_keluar;
    private widget.TextBox tgl_lahir;
    private widget.Tanggal tgl_masuk;
    private widget.Button validasi_tb;
    private javax.swing.JCheckBox ventilator;
    // End of variables declaration//GEN-END:variables

    public void emptTeks() {
        TNoRM.setText("");
        TPasien.setText("");
        Tsep.setText("");
        tgl_masuk.setDate(new Date());
        tgl_masuk.requestFocus();
    }

    public void setDiagnosa(String norw, String norm, String pasien) {
        //cek koder
        try {

            // Satu no_rawat bisa punya 2 SEP (ralan IGD + ranap). Pilih SEP yang cocok
            // dengan jenis kunjungan (Ranap->jnspelayanan '1', Ralan->'2') agar klaim ranap
            // memakai SEP ranap, bukan SEP ralan. Sebelumnya query tanpa urutan -> baris
            // terakhir (acak) yang terpakai.
            rs = koneksi.prepareStatement("select bs.* from bridging_sep bs left join reg_periksa rp on rp.no_rawat=bs.no_rawat where bs.no_rawat='" + norw + "' order by (bs.jnspelayanan=if(rp.status_lanjut='Ranap','1','2')) desc, bs.tglsep desc limit 1").executeQuery();
            while (rs.next()) {
                coder = Sequel.cariIsi("select no_ik from inacbg_coder_nik where nik='" + akses.getkode() + "'");
                if (akses.getkode().equals("Admin Utama")) {
                    coder = Sequel.cariIsi("select no_ik from inacbg_coder_nik LIMIT 1");
                } else if (!coder.equals("")) {
                    coder = coder;
                } else {
                    JOptionPane.showMessageDialog(this, "ANDA BUKAN CODER KLAIM, JADINYA TIDAK BISA !!!!!");
                }

                no_rawat.setText(norw);
                no_kartu.setText(rs.getString("no_kartu"));
                Tsep.setText(rs.getString("no_sep"));
                hapusKlaim4.setEnabled(true);

                TPasien.setText(pasien);
                jenis_kelamin.setText(rs.getString("jkel"));
                tgl_lahir.setText(rs.getString("tanggal_lahir"));
                TNoRM.setText(norm);

                if (rs.getString("jnspelayanan").equals("2")) {
                    hak_kelas.setSelectedIndex(2);
                } else {
                    switch (rs.getString("klsrawat")) {
                        case "1":
                            hak_kelas.setSelectedIndex(0);
                            break;
                        case "2":
                            hak_kelas.setSelectedIndex(1);
                            break;
                        case "3":
                            hak_kelas.setSelectedIndex(2);
                            break;
                        default:
                            throw new AssertionError();
                    }
                }

                if (rs.getString("kdpolitujuan").equals("HDL")) {
                    check_hd.setSelected(true);
                } else {
                    check_hd.setSelected(false);
                }

                pnb.setText(bulat(Sequel.cariIsi("select (select ifnull(sum(totalbiaya),0) from billing where status='Ralan Dokter Paramedis' and nm_perawatan not like '%terapi%' and no_rawat='" + no_rawat.getText() + "') + (select ifnull(sum(totalbiaya),0) from billing where status='Ranap Dokter Paramedis' and nm_perawatan not like '%terapi%' and no_rawat='" + no_rawat.getText() + "')"))); 
                ksl.setText(bulat(Sequel.cariIsi("select (select ifnull(sum(totalbiaya),0) from billing where status='Ralan Dokter' and no_rawat='" + no_rawat.getText() + "') + (select ifnull(sum(totalbiaya),0) from billing where status='Ranap Dokter' and no_rawat='" + no_rawat.getText()+ "')")));                
                kpr.setText(bulat(Sequel.cariIsi("select (select ifnull(sum(totalbiaya),0) from billing where status='Ranap Paramedis' and no_rawat='" + no_rawat.getText() + "') + (select ifnull(sum(totalbiaya),0) from billing where status='Ralan Paramedis' and no_rawat='" + no_rawat.getText() + "')")));
                pb.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Operasi'")));
                ahli.setText("0");
                rad.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Radiologi'")));
                lab.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Laborat'")));
                alkes.setText(bulat(Sequel.cariIsi("select (select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(ALKES)%' and no_rawat='" + no_rawat.getText() + "') + (select ifnull(sum(totalbiaya),0) from billing where (nm_perawatan like '%Lensa%' OR nm_perawatan like '%LENSA%' OR nm_perawatan like '%lensa%') and no_rawat='" + no_rawat.getText() + "')")));
                kronis.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(kronis)%' and no_rawat='" + no_rawat.getText() + "' and status='Obat'")));
                obat_kemo.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(kemo)%' and no_rawat='" + no_rawat.getText() + "' and status='Obat'")));
                obat.setText(bulat(Sequel.cariIsi("select (select ifnull(sum(totalbiaya),0) from billing where nm_perawatan not like '%(ALKES)%' and no_rawat='" + no_rawat.getText() + "' and status='Obat') + (select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Retur Obat') + (select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Resep Pulang') - ((select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%kronis%' and no_rawat='" + no_rawat.getText() + "' and status='Obat') + (select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%kemo%' and no_rawat='" + no_rawat.getText() + "' and status='Obat'))")));
                pnj.setText(bulat(Sequel.cariIsi("select (select ifnull(sum(totalbiaya),0) from billing where nm_perawatan not like '%selisih%' and no_rawat='" + no_rawat.getText() + "' and status='Tambahan') + (select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(PNG)%' and no_rawat='" + no_rawat.getText() + "')")));
                pl_darah.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(DRH)%' and no_rawat='" + no_rawat.getText() + "'")));
                bmhp.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(BMHP)%' and no_rawat='" + no_rawat.getText() + "'")));
                sewa_alat.setText(bulat(Sequel.cariIsi("select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(SEWA)%' and no_rawat='" + no_rawat.getText() + "'")));
                akomodasi.setText(bulat(Sequel.cariIsi("SELECT (select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Kamar')+(select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Harian')+(select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Service')+(select ifnull(sum(totalbiaya),0) from billing where no_rawat='" + no_rawat.getText() + "' and status='Registrasi')+(select ifnull(sum(totalbiaya),0) from billing where nm_perawatan like '%(HRP)%' and no_rawat='" + no_rawat.getText() + "')")));
                rehab.setText("0");
                ri.setText("0");

//                set jumlah darah hd
                kt_darah.setText(Sequel.cariIsi("select jumlah from billing where nm_perawatan like '%(DRH)%' and no_rawat='" + no_rawat.getText() + "'"));
                pasien_tb.setText(Sequel.cariIsi("select no_sitb from inacbg_validasi_tb where no_rkm_medis='" + TNoRM.getText().trim() + "' "));
                hitungtotalbiaya();

                no_rawat_bayi = Sequel.cariIsi("SELECT ranap_gabung.no_rawat2 from ranap_gabung WHERE ranap_gabung.no_rawat='" + no_rawat.getText().trim() + "' ");
                berat_bayi.setText(Sequel.cariIsi("SELECT pasien_bayi.berat_badan FROM pasien_bayi INNER JOIN reg_periksa ON reg_periksa.no_rkm_medis=pasien_bayi.no_rkm_medis WHERE reg_periksa.no_rawat='" + no_rawat_bayi + "'"));

                try {
                    rs = koneksi.prepareStatement("select reg_periksa.tgl_registrasi, reg_periksa.jam_reg, reg_periksa.status_lanjut, reg_periksa.status_bayar, dokter.nm_dokter from reg_periksa inner join "
                            + "dokter on reg_periksa.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + no_rawat.getText() + "'").executeQuery();
                    if (rs.next()) {
                        if (rs.getString("status_lanjut").equals("Ralan")) {
                            dokterkonsul = Sequel.cariIsi("select concat(', ',dokter.nm_dokter) from reg_periksa inner "
                                    + "join konsultasi_medik on konsultasi_medik.no_rawat=reg_periksa.no_rawat inner join dokter on konsultasi_medik.kd_dokter_dikonsuli=dokter.kd_dokter where "
                                    + "reg_periksa.no_rawat='" + no_rawat.getText() + "'");

                            tgl_masuk.setDate(rs.getDate("tgl_registrasi"));
                            tgl_keluar.setDate(rs.getDate("tgl_registrasi"));
                            jam_masuk.setSelectedItem(rs.getString("jam_reg").substring(0, 2));
                            jam_keluar.setSelectedItem(rs.getString("jam_reg").substring(0, 2));
                            menit_masuk.setSelectedItem(rs.getString("jam_reg").substring(3, 5));
                            menit_keluar.setSelectedItem(rs.getString("jam_reg").substring(3, 5));
                            dpjp.setText(rs.getString("nm_dokter") + dokterkonsul);
                        } else {
                            jenis_rawat.setSelectedIndex(0);

                            tgl_masuk_ranap = Sequel.cariIsi("SELECT DISTINCT(concat(tgl_masuk,' ',jam_masuk)) as jam_masuk FROM kamar_inap WHERE no_rawat='" + no_rawat.getText() + "' ORDER BY tgl_masuk ASC LIMIT 1");
                            tgl_keluar_ranap = Sequel.cariIsi("SELECT DISTINCT(concat(tgl_keluar,' ',jam_keluar)) as jam_keluar FROM kamar_inap WHERE no_rawat='" + no_rawat.getText() + "' AND tgl_keluar<>'0000-00-00' ORDER BY tgl_masuk DESC LIMIT 1");

                            if (tgl_keluar_ranap.equals("")) {
                                tgl_keluar_ranap = tgl_masuk_ranap;
                                JOptionPane.showMessageDialog(this, "PASIEN INI BELUM DIPULANGKAN !!!!!!");
                            }

                            LocalDateTime tanggalWaktu1 = LocalDateTime.parse(tgl_masuk_ranap, formatter);
                            LocalDateTime tanggalWaktu2 = LocalDateTime.parse(tgl_keluar_ranap, formatter);

                            // Menghitung selisih hari
                            long selisihHari = ChronoUnit.DAYS.between(tanggalWaktu1, tanggalWaktu2);
                            long selisihJam = ChronoUnit.HOURS.between(tanggalWaktu1, tanggalWaktu2);

                            try {
                                rs3 = koneksi.prepareStatement(
                                        "select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat='" + no_rawat.getText() + "'").executeQuery();

                                while (rs3.next()) {
                                    dpjp_ranap = dpjp_ranap + rs3.getString("nm_dokter") + ", ";
                                }
                            } catch (Exception e) {
                                System.out.println("Status Lanjut : " + e);
                            }

                            Valid.SetTgl(tgl_masuk, tgl_masuk_ranap.substring(0, 10));
                            Valid.SetTgl(tgl_keluar, tgl_keluar_ranap.substring(0, 10));
                            jam_masuk.setSelectedItem(tgl_masuk_ranap.substring(11, 13));
                            jam_keluar.setSelectedItem(tgl_keluar_ranap.substring(11, 13));
                            menit_masuk.setSelectedItem(tgl_masuk_ranap.substring(14, 16));
                            menit_keluar.setSelectedItem(tgl_keluar_ranap.substring(14, 16));
                            dpjp.setText(dpjp_ranap);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("error eklaim : " + e);
                }

                jLabel49.setVisible(false);
                jLabel53.setVisible(false);

                check_hdItemStateChanged(null);
                jenis_rawatItemStateChanged(null);
                rawat_intensifItemStateChanged(null);
                kelas_eksekutifItemStateChanged(null);
                naik_turun_kelasItemStateChanged(null);

//                cek sudah di grouper 
                try {
                    rsgrouper = koneksi.prepareStatement("select * from inacbg_grouping_stage1 where no_sep='" + Tsep.getText().trim() + "'").executeQuery();
                    while (rsgrouper.next()) {
                        lblTarifRS.setText(tarifRS.getText());
                        lblBaseTariff.setText(Valid.SetAngka(rsgrouper.getDouble("tarif")));
                        Txtgroup.setText(rsgrouper.getString("deskripsi"));
                        lJenis1.setText(rsgrouper.getString("code_cbg"));
                    }
                } catch (Exception e) {
                    System.out.println("error tampil : " + e);
                }

                for (i = 0; i < kd_psdr.length; i++) {
                    kd_diag[i].setText("");
                    kd_diag2[i].setText("");
                    nm_diag[i].setText("");
                    nm_diag2[i].setText("");
                    kd_psdr[i].setText("");
                    kd_psdr2[i].setText("");
                    nm_psdr[i].setText("");
                    nm_psdr2[i].setText("");
                    chk_diag[i].setSelected(false);
                    chk_diag2[i].setSelected(false);
                    multiple[i].setText("");
                }

                try {

                    rs = koneksi.prepareStatement(
                            "select * from idrg_diagnosa where no_sep='" + Tsep.getText().trim() + "'"
                    ).executeQuery();

                    j = 0;
                    while (rs.next() && j < kd_diag.length) {
                        kd_diag[j].setText(rs.getString("icd10"));
                        nm_diag[j].setText(rs.getString("icd10_desc"));
                        if (rs.getString("prioritas").equals("1")) {
                            chk_diag[j].setSelected(true);
                        } else {
                            chk_diag[j].setSelected(false);
                        }

                        j++;
                    }
                } catch (Exception e) {
                    System.out.println("error idrg diagnosa : " + e);
                }

                // fallback: kalau idrg_diagnosa belum ada, tarik dari diagnosa_pasien (rekam medis)
                if (kd_diag[0].getText().trim().equals("")) {
                    try {
                        rs = koneksi.prepareStatement(
                                "select dp.kd_penyakit as icd10, ifnull(pny.nm_penyakit,'') as icd10_desc, dp.prioritas "
                                + "from diagnosa_pasien dp left join penyakit pny on pny.kd_penyakit=dp.kd_penyakit "
                                + "where dp.no_rawat='" + no_rawat.getText().trim() + "' order by dp.prioritas, dp.status_penyakit").executeQuery();
                        j = 0;
                        while (rs.next() && j < kd_diag.length) {
                            kd_diag[j].setText(rs.getString("icd10"));
                            nm_diag[j].setText(rs.getString("icd10_desc"));
                            chk_diag[j].setSelected("1".equals(rs.getString("prioritas")));
                            j++;
                        }
                    } catch (Exception e) {
                        System.out.println("error diagnosa_pasien : " + e);
                    }
                }

                try {

                    rs = koneksi.prepareStatement(
                            "select * from inacbg_diagnosa where no_sep='" + Tsep.getText().trim() + "'"
                    ).executeQuery();

                    j = 0;
                    while (rs.next() && j < kd_diag2.length) {
                        kd_diag2[j].setText(rs.getString("icd10"));
                        nm_diag2[j].setText(rs.getString("icd10_desc"));
                        if (rs.getString("prioritas").equals("1")) {
                            chk_diag2[j].setSelected(true);
                        } else {
                            chk_diag2[j].setSelected(false);
                        }

                        j++;
                    }

                } catch (Exception e) {
                    System.out.println("error INA diagnosa : " + e);
                }

                try {

                    rs = koneksi.prepareStatement(
                            "select * from idrg_prosedur where no_sep='" + Tsep.getText().trim() + "'"
                    ).executeQuery();

                    j = 0;
                    while (rs.next() && j < kd_psdr.length) {
                        kd_psdr[j].setText(rs.getString("kd_icd9"));
                        nm_psdr[j].setText(rs.getString("nm_icd9"));
                        multiple[j].setText(rs.getString("multiplicity"));

                        j++;
                    }
                } catch (Exception e) {
                    System.out.println("error idrg diagnosa : " + e);
                }

                // fallback: kalau idrg_prosedur belum ada, tarik dari prosedur_pasien (rekam medis)
                if (kd_psdr[0].getText().trim().equals("")) {
                    try {
                        rs = koneksi.prepareStatement(
                                "select pp.kode as kd_icd9, ifnull(i9.deskripsi_panjang,'') as nm_icd9, ifnull(pp.jumlah,1) as multiplicity "
                                + "from prosedur_pasien pp left join icd9 i9 on i9.kode=pp.kode "
                                + "where pp.no_rawat='" + no_rawat.getText().trim() + "' order by pp.prioritas").executeQuery();
                        j = 0;
                        while (rs.next() && j < kd_psdr.length) {
                            kd_psdr[j].setText(rs.getString("kd_icd9"));
                            nm_psdr[j].setText(rs.getString("nm_icd9"));
                            multiple[j].setText(rs.getString("multiplicity"));
                            j++;
                        }
                    } catch (Exception e) {
                        System.out.println("error prosedur_pasien : " + e);
                    }
                }

                try {

                    rs = koneksi.prepareStatement(
                            "select * from inacbg_prosedur where no_sep='" + Tsep.getText().trim() + "'"
                    ).executeQuery();

                    j = 0;
                    while (rs.next() && j < kd_psdr2.length) {
                        kd_psdr2[j].setText(rs.getString("kd_icd9"));
                        nm_psdr2[j].setText(rs.getString("nm_icd9"));

                        j++;
                    }

                } catch (Exception e) {
                    System.out.println("error INA prosedur : " + e);
                }

                try {
                    rs = koneksi.prepareStatement("select * from idrg_grouper where no_sep='" + Tsep.getText().trim() + "'").executeQuery();
                    while (rs.next()) {
                        responIDRG.setText(
                                "<html>"
                                + "JENIS RAWAT : " + jenis_rawat.getSelectedItem().toString() + "<br><br>"
                                + "MDC : " + rs.getString("mdc_desc") + " (" + rs.getString("mdc") + ")<br><br>"
                                + "DRG : " + rs.getString("drg_desc") + " (" + rs.getString("drg") + ")"
                                + "</html>"
                        );
                    }
                } catch (Exception e) {
                    System.out.println("error tampil : " + e);
                }

                isLoading = false;
                try {
                    rs = koneksi.prepareStatement("select * from inacbg_grouping_stage2 where no_sep='" + Tsep.getText().trim() + "'").executeQuery();
                    while (rs.next()) {
                        if (rs.getString("type").toLowerCase().contains("prosthesis")) {
                            lblTarifPros.setText(rs.getString("tarif"));
                            cmbPros.addItem(rs.getString("deskripsi"));
                            cmbPros.setSelectedIndex(1);
                        }

                        if (rs.getString("type").toLowerCase().contains("procedure")) {
                            lbltarifProc.setText(rs.getString("tarif"));
                            cmbProc.addItem(rs.getString("deskripsi"));
                            cmbProc.setSelectedIndex(1);
                        }

                        if (rs.getString("type").toLowerCase().contains("investigation")) {
                            lblTarifInv.setText(rs.getString("tarif"));
                            cmbInv.addItem(rs.getString("deskripsi"));
                            cmbInv.setSelectedIndex(1);
                        }

                        if (rs.getString("type").toLowerCase().contains("drugs")) {
                            lblTarifDrugs.setText(rs.getString("tarif"));
                            cmbDrugs.addItem(rs.getString("deskripsi"));
                            cmbDrugs.setSelectedIndex(1);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("error tampil : " + e);
                }

                double totalTarif = 0;

                double totalStage1 = Sequel.cariIsiAngka(
                        "SELECT IFNULL(SUM(tarif), 0) FROM inacbg_grouping_stage1 WHERE no_sep = ?", Tsep.getText().trim()
                );

                double totalStage2 = Sequel.cariIsiAngka(
                        "SELECT IFNULL(SUM(tarif), 0) FROM inacbg_grouping_stage2 WHERE no_sep = ?", Tsep.getText().trim()
                );

                totalTarif = totalStage1 + totalStage2;

                lblTarifTotal.setText(Valid.SetAngka(totalTarif));

                getICD10();
                getICD10INA();
                getICD9();
                getICD9INA();

                tombolinacbg();
                pasangFiturEklaim();
                cekStatusEklaim(true);
            }

        } catch (Exception e) {
            System.out.println("error : " + e);
        }
    }

    public void hitungtotalbiaya() {
        if (pnb.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            pnb.requestFocus();
        } else if (pb.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            pb.requestFocus();
        } else if (rad.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            rad.requestFocus();
        } else if (rad.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            rad.requestFocus();
        } else if (rehab.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            rehab.requestFocus();
        } else if (obat.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            obat.requestFocus();
        } else if (alkes.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            alkes.requestFocus();
        } else if (kpr.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            kpr.requestFocus();
        } else if (lab.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            lab.requestFocus();
        } else if (akomodasi.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            akomodasi.requestFocus();
        } else if (kronis.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            kronis.requestFocus();
        } else if (bmhp.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            bmhp.requestFocus();
        } else if (ksl.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            ksl.requestFocus();
        } else if (pnb.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            pnb.requestFocus();
        } else if (pnj.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            pnj.requestFocus();
        } else if (pl_darah.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            pl_darah.requestFocus();
        } else if (ri.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            ri.requestFocus();
        } else if (obat_kemo.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            obat_kemo.requestFocus();
        } else if (sewa_alat.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA INI TIDAK BOLEH KOSONG DAN HANYA BOLEH ANGKA !!!!");
            sewa_alat.requestFocus();
        } else {
            total_biaya = (Integer.parseInt(pnb.getText()) + Integer.parseInt(pb.getText()) + Integer.parseInt(ksl.getText()) + Integer.parseInt(ahli.getText()) + Integer.parseInt(kpr.getText()) + Integer.parseInt(rad.getText()) + Integer.parseInt(lab.getText()) + Integer.parseInt(alkes.getText()) + Integer.parseInt(kronis.getText()) + Integer.parseInt(obat_kemo.getText()) + Integer.parseInt(obat.getText()) + Integer.parseInt(pnj.getText()) + Integer.parseInt(pl_darah.getText()) + Integer.parseInt(bmhp.getText()) + Integer.parseInt(sewa_alat.getText()) + Integer.parseInt(rehab.getText()) + Integer.parseInt(ri.getText()) + Integer.parseInt(akomodasi.getText()));
            tarifRS.setText(String.valueOf(Valid.SetAngka(total_biaya)));
        }
    }

    private void idrg_diagnosa() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"idrg_diagnosa_set\",\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"diagnosa\":\"" + icd10.trim() + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response idrg1 : " + response);

                if (response.has("metadata")) {
                    if (code.getInt("code") == 200) {
                        System.out.println("set " + code.getString("method") + " : " + code.getString("message"));
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void idrg_prosedur() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            if (kd_psdr_1.getText().equals("")) {
                System.out.println("Tidak Ada Prosedur");
            } else {
                try {
                    // Create a human-readable JSON string
                    String requestJson = "{\n"
                            + "\"metadata\": {\n"
                            + "\"method\":\"idrg_procedure_set\",\n"
                            + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                            + "},\n"
                            + "\"data\": {\n"
                            + "\"procedure\":\"" + icd9.trim() + "\"\n"
                            + "}\n"
                            + "}";

                    // Use the JSON string in your request
                    String request = requestJson;
                    System.out.println("request JSON : " + requestJson);
                    JSONObject response = inacbg.request(request);
                    JSONObject code = response.getJSONObject("metadata");
                    System.out.println("response idrg prosedur 1 : " + response);

                    if (response.has("metadata")) {
                        if (code.getInt("code") == 200) {
                            System.out.println("set " + code.getString("method") + " : " + code.getString("message"));
                        } else {
                            JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                        }
                    } else {
                        System.out.println("response : " + response);
                        System.err.println("\n The 'data' field is missing in the response.");
                    }

                } catch (Exception e) {
                    System.err.println("Error in sitb: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void tombolinacbg() {
        String cek_grouper_idrg = Sequel.cariIsi("select * from idrg_grouper where no_sep=?", Tsep.getText().trim());
        String cek_grouper_ina1 = Sequel.cariIsi("select * from inacbg_grouping_stage1 where no_sep=?", Tsep.getText().trim());
        String cek_final_idrg = Sequel.cariIsi("select * from inacbg_idrg_final where no_sep=?", Tsep.getText().trim());
        String cek_final_ina = Sequel.cariIsi("select * from inacbg_final where no_sep=?", Tsep.getText().trim());
        String cek_final = Sequel.cariIsi("select * from inacbg_grouping_final where no_sep=?", Tsep.getText().trim());
        String gagal_idrg = Sequel.cariIsi("select mdc_desc from idrg_grouper where no_sep=?", Tsep.getText().trim());
        String gagal_ina = Txtgroup.getText().trim();
        if (cek_grouper_idrg.trim().equals("")) {
            TabRawat.setSelectedIndex(0);
            BtnGrouperIDRG.setVisible(true);
            finalklaimIDRG.setVisible(false);
            editKlaimIDRG.setVisible(false);
            Import.setVisible(false);
            BtnGrouperINA.setVisible(false);
            editKlaim.setVisible(false);
            finalklaim.setVisible(false);
            editINA.setVisible(false);
            finalINA.setVisible(false);
            kirimonline.setVisible(false);
            CetakKlaim.setVisible(false);

            for (int i = 0; i < h_diag.length; i++) {
                h_diag[i].setEnabled(true);
            }

            for (int i = 0; i < h_diag2.length; i++) {
                h_diag2[i].setEnabled(false);
            }

            for (int i = 0; i < h_psdr.length; i++) {
                h_psdr[i].setEnabled(true);
            }

            for (int i = 0; i < h_psdr2.length; i++) {
                h_psdr2[i].setEnabled(false);
            }

            for (int i = 0; i < chk_diag.length; i++) {
                chk_diag[i].setEnabled(true);
            }

            for (int i = 0; i < chk_diag2.length; i++) {
                chk_diag2[i].setEnabled(false);
            }

            INAdiagnosatambah.setEnabled(false);
            INAprosedurtambah.setEnabled(false);
            idrgPenyakittambah.setEnabled(true);
            idrgProseduttambah.setEnabled(true);

            cmbProc.setEnabled(false);
            cmbDrugs.setEnabled(false);
            cmbPros.setEnabled(false);
            cmbInv.setEnabled(false);
            cmbSelisih.setEnabled(false);

        } else if (!cek_grouper_idrg.trim().equals("") && cek_final_idrg.trim().equals("")) {
            TabRawat.setSelectedIndex(0);
            BtnGrouperIDRG.setVisible(true);
            if (gagal_idrg.trim().toLowerCase().contains("ungroupable")
                    || gagal_idrg.trim().toLowerCase().contains("unrelated")
                    || gagal_idrg.trim().toLowerCase().contains("gagal")
                    || gagal_idrg.trim().toLowerCase().contains("error")
                    || gagal_idrg.trim().toLowerCase().contains("failed")
                    || gagal_idrg.trim().toLowerCase().contains("invalid")) {
                finalklaimIDRG.setVisible(false);
            } else {
                finalklaimIDRG.setVisible(true);
            }
            editKlaimIDRG.setVisible(false);
            Import.setVisible(false);
            BtnGrouperINA.setVisible(false);
            editKlaim.setVisible(false);
            finalklaim.setVisible(false);
            editINA.setVisible(false);
            finalINA.setVisible(false);
            kirimonline.setVisible(false);
            CetakKlaim.setVisible(false);

            cmbProc.setEnabled(false);
            cmbDrugs.setEnabled(false);
            cmbPros.setEnabled(false);
            cmbInv.setEnabled(false);
            cmbSelisih.setEnabled(false);

        } else if (!cek_final_idrg.trim().equals("") && cek_grouper_ina1.trim().equals("")) {
            TabRawat.setSelectedIndex(1);
            BtnGrouperIDRG.setVisible(false);
            finalklaimIDRG.setVisible(false);
            editKlaimIDRG.setVisible(true);
            Import.setVisible(true);
            BtnGrouperINA.setVisible(true);
            editKlaim.setVisible(false);
            finalklaim.setVisible(false);
            editINA.setVisible(false);
            finalINA.setVisible(false);
            kirimonline.setVisible(false);
            CetakKlaim.setVisible(false);

            for (int i = 0; i < h_diag.length; i++) {
                h_diag[i].setEnabled(false);
            }

            for (int i = 0; i < h_diag2.length; i++) {
                h_diag2[i].setEnabled(true);
            }

            for (int i = 0; i < h_psdr.length; i++) {
                h_psdr[i].setEnabled(false);
            }

            for (int i = 0; i < h_psdr2.length; i++) {
                h_psdr2[i].setEnabled(true);
            }

            for (int i = 0; i < chk_diag.length; i++) {
                chk_diag[i].setEnabled(false);
            }

            for (int i = 0; i < chk_diag2.length; i++) {
                chk_diag2[i].setEnabled(true);
            }

            INAdiagnosatambah.setEnabled(true);
            INAprosedurtambah.setEnabled(true);
            idrgPenyakittambah.setEnabled(false);
            idrgProseduttambah.setEnabled(false);

            cmbProc.setEnabled(false);
            cmbDrugs.setEnabled(false);
            cmbPros.setEnabled(false);
            cmbInv.setEnabled(false);
            cmbSelisih.setEnabled(false);

        } else if (!cek_grouper_ina1.trim().equals("") && cek_final_ina.trim().equals("")) {
            TabRawat.setSelectedIndex(1);
            BtnGrouperIDRG.setVisible(false);
            finalklaimIDRG.setVisible(false);
            editKlaimIDRG.setVisible(true);
            Import.setVisible(true);
            BtnGrouperINA.setVisible(true);
            finalklaim.setVisible(false);
            editKlaim.setVisible(false);
            editINA.setVisible(false);
            kirimonline.setVisible(false);
            CetakKlaim.setVisible(false);

            if (gagal_ina.trim().toLowerCase().contains("ungroupable")
                    || gagal_idrg.trim().toLowerCase().contains("unrelated")
                    || gagal_idrg.trim().toLowerCase().contains("gagal")
                    || gagal_idrg.trim().toLowerCase().contains("error")
                    || gagal_idrg.trim().toLowerCase().contains("failed")
                    || gagal_idrg.trim().toLowerCase().contains("invalid")) {
                finalINA.setVisible(false);
            } else {
                finalINA.setVisible(true);
            }

            for (int i = 0; i < h_diag.length; i++) {
                h_diag[i].setEnabled(false);
            }

            for (int i = 0; i < h_psdr.length; i++) {
                h_psdr[i].setEnabled(false);
            }

            for (i = 0; i < h_diag2.length; i++) {
                h_diag2[i].setEnabled(true);
            }

            for (i = 0; i < h_psdr2.length; i++) {
                h_psdr2[i].setEnabled(true);
            }

            for (int i = 0; i < chk_diag.length; i++) {
                chk_diag[i].setEnabled(false);
            }

            for (int i = 0; i < chk_diag2.length; i++) {
                chk_diag2[i].setEnabled(true);
            }

            INAdiagnosatambah.setEnabled(true);
            INAprosedurtambah.setEnabled(true);
            idrgPenyakittambah.setEnabled(false);
            idrgProseduttambah.setEnabled(false);

            cmbProc.setEnabled(true);
            cmbDrugs.setEnabled(true);
            cmbPros.setEnabled(true);
            cmbInv.setEnabled(true);
            cmbSelisih.setEnabled(true);

        } else if (!cek_grouper_ina1.trim().equals("") && !cek_final_ina.trim().equals("") && cek_final.trim().equals("")) {
            TabRawat.setSelectedIndex(1);
            BtnGrouperIDRG.setVisible(false);
            finalklaimIDRG.setVisible(false);
            editKlaimIDRG.setVisible(true);
            Import.setVisible(false);
            BtnGrouperINA.setVisible(false);
            finalklaim.setVisible(true);
            editKlaim.setVisible(false);
            finalINA.setVisible(false);
            editINA.setVisible(true);
            kirimonline.setVisible(false);
            CetakKlaim.setVisible(false);

            for (int i = 0; i < h_diag.length; i++) {
                h_diag[i].setEnabled(false);
            }

            for (int i = 0; i < h_psdr.length; i++) {
                h_psdr[i].setEnabled(false);
            }

            for (i = 0; i < h_diag2.length; i++) {
                h_diag2[i].setEnabled(false);
            }

            for (i = 0; i < h_psdr2.length; i++) {
                h_psdr2[i].setEnabled(false);
            }
            for (int i = 0; i < chk_diag.length; i++) {
                chk_diag[i].setEnabled(false);
            }

            for (int i = 0; i < chk_diag2.length; i++) {
                chk_diag2[i].setEnabled(false);
            }

            INAdiagnosatambah.setEnabled(false);
            INAprosedurtambah.setEnabled(false);
            idrgPenyakittambah.setEnabled(false);
            idrgProseduttambah.setEnabled(false);

            cmbProc.setEnabled(true);
            cmbDrugs.setEnabled(true);
            cmbPros.setEnabled(true);
            cmbInv.setEnabled(true);
            cmbSelisih.setEnabled(true);

        } else if (!cek_final.trim().equals("")) {

            for (int i = 0; i < h_diag.length; i++) {
                h_diag[i].setEnabled(false);
            }

            for (int i = 0; i < h_psdr.length; i++) {
                h_psdr[i].setEnabled(false);
            }

            for (i = 0; i < h_diag2.length; i++) {
                h_diag2[i].setEnabled(false);
            }

            for (i = 0; i < h_psdr2.length; i++) {
                h_psdr2[i].setEnabled(false);
            }
            for (int i = 0; i < chk_diag.length; i++) {
                chk_diag[i].setEnabled(false);
            }

            for (int i = 0; i < chk_diag2.length; i++) {
                chk_diag2[i].setEnabled(false);
            }

            INAdiagnosatambah.setEnabled(false);
            INAprosedurtambah.setEnabled(false);
            idrgPenyakittambah.setEnabled(false);
            idrgProseduttambah.setEnabled(false);

            cmbProc.setEnabled(false);
            cmbDrugs.setEnabled(false);
            cmbPros.setEnabled(false);
            cmbInv.setEnabled(false);
            cmbSelisih.setEnabled(false);

            TabRawat.setSelectedIndex(1);
            BtnGrouperIDRG.setVisible(false);
            finalklaimIDRG.setVisible(false);
            editKlaimIDRG.setVisible(false);
            Import.setVisible(false);
            BtnGrouperINA.setVisible(false);
            finalklaim.setVisible(false);
            editKlaim.setVisible(true);
            finalINA.setVisible(false);
            editINA.setVisible(false);
            kirimonline.setVisible(true);
            CetakKlaim.setVisible(true);
        }
    }

    private void grouperidrg() {
        responIDRG.setText("");

        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"grouper\",\n"
                        + "\"stage\":\"1\",\n"
                        + "\"grouper\":\"idrg\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response grouper idrg : " + response);

                if (response.has("metadata")) {
                    if (code.getInt("code") == 200) {
                        Sequel.meghapus("idrg_grouper", "no_sep", Tsep.getText());
                        JSONObject idrg = response.getJSONObject("response_idrg");
                        responIDRG.setText(
                                "<html>"
                                + "JENIS RAWAT : " + jenis_rawat.getSelectedItem().toString() + "<br><br>"
                                + "MDC : " + idrg.getString("mdc_description") + " (" + idrg.getString("mdc_number") + ")<br><br>"
                                + "DRG : " + idrg.getString("drg_description") + " (" + idrg.getString("drg_code") + ")"
                                + "</html>"
                        );
                        Sequel.menyimpantf2("idrg_grouper", "?,?,?,?,?", "GROUPER IDRG", 5, new String[]{
                            Tsep.getText(), idrg.getString("mdc_number"), idrg.getString("mdc_description"), idrg.getString("drg_code"), idrg.getString("drg_description")
                        });

                        // PEMBARUAN API 2026-04: tawarkan top-up jika ada opsi eligible (mis. implan)
                        if (idrg.has("topup_options")) {
                            org.json.JSONArray options = idrg.getJSONArray("topup_options");
                            if (options.length() > 0) {
                                String[] labels = new String[options.length() + 1];
                                String[] codes  = new String[options.length() + 1];
                                labels[0] = "(Tidak Pakai Top-Up)";
                                codes[0]  = "";
                                for (int k = 0; k < options.length(); k++) {
                                    org.json.JSONObject opt = options.getJSONObject(k);
                                    labels[k + 1] = opt.optString("description", "?") + " ["
                                                  + opt.optString("type", "?") + ", CW "
                                                  + opt.optString("cost_weight", "?") + "]";
                                    codes[k + 1]  = opt.optString("code", "");
                                }
                                String pick = (String) JOptionPane.showInputDialog(this,
                                    "Pilih Top-Up iDRG (kasus eligible terdeteksi)\n"
                                  + "DRG: " + idrg.getString("drg_description") + " (CW " + idrg.optString("cost_weight","-") + ")",
                                    "Top-Up iDRG", JOptionPane.QUESTION_MESSAGE,
                                    null, labels, labels[0]);
                                if (pick != null && !pick.equals(labels[0])) {
                                    for (int k = 0; k < labels.length; k++) {
                                        if (labels[k].equals(pick)) {
                                            grouperidrg2(codes[k]);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        finalklaimIDRG.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

            } catch (Exception e) {
                System.err.println("Error idrg : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /** iDRG Grouper Stage 2 dengan top-up codes (pembaruan API E-Klaim April 2026). */
    private void grouperidrg2(String topupCodes) {
        if (Tsep.getText().trim().isEmpty()) return;
        try {
            String requestJson = "{\n"
                    + "\"metadata\": {\n"
                    + "\"method\":\"grouper\",\n"
                    + "\"stage\":\"2\",\n"
                    + "\"grouper\":\"idrg\"\n"
                    + "},\n"
                    + "\"data\": {\n"
                    + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\",\n"
                    + "\"topup_codes\":\"" + topupCodes + "\"\n"
                    + "}\n"
                    + "}";
            System.out.println("request JSON (iDRG Stage 2 Top-Up) : " + requestJson);
            JSONObject response = inacbg.request(requestJson);
            System.out.println("response iDRG Stage 2 : " + response);

            JSONObject meta = response.getJSONObject("metadata");
            if (meta.getInt("code") != 200) {
                JOptionPane.showMessageDialog(this, "RESPON Top-Up iDRG : " + pesanEklaim(meta));
                return;
            }
            JSONObject idrg = response.getJSONObject("response_idrg");

            double drgCw = 0.0;
            double nbr   = 0.0;
            try { drgCw = Double.parseDouble(idrg.optString("cost_weight", "0")); } catch (NumberFormatException ignored) {}
            try { nbr   = Double.parseDouble(idrg.optString("nbr", "0")); }         catch (NumberFormatException ignored) {}

            double totalCw = drgCw;
            org.json.JSONArray topups = idrg.has("topup") ? idrg.getJSONArray("topup") : new org.json.JSONArray();
            for (int k = 0; k < topups.length(); k++) {
                try {
                    totalCw += Double.parseDouble(topups.getJSONObject(k).optString("cost_weight", "0"));
                } catch (NumberFormatException ignored) {}
            }

            double totalKlaim = Math.ceil(nbr * totalCw);

            StringBuilder summary = new StringBuilder(
                "<html>"
              + "<b>Top-Up iDRG berhasil di-apply.</b><br><br>"
              + "DRG: " + idrg.optString("drg_description", "-") + "<br>"
              + "DRG Cost Weight: " + idrg.optString("cost_weight", "-") + "<br>");
            for (int k = 0; k < topups.length(); k++) {
                org.json.JSONObject t = topups.getJSONObject(k);
                summary.append("Top-Up ").append(t.optString("type", "-")).append(": ")
                       .append(t.optString("description", "-"))
                       .append(" (CW ").append(t.optString("cost_weight", "-")).append(")<br>");
            }
            summary.append("Total Cost Weight: ").append(String.format("%.2f", totalCw)).append("<br>")
                   .append("NBR: ").append(String.format("%,.0f", nbr)).append("<br>")
                   .append("<b>Total Klaim: Rp ").append(String.format("%,.0f", totalKlaim)).append("</b></html>");
            JOptionPane.showMessageDialog(this, summary.toString(), "Top-Up iDRG", JOptionPane.INFORMATION_MESSAGE);

            StringBuilder responText = new StringBuilder(
                "<html>"
              + "JENIS RAWAT : " + jenis_rawat.getSelectedItem().toString() + "<br><br>"
              + "MDC : " + idrg.optString("mdc_description", "-") + " (" + idrg.optString("mdc_number", "-") + ")<br><br>"
              + "DRG : " + idrg.optString("drg_description", "-") + " (" + idrg.optString("drg_code", "-") + ")<br>"
              + "DRG Cost Weight : " + idrg.optString("cost_weight", "-") + "<br><br>");
            for (int k = 0; k < topups.length(); k++) {
                org.json.JSONObject t = topups.getJSONObject(k);
                responText.append("Top-Up [").append(t.optString("type", "-")).append("] : ")
                          .append(t.optString("description", "-"))
                          .append(" (CW ").append(t.optString("cost_weight", "-")).append(")<br>");
            }
            responText.append("<br>")
                      .append("<b>Total Cost Weight : ").append(String.format("%.2f", totalCw)).append("</b><br>")
                      .append("NBR : ").append(String.format("%,.0f", nbr)).append("<br>")
                      .append("<b>Total Klaim : Rp ").append(String.format("%,.0f", totalKlaim)).append("</b>")
                      .append("</html>");
            responIDRG.setText(responText.toString());

            Sequel.meghapus("idrg_topup", "no_sep", Tsep.getText());
            for (int k = 0; k < topups.length(); k++) {
                org.json.JSONObject t = topups.getJSONObject(k);
                Sequel.menyimpantf2("idrg_topup", "?,?,?,?,?", "TOP-UP IDRG", 5, new String[]{
                    Tsep.getText(),
                    t.optString("code", ""),
                    t.optString("description", ""),
                    t.optString("type", ""),
                    t.optString("cost_weight", "")
                });
            }
        } catch (Exception e) {
            System.err.println("Error grouperidrg2 : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Kirim id_encounter SatuSehat ke E-Klaim untuk validasi bridging RME (API 2026-04).
     * Method: satusehat_encounter_set; data.encounters[] = UUID dari satu_sehat_encounter.
     */
    private void satusehatEncounterSet() {
        if (Tsep.getText().trim().isEmpty() || no_rawat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor SEP / No.Rawat kosong.");
            return;
        }
        java.util.List<String> encounters = new java.util.ArrayList<>();
        try (java.sql.PreparedStatement ps = fungsi.koneksiDB.condb().prepareStatement(
                "select id_encounter from satu_sehat_encounter where no_rawat=? and ifnull(id_encounter,'') <> ''")) {
            ps.setString(1, no_rawat.getText().trim());
            java.sql.ResultSet rsE = ps.executeQuery();
            while (rsE.next()) encounters.add(rsE.getString(1));
            rsE.close();
        } catch (Exception e) {
            System.err.println("Notif lookup satu_sehat_encounter : " + e);
        }
        if (encounters.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Pasien ini belum punya Encounter ter-sync ke SatuSehat.\n"
              + "Silakan kirim Encounter SatuSehat dulu sebelum bind ke klaim E-Klaim.");
            return;
        }

        StringBuilder encArr = new StringBuilder();
        for (int k = 0; k < encounters.size(); k++) {
            if (k > 0) encArr.append(",");
            encArr.append("\"").append(encounters.get(k)).append("\"");
        }

        try {
            String requestJson = "{\n"
                    + "\"metadata\": {\n"
                    + "\"method\":\"satusehat_encounter_set\",\n"
                    + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                    + "},\n"
                    + "\"data\": {\n"
                    + "\"encounters\": [" + encArr + "]\n"
                    + "}\n"
                    + "}";
            System.out.println("request JSON (SatuSehat Encounter Set) : " + requestJson);
            JSONObject response = inacbg.request(requestJson);
            System.out.println("response SatuSehat Encounter Set : " + response);

            JSONObject meta = response.getJSONObject("metadata");
            if (meta.getInt("code") != 200) {
                JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(meta));
                return;
            }
            StringBuilder sum = new StringBuilder("<html><b>SatuSehat Encounter Set berhasil.</b><br><br>");
            if (response.has("data")) {
                org.json.JSONArray data = response.getJSONArray("data");
                for (int k = 0; k < data.length(); k++) {
                    org.json.JSONObject enc = data.getJSONObject(k);
                    sum.append("Encounter: ").append(enc.optString("encounter_id", "-"))
                       .append(" — valid=").append(enc.optBoolean("valid", false)).append("<br>");
                    if (enc.has("statistic")) {
                        org.json.JSONArray stat = enc.getJSONArray("statistic");
                        for (int j = 0; j < stat.length(); j++) {
                            org.json.JSONObject s = stat.getJSONObject(j);
                            sum.append("&nbsp;&nbsp;").append(s.optString("resourceType", "-"))
                               .append(": ").append(s.optInt("count", 0)).append("<br>");
                        }
                    }
                }
            }
            sum.append("</html>");
            JOptionPane.showMessageDialog(this, sum.toString(), "SatuSehat Encounter Set", JOptionPane.INFORMATION_MESSAGE);

            Sequel.meghapus("inacbg_satusehat_encounter", "no_sep", Tsep.getText());
            for (String idEnc : encounters) {
                Sequel.menyimpantf2("inacbg_satusehat_encounter", "?,?", "INACBG-SATUSEHAT", 2,
                    new String[]{Tsep.getText(), idEnc});
            }
        } catch (Exception e) {
            System.err.println("Error satusehatEncounterSet : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void idrgtoinacbg() {
        if (TNoRM.getText().trim().equals("") || Tsep.getText().trim().equals("") || no_kartu.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "DATA NOMOR REKAM MEDIS / NOMOR SEP / NOMOR KARTU TIDAK TERISI !!!!!");
        } else {
            try {
                // Create a human-readable JSON string
                String requestJson = "{\n"
                        + "\"metadata\": {\n"
                        + "\"method\":\"idrg_to_inacbg_import\"\n"
                        + "},\n"
                        + "\"data\": {\n"
                        + "\"nomor_sep\":\"" + Tsep.getText().trim() + "\"\n"
                        + "}\n"
                        + "}";

                // Use the JSON string in your request
                String request = requestJson;
                System.out.println("request JSON : " + requestJson);
                JSONObject response = inacbg.request(request);
                JSONObject code = response.getJSONObject("metadata");
                System.out.println("response import idrg to inacbg : " + response);

                if (response.has("metadata")) {
                    if (code.getInt("code") == 200) {
                        System.out.println("response import idrg : " + code.getString("message"));
                    } else {
                        JOptionPane.showMessageDialog(this, "RESPON : " + pesanEklaim(code));
                    }
                } else {
                    System.out.println("response : " + response);
                    System.err.println("\n The 'data' field is missing in the response.");
                }

                if (response.has("data")) {
                    JSONObject data = response.getJSONObject("data");

                    // Ambil diagnosa
                    JSONObject diagnosa = data.getJSONObject("diagnosa");
                    String diagnosaString = diagnosa.getString("string");
                    JSONArray diagnosaExpanded = diagnosa.getJSONArray("expanded");

                    // Ambil prosedur
                    JSONObject prosedur = data.getJSONObject("procedure");
                    String prosedurString = prosedur.getString("string");
                    JSONArray prosedurExpanded = prosedur.getJSONArray("expanded");

                    System.out.println("Diagnosa: " + diagnosaString);
                    for (int i = 0; i < diagnosaExpanded.length(); i++) {
                        JSONObject dx = diagnosaExpanded.getJSONObject(i);
                        if (dx.getString("validcode").equals("0")) {
                            JOptionPane.showMessageDialog(this, "DIAGNOSA " + dx.getString("code") + " " + dx.getString("display") + " (TIDAK BERLAKU)");
                        } else {
                            kd_diag2[i].setText(dx.getString("code"));
                            nm_diag2[i].setText(dx.getString("display"));
                            System.out.println("  - " + dx.getString("code") + " : " + dx.getString("display"));
                        }
                    }

                    System.out.println("Prosedur: " + prosedurString);
                    for (int i = 0; i < prosedurExpanded.length(); i++) {
                        JSONObject pr = prosedurExpanded.getJSONObject(i);
                        if (pr.getString("validcode").equals("0")) {
                            JOptionPane.showMessageDialog(this, "DIAGNOSA " + pr.getString("code") + " " + pr.getString("display") + " (IM TIDAK BERLAKU)");
                        } else {
                            kd_psdr2[i].setText(pr.getString("code"));
                            nm_psdr2[i].setText(pr.getString("display"));
                            System.out.println("  - " + pr.getString("code") + " : " + pr.getString("display"));
                        }
                    }

                    for (i = 0; i < kd_diag2.length; i++) {
                        if (!kd_diag2[i].getText().trim().equals("")) {
                            Sequel.menyimpantf2("inacbg_diagnosa", "?,?,?,?", "INA EKLAIM", 4, new String[]{
                                Tsep.getText(), kd_diag2[i].getText(), nm_diag2[i].getText(), (chk_diag2[i].isSelected() == true ? "1" : "0")
                            });
                        }
                    }

                    for (i = 0; i < kd_psdr2.length; i++) {
                        if (!kd_psdr2[i].getText().trim().equals("")) {
                            Sequel.menyimpantf2("inacbg_prosedur", "?,?,?", "INA PSDR EKLAIM", 3, new String[]{
                                Tsep.getText(), kd_psdr2[i].getText(), nm_psdr2[i].getText()
                            });
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Error in sitb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void tampildiagnosa() {
        try {
            Valid.tabelKosong(tabModeDiagnosa);
            psdiagnosa = koneksi.prepareStatement(
                    "select * from icd_idrg where SYSTEM='ICD_10_2010_IM' and "
                    + "(DESCRIPTION like ? or CODE like ?) order by CODE LIMIT 50"
            );
            try {
                psdiagnosa.setString(1, "%" + Diagnosa.getText().trim() + "%");
                psdiagnosa.setString(2, "%" + Diagnosa.getText().trim() + "%");
                rs = psdiagnosa.executeQuery();
                while (rs.next()) {
                    tabModeDiagnosa.addRow(new Object[]{
                        false,
                        rs.getString("CODE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("ACCPDX"),
                        rs.getString("ASTERISK"),
                        rs.getString("VALIDCODE")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (psdiagnosa != null) {
                    psdiagnosa.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampildiagnosaINA() {
        try {
            Valid.tabelKosong(tabModeDiagnosa2);
            psdiagnosa = koneksi.prepareStatement(
                    "select * from icd_inacbg where SYSTEM='ICD_10_2010' and VALIDCODE='1' and "
                    + "(DESCRIPTION like ? or CODE like ?) order by CODE LIMIT 50"
            );
            try {
                psdiagnosa.setString(1, "%" + DiagnosaINACBG.getText().trim() + "%");
                psdiagnosa.setString(2, "%" + DiagnosaINACBG.getText().trim() + "%");
                rs = psdiagnosa.executeQuery();
                while (rs.next()) {
                    tabModeDiagnosa2.addRow(new Object[]{
                        false,
                        rs.getString("CODE"),
                        rs.getString("DESCRIPTION")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (psdiagnosa != null) {
                    psdiagnosa.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilprosedure() {
        try {
            Valid.tabelKosong(tabModeProsedur);
            psprosedur = koneksi.prepareStatement(
                    "select * from icd_idrg where SYSTEM='ICD_9CM_2010_IM' "
                    + "and (DESCRIPTION like ? or CODE like ?) order by CODE LIMIT 50"
            );
            try {
                psprosedur.setString(1, "%" + Prosedur.getText().trim() + "%");
                psprosedur.setString(2, "%" + Prosedur.getText().trim() + "%");
                rs = psprosedur.executeQuery();
                while (rs.next()) {
                    tabModeProsedur.addRow(new Object[]{
                        false,
                        rs.getString("CODE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("ACCPDX"),
                        rs.getString("ASTERISK"),
                        rs.getString("VALIDCODE")
                    });
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi : " + ex);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (psprosedur != null) {
                    psprosedur.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilprosedureINA() {
        try {

            Valid.tabelKosong(tabModeProsedur2);
            psprosedur = koneksi.prepareStatement(
                    "select * from icd_inacbg where VALIDCODE='1' and SYSTEM='ICD_9CM_2010' "
                    + "and (DESCRIPTION like ? or CODE like ?) order by CODE LIMIT 50"
            );
            try {
                psprosedur.setString(1, "%" + ProsedurINACBG.getText().trim() + "%");
                psprosedur.setString(2, "%" + ProsedurINACBG.getText().trim() + "%");
                rs = psprosedur.executeQuery();
                while (rs.next()) {
                    tabModeProsedur2.addRow(new Object[]{
                        false,
                        rs.getString("CODE"),
                        rs.getString("DESCRIPTION")
                    });
                }
            } catch (Exception ex) {
                System.out.println("Notifikasi : " + ex);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (psprosedur != null) {
                    psprosedur.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void getICD9() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kd_psdr.length; i++) {
            String kode = kd_psdr[i].getText().trim();
            String multiVal = multiple[i].getText().trim();

            if (!kode.equals("")) {
                // Tambahkan separator antar prosedur
                if (sb.length() > 0) {
                    sb.append("#");
                }

                // Kalau multiple kosong atau "1" → tampilkan kode saja
                // Kalau lebih dari 1 → tambahkan +angka
                if (multiVal.equals("") || multiVal.equals("1")) {
                    sb.append(kode);
                } else {
                    sb.append(kode).append("+").append(multiVal);
                }
            }
        }
        icd9 = sb.toString();
        System.out.println("icd9 : " + icd9);
        responIDRG.setText("");
    }

    private void getICD9INA() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kd_psdr2.length; i++) {
            String kode = kd_psdr2[i].getText().trim();

            if (!kode.equals("")) {
                // Tambahkan separator antar prosedur
                if (sb.length() > 0) {
                    sb.append("#");
                }
            }
        }
        icd9INA = sb.toString();
        System.out.println("icd9 INA : " + icd9INA);
        responIDRG.setText("");
    }

    private void getICD10() {
        icd10 = "";

        String primary = "";
        List<String> secondary = new ArrayList<>();

        for (int i = 0; i < kd_diag.length; i++) {
            String kode = kd_diag[i].getText().trim();
            if (!kode.equals("")) {
                if (chk_diag[i].isSelected()) {
                    // Kalau checkbox dicentang → anggap primary
                    primary = kode;
                } else {
                    secondary.add(kode);
                }
            }
        }

        // Susun ICD10 (diagnosa) → primary dulu, baru sisanya
        StringBuilder sb = new StringBuilder();
        if (!primary.equals("")) {
            sb.append(primary);
        }
        for (String kode : secondary) {
            if (sb.length() > 0) {
                sb.append("#");
            }
            sb.append(kode);
        }

        icd10 = sb.toString();
        System.out.println("icd10 : " + icd10);
    }

    private void getICD10INA() {
        icd10 = "";

        String primary = "";
        List<String> secondary = new ArrayList<>();

        for (int i = 0; i < kd_diag2.length; i++) {
            String kode = kd_diag2[i].getText().trim();
            if (!kode.equals("")) {
                if (chk_diag2[i].isSelected()) {
                    // Kalau checkbox dicentang → anggap primary
                    primary = kode;
                } else {
                    secondary.add(kode);
                }
            }
        }

        // Susun ICD10 (diagnosa) → primary dulu, baru sisanya
        StringBuilder sb = new StringBuilder();
        if (!primary.equals("")) {
            sb.append(primary);
        }
        for (String kode : secondary) {
            if (sb.length() > 0) {
                sb.append("#");
            }
            sb.append(kode);
        }

        icd10INA = sb.toString();
        System.out.println("icd10 INA : " + icd10INA);
    }

    private String getSelectedCode(JComboBox combo, JLabel lbl) {
        String val = combo.getSelectedItem().toString().trim();
        if (combo.getSelectedItem() == null) {
            return "";
        } else if (val.toLowerCase().contains("none")) {
            lbl.setText("0");
        } else if (val.contains(":")) {
            return val.split(":", 2)[0].trim();
        }

        // Jika tidak ada tanda ":" kembalikan apa adanya
        return val;
    }

    private String getCombinedCodes() {
        List<String> codes = new ArrayList<>();

        String proc = getSelectedCode(cmbProc, lbltarifProc);
        String pros = getSelectedCode(cmbPros, lblTarifPros);
        String inv = getSelectedCode(cmbInv, lblTarifInv);
        String drug = getSelectedCode(cmbDrugs, lblTarifDrugs);

        if (!proc.isEmpty()) {
            codes.add(proc);
        }
        if (!pros.isEmpty()) {
            codes.add(pros);
        }
        if (!inv.isEmpty()) {
            codes.add(inv);
        }
        if (!drug.isEmpty()) {
            codes.add(drug);
        }

        return String.join("#", codes);
    }

    private void handleComboChange() {
        String combined = getCombinedCodes();

        if (!combined.isEmpty()) {
            grouper2();
        }
    }

    private void cekDiagnosaUtama(int i) {
//        String cek_primary = Sequel.cariIsi(
//            "SELECT CODE FROM icd_idrg " +
//            "WHERE CODE = '" + kd_diag[i].getText().trim() + "' " +
//            "AND ( (VALIDCODE = '1' AND ACCPDX = 'N' AND ASTERISK = '0') " +
//            "OR (VALIDCODE = '1' AND ACCPDX = 'N' AND ASTERISK = '1') )"
//        );               
//        System.out.println("ini : "+i);
//        System.out.println("SELECT CODE FROM icd_idrg " +
//            "WHERE CODE = '" + kd_diag[i].getText().trim() + "' " +
//            "AND ( (VALIDCODE = '1' AND ACCPDX = 'N' AND ASTERISK = '0') " +
//            "OR (VALIDCODE = '1' AND ACCPDX = 'N' AND ASTERISK = '1') )");
//        if (!cek_primary.trim().equals("")) {
//            JOptionPane.showMessageDialog(this, "DIAGNOSA INI TIDAK BISA DI JADIKAN PRIMARY DI IDRG");
//            chk_diag[i].setSelected(false);
//        }
    }


    // ------------------------------------------------------------------
    // Status klaim di server e-Klaim, pesan error e-Klaim, dan sinkron data pasien
    // (mengacu koleksi Postman E-KLAIM IDRG). Tombol & status di modul ini dihitung dari tabel
    // lokal; bagian ini membaca keadaan sebenarnya di e-Klaim supaya coder tahu bila klaim sudah
    // dikode/final di web e-Klaim atau di komputer lain.
    // ------------------------------------------------------------------
    private boolean fiturEklaimTerpasang = false;
    private final widget.Label lblStatusEklaim = new widget.Label();
    private final widget.Button BtnSinkronPasien = new widget.Button();
    private final widget.Button BtnCekEklaim = new widget.Button();

    private void pasangFiturEklaim() {
        if (fiturEklaimTerpasang) {
            return;
        }
        fiturEklaimTerpasang = true;
        BtnSinkronPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        BtnSinkronPasien.setText("Sinkron Pasien");
        BtnSinkronPasien.setToolTipText("Samakan nomor kartu, nama, tanggal lahir, dan jenis kelamin pasien di e-Klaim dengan data SIMRS (update_patient)");
        BtnSinkronPasien.setPreferredSize(new java.awt.Dimension(140, 30));
        BtnSinkronPasien.addActionListener(e -> sinkronPasienEklaim());
        BtnCekEklaim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnCekEklaim.setText("Cek e-Klaim");
        BtnCekEklaim.setToolTipText("Baca ulang status klaim, hasil grouping, dan coding dari server e-Klaim (get_claim_data)");
        BtnCekEklaim.setPreferredSize(new java.awt.Dimension(120, 30));
        BtnCekEklaim.addActionListener(e -> cekStatusEklaim(false));
        lblStatusEklaim.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        panelBawah.add(BtnSinkronPasien);
        panelBawah.add(BtnCekEklaim);
        panelBawah.add(lblStatusEklaim);
    }

    /**
     * Pesan e-Klaim + petunjuk langkah berdasarkan error_no (daftar dari contoh respons koleksi
     * Postman E-KLAIM IDRG).
     */
    static String pesanEklaim(JSONObject meta) {
        String pesan = meta.optString("message", "-");
        String no = meta.optString("error_no", "");
        String saran;
        switch (no) {
            case "E2004": saran = "Klaim untuk SEP ini belum ada di e-Klaim. Klik Grouping IDRG untuk membuat klaim baru."; break;
            case "E2005":
            case "E2006": saran = "NIK coder belum diisi/terdaftar di e-Klaim. Daftarkan coder di e-Klaim dan di tabel inacbg_coder_nik."; break;
            case "E2007": saran = "Klaim untuk SEP ini sudah ada di e-Klaim. Klik Cek e-Klaim untuk melihat isinya."; break;
            case "E2008": saran = "Pasien belum terdaftar di e-Klaim. Buat klaim baru dulu (Grouping IDRG)."; break;
            case "E2009": saran = "Klaim sudah final. Klik Edit Klaim (re-edit) dulu bila perlu diubah."; break;
            case "E2018": saran = "Klaim belum final. Finalkan klaim dulu sebelum dikirim."; break;
            case "E2043": saran = "Nomor klaim sudah terpakai karena klaim pernah dihapus. Hubungi admin e-Klaim untuk generate nomor klaim baru."; break;
            case "E2044": saran = "Ada parameter yang tidak berlaku untuk method ini."; break;
            case "E2101": saran = "Kode IM tidak berlaku di INACBG. Ganti kode tersebut di tab INACBG."; break;
            case "E2102": saran = "Coding sudah final. Klik Edit iDRG / Edit INACBG (re-edit) dulu bila perlu diubah."; break;
            case "E2103": saran = "iDRG belum final. Klik Final iDRG dulu."; break;
            case "E2104": saran = "INACBG belum final. Klik Final INACBG dulu."; break;
            case "E2105": saran = "iDRG tidak bisa di-group. Periksa diagnosa utama (harus boleh jadi diagnosa utama) dan validitas kode IM."; break;
            case "E2110": saran = "Kode top-up tidak sesuai. Pilih dari daftar top-up yang ditawarkan hasil grouping."; break;
            default: saran = "";
        }
        return pesan + (no.isEmpty() ? "" : " (" + no + ")") + (saran.isEmpty() ? "" : "\n\n" + saran);
    }

    private static String teksEklaim(JSONObject o, String kunci) {
        if (o == null || o.isNull(kunci)) {
            return "";
        }
        String s = o.optString(kunci, "").trim();
        return "-".equals(s) || "#".equals(s) ? "" : s;
    }

    /** Baca get_claim_data; isiOtomatis = isi coding dari e-Klaim bila tabel lokal masih kosong. */
    private void cekStatusEklaim(boolean isiOtomatis) {
        final String sep = Tsep.getText().trim();
        if (sep.isEmpty()) {
            lblStatusEklaim.setText("");
            return;
        }
        lblStatusEklaim.setText("e-Klaim: memeriksa ...");
        new javax.swing.SwingWorker<JSONObject, Void>() {
            private Exception error;

            @Override
            protected JSONObject doInBackground() {
                try {
                    return inacbg.request("{\"metadata\":{\"method\":\"get_claim_data\"},\"data\":{\"nomor_sep\":\"" + sep + "\"}}");
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (!sep.equals(Tsep.getText().trim())) {
                    return;
                }
                JSONObject res = null;
                try {
                    res = get();
                } catch (Exception e) {
                    error = e;
                }
                if (error != null || res == null) {
                    lblStatusEklaim.setText("<html><font color='#cc0000'>e-Klaim tidak terhubung: "
                            + (error == null ? "-" : error.getMessage()) + "</font></html>");
                    return;
                }
                JSONObject meta = res.optJSONObject("metadata");
                if (meta == null || meta.optInt("code", 0) != 200) {
                    String no = meta == null ? "" : meta.optString("error_no", "");
                    lblStatusEklaim.setText("E2004".equals(no) ? "e-Klaim: klaim untuk SEP ini belum ada"
                            : "<html><font color='#cc0000'>e-Klaim: " + (meta == null ? "-" : meta.optString("message", "-")) + "</font></html>");
                    lblStatusEklaim.setToolTipText(meta == null ? null : pesanEklaim(meta));
                    return;
                }
                JSONObject r = res.optJSONObject("response");
                JSONObject d = r == null ? null : r.optJSONObject("data");
                if (d == null) {
                    lblStatusEklaim.setText("e-Klaim: respons tanpa data");
                    return;
                }
                tampilkanStatusEklaim(d, isiOtomatis);
            }
        }.execute();
    }

    private void tampilkanStatusEklaim(JSONObject d, boolean isiOtomatis) {
        String sep = Tsep.getText().trim();
        JSONObject grouper = d.optJSONObject("grouper");
        JSONObject idrg = grouper == null ? null : grouper.optJSONObject("response_idrg");
        JSONObject ina = grouper == null ? null : grouper.optJSONObject("response_inacbg");
        String statusKlaim = teksEklaim(d, "klaim_status_cd");
        String statusIdrg = teksEklaim(idrg, "status_cd");
        String statusIna = teksEklaim(ina, "status_cd");

        StringBuilder sb = new StringBuilder("<html>e-Klaim: <b>").append(statusKlaim.isEmpty() ? "-" : statusKlaim.toUpperCase()).append("</b>");
        if (idrg != null && !teksEklaim(idrg, "drg_code").isEmpty()) {
            sb.append(" &middot; iDRG ").append(teksEklaim(idrg, "drg_code")).append(" (").append(statusIdrg).append(")");
        }
        if (ina != null && ina.optJSONObject("cbg") != null) {
            sb.append(" &middot; INACBG ").append(teksEklaim(ina.optJSONObject("cbg"), "code"))
              .append(" Rp ").append(Valid.SetAngka(ina.optDouble("tariff", 0))).append(" (").append(statusIna).append(")");
        }
        sb.append(" &middot; Kemenkes: ").append(teksEklaim(d, "kemenkes_dc_status_cd"));

        // Keadaan di e-Klaim yang belum tercatat di SIMRS
        java.util.List<String> beda = new java.util.ArrayList<>();
        if ("final".equals(statusIdrg) && Sequel.cariIsi("select no_sep from inacbg_idrg_final where no_sep=?", sep).isEmpty()) {
            beda.add("iDRG sudah final di e-Klaim");
        }
        if ("final".equals(statusIna) && Sequel.cariIsi("select no_sep from inacbg_final where no_sep=?", sep).isEmpty()) {
            beda.add("INACBG sudah final di e-Klaim");
        }
        if ("final".equals(statusKlaim) && Sequel.cariIsi("select no_sep from inacbg_grouping_final where no_sep=?", sep).isEmpty()) {
            beda.add("klaim sudah final di e-Klaim");
        }
        String tglEklaim = teksEklaim(d, "tgl_masuk");
        String tglForm = String.valueOf(tgl_masuk.getSelectedItem());
        if (tglEklaim.matches("\\d{2}/\\d{2}/\\d{4}") && tglForm.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String iso = tglEklaim.substring(6) + "-" + tglEklaim.substring(3, 5) + "-" + tglEklaim.substring(0, 2);
            if (!iso.equals(tglForm)) {
                beda.add("tanggal masuk di e-Klaim " + tglEklaim + " (" + teksEklaim(d, "tgl_pulang") + "), bukan kunjungan ini");
            }
        }
        String namaEklaim = teksEklaim(d, "nama_pasien");
        // Nama SEP/e-Klaim sering lebih pendek ("KARYATI" vs "KARYATI BINTI MAMAT"); yang ditandai hanya nama yang benar-benar beda.
        String namaSimrs = TPasien.getText().trim().toUpperCase();
        if (!namaEklaim.isEmpty() && !namaSimrs.contains(namaEklaim.toUpperCase()) && !namaEklaim.toUpperCase().contains(namaSimrs)) {
            beda.add("nama di e-Klaim \"" + namaEklaim + "\" (klik Sinkron Pasien)");
        }
        if (!beda.isEmpty()) {
            sb.append("<br><font color='#cc0000'>Berbeda dengan SIMRS: ").append(String.join("; ", beda)).append("</font>");
        }

        if (isiOtomatis) {
            boolean diisi = isiCodingDariEklaim(d);
            if (idrg != null && !teksEklaim(idrg, "drg_code").isEmpty()
                    && Sequel.cariIsi("select no_sep from idrg_grouper where no_sep=?", sep).isEmpty()) {
                responIDRG.setText("<html>HASIL DARI e-KLAIM (" + statusIdrg + ")<br><br>"
                        + "MDC : " + teksEklaim(idrg, "mdc_description") + " (" + teksEklaim(idrg, "mdc_number") + ")<br><br>"
                        + "DRG : " + teksEklaim(idrg, "drg_description") + " (" + teksEklaim(idrg, "drg_code") + ")<br><br>"
                        + "TARIF : Rp " + Valid.SetAngka(idrg.optDouble("total_tarif", 0)) + "</html>");
            }
            if (diisi) {
                sb.append("<br>Coding diambil dari e-Klaim karena belum tersimpan di SIMRS.");
            }
        }
        lblStatusEklaim.setText(sb.append("</html>").toString());
        lblStatusEklaim.setToolTipText("Coder e-Klaim: " + teksEklaim(d, "coder_nm") + ", payor: " + teksEklaim(d, "payor_nm")
                + ", BPJS: " + teksEklaim(d, "bpjs_dc_status_cd"));
    }

    /**
     * Isi kolom diagnosa/prosedur iDRG dan INACBG dari string e-Klaim ("A91.0#E11.9",
     * prosedur "86.28+2" = kode + multiplicity) bila tabel lokal untuk SEP ini masih kosong.
     */
    private boolean isiCodingDariEklaim(JSONObject d) {
        String sep = Tsep.getText().trim();
        boolean diisi = false;
        if (Sequel.cariInteger("select count(*) from idrg_diagnosa where no_sep=?", sep) == 0) {
            diisi |= isiKode(teksEklaim(d, "diagnosa_inagrouper"), kd_diag, nm_diag, null, chk_diag, "icd_idrg", "ICD_10_2010_IM");
        }
        if (Sequel.cariInteger("select count(*) from idrg_prosedur where no_sep=?", sep) == 0) {
            diisi |= isiKode(teksEklaim(d, "procedure_inagrouper"), kd_psdr, nm_psdr, multiple, null, "icd_idrg", "ICD_9CM_2010_IM");
        }
        if (Sequel.cariInteger("select count(*) from inacbg_diagnosa where no_sep=?", sep) == 0) {
            diisi |= isiKode(teksEklaim(d, "diagnosa"), kd_diag2, nm_diag2, null, chk_diag2, "icd_inacbg", "ICD_10_2010");
        }
        if (Sequel.cariInteger("select count(*) from inacbg_prosedur where no_sep=?", sep) == 0) {
            diisi |= isiKode(teksEklaim(d, "procedure"), kd_psdr2, nm_psdr2, null, null, "icd_inacbg", "ICD_9CM_2010");
        }
        return diisi;
    }

    private boolean isiKode(String daftar, JTextField[] kd, JTextField[] nm, JTextField[] jumlah, JCheckBox[] utama, String tabel, String sistem) {
        if (daftar.isEmpty()) {
            return false;
        }
        String[] item = daftar.split("#");
        for (int k = 0; k < kd.length; k++) {
            String kode = "", kali = "1";
            if (k < item.length && !item[k].trim().isEmpty()) {
                String[] p = item[k].trim().split("\\+");
                kode = p[0].trim();
                kali = p.length > 1 ? p[1].trim() : "1";
            }
            kd[k].setText(kode);
            nm[k].setText(kode.isEmpty() ? "" : Sequel.cariIsi("select DESCRIPTION from " + tabel + " where CODE=? and SYSTEM='" + sistem + "' limit 1", kode));
            if (jumlah != null) {
                jumlah[k].setText(kode.isEmpty() ? "" : kali);
            }
            if (utama != null) {
                utama[k].setSelected(k == 0 && !kode.isEmpty());
            }
        }
        return true;
    }

    private void sinkronPasienEklaim() {
        String norm = TNoRM.getText().trim();
        if (norm.isEmpty() || no_kartu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor rekam medis / nomor kartu belum terisi.");
            return;
        }
        String nama = Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", norm);
        String lahir = Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", norm);
        String jk = Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", norm);
        if (nama.isEmpty()) {
            nama = TPasien.getText().trim();
        }
        if (lahir.isEmpty()) {
            lahir = tgl_lahir.getText().trim();
        }
        if (jk.isEmpty()) {
            jk = jenis_kelamin.getText().trim();
        }
        int ya = JOptionPane.showConfirmDialog(this,
                "Kirim data pasien dari SIMRS ke e-Klaim (update_patient)?\n\n"
                + "No. RM        : " + norm + "\n"
                + "No. Kartu     : " + no_kartu.getText().trim() + "\n"
                + "Nama          : " + nama + "\n"
                + "Tgl. Lahir    : " + lahir + "\n"
                + "Jenis Kelamin : " + ("L".equals(jk) ? "Laki-laki" : "Perempuan"),
                "Sinkron Data Pasien e-Klaim", JOptionPane.YES_NO_OPTION);
        if (ya != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            JSONObject req = new JSONObject();
            req.put("metadata", new JSONObject().put("method", "update_patient").put("nomor_rm", norm));
            req.put("data", new JSONObject()
                    .put("nomor_kartu", no_kartu.getText().trim())
                    .put("nomor_rm", norm)
                    .put("nama_pasien", nama)
                    .put("tgl_lahir", lahir + " 00:00:00")
                    .put("gender", "L".equals(jk) ? "1" : "2"));
            JSONObject res = inacbg.request(req.toString());
            JSONObject meta = res.optJSONObject("metadata");
            if (meta != null && meta.optInt("code", 0) == 200) {
                TPasien.setText(nama);
                JOptionPane.showMessageDialog(this, "Data pasien di e-Klaim sudah disamakan dengan SIMRS.");
            } else {
                JOptionPane.showMessageDialog(this, "RESPON : " + (meta == null ? res.toString() : pesanEklaim(meta)));
            }
            cekStatusEklaim(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghubungi e-Klaim: " + e.getMessage());
        }
    }

}
