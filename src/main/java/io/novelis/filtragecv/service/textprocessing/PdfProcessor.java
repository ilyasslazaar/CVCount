package io.novelis.filtragecv.service.textprocessing;

import io.novelis.filtragecv.web.rest.errors.TextExtractionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfProcessor extends TextProcessor {
    public PdfProcessor(String path) {
        super(path);
    }

    protected String extractText() {
        try (PDDocument document = PDDocument.load(new File(this.filePath))) {

            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String text = pdfTextStripper.getText(document).toLowerCase();
            if (text.length() > 500) {
                document.close();
                return text.toLowerCase();
            }
            text = "";
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                text += new ImageProcessor(pdfRenderer.renderImageWithDPI(page, 400, ImageType.RGB)).extractText();
            }
            document.close();
            return text.toLowerCase();
        } catch (IOException e) {
            throw new TextExtractionException("could not extract text from pdf");
        }
    }

}
