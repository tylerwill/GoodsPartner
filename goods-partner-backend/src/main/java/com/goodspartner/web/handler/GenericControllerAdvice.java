package com.goodspartner.web.handler;

import com.goodspartner.exception.AddressOutOfRegionException;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.GoogleApiException;
import com.goodspartner.exception.RouteNotFoundException;
import com.goodspartner.exception.SubscriberNotFoundException;
import com.goodspartner.exception.UnknownAddressException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class GenericControllerAdvice extends ResponseEntityExceptionHandler { // TODO do ve need extends from ResponseEntityExceptionHandler

    @ExceptionHandler(GoogleApiException.class)
    public ResponseEntity<ErrorMessage> googleApiException(GoogleApiException exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler({
            CarNotFoundException.class,
            RouteNotFoundException.class
    })
    public ResponseEntity<ErrorMessage> entityNotFoundException(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler({
            SubscriberNotFoundException.class,
            AddressOutOfRegionException.class,
            UnknownAddressException.class
    })
    public ResponseEntity<ErrorMessage> badRequestExceptionHandler(Exception exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
