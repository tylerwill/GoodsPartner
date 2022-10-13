package com.goodspartner.exceptions;

import java.util.UUID;

public class RoutePointNotFoundException extends RuntimeException {

    private static final String MESSAGE = " %s";

    public RoutePointNotFoundException(UUID routePointId) {
        super(String.format(MESSAGE, routePointId));
    }
}
