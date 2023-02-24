package com.goodspartner.exception.route;

public class IllegalRouteStatusForStart extends RuntimeException {

    private static final String ROUTE_IN_WRONG_STATE_MESSAGE = "Wrong state %s for route for requested operation";

    private static final String ROUTE_IN_WRONG_STATE_MESSAGE_UKR = "Розпочати можливо тільки підтверджений маршрут";

    public IllegalRouteStatusForStart() {
        super(ROUTE_IN_WRONG_STATE_MESSAGE_UKR);
    }
}
