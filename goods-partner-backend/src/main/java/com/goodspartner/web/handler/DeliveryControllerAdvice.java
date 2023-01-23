package com.goodspartner.web.handler;

import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.NoOrdersFoundForDelivery;
import com.goodspartner.exception.NoRoutesFoundForDelivery;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DeliveryControllerAdvice {

    @ExceptionHandler(DeliveryNotFoundException.class)
    public ResponseEntity<ErrorResponse> deliveryNotFoundException(DeliveryNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({
            IllegalDeliveryStatusForOperation.class,
            NoRoutesFoundForDelivery.class,
            NoOrdersFoundForDelivery.class,
            UnknownAddressException.class
    })
    public ResponseEntity<ErrorResponse> genericBadRequestResponseHandler(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
