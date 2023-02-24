package com.goodspartner.exception.delivery;

// Temp exception before localization
public class IllegalDeliveryStateForRecalculation extends RuntimeException {

    private static final String MESSAGE_UKR = "Перерахування доставки можливе лише в статусі - Створена";

    public IllegalDeliveryStateForRecalculation() {
        super(MESSAGE_UKR);
    }
}
