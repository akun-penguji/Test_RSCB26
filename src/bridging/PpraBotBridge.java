package bridging;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Bridge pengiriman laporan PPRA Kemenkes RI (Program Pengendalian Resistensi Antimikroba)
 * Portal: https://mutufasyankes.kemkes.go.id/ppra/
 * Terhubung langsung ke Backend Rust simrscb-backend (Port 8088 / URLPPRABOT).
 */
public class PpraBotBridge {

    public static class BridgeResult {
        public final boolean ok;
        public final String message;
        public final String detail;

        public BridgeResult(boolean ok, String message, String detail) {
            this.ok = ok;
            this.message = message;
            this.detail = detail;
        }
    }

    public static String getBaseUrl() {
        String envUrl = System.getenv("PPRABOT_BACKEND_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim().replaceAll("/+$", "");
        }
        try {
            java.io.File f = new java.io.File("setting/database.xml");
            if (f.exists()) {
                java.util.Properties prop = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) {
                    prop.loadFromXML(fis);
                    String val = prop.getProperty("URLPPRABOT");
                    if (val != null && !val.trim().isEmpty()) {
                        return val.trim().replaceAll("/+$", "");
                    }
                }
            }
        } catch (Exception ignored) {}
        return "http://100.75.16.105:8088/api/ppra/bot";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Bilangan bulat dari isi sel tabel. Sel bisa berupa angka atau teks hasil String.format
     * yang pemisah desimalnya tergantung locale ("12.0" / "12,0"). Dulu titik & koma sama-sama
     * dibuang sehingga "12.0" menjadi 120 (jumlah kemasan RL 3.19.3 terkirim 10x lipat).
     */
    static long toLong(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return Math.round(((Number) val).doubleValue());
        String s = val.toString().trim().replace(" ", "");
        if (s.isEmpty()) return 0;
        int titik = s.lastIndexOf('.'), koma = s.lastIndexOf(',');
        if (titik >= 0 && koma >= 0) {
            // keduanya ada: yang paling kanan adalah pemisah desimal
            s = titik > koma ? s.replace(",", "") : s.replace(".", "").replace(',', '.');
        } else if (titik >= 0 || koma >= 0) {
            char sep = titik >= 0 ? '.' : ',';
            int pos = Math.max(titik, koma);
            boolean ribuan = s.indexOf(sep) != pos || s.length() - pos - 1 == 3;
            s = ribuan ? s.replace(String.valueOf(sep), "") : s.replace(sep, '.');
        }
        try {
            return Math.round(Double.parseDouble(s));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Sub-laporan yang pemetaan field-nya ke form portal sudah dicocokkan dengan form asli.
     * Hanya RL 3.19.2: backend menerjemahkan isinya ke nama field portal (id_jenis_perawatan,
     * periode_pengambilan_data, ...). Sub-laporan lain diteruskan backend dengan nama kunci
     * buatan sendiri (triwulan, kode_atc, komite_kprs, ...) yang tidak pernah dicocokkan ke portal,
     * dan beberapa tabel manual malah terkirim kosong - jadi pengiriman otomatisnya dimatikan
     * sampai nama field portal masing-masing diverifikasi (rekam form asli lewat DevTools).
     */
    private static final java.util.Set<String> FORM_TERVERIFIKASI =
            new java.util.HashSet<>(java.util.Arrays.asList("3.19.2"));

    private static BridgeResult belumDidukung(String form) {
        return new BridgeResult(false,
            "Pengiriman otomatis RL " + form + " belum didukung",
            "Nama field form portal untuk RL " + form + " belum diverifikasi, sehingga data bisa\n"
            + "tidak tercatat atau tercatat salah di portal Kemenkes. Untuk sementara, isi RL " + form + "\n"
            + "langsung di portal (gunakan Salin ke Clipboard bila tersedia).");
    }

    /** Token opsional untuk endpoint bot (env PPRABOT_TOKEN atau TOKENPPRABOT di setting/database.xml). */
    private static String token() {
        String env = System.getenv("PPRABOT_TOKEN");
        if (env != null && !env.trim().isEmpty()) return env.trim();
        try (java.io.FileInputStream fis = new java.io.FileInputStream("setting/database.xml")) {
            java.util.Properties prop = new java.util.Properties();
            prop.loadFromXML(fis);
            String v = prop.getProperty("TOKENPPRABOT");
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private static void pasangToken(HttpURLConnection conn) {
        String t = token();
        if (!t.isEmpty()) conn.setRequestProperty("X-Ppra-Token", t);
    }

    private static double toDouble(Object val) {
        if (val == null) return 0.0;
        String s = val.toString().trim().replace(",", ".");
        if (s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static BridgeResult postJson(String endpoint, String jsonPayload) {
        String targetUrl = getBaseUrl() + endpoint;
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            pasangToken(conn);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);

            byte[] outBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(outBytes);
                os.flush();
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();
            StringBuilder resp = new StringBuilder();
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resp.append(line);
                    }
                }
            }

            String respText = resp.toString();
            if (code >= 200 && code < 300) {
                return new BridgeResult(true, "Berhasil diproses oleh server bot PPRA", respText);
            } else {
                return new BridgeResult(false, "Server bot merespon HTTP " + code, respText);
            }
        } catch (java.net.ConnectException ce) {
            return new BridgeResult(false, "Tidak dapat terhubung ke Backend Bot PPRA",
                "Koneksi ke " + targetUrl + " gagal.\nPastikan service backend simrscb (port 8088) aktif.");
        } catch (Exception e) {
            return new BridgeResult(false, "Terjadi kesalahan koneksi", e.getMessage());
        }
    }

    /**
     * Cek status koneksi dan sesi login bot PPRA
     */
    public static BridgeResult getStatus() {
        String targetUrl = getBaseUrl() + "/status";
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            pasangToken(conn);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();
            StringBuilder resp = new StringBuilder();
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resp.append(line);
                    }
                }
            }
            return new BridgeResult(code == 200, "Status PPRA Bot", resp.toString());
        } catch (Exception e) {
            return new BridgeResult(false, "Koneksi ke status bot PPRA gagal", e.getMessage());
        }
    }

    /**
     * Trigger login manual / refresh sesi ke portal PPRA
     */
    public static BridgeResult login() {
        return postJson("/login", "{}");
    }

    /**
     * Hapus satu baris laporan dari portal PPRA berdasarkan kode formulir dan ID
     */
    public static BridgeResult hapusLaporan(String form, String id) {
        // Rute backend: DELETE /laporan/:form/:id (dulu GET /delete/.. yang tidak ada di backend).
        String targetUrl = getBaseUrl() + "/laporan/" + form + "/" + id;
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");
            pasangToken(conn);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();
            StringBuilder resp = new StringBuilder();
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resp.append(line);
                    }
                }
            }
            return new BridgeResult(code >= 200 && code < 400, "Hapus Data PPRA", resp.toString());
        } catch (Exception e) {
            return new BridgeResult(false, "Gagal menghapus data dari portal PPRA", e.getMessage());
        }
    }

    // =========================================================================
    //  Sub-laporan 1: RL 3.19.1 Kegiatan PPRA
    // =========================================================================
    public static BridgeResult kirim191(int tahun, Object[] data) {
        if (!FORM_TERVERIFIKASI.contains("3.19.1")) return belumDidukung("3.19.1");
        if (data == null || data.length < 12) {
            return new BridgeResult(false, "Data RL 3.19.1 tidak lengkap", "Pastikan seluruh 12 item indikator kegiatan telah dipilih.");
        }
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"komite_kprs\":\"").append(data[0]).append("\",");
        json.append("\"tim_ppra\":\"").append(data[1]).append("\",");
        json.append("\"pedoman_ppra\":\"").append(data[2]).append("\",");
        json.append("\"panduan_penggunaan_antibiotik\":\"").append(data[3]).append("\",");
        json.append("\"forum_kajian_penyakit_infeksi\":\"").append(data[4]).append("\",");
        json.append("\"surveilans_pola_penggunaan_ab\":\"").append(data[5]).append("\",");
        json.append("\"surveilans_pola_mikroba\":\"").append(data[6]).append("\",");
        json.append("\"pola_kepekaan_ab\":\"").append(data[7]).append("\",");
        json.append("\"pemeriksaan_mikrobiologi\":\"").append(data[8]).append("\",");
        json.append("\"antibiogram\":\"").append(data[9]).append("\",");
        json.append("\"evaluasi_penggunaan_ab\":\"").append(data[10]).append("\",");
        json.append("\"penyebarluasan_informasi\":\"").append(data[11]).append("\"");
        json.append("}");
        return postJson("/submit/3.19.1", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 2: RL 3.19.2 Pasien Mendapatkan Antibiotik
    // =========================================================================
    public static BridgeResult kirim192(int tahun, int triwulan, String jenisRawat, List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.2 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        // Portal: id_jenis_perawatan 1=Rawat Jalan, 2=Rawat Inap.
        String kodeRawat = jenisRawat.equalsIgnoreCase("Rawat Jalan") || jenisRawat.equals("1") ? "1" : "2";

        StringBuilder poliklinik = new StringBuilder("[");
        StringBuilder jumlahSampel = new StringBuilder("[");
        StringBuilder jumlahPasienAb = new StringBuilder("[");
        StringBuilder sebutkanArr = new StringBuilder("[");

        // Kirim per-baris. Untuk SMF yang jatuh ke nilai dropdown 6 (Lain-lain) —
        // IGD, Pulmonologi, Lain-lain — portal membedakannya lewat kolom
        // "sebutkan" (isian bebas), sehingga tiap unit tetap tampil terpisah.
        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 5) continue;
            String kode = r[0] != null ? r[0].toString().trim() : "";
            // Lewati baris total jika ada
            if (kode.equalsIgnoreCase("TOTAL") || kode.equalsIgnoreCase("JUMLAH")) continue;

            String dropVal = r[2] != null ? r[2].toString().trim() : kode;
            String namaSmf = r[1] != null ? r[1].toString().trim() : "";
            // Sebutkan hanya dipakai portal untuk dropdown 6 (Lain-lain).
            String sebutkan = dropVal.equals("6") ? namaSmf : "";
            long jmlSampel = toLong(r[3]);
            long jmlAb = toLong(r[4]);

            if (!first) {
                poliklinik.append(",");
                jumlahSampel.append(",");
                jumlahPasienAb.append(",");
                sebutkanArr.append(",");
            }
            poliklinik.append("\"").append(escape(dropVal)).append("\"");
            jumlahSampel.append(jmlSampel);
            jumlahPasienAb.append(jmlAb);
            sebutkanArr.append("\"").append(escape(sebutkan)).append("\"");
            first = false;
        }
        poliklinik.append("]");
        jumlahSampel.append("]");
        jumlahPasienAb.append("]");
        sebutkanArr.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"triwulan\":\"").append(triwulan).append("\",");
        json.append("\"rawat\":\"").append(kodeRawat).append("\",");
        json.append("\"poliklinik\":").append(poliklinik).append(",");
        json.append("\"poliklinik_sebutkan\":").append(sebutkanArr).append(",");
        json.append("\"jumlah_sampel\":").append(jumlahSampel).append(",");
        json.append("\"jumlah_pasien_antibiotik\":").append(jumlahPasienAb);
        json.append("}");

        return postJson("/submit/3.19.2", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 3: RL 3.19.3 Kuantitas Penggunaan Antibiotik (DDD)
    // =========================================================================
    public static BridgeResult kirim193(int tahun, int triwulan, String jenisRawat, double totalPenyebut, List<Object[]> rows) {
        if (!FORM_TERVERIFIKASI.contains("3.19.3")) return belumDidukung("3.19.3");
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.3 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        // Portal: id_jenis_perawatan 1=Rawat Jalan, 2=Rawat Inap.
        String kodeRawat = jenisRawat.equalsIgnoreCase("Rawat Jalan") || jenisRawat.equals("1") ? "1" : "2";

        StringBuilder kodeAtc = new StringBuilder("[");
        StringBuilder jumlahKemasan = new StringBuilder("[");
        StringBuilder jumlahGram = new StringBuilder("[");

        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 5) continue;
            String atc = r[0] != null ? r[0].toString().trim() : "";
            if (atc.isEmpty() || atc.equalsIgnoreCase("TOTAL") || atc.equalsIgnoreCase("JUMLAH")) continue;

            long kemasan = toLong(r[3]);
            double gram = toDouble(r[4]);

            if (!first) {
                kodeAtc.append(",");
                jumlahKemasan.append(",");
                jumlahGram.append(",");
            }
            kodeAtc.append("\"").append(escape(atc)).append("\"");
            jumlahKemasan.append(kemasan);
            jumlahGram.append(String.format(java.util.Locale.US, "%.2f", gram));
            first = false;
        }
        kodeAtc.append("]");
        jumlahKemasan.append("]");
        jumlahGram.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"triwulan\":\"").append(triwulan).append("\",");
        json.append("\"rawat\":\"").append(kodeRawat).append("\",");
        json.append("\"total_hari_rawat_kunjungan\":").append(String.format(java.util.Locale.US, "%.0f", totalPenyebut)).append(",");
        json.append("\"kode_atc\":").append(kodeAtc).append(",");
        json.append("\"jumlah_kemasan\":").append(jumlahKemasan).append(",");
        json.append("\"jumlah_gram\":").append(jumlahGram);
        json.append("}");

        return postJson("/submit/3.19.3", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 4: RL 3.19.4 Kualitas Penggunaan Antibiotik (Gyssens)
    // =========================================================================
    public static BridgeResult kirim194(int tahun, List<Object[]> rows) {
        if (!FORM_TERVERIFIKASI.contains("3.19.4")) return belumDidukung("3.19.4");
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.4 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        StringBuilder smf = new StringBuilder("[");
        StringBuilder totalKasus = new StringBuilder("[");
        StringBuilder k0 = new StringBuilder("[");
        StringBuilder k1 = new StringBuilder("[");
        StringBuilder k2a = new StringBuilder("[");
        StringBuilder k2b = new StringBuilder("[");
        StringBuilder k2c = new StringBuilder("[");
        StringBuilder k3a = new StringBuilder("[");
        StringBuilder k3b = new StringBuilder("[");
        StringBuilder k4a = new StringBuilder("[");
        StringBuilder k4b = new StringBuilder("[");
        StringBuilder k4c = new StringBuilder("[");
        StringBuilder k4d = new StringBuilder("[");
        StringBuilder k5 = new StringBuilder("[");
        StringBuilder k6 = new StringBuilder("[");

        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 15) continue;
            String namaSmf = r[1] != null ? r[1].toString().trim() : "";
            if (namaSmf.equalsIgnoreCase("TOTAL") || namaSmf.equalsIgnoreCase("JUMLAH")) continue;

            if (!first) {
                smf.append(","); totalKasus.append(",");
                k0.append(","); k1.append(","); k2a.append(","); k2b.append(","); k2c.append(",");
                k3a.append(","); k3b.append(","); k4a.append(","); k4b.append(","); k4c.append(","); k4d.append(",");
                k5.append(","); k6.append(",");
            }
            smf.append("\"").append(escape(namaSmf)).append("\"");
            totalKasus.append(toLong(r[2]));
            k0.append(toLong(r[3]));
            k1.append(toLong(r[4]));
            k2a.append(toLong(r[5]));
            k2b.append(toLong(r[6]));
            k2c.append(toLong(r[7]));
            k3a.append(toLong(r[8]));
            k3b.append(toLong(r[9]));
            k4a.append(toLong(r[10]));
            k4b.append(toLong(r[11]));
            k4c.append(toLong(r[12]));
            k4d.append(toLong(r[13]));
            k5.append(toLong(r[14]));
            k6.append(r.length > 15 ? toLong(r[15]) : 0);
            first = false;
        }

        smf.append("]"); totalKasus.append("]");
        k0.append("]"); k1.append("]"); k2a.append("]"); k2b.append("]"); k2c.append("]");
        k3a.append("]"); k3b.append("]"); k4a.append("]"); k4b.append("]"); k4c.append("]"); k4d.append("]");
        k5.append("]"); k6.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"smf\":").append(smf).append(",");
        json.append("\"total_kasus\":").append(totalKasus).append(",");
        json.append("\"kategori_0\":").append(k0).append(",");
        json.append("\"kategori_i\":").append(k1).append(",");
        json.append("\"kategori_iia\":").append(k2a).append(",");
        json.append("\"kategori_iib\":").append(k2b).append(",");
        json.append("\"kategori_iic\":").append(k2c).append(",");
        json.append("\"kategori_iiia\":").append(k3a).append(",");
        json.append("\"kategori_iiib\":").append(k3b).append(",");
        json.append("\"kategori_iva\":").append(k4a).append(",");
        json.append("\"kategori_ivb\":").append(k4b).append(",");
        json.append("\"kategori_ivc\":").append(k4c).append(",");
        json.append("\"kategori_ivd\":").append(k4d).append(",");
        json.append("\"kategori_v\":").append(k5).append(",");
        json.append("\"kategori_vi\":").append(k6);
        json.append("}");

        return postJson("/submit/3.19.4", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 5: RL 3.19.5 Pola Bakteri
    // =========================================================================
    public static BridgeResult kirim195(int tahun, List<Object[]> rows) {
        if (!FORM_TERVERIFIKASI.contains("3.19.5")) return belumDidukung("3.19.5");
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.5 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        StringBuilder bakteri = new StringBuilder("[");
        StringBuilder darah = new StringBuilder("[");
        StringBuilder urine = new StringBuilder("[");
        StringBuilder sputum = new StringBuilder("[");
        StringBuilder lainnya = new StringBuilder("[");

        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 6) continue;
            String namaBakteri = r[1] != null ? r[1].toString().trim() : "";
            if (namaBakteri.equalsIgnoreCase("TOTAL") || namaBakteri.equalsIgnoreCase("JUMLAH")) continue;

            if (!first) {
                bakteri.append(","); darah.append(","); urine.append(","); sputum.append(","); lainnya.append(",");
            }
            bakteri.append("\"").append(escape(namaBakteri)).append("\"");
            darah.append(toLong(r[2]));
            urine.append(toLong(r[3]));
            sputum.append(toLong(r[4]));
            lainnya.append(toLong(r[5]));
            first = false;
        }
        bakteri.append("]"); darah.append("]"); urine.append("]"); sputum.append("]"); lainnya.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"bakteri\":").append(bakteri).append(",");
        json.append("\"spesimen_darah\":").append(darah).append(",");
        json.append("\"spesimen_urine\":").append(urine).append(",");
        json.append("\"spesimen_sputum\":").append(sputum).append(",");
        json.append("\"spesimen_lainnya\":").append(lainnya);
        json.append("}");

        return postJson("/submit/3.19.5", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 6: RL 3.19.6 Kepekaan Bakteri
    // =========================================================================
    public static BridgeResult kirim196(int tahun, List<Object[]> rows) {
        if (!FORM_TERVERIFIKASI.contains("3.19.6")) return belumDidukung("3.19.6");
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.6 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        StringBuilder spesimen = new StringBuilder("[");
        StringBuilder bakteri = new StringBuilder("[");
        StringBuilder jumlahIsolat = new StringBuilder("[");

        String[] abNames = {"ctx","ctr","caz","fep","amc","sam","ipm","mem","gen","amk",
                            "cip","lvx","sxt","fos","pen","amp","ery","cli","van","lnz"};
        StringBuilder[] abBuilders = new StringBuilder[abNames.length];
        for (int i = 0; i < abBuilders.length; i++) {
            abBuilders[i] = new StringBuilder("[");
        }

        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 24) continue;
            String sp = r[1] != null ? r[1].toString().trim() : "";
            String bak = r[2] != null ? r[2].toString().trim() : "";
            long isolat = toLong(r[3]);

            if (!first) {
                spesimen.append(","); bakteri.append(","); jumlahIsolat.append(",");
                for (StringBuilder ab : abBuilders) ab.append(",");
            }
            spesimen.append("\"").append(escape(sp)).append("\"");
            bakteri.append("\"").append(escape(bak)).append("\"");
            jumlahIsolat.append(isolat);

            for (int a = 0; a < abNames.length; a++) {
                abBuilders[a].append(toDouble(r[4 + a]));
            }
            first = false;
        }

        spesimen.append("]"); bakteri.append("]"); jumlahIsolat.append("]");
        for (StringBuilder ab : abBuilders) ab.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"spesimen\":").append(spesimen).append(",");
        json.append("\"bakteri\":").append(bakteri).append(",");
        json.append("\"jumlah_isolat\":").append(jumlahIsolat);
        for (int a = 0; a < abNames.length; a++) {
            json.append(",\"").append(abNames[a]).append("\":").append(abBuilders[a]);
        }
        json.append("}");

        return postJson("/submit/3.19.6", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 7: RL 3.19.7 MDRO (Multidrug-Resistant Organisms)
    // =========================================================================
    public static BridgeResult kirim197(int tahun, List<Object[]> rows) {
        if (!FORM_TERVERIFIKASI.contains("3.19.7")) return belumDidukung("3.19.7");
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.7 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        StringBuilder isolat = new StringBuilder("[");
        StringBuilder darah = new StringBuilder("[");
        StringBuilder urine = new StringBuilder("[");
        StringBuilder sputum = new StringBuilder("[");
        StringBuilder lainnya = new StringBuilder("[");
        StringBuilder total = new StringBuilder("[");

        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 7) continue;
            String namaIsolat = r[2] != null ? r[2].toString().trim() : "";

            if (!first) {
                isolat.append(","); darah.append(","); urine.append(","); sputum.append(","); lainnya.append(","); total.append(",");
            }
            isolat.append("\"").append(escape(namaIsolat)).append("\"");
            darah.append(toLong(r[3]));
            urine.append(toLong(r[4]));
            sputum.append(toLong(r[5]));
            lainnya.append(toLong(r[6]));
            total.append(toLong(r[3]) + toLong(r[4]) + toLong(r[5]) + toLong(r[6]));
            first = false;
        }
        isolat.append("]"); darah.append("]"); urine.append("]"); sputum.append("]"); lainnya.append("]"); total.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"isolat\":").append(isolat).append(",");
        json.append("\"spesimen_darah\":").append(darah).append(",");
        json.append("\"spesimen_urine\":").append(urine).append(",");
        json.append("\"spesimen_sputum\":").append(sputum).append(",");
        json.append("\"spesimen_lainnya\":").append(lainnya).append(",");
        json.append("\"total\":").append(total);
        json.append("}");

        return postJson("/submit/3.19.7", json.toString());
    }

    // =========================================================================
    //  Sub-laporan 8: RL 3.19.8 Antibiotik Sistemik Empirik
    // =========================================================================
    public static BridgeResult kirim198(int tahun, List<Object[]> rows) {
        if (!FORM_TERVERIFIKASI.contains("3.19.8")) return belumDidukung("3.19.8");
        if (rows == null || rows.isEmpty()) {
            return new BridgeResult(false, "Data RL 3.19.8 kosong", "Silakan klik Tampilkan terlebih dahulu.");
        }
        StringBuilder diagnosa = new StringBuilder("[");
        StringBuilder namaAb = new StringBuilder("[");
        StringBuilder jumlahKasus = new StringBuilder("[");
        StringBuilder kepatuhan = new StringBuilder("[");

        boolean first = true;
        for (Object[] r : rows) {
            if (r == null || r.length < 5) continue;
            String diag = r[1] != null ? r[1].toString().trim() : "";
            String ab = r[2] != null ? r[2].toString().trim() : "";
            long kasus = toLong(r[3]);
            long patuh = toLong(r[4]);

            if (!first) {
                diagnosa.append(","); namaAb.append(","); jumlahKasus.append(","); kepatuhan.append(",");
            }
            diagnosa.append("\"").append(escape(diag)).append("\"");
            namaAb.append("\"").append(escape(ab)).append("\"");
            jumlahKasus.append(kasus);
            kepatuhan.append(patuh);
            first = false;
        }
        diagnosa.append("]"); namaAb.append("]"); jumlahKasus.append("]"); kepatuhan.append("]");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":\"").append(tahun).append("\",");
        json.append("\"diagnosa\":").append(diagnosa).append(",");
        json.append("\"nama_antibiotik\":").append(namaAb).append(",");
        json.append("\"jumlah_kasus\":").append(jumlahKasus).append(",");
        json.append("\"kepatuhan\":").append(kepatuhan);
        json.append("}");

        return postJson("/submit/3.19.8", json.toString());
    }
}
