/*
 * SatuSehatEpisodeOfCareHelper — shared logic untuk handle FHIR EpisodeOfCare.
 *
 * Tujuan: hindari duplikasi code EOC yang sebelumnya inline di
 * SatuSehatKirimANC + SatuSehatKirimEncounter. Pakai shared helper untuk:
 *   - getOrCreate: cek EOC existing (no_rkm + type + status='active'),
 *                  kalau tidak ada → POST /EpisodeOfCare → simpan local
 *   - updateStatus: PUT /EpisodeOfCare/{id} update status (mis. active → finished)
 *   - getTypeByPoli: map nama poli ke type code Kemkes (9 jenis program)
 *   - linkEncounter: insert ke tabel relasi satu_sehat_encounter_episodeofcare
 *
 * Lihat dokumentasi lengkap: SIMRSKhanza-Trimitra/EPISODE_OF_CARE.md
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class SatuSehatEpisodeOfCareHelper {

    private static final ApiSatuSehat api = new ApiSatuSehat();
    private static final SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();
    private static final ObjectMapper mapper = new ObjectMapper();

    private SatuSehatEpisodeOfCareHelper() { /* utility class, no instances */ }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Get-or-create EpisodeOfCare untuk pasien + type.
     * Kalau ada EOC active matching → return id_eoc existing (REUSE).
     * Kalau tidak ada → POST /EpisodeOfCare ke SatuSehat, simpan ke local, return new id.
     * Return "" kalau gagal (network, validation, dll).
     *
     * @param noRkmMedis        No.RM pasien
     * @param idPasienIhs       IHS Patient ID (dari cekViaSatuSehat.tampilIDPasien)
     * @param namaPasien        Nama pasien (untuk display di JSON)
     * @param typeCode          mis. "TB-DOTS", "ANC" (dari getTypeByPoli atau dialog)
     * @param typeDisplay       mis. "TB-DOTS Program"
     * @param conditionKdPenyakit ICD-10 (boleh null/empty)
     * @param idConditionFhir   id Condition di SatuSehat (boleh null/empty) — untuk diagnosis.reference
     * @param periodStart       YYYY-MM-DD (default hari ini kalau null/empty)
     * @param careManagerNik    NIP pegawai case manager (boleh null/empty)
     * @return id_eoc UUID dari SatuSehat, atau "" kalau gagal
     */
    public static String getOrCreate(
            String noRkmMedis,
            String idPasienIhs,
            String namaPasien,
            String typeCode,
            String typeDisplay,
            String conditionKdPenyakit,
            String idConditionFhir,
            String periodStart,
            String careManagerNik) {
        // Delegasi dengan perilaku PERSIS seperti sebelumnya: status awal "active"
        // dan reuse hanya terhadap EOC berstatus 'active'. Jangan diubah — dipakai
        // modul ANC, Kanker, dan Encounter.
        return getOrCreateCore(noRkmMedis, idPasienIhs, namaPasien, typeCode, typeDisplay,
                conditionKdPenyakit, idConditionFhir, periodStart, careManagerNik,
                "active", false);
    }

    /**
     * Varian untuk registry yang TIDAK dimulai dari status "active" — mis. Registrasi
     * Jantung yang membuka episode sebagai "waitlist" (suspek) sebelum diagnosis
     * terkonfirmasi.
     *
     * Bedanya dengan {@link #getOrCreate}:
     *   1. status awal dapat ditentukan (waitlist/active/...)
     *   2. reuse memakai {@link #findLiveEoc} — mencakup waitlist/active/onhold,
     *      bukan hanya active
     *
     * Poin 2 penting: kalau memakai findActiveEoc, EOC berstatus "waitlist" tidak
     * akan ditemukan pada kunjungan berikutnya sehingga terbuat EOC kedua dan
     * registry longitudinal pecah.
     *
     * @param statusAwal status EpisodeOfCare saat dibuat (mis. "waitlist")
     */
    public static String getOrCreate(
            String noRkmMedis,
            String idPasienIhs,
            String namaPasien,
            String typeCode,
            String typeDisplay,
            String conditionKdPenyakit,
            String idConditionFhir,
            String periodStart,
            String careManagerNik,
            String statusAwal) {
        return getOrCreateCore(noRkmMedis, idPasienIhs, namaPasien, typeCode, typeDisplay,
                conditionKdPenyakit, idConditionFhir, periodStart, careManagerNik,
                (statusAwal == null || statusAwal.isEmpty()) ? "active" : statusAwal, true);
    }

    private static String getOrCreateCore(
            String noRkmMedis,
            String idPasienIhs,
            String namaPasien,
            String typeCode,
            String typeDisplay,
            String conditionKdPenyakit,
            String idConditionFhir,
            String periodStart,
            String careManagerNik,
            String statusAwal,
            boolean reuseSemuaYangHidup) {

        if (noRkmMedis == null || noRkmMedis.isEmpty()
                || typeCode == null || typeCode.isEmpty()
                || idPasienIhs == null || idPasienIhs.isEmpty()) {
            return "";
        }

        // 1. Cek REUSE — EOC existing untuk pasien + type
        String existing = reuseSemuaYangHidup
                ? findLiveEoc(noRkmMedis, typeCode)
                : findActiveEoc(noRkmMedis, typeCode);
        if (!existing.isEmpty()) return existing;

        // 2. Tidak ada → POST baru
        if (periodStart == null || periodStart.isEmpty()) {
            periodStart = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        }
        String idCareManager = "";
        if (careManagerNik != null && !careManagerNik.isEmpty()) {
            idCareManager = cekViaSatuSehat.tampilIDParktisi(careManagerNik);
        }

        String json = buildJson(null, noRkmMedis, idPasienIhs, namaPasien,
                                typeCode, typeDisplay, conditionKdPenyakit, idConditionFhir,
                                statusAwal, periodStart, null, idCareManager);

        try {
            String link = koneksiDB.URLFHIRSATUSEHAT();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity<String> req = new HttpEntity<>(json, headers);

            System.out.println("URL : " + link + "/EpisodeOfCare");
            System.out.println("Request JSON : " + json);
            String resp = api.getRest().exchange(link + "/EpisodeOfCare", HttpMethod.POST, req, String.class).getBody();
            System.out.println("Result JSON : " + resp);

            JsonNode root = mapper.readTree(resp);
            String idEoc = root.path("id").asText();
            if (idEoc == null || idEoc.isEmpty()) return "";

            // 3. Simpan local
            try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                    "insert into satu_sehat_episodeofcare (id_eoc,no_rkm_medis,type_code,type_display,"
                  + "status,period_start,condition_kd_penyakit,care_manager_nik) values (?,?,?,?,?,?,?,?)")) {
                ps.setString(1, idEoc);
                ps.setString(2, noRkmMedis);
                ps.setString(3, typeCode);
                ps.setString(4, typeDisplay);
                ps.setString(5, statusAwal);
                ps.setString(6, periodStart);
                if (conditionKdPenyakit != null && !conditionKdPenyakit.isEmpty()) ps.setString(7, conditionKdPenyakit);
                else ps.setNull(7, java.sql.Types.VARCHAR);
                if (careManagerNik != null && !careManagerNik.isEmpty()) ps.setString(8, careManagerNik);
                else ps.setNull(8, java.sql.Types.VARCHAR);
                ps.executeUpdate();
            }
            return idEoc;
        } catch (Exception e) {
            System.out.println("Notif EpisodeOfCare.getOrCreate : " + e);
            return "";
        }
    }

    /**
     * Lookup EOC active existing untuk pasien + type.
     * Return id_eoc kalau ada, "" kalau tidak ada.
     */
    public static String findActiveEoc(String noRkmMedis, String typeCode) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "select id_eoc from satu_sehat_episodeofcare "
              + "where no_rkm_medis=? and type_code=? and status='active' "
              + "order by tgl_create desc limit 1")) {
            ps.setString(1, noRkmMedis);
            ps.setString(2, typeCode);
            ResultSet rs = ps.executeQuery();
            String id = rs.next() ? rs.getString("id_eoc") : "";
            rs.close();
            return id == null ? "" : id;
        } catch (Exception e) {
            System.out.println("Notif findActiveEoc : " + e);
            return "";
        }
    }

    /**
     * Lookup EOC yang MASIH HIDUP (belum selesai) untuk pasien + type —
     * mencakup waitlist, active, dan onhold.
     *
     * Dibuat terpisah dari {@link #findActiveEoc} supaya perilaku modul lama
     * (ANC, Kanker, Encounter) tidak berubah. Dipakai registry yang episodenya
     * dimulai sebagai "waitlist", di mana findActiveEoc akan gagal menemukan
     * EOC yang sudah ada dan menyebabkan duplikasi.
     *
     * Status 'finished' dan 'cancelled' sengaja TIDAK dianggap hidup: registry
     * yang sudah ditutup (mis. pasien meninggal) tidak boleh dipakai ulang.
     *
     * @return id_eoc kalau ada, "" kalau tidak ada
     */
    public static String findLiveEoc(String noRkmMedis, String typeCode) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "select id_eoc from satu_sehat_episodeofcare "
              + "where no_rkm_medis=? and type_code=? "
              + "and status in ('waitlist','active','onhold') "
              + "order by tgl_create desc limit 1")) {
            ps.setString(1, noRkmMedis);
            ps.setString(2, typeCode);
            ResultSet rs = ps.executeQuery();
            String id = rs.next() ? rs.getString("id_eoc") : "";
            rs.close();
            return id == null ? "" : id;
        } catch (Exception e) {
            System.out.println("Notif findLiveEoc : " + e);
            return "";
        }
    }

    /**
     * Ubah status EpisodeOfCare untuk registry longitudinal (mis. Registrasi Jantung).
     *
     * <p><b>Catatan penyimpangan dari playbook.</b> Playbook Registrasi Jantung
     * meminta transisi status memakai HTTP <b>PATCH</b>. Saat ini PATCH belum bisa
     * dipakai di codebase ini:
     *
     * <ul>
     *   <li>{@code org.springframework.web-3.1.0.M2.jar} berada lebih dulu di
     *       javac.classpath (baris 475) daripada spring-web 5.3.30 (baris 927),
     *       dan {@code HttpMethod} versi 3.1 belum mengenal konstanta PATCH.</li>
     *   <li>Jalur alternatif lewat Apache HttpClient juga tidak aman: jar yang
     *       menang di classpath ({@code apache-httpcomponents-httpclient.jar})
     *       tidak memuat {@code HttpPatch}, sehingga kelasnya akan diambil dari
     *       httpclient-4.5.1 sementara kelas induknya dari jar lain — rawan
     *       {@code NoSuchMethodError} saat runtime.</li>
     * </ul>
     *
     * <p>Karena itu method ini mendelegasikan ke {@link #updateStatus} yang memakai
     * <b>PUT</b> — operasi FHIR yang sah dan sudah terpakai di modul lain. Efek
     * akhirnya sama: status EpisodeOfCare berubah dan salinan lokal ikut diperbarui.
     *
     * <p><b>Cara memperbaiki nanti</b> (bila SatuSehat menolak PUT untuk transisi ini):
     * keluarkan {@code org.springframework.web-3.1.0.M2.jar} dari classpath, lalu
     * ganti isi method ini dengan JSON Patch (RFC 6902):
     * <pre>
     *   Content-Type: application/json-patch+json
     *   [{"op":"replace","path":"/status","value":"active"}]
     * </pre>
     * Perlu diverifikasi bahwa pengeluaran jar lama tidak meregresi modul lain
     * yang terkompilasi terhadap API Spring 3.1.
     *
     * @param idEoc     UUID EpisodeOfCare di SatuSehat
     * @param newStatus mis. "active" (dari waitlist) atau "finished" (pasien meninggal)
     * @param periodEnd YYYY-MM-DD, diisi saat menutup episode; boleh null
     * @return true kalau SatuSehat menerima
     */
    public static boolean patchStatus(String idEoc, String newStatus, String periodEnd) {
        if (idEoc == null || idEoc.isEmpty() || newStatus == null || newStatus.isEmpty()) {
            return false;
        }
        return updateStatus(idEoc, newStatus, periodEnd);
    }

    /**
     * Map nama poli ke type code Kemkes + display.
     * Return String[2] = {code, display}, atau null kalau poli tidak match program EOC.
     *
     * NB: Mapping ini ada di SatuSehatKirimEncounter sebelumnya, sekarang
     * jadi single source of truth di helper ini.
     */
    /**
     * Type code Registrasi Jantung.
     *
     * SENGAJA TIDAK dimasukkan ke getTypeByPoli/getTypeByDiagnosa: ICD-10 I10-I15
     * saat ini dipetakan ke CHRONIC, dan mengalihkannya ke CAD akan membuat pasien
     * hipertensi yang sudah punya EOC CHRONIC dibuatkan EOC kedua. Registry jantung
     * membuat EOC CAD secara EKSPLISIT dari form, bukan dari tebakan diagnosa.
     */
    /**
     * Ubah "YYYY-MM-DD" menjadi dateTime lengkap berzona WIB.
     * Bila sudah berupa dateTime, dibiarkan apa adanya.
     *
     * Tanggal saja ditolak SatuSehat sebagai "Future Date" karena dibaca sebagai
     * tengah malam UTC — untuk tanggal hari ini, itu bisa jatuh setelah jam server.
     */
    static String keDateTime(String tgl) {
        if (tgl == null) return "";
        String s = tgl.trim();
        if (s.isEmpty()) return "";
        if (s.length() > 10) return s;              // sudah dateTime
        // Pukul 00:00:01 WIB = 17:00:01 UTC hari sebelumnya, aman dari
        // pembulatan yang membuatnya terbaca sebagai masa depan.
        return s + "T00:00:01+07:00";
    }

    public static final String[] TYPE_CAD =
            { "CAD", "Coronary Arterial Disease Management Care" };

    /**
     * Type code Registrasi Stroke.
     *
     * Sama seperti TYPE_CAD, SENGAJA TIDAK dimasukkan ke getTypeByPoli/
     * getTypeByDiagnosa. ICD-10 I60-I69 saat ini tidak match apa pun di
     * pemetaan otomatis, tetapi menambahkannya berarti setiap Encounter pasien
     * stroke otomatis membuat EOC CVD — padahal registry hanya boleh dibuka
     * lewat form, setelah kriteria inklusi dinilai petugas.
     */
    public static final String[] TYPE_CVD =
            { "CVD", "Cerebrovascular Disease Management Care" };

    /**
     * Type code Registrasi Uronefrologi.
     *
     * PERHATIAN: nama registry-nya "Uronefrologi" tetapi kode tipenya CKD.
     * Jangan diterjemahkan sendiri menjadi "URO" atau sejenisnya.
     *
     * Sama seperti TYPE_CAD dan TYPE_CVD, sengaja tidak dimasukkan ke
     * getTypeByPoli/getTypeByDiagnosa — registry hanya dibuka lewat form.
     */
    public static final String[] TYPE_CKD =
            { "CKD", "Chronic Kidney Disease" };

    /**
     * Type code modul Data Kelahiran — episode perawatan neonatus.
     *
     * Kode yang benar adalah "Neonate". Varian "NEO" DITOLAK:
     *   Code not found: 'NEO' in system:
     *   http://terminology.kemkes.go.id/CodeSystem/episodeofcare-type (RuleNumber: 10110)
     *
     * Seperti CAD/CVD/CKD, sengaja tidak dimasukkan ke getTypeByPoli/
     * getTypeByDiagnosa: episode neonatus dibuka dari form Kelahiran Bayi
     * (DlgIKBBayi) lewat SatuSehatDataKelahiranHelper, bukan otomatis dari poli
     * atau kode diagnosa.
     */
    public static final String[] TYPE_NEONATE =
            { "Neonate", "Neonate" };

    /** Antenatal Care — dipetakan otomatis lewat getTypeByPoli/getTypeByDiagnosa. */
    public static final String[] TYPE_ANC =
            { "ANC", "Antenatal Care" };

    public static String[] getTypeByPoli(String nmPoli) {
        if (nmPoli == null) return null;
        String p = nmPoli.toLowerCase();
        if (p.contains("anc"))  return new String[]{"ANC", "Antenatal Care"};
        if (p.contains("pnc"))  return new String[]{"PNC", "Postnatal Care"};
        // Pemetaan poli lain SENGAJA DIHAPUS — lihat catatan KODE TIDAK VALID di bawah.
        return null;
    }

    /*
     * ===================== KODE TIPE YANG TIDAK VALID =====================
     *
     * Diuji satu per satu ke operasi $validate SatuSehat (2026-07-19). Hasilnya:
     *
     *   VALID       : ANC, PNC, CAD, CVD, CKD
     *   TIDAK VALID : TB-DOTS, HIV-AIDS, HD, ONCO, REHAB, MENTAL, CHRONIC
     *                 -> "Code not found: '<kode>' in system:
     *                     http://terminology.kemkes.go.id/CodeSystem/episodeofcare-type"
     *
     * Ketujuh kode tidak valid itu berasal dari port SIMRSKhanza-Trimitra dan
     * tampaknya dikarang, bukan diambil dari CodeSystem resmi. Setiap EpisodeOfCare
     * yang memakainya PASTI ditolak 400 — termasuk jalur CHRONIC yang dikenai
     * seluruh pasien hipertensi lewat pemetaan ICD-10 I10-I15.
     *
     * Pemetaannya dihapus, bukan diganti kode lain, karena tidak ada padanan yang
     * sah: probe terhadap TB, TBC, DM, HT, KANKER, CANCER, STROKE, JANTUNG, GINJAL,
     * DIALISIS, HIV, AIDS, PALLIATIVE, MATERNAL, NEONATAL, IMS, THALASSEMIA, dan
     * GERIATRI semuanya juga tidak ada di CodeSystem.
     *
     * Kelima kode yang sah persis sesuai lima modul registry yang didokumentasikan
     * Kemenkes: ANC, PNC, Jantung (CAD), Stroke (CVD), Uronefrologi (CKD). Ketiga
     * yang terakhir dibuka lewat form registry masing-masing, bukan lewat pemetaan
     * otomatis di sini.
     *
     * Konsekuensi yang diterima: pasien TB/HIV/HD/kanker/rehab/jiwa/kronis tidak lagi
     * dibuatkan EpisodeOfCare otomatis. Lebih baik tidak ada EOC daripada 400 setiap
     * kali kirim — dan modul Registrasi Kanker perlu ditinjau ulang karena memakai
     * ONCO yang ternyata tidak sah.
     * ======================================================================
     */

    /**
     * Map kode ICD-10 (diagnosa) ke EOC type Kemkes.
     * Return String[2] = {code, display}, atau null kalau tidak match program EOC.
     *
     * Mapping pakai prefix ICD-10 chapter standar WHO:
     *   - A15-A19   → TB-DOTS  (Tuberculosis)
     *   - B20-B24   → HIV-AIDS (HIV disease)
     *   - N17-N19   → HD       (Renal failure — ESRD pakai HD)
     *   - C00-C97   → ONCO     (Malignant neoplasms)
     *   - F00-F99   → MENTAL   (Mental and behavioural disorders)
     *   - E10-E14   → CHRONIC  (Diabetes mellitus)
     *   - I10-I15   → CHRONIC  (Hypertensive diseases)
     *   - O00-O99   → ANC      (Pregnancy complications)
     *   - Z34       → ANC      (Supervision of normal pregnancy)
     *   - Z39       → PNC      (Postpartum care)
     *
     * Catatan: bukan total coverage — only program longitudinal yang ada di EOC type list.
     * Cara extend: tambah branch baru kalau ada program baru (mis. stroke, palliative).
     */
    public static String[] getTypeByDiagnosa(String kdPenyakit) {
        if (kdPenyakit == null || kdPenyakit.length() < 3) return null;
        String kd = kdPenyakit.toUpperCase();
        String prefix3 = kd.substring(0, Math.min(3, kd.length()));

        // HANYA ANC dan PNC yang dipetakan otomatis. Pemetaan ke TB-DOTS, HIV-AIDS,
        // HD, ONCO, MENTAL, dan CHRONIC dihapus karena keenam kode itu TIDAK ADA di
        // CodeSystem episodeofcare-type SatuSehat — lihat catatan KODE TIDAK VALID
        // di atas. Sebelumnya, tiap pasien hipertensi (I10-I15) dan diabetes
        // (E10-E14) memicu pembuatan EpisodeOfCare CHRONIC yang pasti ditolak 400.
        //
        // CAD/CVD/CKD sengaja TIDAK dipetakan di sini: registry jantung, stroke, dan
        // uronefrologi hanya boleh dibuka lewat formnya masing-masing setelah petugas
        // menilai kriteria inklusi, bukan otomatis dari kode diagnosa.

        // ANC: O00-O99 (komplikasi kehamilan), Z34 (pengawasan kehamilan normal)
        if (prefix3.length() == 3 && prefix3.charAt(0) == 'O') return new String[]{"ANC", "Antenatal Care"};
        if (prefix3.startsWith("Z34")) return new String[]{"ANC", "Antenatal Care"};

        // PNC: Z39 (perawatan nifas)
        if (prefix3.startsWith("Z39")) return new String[]{"PNC", "Postnatal Care"};

        return null;
    }

    /**
     * Scan diagnosa pasien di tabel diagnosa_pasien untuk no_rawat tertentu,
     * cari diagnosa pertama yang map ke EOC type. Prioritas: diagnosa primer dulu
     * (prioritas=1), lalu sekunder.
     *
     * Return String[2] = {code, display} atau null kalau semua diagnosa tidak match.
     * Dipakai sebagai fallback di SatuSehatKirimEncounter kalau getTypeByPoli null.
     */
    public static String[] findTypeByDiagnosaRawat(String noRawat) {
        if (noRawat == null || noRawat.isEmpty()) return null;
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "select kd_penyakit from diagnosa_pasien "
              + "where no_rawat=? order by prioritas asc")) {
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            String[] result = null;
            while (rs.next()) {
                String kd = rs.getString(1);
                String[] match = getTypeByDiagnosa(kd);
                if (match != null) {
                    result = match;
                    break;  // ambil yang paling primer (prioritas terkecil)
                }
            }
            rs.close();
            return result;
        } catch (Exception e) {
            System.out.println("Notif findTypeByDiagnosaRawat : " + e);
            return null;
        }
    }

    /**
     * Link 1 Encounter ke EOC via tabel satu_sehat_encounter_episodeofcare.
     * Idempotent — INSERT IGNORE pattern (kalau no_rawat sudah ter-link, skip).
     */
    public static void linkEncounter(String noRawat, String idEoc) {
        if (noRawat == null || noRawat.isEmpty() || idEoc == null || idEoc.isEmpty()) return;
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "insert into satu_sehat_encounter_episodeofcare (no_rawat,id_eoc) values (?,?) "
              + "on duplicate key update id_eoc=values(id_eoc), tgl_link=now()")) {
            ps.setString(1, noRawat);
            ps.setString(2, idEoc);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif linkEncounter EOC : " + e);
        }
    }

    /**
     * Update status EOC (mis. active → finished).
     * PUT /EpisodeOfCare/{id} ke SatuSehat + UPDATE local.
     * Caller bertanggung jawab pastikan idEoc valid (sudah pernah create).
     *
     * @param idEoc      UUID EpisodeOfCare di SatuSehat
     * @param newStatus  active/onhold/finished/cancelled/entered-in-error
     * @param periodEnd  YYYY-MM-DD (set kalau finished/cancelled, boleh null untuk status lain)
     * @return true kalau sukses sync + simpan
     */
    public static boolean updateStatus(String idEoc, String newStatus, String periodEnd) {
        if (idEoc == null || idEoc.isEmpty() || newStatus == null || newStatus.isEmpty()) return false;

        // Load existing data dari local untuk rebuild full JSON (FHIR PUT = full replacement)
        EocLocal local = loadLocal(idEoc);
        if (local == null) {
            System.out.println("Notif updateStatus: EOC " + idEoc + " tidak ada di local");
            return false;
        }

        try {
            // Lookup IHS untuk patient & care manager
            String idPasienIhs = cekViaSatuSehat.tampilIDPasien(lookupNoKtpPasien(local.noRkmMedis));
            String idCareManager = "";
            if (local.careManagerNik != null && !local.careManagerNik.isEmpty()) {
                idCareManager = cekViaSatuSehat.tampilIDParktisi(local.careManagerNik);
            }

            String json = buildJson(idEoc, local.noRkmMedis, idPasienIhs, lookupNamaPasien(local.noRkmMedis),
                                    local.typeCode, local.typeDisplay,
                                    local.conditionKdPenyakit, null,
                                    newStatus, local.periodStart, periodEnd, idCareManager);

            String link = koneksiDB.URLFHIRSATUSEHAT();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
            HttpEntity<String> req = new HttpEntity<>(json, headers);

            System.out.println("URL : " + link + "/EpisodeOfCare/" + idEoc);
            System.out.println("Request JSON : " + json);
            String resp = api.getRest().exchange(link + "/EpisodeOfCare/" + idEoc, HttpMethod.PUT, req, String.class).getBody();
            System.out.println("Result JSON : " + resp);

            // Update local
            try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                    "update satu_sehat_episodeofcare set status=?, period_end=? where id_eoc=?")) {
                ps.setString(1, newStatus);
                if (periodEnd != null && !periodEnd.isEmpty()) ps.setString(2, periodEnd);
                else ps.setNull(2, java.sql.Types.DATE);
                ps.setString(3, idEoc);
                ps.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Notif EpisodeOfCare.updateStatus : " + e);
            return false;
        }
    }

    // ============================================================
    // INTERNAL
    // ============================================================

    /** Bangun JSON payload EpisodeOfCare (untuk POST atau PUT).
     *  Kalau idEoc != null, dimasukkan sebagai "id" field (untuk PUT). */
    private static String buildJson(String idEoc, String noRkmMedis, String idPasienIhs, String namaPasien,
                                    String typeCode, String typeDisplay,
                                    String conditionKdPenyakit, String idConditionFhir,
                                    String status, String periodStart, String periodEnd,
                                    String idCareManagerIhs) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("{")
          .append("\"resourceType\": \"EpisodeOfCare\",");
        if (idEoc != null && !idEoc.isEmpty()) {
            sb.append("\"id\": \"").append(idEoc).append("\",");
        }
        // Penulisan system WAJIB persis. Terverifikasi lewat operasi $validate:
        //   - "episode-of-care" berTANDA HUBUNG. Varian "episodeofcare" dan
        //     "episodeOfCare" ditolak: Invalid identifier system (RuleNumber 10458).
        //   - TANPA garis miring di akhir. Versi sebelumnya menambahkan "/" dan ikut
        //     ditolak dengan pesan yang sama.
        sb.append("\"identifier\": [{")
          .append("\"system\": \"http://sys-ids.kemkes.go.id/episode-of-care/").append(koneksiDB.IDSATUSEHAT()).append("\",")
          .append("\"use\": \"official\",")
          .append("\"value\": \"").append(jsonEscape(noRkmMedis + "-" + typeCode)).append("\"")
          .append("}],")
          .append("\"status\": \"").append(status).append("\",")
          .append("\"type\": [{\"coding\": [{")
          // http://, BUKAN https:// — versi https ditolak dengan
          // "Invalid coding system" (RuleNumber 10110).
          .append("\"system\": \"http://terminology.kemkes.go.id/CodeSystem/episodeofcare-type\",")
          .append("\"code\": \"").append(jsonEscape(typeCode)).append("\",")
          .append("\"display\": \"").append(jsonEscape(typeDisplay)).append("\"")
          .append("}]}],");
        // Diagnosis kalau ada condition reference
        if (idConditionFhir != null && !idConditionFhir.isEmpty()) {
            sb.append("\"diagnosis\": [{")
              .append("\"condition\": {\"reference\": \"Condition/").append(idConditionFhir).append("\"},")
              .append("\"role\": {\"coding\": [{")
                .append("\"system\": \"http://terminology.hl7.org/CodeSystem/diagnosis-role\",")
                .append("\"code\": \"AD\",")
                .append("\"display\": \"Admission diagnosis\"")
              .append("}]},")
              .append("\"rank\": 1")
              .append("}],");
        }
        sb.append("\"patient\": {")
          .append("\"reference\": \"Patient/").append(idPasienIhs).append("\",")
          .append("\"display\": \"").append(jsonEscape(namaPasien == null ? "" : namaPasien)).append("\"")
          .append("},")
          // managingOrganization WAJIB dikirim. SatuSehat memang tidak menolak
          // payload tanpa ini — 104 EOC ANC kita di produksi lolos tanpanya —
          // TETAPI episode yang tidak menyebut organisasinya tidak bisa dicari
          // lewat EpisodeOfCare?organization={org_id}, sehingga tidak pernah
          // terhitung sebagai milik faskes ini. Terbukti di papan nilai lokakarya
          // Data Kelahiran: item "EpisodeOfCare neonatus" tetap merah walau
          // resource-nya ada dan isinya benar.
          //
          // Harus Organization UTAMA, bukan sub-organisasi: sub-org ditolak
          // "wrong organization ID" (Rule 10124).
          .append("\"managingOrganization\": {\"reference\": \"Organization/")
          .append(koneksiDB.IDSATUSEHAT()).append("\"},")
          .append("\"period\": {")
          // Tanggal saja ("2026-07-19") ditolak: "Invalid date time value ...
          // Not Allowed : Future Date" (RuleNumber 10406), karena dibaca sebagai
          // tengah malam UTC yang bisa jatuh di masa depan relatif jam server.
          // Kirim sebagai dateTime lengkap berzona waktu.
          .append("\"start\": \"").append(keDateTime(periodStart)).append("\"");
        if (periodEnd != null && !periodEnd.isEmpty()) {
            sb.append(",\"end\": \"").append(keDateTime(periodEnd)).append("\"");
        }
        sb.append("}");
        if (idCareManagerIhs != null && !idCareManagerIhs.isEmpty()) {
            sb.append(",\"careManager\": {\"reference\": \"Practitioner/").append(idCareManagerIhs).append("\"}");
        }
        sb.append("}");
        return sb.toString();
    }

    private static EocLocal loadLocal(String idEoc) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "select * from satu_sehat_episodeofcare where id_eoc=? limit 1")) {
            ps.setString(1, idEoc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EocLocal e = new EocLocal();
                e.idEoc = rs.getString("id_eoc");
                e.noRkmMedis = rs.getString("no_rkm_medis");
                e.typeCode = rs.getString("type_code");
                e.typeDisplay = rs.getString("type_display");
                e.status = rs.getString("status");
                e.periodStart = rs.getString("period_start");
                e.periodEnd = rs.getString("period_end");
                e.conditionKdPenyakit = rs.getString("condition_kd_penyakit");
                e.careManagerNik = rs.getString("care_manager_nik");
                rs.close();
                return e;
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Notif loadLocal EOC : " + ex);
        }
        return null;
    }

    private static String lookupNoKtpPasien(String noRkmMedis) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "select no_ktp from pasien where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRkmMedis);
            ResultSet rs = ps.executeQuery();
            String r = rs.next() ? rs.getString(1) : "";
            rs.close();
            return r == null ? "" : r;
        } catch (Exception e) { return ""; }
    }

    private static String lookupNamaPasien(String noRkmMedis) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(
                "select nm_pasien from pasien where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRkmMedis);
            ResultSet rs = ps.executeQuery();
            String r = rs.next() ? rs.getString(1) : "";
            rs.close();
            return r == null ? "" : r;
        } catch (Exception e) { return ""; }
    }

    /** Escape karakter untuk JSON manual (cegah Rule 20006). */
    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Internal struct untuk load data EOC dari local. */
    private static class EocLocal {
        String idEoc, noRkmMedis, typeCode, typeDisplay, status;
        String periodStart, periodEnd;
        String conditionKdPenyakit, careManagerNik;
    }
}
