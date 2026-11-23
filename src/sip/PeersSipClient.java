package sip;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.peers.FileLogger;
import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.media.MediaManager;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.media.javaxsound.JavaxSoundManager;
import net.sourceforge.peers.rtp.RFC4733;
import net.sourceforge.peers.sip.RFC3261;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.core.useragent.SipListener;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.transactionuser.Dialog;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;

/**
 * Implementasi SipClient dengan library Peers. Mendukung register, call,
 * mute/hold (lokal via javax.sound), DTMF, blind transfer (feature code `##`),
 * dan auto-reconnect exponential backoff.
 */
public class PeersSipClient extends SipClient implements SipListener {
    private static final Logger LOG = Logger.getLogger(PeersSipClient.class.getName());

    private static final int INITIAL_BACKOFF_SEC = 5;
    private static final int MAX_BACKOFF_SEC     = 60;

    private volatile UserAgent userAgent;
    private volatile JavaConfig peersConfig;
    private volatile net.sourceforge.peers.Logger peersLogger;
    private volatile JavaxSoundManager soundManager;

    private volatile SipRequest activeRequest;
    private volatile String currentRemote;
    private volatile boolean userInitiatedStop = false;

    private volatile ScheduledExecutorService reconnectExec;
    private volatile int reconnectDelaySec = INITIAL_BACKOFF_SEC;

    public PeersSipClient(SipConfig config) {
        super(config);
    }

    // ===== Lifecycle =====

    @Override
    public void start() {
        userInitiatedStop = false;
        doStart();
    }

    private void doStart() {
        if (state == SipState.REGISTERED || state == SipState.CONNECTING) return;
        setState(SipState.CONNECTING, "Registering ke " + config.getSrvHost());
        try {
            // Pakai SilentSipLogger agar DEBUG/trace SIP (UdpMessageSender/TransportManager)
            // tidak membanjiri console. FileLogger lama menulis DEBUG ke System.out.
            peersLogger = new SilentSipLogger();
            soundManager = new JavaxSoundManager(false, peersLogger, null);

            peersConfig = new JavaConfig();
            peersConfig.setAuthorizationUsername(config.getUser());
            peersConfig.setUserPart(config.getUser());
            peersConfig.setPassword(config.getPass());
            peersConfig.setDomain(config.getSrvHost());
            if (!config.getIp().isEmpty()) {
                peersConfig.setLocalInetAddress(InetAddress.getByName(config.getIp()));
            }
            peersConfig.setMediaDebug(false);
            peersConfig.setSipPort(config.getSrvPort());
            peersConfig.setMediaMode(MediaMode.captureAndPlayback);

            userAgent = new UserAgent(this, peersConfig, peersLogger);
            userAgent.register();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Gagal start SIP", e);
            setState(SipState.FAILED, "Start error: " + e.getMessage());
            scheduleReconnect();
        }
    }

    @Override
    public void stop() {
        userInitiatedStop = true;
        cancelReconnect();
        setState(SipState.STOPPING, "shutting down");
        try {
            if (userAgent != null) {
                try { if (userAgent.isRegistered()) userAgent.unregister(); } catch (Exception ignored) {}
                try { userAgent.close(); } catch (Exception ignored) {}
            }
        } finally {
            userAgent = null;
            peersConfig = null;
            peersLogger = null;
            soundManager = null;
            activeRequest = null;
            currentRemote = null;
            setState(SipState.OFFLINE, "stopped");
        }
    }

    // ===== Auto-reconnect =====

