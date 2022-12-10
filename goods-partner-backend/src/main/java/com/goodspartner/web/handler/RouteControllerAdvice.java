package com.goodspartner.web.handler;

import com.goodspartner.exception.IllegalRoutePointStatusForOperation;
import com.goodspartner.exception.IllegalRouteStatusForOperation;
import com.goodspartner.exception.RouteInWrongState;
import com.goodspartner.exception.RouteNotFoundException;
import com.goodspartner.web.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RouteControllerAdvice {

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorResponse> routeNotFoundException(RouteNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({
            RouteInWrongState.class,
            IllegalRouteStatusForOperation.class,
            IllegalRoutePointStatusForOperation.class
    })
    public ResponseEntity<ErrorResponse> genericBadRequestResponseHandler(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
