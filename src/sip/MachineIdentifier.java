package sip;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * Identifier komputer client: MAC address dari interface fisik utama.
 * Cross-platform: Windows, macOS, Linux.
 *
 * <p>Strategi pemilihan interface primer:
 * <ol>
 *   <li>Skip loopback, sub-interface virtual, interface yang tidak UP.</li>
 *   <li>Skip nama virtual yang dikenal di Linux (docker, veth, virbr, br-, tun,
 *       tap, zt, wg), Mac (utun, bridge, awdl, llw, anpi, ap1, gif, stf,
 *       pktap), Windows (vethernet, vmware, hyper-v-prefixed virtual).</li>
 *   <li>Skip MAC yang <b>locally-administered</b> (bit {@code 0x02} di byte
 *       pertama = 1). NIC fisik punya MAC globally-unique (bit ini 0); MAC
 *       buatan Docker/VPN/Hyper-V hampir selalu set bit ini.</li>
 *   <li>Return interface pertama yang lolos filter dan punya MAC non-zero.</li>
 * </ol>
 */
public final class MachineIdentifier {
    private static final Logger LOG = Logger.getLogger(MachineIdentifier.class.getName());

    private static final String[] VIRTUAL_PREFIXES = {
            // Linux
            "docker", "veth", "vboxnet", "virbr", "br-", "tun", "tap", "zt", "wg",
            // macOS
            "utun", "awdl", "llw", "anpi", "ap1", "gif", "stf", "pktap", "bridge",
            // Windows (lowercase karena kita lowercase nama-nya)
            "vethernet", "vmware", "vmnet", "vbox"
    };

    private MachineIdentifier() {}

    /**
     * MAC address interface fisik utama, format lowercase hex colon-separated.
     * Contoh: {@code "f4:c8:8a:51:42:3a"}. Null kalau tidak terdeteksi.
     */
    public static String primaryMacAddress() {
        NetworkInterface ni = primaryInterface();
        if (ni == null) return null;
        try {
            byte[] mac = ni.getHardwareAddress();
            if (mac == null || mac.length == 0) return null;
            return formatMac(mac);
        } catch (Exception e) {
            LOG.warning("Gagal baca MAC address: " + e);
            return null;
        }
    }

    public static String hostname() {
        try { return InetAddress.getLocalHost().getHostName(); }
        catch (Exception e) { return "unknown"; }
    }

