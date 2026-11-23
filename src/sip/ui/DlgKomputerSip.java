package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import sip.MachineIdentifier;
import sip.SipKomputer;
import sip.SipKomputerService;

/** Daftar semua komputer SIP yang terdaftar + toolbar kelola. */
public class DlgKomputerSip extends JDialog {
    private final SipKomputerService service = new SipKomputerService();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"MAC Address","Hostname","Ext","Display Name","Aktif","Terakhir Update","Keterangan"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblLocal = new JLabel();

    public DlgKomputerSip(java.awt.Window parent) {
        super(parent, "Kelola Komputer SIP", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setSize(new Dimension(820, 500));
        setLocationRelativeTo(parent);
        buildUI();
        reload();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBorder(new EmptyBorder(10, 12, 6, 12));
        JLabel title = new JLabel("Komputer SIP — dikelola di DB (tidak terpengaruh sync Nextcloud)");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 12f));
        top.add(title, BorderLayout.WEST);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        JButton btnAdd = new JButton("+ Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDel = new JButton("Hapus");
        JButton btnRegisterThis = new JButton("Daftarkan Komputer Ini");
        btnRegisterThis.setBackground(new Color(0x2563EB));
        btnRegisterThis.setForeground(Color.WHITE);
        btnRegisterThis.setFocusPainted(false);
        btnAdd.addActionListener(e -> edit(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDel.addActionListener(e -> deleteSelected());
        btnRegisterThis.addActionListener(e -> registerThisMachine());
        tools.add(btnRegisterThis);
        tools.add(btnAdd);
        tools.add(btnEdit);
        tools.add(btnDel);
        top.add(tools, BorderLayout.EAST);

        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setMaxWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);
        table.getColumnModel().getColumn(4).setMaxWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        String mac = MachineIdentifier.primaryMacAddress();
        String host = MachineIdentifier.hostname();
        lblLocal.setText("Komputer ini: " + (mac == null ? "(MAC tidak terdeteksi)" : mac) + "  ·  " + host);
        lblLocal.setForeground(new Color(0x6B7280));
        lblLocal.setBorder(new EmptyBorder(6, 12, 6, 12));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        getContentPane().add(lblLocal, BorderLayout.SOUTH);
    }

    private void reload() {
        List<SipKomputer> list = service.list();
        model.setRowCount(0);
        String localMac = MachineIdentifier.primaryMacAddress();
        for (SipKomputer k : list) {
            String mac = k.idKomputer + (k.idKomputer != null && k.idKomputer.equals(localMac) ? "  (ini)" : "");
            model.addRow(new Object[]{
                    mac,
                    k.hostname == null ? "" : k.hostname,
                    k.extension,
                    k.displayName == null ? "" : k.displayName,
                    k.aktif ? "✓" : "—",
                    k.terakhirUpdate == null ? "" : k.terakhirUpdate.toString(),
                    k.keterangan == null ? "" : k.keterangan
            });
        }
    }

    private SipKomputer getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String mac = String.valueOf(model.getValueAt(row, 0)).replace("  (ini)", "").trim();
        return service.find(mac);
    }

    private void edit(SipKomputer k) {
        DlgEditKomputer d = new DlgEditKomputer(this, service, k);
        d.setVisible(true);
        if (d.isSaved()) reload();
    }

    private void editSelected() {
        SipKomputer k = getSelected();
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Pilih dulu satu baris.",
                    "Tidak ada pilihan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        edit(k);
    }

    private void deleteSelected() {
        SipKomputer k = getSelected();
        if (k == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "Hapus komputer " + k.idKomputer + " (ext " + k.extension + ")?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION && service.delete(k.idKomputer)) reload();
    }

    private void registerThisMachine() {
        String mac = MachineIdentifier.primaryMacAddress();
        if (mac == null) {
            JOptionPane.showMessageDialog(this,
                    "MAC address tidak terdeteksi. Pastikan ada network interface aktif.",
                    "Tidak bisa mendeteksi MAC", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SipKomputer existing = service.find(mac);
        if (existing != null) {
            edit(existing);
        } else {
            DlgEditKomputer d = DlgEditKomputer.untukKomputerIni(this, service);
            d.setVisible(true);
            if (d.isSaved()) reload();
        }
    }
}
