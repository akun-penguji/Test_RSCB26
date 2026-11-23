package sip.chat.rc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * High-level API chat Rocket.Chat: kirim pesan, list conversation, list
 * history, mark-read, unread count. Pakai REST polling dengan interval
 * configurable (dari UI).
 *
 * <p>Attach {@code no_rawat} di-embed di {@code attachments[0].text} supaya
 * konsisten antara SIMRS desktop UI dan RC mobile/web app.
 */
public final class RocketChatService {
    private static final Logger LOG = Logger.getLogger(RocketChatService.class.getName());
    private static final Pattern RFC3339 = Pattern.compile(
            "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})");

    private final RocketChatClient client;

    public RocketChatService(RocketChatClient client) {
        this.client = client;
    }

    // ===== Send =====

    public boolean sendDirect(String toExt, String body, String noRawat) {
        if (!client.isConnected()) {
            LOG.warning("sendDirect: RC client belum connect");
            return false;
        }
        try {
            JSONObject payload = new JSONObject();
            payload.put("channel", "@" + toExt);
            payload.put("text", body == null ? "" : body);
            if (noRawat != null && !noRawat.trim().isEmpty()) {
                JSONObject attach = new JSONObject();
                attach.put("title", "🏥 No. Rawat");
                attach.put("text",  noRawat.trim());
                attach.put("color", "#2563EB");
                payload.put("attachments", new JSONArray().put(attach));
            }
            String resp = RocketChatHttp.userPost(
                    client.getUrl(), "/api/v1/chat.postMessage", payload.toString(),
                    client.getAuthToken(), client.getUserId());
            if (resp == null) return false;
            return new JSONObject(resp).optBoolean("success", false);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "sendDirect gagal", e);
            return false;
        }
    }

    // ===== List conversations =====

    public List<RcConversation> listConversations() {
        List<RcConversation> out = new ArrayList<>();
        if (!client.isConnected()) return out;
        try {
            String resp = RocketChatHttp.userGet(
                    client.getUrl(),
                    "/api/v1/im.list?count=100&sort=%7B%22lm%22%3A-1%7D",
                    client.getAuthToken(), client.getUserId());
            if (resp == null) return out;
            JSONObject o = new JSONObject(resp);
            if (!o.optBoolean("success", false)) return out;
            JSONArray ims = o.optJSONArray("ims");
            if (ims == null) return out;
            String myUsername = client.getUsername();
            for (int i = 0; i < ims.length(); i++) {
                JSONObject r = ims.optJSONObject(i);
                if (r == null) continue;
                RcConversation c = new RcConversation();
                c.roomId  = r.optString("_id", null);
                c.unread  = r.optInt("unread", 0);
                // peer username
                JSONArray usernames = r.optJSONArray("usernames");
                if (usernames != null) {
                    for (int j = 0; j < usernames.length(); j++) {
                        String u = usernames.optString(j);
                        if (!u.equals(myUsername)) { c.peerUsername = u; break; }
                    }
                }
                // last message body + timestamp
                JSONObject last = r.optJSONObject("lastMessage");
                if (last != null) {
                    c.lastBody = last.optString("msg", "");
                    c.lastTgl  = parseTs(last.optString("ts", null));
                }
                if (c.lastTgl == null) c.lastTgl = parseTs(r.optString("lm", null));
                if (c.peerUsername != null && !c.peerUsername.isEmpty()) out.add(c);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "listConversations gagal", e);
        }
        return out;
    }

    // ===== List messages in DM =====

    /** Return null kalau rate-limited / error (supaya UI tidak clear bubble). */
    public List<RcMessage> listMessages(String peerExt, int count) {
        if (!client.isConnected()) return null;
        try {
            String resp = RocketChatHttp.userGet(
                    client.getUrl(),
                    "/api/v1/im.messages?username=" + RocketChatHttp.urlEncode(peerExt)
                    + "&count=" + count,
                    client.getAuthToken(), client.getUserId());
            if (resp == null || !resp.startsWith("{")) return null;
            JSONObject o = new JSONObject(resp);
            if (!o.optBoolean("success", false)) {
                String err = o.optString("error", "?");
                if (err.contains("too-many-requests")) {
                    LOG.fine("im.messages rate-limited, skip");
                } else {
                    LOG.warning("im.messages error: " + err);
                }
                return null;
            }
            List<RcMessage> out = new ArrayList<>();
            JSONArray arr = o.optJSONArray("messages");
            if (arr == null) return out;
            List<RcMessage> tmp = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject m = arr.optJSONObject(i);
                if (m == null) continue;
                RcMessage rm = new RcMessage();
                rm.id       = m.optString("_id", null);
                rm.roomId   = m.optString("rid", null);
                rm.body     = m.optString("msg", "");
                rm.tglKirim = parseTs(m.optString("ts", null));
                JSONObject u = m.optJSONObject("u");
                if (u != null) {
                    rm.fromUsername = u.optString("username", "");
                    rm.fromDisplay  = u.optString("name", "");
                }
                // no_rawat dari attachments[0]
                JSONArray atts = m.optJSONArray("attachments");
                if (atts != null && atts.length() > 0) {
                    JSONObject a0 = atts.optJSONObject(0);
                    if (a0 != null) {
                        String title = a0.optString("title", "");
                        if (title.contains("Rawat")) {
                            rm.noRawat = a0.optString("text", null);
                        }
                    }
                }
                tmp.add(rm);
            }
            // Reverse jadi ascending (RC return terbaru-dulu, UI butuh lama→baru)
            for (int i = tmp.size() - 1; i >= 0; i--) out.add(tmp.get(i));
            return out;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "listMessages gagal", e);
            return null;
        }
    }

    // ===== Mark read =====

    public boolean markRead(String roomId) {
        if (!client.isConnected() || roomId == null) return false;
        try {
            JSONObject p = new JSONObject();
            p.put("rid", roomId);
            String resp = RocketChatHttp.userPost(client.getUrl(),
                    "/api/v1/subscriptions.read", p.toString(),
                    client.getAuthToken(), client.getUserId());
            return resp != null && new JSONObject(resp).optBoolean("success", false);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "markRead gagal", e);
            return false;
        }
    }

    // ===== Unread total =====

    public int unreadTotal() {
        if (!client.isConnected()) return 0;
        try {
            // Endpoint yang benar di RC 6+: subscriptions.get (bukan getAll)
            String resp = RocketChatHttp.userGet(client.getUrl(),
                    "/api/v1/subscriptions.get",
                    client.getAuthToken(), client.getUserId());
            if (resp == null || !resp.startsWith("{")) return 0;
            JSONObject o = new JSONObject(resp);
            // subscriptions.get balik {"update":[...],"remove":[]} tanpa "success" field
            JSONArray arr = o.optJSONArray("update");
            if (arr == null) return 0;
            int sum = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject s = arr.optJSONObject(i);
                if (s == null) continue;
                // "t":"d" = direct message
                if ("d".equals(s.optString("t", "")))  sum += s.optInt("unread", 0);
            }
            return sum;
        } catch (Exception e) {
            LOG.log(Level.FINE, "unreadTotal gagal (bisa rate-limit / token expire)", e);
            return 0;
        }
    }

    // ===== Helpers =====

    private static Timestamp parseTs(String rfc3339) {
        if (rfc3339 == null || rfc3339.isEmpty()) return null;
        Matcher m = RFC3339.matcher(rfc3339);
        if (!m.find()) return null;
        try {
            java.util.Calendar c = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
            c.set(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)) - 1,
                  Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)),
                  Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));
            c.set(java.util.Calendar.MILLISECOND, 0);
            return new Timestamp(c.getTimeInMillis());
        } catch (Exception e) { return null; }
    }
}
