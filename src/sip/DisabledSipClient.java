package sip;

import java.util.logging.Logger;

public class DisabledSipClient extends SipClient {
    private static final Logger LOG = Logger.getLogger(DisabledSipClient.class.getName());
    private final String reason;

    public DisabledSipClient(SipConfig config, String reason) {
        super(config);
        this.reason = reason == null ? "" : reason;
    }

    @Override public void start()  { LOG.info("SipClient dinonaktifkan: " + reason); setState(SipState.DISABLED, reason); }
    @Override public void stop()   { setState(SipState.OFFLINE, "stopped"); }
    @Override public void call(String extension) {}
    @Override public void answer() {}
    @Override public void reject() {}
    @Override public void hangup() {}
    @Override public boolean toggleMute() { return false; }
    @Override public boolean toggleHold() { return false; }
    @Override public void sendDtmf(char digit) {}
    @Override public void blindTransfer(String targetExtension) {}
}
