package sip;

/**
 * Implementasi {@link net.sourceforge.peers.Logger} yang MEMBUNGKAM log DEBUG &
 * trace jaringan SIP (UdpMessageSender / TransportManager / dll) yang membanjiri
 * console aplikasi. Hanya error yang diteruskan ke System.err.
 *
 * Dipakai menggantikan peers FileLogger (yang menulis DEBUG ke System.out).
 */
public final class SilentSipLogger implements net.sourceforge.peers.Logger {
    @Override public void debug(String message) { /* sengaja dibungkam */ }
    @Override public void info(String message) { /* sengaja dibungkam */ }
    @Override public void error(String message) { System.err.println("SIP ERROR: " + message); }
    @Override public void error(String message, Exception exception) { System.err.println("SIP ERROR: " + message + " - " + exception); }
    @Override public void traceNetwork(String message, String direction) { /* sengaja dibungkam */ }
}
