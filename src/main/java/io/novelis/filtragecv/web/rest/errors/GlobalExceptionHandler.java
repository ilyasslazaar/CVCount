package io.novelis.filtragecv.web.rest.errors;

import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;


@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(NotFoundException ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(Exception ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(FileStorageException ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<CustomGlobalException> handleException(TextExtractionException ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = MultipartException.class)
    public ResponseEntity<CustomGlobalException> handleFileUploadingError(Exception ex) {
        return new ResponseEntity<>(new CustomGlobalException(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
