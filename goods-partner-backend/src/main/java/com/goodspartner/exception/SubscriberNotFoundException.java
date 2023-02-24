package com.goodspartner.exception;

public class SubscriberNotFoundException extends RuntimeException {

    private static final String NO_SUBSCRIBER_MESSAGE = "User is not in subscribers list: %s";
    private static final String NO_SUBSCRIBER_MESSAGE_UKR = "Користувач: %s - не зареєстрований";

    public SubscriberNotFoundException(String userName) {
        super(String.format(NO_SUBSCRIBER_MESSAGE_UKR, userName));
    }
}

