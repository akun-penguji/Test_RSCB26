package rmebpjs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ConditionDAO {
    
    private Connection conn;
    
    public ConditionDAO(Connection conn) {
        this.conn = conn;
    }
    
    public List<ConditionData> getConditionsBySEP(String noSEP) {
        List<ConditionData> list = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = 
            "SELECT p.kd_penyakit, p.nm_penyakit, rp.tgl_registrasi " +
            "FROM diagnosa_pasien dp " +
            "JOIN penyakit p ON dp.kd_penyakit = p.kd_penyakit " +
            "JOIN reg_periksa rp ON dp.no_rawat = rp.no_rawat " +
            "JOIN bridging_sep bs ON rp.no_rawat = bs.no_rawat " +
            "WHERE bs.no_sep = ? " +
            "ORDER BY dp.prioritas ASC " +
            "LIMIT 10";
        
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, noSEP);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ConditionData c = new ConditionData();
                c.code = rs.getString("kd_penyakit");
                c.display = rs.getString("nm_penyakit");
                
                String tgl = rs.getString("tgl_registrasi");
                c.onset = (tgl != null) ? tgl + " 00:00:00" : "";
                
                list.add(c);
            }
        } catch (Exception e) {
            System.out.println("Error getConditionsBySEP: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
        
        return list;
    }
}
