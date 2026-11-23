package voip;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;
import java.util.*;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.FileInputStream;
import java.util.Properties;

public class SIPClient implements SipListener {
    private static SIPClient instance;
    private SipFactory sipFactory;
    private SipStack sipStack;
    private SipProvider sipProvider;
    private ListeningPoint listeningPoint;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;
    private AddressFactory addressFactory;
    
    private String sipUser;
    private String sipPassword;
    private String sipServer;
    private int sipPort;
    private String extension;
    private boolean isRegistered = false;
    
    private Dialog currentDialog;
    private ClientTransaction registerTransaction;
    private ClientTransaction inviteTransaction;
    
    private List<SIPEventListener> eventListeners = new ArrayList<>();
    
    private SIPClient() {
        loadConfiguration();
        initializeSIP();
    }
    
    public static SIPClient getInstance() {
        if (instance == null) {
            instance = new SIPClient();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        try {
            Properties prop = new Properties();
            FileInputStream file = new FileInputStream("setting/database.xml");
            prop.loadFromXML(file);
            file.close();
            
            sipServer = koneksiDB.DECRIPSIPSERVER();
            sipPort = Integer.parseInt(koneksiDB.DECRIPSIPPORT());
            sipUser = koneksiDB.DECRIPSIPUSER();
            sipPassword = koneksiDB.DECRIPSIPPASSWORD();
            extension = koneksiDB.DECRIPSIPEXTENSION();
            
            if (sipServer == null || sipServer.isEmpty()) {
                sipServer = "192.168.88.119";
                sipPort = 5061;
            }
            
        } catch (Exception e) {
            System.err.println("Error loading VoIP configuration: " + e.getMessage());
            sipServer = "192.168.88.119";
            sipPort = 5061;
            sipUser = "";
            sipPassword = "";
            extension = "";
        }
    }
    
    private void initializeSIP() {
        try {
            sipFactory = SipFactory.getInstance();
            sipFactory.setPathName("gov.nist");
            
            Properties properties = new Properties();
            properties.setProperty("javax.sip.STACK_NAME", "KhanzaSIPStack");
            properties.setProperty("javax.sip.IP_ADDRESS", "0.0.0.0");
            properties.setProperty("javax.sip.OUTBOUND_PROXY", sipServer + ":" + sipPort + "/UDP");
            properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
            properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "khanzasip_debug.txt");
            properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "khanzasip_log.txt");
            
            sipStack = sipFactory.createSipStack(properties);
            
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            messageFactory = sipFactory.createMessageFactory();
            
            listeningPoint = sipStack.createListeningPoint("0.0.0.0", 5060, "UDP");
            sipProvider = sipStack.createSipProvider(listeningPoint);
            sipProvider.addSipListener(this);
            
        } catch (Exception e) {
            System.err.println("Failed to initialize SIP: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void register() {
        try {
            String fromName = sipUser;
            String fromSipAddress = sipServer;
            String fromDisplayName = "SIMRS Khanza";
            
            SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);
            fromAddress.setPort(sipPort);
            
            Address fromNameAddress = addressFactory.createAddress(fromAddress);
            fromNameAddress.setDisplayName(fromDisplayName);
            FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, null);
            
            String toUser = sipUser;
            String toSipAddress = sipServer;
            String toDisplayName = "SIMRS Khanza";
            
            SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
            toAddress.setPort(sipPort);
            Address toNameAddress = addressFactory.createAddress(toAddress);
            toNameAddress.setDisplayName(toDisplayName);
            ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);
            
