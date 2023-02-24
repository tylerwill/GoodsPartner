package com.goodspartner.exception;

public class NoRoutesFoundForDelivery extends RuntimeException {

    private static final String NO_ORDERS_MESSAGE = "No routes found for delivery: %s";
    private static final String NO_ORDERS_MESSAGE_UKR = "Неможливо затвердити доставку без маршрутів";

    public NoRoutesFoundForDelivery() {
        super(NO_ORDERS_MESSAGE_UKR);
    }
}

