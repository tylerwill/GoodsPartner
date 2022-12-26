package com.goodspartner.exception;


public class GoogleApiException extends RuntimeException {
    private static final String MESSAGE = "Exception while trying to geocode address: %s";

    public GoogleApiException(String address, Throwable cause) {
        super(String.format(MESSAGE, address), cause);
    }

    public GoogleApiException() {
    }
}