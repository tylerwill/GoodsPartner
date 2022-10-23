package com.goodspartner.exception;

import com.goodspartner.entity.DeliveryStatus;

import java.time.LocalDate;
import java.util.UUID;

public class DeliveryNotFoundException extends RuntimeException {

    private static final String NO_DELIVERY_BY_ID_MESSAGE = "There is no delivery with id: %s";
    private static final String NO_DELIVERIES_FOR_DATE_RANGE_MESSAGE = "No deliveries with status %s for date range from %s to %s";
    private static final String NO_DELIVERY_FOR_DATE_MESSAGE = "No delivery with status %s for date: %s";

    public DeliveryNotFoundException(UUID deliveryId) {
        super(String.format(NO_DELIVERY_BY_ID_MESSAGE, deliveryId));
    }

    public DeliveryNotFoundException(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo) {
        super(String.format(NO_DELIVERIES_FOR_DATE_RANGE_MESSAGE, status, dateFrom, dateTo));
    }

    public DeliveryNotFoundException(DeliveryStatus status, LocalDate date) {
        super(String.format(NO_DELIVERY_FOR_DATE_MESSAGE, status, date));
    }
}
