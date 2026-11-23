/*
  Alkes SatuSehat — definisi cakupan + pengirim keluarga Device/Supply untuk 1 kunjungan.

  Bagian 1 (filter): definisi tunggal "barang apa yang dihitung sebagai Alkes".
  Sebelumnya tiap query menyaring dengan `jenis.nama like '%alkes%'`. Itu rapuh:
  dari 22 kdjns di master `jenis`, 16 di antaranya bernama '-', dan jenis J024
  "Bahan Medis Habis Pakai" — 654 barang aktif, ~19 ribu baris pemakaian per 3
  bulan di produksi — tidak mengandung kata "alkes" sehingga TIDAK PERNAH ikut
  terkirim, padahal isinya IV catheter, foley catheter, infus set, kasa, dsb.

  Bagian 2 (pengirim): keenam form sender Device-family hanya bisa mengirim baris
  yang dicentang di grid-nya, jadi tombol Alkes di "Kirim Bundel" tidak punya
  jalan masuk. Di sini logikanya disediakan headless per-no_rawat, sepola dengan
  SatuSehatBundelHelper. Payload-nya mengikuti bab "# Alkes" dokumen Update RME
  IGD Des 2025 dan bentuk yang terbukti diterima server.

  Urutan kirim mengikuti dependensi referensi:
      Device -> DeviceRequest -> DeviceDispense (extension pada DeviceRequest)
             -> DeviceUseStatement
             -> SupplyRequest -> SupplyDelivery

  Bagian 3 (update): PUT ulang resource yang SUDAH punya id dengan data terbaru —
  dipakai bila nama barang, pemetaan KFA, atau jam pemakaian diperbaiki. Tanpa ini
  satu-satunya cara memperbarui adalah menghapus baris tracking lalu kirim ulang.
  By Mas Elkhanza
 */
