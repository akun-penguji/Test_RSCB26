package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 * Setting dialog untuk parameter SIP: volume ringtone, auto-answer, interval
 * sync kontak. Menulis balik ke {@code setting/database.xml}.
 */
public class DlgSettingSip extends JDialog {
    private static final String XML_PATH = "setting/database.xml";

    private final JSlider    slideVol    = new JSlider(0, 100, 80);
    private final JCheckBox  chkAuto     = new JCheckBox("Auto-answer panggilan masuk");
    private final JSpinner   spInterval  = new JSpinner(new SpinnerNumberModel(30, 1, 1440, 5));
    private final JTextField txtServer   = new JTextField();
    private final JTextField txtExt      = new JTextField();

    public DlgSettingSip(JDialog parent) {
        super(parent, "Setting Telepon", true);
        setSize(new Dimension(460, 360));
        setLocationRelativeTo(parent);
        buildUI();
        loadValues();
    }

    private void buildUI() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(12, 16, 8, 16));

        form.add(labeled("Server FreePBX:", txtServer, false));
        form.add(Box.createVerticalStrut(6));
        form.add(labeled("Extension (num):", txtExt, false));
        form.add(Box.createVerticalStrut(10));

        JLabel lblVol = new JLabel("Volume ringtone: 80%");
        slideVol.addChangeListener(e -> lblVol.setText("Volume ringtone: " + slideVol.getValue() + "%"));
        form.add(lblVol);
        form.add(slideVol);
        form.add(Box.createVerticalStrut(10));

        form.add(chkAuto);
        form.add(Box.createVerticalStrut(10));

        JPanel intRow = new JPanel(new BorderLayout(8, 0));
        intRow.add(new JLabel("Interval sync kontak (menit):"), BorderLayout.WEST);
        intRow.add(spInterval, BorderLayout.EAST);
        form.add(intRow);
        form.add(Box.createVerticalStrut(4));
        JLabel note = new JLabel("<html><span style='color:#6B7280;font-size:10px;'>"
                + "Perubahan server/extension butuh restart SIMRS agar efektif.</span></html>");
        form.add(note);

        JButton btnKomputer = new JButton("Kelola Komputer SIP");
        btnKomputer.setBackground(new Color(0x2563EB));
        btnKomputer.setForeground(Color.WHITE);
        btnKomputer.setFocusPainted(false);
        btnKomputer.setToolTipText("Atur extension SIP per komputer (keyed by MAC)");
        btnKomputer.addActionListener(e -> new DlgKomputerSip(this).setVisible(true));

        JButton btnSave = new JButton("Simpan");
        btnSave.setBackground(new Color(0x10B981));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> save());

        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JPanel bot = new JPanel(new BorderLayout(8, 0));
        bot.setBorder(new EmptyBorder(8, 16, 12, 16));
        bot.add(btnKomputer, BorderLayout.WEST);
        JPanel right = new JPanel(new GridLayout(1, 2, 6, 0));
        right.add(btnCancel);
        right.add(btnSave);
        bot.add(right, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(bot,  BorderLayout.SOUTH);
    }

    private JPanel labeled(String lbl, JTextField tf, boolean password) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        JLabel l = new JLabel(lbl);
        l.setPreferredSize(new Dimension(150, 26));
        p.add(l, BorderLayout.WEST);
        tf.setPreferredSize(new Dimension(0, 28));
        p.add(tf, BorderLayout.CENTER);
        return p;
    }

    private void loadValues() {
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(XML_PATH)) {
            p.loadFromXML(fis);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal baca database.xml: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtServer.setText(p.getProperty("SIP_SRV_HOST", ""));
        txtExt.setText(p.getProperty("SIP_NUM", ""));
        try { slideVol.setValue(Integer.parseInt(p.getProperty("SIP_RINGTONE_VOLUME", "80"))); }
        catch (NumberFormatException ignored) {}
        String aa = p.getProperty("SIP_AUTO_ANSWER", "no").toLowerCase();
        chkAuto.setSelected(aa.equals("yes") || aa.equals("true") || aa.equals("aktif"));
        try { spInterval.setValue(Integer.parseInt(p.getProperty("SIP_KONTAK_SYNC_INTERVAL", "30"))); }
        catch (NumberFormatException ignored) {}
    }

    private void save() {
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(XML_PATH)) {
            p.loadFromXML(fis);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal baca database.xml: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        p.setProperty("SIP_SRV_HOST", txtServer.getText().trim());
        p.setProperty("SIP_NUM", txtExt.getText().trim());
        p.setProperty("SIP_RINGTONE_VOLUME", String.valueOf(slideVol.getValue()));
        p.setProperty("SIP_AUTO_ANSWER", chkAuto.isSelected() ? "yes" : "no");
        p.setProperty("SIP_KONTAK_SYNC_INTERVAL", String.valueOf((Integer) spInterval.getValue()));
        try (FileOutputStream fos = new FileOutputStream(XML_PATH)) {
            p.storeToXML(fos, "KhanzaHMS");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Setting tersimpan. Beberapa perubahan aktif setelah SIMRS di-restart.",
                "Tersimpan", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
