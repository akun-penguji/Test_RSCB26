package sip;

public interface SipClientListener {

    enum CallEndReason { ANSWERED, MISSED, REJECTED, FAILED, CANCELLED }

    default void onStateChanged(SipState newState, String message) {}

    default void onIncomingCall(String remoteExtension, String displayName) {}

    default void onOutgoingCall(String remoteExtension) {}

    default void onOutgoingRinging(String remoteExtension) {}

    default void onCallAnswered(String remoteExtension) {}

    default void onCallEnded(String remoteExtension, CallEndReason reason) {}
}
