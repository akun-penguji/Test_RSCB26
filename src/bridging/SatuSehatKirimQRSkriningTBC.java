/*
 * SatuSehatKirimQRSkriningTBC — kirim QuestionnaireResponse Skrining TBC ke SatuSehat.
 *
 * Source: tabel skrining_tbc (program nasional TB, sinergi dgn EOC TB-DOTS).
 *
 * Pattern:
 *   - Filter tanggal range + status (Belum/Sudah Terkirim) + cari nama/no_rm
 *   - List pasien yang sudah ada record skrining_tbc dalam range
 *   - User pilih row → klik Kirim → build QR JSON → POST → simpan tracking
 *   - Update untuk PUT existing
 *
 * Questionnaire canonical URL: https://fhir.kemkes.go.id/Questionnaire/skrining-tbc
 *
 * Mapping kolom DB ke linkId QR:
 *   IMT/lingkar pinggang (4 item) | Riwayat kontak (4 item) | Faktor risiko (8 item bool)
 *   | Gejala TB (4 item bool + 1 keterangan) | Abnormalitas radiologi | Kesimpulan skrining
 *
 * Pakai valueBoolean untuk enum Ya/Tidak, valueString untuk enum bertingkat,
 * valueQuantity untuk numerik (BB/TB/IMT/LP), valueCoding kalau ada kode SNOMED.
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

public final class SatuSehatKirimQRSkriningTBC extends javax.swing.JDialog {

    // Kemkes Q-ID registry (per terminology.kemkes.go.id/CodeSystem/kuesioner-ihs):
    //   Q0001 = TB klasifikasi (terduga RO/SO, riwayat pengobatan, lokasi anatomis)
    //   Q0013 = Faktor Risiko PTM (overlap dgn 8 faktor risiko di skrining_tbc)
    // Skrining TBC Khanza kita lebih dekat ke "skrining gejala + faktor risiko" — tidak
    // exactly match Q0001 (yang fokus klasifikasi) atau Q0013 (PTM umum).
    // OPTIONAL: ubah ke "https://fhir.kemkes.go.id/Questionnaire/Q0001" kalau dipakai
    // untuk klasifikasi TB. Untuk skrining umum, kosongkan dulu (server omit-tolerant).
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

    // ---- UI Components ----
    private widget.InternalFrame internalFrame1;
    private widget.Table tbObat;
    private widget.ScrollPane Scroll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.TextBox TCari;
    private widget.ComboBox CmbStatus;
    private widget.Button BtnKirim, BtnUpdate, BtnCari, BtnKeluar;
    private widget.Label LCount;

    public SatuSehatKirimQRSkriningTBC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Kirim QR Skrining TBC Satu Sehat ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);

        tabMode = new DefaultTableModel(null, new String[]{
            "P", "Tgl Skrining", "No.Rawat", "No.RM", "Nama Pasien", "No.KTP Pasien",
            "Petugas (NIP)", "No.KTP Petugas", "ID Encounter", "Kesimpulan", "ID QR Response"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Boolean.class : String.class;
            }
        };
        buildUI();
        try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { System.out.println("Notif : " + e); }
        // Default date range: 1 bulan terakhir
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Valid.SetTgl(DTPCari2, new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        cal.add(java.util.Calendar.MONTH, -1);
        Valid.SetTgl(DTPCari1, new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        tampil();
    }

    public void isCek() {
        // Reuse akses encounter kirim (untuk consistency dgn EOC TB-DOTS yg juga reuse encounter access)
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnUpdate.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }

    public JTable getTable() { return tbObat; }

    // ====================================================================
    // UI BUILD — pattern Khanza standar:
    //   internalFrame1 (BorderLayout)
    //     ├── CENTER: Scroll(tbObat)
    //     └── PAGE_END: jPanel3 (BorderLayout)
    //         ├── PAGE_START: panelGlass9 (filter row: tanggal, cari, status)
    //         └── CENTER:     panelGlass8 (button bar: record count, kirim, update, keluar)
    // ====================================================================
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(220, 240, 220)),
            "::[ Kirim QuestionnaireResponse Skrining TBC ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout());

        // CENTER: table
        tbObat = new widget.Table();
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {25, 130, 105, 70, 180, 110, 110, 110, 200, 200, 200};
        for (int c = 0; c < widths.length; c++) {
            TableColumn col = tbObat.getColumnModel().getColumn(c);
            col.setPreferredWidth(widths[c]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        Scroll = new widget.ScrollPane();
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        // SOUTH: panel container with filter (PAGE_START) + button bar (CENTER)
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

    /** Panel filter row di SOUTH > PAGE_START — FlowLayout LEFT mirip pattern Condition/Dispense. */
    private widget.panelisi buildFilterRow() {
        widget.panelisi p = new widget.panelisi();
        p.setPreferredSize(new Dimension(900, 44));
        p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        widget.Label lbl1 = new widget.Label(); lbl1.setText("Tgl.Skrining :");
        lbl1.setPreferredSize(new Dimension(85, 23)); p.add(lbl1);
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
        BtnCari = mkButton("", '6', "/picture/accept.png", e -> tampil());
        BtnCari.setPreferredSize(new Dimension(28, 23));
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

        BtnKirim  = mkButton("Kirim",   'K', "/picture/34.png",      e -> kirim());
        BtnUpdate = mkButton("Update",  'U', "/picture/edit_f2.png", e -> updatePut());
        BtnKeluar = mkButton("Keluar",  'X', "/picture/exit.png",    e -> dispose());
        BtnKirim.setPreferredSize(new Dimension(100, 30));
        BtnUpdate.setPreferredSize(new Dimension(100, 30));
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        bar.add(BtnKirim); bar.add(BtnUpdate); bar.add(BtnKeluar);
        return bar;
    }

    private widget.Label mkLabel(String text, int x, int y, int w, int h, Font f) {
        widget.Label l = new widget.Label();
        l.setText(text); l.setFont(f); l.setBounds(x, y, w, h);
        return l;
    }

    private widget.Button mkButton(String label, char mn, String icon, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(label); b.setMnemonic(mn);
        if (icon != null) try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(icon))); } catch (Exception ignore) {}
        b.addActionListener(act);
        return b;
    }

    // ====================================================================
    // DATA OPS
    // ====================================================================
    private void tampil() {
        Valid.tabelKosong(tabMode);
        String statusFilter = CmbStatus.getSelectedItem().toString();
        String cari = TCari.getText().trim();
        // Filter: hanya tampilkan pasien yang sudah punya id_encounter di satu_sehat_encounter
        // (encounter sudah ter-sync ke SatuSehat). Tanpa id_encounter, QR tidak bisa di-link ke
        // kunjungan apapun di SatuSehat.
        StringBuilder sql = new StringBuilder(
            "select skr.no_rawat, skr.tanggal, rp.no_rkm_medis, p.nm_pasien, p.no_ktp, "
          + "skr.nip, ifnull(pg.no_ktp,'') as ktppraktisi, "
          + "enc.id_encounter, ifnull(skr.kesimpulan_skrining,'') as kesimpulan, "
          + "ifnull(qr.id_questionnaireresponse,'') as id_qr "
          + "from skrining_tbc skr "
          + "inner join reg_periksa rp on rp.no_rawat = skr.no_rawat "
          + "inner join pasien p on p.no_rkm_medis = rp.no_rkm_medis "
          + "inner join satu_sehat_encounter enc on enc.no_rawat = rp.no_rawat "
          + "left join petugas pt on pt.nip = skr.nip "
          + "left join pegawai pg on pg.nik = pt.nip "
          + "left join satu_sehat_qr_skrining_tbc qr on qr.no_rawat = skr.no_rawat "
          + "where date(skr.tanggal) between ? and ? "
          + "and ifnull(enc.id_encounter,'') <> '' "
        );
        if (!cari.isEmpty()) sql.append("and (p.nm_pasien like ? or rp.no_rkm_medis like ? or p.no_ktp like ?) ");
        if (statusFilter.equals("Belum Terkirim")) sql.append("and ifnull(qr.id_questionnaireresponse,'') = '' ");
        if (statusFilter.equals("Sudah Terkirim")) sql.append("and ifnull(qr.id_questionnaireresponse,'') <> '' ");
        sql.append("order by skr.tanggal desc");

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
                    rs.getString("ktppraktisi"), rs.getString("id_encounter"), rs.getString("kesimpulan"),
                    rs.getString("id_qr")
                });
            }
            rs.close();
        } catch (Exception e) { System.out.println("Notif tampil QR TBC : " + e); }
        LCount.setText("Total: " + tabMode.getRowCount());
    }

    private void kirim() {
        for (i = 0; i < tbObat.getRowCount(); i++) {
            if (!tbObat.getValueAt(i, 0).toString().equals("true")) continue;
            if (!tbObat.getValueAt(i, 10).toString().isEmpty()) continue;  // sudah ada id_qr → skip POST
            if (tbObat.getValueAt(i, 8).toString().isEmpty()) {
                System.out.println("Skip row " + i + ": id_encounter kosong, kirim Encounter dulu");
                continue;
            }
            try {
                idpasien = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i, 5).toString());
                idpraktisi = tbObat.getValueAt(i, 7).toString().isEmpty() ? ""
                           : cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i, 7).toString());
                if (idpasien.isEmpty()) {
                    System.out.println("Skip row " + i + ": IHS Patient kosong");
                    continue;
                }
                String noRawat = tbObat.getValueAt(i, 2).toString();
                String authored = fungsi.IsoTime.toUtc(tbObat.getValueAt(i, 1).toString());

                json = buildQrJson(noRawat, authored, idpasien, tbObat.getValueAt(i, 4).toString(),
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
                    if (Sequel.menyimpantf2("satu_sehat_qr_skrining_tbc", "?,?,?", "QR Skrining TBC", 3,
                            new String[]{noRawat, idQr, tglKirim})) {
                        tbObat.setValueAt(idQr, i, 10);
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
            String idQr = tbObat.getValueAt(i, 10).toString();
            if (idQr.isEmpty()) continue;  // belum pernah POST, skip PUT
            try {
                idpasien = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i, 5).toString());
                idpraktisi = tbObat.getValueAt(i, 7).toString().isEmpty() ? ""
                           : cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i, 7).toString());
                if (idpasien.isEmpty()) continue;

                String noRawat = tbObat.getValueAt(i, 2).toString();
                String authored = fungsi.IsoTime.toUtc(tbObat.getValueAt(i, 1).toString());

                json = buildQrJson(noRawat, authored, idpasien, tbObat.getValueAt(i, 4).toString(),
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

    /** Build full QR JSON dari row skrining_tbc. */
    private String buildQrJson(String noRawat, String authored, String idPasienIhs, String namaPasien,
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
        // Item dari skrining_tbc
        appendItems(sb, noRawat);
        sb.append("]}");
        return sb.toString();
    }

    /** Build item array dari row skrining_tbc current no_rawat. */
    private void appendItems(StringBuilder sb, String noRawat) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from skrining_tbc where no_rawat=? limit 1")) {
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return;

            // Panel: Antropometri
            sb.append("{\"linkId\": \"antropometri\",\"text\": \"Antropometri\",\"item\": [");
            appendQty(sb, rs, "berat_badan",      "bb",  "Berat Badan", "kg");
            sb.append(",");
            appendQty(sb, rs, "tinggi_badan",     "tb",  "Tinggi Badan", "cm");
            sb.append(",");
            appendQty(sb, rs, "imt",              "imt", "Indeks Masa Tubuh", "kg/m2");
            sb.append(",");
            appendStr(sb, rs, "kasifikasi_imt",   "klasifikasi-imt", "Klasifikasi IMT");
            sb.append(",");
            appendQty(sb, rs, "lingkar_pinggang", "lp", "Lingkar Pinggang", "cm");
            sb.append(",");
            appendStr(sb, rs, "risiko_lingkar_pinggang", "risiko-lp", "Risiko Lingkar Pinggang");
            sb.append("]},");

            // Panel: Riwayat Kontak
            sb.append("{\"linkId\": \"riwayat-kontak\",\"text\": \"Riwayat Kontak TBC\",\"item\": [");
            appendBool(sb, rs, "riwayat_kontak_tbc",            "kontak-tbc", "Riwayat Kontak TBC");
            sb.append(",");
            appendStr(sb, rs, "jenis_kontak_tbc",               "jenis-kontak", "Jenis Kontak TBC");
            sb.append("]},");

            // Panel: Faktor Risiko
            sb.append("{\"linkId\": \"faktor-risiko\",\"text\": \"Faktor Risiko\",\"item\": [");
            appendBool(sb, rs, "faktor_resiko_pernah_terdiagnosa_tbc", "fr-pernah-diagnosa", "Pernah Terdiagnosa TBC");
            sb.append(",");
            appendStr(sb, rs, "keterangan_pernah_terdiagnosa", "fr-pernah-diagnosa-ket", "Keterangan Pernah Terdiagnosa");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_pernah_berobat_tbc", "fr-pernah-berobat", "Pernah Berobat TBC");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_malnutrisi",     "fr-malnutrisi", "Malnutrisi");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_merokok",        "fr-merokok", "Perokok");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_riwayat_dm",     "fr-dm", "Riwayat DM");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_odhiv",          "fr-hiv", "ODHIV");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_lansia",         "fr-lansia", "Lansia");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_ibu_hamil",      "fr-hamil", "Ibu Hamil");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_wbp",            "fr-wbp", "Warga Binaan Pemasyarakatan");
            sb.append(",");
            appendBool(sb, rs, "faktor_resiko_tinggal_diwilayah_padat_kumuh", "fr-padat-kumuh", "Tinggal di Wilayah Padat/Kumuh");
            sb.append("]},");

            // Panel: Gejala
            sb.append("{\"linkId\": \"gejala\",\"text\": \"Gejala TBC\",\"item\": [");
            appendStr(sb, rs,  "abnormalitas_tbc",         "abnormalitas-radiologi", "Abnormalitas Radiologi");
            sb.append(",");
            appendBool(sb, rs, "gejala_tbc_batuk",         "gejala-batuk", "Batuk");
            sb.append(",");
            appendBool(sb, rs, "gejala_tbc_bb_turun",      "gejala-bb-turun", "BB Turun");
            sb.append(",");
            appendBool(sb, rs, "gejala_tbc_demam",         "gejala-demam", "Demam");
            sb.append(",");
            appendBool(sb, rs, "gejala_tbc_berkeringat_malam_hari", "gejala-keringat-malam", "Berkeringat Malam Hari");
            sb.append(",");
            appendStr(sb, rs,  "keterangan_gejala_penyakit_lain", "gejala-lain-ket", "Keterangan Penyakit Lain");
            sb.append("]},");

            // Panel: Kesimpulan
            sb.append("{\"linkId\": \"kesimpulan\",\"text\": \"Kesimpulan Skrining\",\"item\": [");
            appendStr(sb, rs, "kesimpulan_skrining",        "kesimpulan-skrining", "Kesimpulan Skrining");
            sb.append(",");
            appendStr(sb, rs, "keterangan_hasil_skrining", "keterangan-hasil",    "Keterangan Hasil Skrining");
            sb.append("]}");

            rs.close();
        } catch (Exception e) {
            System.out.println("Notif appendItems QR TBC : " + e);
        }
    }

    /** Item dgn valueBoolean (untuk enum Ya/Tidak). */
    private void appendBool(StringBuilder sb, ResultSet rs, String col, String linkId, String text) throws Exception {
        String v = rs.getString(col);
        boolean bool = "Ya".equalsIgnoreCase(v);
        sb.append("{\"linkId\": \"").append(linkId).append("\",")
          .append("\"text\": \"").append(jsonEscape(text)).append("\",")
          .append("\"answer\": [{\"valueBoolean\": ").append(bool).append("}]}");
    }

    /** Item dgn valueString (untuk enum bertingkat / free text). */
    private void appendStr(StringBuilder sb, ResultSet rs, String col, String linkId, String text) throws Exception {
        String v = rs.getString(col);
        if (v == null) v = "";
        sb.append("{\"linkId\": \"").append(linkId).append("\",")
          .append("\"text\": \"").append(jsonEscape(text)).append("\",")
          .append("\"answer\": [{\"valueString\": \"").append(jsonEscape(v)).append("\"}]}");
    }

    /** Item dgn valueQuantity (untuk numerik dgn unit). */
    private void appendQty(StringBuilder sb, ResultSet rs, String col, String linkId, String text, String unit) throws Exception {
        String v = rs.getString(col);
        double val = 0;
        try { if (v != null && !v.isEmpty()) val = Double.parseDouble(v); } catch (NumberFormatException ignore) {}
        sb.append("{\"linkId\": \"").append(linkId).append("\",")
          .append("\"text\": \"").append(jsonEscape(text)).append("\",")
          .append("\"answer\": [{\"valueQuantity\": {\"value\": ").append(val)
          .append(",\"unit\": \"").append(unit).append("\"}}]}");
    }

    /** Escape karakter JSON manual (Rule 20006 protection). */
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
