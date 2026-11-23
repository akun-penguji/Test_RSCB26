package sip;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** CRUD sederhana untuk tabel {@code sip_kontak}. */
public final class KontakService {
    private static final Logger LOG = Logger.getLogger(KontakService.class.getName());

    /** Daftar kontak aktif, order favorit desc, nama asc. Optional search keyword. */
    public List<Kontak> list(String keyword) {
        List<Kontak> out = new ArrayList<>();
        String sql = "SELECT extension, nama, unit, lokasi, favorit, aktif, sumber "
                   + "FROM sip_kontak WHERE aktif = 1 ";
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) sql += "AND (extension LIKE ? OR nama LIKE ? OR unit LIKE ?) ";
        sql += "ORDER BY favorit DESC, nama ASC";

        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (hasKeyword) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kontak k = new Kontak();
                    k.extension = rs.getString("extension");
                    k.nama      = rs.getString("nama");
                    k.unit      = rs.getString("unit");
                    k.lokasi    = rs.getString("lokasi");
                    k.favorit   = rs.getInt("favorit") == 1;
                    k.aktif     = rs.getInt("aktif") == 1;
                    k.sumber    = rs.getString("sumber");
                    out.add(k);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "KontakService.list gagal", e);
        }
        return out;
    }

    public Kontak find(String extension) {
        if (extension == null) return null;
        String sql = "SELECT extension, nama, unit, lokasi, favorit, aktif, sumber "
                   + "FROM sip_kontak WHERE extension = ?";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, extension);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Kontak k = new Kontak();
                    k.extension = rs.getString("extension");
                    k.nama      = rs.getString("nama");
                    k.unit      = rs.getString("unit");
                    k.lokasi    = rs.getString("lokasi");
                    k.favorit   = rs.getInt("favorit") == 1;
                    k.aktif     = rs.getInt("aktif") == 1;
                    k.sumber    = rs.getString("sumber");
                    return k;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "KontakService.find gagal", e);
        }
        return null;
    }

    public boolean save(Kontak k) {
        String sql = "INSERT INTO sip_kontak (extension, nama, unit, lokasi, favorit, aktif, sumber, terakhir_sync) "
                   + "VALUES (?,?,?,?,?,?,?,?) "
                   + "ON DUPLICATE KEY UPDATE nama=VALUES(nama), unit=VALUES(unit), "
                   + "lokasi=VALUES(lokasi), favorit=VALUES(favorit), aktif=VALUES(aktif), "
                   + "sumber=VALUES(sumber), terakhir_sync=VALUES(terakhir_sync)";
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, k.extension);
            ps.setString(2, k.nama);
            ps.setString(3, k.unit);
            ps.setString(4, k.lokasi);
            ps.setInt(5, k.favorit ? 1 : 0);
            ps.setInt(6, k.aktif ? 1 : 0);
            ps.setString(7, k.sumber == null ? "manual" : k.sumber);
            ps.setTimestamp(8, k.terakhirSync);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "KontakService.save gagal", e);
            return false;
        }
    }

    public boolean delete(String extension) {
        Connection con = koneksiDB.condb();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM sip_kontak WHERE extension=?")) {
            ps.setString(1, extension);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "KontakService.delete gagal", e);
            return false;
        }
    }
}
