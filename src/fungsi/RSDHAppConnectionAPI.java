/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 *
 * @author it999
 */
public class RSDHAppConnectionAPI {
    public static int REQUEST_TIME_OUT = 10000;
    public static int READ_TIME_OUT = 15000;
    public static int REQUEST_TIME_OUT_BIN = 10000;
    public static int READ_TIME_OUT_BIN = 15000;
    public String readUrl(String mapsApiDirectionsUrl, String[] headers) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(mapsApiDirectionsUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        //urlConnection.setConnectTimeout(REQUEST_TIME_OUT);
        //urlConnection.setReadTimeout(READ_TIME_OUT);
        if (headers != null)
            for (int i = 0; i < headers.length; ++i)
                urlConnection.setRequestProperty(
                        headers[i].split(":")[0],
                        headers[i].split(":")[1]
                );
        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        iStream
                )
        );
        StringBuffer sb = new StringBuffer();
        String line = "";


        while ((line = br.readLine()) != null)
            sb.append(line);

        if (urlConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Error : " + urlConnection.getResponseMessage());
        }

        urlConnection.disconnect();
        data = sb.toString();
        br.close();
        iStream.close();


        return data;
    }
    
    public BinaryData getUrlPostBin(String url_s, String raw_json) throws Exception {
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(url_s);
        byte[] paramB = raw_json.getBytes(Charset.forName("UTF-8"));

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        
        /*urlConnection.setConnectTimeout(REQUEST_TIME_OUT_BIN);
        urlConnection.setReadTimeout(READ_TIME_OUT_BIN);*/
        
        urlConnection.setDoOutput(true);
        
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.write(paramB);
        
        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        byte[] tmp = iStream.readAllBytes();

        if (urlConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Error : " + urlConnection.getResponseMessage());
        }
        System.err.println("Content Length: " + urlConnection.getContentLength());
        if (!"application/pdf".equals(urlConnection.getContentType().trim())) {
            if ("application/json".equals(urlConnection.getContentType().trim())) {
                iStream.close();
                urlConnection.disconnect();
                throw new Exception("File tidak di temukan !");
            } else {
                iStream.close();
                urlConnection.disconnect();
                throw new Exception("Internal server errors");
            }
        }
        
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int len;
        while ((len = iStream.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();*/
        
        urlConnection.disconnect();
        return new BinaryData(tmp, 0);
    }
    
    public String readUrlPostBin(String url_s, String raw_json, File file) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(url_s);
        byte[] paramB = raw_json.getBytes(Charset.forName("UTF-8"));
        MultipartEntityBuilder m = MultipartEntityBuilder.create();
        m.addTextBody("json", raw_json);
        m.addBinaryBody("file", file);
        org.apache.http.HttpEntity e = m.build();
        

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        
        urlConnection.addRequestProperty(e.getContentType().getName(), e.getContentType().getValue());
        urlConnection.addRequestProperty("Content-Length", String.valueOf(e.getContentLength()));
        
        urlConnection.setConnectTimeout(REQUEST_TIME_OUT_BIN);
        urlConnection.setReadTimeout(READ_TIME_OUT_BIN);
        urlConnection.setDoOutput(true);


        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        e.writeTo(wr);
        
        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    iStream
                )
        );
        StringBuffer sb = new StringBuffer();
        String line = "";
        
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        if (urlConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Error : " + urlConnection.getResponseMessage());
        }

        if (!"application/json".equals(urlConnection.getContentType())) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Internal server errors");
        }

        data = sb.toString();
        br.close();

        iStream.close();
        urlConnection.disconnect();
        
        return data;
    }

    public String readUrlPost(String url_s, String raw_json) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(url_s);
        byte[] paramB = raw_json.getBytes(Charset.forName("UTF-8"));

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setConnectTimeout(REQUEST_TIME_OUT);
        urlConnection.setReadTimeout(READ_TIME_OUT);
        urlConnection.setDoOutput(true);


        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.write(paramB);

        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                iStream));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        if (urlConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Error : " + urlConnection.getResponseMessage());
        }

        if (!"application/json".equals(urlConnection.getContentType())) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Internal server errors");
        }

        data = sb.toString();
        br.close();

        iStream.close();
        urlConnection.disconnect();
        return data;
    }
    
    public String readUrlHTMLPost(String url_s, String raw_json) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(url_s);
        byte[] paramB = raw_json.getBytes(Charset.forName("UTF-8"));
        //System.out.println(raw_json);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setConnectTimeout(REQUEST_TIME_OUT);
        urlConnection.setReadTimeout(READ_TIME_OUT);
        urlConnection.setDoOutput(true);


        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.write(paramB);

        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                iStream));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        if (urlConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Error : " + urlConnection.getResponseMessage());
        }

        data = sb.toString();
        br.close();

        iStream.close();
        urlConnection.disconnect();
        return data;
    }
    
    public String readUrlPost(String url_s, String[] headers, String raw_json) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        URL url = new URL(url_s);
        byte[] paramB = raw_json.getBytes(Charset.forName("UTF-8"));
        
        urlConnection = (HttpURLConnection) url.openConnection();
        if (headers != null)
            for (int i = 0; i < headers.length; ++i)
                urlConnection.setRequestProperty(
                        headers[i].split(":")[0],
                        headers[i].split(":")[1]
                );
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setConnectTimeout(REQUEST_TIME_OUT);
        urlConnection.setReadTimeout(READ_TIME_OUT);
        urlConnection.setDoOutput(true);


        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        wr.write(paramB);

        urlConnection.connect();
        iStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                iStream));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        if (urlConnection.getResponseCode() !=  HttpURLConnection.HTTP_OK) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Error : " + urlConnection.getResponseMessage());
        }

        if (!"application/json".equals(urlConnection.getContentType())) {
            br.close();
            iStream.close();
            urlConnection.disconnect();
            throw new Exception("Internal server errors");
        }

        data = sb.toString();
        br.close();

        iStream.close();
        urlConnection.disconnect();
        return data;
    }
    
    public class BinaryData {
        public BinaryData(byte[] file, int response) {
            this.file = file;
            this.response = response;
        }
        
        public byte[] file;
        public int response;
    }
    
    public static class RSDHHttp {
        //public static String ROOT = "http://36.91.115.133:2018/web-dev/webapps/pendaftaran/api";
        //public static final String ROOT_KTP = "http://api.rsdh.co.id/upload-ktp/upload.php";
        public static String ROOT = "http://api.rsdh.co.id";
        
        public static final String SET_STATUS_KAMAR = "set-status-kamar.php";
        public static final String GET_STATUS_KAMAR = "get-status-kamar.php";
        public static final String GET_KAMAR = "get-kamar.php";
        public static final String AUTH = "auth-bpjs.php";
        public static final String AUTH_PATIEN = "auth.php";
        public static final String AUTH_SUPERADMIN = "auth-superadmin.php";
        public static final String BPJS_NO_PESERTA = "get-no-bpjs.php";
        public static final String GET_BANGSAL = "get-bangsal.php";
        public static final String GET_OBAT = "get-obat.php";
        public static final String GET_DASHBOARD = "get-dashboard.php";
        public static final String GET_RIWAYAT_PASIEN = "get-riwayat-pasien.php";
        public static final String GET_JADWAL = "get-jadwal.php";
        public static final String GET_ENCRYPT_SURAT = "get-encrypt-surat.php";
        
        
        public static final String GET_KETERSEDIAAN_KAMAR = "get-ketersediaan-kamar.php";
        public static final String GET_EXTENTION_PHONE = "get-phone-extentions.php";
        
        public static final String SET_KTP = "upload-ktp/upload.php";
        public static final String GET_KTP = "upload-ktp/get-ktp.php";
        
        public static final String GET_EKLAIM = "inacbg/get-klaim.php";
        public static final String SET_EKLAIM = "inacbg/set-klaim.php";
        
        public static final String SET_RM = "upload-rm/upload.php";
        public static final String GET_RM = "upload-rm/get-doc.php";
        public static final String SET_SURAT = "upload-surat/upload.php";
        public static final String GET_SURAT = "upload-surat/get-doc.php";
        
        public static final String getUrlSetKTP(String act) {
            try {
                act = URLEncoder.encode(act, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + SET_KTP + "?act=" +  act;
        }
        
        public static final String getUrlKTP(String act) {
            try {
                act = URLEncoder.encode(act, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + GET_KTP + "?act=" +  act;
        }
        
        public static final String getUrlSetRM(String act) {
            try {
                act = URLEncoder.encode(act, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + SET_RM + "?act=" +  act;
        }
        
        public static final String getUrlRM(String act) {
            try {
                act = URLEncoder.encode(act, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + GET_RM + "?act=" +  act;
        }
        
        public static final String getUrlSetSurat(String act) {
            try {
                act = URLEncoder.encode(act, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + SET_SURAT + "?act=" +  act;
        }
        
        public static final String getUrlSurat(String act) {
            try {
                act = URLEncoder.encode(act, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + GET_SURAT + "?act=" +  act;
        }
        
        public static final String getUrlEklaim() {
            return ROOT + "/" + GET_EKLAIM;
        }
        
        public static final String getUrlSetEklaim() {
            return ROOT + "/" + SET_EKLAIM;
        }

        public static final String getUrlAuth(String user, String pass) {
            try {
                user = URLEncoder.encode(user, "UTF-8");
                pass = URLEncoder.encode(pass, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + AUTH + "?user=" +  user + "&pass=" + pass + new String(Base64.decodeBase64("JmlhdD1pbw=="));
        }

        public static final String getUrlAuthPatien(String userrm, String usertgl) {
            try {
                userrm = URLEncoder.encode(userrm, "UTF-8");
                usertgl = URLEncoder.encode(usertgl, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + AUTH_PATIEN + "?userrm=" +  userrm + "&usertgl=" + usertgl;
        }

        public static final String getUrlAuthSuperAdmin(String pass) {
            try {
                pass = URLEncoder.encode(pass, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + AUTH_SUPERADMIN + "?pass=" + pass;
        }

        public static final String getUrlSetKamarBed(String kodeKamar, String status) {
            try {
                kodeKamar = URLEncoder.encode(kodeKamar, "UTF-8");
                status= URLEncoder.encode(status, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + SET_STATUS_KAMAR + "?kdkamar=" +  kodeKamar + "&status=" + status ;
        }

        public static final String getUrlKamarBed(String kodeKamar) {
            try {
                kodeKamar = URLEncoder.encode(kodeKamar, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + GET_STATUS_KAMAR + "?kdkamar=" +  kodeKamar;
        }

        public static final String getUrlKamarBeds(String kamar) {
            try {
                kamar = URLEncoder.encode(kamar, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + GET_KAMAR + "?kam=" +  kamar;
        }

        public static final String getUrlNoPesertaBPJS(String noPeserta, String tglSEP) {
            try {
                noPeserta = URLEncoder.encode(noPeserta, "UTF-8");
                tglSEP = URLEncoder.encode(tglSEP, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + BPJS_NO_PESERTA + "?nopeserta=" +  noPeserta + "&tglSEP=" +tglSEP;
        }

        public static final String getUrlBangsal(){
            return ROOT + "/" + GET_BANGSAL;
        }

        public static final String getUrlObat(String kdBangsal, String key){
            try {
                kdBangsal = URLEncoder.encode(kdBangsal, "UTF-8");
                key = URLEncoder.encode(key, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return ROOT + "/" + GET_OBAT + "?kdbangsal=" + kdBangsal + "&key=" + key;
        }

        public static final String getUrlDashboard(String norm) {
            try {
                norm = URLEncoder.encode(norm, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return ROOT + "/" + GET_DASHBOARD + "?norm=" +norm;
        }

        public static final String getUrlRiwayatPasien(String norm) {
            try {
                norm = URLEncoder.encode(norm, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return ROOT + "/" + GET_RIWAYAT_PASIEN + "?norm=" +norm;
        }

        public static final String getUrlJadwal(String hari) {
            try {
                hari = URLEncoder.encode(hari, "UTF-8");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return ROOT + "/" + GET_JADWAL + "?hari=" + hari;
        }

        public static final String getUrlAllKamar() {

            return ROOT + "/" + GET_KETERSEDIAAN_KAMAR;
        }
        
        public static final String getUrlAllExtentionPohones() {

            return ROOT + "/" + GET_EXTENTION_PHONE;
        }
        
        public static final String getUrlEncSurat(String act) {

            return ROOT + "/" + GET_ENCRYPT_SURAT + "?act" + act;
        }

        public static String getHeaderAuth(String token) {
            return "Authorization:Bearer" + token;
        }
    }
}
