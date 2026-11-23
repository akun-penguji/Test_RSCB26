package inventory;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Cetak tiket antrian farmasi (thermal 80mm) untuk resep rawat jalan non IGD.
 *
 * Nomor antrian diambil dari 4 digit terakhir nomor resep.
 * Diaktifkan lewat setting/database.xml :
 *   TIKETANTRIANFARMASI        yes/no  -> aktifkan fitur
 *   PRINTERTIKETFARMASI        nama printer, kosong = printer default
 *   TIKETANTRIANFARMASIPREVIEW yes/no  -> tampilkan preview sebelum cetak
 */
public class TiketAntrianFarmasi {

    private static final double MM = 72d / 25.4d;          //1 mm dalam point
    private static final double LEBAR_KERTAS = 80 * MM;    //80 mm
    private static final double MARGIN = 3 * MM;
    private static final double LEBAR_CETAK = LEBAR_KERTAS - (2 * MARGIN);
    private static final double TINGGI_KERTAS = 120 * MM;  //cukup panjang, kertas roll dipotong sesuai isi

    private static final String[] POLI_IGD = {"IGD", "IGDK", "U0001"};

    /**
     * Data yang tercetak di tiket.
     */
    public static class Data {
        public String namars = "";
        public String noAntrian = "";
        public String noResep = "";
        public String namaPasien = "";
        public String noRawat = "";
        public String noRM = "";
        public String tglLahir = "";
        public String poli = "";
        public String dokter = "";
        public String waktuCetak = "";
    }

    /**
     * Ambil 4 digit terakhir nomor resep sebagai nomor antrian.
     */
    public static String nomorAntrian(String noResep) {
        if (noResep == null) {
            return "";
        }
        String bersih = noResep.trim();
        if (bersih.length() <= 4) {
            return bersih;
        }
        return bersih.substring(bersih.length() - 4);
    }

    /**
     * Cek apakah pendaftaran ini poli IGD (tiket tidak dicetak untuk IGD).
     */
    public static boolean isIGD(String noRawat) {
        String kdPoli = new sekuel().cariIsi("select reg_periksa.kd_poli from reg_periksa where reg_periksa.no_rawat=?", noRawat);
        if (kdPoli == null) {
            return false;
        }
        for (String kode : POLI_IGD) {
            if (kode.equalsIgnoreCase(kdPoli.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Titik masuk utama, dipanggil setelah resep rawat jalan tersimpan.
     * Aman dipanggil apa adanya : semua kegagalan hanya dicatat ke console
     * supaya proses simpan resep tidak pernah terganggu.
     */
    public static void cetakBilaAktif(final String noResep, final String noRawat, final String kdDokter) {
        try {
            if (!"yes".equalsIgnoreCase(koneksiDB.TIKETANTRIANFARMASI())) {
                return;
            }
            if (isIGD(noRawat)) {
                return;
            }
            final Data data = ambilData(noResep, noRawat, kdDokter);
            if ("yes".equalsIgnoreCase(koneksiDB.TIKETANTRIANFARMASIPREVIEW())) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        preview(data);
                    }
                });
            } else {
                cetak(data, koneksiDB.PRINTERTIKETFARMASI());
            }
        } catch (Exception e) {
            System.out.println("Notif tiket antrian farmasi : " + e);
        }
    }

