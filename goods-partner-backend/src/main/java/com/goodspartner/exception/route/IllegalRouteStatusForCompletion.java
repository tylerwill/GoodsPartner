package com.goodspartner.exception.route;

public class IllegalRouteStatusForCompletion extends RuntimeException {

    private static final String ROUTE_IN_WRONG_STATE_MESSAGE = "Wrong state %s for route for requested operation";

    private static final String ROUTE_IN_WRONG_STATE_MESSAGE_UKR = "Завершити можливо тільки розпочатий маршрут";

    public IllegalRouteStatusForCompletion() {
        super(ROUTE_IN_WRONG_STATE_MESSAGE_UKR);
    }
}
