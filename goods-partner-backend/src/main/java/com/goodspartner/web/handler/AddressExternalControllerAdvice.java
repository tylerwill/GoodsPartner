package com.goodspartner.web.handler;

import com.goodspartner.exception.AddressExternalNotFoundException;
import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AddressExternalControllerAdvice {

    @ExceptionHandler(AddressExternalNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressExternalNotFoundException(AddressExternalNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
