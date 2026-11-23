package keuangan;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Helper logging ke tabel {@code audit_nilai_kritis} — wajib untuk akreditasi
 * KARS PAP 3 (pelaporan hasil kritis).
 *
 * <p>Dipanggil dari {@code DlgPeriksaLaboratorium.validateKritisDanKonfirmasi()}
 * setelah user konfirmasi "Ya, Simpan" — hanya entry valid yang di-log.
 */
public class NilaiKritisAudit {

    private NilaiKritisAudit() {}

    /** One-row audit entry. Builder pattern untuk readability. */
    public static final class Entry {
        public String noRawat;
        public String noRkmMedis;
        public java.sql.Date tglKejadian;
        public java.sql.Time jamKejadian;
        public String petugasNip;
        public String petugasNama;
        public int    idTemplate;
        public String kdJenisPrw;
        public String namaPemeriksaan;
        public String nilaiHasil;
        public String kritisMin;       // nullable
        public String kritisMax;       // nullable
        public String satuan;
        public String kdDokterPerujuk; // nullable
        public String keterangan;      // nullable
    }

    /**
     * INSERT satu row ke audit_nilai_kritis. Tidak throw — failure di-log saja.
     *
     * @return id (auto-increment) row yang baru di-insert, atau -1 kalau gagal.
     *         Dipakai pemanggil untuk langsung membuka DlgLaporKritis (Item #3).
     */
    public static int log(Connection conn, Entry e) {
        if (e == null) return -1;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO audit_nilai_kritis " +
                "(no_rawat, no_rkm_medis, tgl_kejadian, jam_kejadian, petugas_nip, petugas_nama, " +
                " id_template, kd_jenis_prw, nama_pemeriksaan, nilai_hasil, kritis_min, kritis_max, " +
                " satuan, kd_dokter_perujuk, status_notifikasi, keterangan) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.noRawat);
            ps.setString(2, e.noRkmMedis);
            ps.setDate(3, e.tglKejadian != null ? e.tglKejadian
                        : new java.sql.Date(System.currentTimeMillis()));
            ps.setTime(4, e.jamKejadian != null ? e.jamKejadian
                        : new java.sql.Time(System.currentTimeMillis()));
            ps.setString(5, nz(e.petugasNip));
            ps.setString(6, nz(e.petugasNama));
            ps.setInt(7, e.idTemplate);
            ps.setString(8, nz(e.kdJenisPrw));
            ps.setString(9, nz(e.namaPemeriksaan));
            ps.setString(10, nz(e.nilaiHasil));
            ps.setString(11, e.kritisMin);   // biarkan null kalau kosong
            ps.setString(12, e.kritisMax);
            ps.setString(13, nz(e.satuan));
            ps.setString(14, nz(e.kdDokterPerujuk));
            ps.setString(15, "PENDING");      // default — Item #7 akan update kalau dokter di-notify
            ps.setString(16, nz(e.keterangan));
            ps.executeUpdate();
            try (java.sql.ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
            }
        } catch (Exception ex) {
            // Audit failure jangan blok save — log error ke console saja.
            System.out.println("NilaiKritisAudit.log failed: " + ex);
        }
        return -1;
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
