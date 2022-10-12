package com.goodspartner.exceptions;


import java.util.UUID;

public class RouteNotFoundException extends RuntimeException {
    private static final String NO_ROUTE_BY_ID_MESSAGE = "There is no route with id: %s";
    private static final String NO_ROUTE_WITH_DELIVERY_ID_MESSAGE = "There is no route with delivery`s id: %s";

    public RouteNotFoundException(String message) {
        super(message);
    }

    public RouteNotFoundException(int id) {
        super(String.format(NO_ROUTE_BY_ID_MESSAGE, id));
    }

    public RouteNotFoundException(UUID deliveryId) {
        super(String.format(NO_ROUTE_WITH_DELIVERY_ID_MESSAGE, deliveryId));
    }
}