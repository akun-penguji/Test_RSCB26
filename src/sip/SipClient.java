package sip;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public abstract class SipClient {
    private static final Logger LOG = Logger.getLogger(SipClient.class.getName());
    private static volatile SipClient INSTANCE;

    protected final SipConfig config;
    protected volatile SipState state = SipState.OFFLINE;
    protected volatile boolean muted = false;
    protected volatile boolean heldLocal = false;
    protected final List<SipClientListener> listeners = new CopyOnWriteArrayList<>();

    protected SipClient(SipConfig config) {
        this.config = config;
    }

    public static synchronized SipClient getInstance(Properties prop) {
        if (INSTANCE == null) {
            SipConfig cfg = SipConfig.load(prop);
            LOG.info(cfg.summary());
            if (!cfg.isEnabled()) {
                INSTANCE = new DisabledSipClient(cfg, "SIP_PHONE=nonaktif di database.xml");
            } else if (!cfg.isValid()) {
                INSTANCE = new DisabledSipClient(cfg, "SIP config tidak lengkap (server/extension kosong)");
            } else {
                INSTANCE = createRealClient(cfg);
            }
        }
        return INSTANCE;
    }

    public static SipClient getInstance() {
        SipClient c = INSTANCE;
        if (c == null) {
            throw new IllegalStateException("SipClient belum di-init. Panggil getInstance(prop) dulu.");
        }
        return c;
    }

    private static SipClient createRealClient(SipConfig cfg) {
        try {
            return new PeersSipClient(cfg);
        } catch (Throwable t) {
            return new DisabledSipClient(cfg, "Peers SIP stack tidak tersedia: " + t.getMessage());
        }
    }

    public abstract void start();
    public abstract void stop();

    public abstract void call(String extension);
    public abstract void answer();
    public abstract void reject();
    public abstract void hangup();

    /** Toggle mikrofon mute. Return state baru (true = muted). */
    public abstract boolean toggleMute();

    /** Toggle hold (lokal-only: mikrofon + speaker dimatikan). Return state baru. */
    public abstract boolean toggleHold();

    /** Kirim satu digit DTMF saat in-call. */
    public abstract void sendDtmf(char digit);

    /** Blind transfer panggilan aktif ke extension target via feature code FreePBX `##`. */
    public abstract void blindTransfer(String targetExtension);

    public String  getCurrentRemote() { return null; }
    public boolean isMuted()          { return muted; }
    public boolean isHoldActive()     { return heldLocal; }
    public SipState  getState()  { return state; }
    public SipConfig getConfig() { return config; }

    public void addListener(SipClientListener l) {
        if (l == null) return;
        listeners.add(l);
        try { l.onStateChanged(state, "attach"); } catch (Exception ignored) {}
    }

    public void removeListener(SipClientListener l) {
        listeners.remove(l);
    }

    protected void setState(SipState newState, String message) {
        this.state = newState;
        for (SipClientListener l : listeners) {
            try { l.onStateChanged(newState, message); } catch (Exception ignored) {}
        }
    }
}
