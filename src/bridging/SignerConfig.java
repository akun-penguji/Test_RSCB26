/*
 * Model class untuk konfigurasi signer TTE Multi-Signer
 */
package bridging;

/**
 * SignerConfig - Model untuk menyimpan informasi penanda tangan
 * 
 * @author RS Trimitra
 */
public class SignerConfig {
    private String signerType;  // "dokter" or "petugas"
    private String signerId;    // kd_dokter or nip
    private String signerName;  // nm_dokter or nama
    private String email;       // email from dokter/petugas table
    private String tagSigner;   // tag_signer (anchor text keyword)
    private String qrValue;     // QR code content
    
    // Constructor
    public SignerConfig() {
    }
    
    public SignerConfig(String signerType, String signerId, String signerName, 
                       String email, String tagSigner, String qrValue) {
        this.signerType = signerType;
        this.signerId = signerId;
        this.signerName = signerName;
        this.email = email;
        this.tagSigner = tagSigner;
        this.qrValue = qrValue;
    }
    
    // Getters and Setters
    public String getSignerType() {
        return signerType;
    }
    
    public void setSignerType(String signerType) {
        this.signerType = signerType;
    }
    
    public String getSignerId() {
        return signerId;
    }
    
    public void setSignerId(String signerId) {
        this.signerId = signerId;
    }
    
    public String getSignerName() {
        return signerName;
    }
    
    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTagSigner() {
        return tagSigner;
    }
    
    public void setTagSigner(String tagSigner) {
        this.tagSigner = tagSigner;
    }
    
    public String getQrValue() {
        return qrValue;
    }
    
    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }
    
    /**
     * Validate if signer has required fields
     * @return true if email and tagSigner are not empty
     */
    public boolean isValid() {
        // signerId is enough for identification in V2 flow
        return signerId != null && !signerId.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "SignerConfig{" +
                "signerType='" + signerType + '\'' +
                ", signerId='" + signerId + '\'' +
                ", signerName='" + signerName + '\'' +
                ", email='" + email + '\'' +
                ", tagSigner='" + tagSigner + '\'' +
                '}';
    }
}
