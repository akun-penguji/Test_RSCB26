/*
 * Custom Table Cell Renderer for merging cells with duplicate values
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * MergeCellRenderer - Merges adjacent cells with the same value
 * Used for grouping rows by common values (e.g., patient data)
 * 
 * @author RSUD Cabang Bungin
 */
public class MergeCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component component = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
        
        // Apply alternating row colors
        if (!isSelected) {
            if (row % 2 == 1) {
                component.setBackground(new Color(255, 246, 244));
            } else {
                component.setBackground(new Color(255, 255, 255));
            }
        }
        
        // Check if this cell should be "merged" (hidden) with the cell above
        if (row > 0) {
            Object currentValue = table.getValueAt(row, column);
            Object previousValue = table.getValueAt(row - 1, column);
            
            // If the value is the same as the row above, hide the border
            if (currentValue != null && currentValue.equals(previousValue)) {
                // Make the text invisible for merged cells (except the first occurrence)
                setForeground(component.getBackground());
            } else {
                // Normal text color
                if (!isSelected) {
                    setForeground(new Color(70, 70, 70));
                }
            }
        } else {
            // First row always shows text
            if (!isSelected) {
                setForeground(new Color(70, 70, 70));
            }
        }
        
        return component;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
