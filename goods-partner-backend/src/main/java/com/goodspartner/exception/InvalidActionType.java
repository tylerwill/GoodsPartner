package com.goodspartner.exception;

public class InvalidActionType extends RuntimeException{
    private static final String ILLEGAL_ACTION = "Unknown action type: %s";

    private static final String ILLEGAL_ACTION_UKR = "Невідома дія: %s";

    public InvalidActionType(String action) {
        super(String.format(ILLEGAL_ACTION_UKR, action));
    }
}
