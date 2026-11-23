package sip;

import java.sql.Timestamp;

/**
 * Listener yang merekam riwayat panggilan ke tabel {@code sip_call_log}.
 * Track start / answer / end events, insert 1 row saat panggilan berakhir.
 */
public class CallLogPersister implements SipClientListener {
    private final SipClient sipClient;
    private final CallLogService service;
    private final KontakService kontakService;

    private volatile CallLogEntry active;

    public CallLogPersister(SipClient sipClient, CallLogService service, KontakService kontakService) {
        this.sipClient = sipClient;
        this.service = service;
        this.kontakService = kontakService;
    }

    @Override
    public void onIncomingCall(String remoteExtension, String displayName) {
        startEntry("in", remoteExtension);
    }

    @Override
    public void onOutgoingCall(String remoteExtension) {
        startEntry("out", remoteExtension);
    }

    @Override
    public void onCallAnswered(String remoteExtension) {
        if (active != null) active.tglJawab = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void onCallEnded(String remoteExtension, CallEndReason reason) {
        CallLogEntry e = active;
        if (e == null) return;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        e.tglSelesai = now;
        if (e.tglJawab != null) {
            e.status = "answered";
            e.durasiDetik = (int) ((now.getTime() - e.tglJawab.getTime()) / 1000);
        } else {
            switch (reason) {
                case REJECTED:  e.status = "rejected";  break;
                case FAILED:    e.status = "failed";    break;
                case CANCELLED: e.status = "in".equals(e.arah) ? "missed" : "cancelled"; break;
                case MISSED:    e.status = "missed";    break;
                default:        e.status = "answered"; break;
            }
            e.durasiDetik = 0;
        }
        try { service.insert(e); } catch (Exception ignored) {}
        active = null;
    }

    private void startEntry(String arah, String remoteExt) {
        CallLogEntry e = new CallLogEntry();
        e.extensionLokal  = sipClient.getConfig().getNum();
        e.extensionRemote = remoteExt == null ? "" : remoteExt;
        e.arah            = arah;
        e.status          = "failed"; // tentative, akan di-update di onCallEnded
        e.tglMulai        = new Timestamp(System.currentTimeMillis());
        if (kontakService != null && remoteExt != null) {
            Kontak k = kontakService.find(remoteExt);
            if (k != null) e.namaRemote = k.nama;
        }
        this.active = e;
    }
}
