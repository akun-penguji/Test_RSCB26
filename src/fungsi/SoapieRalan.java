package fungsi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Pembantu "Ambil SOAPIE" untuk form Penilaian Awal (Medis/Keperawatan) Rawat Jalan.
 * Mengambil isian SOAPIE dari tabel pemeriksaan_ralan dan mengisikannya ke field
 * form yang sesuai (bila ada). Baris dokter vs perawat dibedakan dari kolom nip:
 * dokter = nip ada di tabel dokter; perawat = nip tidak ada di tabel dokter.
 */
public final class SoapieRalan {

    private SoapieRalan() {}

    /**
     * Ambil 1 baris SOAPIE terbaru dari pemeriksaan_ralan untuk no_rawat.
     * @param dokter true = baris input dokter; false = baris input perawat.
     * @return array 15 elemen atau null bila belum ada baris yang cocok:
     *         [0]=keluhan(S) [1]=pemeriksaan(O) [2]=penilaian(A) [3]=rtl(P) [4]=instruksi(I) [5]=evaluasi(E)
     *         [6]=tensi [7]=nadi [8]=respirasi [9]=suhu [10]=spo2 [11]=tinggi [12]=berat [13]=gcs [14]=kesadaran.
     */
    public static String[] ambil(String noRawat, boolean dokter) {
        String cond = dokter
                ? "pr.nip in (select kd_dokter from dokter)"
                : "pr.nip not in (select kd_dokter from dokter)";
        String sql = "select ifnull(pr.keluhan,''), ifnull(pr.pemeriksaan,''), ifnull(pr.penilaian,''), "
                   + "ifnull(pr.rtl,''), ifnull(pr.instruksi,''), ifnull(pr.evaluasi,''), "
                   + "ifnull(pr.tensi,''), ifnull(pr.nadi,''), ifnull(pr.respirasi,''), ifnull(pr.suhu_tubuh,''), "
                   + "ifnull(pr.spo2,''), ifnull(pr.tinggi,''), ifnull(pr.berat,''), ifnull(pr.gcs,''), ifnull(pr.kesadaran,'') "
                   + "from pemeriksaan_ralan pr where pr.no_rawat=? and " + cond
                   + " order by pr.tgl_perawatan desc, pr.jam_rawat desc limit 1";
        Connection k = koneksiDB.condb(); // koneksi bersama — jangan ditutup
        try (PreparedStatement p = k.prepareStatement(sql)) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    String[] a = new String[15];
                    for (int i = 0; i < 15; i++) a[i] = r.getString(i + 1);
                    return a;
                }
            }
        } catch (Exception e) {
            System.out.println("SoapieRalan.ambil : " + e);
        }
        return null;
    }

    /**
     * Isi field HANYA bila field masih kosong (untuk TTV nilai tunggal: tensi,
     * nadi, suhu, dll — jangan di-append). Field/nilai kosong dilewati.
     */
    public static void isiKosong(javax.swing.text.JTextComponent ta, String v) {
        if (ta == null || v == null) return;
        v = v.trim();
        if (v.isEmpty()) return;
        String lama = ta.getText() == null ? "" : ta.getText().trim();
        if (lama.isEmpty()) ta.setText(v);
    }

    /**
     * Tambahkan teks ke bawah isi field (append). Field/teks kosong dilewati,
     * sehingga aman dipanggil untuk field yang mungkin tidak ada di sebuah form
     * (cukup jangan panggil bila field tak ada).
     */
    public static void tambah(javax.swing.text.JTextComponent ta, String v) {
        if (ta == null || v == null) return;
        v = v.trim();
        if (v.isEmpty()) return;
        String lama = ta.getText() == null ? "" : ta.getText().trim();
        ta.setText(lama.isEmpty() ? v : lama + "\n" + v);
    }

    /** Gabung dua bagian rencana (rtl + instruksi) jadi satu blok. */
    public static String gabung(String a, String b) {
        a = a == null ? "" : a.trim();
        b = b == null ? "" : b.trim();
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        return a + "\n" + b;
    }
}
