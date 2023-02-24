package com.goodspartner.exception;

import com.goodspartner.entity.AddressExternal;

public class AddressExternalNotFoundException extends RuntimeException {

    private static final String NO_ADDRESS_BY_ID_MESSAGE = "There is no address with id: %s";
    private static final String NO_ADDRESS_BY_ID_MESSAGE_UKR = "Адреси з id: %s не знайдено";

    public AddressExternalNotFoundException(AddressExternal.OrderAddressId id) {
        super(String.format(NO_ADDRESS_BY_ID_MESSAGE_UKR, id));
    }
}
