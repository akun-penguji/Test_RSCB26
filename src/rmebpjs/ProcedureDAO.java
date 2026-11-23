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

public class ProcedureDAO {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;

    public ProcedureData getProceduresBySEP(String noSEP) throws Exception {

        //List<ProcedureData> list = new ArrayList<>();

        String sql =
              "SELECT " +
                "prosedur_pasien.no_rawat, " +
                "prosedur_pasien.kode, " +
                "icd9.deskripsi_panjang, " +
                "reg_periksa.tgl_registrasi, " +
                "reg_periksa.jam_reg " +
                "FROM prosedur_pasien " +
                "JOIN icd9 ON icd9.kode = prosedur_pasien.kode " +
                "JOIN reg_periksa ON reg_periksa.no_rawat = prosedur_pasien.no_rawat " +
                "JOIN bridging_sep ON bridging_sep.no_rawat = prosedur_pasien.no_rawat " +
                "WHERE bridging_sep.no_sep = ? " +
                "ORDER BY prosedur_pasien.prioritas ASC " +   // diagnosis utama lebih dulu
                "LIMIT 1";
        
        ProcedureData pd = null;
        ps = koneksi.prepareStatement(sql);
        ps.setString(1, noSEP);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                
                String tgl = rs.getString("tgl_registrasi");  // yyyy-MM-dd
                String jam = rs.getString("jam_reg");         // HH:mm:ss
                String onset = tgl + "T" + jam + "+07:00";    // format FHIR datetime

                pd = new ProcedureData();
                pd.idProcedure   = rs.getString("kode");
                pd.code          = rs.getString("kode");
                pd.display       = rs.getString("deskripsi_panjang");
                pd.start         = onset;
                pd.end           = onset;
                pd.performerCode    = rs.getString("kode");
                pd.performerDisplay = rs.getString("deskripsi_panjang");
                pd.note             = rs.getString("deskripsi_panjang");

                }
        }

        return pd;
    }
}
