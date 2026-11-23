package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class WarnaTableKamarInap extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Warna default berdasarkan baris ganjil/genap
        if (row % 2 == 1){
            component.setBackground(new Color(255, 255, 255)); // Warna merah muda pucat untuk baris ganjil
            component.setForeground(new Color(50, 50, 50));   // Warna teks abu-abu gelap
        } else {
            component.setBackground(new Color(245, 245, 245)); // Warna putih untuk baris genap
            component.setForeground(new Color(50, 50, 50));   // Warna teks abu-abu gelap
        }
        
        // Pengecekan jumlah kolom agar tidak terjadi out of bounds exception
        if (table.getColumnCount() > 20) {
            Object status = table.getValueAt(row, 20); // Kolom ke-21 (index 20)
            
            if (status != null && "Sudah Bayar".equals(status.toString())) {
                component.setBackground(new Color(50, 50, 50)); // Warna latar abu-abu gelap
                component.setForeground(new Color(255, 255, 255)); // Warna teks putih
            }
        }

        // Penanganan jika baris sedang dipilih
        if (isSelected) {
            component.setBackground(new Color(116, 185, 255)); // Warna biru untuk baris yang dipilih
            component.setForeground(Color.WHITE);           // Warna teks putih agar kontras
        }
        
        // mengatur aligment teks di isi tabel
        if (column == 0 || column == 1 || column == 5 || column == 6 || column == 11 || column == 12 || 
            column == 13 || column == 14) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else if (column == 8 || column == 15){
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        return component;
    }
}