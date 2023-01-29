package com.goodspartner.exception;

public class SubscriberNotFoundException extends RuntimeException {

    private static final String NO_SUBSCRIBER_MESSAGE = "User is not in subscribers list: %s";

    public SubscriberNotFoundException(String userName) {
        super(String.format(NO_SUBSCRIBER_MESSAGE, userName));
    }
}

