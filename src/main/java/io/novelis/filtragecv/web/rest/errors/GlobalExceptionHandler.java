package io.novelis.filtragecv.web.rest.errors;

import io.undertow.server.RequestTooBigException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(NotFoundException ex) {
        return new ResponseEntity<>(new CustomGlobalException("df", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(Exception ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(new CustomGlobalException("fuck off", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(BadRequestException ex) {
        return new ResponseEntity<>(new CustomGlobalException("fuck off", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RequestTooBigException.class)
    public ResponseEntity<CustomGlobalException> handleException(io.undertow.server.RequestTooBigException ex) {
        return new ResponseEntity<>(new CustomGlobalException("fuck off", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
