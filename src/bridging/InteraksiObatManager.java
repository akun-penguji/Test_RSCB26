/*
 * Class untuk managing operasi database interaksi obat dengan cache system
 * Package: bridging
 * Author: System
 * Description: Class untuk mengelola operasi CRUD dan business logic interaksi obat dengan performa optimal
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.validasi;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class untuk mengelola operasi database dan business logic interaksi obat dengan cache system
 */
public class InteraksiObatManager {

    private Connection koneksi;
    private boolean databaseReady = false;
    
    // Cache system properties
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private FileReader myObj;
    private File cacheFile;
    private FileWriter fileWriter;
    private String jsonCache = "";
    private validasi Valid = new validasi();

    /**
     * Constructor
     *
     * @param koneksi koneksi database
     */
    public InteraksiObatManager(Connection koneksi) {
        this.koneksi = koneksi;
    }

    // =====================================================
    // CACHE SYSTEM METHODS
    // =====================================================
    
    /**
     * Load data interaksi dari cache atau database
     * Mirip seperti sistem DlgCariDokter
     */
    public boolean loadInteraksiData() {
        try {
            // Cek apakah cache masih fresh (kurang dari 7 hari)
            cacheFile = new File("./cache/interaksi_obat.iyem");
            
            if (Valid.daysOld("./cache/interaksi_obat.iyem") < 7) {
                System.out.println("📁 Loading interaksi obat dari cache...");
                return loadFromCache();
            } else {
                System.out.println("🔄 Cache expired, reload dari database...");
                return loadFromDatabase();
            }
        } catch (Exception e) {
            if (e.toString().contains("No such file or directory")) {
                System.out.println("📁 Cache belum ada, buat cache baru...");
                return loadFromDatabase();
            } else {
                System.out.println("❌ Error load cache: " + e.getMessage());
                return loadFromDatabase();
            }
        }
    }

