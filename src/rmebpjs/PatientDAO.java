package rmebpjs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import simrskhanza.DlgKamarInap;
import rekammedis.RMRiwayatPerawatan;

public class PatientDAO {

    private Connection koneksi = koneksiDB.condb();  
    private PreparedStatement ps;

    public PatientData getPatientBySEP(String noSEP) throws Exception {

        String sql =
            "SELECT "
          + "    pasien.no_rkm_medis, "
          + "    pasien.nm_pasien, "
          + "    pasien.no_ktp, "
          + "    pasien.jk, "
          + "    pasien.tgl_lahir, "
          + "    pasien.alamat, "
          + "    pasien.no_peserta, "
          + "    pasien.no_tlp, "
          + "    pasien.kecamatanpj, "
          + "    pasien.kabupatenpj, "
          + "    pasien.propinsipj "
          + "FROM pasien "
          + "JOIN bridging_sep ON bridging_sep.nomr = pasien.no_rkm_medis "
          + "WHERE bridging_sep.no_sep = ? "
          + "LIMIT 1";

        PatientData p = null;

        ps = koneksi.prepareStatement(sql);
        ps.setString(1, noSEP);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                p = new PatientData();
                
                String jkDb = rs.getString("jk");

                p.jk = "L".equalsIgnoreCase(jkDb) ? "male" :
                       "P".equalsIgnoreCase(jkDb) ? "female" :
                       "unknown";

                p.idPatient = rs.getString("no_rkm_medis");
                p.norm      = rs.getString("no_rkm_medis");
                p.nama      = rs.getString("nm_pasien");
                p.noka      = rs.getString("no_peserta");
                p.ktp       = rs.getString("no_ktp");
                  
                p.tglLahir  = rs.getString("tgl_lahir");
                p.alamat    = rs.getString("alamat");
                p.kecamatan = rs.getString("kecamatanpj");
                p.kota      = rs.getString("kabupatenpj");
                p.provinsi  = rs.getString("propinsipj");
                p.telp      = rs.getString("no_tlp");
                p.hp        = rs.getString("no_tlp");
            }
        }

        return p;
    }
}
        

