package sip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class IconStatusSip extends JLabel implements SipClientListener {

    public IconStatusSip() {
        super();
        setFont(new Font("Tahoma", 0, 11));
        setHorizontalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setName("IconStatusSip");
        setPreferredSize(new Dimension(140, 23));
        render(SipState.OFFLINE, null);
    }

    @Override
    public void onStateChanged(SipState newState, String message) {
        SwingUtilities.invokeLater(() -> render(newState, message));
    }

    private void render(SipState state, String message) {
        String dot;
        String label;
        Color fg;
        switch (state) {
            case REGISTERED:
                dot   = "●";
                label = "SIP Online";
                fg    = new Color(30, 140, 30);
                break;
            case CONNECTING:
                dot   = "●";
                label = "SIP Connecting";
                fg    = new Color(200, 150, 0);
                break;
            case FAILED:
                dot   = "●";
                label = "SIP Error";
                fg    = new Color(200, 30, 30);
                break;
            case DISABLED:
                dot   = "○";
                label = "SIP Off";
                fg    = new Color(120, 120, 120);
                break;
            case STOPPING:
                dot   = "○";
                label = "SIP Stopping";
                fg    = new Color(120, 120, 120);
                break;
            case OFFLINE:
            default:
                dot   = "○";
                label = "SIP Offline";
                fg    = new Color(120, 120, 120);
                break;
        }
        setText(dot + " " + label);
        setForeground(fg);
        if (message != null && !message.isEmpty()) setToolTipText(message);
    }
}