    /**
     * Load data dari cache file
     */
    private boolean loadFromCache() {
        try {
            myObj = new FileReader("./cache/interaksi_obat.iyem");
            root = mapper.readTree(myObj);
            response = root.path("interaksi");
            
            int count = 0;
            if (response.isArray()) {
                for (JsonNode item : response) {
                    if ("AKTIF".equals(item.path("status").asText())) {
                        count++;
                    }
                }
            }
            
            myObj.close();
            
            if (count > 0) {
                databaseReady = true;
                System.out.println("✅ Cache loaded: " + count + " data interaksi aktif");
                return true;
            } else {
                System.out.println("⚠️ Cache kosong atau tidak ada data aktif");
                return loadFromDatabase();
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error baca cache: " + e.getMessage());
            return loadFromDatabase();
        }
    }

    /**
     * Load data dari database dan buat cache
     */
    private boolean loadFromDatabase() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Buat folder cache jika belum ada
            File cacheDir = new File("./cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            
            cacheFile = new File("./cache/interaksi_obat.iyem");
            cacheFile.createNewFile();
            fileWriter = new FileWriter(cacheFile);
            jsonCache = "";
            
            System.out.println("🔄 Loading data interaksi dari database...");
            
            String sql = "SELECT id, komposisi1, komposisi2, tingkat_interaksi, " +
                        "deskripsi_interaksi, rekomendasi, mekanisme, onset, status " +
                        "FROM interaksi_obat ORDER BY tingkat_interaksi, komposisi1";
            
            ps = koneksi.prepareStatement(sql);
            rs = ps.executeQuery();
            
            int count = 0;
            int aktifCount = 0;
            
            while (rs.next()) {
                count++;
                if ("AKTIF".equals(rs.getString("status"))) {
                    aktifCount++;
                }
                
                // Build JSON string
                jsonCache += "{" +
                    "\"id\":\"" + rs.getString("id") + "\"," +
                    "\"komposisi1\":\"" + rs.getString("komposisi1").replaceAll("\"", "") + "\"," +
                    "\"komposisi2\":\"" + rs.getString("komposisi2").replaceAll("\"", "") + "\"," +
                    "\"tingkat\":\"" + rs.getString("tingkat_interaksi") + "\"," +
                    "\"deskripsi\":\"" + rs.getString("deskripsi_interaksi").replaceAll("\"", "") + "\"," +
                    "\"rekomendasi\":\"" + rs.getString("rekomendasi").replaceAll("\"", "") + "\"," +
                    "\"mekanisme\":\"" + (rs.getString("mekanisme") != null ? rs.getString("mekanisme").replaceAll("\"", "") : "") + "\"," +
                    "\"onset\":\"" + (rs.getString("onset") != null ? rs.getString("onset") : "") + "\"," +
                    "\"status\":\"" + rs.getString("status") + "\"" +
                    "},";
            }
            
            if (count > 0) {
                // Write cache file
                String finalJson = "{\"interaksi\":[" + jsonCache.substring(0, jsonCache.length() - 1) + "]}";
                fileWriter.write(finalJson);
                fileWriter.flush();
                fileWriter.close();
                
                databaseReady = (aktifCount > 0);
                
                System.out.println("✅ Database loaded: " + count + " total, " + aktifCount + " aktif");
                System.out.println("📁 Cache file created: ./cache/interaksi_obat.iyem");
                
                return databaseReady;
            } else {
                System.out.println("⚠️ Tidak ada data interaksi di database");
                databaseReady = false;
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error load database: " + e.getMessage());
            databaseReady = false;
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error create cache: " + e.getMessage());
            databaseReady = false;
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (fileWriter != null) fileWriter.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Force refresh cache (panggil jika ada update data)
     */
    public boolean refreshCache() {
        try {
            File oldCache = new File("./cache/interaksi_obat.iyem");
            if (oldCache.exists()) {
                oldCache.delete();
            }
            System.out.println("🗑️ Cache lama dihapus, reload dari database...");
            return loadFromDatabase();
        } catch (Exception e) {
            System.out.println("❌ Error refresh cache: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // DATABASE READY METHODS
    // =====================================================
    
    /**
     * Method untuk mengecek apakah database interaksi obat sudah ready
     *
     * @return true jika database ready, false jika belum
     */
    public boolean isDatabaseReady() {
        return databaseReady;
    }

    /**
     * Method untuk set status database ready
     *
     * @param ready status ready
     */
    public void setDatabaseReady(boolean ready) {
        this.databaseReady = ready;
    }

    /**
     * Method untuk cek dan inisialisasi database dengan cache system
     *
     * @return true jika berhasil, false jika gagal
     */
    public boolean initializeDatabase() {
        System.out.println("🚀 Inisialisasi database interaksi dengan cache system...");
        
        if (koneksi == null) {
            System.out.println("❌ Koneksi database null");
            databaseReady = false;
            return false;
        }
        
        try {
            if (koneksi.isClosed()) {
                System.out.println("❌ Koneksi database tertutup");
                databaseReady = false;
                return false;
            }
            
            // Load data menggunakan cache system
            return loadInteraksiData();
            
        } catch (SQLException e) {
            System.out.println("❌ Error cek koneksi: " + e.getMessage());
            databaseReady = false;
            return false;
        }
    }

    // =====================================================
    // MAIN SEARCH METHODS (WITH CACHE OPTIMIZATION)
    // =====================================================
    
    /**
     * Cek interaksi menggunakan cache (lebih cepat)
     */
    public InteraksiObat cekInteraksiCache(String komposisi1, String komposisi2) {
        if (komposisi1 == null || komposisi2 == null || 
            komposisi1.trim().isEmpty() || komposisi2.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Load dari cache jika belum ready
            if (!databaseReady) {
                loadInteraksiData();
            }
            
            myObj = new FileReader("./cache/interaksi_obat.iyem");
            root = mapper.readTree(myObj);
            response = root.path("interaksi");
            
            String komp1Upper = komposisi1.toUpperCase().trim();
            String komp2Upper = komposisi2.toUpperCase().trim();
            
            InteraksiObat result = null;
            int prioritas = 5; // 1=KONTRAINDIKASI, 2=BERAT, 3=SEDANG, 4=RINGAN
            
            if (response.isArray()) {
                for (JsonNode item : response) {
                    if (!"AKTIF".equals(item.path("status").asText())) {
                        continue;
                    }
                    
                    String itemKomp1 = item.path("komposisi1").asText().toUpperCase();
                    String itemKomp2 = item.path("komposisi2").asText().toUpperCase();
                    
                    // Cek kombinasi A+B atau B+A
                    if ((itemKomp1.equals(komp1Upper) && itemKomp2.equals(komp2Upper)) ||
                        (itemKomp1.equals(komp2Upper) && itemKomp2.equals(komp1Upper))) {
                        
                        String tingkat = item.path("tingkat").asText();
                        int currentPrioritas = getTingkatPrioritas(tingkat);
                        
                        // Ambil yang prioritas tertinggi (angka terkecil)
                        if (currentPrioritas < prioritas) {
                            prioritas = currentPrioritas;
                            result = new InteraksiObat(
                                item.path("komposisi1").asText(),
                                item.path("komposisi2").asText(),
                                tingkat,
                                item.path("deskripsi").asText(),
                                item.path("rekomendasi").asText(),
                                item.path("mekanisme").asText(),
                                item.path("onset").asText()
                            );
                        }
                    }
                }
            }
            
            myObj.close();
            return result;
            
        } catch (Exception e) {
            System.out.println("❌ Error cek interaksi cache: " + e.getMessage());
            // Fallback ke database
            return cekInteraksi(komposisi1, komposisi2);
        }
    }

    /**
     * Update method cekMultipleInteraksi untuk menggunakan cache
     */
    public List<InteraksiObat> cekMultipleInteraksiCache(List<String> daftarKomposisi) {
        List<InteraksiObat> hasilInteraksi = new ArrayList<>();

        if (daftarKomposisi == null || daftarKomposisi.size() < 2) {
            return hasilInteraksi;
        }

        // Cek semua kombinasi pasangan obat menggunakan cache
        for (int i = 0; i < daftarKomposisi.size(); i++) {
            for (int j = i + 1; j < daftarKomposisi.size(); j++) {
                String komp1 = daftarKomposisi.get(i);
                String komp2 = daftarKomposisi.get(j);

                // Skip jika komposisi sama
                if (komp1.equalsIgnoreCase(komp2)) {
                    continue;
                }

                InteraksiObat interaksi = cekInteraksiCache(komp1, komp2);
                if (interaksi != null) {
                    hasilInteraksi.add(interaksi);
                }
            }
        }

        // Sort berdasarkan prioritas
        InteraksiObat.sortByPrioritas(hasilInteraksi);
        return hasilInteraksi;
    }

    /**
     * Helper method untuk prioritas tingkat
     */
    private int getTingkatPrioritas(String tingkat) {
        switch (tingkat) {
            case "KONTRAINDIKASI": return 1;
            case "BERAT": return 2;
            case "SEDANG": return 3;
            case "RINGAN": return 4;
            default: return 5;
        }
    }

    /**
     * Cek interaksi antara dua komposisi obat (fallback ke database)
     *
     * @param komposisi1 komposisi obat pertama
     * @param komposisi2 komposisi obat kedua
     * @return InteraksiObat jika ditemukan, null jika tidak ada
     */
    public InteraksiObat cekInteraksi(String komposisi1, String komposisi2) {
        if (komposisi1 == null || komposisi2 == null
                || komposisi1.trim().isEmpty() || komposisi2.trim().isEmpty()) {
            return null;
        }

        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT komposisi1, komposisi2, tingkat_interaksi, deskripsi_interaksi, "
                    + "rekomendasi, mekanisme, onset FROM interaksi_obat "
                    + "WHERE status='AKTIF' AND "
                    + "((UPPER(komposisi1)=? AND UPPER(komposisi2)=?) OR "
                    + "(UPPER(komposisi1)=? AND UPPER(komposisi2)=?)) "
                    + "ORDER BY CASE tingkat_interaksi "
                    + "WHEN 'KONTRAINDIKASI' THEN 1 WHEN 'BERAT' THEN 2 "
                    + "WHEN 'SEDANG' THEN 3 WHEN 'RINGAN' THEN 4 END LIMIT 1"
            );

            ps.setString(1, komposisi1.toUpperCase().trim());
            ps.setString(2, komposisi2.toUpperCase().trim());
            ps.setString(3, komposisi2.toUpperCase().trim());
            ps.setString(4, komposisi1.toUpperCase().trim());

            ResultSet rs = ps.executeQuery();
            InteraksiObat result = null;

            if (rs.next()) {
                result = new InteraksiObat(
                        rs.getString("komposisi1"),
                        rs.getString("komposisi2"),
                        rs.getString("tingkat_interaksi"),
                        rs.getString("deskripsi_interaksi"),
                        rs.getString("rekomendasi"),
                        rs.getString("mekanisme"),
                        rs.getString("onset")
                );
            }

            rs.close();
            ps.close();
            return result;

        } catch (SQLException e) {
            System.out.println("Error cek interaksi: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cek interaksi untuk multiple komposisi obat (fallback ke database)
     *
     * @param daftarKomposisi list komposisi obat
     * @return list semua interaksi yang ditemukan
     */
    public List<InteraksiObat> cekMultipleInteraksi(List<String> daftarKomposisi) {
        List<InteraksiObat> hasilInteraksi = new ArrayList<>();

        if (daftarKomposisi == null || daftarKomposisi.size() < 2) {
            return hasilInteraksi;
        }

        // Cek semua kombinasi pasangan obat
        for (int i = 0; i < daftarKomposisi.size(); i++) {
            for (int j = i + 1; j < daftarKomposisi.size(); j++) {
                String komp1 = daftarKomposisi.get(i);
                String komp2 = daftarKomposisi.get(j);

                // Skip jika komposisi sama
                if (komp1.equalsIgnoreCase(komp2)) {
                    continue;
                }

                InteraksiObat interaksi = cekInteraksi(komp1, komp2);
                if (interaksi != null) {
                    hasilInteraksi.add(interaksi);
                }
            }
        }

        // Sort berdasarkan prioritas
        InteraksiObat.sortByPrioritas(hasilInteraksi);
        return hasilInteraksi;
    }

    /**
     * Parse komposisi obat dari string
     *
     * @param komposisiString string komposisi yang mengandung multiple
     * kandungan
     * @return array kandungan yang sudah dibersihkan
     */
    public String[] parseKomposisi(String komposisiString) {
        if (komposisiString == null || komposisiString.trim().isEmpty()) {
            return new String[0];
        }

        // Bersihkan komposisi dari angka dosis dan satuan
        String cleanKomposisi = komposisiString
                .replaceAll("\\d+(\\.\\d+)?\\s*(mg|g|ml|mcg|iu|%|tablet|kapsul|amp)", "")
                .replaceAll("\\s+", " ")
                .trim();

        // Split berdasarkan pemisah umum
        String[] kandungan = cleanKomposisi.split("[,+;&|/\\-]");

        // Bersihkan setiap kandungan
        List<String> hasil = new ArrayList<>();
        for (String item : kandungan) {
            String clean = item.trim()
                    .replaceAll("[()\\[\\]{}]", "") // hapus kurung
                    .replaceAll("\\s+", " ") // multiple space jadi single
                    .trim();

            // Skip jika kosong atau terlalu pendek
            if (!clean.isEmpty() && clean.length() > 2) {
                hasil.add(clean.toUpperCase());
            }
        }

        return hasil.toArray(new String[0]);
    }

    // =====================================================
    // STATISTICS METHODS (OPTIMIZED WITH CACHE)
    // =====================================================
    
    /**
     * Dapatkan statistik database interaksi dari cache (lebih cepat)
     *
     * @return array [total, kontraindikasi, berat, sedang, ringan]
     */
    public int[] getStatistikInteraksi() {
        int[] stats = new int[5]; // [total, kontraindikasi, berat, sedang, ringan]

        try {
            // Coba dari cache dulu
            if (new File("./cache/interaksi_obat.iyem").exists()) {
                myObj = new FileReader("./cache/interaksi_obat.iyem");
                root = mapper.readTree(myObj);
                response = root.path("interaksi");
                
                if (response.isArray()) {
                    for (JsonNode item : response) {
                        if ("AKTIF".equals(item.path("status").asText())) {
                            stats[0]++; // total
                            
                            String tingkat = item.path("tingkat").asText();
                            switch (tingkat) {
                                case "KONTRAINDIKASI": stats[1]++; break;
                                case "BERAT": stats[2]++; break;
                                case "SEDANG": stats[3]++; break;
                                case "RINGAN": stats[4]++; break;
                            }
                        }
                    }
                }
                
                myObj.close();
                return stats;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error baca statistik cache, fallback ke database");
        }

        // Fallback ke database jika cache gagal
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT tingkat_interaksi, COUNT(*) as jumlah "
                    + "FROM interaksi_obat WHERE status='AKTIF' "
                    + "GROUP BY tingkat_interaksi"
            );

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tingkat = rs.getString("tingkat_interaksi");
                int jumlah = rs.getInt("jumlah");
                stats[0] += jumlah; // total

                switch (tingkat) {
                    case "KONTRAINDIKASI":
                        stats[1] = jumlah;
                        break;
                    case "BERAT":
                        stats[2] = jumlah;
                        break;
                    case "SEDANG":
                        stats[3] = jumlah;
                        break;
                    case "RINGAN":
                        stats[4] = jumlah;
                        break;
                }
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Error get statistik: " + e.getMessage());
        }

        return stats;
    }

    // =====================================================
    // SEARCH BY CATEGORY METHODS
    // =====================================================
    
    /**
     * Cari semua interaksi untuk komposisi tertentu
     *
     * @param komposisi komposisi yang dicari
     * @return list interaksi yang melibatkan komposisi tersebut
     */
    public List<InteraksiObat> cariInteraksiByKomposisi(String komposisi) {
        List<InteraksiObat> hasil = new ArrayList<>();

        if (komposisi == null || komposisi.trim().isEmpty()) {
            return hasil;
        }

        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT komposisi1, komposisi2, tingkat_interaksi, deskripsi_interaksi, "
                    + "rekomendasi, mekanisme, onset FROM interaksi_obat "
                    + "WHERE status='AKTIF' AND "
                    + "(UPPER(komposisi1) LIKE ? OR UPPER(komposisi2) LIKE ?) "
                    + "ORDER BY CASE tingkat_interaksi "
                    + "WHEN 'KONTRAINDIKASI' THEN 1 WHEN 'BERAT' THEN 2 "
                    + "WHEN 'SEDANG' THEN 3 WHEN 'RINGAN' THEN 4 END"
            );

            String searchPattern = "%" + komposisi.toUpperCase().trim() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InteraksiObat interaksi = new InteraksiObat(
                        rs.getString("komposisi1"),
                        rs.getString("komposisi2"),
                        rs.getString("tingkat_interaksi"),
                        rs.getString("deskripsi_interaksi"),
                        rs.getString("rekomendasi"),
                        rs.getString("mekanisme"),
                        rs.getString("onset")
                );
                hasil.add(interaksi);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Error cari interaksi by komposisi: " + e.getMessage());
        }

        return hasil;
    }

    /**
     * Cari interaksi berdasarkan tingkat (BERAT, SEDANG, RINGAN)
     *
     * @param tingkat tingkat interaksi
     * @return list interaksi dengan tingkat tersebut
     */
    public List<InteraksiObat> cariInteraksiByTingkat(String tingkat) {
        List<InteraksiObat> hasil = new ArrayList<>();

        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT komposisi1, komposisi2, tingkat_interaksi, deskripsi_interaksi, "
                    + "rekomendasi, mekanisme, onset FROM interaksi_obat "
                    + "WHERE status='AKTIF' AND tingkat_interaksi=? "
                    + "ORDER BY komposisi1, komposisi2"
            );

            ps.setString(1, tingkat.toUpperCase());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InteraksiObat interaksi = new InteraksiObat(
                        rs.getString("komposisi1"),
                        rs.getString("komposisi2"),
                        rs.getString("tingkat_interaksi"),
                        rs.getString("deskripsi_interaksi"),
                        rs.getString("rekomendasi"),
                        rs.getString("mekanisme"),
                        rs.getString("onset")
                );
                hasil.add(interaksi);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Error cari interaksi by tingkat: " + e.getMessage());
        }

        return hasil;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    /**
     * Test koneksi database
     *
     * @return true jika koneksi berhasil
     */
    public boolean testKoneksi() {
        try {
            PreparedStatement ps = koneksi.prepareStatement("SELECT COUNT(*) FROM interaksi_obat LIMIT 1");
            ResultSet rs = ps.executeQuery();
            boolean result = rs.next();
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Error test koneksi: " + e.getMessage());
            return false;
        }
    }

    // TAMBAHKAN METHOD INI ke class InteraksiObatManager (di akhir class sebelum closing brace):
    /**
     * Method untuk debug database interaksi
     */
    public void debugDatabase() {
        System.out.println("=== DEBUG DATABASE INTERAKSI ===");

        if (koneksi == null) {
            System.out.println("❌ Koneksi null");
            return;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Test koneksi dasar
            String testQuery = "SELECT 1";
            ps = koneksi.prepareStatement(testQuery);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Koneksi database OK");
            }
            rs.close();
            ps.close();

            // Cek apakah tabel ada
            String checkTable = "SHOW TABLES LIKE 'interaksi_obat'";
            ps = koneksi.prepareStatement(checkTable);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Tabel interaksi_obat ditemukan");
            } else {
                System.out.println("❌ Tabel interaksi_obat tidak ditemukan");
                rs.close();
                ps.close();
                return;
            }
            rs.close();
            ps.close();

            // Cek struktur tabel
            String structureQuery = "DESCRIBE interaksi_obat";
            ps = koneksi.prepareStatement(structureQuery);
            rs = ps.executeQuery();
            System.out.println("📋 Struktur tabel:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("Field") + " (" + rs.getString("Type") + ")");
            }
            rs.close();
            ps.close();

            // Cek data
            String dataQuery = "SELECT COUNT(*) as total, "
                    + "SUM(CASE WHEN status='AKTIF' THEN 1 ELSE 0 END) as aktif, "
                    + "SUM(CASE WHEN status='NONAKTIF' THEN 1 ELSE 0 END) as nonaktif "
                    + "FROM interaksi_obat";
            ps = koneksi.prepareStatement(dataQuery);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("📊 Data interaksi:");
                System.out.println("  - Total: " + rs.getInt("total"));
                System.out.println("  - Aktif: " + rs.getInt("aktif"));
                System.out.println("  - Non-aktif: " + rs.getInt("nonaktif"));
            }
            rs.close();
            ps.close();

            // Sample data
            String sampleQuery = "SELECT komposisi1, komposisi2, tingkat_interaksi, status FROM interaksi_obat LIMIT 3";
            ps = koneksi.prepareStatement(sampleQuery);
            rs = ps.executeQuery();
            System.out.println("📝 Sample data:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("komposisi1") + " + "
                        + rs.getString("komposisi2") + " ("
                        + rs.getString("tingkat_interaksi") + ") ["
                        + rs.getString("status") + "]");
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("❌ Error debug: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing debug resources: " + e.getMessage());
            }
        }

        System.out.println("===============================");
    }
}
