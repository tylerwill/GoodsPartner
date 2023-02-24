package com.goodspartner.exception.route;

public class IllegalRoutePointStatusForReordering extends RuntimeException {

    private static final String ILLEGAL_STATUS_MESSAGE = "Unable to %s routePoint: %s with status: %s";

    private static final String MESSAGE_UKR = "Зміна порядкку маршрута можлива лише для точок що не були завершені";

    public IllegalRoutePointStatusForReordering() {
        super(MESSAGE_UKR);
    }
}
