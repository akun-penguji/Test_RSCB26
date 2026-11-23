/*
 * Class untuk handling data interaksi obat
 * Package: bridging
 * Author: System
 * Description: Class untuk menyimpan dan mengelola data interaksi obat berdasarkan komposisi zat aktif
 */

package bridging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Class untuk menyimpan data interaksi obat berdasarkan komposisi zat aktif
 */
public class InteraksiObat {
    private String komposisi1;
    private String komposisi2;
    private String tingkatInteraksi;
    private String deskripsiInteraksi;
    private String rekomendasi;
    private String mekanisme;
    private String onset;
    private String dokumentasi;
    
    /**
     * Constructor untuk membuat objek InteraksiObat
     * @param komposisi1 Kandungan zat aktif pertama
     * @param komposisi2 Kandungan zat aktif kedua
     * @param tingkatInteraksi Tingkat interaksi (RINGAN, SEDANG, BERAT, KONTRAINDIKASI)
     * @param deskripsiInteraksi Deskripsi efek interaksi
     * @param rekomendasi Rekomendasi tindakan
     * @param mekanisme Mekanisme interaksi
     * @param onset Waktu timbul interaksi
     */
    public InteraksiObat(String komposisi1, String komposisi2, String tingkatInteraksi, 
                        String deskripsiInteraksi, String rekomendasi, String mekanisme, String onset) {
        this.komposisi1 = komposisi1;
        this.komposisi2 = komposisi2;
        this.tingkatInteraksi = tingkatInteraksi;
        this.deskripsiInteraksi = deskripsiInteraksi;
        this.rekomendasi = rekomendasi;
        this.mekanisme = mekanisme;
        this.onset = onset;
        this.dokumentasi = "";
    }
    
    /**
     * Constructor lengkap dengan dokumentasi
     */
    public InteraksiObat(String komposisi1, String komposisi2, String tingkatInteraksi, 
                        String deskripsiInteraksi, String rekomendasi, String mekanisme, 
                        String onset, String dokumentasi) {
        this(komposisi1, komposisi2, tingkatInteraksi, deskripsiInteraksi, rekomendasi, mekanisme, onset);
        this.dokumentasi = dokumentasi;
    }
    
    // =====================================================
    // GETTER METHODS
    // =====================================================
    
    public String getKomposisi1() { 
        return komposisi1 != null ? komposisi1 : ""; 
    }
    
    public String getKomposisi2() { 
        return komposisi2 != null ? komposisi2 : ""; 
    }
    
    public String getTingkatInteraksi() { 
        return tingkatInteraksi != null ? tingkatInteraksi : "RINGAN"; 
    }
    
    public String getDeskripsiInteraksi() { 
        return deskripsiInteraksi != null ? deskripsiInteraksi : ""; 
    }
    
    public String getRekomendasi() { 
        return rekomendasi != null ? rekomendasi : "Monitor pasien untuk efek samping"; 
    }
    
    public String getMekanisme() { 
        return mekanisme != null ? mekanisme : ""; 
    }
    
    public String getOnset() { 
        return onset != null ? onset : ""; 
    }
    
    public String getDokumentasi() { 
        return dokumentasi != null ? dokumentasi : ""; 
    }
    
    // =====================================================
    // SETTER METHODS
    // =====================================================
    
    public void setKomposisi1(String komposisi1) {
        this.komposisi1 = komposisi1;
    }
    
    public void setKomposisi2(String komposisi2) {
        this.komposisi2 = komposisi2;
    }
    
    public void setTingkatInteraksi(String tingkatInteraksi) {
        this.tingkatInteraksi = tingkatInteraksi;
    }
    
    public void setDeskripsiInteraksi(String deskripsiInteraksi) {
        this.deskripsiInteraksi = deskripsiInteraksi;
    }
    
    public void setRekomendasi(String rekomendasi) {
        this.rekomendasi = rekomendasi;
    }
    
    public void setMekanisme(String mekanisme) {
        this.mekanisme = mekanisme;
    }
    
