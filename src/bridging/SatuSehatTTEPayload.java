/*
  Logika murni TTE SatuSehat: penyusunan payload dan pembacaan respons.

  Dipisahkan dari SatuSehatKirimTTE supaya bisa diuji tanpa memuat komponen
  Swing -- memuat kelas form ikut menarik seluruh paket widget sehingga harness
  uji gagal sebelum sempat memanggil apa pun. Kelas ini tidak punya
  ketergantungan UI maupun basis data.
 */

package bridging;

import com.fasterxml.jackson.databind.JsonNode;

/** Penyusun payload dan pembaca respons TTE. Semua metode murni. */
public final class SatuSehatTTEPayload {

    private SatuSehatTTEPayload() { }

    public static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    /**
     * Bundle transaction berisi N Provenance (placeholder) + 1 Task, jalur "01b.
     * Bundle Buat TTE" pada collection resmi. Task merujuk tiap Provenance lewat
     * urn:uuid sehingga semuanya dibuat dalam satu request tanpa menunggu ID.
     *
     * Satu Task boleh membungkus beberapa Provenance -- satu Provenance per dokumen
     * (varian "Task - Multiple Provenance"). Seluruh dokumen di sini milik satu
     * encounter/DPJP yang sama, jadi sekali pindai QR menandatangani semuanya.
     * Array uuidProv/idResource/tipe/jenis sejajar berdasarkan indeks, dan urutan
     * entry Provenance di request = urutan ID pada response (lihat idProvenanceUrut).
     *
     * {@code tipe[i]} adalah tipe resource target ("Composition", "ServiceRequest",
     * "DiagnosticReport", ...) sehingga satu Task boleh mencampur beragam dokumen --
     * bukan hanya Composition. {@code idResource[i]} = id resource itu.
     *
     * Dua hal yang berbeda dari contoh resmi, keduanya hasil uji POST nyata:
     *
     * 1. fullUrl disertakan pada SETIAP entry. Tanpa fullUrl pada Task, platform
     *    menolak dengan "Invalid resource Task due to missing fullUrl element"
     *    (RuleNumber 20021).
     * 2. Task.encounter disertakan karena platform mewajibkannya (RuleNumber
     *    10875); collection versi 21072026 kini ikut memuatnya (dengan display),
     *    padahal versi lama dan slide tidak.
     */
    public static String buildBundleMulti(String[] uuidProv, String[] idResource, String[] tipe,
                                          String[] jenis, String uuidTask, String idPractitioner,
                                          String nmDokter, String idEncounter, String idOrganization,
                                          String namaRS, String waktuIso) {
        return buildBundleMulti(uuidProv, idResource, tipe, jenis, uuidTask, idPractitioner,
                nmDokter, idEncounter, idOrganization, namaRS, waktuIso, "", "");
    }

    /**
     * Varian yang menyertakan {@code occurredPeriod} — periode pelayanan yang
     * didokumentasikan, diambil dari {@code Encounter.period}.
     *
     * Dibedakan dari {@code recorded}: {@code recorded} = kapan permintaan tanda
     * tangan ini dibuat, {@code occurredPeriod} = kapan pelayanan yang didokumenkan
     * itu berlangsung. Contoh Bundle resmi memuat keduanya.
     *
     * {@code mulaiIso} kosong -> {@code occurredPeriod} tidak dikirim sama sekali.
     * {@code selesaiIso} kosong -> hanya {@code start} yang dikirim; itu sah untuk
     * pasien yang masih dirawat, dan lebih jujur daripada mengarang waktu selesai.
     */
    public static String buildBundleMulti(String[] uuidProv, String[] idResource, String[] tipe,
                                          String[] jenis, String uuidTask, String idPractitioner,
                                          String nmDokter, String idEncounter, String idOrganization,
                                          String namaRS, String waktuIso,
                                          String mulaiIso, String selesaiIso) {
        return buildBundleMulti(uuidProv, idResource, tipe, jenis, uuidTask, idPractitioner, nmDokter,
                idEncounter, idOrganization, namaRS, waktuIso, mulaiIso, selesaiIso, "");
    }

