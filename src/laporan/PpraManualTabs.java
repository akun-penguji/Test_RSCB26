/*
 * Enam sub-laporan RL 3.19 yang datanya tidak ada di Khanza dan harus diisi
 * petugas: 3.19.1, 3.19.4, 3.19.5, 3.19.6, 3.19.7, 3.19.8.
 *
 * Dipisah dari DlgRL319PPRA supaya file itu tetap fokus ke dua laporan yang
 * dihitung otomatis. Kelas ini hanya membangun JPanel + baca/simpan tabelnya.
 *
 * Baris master (bakteri, isolat MDRO, SMF Gyssens) mengikuti persis urutan dan
 * penomoran portal, dibuat sekali per tahun lewat siapkanBarisTahun().
 */
package laporan;

import bridging.PpraBotBridge;
import fungsi.WarnaTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class PpraManualTabs {

    private final Connection koneksi;
    private final java.awt.Component induk;

    /** Urutan & penomoran mengikuti portal, jangan diubah tanpa cek ulang. */
    private static final String[] BAKTERI_3195 = {
        "Escherichia coli", "Klebsiella pneumonia", "Enterobacter cloacae",
        "Enterobacter aerogenes", "Pseudomonas aeruginosa", "Acinetobacter baumannii",
        "Enterococcus faecalis", "Enterococcus faecium", "Staphylococcus aureus",
        "Staphylococcus coagulase negatif", "Proteus mirabilis"
    };

    /** no_urut, spesimen, nama, induk(1=judul kelompok). */
    private static final String[][] BAKTERI_3196 = {
        {"1","Darah","Bakteri Batang Gram Negatif","1"},
        {"1.1","Darah","Escherichia coli","0"},
        {"1.2","Darah","Klebsiella pneumoniae","0"},
        {"1.3","Darah","Enterobacter cloacae","0"},
        {"1.4","Darah","Enterobacter aerogenes","0"},
        {"1.5","Darah","Pseudomonas aeruginosa","0"},
        {"1.6","Darah","Acinetobacter baumannii","0"},
        {"2","Darah","Bakteri Kokus Gram Positif","1"},
        {"2.1","Darah","Enterococcus faecalis","0"},
        {"2.2","Darah","Enterococcus faecium","0"},
        {"2.3","Darah","Staphylococcus aureus","0"},
        {"2.4","Darah","Stafilokokus koagulase negatif","0"},
        {"3","Urin","Bakteri Batang Gram Negatif","1"},
        {"3.1","Urin","Escherichia coli","0"},
        {"3.2","Urin","Klebsiella pneumoniae","0"},
        {"3.3","Urin","Enterobacter cloacae","0"},
        {"3.4","Urin","Pseudomonas aeruginosa","0"},
        {"3.5","Urin","Acinetobacter baumannii","0"},
        {"3.6","Urin","Proteus mirabilis","0"},
        {"4","Urin","Bakteri Kokus Gram Positif","1"},
        {"4.1","Urin","Enterococcus faecalis","0"},
        {"4.2","Urin","Staphylococcus aureus","0"}
    };

    private static final String[] AB_3196 = {
        "ctx","ctr","caz","fep","amc","sam","ipm","mem","gen","amk",
        "cip","lvx","sxt","fos","pen","amp","ery","cli","van","lnz"
    };
    private static final String[] AB_3196_JUDUL = {
        "CTX","CTR","CAZ","FEP","AMC","SAM","IPM","MEM","GEN","AMK",
        "CIP","LVX","SXT","FOS","PEN","AMP","ERY","CLI","VAN","LNZ"
    };

    /** no, kode, nama, isi_esbl, isi_mrsa, isi_total (0 = sel mati di portal). */
    private static final String[][] MDRO = {
        {"1","1","Isolat Penghasil ESBL","1","0","0"},
        {"2","1.1","Klebsiella pneumoniae","1","0","1"},
        {"3","1.2","E. coli","1","0","1"},
        {"4","2","Staphylococcus aureus","0","1","1"}
    };

    public PpraManualTabs(Connection koneksi, java.awt.Component induk) {
        this.koneksi = koneksi;
        this.induk = induk;
    }

    // =====================================================================
    //  Bantu umum
    // =====================================================================
    private widget.Label lbl(String t) {
        widget.Label l = new widget.Label();
        l.setText(t);
        return l;
    }

    private static javax.swing.ImageIcon ikon(String path) {
        try {
            java.net.URL u = PpraManualTabs.class.getResource(path);
            if (u != null) return new javax.swing.ImageIcon(u);
        } catch (Exception ignored) {}
        return null;
    }

    private widget.Button btn(String teks, int lebar, java.awt.event.ActionListener aksi) {
        widget.Button b = new widget.Button();
        b.setText(teks);
        b.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.setRoundRect(true);
        b.setIconTextGap(6);

        if (teks.contains("Kirim ke PPRA")) {
            b.setPreferredSize(new Dimension(Math.max(lebar, 195), 30));
            b.setIcon(ikon("/picture/accept.png"));
            b.setGlassColor(new java.awt.Color(200, 245, 215));
            b.setForeground(new java.awt.Color(0, 95, 45));
            b.setToolTipText("Kirim data otomatis ke Portal PPRA Kemenkes RI via Bot Bridging");
            b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(130, 195, 150), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
        } else if (teks.contains("Simpan")) {
            b.setPreferredSize(new Dimension(Math.max(lebar, 105), 30));
            b.setIcon(ikon("/picture/save-16x16.png"));
            b.setGlassColor(new java.awt.Color(225, 238, 255));
            b.setForeground(new java.awt.Color(25, 55, 115));
            b.setToolTipText("Simpan data ke basis data lokal");
            b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(160, 190, 230), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
        } else if (teks.contains("Tampilkan") || teks.contains("Muat")) {
            b.setPreferredSize(new Dimension(Math.max(lebar, 105), 30));
            b.setIcon(ikon("/picture/accept.png"));
            b.setGlassColor(new java.awt.Color(245, 245, 245));
            b.setForeground(new java.awt.Color(40, 40, 40));
            b.setToolTipText("Muat data laporan");
            b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(185, 190, 185), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
        } else if (teks.contains("Salin") || teks.contains("Clipboard")) {
            b.setPreferredSize(new Dimension(Math.max(lebar, 150), 30));
            b.setIcon(ikon("/picture/copy.png"));
            b.setGlassColor(new java.awt.Color(245, 245, 245));
            b.setForeground(new java.awt.Color(50, 50, 50));
            b.setToolTipText("Salin data tabel ke clipboard");
            b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(185, 190, 185), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
        } else {
            b.setPreferredSize(new Dimension(lebar, 30));
            b.setGlassColor(new java.awt.Color(245, 245, 245));
            b.setForeground(new java.awt.Color(50, 50, 50));
            b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(185, 190, 185), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 6)
            ));
        }

        b.addActionListener(aksi);
        return b;
    }

    private widget.panelisi baris() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        return p;
    }

    private widget.ComboBox comboTahun() {
        widget.ComboBox c = new widget.ComboBox();
        int kini = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        String[] th = new String[6];
        for (int i = 0; i < 6; i++) th[i] = String.valueOf(kini - i);
        c.setModel(new javax.swing.DefaultComboBoxModel(th));
        c.setPreferredSize(new Dimension(80, 23));
        return c;
    }

    private int tahun(widget.ComboBox c) {
        return Integer.parseInt(c.getSelectedItem().toString());
    }

    /** Judul laporan di kepala tiap tab, memakai penamaan resmi portal supaya
     *  petugas yakin sedang mengisi sub-laporan yang benar. */
    private JPanel judul(String teks) {
        widget.Label j = new widget.Label();
        j.setText(teks);
        j.setFont(new java.awt.Font("Tahoma", 1, 14));
        j.setForeground(new java.awt.Color(50, 50, 50));
        widget.panelisi p = new widget.panelisi();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        p.add(j);
        JPanel bungkus = new JPanel(new BorderLayout());
        bungkus.add(p, BorderLayout.CENTER);
        bungkus.setBorder(javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, new java.awt.Color(226, 231, 221)));
        return bungkus;
    }

    private JPanel bungkus(String teksJudul, java.awt.Component atas,
                           java.awt.Component tengah, java.awt.Component bawah) {
        JPanel kepala = new JPanel(new BorderLayout());
        kepala.add(judul(teksJudul), BorderLayout.NORTH);
        if (atas != null) kepala.add(atas, BorderLayout.CENTER);
        JPanel p = new JPanel(new BorderLayout());
        p.add(kepala, BorderLayout.NORTH);
        p.add(tengah, BorderLayout.CENTER);
        if (bawah != null) p.add(bawah, BorderLayout.SOUTH);
        return p;
    }

    private widget.ScrollPane scroll(JTable t) {
        widget.ScrollPane s = new widget.ScrollPane();
        s.setViewportView(t);
        return s;
    }

    private void siapTabel(widget.Table t, int[] lebar) {
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < lebar.length && i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        t.setDefaultRenderer(Object.class, new WarnaTable());
    }

    private Integer bulat(Object o) {
        String s = o == null ? "" : o.toString().trim();
        if (s.isEmpty()) return null;
        try { return Integer.valueOf(s); } catch (Exception e) { return null; }
    }

    private Double desimal(Object o) {
        String s = o == null ? "" : o.toString().trim().replace(',', '.');
        if (s.isEmpty()) return null;
        try { return Double.valueOf(s); } catch (Exception e) { return null; }
    }

    private void setInt(PreparedStatement p, int i, Integer v) throws java.sql.SQLException {
        if (v == null) p.setNull(i, java.sql.Types.INTEGER); else p.setInt(i, v);
    }

    private void setDbl(PreparedStatement p, int i, Double v) throws java.sql.SQLException {
        if (v == null) p.setNull(i, java.sql.Types.DECIMAL); else p.setDouble(i, v);
    }

    private void pesan(String s) {
        JOptionPane.showMessageDialog(induk, s);
    }

    private void galat(Exception e) {
        System.out.println("Notifikasi : " + e);
        JOptionPane.showMessageDialog(induk, "Gagal: " + e.getMessage());
    }

    /** Buat baris master untuk satu tahun bila belum ada. Aman diulang. */
    private void siapkanBarisTahun(int th) throws Exception {
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert ignore into ppra_rl3195_pola_bakteri (tahun,no_urut,jenis_bakteri) values (?,?,?)")) {
            for (int i = 0; i < BAKTERI_3195.length; i++) {
                p.setInt(1, th); p.setInt(2, i + 1); p.setString(3, BAKTERI_3195[i]);
                p.addBatch();
            }
            p.executeBatch();
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert ignore into ppra_rl3196_kepekaan (tahun,no_urut,spesimen,jenis_bakteri,induk) values (?,?,?,?,?)")) {
            for (String[] b : BAKTERI_3196) {
                p.setInt(1, th); p.setString(2, b[0]); p.setString(3, b[1]);
                p.setString(4, b[2]); p.setInt(5, Integer.parseInt(b[3]));
                p.addBatch();
            }
            p.executeBatch();
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert ignore into ppra_rl3197_mdro (tahun,no_urut,kode,jenis_isolat,isi_esbl,isi_mrsa,isi_total) "
              + "values (?,?,?,?,?,?,?)")) {
            for (String[] m : MDRO) {
                p.setInt(1, th); p.setInt(2, Integer.parseInt(m[0])); p.setString(3, m[1]);
                p.setString(4, m[2]); p.setInt(5, Integer.parseInt(m[3]));
                p.setInt(6, Integer.parseInt(m[4])); p.setInt(7, Integer.parseInt(m[5]));
                p.addBatch();
            }
            p.executeBatch();
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert ignore into ppra_rl3194_gyssens (tahun,kode_smf) select ?, kode_smf from ppra_smf")) {
            p.setInt(1, th);
            p.executeUpdate();
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert ignore into ppra_kultur_status (tahun,sub_laporan) values (?,'3.19.5'),(?,'3.19.6'),(?,'3.19.7')")) {
            p.setInt(1, th); p.setInt(2, th); p.setInt(3, th);
            p.executeUpdate();
        }
    }

    // =====================================================================
    //  3.19.1 Kegiatan PPRA
    // =====================================================================
    private widget.ComboBox th1, cmbTriwulan;
    private DefaultTableModel md1;
    private widget.Table tb1;

    /** Field 3.19.1 terlalu banyak untuk diatur satu per satu; ditampilkan
     *  sebagai daftar Isian-Nilai supaya urutannya sama dengan portal dan
     *  petugas tinggal menyalin ke atas-bawah. */
    private static final String[][] FIELD_3191 = {
        {"jenis_struktur","Jenis Struktur"},
        {"pemeriksaan_mikro","Pemeriksaan Mikro"},
        {"sebutkan_pemeriksaan_luar","Sebutkan Pemeriksaan Luar"},
        {"sdm_tanggung_jawab","SDM Tanggung Jawab"},
        {"sebutkan_sdm","Sebutkan SDM"},
        {"jumlah_apoteker","Jumlah tenaga apoteker farmasi klinik"},
        {"link_sk_tim","SK Komite/TIM PPRA (Link Gdrive)"},
        {"link_pedoman","Program PPRA Tahun Berjalan (Link Gdrive)"},
        {"link_laporan_program","Laporan Program PPRA 3 Bulanan (Link Gdrive)"},
        {"kegiatan_forkkit","Kegiatan forum kajian kasus infeksi terintegrasi"},
        {"jumlah_kasus","Jumlah kasus"},
        {"macam_kriteria_kasus","Macam kriteria kasus"},
        {"asal_smf","Asal SMF/KSM"},
        {"outcome_kondisi_pasien","Outcome kondisi pasien"},
        {"ada_kendala","Apakah ada kendala dalam pelaksanaan PPRA"},
        {"kendala_yg_dihadapi","Kendala yang dihadapi"},
        {"kendala_lainnya","Isi jika kendala Lainnya"},
        {"solusi","Solusi"},
        {"keterangan_tidak_lapor","Keterangan jika tidak dapat melaporkan data PPRA"}
    };

    public JPanel tab3191() {
        th1 = comboTahun();
        cmbTriwulan = new widget.ComboBox();
        cmbTriwulan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1", "2", "3", "4"}));
        cmbTriwulan.setPreferredSize(new Dimension(60, 23));

        md1 = new DefaultTableModel(null, new Object[]{"Isian", "Nilai"}) {
            @Override public boolean isCellEditable(int b, int k) { return k == 1; }
        };
        tb1 = new widget.Table();
        tb1.setModel(md1);
        siapTabel(tb1, new int[]{380, 640});

        widget.panelisi atas = baris();
        atas.add(lbl("Tahun :")); atas.add(th1);
        atas.add(lbl("Triwulan :")); atas.add(cmbTriwulan);
        atas.add(btn("Muat", 80, (ActionEvent e) -> muat3191()));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpan3191()));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra3191()));

        muat3191();
        return bungkus("RL 3.19.1  Kegiatan PPRA", atas, scroll(tb1), null);
    }

    private void muat3191() {
        md1.setRowCount(0);
        java.util.Map<String, String> isi = new java.util.HashMap<>();
        StringBuilder kol = new StringBuilder();
        for (String[] f : FIELD_3191) { if (kol.length() > 0) kol.append(","); kol.append(f[0]); }
        try (PreparedStatement p = koneksi.prepareStatement(
                "select " + kol + " from ppra_rl3191_kegiatan where tahun=? and triwulan=?")) {
            p.setInt(1, tahun(th1));
            p.setInt(2, Integer.parseInt(cmbTriwulan.getSelectedItem().toString()));
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    for (String[] f : FIELD_3191) {
                        String v = r.getString(f[0]);
                        isi.put(f[0], v == null ? "" : v);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        for (String[] f : FIELD_3191) {
            md1.addRow(new Object[]{f[1], isi.containsKey(f[0]) ? isi.get(f[0]) : ""});
        }
    }

    private void simpan3191() {
        StringBuilder kol = new StringBuilder(), tanya = new StringBuilder(), upd = new StringBuilder();
        for (String[] f : FIELD_3191) {
            if (kol.length() > 0) { kol.append(","); tanya.append(","); upd.append(","); }
            kol.append(f[0]); tanya.append("?"); upd.append(f[0]).append("=values(").append(f[0]).append(")");
        }
        String sql = "insert into ppra_rl3191_kegiatan (tahun,triwulan," + kol + ") values (?,?," + tanya + ") "
                   + "on duplicate key update " + upd;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setInt(1, tahun(th1));
            p.setInt(2, Integer.parseInt(cmbTriwulan.getSelectedItem().toString()));
            for (int i = 0; i < FIELD_3191.length; i++) {
                Object v = md1.getValueAt(i, 1);
                p.setString(3 + i, v == null ? "" : v.toString().trim());
            }
            p.executeUpdate();
            pesan("RL 3.19.1 tersimpan untuk tahun " + tahun(th1)
                    + " triwulan " + cmbTriwulan.getSelectedItem() + ".");
        } catch (Exception e) {
            galat(e);
        }
    }

    // =====================================================================
    //  3.19.4 Gyssens
    // =====================================================================
    private widget.ComboBox th4;
    private DefaultTableModel md4;
    private widget.Table tb4;

    public JPanel tab3194() {
        th4 = comboTahun();
        md4 = new DefaultTableModel(null, new Object[]{
            "Kode", "SMF/Instalasi", "Poliklinik Lainnya", "Jml Pasien Periode Survei",
            "Jml Pasien Pakai AB", "Gyssens VI", "Gyssens V", "Gyssens IV",
            "Gyssens IIIA", "Gyssens IIIB", "Gyssens II", "Gyssens I", "Gyssens 0"
        }) {
            @Override public boolean isCellEditable(int b, int k) { return k >= 2; }
        };
        tb4 = new widget.Table();
        tb4.setModel(md4);
        siapTabel(tb4, new int[]{45, 230, 140, 165, 130, 85, 85, 85, 95, 95, 85, 85, 85});

        widget.panelisi atas = baris();
        atas.add(lbl("Tahun :")); atas.add(th4);
        atas.add(btn("Muat", 80, (ActionEvent e) -> muat3194()));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpan3194()));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra3194()));
        atas.add(lbl("   Gyssens IV kumulatif IVA+IVB+IVC+IVD; Gyssens II kumulatif IIA+IIB+IIC."));

        muat3194();
        return bungkus("RL 3.19.4  Kualitas Penggunaan Antibiotik (Gyssens)", atas, scroll(tb4), null);
    }

    private void muat3194() {
        md4.setRowCount(0);
        try {
            siapkanBarisTahun(tahun(th4));
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select g.kode_smf, s.nama_smf, ifnull(g.poliklinik_lainnya,'') pl, g.jml_pasien_survei, "
                  + "  g.jml_pasien_antibiotik, g.gyssens_vi, g.gyssens_v, g.gyssens_iv, g.gyssens_iiia, "
                  + "  g.gyssens_iiib, g.gyssens_ii, g.gyssens_i, g.gyssens_0 "
                  + "from ppra_rl3194_gyssens g inner join ppra_smf s on s.kode_smf=g.kode_smf "
                  + "where g.tahun=? order by s.urut_gyssens")) {
                p.setInt(1, tahun(th4));
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        Object[] baris = new Object[13];
                        for (int i = 0; i < 13; i++) baris[i] = r.getString(i + 1);
                        md4.addRow(baris);
                    }
                }
            }
        } catch (Exception e) {
            galat(e);
        }
    }

    private void simpan3194() {
        String sql = "update ppra_rl3194_gyssens set poliklinik_lainnya=?, jml_pasien_survei=?, "
                   + "jml_pasien_antibiotik=?, gyssens_vi=?, gyssens_v=?, gyssens_iv=?, gyssens_iiia=?, "
                   + "gyssens_iiib=?, gyssens_ii=?, gyssens_i=?, gyssens_0=? where tahun=? and kode_smf=?";
        int n = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < md4.getRowCount(); i++) {
                p.setString(1, md4.getValueAt(i, 2) == null ? "" : md4.getValueAt(i, 2).toString().trim());
                for (int k = 0; k < 9; k++) {
                    Integer v = bulat(md4.getValueAt(i, 3 + k));
                    setInt(p, 2 + k, v == null ? 0 : v);
                }
                p.setInt(12, tahun(th4));
                p.setInt(13, Integer.parseInt(md4.getValueAt(i, 0).toString()));
                n += p.executeUpdate();
            }
            pesan("RL 3.19.4 tersimpan, " + n + " baris.");
        } catch (Exception e) {
            galat(e);
        }
    }

    // =====================================================================
    //  3.19.5 Pola Bakteri
    // =====================================================================
    private widget.ComboBox th5;
    private javax.swing.JCheckBox chk5;
    private DefaultTableModel md5;
    private widget.Table tb5;

    public JPanel tab3195() {
        th5 = comboTahun();
        chk5 = new javax.swing.JCheckBox("Tidak Melakukan (Tidak Ada Lab Mikro, Alat Kultur atau SDM)");
        chk5.setFont(new java.awt.Font("Tahoma", 0, 11));
        chk5.setOpaque(false);

        md5 = new DefaultTableModel(null, new Object[]{
            "No", "Jenis Bakteri", "Isolasi dari Spesimen Darah", "Isolasi dari Spesimen Urin"
        }) {
            @Override public boolean isCellEditable(int b, int k) { return k >= 2; }
        };
        tb5 = new widget.Table();
        tb5.setModel(md5);
        siapTabel(tb5, new int[]{45, 300, 220, 220});

        widget.panelisi atas = baris();
        atas.add(lbl("Tahun :")); atas.add(th5);
        atas.add(btn("Muat", 80, (ActionEvent e) -> muat3195()));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpan3195()));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra3195()));
        atas.add(chk5);

        muat3195();
        return bungkus("RL 3.19.5  Pola Bakteri Yang Diisolasi", atas, scroll(tb5), null);
    }

    private void muat3195() {
        md5.setRowCount(0);
        try {
            siapkanBarisTahun(tahun(th5));
            chk5.setSelected(statusKultur(tahun(th5), "3.19.5"));
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select no_urut, jenis_bakteri, ifnull(jml_isolasi_darah,'') d, ifnull(jml_isolasi_urin,'') u "
                  + "from ppra_rl3195_pola_bakteri where tahun=? order by no_urut")) {
                p.setInt(1, tahun(th5));
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        md5.addRow(new Object[]{r.getInt(1), r.getString(2), r.getString(3), r.getString(4)});
                    }
                }
            }
        } catch (Exception e) {
            galat(e);
        }
    }

    private void simpan3195() {
        int n = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "update ppra_rl3195_pola_bakteri set jml_isolasi_darah=?, jml_isolasi_urin=? "
              + "where tahun=? and no_urut=?")) {
            for (int i = 0; i < md5.getRowCount(); i++) {
                setInt(p, 1, bulat(md5.getValueAt(i, 2)));
                setInt(p, 2, bulat(md5.getValueAt(i, 3)));
                p.setInt(3, tahun(th5));
                p.setInt(4, Integer.parseInt(md5.getValueAt(i, 0).toString()));
                n += p.executeUpdate();
            }
            simpanStatusKultur(tahun(th5), "3.19.5", chk5.isSelected());
            pesan("RL 3.19.5 tersimpan, " + n + " baris.");
        } catch (Exception e) {
            galat(e);
        }
    }

    // =====================================================================
    //  3.19.6 Kepekaan Bakteri
    // =====================================================================
    private widget.ComboBox th6;
    private javax.swing.JCheckBox chk6;
    private DefaultTableModel md6;
    private widget.Table tb6;

    public JPanel tab3196() {
        th6 = comboTahun();
        chk6 = new javax.swing.JCheckBox("Tidak Melakukan (Tidak Ada Lab Mikro, Alat Kultur atau SDM)");
        chk6.setFont(new java.awt.Font("Tahoma", 0, 11));
        chk6.setOpaque(false);

        Object[] judul = new Object[4 + AB_3196.length];
        judul[0] = "No"; judul[1] = "Spesimen"; judul[2] = "Jenis Bakteri"; judul[3] = "Jumlah Isolat";
        for (int i = 0; i < AB_3196_JUDUL.length; i++) judul[4 + i] = AB_3196_JUDUL[i] + " %S";

        md6 = new DefaultTableModel(null, judul) {
            @Override public boolean isCellEditable(int b, int k) {
                if (k < 3) return false;
                // baris judul kelompok (induk) tidak diisi, sama seperti portal
                Object ind = getValueAt(b, 2);
                return ind == null || !ind.toString().startsWith("» ");
            }
        };
        tb6 = new widget.Table();
        tb6.setModel(md6);
        int[] lebar = new int[judul.length];
        lebar[0] = 45; lebar[1] = 70; lebar[2] = 230; lebar[3] = 90;
        for (int i = 4; i < lebar.length; i++) lebar[i] = 70;
        siapTabel(tb6, lebar);

        widget.panelisi atas = baris();
        atas.add(lbl("Tahun :")); atas.add(th6);
        atas.add(btn("Muat", 80, (ActionEvent e) -> muat3196()));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpan3196()));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra3196()));
        atas.add(chk6);

        muat3196();
        return bungkus("RL 3.19.6  Kepekaan Bakteri Terhadap Antibiotik", atas, scroll(tb6), null);
    }

    private void muat3196() {
        md6.setRowCount(0);
        try {
            siapkanBarisTahun(tahun(th6));
            chk6.setSelected(statusKultur(tahun(th6), "3.19.6"));
            StringBuilder kol = new StringBuilder();
            for (String c : AB_3196) kol.append(", ifnull(").append(c).append(",'') ").append(c);
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select no_urut, spesimen, jenis_bakteri, induk, ifnull(jumlah_isolat,'') jumlah_isolat" + kol
                  + " from ppra_rl3196_kepekaan where tahun=? order by cast(substring_index(no_urut,'.',1) as unsigned), "
                  + "   length(no_urut), no_urut")) {
                p.setInt(1, tahun(th6));
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        Object[] b = new Object[4 + AB_3196.length];
                        boolean induk = r.getInt("induk") == 1;
                        b[0] = r.getString("no_urut");
                        b[1] = r.getString("spesimen");
                        b[2] = (induk ? "» " : "   ") + r.getString("jenis_bakteri");
                        b[3] = induk ? "" : r.getString("jumlah_isolat");
                        for (int i = 0; i < AB_3196.length; i++) {
                            b[4 + i] = induk ? "" : r.getString(AB_3196[i]);
                        }
                        md6.addRow(b);
                    }
                }
            }
        } catch (Exception e) {
            galat(e);
        }
    }

    private void simpan3196() {
        StringBuilder set = new StringBuilder("jumlah_isolat=?");
        for (String c : AB_3196) set.append(", ").append(c).append("=?");
        int n = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "update ppra_rl3196_kepekaan set " + set + " where tahun=? and no_urut=?")) {
            for (int i = 0; i < md6.getRowCount(); i++) {
                if (md6.getValueAt(i, 2).toString().startsWith("» ")) continue;  // baris judul
                setInt(p, 1, bulat(md6.getValueAt(i, 3)));
                for (int k = 0; k < AB_3196.length; k++) {
                    setDbl(p, 2 + k, desimal(md6.getValueAt(i, 4 + k)));
                }
                p.setInt(2 + AB_3196.length, tahun(th6));
                p.setString(3 + AB_3196.length, md6.getValueAt(i, 0).toString());
                n += p.executeUpdate();
            }
            simpanStatusKultur(tahun(th6), "3.19.6", chk6.isSelected());
            pesan("RL 3.19.6 tersimpan, " + n + " baris.");
        } catch (Exception e) {
            galat(e);
        }
    }

    // =====================================================================
    //  3.19.7 MDRO
    // =====================================================================
    private widget.ComboBox th7;
    private javax.swing.JCheckBox chk7;
    private DefaultTableModel md7;
    private widget.Table tb7;

    public JPanel tab3197() {
        th7 = comboTahun();
        chk7 = new javax.swing.JCheckBox("Tidak Melakukan (Tidak Ada Lab Mikro, Alat Kultur atau SDM)");
        chk7.setFont(new java.awt.Font("Tahoma", 0, 11));
        chk7.setOpaque(false);

        md7 = new DefaultTableModel(null, new Object[]{
            "Nomor", "Kode", "Jenis Isolat", "Jumlah Isolat Penghasil ESBL", "Jumlah Isolat MRSA", "Total Isolat"
        }) {
            @Override public boolean isCellEditable(int b, int k) {
                if (k < 3) return false;
                // sel yang di portal mati ditandai "-" saat dimuat
                Object v = getValueAt(b, k);
                return v == null || !v.toString().equals("-");
            }
        };
        tb7 = new widget.Table();
        tb7.setModel(md7);
        siapTabel(tb7, new int[]{60, 60, 260, 220, 180, 150});

        widget.panelisi atas = baris();
        atas.add(lbl("Tahun :")); atas.add(th7);
        atas.add(btn("Muat", 80, (ActionEvent e) -> muat3197()));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpan3197()));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra3197()));
        atas.add(chk7);
        atas.add(lbl("   Sel bertanda \"-\" memang dinonaktifkan di portal."));

        muat3197();
        return bungkus("RL 3.19.7  Multidrug Resistant Organism (MDRO)", atas, scroll(tb7), null);
    }

    private void muat3197() {
        md7.setRowCount(0);
        try {
            siapkanBarisTahun(tahun(th7));
            chk7.setSelected(statusKultur(tahun(th7), "3.19.7"));
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select no_urut, kode, jenis_isolat, isi_esbl, isi_mrsa, isi_total, "
                  + "  ifnull(jml_isolat_esbl,'') e, ifnull(jml_isolat_mrsa,'') m, ifnull(total_isolat,'') t "
                  + "from ppra_rl3197_mdro where tahun=? order by no_urut")) {
                p.setInt(1, tahun(th7));
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        md7.addRow(new Object[]{
                            r.getInt("no_urut"), r.getString("kode"), r.getString("jenis_isolat"),
                            r.getInt("isi_esbl") == 1 ? r.getString("e") : "-",
                            r.getInt("isi_mrsa") == 1 ? r.getString("m") : "-",
                            r.getInt("isi_total") == 1 ? r.getString("t") : "-"
                        });
                    }
                }
            }
        } catch (Exception e) {
            galat(e);
        }
    }

    private void simpan3197() {
        int n = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "update ppra_rl3197_mdro set jml_isolat_esbl=?, jml_isolat_mrsa=?, total_isolat=? "
              + "where tahun=? and no_urut=?")) {
            for (int i = 0; i < md7.getRowCount(); i++) {
                setInt(p, 1, bulat(md7.getValueAt(i, 3)));
                setInt(p, 2, bulat(md7.getValueAt(i, 4)));
                setInt(p, 3, bulat(md7.getValueAt(i, 5)));
                p.setInt(4, tahun(th7));
                p.setInt(5, Integer.parseInt(md7.getValueAt(i, 0).toString()));
                n += p.executeUpdate();
            }
            simpanStatusKultur(tahun(th7), "3.19.7", chk7.isSelected());
            pesan("RL 3.19.7 tersimpan, " + n + " baris.");
        } catch (Exception e) {
            galat(e);
        }
    }

    // =====================================================================
    //  3.19.8 AB Empirik Sistemik
    // =====================================================================
    private widget.ComboBox th8;
    private widget.TextBox tPeriode8, tLink8;
    private DefaultTableModel md8;
    private widget.Table tb8;
    private widget.Label lblRingkas8;

    private static final String[][] BARIS_3198 = {
        {"pneumonia","Pneumonia"},
        {"sepsis","Sepsis"},
        {"isk","Infeksi Saluran Kemih (ISK)"},
        {"lain1","Diagnosis Lain 1"},
        {"lain2","Diagnosis Lain 2"},
        {"lain3","Diagnosis Lain 3"}
    };

    public JPanel tab3198() {
        th8 = comboTahun();
        tPeriode8 = new widget.TextBox();
        tPeriode8.setPreferredSize(new Dimension(150, 23));
        tLink8 = new widget.TextBox();
        tLink8.setPreferredSize(new Dimension(420, 23));

        md8 = new DefaultTableModel(null, new Object[]{
            "Diagnosis", "Label (untuk Diagnosis Lain)", "Total Pasien Sebagai Sample",
            "Total Sesuai Standar", "% Kesesuaian"
        }) {
            @Override public boolean isCellEditable(int b, int k) {
                if (k == 4) return false;
                if (k == 1) return b >= 3;   // label hanya untuk diagnosis lain
                return true;
            }
        };
        tb8 = new widget.Table();
        tb8.setModel(md8);
        siapTabel(tb8, new int[]{240, 240, 230, 190, 120});
        md8.addTableModelListener((javax.swing.event.TableModelEvent e) -> hitung3198());

        lblRingkas8 = lbl("");

        widget.panelisi atas = baris();
        atas.add(lbl("Tahun :")); atas.add(th8);
        atas.add(lbl("Periode :")); atas.add(tPeriode8);
        atas.add(btn("Muat", 80, (ActionEvent e) -> muat3198()));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpan3198()));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra3198()));

        widget.panelisi bawah = new widget.panelisi();
        bawah.setLayout(new java.awt.GridLayout(2, 1));
        widget.panelisi barisLink = baris();
        barisLink.add(lbl("Link GD lembar kerja (wajib di portal) :"));
        barisLink.add(tLink8);
        bawah.add(barisLink);
        bawah.add(lblRingkas8);

        isiKerangka3198();
        return bungkus("RL 3.19.8  Kepatuhan Empirik Sistemik Sesuai Standar", atas, scroll(tb8), bawah);
    }

    private void isiKerangka3198() {
        md8.setRowCount(0);
        for (String[] b : BARIS_3198) {
            md8.addRow(new Object[]{b[1], "", "0", "0", "0,00"});
        }
    }

    private void hitung3198() {
        int totS = 0, totOk = 0;
        for (int i = 0; i < md8.getRowCount(); i++) {
            Integer s = bulat(md8.getValueAt(i, 2)), ok = bulat(md8.getValueAt(i, 3));
            int sv = s == null ? 0 : s, okv = ok == null ? 0 : ok;
            totS += sv; totOk += okv;
            String persen = sv == 0 ? "0,00" : String.format("%.2f", 100.0 * okv / sv).replace('.', ',');
            if (!persen.equals(String.valueOf(md8.getValueAt(i, 4)))) {
                md8.setValueAt(persen, i, 4);
            }
        }
        lblRingkas8.setText("Total Diagnosa Sample " + totS + "   |   Total Sesuai Standar " + totOk
                + "   |   Rata Kesesuaian "
                + (totS == 0 ? "0,00" : String.format("%.2f", 100.0 * totOk / totS).replace('.', ',')) + "%");
    }

    private void muat3198() {
        isiKerangka3198();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select * from ppra_rl3198_empirik where tahun=? and periode=?")) {
            p.setInt(1, tahun(th8));
            p.setString(2, tPeriode8.getText().trim());
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    for (int i = 0; i < BARIS_3198.length; i++) {
                        String k = BARIS_3198[i][0];
                        if (i >= 3) md8.setValueAt(nz(r.getString(k + "_label")), i, 1);
                        md8.setValueAt(nz(r.getString(k + "_sample")), i, 2);
                        md8.setValueAt(nz(r.getString(k + "_sesuai")), i, 3);
                    }
                    tLink8.setText(nz(r.getString("link_gd")));
                } else {
                    pesan("Belum ada data untuk tahun " + tahun(th8)
                            + " periode \"" + tPeriode8.getText().trim() + "\".");
                }
            }
        } catch (Exception e) {
            galat(e);
        }
        hitung3198();
    }

    private String nz(String s) { return s == null ? "" : s; }

    private void simpan3198() {
        if (tPeriode8.getText().trim().isEmpty()) {
            pesan("Periode wajib diisi (jadi bagian kunci data).");
            return;
        }
        StringBuilder kol = new StringBuilder(), tanya = new StringBuilder(), upd = new StringBuilder();
        java.util.List<String> nilai = new java.util.ArrayList<>();
        for (int i = 0; i < BARIS_3198.length; i++) {
            String k = BARIS_3198[i][0];
            if (i >= 3) {
                kol.append(k).append("_label,"); tanya.append("?,");
                upd.append(k).append("_label=values(").append(k).append("_label),");
                nilai.add(md8.getValueAt(i, 1) == null ? "" : md8.getValueAt(i, 1).toString().trim());
            }
            kol.append(k).append("_sample,").append(k).append("_sesuai,");
            tanya.append("?,?,");
            upd.append(k).append("_sample=values(").append(k).append("_sample),")
               .append(k).append("_sesuai=values(").append(k).append("_sesuai),");
            Integer s = bulat(md8.getValueAt(i, 2)), ok = bulat(md8.getValueAt(i, 3));
            nilai.add(String.valueOf(s == null ? 0 : s));
            nilai.add(String.valueOf(ok == null ? 0 : ok));
        }
        kol.append("link_gd"); tanya.append("?"); upd.append("link_gd=values(link_gd)");
        nilai.add(tLink8.getText().trim());

        String sql = "insert into ppra_rl3198_empirik (tahun,periode," + kol + ") values (?,?," + tanya + ") "
                   + "on duplicate key update " + upd;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setInt(1, tahun(th8));
            p.setString(2, tPeriode8.getText().trim());
            for (int i = 0; i < nilai.size(); i++) p.setString(3 + i, nilai.get(i));
            p.executeUpdate();
            pesan("RL 3.19.8 tersimpan untuk tahun " + tahun(th8)
                    + " periode \"" + tPeriode8.getText().trim() + "\".");
        } catch (Exception e) {
            galat(e);
        }
    }

    // =====================================================================
    //  Penanda "Tidak Melakukan"
    // =====================================================================
    private boolean statusKultur(int th, String sub) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select tidak_melakukan from ppra_kultur_status where tahun=? and sub_laporan=?")) {
            p.setInt(1, th); p.setString(2, sub);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return r.getInt(1) == 1;
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        return false;
    }

    private void simpanStatusKultur(int th, String sub, boolean tidak) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert into ppra_kultur_status (tahun,sub_laporan,tidak_melakukan,alasan) values (?,?,?,?) "
              + "on duplicate key update tidak_melakukan=values(tidak_melakukan), alasan=values(alasan)")) {
            p.setInt(1, th); p.setString(2, sub); p.setInt(3, tidak ? 1 : 0);
            p.setString(4, tidak ? "Tidak ada lab mikrobiologi / alat kultur / SDM" : null);
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    // =========================================================================
    //  Metode Pengiriman Otomatis via Bot PPRA Kemenkes
    // =========================================================================
    private void kirimPpra3191() {
        int th = tahun(th1);
        int tw = Integer.parseInt(cmbTriwulan.getSelectedItem().toString());
        int konfirmasi = JOptionPane.showConfirmDialog(induk,
            "Kirim data RL 3.19.1 (Kegiatan PPRA) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + th + "\n"
            + "• Triwulan: " + tw + "\n"
            + "• Jumlah Indikator: " + md1.getRowCount() + " item\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.1",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        Object[] data = new Object[md1.getRowCount()];
        for (int i = 0; i < md1.getRowCount(); i++) {
            data[i] = md1.getValueAt(i, 1);
        }
        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim191(th, data);
        tampilkanHasil(res, "RL 3.19.1 Kegiatan PPRA");
    }

    private void kirimPpra3194() {
        int th = tahun(th4);
        int konfirmasi = JOptionPane.showConfirmDialog(induk,
            "Kirim data RL 3.19.4 (Gyssens) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + th + "\n"
            + "• Jumlah SMF: " + md4.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.4",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md4.getRowCount(); i++) {
            Object[] r = new Object[md4.getColumnCount()];
            for (int c = 0; c < md4.getColumnCount(); c++) r[c] = md4.getValueAt(i, c);
            rows.add(r);
        }
        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim194(th, rows);
        tampilkanHasil(res, "RL 3.19.4 Kualitas Gyssens");
    }

    private void kirimPpra3195() {
        int th = tahun(th5);
        int konfirmasi = JOptionPane.showConfirmDialog(induk,
            "Kirim data RL 3.19.5 (Pola Bakteri) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + th + "\n"
            + "• Jumlah Bakteri: " + md5.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.5",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md5.getRowCount(); i++) {
            Object[] r = new Object[md5.getColumnCount()];
            for (int c = 0; c < md5.getColumnCount(); c++) r[c] = md5.getValueAt(i, c);
            rows.add(r);
        }
        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim195(th, rows);
        tampilkanHasil(res, "RL 3.19.5 Pola Bakteri");
    }

    private void kirimPpra3196() {
        int th = tahun(th6);
        int konfirmasi = JOptionPane.showConfirmDialog(induk,
            "Kirim data RL 3.19.6 (Kepekaan Bakteri) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + th + "\n"
            + "• Jumlah Baris: " + md6.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.6",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md6.getRowCount(); i++) {
            Object[] r = new Object[md6.getColumnCount()];
            for (int c = 0; c < md6.getColumnCount(); c++) r[c] = md6.getValueAt(i, c);
            rows.add(r);
        }
        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim196(th, rows);
        tampilkanHasil(res, "RL 3.19.6 Kepekaan Bakteri");
    }

    private void kirimPpra3197() {
        int th = tahun(th7);
        int konfirmasi = JOptionPane.showConfirmDialog(induk,
            "Kirim data RL 3.19.7 (MDRO) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + th + "\n"
            + "• Jumlah Isolat: " + md7.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.7",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md7.getRowCount(); i++) {
            Object[] r = new Object[md7.getColumnCount()];
            for (int c = 0; c < md7.getColumnCount(); c++) r[c] = md7.getValueAt(i, c);
            rows.add(r);
        }
        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim197(th, rows);
        tampilkanHasil(res, "RL 3.19.7 MDRO");
    }

    private void kirimPpra3198() {
        int th = tahun(th8);
        int konfirmasi = JOptionPane.showConfirmDialog(induk,
            "Kirim data RL 3.19.8 (AB Empirik) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + th + "\n"
            + "• Periode: " + tPeriode8.getText().trim() + "\n"
            + "• Jumlah Kasus: " + md8.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.8",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md8.getRowCount(); i++) {
            Object[] r = new Object[md8.getColumnCount()];
            for (int c = 0; c < md8.getColumnCount(); c++) r[c] = md8.getValueAt(i, c);
            rows.add(r);
        }
        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim198(th, rows);
        tampilkanHasil(res, "RL 3.19.8 AB Empirik");
    }

    private void tampilkanHasil(PpraBotBridge.BridgeResult res, String judul) {
        if (res.ok) {
            JOptionPane.showMessageDialog(induk,
                "Laporan " + judul + " BERHASIL diproses via Bot PPRA Kemenkes!\n\n"
                + "Hasil: " + res.message + "\n\nRespon Portal:\n" + res.detail,
                "Sukses Bridging PPRA",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(induk,
                "Gagal mengirim " + judul + " ke Portal PPRA Kemenkes:\n\n"
                + res.message + "\n\nDetail: " + res.detail,
                "Gagal Bridging PPRA",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
