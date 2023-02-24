package com.goodspartner.exception.delivery;

// Temp exception before localization
public class IllegalDeliveryStateForDeletion extends RuntimeException {

    private static final String MESSAGE_UKR = "Видалення доставки можливе лише в статусі - Створена";

    public IllegalDeliveryStateForDeletion() {
        super(MESSAGE_UKR);
    }
}
