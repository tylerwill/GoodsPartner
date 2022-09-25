package com.goodspartner.web.handler;

import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.exceptions.NoOrdersFoundForDelivery;
import com.goodspartner.web.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DeliveryControllerAdvice {

    @ExceptionHandler(DeliveryNotFoundException.class)
    public ResponseEntity<ErrorMessage> deliveryNotFoundException(DeliveryNotFoundException exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(NoOrdersFoundForDelivery.class)
    public ResponseEntity<ErrorMessage> ordersNotFoundInDelivery(NoOrdersFoundForDelivery exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
