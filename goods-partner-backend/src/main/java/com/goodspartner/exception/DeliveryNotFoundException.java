package com.goodspartner.exception;

import com.goodspartner.entity.DeliveryStatus;

import java.time.LocalDate;
import java.util.UUID;

public class DeliveryNotFoundException extends RuntimeException {

    private static final String NO_DELIVERY_BY_ID_MESSAGE = "There is no delivery with id: %s";
    private static final String NO_DELIVERY_FOR_DATE_MESSAGE = "No delivery with status %s for date: %s";

    private static final String NO_DELIVERY_BY_ID_MESSAGE_UKR = "Відсутня доставка з id: %s";
    private static final String NO_DELIVERY_FOR_DATE_MESSAGE_UKR = "Відсутня доставка із статусом %s на %s";

    public DeliveryNotFoundException(UUID deliveryId) {
        super(String.format(NO_DELIVERY_BY_ID_MESSAGE_UKR, deliveryId));
    }

    public DeliveryNotFoundException(DeliveryStatus status, LocalDate date) {
        super(String.format(NO_DELIVERY_FOR_DATE_MESSAGE_UKR, status, date));
    }
}
