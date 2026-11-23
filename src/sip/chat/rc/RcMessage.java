package sip.chat.rc;

import java.sql.Timestamp;

/** Satu pesan chat dari Rocket.Chat. */
public class RcMessage {
    public String    id;             // msg _id
    public String    roomId;         // rid
    public String    fromUsername;   // u.username (extension pengirim)
    public String    fromDisplay;    // u.name
    public String    body;           // msg
    public Timestamp tglKirim;       // ts
    public String    noRawat;        // dari attachments[0].text kalau ada

    public boolean fromMe(String myUsername) {
        return fromUsername != null && fromUsername.equals(myUsername);
    }
}
