package sip;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Insert + query ringan untuk tabel {@code sip_call_log}. */
public final class CallLogService {
    private static final Logger LOG = Logger.getLogger(CallLogService.class.getName());

    public boolean insert(CallLogEntry e) {
        String sql = "INSERT INTO sip_call_log "
                + "(extension_lokal, extension_remote, nama_remote, arah, status, "
                + " tgl_mulai, tgl_jawab, tgl_selesai, durasi_detik) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.extensionLokal);
            ps.setString(2, e.extensionRemote);
            ps.setString(3, e.namaRemote);
            ps.setString(4, e.arah);
            ps.setString(5, e.status);
            ps.setTimestamp(6, e.tglMulai);
            ps.setTimestamp(7, e.tglJawab);
            ps.setTimestamp(8, e.tglSelesai);
            ps.setInt(9, e.durasiDetik);
            ps.executeUpdate();
            return true;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "CallLogService.insert gagal", ex);
            return false;
        }
    }

    public List<CallLogEntry> recent(String extensionLokal, int limit) {
        List<CallLogEntry> out = new ArrayList<>();
        String sql = "SELECT id, extension_lokal, extension_remote, nama_remote, arah, status, "
                + "tgl_mulai, tgl_jawab, tgl_selesai, durasi_detik "
                + "FROM sip_call_log WHERE extension_lokal=? ORDER BY tgl_mulai DESC LIMIT ?";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, extensionLokal);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CallLogEntry e = new CallLogEntry();
                    e.id              = rs.getLong("id");
                    e.extensionLokal  = rs.getString("extension_lokal");
                    e.extensionRemote = rs.getString("extension_remote");
                    e.namaRemote      = rs.getString("nama_remote");
                    e.arah            = rs.getString("arah");
                    e.status          = rs.getString("status");
                    e.tglMulai        = rs.getTimestamp("tgl_mulai");
                    e.tglJawab        = rs.getTimestamp("tgl_jawab");
                    e.tglSelesai      = rs.getTimestamp("tgl_selesai");
                    e.durasiDetik     = rs.getInt("durasi_detik");
                    out.add(e);
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "CallLogService.recent gagal", ex);
        }
        return out;
    }
}
