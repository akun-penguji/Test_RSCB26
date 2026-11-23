package sip.chat.rc;

import java.sql.Timestamp;

/** Ringkasan 1 percakapan DM (1-on-1) dari Rocket.Chat. */
public class RcConversation {
    public String    roomId;
    public String    peerUsername;  // extension lawan bicara
    public String    peerDisplay;   // nama
    public String    lastBody;
    public Timestamp lastTgl;
    public int       unread;
}
