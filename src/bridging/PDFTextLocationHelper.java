package bridging;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper to find coordinates of specific text tags in a PDF file.
 * Used for TTE BSrE V2 to determine signature position.
 */
public class PDFTextLocationHelper extends PDFTextStripper {
    private String searchText;
    private List<TextLocation> locations = new ArrayList<>();
    private int currentPage = 0;
    private boolean caseInsensitive = true;

    public PDFTextLocationHelper() throws IOException {
        super();
    }

    public static class TextLocation {
        public int page;
        public float x;
        public float y;
        public float width;
        public float height;
        public float pageWidth;
        public float pageHeight;

        @Override
        public String toString() {
            return "Page " + page + " (x=" + x + ", y=" + y + ", w=" + pageWidth + ", h=" + pageHeight + ")";
        }
    }

    /**
     * Finds the first occurrence of the text in the PDF.
     * @return TextLocation or null if not found
     */
    public TextLocation findText(File pdfFile, String text) throws IOException {
        this.searchText = text;
        this.locations.clear();
        
        System.out.println("Mencari tag: " + text + " di file: " + pdfFile.getName());
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                this.currentPage = i;
                this.setStartPage(i);
                this.setEndPage(i);
                this.getText(document);
                if (!locations.isEmpty()) {
                    System.out.println("Tag '" + text + "' ditemukan di halaman " + i + " pada " + locations.get(0));
                    return locations.get(0); // Return first match
                }
            }
        }
        System.out.println("Tag '" + text + "' TIDAK ditemukan.");
        return null;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        if (searchText == null || string == null || textPositions.isEmpty()) return;
        
        String processedString = caseInsensitive ? string.toLowerCase() : string;
        String processedSearch = caseInsensitive ? searchText.toLowerCase() : searchText;
        
        // Debug: Show what we are looking at
        System.out.println("Processing chunk: '" + string + "' (length: " + string.length() + ")");
        
        // Escape search text for regex (it might contain chars like <, [, {)
        String regex = "";
        for (char c : processedSearch.toCharArray()) {
            String schar = String.valueOf(c);
            if ("<[{()^$|*+?.".contains(schar)) schar = "\\" + schar;
            regex += schar + "\\s*"; // Allow optional spaces between characters
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(processedString);
        
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            System.out.println("Match found at [" + start + "," + end + "] for regex: " + regex);
            
            // Map indexes back to textPositions
            TextPosition first = textPositions.get(start);
            TextPosition last = textPositions.get(Math.min(end - 1, textPositions.size() - 1));
            
            TextLocation loc = new TextLocation();
            loc.page = currentPage;
            loc.x = first.getXDirAdj();
            loc.y = first.getYDirAdj(); 
            // Width spans from the start of the first char to the end of the last char in the match
            loc.width = last.getXDirAdj() + last.getWidthDirAdj() - first.getXDirAdj();
            loc.height = first.getHeightDir();
            loc.pageHeight = getCurrentPage().getMediaBox().getHeight();
            loc.pageWidth = getCurrentPage().getMediaBox().getWidth();
            
            System.out.println("DEBUG (PDFLocation): Mapped to coordinates: X=" + loc.x + ", Y=" + loc.y + " (Page: " + loc.pageWidth + "x" + loc.pageHeight + ")");
            
            locations.add(loc);
        }
    }
}
