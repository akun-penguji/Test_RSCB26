package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Dialog SATUSEHAT RME — consent (CHL) lalu buka RME Nasional (SHL).
 * Panggil setNoRm(noRm, nmPasien) lalu tampil().
 */
public class SatuSehatRMEDialog extends JDialog {

    private final sekuel Sequel = new sekuel();
    private String noRm = "", nmPasien = "";
    private final JLabel lblInfo = new JLabel("Pilih pasien terlebih dahulu");
    private final JButton btnSHL = new JButton("Buka RME Nasional");

    public SatuSehatRMEDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("SATUSEHAT RME");
        setSize(900, 650);
        setLocationRelativeTo(parent);

        JPanel top = new JPanel(new FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        btnSHL.setEnabled(false);
        btnSHL.addActionListener(e -> tampilSHL());
        top.add(lblInfo);
        top.add(btnSHL);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
    }

    private String stripSapaan(String nama) {
        if (nama == null) return "";
        return nama.trim().replaceFirst("(?i)^(SDR\\.|SDI\\.|NY\\.|NYA\\.|TN\\.|AN\\.|BAYI\\s+NY\\.?|BAYI\\s+TN\\.?|BAYI\\b)\\s*", "").trim();
    }

    public void setNoRm(String noRm, String nmPasien) {
        this.noRm = noRm;
        this.nmPasien = nmPasien;
    }

