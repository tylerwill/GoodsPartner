package com.goodspartner.exceptions;

import com.goodspartner.entity.Route;

public class IllegalRouteStatusForOperation extends RuntimeException {

    private static final String ILLEGAL_STATUS_MESSAGE = "Unable to %s route: %s with status: %s";

    public IllegalRouteStatusForOperation(Route route, String action) {
        super(String.format(ILLEGAL_STATUS_MESSAGE, action, route.getId(), route.getStatus()));
    }
}
