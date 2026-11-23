package bridging;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import laporan.sirs.SirsResult;

/**
 * Bridge pengiriman laporan SIRS Online 6.3 Kemenkes RI
 * Terhubung langsung ke Backend Rust simrscb-backend (Port 8080).
 */
public class Sirs6BotBridge {

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
        String envUrl = System.getenv("SIRS6_BACKEND_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim().replaceAll("/+$", "");
        }
        try {
            java.io.File f = new java.io.File("setting/database.xml");
            if (f.exists()) {
                java.util.Properties prop = new java.util.Properties();
                try (java.io.FileInputStream fis = new java.io.FileInputStream(f)) {
                    prop.loadFromXML(fis);
                    String val = prop.getProperty("URLSIRS6BOT");
                    if (val != null && !val.trim().isEmpty()) {
                        return val.trim().replaceAll("/+$", "");
                    }
                }
            }
        } catch (Exception ignored) {}
        return "http://localhost:8080/api/sirs/bot";
    }

    private static long toLong(Object val) {
        if (val == null) return 0;
        String s = val.toString().trim().replace(",", "").replace(".", "");
        if (s.isEmpty()) return 0;
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static final String[][] RL41_UMUR_FIELDS = {
        {"jmlhPasHidupMatiUmurGen01JamL", "jmlhPasHidupMatiUmurGen01JamP"},
        {"jmlhPasHidupMatiUmurGen123JamL", "jmlhPasHidupMatiUmurGen123JamP"},
        {"jmlhPasHidupMatiUmurGen17hrL", "jmlhPasHidupMatiUmurGen17hrP"},
        {"jmlhPasHidupMatiUmurGen828hrL", "jmlhPasHidupMatiUmurGen828hrP"},
        {"jmlhPasHidupMatiUmurGen29hr3blnL", "jmlhPasHidupMatiUmurGen29hr3blnP"},
        {"jmlhPasHidupMatiUmurGen36blnL", "jmlhPasHidupMatiUmurGen36blnP"},
        {"jmlhPasHidupMatiUmurGen611blnL", "jmlhPasHidupMatiUmurGen611blnP"},
        {"jmlhPasHidupMatiUmurGen14thL", "jmlhPasHidupMatiUmurGen14thP"},
        {"jmlhPasHidupMatiUmurGen59thL", "jmlhPasHidupMatiUmurGen59thP"},
        {"jmlhPasHidupMatiUmurGen1014thL", "jmlhPasHidupMatiUmurGen1014thP"},
        {"jmlhPasHidupMatiUmurGen1519thL", "jmlhPasHidupMatiUmurGen1519thP"},
        {"jmlhPasHidupMatiUmurGen2024thL", "jmlhPasHidupMatiUmurGen2024thP"},
        {"jmlhPasHidupMatiUmurGen2529thL", "jmlhPasHidupMatiUmurGen2529thP"},
        {"jmlhPasHidupMatiUmurGen3034thL", "jmlhPasHidupMatiUmurGen3034thP"},
        {"jmlhPasHidupMatiUmurGen3539thL", "jmlhPasHidupMatiUmurGen3539thP"},
        {"jmlhPasHidupMatiUmurGen4044thL", "jmlhPasHidupMatiUmurGen4044thP"},
        {"jmlhPasHidupMatiUmurGen4549thL", "jmlhPasHidupMatiUmurGen4549thP"},
        {"jmlhPasHidupMatiUmurGen5054thL", "jmlhPasHidupMatiUmurGen5054thP"},
        {"jmlhPasHidupMatiUmurGen5559thL", "jmlhPasHidupMatiUmurGen5559thP"},
        {"jmlhPasHidupMatiUmurGen6064thL", "jmlhPasHidupMatiUmurGen6064thP"},
        {"jmlhPasHidupMatiUmurGen6569thL", "jmlhPasHidupMatiUmurGen6569thP"},
        {"jmlhPasHidupMatiUmurGen7074thL", "jmlhPasHidupMatiUmurGen7074thP"},
        {"jmlhPasHidupMatiUmurGen7579thL", "jmlhPasHidupMatiUmurGen7579thP"},
        {"jmlhPasHidupMatiUmurGen8084thL", "jmlhPasHidupMatiUmurGen8084thP"},
        {"jmlhPasHidupMatiUmurGenLebih85thL", "jmlhPasHidupMatiUmurGenLebih85thP"}
    };

    private static final String[][] RL51_UMUR_FIELDS = {
        {"jumlah_L_dibawah_1_jam", "jumlah_P_dibawah_1_jam"},
        {"jumlah_L_1_sampai_23_jam", "jumlah_P_1_sampai_23_jam"},
        {"jumlah_L_1_sampai_7_hari", "jumlah_P_1_sampai_7_hari"},
        {"jumlah_L_8_sampai_28_hari", "jumlah_P_8_sampai_28_hari"},
        {"jumlah_L_29_hari_sampai_dibawah_3_bulan", "jumlah_P_29_hari_sampai_dibawah_3_bulan"},
        {"jumlah_L_3_bulan_sampai_dibawah_6_bulan", "jumlah_P_3_bulan_sampai_dibawah_6_bulan"},
        {"jumlah_L_6_bulan_sampai_11_bulan", "jumlah_P_6_bulan_sampai_11_bulan"},
        {"jumlah_L_1_sampai_4_tahun", "jumlah_P_1_sampai_4_tahun"},
        {"jumlah_L_5_sampai_9_tahun", "jumlah_P_5_sampai_9_tahun"},
        {"jumlah_L_10_sampai_14_tahun", "jumlah_P_10_sampai_14_tahun"},
        {"jumlah_L_15_sampai_19_tahun", "jumlah_P_15_sampai_19_tahun"},
        {"jumlah_L_20_sampai_24_tahun", "jumlah_P_20_sampai_24_tahun"},
        {"jumlah_L_25_sampai_29_tahun", "jumlah_P_25_sampai_29_tahun"},
        {"jumlah_L_30_sampai_34_tahun", "jumlah_P_30_sampai_34_tahun"},
        {"jumlah_L_35_sampai_39_tahun", "jumlah_P_35_sampai_39_tahun"},
        {"jumlah_L_40_sampai_44_tahun", "jumlah_P_40_sampai_44_tahun"},
        {"jumlah_L_45_sampai_49_tahun", "jumlah_P_45_sampai_49_tahun"},
        {"jumlah_L_50_sampai_54_tahun", "jumlah_P_50_sampai_54_tahun"},
        {"jumlah_L_55_sampai_59_tahun", "jumlah_P_55_sampai_59_tahun"},
        {"jumlah_L_60_sampai_64_tahun", "jumlah_P_60_sampai_64_tahun"},
        {"jumlah_L_65_sampai_69_tahun", "jumlah_P_65_sampai_69_tahun"},
        {"jumlah_L_70_sampai_74_tahun", "jumlah_P_70_sampai_74_tahun"},
        {"jumlah_L_75_sampai_79_tahun", "jumlah_P_75_sampai_79_tahun"},
        {"jumlah_L_80_sampai_84_tahun", "jumlah_P_80_sampai_84_tahun"},
        {"jumlah_L_diatas_85_tahun", "jumlah_P_diatas_85_tahun"}
    };

    /**
     * Mengirim data laporan ke backend SIRS Bot
     */
    public static BridgeResult kirimLaporan(String kodeForm, int tahun, String tahunBulan, SirsResult hasil) {
        if (hasil == null || hasil.rows == null || hasil.rows.isEmpty()) {
            return new BridgeResult(false, "Tidak ada data untuk dikirim", "Tabel data kosong. Silakan klik Tampilkan terlebih dahulu.");
        }

        String form = kodeForm.toLowerCase().replace(".", "");
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"tahun\":").append(tahun).append(",");
        json.append("\"tahunDanBulan\":\"").append(tahunBulan).append("\",");
        int bulan = 1;
        if (tahunBulan != null && tahunBulan.length() >= 7) {
            try {
                bulan = Integer.parseInt(tahunBulan.substring(5, 7));
            } catch (Exception ignored) {}
        }
        json.append("\"bulan\":").append(bulan).append(",");
        json.append("\"periodeTahun\":").append(tahun).append(",");
        json.append("\"periodeBulan\":").append(bulan).append(",");

        if (form.equals("32") || form.equals("rl32")) {
            form = "rl32";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 20) continue;
                String jenis = r[1] != null ? r[1].toString().trim() : "";
                if (jenis.equalsIgnoreCase("TOTAL") || jenis.equalsIgnoreCase("JUMLAH")) {
                    continue;
                }

                int idPelayanan = i + 1;
                long awal = toLong(r[2]);
                long masuk = toLong(r[3]);
                long pindahan = toLong(r[4]);
                long dipindahkan = toLong(r[5]);
                long keluarHidup = toLong(r[6]);
                long matiL1 = toLong(r[7]);
                long matiL2 = toLong(r[8]);
                long matiP1 = toLong(r[9]);
                long matiP2 = toLong(r[10]);
                long lamaDirawat = toLong(r[11]);
                long akhir = toLong(r[12]);
                long totalHp = toLong(r[13]);
                long vvip = toLong(r[14]);
                long vip = toLong(r[15]);
                long k1 = toLong(r[16]);
                long k2 = toLong(r[17]);
                long k3 = toLong(r[18]);
                long khusus = toLong(r[19]);
                long tt = r.length > 20 ? toLong(r[20]) : 0;

                long totalAktivitas = awal + masuk + pindahan + keluarHidup + dipindahkan + totalHp + lamaDirawat;
                if (totalAktivitas == 0 && tt == 0) {
                    continue;
                }

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenis_pelayanan_id\":").append(idPelayanan).append(",");
                json.append("\"alokasi_tempat_tidur\":").append(tt).append(",");
                json.append("\"pasien_awal_bulan\":").append(awal).append(",");
                json.append("\"pasien_masuk\":").append(masuk).append(",");
                json.append("\"pasien_pindahan\":").append(pindahan).append(",");
                json.append("\"pasien_dipindahkan\":").append(dipindahkan).append(",");
                json.append("\"pasien_keluar_hidup\":").append(keluarHidup).append(",");
                json.append("\"pasien_keluar_mati_kurang_48_l\":").append(matiL1).append(",");
                json.append("\"pasien_keluar_mati_lebih_48_l\":").append(matiL2).append(",");
                json.append("\"pasien_keluar_mati_kurang_48_p\":").append(matiP1).append(",");
                json.append("\"pasien_keluar_mati_lebih_48_p\":").append(matiP2).append(",");
                json.append("\"pasien_akhir_bulan\":").append(akhir).append(",");
                json.append("\"jumlah_lama_dirawat\":").append(lamaDirawat).append(",");
                json.append("\"jumlah_hari_perawatan\":").append(totalHp).append(",");
                json.append("\"rincian_hari_perawatan_vvip\":").append(vvip).append(",");
                json.append("\"rincian_hari_perawatan_vip\":").append(vip).append(",");
                json.append("\"rincian_hari_perawatan_1\":").append(k1).append(",");
                json.append("\"rincian_hari_perawatan_2\":").append(k2).append(",");
                json.append("\"rincian_hari_perawatan_3\":").append(k3).append(",");
                json.append("\"rincian_hari_perawatan_khusus\":").append(khusus);
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("33") || form.equals("rl33")) {
            form = "rl33";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 11) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99") || no.equals("66") || no.equals("77")) continue;

                int id = i + 1;
                long rujukan = toLong(r[2]);
                long nonRujukan = toLong(r[3]);
                long dirawat = toLong(r[4]);
                long dirujuk = toLong(r[5]);
                long pulang = toLong(r[6]);
                long mati = toLong(r[7]);
                long doa = toLong(r[8]);
                long luka = toLong(r[9]);
                long fem = r.length > 10 ? toLong(r[10]) : 0;

                long total = rujukan + nonRujukan + dirawat + dirujuk + pulang + mati + doa;
                if (total == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenis_pelayanan_id\":").append(id).append(",");
                json.append("\"total_pasien_rujukan\":").append(rujukan).append(",");
                json.append("\"total_pasien_non_rujukan\":").append(nonRujukan).append(",");
                json.append("\"tindak_lanjut_dirawat\":").append(dirawat).append(",");
                json.append("\"tindak_lanjut_dirujuk\":").append(dirujuk).append(",");
                json.append("\"tindak_lanjut_pulang\":").append(pulang).append(",");
                json.append("\"mati_di_igd\":").append(mati).append(",");
                json.append("\"doa\":").append(doa).append(",");
                json.append("\"luka_luka\":").append(luka).append(",");
                json.append("\"false_emergency\":").append(fem);
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("34") || form.equals("rl34")) {
            form = "rl34";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 3) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;

                int id = 1;
                try {
                    id = Integer.parseInt(no);
                } catch (Exception e) {
                    id = i + 1;
                }
                long jumlah = toLong(r[2]);

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenis_pengunjung_id\":").append(id).append(",");
                json.append("\"jumlah\":").append(jumlah);
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("35") || form.equals("rl35")) {
            form = "rl35";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 7) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99") || no.equals("66") || no.equals("77")) continue;

                int id = i + 1;
                long dlm_l = toLong(r[2]);
                long dlm_p = toLong(r[3]);
                long luar_l = toLong(r[4]);
                long luar_p = toLong(r[5]);
                long total = toLong(r[6]);

                if (total == 0 && (dlm_l + dlm_p + luar_l + luar_p) == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenis_kegiatan_id\":").append(id).append(",");
                json.append("\"kunjungan_pasien_dalam_kabkota_laki\":").append(dlm_l).append(",");
                json.append("\"kunjungan_pasien_dalam_kabkota_perempuan\":").append(dlm_p).append(",");
                json.append("\"kunjungan_pasien_luar_kabkota_laki\":").append(luar_l).append(",");
                json.append("\"kunjungan_pasien_luar_kabkota_perempuan\":").append(luar_p).append(",");
                json.append("\"total_kunjungan\":").append(total);
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("36") || form.equals("rl36")) {
            form = "rl36";
            json.append("\"data\":[");
            boolean first = true;
            int idCounter = 1;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 17) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")
                    || no.equals("3") || no.equals("4") || no.equals("5") || no.equals("7") || no.equals("8")) {
                    continue;
                }
                String valCheck = r[2] != null ? r[2].toString().trim() : "";
                if (valCheck.isEmpty()) {
                    continue;
                }

                int id = idCounter++;
                long rmRs = toLong(r[2]);
                long rmPusk = toLong(r[3]);
                long rmBidan = toLong(r[4]);
                long rmKesmas = toLong(r[5]);
                long rmLainnya = toLong(r[6]);
                long rmFaskesLainnya = rmKesmas + rmLainnya;
                long rmHidup = toLong(r[7]);
                long rmMati = toLong(r[8]);
                long rmTotal = toLong(r[9]);
                long rnmHidup = toLong(r[10]);
                long rnmMati = toLong(r[11]);
                long rnmTotal = toLong(r[12]);
                long nrHidup = toLong(r[13]);
                long nrMati = toLong(r[14]);
                long nrTotal = toLong(r[15]);
                long dirujuk = toLong(r[16]);

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenis_kegiatan_id\":").append(id).append(",");
                json.append("\"rm_rumah_sakit\":").append(rmRs).append(",");
                json.append("\"rm_bidan\":").append(rmBidan).append(",");
                json.append("\"rm_puskesmas\":").append(rmPusk).append(",");
                json.append("\"rm_faskes_lainnya\":").append(rmFaskesLainnya).append(",");
                json.append("\"rm_hidup\":").append(rmHidup).append(",");
                json.append("\"rm_mati\":").append(rmMati).append(",");
                json.append("\"rm_total\":").append(rmTotal).append(",");
                json.append("\"rnm_hidup\":").append(rnmHidup).append(",");
                json.append("\"rnm_mati\":").append(rnmMati).append(",");
                json.append("\"rnm_total\":").append(rnmTotal).append(",");
                json.append("\"nr_hidup\":").append(nrHidup).append(",");
                json.append("\"nr_mati\":").append(nrMati).append(",");
                json.append("\"nr_total\":").append(nrTotal).append(",");
                json.append("\"dirujuk\":").append(dirujuk);
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("37") || form.equals("rl37")) {
            form = "rl37";
            json.append("\"data\":[");
            boolean first = true;
            int idCounter = 1;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 4) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                String jmlStr = r[3] != null ? r[3].toString().trim() : "";
                if (jmlStr.isEmpty() || jmlStr.equals("-")) {
                    if (no.equals("1") || no.equals("1.1") || no.equals("1.2") || no.equals("1.3")
                        || no.equals("2") || no.equals("3") || no.equals("4") || no.equals("8")
                        || no.equals("9") || no.equals("11") || no.equals("12") || no.equalsIgnoreCase("TOTAL")) {
                        continue;
                    }
                }

                int id = idCounter++;
                long jumlah = toLong(r[3]);
                boolean isMati = no.equals("2.1") || no.equals("2.2") || no.equals("3.1") || no.equals("3.2");

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenis_kegiatan_id\":").append(id).append(",");
                json.append("\"rm_rumah_sakit\":0,");
                json.append("\"rm_bidan\":0,");
                json.append("\"rm_puskesmas\":0,");
                json.append("\"rm_faskes_lainnya\":0,");
                json.append("\"rm_hidup\":0,");
                json.append("\"rm_mati\":0,");
                json.append("\"rm_total\":0,");
                json.append("\"rnm_hidup\":0,");
                json.append("\"rnm_mati\":0,");
                json.append("\"rnm_total\":0,");
                json.append("\"nr_hidup\":").append(isMati ? 0 : jumlah).append(",");
                json.append("\"nr_mati\":").append(isMati ? jumlah : 0).append(",");
                json.append("\"nr_total\":").append(jumlah).append(",");
                json.append("\"dirujuk\":0");
                json.append("}");
            }
        } else if (form.equals("38") || form.equals("rl38")) {
            form = "rl38";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 6) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("A") || no.equals("B") || no.equals("C") || no.equals("D")) {
                    continue;
                }

                long jl = toLong(r[2]);
                long jp = toLong(r[3]);
                String rataL = r[4] != null ? r[4].toString().trim().replace(",", ".") : "0.00";
                String rataP = r[5] != null ? r[5].toString().trim().replace(",", ".") : "0.00";
                if (rataL.isEmpty()) rataL = "0.00";
                if (rataP.isEmpty()) rataP = "0.00";

                if (jl == 0 && jp == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"kode_pemeriksaan\":\"").append(no).append("\",");
                json.append("\"jumlah_laki\":").append(jl).append(",");
                json.append("\"jumlah_perempuan\":").append(jp).append(",");
                json.append("\"rata_laki\":\"").append(rataL).append("\",");
                json.append("\"rata_perempuan\":\"").append(rataP).append("\"");
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("39") || form.equals("rl39")) {
            form = "rl39";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 3) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equals("1") || no.equals("2") || no.equals("3") || no.equals("4") || no.equalsIgnoreCase("TOTAL") || no.equals("99")) {
                    continue;
                }

                long jumlah = toLong(r[2]);

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"kode_kegiatan\":\"").append(no).append("\",");
                json.append("\"jumlah\":").append(jumlah);
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("310") || form.equals("rl310")) {
            form = "rl310";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 14) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                try {
                    id = Integer.parseInt(no);
                } catch (Exception e) {
                    continue;
                }
                if (id < 1 || id > 20) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenisSpesialisTigaTitikSepuluhId\":").append(id).append(",");
                json.append("\"rm_diterima_puskesmas\":").append(toLong(r[2])).append(",");
                json.append("\"rm_diterima_rs\":").append(toLong(r[3])).append(",");
                json.append("\"rm_diterima_faskes_lain\":").append(toLong(r[4])).append(",");
                json.append("\"rm_diterima_total_rm\":").append(toLong(r[5])).append(",");
                json.append("\"rm_dikembalikan_puskesmas\":").append(toLong(r[6])).append(",");
                json.append("\"rm_dikembalikan_rs\":").append(toLong(r[7])).append(",");
                json.append("\"rm_dikembalikan_faskes_lain\":").append(toLong(r[8])).append(",");
                json.append("\"rm_dikembalikan_total_rm\":").append(toLong(r[9])).append(",");
                json.append("\"keluar_pasien_rujukan\":").append(toLong(r[10])).append(",");
                json.append("\"keluar_pasien_datang_sendiri\":").append(toLong(r[11])).append(",");
                json.append("\"keluar_total_keluar\":").append(toLong(r[12])).append(",");
                json.append("\"keluar_diterima_kembali\":").append(toLong(r[13]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("311") || form.equals("rl311")) {
            form = "rl311";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 4) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                try {
                    id = Integer.parseInt(no);
                } catch (Exception e) {
                    continue;
                }
                if (id < 1 || id > 17) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenisKegiatanId\":").append(id).append(",");
                json.append("\"jumlah\":").append(toLong(r[3]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("312") || form.equals("rl312")) {
            form = "rl312";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 7) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                try {
                    id = Integer.parseInt(no);
                } catch (Exception e) {
                    continue;
                }
                if (id < 1 || id > 16) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"SpesialisasiId\":").append(id).append(",");
                json.append("\"Khusus\":").append(toLong(r[2])).append(",");
                json.append("\"Besar\":").append(toLong(r[3])).append(",");
                json.append("\"Sedang\":").append(toLong(r[4])).append(",");
                json.append("\"Kecil\":").append(toLong(r[5]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("313") || form.equals("rl313")) {
            form = "rl313";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 3) continue;
                String kode = r[0] != null ? r[0].toString().trim() : "";
                int id = mapRl313Id(kode);
                if (id == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"rl_tiga_titik_tiga_belas_jenis_tindakan_id\":").append(id).append(",");
                json.append("\"jumlah\":").append(toLong(r[2]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("314") || form.equals("rl314")) {
            form = "rl314";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 3) continue;
                String kode = r[0] != null ? r[0].toString().trim() : "";
                int id = mapRl314Id(kode);
                if (id == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"rLTigaTitikEmpatBelasJenisKegiatanId\":").append(id).append(",");
                json.append("\"jumlah\":").append(toLong(r[2]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("315") || form.equals("rl315")) {
            form = "rl315";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 5) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                try {
                    id = Integer.parseInt(no);
                } catch (Exception e) {
                    continue;
                }
                if (id < 1 || id > 8) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"jenisKegiatanTigaTitikLimaBelasId\":").append(id).append(",");
                json.append("\"laki\":").append(toLong(r[2])).append(",");
                json.append("\"perempuan\":").append(toLong(r[3])).append(",");
                json.append("\"jumlah\":").append(toLong(r[4]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("316") || form.equals("rl316")) {
            form = "rl316";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 10) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                try {
                    id = Integer.parseInt(no);
                } catch (Exception e) {
                    continue;
                }
                if (id < 1 || id > 8) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"JenisPelayananKeluargaBerencanaId\":").append(id).append(",");
                json.append("\"pelayananKbPaskaPersalinan\":").append(toLong(r[2])).append(",");
                json.append("\"pelayananKbPaskaKeguguran\":").append(toLong(r[3])).append(",");
                json.append("\"pelayananKbInterval\":").append(toLong(r[4])).append(",");
                json.append("\"komplikasiKB\":").append(toLong(r[6])).append(",");
                json.append("\"kegagalanKB\":").append(toLong(r[7])).append(",");
                json.append("\"efekSamping\":").append(toLong(r[8])).append(",");
                json.append("\"dropOut\":").append(toLong(r[9]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("317") || form.equals("rl317")) {
            form = "rl317";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 3) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                if (no.equals("1")) id = 1;
                else if (no.equals("2")) id = 3;
                else if (no.equals("3")) id = 4;
                if (id == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"golonganObatId\":").append(id).append(",");
                json.append("\"jumlahItemObat\":").append(toLong(r[2])).append(",");
                json.append("\"jumlahItemObatRs\":").append(r.length > 3 ? toLong(r[3]) : toLong(r[2]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("318") || form.equals("rl318")) {
            form = "rl318";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 5) continue;
                String no = r[0] != null ? r[0].toString().trim() : "";
                if (no.isEmpty() || no.equalsIgnoreCase("TOTAL") || no.equals("99")) continue;
                int id = 0;
                if (no.equals("1")) id = 1;
                else if (no.equals("2")) id = 3;
                else if (no.equals("3")) id = 4;
                if (id == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"golonganObatId\":").append(id).append(",");
                json.append("\"rawatJalan\":").append(toLong(r[2])).append(",");
                json.append("\"igd\":").append(toLong(r[3])).append(",");
                json.append("\"rawatInap\":").append(toLong(r[4]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("319") || form.equals("rl319")) {
            form = "rl319";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 8) continue;
                String kode = r[0] != null ? r[0].toString().trim() : "";
                int id = mapRl319Id(kode);
                if (id == 0) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"golonganObatTigaTitikSembilanBelasId\":").append(id).append(",");
                json.append("\"ranap_pasien_keluar\":").append(toLong(r[2])).append(",");
                json.append("\"ranap_lama_dirawat\":").append(toLong(r[3])).append(",");
                json.append("\"jumlah_pasien_rajal\":").append(toLong(r[4])).append(",");
                json.append("\"rajal_lab\":").append(toLong(r[5])).append(",");
                json.append("\"rajal_radiologi\":").append(toLong(r[6])).append(",");
                json.append("\"rajal_lain_lain\":").append(toLong(r[7]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("41") || form.equals("rl41") || form.equals("4.1")) {
            form = "rl41";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 59) continue;
                String icd = r[1] != null ? r[1].toString().trim() : "";
                if (icd.isEmpty()) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"icd_code\":\"").append(icd.replace("\"", "\\\"")).append("\",");
                for (int u = 0; u < 25; u++) {
                    json.append("\"").append(RL41_UMUR_FIELDS[u][0]).append("\":").append(toLong(r[3 + u * 2])).append(",");
                    json.append("\"").append(RL41_UMUR_FIELDS[u][1]).append("\":").append(toLong(r[4 + u * 2])).append(",");
                }
                json.append("\"jmlhPasKeluarMatiGenL\":").append(toLong(r[56])).append(",");
                json.append("\"jmlhPasKeluarMatiGenP\":").append(toLong(r[57]));
                json.append("}");
            }
            json.append("]");
        } else if (form.equals("51") || form.equals("rl51") || form.equals("5.1")) {
            form = "rl51";
            json.append("\"data\":[");
            boolean first = true;
            for (int i = 0; i < hasil.rows.size(); i++) {
                Object[] r = hasil.rows.get(i);
                if (r == null || r.length < 59) continue;
                String icd = r[1] != null ? r[1].toString().trim() : "";
                if (icd.isEmpty()) continue;

                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"icd_code\":\"").append(icd.replace("\"", "\\\"")).append("\",");
                long sumL = 0;
                long sumP = 0;
                for (int u = 0; u < 25; u++) {
                    long valL = toLong(r[3 + u * 2]);
                    long valP = toLong(r[4 + u * 2]);
                    sumL += valL;
                    sumP += valP;
                    json.append("\"").append(RL51_UMUR_FIELDS[u][0]).append("\":").append(valL).append(",");
                    json.append("\"").append(RL51_UMUR_FIELDS[u][1]).append("\":").append(valP).append(",");
                }
                long kunjL = Math.max(toLong(r[56]), sumL);
                long kunjP = Math.max(toLong(r[57]), sumP);
                json.append("\"jumlah_kunjungan_L\":").append(kunjL).append(",");
                json.append("\"jumlah_kunjungan_P\":").append(kunjP);
                json.append("}");
            }
            json.append("]");
        } else {
            // Generic fallback untuk formulir lain: raw_rows
            json.append("\"raw_rows\":[");
            for (int i = 0; i < hasil.rows.size(); i++) {
                if (i > 0) json.append(",");
                json.append("[");
                Object[] row = hasil.rows.get(i);
                for (int j = 0; j < row.length; j++) {
                    if (j > 0) json.append(",");
                    Object val = row[j];
                    if (val == null) {
                        json.append("null");
                    } else if (val instanceof Number) {
                        json.append(val);
                    } else {
                        json.append("\"").append(val.toString().replace("\"", "\\\"")).append("\"");
                    }
                }
                json.append("]");
            }
            json.append("]");
        }

        json.append("}");

        // Eksekusi HTTP Request ke Backend Rust
        String targetUrl = getBaseUrl() + "/submit/" + form;
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);

            byte[] outBytes = json.toString().getBytes(StandardCharsets.UTF_8);
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
                return new BridgeResult(true, "Laporan berhasil dikirim ke Kemenkes RI!", respText);
            } else {
                return new BridgeResult(false, "Pengiriman laporan ditolak (HTTP " + code + ")", respText);
            }
        } catch (java.net.ConnectException ce) {
            return new BridgeResult(false, "Tidak dapat terhubung ke Backend SIRS",
                "Koneksi ke " + targetUrl + " gagal.\nPastikan service backend simrscb (port 8080) sudah berjalan.");
        } catch (Exception e) {
            return new BridgeResult(false, "Terjadi kesalahan koneksi", e.getMessage());
        }
    }

    private static int mapRl313Id(String kode) {
        if (kode == null) return 0;
        switch (kode.trim()) {
            case "1.1": return 180;
            case "1.2": return 181;
            case "1.3": return 182;
            case "1.4": return 183;
            case "1.5": return 184;
            case "1.6": return 185;
            case "1.7": return 186;
            case "1.8": return 187;
            case "1.9": return 188;
            case "1.10": return 189;
            case "2.1": return 190;
            case "2.2": return 191;
            case "2.3": return 192;
            case "2.4": return 193;
            case "2.5": return 194;
            case "2.6": return 195;
            case "2.7": return 196;
            case "3.1": return 197;
            case "3.2": return 198;
            case "3.3": return 199;
            case "3.4": return 200;
            case "3.5": return 201;
            case "3.6": return 202;
            case "3.7": return 203;
            case "3.8": return 204;
            case "3.9": return 205;
            case "3.10": return 206;
            case "4.1": return 207;
            case "4.2": return 208;
            case "4.3": return 209;
            case "4.4": return 210;
            case "4.5": return 211;
            case "5.1": return 212;
            case "5.2": return 213;
            case "5.3": return 214;
            case "6.1": return 215;
            case "6.2": return 216;
            case "6.3": return 217;
            case "6.4": return 218;
            case "7.1": return 219;
            case "7.2": return 220;
            case "7.3": return 221;
            case "88": return 222;
            default: return 0;
        }
    }

    private static int mapRl314Id(String kode) {
        if (kode == null) return 0;
        switch (kode.trim()) {
            case "1": return 1;
            case "2": return 2;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 7;
            case "8": return 8;
            case "9": return 9;
            case "10": return 10;
            case "11": return 11;
            case "12": return 12;
            case "13": return 13;
            case "14": return 14;
            case "15": return 15;
            case "16.1": return 16;
            case "16.2": return 17;
            case "16.3": return 18;
            case "16.4": return 19;
            case "16.5": return 20;
            case "17": return 21;
            case "18": return 22;
            default: return 0;
        }
    }

    private static int mapRl319Id(String kode) {
        if (kode == null) return 0;
        switch (kode.trim()) {
            case "1": return 1;
            case "2": return 2;
            case "2.1": return 3;
            case "2.2": return 4;
            case "2.3": return 5;
            case "2.4": return 6;
            case "3": return 7;
            case "4": return 8;
            case "4.1": return 9;
            case "4.2": return 10;
            case "4.3": return 11;
            default: return 0;
        }
    }
}
