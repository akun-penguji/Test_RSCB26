package bridging;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import fungsi.koneksiDB;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ViewPDFNextcloud {

    private final String username = koneksiDB.USERNEXTCLOUD();
    private final String password = koneksiDB.PASNEXTCLOUD();
    private final String baseUrl = koneksiDB.URLNEXTCLOUD() + username + "/";

    public JPanel getViewerPanel(String relativePath) {
        String pdfUrl = baseUrl + relativePath;

        try {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(null, -1),
                    new UsernamePasswordCredentials(username, password.toCharArray())
            );

            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .build()) {

                HttpGet request = new HttpGet(pdfUrl);
                byte[] pdfBytes = httpClient.execute(request, response ->
                        EntityUtils.toByteArray(response.getEntity())
                );

                InputStream inputStream = new ByteArrayInputStream(pdfBytes);

                SwingController controller = new SwingController();
                SwingViewBuilder factory = new SwingViewBuilder(controller);
                JPanel viewerComponentPanel = factory.buildViewerPanel();
                controller.openDocument(inputStream, "", null);

                return viewerComponentPanel;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat PDF: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
