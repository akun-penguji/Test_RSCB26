/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laporan;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author pop
 */
public class TestTampilBerkasKlaim {
    public static void main(String[] args) {
        Connection koneksi = koneksiDB.condb();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        System.out.println("Testing Tampil Berkas Klaim...");
        
        try{
            String sql = "select berkas_klaim.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, "+
                    "berkas_klaim.tgl_upload, berkas_klaim.nama_berkas, berkas_klaim.lokasi_file, berkas_klaim.status, "+
                    "GROUP_CONCAT(DISTINCT dokter.nm_dokter SEPARATOR ', ') as signers "+
                    "from berkas_klaim inner join reg_periksa on berkas_klaim.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "left join berkas_digital_perawatan on berkas_klaim.no_rawat=berkas_digital_perawatan.no_rawat "+
                    "left join dokter on berkas_digital_perawatan.nip=dokter.kd_dokter "+
                    "where berkas_klaim.tgl_upload between ? and ? and (berkas_klaim.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ?) ";
            
            sql += "group by berkas_klaim.no_rawat, berkas_klaim.tgl_upload, berkas_klaim.nama_berkas, berkas_klaim.lokasi_file, berkas_klaim.status "+
                   "order by berkas_klaim.tgl_upload desc";
            
            System.out.println("Query: " + sql);
            
            // 1. Get a valid no_rawat
            ps = koneksi.prepareStatement("select no_rawat from reg_periksa limit 1");
            rs = ps.executeQuery();
            String validNoRawat = "";
            if(rs.next()) validNoRawat = rs.getString(1);
            
            if(!validNoRawat.equals("")){
                System.out.println("Using valid no_rawat: " + validNoRawat);
                // 2. Insert into berkas_klaim
                ps = koneksi.prepareStatement("delete from berkas_klaim where no_rawat=?");
                ps.setString(1, validNoRawat);
                ps.execute();
                
                ps = koneksi.prepareStatement("INSERT INTO berkas_klaim (no_rawat, tgl_upload, nama_berkas, lokasi_file, status) VALUES (?, NOW(), 'Test.pdf', 'pages/Test.pdf', 'MENUNGGU_TTD')");
                ps.setString(1, validNoRawat);
                ps.execute();
            }

            ps=koneksi.prepareStatement(sql);
            // Search range covering the dummy data YEAR 2023
            ps.setString(1, "2023-01-01 00:00:00");
            ps.setString(2, "2026-12-31 23:59:59");
            ps.setString(3, "%%");
            ps.setString(4, "%%");
            ps.setString(5, "%%");
             
            rs=ps.executeQuery();
            int count = 0;
            while(rs.next()){
                count++;
                System.out.println("Found: " + rs.getString("no_rawat") + 
                        " | " + rs.getString("nm_pasien") + 
                        " | " + rs.getString("tgl_upload") +
                        " | " + rs.getString("status") +
                        " | Signers: " + rs.getString("signers"));
            }
            if(count == 0) {
                System.out.println("No records found in range 2023-2026.");
            }
        }catch(Exception e){
            System.out.println("Error: "+e);
            e.printStackTrace();
        }
    }
}
