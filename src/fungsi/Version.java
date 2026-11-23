/*
 * Version.java — central constant untuk versi build SIMRSKhanza-RSUDCabangBungin.
 *
 * UPDATE setiap commit fitur baru. Format: vYYYY.MM.DD.NN (NN = nomor commit dalam hari).
 *
 * Display di:
 *   - frmUtama title bar (tampilkan + last update + latest feature)
 *   - About dialog (kalau ada)
 *
 * Detail per versi: lihat RELEASE_NOTES.md di root project.
 */
package fungsi;

public final class Version {

    public static final String CURRENT      = "v2026.06.28.01";
    public static final String LAST_UPDATE  = "2026-06-28";
    public static final String LATEST       = "Partograf: deteksi garis waspada/bertindak WHO (auto-isi checklist Kala I) + pengingat interval pencatatan + report Cetak Observasi (rptPartografObservasi) — cherry-pick dari RSUD Tualang/rsthb.";
    public static final String CHANGELOG_URL = "RELEASE_NOTES.md";

    private Version() { /* utility */ }

    /** Format singkat untuk title bar / status bar. */
    public static String displayShort() {
        return CURRENT + " (" + LAST_UPDATE + ")";
    }

    /** Format lengkap untuk About dialog. */
    public static String displayFull() {
        return "SIMRSKhanza-RSUDCabangBungin " + CURRENT
             + "\nLast update: " + LAST_UPDATE
             + "\nLatest feature: " + LATEST
             + "\nChangelog: " + CHANGELOG_URL;
    }
}
