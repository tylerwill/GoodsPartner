package com.goodspartner.exception;

public class RoutePointNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Route point with id: %s not found";
    private static final String MESSAGE_UKR = "Точка маршруту з id: %s не знайдена";

    public RoutePointNotFoundException(long routePointId) {
        super(String.format(MESSAGE_UKR, routePointId));
    }
}
