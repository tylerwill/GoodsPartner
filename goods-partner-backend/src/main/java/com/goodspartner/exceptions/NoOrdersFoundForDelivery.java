package com.goodspartner.exceptions;

import java.util.UUID;

public class NoOrdersFoundForDelivery extends RuntimeException {

    private static final String NO_ORDERS_MESSAGE = "No orders found for delivery: %s";

    public NoOrdersFoundForDelivery(UUID deliveryId) {
        super(String.format(NO_ORDERS_MESSAGE, deliveryId));
    }
}
