package io.novelis.filtragecv.web.rest.errors;

import org.springframework.http.HttpStatus;

public class CustomGlobalException {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    private String message;
    private HttpStatus status;

    public CustomGlobalException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
