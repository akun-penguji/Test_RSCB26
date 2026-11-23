/*
 * SatuSehatKirimQRRisikoJatuhDewasa — kirim QR Morse Fall Scale ke SatuSehat.
 *
 * Source: tabel penilaian_lanjutan_resiko_jatuh_dewasa.
 * Pattern sama dgn QR Skrining TBC.
 *
 * Questionnaire canonical: https://fhir.kemkes.go.id/Questionnaire/morse-fall-scale
 *
 * Morse Fall Scale (6 item):
 *   1. Riwayat jatuh dalam 3 bulan terakhir
 *   2. Diagnosis sekunder
 *   3. Alat bantu jalan
 *   4. IV/heparin lock
 *   5. Gaya berjalan
 *   6. Status mental
 * Plus total nilai + hasil skrining + saran.
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

public final class SatuSehatKirimQRRisikoJatuhDewasa extends javax.swing.JDialog {

    // OMIT questionnaire field: Kemkes validate vs http://terminology.kemkes.go.id/CodeSystem/kuesioner-ihs
    // registry (Rule 10169). URL custom kita ("morse-fall-scale") tidak terdaftar di registry resmi.
    // Selama belum ada ID resmi Kemkes untuk Morse Fall Scale, kosongkan saja (field optional 0..1).
    // Update string ini kalau ada Q-ID resmi (mis. "Q00XX") lalu format "https://fhir.kemkes.go.id/Questionnaire/Q00XX".
    private static final String QUESTIONNAIRE_URL = "";

    private final DefaultTableModel tabMode;
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();
    private String link = "", json = "", idpasien = "", idpraktisi = "";
    private int i = 0;

    private widget.InternalFrame internalFrame1;
    private widget.Table tbObat;
    private widget.ScrollPane Scroll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.TextBox TCari;
    private widget.ComboBox CmbStatus;
    private widget.Button BtnKirim, BtnUpdate, BtnCari, BtnKeluar;
    private widget.Label LCount;

    public SatuSehatKirimQRRisikoJatuhDewasa(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Kirim QR Risiko Jatuh Dewasa (Morse Fall Scale) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);

        tabMode = new DefaultTableModel(null, new String[]{
            "P", "Tanggal", "No.Rawat", "No.RM", "Nama Pasien", "No.KTP Pasien",
            "Petugas (NIP)", "No.KTP Petugas", "ID Encounter", "Total Skor", "Hasil", "ID QR Response"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        };

        buildUI();
        try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { System.out.println(e); }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Valid.SetTgl(DTPCari2, new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        cal.add(java.util.Calendar.MONTH, -1);
        Valid.SetTgl(DTPCari1, new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        tampil();
    }

    public void isCek() {
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnUpdate.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }

    public JTable getTable() { return tbObat; }

    // UI BUILD — Khanza standar: CENTER=table, PAGE_END=jPanel3(filter PAGE_START + buttons CENTER)
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(230, 220, 200)),
            "::[ Kirim QR Risiko Jatuh Dewasa (Morse Fall Scale) ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout());

        tbObat = new widget.Table();
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {25, 130, 105, 70, 180, 110, 110, 110, 200, 70, 200, 200};
        for (int c = 0; c < widths.length; c++) tbObat.getColumnModel().getColumn(c).setPreferredWidth(widths[c]);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        Scroll = new widget.ScrollPane();
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new Dimension(900, 100));
        jPanel3.setLayout(new BorderLayout(1, 1));
        jPanel3.add(buildFilterRow(),  BorderLayout.PAGE_START);
        jPanel3.add(buildButtonBar(),  BorderLayout.CENTER);
        internalFrame1.add(jPanel3, BorderLayout.PAGE_END);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private widget.panelisi buildFilterRow() {
        widget.panelisi p = new widget.panelisi();
        p.setPreferredSize(new Dimension(900, 44));
        p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        widget.Label lbl1 = new widget.Label(); lbl1.setText("Tanggal :");
        lbl1.setPreferredSize(new Dimension(70, 23)); p.add(lbl1);
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new Dimension(95, 23)); p.add(DTPCari1);

        widget.Label lbl2 = new widget.Label(); lbl2.setText("s.d.");
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setPreferredSize(new Dimension(24, 23)); p.add(lbl2);
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setPreferredSize(new Dimension(95, 23)); p.add(DTPCari2);

        widget.Label lbl3 = new widget.Label(); lbl3.setText("Cari :");
        lbl3.setPreferredSize(new Dimension(50, 23)); p.add(lbl3);
        TCari = new widget.TextBox();
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        TCari.setPreferredSize(new Dimension(210, 23));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
            });
        }
        p.add(TCari);

        BtnCari = new widget.Button();
        try { BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); } catch (Exception ignore) {}
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6 — Cari");
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(e -> tampil());
        p.add(BtnCari);

        widget.Label lbl4 = new widget.Label(); lbl4.setText("Status :");
        lbl4.setPreferredSize(new Dimension(50, 23)); p.add(lbl4);
        CmbStatus = new widget.ComboBox();
        CmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Semua", "Belum Terkirim", "Sudah Terkirim"}));
        CmbStatus.setPreferredSize(new Dimension(120, 23));
        CmbStatus.addActionListener(e -> tampil());
        p.add(CmbStatus);
        return p;
    }

    private widget.panelisi buildButtonBar() {
        widget.panelisi bar = new widget.panelisi();
        bar.setPreferredSize(new Dimension(900, 44));
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        widget.Label lblRec = new widget.Label(); lblRec.setText("Record :");
        lblRec.setPreferredSize(new Dimension(53, 23)); bar.add(lblRec);
        LCount = new widget.Label(); LCount.setText("0");
        LCount.setPreferredSize(new Dimension(60, 23)); bar.add(LCount);

        BtnKirim  = mk("Kirim",  'K', "/picture/34.png",       e -> kirim());
        BtnUpdate = mk("Update", 'U', "/picture/edit_f2.png",  e -> updatePut());
        BtnKeluar = mk("Keluar", 'X', "/picture/exit.png",     e -> dispose());
        BtnKirim.setPreferredSize(new Dimension(100, 30));
        BtnUpdate.setPreferredSize(new Dimension(100, 30));
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        bar.add(BtnKirim); bar.add(BtnUpdate); bar.add(BtnKeluar);
        return bar;
    }

    private widget.Button mk(String label, char mn, String icon, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(label); b.setMnemonic(mn);
        if (icon != null) try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(icon))); } catch (Exception ignore) {}
        b.addActionListener(act);
        return b;
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        String statusFilter = CmbStatus.getSelectedItem().toString();
        String cari = TCari.getText().trim();
        // Filter: hanya tampilkan pasien yang sudah punya id_encounter di satu_sehat_encounter
        // (encounter sudah ter-sync ke SatuSehat). Tanpa id_encounter, QR tidak bisa di-link.
        StringBuilder sql = new StringBuilder(
            "select rj.no_rawat, rj.tanggal, rp.no_rkm_medis, p.nm_pasien, p.no_ktp, "
          + "rj.nip, ifnull(pg.no_ktp,'') as ktppraktisi, "
          + "enc.id_encounter, "
          + "ifnull(rj.penilaian_jatuhmorse_totalnilai,0) as totalnilai, "
          + "ifnull(rj.hasil_skrining,'') as hasil, "
          + "ifnull(qr.id_questionnaireresponse,'') as id_qr "
          + "from penilaian_lanjutan_resiko_jatuh_dewasa rj "
          + "inner join reg_periksa rp on rp.no_rawat = rj.no_rawat "
          + "inner join pasien p on p.no_rkm_medis = rp.no_rkm_medis "
          + "inner join satu_sehat_encounter enc on enc.no_rawat = rp.no_rawat "
          + "left join petugas pt on pt.nip = rj.nip "
          + "left join pegawai pg on pg.nik = pt.nip "
          + "left join satu_sehat_qr_risiko_jatuh_dewasa qr on qr.no_rawat = rj.no_rawat and qr.tanggal = rj.tanggal "
          + "where date(rj.tanggal) between ? and ? "
          + "and ifnull(enc.id_encounter,'') <> '' "
        );
        if (!cari.isEmpty()) sql.append("and (p.nm_pasien like ? or rp.no_rkm_medis like ? or p.no_ktp like ?) ");
        if (statusFilter.equals("Belum Terkirim")) sql.append("and ifnull(qr.id_questionnaireresponse,'') = '' ");
        if (statusFilter.equals("Sudah Terkirim")) sql.append("and ifnull(qr.id_questionnaireresponse,'') <> '' ");
        sql.append("order by rj.tanggal desc");

        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(idx++, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            if (!cari.isEmpty()) {
                ps.setString(idx++, "%" + cari + "%");
                ps.setString(idx++, "%" + cari + "%");
                ps.setString(idx++, "%" + cari + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    false, rs.getString("tanggal"), rs.getString("no_rawat"), rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"), rs.getString("no_ktp"), rs.getString("nip"),
                    rs.getString("ktppraktisi"), rs.getString("id_encounter"),
                    String.valueOf(rs.getInt("totalnilai")), rs.getString("hasil"), rs.getString("id_qr")
                });
            }
            rs.close();
        } catch (Exception e) { System.out.println("Notif tampil QR RJ : " + e); }
        LCount.setText("Total: " + tabMode.getRowCount());
    }

    private void kirim() {
        for (i = 0; i < tbObat.getRowCount(); i++) {
            if (!tbObat.getValueAt(i, 0).toString().equals("true")) continue;
            if (!tbObat.getValueAt(i, 11).toString().isEmpty()) continue;
            if (tbObat.getValueAt(i, 8).toString().isEmpty()) {
                System.out.println("Skip row " + i + ": id_encounter kosong");
                continue;
            }
            try {
                idpasien = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i, 5).toString());
                idpraktisi = tbObat.getValueAt(i, 7).toString().isEmpty() ? ""
                           : cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i, 7).toString());
                if (idpasien.isEmpty()) continue;

                String noRawat = tbObat.getValueAt(i, 2).toString();
                String tanggal = tbObat.getValueAt(i, 1).toString();
                String authored = fungsi.IsoTime.toUtc(tanggal);

                json = buildQrJson(noRawat, tanggal, authored, idpasien, tbObat.getValueAt(i, 4).toString(),
                                   idpraktisi, tbObat.getValueAt(i, 8).toString(), null);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                HttpEntity<String> req = new HttpEntity<>(json, headers);

                System.out.println("URL : " + link + "/QuestionnaireResponse");
                System.out.println("Request JSON : " + json);
                String resp = api.getRest().exchange(link + "/QuestionnaireResponse", HttpMethod.POST, req, String.class).getBody();
                System.out.println("Result JSON : " + resp);
                JsonNode root = mapper.readTree(resp);
                String idQr = root.path("id").asText();
                if (!idQr.isEmpty()) {
                    String tglKirim = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                    if (Sequel.menyimpantf2("satu_sehat_qr_risiko_jatuh_dewasa", "?,?,?,?", "QR Risiko Jatuh Dewasa", 4,
                            new String[]{noRawat, tanggal, idQr, tglKirim})) {
                        tbObat.setValueAt(idQr, i, 11);
                        tbObat.setValueAt(false, i, 0);
                    }
                }
            } catch (HttpClientErrorException e) {
                System.out.println("Notifikasi Bridging : " + e);
                System.out.println("Response Body : " + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }
    }

    private void updatePut() {
        for (i = 0; i < tbObat.getRowCount(); i++) {
            if (!tbObat.getValueAt(i, 0).toString().equals("true")) continue;
            String idQr = tbObat.getValueAt(i, 11).toString();
            if (idQr.isEmpty()) continue;
            try {
                idpasien = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i, 5).toString());
                idpraktisi = tbObat.getValueAt(i, 7).toString().isEmpty() ? ""
                           : cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i, 7).toString());
                if (idpasien.isEmpty()) continue;
                String noRawat = tbObat.getValueAt(i, 2).toString();
                String tanggal = tbObat.getValueAt(i, 1).toString();
                String authored = fungsi.IsoTime.toUtc(tanggal);

                json = buildQrJson(noRawat, tanggal, authored, idpasien, tbObat.getValueAt(i, 4).toString(),
                                   idpraktisi, tbObat.getValueAt(i, 8).toString(), idQr);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + api.TokenSatuSehat());
                HttpEntity<String> req = new HttpEntity<>(json, headers);
                System.out.println("URL : " + link + "/QuestionnaireResponse/" + idQr);
                System.out.println("Request JSON : " + json);
                api.getRest().exchange(link + "/QuestionnaireResponse/" + idQr, HttpMethod.PUT, req, String.class).getBody();
                tbObat.setValueAt(false, i, 0);
            } catch (HttpClientErrorException e) {
                System.out.println("Notifikasi Bridging : " + e);
                System.out.println("Response Body : " + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }
    }

    private String buildQrJson(String noRawat, String tanggal, String authored, String idPasienIhs, String namaPasien,
                               String idPraktisiIhs, String idEncounter, String idQrForPut) {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("{\"resourceType\": \"QuestionnaireResponse\",");
        if (idQrForPut != null && !idQrForPut.isEmpty()) {
            sb.append("\"id\": \"").append(idQrForPut).append("\",");
        }
        if (!QUESTIONNAIRE_URL.isEmpty()) {
            sb.append("\"questionnaire\": \"").append(QUESTIONNAIRE_URL).append("\",");
        }
        sb.append("\"status\": \"completed\",")
          .append("\"authored\": \"").append(authored).append("\",")
          .append("\"subject\": {\"reference\": \"Patient/").append(idPasienIhs).append("\",")
          .append("\"display\": \"").append(jsonEscape(namaPasien)).append("\"},")
          .append("\"encounter\": {\"reference\": \"Encounter/").append(idEncounter).append("\"},");
        if (!idPraktisiIhs.isEmpty()) {
            sb.append("\"author\": {\"reference\": \"Practitioner/").append(idPraktisiIhs).append("\"},")
              .append("\"source\": {\"reference\": \"Practitioner/").append(idPraktisiIhs).append("\"},");
        } else {
            sb.append("\"source\": {\"reference\": \"Patient/").append(idPasienIhs).append("\"},");
        }
        sb.append("\"item\": [");
        appendMorseItems(sb, noRawat, tanggal);
        sb.append("]}");
        return sb.toString();
    }

    /** Build Morse Fall Scale items dari row penilaian_lanjutan_resiko_jatuh_dewasa. */
    private void appendMorseItems(StringBuilder sb, String noRawat, String tanggal) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from penilaian_lanjutan_resiko_jatuh_dewasa where no_rawat=? and tanggal=? limit 1")) {
            ps.setString(1, noRawat);
            ps.setString(2, tanggal);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return;

            sb.append("{\"linkId\": \"morse-fall-scale\",\"text\": \"Morse Fall Scale\",\"item\": [");

            // Item 1 - Riwayat jatuh
            morseItem(sb, "1-riwayat-jatuh", "Riwayat jatuh dalam 3 bulan terakhir",
                rs.getString("penilaian_jatuhmorse_skala1"), rs.getInt("penilaian_jatuhmorse_nilai1"));
            sb.append(",");
            // Item 2 - Diagnosis sekunder
            morseItem(sb, "2-diagnosis-sekunder", "Diagnosis sekunder",
                rs.getString("penilaian_jatuhmorse_skala2"), rs.getInt("penilaian_jatuhmorse_nilai2"));
            sb.append(",");
            // Item 3 - Alat bantu jalan
            morseItem(sb, "3-alat-bantu", "Alat bantu jalan",
                rs.getString("penilaian_jatuhmorse_skala3"), rs.getInt("penilaian_jatuhmorse_nilai3"));
            sb.append(",");
            // Item 4 - IV/heparin lock
            morseItem(sb, "4-iv-heparin", "IV/heparin lock terpasang",
                rs.getString("penilaian_jatuhmorse_skala4"), rs.getInt("penilaian_jatuhmorse_nilai4"));
            sb.append(",");
            // Item 5 - Gaya berjalan
            morseItem(sb, "5-gaya-berjalan", "Gaya berjalan",
                rs.getString("penilaian_jatuhmorse_skala5"), rs.getInt("penilaian_jatuhmorse_nilai5"));
            sb.append(",");
            // Item 6 - Status mental
            morseItem(sb, "6-status-mental", "Status mental",
                rs.getString("penilaian_jatuhmorse_skala6"), rs.getInt("penilaian_jatuhmorse_nilai6"));
            sb.append("]},");

            // Total skor
            sb.append("{\"linkId\": \"total-skor\",\"text\": \"Total Skor Morse\",")
              .append("\"answer\": [{\"valueInteger\": ").append(rs.getInt("penilaian_jatuhmorse_totalnilai")).append("}]},");

            // Hasil skrining
            sb.append("{\"linkId\": \"hasil-skrining\",\"text\": \"Hasil Skrining\",")
              .append("\"answer\": [{\"valueString\": \"").append(jsonEscape(nz(rs.getString("hasil_skrining")))).append("\"}]},");

            // Saran
            sb.append("{\"linkId\": \"saran\",\"text\": \"Saran/Intervensi\",")
              .append("\"answer\": [{\"valueString\": \"").append(jsonEscape(nz(rs.getString("saran")))).append("\"}]}");

            rs.close();
        } catch (Exception e) {
            System.out.println("Notif appendMorseItems : " + e);
        }
    }

    /** 1 item Morse dgn skala (string) + nilai (integer). Pakai MULTI-ANSWER dalam 1 item
     *  (bukan nested item) — FHIR spec QuestionnaireResponse.item.item: item tidak boleh punya
     *  answer DAN nested item sekaligus (FHIRPath constraint). */
    private void morseItem(StringBuilder sb, String linkId, String text, String skala, int nilai) {
        sb.append("{\"linkId\": \"").append(linkId).append("\",")
          .append("\"text\": \"").append(jsonEscape(text)).append("\",")
          .append("\"answer\": [")
            .append("{\"valueString\": \"").append(jsonEscape(nz(skala))).append("\"},")
            .append("{\"valueInteger\": ").append(nilai).append("}")
          .append("]}");
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }
}
