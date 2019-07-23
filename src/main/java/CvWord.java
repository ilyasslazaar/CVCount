import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;

public class CvWord extends Cv {
    public CvWord(String path) throws Exception {
        super(path);
    }

    protected String extractText() throws Exception {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(this.filePath));
        //using XWPFWordExtractor Class
        XWPFWordExtractor we = new XWPFWordExtractor(docx);
        return we.getText().toLowerCase();
    }
}