    public void setOnset(String onset) {
        this.onset = onset;
    }
    
    public void setDokumentasi(String dokumentasi) {
        this.dokumentasi = dokumentasi;
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    /**
     * Mengecek apakah interaksi ini termasuk kategori berbahaya
     * @return true jika BERAT atau KONTRAINDIKASI
     */
    public boolean isBerbahaya() {
        return "BERAT".equals(tingkatInteraksi) || "KONTRAINDIKASI".equals(tingkatInteraksi);
    }
    
    /**
     * Mengecek apakah interaksi ini termasuk kategori sedang
     * @return true jika SEDANG
     */
    public boolean isSedang() {
        return "SEDANG".equals(tingkatInteraksi);
    }
    
    /**
     * Mengecek apakah interaksi ini termasuk kategori ringan
     * @return true jika RINGAN
     */
    public boolean isRingan() {
        return "RINGAN".equals(tingkatInteraksi);
    }
    
    /**
     * Mendapatkan prioritas numerik untuk sorting
     * @return 1=KONTRAINDIKASI, 2=BERAT, 3=SEDANG, 4=RINGAN
     */
    public int getPrioritas() {
        switch (tingkatInteraksi) {
            case "KONTRAINDIKASI": return 1;
            case "BERAT": return 2;
            case "SEDANG": return 3;
            case "RINGAN": return 4;
            default: return 5;
        }
    }
    
    /**
     * Mendapatkan emoji untuk tingkat interaksi
     * @return emoji yang sesuai
     */
    public String getEmojiTingkat() {
        switch (tingkatInteraksi) {
            case "KONTRAINDIKASI": return "🚫";
            case "BERAT": return "🚨";
            case "SEDANG": return "⚠️";
            case "RINGAN": return "ℹ️";
            default: return "❓";
        }
    }
    
    /**
     * Mendapatkan warna untuk tingkat interaksi (untuk UI)
     * @return kode warna hex
     */
    public String getWarnaTingkat() {
        switch (tingkatInteraksi) {
            case "KONTRAINDIKASI": return "#FF0000"; // Merah
            case "BERAT": return "#FF6600";         // Orange merah
            case "SEDANG": return "#FFAA00";        // Orange
            case "RINGAN": return "#3399FF";        // Biru
            default: return "#666666";              // Abu-abu
        }
    }
    
    /**
     * Format output untuk display di dialog
     * @return string formatted untuk tampilan
     */
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append(getEmojiTingkat()).append(" ");
        sb.append(komposisi1).append(" + ").append(komposisi2).append("\n");
        sb.append("Tingkat: ").append(tingkatInteraksi).append("\n");
        sb.append("Efek: ").append(deskripsiInteraksi).append("\n");
        sb.append("Rekomendasi: ").append(rekomendasi);
        
        if (mekanisme != null && !mekanisme.trim().isEmpty()) {
            sb.append("\nMekanisme: ").append(mekanisme);
        }
        
        if (onset != null && !onset.trim().isEmpty()) {
            sb.append("\nOnset: ").append(onset);
        }
        
        return sb.toString();
    }
    
    /**
     * Mengecek apakah komposisi tertentu terlibat dalam interaksi ini
     * @param komposisi komposisi yang dicari
     * @return true jika terlibat
     */
    public boolean mengandungKomposisi(String komposisi) {
        if (komposisi == null) return false;
        String upperKomposisi = komposisi.toUpperCase();
        return getKomposisi1().toUpperCase().contains(upperKomposisi) || 
               getKomposisi2().toUpperCase().contains(upperKomposisi);
    }
    
    /**
     * Override toString untuk debugging
     */
    @Override
    public String toString() {
        return String.format("InteraksiObat[%s + %s = %s]", 
                           komposisi1, komposisi2, tingkatInteraksi);
    }
    
