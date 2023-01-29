package com.goodspartner.exception;

public class InvalidAuthenticationType extends RuntimeException {
    private static final String MESSAGE = "Invalid authentication type";

    public InvalidAuthenticationType() {
        super(MESSAGE);
    }
}
