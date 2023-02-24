package com.goodspartner.exception;

public class DriverNotFoundException extends RuntimeException {

    private static final String USER_NOT_FOUND_BY_ID_ROUTE_ID_UKR = "Для маршруту з id: %s не знайдено жодного водія";

    public DriverNotFoundException(long routeId) {
        super(String.format(USER_NOT_FOUND_BY_ID_ROUTE_ID_UKR, routeId));
    }
}
