/*
 * SirsRLReport.java
 *
 * Pencetak laporan SIRS RL. Tiap formulir RL punya jumlah & lebar kolom yang
 * berbeda, jadi jasper statis selalu berantakan: kolom yang tak terpakai tetap
 * tergambar kosong dan kolom uraian jadi sempit sampai teksnya melipat.
 *
 * Di sini JRXML-nya dibangun on the fly sesuai kolom formulir yang sedang
 * ditampilkan: lebar kolom proporsional terhadap isi, kolom angka rata tengah,
 * dan tidak ada kolom kosong. Datanya dibaca langsung dari TableModel-nya
 * (tanpa tabel `temporary`) sehingga tidak bentrok antar-pengguna.
 */
package laporan;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public final class SirsRLReport {

    /** Lebar area cetak = pageWidth - margin kiri/kanan. */
    private static final int LEBAR_LANDSCAPE = 802;   // A4 landscape 842 - 20 - 20
    private static final int LEBAR_POTRAIT = 555;     // A4 potrait  595 - 20 - 20
    private static final int MIN_KOLOM = 38;

    private SirsRLReport() {
    }

    /**
     * Susun JRXML sesuai kolom formulir, compile, lalu tampilkan.
     *
     * @param kolom  judul kolom
     * @param data   isi tabel (dibaca apa adanya)
     * @param param  parameter kop RS + judul + periode + tanggal
     */
    public static void cetak(String judul, String periode, Date tanggal,
            String[] kolom, TableModel data, Map<String, Object> param) throws Exception {
        JasperPrint jp = buatPrint(judul, periode, tanggal, kolom, data, param);
        JasperViewer v = new JasperViewer(jp, false);
        v.setTitle("::[ " + judul + " ]::");
        Dimension layar = Toolkit.getDefaultToolkit().getScreenSize();
        v.setSize(layar.width - 50, layar.height - 50);
        v.setLocationRelativeTo(null);
        v.setVisible(true);
    }

    /** Susun + compile + isi laporan. Dipisah supaya bisa diuji tanpa layar. */
    static JasperPrint buatPrint(String judul, String periode, Date tanggal,
            String[] kolom, TableModel data, Map<String, Object> param) throws Exception {

        int n = kolom.length;
        boolean landscape = n > 4;
        int total = landscape ? LEBAR_LANDSCAPE : LEBAR_POTRAIT;

        boolean[] angka = new boolean[n];
        int[] lebar = hitungLebar(kolom, data, total, angka);

        StringBuilder x = new StringBuilder(8192);
        x.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("name=\"rptSirsRL\" language=\"groovy\" ")
                .append(landscape
                        ? "pageWidth=\"842\" pageHeight=\"595\" orientation=\"Landscape\" columnWidth=\"802\" "
                        : "pageWidth=\"595\" pageHeight=\"842\" columnWidth=\"555\" ")
                .append("leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\" ")
                .append("uuid=\"d4e5f6a7-3333-4444-8555-666677778888\">\n");

        for (String p : new String[]{"namars", "alamatrs", "kotars", "propinsirs", "kontakrs", "emailrs", "judul", "periode"}) {
            x.append("\t<parameter name=\"").append(p).append("\" class=\"java.lang.String\"/>\n");
        }
        x.append("\t<parameter name=\"logo\" class=\"java.io.InputStream\"/>\n");
        x.append("\t<parameter name=\"ttd\" class=\"java.lang.String\"/>\n");
        for (int i = 0; i < n; i++) {
            x.append("\t<field name=\"COLUMN_").append(i).append("\" class=\"java.lang.String\"/>\n");
        }

        // ---------------- judul + kop ----------------
        x.append("\t<title>\n\t\t<band height=\"92\">\n")
                .append(gambar(1, 4, 45, 45, "$P{logo}"))
                .append(teksP(0, 2, total, 17, "$P{namars}", "Center", 12, true))
                .append(teksP(0, 19, total, 13, "$P{alamatrs}+\", \"+$P{kotars}+\", \"+$P{propinsirs}", "Center", 8, false))
                .append(teksP(0, 32, total, 13, "$P{kontakrs}+\"   E-mail : \"+$P{emailrs}", "Center", 8, false))
                .append("\t\t\t<line><reportElement x=\"0\" y=\"50\" width=\"").append(total)
                .append("\" height=\"1\"/><graphicElement><pen lineWidth=\"2.0\" lineStyle=\"Double\"/></graphicElement></line>\n")
                .append(teksP(0, 56, total, 16, "$P{judul}", "Center", 10, true))
                .append(teksP(0, 72, total, 13, "\"Periode : \"+$P{periode}", "Center", 8, false))
                .append("\t\t</band>\n\t</title>\n");

        // ---------------- header kolom ----------------
        x.append("\t<columnHeader>\n\t\t<band height=\"24\">\n");
        int pos = 0;
        for (int i = 0; i < n; i++) {
            x.append(judulKolom(pos, lebar[i], kolom[i]));
            pos += lebar[i];
        }
        x.append("\t\t</band>\n\t</columnHeader>\n");

        // ---------------- isi ----------------
        x.append("\t<detail>\n\t\t<band height=\"14\">\n");
        pos = 0;
        for (int i = 0; i < n; i++) {
            x.append(selData(pos, lebar[i], "$F{COLUMN_" + i + "}", angka[i] ? "Center" : "Left"));
            pos += lebar[i];
        }
        x.append("\t\t</band>\n\t</detail>\n");

        // ---------------- footer + tanda tangan ----------------
        x.append("\t<pageFooter>\n\t\t<band height=\"14\">\n")
                .append(teksP(total - 200, 0, 200, 12, "\"Halaman \"+$V{PAGE_NUMBER}", "Right", 7, false))
                .append("\t\t</band>\n\t</pageFooter>\n");
        x.append("\t<summary>\n\t\t<band height=\"72\">\n")
                .append(teksP(total - 240, 8, 240, 13, "$P{ttd}", "Center", 8, false))
                .append("\t\t\t<staticText><reportElement x=\"").append(total - 240)
                .append("\" y=\"21\" width=\"240\" height=\"13\"/>")
                .append("<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">")
                .append("<font fontName=\"Tahoma\" size=\"8\"/></textElement><text><![CDATA[Direktur]]></text></staticText>\n")
                .append("\t\t\t<line><reportElement x=\"").append(total - 180)
                .append("\" y=\"62\" width=\"120\" height=\"1\"/></line>\n")
                .append("\t\t</band>\n\t</summary>\n");
        x.append("</jasperReport>\n");

        param.put("judul", judul);
        param.put("periode", periode);
        param.put("ttd", (param.get("kotars") == null ? "" : param.get("kotars") + ", ")
                + new SimpleDateFormat("dd MMMM yyyy").format(tanggal == null ? new Date() : tanggal));

        JasperDesign design = JRXmlLoader.load(new ByteArrayInputStream(
                x.toString().getBytes(StandardCharsets.UTF_8)));
        JasperReport jr = JasperCompileManager.compileReport(design);
        return JasperFillManager.fillReport(jr, param, new JRTableModelDataSource(data));
    }

    /**
     * Lebar kolom proporsional: dihitung dari panjang judul & isi terpanjang,
     * lalu dinormalkan supaya totalnya pas selebar halaman. Kolom angka diberi
     * batas atas agar tidak memakan ruang kolom uraian.
     */
    private static int[] hitungLebar(String[] kolom, TableModel data, int total, boolean[] angka) {
        int n = kolom.length;
        int[] bobot = new int[n];
        int jml = Math.min(data.getRowCount(), 500); // cukup 500 baris untuk menaksir
        for (int c = 0; c < n; c++) {
            int maks = kolom[c] == null ? 4 : Math.min(kolom[c].length(), 22);
            boolean semuaAngka = jml > 0;
            for (int r = 0; r < jml; r++) {
                Object o = data.getValueAt(r, c);
                String s = o == null ? "" : o.toString();
                maks = Math.max(maks, Math.min(s.length(), 40));
                if (!s.trim().isEmpty() && !s.trim().matches("-?[0-9.,]+%?")) {
                    semuaAngka = false;
                }
            }
            angka[c] = semuaAngka;
            bobot[c] = semuaAngka ? Math.min(maks, 8) : maks;
        }
        int jmlBobot = 0;
        for (int b : bobot) {
            jmlBobot += b;
        }
        int[] lebar = new int[n];
        int terpakai = 0;
        for (int c = 0; c < n; c++) {
            lebar[c] = Math.max(MIN_KOLOM, (int) ((long) total * bobot[c] / jmlBobot));
            terpakai += lebar[c];
        }
        // Rapikan selisih pembulatan ke kolom teks terlebar.
        int lebarTeks = -1;
        for (int c = 0; c < n; c++) {
            if (!angka[c] && (lebarTeks < 0 || lebar[c] > lebar[lebarTeks])) {
                lebarTeks = c;
            }
        }
        if (lebarTeks < 0) {
            lebarTeks = n - 1;
        }
        lebar[lebarTeks] += total - terpakai;
        if (lebar[lebarTeks] < MIN_KOLOM) { // pengaman bila kolom terlalu banyak
            lebar[lebarTeks] = MIN_KOLOM;
        }
        return lebar;
    }

    // ------------------------------------------------------------ potongan XML
    private static String gambar(int x, int y, int w, int h, String expr) {
        return "\t\t\t<image scaleImage=\"RetainShape\" isUsingCache=\"true\" onErrorType=\"Blank\">"
                + "<reportElement x=\"" + x + "\" y=\"" + y + "\" width=\"" + w + "\" height=\"" + h + "\"/>"
                + "<imageExpression><![CDATA[" + expr + "]]></imageExpression></image>\n";
    }

    private static String teksP(int x, int y, int w, int h, String expr, String align, int size, boolean bold) {
        return "\t\t\t<textField isBlankWhenNull=\"true\"><reportElement x=\"" + x + "\" y=\"" + y
                + "\" width=\"" + w + "\" height=\"" + h + "\"/>"
                + "<textElement textAlignment=\"" + align + "\" verticalAlignment=\"Middle\">"
                + "<font fontName=\"Tahoma\" size=\"" + size + "\" isBold=\"" + bold + "\"/></textElement>"
                + "<textFieldExpression><![CDATA[" + expr + "]]></textFieldExpression></textField>\n";
    }

    private static String judulKolom(int x, int w, String teks) {
        return "\t\t\t<staticText><reportElement mode=\"Opaque\" x=\"" + x + "\" y=\"0\" width=\"" + w
                + "\" height=\"24\" backcolor=\"#F0F0DC\"/><box leftPadding=\"2\" rightPadding=\"2\">"
                + "<pen lineWidth=\"0.5\"/></box>"
                + "<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">"
                + "<font fontName=\"Tahoma\" size=\"7\" isBold=\"true\"/></textElement>"
                + "<text><![CDATA[" + xml(teks) + "]]></text></staticText>\n";
    }

    private static String selData(int x, int w, String expr, String align) {
        return "\t\t\t<textField isBlankWhenNull=\"true\" isStretchWithOverflow=\"true\">"
                + "<reportElement x=\"" + x + "\" y=\"0\" width=\"" + w + "\" height=\"14\"/>"
                + "<box leftPadding=\"2\" rightPadding=\"2\"><pen lineWidth=\"0.5\"/></box>"
                + "<textElement textAlignment=\"" + align + "\" verticalAlignment=\"Middle\">"
                + "<font fontName=\"Tahoma\" size=\"7\"/></textElement>"
                + "<textFieldExpression><![CDATA[" + expr + "]]></textFieldExpression></textField>\n";
    }

    private static String xml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("]]>", "]]&gt;");
    }
}
