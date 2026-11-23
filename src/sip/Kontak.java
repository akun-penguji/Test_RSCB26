package sip;

public class Kontak {
    public String  extension;
    public String  nama;
    public String  unit;
    public String  lokasi;
    public boolean favorit;
    public boolean aktif;
    public String  sumber;   // manual | freepbx_sync
    public java.sql.Timestamp terakhirSync;

    public Kontak() {}

    public Kontak(String extension, String nama, String unit) {
        this.extension = extension;
        this.nama = nama;
        this.unit = unit;
        this.aktif = true;
        this.sumber = "manual";
    }

    @Override public String toString() {
        return extension + " " + nama + (unit != null && !unit.isEmpty() ? " (" + unit + ")" : "");
    }
}
