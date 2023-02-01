package com.goodspartner.exception;

import com.goodspartner.entity.Route;

public class ImppossbleRouteRecalculationException extends RuntimeException {

    private static final String MESSAGE = "Impossible to recalculate the route for car: %s";

    public ImppossbleRouteRecalculationException(Route route) {
        super(String.format(MESSAGE, route.getCar().getName()));
    }
}
