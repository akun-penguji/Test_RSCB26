package fungsi;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Pencatat waktu simpan SEP ke tabel terpisah `hbk_log_simpan_sep`.
 *
 * Kembaran dari kelas dengan nama sama di proyek simrskhanza-rshbkdev —
 * keduanya menulis ke tabel yang sama, hanya beda project sehingga kelasnya
 * digandakan.
 *
 * Sengaja TIDAK menyentuh tabel `bridging_sep` sama sekali (tanpa kolom baru,
 * tanpa trigger) supaya alur produksi penyimpanan SEP tidak berubah.
 *
 * Aturan main:
 *  - dipanggil SETELAH penyimpanan SEP dinyatakan berhasil;
 *  - semua kegagalan ditelan di sini — pencatatan tidak boleh sampai membuat
 *    proses SEP gagal atau memunculkan dialog error ke petugas;
 *  - satu baris per no_sep (nomor SEP unik global dari BPJS; satu pasien bisa
 *    punya SEP ralan dan SEP ranap sekaligus, masing-masing dapat barisnya
 *    sendiri). Simpan ulang hanya memperbarui waktu_terakhir dan menambah
 *    jumlah_simpan — waktu_simpan tetap yang pertama.
 */
public final class LogSimpanSEP {

    private LogSimpanSEP() {
    }

    private static final String SQL =
            "insert into hbk_log_simpan_sep "
          + "(no_sep, no_rawat, waktu_simpan, waktu_terakhir, jumlah_simpan, sumber, petugas, ip) "
          + "values (?, ?, now(), now(), 1, ?, ?, ?) "
          + "on duplicate key update no_rawat=values(no_rawat), waktu_terakhir=now(), "
          + "jumlah_simpan=jumlah_simpan+1, sumber=values(sumber), petugas=values(petugas), ip=values(ip)";

    /**
     * @param noSep    nomor SEP hasil bridging
     * @param noRawat  no.rawat pasien
     * @param sumber   nama form pemanggil, mis. "BPJSDataSEP"
     */
    public static void catat(String noSep, String noRawat, String sumber) {
        if (noSep == null || noSep.trim().isEmpty() || noRawat == null || noRawat.trim().isEmpty()) {
            return;
        }
        PreparedStatement ps = null;
        try {
            Connection koneksi = koneksiDB.condb();
            if (koneksi == null) {
                return;
            }
            ps = koneksi.prepareStatement(SQL);
            ps.setString(1, noSep.trim());
            ps.setString(2, noRawat.trim());
            ps.setString(3, sumber == null ? "" : sumber);
            ps.setString(4, nz(akses.getkode()));
            ps.setString(5, nz(akses.getalamatip()));
            ps.executeUpdate();
        } catch (Exception e) {
            // Diamkan: pencatatan waktu tidak boleh mengganggu penyimpanan SEP.
            System.out.println("Notif LogSimpanSEP : " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                /* abaikan */
            }
        }
    }

    private static final String SQL_EDIT =
            "update hbk_log_simpan_sep set waktu_terakhir=now(), jumlah_simpan=jumlah_simpan+1, "
          + "sumber=?, petugas=?, ip=? where no_sep=?";

    /**
     * Dipanggil setelah SEP berhasil DIUBAH (bukan dibuat).
     *
     * Sengaja UPDATE saja, tidak insert: SEP yang dibuat sebelum fitur ini
     * terpasang belum punya baris log, dan kalau di-insert di sini waktu_simpan
     * akan terisi waktu edit — seolah SEP itu baru dibuat hari ini. Lebih baik
     * kolom Tgl.Buat SEP-nya tetap kosong daripada menampilkan waktu yang salah.
     */
    public static void catatEdit(String noSep, String sumber) {
        if (noSep == null || noSep.trim().isEmpty()) {
            return;
        }
        PreparedStatement ps = null;
        try {
            Connection koneksi = koneksiDB.condb();
            if (koneksi == null) {
                return;
            }
            ps = koneksi.prepareStatement(SQL_EDIT);
            ps.setString(1, sumber == null ? "" : sumber);
            ps.setString(2, nz(akses.getkode()));
            ps.setString(3, nz(akses.getalamatip()));
            ps.setString(4, noSep.trim());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif LogSimpanSEP.catatEdit : " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                /* abaikan */
            }
        }
    }

    /**
     * Dipanggil setelah SEP berhasil DIHAPUS, supaya baris log tidak menggantung.
     *
     * Penting bukan cuma soal kebersihan tabel: kalau nomor SEP yang sama muncul
     * lagi setelah dihapus, aturan on-duplicate di {@link #catat} akan
     * mempertahankan waktu_simpan yang lama sehingga Tgl.Buat SEP jadi salah.
     */
    public static void hapus(String noSep) {
        if (noSep == null || noSep.trim().isEmpty()) {
            return;
        }
        PreparedStatement ps = null;
        try {
            Connection koneksi = koneksiDB.condb();
            if (koneksi == null) {
                return;
            }
            ps = koneksi.prepareStatement("delete from hbk_log_simpan_sep where no_sep=?");
            ps.setString(1, noSep.trim());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif LogSimpanSEP.hapus : " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                /* abaikan */
            }
        }
    }

    private static String nz(String s) {
        return s == null ? "" : (s.length() > 20 ? s.substring(0, 20) : s);
    }
}
