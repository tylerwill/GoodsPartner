package com.goodspartner.exception;

import com.goodspartner.entity.RoutePoint;

import java.util.List;

public class IllegalRoutePointStatusForOperation extends RuntimeException {

    private static final String ILLEGAL_STATUS_MESSAGE = "Unable to %s routePoint: %s with status: %s";

    public IllegalRoutePointStatusForOperation(List<RoutePoint> routePoints, String action) {
        super(String.format(ILLEGAL_STATUS_MESSAGE, action, routePoints, "not pending"));
    }
}
