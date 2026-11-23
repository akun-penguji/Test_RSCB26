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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MedicationDAO {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;

    public List<MedicationData> getMedicationBySEP(String noSEP) throws Exception {

        List<MedicationData> list = new ArrayList<>();

        String sql =
              "SELECT " +
                "resep_dokter.no_resep, " +
                "resep_dokter.kode_brng, " +
                "resep_dokter.jml, " +
                "resep_dokter.aturan_pakai, " +
                "databarang.nama_brng " +
                "FROM resep_dokter " +
                "JOIN databarang ON databarang.kode_brng = resep_dokter.kode_brng " +
                "JOIN resep_obat ON resep_obat.no_resep = resep_dokter.no_resep " +
                "JOIN bridging_sep ON bridging_sep.no_rawat = resep_obat.no_rawat " +
                "WHERE bridging_sep.no_sep = ? " +
                "LIMIT 10";   // Ambil sampai 10 obat (atau sesuaikan)

        ps = koneksi.prepareStatement(sql);
        ps.setString(1, noSEP);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MedicationData m = new MedicationData();

                m.idMedication  = rs.getString("no_resep");
                m.code          = rs.getString("kode_brng");
                m.text          = rs.getString("nama_brng");
                m.aturanpakai   = rs.getString("aturan_pakai");

                // Anda bisa ubah routecode sesuai aturan medis
                m.routecode     = rs.getString("kode_brng");
                m.routedisplay  = rs.getString("nama_brng");

                list.add(m);
            }
        }

        return list;
    }
}

