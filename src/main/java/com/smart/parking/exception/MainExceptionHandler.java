package com.smart.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MainExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MainException> handleProductNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new MainException(
                ex.getMessage(), 404, "PLEASE CHOOSE CORRECT VALUE"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<MainException> userAlreadyExists(UserAlreadyExists ex) {
        return new ResponseEntity<>(new MainException(
                ex.getMessage(), 404, "PLEASE LOGIN IN"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExists.class)
    public ResponseEntity<MainException> userAlreadyExists(AlreadyExists ex) {
        return new ResponseEntity<>(new MainException(
                ex.getMessage(), 404, "WRONG VALUE"), HttpStatus.BAD_REQUEST);
    }
}
