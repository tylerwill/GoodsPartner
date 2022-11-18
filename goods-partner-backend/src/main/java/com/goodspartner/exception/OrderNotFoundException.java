package com.goodspartner.exception;

public class OrderNotFoundException extends RuntimeException {

    private final static String ORDER_NOT_FOUND_MESSAGE = "Order with id: %s not found";

    public OrderNotFoundException(int id) {
        super(String.format(ORDER_NOT_FOUND_MESSAGE, id));
    }
}
