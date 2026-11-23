package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import sip.Kontak;
import sip.KontakService;

/** Form tambah/edit kontak manual. */
public class DlgEditKontak extends JDialog {
    private final KontakService service;
    private final Kontak kontak;
    private final boolean isNew;

    private final JTextField txtExt     = new JTextField();
    private final JTextField txtNama    = new JTextField();
    private final JTextField txtUnit    = new JTextField();
    private final JTextField txtLokasi  = new JTextField();
    private final JCheckBox  chkFavorit = new JCheckBox("Favorit");
    private boolean saved = false;

    public DlgEditKontak(JDialog parent, KontakService service, Kontak existing) {
        super(parent, existing == null ? "Tambah Kontak" : "Edit Kontak", true);
        this.service = service;
        this.isNew = existing == null;
        this.kontak = existing != null ? existing : new Kontak();

        setSize(new Dimension(380, 300));
        setLocationRelativeTo(parent);
        buildUI();
        loadValues();
    }

    private void buildUI() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(12, 16, 8, 16));

        form.add(row("Extension:", txtExt));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Nama:", txtNama));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Unit:", txtUnit));
        form.add(Box.createVerticalStrut(8));
        form.add(row("Lokasi:", txtLokasi));
        form.add(Box.createVerticalStrut(8));
        form.add(chkFavorit);

        if (!isNew) txtExt.setEditable(false);

        JButton btnSave  = new JButton("Simpan");
        btnSave.setBackground(new Color(0x10B981));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> save());

        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> dispose());

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBackground(new Color(0xDC2626));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setVisible(!isNew);
        btnDelete.addActionListener(e -> delete());

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setBorder(new EmptyBorder(8, 16, 12, 16));
        bottom.add(btnDelete, BorderLayout.WEST);

        JPanel right = new JPanel(new GridLayout(1, 2, 6, 0));
        right.add(btnCancel);
        right.add(btnSave);
        bottom.add(right, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnSave);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });
    }

    private JPanel row(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(80, 26));
        p.add(l, BorderLayout.WEST);
        field.setPreferredSize(new Dimension(0, 28));
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void loadValues() {
        txtExt.setText(kontak.extension == null ? "" : kontak.extension);
        txtNama.setText(kontak.nama == null ? "" : kontak.nama);
        txtUnit.setText(kontak.unit == null ? "" : kontak.unit);
        txtLokasi.setText(kontak.lokasi == null ? "" : kontak.lokasi);
        chkFavorit.setSelected(kontak.favorit);
    }

    private void save() {
        String ext = txtExt.getText().trim();
        String nama = txtNama.getText().trim();
        if (ext.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Extension dan Nama wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        kontak.extension = ext;
        kontak.nama      = nama;
        kontak.unit      = txtUnit.getText().trim();
        kontak.lokasi    = txtLokasi.getText().trim();
        kontak.favorit   = chkFavorit.isSelected();
        kontak.aktif     = true;
        if (kontak.sumber == null || kontak.sumber.isEmpty()) kontak.sumber = "manual";
        if (service.save(kontak)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan ke database.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        int r = JOptionPane.showConfirmDialog(this,
                "Hapus kontak " + kontak.extension + " (" + kontak.nama + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        if (service.delete(kontak.extension)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
