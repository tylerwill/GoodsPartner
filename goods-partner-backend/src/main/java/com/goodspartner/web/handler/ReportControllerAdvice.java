package com.goodspartner.web.handler;

import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ReportControllerAdvice {
    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponse> deliveryTypeDoesNotExist(ConversionFailedException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
