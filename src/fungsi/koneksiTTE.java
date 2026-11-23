/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fungsi;

import AESsecurity.EnkripsiAES;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.Properties;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author it_rs
 */
public class koneksiTTE {
    private static Connection connection=null;
    private static final Properties prop = new Properties();  
    private static final MysqlDataSource dataSource=new MysqlDataSource();
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private SecretKeySpec secretKey;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private ObjectMapper mapper = new ObjectMapper();

    public koneksiTTE(){} 
    
    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException { 
        sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {
            new X509TrustManager(){
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{}
            }
        };
                
        sslContext.init(null, trustManagers, new SecureRandom());
        sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme = new Scheme("https", 443, sslFactory);
        factory = new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }
    
    public Boolean cek_koneksi(){
        Boolean hasil = false;
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            String urlTTE=prop.getProperty("URLTTE");

            String URL = urlTTE +"/check_tte_server";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            String requestJson =" {}" ;
            HttpEntity requestEntity = new HttpEntity(requestJson,headers);
            JsonNode data = mapper.readTree(getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            
            hasil = data.path("status").asText().equals("true");
            System.out.println("Url : "+URL+" | "+hasil);
        } catch (Exception e) {
            System.out.println(e);
        }
        return hasil;
    }
}
