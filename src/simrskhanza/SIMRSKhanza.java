/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simrskhanza;

import usu.widget.util.WidgetUtilities;

/**
 *
 * @author khanzasoft
 */
public class SIMRSKhanza {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Buang cache data master yang tanda tangannya tidak sah sebelum
        // antarmuka tampil. Cache hanya salinan basis data, jadi membuangnya
        // aman — dan kalau isinya sudah terlanjur diubah, ini membuangnya
        // alih-alih mengesahkannya. Lihat fungsi.CacheAman.
        fungsi.CacheAman.siapkanSaatMulai();

        WidgetUtilities.invokeLater(() -> {
           frmUtama utama=frmUtama.getInstance();
           utama.setVisible(true);
        });
    }
    
}
