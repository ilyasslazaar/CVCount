import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class CvPdf extends Cv {
    public CvPdf(String path) throws Exception {
        super(path);
    }
    protected String extractText() throws Exception {
        File file = new File(this.filePath);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(document).toLowerCase();
        if(text.length() > 500) {
            document.close();
            return text;
        }
        text = "";
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            text += new Img(pdfRenderer.renderImageWithDPI(page, 400, ImageType.RGB)).extractText();
        }
        document.close();
        return text.toLowerCase();
    }
}