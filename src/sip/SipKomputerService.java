package sip;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** CRUD untuk tabel {@code sip_komputer}. */
public final class SipKomputerService {
    private static final Logger LOG = Logger.getLogger(SipKomputerService.class.getName());

    private static final String COLS =
            "id_komputer, hostname, extension, username, password, display_name, "
          + "auto_answer, ringtone_volume, aktif, keterangan, rc_user_id, terakhir_update";

    /** Cari config untuk komputer ini (berdasar MAC). Null kalau tidak ada. */
    public SipKomputer findLocal() {
        String mac = MachineIdentifier.primaryMacAddress();
        if (mac == null) return null;
        return find(mac);
    }

    public SipKomputer find(String idKomputer) {
        if (idKomputer == null) return null;
        String sql = "SELECT " + COLS + " FROM sip_komputer WHERE id_komputer=?";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idKomputer.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readRow(rs);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "SipKomputerService.find gagal", e);
        }
        return null;
    }

    public List<SipKomputer> list() {
        List<SipKomputer> out = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM sip_komputer ORDER BY aktif DESC, extension ASC";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(readRow(rs));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "SipKomputerService.list gagal", e);
        }
        return out;
    }

    public boolean save(SipKomputer k) {
        String sql = "INSERT INTO sip_komputer "
                + "(id_komputer, hostname, extension, username, password, display_name, "
                + " auto_answer, ringtone_volume, aktif, keterangan, rc_user_id) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE hostname=VALUES(hostname), extension=VALUES(extension), "
                + "username=VALUES(username), password=VALUES(password), display_name=VALUES(display_name), "
                + "auto_answer=VALUES(auto_answer), ringtone_volume=VALUES(ringtone_volume), "
                + "aktif=VALUES(aktif), keterangan=VALUES(keterangan), rc_user_id=VALUES(rc_user_id)";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, k.idKomputer == null ? "" : k.idKomputer.toLowerCase());
            ps.setString(2, k.hostname);
            ps.setString(3, k.extension);
            ps.setString(4, k.username);
            ps.setString(5, k.password);
            ps.setString(6, k.displayName);
            ps.setInt(7, k.autoAnswer ? 1 : 0);
            ps.setInt(8, k.ringtoneVolume);
            ps.setInt(9, k.aktif ? 1 : 0);
            ps.setString(10, k.keterangan);
            ps.setString(11, k.rcUserId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "SipKomputerService.save gagal", e);
            return false;
        }
    }

    /** Update rc_user_id saja (dipanggil setelah RC provision). */
    public boolean updateRcUserId(String idKomputer, String rcUserId) {
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE sip_komputer SET rc_user_id=? WHERE id_komputer=?")) {
            ps.setString(1, rcUserId);
            ps.setString(2, idKomputer.toLowerCase());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "SipKomputerService.updateRcUserId gagal", e);
            return false;
        }
    }

    public boolean delete(String idKomputer) {
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM sip_komputer WHERE id_komputer=?")) {
            ps.setString(1, idKomputer.toLowerCase());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "SipKomputerService.delete gagal", e);
            return false;
        }
    }

    private static SipKomputer readRow(ResultSet rs) throws Exception {
        SipKomputer k = new SipKomputer();
        k.idKomputer     = rs.getString("id_komputer");
        k.hostname       = rs.getString("hostname");
        k.extension      = rs.getString("extension");
        k.username       = rs.getString("username");
        k.password       = rs.getString("password");
        k.displayName    = rs.getString("display_name");
        k.autoAnswer     = rs.getInt("auto_answer") == 1;
        k.ringtoneVolume = rs.getInt("ringtone_volume");
        k.aktif          = rs.getInt("aktif") == 1;
        k.keterangan     = rs.getString("keterangan");
        k.rcUserId       = rs.getString("rc_user_id");
        k.terakhirUpdate = rs.getTimestamp("terakhir_update");
        return k;
    }
}
