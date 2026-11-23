/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import AESsecurity.EnkripsiAES;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author khanzasoft
 */
public class koneksiDB {
    private static Connection connection=null;
    private static final Properties prop = new Properties();  
    private static final MysqlDataSource dataSource=new MysqlDataSource();
    private static String var="";
    
    public koneksiDB(){} 
    public static synchronized Connection condb(){ 
        try {
            if (connection == null || connection.isClosed()) {
                try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
                    prop.loadFromXML(fis);
                    dataSource.setURL("jdbc:mysql://"+EnkripsiAES.decrypt(prop.getProperty("HOST"))+":"+EnkripsiAES.decrypt(prop.getProperty("PORT"))+"/"+EnkripsiAES.decrypt(prop.getProperty("DATABASE"))+"?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useCompression=true");
                    dataSource.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
                    dataSource.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
                    dataSource.setCachePreparedStatements(true);
                    dataSource.setUseCompression(true);
                    dataSource.setUseLocalSessionState(true);
                    dataSource.setUseLocalTransactionState(true);
                    
                    int retries = 3;
                    while (retries > 0) {
                        try {
                            connection=dataSource.getConnection();
                            System.out.println("  Koneksi Berhasil. Sorry bro loading, silahkan baca dulu.... \n\n"+
                                    "	Software ini adalah Software Menejemen Rumah Sakit/Klinik/\n" +
                                    "  Puskesmas yang  gratis dan boleh digunakan siapa saja tanpa dikenai \n" +
                                    "  biaya apapun. Dilarang keras memperjualbelikan/mengambil \n" +
                                    "  keuntungan dari Software ini dalam bentuk apapun tanpa seijin pembuat \n" +
                                    "  software (Khanza.Soft Media).\n"+
                                    "                                                                           \n"+
                                    "  #    ____  ___  __  __  ____   ____    _  __ _                              \n" +
                                    "  #   / ___||_ _||  \\/  ||  _ \\ / ___|  | |/ /| |__    __ _  _ __   ____ __ _ \n" +
                                    "  #   \\___ \\ | | | |\\/| || |_) |\\___ \\  | ' / | '_ \\  / _` || '_ \\ |_  // _` |\n" +
                                    "  #    ___) || | | |  | ||  _ <  ___) | | . \\ | | | || (_| || | | | / /| (_| |\n" +
                                    "  #   |____/|___||_|  |_||_| \\_\\|____/  |_|\\_\\|_| |_| \\__,_||_| |_|/___|\\__,_|\n" +
                                    "  #                                                                           \n"+
                                    "                                                                           \n"+
                                    "  Licensi yang dianut di software ini https://en.wikipedia.org/wiki/Aladdin_Free_Public_License \n"+
                                    "  Informasi dan panduan bisa dicek di halaman https://github.com/mas-elkhanza/SIMRS-Khanza/wiki \n"+
                                    "  Bagi yang ingin berdonasi untuk pengembangan aplikasi ini bisa ke BSI 1015369872 atas nama Windiarto\n"+
                                    "                                                                           ");
                            break;
                        } catch (SQLException e) {
                            retries--;
                            JOptionPane.showMessageDialog(null,"Gagal koneksi ke database. Sisa percobaan : " + retries);
                            if (retries == 0) {
                                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal. Silakan periksa koneksi jaringan atau konfigurasi database.");
                                throw new SQLException("Gagal koneksi ke database setelah beberapa percobaan.", e);
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new SQLException("Thread terinterupsi saat mencoba koneksi."+ie);
                            }
                        }
                    }
                }catch(IOException e){
                    throw new SQLException("Notif : "+e);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(koneksiDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;        
    }
    
    public static String TOKENWEBAPPS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("TOKENWEBAPPS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String HOST(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("HOST"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DATABASE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("DATABASE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PORT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PORT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USER"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPISNOWSTORM(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISNOWSTORM","");
        }catch(Exception e){
            var="";
        }
        return var;
    }

    public static String URLKYCPHP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLKYCPHP","");
        }catch(Exception e){
            var="";
        }
        return var;
    }

    public static String NOTIFWAPASIEN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("NOTIFWAPASIEN");
        }catch(Exception e){
            var="";
        }
        return var;
    }
    
    public static String HOSTWAWEB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("HOSTWAWEB"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PORTWEBWA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PORTWEBWA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String FOLDERFILEWA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("FOLDERFILEWA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String NOTIFWASURAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("NOTIFWASURAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDGROUPWASURAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("IDGROUPWASURAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String NOTIFWAIT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("NOTIFWAIT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDGROUPWAIT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("IDGROUPWAIT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CARICEPAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("CARICEPAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String HOSTHYBRIDWEB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("HOSTHYBRIDWEB"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERHYBRIDWEB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERHYBRIDWEB"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASHYBRIDWEB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASHYBRIDWEB"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String HYBRIDWEB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("HYBRIDWEB");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    /**
     * Token untuk endpoint unggah di webapps (header X-Upload-Token).
     *
     * Endpoint unggah dipanggil tanpa autentikasi sama sekali sebelumnya, yang
     * membuat siapa pun di jaringan bisa menulis berkas ke server. Token ini
     * menutupnya dari sisi klien.
     *
     * Sengaja mengembalikan string kosong kalau kunci belum ada atau gagal
     * didekripsi: sisi server berjalan dalam mode "catat" sampai seluruh JAR
     * tersebar, jadi workstation yang belum diperbarui tetap bisa mengunggah.
     * Mengembalikan null atau melempar di sini akan mematikan unggahan di
     * tengah peralihan.
     */
    public static String TOKENUNGGAH(){
        String nilai = "";
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            String mentah = prop.getProperty("TOKENUNGGAH");
            if (mentah != null && !mentah.trim().isEmpty()) {
                String hasil = EnkripsiAES.decrypt(mentah.trim());
                nilai = (hasil == null) ? "" : hasil;
            }
        }catch(Exception e){
            nilai = "";
        }
        return nilai;
    }

    public static String PORTWEB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("PORTWEB");
        }catch(Exception e){
            var="";
        }
        return var;
    }

    // URL publik EPasien (untuk link/QR hasil radiologi yang di-scan pasien).
    // Contoh: https://epasien.rsudcabangbungin.id (tanpa garis miring di akhir).
    public static String URLEPASIEN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLEPASIEN");
        }catch(Exception e){
            var="";
        }
        return var;
    }

    public static String URLWAGATEWAY(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var=prop.getProperty("URLWAGATEWAY");
            var=EnkripsiAES.decrypt(prop.getProperty("URLWAGATEWAY"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERWAGATEWAY(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
         //   var=prop.getProperty("USERWAGATEWAY");
            var=EnkripsiAES.decrypt(prop.getProperty("USERWAGATEWAY"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSWAGATEWAY(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var=prop.getProperty("PASSWAGATEWAY");
            var=EnkripsiAES.decrypt(prop.getProperty("PASSWAGATEWAY"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ANTRIAN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ANTRIAN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMAPOTEK(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMAPOTEK");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String FORMALARMAPOTEK(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("FORMALARMAPOTEK");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMLAB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMLAB");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String FORMALARMLAB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("FORMALARMLAB");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMRADIOLOGI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMRADIOLOGI");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String FORMALARMRADIOLOGI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("FORMALARMRADIOLOGI");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMRSISRUTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMRSISRUTE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMBOOKINGPERIKSA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMBOOKINGPERIKSA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMPERMINTAANRANAP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMPERMINTAANRANAP");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ALARMPENGADUANPASIEN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ALARMPENGADUANPASIEN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String MENUTRANSPARAN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("MENUTRANSPARAN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIBPJS");
        }catch(Exception e){
            var="";
        }
        return var;
    }

    public static String URLAPISMARTCLAIM(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISMARTCLAIM");
            if (var == null || var.isEmpty()) {
                var = prop.getProperty("URLAPIBPJS");
            }
        }catch(Exception e){
            var="";
        }
        return var;
    }
    
    public static String SECRETKEYAPIBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPIBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERNAMEAPIBSRE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERNAMEAPIBSRE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSWORDAPIBSRE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSWORDAPIBSRE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KEYEKLAIM(){
        return bacaSettingEklaim("KEYEKLAIM",true);
    }

    public static String WSEKLAIM(){
        return bacaSettingEklaim("WSEKLAIM",true);
    }

    public static String KELASRSEKLAIM(){
        return bacaSettingEklaim("KELASRSEKLAIM",false);
    }

    /**
     * Dibaca ke variabel lokal dan disinkronkan, supaya nilainya tidak ketukar
     * dengan setting lain saat getter koneksiDB lain dipanggil dari thread lain.
     */
    private static synchronized String bacaSettingEklaim(String key,boolean terenkripsi){
        String isi;
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            Properties lokal = new Properties();
            lokal.loadFromXML(fis);
            isi=terenkripsi?EnkripsiAES.decrypt(lokal.getProperty(key)):lokal.getProperty(key);
        }catch(Exception e){
            isi="";
        }
        return isi==null?"":isi.trim();
    }

    
    public static String GEMINIAPIKEY(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("GEMINIAPIKEY"));
            if(var == null || var.isEmpty()){
                var = "";
            }
        }catch(Exception e){
            var=""; 
            System.out.println("Error loading Gemini API Key: " + e.getMessage());
        }
        return var;
    }
    
    public static String GEMINIAPIURL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("GEMINIAPIURL");
            if(var == null || var.isEmpty()){
                var = "https://generativelanguage.googleapis.com/v1/models";
            }
        }catch(Exception e){
            var="https://generativelanguage.googleapis.com/v1/models"; 
            System.out.println("Error loading Gemini API URL, using default: " + e.getMessage());
        }
        return var;
    }
    
    public static String SPEECHSERVICE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("SPEECHSERVICE", "whisper");
            if(var == null || var.trim().isEmpty()){
                var = "whisper";
            }
            var = var.trim().toLowerCase();
        }catch(Exception e){
            var="whisper"; 
            System.out.println("Error loading Speech Service config, using default (whisper): " + e.getMessage());
        }
        return var;
    }
    
    public static boolean setGOOGLESPEECHAPIKEY(String apiKey){
        try {
            try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
                prop.loadFromXML(fis);
            }
            String encryptedKey = EnkripsiAES.encrypt(apiKey);
            prop.setProperty("GOOGLESPEECHAPIKEY", encryptedKey);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream("setting/database.xml")) {
                prop.storeToXML(fos, "Database Configuration with Google Speech API Key");
            }
            System.out.println("✅ Google Speech API Key encrypted and saved successfully");
            return true;
        }catch(Exception e){
            System.out.println("❌ Error saving Google Speech API Key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean setSPEECHSERVICE(String service){
        try {
            if(service == null || service.trim().isEmpty()){
                service = "whisper";
            }
            String normalizedService = service.trim().toLowerCase();
            if(!normalizedService.equals("whisper") && !normalizedService.equals("google")){
                System.out.println("⚠️ Invalid speech service: " + service + ", using whisper");
                normalizedService = "whisper";
            }
            try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
                prop.loadFromXML(fis);
            }
            prop.setProperty("SPEECHSERVICE", normalizedService);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream("setting/database.xml")) {
                prop.storeToXML(fos, "Database Configuration with Speech Service Selection");
            }
            System.out.println("✅ Speech Service set to: " + normalizedService);
            return true;
        }catch(Exception e){
            System.out.println("❌ Error saving Speech Service config: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Restore credentials RSUD Cabang Bungin ke database.xml (sudah terenkripsi, jangan dipanggil kalau XML sudah ada)
    public static boolean setSatuSehatCredentials() {
        try {
            try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
                prop.loadFromXML(fis);
            }
            prop.setProperty("CLIENTIDSATUSEHAT", "DUMMY_CLIENT_ID");
            prop.setProperty("SECRETKEYSATUSEHAT", "DUMMY_SECRET_KEY");
            prop.setProperty("IDSATUSEHAT", "DUMMY_ORG_ID");
            prop.setProperty("URLAUTHSATUSEHAT", "https://api-satusehat.kemkes.go.id/oauth2/v1");
            prop.setProperty("URLFHIRSATUSEHAT", "https://api-satusehat.kemkes.go.id/fhir-r4/v1");
            prop.setProperty("KELURAHANSATUSEHAT", "3216162002");
            prop.setProperty("KECAMATANSATUSEHAT", "321616");
            prop.setProperty("KABUPATENSATUSEHAT", "3216");
            prop.setProperty("PROPINSISATUSEHAT", "32");
            prop.setProperty("KODEPOSSATUSEHAT", "17720");
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream("setting/database.xml")) {
                prop.storeToXML(fos, "Database Configuration with Satu Sehat Credentials");
            }
            System.out.println("Satu Sehat credentials restored successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error restoring Satu Sehat credentials: " + e.getMessage());
            return false;
        }
    }

    public static String GOOGLESPEECHAPIKEY(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("GOOGLESPEECHAPIKEY"));
            if(var == null || var.isEmpty()){
                var = "";
            }
        }catch(Exception e){
            var=""; 
            System.out.println("Error loading Google Speech API Key: " + e.getMessage());
        }
        return var;
    }
    
    public static String WHISPERSERVERURL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("WHISPERSERVERURL");
            if(var == null || var.isEmpty()){
                var = "http://localhost:9000";
            }
        }catch(Exception e){
            var="http://localhost:9000"; 
            System.out.println("Error loading Whisper Server URL, using default: " + e.getMessage());
        }
        return var;
    }
    
    public static String URLNEXTCLOUD(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLNEXTCLOUD");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERNEXTCLOUD(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERNEXTCLOUD"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASNEXTCLOUD(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASNEXTCLOUD"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIAPLICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIAPLICARE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYAPIAPLICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIAPLICARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIAPLICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIAPLICARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPIAPLICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIAPLICARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIMOBILEJKN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIMOBILEJKN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYAPIMOBILEJKN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIMOBILEJKN"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIMOBILEJKN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIMOBILEJKN"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPIMOBILEJKN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIMOBILEJKN"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIAPOTEKBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIAPOTEKBPJS");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYAPIAPOTEKBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIAPOTEKBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIAPOTEKBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIAPOTEKBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPIAPOTEKBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIAPOTEKBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String JADIKANPIUTANGAPOTEKBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("JADIKANPIUTANGAPOTEKBPJS"); // plain (samakan dg SIMRS-Khanza), isi "yes"/"no"
        }catch(Exception e){
            var="no";
        }
        // key belum ada di database.xml → getProperty balikin null,
        // default "no" supaya pemanggil (JADIKANPIUTANGAPOTEKBPJS.equals(...)) tidak NPE.
        if (var == null || var.trim().isEmpty()) {
            var = "no";
        }
        return var;
    }
    
    public static String URLAPIPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIPCARE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYAPIPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIPCARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIPCARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPIPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIPCARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSPCARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERPCARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DIVREGPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("DIVREGPCARE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KACABPCARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KACABPCARE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPISISRUTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISISRUTE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDSISRUTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("IDSISRUTE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSSISRUTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSSISRUTE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPISIRS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISIRS");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDSIRS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("IDSIRS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSSIRS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSSIRS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPICORONA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPICORONA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDCORONA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("IDCORONA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSCORONA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSCORONA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPISITT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISITT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDSITT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("IDSITT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSSITT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSSITT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KABUPATENSITT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KABUPATENSITT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KAMARAKTIFRANAP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KAMARAKTIFRANAP").replaceAll("'","");;
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DOKTERAKTIFKASIRRALAN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("DOKTERAKTIFKASIRRALAN").replaceAll("'","");;
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String POLIAKTIFKASIRRALAN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("POLIAKTIFKASIRRALAN").replaceAll("'","");;
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String RUANGANAKTIFINVENTARIS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("RUANGANAKTIFINVENTARIS").replaceAll("'","");;
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String BASENOREG(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("BASENOREG");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String VALIDASIULANGBERIOBAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("VALIDASIULANGBERIOBAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URUTNOREG(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URUTNOREG");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String JADWALDOKTERDIREGISTRASI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("JADWALDOKTERDIREGISTRASI");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IPPRINTERTRACER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("IPPRINTERTRACER");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIINHEALTH(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIINHEALTH");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String TOKENINHEALTH(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("TOKENINHEALTH"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PEMBULATANHARGAOBAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("PEMBULATANHARGAOBAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String AKTIFKANBATCHOBAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("AKTIFKANBATCHOBAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CETAKRINCIANOBAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("CETAKRINCIANOBAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String AKTIFKANBILLINGPARSIAL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("AKTIFKANBILLINGPARSIAL");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLDUKCAPILJAKARTA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLDUKCAPILJAKARTA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERDUKCAPILJAKARTA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERDUKCAPILJAKARTA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSDUKCAPILJAKARTA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSDUKCAPILJAKARTA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String VAR1DUKCAPILJAKARTA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("VAR1DUKCAPILJAKARTA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String VAR2DUKCAPILJAKARTA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("VAR2DUKCAPILJAKARTA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLDUKCAPIL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLDUKCAPIL");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERDUKCAPIL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERDUKCAPIL"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSDUKCAPIL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSDUKCAPIL"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IPUSERDUKCAPIL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("IPUSERDUKCAPIL");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String AKTIFKANTRACKSQL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("AKTIFKANTRACKSQL"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String HOSTWSLICA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("HOSTWSLICA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KEYWSLICA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("KEYWSLICA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DEPOAKTIFOBAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("DEPOAKTIFOBAT").replaceAll("'","");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String STOKKOSONGRESEP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("STOKKOSONGRESEP");
        }catch(Exception e){
            var="no";
        }
        return var;
    }

    public static String IZINKANJAMMANUALRESEP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("IZINKANJAMMANUALRESEP");
        }catch(Exception e){
            var="no";
        }
        if(var==null){ var="no"; }
        return var;
    }
    
    public static String TAMPILKANCOPYRESEPDOKTERLAIN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("TAMPILKANCOPYRESEPDOKTERLAIN");
        }catch(Exception e){
            var="no"; 
        }
        return var;
    }
    
    public static String HPPFARMASI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            if(prop.getProperty("HPPFARMASI").equals("h_beli")){
                var="h_beli";
            }else{
                var="dasar";
            }
        }catch(Exception e){
            var="dasar"; 
        }
        return var;
    }
    
    public static String HPPTOKO(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            if(prop.getProperty("HPPTOKO").equals("h_beli")){
                var="h_beli";
            }else{
                var="dasar";
            }
        }catch(Exception e){
            var="dasar"; 
        }
        return var;
    }
    
    public static String URLAPIMEDQLAB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIMEDQLAB");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYAPIMEDQLAB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIMEDQLAB"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIMEDQLAB(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIMEDQLAB"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLCARESTREAM(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLCARESTREAM");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPISOFTMEDIX(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISOFTMEDIX");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PRODUCTSOFTMEDIX(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PRODUCTSOFTMEDIX"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String VERSIONSOFTMEDIX(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("VERSIONSOFTMEDIX"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERIDSOFTMEDIX(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERIDSOFTMEDIX"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KEYSOFTMEDIX(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("KEYSOFTMEDIX"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String RESEPRAJALKEPLAN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("RESEPRAJALKEPLAN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String TIKETANTRIANFARMASI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("TIKETANTRIANFARMASI","no");
        }catch(Exception e){
            var="no";
        }
        return var;
    }

    public static String PRINTERTIKETFARMASI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("PRINTERTIKETFARMASI","");
        }catch(Exception e){
            var="";
        }
        return var;
    }

    public static String TIKETANTRIANFARMASIPREVIEW(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("TIKETANTRIANFARMASIPREVIEW","no");
        }catch(Exception e){
            var="no";
        }
        return var;
    }

    public static String DIAGNOSARUJUKANMASUKAPIBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("DIAGNOSARUJUKANMASUKAPIBPJS");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String AKTIFKANWARNARALAN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("AKTIFKANWARNARALAN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CLIENTIDSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CLIENTIDSATUSEHAT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYSATUSEHAT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String IDSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("IDSATUSEHAT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAUTHSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAUTHSATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLFHIRSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLFHIRSATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    /**
     * Basis URL applink TTE yang dipindai SATUSEHAT Mobile dari QR.
     * Staging : https://applink-stg.dto.kemkes.go.id/ssm/tte
     * Produksi: https://applink.kemkes.go.id/ssm/tte
     * Isi QR = basis + "/{taskUuid}/{timestamp}". Dibuat konfigurabel supaya
     * pindah staging&lt;-&gt;produksi cukup ganti setting, tanpa build ulang.
     */
    public static String URLAPPLINKTTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPPLINKTTE");
        }catch(Exception e){
            var="";
        }
        return var==null?"":var;
    }

    /**
     * Saklar auto-refresh status pada panel QR TTE. Bernilai "true"/"1"/"ya"/"aktif"
     * (huruf besar-kecil bebas) berarti AKTIF; nilai lain atau key tak ada berarti
     * MATI (default). Dipakai sebagai cadangan bila webhook SatuSehat bermasalah:
     * saat aktif, panel QR menjajaki status Task langsung ke server tiap 5 detik.
     */
    /**
     * Basis URL layanan RIS (DokterTeknoRIS) yang mengonversi dokumen/gambar
     * menjadi DICOM lalu meneruskannya ke DICOM Router. Contoh:
     * http://10.99.1.10:5000  (endpoint dipakai: /api/v1/tools/document-to-dicom).
     * Kosong = fitur kirim DICOM di form Bundel dimatikan.
     */
    public static String URLDICOMRIS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLDICOMRIS");
        }catch(Exception e){
            var="";
        }
        return var==null?"":var;
    }

    /** Nama pengguna Basic Auth ke layanan RIS DICOM. */
    public static String USERDICOMRIS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("USERDICOMRIS");
        }catch(Exception e){
            var="";
        }
        return var==null?"":var;
    }

    /** Kata sandi Basic Auth ke layanan RIS DICOM. */
    public static String PASSDICOMRIS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("PASSDICOMRIS");
        }catch(Exception e){
            var="";
        }
        return var==null?"":var;
    }

    public static String AUTOREFRESHTTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("AUTOREFRESHTTE");
        }catch(Exception e){
            var="";
        }
        return var==null?"":var;
    }

    public static String KELURAHANSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KELURAHANSATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KECAMATANSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KECAMATANSATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KABUPATENSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KABUPATENSATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PROPINSISATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("PROPINSISATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KODEPOSSATUSEHAT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("KODEPOSSATUSEHAT");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERORTHANC"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSORTHANC"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PORTORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PORTORTHANC"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLORTHANC");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ADDANTRIANAPIMOBILEJKN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ADDANTRIANAPIMOBILEJKN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String JADIKANBOOKINGSURATKONTROL(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("JADIKANBOOKINGSURATKONTROL");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String JADIKANBOOKINGSURATKONTROLAPIBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("JADIKANBOOKINGSURATKONTROLAPIBPJS");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIICARE");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYAPIICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPIICARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPIICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPIICARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPIICARE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPIICARE"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
