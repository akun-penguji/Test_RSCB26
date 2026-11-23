/*
  Tanda Tangan Elektronik (TTE) SatuSehat.

  Acuan: Postman collection resmi Kemenkes "00. TTE versi 21072026" (21 Juli 2026)
  dan Panduan Teknis TTE. Rinciannya di
  docs/SatuSehat_TTE_panduan.md.

  Alur:
    1. SIMRS POST Bundle transaction berisi Provenance (signature kosong) + Task
       -> jalur "01b. Bundle Buat TTE" pada collection, dipilih karena Task bisa
          merujuk Provenance lewat urn:uuid tanpa menunggu ID-nya lebih dulu.
    2. Platform mengembalikan Provenance ID dan Task UUID, disimpan di
       satu_sehat_tte.
    3. Dokter memindai QR lewat SATUSEHAT Mobile, lalu SSM yang melakukan
       PUT /Provenance/{id} berisi signature[]. SIMRS tidak menulis apa pun di
       tahap ini.
    4. SIMRS GET Provenance/{id}; bila signature[] sudah terisi -> status Signed.

  Pembuatan QR: isi QR = "Task/{id}/{epochDetik}" (dipastikan dari pindai QR
  nyata, mis. Task/33bbfc99-86d8-49f4-a63a-08727e074e52/1784601977). Task UUID
  tetap; segmen timestamp di-refresh tiap 10 detik. Tombol "Lihat Task" tetap ada
  untuk menampilkan Task JSON, sesuai Flow 2b.

  Berbeda dari TTE BSrE lokal (akun_tte/status_tte/berkas_tte) yang
  menandatangani berkas PDF; yang ini menandatangani resource FHIR.
 */

package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.akses;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Form pembuatan dan pemantauan TTE SatuSehat.
 *
 * @author Salim Mulyana
 */
public final class SatuSehatKirimTTE extends javax.swing.JDialog {

    private final DefaultTableModel tabMode, tabPasien;
    private String noRawatAktif = "";
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private String link = "", json = "";
    private int i = 0;

    /** Kolom tabel. Kolom ID sengaja ditampilkan supaya bisa ditelusuri saat uji coba. */
    private static final String[] KOLOM = {
        "P", "No.Rawat", "No.RM", "Nama Pasien", "NIK Pasien", "Jenis Dokumen",
        "ID Resource", "Kode DPJP", "Practitioner (DPJP)", "NIK DPJP", "ID Encounter",
        "ID Provenance", "ID Task", "Status"
    };
    private static final int COL_PILIH = 0, COL_NORAWAT = 1, COL_NIKPASIEN = 4,
            COL_JENIS = 5, COL_COMPOSITION = 6, COL_NAMADPJP = 8, COL_NIKDPJP = 9,
            COL_ENCOUNTER = 10, COL_PROVENANCE = 11, COL_TASK = 12, COL_STATUS = 13;

    /** Kolom kanan (dokumen) yang disembunyikan -- datanya dipakai kode, tapi identitas
     *  pasien sudah tampil di tabel kiri, jadi tak perlu diulang di kanan. Nama DPJP
     *  (Practitioner penandatangan) sengaja TETAP tampil karena bagian dari Provenance. */
    private static final int[] KOLOM_SEMBUNYI = {
        COL_NORAWAT, 2, 3, COL_NIKPASIEN, 7, COL_NIKDPJP, COL_ENCOUNTER };

    /** Tabel kiri: pasien yang encounter-nya sudah terkirim ke SatuSehat. */
    private static final String[] KOLOM_PASIEN = { "No.Rawat", "No.RM", "Nama Pasien", "Nama DPJP" };
    private static final int PS_NORAWAT = 0;

    public SatuSehatKirimTTE(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 2);
        setSize(1000, 674);