    private synchronized void scheduleReconnect() {
        if (userInitiatedStop) return;
        if (reconnectExec == null || reconnectExec.isShutdown()) {
            reconnectExec = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "sip-reconnect");
                t.setDaemon(true);
                return t;
            });
        }
        final int delay = reconnectDelaySec;
        LOG.info("SIP reconnect dijadwal dalam " + delay + " detik");
        reconnectExec.schedule(() -> {
            if (userInitiatedStop) return;
            if (state == SipState.REGISTERED) return;
            try {
                if (userAgent != null) {
                    try { userAgent.close(); } catch (Exception ignored) {}
                }
                userAgent = null;
                peersConfig = null;
            } catch (Exception ignored) {}
            doStart();
        }, delay, TimeUnit.SECONDS);
        reconnectDelaySec = Math.min(reconnectDelaySec * 2, MAX_BACKOFF_SEC);
    }

    private synchronized void cancelReconnect() {
        if (reconnectExec != null) {
            reconnectExec.shutdownNow();
            reconnectExec = null;
        }
        reconnectDelaySec = INITIAL_BACKOFF_SEC;
    }

    // ===== Call control =====

    @Override
    public void call(String extension) {
        if (userAgent == null || state != SipState.REGISTERED) {
            LOG.warning("call() ditolak, state=" + state);
            return;
        }
        if (activeRequest != null) return;
        try {
            String uri = "sip:" + extension + "@" + peersConfig.getDomain();
            String callId = Utils.generateCallID(peersConfig.getLocalInetAddress());
            activeRequest = userAgent.invite(uri, callId);
            currentRemote = extension;
            for (SipClientListener l : listeners) {
                try { l.onOutgoingCall(extension); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "call() gagal", e);
            activeRequest = null; currentRemote = null;
        }
    }

    @Override
    public void answer() {
        if (activeRequest == null || userAgent == null) return;
        try {
            String callId = Utils.getMessageCallId(activeRequest);
            DialogManager dm = userAgent.getDialogManager();
            Dialog dialog = dm.getDialog(callId);
            userAgent.acceptCall(activeRequest, dialog);
            for (SipClientListener l : listeners) {
                try { l.onCallAnswered(currentRemote); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "answer() gagal", e);
        }
    }

    @Override
    public void reject() {
        if (activeRequest == null || userAgent == null) return;
        try { userAgent.rejectCall(activeRequest); }
        catch (Exception e) { LOG.log(Level.SEVERE, "reject() gagal", e); }
        finally {
            String from = currentRemote;
            activeRequest = null; currentRemote = null;
            for (SipClientListener l : listeners) {
                try { l.onCallEnded(from, SipClientListener.CallEndReason.REJECTED); } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public void hangup() {
        if (activeRequest == null || userAgent == null) return;
        try { userAgent.terminate(activeRequest); }
        catch (Exception e) { LOG.log(Level.SEVERE, "hangup() gagal", e); }
        finally {
            String from = currentRemote;
            activeRequest = null; currentRemote = null;
            if (muted)     { AudioMute.setMicMute(false);     muted = false; }
            if (heldLocal) { AudioMute.setSpeakerMute(false); heldLocal = false; }
            for (SipClientListener l : listeners) {
                try { l.onCallEnded(from, SipClientListener.CallEndReason.CANCELLED); } catch (Exception ignored) {}
            }
        }
    }

    // ===== Mute / Hold / DTMF / Transfer =====

    @Override
    public boolean toggleMute() {
        muted = !muted;
        AudioMute.setMicMute(muted);
        return muted;
    }

    @Override
    public boolean toggleHold() {
        heldLocal = !heldLocal;
        AudioMute.setMicMute(heldLocal || muted);
        AudioMute.setSpeakerMute(heldLocal);
        return heldLocal;
    }

    @Override
    public void sendDtmf(char digit) {
        if (userAgent == null) return;
        try {
            MediaManager mm = userAgent.getMediaManager();
            if (mm != null) mm.sendDtmf(digit);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "sendDtmf gagal", e);
        }
    }

    @Override
    public void blindTransfer(String targetExtension) {
        // Peers tidak expose SIP REFER, jadi pakai FreePBX feature code `##<ext>`
        // via DTMF. Ini men-trigger Asterisk blind transfer di context `transfer_context`.
        if (userAgent == null || targetExtension == null || targetExtension.isEmpty()) return;
        try {
            MediaManager mm = userAgent.getMediaManager();
            if (mm == null) return;
            mm.sendDtmf('#');
            mm.sendDtmf('#');
            Thread.sleep(150);
            for (char c : targetExtension.toCharArray()) {
                mm.sendDtmf(c);
                Thread.sleep(100);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "blindTransfer gagal", e);
        }
    }

    @Override
    public String getCurrentRemote() { return currentRemote; }

    // ===== SipListener callbacks =====

    @Override
    public void registering(SipRequest sipRequest) {
        setState(SipState.CONNECTING, "Registering...");
    }

    @Override
    public void registerSuccessful(SipResponse sipResponse) {
        reconnectDelaySec = INITIAL_BACKOFF_SEC;
        cancelReconnect();
        String msg = "Registered " + config.getDisplayName() + " (" + config.getNum() + ")";
        LOG.info(msg);
        setState(SipState.REGISTERED, msg);
    }

    @Override
    public void registerFailed(SipResponse sipResponse) {
        String reason = sipResponse == null
                ? "no response"
                : (sipResponse.getStatusCode() + " " + sipResponse.getReasonPhrase());
        LOG.warning("Register failed: " + reason);
        setState(SipState.FAILED, "Register failed: " + reason);
        scheduleReconnect();
    }

    @Override
    public void incomingCall(SipRequest sipRequest, SipResponse provResponse) {
        this.activeRequest = sipRequest;
        String from = extractFromExtension(sipRequest);
        this.currentRemote = from;
        LOG.info("Incoming call dari " + from);
        for (SipClientListener l : listeners) {
            try { l.onIncomingCall(from, from); } catch (Exception ignored) {}
        }
    }

    @Override
    public void remoteHangup(SipRequest sipRequest) {
        String from = currentRemote;
        activeRequest = null; currentRemote = null;
        if (muted)     { AudioMute.setMicMute(false);     muted = false; }
        if (heldLocal) { AudioMute.setSpeakerMute(false); heldLocal = false; }
        for (SipClientListener l : listeners) {
            try { l.onCallEnded(from, SipClientListener.CallEndReason.CANCELLED); } catch (Exception ignored) {}
        }
    }

    @Override
    public void ringing(SipResponse sipResponse) {
        for (SipClientListener l : listeners) {
            try { l.onOutgoingRinging(currentRemote); } catch (Exception ignored) {}
        }
    }

    @Override
    public void calleePickup(SipResponse sipResponse) {
        for (SipClientListener l : listeners) {
            try { l.onCallAnswered(currentRemote); } catch (Exception ignored) {}
        }
    }

    @Override
    public void error(SipResponse sipResponse) {
        String reason = sipResponse == null ? "" :
                (sipResponse.getStatusCode() + " " + sipResponse.getReasonPhrase());
        LOG.warning("SIP error: " + reason);
        String from = currentRemote;
        activeRequest = null; currentRemote = null;
        for (SipClientListener l : listeners) {
            try { l.onCallEnded(from, SipClientListener.CallEndReason.FAILED); } catch (Exception ignored) {}
        }
    }

    @Override
    public void dtmfEvent(RFC4733.DTMFEvent dtmfEvent, int i) {}

    // ===== helpers =====

    private static String extractFromExtension(SipRequest req) {
        if (req == null) return "";
        try {
            return extractSipUser(req.getSipHeaders().get(
                new SipHeaderFieldName(RFC3261.HDR_FROM)).getValue());
        } catch (Exception e) { return ""; }
    }

    private static String extractSipUser(String headerValue) {
        if (headerValue == null) return "";
        int i = headerValue.indexOf(':');
        int j = headerValue.indexOf('@', i + 1);
        if (i >= 0 && j > i) return headerValue.substring(i + 1, j);
        return headerValue;
    }
}
