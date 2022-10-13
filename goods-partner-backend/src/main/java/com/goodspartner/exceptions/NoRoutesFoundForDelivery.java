package com.goodspartner.exceptions;

import com.goodspartner.entity.Delivery;

public class NoRoutesFoundForDelivery extends RuntimeException {

    private static final String NO_ORDERS_MESSAGE = "No routes found for delivery: %s";

    public NoRoutesFoundForDelivery(Delivery delivery) {
        super(String.format(NO_ORDERS_MESSAGE, delivery.getId()));
    }
}

