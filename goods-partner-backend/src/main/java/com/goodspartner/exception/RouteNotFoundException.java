package com.goodspartner.exception;

import java.util.UUID;

public class RouteNotFoundException extends RuntimeException {
    private static final String NO_ROUTE_BY_ID_MESSAGE = "There is no route with id: %s";
    private static final String NO_ROUTE_WITH_DELIVERY_ID_MESSAGE = "There is no route with delivery`s id: %s";

    private static final String NO_ROUTE_BY_ID_MESSAGE_UKR = "Не знайдено жодного маршруту з id: %s";
    private static final String NO_ROUTE_WITH_DELIVERY_ID_MESSAGE_UKR = "Не знайдено жодного маршруту з id доставки: %s";

    public RouteNotFoundException(String message) {
        super(message);
    }

    public RouteNotFoundException(long routeId) {
        super(String.format(NO_ROUTE_BY_ID_MESSAGE_UKR, routeId));
    }

    public RouteNotFoundException(UUID deliveryId) {
        super(String.format(NO_ROUTE_WITH_DELIVERY_ID_MESSAGE_UKR, deliveryId));
    }
}