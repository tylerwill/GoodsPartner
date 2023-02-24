package com.goodspartner.exception.route;

public class IllegalRouteStateForRoutePointUpdateException extends RuntimeException {

    private static final String ROUTE_IS_NOT_STARTED_YET = "Route should be in state %s for operation %s";

    private static final String MESSAGE_UKR = "Модифікаціія точки можлива лише для активного маршруту";

    public IllegalRouteStateForRoutePointUpdateException() {
        super(MESSAGE_UKR);
    }
}
