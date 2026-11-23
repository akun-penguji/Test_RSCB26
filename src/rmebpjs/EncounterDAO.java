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

public class EncounterDAO {
    private Connection koneksi = koneksiDB.condb();  
    private PreparedStatement ps;
    public EncounterData getEncounterBySEP(String noSEP) throws Exception {

        String sql =
                "SELECT "
              + "    no_sep, "
              + "    tglsep, "
              + "    no_rujukan, "
              + "    diagawal, "
              + "    nmdiagnosaawal, "
              + "    no_rawat "
              
              + "FROM bridging_sep "
              + "WHERE no_sep = ? "
              + "LIMIT 1";

        EncounterData e = null;
        ps = koneksi.prepareStatement(sql);
        ps.setString(1, noSEP);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                    e = new EncounterData();
                    e.idEncounter = rs.getString("diagawal");
                    e.start       = rs.getString("tglsep");
                    e.end         = rs.getString("tglsep");
                    e.reason      = rs.getString("nmdiagnosaawal");
                    e.sep         = rs.getString("no_sep");
                    e.rujukan     = rs.getString("no_rujukan");
                    e.nomorrawat  = rs.getString("no_rawat");
                }
        }

        return e;
    }
}
