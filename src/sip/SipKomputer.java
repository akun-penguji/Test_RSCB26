package sip;

import java.sql.Timestamp;

/** Row dari tabel {@code sip_komputer} — config SIP untuk satu komputer client. */
public class SipKomputer {
    public String    idKomputer;      // MAC address primary key
    public String    hostname;
    public String    extension;
    public String    username;
    public String    password;
    public String    displayName;
    public boolean   autoAnswer;
    public int       ringtoneVolume = 80;
    public boolean   aktif          = true;
    public String    keterangan;
    public String    rcUserId;       // Rocket.Chat user _id (cache, diisi saat provision)
    public Timestamp terakhirUpdate;

    public SipKomputer() {}

    @Override public String toString() {
        return idKomputer + " / " + extension + " " + displayName;
    }
}
