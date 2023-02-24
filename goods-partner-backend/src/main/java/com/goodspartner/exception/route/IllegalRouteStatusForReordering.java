package com.goodspartner.exception.route;

public class IllegalRouteStatusForReordering extends RuntimeException {

    private static final String ILLEGAL_STATUS_MESSAGE = "Unable to %s route: %s with status: %s";

    private static final String MESSAGE_UKR = "Зміна порядку не можлива лише для завершеного маршруту";

    public IllegalRouteStatusForReordering() {
        super(MESSAGE_UKR);
    }
}
