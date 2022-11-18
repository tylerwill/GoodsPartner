package com.goodspartner.exception;

import com.goodspartner.entity.AddressExternal;

public class UnknownAddressException extends RuntimeException{

    private static final String MESSAGE = "Client: %s with Address: %s is in Unknown status";
    public UnknownAddressException(AddressExternal addressExternal) {
        super(formatMessage(addressExternal));
    }

    private static String formatMessage(AddressExternal addressExternal) {
        String clientName = addressExternal.getOrderAddressId().getClientName();
        String orderAddress = addressExternal.getOrderAddressId().getOrderAddress();
        return String.format(MESSAGE, clientName, orderAddress);
    }
}
