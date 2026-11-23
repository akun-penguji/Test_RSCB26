/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMWongBakerScale extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    /** Creates new form RMWongBakerScale
     * @param parent
     * @param modal */
    public RMWongBakerScale(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(880, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Pengkajian",
            "Skala", "Deskripsi", "Interpretasi", "Lokasi", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 10; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) { column.setPreferredWidth(110); }
            else if (i == 1) { column.setPreferredWidth(70); }
            else if (i == 2) { column.setPreferredWidth(160); }
            else if (i == 3) { column.setPreferredWidth(115); }
            else if (i == 4) { column.setPreferredWidth(40); }
            else if (i == 5) { column.setPreferredWidth(180); }
            else if (i == 6) { column.setPreferredWidth(150); }
            else if (i == 7) { column.setPreferredWidth(150); }
            else if (i == 8) { column.setPreferredWidth(85); }
            else if (i == 9) { column.setPreferredWidth(150); }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
        LokasiNyeri.setDocument(new batasInput((int) 100).getKata(LokasiNyeri));
        DurasiNyeri.setDocument(new batasInput((int) 50).getKata(DurasiNyeri));
        KualitasNyeri.setDocument(new batasInput((int) 100).getKata(KualitasNyeri));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { tampil(); } }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { tampil(); } }
                @Override public void changedUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { tampil(); } }
            });
        }

        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
                KdPetugas.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        ChkInput.setSelected(false);
        isForm(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TglPengkajian = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel18 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        BtnPetugas = new widget.Button();
        jLabel1 = new widget.Label();
        SkalaNyeri = new widget.ComboBox();
        jLabel2 = new widget.Label();
        TInterpretasi = new widget.TextBox();
        jLabel3 = new widget.Label();
        LokasiNyeri = new widget.TextBox();
        jLabel5 = new widget.Label();
        DurasiNyeri = new widget.TextBox();
        jLabel8 = new widget.Label();
        KualitasNyeri = new widget.TextBox();
        jLabel9 = new widget.Label();
        TDeskripsi = new widget.TextBox();
        LabelSkor = new widget.Label();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Formulir Penilaian Nyeri Wong-Baker Faces Scale ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat");
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tbObatMouseClicked(evt); }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { tbObatKeyPressed(evt); }
        });
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3");
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnSimpanActionPerformed(evt); }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnSimpanActionPerformed(null);
                else Valid.pindah(evt, KualitasNyeri, BtnBatal);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnBatalActionPerformed(evt); }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnBatalActionPerformed(null);
                else Valid.pindah(evt, BtnSimpan, BtnHapus);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnHapusActionPerformed(evt); }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnHapusActionPerformed(null);
                else Valid.pindah(evt, BtnBatal, BtnEdit);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnEditActionPerformed(evt); }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnEditActionPerformed(null);
                else Valid.pindah(evt, BtnHapus, BtnKeluar);
            }
        });
        panelGlass8.add(BtnEdit);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0"); LCount.setName("LCount"); LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { petugas.dispose(); dispose(); }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) { petugas.dispose(); dispose(); }
                else Valid.pindah(evt, BtnEdit, TCari);
            }
        });
        panelGlass8.add(BtnKeluar);
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :"); jLabel19.setName("jLabel19"); jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"02-10-2023"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false); DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d."); jLabel21.setName("jLabel21"); jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"02-10-2023"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false); DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :"); jLabel6.setName("jLabel6"); jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil();
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('3'); BtnCari.setToolTipText("Alt+3"); BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { tampil(); }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M'); BtnAll.setToolTipText("Alt+M"); BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { TCari.setText(""); tampil(); }
        });
        panelGlass9.add(BtnAll);
        jPanel3.add(panelGlass9, java.awt.BorderLayout.SOUTH);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        PanelInput.setName("PanelInput"); PanelInput.setOpaque(false);
        PanelInput.setLayout(new java.awt.BorderLayout());

        ChkInput.setText("Input Data"); ChkInput.setName("ChkInput");
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { isForm(ChkInput.isSelected()); }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.NORTH);

        scrollInput.setName("scrollInput");
        FormInput = new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new java.awt.Dimension(850, 320));

        jLabel4.setText("No. Rawat :"); jLabel4.setName("jLabel4");
        FormInput.add(jLabel4); jLabel4.setBounds(14, 10, 80, 23);

        TNoRw.setName("TNoRw");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) { isRawat(); isPasien(); }
                else Valid.pindah(evt, TCari, TglPengkajian);
            }
        });
        FormInput.add(TNoRw); TNoRw.setBounds(100, 10, 130, 23);

        TNoRM.setEditable(false); TNoRM.setName("TNoRM");
        FormInput.add(TNoRM); TNoRM.setBounds(240, 10, 100, 23);

        TPasien.setEditable(false); TPasien.setName("TPasien");
        FormInput.add(TPasien); TPasien.setBounds(350, 10, 250, 23);

        new widget.Label() {{ setText("Tgl. Pengkajian :"); setBounds(14, 40, 100, 23); }};
        jLabel18 = new widget.Label(); jLabel18.setText("Tgl. Pengkajian :"); jLabel18.setName("jLabel18");
        FormInput.add(jLabel18); jLabel18.setBounds(14, 40, 100, 23);

        TglPengkajian.setForeground(new java.awt.Color(50, 70, 50));
        TglPengkajian.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"02-10-2023"}));
        TglPengkajian.setDisplayFormat("dd-MM-yyyy"); TglPengkajian.setName("TglPengkajian");
        TglPengkajian.setOpaque(false); TglPengkajian.setPreferredSize(new java.awt.Dimension(95, 23));
        FormInput.add(TglPengkajian); TglPengkajian.setBounds(120, 40, 100, 23);

        jLabel1 = new widget.Label(); jLabel1.setText("Petugas :"); jLabel1.setName("jLabel1");
        FormInput.add(jLabel1); jLabel1.setBounds(240, 40, 60, 23);

        KdPetugas.setName("KdPetugas");
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?", NmPetugas, KdPetugas.getText());
                } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
                    btnPetugasActionPerformed();
                }
            }
        });
        FormInput.add(KdPetugas); KdPetugas.setBounds(310, 40, 100, 23);

        NmPetugas.setEditable(false); NmPetugas.setName("NmPetugas");
        FormInput.add(NmPetugas); NmPetugas.setBounds(420, 40, 200, 23);

        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnPetugas.setName("BtnPetugas"); BtnPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { btnPetugasActionPerformed(); }
        });
        FormInput.add(BtnPetugas); BtnPetugas.setBounds(630, 40, 28, 23);

        jSeparator1 = new javax.swing.JSeparator();
        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        FormInput.add(jSeparator1); jSeparator1.setBounds(0, 70, 850, 1);

        jLabel2 = new widget.Label(); jLabel2.setText("Skala Nyeri :"); jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(jLabel2); jLabel2.setBounds(14, 80, 100, 23);

        SkalaNyeri.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0 - Tidak Nyeri",
            "1 - Sangat sedikit nyeri",
            "2 - Sedikit lebih nyeri",
            "3 - Lebih nyeri lagi",
            "4 - Cukup nyeri",
            "5 - Sedikit lebih banyak nyeri",
            "6 - Cukup banyak nyeri",
            "7 - Nyeri yang cukup kuat",
            "8 - Lebih nyeri lagi",
            "9 - Nyeri yang sangat kuat",
            "10 - Nyeri tidak tertahankan"
        }));
        SkalaNyeri.setName("SkalaNyeri");
        SkalaNyeri.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) { hitungSkor(); }
        });
        FormInput.add(SkalaNyeri); SkalaNyeri.setBounds(120, 80, 300, 23);

        jLabel3 = new widget.Label(); jLabel3.setText("Interpretasi :"); jLabel3.setName("jLabel3");
        FormInput.add(jLabel3); jLabel3.setBounds(440, 80, 80, 23);

        TInterpretasi.setEditable(false); TInterpretasi.setName("TInterpretasi");
        FormInput.add(TInterpretasi); TInterpretasi.setBounds(530, 80, 250, 23);

        LabelSkor = new widget.Label(); LabelSkor.setText("Skor : 0 | Tidak Nyeri"); LabelSkor.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(LabelSkor); LabelSkor.setBounds(14, 110, 500, 23);

        jLabel9 = new widget.Label(); jLabel9.setText("Deskripsi :"); jLabel9.setName("jLabel9");
        FormInput.add(jLabel9); jLabel9.setBounds(14, 140, 80, 23);

        TDeskripsi.setEditable(false); TDeskripsi.setName("TDeskripsi");
        FormInput.add(TDeskripsi); TDeskripsi.setBounds(100, 140, 680, 23);

        jSeparator2 = new javax.swing.JSeparator();
        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        FormInput.add(jSeparator2); jSeparator2.setBounds(0, 170, 850, 1);

        jLabel5 = new widget.Label(); jLabel5.setText("Lokasi Nyeri :"); jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(jLabel5); jLabel5.setBounds(14, 180, 100, 23);

        LokasiNyeri.setName("LokasiNyeri");
        FormInput.add(LokasiNyeri); LokasiNyeri.setBounds(120, 180, 660, 23);

        jLabel8 = new widget.Label(); jLabel8.setText("Durasi Nyeri :"); jLabel8.setName("jLabel8");
        FormInput.add(jLabel8); jLabel8.setBounds(14, 210, 100, 23);

        DurasiNyeri.setName("DurasiNyeri");
        FormInput.add(DurasiNyeri); DurasiNyeri.setBounds(120, 210, 660, 23);

        jLabel9b = new widget.Label(); jLabel9b.setText("Kualitas Nyeri :"); jLabel9b.setName("jLabel9b");
        FormInput.add(jLabel9b); jLabel9b.setBounds(14, 240, 100, 23);

        KualitasNyeri.setName("KualitasNyeri");
        KualitasNyeri.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Valid.pindah(evt, DurasiNyeri, BtnSimpan);
            }
        });
        FormInput.add(KualitasNyeri); KualitasNyeri.setBounds(120, 240, 660, 23);

        scrollInput.setViewportView(FormInput);
        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);
        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try { getData(); } catch (java.lang.NullPointerException e) {}
        }
    }

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try { getData(); } catch (java.lang.NullPointerException e) {}
            }
        }
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            int skala = SkalaNyeri.getSelectedIndex();
            String tgl = Valid.SetTgl(TglPengkajian.getSelectedItem() + "");
            if (Sequel.menyimpantf("skrining_nyeri_wongbaker",
                "?,?,?,?,?,?,?,?,?,?,?", "Data", 11,
                new String[]{
                    TNoRw.getText(), TNoRM.getText(), tgl,
                    "" + skala, TDeskripsi.getText(), TInterpretasi.getText(),
                    LokasiNyeri.getText(), DurasiNyeri.getText(), KualitasNyeri.getText(),
                    KdPetugas.getText(), NmPetugas.getText()
                }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
        ChkInput.setSelected(true);
        isForm(true);
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }

    private void btnPetugasActionPerformed() {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMWongBakerScale dialog = new RMWongBakerScale(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPetugas;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox DurasiNyeri;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdPetugas;
    private widget.TextBox KualitasNyeri;
    private widget.Label LCount;
    private widget.Label LabelSkor;
    private widget.TextBox LokasiNyeri;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.ComboBox SkalaNyeri;
    private widget.TextBox TCari;
    private widget.TextBox TDeskripsi;
    private widget.TextBox TInterpretasi;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TglPengkajian;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel2;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabel9b;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String kw = "%" + TCari.getText() + "%";
            String d1 = Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00";
            String d2 = Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                    "select s.no_rawat, s.no_rm, p.nm_pasien, s.tgl_pengkajian, s.skala_nyeri, " +
                    "s.deskripsi_nyeri, s.interpretasi, s.lokasi_nyeri, s.id_petugas, s.nm_petugas " +
                    "from skrining_nyeri_wongbaker s " +
                    "inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where s.tgl_pengkajian between ? and ? order by s.tgl_pengkajian desc");
                ps.setString(1, d1); ps.setString(2, d2);
            } else {
                ps = koneksi.prepareStatement(
                    "select s.no_rawat, s.no_rm, p.nm_pasien, s.tgl_pengkajian, s.skala_nyeri, " +
                    "s.deskripsi_nyeri, s.interpretasi, s.lokasi_nyeri, s.id_petugas, s.nm_petugas " +
                    "from skrining_nyeri_wongbaker s " +
                    "inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where s.tgl_pengkajian between ? and ? and " +
                    "(s.no_rawat like ? or s.no_rm like ? or p.nm_pasien like ? or s.id_petugas like ? or s.nm_petugas like ?) " +
                    "order by s.tgl_pengkajian desc");
                ps.setString(1, d1); ps.setString(2, d2);
                ps.setString(3, kw); ps.setString(4, kw); ps.setString(5, kw);
                ps.setString(6, kw); ps.setString(7, kw);
            }
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rm"), rs.getString("nm_pasien"),
                        rs.getString("tgl_pengkajian"), rs.getString("skala_nyeri"),
                        rs.getString("deskripsi_nyeri"), rs.getString("interpretasi"),
                        rs.getString("lokasi_nyeri"), rs.getString("id_petugas"), rs.getString("nm_petugas")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (SQLException e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        TglPengkajian.setDate(new Date());
        SkalaNyeri.setSelectedIndex(0);
        TInterpretasi.setText("Tidak Nyeri");
        TDeskripsi.setText("0 - Tidak Nyeri");
        LabelSkor.setText("Skor : 0 | Tidak Nyeri");
        LokasiNyeri.setText("");
        DurasiNyeri.setText("");
        KualitasNyeri.setText("");
        SkalaNyeri.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            Valid.SetTgl(TglPengkajian, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            int skala = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            SkalaNyeri.setSelectedIndex(skala);
            TDeskripsi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            TInterpretasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            LokasiNyeri.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            KdPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", TNoRM, TNoRw.getText());
    }

    private void isPasien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", TPasien, TNoRM.getText());
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat='" + norwt + "'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPasien();
        ChkInput.setSelected(true);
        isForm(true);
        tampil();
    }

    public void isForm(boolean show) {
        if (show) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 286));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        if (!akses.getstatus()) {
            JOptionPane.showMessageDialog(null, "Maaf, anda tidak mempunyai akses ke form ini....!!!!");
            dispose();
        }
        BtnSimpan.setEnabled(akses.gettindakan_ranap());
        BtnHapus.setEnabled(akses.gettindakan_ranap());
        BtnEdit.setEnabled(akses.gettindakan_ranap());
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false);
            BtnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?", NmPetugas, KdPetugas.getText());
            if (NmPetugas.getText().equals("")) {
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
        tampil();
    }

    private void hitungSkor() {
        int skala = SkalaNyeri.getSelectedIndex();
        String[] desc = {
            "0 - Tidak Nyeri", "1 - Sangat sedikit nyeri", "2 - Sedikit lebih nyeri",
            "3 - Lebih nyeri lagi", "4 - Cukup nyeri", "5 - Sedikit lebih banyak nyeri",
            "6 - Cukup banyak nyeri", "7 - Nyeri yang cukup kuat", "8 - Lebih nyeri lagi",
            "9 - Nyeri yang sangat kuat", "10 - Nyeri tidak tertahankan"
        };
        TDeskripsi.setText(desc[skala]);
        String interpretasi;
        if (skala == 0) {
            interpretasi = "Tidak Nyeri";
        } else if (skala <= 3) {
            interpretasi = "Nyeri Ringan";
        } else if (skala <= 6) {
            interpretasi = "Nyeri Sedang";
        } else if (skala <= 9) {
            interpretasi = "Nyeri Berat";
        } else {
            interpretasi = "Nyeri Tidak Tertahankan";
        }
        TInterpretasi.setText(interpretasi);
        LabelSkor.setText("Skor : " + skala + " | " + interpretasi);
    }

    private void ganti() {
        int skala = SkalaNyeri.getSelectedIndex();
        String tgl = Valid.SetTgl(TglPengkajian.getSelectedItem() + "");
        Sequel.mengedit("skrining_nyeri_wongbaker",
            "no_rawat=? and tgl_pengkajian=?",
            "no_rawat=?,no_rm=?,tgl_pengkajian=?,skala_nyeri=?,deskripsi_nyeri=?,interpretasi=?,lokasi_nyeri=?,durasi_nyeri=?,kualitas_nyeri=?,id_petugas=?,nm_petugas=?",
            13, new String[]{
                TNoRw.getText(), TNoRM.getText(), tgl,
                "" + skala, TDeskripsi.getText(), TInterpretasi.getText(),
                LokasiNyeri.getText(), DurasiNyeri.getText(), KualitasNyeri.getText(),
                KdPetugas.getText(), NmPetugas.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
            });
        if (tabMode.getRowCount() != 0) { tampil(); }
        emptTeks();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from skrining_nyeri_wongbaker where no_rawat=? and tgl_pengkajian=?", 2, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
        }) == true) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }
}
