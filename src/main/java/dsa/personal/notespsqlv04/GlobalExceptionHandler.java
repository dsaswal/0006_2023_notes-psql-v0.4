package dsa.personal.notespsqlv04;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> defaultExceptionHandler(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<ErrorResponse>(error, null, HttpStatus.BAD_REQUEST);
    }
}
