package io.novelis.filtragecv.service.textprocessing;

import io.novelis.filtragecv.config.ApplicationProperties;
import io.novelis.filtragecv.web.rest.errors.TextExtractionException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.nio.file.Paths;

public class ImageProcessor {
    @Autowired
    static ApplicationProperties applicationProperties;
    private BufferedImage img;

    public ImageProcessor(BufferedImage _img) {
        img = _img;
    }

    private static Tesseract getTesseract() {
        Tesseract instance = new Tesseract();
        instance.setDatapath(Paths.get(applicationProperties.getUploadDir())
            .toAbsolutePath().normalize().toString());
        instance.setLanguage("fra");
        instance.setPageSegMode(1);
        instance.setTessVariable("N", "400");
        return instance;
    }

    public String extractText() {
        Tesseract tesseract = getTesseract();
        try {
            String result = tesseract.doOCR(this.img);
            return result;
        } catch (TesseractException e) {
            throw new TextExtractionException("Could not extract text from image");
        }
    }
}
