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


public class PractitionerDAO {
 private Connection koneksi = koneksiDB.condb();  
    private PreparedStatement ps;
    public PractitionerData getPractitionerBySEP(String noSEP) throws Exception {

        String sql =
            "SELECT "
                + "    dokter.kd_dokter, "
                + "    dokter.nm_dokter, "
                + "    dokter.jk, "
                + "    dokter.tgl_lahir, "
                + "    dokter.no_ijn_praktek, "
                + "    dokter.no_telp, "
                + "    dokter.almt_tgl, "
                + "    pegawai.no_ktp "
                + "FROM dokter "
                + "JOIN pegawai ON pegawai.nik = dokter.kd_dokter "
                + "JOIN maping_dokter_dpjpvclaim ON maping_dokter_dpjpvclaim.kd_dokter = dokter.kd_dokter "
                + "JOIN bridging_sep ON bridging_sep.kddpjp = maping_dokter_dpjpvclaim.kd_dokter_bpjs "
                + "WHERE bridging_sep.no_sep = ? "
                + "LIMIT 1";

        PractitionerData pr = null;
        ps = koneksi.prepareStatement(sql);
        ps.setString(1, noSEP);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                    pr = new PractitionerData();
                    pr.idPractitioner = rs.getString("kd_dokter");
                    pr.nama           = rs.getString("nm_dokter");
                    pr.sip            = rs.getString("no_ijn_praktek");
                    pr.jk             = rs.getString("jk");
                    pr.tgllahir       = rs.getString("tgl_lahir");
                    pr.nik            = rs.getString("no_ktp");
                    pr.phone          = rs.getString("no_telp");
                    pr.alamat         = rs.getString("almt_tgl");
              }
        }

        return pr;
    }
}
