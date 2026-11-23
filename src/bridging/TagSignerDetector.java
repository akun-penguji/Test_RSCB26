package bridging;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Optimized utility to detect coordinates and page number of all text tags in a PDF in one pass.
 * Tags should be in the format #TAG_NAME#
 */
public class TagSignerDetector extends PDFTextStripper {
    private Map<String, List<TagCoordinate>> tagMap = new HashMap<>();
    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9_]+#");

    public TagSignerDetector() throws IOException {
        super();
        setSortByPosition(true);
    }

    /**
     * Scans the PDF and returns a map of all tags found and their coordinates.
     * 
     * @param filePath Path to the PDF file
     * @return Map where key is the tag (e.g., "#SIGN_DOKTER_A#") and value is a list of coordinates
     * @throws IOException 
     */
    public Map<String, List<TagCoordinate>> detectAllTags(String filePath) throws IOException {
        tagMap.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        try (PDDocument document = PDDocument.load(file)) {
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                setStartPage(i);
                setEndPage(i);
                getText(document);
            }
        }
        return tagMap;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        Matcher matcher = TAG_PATTERN.matcher(string);
        while (matcher.find()) {
            String tag = matcher.group();
            int start = matcher.start();
            
            if (start < textPositions.size()) {
                TextPosition firstChar = textPositions.get(start);
                float pageWidth = getCurrentPage().getMediaBox().getWidth();
                float pageHeight = getCurrentPage().getMediaBox().getHeight();
                
                TagCoordinate coord = new TagCoordinate(
                    getCurrentPageNo(),
                    firstChar.getXDirAdj(),
                    firstChar.getYDirAdj(),
                    firstChar.getWidthDirAdj() * tag.length(),
                    firstChar.getHeightDir(),
                    pageWidth,
                    pageHeight
                );
                
                tagMap.computeIfAbsent(tag, k -> new ArrayList<>()).add(coord);
            }
        }
    }

    public static class TagCoordinate {
        public int page;
        public float x;
        public float y;
        public float width;
        public float height;
        public float pageWidth;
        public float pageHeight;

        public TagCoordinate(int page, float x, float y, float width, float height, float pageWidth, float pageHeight) {
            this.page = page;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.pageWidth = pageWidth;
            this.pageHeight = pageHeight;
        }

        @Override
        public String toString() {
            return "TagCoordinate{page=" + page + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", pageWidth=" + pageWidth + ", pageHeight=" + pageHeight + "}";
        }
    }
}