    /**
     * Ambil data pasien, poli, dan dokter untuk tiket.
     */
    public static Data ambilData(String noResep, String noRawat, String kdDokter) {
        Data data = new Data();
        data.noResep = noResep == null ? "" : noResep.trim();
        data.noRawat = noRawat == null ? "" : noRawat.trim();
        data.noAntrian = nomorAntrian(noResep);
        data.waktuCetak = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        try {
            data.namars = akses.getnamars();
        } catch (Exception e) {
            data.namars = "";
        }

        Connection koneksi = koneksiDB.condb();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = koneksi.prepareStatement(
                    "select pasien.nm_pasien,pasien.no_rkm_medis,pasien.tgl_lahir,poliklinik.nm_poli "
                    + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?");
            ps.setString(1, data.noRawat);
            rs = ps.executeQuery();
            if (rs.next()) {
                data.namaPasien = rs.getString("nm_pasien");
                data.noRM = rs.getString("no_rkm_medis");
                data.poli = rs.getString("nm_poli");
                String lahir = rs.getString("tgl_lahir");
                if (lahir != null && lahir.length() == 10) {
                    data.tglLahir = lahir.substring(8, 10) + "-" + lahir.substring(5, 7) + "-" + lahir.substring(0, 4);
                } else {
                    data.tglLahir = lahir == null ? "" : lahir;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif tiket antrian farmasi : " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                System.out.println("Notif tiket antrian farmasi : " + e);
            }
        }

        data.dokter = new sekuel().cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kdDokter == null ? "" : kdDokter);
        return data;
    }

