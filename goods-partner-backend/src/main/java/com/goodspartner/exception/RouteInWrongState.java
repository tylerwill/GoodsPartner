package com.goodspartner.exception;

import com.goodspartner.entity.Route;

public class RouteInWrongState extends RuntimeException {

    private static final String ROUTE_IN_WRONG_STATE_MESSAGE = "Wrong state %s for route for requested operation";

    public RouteInWrongState(Route route) {
        super(String.format(ROUTE_IN_WRONG_STATE_MESSAGE, route.getStatus()));
    }
}