    public void tampil() {
        if (noRm.isEmpty()) return;
        btnSHL.setEnabled(false);
        lblInfo.setText("Menghubungi SATUSEHAT...");
        new Thread(() -> {
            try {
                String nikPasien = Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis=?", noRm);
                if (nikPasien.isEmpty()) {
                    SwingUtilities.invokeLater(() -> lblInfo.setText("NIK pasien tidak tersedia di data SIMRS"));
                    return;
                }
                SatuSehatCekNIK cek = new SatuSehatCekNIK();
                String idPasien = Sequel.cariIsi("select ihs_number from satu_sehat_cache_ihs_pasien where no_ktp=?", nikPasien);
                if (idPasien == null || idPasien.isEmpty()) {
                    idPasien = cek.tampilIDPasien(nikPasien);
                }
                if (idPasien == null || idPasien.isEmpty()) {
                    SwingUtilities.invokeLater(() -> lblInfo.setText("Pasien belum terdaftar di SATUSEHAT — lakukan KYC terlebih dahulu"));
                    return;
                }
                String nikDokter = Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode());
                String nmDokter  = Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?", akses.getkode());
                String idDokter  = nikDokter.isEmpty() ? "" : cek.tampilIDParktisi(nikDokter);
                String idOrg = koneksiDB.IDSATUSEHAT();
                String nmOrg = akses.getnamars();

                ApiSatuSehat api = new ApiSatuSehat();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                String baseUrl = koneksiDB.URLAUTHSATUSEHAT().replace("/oauth2/v1", "");
                String body = "{\"patient_id\":\"" + idPasien + "\",\"patient_name\":\"" + stripSapaan(nmPasien) +
                    "\",\"practitioner_id\":\"" + idDokter + "\",\"practitioner_name\":\"" + nmDokter +
                    "\",\"organization_id\":\"" + idOrg + "\",\"organization_name\":\"" + nmOrg + "\"}";
                System.out.println("SSRME CHL body: " + body);
                HttpEntity reqEntity = new HttpEntity(body, headers);
                String resp;
                try {
                    resp = api.getRest().exchange(baseUrl + "/ssrme/v2/ntl/chl", HttpMethod.POST, reqEntity, String.class).getBody();
                } catch (org.springframework.web.client.HttpStatusCodeException ex) {
                    String errBody = ex.getResponseBodyAsString();
                    System.out.println("SSRME CHL error " + ex.getStatusCode() + ": " + errBody);
                    try {
                        String errMsg = new ObjectMapper().readTree(errBody).path("message").asText();
                        if (!errMsg.isEmpty()) {
                            SwingUtilities.invokeLater(() -> {
                                lblInfo.setText("SATUSEHAT: " + errMsg);
                                if (errMsg.contains("belum dibagikan")) {
                                    JOptionPane.showMessageDialog(SatuSehatRMEDialog.this,
                                        "Pasien belum mengaktifkan berbagi resume medis.\n\n" +
                                        "Minta pasien:\n" +
                                        "1. Download aplikasi SATUSEHAT Mobile\n" +
                                        "2. Lakukan verifikasi identitas (KYC)\n" +
                                        "3. Aktifkan 'Bagikan Resume Medis' di pengaturan aplikasi",
                                        "SATUSEHAT RME", JOptionPane.WARNING_MESSAGE);
                                }
                            });
                            return;
                        }
                    } catch (Exception ignored) {}
                    throw ex;
                }
                JsonNode root = new ObjectMapper().readTree(resp);
                String verificationUrl = root.path("data").path("verificationUrl").asText();
                if (!verificationUrl.isEmpty()) {
                    System.out.println("SSRME CHL verificationUrl: " + verificationUrl);
                    loadURL(verificationUrl);
                    SwingUtilities.invokeLater(() -> {
                        lblInfo.setText("Halaman persetujuan dibuka di browser — masukkan kode dari SATUSEHAT Mobile, lalu klik Buka RME Nasional");
                        btnSHL.setEnabled(true);
                    });
                } else {
                    String msg = root.path("message").asText();
                    SwingUtilities.invokeLater(() -> lblInfo.setText("Gagal memuat consent: " + msg));
                }
            } catch (Exception e) {
                System.out.println("Notif SSRME CHL: " + e);
                SwingUtilities.invokeLater(() -> lblInfo.setText("Gagal: " + e.getMessage()));
            }
        }).start();
    }

    private void tampilSHL() {
        btnSHL.setEnabled(false);
        lblInfo.setText("Membuka RME Nasional...");
        new Thread(() -> {
            try {
                String nikPasien = Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis=?", noRm);
                SatuSehatCekNIK cek = new SatuSehatCekNIK();
                String idPasien = Sequel.cariIsi("select ihs_number from satu_sehat_cache_ihs_pasien where no_ktp=?", nikPasien);
                if (idPasien == null || idPasien.isEmpty()) {
                    idPasien = cek.tampilIDPasien(nikPasien);
                }
                String nikDokter = Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode());
                String nmDokter  = Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?", akses.getkode());
                String idDokter  = nikDokter.isEmpty() ? "" : cek.tampilIDParktisi(nikDokter);
                String idOrg = koneksiDB.IDSATUSEHAT();
                String nmOrg = akses.getnamars();

                ApiSatuSehat api = new ApiSatuSehat();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                String baseUrl = koneksiDB.URLAUTHSATUSEHAT().replace("/oauth2/v1", "");
                String body = "{\"patient_id\":\"" + idPasien + "\",\"patient_name\":\"" + stripSapaan(nmPasien) +
                    "\",\"practitioner_id\":\"" + idDokter + "\",\"practitioner_name\":\"" + nmDokter +
                    "\",\"organization_id\":\"" + idOrg + "\",\"organization_name\":\"" + nmOrg + "\"}";
                HttpEntity reqEntity = new HttpEntity(body, headers);
                String resp;
                try {
                    resp = api.getRest().exchange(baseUrl + "/ssrme/v2/ntl/shl", HttpMethod.POST, reqEntity, String.class).getBody();
                } catch (org.springframework.web.client.HttpStatusCodeException ex) {
                    String errBody = ex.getResponseBodyAsString();
                    System.out.println("SSRME SHL error " + ex.getStatusCode() + ": " + errBody);
                    try {
                        String errMsg = new ObjectMapper().readTree(errBody).path("message").asText();
                        if (errMsg.isEmpty()) errMsg = ex.getStatusCode().toString();
                        final String fm = errMsg;
                        SwingUtilities.invokeLater(() -> {
                            lblInfo.setText("Persetujuan belum selesai — " + fm);
                            btnSHL.setEnabled(true);
                            JOptionPane.showMessageDialog(SatuSehatRMEDialog.this,
                                "Persetujuan pasien belum diterima.\n\n" + fm +
                                "\n\nMinta pasien masukkan kode dari SATUSEHAT Mobile\npada halaman persetujuan di atas, lalu coba lagi.",
                                "SATUSEHAT RME", JOptionPane.WARNING_MESSAGE);
                        });
                        return;
                    } catch (Exception ignored) {}
                    throw ex;
                }
                JsonNode root = new ObjectMapper().readTree(resp);
                if (root.path("success").asBoolean()) {
                    String shlinkUrl = root.path("data").path("shlinkUrl").asText();
                    loadURL(shlinkUrl);
                    SwingUtilities.invokeLater(() -> lblInfo.setText("RME Nasional terbuka di browser"));
                } else {
                    String msg = root.path("message").asText();
                    SwingUtilities.invokeLater(() -> {
                        lblInfo.setText("Persetujuan belum diberikan: " + msg);
                        btnSHL.setEnabled(true);
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif SSRME SHL: " + e);
                SwingUtilities.invokeLater(() -> {
                    lblInfo.setText("Gagal membuka RME Nasional: " + e.getMessage());
                    btnSHL.setEnabled(true);
                });
            }
        }).start();
    }

    private void loadURL(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            System.out.println("SSRME gagal buka browser: " + e);
            SwingUtilities.invokeLater(() -> lblInfo.setText("Gagal membuka browser: " + e.getMessage()));
        }
    }
}
