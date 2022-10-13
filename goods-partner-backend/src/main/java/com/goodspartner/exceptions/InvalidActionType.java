package com.goodspartner.exceptions;

public class InvalidActionType extends RuntimeException{
    private static final String ILLEGAL_ACTION = "Unknown action type: %s";

    public InvalidActionType(String action) {
        super(String.format(ILLEGAL_ACTION, action));
    }
}
