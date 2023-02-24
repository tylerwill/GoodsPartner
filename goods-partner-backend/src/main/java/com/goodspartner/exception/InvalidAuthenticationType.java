package com.goodspartner.exception;

public class InvalidAuthenticationType extends RuntimeException {
    private static final String MESSAGE = "Invalid authentication type";
    private static final String MESSAGE_UKR = "Невірно заданий тип аутентифікації";

    public InvalidAuthenticationType() {
        super(MESSAGE_UKR);
    }
}
