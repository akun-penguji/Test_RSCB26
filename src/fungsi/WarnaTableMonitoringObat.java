/*
 * Custom Table Cell Renderer for Monitoring Pemberian Obat
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * WarnaTableMonitoringObat - Custom renderer for monitoring obat table
 * Applies color coding based on validation status and pemberian status
 * 
 * Color scheme:
 * - Yellow (255,255,200): Belum Divalidasi
 * - Orange (255,200,150): Belum Lengkap (partially given)
 * - Green (200,255,200): Sudah Divalidasi
 * - Pink (255,200,255): Jam sudah diberikan (for individual time slots)
 * 
 * @author RSUD Cabang Bungin
 */
public class WarnaTableMonitoringObat extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component component = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
        
        // Don't override selection color
        if (isSelected) {
            return component;
        }
        
        // Get status validasi from column 23
        String statusValidasi = "";
        if (table.getRowCount() > row && table.getColumnCount() > 23) {
            Object statusObj = table.getValueAt(row, 23);
            statusValidasi = statusObj != null ? statusObj.toString() : "";
        }
        
        // Apply row color based on validation status
        Color rowColor = Color.WHITE;
        if ("Belum".equals(statusValidasi)) {
            rowColor = new Color(255, 255, 200); // Yellow - Belum Divalidasi
        } else if ("Belum Lengkap".equals(statusValidasi)) {
            rowColor = new Color(255, 200, 150); // Orange - Belum Lengkap
        } else if ("Sudah".equals(statusValidasi)) {
            rowColor = new Color(200, 255, 200); // Green - Sudah Divalidasi
        }
        
        // For columns 6-13 (PEMBERIAN 1-8), check individual status
        if (column >= 6 && column <= 13) {
            int jamIndex = column - 6; // 0-7
            int statusColumnIndex = 26 + jamIndex; // status_jam1 is at column 26
            
            if (table.getColumnCount() > statusColumnIndex) {
                Object statusJamObj = table.getValueAt(row, statusColumnIndex);
                String statusJam = statusJamObj != null ? statusJamObj.toString() : "belum";
                
                if ("sudah".equals(statusJam)) {
                    component.setBackground(new Color(255, 200, 255)); // Pink - Sudah diberikan
                } else {
                    component.setBackground(rowColor);
                }
            } else {
                component.setBackground(rowColor);
            }
        } else {
            // Other columns use the row color
            component.setBackground(rowColor);
        }
        
        // Set text color
        component.setForeground(new Color(50, 50, 50));
        
        return component;
    }
}
