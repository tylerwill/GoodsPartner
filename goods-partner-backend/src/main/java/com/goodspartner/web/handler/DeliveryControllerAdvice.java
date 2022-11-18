package com.goodspartner.web.handler;

import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.IllegalRoutePointStatusForOperation;
import com.goodspartner.exception.IllegalRouteStatusForOperation;
import com.goodspartner.exception.InvalidActionType;
import com.goodspartner.exception.NoOrdersFoundForDelivery;
import com.goodspartner.exception.NoRoutesFoundForDelivery;
import com.goodspartner.exception.RouteInWrongState;
import com.goodspartner.exception.StoreNotFoundException;
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

    @ExceptionHandler(NoRoutesFoundForDelivery.class)
    public ResponseEntity<ErrorMessage> routesNotFoundInDelivery(NoRoutesFoundForDelivery exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(IllegalDeliveryStatusForOperation.class)
    public ResponseEntity<ErrorMessage> illegalDeliveryStatusForOperation(IllegalDeliveryStatusForOperation exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(IllegalRoutePointStatusForOperation.class)
    public ResponseEntity<ErrorMessage> illegalRoutePointStatusForOperation(IllegalRoutePointStatusForOperation exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(IllegalRouteStatusForOperation.class)
    public ResponseEntity<ErrorMessage> illegalRouteStatusForOperation(IllegalRouteStatusForOperation exception) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(InvalidActionType.class)
    public ResponseEntity<ErrorMessage> illegalActionForOperation(InvalidActionType invalidActionType) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, invalidActionType.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(RouteInWrongState.class)
    public ResponseEntity<ErrorMessage> routeInWrongState(RouteInWrongState routeInWrongState) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, routeInWrongState.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<ErrorMessage> storeNotFound(StoreNotFoundException storeNotFoundException) {
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, storeNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
