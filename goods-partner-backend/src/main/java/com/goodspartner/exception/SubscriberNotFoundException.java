package com.goodspartner.exception;

public class SubscriberNotFoundException extends RuntimeException {

    private static final String NO_SUBSCRIBER_MESSAGE = "User is not in subscribers list";

    public SubscriberNotFoundException() {
        super(NO_SUBSCRIBER_MESSAGE);
    }
}

