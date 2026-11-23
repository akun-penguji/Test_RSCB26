/*
 * IsoTime.java — utility konversi timestamp WIB (Asia/Jakarta) ke ISO-8601 UTC
 * dengan offset +00:00, sesuai requirement Playbook Klaim BPJS-K v1.3 Kemkes:
 *   "format waktu WAJIB UTC+00 (WIB-7, WITA-8, WIT-9), tanggal tidak boleh
 *    < 3 Juni 2014".
 *
 * Sebelum:
 *   "\"date\": \"" + tgl + "T" + jam + "+07:00\""
 *   → 2026-05-27T14:00:00+07:00  (WIB offset, validator strict reject)
 *
 * Sesudah (helper ini):
 *   "\"date\": \"" + IsoTime.toUtc(tgl, jam) + "\""
 *   → 2026-05-27T07:00:00+00:00  (geser -7 jam ke UTC)
 *
 * Idempotent untuk fallback: kalau parse gagal, return input + "+07:00" supaya
 * tidak crash. Empty/null input return empty string.
 */
package fungsi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class IsoTime {

    private static final ZoneId WIB = ZoneId.of("Asia/Jakarta");
    private static final DateTimeFormatter OUT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final DateTimeFormatter IN_T =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter IN_SPACE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private IsoTime() { /* utility */ }

    /**
     * Convert single WIB datetime string ("yyyy-MM-dd HH:mm:ss" atau
     * "yyyy-MM-ddTHH:mm:ss") ke ISO UTC dengan offset +00:00.
     * Empty/null → "". Parse failure → fallback "{input dgn T}+07:00".
     */
    public static String toUtc(String wibDateTime) {
        if (wibDateTime == null) return "";
        String s = wibDateTime.trim();
        if (s.isEmpty() || s.startsWith("0000")) return "";
        try {
            String normalized = s.contains("T") ? s : s.replace(" ", "T");
            if (normalized.length() > 19) normalized = normalized.substring(0, 19);
            LocalDateTime ldt;
            try {
                ldt = LocalDateTime.parse(normalized, IN_T);
            } catch (Exception e) {
                ldt = LocalDateTime.parse(normalized.replace("T", " "), IN_SPACE);
            }
            return ldt.atZone(WIB).withZoneSameInstant(ZoneOffset.UTC).format(OUT);
        } catch (Exception e) {
            return s.replace(" ", "T") + "+07:00";
        }
    }

    /**
     * Convert separate tanggal "yyyy-MM-dd" + jam "HH:mm[:ss]" → ISO UTC.
     */
    public static String toUtc(String tglWib, String jamWib) {
        if (tglWib == null || jamWib == null) return "";
        String t = tglWib.trim();
        String j = jamWib.trim();
        if (t.isEmpty() || j.isEmpty() || t.startsWith("0000")) return "";
        if (j.length() == 5) j = j + ":00";       // HH:mm → HH:mm:00
        else if (j.length() == 8) { /* HH:mm:ss OK */ }
        else if (j.length() < 5)  j = (j + ":00:00").substring(0, 8);
        return toUtc(t + "T" + j);
    }
}
