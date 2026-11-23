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


public class OrganizationDAO {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;

    public OrganizationData getOrganizationById(String idPpk) throws Exception {

        String sql =
                "SELECT "
              + "    nama_instansi, "
              + "    kode_ppk, "
              + "    kode_ppkkemenkes, "
              + "    alamat_instansi, "
              + "    kabupaten, "
              + "    propinsi, "
              + "    kontak "
              + "FROM setting "
              + "WHERE kode_ppk = ? "
              + "LIMIT 1";

        OrganizationData o = null;

        ps = koneksi.prepareStatement(sql);
        ps.setString(1, idPpk);   // ← memakai parameter yang benar

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                o = new OrganizationData();

                o.idOrganization = rs.getString("kode_ppk");
                o.nama           = rs.getString("nama_instansi");
                o.kdPpk          = rs.getString("kode_ppk");
                o.kdPpkKemenkes  = rs.getString("kode_ppkkemenkes");
                o.alamat         = rs.getString("alamat_instansi");
                o.kota           = rs.getString("kabupaten");
                o.provinsi       = rs.getString("propinsi");
                o.telp           = rs.getString("kontak");

                // KhanzA tidak punya kodepos & negara → isi default
                o.kodepos = "90561";
                o.negara  = "Indonesia";
            }
        }

        return o;
    }
}

