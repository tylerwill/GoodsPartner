package com.goodspartner.exception;

public class OrderNotFoundException extends RuntimeException {
    private static final String NO_ORDER_BY_ID_LIST = "There is no order for list of ids";

    public OrderNotFoundException() {
        super(NO_ORDER_BY_ID_LIST);
    }

}
