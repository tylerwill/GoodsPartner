package com.goodspartner.exceptions;

import java.time.LocalDate;
import java.util.UUID;

public class DeliveryNotFoundException extends RuntimeException {

    private static final String NO_DELIVERY_BY_ID_MESSAGE = "There is no delivery with id: %s";
    private static final String NO_DELIVERIES_FOR_DATE_RANGE_MESSAGE = "No deliveries for date range from %s to %s";
    private static final String NO_DELIVERY_FOR_DATE_MESSAGE = "No deliverY for date: %s";

    public DeliveryNotFoundException(UUID deliveryId) {
        super(String.format(NO_DELIVERY_BY_ID_MESSAGE, deliveryId));
    }

    public DeliveryNotFoundException(LocalDate rangeStartDate, LocalDate rangeFinishDate) {
        super(String.format(NO_DELIVERIES_FOR_DATE_RANGE_MESSAGE, rangeStartDate, rangeFinishDate));
    }

    public DeliveryNotFoundException(LocalDate date) {
        super(String.format(NO_DELIVERY_FOR_DATE_MESSAGE, date));
    }
}