package bridging;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class SatuSehatAlkes {

    private SatuSehatAlkes() {}

    /** Kode jenis barang yang diperlakukan sebagai alkes. */
    public static final String KDJNS = "'J022','J024','J026'";

    /**
     * Fragmen SQL penyaring alkes, mis. {@code filter("jenis")} menghasilkan
     * {@code jenis.kdjns in ('J022','J024','J026')}.
     *
     * @param aliasJenis alias tabel {@code jenis} pada query pemanggil
     */
    public static String filter(String aliasJenis) {
        return aliasJenis + ".kdjns in (" + KDJNS + ")";
    }

    // ------------------------------------------------------------------ util

    private static String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }

    /** Satu alkes yang dipakai pasien pada satu kunjungan, beserta status kirimnya. */
    private static final class Baris {
        String kodeBrng, nama, idDevice, tglWib, kfaCode, kfaDisplay;
        String idDr = "", idDd = "", idDus = "", idSr = "", idSd = "";
    }

    /** Data kunjungan + daftar alkes-nya; dipakai bersama oleh kirim dan update. */
    private static final class Konteks {
        Connection kon; ApiSatuSehat api;
        String link = "", org = "", idP = "", nmPasien = "", idPrak = "", nmDokter = "", idEnc = "";
        java.util.List<Baris> daftar = new java.util.ArrayList<>();
    }

    private static void simpan(Connection kon, String tabel, String kolom, String noRawat, String kodeBrng, String id) {
        try (PreparedStatement p = kon.prepareStatement(
                "replace into " + tabel + " (no_rawat,kode_brng," + kolom + ",tgl_kirim) values (?,?,?,now())")) {
            p.setString(1, noRawat); p.setString(2, kodeBrng); p.setString(3, id);
            p.executeUpdate();
        } catch (Exception e) { System.out.println("ALKES simpan " + tabel + " : " + e); }
    }

    /** Muat konteks kunjungan + alkes yang dipakai. null bila prasyarat tidak terpenuhi. */
    private static Konteks muat(String noRawat, StringBuilder log) {
        Konteks k = new Konteks();
        k.kon = koneksiDB.condb();
        k.api = new ApiSatuSehat();
        SatuSehatCekNIK cek = new SatuSehatCekNIK();
        try { k.link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { k.link = ""; }
        try { k.org = koneksiDB.IDSATUSEHAT(); } catch (Exception e) { k.org = ""; }

        String nik = "", ktpDok = "";
        try (PreparedStatement p = k.kon.prepareStatement(
                "select ifnull(p.no_ktp,'') nik, p.nm_pasien, ifnull(pg.no_ktp,'') ktpdok, ifnull(dr.nm_dokter,'') nmdok, "
              + "ifnull(se.id_encounter,'') id_encounter from reg_periksa rp "
              + "join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
              + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
              + "left join pegawai pg on pg.nik=rp.kd_dokter "
              + "left join satu_sehat_encounter se on se.no_rawat=rp.no_rawat where rp.no_rawat=?")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) { nik = r.getString("nik"); k.nmPasien = r.getString("nm_pasien");
                    ktpDok = r.getString("ktpdok"); k.nmDokter = r.getString("nmdok"); k.idEnc = r.getString("id_encounter"); }
            }
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>✗ Data kunjungan: ").append(e).append("</font><br>"); return null;
        }
        if (k.idEnc.isEmpty()) {
            log.append("&nbsp;&nbsp;<font color='red'>✗ Encounter belum dikirim — kirim Encounter dulu.</font><br>"); return null;
        }
        k.idP = cek.tampilIDPasien(nik);
        if (k.idP == null || k.idP.isEmpty()) {
            log.append("&nbsp;&nbsp;<font color='red'>✗ IHS Patient tidak ditemukan (NIK ").append(esc(nik)).append(").</font><br>"); return null;
        }
        k.idPrak = ktpDok.isEmpty() ? "" : cek.tampilIDParktisi(ktpDok);
        if (k.idPrak == null) k.idPrak = "";

        // Satu baris per kode_brng, memakai waktu pemakaian paling awal.
        try (PreparedStatement p = k.kon.prepareStatement(
                "select dpo.kode_brng, ifnull(db.nama_brng,'') nama, "
              + "min(concat(dpo.tgl_perawatan,' ',dpo.jam)) tgl, "
              + "ifnull(ssd.id_device,'') id_device, ifnull(m.obat_code,'') kfa_code, ifnull(m.obat_display,'') kfa_display, "
              + "ifnull(dr.id_devicerequest,'') id_dr, ifnull(dd.id_devicedispense,'') id_dd, "
              + "ifnull(du.id_deviceusestatement,'') id_dus, ifnull(sr.id_supplyrequest,'') id_sr, "
              + "ifnull(sd.id_supplydelivery,'') id_sd "
              + "from detail_pemberian_obat dpo "
              + "join databarang db on db.kode_brng=dpo.kode_brng "
              + "join jenis j on j.kdjns=db.kdjns and " + filter("j") + " "
              + "left join satu_sehat_device ssd on ssd.kode_brng=dpo.kode_brng "
              + "left join satu_sehat_mapping_obat m on m.kode_brng=dpo.kode_brng "
              + "left join satu_sehat_devicerequest dr on dr.no_rawat=dpo.no_rawat and dr.kode_brng=dpo.kode_brng "
              + "left join satu_sehat_devicedispense dd on dd.no_rawat=dpo.no_rawat and dd.kode_brng=dpo.kode_brng "
              + "left join satu_sehat_deviceusestatement du on du.no_rawat=dpo.no_rawat and du.kode_brng=dpo.kode_brng "
              + "left join satu_sehat_supplyrequest sr on sr.no_rawat=dpo.no_rawat and sr.kode_brng=dpo.kode_brng "
              + "left join satu_sehat_supplydelivery sd on sd.no_rawat=dpo.no_rawat and sd.kode_brng=dpo.kode_brng "
              + "where dpo.no_rawat=? "
              + "group by dpo.kode_brng, db.nama_brng, ssd.id_device, m.obat_code, m.obat_display, "
              + "dr.id_devicerequest, dd.id_devicedispense, du.id_deviceusestatement, sr.id_supplyrequest, sd.id_supplydelivery "
              + "order by tgl")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Baris b = new Baris();
                    b.kodeBrng = r.getString("kode_brng"); b.nama = r.getString("nama");
                    b.idDevice = r.getString("id_device"); b.tglWib = r.getString("tgl");
                    b.kfaCode = r.getString("kfa_code"); b.kfaDisplay = r.getString("kfa_display");
                    b.idDr = r.getString("id_dr"); b.idDd = r.getString("id_dd"); b.idDus = r.getString("id_dus");
                    b.idSr = r.getString("id_sr"); b.idSd = r.getString("id_sd");
                    k.daftar.add(b);
                }
            }
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>✗ Baca alkes: ").append(e).append("</font><br>"); return null;
        }
        if (k.daftar.isEmpty()) {
            log.append("&nbsp;&nbsp;<font color='orange'>• Tidak ada alkes pada kunjungan ini.</font><br>"); return null;
        }
        return k;
    }

    // -------------------------------------------------------------- payload
    // Parameter idResource: kosong utk POST, diisi utk PUT (FHIR mewajibkan
    // Resource.id pada body PUT sama dengan id di URL).

    /** Device master (per kode_brng, bukan per kunjungan). KFA ada di type.coding. */
    private static String jsonDevice(String org, Baris b, String idResource) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"Device\",");
        if (!idResource.isEmpty()) sb.append("\"id\":\"").append(esc(idResource)).append("\",");
        sb.append("\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/device/").append(org)
          .append("\",\"use\":\"official\",\"value\":\"").append(esc(b.kodeBrng)).append("\"}],");
        sb.append("\"status\":\"active\",\"type\":{");
        if (!b.kfaCode.isEmpty()) {
            sb.append("\"coding\":[{\"system\":\"http://sys-ids.kemkes.go.id/kfa\",\"code\":\"").append(esc(b.kfaCode))
              .append("\",\"display\":\"").append(esc(b.kfaDisplay)).append("\"}],");
        }
        sb.append("\"text\":\"").append(esc(b.nama)).append("\"},");
        sb.append("\"deviceName\":[{\"name\":\"").append(esc(b.nama)).append("\",\"type\":\"user-friendly-name\"}],");
        sb.append("\"owner\":{\"reference\":\"Organization/").append(org).append("\"}}");
        return sb.toString();
    }

    /**
     * DeviceRequest, opsional membawa DeviceDispense sebagai extension.
     *
     * FHIR R4 tidak punya resource DeviceDispense (baru ada di R5). POST
     * /DeviceDispense di SatuSehat membalas 201 tapi hanya meng-echo payload —
     * tanpa id, dan pencariannya balas body kosong, jadi tidak pernah tersimpan.
     * Yang tersimpan adalah extension pada DeviceRequest.
     */
    private static String jsonDeviceRequest(Konteks k, String noRawat, Baris b, String dtUtc,
                                            String idResource, boolean denganDispense) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"DeviceRequest\",");
        if (!idResource.isEmpty()) sb.append("\"id\":\"").append(esc(idResource)).append("\",");
        sb.append("\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/devicerequest/").append(k.org)
          .append("\",\"use\":\"official\",\"value\":\"DR-").append(esc(noRawat)).append("-").append(esc(b.kodeBrng)).append("\"}],");
        sb.append("\"status\":\"active\",\"intent\":\"order\",");
        sb.append("\"codeReference\":{\"reference\":\"Device/").append(b.idDevice).append("\",\"display\":\"").append(esc(b.nama)).append("\"},");
        sb.append("\"subject\":{\"reference\":\"Patient/").append(k.idP).append("\",\"display\":\"").append(esc(k.nmPasien)).append("\"},");
        sb.append("\"encounter\":{\"reference\":\"Encounter/").append(k.idEnc).append("\"},");
        sb.append("\"authoredOn\":\"").append(dtUtc).append("\"");
        if (!k.idPrak.isEmpty()) {
            sb.append(",\"requester\":{\"reference\":\"Practitioner/").append(k.idPrak).append("\",\"display\":\"").append(esc(k.nmDokter)).append("\"}");
        }
        if (denganDispense) {
            sb.append(",\"extension\":[{\"url\":\"https://fhir.kemkes.go.id/r4/StructureDefinition/DeviceDispense\",\"extension\":[");
            sb.append("{\"url\":\"identifier\",\"valueIdentifier\":{\"system\":\"http://sys-ids.kemkes.go.id/devicedispense/").append(k.org)
              .append("\",\"use\":\"official\",\"value\":\"DD-").append(esc(noRawat)).append("-").append(esc(b.kodeBrng)).append("\"}},");
            sb.append("{\"url\":\"status\",\"valueCode\":\"completed\"},");
            sb.append("{\"url\":\"device\",\"valueReference\":{\"reference\":\"Device/").append(b.idDevice).append("\",\"display\":\"").append(esc(b.nama)).append("\"}},");
            sb.append("{\"url\":\"basedOn\",\"valueReference\":{\"reference\":\"DeviceRequest/").append(esc(idResource)).append("\"}},");
            sb.append("{\"url\":\"subject\",\"valueReference\":{\"reference\":\"Patient/").append(k.idP).append("\",\"display\":\"").append(esc(k.nmPasien)).append("\"}},");
            sb.append("{\"url\":\"receiver\",\"valueReference\":{\"reference\":\"Patient/").append(k.idP).append("\",\"display\":\"").append(esc(k.nmPasien)).append("\"}}");
            sb.append("]}]");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String jsonDeviceUseStatement(Konteks k, String noRawat, Baris b, String dtUtc, String idResource) {
        return "{\"resourceType\":\"DeviceUseStatement\","
             + (idResource.isEmpty() ? "" : "\"id\":\"" + esc(idResource) + "\",")
             + "\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/deviceusestatement/" + k.org
             + "\",\"use\":\"official\",\"value\":\"DUS-" + esc(noRawat) + "-" + esc(b.kodeBrng) + "\"}],"
             + "\"status\":\"active\","
             + "\"subject\":{\"reference\":\"Patient/" + k.idP + "\",\"display\":\"" + esc(k.nmPasien) + "\"},"
             + "\"device\":{\"reference\":\"Device/" + b.idDevice + "\",\"display\":\"" + esc(b.nama) + "\"},"
             + "\"timingDateTime\":\"" + dtUtc + "\",\"recordedOn\":\"" + dtUtc + "\"}";
    }

    private static String jsonSupplyRequest(Konteks k, String noRawat, Baris b, String dtUtc, String idResource) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"SupplyRequest\",");
        if (!idResource.isEmpty()) sb.append("\"id\":\"").append(esc(idResource)).append("\",");
        sb.append("\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/supplyrequest/").append(k.org)
          .append("\",\"use\":\"official\",\"value\":\"SR-").append(esc(noRawat)).append("-").append(esc(b.kodeBrng)).append("\"}],");
        sb.append("\"status\":\"active\",");
        sb.append("\"category\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/supply-kind\",\"code\":\"central\"}]},");
        sb.append("\"itemReference\":{\"reference\":\"Device/").append(b.idDevice).append("\",\"display\":\"").append(esc(b.nama)).append("\"},");
        sb.append("\"quantity\":{\"value\":1},");
        sb.append("\"occurrenceDateTime\":\"").append(dtUtc).append("\"");
        if (!k.idPrak.isEmpty()) {
            sb.append(",\"requester\":{\"reference\":\"Practitioner/").append(k.idPrak).append("\",\"display\":\"").append(esc(k.nmDokter)).append("\"}");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String jsonSupplyDelivery(Konteks k, String noRawat, Baris b, String dtUtc, String idResource) {
        return "{\"resourceType\":\"SupplyDelivery\","
             + (idResource.isEmpty() ? "" : "\"id\":\"" + esc(idResource) + "\",")
             + "\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/supplydelivery/" + k.org
             + "\",\"use\":\"official\",\"value\":\"SD-" + esc(noRawat) + "-" + esc(b.kodeBrng) + "\"}],"
             + "\"basedOn\":[{\"reference\":\"SupplyRequest/" + b.idSr + "\"}],"
             + "\"status\":\"completed\","
             + "\"patient\":{\"reference\":\"Patient/" + k.idP + "\",\"display\":\"" + esc(k.nmPasien) + "\"},"
             + "\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/supply-item-type\",\"code\":\"device\"}]},"
             + "\"suppliedItem\":{\"quantity\":{\"value\":1},\"itemReference\":{\"reference\":\"Device/" + b.idDevice
             + "\",\"display\":\"" + esc(b.nama) + "\"}},"
             + "\"occurrenceDateTime\":\"" + dtUtc + "\"}";
    }

    // -------------------------------------------------------------- pengirim

    /**
     * Kirim seluruh keluarga Alkes untuk satu kunjungan. Idempoten: baris yang
     * id-nya sudah ada di tabel tracking dilewati, jadi aman ditekan berulang.
     *
     * @return jumlah resource yang benar-benar terkirim pada pemanggilan ini
     */
    public static int kirimSatuNoRawat(String noRawat, StringBuilder log) {
        Konteks k = muat(noRawat, log);
        if (k == null) return 0;

        int n = 0;
        for (Baris b : k.daftar) {
            String dt = fungsi.IsoTime.toUtc(b.tglWib);
            int nAwal = n;
            log.append("<b>&bull; ").append(esc(b.nama)).append("</b><br>");

            // 1. Device (master per barang) — prasyarat semua resource lain
            if (b.idDevice.isEmpty()) {
                if (b.kfaCode.isEmpty()) {
                    log.append("&nbsp;&nbsp;<font color='#c00'>✗ Belum ada pemetaan KFA — lewati.</font><br>");
                    continue;
                }
                String id = SatuSehatBundelHelper.post(k.api, k.link, "Device", jsonDevice(k.org, b, ""), log);
                if (id.isEmpty()) { log.append("&nbsp;&nbsp;<font color='red'>✗ Device gagal — lewati.</font><br>"); continue; }
                try (PreparedStatement w = k.kon.prepareStatement(
                        "replace into satu_sehat_device (kode_brng,id_device,tgl_kirim) values (?,?,now())")) {
                    w.setString(1, b.kodeBrng); w.setString(2, id); w.executeUpdate();
                } catch (Exception e) { System.out.println("ALKES simpan device : " + e); }
                b.idDevice = id; n++;
                log.append("&nbsp;&nbsp;<font color='green'>✓ Device</font><br>");
            }

            // 2. DeviceRequest
            if (b.idDr.isEmpty()) {
                String id = SatuSehatBundelHelper.post(k.api, k.link, "DeviceRequest",
                        jsonDeviceRequest(k, noRawat, b, dt, "", false), log);
                if (!id.isEmpty()) {
                    simpan(k.kon, "satu_sehat_devicerequest", "id_devicerequest", noRawat, b.kodeBrng, id);
                    b.idDr = id; n++;
                    log.append("&nbsp;&nbsp;<font color='green'>✓ DeviceRequest</font><br>");
                } else {
                    log.append("&nbsp;&nbsp;<font color='red'>✗ DeviceRequest gagal — sisanya dilewati.</font><br>");
                    continue;
                }
            }

            // 3. DeviceDispense — PUT ulang DeviceRequest dengan extension
            if (b.idDd.isEmpty()) {
                boolean ok = SatuSehatBundelHelper.put(k.api, k.link, "DeviceRequest", b.idDr,
                        jsonDeviceRequest(k, noRawat, b, dt, b.idDr, true), log);
                if (ok) {
                    simpan(k.kon, "satu_sehat_devicedispense", "id_devicedispense", noRawat, b.kodeBrng, b.idDr);
                    b.idDd = b.idDr; n++;
                    log.append("&nbsp;&nbsp;<font color='green'>✓ DeviceDispense (extension pada DeviceRequest)</font><br>");
                }
            }

            // 4. DeviceUseStatement
            if (b.idDus.isEmpty()) {
                String id = SatuSehatBundelHelper.post(k.api, k.link, "DeviceUseStatement",
                        jsonDeviceUseStatement(k, noRawat, b, dt, ""), log);
                if (!id.isEmpty()) {
                    simpan(k.kon, "satu_sehat_deviceusestatement", "id_deviceusestatement", noRawat, b.kodeBrng, id);
                    b.idDus = id; n++;
                    log.append("&nbsp;&nbsp;<font color='green'>✓ DeviceUseStatement</font><br>");
                }
            }

            // 5. SupplyRequest
            if (b.idSr.isEmpty()) {
                String id = SatuSehatBundelHelper.post(k.api, k.link, "SupplyRequest",
                        jsonSupplyRequest(k, noRawat, b, dt, ""), log);
                if (!id.isEmpty()) {
                    simpan(k.kon, "satu_sehat_supplyrequest", "id_supplyrequest", noRawat, b.kodeBrng, id);
                    b.idSr = id; n++;
                    log.append("&nbsp;&nbsp;<font color='green'>✓ SupplyRequest</font><br>");
                }
            }

            // 6. SupplyDelivery — butuh SupplyRequest sbg basedOn
            if (b.idSd.isEmpty() && !b.idSr.isEmpty()) {
                String id = SatuSehatBundelHelper.post(k.api, k.link, "SupplyDelivery",
                        jsonSupplyDelivery(k, noRawat, b, dt, ""), log);
                if (!id.isEmpty()) {
                    simpan(k.kon, "satu_sehat_supplydelivery", "id_supplydelivery", noRawat, b.kodeBrng, id);
                    b.idSd = id; n++;
                    log.append("&nbsp;&nbsp;<font color='green'>✓ SupplyDelivery</font><br>");
                }
            }

            // Tanpa baris ini, alkes yang sudah lengkap hanya menyisakan judulnya —
            // terbaca seperti gagal padahal justru sudah beres.
            if (n == nAwal) {
                log.append("&nbsp;&nbsp;<font color='#090'>✓ sudah lengkap, semua resource terkirim sebelumnya.</font><br>");
            }
        }
        if (n == 0) {
            log.append("<br><font color='#090'><b>Tidak ada yang perlu dikirim</b></font> — seluruh alkes kunjungan ini sudah lengkap.<br>");
        }
        return n;
    }

    /**
     * PUT ulang resource Alkes yang SUDAH terkirim dengan data terbaru — dipakai
     * setelah nama barang, pemetaan KFA, atau jam pemakaian diperbaiki.
     *
     * Hanya menyentuh yang sudah punya id; yang belum pernah dikirim dilewati
     * (pakai tombol Kirim untuk itu) supaya Update tidak diam-diam membuat
     * resource baru. DeviceDispense ikut terbarui lewat PUT DeviceRequest,
     * karena memang tinggal di extension-nya.
     *
     * @return jumlah resource yang berhasil diperbarui
     */
    public static int updateSatuNoRawat(String noRawat, StringBuilder log) {
        Konteks k = muat(noRawat, log);
        if (k == null) return 0;

        int n = 0, dilewati = 0;
        for (Baris b : k.daftar) {
            String dt = fungsi.IsoTime.toUtc(b.tglWib);
            int nAwal = n;
            log.append("<b>&bull; ").append(esc(b.nama)).append("</b><br>");

            if (b.idDevice.isEmpty()) {
                log.append("&nbsp;&nbsp;<font color='#c80'>• Belum pernah dikirim — pakai tombol Kirim.</font><br>");
                dilewati++;
                continue;
            }
            if (SatuSehatBundelHelper.put(k.api, k.link, "Device", b.idDevice, jsonDevice(k.org, b, b.idDevice), log)) {
                n++; log.append("&nbsp;&nbsp;<font color='green'>✓ Device diperbarui</font><br>");
            }
            if (!b.idDr.isEmpty()) {
                // Extension DeviceDispense ikut disertakan hanya bila memang sudah pernah
                // dikirim — kalau tidak, Update akan menciptakannya diam-diam.
                boolean adaDispense = !b.idDd.isEmpty();
                if (SatuSehatBundelHelper.put(k.api, k.link, "DeviceRequest", b.idDr,
                        jsonDeviceRequest(k, noRawat, b, dt, b.idDr, adaDispense), log)) {
                    n++;
                    log.append("&nbsp;&nbsp;<font color='green'>✓ DeviceRequest diperbarui")
                       .append(adaDispense ? " (termasuk DeviceDispense)" : "").append("</font><br>");
                }
            }
            if (!b.idDus.isEmpty() && SatuSehatBundelHelper.put(k.api, k.link, "DeviceUseStatement", b.idDus,
                    jsonDeviceUseStatement(k, noRawat, b, dt, b.idDus), log)) {
                n++; log.append("&nbsp;&nbsp;<font color='green'>✓ DeviceUseStatement diperbarui</font><br>");
            }
            if (!b.idSr.isEmpty() && SatuSehatBundelHelper.put(k.api, k.link, "SupplyRequest", b.idSr,
                    jsonSupplyRequest(k, noRawat, b, dt, b.idSr), log)) {
                n++; log.append("&nbsp;&nbsp;<font color='green'>✓ SupplyRequest diperbarui</font><br>");
            }
            if (!b.idSd.isEmpty() && !b.idSr.isEmpty() && SatuSehatBundelHelper.put(k.api, k.link, "SupplyDelivery", b.idSd,
                    jsonSupplyDelivery(k, noRawat, b, dt, b.idSd), log)) {
                n++; log.append("&nbsp;&nbsp;<font color='green'>✓ SupplyDelivery diperbarui</font><br>");
            }
            if (n == nAwal) log.append("&nbsp;&nbsp;<font color='red'>✗ Tidak ada yang berhasil diperbarui.</font><br>");
        }
        if (dilewati > 0) {
            log.append("<br><font color='#c80'>").append(dilewati)
               .append(" alkes dilewati karena belum pernah dikirim — tekan <b>Kirim Alkes</b> lebih dulu.</font><br>");
        }
        return n;
    }
}
