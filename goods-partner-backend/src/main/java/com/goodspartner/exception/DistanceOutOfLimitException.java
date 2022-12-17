package com.goodspartner.exception;


public class DistanceOutOfLimitException extends RuntimeException {

    private static final String DISTANCE_OUT_OF_LIMIT_MESSAGE =
            "Requested address coordinates out of distance limit:\n %s.\n Change distance if needed";

    public DistanceOutOfLimitException(double distance) {
        super(String.format(DISTANCE_OUT_OF_LIMIT_MESSAGE, distance));
    }
}
