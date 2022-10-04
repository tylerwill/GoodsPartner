package com.goodspartner.exceptions;

import java.util.UUID;

public class NoRoutesFoundForDelivery extends RuntimeException {

    private static final String NO_ORDERS_MESSAGE = "No routes found for delivery: %s";

    public NoRoutesFoundForDelivery(UUID deliveryId) {
        super(String.format(NO_ORDERS_MESSAGE, deliveryId));
    }
}