    /**
     * Override equals untuk comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        InteraksiObat other = (InteraksiObat) obj;
        return ((komposisi1.equals(other.komposisi1) && komposisi2.equals(other.komposisi2)) ||
                (komposisi1.equals(other.komposisi2) && komposisi2.equals(other.komposisi1)));
    }
    
    /**
     * Override hashCode
     */
    @Override
    public int hashCode() {
        // Ensure same hash for A+B and B+A combinations
        int hash1 = komposisi1.hashCode();
        int hash2 = komposisi2.hashCode();
        return Math.min(hash1, hash2) + Math.max(hash1, hash2);
    }
    
    // =====================================================
    // STATIC UTILITY METHODS
    // =====================================================
    
    /**
     * Membuat list kosong untuk kategori interaksi
     * @return List kosong yang siap digunakan
     */
    public static List<InteraksiObat> createEmptyList() {
        return new ArrayList<>();
    }
    
    /**
     * Filter list berdasarkan tingkat interaksi
     * @param daftarInteraksi list sumber
     * @param tingkat tingkat yang dicari
     * @return list yang sudah difilter
     */
    public static List<InteraksiObat> filterByTingkat(List<InteraksiObat> daftarInteraksi, String tingkat) {
        List<InteraksiObat> result = new ArrayList<>();
        for (InteraksiObat interaksi : daftarInteraksi) {
            if (tingkat.equals(interaksi.getTingkatInteraksi())) {
                result.add(interaksi);
            }
        }
        return result;
    }
    
    /**
     * Sort list berdasarkan prioritas (KONTRAINDIKASI -> BERAT -> SEDANG -> RINGAN)
     * @param daftarInteraksi list yang akan disort
     */
    public static void sortByPrioritas(List<InteraksiObat> daftarInteraksi) {
        daftarInteraksi.sort((a, b) -> Integer.compare(a.getPrioritas(), b.getPrioritas()));
    }
    
    /**
     * Cek apakah ada interaksi berbahaya dalam list
     * @param daftarInteraksi list yang dicek
     * @return true jika ada interaksi BERAT atau KONTRAINDIKASI
     */
    public static boolean adaInteraksiBerbahaya(List<InteraksiObat> daftarInteraksi) {
        for (InteraksiObat interaksi : daftarInteraksi) {
            if (interaksi.isBerbahaya()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Hitung jumlah interaksi per tingkat
     * @param daftarInteraksi list sumber
     * @return array [kontraindikasi, berat, sedang, ringan]
     */
    public static int[] hitungPerTingkat(List<InteraksiObat> daftarInteraksi) {
        int[] count = new int[4]; // [kontraindikasi, berat, sedang, ringan]
        
        for (InteraksiObat interaksi : daftarInteraksi) {
            switch (interaksi.getTingkatInteraksi()) {
                case "KONTRAINDIKASI": count[0]++; break;
                case "BERAT": count[1]++; break;
                case "SEDANG": count[2]++; break;
                case "RINGAN": count[3]++; break;
            }
        }
        
        return count;
    }
    
    /**
     * Generate summary text untuk multiple interaksi
     * @param daftarInteraksi list interaksi
     * @return summary text
     */
    public static String generateSummary(List<InteraksiObat> daftarInteraksi) {
        if (daftarInteraksi.isEmpty()) {
            return "Tidak ada interaksi obat yang terdeteksi.";
        }
        
        int[] count = hitungPerTingkat(daftarInteraksi);
        StringBuilder sb = new StringBuilder();
        sb.append("Ditemukan ").append(daftarInteraksi.size()).append(" interaksi obat:\n");
        
        if (count[0] > 0) sb.append("🚫 Kontraindikasi: ").append(count[0]).append("\n");
        if (count[1] > 0) sb.append("🚨 Berat: ").append(count[1]).append("\n");
        if (count[2] > 0) sb.append("⚠️ Sedang: ").append(count[2]).append("\n");
        if (count[3] > 0) sb.append("ℹ️ Ringan: ").append(count[3]).append("\n");
        
        return sb.toString();
    }
}