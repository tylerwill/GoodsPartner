package com.goodspartner.web.handler;

import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.NoOrdersFoundForDelivery;
import com.goodspartner.exception.NoRoutesFoundForDelivery;
import com.goodspartner.exception.delivery.DeliveryAlreadyExistException;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForApproval;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForDeletion;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForOrderUpdate;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForRecalculation;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForRouteReordering;
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
            DeliveryAlreadyExistException.class,

            IllegalDeliveryStateForApproval.class,
            IllegalDeliveryStateForDeletion.class,
            IllegalDeliveryStateForRecalculation.class,
            IllegalDeliveryStateForOrderUpdate.class,
            IllegalDeliveryStateForRouteReordering.class,

            NoRoutesFoundForDelivery.class,
            NoOrdersFoundForDelivery.class,
    })
    public ResponseEntity<ErrorResponse> genericBadRequestResponseHandler(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
