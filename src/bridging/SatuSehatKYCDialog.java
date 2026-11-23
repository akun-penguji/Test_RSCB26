package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.URI;
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
import org.springframework.web.client.RestTemplate;

/**
 * Dialog SATUSEHAT KYC — generate URL untuk selfie pasien atau verifikasi NIK.
 * Panggil setData(nikPasien, nmPasien) lalu setVisible(true).
 */
public class SatuSehatKYCDialog extends JDialog {

    private final sekuel Sequel = new sekuel();
    private String nikPasien = "", nmPasien = "";

    private final JLabel lblPasien = new JLabel("Pasien: -");
    private final JLabel lblStatus = new JLabel("Siap");

    public SatuSehatKYCDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("SATUSEHAT KYC");
        setSize(550, 280);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel top = new JPanel(new FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        top.add(lblPasien);

        JPanel middle = new JPanel(new FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        middle.add(lblStatus);

        JButton btnKYCBrowser = new JButton("KYC via Browser");
        btnKYCBrowser.addActionListener(e -> generateUrl());

        JButton btnVerifikasiNIK = new JButton("Verifikasi via NIK");
        btnVerifikasiNIK.addActionListener(e -> challengeCode());

        JButton btnTutup = new JButton("Tutup");
        btnTutup.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(java.awt.FlowLayout.CENTER, 8, 4));
        bottom.add(btnKYCBrowser);
        bottom.add(btnVerifikasiNIK);
        bottom.add(btnTutup);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(middle, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    public void setData(String nikPasien, String nmPasien) {
        this.nikPasien = nikPasien == null ? "" : nikPasien.trim();
        this.nmPasien  = nmPasien  == null ? "" : nmPasien.trim().replaceFirst("(?i)^(SDR\\.|SDI\\.|NY\\.|NYA\\.|TN\\.|AN\\.|BAYI\\s+NY\\.?|BAYI\\s+TN\\.?|BAYI\\b)\\s*", "").trim();
        lblPasien.setText("Pasien: " + this.nmPasien + " | NIK: " + this.nikPasien);
        lblStatus.setText("Siap");
    }

    private void generateUrl() {
        if (nikPasien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIK pasien belum diisi.");
            return;
        }
        SwingUtilities.invokeLater(() -> lblStatus.setText("Menghubungi server KYC..."));
        new Thread(() -> {
            try {
                String agentNik  = Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode());
                String agentName = Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?", akses.getkode());

                String url = koneksiDB.URLKYCPHP();
                if (url == null || url.isEmpty()) {
                    SwingUtilities.invokeLater(() -> lblStatus.setText("URL KYC PHP belum dikonfigurasi di database.xml"));
                    return;
                }

                String body = "{\"action\":\"generate_url\",\"agent_name\":\"" + agentName + "\",\"agent_nik\":\"" + agentNik + "\"}";
                System.out.println("KYC generate_url: POST " + url + " body=" + body);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                RestTemplate rt = new RestTemplate();
                String response = rt.exchange(url, HttpMethod.POST, entity, String.class).getBody();
                System.out.println("KYC generate_url response: " + response);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response);
                if (root.path("success").asBoolean()) {
                    String kycUrl = root.path("url").asText();
                    if (kycUrl != null && !kycUrl.isEmpty()) {
                        java.awt.Desktop.getDesktop().browse(new URI(kycUrl));
                        SwingUtilities.invokeLater(() -> lblStatus.setText("Halaman KYC dibuka di browser"));
                    } else {
                        SwingUtilities.invokeLater(() -> lblStatus.setText("URL KYC tidak tersedia dalam respons"));
                    }
                } else {
                    JsonNode errNode = root.path("error");
                    String err = (errNode != null && !errNode.isMissingNode()) ? errNode.asText() : "Gagal mendapatkan URL KYC";
                    SwingUtilities.invokeLater(() -> lblStatus.setText("Error: " + err));
                }
            } catch (Exception ex) {
                System.out.println("KYC generate_url error: " + ex);
                SwingUtilities.invokeLater(() -> lblStatus.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }

    private void challengeCode() {
        if (nikPasien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIK pasien belum diisi.");
            return;
        }
        SwingUtilities.invokeLater(() -> lblStatus.setText("Memverifikasi NIK pasien..."));
        new Thread(() -> {
            try {
                String url = koneksiDB.URLKYCPHP();
                if (url == null || url.isEmpty()) {
                    SwingUtilities.invokeLater(() -> lblStatus.setText("URL KYC PHP belum dikonfigurasi di database.xml"));
                    return;
                }

                String body = "{\"action\":\"challenge_code\",\"nik\":\"" + nikPasien + "\",\"nama\":\"" + nmPasien + "\"}";
                System.out.println("KYC challenge_code: POST " + url + " body=" + body);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                RestTemplate rt = new RestTemplate();
                String response = rt.exchange(url, HttpMethod.POST, entity, String.class).getBody();
                System.out.println("KYC challenge_code response: " + response);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response);
                if (root.path("success").asBoolean()) {
                    String ihsNumber = root.path("ihs_number").asText();
                    // simpan ke cache lokal
                    Sequel.queryu2tf(
                        "INSERT INTO satu_sehat_cache_ihs_pasien (no_ktp, ihs_number) VALUES (?,?) " +
                        "ON DUPLICATE KEY UPDATE ihs_number=VALUES(ihs_number)",
                        2, new String[]{ nikPasien, ihsNumber });
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("IHS Number: " + ihsNumber + " (tersimpan)");
                        JOptionPane.showMessageDialog(SatuSehatKYCDialog.this,
                            "Verifikasi berhasil!\n\nIHS Number: " + ihsNumber +
                            "\nData disimpan ke cache lokal SATUSEHAT.",
                            "KYC SATUSEHAT", JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    JsonNode errNode = root.path("error");
                    String err = (errNode != null && !errNode.isMissingNode()) ? errNode.asText() : "Verifikasi NIK gagal";
                    SwingUtilities.invokeLater(() -> lblStatus.setText("Error: " + err));
                }
            } catch (Exception ex) {
                System.out.println("KYC challenge_code error: " + ex);
                SwingUtilities.invokeLater(() -> lblStatus.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }
}
