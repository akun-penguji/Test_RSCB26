package laporan;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DummyDataGenerator {
    public static void main(String[] args) {
        Connection koneksi = koneksiDB.condb();
        PreparedStatement ps = null;
        try {
            // 1. Ensure tables exist (just in case)
            // reg_periksa, pasien, dokter usually exist but we need sample data
            
            // 2. Insert Pasien (if not exists)
            String[] pasiens = {
                "('000001', 'Budi Santoso', 'L', '1980-01-01')",
                "('000002', 'Siti Aminah', 'P', '1985-05-05')",
                "('000003', 'Rudi Hartono', 'L', '1990-10-10')"
            };
            for (String val : pasiens) {
                try {
                    ps = koneksi.prepareStatement("INSERT IGNORE INTO pasien (no_rkm_medis, nm_pasien, jk, tgl_lahir) VALUES " + val);
                    ps.execute();
                } catch (Exception e) {}
            }

            // 3. Insert Dokter (if not exists)
            String[] dokters = {
                "('D001', 'Dr. Andi', 'L', 'Umum')",
                "('D002', 'Dr. Budi', 'L', 'Bedah')",
                "('D003', 'Dr. Citra', 'P', 'Anak')"
            };
            for (String val : dokters) {
                try {
                    ps = koneksi.prepareStatement("INSERT IGNORE INTO dokter (kd_dokter, nm_dokter, jk, kd_sps) VALUES " + val);
                    ps.execute();
                } catch (Exception e) {}
            }

            // 3.5 Insert Poliklinik & Penjab (References for Reg Periksa)
            try {
                ps = koneksi.prepareStatement("INSERT IGNORE INTO poliklinik (kd_poli, nm_poli) VALUES ('P001', 'Poli Umum')");
                ps.execute();
            } catch (Exception e) {}
            
            try {
                ps = koneksi.prepareStatement("INSERT IGNORE INTO penjab (kd_pj, png_jawab) VALUES ('A01', 'Umum')");
                ps.execute();
            } catch (Exception e) {}

            // 4. Insert Reg Periksa
            // Columns: no_rawat, no_rkm_medis, kd_dokter, tgl_registrasi, jam_reg, kd_poli, kd_pj, stts, stts_daftar, status_lanjut, p_jawab, almt_pj, hubunganpj, biaya_reg, umurnam, umurthn, umurbln, umurhr
            String[] regs = {
                "('2023/01/01/000001', '000001', 'D001', '2023-01-01', '08:00:00', 'P001', 'A01', 'Belum', 'Lama', 'Ralan', 'Keluarga', 'Alamat', 'Saudara', 0, '0', 0, 0, 0)",
                "('2023/01/02/000001', '000002', 'D002', '2023-01-02', '09:00:00', 'P001', 'A01', 'Belum', 'Lama', 'Ralan', 'Keluarga', 'Alamat', 'Saudara', 0, '0', 0, 0, 0)",
                "('2023/01/03/000001', '000003', 'D003', '2023-01-03', '10:00:00', 'P001', 'A01', 'Belum', 'Lama', 'Ralan', 'Keluarga', 'Alamat', 'Saudara', 0, '0', 0, 0, 0)"
            };
            for (String val : regs) {
                try {
                    ps = koneksi.prepareStatement("INSERT IGNORE INTO reg_periksa (no_rawat, no_rkm_medis, kd_dokter, tgl_registrasi, jam_reg, kd_poli, kd_pj, stts, stts_daftar, status_lanjut, p_jawab, almt_pj, hubunganpj, biaya_reg, umurnam, umurthn, umurbln, umurhr) VALUES " + val);
                    ps.execute();
                } catch (Exception e) {
                     System.out.println("Error inserting reg_periksa: " + e);
                }
            }

            // 5. Insert Berkas Klaim (The Drafts)
            String[] berkas = {
                "('2023/01/01/000001', '2023-01-01 10:00:00', 'Draft_Budi.pdf', 'berkasklaim/Draft_Budi.pdf')",
                "('2023/01/02/000001', '2023-01-02 11:00:00', 'Draft_Siti.pdf', 'berkasklaim/Draft_Siti.pdf')",
                "('2023/01/03/000001', '2023-01-03 12:00:00', 'Draft_Rudi.pdf', 'berkasklaim/Draft_Rudi.pdf')"
            };
            for (String val : berkas) {
                try {
                    ps = koneksi.prepareStatement("INSERT IGNORE INTO berkas_klaim (no_rawat, tgl_upload, nama_berkas, lokasi_file) VALUES " + val);
                    ps.execute();
                } catch (Exception e) {
                    System.out.println("Error inserting berkas_klaim: " + e);
                }
            }

            // 6. Insert Berkas Digital Perawatan (The Signers Linking)
            // Budi (D001, D002)
            // Siti (D002)
            // Rudi (D003, D001)
            // Format: no_rawat, nip, kode, file
            String[] signers = {
                "2023/01/01/000001, D001, B01, file1.pdf", // D001 for Budi
                "2023/01/01/000001, D002, B02, file2.pdf", // D002 for Budi
                "2023/01/02/000001, D002, B01, file3.pdf", // D002 for Siti
                "2023/01/03/000001, D003, B01, file4.pdf", // D003 for Rudi
                "2023/01/03/000001, D001, B02, file5.pdf"  // D001 for Rudi
            };
            // Note: We need master_berkas_digital or ensure constraints? Usually simple insert works if no FK enforcement on master
            
            // Re-doing the loop with correct PreparedStatement
             for (String val : signers) {
                 String[] parts = val.split(", ");
                 try {
                     // Ensure kode exists in master_berkas_digital if enforced
                     try {
                        ps = koneksi.prepareStatement("INSERT IGNORE INTO master_berkas_digital (kode, nama) VALUES ('"+parts[2]+"', 'Berkas "+parts[2]+"')");
                        ps.execute();
                     } catch(Exception ex){}

                     ps = koneksi.prepareStatement("INSERT INTO berkas_digital_perawatan (no_rawat, kode, lokasi_file) VALUES (?, ?, ?)");
                     ps.setString(1, parts[0]); // no_rawat
                     ps.setString(2, parts[2]); // Kode
                     ps.setString(3, parts[3]); // File
                     ps.executeUpdate();
                     
                     // Update NIP (assuming nip column exists based on DlgBerkasRawat usage)
                     try {
                         ps = koneksi.prepareStatement("UPDATE berkas_digital_perawatan SET nip=? WHERE no_rawat=? AND kode=?");
                         ps.setString(1, parts[1]); // NIP
                         ps.setString(2, parts[0]);
                         ps.setString(3, parts[2]);
                         ps.executeUpdate();
                     } catch(Exception e) {
                         System.out.println("Error updating nip: " + e);
                     }
                     
                 } catch (Exception e) {
                     System.out.println("Error inserting signer: " + e);
                 }
             }

            System.out.println("Dummy data generation complete.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