    /**
     * Varian dengan chaining signature: bila {@code chainProvenanceId} tidak kosong,
     * setiap Provenance menambahkan entity ke-2 {@code role=source} yang menunjuk
     * Provenance sebelumnya (mis. Provenance dr. Residen). Dipakai untuk tanda tangan
     * berjenjang DPJP Supervisi menautkan tanda tangan Residen (Postman 04082026).
     */
    public static String buildBundleMulti(String[] uuidProv, String[] idResource, String[] tipe,
                                          String[] jenis, String uuidTask, String idPractitioner,
                                          String nmDokter, String idEncounter, String idOrganization,
                                          String namaRS, String waktuIso,
                                          String mulaiIso, String selesaiIso, String chainProvenanceId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"Bundle\",\"type\":\"transaction\",\"entry\":[");

        // --- entry Provenance placeholder (signature kosong), satu per dokumen ---
        for (int i = 0; i < uuidProv.length; i++) {
            sb.append("{\"fullUrl\":\"urn:uuid:").append(uuidProv[i]).append("\",\"resource\":{");
            sb.append("\"resourceType\":\"Provenance\",");
            sb.append("\"target\":[{\"reference\":\"").append(tipe[i]).append("/").append(idResource[i]).append("\",")
              .append("\"display\":\"").append(jsonEscape(jenis[i])).append("\"}],");
            // occurredPeriod: rentang pelayanan yang didokumentasikan, bukan waktu
            // pengajuan TTE (itu "recorded" di bawah). Dilewati bila tidak diketahui.
            if (mulaiIso != null && !mulaiIso.trim().isEmpty()) {
                sb.append("\"occurredPeriod\":{\"start\":\"").append(mulaiIso.trim()).append("\"");
                if (selesaiIso != null && !selesaiIso.trim().isEmpty()) {
                    sb.append(",\"end\":\"").append(selesaiIso.trim()).append("\"");
                }
                sb.append("},");
            }
            sb.append("\"recorded\":\"").append(waktuIso).append("\",");
            sb.append("\"activity\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-DataOperation\",")
              .append("\"code\":\"CREATE\",\"display\":\"create\"}]},");
            sb.append("\"agent\":[{");
            sb.append("\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/provenance-participant-type\",")
              .append("\"code\":\"author\",\"display\":\"Author\"}]},");
            sb.append("\"who\":{\"reference\":\"Practitioner/").append(idPractitioner).append("\",")
              .append("\"display\":\"").append(jsonEscape(nmDokter)).append("\"},");
            sb.append("\"onBehalfOf\":{\"reference\":\"Organization/").append(idOrganization).append("\",")
              .append("\"display\":\"").append(jsonEscape(namaRS)).append("\"}");
            sb.append("}],");
            // entity[] role=source: dokumen sumber yang ditandatangani. Baru muncul di
            // collection "00. TTE versi 22072026" — versi 21072026 belum memuatnya.
            // Menunjuk resource yang sama dengan target, tanpa display.
            sb.append("\"entity\":[{\"role\":\"source\",\"what\":{\"reference\":\"")
              .append(tipe[i]).append("/").append(idResource[i]).append("\"}}");
            // Chaining: DPJP Supervisi menautkan Provenance Residen sebagai source ke-2.
            if (chainProvenanceId != null && !chainProvenanceId.isEmpty()) {
                sb.append(",{\"role\":\"source\",\"what\":{\"reference\":\"Provenance/")
                  .append(chainProvenanceId).append("\"}}");
            }
            sb.append("],");
            sb.append("\"signature\":[]");
            sb.append("},\"request\":{\"method\":\"POST\",\"url\":\"Provenance\"}},");
        }

