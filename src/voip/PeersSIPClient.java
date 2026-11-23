package voip;

import java.awt.Frame;
import net.sourceforge.peers.Config;
import net.sourceforge.peers.FileLogger;
import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.media.AbstractSoundManager;
import net.sourceforge.peers.media.AbstractSoundManagerFactory;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.media.javaxsound.JavaxSoundManager;
import net.sourceforge.peers.sip.core.useragent.SipListener;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.rtp.RFC4733;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.transactionuser.Dialog;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.RFC3261;
// import fungsi.koneksiDB;
// import AESsecurity.EnkripsiAES;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;

public class PeersSIPClient implements SipListener, AbstractSoundManagerFactory {
    private static PeersSIPClient instance;
    private UserAgent userAgent;
    private Config config;
    private AbstractSoundManager soundManager;
    private Logger logger;
    private boolean isRegistered = false;
    
    private SipRequest incomingRequest;
    private SipResponse lastResponse;
    private VoIPOutgoingDialog outgoingDialog;
    
    private List<SIPEventListener> eventListeners = new ArrayList<>();
    
    public interface SIPEventListener {
        void onSIPEvent(String message);
    }
    
    private PeersSIPClient() {
        initializeSIP();
    }
    
    public static synchronized PeersSIPClient getInstance() {
        if (instance == null) {
            instance = new PeersSIPClient();
        }
        return instance;
    }
    
