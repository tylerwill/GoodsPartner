package com.goodspartner.web.handler;

import com.goodspartner.exception.AddressExternalNotFoundException;
import com.goodspartner.exception.AddressGeocodeException;
import com.goodspartner.exception.AddressOutOfRegionException;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AddressControllerAdvice {

    @ExceptionHandler({
            AddressExternalNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> addressExternalNotFoundException(AddressExternalNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({
            AddressOutOfRegionException.class,
            UnknownAddressException.class
    })
    public ResponseEntity<ErrorResponse> badRequest(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AddressGeocodeException.class)
    public ResponseEntity<ErrorResponse> googleApiException(AddressGeocodeException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