        // --- entry Task yang menunjuk seluruh Provenance di atas ---
        sb.append("{\"fullUrl\":\"urn:uuid:").append(uuidTask).append("\",\"resource\":{");
        sb.append("\"resourceType\":\"Task\",\"status\":\"requested\",\"intent\":\"order\",\"priority\":\"routine\",");
        sb.append("\"code\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id\",")
          .append("\"code\":\"electronic-sign-doc\",\"display\":\"Penandatanganan dokumen elektronik\"}],")
          .append("\"text\":\"Penandatanganan elektronik dokumen\"},");
        sb.append("\"authoredOn\":\"").append(waktuIso).append("\",");
        sb.append("\"lastModified\":\"").append(waktuIso).append("\",");
        sb.append("\"encounter\":{\"reference\":\"Encounter/").append(idEncounter).append("\"},");
        sb.append("\"requester\":{\"reference\":\"Practitioner/").append(idPractitioner).append("\",")
          .append("\"display\":\"").append(jsonEscape(nmDokter)).append("\"},");
        sb.append("\"owner\":{\"reference\":\"Organization/").append(idOrganization).append("\",")
          .append("\"display\":\"").append(jsonEscape(namaRS)).append("\"},");
        sb.append("\"input\":[");
        for (int i = 0; i < uuidProv.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"type\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id\",")
              .append("\"code\":\"document-provenance\",\"display\":\"Document Provenance\"}],")
              .append("\"text\":\"Dokumen yang akan ditandatangani\"},");
            sb.append("\"valueReference\":{\"reference\":\"urn:uuid:").append(uuidProv[i]).append("\"}}");
        }
        sb.append("]");
        sb.append("},\"request\":{\"method\":\"POST\",\"url\":\"Task\"}}");

        sb.append("]}");
        return sb.toString();
    }

    /** Varian satu dokumen Composition: Task dengan satu Provenance. */
    public static String buildBundle(String uuidProv, String uuidTask, String idComposition,
                                     String idPractitioner, String nmDokter, String idEncounter,
                                     String jenis, String idOrganization, String namaRS,
                                     String waktuIso) {
        return buildBundleMulti(new String[]{uuidProv}, new String[]{idComposition},
                new String[]{"Composition"}, new String[]{jenis}, uuidTask, idPractitioner,
                nmDokter, idEncounter, idOrganization, namaRS, waktuIso);
    }

    /**
     * Mengambil ID resource dari Bundle response.
     *
     * response.location dikembalikan sebagai URL ABSOLUT, mis.
     * "https://api-satusehat-stg.../fhir-r4/v1/Provenance/{id}/_history/{versi}",
     * jadi dicari lewat penggal "/{resourceType}/" alih-alih diperiksa awalannya.
     *
     * @return ID, atau string kosong bila tidak ditemukan.
     */
    /**
     * Task baru yang memakai ulang Provenance yang SUDAH ada — untuk dokumen
     * yang ditolak penandatangan.
     *
     * Panduan Teknis TTE v.1.2 (slide 11): <i>"jika status = rejected, maka Ditolak
     * &amp; buat Task Baru. Provenance bisa di reuse"</i>. Jadi yang dibuat ulang cukup
     * Task-nya; Provenance placeholder tidak perlu diterbitkan lagi, dan
     * <code>Task.input</code> menunjuk <code>Provenance/{id}</code> yang sudah ada
     * (bukan <code>urn:uuid:</code> seperti pada Bundle transaction).
     *
     * Dikirim sebagai POST /Task tunggal, bukan Bundle.
     */
    public static String buildTaskProvenanceLama(String[] idProvenance, String idPractitioner,
                                                 String nmDokter, String idEncounter,
                                                 String idOrganization, String namaRS, String waktuIso) {
        return buildTaskProvenanceLama(idProvenance, idPractitioner, nmDokter, idEncounter,
                idOrganization, namaRS, waktuIso, false);
    }

    /**
     * Varian bulk lintas kunjungan (collection "00. TTE versi 22072026", request
     * <i>Task - Multiple Provenance + Multiple Encounter</i>).
     *
     * Bedanya dari varian satu kunjungan hanya dua, tapi keduanya wajib:
     * <ul>
     *   <li>{@code code} = {@code electronic-sign-doc-out} ("Penandatanganan dokumen
     *       elektronik Bulk"), bukan {@code electronic-sign-doc}</li>
     *   <li>{@code Task.encounter} TIDAK dikirim — Task-nya memang menaungi banyak
     *       Encounter sekaligus, jadi tidak ada satu encounter yang mewakili</li>
     * </ul>
     *
     * <b>BELUM DIDUKUNG SERVER (diuji staging 22-07-2026).</b> POST Task dengan
     * varian ini ditolak 400 dengan dua alasan sekaligus:
     * <pre>
     *   Code not found: 'electronic-sign-doc-out' in system:
     *       http://terminology.kemkes.go.id   (RuleNumber: 10871)
     *   Reference is mandatory : Task.encounter (RuleNumber: 10875)
     * </pre>
     * Jadi collection 22072026 mendokumentasikan sesuatu yang terminologi dan
     * profil validasi staging belum menerimanya. Jangan panggil dengan
     * lintasKunjungan=true sampai Kemenkes mengaktifkannya; pakai varian satu
     * kunjungan (kode electronic-sign-doc + Task.encounter) yang terbukti 201.
     *
     * Catatan terpisah: satu Task menaungi Provenance dari <i>dokter berbeda</i>
     * SUDAH diterima server (teruji 201) selama memakai varian satu kunjungan.
     *
     * @param lintasKunjungan true bila Provenance berasal dari lebih dari satu no_rawat
     */
    public static String buildTaskProvenanceLama(String[] idProvenance, String idPractitioner,
                                                 String nmDokter, String idEncounter,
                                                 String idOrganization, String namaRS, String waktuIso,
                                                 boolean lintasKunjungan) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"Task\",\"status\":\"requested\",\"intent\":\"order\",\"priority\":\"routine\",");
        sb.append("\"code\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id\",");
        if (lintasKunjungan) {
            sb.append("\"code\":\"electronic-sign-doc-out\",\"display\":\"Penandatanganan dokumen elektronik Bulk\"}],");
        } else {
            sb.append("\"code\":\"electronic-sign-doc\",\"display\":\"Penandatanganan dokumen elektronik\"}],");
        }
        sb.append("\"text\":\"Penandatanganan elektronik dokumen\"},");
        sb.append("\"authoredOn\":\"").append(waktuIso).append("\",");
        sb.append("\"lastModified\":\"").append(waktuIso).append("\",");
        if (!lintasKunjungan) {
            sb.append("\"encounter\":{\"reference\":\"Encounter/").append(idEncounter).append("\"},");
        }
        sb.append("\"requester\":{\"reference\":\"Practitioner/").append(idPractitioner).append("\",")
          .append("\"display\":\"").append(jsonEscape(nmDokter)).append("\"},");
        sb.append("\"owner\":{\"reference\":\"Organization/").append(idOrganization).append("\",")
          .append("\"display\":\"").append(jsonEscape(namaRS)).append("\"},");
        sb.append("\"input\":[");
        for (int i = 0; i < idProvenance.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"type\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id\",")
              .append("\"code\":\"document-provenance\",\"display\":\"Document Provenance\"}],")
              .append("\"text\":\"Dokumen yang akan ditandatangani\"},");
            sb.append("\"valueReference\":{\"reference\":\"Provenance/").append(idProvenance[i]).append("\"}}");
        }
        sb.append("]}");
        return sb.toString();
    }

    /**
     * Baca <code>Task.output[]</code> jadi peta idProvenance -&gt; status lokal
     * ("Signed" / "Ditolak").
     *
     * Slide 11 v.1.2 menuliskan output sebagai <i>"signed | rejected Provenance
     * refs"</i> tanpa menjelaskan penandanya. Karena itu di sini penanda dicari
     * longgar: dari <code>type.coding[].code</code>, <code>type.text</code>, atau
     * teks apa pun di entry yang mengandung "reject"/"tolak" (=Ditolak) atau
     * "sign"/"complete" (=Signed). Provenance yang tidak bisa disimpulkan TIDAK
     * dimasukkan ke peta — pemanggil memakai status Task sebagai cadangan.
     *
     * Ini penting untuk bulk: satu Task memuat banyak dokumen dan hasilnya bisa
     * berbeda-beda, jadi menyamaratakan status Task ke semua dokumen bisa keliru.
     */
    public static java.util.Map<String, String> statusPerProvenance(JsonNode task) {
        java.util.Map<String, String> peta = new java.util.LinkedHashMap<>();
        if (task == null) return peta;
        JsonNode out = task.path("output");
        if (!out.isArray()) return peta;
        for (JsonNode o : out) {
            String ref = o.path("valueReference").path("reference").asText();
            if (ref == null || !ref.startsWith("Provenance/")) continue;
            String idProv = ref.substring("Provenance/".length());
            // Utamakan kode baku output.type.coding[].code (JSON PUT Task nyata 22-07-2026):
            // "signed" -> Signed, "failed" -> Gagal, "rejected" -> Ditolak.
            String code = "";
            JsonNode coding = o.path("type").path("coding");
            if (coding.isArray() && coding.size() > 0) code = coding.get(0).path("code").asText().toLowerCase();
            String status = null;
            switch (code) {
                case "signed":   status = "Signed";  break;
                case "failed":   status = "Gagal";   break;
                case "rejected": status = "Ditolak"; break;
                default: {
                    // Cadangan longgar untuk versi lama tanpa kode baku.
                    String petunjuk = (o.path("type").toString() + " " + o.path("valueReference").path("display").asText())
                            .toLowerCase();
                    if (petunjuk.contains("reject") || petunjuk.contains("tolak")) status = "Ditolak";
                    else if (petunjuk.contains("fail") || petunjuk.contains("gagal")) status = "Gagal";
                    else if (petunjuk.contains("sign") || petunjuk.contains("complete")) status = "Signed";
                }
            }
            if (status != null && !idProv.isEmpty()) peta.put(idProv, status);
        }
        return peta;
    }

    public static String idDariLokasi(JsonNode entry, String resourceType) {
        if (entry == null || !entry.isArray()) return "";
        String penanda = "/" + resourceType + "/";
        for (JsonNode e : entry) {
            String loc = e.path("response").path("location").asText();
            if (loc == null || loc.isEmpty()) continue;
            int p = loc.indexOf(penanda);
            if (p < 0) continue;
            String sisa = loc.substring(p + penanda.length());
            int q = sisa.indexOf('/');
            return q < 0 ? sisa : sisa.substring(0, q);
        }
        return "";
    }

    /**
     * Mengambil SELURUH ID Provenance dari Bundle response, dalam urutan entry.
     *
     * FHIR transaction menjamin entry response berurut sama dengan entry request,
     * jadi hasilnya sejajar dengan array uuidProv yang dikirim buildBundleMulti --
     * indeks ke-i di sini = dokumen ke-i. Dipakai untuk memetakan tiap Provenance
     * kembali ke barisnya saat satu Task memuat banyak Provenance.
     */
    public static java.util.List<String> idProvenanceUrut(JsonNode entry) {
        java.util.List<String> hasil = new java.util.ArrayList<>();
        if (entry == null || !entry.isArray()) return hasil;
        String penanda = "/Provenance/";
        for (JsonNode e : entry) {
            String loc = e.path("response").path("location").asText();
            if (loc == null || loc.isEmpty()) continue;
            int p = loc.indexOf(penanda);
            if (p < 0) continue;
            String sisa = loc.substring(p + penanda.length());
            int q = sisa.indexOf('/');
            hasil.add(q < 0 ? sisa : sisa.substring(0, q));
        }
        return hasil;
    }

    /**
     * Menentukan apakah Provenance sudah ditandatangani.
     *
     * Platform mengembalikan elemen signature tidak ada sama sekali (bukan array
     * kosong) selama belum ditandatangani, jadi keduanya harus diperlakukan sama.
     */
    public static boolean sudahDitandatangani(JsonNode provenance) {
        if (provenance == null) return false;
        JsonNode sig = provenance.path("signature");
        if (sig.isArray() && sig.size() > 0) return true;
        // Setelah TTE, Provenance.activity berubah dari CREATE menjadi LA (Legally
        // Authenticated) — lihat JSON Provenance signed nyata 22-07-2026. Jadikan
        // sinyal cadangan bila signature belum ikut terbaca.
        for (JsonNode c : provenance.path("activity").path("coding")) {
            if ("LA".equals(c.path("code").asText())) return true;
        }
        return false;
    }

    /**
     * Decode {@code signature.data} (base64 dari sebuah JWT/JOSE compact) menjadi
     * {@code [header, payload]} yang sudah di-decode jadi JSON teks.
     *
     * {@code signature.data} pada Provenance signed berisi base64 dari string JWT
     * {@code header.payload.signature} (tiap bagian base64url tanpa padding). Ini
     * dipakai panel Verifikasi untuk menampilkan alg (header) dan klaim seperti
     * {@code resourceHash}/{@code iat}/{@code kid} (payload). Kegagalan apa pun
     * mengembalikan dua string kosong — verifikasi tetap menampilkan sisanya.
     */
    public static String[] decodeJose(String dataB64) {
        try {
            if (dataB64 == null || dataB64.trim().isEmpty()) return new String[]{"", ""};
            byte[] jwtBytes = java.util.Base64.getDecoder().decode(dataB64.trim());
            String jwt = new String(jwtBytes, java.nio.charset.StandardCharsets.UTF_8);
            String[] bagian = jwt.split("\\.");
            if (bagian.length < 2) return new String[]{"", ""};
            return new String[]{ decodeB64Url(bagian[0]), decodeB64Url(bagian[1]) };
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    /** base64url tanpa padding -> teks UTF-8. */
    private static String decodeB64Url(String s) {
        String t = s.replace('-', '+').replace('_', '/');
        while (t.length() % 4 != 0) t += "=";
        return new String(java.util.Base64.getDecoder().decode(t), java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Isi teks QR yang dipindai SATUSEHAT Mobile.
     *
     * Panduan Teknis TTE v.1.2 (Flow 2a) menetapkan QR memuat <b>URL applink</b>:
     * <pre>
     *   Staging  : https://applink-stg.dto.kemkes.go.id/ssm/tte/{taskuuid}/{timestamp}
     *   Produksi : https://applink.kemkes.go.id/ssm/tte/{taskuuid}/{timestamp}
     * </pre>
     * Basis URL diambil dari setting <code>URLAPPLINKTTE</code> di database.xml supaya
     * pindah staging&lt;-&gt;produksi tidak perlu build ulang.
     *
     * Task UUID tetap; hanya segmen timestamp (epoch detik) yang berganti tiap
     * refresh — "Task tetap sama, hanya token QR yang berubah".
     *
     * Bila setting belum diisi, dipakai bentuk lama "Task/{id}/{epoch}" (hasil
     * pindai QR nyata sebelum v1.2) supaya instalasi lama tidak langsung rusak.
     *
     * @param epochDetik waktu saat QR dibuat, dipisah sebagai parameter agar bisa diuji
     */
    public static String isiQr(String taskId, long epochDetik) {
        String basis;
        try { basis = fungsi.koneksiDB.URLAPPLINKTTE(); } catch (Exception e) { basis = ""; }
        return isiQr(basis, taskId, epochDetik);
    }

    /** Varian yang basis URL-nya disuntikkan — dipakai pengujian. */
    public static String isiQr(String basisUrl, String taskId, long epochDetik) {
        String id = taskId == null ? "" : taskId.trim();
        String b = basisUrl == null ? "" : basisUrl.trim();
        if (b.isEmpty()) return "Task/" + id + "/" + epochDetik;
        while (b.endsWith("/")) b = b.substring(0, b.length() - 1);
        return b + "/" + id + "/" + epochDetik;
    }

    /**
     * Logo yang ditempel di tengah QR. Bila berkas tak ada, QR tetap dibuat polos.
     *
     * Dipakai berkas 240px, bukan {@code /48x48/kemenkes.png} yang sudah ada:
     * logo digambar sekitar 54px, jadi versi 48px harus diperbesar dan tepinya
     * pecah. Latar papan catur pada berkas asal (WebP tanpa kanal alpha) sudah
     * dibuang menjadi transparan -- kalau tidak, tengah QR jadi tambalan abu-abu.
     */
    private static final String BERKAS_LOGO = "/picture/kemenkes.png";

    /**
     * Lebar logo terhadap lebar QR. 18% -- sengaja konservatif.
     *
     * Level H memulihkan 30% modul, tapi angka itu jangan dihabiskan: QR ini
     * gerbang penandatanganan TTE, dan kamera HP di ruangan remang dengan layar
     * memantul sudah memakan sebagian daya pulih itu sebelum logo diperhitungkan.
     * Pada 18% (+bantalan putih) area tertutup sekitar 5% modul, jadi masih jauh
     * dari ambang.
     */
    private static final float RASIO_LOGO = 0.18f;

    /**
     * QR berisi {@code konten} dengan logo Kemenkes di tengahnya.
     *
     * Error correction dinaikkan ke level H (pulih 30%) -- wajib, karena logo
     * menutupi modul di tengah. Dengan level M seperti sebelumnya, penempelan
     * logo bisa membuat QR gagal dipindai.
     *
     * Logo digambar di atas bantalan putih membulat supaya tepinya tidak menyatu
     * dengan modul hitam di sekelilingnya -- tanpa itu, pemindai kesulitan
     * menentukan batas modul di sekitar logo.
     *
     * Bila berkas logo tidak ditemukan, QR tetap dikembalikan tanpa logo:
     * kegagalan memuat gambar hiasan tidak boleh menggagalkan penandatanganan.
     */
    public static java.awt.image.BufferedImage gambarQr(String konten, int ukuran) throws Exception {
        com.google.zxing.qrcode.QRCodeWriter writer = new com.google.zxing.qrcode.QRCodeWriter();
        java.util.Map<com.google.zxing.EncodeHintType, Object> hint = new java.util.HashMap<>();
        hint.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION,
                com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);
        hint.put(com.google.zxing.EncodeHintType.MARGIN, 1);
        hint.put(com.google.zxing.EncodeHintType.CHARACTER_SET, "UTF-8");

        com.google.zxing.common.BitMatrix m = writer.encode(
                konten, com.google.zxing.BarcodeFormat.QR_CODE, ukuran, ukuran, hint);
        java.awt.image.BufferedImage img =
                com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(m);

        java.awt.image.BufferedImage logo = null;
        try {
            java.net.URL url = SatuSehatTTEPayload.class.getResource(BERKAS_LOGO);
            if (url != null) logo = javax.imageio.ImageIO.read(url);
        } catch (Exception e) {
            logo = null;    // QR polos lebih baik daripada tidak ada QR sama sekali
        }
        if (logo == null) return img;

        java.awt.image.BufferedImage keluar =
                new java.awt.image.BufferedImage(ukuran, ukuran, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = keluar.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        int sisi = Math.round(ukuran * RASIO_LOGO);
        int x = (ukuran - sisi) / 2;
        int bantalan = Math.max(3, sisi / 8);
        g.setColor(java.awt.Color.WHITE);
        g.fillRoundRect(x - bantalan, x - bantalan, sisi + 2 * bantalan, sisi + 2 * bantalan,
                bantalan * 2, bantalan * 2);
        g.drawImage(logo, x, x, sisi, sisi, null);
        g.dispose();
        return keluar;
    }
}