    private void initializeSIP() {
        try {
            // Load configuration from database.xml
            Properties prop = new Properties();
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            
            // Use proper configuration like RSDH
            String sipUser = "119";
            String sipPass = "119";
            String sipServer = "192.168.88.119";
            int sipServerPort = 5061;
            
            // Use localhost for local binding to avoid IP conflicts
            String localIP = "127.0.0.1";
            int localPort = 5060; // Use different port for local binding
            
            notifyEventListeners("Initializing SIP with Peers library...");
            notifyEventListeners("Server: " + sipServer + ":" + sipServerPort);
            notifyEventListeners("User: " + sipUser);
            notifyEventListeners("Local IP: " + localIP);
            
            // Initialize logger
            logger = new FileLogger(null);
            
            // Initialize sound manager
            soundManager = new JavaxSoundManager(false, logger, null);
            
            // Create config
            config = new JavaConfig();
            config.setAuthorizationUsername(sipUser);
            config.setUserPart(sipUser);
            config.setPassword(sipPass);
            config.setDomain(sipServer);
            config.setLocalInetAddress(InetAddress.getByName(localIP));
            config.setMediaDebug(false);
            config.setSipPort(localPort);
            config.setMediaMode(MediaMode.captureAndPlayback);
            
            // Create user agent
            userAgent = new UserAgent(this, config, logger);
            
            notifyEventListeners("SIP initialized successfully");
            
            // Auto-register after 1 second
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(1000);
                    register();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
        } catch (Exception e) {
            notifyEventListeners("Failed to initialize SIP: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void register() {
        try {
            if (userAgent != null) {
                notifyEventListeners("Registering to SIP server...");
                userAgent.register();
            }
        } catch (Exception e) {
            notifyEventListeners("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void makeCall(String phoneNumber) {
        try {
            if (!isRegistered) {
                notifyEventListeners("Cannot make call: Not registered");
                return;
            }
            
            if (userAgent != null) {
                notifyEventListeners("Calling " + phoneNumber + "...");
                String sipUri = "sip:" + phoneNumber + "@" + config.getDomain();
                userAgent.invite(sipUri, null);
                
                // Show outgoing call dialog
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        Frame parentFrame = null;
                        for (Frame frame : Frame.getFrames()) {
                            if (frame.isVisible()) {
                                parentFrame = frame;
                                break;
                            }
                        }
                        
                        outgoingDialog = new VoIPOutgoingDialog(parentFrame, phoneNumber, this);
                        outgoingDialog.setVisible(true);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            notifyEventListeners("Failed to make call: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void hangUp() {
        try {
            if (userAgent != null && lastResponse != null) {
                String callId = Utils.getMessageCallId(lastResponse);
                DialogManager dialogManager = userAgent.getDialogManager();
                Dialog dialog = dialogManager.getDialog(callId);
                if (dialog != null) {
                    // Create BYE request and terminate
                    userAgent.terminate(incomingRequest != null ? incomingRequest : null);
                    notifyEventListeners("Call ended");
                }
            }
        } catch (Exception e) {
            notifyEventListeners("Failed to hang up: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void answerCall() {
        try {
            if (userAgent != null && incomingRequest != null) {
                String callId = Utils.getMessageCallId(incomingRequest);
                DialogManager dialogManager = userAgent.getDialogManager();
                Dialog dialog = dialogManager.getDialog(callId);
                userAgent.acceptCall(incomingRequest, dialog);
                notifyEventListeners("Call answered");
                incomingRequest = null;
            }
        } catch (Exception e) {
            notifyEventListeners("Failed to answer call: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isRegistered() {
        return isRegistered && userAgent != null && userAgent.isRegistered();
    }
    
    public void addEventListener(SIPEventListener listener) {
        eventListeners.add(listener);
    }
    
    private void notifyEventListeners(String message) {
        for (SIPEventListener listener : eventListeners) {
            listener.onSIPEvent(message);
        }
    }
    
    public void shutdown() {
        try {
            if (userAgent != null && isRegistered) {
                userAgent.unregister();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // SipListener implementation
    @Override
    public void registering(SipRequest sipRequest) {
        notifyEventListeners("Registering...");
    }
    
    @Override
    public void registerSuccessful(SipResponse sipResponse) {
        isRegistered = true;
        notifyEventListeners("✅ Registration successful - VoIP ready!");
    }
    
    @Override
    public void registerFailed(SipResponse sipResponse) {
        isRegistered = false;
        notifyEventListeners("❌ Registration failed: " + sipResponse.getReasonPhrase());
    }
    
    @Override
    public void incomingCall(SipRequest sipRequest, SipResponse provResponse) {
        incomingRequest = sipRequest;
        String caller = getCallerFromRequest(sipRequest);
        notifyEventListeners("INCOMING_CALL_START");
        notifyEventListeners("📞 Incoming call from: " + caller);
        
        // Show incoming call dialog
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                Frame parentFrame = null;
                // Try to find the main frame
                for (Frame frame : Frame.getFrames()) {
                    if (frame.isVisible()) {
                        parentFrame = frame;
                        break;
                    }
                }
                
                VoIPCallDialog callDialog = new VoIPCallDialog(parentFrame, caller, caller, this);
                callDialog.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    @Override
    public void remoteHangup(SipRequest sipRequest) {
        notifyEventListeners("Call ended by remote party");
        notifyEventListeners("INCOMING_CALL_STOP");
    }
    
    @Override
    public void ringing(SipResponse sipResponse) {
        lastResponse = sipResponse;
        notifyEventListeners("📞 Ringing...");
        
        if (outgoingDialog != null) {
            outgoingDialog.updateStatus("ringing");
        }
    }
    
    @Override
    public void calleePickup(SipResponse sipResponse) {
        lastResponse = sipResponse;
        notifyEventListeners("✅ Call connected");
        
        if (outgoingDialog != null) {
            outgoingDialog.updateStatus("connected");
        }
    }
    
    @Override
    public void error(SipResponse sipResponse) {
        notifyEventListeners("❌ SIP Error: " + sipResponse.getReasonPhrase());
    }
    
    @Override
    public void dtmfEvent(RFC4733.DTMFEvent dtmfEvent, int duration) {
        // Handle DTMF events if needed
    }
    
    private String getCallerFromRequest(SipRequest request) {
        try {
            String fromHeader = request.getSipHeaders().get(new SipHeaderFieldName(RFC3261.HDR_FROM)).getValue();
            // Extract display name or user part from From header
            if (fromHeader.contains("\"")) {
                return fromHeader.substring(fromHeader.indexOf("\"") + 1, fromHeader.lastIndexOf("\""));
            } else if (fromHeader.contains("sip:")) {
                String sipPart = fromHeader.substring(fromHeader.indexOf("sip:") + 4);
                if (sipPart.contains("@")) {
                    return sipPart.substring(0, sipPart.indexOf("@"));
                }
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    // AbstractSoundManagerFactory implementation
    @Override
    public AbstractSoundManager getSoundManager() {
        return soundManager;
    }
}