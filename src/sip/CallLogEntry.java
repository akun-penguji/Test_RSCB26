package sip;

import java.sql.Timestamp;

public class CallLogEntry {
    public long      id;
    public String    extensionLokal;
    public String    extensionRemote;
    public String    namaRemote;
    public String    arah;      // in | out
    public String    status;    // answered | missed | rejected | failed | cancelled
    public Timestamp tglMulai;
    public Timestamp tglJawab;
    public Timestamp tglSelesai;
    public int       durasiDetik;
}
