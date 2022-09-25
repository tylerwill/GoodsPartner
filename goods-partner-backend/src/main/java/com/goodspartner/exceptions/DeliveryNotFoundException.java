package com.goodspartner.exceptions;

import java.util.UUID;

public class DeliveryNotFoundException extends RuntimeException {

    private static final String NO_DELIVERY_BY_ID_MESSAGE = "There is no delivery with id: %s";

    public DeliveryNotFoundException(UUID deliveryId) {
        super(String.format(NO_DELIVERY_BY_ID_MESSAGE, deliveryId));
    }
}
