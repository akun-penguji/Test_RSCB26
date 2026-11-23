/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Owner
 */
public class WarnaTableTTE extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        String stts = "";
        try {
            // Kolom Status TTE biasanya di index 7 atau 6
            for (int col : new int[]{7, 6, 5}) {
                if (col < table.getColumnCount() && table.getValueAt(row, col) != null) {
                    String valStr = table.getValueAt(row, col).toString().trim().toUpperCase();
                    if (valStr.contains("AKTIF") || valStr.contains("TERDAFTAR") || valStr.contains("BELUM") || valStr.contains("KEDALUWARSA") || valStr.contains("TERBLOKIR")) {
                        stts = valStr;
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        if (isSelected) {
            component.setBackground(new Color(200, 230, 201));
            component.setForeground(Color.BLACK);
        } else {
            if (stts.contains("AKTIF") && !stts.contains("TIDAK")) {
                component.setBackground(row % 2 == 1 ? new Color(232, 250, 232) : new Color(244, 255, 244));
                component.setForeground(new Color(20, 100, 20));
            } else if (stts.contains("BELUM TERDAFTAR") || stts.contains("TIDAK")) {
                component.setBackground(row % 2 == 1 ? new Color(255, 238, 238) : new Color(255, 248, 248));
                component.setForeground(new Color(150, 30, 30));
            } else if (stts.contains("KEDALUWARSA") || stts.contains("EXPIRED")) {
                component.setBackground(row % 2 == 1 ? new Color(255, 248, 220) : new Color(255, 253, 235));
                component.setForeground(new Color(160, 100, 0));
            } else if (stts.contains("TERBLOKIR") || stts.contains("DICABUT")) {
                component.setBackground(row % 2 == 1 ? new Color(255, 225, 225) : new Color(255, 235, 235));
                component.setForeground(new Color(180, 0, 0));
            } else {
                component.setBackground(row % 2 == 1 ? new Color(248, 250, 252) : Color.WHITE);
                component.setForeground(new Color(50, 50, 50));
            }
        }
        
        return component;
    }
}
