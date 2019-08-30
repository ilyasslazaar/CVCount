package io.novelis.filtragecv.service.textprocessing;

import java.io.FileInputStream;

import io.novelis.filtragecv.web.rest.errors.TextExtractionException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DocProcessor extends TextProcessor{
    public DocProcessor(String path) {
        super(path);
    }

    protected String extractText() {
        try(XWPFDocument docx = new XWPFDocument(new FileInputStream(this.filePath))) {
            //using XWPFWordExtractor Class
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            return we.getText().toLowerCase();
        } catch (FileNotFoundException ex){
            throw new TextExtractionException("could not extract text from word doc");
        } catch (IOException ex) {
            throw new TextExtractionException("could not extract text from word doc");
        }
    }
}

