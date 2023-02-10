package com.goodspartner.exception;

import com.goodspartner.entity.AddressExternal;

public class AddressExternalNotFoundException extends RuntimeException {

    private static final String NO_ADDRESS_EXTERNAL_BY_ID_MESSAGE = "There is no client address with id: %s";

    public AddressExternalNotFoundException(AddressExternal.OrderAddressId id) {
        super(String.format(NO_ADDRESS_EXTERNAL_BY_ID_MESSAGE, id));
    }

}