//    public static String URLAPISMARTCLAIM(){
//        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
//            prop.loadFromXML(fis);
//            var=prop.getProperty("URLAPISMARTCLAIM");
//        }catch(Exception e){
//            var=""; 
//        }
//        return var;
//    }
    
    public static String SECRETKEYAPISMARTCLAIM(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYAPISMARTCLAIM"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDAPISMARTCLAIM(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDAPISMARTCLAIM"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYAPISMARTCLAIM(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYAPISMARTCLAIM"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String TANGGALMUNDUR(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("TANGGALMUNDUR");
        }catch(Exception e){
            var="yes"; 
        }
        return var;
    }
    
    public static String URLMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLMOBILEJKNFKTP");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SECRETKEYMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SECRETKEYMOBILEJKNFKTP"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CONSIDMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CONSIDMOBILEJKNFKTP"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERKEYMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERKEYMOBILEJKNFKTP"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSMOBILEJKNFKTP"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERMOBILEJKNFKTP"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPATHPEMBAYARANPIHAKKETIGA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPATHPEMBAYARANPIHAKKETIGA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPATHPEMBAYARANPAJAK(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPATHPEMBAYARANPAJAK"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPATHPEMBAYARANVIRTUALACCOUNT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPATHPEMBAYARANVIRTUALACCOUNT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPATHACK(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPATHACK"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPATHMT940(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPATHMT940"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIHOST(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIHOST"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPORT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPORT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIUSER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIUSER"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPMANDIRIPAS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPMANDIRIPAS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String KUNCIDOKTERRANAP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("KUNCIDOKTERRANAP"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String ADDANTRIANAPIMOBILEJKNFKTP(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("ADDANTRIANAPIMOBILEJKNFKTP");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIESIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIESIGN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERNAMEAPIESIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERNAMEAPIESIGN"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSAPIESIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSAPIESIGN"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPFILEESIGNHOST(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPFILEESIGNHOST"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPFILEESIGNPORT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPFILEESIGNPORT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPFILEESIGNUSER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPFILEESIGNUSER"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPFILEESIGNPAS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPFILEESIGNPAS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String SFTPFILEESIGNFOLDER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("SFTPFILEESIGNFOLDER"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAKSESFILEESIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAKSESFILEESIGN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLVERIFIKASI(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("URLVERIFIKASI"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLVERIFIKASITTE(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            String val = prop.getProperty("URLVERIFIKASITTE");
            if (val != null && !val.trim().isEmpty()) {
                try {
                    String dec = EnkripsiAES.decrypt(val);
                    if (dec != null && !dec.trim().isEmpty()) {
                        return dec;
                    }
                } catch (Exception ignored) {}
                return val;
            }
        } catch (Exception e) {
            var = ""; 
        }
        return "simrs.rsudcabangbungin.id";
    }
    
    public static String URLAPISERTISIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPISERTISIGN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String APIKEYSERTISIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("APIKEYSERTISIGN"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLDOKUMENSERTISIGN(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLDOKUMENSERTISIGN");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPIESIGNV2(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPIESIGNV2");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERNAMEAPIESIGNV2(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERNAMEAPIESIGNV2"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSAPIESIGNV2(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSAPIESIGNV2"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAKSESFILEESIGNV2(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAKSESFILEESIGNV2");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAUTHTILAKA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAUTHTILAKA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CLIENTIDTILAKA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CLIENTIDTILAKA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String CLIENTSECRETTILAKA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("CLIENTSECRETTILAKA"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String URLAPITILAKA(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("URLAPITILAKA");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PORTAPIORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=prop.getProperty("PORTAPIORTHANC");
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String USERAPIORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("USERAPIORTHANC"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String PASSAPIORTHANC(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("PASSAPIORTHANC"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DECRIPSIPSERVER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("DECRIPSIPSERVER"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DECRIPSIPPORT(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("DECRIPSIPPORT"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DECRIPSIPUSER(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("DECRIPSIPUSER"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DECRIPSIPPASSWORD(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("DECRIPSIPPASSWORD"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }
    
    public static String DECRIPSIPEXTENSION(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("DECRIPSIPEXTENSION"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }

    public static String GOWADEVICEID() {
        try ( FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var = prop.getProperty("GOWA_DEVICE_ID");
            var=EnkripsiAES.decrypt(prop.getProperty("GOWA_DEVICE_ID"));
            if (var == null || var.isEmpty()) {
                var = ""; 
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID: " + e.getMessage());
        }
        return var;
    }

    /** MAC address PC lokal (format XX:XX:XX:XX:XX:XX), untuk mapping device WA
     *  per komputer via whatsapp.DlgMapMacDevice. "" bila tidak terdeteksi. */
    public static String getLocalMacAddress() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> nis = java.net.NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                java.net.NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual() || ni.isPointToPoint()) continue;
                byte[] mac = ni.getHardwareAddress();
                if (mac == null || mac.length != 6) continue;
                boolean allZero = true;
                for (byte b : mac) if (b != 0) { allZero = false; break; }
                if (allZero) continue;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X", mac[i]));
                    if (i < mac.length - 1) sb.append(":");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            System.out.println("getLocalMacAddress: " + e);
        }
        return "";
    }

    /** device_id gowa hasil mapping MAC PC ini (tabel wa_mac_device_mapping).
     *  "" bila MAC tak ter-mapping — pemanggil fallback ke device global. */
    public static String GOWADEVICEID_BYMAC() {
        String mac = getLocalMacAddress();
        if (mac.isEmpty()) return "";
        try (java.sql.PreparedStatement ps = condb().prepareStatement(
                "select device_id from wa_mac_device_mapping where mac_address=? limit 1")) {
            ps.setString(1, mac);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String d = rs.getString(1);
                    if (d != null && !d.trim().isEmpty()) return d.trim();
                }
            }
        } catch (Exception e) {
            System.out.println("GOWADEVICEID_BYMAC: " + e);
        }
        return "";
    }

    public static String GOWADEVICEIDSKM() {
        try ( FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var = prop.getProperty("GOWA_DEVICE_ID");
            var=EnkripsiAES.decrypt(prop.getProperty("GOWA_DEVICE_ID_SKM"));
            if (var == null || var.isEmpty()) {
                var = ""; 
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID: " + e.getMessage());
        }
        return var;
    }
    
    public static String GOWADEVICEIDBATALPRAKTEK() {
        try ( FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var = prop.getProperty("GOWA_DEVICE_ID");
            var=EnkripsiAES.decrypt(prop.getProperty("GOWA_DEVICE_ID_BATAL_PRAKTEK"));
            if (var == null || var.isEmpty()) {
                var = ""; 
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID: " + e.getMessage());
        }
        return var;
    }
    
    public static String GOWADEVICEIDLAB() {
        try ( FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var = prop.getProperty("GOWA_DEVICE_ID");
            var=EnkripsiAES.decrypt(prop.getProperty("GOWA_DEVICE_ID_LAB"));
            if (var == null || var.isEmpty()) {
                var = ""; 
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID: " + e.getMessage());
        }
        return var;
    }
    
    public static String GOWADEVICEIDRADIOLOGI() {
        try ( FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var = prop.getProperty("GOWA_DEVICE_ID");
            var=EnkripsiAES.decrypt(prop.getProperty("GOWA_DEVICE_ID_RADIOLOGI"));
            if (var == null || var.isEmpty()) {
                var = ""; 
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID: " + e.getMessage());
        }
        return var;
    }
    
    public static String GOWADEVICEIDKONTROL() {
        try ( FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
        //    var = prop.getProperty("GOWA_DEVICE_ID");
            var=EnkripsiAES.decrypt(prop.getProperty("GOWA_DEVICE_ID_KONTROL"));
            if (var == null || var.isEmpty()) {
                var = "";
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID: " + e.getMessage());
        }
        return var;
    }

    public static String GOWADEVICEIDFARMASIGUDANG() {
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            String raw = prop.getProperty("GOWA_DEVICE_ID_FARMASIGUDANG");
            if (raw == null || raw.isEmpty()) {
                var = "";
            } else {
                var = EnkripsiAES.decrypt(raw);
                if (var == null) var = "";
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading GOWA Device ID Farmasi Gudang: " + e.getMessage());
        }
        if (var == null || var.isEmpty()) {
            var = GOWADEVICEID();
        }
        return var;
    }

    public static String IDGROUPWAFARMASIGUDANG() {
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var = prop.getProperty("IDGROUPWAFARMASIGUDANG");
            if (var == null) var = "";
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading IDGROUPWAFARMASIGUDANG: " + e.getMessage());
        }
        return var;
    }

    public static String NOTIFWAFARMASIGUDANG() {
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var = prop.getProperty("NOTIFWAFARMASIGUDANG");
            if (var == null || var.isEmpty()) var = "no";
        } catch (Exception e) {
            var = "no";
            System.out.println("Error loading NOTIFWAFARMASIGUDANG: " + e.getMessage());
        }
        return var;
    }

    public static String URLSURVEIKEPUASAN() {
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var = prop.getProperty("URLSURVEIKEPUASAN");
            if (var == null || var.isEmpty()) {
                var = "";
            }
        } catch (Exception e) {
            var = "";
            System.out.println("Error loading URL Survei Kepuasan: " + e.getMessage());
        }
        return var;
    }

    public static String SYNOLOGYURLLOCAL() {
        try { prop.loadFromXML(new FileInputStream("setting/database.xml")); var = EnkripsiAES.decrypt(prop.getProperty("SYNOLOGYURLLOCAL")); } catch (Exception e) { var = ""; }
        return var;
    }

    public static String SYNOLOGYURLONLINE() {
        try { prop.loadFromXML(new FileInputStream("setting/database.xml")); var = EnkripsiAES.decrypt(prop.getProperty("SYNOLOGYURLONLINE")); } catch (Exception e) { var = ""; }
        return var;
    }

    public static String SYNOLOGYUSER() {
        try { prop.loadFromXML(new FileInputStream("setting/database.xml")); var = EnkripsiAES.decrypt(prop.getProperty("SYNOLOGYUSER")); } catch (Exception e) { var = ""; }
        return var;
    }

    public static String SYNOLOGYPASS() {
        try { prop.loadFromXML(new FileInputStream("setting/database.xml")); var = EnkripsiAES.decrypt(prop.getProperty("SYNOLOGYPASS")); } catch (Exception e) { var = ""; }
        return var;
    }

    public static String SYNOLOGYFOLDER() {
        try { prop.loadFromXML(new FileInputStream("setting/database.xml")); var = EnkripsiAES.decrypt(prop.getProperty("SYNOLOGYFOLDER")); } catch (Exception e) { var = ""; }
        return var;
    }

    public static String KODEPPKAPOTEKBPJS(){
        try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fis);
            var=EnkripsiAES.decrypt(prop.getProperty("KODEPPKAPOTEKBPJS"));
        }catch(Exception e){
            var=""; 
        }
        return var;
    }

}