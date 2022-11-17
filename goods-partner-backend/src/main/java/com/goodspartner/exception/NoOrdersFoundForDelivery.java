package com.goodspartner.exception;

import java.util.UUID;

public class NoOrdersFoundForDelivery extends RuntimeException {

    private static final String NO_ORDERS_MESSAGE = "No orders found for delivery: %s";

    public NoOrdersFoundForDelivery(int deliveryId) {
        super(String.format(NO_ORDERS_MESSAGE, deliveryId));
    }
}
