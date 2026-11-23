package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import sip.MachineIdentifier;
import sip.SipKomputer;
import sip.SipKomputerService;

/** Form tambah/edit row {@code sip_komputer}. */
public class DlgEditKomputer extends JDialog {
    private final SipKomputerService service;
    private final SipKomputer        row;
    private final boolean            isNew;

    private final JTextField txtId     = new JTextField();
    private final JTextField txtHost   = new JTextField();
    private final JTextField txtExt    = new JTextField();
    private final JTextField txtUser   = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JCheckBox  chkShow   = new JCheckBox("Tampilkan password");
    private final JTextField txtDisp   = new JTextField();
    private final JCheckBox  chkAuto   = new JCheckBox("Auto-answer panggilan masuk");
    private final JSlider    slideVol  = new JSlider(0, 100, 80);
    private final JLabel     lblVol    = new JLabel("Volume ringtone: 80%");
    private final JCheckBox  chkAktif  = new JCheckBox("Aktif");
    private final JTextField txtKet    = new JTextField();
    private boolean saved = false;

    public DlgEditKomputer(JDialog parent, SipKomputerService service, SipKomputer existing) {
        super(parent, existing == null ? "Daftarkan Komputer SIP" : "Edit Komputer SIP", true);
        this.service = service;
        this.isNew = existing == null;
        this.row = existing != null ? existing : new SipKomputer();
        setSize(new Dimension(480, 500));
        setLocationRelativeTo(parent);
        buildUI();
        loadValues();
    }

    /** Pre-fill form dengan MAC & hostname dari komputer ini. */
    public static DlgEditKomputer untukKomputerIni(JDialog parent, SipKomputerService service) {
        SipKomputer k = new SipKomputer();
        k.idKomputer = MachineIdentifier.primaryMacAddress();
        k.hostname   = MachineIdentifier.hostname();
        k.aktif      = true;
        k.ringtoneVolume = 80;
        return new DlgEditKomputer(parent, service, k);
    }

