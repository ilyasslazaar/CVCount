import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public class Img {
    private BufferedImage img;

    public Img(BufferedImage _img) {
        img = _img;
    }

    private static Tesseract getTesseract() {
        Tesseract instance = new Tesseract();
        instance.setDatapath("C:/Users/Probook/IdeaProjects/FiltrageCvProject/tessdata");
        instance.setLanguage("fra");
        instance.setPageSegMode(1);
        instance.setTessVariable("N", "400");
        //instance.setHocr(true);
        return instance;
    }

    public String extractText() throws TesseractException {
        Tesseract tesseract = getTesseract();
        String result = tesseract.doOCR(this.img);
        //String result = tesseract.doOCR(file);
        //System.out.println(result);
        return result;
    }
}
