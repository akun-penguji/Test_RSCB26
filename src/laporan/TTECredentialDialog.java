/*
 * TTECredentialDialog.java
 * Dialog for TTE credential input
 */

package laporan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fungsi.sekuel;

/**
 * Dialog for inputting TTE credentials
 */
public class TTECredentialDialog extends JDialog {
    
    private JTextField txtNik;
    private JPasswordField txtPassphrase;
    private JTextField txtTagCoordinate;
    private boolean confirmed = false;
    
    private String nik;
    private String passphrase;
    private String tagCoordinate;
    
    public TTECredentialDialog(Dialog parent, String nip, int currentSigner, int totalSigners) {
        super(parent, "Input Kredensial TTE - Penanda Tangan " + currentSigner + "/" + totalSigners, true);
        initComponents(nip);
    }
    
    private void initComponents(String nip) {
        setLayout(new BorderLayout(10, 10));
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        
        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel lblInfo = new JLabel("<html><b>Penanda Tangan: " + nip + "</b><br>" +
                                    "Masukkan kredensial untuk proses TTE BSrE</html>");
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // NIK
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("NIK:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNik = new JTextField(20);
        String fetchNik = new sekuel().cariIsi("select no_ktp from pegawai where nik=?", nip);
        txtNik.setText(fetchNik);
        formPanel.add(txtNik, gbc);
        
        // Passphrase
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Passphrase:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPassphrase = new JPasswordField(20);
        formPanel.add(txtPassphrase, gbc);
        
        // Tag Coordinate
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Tag Koordinat:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtTagCoordinate = new JTextField(20);
        String fetchTag = new sekuel().cariIsi("select tag_signer from dokter where kd_dokter=?", nip);
        if (fetchTag.isEmpty() || fetchTag.equals("-")) {
            txtTagCoordinate.setText("0,0,200,100"); // Default value
        } else {
            txtTagCoordinate.setText(fetchTag);
        }
        formPanel.add(txtTagCoordinate, gbc);
        
        // Help text
        gbc.gridx = 1;
        gbc.gridy = 3;
        JLabel lblHelp = new JLabel("<html><i>Format: x,y,width,height (contoh: 0,0,200,100)</i></html>");
        lblHelp.setFont(lblHelp.getFont().deriveFont(10f));
        formPanel.add(lblHelp, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            if (validateInput()) {
                nik = txtNik.getText().trim();
                passphrase = new String(txtPassphrase.getPassword());
                tagCoordinate = txtTagCoordinate.getText().trim();
                confirmed = true;
                dispose();
            }
        });
        
        JButton btnCancel = new JButton("Batal");
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button
        getRootPane().setDefaultButton(btnOk);
        
        // Focus on NIK field
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtNik.requestFocus();
            }
        });
    }
    
    private boolean validateInput() {
        if (txtNik.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIK harus diisi!", 
                "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNik.requestFocus();
            return false;
        }
        
        if (txtPassphrase.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Passphrase harus diisi!", 
                "Validasi", JOptionPane.WARNING_MESSAGE);
            txtPassphrase.requestFocus();
            return false;
        }
        
        if (txtTagCoordinate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tag koordinat harus diisi!", 
                "Validasi", JOptionPane.WARNING_MESSAGE);
            txtTagCoordinate.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getNik() {
        return nik;
    }
    
    public String getPassphrase() {
        return passphrase;
    }
    
    public String getTagCoordinate() {
        return tagCoordinate;
    }
}
