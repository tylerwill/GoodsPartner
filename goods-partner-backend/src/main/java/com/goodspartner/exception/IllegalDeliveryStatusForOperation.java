package com.goodspartner.exception;

import com.goodspartner.entity.Delivery;

public class IllegalDeliveryStatusForOperation extends RuntimeException {

    private static final String ILLEGAL_STATUS_MESSAGE = "Unable to %s delivery: %s with status: %s";

    public IllegalDeliveryStatusForOperation(Delivery delivery, String action) {
        super(String.format(ILLEGAL_STATUS_MESSAGE, action, delivery.getId(), delivery.getStatus()));
    }
}
