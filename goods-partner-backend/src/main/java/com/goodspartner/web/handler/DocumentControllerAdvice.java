package com.goodspartner.web.handler;

import com.goodspartner.exception.DocumentNotFoundException;
import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DocumentControllerAdvice {
    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorResponse> documentNotFoundException(DocumentNotFoundException exception) {
        ErrorResponse errorMessage = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