        tabMode = new DefaultTableModel(null, KOLOM) {
            @Override public boolean isCellEditable(int row, int col) { return col == COL_PILIH; }
            @Override public Class getColumnClass(int col) {
                return col == COL_PILIH ? java.lang.Boolean.class : java.lang.String.class;
            }
        };
        tbTTE.setModel(tabMode);
        tbTTE.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTTE.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Kanan hanya menonjolkan dokumen + Provenance; identitas pasien di tabel kiri.
        // Kolom UUID (Resource/Provenance/Task) diciutkan — nilai penuh muncul lewat tooltip.
        int[] lebar = {20, 105, 60, 150, 110, 165, 115, 70, 160, 110, 210, 115, 115, 80};
        for (i = 0; i < KOLOM.length; i++) {
            TableColumn column = tbTTE.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebar[i]);
        }
        for (int c : KOLOM_SEMBUNYI) {
            TableColumn column = tbTTE.getColumnModel().getColumn(c);
            column.setMinWidth(0); column.setMaxWidth(0); column.setPreferredWidth(0);
        }
        tbTTE.setDefaultRenderer(Object.class, new WarnaTable());
        // Kolom Status diberi warna: Signed hijau, Ditolak/Gagal merah, Requested biru.
        tbTTE.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                java.awt.Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v == null ? "" : v.toString();
                java.awt.Color fg = new java.awt.Color(90, 90, 90);
                if ("Signed".equals(s)) fg = new java.awt.Color(0, 140, 70);
                else if ("Ditolak".equals(s) || "Gagal".equals(s)) fg = new java.awt.Color(200, 50, 40);
                else if ("Requested".equals(s)) fg = new java.awt.Color(30, 110, 200);
                else if ("Dibatalkan".equals(s)) fg = new java.awt.Color(150, 110, 20);
                if (!sel) comp.setForeground(fg);
                setFont(getFont().deriveFont(java.awt.Font.BOLD));
                return comp;
            }
        });
        // Kolom UUID diciutkan; tooltip menampilkan nilai penuh saat kursor di atasnya.
        javax.swing.table.DefaultTableCellRenderer rUuid = new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                java.awt.Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v == null ? "" : v.toString();
                setToolTipText(s.isEmpty() ? null : s);
                return comp;
            }
        };
        tbTTE.getColumnModel().getColumn(COL_COMPOSITION).setCellRenderer(rUuid);
        tbTTE.getColumnModel().getColumn(COL_PROVENANCE).setCellRenderer(rUuid);
        tbTTE.getColumnModel().getColumn(COL_TASK).setCellRenderer(rUuid);

        // Tabel kiri: daftar pasien. Klik -> muat dokumen/Provenance-nya ke kanan.
        tabPasien = new DefaultTableModel(null, KOLOM_PASIEN) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tbPasien.setModel(tabPasien);
        // AUTO_RESIZE_ALL_COLUMNS: kolom mengisi lebar panel kiri, jadi "Nama DPJP"
        // tidak terpotong dan tak ada scrollbar horizontal. Lebar di bawah jadi rasio.
        tbPasien.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] lebarP = {95, 55, 140, 110};
        for (i = 0; i < KOLOM_PASIEN.length; i++) {
            tbPasien.getColumnModel().getColumn(i).setPreferredWidth(lebarP[i]);
        }
        tbPasien.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbPasien.setDefaultRenderer(Object.class, new WarnaTable());
        tbPasien.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tbPasien.getSelectedRow();
            tampilDokumen(row < 0 ? "" : tbPasien.getValueAt(row, PS_NORAWAT).toString());
        });

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampilPasien(); }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampilPasien(); }
                @Override public void changedUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampilPasien(); }
            });
        }

        // Rentang default sebulan terakhir. Form bridging lain membiarkan kedua
        // pemilih tanggal di hari yang sama sehingga daftar sering tampak kosong;
        // dokumen yang siap di-TTE umumnya baru dibuat beberapa hari sebelumnya.
        java.util.Calendar kal = java.util.Calendar.getInstance();
        DTPCari2.setDate(kal.getTime());
        kal.add(java.util.Calendar.MONTH, -1);
        DTPCari1.setDate(kal.getTime());

        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notif URL FHIR : " + e);
        }
    }

    /**
     * Gerbang hak akses. Sengaja TIDAK membuat kolom hak akses baru -- menumpang
     * satu_sehat_kirim_encounter seperti SatuSehatKirimSPRI, karena TTE hanya
     * berguna bagi petugas yang memang mengirim data ke SatuSehat.
     */
    public void isCek() {
        boolean boleh = akses.getsatu_sehat_kirim_encounter();
        BtnBuat.setEnabled(boleh);
        BtnRefresh.setEnabled(boleh);
        BtnUpdateStatus.setEnabled(boleh);
        BtnSinkron.setEnabled(boleh);
        // Aksi per-baris (Task/Provenance/Verifikasi/Preview/Cetak/QR/Batalkan/Riwayat)
        // pindah ke menu klik-kanan. Batalkan (menulis) dijaga hak akses di batalkanTTE().
    }

    // ==================================================================== data

    /**
     * Union semua dokumen yang bisa di-TTE, dinormalisasi ke (no_rawat, jenis,
     * id_composition, tipe). Dipakai kiri (pasien) dan kanan (dokumen).
     *
     * `id_composition` = id resource apa pun (Composition/ServiceRequest/
     * DiagnosticReport); `tipe` = tipe resource untuk Provenance.target. Resume
     * Medis = Composition RME (idRme), jadi tidak ada baris terpisah. Lab/Rad
     * di-key `noorder` sehingga dipetakan ke no_rawat lewat permintaan_lab/
     * permintaan_radiologi (group by noorder supaya 1 no_rawat per order).
     * Resep (DocumentReference) belum ada sender-nya, jadi belum dimasukkan.
     */
    private static final String SUMBER_DOKUMEN =
        "( select s.no_rawat,'RME Rawat Inap' as jenis, s.id_composition, 'Composition' as tipe, rpi.kd_dokter as kd_dokter_doc "+
        "     from satu_sehat_rme_rawat_inap s "+
        "     left join resume_pasien_ranap rpi on rpi.no_rawat=s.no_rawat "+
        "     where ifnull(s.id_composition,'')<>'' "+
        "  union all select s.no_rawat,'RME Rawat Jalan', s.id_composition, 'Composition', rp.kd_dokter "+
        "     from satu_sehat_rme_rawat_jalan s "+
        "     left join resume_pasien rp on rp.no_rawat=s.no_rawat "+
        "     where ifnull(s.id_composition,'')<>'' "+
        "  union all select no_rawat,'SPRI', id_servicerequest, 'ServiceRequest', null from satu_sehat_spri where ifnull(id_servicerequest,'')<>'' "+
        "  union all select slo.no_rawat,'Laporan Operasi', slo.id_composition, 'Composition', loc.kddokterbedah "+
        "     from satu_sehat_laporan_operasi slo "+
        "     left join laporan_operasi_casemix loc on loc.no_rawat=slo.no_rawat and loc.tanggal=slo.tanggal "+
        "     where ifnull(slo.id_composition,'')<>'' "+
        "  union all select slt.no_rawat,'Laporan Tindakan', slt.id_composition, 'Composition', lt.kd_dokter "+
        "     from satu_sehat_laporan_tindakan slt "+
        "     left join laporan_tindakan lt on lt.no_rawat=slt.no_rawat and lt.tanggal=slt.tanggal "+
        "     where ifnull(slt.id_composition,'')<>'' "+
        "  union all select sra.no_rawat,'Laporan Anestesi', sra.id_composition, 'Composition', lan.dokter_anestesi "+
        "     from satu_sehat_resume_anestesi sra "+
        "     left join laporan_anestesi lan on lan.no_rawat=sra.no_rawat and lan.mulai=sra.tanggal "+
        "     where ifnull(sra.id_composition,'')<>'' "+
        "  union all select slp.no_rawat,'Laporan Persalinan', slp.id_composition, 'Composition', cp.kd_dokter "+
        "     from satu_sehat_laporan_persalinan slp "+
        "     left join catatan_persalinan cp on cp.no_rawat=slp.no_rawat "+
        "     where ifnull(slp.id_composition,'')<>'' "+
        "  union all select pl.no_rawat,'Expertise Lab', dl.id_diagnosticreport, 'DiagnosticReport', pxl.kd_dokter "+
        "     from satu_sehat_diagnosticreport_lab dl "+
        "     join (select noorder, no_rawat from permintaan_lab group by noorder) pl on pl.noorder=dl.noorder "+
        "     left join (select no_rawat, max(kd_dokter) kd_dokter from periksa_lab group by no_rawat) pxl on pxl.no_rawat=pl.no_rawat "+
        "     where ifnull(dl.id_diagnosticreport,'')<>'' "+
        "  union all select pr.no_rawat,'Expertise Radiologi', dr.id_diagnosticreport, 'DiagnosticReport', pxr.kd_dokter "+
        "     from satu_sehat_diagnosticreport_radiologi dr "+
        "     join (select noorder, no_rawat from permintaan_radiologi group by noorder) pr on pr.noorder=dr.noorder "+
        "     left join (select no_rawat, max(kd_dokter) kd_dokter from periksa_radiologi group by no_rawat) pxr on pxr.no_rawat=pr.no_rawat "+
        "     where ifnull(dr.id_diagnosticreport,'')<>'' )";

    /** Tipe resource FHIR target Provenance, diturunkan dari jenis dokumen. */
    private static String tipeResource(String jenis) {
        if ("SPRI".equals(jenis)) return "ServiceRequest";
        if (jenis != null && jenis.startsWith("Expertise")) return "DiagnosticReport";
        return "Composition"; // RME/Resume + semua Laporan
    }

    /**
     * Tabel kiri: pasien yang encounter-nya sudah terkirim DAN punya dokumen siap di-TTE.
     *
     * Sengaja inner join ke satu_sehat_encounter (id_encounter terisi) supaya tiap baris
     * pasti punya sesuatu untuk ditandatangani di panel kanan -- klik pasien tak akan
     * menghasilkan kanan yang kosong. Klik salah satu memanggil tampilDokumen().
     */
    private void tampilPasien() {
        Valid.tabelKosong(tabMode);
        Valid.tabelKosong(tabPasien);
        noRawatAktif = "";
        String cari = TCari.getText().trim();
        String sql =
            "select d.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, "+
            "ifnull(dokter.nm_dokter,'') as nm_dokter, max(reg_periksa.tgl_registrasi) as tgl "+
            "from "+SUMBER_DOKUMEN+" d "+
            "inner join reg_periksa on reg_periksa.no_rawat=d.no_rawat "+
            "inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "+
            "left join dokter on dokter.kd_dokter=reg_periksa.kd_dokter "+
            "inner join satu_sehat_encounter sse on sse.no_rawat=d.no_rawat and ifnull(sse.id_encounter,'')<>'' "+
            "where reg_periksa.tgl_registrasi between ? and ? "+
            (cari.isEmpty() ? "" : "and (d.no_rawat like ? or pasien.nm_pasien like ? or reg_periksa.no_rkm_medis like ? or dokter.nm_dokter like ?) ")+
            "group by d.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, nm_dokter "+
            "order by tgl desc";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            int n = 1;
            ps.setString(n++, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(n++, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            if (!cari.isEmpty()) {
                ps.setString(n++, "%" + cari + "%");
                ps.setString(n++, "%" + cari + "%");
                ps.setString(n++, "%" + cari + "%");
                ps.setString(n++, "%" + cari + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabPasien.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"), rs.getString("nm_dokter") });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif tampil pasien TTE : " + e);
        }
        LCount.setText("" + tabPasien.getRowCount());
    }

    /**
     * Tabel kanan: seluruh dokumen (Composition) satu pasien beserta Provenance/Task-nya.
     *
     * Left join ke dokter/pegawai supaya baris tidak hilang diam-diam saat DPJP belum
     * lengkap di master; kelengkapan diperiksa saat tombol Buat TTE ditekan.
     */
    private void tampilDokumen(String noRawat) {
        Valid.tabelKosong(tabMode);
        noRawatAktif = noRawat;
        if (noRawat == null || noRawat.isEmpty()) return;
        String sql =
            "select d.no_rawat, d.jenis, d.id_composition, "+
            "reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, "+
            "ifnull(nullif(d.kd_dokter_doc,''),reg_periksa.kd_dokter) as kd_dokter, ifnull(dokter.nm_dokter,'') as nm_dokter, "+
            "ifnull(pegawai.no_ktp,'') as ktpdokter, "+
            "ifnull(sse.id_encounter,'') as id_encounter, "+
            "ifnull(t.id_provenance,'') as id_provenance, ifnull(t.id_task,'') as id_task, "+
            "ifnull(t.status,'Belum') as status "+
            "from "+SUMBER_DOKUMEN+" d "+
            "inner join reg_periksa on reg_periksa.no_rawat=d.no_rawat "+
            "inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "+
            "left join dokter on dokter.kd_dokter=ifnull(nullif(d.kd_dokter_doc,''),reg_periksa.kd_dokter) "+
            "left join pegawai on pegawai.nik=ifnull(nullif(d.kd_dokter_doc,''),reg_periksa.kd_dokter) "+
            "left join satu_sehat_encounter sse on sse.no_rawat=d.no_rawat "+
            "left join satu_sehat_tte t on t.no_rawat=d.no_rawat and t.id_composition=d.id_composition "+
            "where d.no_rawat=? order by d.jenis";

        String filter = cmbFilterStatus == null ? "" : String.valueOf(cmbFilterStatus.getSelectedItem());
        boolean semuaStatus = filter.isEmpty() || filter.startsWith("--");
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    if (!semuaStatus && !filter.equals(status)) continue;   // filter Status
                    tabMode.addRow(new Object[]{ false,
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("no_ktp"), rs.getString("jenis"), rs.getString("id_composition"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("ktpdokter"),
                        rs.getString("id_encounter"), rs.getString("id_provenance"),
                        rs.getString("id_task"), status });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif tampil dokumen TTE : " + e);
        }
    }

    // ================================================================ payload

    private String isoSekarang() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
    }


    // ================================================================ tindakan

    /**
     * Membuat Provenance + Task untuk baris yang dicentang.
     *
     * Baris dikelompokkan per (No.Rawat + dokter): dokumen dari satu kunjungan
     * yang ditandatangani DOKTER YANG SAMA digabung menjadi SATU Task berisi
     * beberapa Provenance (varian "Task - Multiple Provenance"), sehingga dokter
     * cukup sekali pindai QR untuk menandatangani semuanya. Dokumen dengan dokter
     * berbeda (mis. beberapa Laporan Tindakan/Operasi beda operator dalam satu
     * kunjungan) otomatis menjadi Task terpisah agar Provenance-nya menunjuk
     * Practitioner yang benar. Encounter tetap sama dalam satu No.Rawat.
     */
    /**
     * Chaining signature untuk dokumen RME Rawat Inap: bila dpjp_ranap memuat >=2 DPJP,
     * dokumen ditandatangani berjenjang — Residen dulu, lalu DPJP Supervisi yang
     * Provenance-nya menautkan Provenance Residen (entity role=source). DPJP Supervisi =
     * DPJP utama kunjungan (reg_periksa.kd_dokter); Residen = DPJP lain di dpjp_ranap.
     *
     * Return true bila baris ini ditangani sebagai chaining (dan tak perlu diproses alur
     * biasa). Return false untuk selain RME Rawat Inap / dpjp_ranap {@literal <}2 / data tak lengkap.
     */
    private boolean prosesChaining(int row, StringBuilder jsonLog) {
        String jenis = tbTTE.getValueAt(row, COL_JENIS).toString();
        if (!"RME Rawat Inap".equals(jenis)) return false;
        if (!tbTTE.getValueAt(row, COL_TASK).toString().isEmpty()) return false;   // sudah diajukan
        String noRawat = tbTTE.getValueAt(row, COL_NORAWAT).toString();
        String idComp  = tbTTE.getValueAt(row, COL_COMPOSITION).toString();
        String idEnc   = tbTTE.getValueAt(row, COL_ENCOUNTER).toString();
        if (idComp.isEmpty() || idEnc.isEmpty()) return false;

        // Ambil DPJP dari dpjp_ranap: tandai mana yang DPJP utama kunjungan (supervisor).
        String kdRes = "", nmRes = "", ktpRes = "", kdSup = "", nmSup = "", ktpSup = "";
        try (PreparedStatement p = koneksi.prepareStatement(
                "select dr.kd_dokter, ifnull(dk.nm_dokter,'') nm, ifnull(pg.no_ktp,'') ktp, "
              + "(dr.kd_dokter = rp.kd_dokter) utama "
              + "from dpjp_ranap dr inner join reg_periksa rp on rp.no_rawat=dr.no_rawat "
              + "left join dokter dk on dk.kd_dokter=dr.kd_dokter "
              + "left join pegawai pg on pg.nik=dr.kd_dokter "
              + "where dr.no_rawat=? order by utama desc, dr.kd_dokter")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                java.util.List<String[]> list = new java.util.ArrayList<>();
                while (r.next()) list.add(new String[]{ r.getString(1), r.getString("nm"), r.getString("ktp"), r.getString("utama") });
                if (list.size() < 2) return false;   // bukan skenario chaining
                // Supervisor = DPJP utama (utama=1, di baris pertama karena order desc);
                // Residen = DPJP lain pertama yang bukan utama.
                String[] sup = list.get(0);
                String[] res = null;
                for (String[] d : list) { if (!"1".equals(d[3])) { res = d; break; } }
                if (res == null) res = list.get(1);   // jaga-jaga tak ada penanda utama
                kdSup = sup[0]; nmSup = sup[1]; ktpSup = sup[2];
                kdRes = res[0]; nmRes = res[1]; ktpRes = res[2];
            }
        } catch (Exception e) { System.out.println("Notif chaining dpjp_ranap : " + e); return false; }

        String idResiden = ktpRes.isEmpty() ? "" : cekViaSatuSehat.tampilIDParktisi(ktpRes);
        String idSupervisor = ktpSup.isEmpty() ? "" : cekViaSatuSehat.tampilIDParktisi(ktpSup);
        if (idResiden == null || idResiden.isEmpty() || idSupervisor == null || idSupervisor.isEmpty()) {
            jsonLog.append("=== Chaining RME Rawat Inap ").append(noRawat).append(" DILEWATI ===\n")
                   .append("IHS Residen/Supervisor belum resolve (Residen=").append(kdRes)
                   .append(", Supervisor=").append(kdSup).append("). Jalankan Sinkron Praktisi dulu.\n\n");
            return false;   // biarkan alur biasa menanganinya (single sign)
        }

        String[] periode = periodeEncounter(idEnc);
        String[] tipe = { tipeResource(jenis) };
        String[] jn = { jenis };
        String[] idc = { idComp };
        try {
            // --- Bundle #1: Residen ---
            String uuidProvR = java.util.UUID.randomUUID().toString();
            String uuidTaskR = java.util.UUID.randomUUID().toString();
            String jsonR = SatuSehatTTEPayload.buildBundleMulti(new String[]{uuidProvR}, idc, tipe, jn, uuidTaskR,
                    idResiden, nmRes, idEnc, koneksiDB.IDSATUSEHAT(), akses.getnamars(), isoSekarang(), periode[0], periode[1]);
            String hasilR = postBundle(jsonR);
            jsonLog.append("=== CHAINING 1/2 — POST Bundle Residen (").append(nmRes).append(") — ").append(noRawat).append(" ===\n")
                   .append("[REQUEST]\n").append(jsonR).append("\n\n[RESPONSE]\n").append(hasilR).append("\n\n");
            JsonNode rootR = mapper.readTree(hasilR);
            java.util.List<String> provsR = SatuSehatTTEPayload.idProvenanceUrut(rootR.path("entry"));
            String idTaskR = SatuSehatTTEPayload.idDariLokasi(rootR.path("entry"), "Task");
            if (provsR.isEmpty() || idTaskR.isEmpty()) { jsonLog.append("(Chaining gagal di langkah Residen)\n\n"); return true; }
            String idProvR = provsR.get(0);

            // --- Bundle #2: DPJP Supervisi (menautkan Provenance Residen) ---
            String uuidProvS = java.util.UUID.randomUUID().toString();
            String uuidTaskS = java.util.UUID.randomUUID().toString();
            String jsonS = SatuSehatTTEPayload.buildBundleMulti(new String[]{uuidProvS}, idc, tipe, jn, uuidTaskS,
                    idSupervisor, nmSup, idEnc, koneksiDB.IDSATUSEHAT(), akses.getnamars(), isoSekarang(), periode[0], periode[1], idProvR);
            String hasilS = postBundle(jsonS);
            jsonLog.append("=== CHAINING 2/2 — POST Bundle DPJP Supervisi (").append(nmSup).append(") menautkan Provenance/")
                   .append(idProvR).append(" — ").append(noRawat).append(" ===\n")
                   .append("[REQUEST]\n").append(jsonS).append("\n\n[RESPONSE]\n").append(hasilS).append("\n\n");
            JsonNode rootS = mapper.readTree(hasilS);
            java.util.List<String> provsS = SatuSehatTTEPayload.idProvenanceUrut(rootS.path("entry"));
            String idTaskS = SatuSehatTTEPayload.idDariLokasi(rootS.path("entry"), "Task");
            String idProvS = provsS.isEmpty() ? "" : provsS.get(0);

            // Tracking: satu_sehat_tte pakai submission Residen (utama di grid) + tabel chaining.
            simpanTracking(noRawat, jenis, idComp, idProvR, idTaskR, "Requested");
            catatRiwayat(noRawat, jenis, idComp, idProvR, idTaskR, "", "Requested", "Chaining: TTE Residen");
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "replace into satu_sehat_tte_chaining (no_rawat,id_composition,kd_residen,id_prov_residen,id_task_residen,"
                  + "kd_supervisor,id_prov_supervisor,id_task_supervisor,tgl_buat) values (?,?,?,?,?,?,?,?,now())")) {
                ps.setString(1, noRawat); ps.setString(2, idComp); ps.setString(3, kdRes); ps.setString(4, idProvR);
                ps.setString(5, idTaskR); ps.setString(6, kdSup); ps.setString(7, idProvS); ps.setString(8, idTaskS);
                ps.executeUpdate();
            } catch (Exception e) { System.out.println("Notif simpan chaining : " + e); }

            tbTTE.setValueAt(idProvR, row, COL_PROVENANCE);
            tbTTE.setValueAt(idTaskR, row, COL_TASK);
            tbTTE.setValueAt("Requested (Chain)", row, COL_STATUS);
            return true;
        } catch (Exception e) {
            System.out.println("Notif chaining : " + e);
            jsonLog.append("=== Chaining RME Rawat Inap ").append(noRawat).append(" GAGAL ===\n[ERROR]\n")
                   .append(e).append(bodyError(e)).append("\n\n");
            return true;   // sudah dicoba sebagai chaining; jangan diproses ulang alur biasa
        }
    }

    /** POST satu Bundle transaction ke root FHIR, return body response. */
    private String postBundle(String json) throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        requestEntity = new HttpEntity(json, headers);
        return api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
    }

    private void buatTTE() {
        // Kumpulan request+response JSON tiap operasi, ditampilkan di akhir supaya
        // bisa langsung di-copy (bukti UAT / debug), baik saat berhasil maupun gagal.
        StringBuilder jsonLog = new StringBuilder();
        // Chaining signature (Residen -> DPJP Supervisi): dokumen RME Rawat Inap yang
        // dpjp_ranap-nya >=2 DPJP diproses berjenjang lebih dulu, lalu dikeluarkan dari
        // pengelompokan biasa (di-uncheck).
        int chainDibuat = 0;
        for (int row = 0; row < tabMode.getRowCount(); row++) {
            if (!"true".equals(tbTTE.getValueAt(row, COL_PILIH).toString())) continue;
            if (prosesChaining(row, jsonLog)) { chainDibuat++; tbTTE.setValueAt(false, row, COL_PILIH); }
        }
        // Kumpulkan baris tercentang per (No.Rawat + NIK dokter), jaga urutan tampil.
        java.util.LinkedHashMap<String, java.util.List<Integer>> grup = new java.util.LinkedHashMap<>();
        for (int row = 0; row < tabMode.getRowCount(); row++) {
            if (!"true".equals(tbTTE.getValueAt(row, COL_PILIH).toString())) continue;
            String keyGrup = tbTTE.getValueAt(row, COL_NORAWAT).toString()
                    + "" + tbTTE.getValueAt(row, COL_NIKDPJP).toString();
            grup.computeIfAbsent(keyGrup, k -> new java.util.ArrayList<>()).add(row);
        }

        int taskDibuat = 0, dokBerhasil = 0, dilewati = 0, diajukanUlang = 0;
        for (java.util.Map.Entry<String, java.util.List<Integer>> g : grup.entrySet()) {
            String noRawat = tbTTE.getValueAt(g.getValue().get(0), COL_NORAWAT).toString();
            // Saring baris yang layak: belum punya Task dan datanya lengkap.
            // Baris berstatus "Ditolak" dipisah — Provenance-nya dipakai ulang,
            // yang dibuat ulang hanya Task-nya (Panduan Teknis TTE v.1.2 slide 11).
            java.util.List<Integer> rows = new java.util.ArrayList<>();
            java.util.List<Integer> rowsUlang = new java.util.ArrayList<>();
            String idEnc = "", nikDok = "";
            for (int row : g.getValue()) {
                String enc = tbTTE.getValueAt(row, COL_ENCOUNTER).toString();
                String nik = tbTTE.getValueAt(row, COL_NIKDPJP).toString();
                String comp = tbTTE.getValueAt(row, COL_COMPOSITION).toString();
                // "Dibatalkan" & "Gagal" diperlakukan sama dengan "Ditolak": Provenance
                // dipakai ulang, hanya Task baru yang dibuat (Panduan Teknis TTE v1.2).
                // Gagal = output.type.code "failed" dari SSM; sah untuk diajukan ulang.
                String st = tbTTE.getValueAt(row, COL_STATUS).toString();
                boolean ditolak = "Ditolak".equals(st) || "Dibatalkan".equals(st) || "Gagal".equals(st);
                boolean adaTask = !tbTTE.getValueAt(row, COL_TASK).toString().isEmpty();
                boolean adaProv = !tbTTE.getValueAt(row, COL_PROVENANCE).toString().isEmpty();
                if (adaTask && !ditolak) { dilewati++; continue; }   // sudah diajukan, belum ditolak
                if (enc.isEmpty() || nik.isEmpty() || comp.isEmpty()) { dilewati++; continue; }
                if (ditolak && adaProv) rowsUlang.add(row); else rows.add(row);
                idEnc = enc; nikDok = nik;   // sama untuk seluruh No.Rawat
            }
            if (rows.isEmpty() && rowsUlang.isEmpty()) continue;

            String idDok = cekViaSatuSehat.tampilIDParktisi(nikDok);
            if (idDok == null || idDok.isEmpty()) { dilewati += rows.size() + rowsUlang.size(); continue; }

            // --- pengajuan ulang: Task baru menunjuk Provenance lama ---
            if (!rowsUlang.isEmpty()) {
                try {
                    String[] idProvLama = new String[rowsUlang.size()];
                    for (int k = 0; k < rowsUlang.size(); k++) {
                        idProvLama[k] = tbTTE.getValueAt(rowsUlang.get(k), COL_PROVENANCE).toString();
                    }
                    String nmDok2 = tbTTE.getValueAt(rowsUlang.get(0), COL_NAMADPJP).toString();
                    json = SatuSehatTTEPayload.buildTaskProvenanceLama(idProvLama, idDok, nmDok2, idEnc,
                            koneksiDB.IDSATUSEHAT(), akses.getnamars(), isoSekarang());
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                    requestEntity = new HttpEntity(json, headers);
                    String hasil = api.getRest().exchange(link + "/Task", HttpMethod.POST, requestEntity, String.class).getBody();
                    jsonLog.append("=== POST Task (ajukan ulang) — No.Rawat ").append(noRawat).append(" ===\n")
                           .append("URL: ").append(link).append("/Task\n\n[REQUEST]\n").append(json)
                           .append("\n\n[RESPONSE]\n").append(hasil).append("\n\n");
                    String idTaskBaru = mapper.readTree(hasil).path("id").asText();
                    if (idTaskBaru == null || idTaskBaru.isEmpty()) {
                        System.out.println("Notif TTE ajukan ulang, respon tak terduga : " + hasil);
                        dilewati += rowsUlang.size();
                    } else {
                        for (int row : rowsUlang) {
                            try (PreparedStatement ps = koneksi.prepareStatement(
                                    "update satu_sehat_tte set id_task=?, status='Requested', tgl_tte=null "
                                  + "where no_rawat=? and id_composition=?")) {
                                ps.setString(1, idTaskBaru);
                                ps.setString(2, noRawat);
                                ps.setString(3, tbTTE.getValueAt(row, COL_COMPOSITION).toString());
                                ps.executeUpdate();
                            }
                            catatRiwayat(noRawat, tbTTE.getValueAt(row, COL_JENIS).toString(),
                                    tbTTE.getValueAt(row, COL_COMPOSITION).toString(),
                                    tbTTE.getValueAt(row, COL_PROVENANCE).toString(), idTaskBaru,
                                    tbTTE.getValueAt(row, COL_STATUS).toString(), "Requested",
                                    "Diajukan ulang, Provenance lama dipakai kembali");
                            tbTTE.setValueAt(idTaskBaru, row, COL_TASK);
                            tbTTE.setValueAt("Requested", row, COL_STATUS);
                            tbTTE.setValueAt(false, row, COL_PILIH);
                            diajukanUlang++;
                        }
                        taskDibuat++;
                    }
                } catch (Exception e) {
                    System.out.println("Notif TTE ajukan ulang : " + e);
                    jsonLog.append("=== POST Task (ajukan ulang) — No.Rawat ").append(noRawat).append(" GAGAL ===\n")
                           .append("[REQUEST]\n").append(json).append("\n\n[ERROR]\n").append(e)
                           .append(bodyError(e)).append("\n\n");
                    dilewati += rowsUlang.size();
                }
            }
            if (rows.isEmpty()) continue;

            try {
                String uuidTask = java.util.UUID.randomUUID().toString();
                String[] uuidProv = new String[rows.size()];
                String[] idComp = new String[rows.size()];
                String[] jenis = new String[rows.size()];
                String[] tipe = new String[rows.size()];
                for (int k = 0; k < rows.size(); k++) {
                    uuidProv[k] = java.util.UUID.randomUUID().toString();
                    idComp[k] = tbTTE.getValueAt(rows.get(k), COL_COMPOSITION).toString();
                    jenis[k] = tbTTE.getValueAt(rows.get(k), COL_JENIS).toString();
                    tipe[k] = tipeResource(jenis[k]);
                }
                String nmDok = tbTTE.getValueAt(rows.get(0), COL_NAMADPJP).toString();

                String[] periode = periodeEncounter(idEnc);
                json = SatuSehatTTEPayload.buildBundleMulti(uuidProv, idComp, tipe, jenis, uuidTask,
                        idDok, nmDok, idEnc, koneksiDB.IDSATUSEHAT(), akses.getnamars(), isoSekarang(),
                        periode[0], periode[1]);

                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                requestEntity = new HttpEntity(json, headers);
                // Transaction bundle dikirim ke root, bukan ke endpoint resource.
                String hasil = api.getRest().exchange(link, HttpMethod.POST, requestEntity, String.class).getBody();
                jsonLog.append("=== POST Bundle (Provenance+Task) — No.Rawat ").append(noRawat).append(" ===\n")
                       .append("URL: ").append(link).append("\n\n[REQUEST]\n").append(json)
                       .append("\n\n[RESPONSE]\n").append(hasil).append("\n\n");

                JsonNode root = mapper.readTree(hasil);
                java.util.List<String> idProvs = SatuSehatTTEPayload.idProvenanceUrut(root.path("entry"));
                String idTask = SatuSehatTTEPayload.idDariLokasi(root.path("entry"), "Task");

                if (idProvs.size() != rows.size() || idTask.isEmpty()) {
                    System.out.println("Notif TTE respon tak terduga : " + hasil);
                    dilewati += rows.size();
                    continue;
                }

                // Response berurut sama dengan request, jadi idProvs[k] milik rows[k].
                for (int k = 0; k < rows.size(); k++) {
                    int row = rows.get(k);
                    String idProv = idProvs.get(k);
                    simpanTracking(noRawat, jenis[k], idComp[k], idProv, idTask, "Requested");
                    catatRiwayat(noRawat, jenis[k], idComp[k], idProv, idTask, "", "Requested", "Pengajuan TTE baru");
                    tbTTE.setValueAt(idProv, row, COL_PROVENANCE);
                    tbTTE.setValueAt(idTask, row, COL_TASK);
                    tbTTE.setValueAt("Requested", row, COL_STATUS);
                    tbTTE.setValueAt(false, row, COL_PILIH);
                    dokBerhasil++;
                }
                taskDibuat++;
            } catch (Exception e) {
                System.out.println("Notif buat TTE : " + e);
                jsonLog.append("=== POST Bundle (Provenance+Task) — No.Rawat ").append(noRawat).append(" GAGAL ===\n")
                       .append("[REQUEST]\n").append(json).append("\n\n[ERROR]\n").append(e)
                       .append(bodyError(e)).append("\n\n");
                dilewati += rows.size();
            }
        }
        String ringkas = "Selesai. Task dibuat : " + taskDibuat
                + ", dokumen berhasil : " + dokBerhasil
                + (chainDibuat > 0 ? ", chaining (Residen→DPJP) : " + chainDibuat : "")
                + (diajukanUlang > 0 ? ", diajukan ulang (Provenance dipakai lagi) : " + diajukanUlang : "")
                + (dilewati > 0 ? ", dilewati : " + dilewati : "");
        // Tampilkan JSON request/response (berhasil maupun gagal) agar bisa di-copy.
        String keterangan =
                "Catatan UAT: SIMRS membuat Provenance + Task dalam SATU Bundle transaction "
              + "(POST ke root SATUSEHAT). Pada [REQUEST] terdapat entry Provenance DAN entry Task; "
              + "id keduanya muncul di [RESPONSE]. Bukti ini berlaku untuk baris \"POST Provenance\" "
              + "maupun \"POST Task\" di dokumen UAT. (POST /Task terpisah hanya terjadi saat ajukan "
              + "ulang dokumen Ditolak/Gagal.)\n"
              + "----------------------------------------------------------------------\n";
        String isi = ringkas + "\n\n" + keterangan + "\n" + (jsonLog.length() == 0
                ? "(Tidak ada operasi dikirim — pastikan ada baris tercentang yang belum ber-Task.)"
                : jsonLog.toString());
        javax.swing.JTextArea area = new javax.swing.JTextArea(isi, 28, 92);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(area), "Buat TTE — JSON", JOptionPane.INFORMATION_MESSAGE);
    }

    private void simpanTracking(String noRawat, String jenis, String idComp,
                                String idProv, String idTask, String status) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into satu_sehat_tte (no_rawat,jenis,id_composition,id_provenance,id_task,status,tgl_buat) "
                + "values (?,?,?,?,?,?,?) on duplicate key update "
                + "id_composition=values(id_composition), id_provenance=values(id_provenance), "
                + "id_task=values(id_task), status=values(status), tgl_buat=values(tgl_buat)")) {
            ps.setString(1, noRawat);
            ps.setString(2, jenis);
            ps.setString(3, idComp);
            ps.setString(4, idProv);
            ps.setString(5, idTask);
            ps.setString(6, status);
            ps.setString(7, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif simpan tracking TTE : " + e);
        }
    }

    /**
     * Memeriksa status tanda tangan.
     *
     * Yang menandatangani adalah SATUSEHAT Mobile lewat PUT /Provenance/{id};
     * SIMRS hanya membaca. Dianggap sudah ditandatangani bila signature[] terisi.
     */
    /**
     * Baca status <b>Task</b> di SatuSehat lalu selaraskan satu_sehat_tte.
     *
     * Bedanya dengan tombol "Status" ({@link #refreshStatus()}): yang itu memeriksa
     * <i>Provenance</i>, sehingga hanya bisa menyimpulkan "sudah ditandatangani" dan
     * TIDAK PERNAH tahu kalau dokumen <b>ditolak</b> penandatangan. Task punya status
     * eksplisit, jadi di sini keduanya tertangani — persis seperti yang dilakukan
     * penerima webhook taskSignatureDone (docs/webhook/golang/server).
     *
     * Pemetaan: Task.status completed -> Signed (+tgl_tte), rejected -> Ditolak,
     * selain itu dibiarkan dan dilaporkan apa adanya (requested/in-progress/dst).
     * Update DB memakai id_task, jadi semua dokumen yang berbagi satu Task ikut
     * tersinkron — bukan hanya baris yang kebetulan tampil.
     */
    /**
     * Catat satu transisi status ke satu_sehat_tte_riwayat.
     *
     * Tabel utama hanya menyimpan status TERAKHIR; begitu dokumen dibatalkan lalu
     * diajukan ulang, jejak "pernah ditandatangani lalu dibatalkan oleh siapa dan
     * kenapa" hilang — padahal itu yang dicari saat audit rekam medis.
     */
    private void catatRiwayat(String noRawat, String jenis, String idComposition, String idProvenance,
                              String idTask, String statusLama, String statusBaru, String alasan) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into satu_sehat_tte_riwayat "
              + "(no_rawat,jenis,id_composition,id_provenance,id_task,status_lama,status_baru,alasan,petugas,waktu) "
              + "values (?,?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, noRawat);
            ps.setString(2, jenis);
            ps.setString(3, idComposition);
            ps.setString(4, idProvenance);
            ps.setString(5, idTask);
            ps.setString(6, statusLama);
            ps.setString(7, statusBaru);
            ps.setString(8, alasan);
            ps.setString(9, akses.getuserlogin() == null || akses.getuserlogin().isEmpty()
                    ? akses.getkode() : akses.getuserlogin());
            ps.setString(10, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif catat riwayat TTE : " + e);
        }
    }

    /**
     * Batalkan pengajuan/tanda tangan dokumen terpilih.
     *
     * Yang dilakukan: PUT Task ke {@code status=cancelled} di SatuSehat (diterima
     * staging, HTTP 200), lalu status lokal jadi <b>Dibatalkan</b> dan transisinya
     * dicatat ke riwayat.
     *
     * Yang TIDAK dilakukan: menghapus Provenance. FHIR tidak mengenal "hapus tanda
     * tangan", dan Provenance lama justru dipakai ulang saat pengajuan berikutnya
     * (Panduan Teknis TTE v1.2: "Provenance bisa di reuse"). Karena itu id_provenance
     * sengaja dipertahankan.
     *
     * Satu Task bisa menaungi beberapa dokumen; membatalkan satu baris otomatis
     * membatalkan seluruh dokumen dalam Task yang sama — dan itu diberitahukan
     * sebelum dijalankan, bukan diam-diam.
     */
    private void batalkanTTE() {
        if (!akses.getsatu_sehat_kirim_encounter()) {
            JOptionPane.showMessageDialog(null, "Anda tidak punya hak akses untuk membatalkan TTE.");
            return;
        }
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dokumen dulu."); return; }
        String idTask = tbTTE.getValueAt(row, COL_TASK).toString().trim();
        String statusLama = tbTTE.getValueAt(row, COL_STATUS).toString();
        if (idTask.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Dokumen ini belum pernah diajukan TTE, tidak ada yang dibatalkan.");
            return;
        }
        if ("Dibatalkan".equals(statusLama)) {
            JOptionPane.showMessageDialog(null, "Dokumen ini sudah berstatus Dibatalkan.");
            return;
        }

        // Berapa dokumen lain ikut terdampak? Satu Task bisa menaungi banyak dokumen.
        int seTask = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select count(*) n from satu_sehat_tte where id_task=?")) {
            p.setString(1, idTask);
            try (ResultSet r = p.executeQuery()) { if (r.next()) seTask = r.getInt("n"); }
        } catch (Exception e) { System.out.println("Notif hitung se-Task : " + e); }

        String peringatan = "Batalkan TTE dokumen \"" + tbTTE.getValueAt(row, COL_JENIS) + "\"?\n"
                + "Status sekarang : " + statusLama + "\n";
        if (seTask > 1) {
            peringatan += "\nPERHATIAN: Task ini menaungi " + seTask + " dokumen.\n"
                        + "Membatalkannya membatalkan SELURUH " + seTask + " dokumen tersebut.\n";
        }
        if ("Signed".equals(statusLama)) {
            peringatan += "\nDokumen ini SUDAH DITANDATANGANI. Pembatalan tidak menghapus\n"
                        + "tanda tangan yang sudah ada di SatuSehat, hanya menandai bahwa\n"
                        + "pengajuannya dibatalkan agar dokumen bisa direvisi & diajukan ulang.\n";
        }
        peringatan += "\nRiwayatnya tetap tersimpan dan bisa dilihat lewat tombol Riwayat.";
        if (JOptionPane.showConfirmDialog(null, peringatan, "Batalkan TTE",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

        String alasan = JOptionPane.showInputDialog(null,
                "Alasan pembatalan (wajib, tersimpan di riwayat):", "Batalkan TTE", JOptionPane.QUESTION_MESSAGE);
        if (alasan == null) return;                       // dibatalkan petugas
        alasan = alasan.trim();
        if (alasan.isEmpty()) { JOptionPane.showMessageDialog(null, "Alasan wajib diisi."); return; }
        if (alasan.length() > 255) alasan = alasan.substring(0, 255);

        // 1) Batalkan Task di SatuSehat. Kalau gagal, jangan ubah status lokal --
        //    lebih baik tetap sinkron dengan server daripada mengaku batal sepihak.
        boolean serverOk = false;
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String taskJson = api.getRest().exchange(link + "/Task/" + idTask,
                    HttpMethod.GET, requestEntity, String.class).getBody();
            com.fasterxml.jackson.databind.node.ObjectNode t =
                    (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree(taskJson);
            t.put("status", "cancelled");
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(mapper.writeValueAsString(t), headers);
            System.out.println("URL : " + link + "/Task/" + idTask);
            System.out.println("Request JSON : " + mapper.writeValueAsString(t));
            String hasil = api.getRest().exchange(link + "/Task/" + idTask,
                    HttpMethod.PUT, requestEntity, String.class).getBody();
            System.out.println("Result JSON : " + hasil);
            serverOk = "cancelled".equals(mapper.readTree(hasil).path("status").asText());
        } catch (Exception e) {
            System.out.println("Notif batalkan Task : " + e);
        }
        if (!serverOk) {
            JOptionPane.showMessageDialog(null,
                    "Gagal membatalkan Task di SatuSehat. Status lokal TIDAK diubah\n"
                  + "supaya tidak berbeda dengan server. Lihat log untuk rinciannya.");
            return;
        }

        // 2) Catat riwayat dulu untuk SEMUA dokumen se-Task, baru ubah statusnya.
        int diubah = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select no_rawat,jenis,id_composition,id_provenance,status from satu_sehat_tte where id_task=?")) {
            p.setString(1, idTask);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    catatRiwayat(r.getString("no_rawat"), r.getString("jenis"), r.getString("id_composition"),
                            r.getString("id_provenance"), idTask, r.getString("status"), "Dibatalkan", alasan);
                    diubah++;
                }
            }
        } catch (Exception e) { System.out.println("Notif riwayat pembatalan : " + e); }

        try (PreparedStatement p = koneksi.prepareStatement(
                "update satu_sehat_tte set status='Dibatalkan', tgl_tte=null where id_task=?")) {
            p.setString(1, idTask);
            p.executeUpdate();
        } catch (Exception e) { System.out.println("Notif update status batal : " + e); }

        if (!noRawatAktif.isEmpty()) tampilDokumen(noRawatAktif);
        JOptionPane.showMessageDialog(null, "TTE dibatalkan. Dokumen terdampak : " + diubah
                + "\nProvenance dipertahankan dan akan dipakai ulang saat diajukan lagi.");
    }

    /** Catat riwayat untuk seluruh dokumen dalam satu Task. */
    private void catatRiwayatTask(String idTask, String statusBaru, String alasan) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select no_rawat,jenis,id_composition,id_provenance,status from satu_sehat_tte where id_task=? and status<>?")) {
            p.setString(1, idTask); p.setString(2, statusBaru);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    catatRiwayat(r.getString("no_rawat"), r.getString("jenis"), r.getString("id_composition"),
                            r.getString("id_provenance"), idTask, r.getString("status"), statusBaru, alasan);
                }
            }
        } catch (Exception e) { System.out.println("Notif riwayat per Task : " + e); }
    }

    /** Catat riwayat untuk satu dokumen berdasarkan id Provenance-nya. */
    private void catatRiwayatProvenance(String idProvenance, String statusBaru, String alasan) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select no_rawat,jenis,id_composition,id_task,status from satu_sehat_tte where id_provenance=? and status<>?")) {
            p.setString(1, idProvenance); p.setString(2, statusBaru);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    catatRiwayat(r.getString("no_rawat"), r.getString("jenis"), r.getString("id_composition"),
                            idProvenance, r.getString("id_task"), r.getString("status"), statusBaru, alasan);
                }
            }
        } catch (Exception e) { System.out.println("Notif riwayat per Provenance : " + e); }
    }

    /** Tampilkan riwayat status TTE dokumen terpilih, terbaru di atas. */
    private void lihatRiwayat() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dokumen dulu."); return; }
        String idComp = tbTTE.getValueAt(row, COL_COMPOSITION).toString();
        String noRawat = tbTTE.getValueAt(row, COL_NORAWAT).toString();

        StringBuilder h = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
        h.append("<div style='text-align:center'><b>RIWAYAT TTE</b><br>")
         .append(tbTTE.getValueAt(row, COL_JENIS)).append("<br><font color='#999'>")
         .append(idComp).append("</font></div><hr>");
        int n = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select waktu, status_lama, status_baru, ifnull(alasan,'') alasan, ifnull(petugas,'') petugas, "
              + "ifnull(id_task,'') id_task from satu_sehat_tte_riwayat "
              + "where no_rawat=? and id_composition=? order by waktu desc, id desc")) {
            p.setString(1, noRawat);
            p.setString(2, idComp);
            try (ResultSet r = p.executeQuery()) {
                h.append("<table cellpadding='3' style='font:11px tahoma'>");
                while (r.next()) {
                    n++;
                    String lama = r.getString("status_lama");
                    h.append("<tr><td valign='top'><b>").append(r.getString("waktu")).append("</b></td><td valign='top'>")
                     .append(lama == null || lama.isEmpty() ? "(baru)" : lama)
                     .append(" &rarr; <b>").append(r.getString("status_baru")).append("</b>");
                    if (!r.getString("petugas").isEmpty()) h.append(" <font color='#999'>oleh ").append(r.getString("petugas")).append("</font>");
                    if (!r.getString("alasan").isEmpty()) h.append("<br><i>").append(r.getString("alasan")).append("</i>");
                    if (!r.getString("id_task").isEmpty()) h.append("<br><font color='#999' size='2'>Task ").append(r.getString("id_task")).append("</font>");
                    h.append("</td></tr>");
                }
                h.append("</table>");
            }
        } catch (Exception e) {
            h.append("<font color='red'>Error: ").append(e).append("</font>");
        }
        if (n == 0) {
            h.append("<i>Belum ada riwayat tercatat untuk dokumen ini.</i><br><br>")
             .append("<font color='#999'>Riwayat mulai dicatat sejak fitur ini dipasang; ")
             .append("dokumen yang diajukan sebelumnya tidak punya jejak lama.</font>");
        }
        h.append("</body></html>");

        widget.editorpane ep = new widget.editorpane();
        ep.setContentType("text/html");
        ep.setEditable(false);
        ep.setText(h.toString());
        ep.setCaretPosition(0);
        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(ep);
        sc.setPreferredSize(new Dimension(560, 340));
        JOptionPane.showMessageDialog(null, sc, "Riwayat TTE", JOptionPane.PLAIN_MESSAGE);
    }

    private void updateStatusTTE() {
        int diperiksa = 0, signed = 0, ditolak = 0, belum = 0, gagal = 0, gagalTanda = 0;
        java.util.Map<String, String> statusLain = new java.util.LinkedHashMap<>();
        java.util.Set<String> sudahDicek = new java.util.HashSet<>();

        for (int row = 0; row < tabMode.getRowCount(); row++) {
            String idTask = tbTTE.getValueAt(row, COL_TASK) == null ? "" : tbTTE.getValueAt(row, COL_TASK).toString().trim();
            if (idTask.isEmpty()) continue;
            if ("Signed".equals(tbTTE.getValueAt(row, COL_STATUS).toString())) continue;
            // Satu Task bisa menaungi beberapa dokumen; cukup satu kali GET per Task.
            if (!sudahDicek.add(idTask)) continue;
            diperiksa++;
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                requestEntity = new HttpEntity(headers);
                String hasil = api.getRest().exchange(link + "/Task/" + idTask,
                        HttpMethod.GET, requestEntity, String.class).getBody();
                JsonNode task = mapper.readTree(hasil);
                String st = task.path("status").asText();

                // Task.output memberi hasil PER Provenance. Untuk bulk (satu Task
                // menaungi banyak dokumen) hasilnya bisa campur — sebagian
                // ditandatangani, sebagian ditolak — jadi status Task saja tidak cukup.
                java.util.Map<String, String> perProv = SatuSehatTTEPayload.statusPerProvenance(task);
                String waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                for (java.util.Map.Entry<String, String> e : perProv.entrySet()) {
                    String stt = e.getValue();   // Signed / Ditolak / Gagal (dari output.type.code)
                    catatRiwayatProvenance(e.getKey(), stt, "Hasil Task.output");
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "Signed".equals(stt)
                              ? "update satu_sehat_tte set status='Signed', tgl_tte=? where id_provenance=?"
                              : "update satu_sehat_tte set status=?, tgl_tte=null where id_provenance=?")) {
                        int n = 1;
                        if ("Signed".equals(stt)) ps.setString(n++, waktu); else ps.setString(n++, stt);
                        ps.setString(n, e.getKey());
                        int ubah = ps.executeUpdate();
                        if ("Signed".equals(stt)) signed += ubah;
                        else if ("Gagal".equals(stt)) gagalTanda += ubah;
                        else ditolak += ubah;
                    }
                }

                if (!perProv.isEmpty()) {
                    // sudah ditangani per dokumen; tidak perlu pukul rata per Task
                } else if ("completed".equals(st)) {
                    catatRiwayatTask(idTask, "Signed", "Task.status=completed");
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "update satu_sehat_tte set status='Signed', tgl_tte=? where id_task=?")) {
                        ps.setString(1, waktu);
                        ps.setString(2, idTask);
                        signed += ps.executeUpdate();
                    }
                } else if ("rejected".equals(st)) {
                    catatRiwayatTask(idTask, "Ditolak", "Task.status=rejected");
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "update satu_sehat_tte set status='Ditolak' where id_task=?")) {
                        ps.setString(1, idTask);
                        ditolak += ps.executeUpdate();
                    }
                } else {
                    belum++;
                    statusLain.put(idTask, st.isEmpty() ? "(tanpa status)" : st);
                }
            } catch (Exception e) {
                gagal++;
                System.out.println("Notif update status TTE (" + idTask + ") : " + e);
            }
        }

        if (!noRawatAktif.isEmpty()) tampilDokumen(noRawatAktif);   // muat ulang grid dari DB

        StringBuilder pesan = new StringBuilder();
        pesan.append("Task diperiksa : ").append(diperiksa)
             .append("\nDokumen jadi Signed : ").append(signed)
             .append("\nDokumen jadi Ditolak : ").append(ditolak)
             .append("\nDokumen jadi Gagal : ").append(gagalTanda)
             .append("\nTask belum selesai : ").append(belum);
        if (gagal > 0) pesan.append("\nGagal dibaca : ").append(gagal).append(" (lihat log)");
        if (!statusLain.isEmpty()) {
            pesan.append("\n\nTask yang belum selesai :");
            for (java.util.Map.Entry<String, String> e : statusLain.entrySet()) {
                pesan.append("\n- ").append(e.getKey()).append(" : ").append(e.getValue());
            }
        }
        if (diperiksa == 0) {
            pesan.setLength(0);
            pesan.append("Tidak ada dokumen yang perlu diperiksa.\n"
                       + "Baris tanpa Task, atau yang statusnya sudah Signed, dilewati.");
        }
        JOptionPane.showMessageDialog(null, pesan.toString());
    }

    private void refreshStatus() {
        int ditandatangani = 0, diperiksa = 0;
        for (int row = 0; row < tabMode.getRowCount(); row++) {
            String idProv = tbTTE.getValueAt(row, COL_PROVENANCE).toString();
            if (idProv.isEmpty()) continue;
            if ("Signed".equals(tbTTE.getValueAt(row, COL_STATUS).toString())) continue;
            diperiksa++;
            try {
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                requestEntity = new HttpEntity(headers);
                String hasil = api.getRest().exchange(link + "/Provenance/" + idProv,
                        HttpMethod.GET, requestEntity, String.class).getBody();

                if (SatuSehatTTEPayload.sudahDitandatangani(mapper.readTree(hasil))) {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "update satu_sehat_tte set status='Signed', tgl_tte=? where id_provenance=?")) {
                        ps.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        ps.setString(2, idProv);
                        ps.executeUpdate();
                    }
                    tbTTE.setValueAt("Signed", row, COL_STATUS);
                    ditandatangani++;
                }
            } catch (Exception e) {
                System.out.println("Notif refresh status TTE : " + e);
            }
        }
        JOptionPane.showMessageDialog(null, "Diperiksa : " + diperiksa
                + ", sudah ditandatangani : " + ditandatangani);
    }

    /**
     * Menampilkan Task UUID dan JSON-nya.
     *
     * Pengganti panel QR: URL applink diketahui tetapi spesifikasi JWT ctxId
     * belum tersedia, jadi QR belum bisa dibuat. Menampilkan Task JSON sesuai
     * Flow 2b pada panduan yang menyebut panel evaluasi Task.
     */
    private void lihatTask() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dulu."); return; }
        String idTask = tbTTE.getValueAt(row, COL_TASK).toString();
        if (idTask.isEmpty()) { JOptionPane.showMessageDialog(null, "Baris ini belum punya Task."); return; }
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String hasil = api.getRest().exchange(link + "/Task/" + idTask,
                    HttpMethod.GET, requestEntity, String.class).getBody();
            javax.swing.JTextArea area = new javax.swing.JTextArea(
                    "Task UUID : " + idTask + "\n\n" + hasil, 25, 80);
            area.setEditable(false);
            area.setCaretPosition(0);
            JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(area), "Task TTE",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil Task : " + e);
        }
    }

    /**
     * Menampilkan Provenance milik baris terpilih.
     *
     * Tiap dokumen punya Provenance-nya sendiri (satu Task bisa memuat beberapa),
     * jadi yang ditampilkan adalah Provenance dokumen pada baris ini -- termasuk
     * signature[] bila sudah ditandatangani. Berguna untuk menelusuri per dokumen,
     * beda dari tombol Task yang menampilkan wadah tandatangannya.
     */
    private void lihatProvenance() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dulu."); return; }
        String idProv = tbTTE.getValueAt(row, COL_PROVENANCE).toString();
        if (idProv.isEmpty()) { JOptionPane.showMessageDialog(null, "Baris ini belum punya Provenance. Tekan Buat TTE dulu."); return; }
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String hasil = api.getRest().exchange(link + "/Provenance/" + idProv,
                    HttpMethod.GET, requestEntity, String.class).getBody();
            javax.swing.JTextArea area = new javax.swing.JTextArea(
                    "Jenis : " + tbTTE.getValueAt(row, COL_JENIS) + "\n"
                    + "Provenance ID : " + idProv + "\n\n" + hasil, 25, 80);
            area.setEditable(false);
            area.setCaretPosition(0);
            JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(area), "Provenance TTE",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil Provenance : " + e);
        }
    }

    /** Ambil body response dari error HTTP (mis. 400/422) agar isi error FHIR terlihat. */
    private static String bodyError(Exception e) {
        if (e instanceof org.springframework.web.client.HttpClientErrorException) {
            return "\n[RESPONSE BODY]\n" + ((org.springframework.web.client.HttpClientErrorException) e).getResponseBodyAsString();
        }
        if (e instanceof org.springframework.web.client.HttpServerErrorException) {
            return "\n[RESPONSE BODY]\n" + ((org.springframework.web.client.HttpServerErrorException) e).getResponseBodyAsString();
        }
        return "";
    }

    /**
     * Simpan payload/response FHIR (Provenance + Task) baris terpilih ke satu file
     * .json — untuk bukti UAT kolom "Bukti Link (Payload/Response FHIR)". File bisa
     * diunggah ke Google Drive lalu tautannya ditempel di dokumen UAT.
     */
    private void simpanJsonUAT() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dokumen dulu."); return; }
        String idProv = tbTTE.getValueAt(row, COL_PROVENANCE).toString();
        String idTask = tbTTE.getValueAt(row, COL_TASK).toString();
        if (idProv.isEmpty() && idTask.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Baris ini belum punya Provenance/Task. Tekan Buat TTE dulu.");
            return;
        }
        String jenis  = tbTTE.getValueAt(row, COL_JENIS).toString();
        String noRawat = tbTTE.getValueAt(row, COL_NORAWAT).toString();
        String status = tbTTE.getValueAt(row, COL_STATUS).toString();
        StringBuilder out = new StringBuilder();
        out.append("{\n");
        out.append("  \"_uat\": {\n");
        out.append("    \"no_rawat\": \"").append(noRawat).append("\",\n");
        out.append("    \"jenis_dokumen\": \"").append(jenis).append("\",\n");
        out.append("    \"status\": \"").append(status).append("\",\n");
        out.append("    \"id_provenance\": \"").append(idProv).append("\",\n");
        out.append("    \"id_task\": \"").append(idTask).append("\"\n");
        out.append("  },\n");
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            out.append("  \"provenance\": ");
            if (!idProv.isEmpty()) {
                out.append(api.getRest().exchange(link + "/Provenance/" + idProv, HttpMethod.GET, requestEntity, String.class).getBody());
            } else { out.append("null"); }
            out.append(",\n  \"task\": ");
            if (!idTask.isEmpty()) {
                out.append(api.getRest().exchange(link + "/Task/" + idTask, HttpMethod.GET, requestEntity, String.class).getBody());
            } else { out.append("null"); }
            out.append("\n}\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil JSON dari SatuSehat : " + e);
            return;
        }
        // Nama file default: TTE_<jenis>_<noRawat>.json (aman untuk nama file)
        String safe = (jenis + "_" + noRawat).replaceAll("[^A-Za-z0-9._-]", "_");
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setDialogTitle("Simpan JSON Bukti UAT");
        fc.setSelectedFile(new java.io.File("TTE_" + safe + ".json"));
        if (fc.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        java.io.File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".json")) f = new java.io.File(f.getParentFile(), f.getName() + ".json");
        try (java.io.Writer w = new java.io.OutputStreamWriter(new java.io.FileOutputStream(f), java.nio.charset.StandardCharsets.UTF_8)) {
            w.write(out.toString());
            JOptionPane.showMessageDialog(null, "JSON tersimpan:\n" + f.getAbsolutePath()
                    + "\n\nUnggah file ini ke Google Drive lalu tempel tautannya di kolom \"Bukti Link\" UAT.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan file : " + e);
        }
    }

    private static String barisHtml(String k, String v) {
        return "<tr><td valign='top'><b>" + k + "</b>&nbsp;&nbsp;</td><td valign='top'>" + escHtml(v) + "</td></tr>";
    }

    private static String escHtml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Item menu klik-kanan (aksi per-baris dokumen). */
    private javax.swing.JMenuItem mkItem(String text, String iconPath, Runnable act) {
        javax.swing.JMenuItem mi = new javax.swing.JMenuItem(text);
        mi.setFont(new java.awt.Font("Tahoma", 0, 11));
        mi.setPreferredSize(new java.awt.Dimension(210, 24));
        try { mi.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath))); } catch (Exception ignore) {}
        mi.addActionListener(e -> act.run());
        return mi;
    }

    /**
     * Verifikasi tanda tangan elektronik dokumen terpilih.
     *
     * GET Provenance/{id} lalu tampilkan hasil TTE dalam bentuk yang bisa dibaca
     * manusia — bukan JSON mentah seperti tombol Provenance. Yang ditampilkan:
     * aktivitas (LA = Legally Authenticated setelah TTE), penandatangan + waktu +
     * tipe tanda tangan, perangkat & lokasi penandatanganan (dari contained
     * Device/Location), dan isi JOSE (alg header + klaim payload) hasil decode
     * signature.data. Validitas kriptografis dijamin BSrE saat penandatanganan;
     * panel ini hanya menampilkannya.
     */
    private void verifikasiTTE() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dokumen dulu."); return; }
        String idProv = tbTTE.getValueAt(row, COL_PROVENANCE).toString();
        if (idProv.isEmpty()) { JOptionPane.showMessageDialog(null, "Baris ini belum punya Provenance."); return; }
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String hasil = api.getRest().exchange(link + "/Provenance/" + idProv,
                    HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode prov = mapper.readTree(hasil);
            JsonNode sigs = prov.path("signature");
            String lat = "", lon = "";   // koordinat lokasi tanda tangan (untuk tombol Peta)

            StringBuilder h = new StringBuilder("<html><body style='font:11px tahoma;color:#222;width:520px'>");
            h.append("<div style='text-align:center'><b>VERIFIKASI TANDA TANGAN</b><br>")
             .append(escHtml(tbTTE.getValueAt(row, COL_JENIS).toString())).append("</div><hr>");

            if (!sigs.isArray() || sigs.size() == 0) {
                h.append("<font color='#c60'><b>&#9888; Belum ada tanda tangan pada Provenance ini.</b></font><br>")
                 .append("<font color='#999'>Dokumen belum ditandatangani di SATUSEHAT Mobile, atau "
                       + "status-nya belum di-Update.</font>");
            } else {
                h.append("<font color='green'><b>&#10004; Tanda tangan elektronik ditemukan (")
                 .append(sigs.size()).append(" signature)</b></font><br><br>");

                String actDisp = "", actCode = "";
                JsonNode ac = prov.path("activity").path("coding");
                if (ac.isArray() && ac.size() > 0) {
                    actDisp = ac.get(0).path("display").asText();
                    actCode = ac.get(0).path("code").asText();
                }
                String device = "", lokasi = "";
                for (JsonNode c : prov.path("contained")) {
                    String rt = c.path("resourceType").asText();
                    if ("Device".equals(rt)) {
                        JsonNode dn = c.path("deviceName");
                        if (dn.isArray() && dn.size() > 0) device = dn.get(0).path("name").asText();
                    } else if ("Location".equals(rt)) {
                        JsonNode pos = c.path("position");
                        if (!pos.isMissingNode()) {
                            lat = pos.path("latitude").asText();
                            lon = pos.path("longitude").asText();
                            lokasi = lat + ", " + lon;
                        }
                    }
                }

                JsonNode s0 = sigs.get(0);
                h.append("<table cellpadding='2'>");
                h.append(barisHtml("Aktivitas", actDisp + (actCode.isEmpty() ? "" : " (" + actCode + ")")));
                h.append(barisHtml("Ditandatangani oleh", s0.path("who").path("display").asText()));
                h.append(barisHtml("Tanggal tanda tangan", s0.path("when").asText()));
                JsonNode tp = s0.path("type");
                if (tp.isArray() && tp.size() > 0)
                    h.append(barisHtml("Tipe tanda tangan",
                            tp.get(0).path("display").asText() + " (" + tp.get(0).path("code").asText() + ")"));
                h.append(barisHtml("Format", s0.path("sigFormat").asText()));
                if (!device.isEmpty()) h.append(barisHtml("Perangkat", device));
                if (!lokasi.isEmpty()) h.append(barisHtml("Lokasi", lokasi));
                h.append("</table>");

                String[] jose = SatuSehatTTEPayload.decodeJose(s0.path("data").asText());
                if (!jose[0].isEmpty() || !jose[1].isEmpty()) {
                    h.append("<hr><b>JWT Signature (JOSE)</b>")
                     .append("<div style='font-family:monospace;font-size:10px;background:#f6f6f6;border:1px solid #e0e0e0;padding:4px;margin-top:3px'>")
                     .append("<b>Header</b><br>").append(escHtml(jose[0]))
                     .append("<br><br><b>Payload</b><br>").append(escHtml(jose[1]))
                     .append("</div>");
                }
            }
            h.append("<hr><font color='#999'>Validitas kriptografis dijamin BSrE saat penandatanganan; "
                   + "panel ini menampilkannya.</font></body></html>");

            widget.editorpane ep = new widget.editorpane();
            ep.setContentType("text/html");
            ep.setEditable(false);
            ep.setText(h.toString());
            ep.setCaretPosition(0);
            widget.ScrollPane sc = new widget.ScrollPane();
            sc.setViewportView(ep);
            sc.setPreferredSize(new Dimension(560, 440));

            javax.swing.JPanel pnl = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
            pnl.add(sc, java.awt.BorderLayout.CENTER);
            if (!lat.isEmpty() && !lon.isEmpty()) {
                final String urlPeta = "https://www.google.com/maps?q=" + lat + "," + lon;
                javax.swing.JButton btnPeta = new javax.swing.JButton("Buka Lokasi di Peta");
                btnPeta.addActionListener(a -> bukaBrowser(urlPeta));
                javax.swing.JPanel bar = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 2));
                bar.add(new widget.Label());   // spacer kiri
                bar.add(btnPeta);
                pnl.add(bar, java.awt.BorderLayout.SOUTH);
            }
            JOptionPane.showMessageDialog(null, pnl, "Verifikasi Tanda Tangan", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal verifikasi : " + e);
        }
    }

    /** Buka URL di browser default (Desktop, fallback xdg-open). */
    private void bukaBrowser(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()
                    && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } else {
                new ProcessBuilder("xdg-open", url).start();   // Linux fallback
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Tidak bisa membuka browser otomatis. Salin URL ini:\n" + url);
        }
    }

    /**
     * Sinkron IHS praktisi: untuk tiap DPJP (pegawai.no_ktp) yang belum ada di
     * cache, panggil GET Practitioner?identifier=nik ke SatuSehat lewat
     * {@code tampilIDParktisi} — yang otomatis mengisi satu_sehat_cache_ihs_praktisi.
     * Sekali klik, DPJP ter-resolve sehingga Buat TTE tak perlu lookup live per orang.
     */
    private void sinkronPraktisi() {
        // Ambil SEMUA DPJP (bukan hanya yang belum tercache) supaya sekalian bisa
        // memastikan PractitionerRole (eligibility TTE) untuk yang sudah punya IHS.
        java.util.List<String[]> list = new java.util.ArrayList<>();   // {no_ktp, kd_dokter, nm_dokter}
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select distinct p.no_ktp, d.kd_dokter, ifnull(d.nm_dokter,'') nm_dokter "
              + "from pegawai p join dokter d on d.kd_dokter=p.nik "
              + "where ifnull(p.no_ktp,'')<>'' ");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new String[]{ rs.getString(1), rs.getString(2), rs.getString("nm_dokter") });
        } catch (Exception e) { System.out.println("Notif sinkron praktisi (query) : " + e); }

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada DPJP untuk disinkron.");
            return;
        }
        if (JOptionPane.showConfirmDialog(null,
                list.size() + " DPJP akan disinkron ke SatuSehat.\n"
              + "Proses: resolve IHS Practitioner (per NIK) + pastikan PractitionerRole (eligibility TTE).\n"
              + "Lanjutkan? (bisa beberapa saat)", "Sinkron Praktisi & PractitionerRole",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        SatuSehatPractitionerRole roleSvc = new SatuSehatPractitionerRole();
        int dapat = 0, gagal = 0, role = 0;
        for (String[] d : list) {
            try {
                String ihs = cekViaSatuSehat.tampilIDParktisi(d[0]);   // otomatis mengisi cache bila ketemu
                if (ihs != null && !ihs.isEmpty()) {
                    dapat++;
                    String idRole = roleSvc.pastikanRole(ihs, d[1], d[2]);   // eligibility TTE
                    if (idRole != null && !idRole.isEmpty()) role++;
                } else gagal++;
            } catch (Exception e) { gagal++; }
        }
        setCursor(java.awt.Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Sinkron praktisi selesai.\nDiperiksa : " + list.size()
                + "\nDapat IHS : " + dapat + "\nEligible TTE (PractitionerRole) : " + role + "\nTidak ketemu : " + gagal);
    }

    /** Buang atribut xmlns pada narrative div supaya tampil rapi di JEditorPane. */
    private static String bersihkanDiv(String div) {
        return div == null ? "" : div.replaceAll("xmlns=\"[^\"]*\"", "");
    }

    /**
     * Preview isi dokumen: GET resource (Composition/ServiceRequest/DiagnosticReport)
     * lalu tampilkan bagian yang bisa dibaca (title, section, narrative/kesimpulan)
     * supaya DPJP tahu isi yang akan/sudah ditandatangani.
     */
    private void previewDokumen() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dokumen dulu."); return; }
        String idRes = tbTTE.getValueAt(row, COL_COMPOSITION).toString();
        if (idRes.isEmpty()) { JOptionPane.showMessageDialog(null, "Baris ini belum punya resource."); return; }
        String jenis = tbTTE.getValueAt(row, COL_JENIS).toString();
        String tipe = tipeResource(jenis);
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String hasil = api.getRest().exchange(link + "/" + tipe + "/" + idRes,
                    HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode r = mapper.readTree(hasil);
            StringBuilder h = new StringBuilder("<html><body style='font:11px tahoma;color:#222;width:560px'>");
            h.append("<div style='text-align:center'><b>PREVIEW DOKUMEN</b><br>")
             .append(escHtml(jenis)).append(" &middot; ").append(tipe).append("/").append(idRes).append("</div><hr>");
            String title = r.path("title").asText();
            if (!title.isEmpty()) h.append("<b>").append(escHtml(title)).append("</b><br>");
            JsonNode sec = r.path("section");
            if (sec.isArray() && sec.size() > 0) {
                for (JsonNode s : sec) {
                    h.append("<div style='margin-top:6px'><b>").append(escHtml(s.path("title").asText()))
                     .append("</b><br>").append(bersihkanDiv(s.path("text").path("div").asText())).append("</div>");
                }
            } else {
                String codeTxt = r.path("code").path("text").asText();
                String concl = r.path("conclusion").asText();
                String div = r.path("text").path("div").asText();
                if (!codeTxt.isEmpty()) h.append("<b>Kode:</b> ").append(escHtml(codeTxt)).append("<br>");
                if (!concl.isEmpty()) h.append("<b>Kesimpulan:</b> ").append(escHtml(concl)).append("<br>");
                if (!div.isEmpty()) h.append(bersihkanDiv(div));
                if (codeTxt.isEmpty() && concl.isEmpty() && div.isEmpty())
                    h.append("<i>Tidak ada narrative untuk ditampilkan; pakai tombol Provenance/Task untuk JSON mentah.</i>");
            }
            h.append("</body></html>");
            widget.editorpane ep = new widget.editorpane();
            ep.setContentType("text/html"); ep.setEditable(false); ep.setText(h.toString()); ep.setCaretPosition(0);
            widget.ScrollPane sc = new widget.ScrollPane();
            sc.setViewportView(ep); sc.setPreferredSize(new Dimension(580, 440));
            JOptionPane.showMessageDialog(null, sc, "Preview Dokumen", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal preview : " + e);
        }
    }

    /**
     * Cetak bukti TTE: GET Provenance signed, susun sertifikat ringkas (aktivitas
     * LA, penandatangan, waktu, perangkat, lokasi) lalu kirim ke printer lewat
     * JEditorPane.print(). Hanya untuk dokumen yang sudah ditandatangani.
     */
    private void cetakBukti() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dokumen dulu."); return; }
        String idProv = tbTTE.getValueAt(row, COL_PROVENANCE).toString();
        if (idProv.isEmpty()) { JOptionPane.showMessageDialog(null, "Baris ini belum punya Provenance."); return; }
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String hasil = api.getRest().exchange(link + "/Provenance/" + idProv,
                    HttpMethod.GET, requestEntity, String.class).getBody();
            JsonNode prov = mapper.readTree(hasil);
            JsonNode sigs = prov.path("signature");
            if (!sigs.isArray() || sigs.size() == 0) {
                JOptionPane.showMessageDialog(null, "Dokumen belum ditandatangani; belum ada bukti untuk dicetak.");
                return;
            }
            JsonNode s0 = sigs.get(0);
            String actDisp = "";
            JsonNode ac = prov.path("activity").path("coding");
            if (ac.isArray() && ac.size() > 0) actDisp = ac.get(0).path("display").asText();
            String device = "", lokasi = "";
            for (JsonNode c : prov.path("contained")) {
                String rt = c.path("resourceType").asText();
                if ("Device".equals(rt)) { JsonNode dn = c.path("deviceName"); if (dn.isArray() && dn.size() > 0) device = dn.get(0).path("name").asText(); }
                else if ("Location".equals(rt)) { JsonNode p = c.path("position"); if (!p.isMissingNode()) lokasi = p.path("latitude").asText() + ", " + p.path("longitude").asText(); }
            }
            StringBuilder h = new StringBuilder("<html><body style='font:12px serif;color:#000;width:600px;padding:14px'>");
            h.append("<div style='text-align:center'><b style='font-size:15px'>BUKTI TANDA TANGAN ELEKTRONIK</b><br>")
             .append("<span style='font-size:11px'>").append(escHtml(akses.getnamars())).append("</span></div><hr>");
            h.append("<table cellpadding='3'>");
            h.append(barisHtml("Jenis Dokumen", tbTTE.getValueAt(row, COL_JENIS).toString()));
            h.append(barisHtml("No. Rawat", tbTTE.getValueAt(row, COL_NORAWAT).toString()));
            h.append(barisHtml("Pasien", tbTTE.getValueAt(row, 3) + " (RM " + tbTTE.getValueAt(row, 2) + ")"));
            h.append(barisHtml("Aktivitas", actDisp));
            h.append(barisHtml("Ditandatangani oleh", s0.path("who").path("display").asText()));
            h.append(barisHtml("Waktu", s0.path("when").asText()));
            if (!device.isEmpty()) h.append(barisHtml("Perangkat", device));
            if (!lokasi.isEmpty()) h.append(barisHtml("Lokasi", lokasi));
            h.append(barisHtml("Provenance ID", idProv));
            h.append(barisHtml("Task ID", tbTTE.getValueAt(row, COL_TASK).toString()));
            h.append("</table><br><div style='font-size:10px;color:#333'>Dokumen ini ditandatangani secara elektronik "
                   + "melalui SATUSEHAT Mobile (BSrE). Keabsahan tanda tangan dijamin secara kriptografis oleh penyelenggara.</div>");
            h.append("<br><br><div style='text-align:right;font-size:11px'>Dicetak: ")
             .append(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date())).append("</div></body></html>");

            javax.swing.JEditorPane ep = new javax.swing.JEditorPane("text/html", h.toString());
            ep.setEditable(false);
            ep.setCaretPosition(0);
            javax.swing.JButton btnCetak = new javax.swing.JButton("Cetak");
            btnCetak.addActionListener(a -> { try { ep.print(); } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Gagal cetak : " + ex); } });
            javax.swing.JPanel pnl = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(ep);
            sp.setPreferredSize(new Dimension(640, 440));
            pnl.add(sp, java.awt.BorderLayout.CENTER);
            javax.swing.JPanel bar = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            bar.add(btnCetak);
            pnl.add(bar, java.awt.BorderLayout.SOUTH);
            JOptionPane.showMessageDialog(null, pnl, "Bukti TTE", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyiapkan bukti : " + e);
        }
    }

    /**
     * Periode pelayanan dari {@code Encounter.period} di SatuSehat, untuk mengisi
     * {@code Provenance.occurredPeriod}.
     *
     * Sengaja dibaca dari server, bukan dihitung ulang dari basis data lokal:
     * periode pada Provenance harus sama persis dengan Encounter yang menjadi
     * konteks Task-nya, dan menghitungnya dua kali dari dua sumber justru
     * membuka peluang keduanya berbeda.
     *
     * Mengembalikan {@code [start, end]}; keduanya bisa kosong. {@code end} kosong
     * berarti pasien masih dirawat -- itu sah, dan lebih baik daripada mengarang
     * waktu selesai. Kegagalan apa pun dikembalikan sebagai dua string kosong
     * sehingga pengajuan TTE tetap berjalan tanpa {@code occurredPeriod}.
     */
    private String[] periodeEncounter(String idEncounter) {
        if (idEncounter == null || idEncounter.trim().isEmpty()) return new String[]{"", ""};
        try {
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String enc = api.getRest().exchange(link + "/Encounter/" + idEncounter.trim(),
                    HttpMethod.GET, new HttpEntity(h), String.class).getBody();
            JsonNode p = mapper.readTree(enc).path("period");
            JsonNode mulai = p.get("start"), selesai = p.get("end");
            return new String[]{mulai == null ? "" : mulai.asText(),
                                selesai == null ? "" : selesai.asText()};
        } catch (Exception e) {
            System.out.println("Periode Encounter " + idEncounter + " gagal dibaca : " + e);
            return new String[]{"", ""};
        }
    }

    /**
     * Panel QR untuk dipindai SATUSEHAT Mobile.
     *
     * QR memuat "Task/{id}/{epochDetik}" (format dipastikan dari pindai QR nyata,
     * lihat {@link SatuSehatTTEPayload#isiQr}). Task UUID tetap; segmen timestamp
     * di-refresh tiap 10 detik sehingga QR ikut berganti, sesuai Flow 2a panduan
     * ("Task tetap sama, hanya token QR yang berubah"). Sisa waktu ke refresh
     * berikutnya ditampilkan sebagai hitung mundur (tick 1 detik).
     *
     * Gambar QR (termasuk logo Kemenkes di tengah dan error correction level H
     * yang menyertainya) dibuat {@link SatuSehatTTEPayload#gambarQr}.
     *
     * Pantau status (opsional, saklar {@code AUTOREFRESHTTE} di database.xml). Bila
     * AKTIF, panel membaca satu_sehat_tte tiap 3 detik — tabel itu diisi WEBHOOK
     * SatuSehat begitu SSM selesai. Saat webhook melapor semua dokumen berkesudahan,
     * panel MENUTUP QR otomatis lalu memunculkan notif (✅ TTE SUKSES bila semua
     * ditandatangani, atau "selesai dengan catatan" bila ada yang ditolak/gagal).
     * Bila MATI (default), status hanya lewat tombol Update Status. Anti-hang: tiap
     * pembacaan dibatasi 5 detik di utas terpisah; bila basis data tak merespon,
     * pemantauan dihentikan dan aplikasi tetap responsif (dialog tak pernah membeku).
     */
    private void tampilQr() {
        int row = tbTTE.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris dulu."); return; }
        final String idTask = tbTTE.getValueAt(row, COL_TASK).toString();
        if (idTask.isEmpty()) { JOptionPane.showMessageDialog(null, "Baris ini belum punya Task. Tekan Buat TTE dulu."); return; }
        final String noRawat = tbTTE.getValueAt(row, COL_NORAWAT).toString();
        final boolean autoRefresh = tteAutoRefreshAktif();

        final javax.swing.JLabel gambar = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
        final javax.swing.JLabel ket = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);

        javax.swing.JPanel isi = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
        isi.add(gambar, java.awt.BorderLayout.CENTER);

        final javax.swing.JLabel pantau = new javax.swing.JLabel(
                autoRefresh ? "Menunggu tanda tangan…" : "", javax.swing.SwingConstants.CENTER);
        pantau.setFont(pantau.getFont().deriveFont(java.awt.Font.BOLD));
        if (autoRefresh) {
            javax.swing.JPanel bawah = new javax.swing.JPanel(new java.awt.BorderLayout(2, 2));
            bawah.add(pantau, java.awt.BorderLayout.NORTH);
            bawah.add(ket, java.awt.BorderLayout.CENTER);
            isi.add(bawah, java.awt.BorderLayout.SOUTH);
        } else {
            isi.add(ket, java.awt.BorderLayout.SOUTH);
        }

        // QR di-refresh tiap 10 detik (Flow 2a). Tick 1 detik dipakai sebagai hitung
        // mundur; QR digambar ulang saat hitungan mencapai 0 lalu direset ke 10.
        final int[] sisa = {10};
        final String[] kontenQr = {""};
        final Runnable regenQr = () -> {
            try {
                long detik = System.currentTimeMillis() / 1000L;
                kontenQr[0] = SatuSehatTTEPayload.isiQr(idTask, detik);
                gambar.setIcon(new javax.swing.ImageIcon(SatuSehatTTEPayload.gambarQr(kontenQr[0], 300)));
            } catch (Exception e) {
                kontenQr[0] = "Gagal membuat QR : " + e;
            }
        };
        final Runnable tampilKet = () -> ket.setText(
                "<html><div style='width:320px;font-size:9px'>" + kontenQr[0]
                + "<br>QR baru dalam <b>" + sisa[0] + "</b> detik</div></html>");
        final javax.swing.Timer timer = new javax.swing.Timer(1000, null);
        timer.addActionListener(evt -> {
            sisa[0]--;
            if (sisa[0] <= 0) { regenQr.run(); sisa[0] = 10; }
            tampilKet.run();
        });
        regenQr.run();      // QR awal
        tampilKet.run();    // label awal "10 detik"
        timer.start();

        // --- Pantau hasil dari WEBHOOK. Tiap 3 detik baca satu_sehat_tte (yang
        //     ditulis webhook); saat webhook menandai sukses, tutup panel + notif.
        //     Baca DB di utas terpisah dgn batas 5 detik supaya tak menggantung. ---
        final String[] ringkas = {null};   // diisi saat semua dokumen sudah berkesudahan
        final javax.swing.Timer pemantau = new javax.swing.Timer(3000, null);
        final java.util.concurrent.ExecutorService exec =
                autoRefresh ? java.util.concurrent.Executors.newSingleThreadExecutor() : null;
        if (autoRefresh) {
            final java.util.concurrent.atomic.AtomicBoolean sibuk =
                    new java.util.concurrent.atomic.AtomicBoolean(false);
            pemantau.addActionListener(evt -> {
                if (!sibuk.compareAndSet(false, true)) return;   // pembacaan lalu belum kelar
                final java.util.concurrent.Future<int[]> f = exec.submit(() -> hitungStatusTaskDb(idTask));
                new Thread(() -> {
                    int[] n;
                    try {
                        n = f.get(5, java.util.concurrent.TimeUnit.SECONDS);   // batas anti-hang
                    } catch (java.util.concurrent.TimeoutException te) {
                        f.cancel(true);
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            pemantau.stop();
                            pantau.setText("<html><font color='#c00'>Pemantauan dihentikan: basis data tak merespon 5 detik. "
                                    + "Gunakan tombol Update Status.</font></html>");
                        });
                        sibuk.set(false);
                        return;
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() ->
                                pantau.setText("Pantau status gagal sekali (dicoba lagi): " + ex.getMessage()));
                        sibuk.set(false);
                        return;
                    }
                    final int[] nn = n;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        try {
                            int total = nn[0], signed = nn[1], ditolak = nn[2], gagal = nn[3], batal = nn[4];
                            if (total == 0) { pantau.setText("Task tak ditemukan lagi."); return; }
                            int belum = total - signed - ditolak - gagal - batal;
                            if (belum > 0) {
                                pantau.setText(total == 1 ? "Menunggu tanda tangan…"
                                        : "Menunggu tanda tangan… (" + belum + " dari " + total + " dokumen)");
                            } else {
                                timer.stop(); pemantau.stop();   // webhook sudah lapor; QR tak perlu lagi
                                String rk = signed > 0 ? signed + " ditandatangani" : "";
                                if (ditolak > 0) rk += (rk.isEmpty() ? "" : ", ") + ditolak + " ditolak";
                                if (gagal > 0)   rk += (rk.isEmpty() ? "" : ", ") + gagal + " gagal";
                                if (batal > 0)   rk += (rk.isEmpty() ? "" : ", ") + batal + " dibatalkan";
                                boolean semuaSukses = ditolak + gagal + batal == 0;
                                ringkas[0] = (semuaSukses ? "TTE SUKSES — dokumen sudah ditandatangani.\n"
                                        : "TTE selesai dengan catatan.\n") + rk;
                                if (!noRawat.isEmpty()) tampilDokumen(noRawat);   // grid ikut segar
                                java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(pantau);
                                if (w != null) w.dispose();   // panel QR tertutup otomatis begitu webhook lapor sukses
                            }
                        } finally {
                            sibuk.set(false);
                        }
                    });
                }, "pantau-tte-qr").start();
            });
            pemantau.start();
        }

        JOptionPane.showMessageDialog(null, isi, "QR TTE", JOptionPane.PLAIN_MESSAGE);
        timer.stop();       // dialog modal; hentikan setelah ditutup
        pemantau.stop();
        if (exec != null) exec.shutdownNow();
        if (ringkas[0] != null)   // panel ditutup otomatis karena semua sudah kelar
            JOptionPane.showMessageDialog(null, ringkas[0], "TTE Selesai", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Saklar auto-refresh TTE dari database.xml (AUTOREFRESHTTE). */
    private boolean tteAutoRefreshAktif() {
        String v = koneksiDB.AUTOREFRESHTTE().trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("ya") || v.equals("aktif") || v.equals("on");
    }

    /**
     * Baca status dokumen Task dari satu_sehat_tte (yang ditulis WEBHOOK SatuSehat),
     * kembalikan [total, signed, ditolak, gagal, dibatalkan]. Panel QR memakainya
     * untuk mendeteksi keberhasilan DARI WEBHOOK — bukan GET langsung ke SatuSehat —
     * dijalankan dgn batas waktu supaya tak menggantung bila basis data terkunci.
     */
    private int[] hitungStatusTaskDb(String idTask) throws Exception {
        int[] n = new int[5];
        try (PreparedStatement p = koneksi.prepareStatement(
                "select count(*), sum(status='Signed'), sum(status='Ditolak'), "
              + "sum(status='Gagal'), sum(status='Dibatalkan') from satu_sehat_tte where id_task=?")) {
            p.setString(1, idTask);
            try (java.sql.ResultSet rs = p.executeQuery()) {
                if (rs.next()) for (int i = 0; i < 5; i++) n[i] = rs.getInt(i + 1);
            }
        }
        return n;
    }

    // ====================================================================== UI

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppPilihSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        splitTTE = new javax.swing.JSplitPane();
        ScrollPasien = new widget.ScrollPane();
        tbPasien = new widget.Table();
        Scroll = new widget.ScrollPane();
        tbTTE = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnAll = new widget.Button();
        BtnBuat = new widget.Button();
        BtnRefresh = new widget.Button();
        BtnUpdateStatus = new widget.Button();
        BtnSinkron = new widget.Button();
        BtnJson = new widget.Button();
        BtnKeluar = new widget.Button();
        cmbFilterStatus = new widget.ComboBox();
        panelGlass9 = new widget.panelisi();
        jLabel15 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel17 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(150, 26));
        ppPilihSemua.addActionListener(evt -> { for (i = 0; i < tbTTE.getRowCount(); i++) tbTTE.setValueAt(true, i, COL_PILIH); });

        // Aksi per-baris dokumen (dipindah dari toolbar ke klik-kanan).
        jPopupMenu1.add(mkItem("Preview Dokumen", "/picture/b_print.png", this::previewDokumen));
        jPopupMenu1.add(mkItem("Lihat Provenance", "/picture/b_print.png", this::lihatProvenance));
        jPopupMenu1.add(mkItem("Lihat Task", "/picture/b_print.png", this::lihatTask));
        jPopupMenu1.add(mkItem("Verifikasi Tanda Tangan", "/picture/accept.png", this::verifikasiTTE));
        jPopupMenu1.add(mkItem("Cetak Bukti", "/picture/b_print.png", this::cetakBukti));
        jPopupMenu1.addSeparator();
        jPopupMenu1.add(mkItem("Tampilkan QR", "/picture/1360484263_barcode.png", this::tampilQr));
        jPopupMenu1.add(mkItem("Batalkan TTE", "/picture/stop_f2.png", this::batalkanTTE));
        jPopupMenu1.add(mkItem("Riwayat", "/picture/b_print.png", this::lihatRiwayat));
        jPopupMenu1.addSeparator();
        jPopupMenu1.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Hilangkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(150, 26));
        ppBersihkan.addActionListener(evt -> { for (i = 0; i < tbTTE.getRowCount(); i++) tbTTE.setValueAt(false, i, COL_PILIH); });
        jPopupMenu1.add(ppBersihkan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Tanda Tangan Elektronik Satu Sehat ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        splitTTE.setDividerLocation(400);
        splitTTE.setName("splitTTE"); // NOI18N

        ScrollPasien.setName("ScrollPasien"); // NOI18N
        ScrollPasien.setOpaque(true);
        tbPasien.setName("tbPasien"); // NOI18N
        ScrollPasien.setViewportView(tbPasien);
        splitTTE.setLeftComponent(ScrollPasien);

        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbTTE.setComponentPopupMenu(jPopupMenu1);
        tbTTE.setName("tbTTE"); // NOI18N
        Scroll.setViewportView(tbTTE);
        splitTTE.setRightComponent(Scroll);

        internalFrame1.add(splitTTE, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 132));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass8.add(LCount);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampilPasien(); });
        panelGlass8.add(BtnAll);

        BtnBuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); // NOI18N
        BtnBuat.setMnemonic('B');
        BtnBuat.setText("Buat TTE");
        BtnBuat.setToolTipText("Alt+B, membuat Provenance + Task untuk baris yang dicentang");
        BtnBuat.setName("BtnBuat"); // NOI18N
        BtnBuat.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnBuat.addActionListener(evt -> buatTTE());
        panelGlass8.add(BtnBuat);

        BtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnRefresh.setMnemonic('R');
        BtnRefresh.setText("Status");
        BtnRefresh.setToolTipText("Alt+R, memeriksa apakah dokumen sudah ditandatangani");
        BtnRefresh.setName("BtnRefresh"); // NOI18N
        BtnRefresh.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnRefresh.addActionListener(evt -> refreshStatus());
        panelGlass8.add(BtnRefresh);

        BtnUpdateStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnUpdateStatus.setMnemonic('U');
        BtnUpdateStatus.setText("Update Status");
        BtnUpdateStatus.setToolTipText("Alt+U, membaca status Task di SatuSehat — mendeteksi Ditandatangani maupun Ditolak");
        BtnUpdateStatus.setName("BtnUpdateStatus"); // NOI18N
        BtnUpdateStatus.setPreferredSize(new java.awt.Dimension(130, 30));
        BtnUpdateStatus.addActionListener(evt -> updateStatusTTE());
        panelGlass8.add(BtnUpdateStatus);

        BtnSinkron.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnSinkron.setText("Sinkron Praktisi");
        BtnSinkron.setToolTipText("Mengisi IHS semua DPJP ke cache (GET Practitioner per NIK)");
        BtnSinkron.setName("BtnSinkron"); // NOI18N
        BtnSinkron.setPreferredSize(new java.awt.Dimension(140, 30));
        BtnSinkron.addActionListener(evt -> sinkronPraktisi());
        panelGlass8.add(BtnSinkron);

        BtnJson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); // NOI18N
        BtnJson.setText("Simpan JSON");
        BtnJson.setToolTipText("Simpan payload/response FHIR (Provenance + Task) baris terpilih ke file .json untuk bukti UAT");
        BtnJson.setName("BtnJson"); // NOI18N
        BtnJson.setPreferredSize(new java.awt.Dimension(130, 30));
        BtnJson.addActionListener(evt -> simpanJsonUAT());
        panelGlass8.add(BtnJson);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(evt -> dispose());
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel15.setText("Tgl.Kunjungan :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass9.add(jLabel15);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-01-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("s.d.");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(24, 23));
        panelGlass9.add(jLabel17);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-01-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel16.setText("Key Word :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(210, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampilPasien();
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(evt -> tampilPasien());
        panelGlass9.add(BtnCari);

        widget.Label lblStatus = new widget.Label();
        lblStatus.setText("Status :");
        lblStatus.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(lblStatus);
        cmbFilterStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
            "-- Semua Status --", "Belum", "Requested", "Signed", "Ditolak", "Gagal", "Dibatalkan" }));
        cmbFilterStatus.setPreferredSize(new java.awt.Dimension(140, 23));
        cmbFilterStatus.addActionListener(evt -> { if (!noRawatAktif.isEmpty()) tampilDokumen(noRawatAktif); });
        panelGlass9.add(cmbFilterStatus);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem ppPilihSemua, ppBersihkan;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8, panelGlass9;
    private widget.Label jLabel7, jLabel15, jLabel16, jLabel17, LCount;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.TextBox TCari;
    private widget.Button BtnAll, BtnBuat, BtnRefresh, BtnUpdateStatus, BtnSinkron, BtnJson, BtnKeluar, BtnCari;
    private widget.ComboBox cmbFilterStatus;
    private widget.ScrollPane Scroll, ScrollPasien;
    private widget.Table tbTTE, tbPasien;
    private javax.swing.JSplitPane splitTTE;

    @Override
    public void setVisible(boolean visible) {
        if (visible) tampilPasien();
        super.setVisible(visible);
    }
}
