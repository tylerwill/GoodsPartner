package com.goodspartner.exception;

public class OrderNotFoundException extends RuntimeException {

    private final static String ORDER_NOT_FOUND_MESSAGE = "Order with id: %s not found";
    private final static String ORDER_NOT_FOUND_MESSAGE_UKR = "Не знайдено жодного замовлення з id: %s";

    public OrderNotFoundException(long id) {
        super(String.format(ORDER_NOT_FOUND_MESSAGE_UKR, id));
    }
}
