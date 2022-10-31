package com.goodspartner.exception;

public class OrderNotFoundException extends RuntimeException {
    private static final String NO_ORDER_BY_ID_MESSAGE = "There is no order with id: %s";

    public OrderNotFoundException(int orderId) {
        super(String.format(NO_ORDER_BY_ID_MESSAGE, orderId));
    }
}
