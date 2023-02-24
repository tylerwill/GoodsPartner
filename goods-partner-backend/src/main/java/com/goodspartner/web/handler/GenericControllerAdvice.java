package com.goodspartner.web.handler;

import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.InvalidActionType;
import com.goodspartner.exception.StoreNotFoundException;
import com.goodspartner.exception.SubscriberNotFoundException;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GenericControllerAdvice {

    @ExceptionHandler({
            CarNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> entityNotFoundException(Exception exception) {
        ErrorResponse errorMessage = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler({
            SubscriberNotFoundException.class,
            UnknownAddressException.class,
            StoreNotFoundException.class,
            InvalidActionType.class
    })
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(Exception exception) {
        ErrorResponse errorMessage = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
