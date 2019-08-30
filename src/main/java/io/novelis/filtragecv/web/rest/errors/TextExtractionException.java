package io.novelis.filtragecv.web.rest.errors;

public class TextExtractionException extends RuntimeException {
    public TextExtractionException() {
    }

    public TextExtractionException(String message) {
        super(message);
    }

    public TextExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextExtractionException(Throwable cause) {
        super(cause);
    }

    public TextExtractionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