    /**
     * Cetak langsung ke printer, tanpa dialog.
     *
     * @param namaPrinter nama printer, kosong berarti printer default OS
     */
    public static void cetak(Data data, String namaPrinter) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            PrintService service = cariPrinter(namaPrinter);
            if (service != null) {
                job.setPrintService(service);
            }
            job.setJobName("Tiket Antrian Farmasi " + data.noAntrian);
            job.setPrintable(new TiketPrintable(data), pageFormat());
            job.print();
        } catch (PrinterException e) {
            System.out.println("Notif cetak tiket antrian farmasi : " + e);
        } catch (Exception e) {
            System.out.println("Notif cetak tiket antrian farmasi : " + e);
        }
    }

    private static PrintService cariPrinter(String namaPrinter) {
        if (namaPrinter == null || namaPrinter.trim().isEmpty()) {
            return PrintServiceLookup.lookupDefaultPrintService();
        }
        String cari = namaPrinter.trim().toLowerCase();
        PrintService[] daftar = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService ps : daftar) {
            if (ps.getName().equalsIgnoreCase(namaPrinter.trim())) {
                return ps;
            }
        }
        for (PrintService ps : daftar) {
            if (ps.getName().toLowerCase().contains(cari)) {
                return ps;
            }
        }
        System.out.println("Notif tiket antrian farmasi : printer '" + namaPrinter + "' tidak ditemukan, memakai printer default.");
        return PrintServiceLookup.lookupDefaultPrintService();
    }

    private static PageFormat pageFormat() {
        Paper paper = new Paper();
        paper.setSize(LEBAR_KERTAS, TINGGI_KERTAS);
        paper.setImageableArea(MARGIN, MARGIN, LEBAR_CETAK, TINGGI_KERTAS - (2 * MARGIN));
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);
        return pf;
    }

    /**
     * Tampilkan jendela preview tiket lengkap dengan tombol cetak.
     */
    public static void preview(final Data data) {
        final JDialog dialog = new JDialog((java.awt.Frame) null, "Preview Tiket Antrian Farmasi", true);
        final double skala = 2.5;

        JPanel kertas = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.scale(skala, skala);
                g2.translate(MARGIN, MARGIN);
                gambar(g2, data, LEBAR_CETAK);
                g2.dispose();
            }
        };
        double tinggi = tinggiIsi(data) + (2 * MARGIN);
        kertas.setBackground(Color.WHITE);
        kertas.setPreferredSize(new Dimension((int) (LEBAR_KERTAS * skala), (int) (tinggi * skala)));

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCetak = new JButton("Cetak");
        JButton btnTutup = new JButton("Tutup");
        btnCetak.addActionListener(evt -> {
            dialog.dispose();
            cetak(data, koneksiDB.PRINTERTIKETFARMASI());
        });
        btnTutup.addActionListener(evt -> dialog.dispose());
        tombol.add(btnCetak);
        tombol.add(btnTutup);

        JPanel isi = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        isi.add(kertas);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(new JScrollPane(isi), BorderLayout.CENTER);
        dialog.getContentPane().add(tombol, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Preview memakai data contoh, untuk mengecek tata letak tanpa resep nyata.
     */
    public static void previewContoh() {
        Data data = new Data();
        try {
            data.namars = akses.getnamars();
        } catch (Exception e) {
            data.namars = "RSUD CABANG BUNGIN";
        }
        if (data.namars == null || data.namars.trim().isEmpty()) {
            data.namars = "RSUD CABANG BUNGIN";
        }
        data.noAntrian = "0123";
        data.noResep = "20260812RJ0123";
        data.namaPasien = "SITI AMINAH BINTI SUKARDI";
        data.noRawat = "2026/08/12/000123";
        data.noRM = "012345";
        data.tglLahir = "12-05-1980";
        data.poli = "POLI PENYAKIT DALAM";
        data.dokter = "dr. BUDI SANTOSO, Sp.PD";
        data.waktuCetak = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        preview(data);
    }

    private static class TiketPrintable implements Printable {

        private final Data data;

        TiketPrintable(Data data) {
            this.data = data;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            gambar(g2, data, pageFormat.getImageableWidth());
            return PAGE_EXISTS;
        }
    }

    //=================== penggambaran tiket (dipakai preview & cetak) ===================

    private static final Font F_RS = new Font("SansSerif", Font.BOLD, 10);
    private static final Font F_JUDUL = new Font("SansSerif", Font.BOLD, 9);
    private static final Font F_LABELKECIL = new Font("SansSerif", Font.PLAIN, 7);
    private static final Font F_ANTRIAN = new Font("SansSerif", Font.BOLD, 40);
    private static final Font F_DATA = new Font("Monospaced", Font.PLAIN, 8);
    private static final Font F_DATATEBAL = new Font("Monospaced", Font.BOLD, 8);
    private static final Font F_KAKI = new Font("SansSerif", Font.PLAIN, 7);

    private static double tinggiIsi(Data data) {
        return gambar(null, data, LEBAR_CETAK);
    }

    /**
     * Menggambar tiket. Bila g2 null hanya menghitung tinggi isi.
     *
     * @return tinggi total isi dalam point
     */
    private static double gambar(Graphics2D g2, Data data, double lebar) {
        if (g2 != null) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
        }
        double y = 0;

        y += 12;
        y = tengah(g2, aman(data.namars, "RUMAH SAKIT"), F_RS, lebar, y, 11);
        y = tengah(g2, "ANTRIAN FARMASI", F_JUDUL, lebar, y, 12);

        y = garisTebal(g2, lebar, y);

        y += 9;
        y = tengah(g2, "NO. ANTRIAN", F_LABELKECIL, lebar, y, 4);

        //kotak nomor antrian
        double tinggiKotak = 46;
        double lebarKotak = lebar - 20;
        if (g2 != null) {
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRect((int) 10, (int) y, (int) lebarKotak, (int) tinggiKotak);
            g2.setStroke(new BasicStroke(0.6f));
            g2.drawRect((int) 13, (int) (y + 3), (int) (lebarKotak - 6), (int) (tinggiKotak - 6));
        }
        String nomor = spasiAngka(aman(data.noAntrian, "----"));
        if (g2 != null) {
            g2.setFont(F_ANTRIAN);
            Rectangle2D b = g2.getFontMetrics(F_ANTRIAN).getStringBounds(nomor, g2);
            double xn = (lebar - b.getWidth()) / 2;
            double yn = y + (tinggiKotak / 2) + (g2.getFontMetrics(F_ANTRIAN).getAscent() / 2.4);
            g2.drawString(nomor, (float) xn, (float) yn);
        }
        y += tinggiKotak + 8;

        y = garisTebal(g2, lebar, y);
        y += 10;

        y = baris(g2, "Nama", data.namaPasien, lebar, y, true);
        y = baris(g2, "No. RM", data.noRM, lebar, y, true);
        y = baris(g2, "No. Rawat", data.noRawat, lebar, y, false);
        y = baris(g2, "Tgl Lahir", data.tglLahir, lebar, y, false);
        y = baris(g2, "Poli", data.poli, lebar, y, false);
        y = baris(g2, "Dokter", data.dokter, lebar, y, false);

        y += 3;
        y = garisPutus(g2, lebar, y);
        y += 9;
        y = tengah(g2, "No. Resep : " + aman(data.noResep, "-"), F_KAKI, lebar, y, 9);
        y = tengah(g2, "Dicetak : " + aman(data.waktuCetak, "-"), F_KAKI, lebar, y, 10);
        y = tengah(g2, "Silakan menunggu panggilan petugas", F_KAKI, lebar, y, 4);
        y = garisPutus(g2, lebar, y);
        y += 14;

        return y;
    }

    private static String aman(String isi, String pengganti) {
        return (isi == null || isi.trim().isEmpty()) ? pengganti : isi.trim();
    }

    /**
     * Beri jarak antar angka supaya nomor antrian lebih mudah dibaca dari jauh.
     */
    private static String spasiAngka(String nomor) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nomor.length(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(nomor.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Tulis teks rata tengah dengan y sebagai garis dasar (baseline).
     *
     * @param jarakBawah jarak ke baseline berikutnya
     */
    private static double tengah(Graphics2D g2, String teks, Font font, double lebar, double y, double jarakBawah) {
        if (g2 != null) {
            g2.setFont(font);
            Rectangle2D b = g2.getFontMetrics(font).getStringBounds(teks, g2);
            g2.drawString(teks, (float) ((lebar - b.getWidth()) / 2), (float) y);
        }
        return y + jarakBawah;
    }

    private static double baris(Graphics2D g2, String label, String isi, double lebar, double y, boolean tebal) {
        Font font = tebal ? F_DATATEBAL : F_DATA;
        String nilai = aman(isi, "-");
        List<String> potongan = potong(g2, nilai, font, lebar - 58);
        for (int i = 0; i < potongan.size(); i++) {
            if (g2 != null) {
                g2.setFont(F_DATA);
                if (i == 0) {
                    g2.drawString(label, 2f, (float) y);
                    g2.drawString(":", 52f, (float) y);
                }
                g2.setFont(font);
                g2.drawString(potongan.get(i), 58f, (float) y);
            }
            y += 11;
        }
        return y;
    }

    private static List<String> potong(Graphics2D g2, String teks, Font font, double lebarMaks) {
        List<String> hasil = new ArrayList<>();
        if (g2 == null) {
            //perkiraan lebar karakter monospaced saat hanya menghitung tinggi
            int maks = Math.max(8, (int) (lebarMaks / (font.getSize() * 0.6)));
            while (teks.length() > maks) {
                hasil.add(teks.substring(0, maks));
                teks = teks.substring(maks);
            }
            hasil.add(teks);
            return hasil;
        }
        FontRenderContext frc = g2.getFontRenderContext();
        StringBuilder baris = new StringBuilder();
        for (String kata : teks.split(" ")) {
            String coba = baris.length() == 0 ? kata : baris + " " + kata;
            if (font.getStringBounds(coba, frc).getWidth() > lebarMaks && baris.length() > 0) {
                hasil.add(baris.toString());
                baris = new StringBuilder(kata);
            } else {
                baris = new StringBuilder(coba);
            }
        }
        hasil.add(baris.toString());
        return hasil;
    }

    private static double garisTebal(Graphics2D g2, double lebar, double y) {
        if (g2 != null) {
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(0, (int) y, (int) lebar, (int) y);
            g2.drawLine(0, (int) y + 3, (int) lebar, (int) y + 3);
        }
        return y + 4;
    }

    private static double garisPutus(Graphics2D g2, double lebar, double y) {
        if (g2 != null) {
            g2.setStroke(new BasicStroke(0.7f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{3f, 3f}, 0f));
            g2.drawLine(0, (int) y, (int) lebar, (int) y);
            g2.setStroke(new BasicStroke(1f));
        }
        return y + 2;
    }

    /**
     * Uji tata letak tanpa menjalankan aplikasi : java -cp ... inventory.TiketAntrianFarmasi
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                previewContoh();
            }
        });
    }
}