            SipURI requestURI = addressFactory.createSipURI(toUser, sipServer);
            requestURI.setPort(sipPort);
            
            ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
            String ipAddress = listeningPoint.getIPAddress();
            int port = listeningPoint.getPort();
            ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress, port, "UDP", null);
            viaHeaders.add(viaHeader);
            
            CallIdHeader callIdHeader = sipProvider.getNewCallId();
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.REGISTER);
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
            
            Request register = messageFactory.createRequest(requestURI, Request.REGISTER, 
                    callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
                    
            SipURI contactURI = addressFactory.createSipURI(sipUser, ipAddress);
            contactURI.setPort(port);
            Address contactAddress = addressFactory.createAddress(contactURI);
            contactAddress.setDisplayName(sipUser);
            ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
            register.addHeader(contactHeader);
            
            ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(3600);
            register.addHeader(expiresHeader);
            
            AuthorizationHeader authorizationHeader = headerFactory.createAuthorizationHeader("Digest");
            authorizationHeader.setUsername(sipUser);
            authorizationHeader.setRealm(sipServer);
            authorizationHeader.setNonce("");
            authorizationHeader.setURI(requestURI);
            authorizationHeader.setResponse(sipPassword);
            register.addHeader(authorizationHeader);
            
            registerTransaction = sipProvider.getNewClientTransaction(register);
            registerTransaction.sendRequest();
            
            System.out.println("REGISTER request sent to " + sipServer + ":" + sipPort);
            
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void makeCall(String phoneNumber) {
        if (!isRegistered) {
            notifyEventListeners("Not registered. Please register first.");
            return;
        }
        
        try {
            String fromName = sipUser;
            String fromSipAddress = sipServer;
            String fromDisplayName = "SIMRS Khanza";
            
            SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);
            fromAddress.setPort(sipPort);
            
            Address fromNameAddress = addressFactory.createAddress(fromAddress);
            fromNameAddress.setDisplayName(fromDisplayName);
            FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "12345");
            
            String toUser = phoneNumber;
            String toSipAddress = sipServer;
            String toDisplayName = phoneNumber;
            
            SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
            toAddress.setPort(sipPort);
            Address toNameAddress = addressFactory.createAddress(toAddress);
            toNameAddress.setDisplayName(toDisplayName);
            ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);
            
            SipURI requestURI = addressFactory.createSipURI(toUser, sipServer);
            requestURI.setPort(sipPort);
            
            ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
            String ipAddress = listeningPoint.getIPAddress();
            int port = listeningPoint.getPort();
            ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress, port, "UDP", null);
            viaHeaders.add(viaHeader);
            
            CallIdHeader callIdHeader = sipProvider.getNewCallId();
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
            
            Request invite = messageFactory.createRequest(requestURI, Request.INVITE, 
                    callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
                    
            SipURI contactURI = addressFactory.createSipURI(sipUser, ipAddress);
            contactURI.setPort(port);
            Address contactAddress = addressFactory.createAddress(contactURI);
            contactAddress.setDisplayName(sipUser);
            ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
            invite.addHeader(contactHeader);
            
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
            String sdp = createSDPOffer(ipAddress);
            invite.setContent(sdp, contentTypeHeader);
            
            inviteTransaction = sipProvider.getNewClientTransaction(invite);
            inviteTransaction.sendRequest();
            
            notifyEventListeners("Calling " + phoneNumber + "...");
            
        } catch (Exception e) {
            System.err.println("Failed to make call: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void hangUp() {
        try {
            if (currentDialog != null) {
                Request bye = currentDialog.createRequest(Request.BYE);
                ClientTransaction byeTransaction = sipProvider.getNewClientTransaction(bye);
                currentDialog.sendRequest(byeTransaction);
                currentDialog = null;
                notifyEventListeners("Call ended");
            }
        } catch (Exception e) {
            System.err.println("Failed to hang up: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String createSDPOffer(String ipAddress) {
        StringBuilder sdp = new StringBuilder();
        sdp.append("v=0\r\n");
        sdp.append("o=khanza 0 0 IN IP4 ").append(ipAddress).append("\r\n");
        sdp.append("s=Khanza VoIP Call\r\n");
        sdp.append("c=IN IP4 ").append(ipAddress).append("\r\n");
        sdp.append("t=0 0\r\n");
        sdp.append("m=audio 7078 RTP/AVP 0 8 101\r\n");
        sdp.append("a=rtpmap:0 PCMU/8000\r\n");
        sdp.append("a=rtpmap:8 PCMA/8000\r\n");
        sdp.append("a=rtpmap:101 telephone-event/8000\r\n");
        sdp.append("a=sendrecv\r\n");
        return sdp.toString();
    }
    
    @Override
    public void processRequest(RequestEvent requestEvent) {
        Request request = requestEvent.getRequest();
        ServerTransaction serverTransaction = requestEvent.getServerTransaction();
        
        System.out.println("Request received: " + request.getMethod());
        
        try {
            if (request.getMethod().equals(Request.INVITE)) {
                if (serverTransaction == null) {
                    serverTransaction = sipProvider.getNewServerTransaction(request);
                }
                
                Response ringingResponse = messageFactory.createResponse(Response.RINGING, request);
                serverTransaction.sendResponse(ringingResponse);
                
                FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
                String caller = fromHeader.getAddress().getDisplayName();
                if (caller == null || caller.isEmpty()) {
                    caller = fromHeader.getAddress().getURI().toString();
                }
                
                notifyEventListeners("Incoming call from: " + caller);
                
            } else if (request.getMethod().equals(Request.BYE)) {
                Response okResponse = messageFactory.createResponse(Response.OK, request);
                serverTransaction.sendResponse(okResponse);
                currentDialog = null;
                notifyEventListeners("Call ended by remote party");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void processResponse(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
        
        System.out.println("Response received: " + response.getStatusCode() + " for " + cseq.getMethod());
        
        if (cseq.getMethod().equals(Request.REGISTER)) {
            if (response.getStatusCode() == Response.OK) {
                isRegistered = true;
                notifyEventListeners("Registration successful");
            } else if (response.getStatusCode() == Response.UNAUTHORIZED) {
                isRegistered = false;
                notifyEventListeners("Registration failed: Unauthorized");
            }
        } else if (cseq.getMethod().equals(Request.INVITE)) {
            if (response.getStatusCode() == Response.OK) {
                currentDialog = responseEvent.getDialog();
                notifyEventListeners("Call connected");
            } else if (response.getStatusCode() == Response.RINGING) {
                notifyEventListeners("Ringing...");
            } else if (response.getStatusCode() >= 400) {
                notifyEventListeners("Call failed: " + response.getReasonPhrase());
            }
        }
    }
    
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        notifyEventListeners("Request timeout");
    }
    
    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        notifyEventListeners("IO Exception: " + exceptionEvent.getHost());
    }
    
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
    }
    
    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        currentDialog = null;
        notifyEventListeners("Call terminated");
    }
    
    public void addEventListener(SIPEventListener listener) {
        eventListeners.add(listener);
    }
    
    public void removeEventListener(SIPEventListener listener) {
        eventListeners.remove(listener);
    }
    
    private void notifyEventListeners(String message) {
        for (SIPEventListener listener : eventListeners) {
            listener.onSIPEvent(message);
        }
    }
    
    public boolean isRegistered() {
        return isRegistered;
    }
    
    public void shutdown() {
        try {
            if (sipStack != null) {
                sipStack.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public interface SIPEventListener {
        void onSIPEvent(String message);
    }
}