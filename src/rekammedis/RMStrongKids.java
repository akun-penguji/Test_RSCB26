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
public final class RMStrongKids extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    /** Creates new form RMStrongKids
     * @param parent
     * @param modal */
    public RMStrongKids(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(880, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Pengkajian",
            "Klinis", "Penyakit", "Asupan", "BB", "Total", "Risiko",
            "Rekomendasi", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 13; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) { column.setPreferredWidth(110); }
            else if (i == 1) { column.setPreferredWidth(70); }
            else if (i == 2) { column.setPreferredWidth(160); }
            else if (i == 3) { column.setPreferredWidth(115); }
            else if (i >= 4 && i <= 7) { column.setPreferredWidth(40); }
            else if (i == 8) { column.setPreferredWidth(40); }
            else if (i == 9) { column.setPreferredWidth(90); }
            else if (i == 10) { column.setPreferredWidth(250); }
            else if (i == 11) { column.setPreferredWidth(85); }
            else if (i == 12) { column.setPreferredWidth(150); }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
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

        // Strong Kids items
        CBKlinis = new widget.ComboBox();
        CBPenyakit = new widget.ComboBox();
        CBAsupan = new widget.ComboBox();
        CBBB = new widget.ComboBox();
        TTotalSkor = new widget.TextBox();
        LabelTotal = new widget.Label();
        LabelRisiko = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        TRekomendasi = new widget.TextArea();
        scrollPane2 = new widget.ScrollPane();
        TCatatan = new widget.TextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Skrining Gizi Modifikasi Strong Kids ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); Scroll.setOpaque(true);
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

        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false);
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
                else Valid.pindah(evt, TCatatan, BtnBatal);
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

        jLabel7.setText("Record :"); jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0"); LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
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

        jLabel19.setText("Tanggal :"); jLabel19.setName("jLabel19");
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"02-10-2023"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false); DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d."); jLabel21.setName("jLabel21");
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"02-10-2023"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false); DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :"); jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
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
        FormInput.setPreferredSize(new java.awt.Dimension(850, 480));

        // --- Header ---
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

        jLabel18.setText("Tgl. Pengkajian :"); jLabel18.setName("jLabel18");
        FormInput.add(jLabel18); jLabel18.setBounds(14, 40, 105, 23);

        TglPengkajian.setForeground(new java.awt.Color(50, 70, 50));
        TglPengkajian.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"02-10-2023"}));
        TglPengkajian.setDisplayFormat("dd-MM-yyyy"); TglPengkajian.setName("TglPengkajian");
        TglPengkajian.setOpaque(false); TglPengkajian.setPreferredSize(new java.awt.Dimension(95, 23));
        FormInput.add(TglPengkajian); TglPengkajian.setBounds(125, 40, 100, 23);

        widget.Label lblPetugas = new widget.Label(); lblPetugas.setText("Petugas :"); lblPetugas.setName("lblPetugas");
        FormInput.add(lblPetugas); lblPetugas.setBounds(240, 40, 60, 23);

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

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        FormInput.add(jSeparator1); jSeparator1.setBounds(0, 70, 850, 1);

        // --- Strong Kids Items ---
        widget.Label lbl1 = new widget.Label(); lbl1.setText("1. Kondisi Subjektif Klinis :"); lbl1.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(lbl1); lbl1.setBounds(14, 80, 200, 23);

        CBKlinis.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Tidak (0)",
            "Ya - tampak kurus/malnutrisi berat/cachexia (1)"
        }));
        CBKlinis.setName("CBKlinis");
        CBKlinis.addItemListener(e -> hitungSkor());
        FormInput.add(CBKlinis); CBKlinis.setBounds(220, 80, 580, 23);

        widget.Label lbl2 = new widget.Label(); lbl2.setText("2. Penyakit Risiko Tinggi :"); lbl2.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(lbl2); lbl2.setBounds(14, 115, 200, 23);

        CBPenyakit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Tidak (0)",
            "Ya - diare \u22655x/hari, muntah >3x/hari, asupan berkurang, intervensi gizi sebelumnya, penyakit kronis (2)"
        }));
        CBPenyakit.setName("CBPenyakit");
        CBPenyakit.addItemListener(e -> hitungSkor());
        FormInput.add(CBPenyakit); CBPenyakit.setBounds(220, 115, 580, 23);

        widget.Label lbl3 = new widget.Label(); lbl3.setText("3. Asupan Nutrisi (seminggu terakhir) :"); lbl3.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(lbl3); lbl3.setBounds(14, 150, 260, 23);

        CBAsupan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Tidak ada penurunan (0)",
            "Ya - ada penurunan asupan (1)"
        }));
        CBAsupan.setName("CBAsupan");
        CBAsupan.addItemListener(e -> hitungSkor());
        FormInput.add(CBAsupan); CBAsupan.setBounds(280, 150, 520, 23);

        widget.Label lbl4 = new widget.Label(); lbl4.setText("4. Kehilangan BB / Kenaikan BB Kurang :"); lbl4.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(lbl4); lbl4.setBounds(14, 185, 275, 23);

        CBBB.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Tidak (0)",
            "Ya - selama beberapa minggu/bulan atau kenaikan berat badan kurang (1)"
        }));
        CBBB.setName("CBBB");
        CBBB.addItemListener(e -> hitungSkor());
        FormInput.add(CBBB); CBBB.setBounds(295, 185, 505, 23);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        FormInput.add(jSeparator2); jSeparator2.setBounds(0, 220, 850, 1);

        LabelTotal.setText("Total Skor : 0"); LabelTotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        LabelTotal.setName("LabelTotal");
        FormInput.add(LabelTotal); LabelTotal.setBounds(14, 228, 200, 23);

        TTotalSkor.setEditable(false); TTotalSkor.setName("TTotalSkor");
        FormInput.add(TTotalSkor); TTotalSkor.setBounds(220, 228, 50, 23);

        LabelRisiko.setText("Kategori Risiko : -"); LabelRisiko.setFont(new java.awt.Font("Tahoma", 1, 11));
        LabelRisiko.setName("LabelRisiko");
        FormInput.add(LabelRisiko); LabelRisiko.setBounds(290, 228, 500, 23);

        widget.Label lbl5 = new widget.Label(); lbl5.setText("Rekomendasi :"); lbl5.setFont(new java.awt.Font("Tahoma", 1, 11));
        FormInput.add(lbl5); lbl5.setBounds(14, 260, 100, 23);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1");

        TRekomendasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TRekomendasi.setColumns(20); TRekomendasi.setRows(3);
        TRekomendasi.setName("TRekomendasi");
        TRekomendasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Valid.pindah2(evt, CBBB, TCatatan);
            }
        });
        scrollPane1.setViewportView(TRekomendasi);
        FormInput.add(scrollPane1); scrollPane1.setBounds(120, 260, 680, 55);

        jSeparator3.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator3.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        FormInput.add(jSeparator3); jSeparator3.setBounds(0, 325, 850, 1);

        widget.Label lbl6 = new widget.Label(); lbl6.setText("Catatan :"); lbl6.setName("lbl6");
        FormInput.add(lbl6); lbl6.setBounds(14, 333, 100, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2");

        TCatatan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TCatatan.setColumns(20); TCatatan.setRows(3);
        TCatatan.setName("TCatatan");
        TCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Valid.pindah2(evt, TRekomendasi, BtnSimpan);
            }
        });
        scrollPane2.setViewportView(TCatatan);
        FormInput.add(scrollPane2); scrollPane2.setBounds(120, 333, 680, 55);

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

    private int skorDariCB(widget.ComboBox cb) {
        String item = cb.getSelectedItem().toString();
        try {
            int start = item.lastIndexOf('(') + 1;
            int end = item.lastIndexOf(')');
            if (start > 0 && end > start) {
                return Integer.parseInt(item.substring(start, end).trim());
            }
        } catch (NumberFormatException ex) {}
        return 0;
    }

    private void hitungSkor() {
        int total = skorDariCB(CBKlinis) + skorDariCB(CBPenyakit) + skorDariCB(CBAsupan) + skorDariCB(CBBB);
        TTotalSkor.setText("" + total);
        String risiko;
        String rekomendasi;
        if (total == 0) {
            risiko = "Risiko Rendah";
            rekomendasi = "Pemantauan berat badan rutin. Evaluasi ulang dalam 1 minggu.";
        } else if (total <= 3) {
            risiko = "Risiko Sedang";
            rekomendasi = "Konsultasikan ke dokter. Lakukan pengkajian nutrisi lengkap oleh dietisien.";
        } else {
            risiko = "Risiko Tinggi";
            rekomendasi = "Segera konsultasi ke dokter dan ahli gizi. Mulai intervensi gizi intensif.";
        }
        LabelTotal.setText("Total Skor : " + total);
        LabelRisiko.setText("Kategori Risiko : " + risiko);
        TRekomendasi.setText(rekomendasi);
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            int total = Integer.parseInt(TTotalSkor.getText().isEmpty() ? "0" : TTotalSkor.getText());
            String risiko = LabelRisiko.getText().replace("Kategori Risiko : ", "");
            String tgl = Valid.SetTgl(TglPengkajian.getSelectedItem() + "");
            if (Sequel.menyimpantf("strong_kids",
                "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13,
                new String[]{
                    TNoRw.getText(), TNoRM.getText(), tgl,
                    "" + skorDariCB(CBKlinis), "" + skorDariCB(CBPenyakit),
                    "" + skorDariCB(CBAsupan), "" + skorDariCB(CBBB),
                    "" + total, risiko, TRekomendasi.getText(),
                    TCatatan.getText(), KdPetugas.getText(), NmPetugas.getText()
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
                if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString())) {
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
                if (akses.getkode().equals("Admin Utama")) { ganti(); }
                else {
                    if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString())) { ganti(); }
                    else { JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!"); }
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
            RMStrongKids dialog = new RMStrongKids(new javax.swing.JFrame(), true);
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
    private widget.ComboBox CBBB;
    private widget.ComboBox CBAsupan;
    private widget.ComboBox CBKlinis;
    private widget.ComboBox CBPenyakit;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdPetugas;
    private widget.Label LCount;
    private widget.Label LabelRisiko;
    private widget.Label LabelTotal;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextArea TCatatan;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextArea TRekomendasi;
    private widget.TextBox TTotalSkor;
    private widget.Tanggal TglPengkajian;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
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
                    "select s.no_rawat, s.no_rm, p.nm_pasien, s.tgl_pengkajian, " +
                    "s.kondisi_subjektif_klinis, s.penyakit_risiko_tinggi, s.asupan_nutrisi, " +
                    "s.kehilangan_berat_badan, s.total_skor, s.risiko, s.rekomendasi, " +
                    "s.id_petugas, s.nm_petugas " +
                    "from strong_kids s " +
                    "inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "where s.tgl_pengkajian between ? and ? order by s.tgl_pengkajian desc");
                ps.setString(1, d1); ps.setString(2, d2);
            } else {
                ps = koneksi.prepareStatement(
                    "select s.no_rawat, s.no_rm, p.nm_pasien, s.tgl_pengkajian, " +
                    "s.kondisi_subjektif_klinis, s.penyakit_risiko_tinggi, s.asupan_nutrisi, " +
                    "s.kehilangan_berat_badan, s.total_skor, s.risiko, s.rekomendasi, " +
                    "s.id_petugas, s.nm_petugas " +
                    "from strong_kids s " +
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
                        rs.getString("tgl_pengkajian"),
                        rs.getString("kondisi_subjektif_klinis"), rs.getString("penyakit_risiko_tinggi"),
                        rs.getString("asupan_nutrisi"), rs.getString("kehilangan_berat_badan"),
                        rs.getString("total_skor"), rs.getString("risiko"), rs.getString("rekomendasi"),
                        rs.getString("id_petugas"), rs.getString("nm_petugas")
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
        CBKlinis.setSelectedIndex(0);
        CBPenyakit.setSelectedIndex(0);
        CBAsupan.setSelectedIndex(0);
        CBBB.setSelectedIndex(0);
        TTotalSkor.setText("0");
        LabelTotal.setText("Total Skor : 0");
        LabelRisiko.setText("Kategori Risiko : Risiko Rendah");
        TRekomendasi.setText("Pemantauan berat badan rutin. Evaluasi ulang dalam 1 minggu.");
        TCatatan.setText("");
        CBKlinis.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            Valid.SetTgl(TglPengkajian, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            setCBByScore(CBKlinis,   intVal(tbObat, 4),  new int[]{0,1});
            setCBByScore(CBPenyakit, intVal(tbObat, 5),  new int[]{0,2});
            setCBByScore(CBAsupan,   intVal(tbObat, 6),  new int[]{0,1});
            setCBByScore(CBBB,       intVal(tbObat, 7),  new int[]{0,1});
            KdPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            hitungSkor();
        }
    }

    private int intVal(widget.Table t, int col) {
        try { return Integer.parseInt(t.getValueAt(t.getSelectedRow(), col).toString()); }
        catch (Exception e) { return 0; }
    }

    private void setCBByScore(widget.ComboBox cb, int score, int[] values) {
        for (int j = 0; j < values.length; j++) {
            if (values[j] == score) { cb.setSelectedIndex(j); break; }
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
        isRawat(); isPasien();
        ChkInput.setSelected(true);
        isForm(true);
        tampil();
    }

    public void isForm(boolean show) {
        if (show) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 400));
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

    private void ganti() {
        int total = Integer.parseInt(TTotalSkor.getText().isEmpty() ? "0" : TTotalSkor.getText());
        String risiko = LabelRisiko.getText().replace("Kategori Risiko : ", "");
        String tgl = Valid.SetTgl(TglPengkajian.getSelectedItem() + "");
        Sequel.mengedit("strong_kids",
            "no_rawat=? and tgl_pengkajian=?",
            "no_rawat=?,no_rm=?,tgl_pengkajian=?,kondisi_subjektif_klinis=?,penyakit_risiko_tinggi=?,asupan_nutrisi=?,kehilangan_berat_badan=?,total_skor=?,risiko=?,rekomendasi=?,catatan=?,id_petugas=?,nm_petugas=?",
            16, new String[]{
                TNoRw.getText(), TNoRM.getText(), tgl,
                "" + skorDariCB(CBKlinis), "" + skorDariCB(CBPenyakit),
                "" + skorDariCB(CBAsupan), "" + skorDariCB(CBBB),
                "" + total, risiko, TRekomendasi.getText(),
                TCatatan.getText(), KdPetugas.getText(), NmPetugas.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
            });
        if (tabMode.getRowCount() != 0) { tampil(); }
        emptTeks();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from strong_kids where no_rawat=? and tgl_pengkajian=?", 2, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
        }) == true) {
            tampil(); emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }
}
