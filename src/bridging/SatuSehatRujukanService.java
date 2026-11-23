/*
 * SatuSehatRujukanService.java
 *
 * Service layer untuk integrasi modul Rujukan Pasien (Rawat Inap & Rawat Darurat)
 * sesuai Buku Panduan SATUSEHAT (Playbook) Rujukan Pasien v5.1, 4 Maret 2026.
 *
 * BERBEDA dari SisruteService:
 *  - SisruteService = lewat BPJS-K (untuk Rawat Jalan)
 *  - SatuSehatRujukanService = LANGSUNG ke Satu Sehat (untuk Ranap & IGD)
 *
 * Memanfaatkan helper di ApiSatuSehat:
 *   - getValidToken() / forceRefreshToken() : token caching
 *   - buildAuthHeaders()                    : headers + Bearer
 *   - getBaseUrl()                          : URL FHIR Satu Sehat (URLFHIRSATUSEHAT)
 *   - getOrgIdPerujuk()                     : ID Organization fasyankes
 *   - getUtcDatetimeNow()                   : format UTC sesuai playbook
 *   - orgRef/patientRef/practitionerRef/encounterRef/taskRef : reference builders
 *   - getMapper()                           : ObjectMapper shared
 *
 * Endpoint utama (FHIR R4):
 *   POST {base}/Task                  - Task (pra-request, search, approval)
 *   POST {base}                       - Bundle transaction (Task + CarePlan)
 *   POST {base}/ServiceRequest        - permintaan rujukan final
 *   GET  {base}/Task/{id}             - cek status accept/reject
 *
 * Alur 4 step:
 *   Step 1 - kirimPraPermintaan()        → Task referral-pre-request
 *   Step 2 - cariKandidatFasyankes()     → Task request-referral-candidate
 *   Step 3 - kirimTugasRujukan()         → Bundle (Task referral-approval-request + CarePlan)
 *            cekStatusAccept()           → GET Task untuk cek accept/reject
 *   Step 4 - kirimRujukanFinal()         → ServiceRequest → balikan Nomor Rujukan Nasional
 *
 * @author SIMRS Khanza Bridging
 */

