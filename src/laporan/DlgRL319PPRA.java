/*
 * Laporan RL 3.19 PPRA — Program Pengendalian Resistansi Antimikroba.
 *
 * Menyiapkan angka untuk portal mutufasyankes.kemkes.go.id/ppra (v3.0.0):
 *   Tab 1  RL 3.19.2  Penggunaan Antibiotik  (otomatis dari data)
 *   Tab 2  RL 3.19.3  Kuantitas / DDD        (otomatis dari data)
 *   Tab 3  Pemetaan SMF                      (master, diedit petugas)
 *   Tab 4  Daftar Antibiotik & DDD           (master, diedit apoteker)
 *   Tab 5-10  RL 3.19.1/4/5/6/7/8            (diisi petugas, lihat PpraManualTabs)
 *
 * Pembilang diambil dari detail_pemberian_obat (keputusan tim PPRA:
 * obat yang benar-benar diberikan, bukan yang diresepkan).
 *
 * Layout programatik dengan widget Khanza, tanpa .form NetBeans, supaya
 * tidak ada risiko koordinat bergeser saat file dibuka di IDE.
 */
package laporan;

import bridging.PpraBotBridge;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgRL319PPRA extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final SimpleDateFormat FDB = new SimpleDateFormat("yyyy-MM-dd");

    private widget.InternalFrame internalFrame1;
    private widget.TabPane tab;

    // Tab 1 & 2 : filter periode dipakai bersama
    private widget.Tanggal Tgl1, Tgl2;
    private widget.ComboBox cmbRawat, cmbSmf193;

    private widget.Table tb192, tb193, tbSmf, tbAb;
    private DefaultTableModel md192, md193, mdSmf, mdAb;
    private widget.Label lblSampel192, lblPenyebut193, lblCakupan193;
    private widget.Label lblInfoRawat193;
    private double terakhirPenyebut193 = 0;

    public DlgRL319PPRA(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initTampilan();
        setSize(1180, 690);
        setLocationRelativeTo(parent);
    }

    // =====================================================================
    //  Kerangka
    // =====================================================================
    private void initTampilan() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("::[ Laporan RL 3.19 PPRA ]::");
        getContentPane().setLayout(new BorderLayout());

        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ RL 3.19 Program Pengendalian Resistansi Antimikroba ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout());

        tab = new widget.TabPane();
        tab.setFont(new java.awt.Font("Tahoma", 0, 11));
        tab.addTab("RL 3.19.2 Penggunaan Antibiotik", buatTab192());
        tab.addTab("RL 3.19.3 Kuantitas (DDD)", buatTab193());
        tab.addTab("Pemetaan SMF", buatTabSmf());
        tab.addTab("Daftar Antibiotik & DDD", buatTabAb());
        tab.addChangeListener((javax.swing.event.ChangeEvent e) -> {
            if (tab.getSelectedIndex() == 1) {
                updatePilihanSmf193();
            }
        });

        // Sub-laporan yang datanya tidak ada di Khanza -> diisi petugas.
        PpraManualTabs manual = new PpraManualTabs(koneksi, this);
        tab.addTab("RL 3.19.1 Kegiatan PPRA", manual.tab3191());
        tab.addTab("RL 3.19.4 Gyssens", manual.tab3194());
        tab.addTab("RL 3.19.5 Pola Bakteri", manual.tab3195());
        tab.addTab("RL 3.19.6 Kepekaan", manual.tab3196());
        tab.addTab("RL 3.19.7 MDRO", manual.tab3197());
        tab.addTab("RL 3.19.8 AB Empirik", manual.tab3198());

        internalFrame1.add(tab, BorderLayout.CENTER);
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        muatSmf();
        muatAb();
        tampil192();
    }

    private widget.Label lbl(String t) {
        widget.Label l = new widget.Label();
        l.setText(t);
        return l;
    }

    private static javax.swing.ImageIcon ikon(String path) {
        try {
            java.net.URL u = DlgRL319PPRA.class.getResource(path);
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
            b.setPreferredSize(new Dimension(Math.max(lebar, 110), 30));
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

    /** Gabung judul + baris kendali jadi satu blok kepala tab. */
    private JPanel kepala(String teksJudul, java.awt.Component kendali) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(judul(teksJudul), BorderLayout.NORTH);
        p.add(kendali, BorderLayout.CENTER);
        return p;
    }

    private widget.panelisi barisAtas() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        return p;
    }

    private void aturKolom(widget.Table t, int[] lebar) {
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < lebar.length; i++) {
            TableColumn k = t.getColumnModel().getColumn(i);
            k.setPreferredWidth(lebar[i]);
        }
        t.setDefaultRenderer(Object.class, new WarnaTable());
    }

    /** Periode default: awal tahun berjalan s.d. hari ini. */
    private void siapkanPeriode() {
        Tgl1 = new widget.Tanggal();
        Tgl2 = new widget.Tanggal();
        Tgl1.setPreferredSize(new Dimension(105, 23));
        Tgl2.setPreferredSize(new Dimension(105, 23));
        Calendar c = Calendar.getInstance();
        Tgl2.setDate(c.getTime());
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, 0);
        Tgl1.setDate(c.getTime());

        cmbRawat = new widget.ComboBox();
        cmbRawat.setModel(new javax.swing.DefaultComboBoxModel(
                new String[]{"Rawat Inap", "Rawat Jalan", "IGD"}));
        cmbRawat.setPreferredSize(new Dimension(110, 23));
        cmbRawat.addActionListener((ActionEvent e) -> updatePilihanSmf193());
    }

    private String tgl(widget.Tanggal t) {
        return FDB.format(t.getDate());
    }

    private boolean ranap() {
        return cmbRawat.getSelectedItem().toString().equals("Rawat Inap");
    }

    // Mode "IGD": subset dari Rawat Jalan (status_lanjut Ralan) yang dibatasi
    // pada poliklinik IGD. Untuk melihat data internal IGD saja; pengiriman ke
    // portal tetap lewat mode "Rawat Jalan" (IGD sudah tergabung di dalamnya).
    private boolean igd() {
        return cmbRawat.getSelectedItem().toString().equals("IGD");
    }

    // IGD dipisahkan dari Rawat Jalan (kebijakan RS):
    // - Mode "IGD": hanya kunjungan IGD/IGDK
    // - Mode "Rawat Jalan": rawat jalan murni (IGD/IGDK dikecualikan)
    // - Mode "Rawat Inap": tidak terpengaruh kd_poli
    private String filterIgd(String alias) {
        if (igd()) {
            return " and " + alias + ".kd_poli in ('IGD','IGDK') ";
        } else if (!ranap()) {
            return " and " + alias + ".kd_poli not in ('IGD','IGDK') ";
        }
        return "";
    }

    /** Kode SMF dari pilihan "N. Nama"; 0 = Semua Spesialis. */
    private int smfTerpilih193() {
        if (cmbSmf193 == null || cmbSmf193.getSelectedItem() == null) return 0;
        String val = cmbSmf193.getSelectedItem().toString().trim();
        int titik = val.indexOf('.');
        if (titik <= 0) return 0;
        try { return Integer.parseInt(val.substring(0, titik)); } catch (Exception e) { return 0; }
    }

    private String namaSmfTerpilih193() {
        if (cmbSmf193 == null || cmbSmf193.getSelectedItem() == null) return "";
        return cmbSmf193.getSelectedItem().toString();
    }

    public void updatePilihanSmf193() {
        if (cmbSmf193 == null || cmbRawat == null) return;
        Object selected = cmbSmf193.getSelectedItem();
        String selectedStr = selected != null ? selected.toString() : "";

        java.awt.event.ActionListener[] listeners = cmbSmf193.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            cmbSmf193.removeActionListener(l);
        }

        cmbSmf193.removeAllItems();
        cmbSmf193.addItem("Semua Spesialis");
        // Diambil dari ppra_smf supaya SMF tambahan (mis. 8 = IGD) ikut tampil.
        // ICU (5) hanya bermakna untuk Rawat Inap, IGD (8) hanya untuk mode IGD.
        try (Statement st = koneksi.createStatement();
             ResultSet r = st.executeQuery("select kode_smf, nama_smf from ppra_smf order by kode_smf")) {
            while (r.next()) {
                int kode = r.getInt(1);
                if (kode == 5 && !ranap()) continue;
                if (kode == 8 && !igd()) continue;
                cmbSmf193.addItem(kode + ". " + r.getString(2));
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        boolean match = false;
        for (int i = 0; i < cmbSmf193.getItemCount(); i++) {
            if (cmbSmf193.getItemAt(i).toString().equals(selectedStr)) {
                cmbSmf193.setSelectedIndex(i);
                match = true;
                break;
            }
        }
        if (!match) {
            cmbSmf193.setSelectedIndex(0);
        }

        for (java.awt.event.ActionListener l : listeners) {
            cmbSmf193.addActionListener(l);
        }

        if (lblInfoRawat193 != null) {
            lblInfoRawat193.setText("   (Jenis Perawatan: " + cmbRawat.getSelectedItem().toString() + ")");
        }
    }

    /**
     * Satu pasien Ranap -> satu SMF, deterministik walau pasien pindah bangsal:
     * pernah di bangsal ICU -> 5; lalu spesialis DPJP pertama (fallback dokter registrasi);
     * pernah di bangsal anak -> 3; sisanya pemetaan bangsal (nilai terbesar) atau 7.
     * Sebelumnya kolom non-agregat dipilih acak per baris kamar_inap, dan versi filter memakai
     * max(kode_smf)=5 sehingga pasien ICU yang juga pernah di bangsal bernilai 7 tidak terdeteksi.
     */
    private static final String SMF_RANAP_CASE =
          "case "
        + "  when max(m.kode_smf = 5) = 1 then 5 "
        + "  when max(sp.kd_sps) = 'S0013' then 1 "
        + "  when max(sp.kd_sps) in ('S0006', 'S0016', 'S0005', 'S0003') then 2 "
        + "  when max(sp.kd_sps) = 'S0011' or max(m.kode_smf = 3) = 1 then 3 "
        + "  when max(sp.kd_sps) = 'S0012' then 4 "
        + "  when max(sp.kd_sps) = 'S0018' then 6 "
        + "  else ifnull(max(m.kode_smf), 7) "
        + "end";

    private static final String KELUAR_KI =
        "if(ki.tgl_keluar is null or ki.tgl_keluar='0000-00-00', curdate(), ki.tgl_keluar)";

    /**
     * Pasien Ranap beserta SMF-nya.
     * dalamPeriode=false (RL 3.19.2): pasien yang MASUK dalam periode (definisi sampel tidak diubah).
     * dalamPeriode=true  (RL 3.19.3): pasien yang dirawat KAPAN SAJA di dalam periode, dengan
     *   hari rawat hanya dihitung di dalam periode (sebelumnya dihitung sampai pulang/hari ini,
     *   sementara antibiotiknya disaring per periode). Masuk & pulang di hari yang sama = 1 hari.
     * Tanggal disisipkan langsung karena berasal dari date picker yang diformat yyyy-MM-dd.
     */
    private String subqueryRanapSmf(String awal, String akhir, boolean dalamPeriode) {
        String a = "'" + awal + "'", z = "'" + akhir + "'";
        String hari = dalamPeriode
            ? "greatest(sum(greatest(datediff(least(" + KELUAR_KI + ", " + z + "), greatest(ki.tgl_masuk, " + a + ")), 0)), "
              + "if(min(ki.tgl_masuk) = max(" + KELUAR_KI + ") and min(ki.tgl_masuk) between " + a + " and " + z + ", 1, 0))"
            : "sum(datediff(" + KELUAR_KI + ", ki.tgl_masuk))";
        String where = dalamPeriode
            ? "ki.tgl_masuk <= " + z + " and " + KELUAR_KI + " >= " + a
            : "ki.tgl_masuk between " + a + " and " + z;
        return "(select ki.no_rawat, " + SMF_RANAP_CASE + " as kode_smf, " + hari + " as hari "
             + "from kamar_inap ki "
             + "inner join kamar k on k.kd_kamar=ki.kd_kamar "
             + "inner join reg_periksa rp on rp.no_rawat=ki.no_rawat "
             + "left join ppra_smf_mapping m on m.jenis='Ranap' and m.kode_ref=k.kd_bangsal "
             + "left join (select no_rawat, min(kd_dokter) kd_dokter from dpjp_ranap group by no_rawat) dr on dr.no_rawat=ki.no_rawat "
             + "left join dokter d on d.kd_dokter=ifnull(dr.kd_dokter, rp.kd_dokter) "
             + "left join spesialis sp on sp.kd_sps=d.kd_sps "
             + "where " + where + " "
             + "group by ki.no_rawat) ";
    }

    // Subquery untuk memetakan pasien Ralan ke SMF spesialis
    private String subqueryRalanSmf() {
        return "(select rp.no_rawat, ifnull(m.kode_smf, 7) as kode_smf "
             + "from reg_periksa rp "
             + "left join ppra_smf_mapping m on m.jenis='Ralan' and m.kode_ref=rp.kd_poli "
             + "where rp.status_lanjut='Ralan' and rp.tgl_registrasi between ? and ? "
             + filterIgd("rp") + ") ";
    }

    /** Batasi antibiotik Ranap ke pasien SMF tertentu - memakai aturan SMF yang sama dengan penyebut. */
    private String filterRanapSmfIn(String awal, String akhir, int smf) {
        return " and d.no_rawat in (select t.no_rawat from " + subqueryRanapSmf(awal, akhir, true)
             + " t where t.kode_smf = " + smf + ") ";
    }

    // =====================================================================
    //  Tab 1 — RL 3.19.2
    // =====================================================================
    private JPanel buatTab192() {
        siapkanPeriode();

        md192 = new DefaultTableModel(null, new Object[]{
            "Kode", "SMF/Instalasi/Poliklinik", "Nilai Dropdown Portal",
            "Jumlah Sampel Periode Survei", "Pasien Mendapatkan Antibiotik", "Persen (%)"
        }) {
            @Override public boolean isCellEditable(int b, int k) { return false; }
        };
        tb192 = new widget.Table();
        tb192.setModel(md192);
        aturKolom(tb192, new int[]{45, 260, 130, 190, 200, 80});

        lblSampel192 = lbl("");

        widget.panelisi atas = barisAtas();
        atas.add(lbl("Periode :"));
        atas.add(Tgl1);
        atas.add(lbl("s.d."));
        atas.add(Tgl2);
        atas.add(lbl("   Jenis Perawatan :"));
        atas.add(cmbRawat);
        atas.add(btn("Tampilkan", 95, (ActionEvent e) -> tampil192()));
        atas.add(btn("Salin ke Clipboard", 135, (ActionEvent e) -> salin(md192)));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra192()));

        widget.panelisi bawah = barisAtas();
        bawah.add(lblSampel192);

        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tb192);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(kepala("RL 3.19.2  Penggunaan Antibiotik", atas), BorderLayout.NORTH);
        isi.add(sc, BorderLayout.CENTER);
        isi.add(bawah, BorderLayout.SOUTH);
        return isi;
    }

    /** Sampel = kunjungan pada periode; pembilang = kunjungan yang menerima
     *  antibiotik sistemik (J01) menurut detail_pemberian_obat. */
    private void tampil192() {
        md192.setRowCount(0);
        boolean ranap = ranap();
        String sql = ranap
            ? "select s.kode_smf, s.nama_smf, s.id_portal, "
            + "  count(distinct t.no_rawat) sampel, "
            + "  count(distinct case when ab.no_rawat is not null then t.no_rawat end) dapat "
            + "from " + subqueryRanapSmf(tgl(Tgl1), tgl(Tgl2), false) + " t "
            + "inner join ppra_smf s on s.kode_smf=t.kode_smf "
            + "left join (select t2.no_rawat from " + subqueryRanapSmf(tgl(Tgl1), tgl(Tgl2), false) + " t2 "
            + "           where exists (select 1 from detail_pemberian_obat d inner join ppra_antibiotik a on a.kode_brng=d.kode_brng "
            + "                         where d.no_rawat=t2.no_rawat and a.atc_code like 'J01%')) ab on ab.no_rawat=t.no_rawat "
            + "group by s.kode_smf, s.nama_smf, s.id_portal order by s.kode_smf"
            : "select s.kode_smf, s.nama_smf, s.id_portal, "
            + "  count(distinct rp.no_rawat) sampel, "
            + "  count(distinct case when ab.no_rawat is not null then rp.no_rawat end) dapat "
            + "from reg_periksa rp "
            + "left join ppra_smf_mapping m on m.jenis='Ralan' and m.kode_ref=rp.kd_poli "
            + "inner join ppra_smf s on s.kode_smf=ifnull(m.kode_smf,7) "
            + "left join (select rp2.no_rawat from reg_periksa rp2 "
            + "           where rp2.status_lanjut='Ralan' and rp2.tgl_registrasi between ? and ? "
            + "             and exists (select 1 from detail_pemberian_obat d inner join ppra_antibiotik a on a.kode_brng=d.kode_brng "
            + "                         where d.no_rawat=rp2.no_rawat and a.atc_code like 'J01%')) ab on ab.no_rawat=rp.no_rawat "
            + "where rp.status_lanjut='Ralan' and rp.tgl_registrasi between ? and ? "
            + filterIgd("rp")
            + "group by s.kode_smf, s.nama_smf, s.id_portal order by s.kode_smf";

        int totS = 0, totD = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            if (!ranap) {
                p.setString(1, tgl(Tgl1));
                p.setString(2, tgl(Tgl2));
                p.setString(3, tgl(Tgl1));
                p.setString(4, tgl(Tgl2));
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    int s = r.getInt("sampel"), d = r.getInt("dapat");
                    totS += s; totD += d;
                    md192.addRow(new Object[]{
                        r.getInt("kode_smf"), r.getString("nama_smf"), r.getInt("id_portal"),
                        s, d, s == 0 ? "0,00" : String.format("%.2f", 100.0 * d / s)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        lblSampel192.setText("Total sampel " + totS + " kunjungan, mendapat antibiotik " + totD
                + (totS == 0 ? "" : String.format(" (%.2f%%)", 100.0 * totD / totS))
                + "   |   Pulmonologi & Lain-lain sama-sama bernilai 6 di dropdown portal, jadi digabung saat entri.");
    }

    // =====================================================================
    //  Tab 2 — RL 3.19.3
    // =====================================================================
    private JPanel buatTab193() {
        md193 = new DefaultTableModel(null, new Object[]{
            "Kode ATC", "Nama Antibiotik", "DDD WHO (g)", "Jumlah Unit",
            "Total Gram", "Total DDD", "DDD per 100 hari-rawat / 1000 kunjungan"
        }) {
            @Override public boolean isCellEditable(int b, int k) { return false; }
        };
        tb193 = new widget.Table();
        tb193.setModel(md193);
        aturKolom(tb193, new int[]{80, 190, 90, 100, 100, 100, 260});

        lblPenyebut193 = lbl("");
        lblCakupan193 = lbl("");
        lblInfoRawat193 = lbl("   (Jenis Perawatan: " + cmbRawat.getSelectedItem().toString() + ")");

        cmbSmf193 = new widget.ComboBox();
        cmbSmf193.setPreferredSize(new Dimension(220, 23));
        updatePilihanSmf193();
        cmbSmf193.addActionListener((ActionEvent e) -> tampil193());

        widget.panelisi atas = barisAtas();
        atas.add(lbl("Spesialis :"));
        atas.add(cmbSmf193);
        atas.add(lblInfoRawat193);
        atas.add(btn("Tampilkan", 95, (ActionEvent e) -> tampil193()));
        atas.add(btn("Salin ke Clipboard", 135, (ActionEvent e) -> salin(md193)));
        atas.add(btn("Kirim ke PPRA Kemenkes", 175, (ActionEvent e) -> kirimPpra193()));

        widget.panelisi bawah = new widget.panelisi();
        bawah.setLayout(new java.awt.GridLayout(2, 1));
        bawah.add(lblPenyebut193);
        bawah.add(lblCakupan193);

        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tb193);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(kepala("RL 3.19.3  Kuantitas Penggunaan Antibiotik", atas), BorderLayout.NORTH);
        isi.add(sc, BorderLayout.CENTER);
        isi.add(bawah, BorderLayout.SOUTH);
        return isi;
    }

    private void tampil193() {
        md193.setRowCount(0);
        boolean ranap = ranap();
        int smf = smfTerpilih193();
        double penyebut = 0;
        String namaPenyebut;

        // Penyebut: ranap = hari rawat, ralan = kunjungan (portal memakai per 1000 kunjungan)
        if (ranap) {
            String sqlPenyebut = "select ifnull(sum(t.hari), 0) from " + subqueryRanapSmf(tgl(Tgl1), tgl(Tgl2), true) + " t"
                + (smf > 0 ? " where t.kode_smf = " + smf : "");
            try (PreparedStatement p = koneksi.prepareStatement(sqlPenyebut)) {
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) penyebut = r.getDouble(1);
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        } else {
            String sqlPenyebut = smf > 0
                ? "select count(*) from reg_periksa rp left join ppra_smf_mapping m on m.jenis='Ralan' and m.kode_ref=rp.kd_poli "
                + "where rp.status_lanjut='Ralan' and rp.tgl_registrasi between ? and ? and ifnull(m.kode_smf, 7) = ?" + filterIgd("rp")
                : "select count(*) from reg_periksa rp where rp.status_lanjut='Ralan' and rp.tgl_registrasi between ? and ?" + filterIgd("rp");
            try (PreparedStatement p = koneksi.prepareStatement(sqlPenyebut)) {
                p.setString(1, tgl(Tgl1));
                p.setString(2, tgl(Tgl2));
                if (smf > 0) p.setInt(3, smf);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) penyebut = r.getDouble(1);
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }

        if (smf > 0) {
            namaPenyebut = (ranap ? "hari rawat inap - " : (igd() ? "kunjungan IGD - " : "kunjungan rawat jalan - ")) + namaSmfTerpilih193();
        } else {
            namaPenyebut = ranap ? "hari rawat inap" : (igd() ? "kunjungan IGD" : "kunjungan rawat jalan");
        }
        terakhirPenyebut193 = penyebut;

        String sql;
        if (!ranap) {
            sql = "select a.atc_code, min(a.nama_antibiotik) nm, min(a.ddd_who) ddd, "
                + "  sum(d.jml) unit, sum(d.jml*a.kekuatan_gram) gram "
                + "from detail_pemberian_obat d "
                + "inner join ppra_antibiotik a on a.kode_brng=d.kode_brng "
                + "inner join reg_periksa rp on rp.no_rawat=d.no_rawat "
                + (smf > 0 ? "left join ppra_smf_mapping m on m.jenis='Ralan' and m.kode_ref=rp.kd_poli " : "")
                + "where a.atc_code like 'J01%' and a.kekuatan_gram is not null and a.ddd_who is not null "
                + "  and rp.status_lanjut='Ralan' and d.tgl_perawatan between ? and ? "
                + (smf > 0 ? "  and ifnull(m.kode_smf, 7) = ? " : "")
                + filterIgd("rp")
                + "group by a.atc_code order by (sum(d.jml*a.kekuatan_gram)/min(a.ddd_who)) desc";
        } else {
            sql = "select a.atc_code, min(a.nama_antibiotik) nm, min(a.ddd_who) ddd, "
                + "  sum(d.jml) unit, sum(d.jml*a.kekuatan_gram) gram "
                + "from detail_pemberian_obat d "
                + "inner join ppra_antibiotik a on a.kode_brng=d.kode_brng "
                + "inner join reg_periksa rp on rp.no_rawat=d.no_rawat "
                + "where a.atc_code like 'J01%' and a.kekuatan_gram is not null and a.ddd_who is not null "
                + "  and rp.status_lanjut='Ranap' and d.tgl_perawatan between ? and ? "
                + (smf > 0 ? filterRanapSmfIn(tgl(Tgl1), tgl(Tgl2), smf) : "")
                + "group by a.atc_code order by (sum(d.jml*a.kekuatan_gram)/min(a.ddd_who)) desc";
        }

        double totalDdd = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, tgl(Tgl1));
            p.setString(2, tgl(Tgl2));
            if (smf > 0 && !ranap) {
                p.setInt(3, smf);
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    double gram = r.getDouble("gram"), ddd = r.getDouble("ddd");
                    double tddd = ddd == 0 ? 0 : gram / ddd;
                    totalDdd += tddd;
                    double per = penyebut == 0 ? 0
                            : (ranap ? tddd / penyebut * 100 : tddd / penyebut * 1000);
                    md193.addRow(new Object[]{
                        r.getString("atc_code"), r.getString("nm"),
                        String.format("%.4f", ddd), String.format("%.1f", r.getDouble("unit")),
                        String.format("%.2f", gram), String.format("%.2f", tddd),
                        String.format("%.2f", per)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        double totalPer = penyebut == 0 ? 0 : (ranap ? totalDdd / penyebut * 100 : totalDdd / penyebut * 1000);
        lblPenyebut193.setText(String.format(
                "Penyebut: %.0f %s   |   Total DDD %.2f   |   Total %s = %.2f",
                penyebut, namaPenyebut, totalDdd,
                ranap ? "DDD/100 hari-rawat" : "DDD/1000 kunjungan", totalPer));

        // Cakupan: berapa pemberian yang tidak ikut terhitung karena kekuatan/DDD kosong
        int terhitung = 0, terlewat = 0;
        String sqlCakupan;
        if (!ranap) {
            sqlCakupan = "select if(a.kekuatan_gram is null or a.ddd_who is null,'x','v') sts, count(*) n "
                + "from detail_pemberian_obat d inner join ppra_antibiotik a on a.kode_brng=d.kode_brng "
                + "inner join reg_periksa rp on rp.no_rawat=d.no_rawat "
                + (smf > 0 ? "left join ppra_smf_mapping m on m.jenis='Ralan' and m.kode_ref=rp.kd_poli " : "")
                + "where a.atc_code like 'J01%' and rp.status_lanjut='Ralan' and d.tgl_perawatan between ? and ? "
                + (smf > 0 ? " and ifnull(m.kode_smf,7) = ? " : "")
                + filterIgd("rp") + " group by 1";
        } else {
            sqlCakupan = "select if(a.kekuatan_gram is null or a.ddd_who is null,'x','v') sts, count(*) n "
                + "from detail_pemberian_obat d inner join ppra_antibiotik a on a.kode_brng=d.kode_brng "
                + "inner join reg_periksa rp on rp.no_rawat=d.no_rawat "
                + "where a.atc_code like 'J01%' and rp.status_lanjut='Ranap' and d.tgl_perawatan between ? and ? "
                + (smf > 0 ? filterRanapSmfIn(tgl(Tgl1), tgl(Tgl2), smf) : "")
                + " group by 1";
        }

        try (PreparedStatement p = koneksi.prepareStatement(sqlCakupan)) {
            p.setString(1, tgl(Tgl1));
            p.setString(2, tgl(Tgl2));
            if (smf > 0 && !ranap) {
                p.setInt(3, smf);
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    if (r.getString(1).equals("v")) terhitung = r.getInt(2); else terlewat = r.getInt(2);
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        lblCakupan193.setText(terlewat == 0
                ? "Cakupan: seluruh " + terhitung + " pemberian antibiotik terhitung."
                : "Cakupan: " + terhitung + " pemberian terhitung, " + terlewat
                  + " TERLEWAT karena kekuatan/DDD belum diisi — lengkapi di tab Daftar Antibiotik & DDD.");
    }

    // =====================================================================
    //  Tab 3 — Pemetaan SMF
    // =====================================================================
    private JPanel buatTabSmf() {
        mdSmf = new DefaultTableModel(null, new Object[]{
            "Jenis", "Kode", "Nama Poli / Bangsal", "Kode SMF", "Nama SMF"
        }) {
            @Override public boolean isCellEditable(int b, int k) { return k == 3; }
        };
        tbSmf = new widget.Table();
        tbSmf.setModel(mdSmf);
        aturKolom(tbSmf, new int[]{70, 80, 300, 80, 280});

        widget.panelisi atas = barisAtas();
        atas.add(lbl("Ubah kolom Kode SMF, lalu Simpan.  1=Penyakit Dalam 2=Bedah 3=Anak "
                + "4=Kandungan 5=ICU 6=Pulmonologi 7=Lain-lain 8=IGD"));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpanSmf()));
        atas.add(btn("Muat Ulang", 100, (ActionEvent e) -> muatSmf()));

        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tbSmf);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(kepala("Pemetaan SMF  —  master pendukung RL 3.19.2 & RL 3.19.3", atas), BorderLayout.NORTH);
        isi.add(sc, BorderLayout.CENTER);
        return isi;
    }

    private void muatSmf() {
        mdSmf.setRowCount(0);
        String sql = "select m.jenis, m.kode_ref, ifnull(m.nama_ref,'') nama_ref, m.kode_smf, s.nama_smf "
                   + "from ppra_smf_mapping m inner join ppra_smf s on s.kode_smf=m.kode_smf "
                   + "order by m.jenis, s.kode_smf, m.kode_ref";
        try (Statement st = koneksi.createStatement(); ResultSet r = st.executeQuery(sql)) {
            while (r.next()) {
                mdSmf.addRow(new Object[]{r.getString(1), r.getString(2), r.getString(3),
                                          r.getInt(4), r.getString(5)});
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void simpanSmf() {
        int ubah = 0, tolak = 0;
        java.util.Set<Integer> kodeValid = new java.util.HashSet<>();
        try (Statement st = koneksi.createStatement(); ResultSet r = st.executeQuery("select kode_smf from ppra_smf")) {
            while (r.next()) kodeValid.add(r.getInt(1));
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        try (PreparedStatement p = koneksi.prepareStatement(
                "update ppra_smf_mapping set kode_smf=? where jenis=? and kode_ref=?")) {
            for (int i = 0; i < mdSmf.getRowCount(); i++) {
                int kode;
                try {
                    kode = Integer.parseInt(mdSmf.getValueAt(i, 3).toString().trim());
                } catch (Exception ex) { tolak++; continue; }
                if (!kodeValid.contains(kode)) { tolak++; continue; }
                p.setInt(1, kode);
                p.setString(2, mdSmf.getValueAt(i, 0).toString());
                p.setString(3, mdSmf.getValueAt(i, 1).toString());
                ubah += p.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        muatSmf();
        JOptionPane.showMessageDialog(this, "Tersimpan. " + ubah + " baris diperbarui."
                + (tolak > 0 ? "\n" + tolak + " baris dilewati karena Kode SMF tidak ada di master ppra_smf." : ""));
    }

    // =====================================================================
    //  Tab 4 — Daftar Antibiotik & DDD
    // =====================================================================
    private JPanel buatTabAb() {
        mdAb = new DefaultTableModel(null, new Object[]{
            "Kode Barang", "Nama Barang", "ATC", "Nama Antibiotik",
            "Kekuatan (gram/satuan)", "DDD WHO (g)", "Rute", "Status", "Keterangan"
        }) {
            @Override public boolean isCellEditable(int b, int k) { return k == 4 || k == 5 || k == 7; }
        };
        tbAb = new widget.Table();
        tbAb.setModel(mdAb);
        aturKolom(tbAb, new int[]{95, 300, 70, 130, 145, 100, 50, 110, 300});

        widget.panelisi atas = barisAtas();
        atas.add(lbl("Isi Kekuatan & DDD yang kosong, ubah Status jadi Terverifikasi "
                + "(baris Terverifikasi tidak akan ditimpa pengisi otomatis)."));
        atas.add(btn("Simpan", 90, (ActionEvent e) -> simpanAb()));
        atas.add(btn("Muat Ulang", 100, (ActionEvent e) -> muatAb()));

        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tbAb);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(kepala("Daftar Antibiotik & DDD  —  master pendukung RL 3.19.3", atas), BorderLayout.NORTH);
        isi.add(sc, BorderLayout.CENTER);
        return isi;
    }

    private void muatAb() {
        mdAb.setRowCount(0);
        String sql = "select kode_brng, ifnull(nama_brng,'') nama_brng, atc_code, ifnull(nama_antibiotik,'') nm, "
                   + "  ifnull(kekuatan_gram,'') kek, ifnull(ddd_who,'') ddd, rute, status, ifnull(keterangan,'') ket "
                   + "from ppra_antibiotik order by field(status,'Perlu Koreksi','Otomatis','Terverifikasi','Dikecualikan'), atc_code, nama_brng";
        try (Statement st = koneksi.createStatement(); ResultSet r = st.executeQuery(sql)) {
            while (r.next()) {
                mdAb.addRow(new Object[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4),
                                         r.getString(5), r.getString(6), r.getString(7),
                                         r.getString(8), r.getString(9)});
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void simpanAb() {
        int ubah = 0, tolak = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "update ppra_antibiotik set kekuatan_gram=?, ddd_who=?, status=? where kode_brng=?")) {
            for (int i = 0; i < mdAb.getRowCount(); i++) {
                Double kek = angka(mdAb.getValueAt(i, 4));
                Double ddd = angka(mdAb.getValueAt(i, 5));
                String sts = mdAb.getValueAt(i, 7).toString().trim();
                if (!sts.equals("Otomatis") && !sts.equals("Terverifikasi")
                        && !sts.equals("Perlu Koreksi") && !sts.equals("Dikecualikan")) { tolak++; continue; }
                if (kek == null) p.setNull(1, java.sql.Types.DECIMAL); else p.setDouble(1, kek);
                if (ddd == null) p.setNull(2, java.sql.Types.DECIMAL); else p.setDouble(2, ddd);
                p.setString(3, sts);
                p.setString(4, mdAb.getValueAt(i, 0).toString());
                ubah += p.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        muatAb();
        JOptionPane.showMessageDialog(this, "Tersimpan. " + ubah + " baris diperbarui."
                + (tolak > 0 ? "\n" + tolak + " baris dilewati karena Status tidak dikenal." : ""));
    }

    /** Terima "0.5" maupun "0,5"; kosong -> null. */
    private Double angka(Object o) {
        String s = o == null ? "" : o.toString().trim().replace(',', '.');
        if (s.isEmpty()) return null;
        try { return Double.valueOf(s); } catch (Exception e) { return null; }
    }

    // =====================================================================
    //  Bantu: salin isi tabel supaya gampang ditempel ke portal
    // =====================================================================
    private void salin(DefaultTableModel m) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < m.getColumnCount(); k++) {
            sb.append(m.getColumnName(k)).append(k == m.getColumnCount() - 1 ? "\n" : "\t");
        }
        for (int i = 0; i < m.getRowCount(); i++) {
            for (int k = 0; k < m.getColumnCount(); k++) {
                Object v = m.getValueAt(i, k);
                sb.append(v == null ? "" : v.toString()).append(k == m.getColumnCount() - 1 ? "\n" : "\t");
            }
        }
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, m.getRowCount() + " baris disalin.\n"
                + "Tinggal tempel ke Excel atau ketik ulang ke portal PPRA.");
    }

    private static final java.text.SimpleDateFormat FTampil = new java.text.SimpleDateFormat("dd-MM-yyyy");

    /**
     * Portal menerima data per triwulan, tapi periode bawaan form adalah 1 Januari s.d. hari ini.
     * Dulu triwulan diambil dari tanggal awal saja, sehingga data setengah tahun bisa terkirim
     * sebagai "Triwulan 1". Kirim hanya diizinkan bila kedua tanggal berada di triwulan yang sama.
     */
    private int[] triwulanPeriode() {
        Calendar a = Calendar.getInstance(), z = Calendar.getInstance();
        a.setTime(Tgl1.getDate());
        z.setTime(Tgl2.getDate());
        int twA = a.get(Calendar.MONTH) / 3 + 1, twZ = z.get(Calendar.MONTH) / 3 + 1;
        if (a.get(Calendar.YEAR) != z.get(Calendar.YEAR) || twA != twZ || a.after(z)) {
            JOptionPane.showMessageDialog(this,
                "Periode " + FTampil.format(Tgl1.getDate()) + " s.d. " + FTampil.format(Tgl2.getDate())
                + " melintasi lebih dari satu triwulan.\n\n"
                + "Portal PPRA menerima data per triwulan. Atur periode di dalam satu triwulan\n"
                + "(mis. 01-01 s.d. 31-03), klik Tampilkan, lalu kirim lagi.",
                "Periode Tidak Sesuai Triwulan", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new int[]{a.get(Calendar.YEAR), twA};
    }

    private void kirimPpra192() {
        if (igd()) {
            JOptionPane.showMessageDialog(this, "Mode IGD hanya untuk melihat data internal IGD.\nPortal PPRA Kemenkes hanya menerima pelaporan untuk Rawat Jalan dan Rawat Inap.", "Mode IGD", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (md192.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel RL 3.19.2 kosong. Silakan klik Tampilkan terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int[] tw = triwulanPeriode();
        if (tw == null) return;
        int tahun = tw[0], triwulan = tw[1];
        String rawat = cmbRawat.getSelectedItem().toString();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Kirim data RL 3.19.2 ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + tahun + "\n"
            + "• Triwulan: " + triwulan + "  (periode " + FTampil.format(Tgl1.getDate()) + " s.d. " + FTampil.format(Tgl2.getDate()) + ")\n"
            + "• Jenis Perawatan: " + rawat + "\n"
            + "• Jumlah Unit SMF: " + md192.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.2",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md192.getRowCount(); i++) {
            rows.add(new Object[]{
                md192.getValueAt(i, 0),
                md192.getValueAt(i, 1),
                md192.getValueAt(i, 2),
                md192.getValueAt(i, 3),
                md192.getValueAt(i, 4)
            });
        }

        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim192(tahun, triwulan, rawat, rows);
        if (res.ok) {
            JOptionPane.showMessageDialog(this,
                "Laporan RL 3.19.2 BERHASIL diproses via Bot PPRA Kemenkes!\n\n"
                + "Hasil: " + res.message + "\n\nRespon Portal:\n" + res.detail,
                "Sukses Bridging PPRA",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengirim RL 3.19.2 ke Portal PPRA Kemenkes:\n\n"
                + res.message + "\n\nDetail: " + res.detail,
                "Gagal Bridging PPRA",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void kirimPpra193() {
        if (igd()) {
            JOptionPane.showMessageDialog(this, "Mode IGD hanya untuk melihat data internal IGD.\nPortal PPRA Kemenkes hanya menerima pelaporan untuk Rawat Jalan dan Rawat Inap.", "Mode IGD", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (md193.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel RL 3.19.3 kosong. Silakan klik Tampilkan terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int[] tw = triwulanPeriode();
        if (tw == null) return;
        int tahun = tw[0], triwulan = tw[1];
        String rawat = cmbRawat.getSelectedItem().toString();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Kirim data RL 3.19.3 (Kuantitas DDD) ke Portal PPRA Kemenkes RI?\n\n"
            + "• Tahun: " + tahun + "\n"
            + "• Triwulan: " + triwulan + "  (periode " + FTampil.format(Tgl1.getDate()) + " s.d. " + FTampil.format(Tgl2.getDate()) + ")\n"
            + "• Jenis Perawatan: " + rawat + "\n"
            + (smfTerpilih193() > 0 ? "• Filter Spesialis: " + namaSmfTerpilih193() + "\n  (Catatan: Pastikan Portal Kemenkes memang memerlukan per spesialis atau pilih 'Semua Spesialis' untuk total RS)\n" : "• Filter Spesialis: Semua Spesialis (Total RS)\n")
            + "• Total Hari Rawat / Kunjungan: " + String.format("%.0f", terakhirPenyebut193) + "\n"
            + "• Jumlah Antibiotik (ATC): " + md193.getRowCount() + " baris\n\n"
            + "Data akan otomatis dikirim via Bot Bridging PPRA ke Kemenkes.",
            "Konfirmasi Pengiriman PPRA 3.19.3",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (int i = 0; i < md193.getRowCount(); i++) {
            rows.add(new Object[]{
                md193.getValueAt(i, 0),
                md193.getValueAt(i, 1),
                md193.getValueAt(i, 2),
                md193.getValueAt(i, 3),
                md193.getValueAt(i, 4),
                md193.getValueAt(i, 5),
                md193.getValueAt(i, 6)
            });
        }

        PpraBotBridge.BridgeResult res = PpraBotBridge.kirim193(tahun, triwulan, rawat, terakhirPenyebut193, rows);
        if (res.ok) {
            JOptionPane.showMessageDialog(this,
                "Laporan RL 3.19.3 BERHASIL diproses via Bot PPRA Kemenkes!\n\n"
                + "Hasil: " + res.message + "\n\nRespon Portal:\n" + res.detail,
                "Sukses Bridging PPRA",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengirim RL 3.19.3 ke Portal PPRA Kemenkes:\n\n"
                + res.message + "\n\nDetail: " + res.detail,
                "Gagal Bridging PPRA",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void isCek() {
        // Seluruh isi form bersifat baca/master; kendali akses ada di tombol frmUtama.
    }
}
