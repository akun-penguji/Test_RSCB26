package sip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/** Player loop sederhana untuk ringtone/dialing/busy. Ambil file dari folder {@code media/}. */
public final class SoundPlayer {
    private static final Logger LOG = Logger.getLogger(SoundPlayer.class.getName());

    public static final String RINGING = "ringing.au";
    public static final String BUSY    = "busy.au";
    public static final String DIALING = "dialing.au";

    private static final String MEDIA_DIR = "media" + File.separator;

    private volatile Clip clip;

    public synchronized void play(String filename) {
        stop();
        try {
            FileInputStream fis = new FileInputStream(MEDIA_DIR + filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            Clip c = AudioSystem.getClip();
            c.open(ais);
            c.loop(Clip.LOOP_CONTINUOUSLY);
            c.start();
            this.clip = c;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Gagal play " + filename, e);
        }
    }

    public synchronized void stop() {
        if (clip != null) {
            try { clip.stop(); } catch (Exception ignored) {}
            try { clip.close(); } catch (Exception ignored) {}
            clip = null;
        }
    }
}
