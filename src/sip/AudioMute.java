package sip;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

/**
 * Kontrol mute mikrofon & speaker via javax.sound. Cross-platform best-effort —
 * tidak semua OS/driver expose MUTE control; jika gagal, return false.
 */
public final class AudioMute {
    private static final Logger LOG = Logger.getLogger(AudioMute.class.getName());

    private AudioMute() {}

    /** Mute/unmute input mikrofon. */
    public static boolean setMicMute(boolean mute) {
        boolean any = setPortMute(Port.Info.MICROPHONE, mute);
        any |= setPortMute(Port.Info.LINE_IN, mute);
        any |= setTargetLinesMute(mute);
        return any;
    }

    /** Mute/unmute output speaker. Dipakai untuk hold lokal. */
    public static boolean setSpeakerMute(boolean mute) {
        boolean any = setPortMute(Port.Info.SPEAKER, mute);
        any |= setPortMute(Port.Info.HEADPHONE, mute);
        any |= setPortMute(Port.Info.LINE_OUT, mute);
        any |= setSourceLinesMute(mute);
        return any;
    }

    private static boolean setPortMute(Port.Info info, boolean mute) {
        if (!AudioSystem.isLineSupported(info)) return false;
        try {
            Port port = (Port) AudioSystem.getLine(info);
            port.open();
            try {
                if (port.isControlSupported(BooleanControl.Type.MUTE)) {
                    ((BooleanControl) port.getControl(BooleanControl.Type.MUTE)).setValue(mute);
                    return true;
                }
            } finally { port.close(); }
        } catch (Exception e) {
            LOG.log(Level.FINE, "setPortMute " + info + " gagal", e);
        }
        return false;
    }

    private static boolean setTargetLinesMute(boolean mute) {
        boolean any = false;
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            try {
                Mixer m = AudioSystem.getMixer(mi);
                for (Line.Info li : m.getTargetLineInfo()) {
                    try {
                        Line line = m.getLine(li);
                        if (line.isControlSupported(BooleanControl.Type.MUTE)) {
                            ((BooleanControl) line.getControl(BooleanControl.Type.MUTE)).setValue(mute);
                            any = true;
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
        return any;
    }

    private static boolean setSourceLinesMute(boolean mute) {
        boolean any = false;
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            try {
                Mixer m = AudioSystem.getMixer(mi);
                for (Line.Info li : m.getSourceLineInfo()) {
                    try {
                        Line line = m.getLine(li);
                        if (line.isControlSupported(BooleanControl.Type.MUTE)) {
                            ((BooleanControl) line.getControl(BooleanControl.Type.MUTE)).setValue(mute);
                            any = true;
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
        return any;
    }
}