package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class SatuSehatRujukanService {

    private final ApiSatuSehat ihs = new ApiSatuSehat();

    // ====== Konstanta sistem terminology dari playbook ======
    public static final String SYS_KEMKES   = "http://terminology.kemkes.go.id";
    public static final String SYS_ICD10    = "http://hl7.org/fhir/sid/icd-10";
    public static final String SYS_SNOMED   = "http://snomed.info/sct";
    public static final String SYS_TASKSTAT = "http://hl7.org/fhir/task-status";

    // SNOMED tipe perawatan (sesuai Lampiran 1 playbook)
    public static final String SNOMED_RANAP = "737481003";  // Inpatient care management
    public static final String SNOMED_RAJAL = "737492002";  // Outpatient care management
    public static final String SNOMED_IGD   = "385868005";  // Emergency treatment management

    // CarePlan.category (Lampiran 3)
    public static final String CP_CAT_RANAP_SYS  = SYS_SNOMED;
    public static final String CP_CAT_RANAP_CODE = "736353004";
    public static final String CP_CAT_RANAP_DISP = "Inpatient care plan";
    public static final String CP_CAT_IGD_SYS    = SYS_KEMKES;
    public static final String CP_CAT_IGD_CODE   = "TK000068";
    public static final String CP_CAT_IGD_DISP   = "Emergency care plan";

    // =================================================================
    //  HELPER POST: Condition (diagnosa) - prerequisite untuk reasonReference
    //  Resource FHIR: Condition - https://hl7.org/fhir/R4/condition.html
    // =================================================================

    /**
     * POST Condition (diagnosa) ke Satu Sehat. Diperlukan supaya kita punya
     * Condition.id untuk dipakai di reasonReference Task & ServiceRequest.
     *
     * Format JSON match dengan SatuSehatKirimCondition.java existing supaya
     * konsisten dengan apa yang sudah teruji diterima Satu Sehat.
     *
     * @param idPasienSS     IHS pasien
     * @param idEncounterSS  ID Encounter Satu Sehat
     * @param namaPasien     nama pasien (untuk display di subject)
     * @param kdIcd10        kode ICD-10
     * @param nmIcd10        nama ICD-10
     * @param kategori       "encounter-diagnosis" untuk diagnosa kunjungan,
     *                       "problem-list-item" untuk problem list (default: encounter-diagnosis)
     * @return JsonNode response (ambil .id untuk dipakai di reasonReference)
     */
    public JsonNode kirimCondition(String idPasienSS, String idEncounterSS,
                                    String namaPasien, String kdIcd10, String nmIcd10,
                                    String kategori) throws Exception {
        String kat = (kategori == null || kategori.isEmpty()) ? "encounter-diagnosis" : kategori;
        String katDisplay = "encounter-diagnosis".equals(kat) ? "Encounter Diagnosis" : "Problem List Item";
        String nm = (namaPasien == null) ? "" : namaPasien;

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"Condition\",")
          .append("\"clinicalStatus\":{\"coding\":[{")
              .append("\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",")
              .append("\"code\":\"active\",")
              .append("\"display\":\"Active\"")
          .append("}]},")
          .append("\"category\":[{\"coding\":[{")
              .append("\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",")
              .append("\"code\":\"").append(kat).append("\",")
              .append("\"display\":\"").append(katDisplay).append("\"")
          .append("}]}],")
          .append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_ICD10).append("\",")
              .append("\"code\":\"").append(escape(kdIcd10)).append("\",")
              .append("\"display\":\"").append(escape(nmIcd10)).append("\"")
          .append("}]},")
          .append("\"subject\":{")
              .append("\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\",")
              .append("\"display\":\"").append(escape(nm)).append("\"")
          .append("},")
          .append("\"encounter\":{")
              .append("\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\",")
              .append("\"display\":\"Diagnosa ").append(escape(nm)).append("\"")
          .append("}")
          .append("}");

        return postFhir("/Condition", sb.toString());
    }

    /**
     * Ambil Condition.id dari respon kirimCondition().
     * @return id (UUID) atau null kalau tidak ditemukan
     */
    public String getConditionId(JsonNode condResp) {
        if (condResp == null) return null;
        String id = condResp.path("id").asText();
        return id.isEmpty() ? null : id;
    }

    // =================================================================
    //  HELPER POST: CarePlan untuk Rujukan (HYBRID format)
    //
    //  Format hybrid: kombinasi style SatuSehatKirimCarePlan.java existing
    //  (title, description, created, display di subject/encounter/author)
    //  + tambahan addresses & activity dari playbook v5.1 yang wajib untuk rujukan.
    //
    //  Berbeda dari buildCarePlanJson() yang hanya menghasilkan JSON inline (untuk Bundle).
    //  Method ini AKTIF kirim POST /CarePlan ke Satu Sehat dan return id-nya.
    // =================================================================

    /**
     * Kirim CarePlan rujukan ke Satu Sehat (POST /CarePlan).
     * Format HYBRID: existing style + addresses & activity rujukan.
     *
     * @param noRawat            no_rawat sebagai identifier value
     * @param idPasienSS         IHS pasien
     * @param namaPasien         nama pasien (untuk display)
     * @param idEncounterSS      ID Encounter Satu Sehat
     * @param idDokterSS         IHS dokter (author)
     * @param namaDokter         nama dokter (untuk display)
     * @param idFaskesTujuanSS   ID Organization fasyankes tujuan (contributor)
     * @param idConditionDiagnosa ID Condition diagnosa (untuk addresses, boleh null)
     * @param tipePerawatan      SNOMED_RANAP / SNOMED_IGD
     * @param description        deskripsi rencana (mis. "Rujukan ke RS X karena ...")
     * @param tglDibuatWIB       tanggal+waktu dibuat dalam WIB (yyyy-MM-dd HH:mm:ss),
     *                           akan dikonversi ke format dengan +07:00
     * @param kdSpesialisasi     kode spesialisasi rujukan (boleh null)
     * @param kdKelasPerawatan   kode kelas perawatan (boleh null)
     * @return JsonNode response (ambil .id untuk dipakai di ServiceRequest.basedOn)
     */
    public JsonNode kirimCarePlanRujukan(String noRawat,
                                          String idPasienSS, String namaPasien,
                                          String idEncounterSS,
                                          String idDokterSS, String namaDokter,
                                          String idFaskesTujuanSS,
                                          String idConditionDiagnosa,
                                          String tipePerawatan,
                                          String description,
                                          String tglDibuatWIB,
                                          String kdSpesialisasi,
                                          String kdKelasPerawatan) throws Exception {
        String orgId = ihs.getOrgIdPerujuk();
        boolean isRanap = SNOMED_RANAP.equals(tipePerawatan);

        // Category[0] - Lampiran 3 playbook
        String catSys  = isRanap ? CP_CAT_RANAP_SYS  : CP_CAT_IGD_SYS;
        String catCode = isRanap ? CP_CAT_RANAP_CODE : CP_CAT_IGD_CODE;
        String catDisp = isRanap ? CP_CAT_RANAP_DISP : CP_CAT_IGD_DISP;

        // Activity kind: ranap pakai jadwal (Appointment), IGD segera (ServiceRequest)
        String activityKind = isRanap ? "Appointment" : "ServiceRequest";
        String activityCodeDisp = isRanap ? "Inpatient care management" : "Emergency treatment management";

        // Konversi tgl_dibuat WIB ke ISO + offset
        String createdIso = "";
        if (tglDibuatWIB != null && !tglDibuatWIB.isEmpty()) {
            createdIso = tglDibuatWIB.replace(" ", "T") + "+07:00";
        } else {
            createdIso = ihs.getUtcDatetimeNow();
        }

        // Description sanitization (sama style dengan existing)
        String desc = (description == null) ? "" : description;
        desc = desc.replaceAll("(\\r\\n|\\r|\\n)", "<br>").replaceAll("\\t", " ");

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"CarePlan\",")
          // identifier - OBJECT (sesuai existing, bukan array)
          .append("\"identifier\":{")
              .append("\"system\":\"http://sys-ids.kemkes.go.id/careplan/").append(orgId).append("\",")
              .append("\"value\":\"").append(escape(noRawat)).append("\"")
          .append("},")
          .append("\"title\":\"Instruksi Medik dan Keperawatan Pasien\",")
          .append("\"status\":\"active\",")
          .append("\"intent\":\"plan\",")
          // === category - HYBRID: array dengan 2 element ===
          // [0] tipe perawatan (Lampiran 3) - format existing
          // [1] Patient referral - tambahan dari playbook untuk rujukan
          .append("\"category\":[")
              .append("{\"coding\":[{")
                  .append("\"system\":\"").append(catSys).append("\",")
                  .append("\"code\":\"").append(catCode).append("\",")
                  .append("\"display\":\"").append(catDisp).append("\"")
              .append("}]},")
              .append("{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_SNOMED).append("\",")
                  .append("\"code\":\"3457005\",")
                  .append("\"display\":\"Patient referral\"")
              .append("}]}")
          .append("],")
          .append("\"description\":\"").append(escape(desc)).append("\",")
          // subject + display (style existing)
          .append("\"subject\":{")
              .append("\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\",")
              .append("\"display\":\"").append(escape(namaPasien == null ? "" : namaPasien)).append("\"")
          .append("},")
          // encounter + display (style existing)
          .append("\"encounter\":{")
              .append("\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\",")
              .append("\"display\":\"Rencana Rujukan untuk no_rawat ").append(escape(noRawat)).append("\"")
          .append("},")
          .append("\"created\":\"").append(createdIso).append("\",")
          // author + display
          .append("\"author\":{")
              .append("\"reference\":\"").append(ihs.practitionerRef(idDokterSS)).append("\",")
              .append("\"display\":\"").append(escape(namaDokter == null ? "" : namaDokter)).append("\"")
          .append("}");

        // === addresses (tambahan dari playbook untuk rujukan) ===
        if (idConditionDiagnosa != null && !idConditionDiagnosa.isEmpty()) {
            sb.append(",\"addresses\":[{\"reference\":\"Condition/").append(idConditionDiagnosa).append("\"}]");
        }

        // === contributor: Org fasyankes tujuan (untuk rujukan) ===
        if (idFaskesTujuanSS != null && !idFaskesTujuanSS.isEmpty()) {
            sb.append(",\"contributor\":[{\"reference\":\"").append(ihs.orgRef(idFaskesTujuanSS)).append("\"}]");
        }

        // === activity (tambahan dari playbook untuk rujukan) ===
        // Minimal 1 activity: tipe perawatan
        sb.append(",\"activity\":[")
          .append("{\"detail\":{")
              .append("\"kind\":\"").append(activityKind).append("\",")
              .append("\"status\":\"not-started\",")
              .append("\"code\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_SNOMED).append("\",")
                  .append("\"code\":\"").append(tipePerawatan).append("\",")
                  .append("\"display\":\"").append(activityCodeDisp).append("\"")
              .append("}]}")
          .append("}}");

        // Activity spesialisasi (kalau ada)
        if (kdSpesialisasi != null && !kdSpesialisasi.isEmpty()) {
            sb.append(",{\"detail\":{")
              .append("\"kind\":\"").append(activityKind).append("\",")
              .append("\"status\":\"not-started\",")
              .append("\"code\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"").append(escape(kdSpesialisasi)).append("\"")
              .append("}]}")
              .append("}}");
        }

        // Activity kelas perawatan (kalau ada)
        if (kdKelasPerawatan != null && !kdKelasPerawatan.isEmpty()) {
            sb.append(",{\"detail\":{")
              .append("\"kind\":\"").append(activityKind).append("\",")
              .append("\"status\":\"not-started\",")
              .append("\"code\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"").append(escape(kdKelasPerawatan)).append("\"")
              .append("}]}")
              .append("}}");
        }

        sb.append("]");  // tutup activity
        sb.append("}");

        return postFhir("/CarePlan", sb.toString());
    }

    /**
     * Ambil CarePlan.id dari respon kirimCarePlanRujukan().
     */
    public String getCarePlanId(JsonNode cpResp) {
        if (cpResp == null) return null;
        String id = cpResp.path("id").asText();
        return id.isEmpty() ? null : id;
    }

    // =================================================================
    //  STEP 1: PRA PERMINTAAN KANDIDAT FASYANKES RUJUKAN
    //  Resource: Task (referral-pre-request) - Sec.1.1.1 playbook
    // =================================================================

    /**
     * Kirim pra permintaan rujukan ke Satu Sehat.
     * Berlaku untuk ranap & IGD (alur sama, beda hanya tipe perawatan di step 2).
     *
     * @param idPasienSS     IHS pasien
     * @param idEncounterSS  ID Encounter Satu Sehat (dari satu_sehat_encounter)
     * @param kdIcd10        kode ICD-10 (mis. "I10")
     * @param nmIcd10        nama ICD-10 (mis. "Essential hypertension")
     * @return JsonNode response (berisi Task.id yang dipakai sebagai based-on di step 2)
     */
    public JsonNode kirimPraPermintaan(String idPasienSS, String idEncounterSS,
                                       String kdIcd10, String nmIcd10) throws Exception {
        String now = ihs.getUtcDatetimeNow();
        String orgId = ihs.getOrgIdPerujuk();

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"Task\",")
          .append("\"status\":\"requested\",")
          .append("\"intent\":\"instance-order\",")
          .append("\"priority\":\"routine\",")
          .append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"referral-pre-request\",")
              .append("\"display\":\"Referral pre request\"")
          .append("}]},")
          .append("\"authoredOn\":\"").append(now).append("\",")
          .append("\"lastModified\":\"").append(now).append("\",")
          .append("\"requester\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},")
          .append("\"owner\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},")
          .append("\"for\":{\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\"},")
          .append("\"encounter\":{\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\"},")
          .append("\"input\":[{")
              .append("\"type\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"primary-diagnosis\",")
                  .append("\"display\":\"Primary diagnosis\"")
              .append("}]},")
              .append("\"valueCoding\":{")
                  .append("\"system\":\"").append(SYS_ICD10).append("\",")
                  .append("\"code\":\"").append(escape(kdIcd10)).append("\",")
                  .append("\"display\":\"").append(escape(nmIcd10)).append("\"")
              .append("}")
          .append("}]")
          .append("}");

        return postFhir("/Task", sb.toString());
    }

    // =================================================================
    //  STEP 2: PERMINTAAN KANDIDAT FASYANKES RUJUKAN
    //  Resource: Task (request-referral-candidate) - Sec.1.2.1 playbook
    // =================================================================

    /**
     * Cari kandidat fasyankes rujukan.
     *
     * @param tipePerawatan        SNOMED_RANAP atau SNOMED_IGD
     * @param idPasienSS           IHS pasien
     * @param kdIcd10Primer        ICD-10 primer
     * @param nmIcd10Primer        nama ICD-10 primer
     * @param kdIcd10Sekunder      ICD-10 sekunder (boleh null/empty)
     * @param nmIcd10Sekunder      nama ICD-10 sekunder
     * @param kuesionerResponJson  JSON QuestionnaireResponse kriteria (jawaban user)
     * @param wilayahResponJson    JSON QuestionnaireResponse wilayah (jawaban user pilih wilayah)
     * @param idEncounterSS        ID Encounter Satu Sehat (WAJIB - rule 10875)
     * @return JsonNode response (berisi list kandidat fasyankes di Task.output)
     */
    public JsonNode cariKandidatFasyankes(String tipePerawatan,
                                          String idPasienSS,
                                          String kdIcd10Primer, String nmIcd10Primer,
                                          String kdIcd10Sekunder, String nmIcd10Sekunder,
                                          String kuesionerResponJson,
                                          String wilayahResponJson,
                                          String idEncounterSS) throws Exception {
        String now = ihs.getUtcDatetimeNow();
        String orgId = ihs.getOrgIdPerujuk();
        String tipeDisplay = SNOMED_RANAP.equals(tipePerawatan) ? "Inpatient care management"
                            : "Emergency treatment management";

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"Task\",")
          .append("\"status\":\"requested\",")
          .append("\"intent\":\"instance-order\",")
          .append("\"priority\":\"routine\",")
          .append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"request-referral-candidate\",")
              .append("\"display\":\"Request for referral candidate\"")
          .append("}]},")
          .append("\"for\":{\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\"},")
          .append("\"encounter\":{\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\"},")
          .append("\"authoredOn\":\"").append(now).append("\",")
          .append("\"lastModified\":\"").append(now).append("\",")
          .append("\"requester\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},")
          .append("\"owner\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},");

        // === input array ===
        sb.append("\"input\":[");

        // input[0] - primary diagnosis
        sb.append("{")
          .append("\"type\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"primary-diagnosis\",")
              .append("\"display\":\"Primary diagnosis\"")
          .append("}]},")
          .append("\"valueCoding\":{")
              .append("\"system\":\"").append(SYS_ICD10).append("\",")
              .append("\"code\":\"").append(escape(kdIcd10Primer)).append("\",")
              .append("\"display\":\"").append(escape(nmIcd10Primer)).append("\"")
          .append("}")
          .append("}");

        // input[1] - secondary diagnosis (opsional)
        if (kdIcd10Sekunder != null && !kdIcd10Sekunder.isEmpty()) {
            sb.append(",{")
              .append("\"type\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"secondary-diagnosis\",")
                  .append("\"display\":\"Secondary diagnosis\"")
              .append("}]},")
              .append("\"valueCoding\":{")
                  .append("\"system\":\"").append(SYS_ICD10).append("\",")
                  .append("\"code\":\"").append(escape(kdIcd10Sekunder)).append("\",")
                  .append("\"display\":\"").append(escape(nmIcd10Sekunder)).append("\"")
              .append("}")
              .append("}");
        }

        // input[2] - referral-criteria (referensi ke contained QuestionnaireResponse)
        sb.append(",{")
          .append("\"type\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"referral-criteria\",")
              .append("\"display\":\"Referral criteria\"")
          .append("}]},")
          .append("\"valueReference\":{\"reference\":\"#qr-criteria\",\"display\":\"Respon Kriteria Rujukan\"}")
          .append("}");

        // input[3] - area (referensi ke contained QuestionnaireResponse)
        sb.append(",{")
          .append("\"type\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"area\",")
              .append("\"display\":\"Area\"")
          .append("}]},")
          .append("\"valueReference\":{\"reference\":\"#qr-area\",\"display\":\"Jejaring Wilayah Rujukan\"}")
          .append("}");

        // input[4] - tipe perawatan
        sb.append(",{")
          .append("\"type\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_SNOMED).append("\",")
              .append("\"code\":\"119270007\",")
              .append("\"display\":\"Management procedure\"")
          .append("}]},")
          .append("\"valueCoding\":{")
              .append("\"system\":\"").append(SYS_SNOMED).append("\",")
              .append("\"code\":\"").append(tipePerawatan).append("\",")
              .append("\"display\":\"").append(tipeDisplay).append("\"")
          .append("}")
          .append("}");

        sb.append("],");  // tutup input

        // === contained: 2 QuestionnaireResponse ===
        sb.append("\"contained\":[");
        if (kuesionerResponJson != null && !kuesionerResponJson.isEmpty()) {
            sb.append(kuesionerResponJson);
        } else {
            sb.append("{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"qr-criteria\",\"status\":\"completed\"}");
        }
        sb.append(",");
        if (wilayahResponJson != null && !wilayahResponJson.isEmpty()) {
            sb.append(wilayahResponJson);
        } else {
            sb.append("{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"qr-area\",\"status\":\"completed\"}");
        }
        sb.append("]");

        sb.append("}");
        return postFhir("/Task", sb.toString());
    }

    // =================================================================
    //  STEP 3: PENGIRIMAN TUGAS RUJUKAN (khusus Ranap & IGD)
    //  Resource: Bundle (Task referral-approval-request + CarePlan)
    //  Sec.2.3.1 playbook
    // =================================================================

    /**
     * Kirim Task pengiriman tugas rujukan ke fasyankes tujuan terpilih.
     * Fasyankes tujuan akan menerima dan response accept/reject (via PATCH Task).
     *
     * Penyederhanaan: kita kirim 1 Task untuk 1 kandidat fasyankes terpilih.
     * Untuk kirim ke beberapa kandidat sekaligus, panggil method ini berkali-kali.
     *
     * @param idPasienSS         IHS pasien
     * @param idFaskesTujuanSS   ID Organization fasyankes tujuan kandidat
     * @param namaFaskesTujuan   nama fasyankes tujuan (untuk display)
     * @param idConditionDiagnosa ID Condition diagnosa (sudah dikirim sebelumnya, boleh null)
     * @param carePlanJson       JSON CarePlan rencana rujukan (full body, inline di Bundle)
     * @return JsonNode response Bundle
     */
    public JsonNode kirimTugasRujukan(String idPasienSS,
                                      String idFaskesTujuanSS,
                                      String namaFaskesTujuan,
                                      String idConditionDiagnosa,
                                      String carePlanJson,
                                      String idEncounterSS) throws Exception {
        String now = ihs.getUtcDatetimeNow();
        String orgId = ihs.getOrgIdPerujuk();

        // Inner Task pengiriman tugas rujukan
        StringBuilder taskKirim = new StringBuilder();
        taskKirim.append("{")
          .append("\"resourceType\":\"Task\",")
          .append("\"status\":\"requested\",")
          .append("\"intent\":\"instance-order\",")
          .append("\"priority\":\"routine\",")
          .append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"referral-approval-request\",")
              .append("\"display\":\"Referral approval request\"")
          .append("}]},")
          .append("\"for\":{\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\"},")
          .append("\"encounter\":{\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\"},")
          .append("\"authoredOn\":\"").append(now).append("\",")
          .append("\"lastModified\":\"").append(now).append("\",")
          .append("\"requester\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},")
          .append("\"owner\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"}");
        if (idConditionDiagnosa != null && !idConditionDiagnosa.isEmpty()) {
            taskKirim.append(",\"reasonReference\":{\"reference\":\"Condition/")
                     .append(idConditionDiagnosa).append("\"}");
        }
        taskKirim.append(",\"input\":[{")
              .append("\"type\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"referral-task\",")
                  .append("\"display\":\"Referral task\"")
              .append("}]},")
              .append("\"valueReference\":{")
                  .append("\"reference\":\"").append(ihs.orgRef(idFaskesTujuanSS)).append("\",")
                  .append("\"display\":\"").append(escape(namaFaskesTujuan)).append("\"")
              .append("}")
          .append("}]")
          .append("}");

        // Wrap dalam Bundle (tipe transaction)
        StringBuilder bundle = new StringBuilder();
        bundle.append("{")
          .append("\"resourceType\":\"Bundle\",")
          .append("\"meta\":{\"tag\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"referral-approval\",")
              .append("\"display\":\"Referral approval\"")
          .append("}]},")
          .append("\"type\":\"transaction\",")
          .append("\"entry\":[")
              // Entry 1: Task kirim
              .append("{")
                .append("\"fullUrl\":\"urn:uuid:task-kirim\",")
                .append("\"resource\":").append(taskKirim).append(",")
                .append("\"request\":{\"method\":\"POST\",\"url\":\"Task\"}")
              .append("}");

        // Entry 2: CarePlan (kalau ada)
        if (carePlanJson != null && !carePlanJson.isEmpty()) {
            bundle.append(",{")
                .append("\"fullUrl\":\"urn:uuid:careplan\",")
                .append("\"resource\":").append(carePlanJson).append(",")
                .append("\"request\":{\"method\":\"POST\",\"url\":\"CarePlan\"}")
            .append("}");
        }

        bundle.append("]}");

        // POST ke base URL untuk transaction Bundle (no path suffix)
        return postFhir("", bundle.toString());
    }

    /**
     * Versi alternatif kirimTugasRujukan() yang pakai CarePlan id YANG SUDAH ADA
     * (sudah dikirim sebelumnya via kirimCarePlanRujukan()).
     *
     * Tidak inline CarePlan di Bundle — Bundle hanya berisi 1 Task. Lebih ringkas
     * dan support flow di mana CarePlan dikirim 1x untuk semua kandidat fasyankes.
     *
     * @param idPasienSS          IHS pasien
     * @param idFaskesTujuanSS    ID Organization fasyankes tujuan
     * @param namaFaskesTujuan    nama fasyankes tujuan
     * @param idConditionDiagnosa ID Condition diagnosa (boleh null)
     * @param idCarePlan          CarePlan.id yang sudah ada (untuk Task.basedOn)
     * @return JsonNode response Bundle
     */
    public JsonNode kirimTugasRujukanRefCarePlan(String idPasienSS,
                                                  String idFaskesTujuanSS,
                                                  String namaFaskesTujuan,
                                                  String idConditionDiagnosa,
                                                  String idCarePlan,
                                                  String idEncounterSS) throws Exception {
        String now = ihs.getUtcDatetimeNow();
        String orgId = ihs.getOrgIdPerujuk();

        StringBuilder taskKirim = new StringBuilder();
        taskKirim.append("{")
          .append("\"resourceType\":\"Task\",")
          .append("\"status\":\"requested\",")
          .append("\"intent\":\"instance-order\",")
          .append("\"priority\":\"routine\",")
          .append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"referral-approval-request\",")
              .append("\"display\":\"Referral approval request\"")
          .append("}]},")
          .append("\"for\":{\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\"},")
          .append("\"encounter\":{\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\"},")
          .append("\"authoredOn\":\"").append(now).append("\",")
          .append("\"lastModified\":\"").append(now).append("\",")
          .append("\"requester\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},")
          .append("\"owner\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"}");

        // basedOn → CarePlan yang sudah ada
        if (idCarePlan != null && !idCarePlan.isEmpty()) {
            taskKirim.append(",\"basedOn\":[{\"reference\":\"CarePlan/").append(idCarePlan).append("\"}]");
        }

        if (idConditionDiagnosa != null && !idConditionDiagnosa.isEmpty()) {
            taskKirim.append(",\"reasonReference\":{\"reference\":\"Condition/")
                     .append(idConditionDiagnosa).append("\"}");
        }
        taskKirim.append(",\"input\":[{")
              .append("\"type\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"referral-task\",")
                  .append("\"display\":\"Referral task\"")
              .append("}]},")
              .append("\"valueReference\":{")
                  .append("\"reference\":\"").append(ihs.orgRef(idFaskesTujuanSS)).append("\",")
                  .append("\"display\":\"").append(escape(namaFaskesTujuan)).append("\"")
              .append("}")
          .append("}]")
          .append("}");

        // Wrap dalam Bundle (tipe transaction) - hanya 1 entry: Task
        StringBuilder bundle = new StringBuilder();
        bundle.append("{")
          .append("\"resourceType\":\"Bundle\",")
          .append("\"meta\":{\"tag\":[{")
              .append("\"system\":\"").append(SYS_KEMKES).append("\",")
              .append("\"code\":\"referral-approval\",")
              .append("\"display\":\"Referral approval\"")
          .append("}]},")
          .append("\"type\":\"transaction\",")
          .append("\"entry\":[{")
                .append("\"fullUrl\":\"urn:uuid:task-kirim\",")
                .append("\"resource\":").append(taskKirim).append(",")
                .append("\"request\":{\"method\":\"POST\",\"url\":\"Task\"}")
          .append("}]")
        .append("}");

        return postFhir("", bundle.toString());
    }

    // =================================================================
    //  STEP 3b: CEK STATUS ACCEPT/REJECT dari Fasyankes Tujuan
    //  Sec.2.3.2 - fasyankes tujuan PATCH Task untuk respon
    // =================================================================

    /**
     * Cek apakah Task pengiriman tugas rujukan sudah direspon (accepted/rejected)
     * oleh fasyankes tujuan.
     *
     * @param taskId ID Task pengiriman tugas rujukan (dari hasil step 3)
     * @return JsonNode Task terbaru (cek field output untuk accept/reject status)
     */
    public JsonNode cekStatusAccept(String taskId) throws Exception {
        return getFhir("/Task/" + taskId);
    }

    // =================================================================
    //  STEP 4: PENGIRIMAN RUJUKAN FINAL
    //  Resource: ServiceRequest (untuk Ranap & IGD - TANPA Bundle, langsung POST)
    //  Sec.2.4 + Sec.1.3.1 poin 2 playbook
    // =================================================================

    /**
     * Kirim ServiceRequest sebagai pengiriman rujukan final.
     * Hanya boleh dikirim setelah salah satu fasyankes tujuan sudah ACCEPT.
     *
     * @param idFaskesTujuanSS        ID Organization fasyankes tujuan FINAL (yang accept)
     * @param idCarePlan              ID CarePlan rencana rujukan (dari step 3, boleh null)
     * @param idConditionDiagnosa     ID Condition diagnosa (boleh null)
     * @param idConditionKriteriaKlinis ID Condition kriteria klinis (boleh null)
     * @param tipePerawatan           SNOMED_RANAP atau SNOMED_IGD
     * @param noRujukanPCare          nomor rujukan dari PCare BPJS (boleh null)
     * @param noKartuAsuransi         nomor kartu asuransi (boleh null)
     * @param orgIdAsuransi           ID Org asuransi untuk system identifier (boleh null)
     * @param kdLocationCodeJenisFaskes kode SNOMED jenis fasyankes (boleh null)
     * @param kdLocationCodeTransport kode SNOMED jenis transportasi (boleh null)
     * @param noRegAmbulans           nomor registrasi ambulans (boleh null)
     * @return JsonNode response (berisi Nomor Rujukan Nasional di identifier)
     */
    public JsonNode kirimRujukanFinal(String idFaskesTujuanSS,
                                       String idCarePlan,
                                       String idConditionDiagnosa,
                                       String idConditionKriteriaKlinis,
                                       String tipePerawatan,
                                       String noRujukanPCare,
                                       String noKartuAsuransi,
                                       String orgIdAsuransi,
                                       String kdLocationCodeJenisFaskes,
                                       String kdLocationCodeTransport,
                                       String noRegAmbulans,
                                       String idPasienSS,
                                       String idEncounterSS) throws Exception {

        String orgId  = ihs.getOrgIdPerujuk();
        String srCode = SNOMED_RANAP.equals(tipePerawatan) ? SNOMED_RANAP : SNOMED_IGD;
        String srDisp = SNOMED_RANAP.equals(tipePerawatan)
                ? "Inpatient care management" : "Emergency treatment management";

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"ServiceRequest\",");

        // identifier (PCare + asuransi) - opsional
        boolean hasId = (noRujukanPCare != null && !noRujukanPCare.isEmpty())
                || (noKartuAsuransi != null && !noKartuAsuransi.isEmpty());
        if (hasId) {
            sb.append("\"identifier\":[");
            boolean first = true;
            if (noRujukanPCare != null && !noRujukanPCare.isEmpty()) {
                sb.append("{")
                  .append("\"system\":\"http://sys-ids.kemkes.go.id/referral-number-pcare\",")
                  .append("\"value\":\"").append(escape(noRujukanPCare)).append("\"")
                .append("}");
                first = false;
            }
            if (noKartuAsuransi != null && !noKartuAsuransi.isEmpty()) {
                if (!first) sb.append(",");
                String orgAsr = (orgIdAsuransi != null && !orgIdAsuransi.isEmpty())
                        ? orgIdAsuransi : "bpjs-kesehatan";
                sb.append("{")
                  .append("\"system\":\"https://sys-ids.kemkes.go.id/insurance-subscriber/")
                      .append(escape(orgAsr)).append("\",")
                  .append("\"value\":\"").append(escape(noKartuAsuransi)).append("\"")
                .append("}");
            }
            sb.append("],");
        }

        sb.append("\"status\":\"active\",")
          .append("\"intent\":\"original-order\",");

        // basedOn → CarePlan
        if (idCarePlan != null && !idCarePlan.isEmpty()) {
            sb.append("\"basedOn\":[{\"reference\":\"CarePlan/").append(idCarePlan).append("\"}],");
        }

        // category - selalu Patient referral
        sb.append("\"category\":[{\"coding\":[{")
              .append("\"system\":\"").append(SYS_SNOMED).append("\",")
              .append("\"code\":\"3457005\",")
              .append("\"display\":\"Patient referral\"")
          .append("}]}],");

        // code (Lampiran 1)
        sb.append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_SNOMED).append("\",")
              .append("\"code\":\"").append(srCode).append("\",")
              .append("\"display\":\"").append(srDisp).append("\"")
          .append("}]},");

        // requester & performer
        sb.append("\"requester\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"},")
          .append("\"performer\":[{\"reference\":\"").append(ihs.orgRef(idFaskesTujuanSS)).append("\"}],");

        // subject (Patient) WAJIB di FHIR ServiceRequest
        sb.append("\"subject\":{\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\"}");

        // encounter (kontekstualkan ke kunjungan rawat inap/IGD ini)
        if (idEncounterSS != null && !idEncounterSS.isEmpty()) {
            sb.append(",\"encounter\":{\"reference\":\"").append(ihs.encounterRef(idEncounterSS)).append("\"}");
        }

        // locationCode - jenis faskes & transportasi (optional)
        boolean hasLoc = (kdLocationCodeJenisFaskes != null && !kdLocationCodeJenisFaskes.isEmpty())
                || (kdLocationCodeTransport != null && !kdLocationCodeTransport.isEmpty());
        if (hasLoc) {
            sb.append(",\"locationCode\":[");
            boolean first = true;
            if (kdLocationCodeJenisFaskes != null && !kdLocationCodeJenisFaskes.isEmpty()) {
                sb.append("{\"coding\":[{\"system\":\"").append(SYS_SNOMED).append("\",")
                  .append("\"code\":\"").append(escape(kdLocationCodeJenisFaskes)).append("\"")
                .append("}]}");
                first = false;
            }
            if (kdLocationCodeTransport != null && !kdLocationCodeTransport.isEmpty()) {
                if (!first) sb.append(",");
                sb.append("{\"coding\":[{\"system\":\"").append(SYS_SNOMED).append("\",")
                  .append("\"code\":\"").append(escape(kdLocationCodeTransport)).append("\"")
                .append("}]");
                if (noRegAmbulans != null && !noRegAmbulans.isEmpty()) {
                    sb.append(",\"text\":\"").append(escape(noRegAmbulans)).append("\"");
                }
                sb.append("}");
            }
            sb.append("]");
        }

        // reasonReference (diagnosa + kriteria klinis)
        boolean hasReason = (idConditionDiagnosa != null && !idConditionDiagnosa.isEmpty())
                || (idConditionKriteriaKlinis != null && !idConditionKriteriaKlinis.isEmpty());
        if (hasReason) {
            sb.append(",\"reasonReference\":[");
            boolean first = true;
            if (idConditionDiagnosa != null && !idConditionDiagnosa.isEmpty()) {
                sb.append("{\"reference\":\"Condition/").append(idConditionDiagnosa).append("\"}");
                first = false;
            }
            if (idConditionKriteriaKlinis != null && !idConditionKriteriaKlinis.isEmpty()) {
                if (!first) sb.append(",");
                sb.append("{\"reference\":\"Condition/").append(idConditionKriteriaKlinis).append("\"}");
            }
            sb.append("]");
        }

        sb.append("}");
        return postFhir("/ServiceRequest", sb.toString());
    }

    // =================================================================
    //  HELPER: build CarePlan body (untuk dipakai di step 3)
    //  Sec.1.3.1 poin 3 + Lampiran 3 playbook
    // =================================================================

    /**
     * Build JSON CarePlan rencana rujukan.
     *
     * @param idDokterSS         IHS dokter (author)
     * @param idFaskesTujuanSS   ID Organization fasyankes tujuan
     * @param idConditionDiagnosa ID Condition diagnosa (boleh null)
     * @param tipePerawatan      SNOMED_RANAP / SNOMED_IGD
     * @param kdSpesialisasi     kode spesialisasi (boleh null)
     * @param kdKelasPerawatan   kode kelas perawatan (boleh null)
     * @return JSON string CarePlan
     */
    public String buildCarePlanJson(String idDokterSS,
                                     String idFaskesTujuanSS,
                                     String idConditionDiagnosa,
                                     String tipePerawatan,
                                     String kdSpesialisasi,
                                     String kdKelasPerawatan) {
        String orgId = ihs.getOrgIdPerujuk();
        boolean isRanap = SNOMED_RANAP.equals(tipePerawatan);
        String catSys  = isRanap ? CP_CAT_RANAP_SYS  : CP_CAT_IGD_SYS;
        String catCode = isRanap ? CP_CAT_RANAP_CODE : CP_CAT_IGD_CODE;
        String catDisp = isRanap ? CP_CAT_RANAP_DISP : CP_CAT_IGD_DISP;
        // Ranap dengan jadwal → Appointment. IGD segera → ServiceRequest.
        String activityKind = isRanap ? "Appointment" : "ServiceRequest";

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"CarePlan\",")
          .append("\"identifier\":[{")
              .append("\"system\":\"http://sys-ids.kemkes.go.id/careplan/authoring-organization\",")
              .append("\"value\":\"").append(orgId).append("\"")
          .append("}],")
          .append("\"status\":\"active\",")
          .append("\"intent\":\"plan\",")
          .append("\"category\":[")
              // category[0] - Lampiran 3
              .append("{\"coding\":[{")
                  .append("\"system\":\"").append(catSys).append("\",")
                  .append("\"code\":\"").append(catCode).append("\",")
                  .append("\"display\":\"").append(catDisp).append("\"")
              .append("}]},")
              // category[1] - selalu Patient referral
              .append("{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_SNOMED).append("\",")
                  .append("\"code\":\"3457005\",")
                  .append("\"display\":\"Patient referral\"")
              .append("}]}")
          .append("],")
          .append("\"author\":{\"reference\":\"").append(ihs.practitionerRef(idDokterSS)).append("\"},")
          .append("\"contributor\":[{\"reference\":\"").append(ihs.orgRef(idFaskesTujuanSS)).append("\"}]");

        // addresses - diagnosa
        if (idConditionDiagnosa != null && !idConditionDiagnosa.isEmpty()) {
            sb.append(",\"addresses\":[{\"reference\":\"Condition/").append(idConditionDiagnosa).append("\"}]");
        }

        // activity - kebutuhan rujukan minimal 1
        sb.append(",\"activity\":[");

        // activity untuk tipe perawatan (jenis pelayanan utama)
        sb.append("{\"detail\":{")
          .append("\"kind\":\"").append(activityKind).append("\",")
          .append("\"status\":\"not-started\",")
          .append("\"code\":{\"coding\":[{")
              .append("\"system\":\"").append(SYS_SNOMED).append("\",")
              .append("\"code\":\"").append(tipePerawatan).append("\",")
              .append("\"display\":\"").append(isRanap ? "Inpatient care management" : "Emergency treatment management").append("\"")
          .append("}]}")
          .append("}}");

        // activity spesialisasi (kalau ada)
        if (kdSpesialisasi != null && !kdSpesialisasi.isEmpty()) {
            sb.append(",{\"detail\":{")
              .append("\"kind\":\"").append(activityKind).append("\",")
              .append("\"status\":\"not-started\",")
              .append("\"code\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"").append(escape(kdSpesialisasi)).append("\"")
              .append("}]}")
              .append("}}");
        }

        // activity kelas perawatan (kalau ada)
        if (kdKelasPerawatan != null && !kdKelasPerawatan.isEmpty()) {
            sb.append(",{\"detail\":{")
              .append("\"kind\":\"").append(activityKind).append("\",")
              .append("\"status\":\"not-started\",")
              .append("\"code\":{\"coding\":[{")
                  .append("\"system\":\"").append(SYS_KEMKES).append("\",")
                  .append("\"code\":\"").append(escape(kdKelasPerawatan)).append("\"")
              .append("}]}")
              .append("}}");
        }

        sb.append("]");  // tutup activity
        sb.append("}");
        return sb.toString();
    }

    // =================================================================
    //  EKSTRAKSI HASIL
    // =================================================================

    /**
     * Ambil daftar kandidat fasyankes dari respon Task SATUSEHAT step 2.
     * Return null jika tidak ditemukan.
     */
    public JsonNode getKandidatDariRespon(JsonNode taskResp) {
        if (taskResp == null) return null;
        JsonNode outputs = taskResp.path("output");
        return outputs.isArray() ? outputs : null;
    }

    /**
     * Ambil Nomor Rujukan Nasional dari respon ServiceRequest step 4.
     * @return nomor rujukan nasional, atau null kalau tidak ditemukan.
     */
    public String getNomorRujukanNasional(JsonNode srResp) {
        if (srResp == null) return null;
        JsonNode ids = srResp.path("identifier");
        if (!ids.isArray()) return null;
        for (JsonNode id : ids) {
            String sys = id.path("system").asText();
            if (sys.contains("referral-number-satusehat")) {
                return id.path("value").asText();
            }
        }
        return null;
    }

    /**
     * Cek apakah Task pengiriman tugas sudah accepted oleh fasyankes tujuan.
     * Cek di field output dengan code "response-referral-task".
     *
     * @return Boolean.TRUE=accepted, Boolean.FALSE=rejected, null=belum direspon
     */
    public Boolean cekResponAccept(JsonNode taskResp) {
        if (taskResp == null) return null;
        JsonNode outputs = taskResp.path("output");
        if (!outputs.isArray()) return null;

        for (JsonNode out : outputs) {
            JsonNode typeCoding = out.path("type").path("coding");
            if (!typeCoding.isArray()) continue;
            for (JsonNode tc : typeCoding) {
                if ("response-referral-task".equals(tc.path("code").asText())) {
                    String code = out.path("valueCoding").path("code").asText();
                    if ("accepted".equalsIgnoreCase(code)) return Boolean.TRUE;
                    if ("rejected".equalsIgnoreCase(code)) return Boolean.FALSE;
                }
            }
        }
        return null;
    }

    // =================================================================
    //  ====== MODUL RUJUK MASUK (RS Anda = Fasyankes Tujuan) ======
    //
    //  Sesuai Playbook Bab 1.3.2 (Rajal) & 2.3.2 + 2.4 (Ranap & IGD).
    //
    //  Flow:
    //    1. cariRujukanMasuk()        → GET ServiceRequest?performer={my-org}
    //    2. getServiceRequestById()   → detail rujukan
    //    3. getCarePlanById()         → lihat rencana rujukan
    //    4. patchTaskAccept/Reject()  → respon tugas (untuk ranap/IGD only)
    //    5. kirimEncounterRujukan()   → daftar kunjungan dari rujukan
    // =================================================================

    /**
     * Cari semua rujukan masuk yang ditujukan ke RS Anda (sebagai performer).
     *
     * @param tglMulaiYmd tanggal mulai filter (YYYY-MM-DD), boleh null
     * @param tglAkhirYmd tanggal akhir filter (YYYY-MM-DD), boleh null
     * @param status      filter ServiceRequest.status (active/completed/etc), boleh null
     * @return JsonNode response Bundle (entry[] berisi ServiceRequest)
     */
    public JsonNode cariRujukanMasuk(String tglMulaiYmd, String tglAkhirYmd,
                                      String status) throws Exception {
        String orgId = ihs.getOrgIdPerujuk();
        StringBuilder query = new StringBuilder();
        query.append("/ServiceRequest?performer=Organization/").append(orgId);

        // Filter category=Patient referral (SNOMED 3457005)
        // PENTING: tanpa filter ini, akan ikut ServiceRequest untuk permintaan
        // radiologi/laboratorium internal yang RS Anda jadi performer-nya.
        // Sesuai playbook, rujukan pasien pakai category SNOMED 3457005.
        query.append("&category=").append(SYS_SNOMED).append("|3457005");

        if (tglMulaiYmd != null && !tglMulaiYmd.isEmpty()) {
            query.append("&authored=ge").append(tglMulaiYmd);
        }
        if (tglAkhirYmd != null && !tglAkhirYmd.isEmpty()) {
            query.append("&authored=le").append(tglAkhirYmd);
        }
        if (status != null && !status.isEmpty()) {
            query.append("&status=").append(status);
        }
        query.append("&_count=200&_sort=-authored");

        return getFhir(query.toString());
    }

    /**
     * Cari rujukan masuk berdasarkan Nomor Rujukan Nasional.
     * @param noRujukanNasional nomor rujukan nasional dari RS perujuk
     */
    public JsonNode cariRujukanMasukByNomor(String noRujukanNasional) throws Exception {
        String url = "/ServiceRequest?identifier=http://sys-ids.kemkes.go.id/referral-number-satusehat|"
                + java.net.URLEncoder.encode(noRujukanNasional, "UTF-8");
        return getFhir(url);
    }

    /** Detail ServiceRequest by ID. */
    public JsonNode getServiceRequestById(String srId) throws Exception {
        return getFhir("/ServiceRequest/" + srId);
    }

    /** Detail ServiceRequest with supporting info (data klinis pendukung). */
    public JsonNode getServiceRequestWithSupport(String srId) throws Exception {
        return getFhir("/ServiceRequest/" + srId + "?_include=ServiceRequest:supporting-info");
    }

    /** Detail CarePlan by ID. */
    public JsonNode getCarePlanById(String cpId) throws Exception {
        return getFhir("/CarePlan/" + cpId);
    }

    /** CarePlan dengan supporting info. */
    public JsonNode getCarePlanWithSupport(String cpId) throws Exception {
        return getFhir("/CarePlan/" + cpId + "?_include=CarePlan:supporting-info");
    }

    /**
     * Cari Task pengiriman tugas rujukan untuk fasyankes tujuan ini.
     * Diperlukan untuk dapat task_id yang akan di-PATCH (accept/reject).
     *
     * Kriteria: Task.code='referral-approval-request' AND Task.input.valueReference=Org/{my-id}
     *
     * Karena FHIR search tidak gampang filter Task.input, kita pakai pendekatan:
     * GET Task?code=referral-approval-request&owner=Organization/{my-id}
     * (atau filter lain di server SATUSEHAT)
     */
    public JsonNode cariTaskRujukanMasuk(String idPasienSS) throws Exception {
        String orgId = ihs.getOrgIdPerujuk();
        StringBuilder url = new StringBuilder("/Task?")
                .append("code=referral-approval-request")
                .append("&_count=50&_sort=-authored");
        if (idPasienSS != null && !idPasienSS.isEmpty()) {
            url.append("&subject=Patient/").append(idPasienSS);
        }
        return getFhir(url.toString());
    }

    /**
     * Accept Task rujukan masuk (RS Anda = fasyankes tujuan).
     * Sec.2.3.2 playbook - PATCH Task untuk respon accept.
     *
     * @param taskId Task.id yang akan di-PATCH
     * @return JsonNode response
     */
    public JsonNode patchTaskAccept(String taskId) throws Exception {
        String body = "[{"
                + "\"op\":\"add\","
                + "\"path\":\"/output\","
                + "\"value\":[{"
                    + "\"type\":{\"coding\":[{"
                        + "\"system\":\"" + SYS_KEMKES + "\","
                        + "\"code\":\"response-referral-task\","
                        + "\"display\":\"Response referral task\""
                    + "}]},"
                    + "\"valueCoding\":{"
                        + "\"system\":\"" + SYS_TASKSTAT + "\","
                        + "\"code\":\"accepted\","
                        + "\"display\":\"Accepted\""
                    + "}"
                + "}]"
                + "},{"
                + "\"op\":\"replace\","
                + "\"path\":\"/status\","
                + "\"value\":\"accepted\""
                + "}]";
        return patchFhir("/Task/" + taskId, body);
    }

    /**
     * Reject Task rujukan masuk.
     * @param taskId    Task.id
     * @param reason    alasan penolakan (boleh null)
     */
    public JsonNode patchTaskReject(String taskId, String reason) throws Exception {
        String reasonJson = "";
        if (reason != null && !reason.isEmpty()) {
            reasonJson = ",{"
                    + "\"op\":\"add\","
                    + "\"path\":\"/statusReason\","
                    + "\"value\":{\"text\":\"" + escape(reason) + "\"}"
                    + "}";
        }
        String body = "[{"
                + "\"op\":\"add\","
                + "\"path\":\"/output\","
                + "\"value\":[{"
                    + "\"type\":{\"coding\":[{"
                        + "\"system\":\"" + SYS_KEMKES + "\","
                        + "\"code\":\"response-referral-task\","
                        + "\"display\":\"Response referral task\""
                    + "}]},"
                    + "\"valueCoding\":{"
                        + "\"system\":\"" + SYS_TASKSTAT + "\","
                        + "\"code\":\"rejected\","
                        + "\"display\":\"Rejected\""
                    + "}"
                + "}]"
                + "},{"
                + "\"op\":\"replace\","
                + "\"path\":\"/status\","
                + "\"value\":\"rejected\""
                + "}"
                + reasonJson
                + "]";
        return patchFhir("/Task/" + taskId, body);
    }

    /**
     * Kirim Encounter sebagai pendaftaran kunjungan dari rujukan masuk.
     * Sec.1.3.2 + Sec.2.4 playbook - Encounter.basedOn = ServiceRequest.id
     *
     * @param idPasienSS         IHS pasien (didapat dari ServiceRequest atau lookup NIK)
     * @param idDokterSS         IHS dokter penerima (DPJP di RS Anda)
     * @param idServiceRequest   ID ServiceRequest rujukan masuk (untuk basedOn)
     * @param tipePerawatan      "AMB" untuk rajal, "IMP" untuk ranap, "EMER" untuk IGD
     *                           (sesuai HL7 v3 EncounterClass)
     * @param idLocationSS       ID Location di Satu Sehat (boleh null)
     * @return JsonNode response Encounter
     */
    public JsonNode kirimEncounterRujukanMasuk(String idPasienSS, String idDokterSS,
                                                String idServiceRequest,
                                                String tipePerawatan,
                                                String idLocationSS) throws Exception {
        String now = ihs.getUtcDatetimeNow();
        String orgId = ihs.getOrgIdPerujuk();
        String classCode = "AMB", classDisp = "ambulatory";
        if ("IMP".equalsIgnoreCase(tipePerawatan)) { classCode = "IMP"; classDisp = "inpatient encounter"; }
        else if ("EMER".equalsIgnoreCase(tipePerawatan)) { classCode = "EMER"; classDisp = "emergency"; }

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"Encounter\",")
          .append("\"status\":\"arrived\",")
          .append("\"class\":{")
              .append("\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",")
              .append("\"code\":\"").append(classCode).append("\",")
              .append("\"display\":\"").append(classDisp).append("\"")
          .append("},")
          .append("\"subject\":{\"reference\":\"").append(ihs.patientRef(idPasienSS)).append("\"},")
          .append("\"basedOn\":[{\"reference\":\"ServiceRequest/").append(idServiceRequest).append("\"}],")
          .append("\"participant\":[{")
              .append("\"type\":[{\"coding\":[{")
                  .append("\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\",")
                  .append("\"code\":\"ATND\",")
                  .append("\"display\":\"attender\"")
              .append("}]}],")
              .append("\"individual\":{\"reference\":\"").append(ihs.practitionerRef(idDokterSS)).append("\"}")
          .append("}],")
          .append("\"period\":{\"start\":\"").append(now).append("\"},")
          .append("\"serviceProvider\":{\"reference\":\"").append(ihs.orgRef(orgId)).append("\"}");

        if (idLocationSS != null && !idLocationSS.isEmpty()) {
            sb.append(",\"location\":[{")
              .append("\"location\":{\"reference\":\"Location/").append(idLocationSS).append("\"}")
            .append("}]");
        }
        sb.append("}");

        return postFhir("/Encounter", sb.toString());
    }

    // =================================================================
    //  EKSTRAKSI HASIL untuk Rujuk Masuk
    // =================================================================

    /** Ambil Encounter.id dari respon kirimEncounterRujukanMasuk(). */
    public String getEncounterId(JsonNode encResp) {
        if (encResp == null) return null;
        String id = encResp.path("id").asText();
        return id.isEmpty() ? null : id;
    }

    /**
     * Parse ServiceRequest.code.coding.code untuk tentukan tipe rujukan.
     * @return "rajal" / "ranap" / "igd" / "" kalau tidak dikenal
     */
    public String parseTipeRujukan(JsonNode srResource) {
        if (srResource == null) return "";
        JsonNode coding = srResource.path("code").path("coding");
        if (!coding.isArray()) return "";
        for (JsonNode c : coding) {
            String code = c.path("code").asText();
            if (SNOMED_RAJAL.equals(code)) return "rajal";
            if (SNOMED_RANAP.equals(code)) return "ranap";
            if (SNOMED_IGD.equals(code)) return "igd";
        }
        return "";
    }

    /** Ambil patient ID dari ServiceRequest.subject. */
    public String parsePatientIdFromSR(JsonNode srResource) {
        if (srResource == null) return "";
        String ref = srResource.path("subject").path("reference").asText();
        return ref.startsWith("Patient/") ? ref.substring(8) : "";
    }

    /** Ambil requester (faskes perujuk) ID dari ServiceRequest. */
    public String parseRequesterIdFromSR(JsonNode srResource) {
        if (srResource == null) return "";
        String ref = srResource.path("requester").path("reference").asText();
        return ref.startsWith("Organization/") ? ref.substring(13) : "";
    }

    /** Ambil CarePlan id (basedOn) dari ServiceRequest. */
    public String parseCarePlanIdFromSR(JsonNode srResource) {
        if (srResource == null) return "";
        JsonNode basedOn = srResource.path("basedOn");
        if (!basedOn.isArray()) return "";
        for (JsonNode bo : basedOn) {
            String ref = bo.path("reference").asText();
            if (ref.startsWith("CarePlan/")) return ref.substring(9);
        }
        return "";
    }

    /** Ambil Nomor Rujukan PCare dari ServiceRequest.identifier. */
    public String parseNoRujukanPCareFromSR(JsonNode srResource) {
        if (srResource == null) return "";
        JsonNode ids = srResource.path("identifier");
        if (!ids.isArray()) return "";
        for (JsonNode id : ids) {
            String sys = id.path("system").asText();
            if (sys.contains("referral-number-pcare")) {
                return id.path("value").asText();
            }
        }
        return "";
    }

    /** Ambil No Kartu Asuransi dari ServiceRequest.identifier. */
    public String parseNoKartuAsuransiFromSR(JsonNode srResource) {
        if (srResource == null) return "";
        JsonNode ids = srResource.path("identifier");
        if (!ids.isArray()) return "";
        for (JsonNode id : ids) {
            String sys = id.path("system").asText();
            if (sys.contains("insurance-subscriber")) {
                return id.path("value").asText();
            }
        }
        return "";
    }

    // =================================================================
    //  HTTP HELPERS - pakai ApiSatuSehat
    // =================================================================

    /**
     * POST ke FHIR Satu Sehat.
     * Auto-retry sekali kalau dapat 401 (force refresh token, sesuai pattern di ApiSatuSehat).
     *
     * @param pathFhir mis. "/Task", "/ServiceRequest", atau "" untuk transaction Bundle
     * @param body     JSON body
     */
    private JsonNode postFhir(String pathFhir, String body) throws Exception {
        String url = ihs.getBaseUrl() + (pathFhir == null ? "" : pathFhir);
        RestTemplate rest = ihs.getRest();

        System.out.println("==== SatuSehatRujukan POST ====");
        System.out.println("URL : " + url);
        System.out.println("Body: " + body);

        try {
            HttpEntity<String> entity = new HttpEntity<>(body, ihs.buildAuthHeaders());
            ResponseEntity<String> resp = rest.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Status: " + resp.getStatusCode());
            System.out.println("Response: " + resp.getBody());
            return ihs.getMapper().readTree(resp.getBody());

        } catch (HttpClientErrorException e) {
            // Auto-retry kalau 401 (token mungkin expired walau cache bilang valid)
            if (e.getStatusCode().value() == 401) {
                System.out.println("Got 401, force refresh token & retry...");
                ihs.forceRefreshToken();
                HttpEntity<String> entity = new HttpEntity<>(body, ihs.buildAuthHeaders());
                ResponseEntity<String> resp = rest.exchange(url, HttpMethod.POST, entity, String.class);
                System.out.println("Status (retry): " + resp.getStatusCode());
                System.out.println("Response (retry): " + resp.getBody());
                return ihs.getMapper().readTree(resp.getBody());
            }
            // Bukan 401 - log body errornya supaya gampang debug
            System.out.println("HTTP error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * GET ke FHIR Satu Sehat.
     * Auto-retry sekali kalau dapat 401.
     *
     * @param pathFhir mis. "/Task/abc-123"
     */
    private JsonNode getFhir(String pathFhir) throws Exception {
        String url = ihs.getBaseUrl() + (pathFhir == null ? "" : pathFhir);
        RestTemplate rest = ihs.getRest();

        System.out.println("==== SatuSehatRujukan GET ====");
        System.out.println("URL : " + url);

        try {
            HttpEntity<String> entity = new HttpEntity<>(ihs.buildAuthHeaders());
            ResponseEntity<String> resp = rest.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println("Status: " + resp.getStatusCode());
            System.out.println("Response: " + resp.getBody());
            return ihs.getMapper().readTree(resp.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                System.out.println("Got 401, force refresh token & retry...");
                ihs.forceRefreshToken();
                HttpEntity<String> entity = new HttpEntity<>(ihs.buildAuthHeaders());
                ResponseEntity<String> resp = rest.exchange(url, HttpMethod.GET, entity, String.class);
                System.out.println("Status (retry): " + resp.getStatusCode());
                System.out.println("Response (retry): " + resp.getBody());
                return ihs.getMapper().readTree(resp.getBody());
            }
            System.out.println("HTTP error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * PATCH ke FHIR Satu Sehat (untuk update partial, mis. PATCH Task accept/reject).
     * Body adalah JSON Patch (array of ops).
     * Auto-retry kalau dapat 401.
     *
     * IMPLEMENTASI: pakai HttpURLConnection langsung (Java standard) supaya
     * tidak tergantung HttpMethod.PATCH dari Spring (yang baru ada di Spring 4.3+).
     * Ini bekerja untuk semua versi Spring.
     */
    private JsonNode patchFhir(String pathFhir, String body) throws Exception {
        String url = ihs.getBaseUrl() + (pathFhir == null ? "" : pathFhir);

        System.out.println("==== SatuSehatRujukan PATCH ====");
        System.out.println("URL : " + url);
        System.out.println("Body: " + body);

        // Try sekali, kalau 401 retry dengan force refresh token
        try {
            String responseBody = doPatchHttpURL(url, body, ihs.getValidToken());
            return ihs.getMapper().readTree(responseBody);
        } catch (Exception ex) {
            // Cek kalau errornya 401 dari response code
            if (ex.getMessage() != null && ex.getMessage().contains("401")) {
                System.out.println("Got 401, force refresh token & retry...");
                ihs.forceRefreshToken();
                String responseBody = doPatchHttpURL(url, body, ihs.getValidToken());
                return ihs.getMapper().readTree(responseBody);
            }
            System.out.println("PATCH error: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Helper: do PATCH via HttpURLConnection (no Spring dependency for PATCH method).
     * Throws Exception kalau response code bukan 2xx.
     */
    private String doPatchHttpURL(String url, String body, String token) throws Exception {
        java.net.URL u = new java.net.URL(url);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();

        // PATCH belum jadi standard HttpURLConnection method, tapi kita bisa
        // override via reflection atau pakai trick X-HTTP-Method-Override.
        // Cara paling reliable: pakai reflection ke field "method"
        try {
            conn.setRequestMethod("PATCH");
        } catch (java.net.ProtocolException pex) {
            // Fallback: set via reflection (some JVMs allow it)
            try {
                java.lang.reflect.Field methodField = java.net.HttpURLConnection.class.getDeclaredField("method");
                methodField.setAccessible(true);
                methodField.set(conn, "PATCH");
            } catch (Exception refEx) {
                // Ultimate fallback: pakai POST + X-HTTP-Method-Override header
                conn.setRequestMethod("POST");
                conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            }
        }

        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json-patch+json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
            os.flush();
        }

        int respCode = conn.getResponseCode();
        java.io.InputStream is = (respCode >= 200 && respCode < 300)
                ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder sb = new StringBuilder();
        if (is != null) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
        }
        String respBody = sb.toString();

        System.out.println("Status: " + respCode);
        System.out.println("Response: " + respBody);

        if (respCode < 200 || respCode >= 300) {
            throw new Exception("PATCH failed with HTTP " + respCode + ": " + respBody);
        }
        return respBody;
    }


    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ");
    }

    // =================================================================
    //  TESTING - jalankan main() dari NetBeans untuk uji koneksi
    // =================================================================

    public static void main(String[] args) {
        SatuSehatRujukanService svc = new SatuSehatRujukanService();
        try {
            // ==== TES 1: Pra Permintaan ====
            System.out.println("\n>>> TES 1: kirimPraPermintaan");
            JsonNode resp1 = svc.kirimPraPermintaan(
                    "P12345678901",       // IHS pasien (ganti dengan yang valid)
                    "ENC-12345",          // ID encounter (ganti dengan yang valid)
                    "I10",
                    "Essential (primary) hypertension"
            );
            System.out.println("Task ID: " + resp1.path("id").asText());

            // ==== TES 2-4 di-comment, uncomment sesuai kebutuhan ====
            // String qrCriteria = "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"qr-criteria\",\"status\":\"completed\",\"item\":[{\"linkId\":\"1\",\"answer\":[{\"valueBoolean\":true}]}]}";
            // String qrArea = "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"qr-area\",\"status\":\"completed\",\"item\":[{\"linkId\":\"prov\",\"answer\":[{\"valueString\":\"31\"}]}]}";
            // JsonNode resp2 = svc.cariKandidatFasyankes(SNOMED_RANAP, "P12345678901", "I10", "Essential hypertension", "", "", qrCriteria, qrArea);
            // System.out.println(resp2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}