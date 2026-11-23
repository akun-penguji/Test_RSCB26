package voip;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simplified VoIP Manager untuk testing tanpa dependency SIP
 * Ini adalah implementasi mock untuk testing UI
 */
public class VoIPManagerSimple {
    private static VoIPManagerSimple instance;
    private List<VoIPEventListener> eventListeners = new ArrayList<>();
    private boolean isRegistered = false;
    private boolean inCall = false;
    private Timer timer = new Timer();
    
    private VoIPManagerSimple() {
        // Simulate auto-registration after 3 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                simulateRegistration();
            }
        }, 3000);
    }
    
    public static VoIPManagerSimple getInstance() {
        if (instance == null) {
            instance = new VoIPManagerSimple();
        }
        return instance;
    }
    
    private void simulateRegistration() {
        isRegistered = true;
        notifyEventListeners("Registration successful - Connected to PBX 192.168.88.119:5061");
        
        // Simulate incoming call after 10 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                simulateIncomingCall();
            }
        }, 10000);
    }
    
    private void simulateIncomingCall() {
        if (isRegistered && !inCall) {
            notifyEventListeners("Incoming call from: 081234567890");
            notifyEventListeners("INCOMING_CALL_START"); // Special event for overlay animation
            
            // Auto-hang up after 15 seconds if not answered
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!inCall) {
                        notifyEventListeners("Call missed");
                        notifyEventListeners("INCOMING_CALL_STOP"); // Stop animation
                    }
                }
            }, 15000);
        }
    }
    
    public void register() {
        notifyEventListeners("Trying to register to VoIP server...");
        // Simulate registration delay
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Math.random() > 0.2) { // 80% success rate
                    isRegistered = true;
                    notifyEventListeners("Registration successful");
                } else {
                    isRegistered = false;
                    notifyEventListeners("Registration failed: Connection timeout");
                }
            }
        }, 2000);
    }
    
    public void makeCall(String phoneNumber) {
        if (!isRegistered) {
            notifyEventListeners("Cannot make call: Not registered");
            return;
        }
        
        notifyEventListeners("Calling " + phoneNumber + "...");
        
        // Simulate call progress
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notifyEventListeners("Ringing...");
            }
        }, 1000);
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Math.random() > 0.3) { // 70% call success rate
                    inCall = true;
                    notifyEventListeners("Call connected");
                } else {
                    notifyEventListeners("Call failed: No answer");
                }
            }
        }, 4000);
    }
    
    public void hangUp() {
        if (inCall) {
            inCall = false;
            notifyEventListeners("Call ended");
            notifyEventListeners("INCOMING_CALL_STOP"); // Stop any ongoing animation
        }
    }
    
    public void answerCall() {
        if (!inCall) {
            inCall = true;
            notifyEventListeners("Call answered");
            notifyEventListeners("INCOMING_CALL_STOP"); // Stop animation
        }
    }
    
    public boolean isRegistered() {
        return isRegistered;
    }
    
    public boolean isInCall() {
        return inCall;
    }
    
    public void addEventListener(VoIPEventListener listener) {
        eventListeners.add(listener);
    }
    
    public void removeEventListener(VoIPEventListener listener) {
        eventListeners.remove(listener);
    }
    
    private void notifyEventListeners(String message) {
        for (VoIPEventListener listener : eventListeners) {
            listener.onVoIPEvent(message);
        }
    }
    
    public void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
        isRegistered = false;
        inCall = false;
    }
    
    public interface VoIPEventListener {
        void onVoIPEvent(String message);
    }
}