    private void buildUI() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(12, 16, 8, 16));

        // Baris MAC Address dengan tombol Deteksi di kanan textbox
        JButton btnDetectMac = new JButton("Deteksi");
        btnDetectMac.setToolTipText("Auto-detect MAC address & hostname komputer ini");
        btnDetectMac.setPreferredSize(new Dimension(80, 28));
        btnDetectMac.setFocusPainted(false);
        btnDetectMac.setEnabled(isNew); // saat edit, MAC = primary key, tidak boleh diubah
        btnDetectMac.addActionListener(e -> autoDetectMac());

        JPanel macRow = new JPanel(new BorderLayout(10, 0));
        JLabel macLabel = new JLabel("MAC Address:");
        macLabel.setPreferredSize(new Dimension(130, 26));
        macRow.add(macLabel, BorderLayout.WEST);
        JPanel macField = new JPanel(new BorderLayout(6, 0));
        txtId.setPreferredSize(new Dimension(0, 28));
        txtId.setToolTipText("Primary key, tidak boleh ubah sembarangan");
        macField.add(txtId, BorderLayout.CENTER);
        macField.add(btnDetectMac, BorderLayout.EAST);
        macRow.add(macField, BorderLayout.CENTER);
        form.add(macRow);

        form.add(Box.createVerticalStrut(6));
        form.add(row("Hostname:", txtHost, null));
        form.add(Box.createVerticalStrut(10));
        form.add(row("Extension (ext):", txtExt, null));
        form.add(Box.createVerticalStrut(6));
        form.add(row("Username:", txtUser, "Biasanya sama dengan extension"));
        form.add(Box.createVerticalStrut(6));
        form.add(row("Password:", txtPass, null));
        form.add(chkShow);
        chkShow.addActionListener(e ->
            txtPass.setEchoChar(chkShow.isSelected() ? (char)0 : '•'));
        form.add(Box.createVerticalStrut(6));
        form.add(row("Display Name:", txtDisp, "Nama yang tampil di caller ID"));

        form.add(Box.createVerticalStrut(10));
        slideVol.addChangeListener(e ->
            lblVol.setText("Volume ringtone: " + slideVol.getValue() + "%"));
        form.add(lblVol);
        form.add(slideVol);
        form.add(Box.createVerticalStrut(6));
        form.add(chkAuto);
        form.add(Box.createVerticalStrut(6));
        form.add(chkAktif);
        form.add(Box.createVerticalStrut(10));
        form.add(row("Keterangan:", txtKet, null));

        if (!isNew) txtId.setEditable(false);

        JButton btnSave = new JButton("Simpan");
        btnSave.setBackground(new Color(0x10B981));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> save());

        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnDel = new JButton("Hapus");
        btnDel.setBackground(new Color(0xDC2626));
        btnDel.setForeground(Color.WHITE);
        btnDel.setFocusPainted(false);
        btnDel.setVisible(!isNew);
        btnDel.addActionListener(e -> delete());

        JPanel bot = new JPanel(new BorderLayout(8, 0));
        bot.setBorder(new EmptyBorder(8, 16, 12, 16));
        bot.add(btnDel, BorderLayout.WEST);
        JPanel right = new JPanel(new GridLayout(1, 2, 6, 0));
        right.add(btnCancel);
        right.add(btnSave);
        bot.add(right, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(bot,  BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnSave);
    }

    private JPanel row(String label, javax.swing.JComponent field, String tooltip) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(130, 26));
        p.add(l, BorderLayout.WEST);
        field.setPreferredSize(new Dimension(0, 28));
        if (tooltip != null) field.setToolTipText(tooltip);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void autoDetectMac() {
        String mac = MachineIdentifier.primaryMacAddress();
        if (mac == null || mac.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Gagal deteksi MAC address dari komputer ini.",
                    "Auto-detect", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtId.setText(mac);
        if (txtHost.getText().trim().isEmpty()) {
            String hn = MachineIdentifier.hostname();
            if (hn != null) txtHost.setText(hn);
        }
    }

    private void loadValues() {
        txtId.setText(row.idKomputer == null ? "" : row.idKomputer);
        txtHost.setText(row.hostname == null ? "" : row.hostname);
        txtExt.setText(row.extension == null ? "" : row.extension);
        txtUser.setText(row.username == null ? "" : row.username);
        txtPass.setText(row.password == null ? "" : row.password);
        txtPass.setEchoChar('•');
        txtDisp.setText(row.displayName == null ? "" : row.displayName);
        chkAuto.setSelected(row.autoAnswer);
        slideVol.setValue(row.ringtoneVolume > 0 ? row.ringtoneVolume : 80);
        lblVol.setText("Volume ringtone: " + slideVol.getValue() + "%");
        chkAktif.setSelected(row.aktif);
        txtKet.setText(row.keterangan == null ? "" : row.keterangan);
    }

    private void save() {
        String id  = txtId.getText().trim().toLowerCase();
        String ext = txtExt.getText().trim();
        String usr = txtUser.getText().trim();
        String pwd = new String(txtPass.getPassword());
        if (id.isEmpty() || ext.isEmpty() || usr.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "MAC Address, Extension, Username, dan Password wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        row.idKomputer     = id;
        row.hostname       = txtHost.getText().trim();
        row.extension      = ext;
        row.username       = usr;
        row.password       = pwd;
        row.displayName    = txtDisp.getText().trim();
        row.autoAnswer     = chkAuto.isSelected();
        row.ringtoneVolume = slideVol.getValue();
        row.aktif          = chkAktif.isSelected();
        row.keterangan     = txtKet.getText().trim();

        if (!service.save(row)) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Auto-provision Rocket.Chat user (idempotent: create kalau belum ada, update kalau sudah)
        try {
            java.util.Properties prop = new java.util.Properties();
            try (java.io.FileInputStream fis = new java.io.FileInputStream("setting/database.xml")) {
                prop.loadFromXML(fis);
            }
            sip.SipConfig cfg = sip.SipConfig.load(prop, row);
            if (cfg.isRcEnabled()) {
                sip.chat.rc.RocketChatAdmin rc = new sip.chat.rc.RocketChatAdmin(cfg);
                String rcUserId = rc.ensureUser(row.extension, row.displayName);
                if (rcUserId != null) {
                    service.updateRcUserId(row.idKomputer, rcUserId);
                    row.rcUserId = rcUserId;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Komputer tersimpan, tapi provisioning Rocket.Chat GAGAL.\n"
                          + "Cek RC_URL/RC_ADMIN_TOKEN di database.xml atau server RC down.\n"
                          + "User bisa manual create via DlgKomputerSip (tombol Retry nanti).",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            // Non-fatal — komputer tersimpan, RC provisioning bisa di-retry
            System.out.println("RC provisioning error: " + ex);
        }
        saved = true;
        dispose();
    }

    private void delete() {
        int r = JOptionPane.showConfirmDialog(this,
                "Hapus komputer " + row.idKomputer + "?\nKonfigurasi SIP di komputer itu akan nonaktif.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        if (service.delete(row.idKomputer)) {
            saved = true;
            dispose();
        }
    }

    public boolean isSaved() { return saved; }
}
