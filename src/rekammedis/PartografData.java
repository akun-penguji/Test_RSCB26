package rekammedis;

/**
 * Sumber kebenaran TUNGGAL untuk geometri &amp; logika partograf (WHO).
 *
 * Dipakai bersama oleh:
 *  - grafik Java ({@code DlgPartografGrafik}),
 *  - cetak HTML ({@code RMPartograf.cetakGrafikPartograf} &amp; {@code cetakCatatanPersalinan}),
 *  - auto-isi checklist "melewati garis waspada" ({@code RMPartograf.deteksiLewatGarisWaspada}).
 *
 * Tujuan: hilangkan duplikasi logika yang dulu menyebabkan bug garis waspada
 * (offset, posisi garis, deteksi lewat garis) — sekarang semua di satu tempat.
 *
 * Konvensi: H = jam sejak mulai fase aktif (sumbu X), p = pembukaan serviks (cm).
 *  - Garis waspada : p = 4 + H  (4 cm di jam-0, menanjak 1 cm/jam sampai 10 cm di jam-6).
 *  - Garis bertindak: paralel, digeser +4 jam ke kanan (p = H).
 *  - Titik pembukaan pertama (fase aktif) "ditempelkan" ke garis waspada dengan menggeser
 *    seluruh observasi ke kanan sebesar offset = (pembukaan_awal - 4) jam.
 */
public final class PartografData {

    private PartografData() {}

    // ---- Garis waspada (posisi tetap, satuan: jam / cm) ----
    public static final double WASPADA_JAM_AWAL  = 0;
    public static final double WASPADA_CM_AWAL   = 4;
    public static final double WASPADA_JAM_AKHIR = 6;
    public static final double WASPADA_CM_AKHIR  = 10;

    // ---- Garis bertindak: paralel garis waspada, geser +4 jam ----
    public static final double BERTINDAK_GESER_JAM = 4;

    /**
     * Offset jam agar titik pembukaan pertama (fase aktif, &ge; 4 cm) jatuh tepat di garis waspada.
     * offset = pembukaan_awal - 4, dibatasi 0..6. Bila belum fase aktif, 0.
     */
    public static double offsetJam(double pembukaanAwal) {
        if (pembukaanAwal < 4) return 0;
        double off = Math.min(pembukaanAwal, 10) - 4;
        return off < 0 ? 0 : off;
    }

    /** Offset dari daftar pembukaan terurut waktu (ambil titik &ge; 4 cm pertama). */
    public static double offsetJam(double[] pembukaanUrut) {
        if (pembukaanUrut != null) {
            for (double p : pembukaanUrut) {
                if (p >= 4) return offsetJam(p);
            }
        }
        return 0;
    }

    /** True bila ada minimal satu observasi fase aktif (pembukaan &ge; 4 cm). */
    public static boolean adaFaseAktif(double[] pembukaanUrut) {
        if (pembukaanUrut != null) {
            for (double p : pembukaanUrut) {
                if (p >= 4) return true;
            }
        }
        return false;
    }

    /**
     * Status grafik pembukaan terhadap garis waspada/bertindak.
     * @return 0 = tidak lewat, 1 = lewat garis waspada, 2 = lewat garis bertindak.
     * jamKe &amp; pembukaan harus terurut waktu (jam MENTAH, sebelum digeser).
     */
    public static int statusLewatGaris(double[] jamKe, double[] pembukaan) {
        if (jamKe == null || pembukaan == null) return 0;
        double off = offsetJam(pembukaan);
        boolean waspada = false, bertindak = false;
        int n = Math.min(jamKe.length, pembukaan.length);
        for (int i = 0; i < n; i++) {
            double p = pembukaan[i];
            if (p < 4) continue;                 // hanya titik fase aktif
            double h = jamKe[i] + off;           // jam setelah digeser
            if (h > (p - 4) + 0.001) waspada = true;   // di kanan garis waspada
            if (h > p + 0.001)       bertindak = true; // di kanan garis bertindak
        }
        return bertindak ? 2 : (waspada ? 1 : 0);
    }
}
