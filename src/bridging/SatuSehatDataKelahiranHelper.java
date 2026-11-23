/*
  Mesin bersama modul Data Kelahiran SATUSEHAT (tahap 3 & 4 playbook).

  Semua data ditarik OTOMATIS dari tabel native Khanza — tidak ada yang perlu
  diketik ulang petugas:

     pasien            nama bayi, jenis kelamin, tanggal lahir, golongan darah
     pasien_bayi       jam lahir, anak ke-, BB, PB, proses lahir, penolong, rhesus
     bayi_ibu_mapping  NIK ibu (bayi didaftarkan ke MPI memakai NIK IBU)
     reg_periksa       no_rawat bayi

  Dipakai dua pemanggil, keduanya lewat pintu yang sama supaya hasilnya identik:
     - DlgIKBBayi        : popup "Kirim Data Kelahiran Satu Sehat" untuk 1 bayi
                           yang sedang dibuka
     - SatuSehatKirimAktaKelahiran : daftar/batch + panel progres

  Tanda "sudah terkirim" adalah ADANYA ID, bukan flag terpisah:
     satu_sehat_bayi_baru_lahir.ihs_bayi          -> Patient bayi
     satu_sehat_episodeofcare                     -> EpisodeOfCare Neonate
     satu_sehat_akta_kelahiran.encounter_bayi     -> Encounter
     satu_sehat_akta_kelahiran_resource           -> tiap Observation
  Jadi memanggil kirim() dua kali aman: yang sudah punya ID dilewati, bukan
  dikirim ulang.
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class SatuSehatDataKelahiranHelper {

    /** Hasil pengiriman satu bayi, untuk ditampilkan ke petugas. */
    public static class Hasil {
        public boolean sukses = false;
        public String ihsBayi = "", idEoc = "", idEncounter = "";
        public int jumlahObservasi = 0;
        /** Baris-baris keterangan, satu per langkah — langsung bisa dijadikan pesan dialog. */
        public final List<String> langkah = new ArrayList<>();
        /** Alasan gagal bila sukses=false. */
        public String pesanGagal = "";

        public String ringkasan() {
            StringBuilder b = new StringBuilder();
            for (String s : langkah) b.append(s).append("\n");
            if (!pesanGagal.isEmpty()) b.append("\n").append(pesanGagal);
            return b.toString();
        }
    }

    // Kode terminologi tahap 4. Sumber: Playbook Modul Data Kelahiran v1.0 +
    // koleksi Postman "49. Use Case - Data Kelahiran".
    private static final String SYS_LOINC  = "http://loinc.org";
    private static final String SYS_SNOMED = "http://snomed.info/sct";
    private static final String SYS_KEMKES = "http://terminology.kemkes.go.id/CodeSystem/clinical-term";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final SatuSehatCekNIK cekNik = new SatuSehatCekNIK();
    private final Connection koneksi = koneksiDB.condb();
    private String link = "";

    private SatuSehatDataKelahiranHelper() {
        try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { System.out.println("Notif link : " + e); }
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /** Kirim dengan no.RM bayi saja — no_rawat dicari sendiri dari reg_periksa. */
    public static Hasil kirim(String noRkmMedisBayi) {
        return kirim(cariNoRawat(noRkmMedisBayi), noRkmMedisBayi);
    }

    /**
     * Kirim tahap 3 & 4 untuk satu bayi. Tidak pernah melempar exception —
     * kegagalan dilaporkan lewat {@link Hasil#sukses} dan {@link Hasil#pesanGagal}
     * supaya form pemanggil cukup menampilkan pesannya.
     */
    public static Hasil kirim(String noRawatBayi, String noRkmMedisBayi) {
        Hasil h = new Hasil();
        SatuSehatDataKelahiranHelper x = new SatuSehatDataKelahiranHelper();
        try {
            x.jalankan(noRawatBayi, noRkmMedisBayi, h);
        } catch (Exception e) {
            h.sukses = false;
            h.pesanGagal = "Gagal: " + pesanRingkas(e);
            System.out.println("Notif kirim data kelahiran : " + e);
        }
        return h;
    }

    /** Ringkasan status kirim untuk kolom tabel: "" kalau belum, IHS bayi kalau sudah. */
    public static String ihsBayiTerkirim(String noRawatBayi) {
        return cariIsi("select ifnull(ihs_bayi,'') from satu_sehat_bayi_baru_lahir where no_rawat=?", noRawatBayi);
    }

    // ============================================================
    // ISI DATA BAYI
    // ============================================================

    /** Data satu bayi, hasil gabungan pasien + pasien_bayi + bayi_ibu_mapping. */
    private static class Bayi {
        String noRawat = "", noRm = "", nama = "", jk = "", tglLahir = "", golDarah = "", rhesus = "";
        String jamLahir = "", anakKe = "", berat = "", panjang = "", prosesLahir = "";
        String nikPenolong = "", namaPenolong = "";
        String nikIbu = "", namaIbu = "";
        // Rawat inap ibu — sumber Encounter tahap 2. Kosong kalau ibunya tidak
        // pernah didaftarkan ranap, dan tahap 2 otomatis dilewati.
        String noRawatIbu = "", tglMasukIbu = "", jamMasukIbu = "", idLokasiIbu = "";
        /** Lokasi Encounter bayi. Kamar bayi sendiri kalau ada, kalau tidak ikut kamar ibu. */
        String idLokasiBayi = "";
    }

    private Bayi baca(String noRawat, String noRm) throws Exception {
        Bayi b = new Bayi();
        b.noRawat = noRawat == null ? "" : noRawat.trim();
        b.noRm = noRm == null ? "" : noRm.trim();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select p.nm_pasien, p.jk, p.tgl_lahir, p.gol_darah, p.nm_ibu, "
              + "  pb.jam_lahir, pb.anakke, pb.berat_badan, pb.panjang_badan, pb.proses_lahir, "
              + "  pb.rhesus, pb.penolong, ifnull(pg.nama,'') nama_penolong, ifnull(pg.no_ktp,'') ktp_penolong "
              + "from pasien p "
              + "inner join pasien_bayi pb on pb.no_rkm_medis = p.no_rkm_medis "
              + "left join pegawai pg on pg.nik = pb.penolong "
              + "where p.no_rkm_medis = ? limit 1")) {
            ps.setString(1, b.noRm);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Data bayi belum ada di tabel pasien_bayi. "
                            + "Lengkapi dan simpan dulu di form Kelahiran Bayi.");
                }
                b.nama        = s(rs, "nm_pasien");
                b.jk          = s(rs, "jk");
                b.tglLahir    = s(rs, "tgl_lahir");
                b.golDarah    = s(rs, "gol_darah");
                b.rhesus      = s(rs, "rhesus");
                b.jamLahir    = s(rs, "jam_lahir");
                b.anakKe      = s(rs, "anakke");
                b.berat       = s(rs, "berat_badan");
                b.panjang     = s(rs, "panjang_badan");
                b.prosesLahir = s(rs, "proses_lahir");
                b.namaPenolong = s(rs, "nama_penolong");
                b.nikPenolong  = s(rs, "ktp_penolong");
                b.namaIbu     = s(rs, "nm_ibu");
            }
        }
        // NIK ibu: sumber utama bayi_ibu_mapping (yang sudah diverifikasi), cadangan
        // tabel pasien lewat nama ibu. Tanpa NIK ibu, MPI tidak bisa mendaftarkan bayi.
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select ifnull(nik_ibu,'') nik_ibu, ifnull(nm_ibu_teks,'') nm_ibu "
              + "from bayi_ibu_mapping where no_rawat_bayi=? and status<>'Ditolak' limit 1")) {
            ps.setString(1, b.noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b.nikIbu = s(rs, "nik_ibu");
                    if (!s(rs, "nm_ibu").isEmpty()) b.namaIbu = s(rs, "nm_ibu");
                }
            }
        }
        if (b.nikIbu.isEmpty() && !b.namaIbu.isEmpty()) {
            b.nikIbu = cariIsi("select ifnull(no_ktp,'') from pasien where nm_pasien=? and jk='P' limit 1", b.namaIbu);
        }
        // Rawat inap ibu: diambil dari admisi ranap terakhir yang mulainya tidak
        // lebih dari sehari sesudah bayi lahir — yaitu admisi persalinannya.
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select ki.no_rawat, ki.tgl_masuk, ki.jam_masuk, "
              + "  ifnull(m.id_lokasi_satusehat,'') id_lokasi "
              + "from bayi_ibu_mapping bm "
              + "inner join reg_periksa r on r.no_rkm_medis = bm.no_rkm_medis_ibu and r.status_lanjut='Ranap' "
              + "inner join kamar_inap ki on ki.no_rawat = r.no_rawat "
              + "left join satu_sehat_mapping_lokasi_ranap m on m.kd_kamar = ki.kd_kamar "
              + "where bm.no_rawat_bayi = ? and ki.tgl_masuk <= date_add(?, interval 1 day) "
              + "order by ki.tgl_masuk desc, ki.jam_masuk desc limit 1")) {
            ps.setString(1, b.noRawat);
            ps.setString(2, tgl(b.tglLahir));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b.noRawatIbu  = s(rs, "no_rawat");
                    b.tglMasukIbu = s(rs, "tgl_masuk");
                    b.jamMasukIbu = s(rs, "jam_masuk");
                    b.idLokasiIbu = s(rs, "id_lokasi");
                }
            }
        }
        // Lokasi Encounter bayi. Encounter.location WAJIB (Rule 10120), jadi kalau
        // bayi belum punya kamar sendiri, dipakai kamar ibunya — saat lahir bayi
        // memang ada di ruang bersalin yang sama.
        b.idLokasiBayi = cariIsi(
                "select ifnull(m.id_lokasi_satusehat,'') from kamar_inap ki "
              + "inner join satu_sehat_mapping_lokasi_ranap m on m.kd_kamar = ki.kd_kamar "
              + "where ki.no_rawat = ? order by ki.tgl_masuk desc limit 1", b.noRawat);
        if (b.idLokasiBayi.isEmpty()) b.idLokasiBayi = b.idLokasiIbu;
        return b;
    }

    // ============================================================
    // ALUR
    // ============================================================

    private void jalankan(String noRawat, String noRm, Hasil h) throws Exception {
        if (noRm == null || noRm.trim().isEmpty()) {
            h.pesanGagal = "No.RM bayi kosong."; return;
        }
        if (noRawat == null || noRawat.trim().isEmpty()) {
            h.pesanGagal = "Bayi ini belum punya registrasi (no_rawat) di reg_periksa."; return;
        }
        Bayi b = baca(noRawat, noRm);

        if (!b.nikIbu.matches("\\d{16}")) {
            h.pesanGagal = "NIK ibu tidak ditemukan atau bukan 16 digit (" + b.nikIbu + ").\n"
                    + "Lengkapi dulu peta bayi-ibu atau NIK ibu di data pasien.";
            return;
        }

        // ── Tahap 3a: Patient bayi (MPI, pakai NIK ibu) ─────────────────────
        h.ihsBayi = ihsBayiTerkirim(b.noRawat);
        if (h.ihsBayi.isEmpty()) {
            long umur = umurHari(b.tglLahir);
            if (umur > 60) {
                h.pesanGagal = "Umur bayi " + umur + " hari (lebih dari 60). SatuSehat MPI menolak\n"
                        + "pendaftaran lewat NIK ibu bila lebih dari 60 hari (Rule 10397).\n"
                        + "Keluarga harus mengurus langsung ke Dinas Dukcapil.";
                return;
            }
            h.ihsBayi = postPatientBayi(b);
            simpan("replace into satu_sehat_bayi_baru_lahir(no_rawat,ihs_bayi,tgl_kirim) values(?,?,now())",
                    b.noRawat, h.ihsBayi);
            catat(b.noRawat, "PatientBayi", h.ihsBayi);
            h.langkah.add("1. Patient bayi  : " + h.ihsBayi + "  (baru)");
        } else {
            h.langkah.add("1. Patient bayi  : " + h.ihsBayi + "  (sudah ada, dilewati)");
        }

        // IHS ibu dipakai dua kali: jadi subjek Observation penolong persalinan,
        // dan disimpan di baris akta untuk tahap 5.
        String ihsIbu = nvl(cekNik.tampilIDPasien(b.nikIbu));
        String idPraktisi = idPraktisi(b);

        // ── Tahap 2: Encounter ibu (rawat inap obstetri) ────────────────────
        // Satu Encounter memenuhi DUA item checklist sekaligus: 02.01 (class IMP
        // + serviceType 186) dan 02.02 (yang sama, ditambah location terisi).
        // Tanpa Encounter ini, Observation penolong persalinan pun tidak dianggap
        // selesai karena ibunya tidak dikenali sebagai bagian dari kasus.
        if (ihsIbu.isEmpty()) {
            h.langkah.add("2. Encounter ibu : dilewati (IHS ibu belum ada di SatuSehat)");
        } else if (b.noRawatIbu.isEmpty()) {
            h.langkah.add("2. Encounter ibu : dilewati (ibu belum terdaftar rawat inap)");
        } else if (b.idLokasiIbu.isEmpty()) {
            // location BUKAN opsional: "Element not found: Encounter.location
            // (RuleNumber: 10120)". Lebih baik berhenti dengan sebab yang jelas
            // daripada menembak API dengan payload yang pasti ditolak.
            h.langkah.add("2. Encounter ibu : dilewati (kamar ibu belum dipetakan ke Lokasi Satu Sehat — "
                    + "lengkapi di menu Mapping Lokasi Ranap Satu Sehat)");
        } else {
            String idEncIbu = cariIsi("select ifnull(id_resource,'') from satu_sehat_akta_kelahiran_resource "
                                    + "where no_rawat=? and jenis='EncounterIbu'", b.noRawat);
            if (idEncIbu.isEmpty()) {
                idEncIbu = post("Encounter", jsonEncounterIbu(b, ihsIbu, idPraktisi));
                catat(b.noRawat, "EncounterIbu", idEncIbu);
                h.langkah.add("2. Encounter ibu : " + idEncIbu + "  (baru)");
            } else {
                h.langkah.add("2. Encounter ibu : " + idEncIbu + "  (sudah ada, dilewati)");
            }
        }

        // ── Tahap 3b: EpisodeOfCare Neonate ─────────────────────────────────
        // Dedup & penyimpanan lokal diurus SatuSehatEpisodeOfCareHelper.
        h.idEoc = SatuSehatEpisodeOfCareHelper.getOrCreate(
                b.noRm, h.ihsBayi, namaBayi(b),
                SatuSehatEpisodeOfCareHelper.TYPE_NEONATE[0],
                SatuSehatEpisodeOfCareHelper.TYPE_NEONATE[1],
                "", "", b.tglLahir, "");
        if (h.idEoc.isEmpty()) {
            h.pesanGagal = "EpisodeOfCare Neonate gagal dibuat. Lihat log untuk pesan SatuSehat.";
            return;
        }
        catat(b.noRawat, "EpisodeOfCareBayi", h.idEoc);
        h.langkah.add("3. EpisodeOfCare : " + h.idEoc);

        // ── Tahap 3c: Encounter bayi ────────────────────────────────────────
        h.idEncounter = cariIsi("select ifnull(encounter_bayi,'') from satu_sehat_akta_kelahiran where no_rawat=?", b.noRawat);
        if (h.idEncounter.isEmpty()) {
            h.idEncounter = post("Encounter", jsonEncounter(b, h.ihsBayi, h.idEoc, idPraktisi));
            simpanEncounter(b, h.ihsBayi, ihsIbu, h.idEncounter);
            catat(b.noRawat, "EncounterBayi", h.idEncounter);
            h.langkah.add("4. Encounter bayi: " + h.idEncounter + "  (baru)");
        } else {
            h.langkah.add("4. Encounter bayi: " + h.idEncounter + "  (sudah ada, dilewati)");
        }

        // ── Tahap 4: delapan Observation ────────────────────────────────────
        String subj = "Patient/" + h.ihsBayi;
        String perf = idPraktisi.isEmpty()
                ? "Organization/" + koneksiDB.IDSATUSEHAT()
                : "Practitioner/" + idPraktisi;
        String eff = utcDariLahir(b.tglLahir, b.jamLahir);

        h.jumlahObservasi += obs(b, "ObservationBeratLahir", SYS_LOINC, "8339-4", "Birth weight Measured",
                subj, h.idEncounter, eff, perf, vQty(b.berat, "g", "g"), angka(b.berat));
        h.jumlahObservasi += obs(b, "ObservationPanjangLahir", SYS_LOINC, "89269-5", "Body height --at birth",
                subj, h.idEncounter, eff, perf, vQty(b.panjang, "cm", "cm"), angka(b.panjang));
        h.jumlahObservasi += obs(b, "ObservationLokasiKelahiran", SYS_LOINC, "72150-6", "Birth place",
                subj, h.idEncounter, eff, perf, vStr(namaFaskes()), true);
        // Penolong persalinan SUBJEKNYA IBU, bukan bayi — dicek begitu oleh
        // checklist resmi (item 03.04). Kalau dikirim atas nama bayi, resource-nya
        // tetap terbentuk tapi item wajibnya tidak pernah dianggap selesai.
        h.jumlahObservasi += obs(b, "ObservationPenolongPersalinan", SYS_KEMKES, "OC000013", "Penolong Persalinan",
                "Patient/" + ihsIbu, h.idEncounter, eff, perf, vStr(b.namaPenolong),
                !b.namaPenolong.isEmpty() && !ihsIbu.isEmpty());
        h.jumlahObservasi += obs(b, "ObservationJamLahir", SYS_LOINC, "57715-5", "Date and time of birth",
                subj, h.idEncounter, eff, perf, vTime(b.jamLahir), jamMasukAkal(b.jamLahir));
        h.jumlahObservasi += obs(b, "ObservationAnakKe", SYS_SNOMED, "365475008", "Finding of birth order",
                subj, h.idEncounter, eff, perf, vInt(b.anakKe), angka(b.anakKe));
        h.jumlahObservasi += obs(b, "ObservationGolonganDarah", SYS_LOINC, "883-9", "ABO group [Type] in Blood",
                subj, h.idEncounter, eff, perf, vStr(b.golDarah), golDarahJelas(b.golDarah));
        h.jumlahObservasi += obs(b, "ObservationGolDarahRhesus", SYS_LOINC, "882-1", "ABO and Rh group [Type] in Blood",
                subj, h.idEncounter, eff, perf, vStr(b.golDarah + b.rhesus),
                golDarahJelas(b.golDarah) && !b.rhesus.isEmpty());

        h.langkah.add("5. Observation   : " + h.jumlahObservasi + " terkirim");
        h.sukses = true;
    }

    /**
     * Kirim satu Observation bila layak dan belum pernah terkirim.
     * @return 1 kalau baru terkirim, 0 kalau dilewati (sudah ada / data tidak layak)
     */
    private int obs(Bayi b, String jenis, String sys, String code, String display,
                    String subj, String idEnc, String eff, String perf, String valueJson, boolean layak) {
        if (!layak) {
            System.out.println("Lewati " + jenis + " : data belum lengkap/tidak masuk akal");
            return 0;
        }
        if (!cariIsi("select ifnull(id_resource,'') from satu_sehat_akta_kelahiran_resource "
                   + "where no_rawat=? and jenis=?", b.noRawat, jenis).isEmpty()) {
            return 0; // sudah punya ID -> sudah terkirim
        }
        try {
            String id = post("Observation", jsonObs(sys, code, display, subj, idEnc, eff, perf, valueJson));
            catat(b.noRawat, jenis, id);
            return 1;
        } catch (Exception e) {
            System.out.println("Notif " + jenis + " : " + pesanRingkas(e));
            return 0;
        }
    }

    // ============================================================
    // PAYLOAD
    // ============================================================

    /** Nama bayi mengikuti pola MPI: "By Ny <nama ibu tanpa gelar>". */
    private String namaBayi(Bayi b) {
        String ibu = b.namaIbu.replaceAll("\"", "'")
                .replaceAll("(?i)^\\s*(ny|nn|sdri|tn)\\s*[.]?\\s+", "").trim();
        return ibu.isEmpty() ? (b.nama.isEmpty() ? "Bayi" : b.nama) : "By Ny " + ibu;
    }

    private String jsonPatientBayi(Bayi b) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"Patient\",");
        sb.append("\"meta\":{\"profile\":[\"https://fhir.kemkes.go.id/r4/StructureDefinition/Patient\"]},");
        // Bayi belum punya NIK -> didaftarkan dengan NIK IBU. Wajib begini, bukan pilihan.
        sb.append("\"identifier\":[{\"use\":\"official\",\"system\":\"https://fhir.kemkes.go.id/id/nik-ibu\",\"value\":")
          .append(q(b.nikIbu)).append("}],");
        sb.append("\"name\":[{\"use\":\"official\",\"text\":").append(q(namaBayi(b))).append("}],");
        sb.append("\"gender\":\"").append(b.jk.equalsIgnoreCase("P") ? "female" : "male").append("\",");
        sb.append("\"birthDate\":").append(q(tgl(b.tglLahir))).append(",");
        sb.append("\"multipleBirthInteger\":1,");
        // administrativeCode WAJIB lengkap sampai kelurahan, dan kode kecamatan/
        // kelurahan harus cocok dengan kode kota (Rule 10633-10635).
        sb.append("\"address\":[{\"use\":\"home\",\"line\":[\"-\"],\"city\":").append(q(koneksiDB.KABUPATENSATUSEHAT())).append(",")
          .append("\"postalCode\":").append(q(koneksiDB.KODEPOSSATUSEHAT())).append(",\"country\":\"ID\",")
          .append("\"extension\":[{\"url\":\"https://fhir.kemkes.go.id/r4/StructureDefinition/administrativeCode\",\"extension\":[")
          .append("{\"url\":\"province\",\"valueCode\":").append(q(koneksiDB.PROPINSISATUSEHAT())).append("},")
          .append("{\"url\":\"city\",\"valueCode\":").append(q(koneksiDB.KABUPATENSATUSEHAT())).append("},")
          .append("{\"url\":\"district\",\"valueCode\":").append(q(koneksiDB.KECAMATANSATUSEHAT())).append("},")
          .append("{\"url\":\"village\",\"valueCode\":").append(q(koneksiDB.KELURAHANSATUSEHAT())).append("}")
          .append("]}]}]}");
        return sb.toString();
    }

    private String jsonEncounter(Bayi b, String ihsBayi, String idEoc, String idPraktisi) {
        String mulai = utcDariLahir(b.tglLahir, b.jamLahir);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"Encounter\",");
        sb.append("\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/encounter/")
          .append(koneksiDB.IDSATUSEHAT()).append("\",\"value\":").append(q(b.noRawat)).append("}],");
        sb.append("\"status\":\"arrived\",");
        sb.append("\"statusHistory\":[{\"status\":\"arrived\",\"period\":{\"start\":").append(q(mulai)).append("}}],");
        sb.append("\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\"AMB\",\"display\":\"ambulatory\"},");
        sb.append("\"serviceType\":{\"coding\":[{\"system\":\"").append(SYS_SNOMED)
          .append("\",\"code\":\"840586005\",\"display\":\"Neonatal service\"}]},");
        sb.append("\"subject\":{\"reference\":\"Patient/").append(ihsBayi).append("\",\"display\":").append(q(namaBayi(b))).append("},");
        if (!idPraktisi.isEmpty()) {
            sb.append("\"participant\":[{\"type\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\",")
              .append("\"code\":\"ATND\",\"display\":\"attender\"}]}],")
              .append("\"individual\":{\"reference\":\"Practitioner/").append(idPraktisi).append("\",\"display\":")
              .append(q(b.namaPenolong)).append("}}],");
        }
        // location WAJIB juga untuk Encounter bayi (Rule 10120), sekaligus syarat
        // item 02.05 "Encounter bayi masuk ruang pemeriksaan".
        if (!b.idLokasiBayi.isEmpty()) {
            sb.append("\"location\":[{\"location\":{\"reference\":\"Location/").append(b.idLokasiBayi).append("\"}}],");
        }
        sb.append("\"period\":{\"start\":").append(q(mulai)).append("},");
        sb.append("\"episodeOfCare\":[{\"reference\":\"EpisodeOfCare/").append(idEoc).append("\"}],");
        // serviceProvider WAJIB Organization UTAMA. Sub-organisasi ditolak
        // "wrong organization ID" (Rule 10124) — jangan diganti sub-unit KIA.
        sb.append("\"serviceProvider\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"}}");
        return sb.toString();
    }

    /**
     * Encounter ibu — rawat inap obstetri.
     *
     * serviceType 186 = Obstetrics, dan class IMP = inpatient. Keduanya dicocokkan
     * lokal oleh pemantau karena parameter service-type diabaikan diam-diam oleh
     * GCP Healthcare, jadi harus benar di payload-nya sendiri.
     */
    private String jsonEncounterIbu(Bayi b, String ihsIbu, String idPraktisi) {
        String mulai = utcDariLahir(b.tglMasukIbu, b.jamMasukIbu);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resourceType\":\"Encounter\",");
        sb.append("\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/encounter/")
          .append(koneksiDB.IDSATUSEHAT()).append("\",\"value\":").append(q(b.noRawatIbu)).append("}],");
        // Sengaja "in-progress", bukan "finished". Encounter berstatus finished
        // dituntut dua hal lagi oleh SatuSehat: setiap statusHistory harus punya
        // start DAN end (Rule 10122), dan Encounter.diagnosis wajib ada
        // (Rule 10457) — padahal saat data kelahiran dikirim, ibunya biasanya
        // memang belum pulang dan diagnosa akhirnya belum ditegakkan.
        sb.append("\"status\":\"in-progress\",");
        sb.append("\"statusHistory\":[{\"status\":\"in-progress\",\"period\":{\"start\":").append(q(mulai)).append("}}],");
        sb.append("\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\"IMP\",\"display\":\"inpatient encounter\"},");
        sb.append("\"serviceType\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/service-type\",")
          .append("\"code\":\"186\",\"display\":\"Obstetrics\"}]},");
        sb.append("\"subject\":{\"reference\":\"Patient/").append(ihsIbu).append("\",\"display\":").append(q(b.namaIbu)).append("},");
        if (!idPraktisi.isEmpty()) {
            sb.append("\"participant\":[{\"type\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\",")
              .append("\"code\":\"ATND\",\"display\":\"attender\"}]}],")
              .append("\"individual\":{\"reference\":\"Practitioner/").append(idPraktisi).append("\",\"display\":")
              .append(q(b.namaPenolong)).append("}}],");
        }
        // location WAJIB terisi untuk item 02.02. Sumbernya peta kamar ranap ->
        // Location SatuSehat (satu_sehat_mapping_lokasi_ranap).
        if (!b.idLokasiIbu.isEmpty()) {
            sb.append("\"location\":[{\"location\":{\"reference\":\"Location/").append(b.idLokasiIbu).append("\"}}],");
        }
        sb.append("\"period\":{\"start\":").append(q(mulai)).append("},");
        sb.append("\"serviceProvider\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"}}");
        return sb.toString();
    }

    private String jsonObs(String sys, String code, String display, String subj, String idEnc,
                           String eff, String perf, String valueJson) {
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"Observation\",");
        b.append("\"status\":\"final\",");
        b.append("\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",")
         .append("\"code\":\"exam\",\"display\":\"Exam\"}]}],");
        b.append("\"code\":{\"coding\":[{\"system\":\"").append(sys).append("\",\"code\":\"").append(code)
         .append("\",\"display\":").append(q(display)).append("}]},");
        b.append("\"subject\":{\"reference\":\"").append(subj).append("\"},");
        b.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
        b.append("\"effectiveDateTime\":").append(q(eff)).append(",");
        // issued WAJIB, walau FHIR R4 menyebutnya opsional:
        // "Element not found: Observation.issued (RuleNumber: 10296)".
        b.append("\"issued\":").append(q(utcSekarang())).append(",");
        b.append("\"performer\":[{\"reference\":\"").append(perf).append("\"}]");
        if (valueJson != null && !valueJson.isEmpty()) b.append(",").append(valueJson);
        b.append("}");
        return b.toString();
    }

    private String vStr(String s)  { return "\"valueString\":" + q(s); }
    private String vInt(String s)  { return "\"valueInteger\":" + s.trim(); }
    private String vTime(String s) { return "\"valueTime\":" + q(jam(s)); }
    private String vQty(String num, String unit, String code) {
        return "\"valueQuantity\":{\"value\":" + num.trim() + ",\"unit\":" + q(unit)
             + ",\"system\":\"http://unitsofmeasure.org\",\"code\":" + q(code) + "}";
    }

    // ============================================================
    // KIRIM & SIMPAN
    // ============================================================

    /**
     * Daftarkan Patient bayi ke MPI.
     *
     * MPI men-DEDUPLIKASI bayi dari kombinasi NIK ibu + tanggal lahir + jenis
     * kelamin. Kalau kombinasi itu sudah terdaftar — termasuk oleh faskes lain —
     * POST membalas <b>400 yang badannya justru resource Patient yang sudah
     * ada</b>, lengkap dengan ihs-number-nya. Itu bukan kegagalan: playbook
     * memang meminta "GET dulu sebelum POST supaya pasien bayi tidak dobel",
     * dan satu bayi memang harus satu IHS.
     *
     * Jadi: cari dulu lewat GET; kalau POST tetap kena 400 berisi Patient,
     * pakai id dari badan balasan itu alih-alih menyatakan gagal.
     */
    private String postPatientBayi(Bayi b) throws Exception {
        String sudahAda = cariPatientBayi(b);
        if (!sudahAda.isEmpty()) return sudahAda;
        try {
            return post("Patient", jsonPatientBayi(b));
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            try {
                JsonNode n = mapper.readTree(body);
                if ("Patient".equals(n.path("resourceType").asText())) {
                    String id = n.path("id").asText();
                    if (!id.isEmpty()) {
                        System.out.println("Patient bayi sudah terdaftar di MPI, memakai " + id);
                        return id;
                    }
                }
            } catch (Exception abaikan) { /* bukan JSON Patient, lempar aslinya */ }
            throw e;
        }
    }

    /**
     * Cari Patient bayi yang sudah terdaftar: NIK ibu + tanggal lahir + jenis
     * kelamin harus sama persis. Satu NIK ibu bisa punya banyak bayi (anak
     * berbeda, kembar), jadi ketiganya dipakai bersama, bukan NIK ibu saja.
     */
    private String cariPatientBayi(Bayi b) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String url = link + "/Patient?identifier=https://fhir.kemkes.go.id/id/nik-ibu|" + b.nikIbu;
            String hasil = api.getRest().exchange(url, HttpMethod.GET,
                    new HttpEntity<>(h), String.class).getBody();
            String jk = b.jk.equalsIgnoreCase("P") ? "female" : "male";
            for (JsonNode e : mapper.readTree(hasil).path("entry")) {
                JsonNode r = e.path("resource");
                if (tgl(b.tglLahir).equals(r.path("birthDate").asText())
                        && jk.equals(r.path("gender").asText())) {
                    String id = r.path("id").asText();
                    if (!id.isEmpty()) return id;
                }
            }
        } catch (Exception e) {
            // Gagal mencari bukan alasan membatalkan; POST di bawah yang menentukan.
            System.out.println("Notif cari Patient bayi : " + pesanRingkas(e));
        }
        return "";
    }

    private String post(String resource, String json) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
        System.out.println("POST " + link + "/" + resource + " : " + json);
        String hasil = api.getRest().exchange(link + "/" + resource, HttpMethod.POST,
                new HttpEntity(json, headers), String.class).getBody();
        System.out.println("Hasil : " + hasil);
        JsonNode root = mapper.readTree(hasil);
        // asText() TANPA argumen — Jackson 2.2.3 di classpath build tidak punya
        // asText(String). Jangan diubah jadi asText("").
        String id = root.path("id").asText();
        if (id == null || id.isEmpty() || id.equals("null")) {
            throw new IllegalStateException(resource + " tidak mengembalikan id");
        }
        return id;
    }

    /** Catat ID resource — inilah penanda "sudah terkirim". */
    private void catat(String noRawat, String jenis, String id) {
        simpan("insert into satu_sehat_akta_kelahiran_resource(no_rawat,jenis,id_resource,waktu) "
             + "values(?,?,?,now()) on duplicate key update id_resource=values(id_resource), waktu=now()",
                noRawat, jenis, id);
    }

    /** Buat/isi baris akta supaya encounter_bayi & identitas dasar sudah terisi otomatis. */
    private void simpanEncounter(Bayi b, String ihsBayi, String ihsIbu, String idEncounter) {
        simpan("insert into satu_sehat_akta_kelahiran"
             + " (no_rawat,ihs_bayi,ihs_ibu,encounter_bayi,nik_ibu,nama_ibu,status) "
             + "values(?,?,?,?,?,?,'Draft') "
             + "on duplicate key update ihs_bayi=values(ihs_bayi), ihs_ibu=values(ihs_ibu), "
             + "encounter_bayi=values(encounter_bayi), nik_ibu=values(nik_ibu), nama_ibu=values(nama_ibu)",
                b.noRawat, ihsBayi, ihsIbu, idEncounter, b.nikIbu, b.namaIbu);
    }

    // ============================================================
    // BANTUAN
    // ============================================================

    private String idPraktisi(Bayi b) {
        if (b.nikPenolong.isEmpty()) return "";
        try { return nvl(cekNik.tampilIDParktisi(b.nikPenolong)); }
        catch (Exception e) { System.out.println("Notif IHS penolong : " + e); return ""; }
    }

    private String namaFaskes() {
        String n = cariIsi("select ifnull(nama_instansi,'') from setting limit 1");
        return n.isEmpty() ? "Rumah Sakit" : n;
    }

    /**
     * Gabung tanggal lahir + jam lahir jadi dateTime UTC.
     * Jam lahir tersimpan WIB; SatuSehat menilai instannya, jadi digeser ke UTC
     * supaya tidak terbaca sebagai masa depan (Rule 10406).
     */
    private static String utcDariLahir(String tglLahir, String jamLahir) {
        try {
            LocalDate d = LocalDate.parse(tgl(tglLahir));
            String j = jamMasukAkal(jamLahir) ? jam(jamLahir) : "00:00:01";
            LocalDateTime wib = LocalDateTime.of(d, java.time.LocalTime.parse(j));
            return wib.atZone(ZoneId.of("Asia/Jakarta")).withZoneSameInstant(ZoneOffset.UTC)
                      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'"));
        } catch (Exception e) {
            return java.time.ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'"));
        }
    }

    private static String utcSekarang() {
        return java.time.ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'"));
    }

    private static long umurHari(String tglLahir) {
        try { return ChronoUnit.DAYS.between(LocalDate.parse(tgl(tglLahir)), LocalDate.now()); }
        catch (Exception e) { return 0; }
    }

    private static String cariNoRawat(String noRm) {
        return cariIsi("select no_rawat from reg_periksa where no_rkm_medis=? "
                     + "order by tgl_registrasi asc, jam_reg asc limit 1", noRm);
    }

    private static String tgl(String s) { return s == null ? "" : s.trim().length() >= 10 ? s.trim().substring(0, 10) : s.trim(); }
    private static String jam(String s) { return s == null ? "" : s.trim().length() >= 8 ? s.trim().substring(0, 8) : s.trim(); }

    /** 00:00:00 di Khanza berarti "belum diisi", bukan tengah malam. */
    private static boolean jamMasukAkal(String s) {
        String j = jam(s);
        return j.matches("\\d{2}:\\d{2}:\\d{2}") && !j.equals("00:00:00");
    }

    private static boolean angka(String s) { return s != null && s.trim().matches("\\d+(\\.\\d+)?") && !s.trim().matches("0+(\\.0+)?"); }

    /** Golongan darah bayi sering "-" atau kosong di Khanza; jangan kirim yang begitu. */
    private static boolean golDarahJelas(String s) {
        return s != null && s.trim().toUpperCase().matches("A|B|AB|O");
    }

    private static String s(ResultSet rs, String kolom) {
        try { String v = rs.getString(kolom); return v == null ? "" : v.trim(); } catch (Exception e) { return ""; }
    }

    private static String nvl(String s) { return s == null ? "" : s.trim(); }

    private static String cariIsi(String sql, String... p) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) ps.setString(i + 1, p[i]);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nvl(rs.getString(1)) : ""; }
        } catch (Exception e) { System.out.println("Notif cariIsi : " + e); return ""; }
    }

    private static void simpan(String sql, String... p) {
        try (PreparedStatement ps = koneksiDB.condb().prepareStatement(sql)) {
            for (int i = 0; i < p.length; i++) ps.setString(i + 1, p[i]);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println("Notif simpan : " + e); }
    }

    /** Ambil pesan SatuSehat dari body error, bukan cuma nama exception-nya. */
    private static String pesanRingkas(Exception e) {
        if (e instanceof org.springframework.web.client.HttpStatusCodeException) {
            return e.getMessage() + " => "
                 + ((org.springframework.web.client.HttpStatusCodeException) e).getResponseBodyAsString();
        }
        return String.valueOf(e.getMessage() == null ? e : e.getMessage());
    }

    /** Kutip sekaligus escape jadi string JSON. */
    private static String q(String s) {
        if (s == null) s = "";
        StringBuilder b = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n");  break;
                case '\r': b.append("\\r");  break;
                case '\t': b.append("\\t");  break;
                default:
                    if (c < 0x20) b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
            }
        }
        return b.append("\"").toString();
    }
}