    public static String primaryLocalIp() {
        NetworkInterface ni = primaryInterface();
        if (ni == null) return "";
        Enumeration<InetAddress> addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
            InetAddress a = addrs.nextElement();
            if (a instanceof Inet4Address && !a.isLoopbackAddress()) return a.getHostAddress();
        }
        return "";
    }

    /**
     * Pilih IP source untuk komunikasi ke {@code targetHost}. Dipakai
     * SipConfig untuk menentukan bind address peers-lib.
     *
     * <p>Logic:
     * <ul>
     *   <li>Kalau {@code targetHost} di range Tailscale CGNAT (100.64.0.0/10) DAN
     *       mesin punya interface Tailscale aktif → return Tailscale IP.</li>
     *   <li>Else → fallback {@link #primaryLocalIp()} (LAN IP).</li>
     * </ul>
     *
     * <p>Additive — tidak mengubah behavior LAN existing. Kasus PBX di LAN tetap
     * pakai LAN IP seperti biasa, bahkan kalau Tailscale aktif (target bukan 100.x).
     */
    public static String pickLocalIpFor(String targetHost) {
        if (isTailscaleRange(targetHost)) {
            String ts = tailscaleLocalIp();
            if (ts != null && !ts.isEmpty()) {
                LOG.info("SIP target " + targetHost + " di Tailscale range → bind ke " + ts);
                return ts;
            }
            LOG.warning("SIP target " + targetHost + " di Tailscale range tapi interface tailscale0 tidak ditemukan; fallback ke LAN");
        }
        return primaryLocalIp();
    }

    /**
     * Cek apakah {@code ip} di range Tailscale CGNAT 100.64.0.0/10.
     * Tailscale assign IP dari blok ini ke peer dalam tailnet.
     */
    public static boolean isTailscaleRange(String ip) {
        if (ip == null) return false;
        String[] octets = ip.trim().split("\\.");
        if (octets.length < 2) return false;
        try {
            int o1 = Integer.parseInt(octets[0]);
            int o2 = Integer.parseInt(octets[1]);
            return o1 == 100 && o2 >= 64 && o2 <= 127;
        } catch (NumberFormatException e) { return false; }
    }

    /**
     * IPv4 address dari interface "tailscale0" (atau yang nama-nya mengandung "tailscale").
     * Return {@code null} kalau tidak ada / interface down.
     */
    public static String tailscaleLocalIp() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                String n = ni.getName() == null ? "" : ni.getName().toLowerCase();
                String d = ni.getDisplayName() == null ? "" : ni.getDisplayName().toLowerCase();
                if (!n.contains("tailscale") && !d.contains("tailscale")) continue;
                if (!ni.isUp()) continue;
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress a = addrs.nextElement();
                    if (a instanceof Inet4Address && !a.isLoopbackAddress()) {
                        return a.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            LOG.warning("Gagal cari Tailscale interface: " + e);
        }
        return null;
    }

    private static NetworkInterface primaryInterface() {
        List<NetworkInterface> physical = new ArrayList<>();
        List<NetworkInterface> fallback = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!isCandidate(ni)) continue;
                byte[] mac = ni.getHardwareAddress();
                if (mac == null || mac.length == 0) continue;
                if (hasNameOfVirtual(ni)) continue;
                if (isLocallyAdministered(mac)) {
                    fallback.add(ni);   // MAC virtual — pakai kalau tidak ada physical
                    continue;
                }
                physical.add(ni);
            }
        } catch (Exception e) {
            LOG.warning("Gagal enumerasi network interface: " + e);
        }
        if (!physical.isEmpty()) return preferIPv4(physical);
        if (!fallback.isEmpty()) return preferIPv4(fallback);
        return null;
    }

    private static boolean isCandidate(NetworkInterface ni) {
        try {
            if (ni.isLoopback()) return false;
            if (!ni.isUp())      return false;
            if (ni.isVirtual())  return false;   // sub-interface (eth0:1 dll)
        } catch (Exception e) { return false; }
        return true;
    }

    private static boolean hasNameOfVirtual(NetworkInterface ni) {
        String n = ni.getName() == null ? "" : ni.getName().toLowerCase();
        String d = ni.getDisplayName() == null ? "" : ni.getDisplayName().toLowerCase();
        for (String pref : VIRTUAL_PREFIXES) {
            if (n.startsWith(pref)) return true;
            if (d.contains(pref))   return true;   // Windows display name
        }
        // Windows: display name sering mengandung kata kunci
        if (d.contains("virtual") || d.contains("hyper-v") || d.contains("tap-")
         || d.contains("pseudo-interface") || d.contains("teredo") || d.contains("isatap")) {
            return true;
        }
        return false;
    }

    private static boolean isLocallyAdministered(byte[] mac) {
        // Bit 0x02 di byte pertama: 0 = globally-unique (NIC fisik),
        // 1 = locally-administered (Docker/VPN/Hyper-V/VirtualBox biasanya begini).
        return (mac[0] & 0x02) != 0;
    }

    /** Prioritaskan interface yang punya IPv4 address (lebih mungkin "online"). */
    private static NetworkInterface preferIPv4(List<NetworkInterface> candidates) {
        for (NetworkInterface ni : candidates) {
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress a = addrs.nextElement();
                if (a instanceof Inet4Address && !a.isLoopbackAddress()) return ni;
            }
        }
        return candidates.get(0);
    }

    private static String formatMac(byte[] mac) {
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < mac.length; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format("%02x", mac[i] & 0xFF));
        }
        return sb.toString();
    }
}
