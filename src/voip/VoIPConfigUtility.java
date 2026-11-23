package voip;

import AESsecurity.EnkripsiAES;

/**
 * Utility class untuk enkripsi konfigurasi VoIP
 * Gunakan class ini untuk mengenkripsi nilai-nilai konfigurasi sebelum disimpan ke database.xml
 */
public class VoIPConfigUtility {
    
    public static void main(String[] args) {
        // Contoh enkripsi untuk konfigurasi VoIP
        System.out.println("=== VoIP Configuration Encryptor ===");
        
        // Server IP
        String serverIP = "192.168.88.119";
        String encryptedServerIP = EnkripsiAES.encrypt(serverIP);
        System.out.println("Server IP: " + serverIP);
        System.out.println("Encrypted: " + encryptedServerIP);
        System.out.println();
        
        // Port
        String port = "5061";
        String encryptedPort = EnkripsiAES.encrypt(port);
        System.out.println("Port: " + port);
        System.out.println("Encrypted: " + encryptedPort);
        System.out.println();
        
        // Enkripsi untuk user dan password
        System.out.println("--- Enkripsi user dan password ---");
        
        String user = "119";
        String encryptedUser = EnkripsiAES.encrypt(user);
        System.out.println("User: " + user);
        System.out.println("Encrypted: " + encryptedUser);
        System.out.println();
        
        String password = "119";
        String encryptedPassword = EnkripsiAES.encrypt(password);
        System.out.println("Password: " + password);
        System.out.println("Encrypted: " + encryptedPassword);
        System.out.println();
        
        String extension = "119";
        String encryptedExtension = EnkripsiAES.encrypt(extension);
        System.out.println("Extension: " + extension);
        System.out.println("Encrypted: " + encryptedExtension);
        
        System.out.println("\nCopy nilai encrypted di atas ke file setting/database.xml");
        System.out.println("pada entry yang sesuai (VOIPSIPSERVER, VOIPSIPPORT, dll)");
    }
}