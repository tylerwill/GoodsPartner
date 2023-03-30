package com.goodspartner.exception;

import com.goodspartner.entity.DeliveryType;

import java.util.UUID;

public class NoOrdersFoundForDelivery extends RuntimeException {

    private static final String NO_ORDERS_MESSAGE = "No orders found for delivery: %s";
    private static final String NO_ORDERS_MESSAGE_DELIVERY_TYPE = "No orders found for delivery %s and delivery type %s";
    private static final String NO_ORDERS_MESSAGE_UKR = "Не знайдено жодного замовлення для доставки: %s";
    private static final String NO_ORDERS_MESSAGE_DELIVERY_TYPE_UKR = "Не знайдено жодного замовлення для доставки %s та типу доставки %s";

    public NoOrdersFoundForDelivery(UUID deliveryId) {
        super(String.format(NO_ORDERS_MESSAGE_UKR, deliveryId));
    }

    public NoOrdersFoundForDelivery(UUID deliveryId, DeliveryType deliveryType) {
        super(String.format(NO_ORDERS_MESSAGE_DELIVERY_TYPE_UKR, deliveryId, deliveryType));
    }
}
