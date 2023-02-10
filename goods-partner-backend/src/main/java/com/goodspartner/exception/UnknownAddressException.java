package com.goodspartner.exception;

import com.goodspartner.entity.OrderExternal;

public class UnknownAddressException extends RuntimeException {

    private static final String MESSAGE = "Client: %s with Address: %s is in Unknown status";

    public UnknownAddressException(OrderExternal orderExternal) {
        super(formatMessage(orderExternal));
    }

    private static String formatMessage(OrderExternal orderExternal) {
        String clientName = orderExternal.getClientName();
        String orderAddress = orderExternal.getAddress();
        return String.format(MESSAGE, clientName, orderAddress);
    }
}
