package com.goodspartner.exception;

import java.util.UUID;

public class RoutePointNotFoundException extends RuntimeException {

    private static final String MESSAGE = " %s";

    public RoutePointNotFoundException(long routePointId) {
        super(String.format(MESSAGE, routePointId));
    }
}
