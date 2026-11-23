/*
 * Dialog Konfirmasi Kirim dengan Opsi Notifikasi
 * Developed for RSUD Cabang Bungin
 */

package bridging;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dialog;

/**
 *
 * @author Developer SIMRS
 */
public class DialogKonfirmasiKirim extends JDialog {
    private int pilihanKirim = JOptionPane.NO_OPTION;
    private boolean tampilkanNotifikasi = true;
    private JCheckBox chkNotifikasi;
    private JButton btnYa, btnTidak;
    
    public DialogKonfirmasiKirim(Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public DialogKonfirmasiKirim(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Konfirmasi Pengiriman");
        setSize(450, 180);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel utama
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        // Label pertanyaan
        JLabel lblPertanyaan = new JLabel("<html><center>Apakah Anda ingin mengirim laporan ini<br>secara otomatis ke API Satu Sehat?</center></html>");
        lblPertanyaan.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(lblPertanyaan);
        
        // Checkbox notifikasi
        chkNotifikasi = new JCheckBox("Tampilkan notifikasi detail (sukses/error) selama proses pengiriman");
        chkNotifikasi.setSelected(true); // Default checked
        chkNotifikasi.setHorizontalAlignment(JCheckBox.CENTER);
        mainPanel.add(chkNotifikasi);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        btnYa = new JButton("Ya, Kirim Otomatis");
        btnYa.setPreferredSize(new java.awt.Dimension(150, 30));
        btnYa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pilihanKirim = JOptionPane.YES_OPTION;
                tampilkanNotifikasi = chkNotifikasi.isSelected();
                dispose();
            }
        });
        
        btnTidak = new JButton("Tidak, Simpan Saja");
        btnTidak.setPreferredSize(new java.awt.Dimension(150, 30));
        btnTidak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pilihanKirim = JOptionPane.NO_OPTION;
                tampilkanNotifikasi = chkNotifikasi.isSelected();
                dispose();
            }
        });
        
        buttonPanel.add(btnYa);
        buttonPanel.add(btnTidak);
        mainPanel.add(buttonPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        JLabel lblInfo = new JLabel("<html><center><small>💡 Tip: Uncheck notifikasi untuk mode batch (pengiriman massal tanpa popup)</small></center></html>");
        lblInfo.setHorizontalAlignment(JLabel.CENTER);
        infoPanel.add(lblInfo);
        add(infoPanel, BorderLayout.SOUTH);
        
        // Set focus default
        btnYa.requestFocus();
    }
    
    public int getPilihanKirim() {
        return pilihanKirim;
    }
    
    public boolean isTampilkanNotifikasi() {
        return tampilkanNotifikasi;
    }
}