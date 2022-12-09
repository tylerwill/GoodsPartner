package com.goodspartner.exception;

public class IllegalRouteStateException extends RuntimeException {

    private static final String ROUTE_IS_NOT_STARTED_YET = "Route should be in state %s for operation %s";

    public IllegalRouteStateException(String requiredState, String operation) {
        super(String.format(ROUTE_IS_NOT_STARTED_YET, requiredState, operation));
    }
}
