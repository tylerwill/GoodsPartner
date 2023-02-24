package com.goodspartner.exception.delivery;

// Temp exception before localization
public class IllegalDeliveryStateForApproval extends RuntimeException {

    private static final String MESSAGE_UKR = "Підтвердження можливе лише для доставки в статусі - Створена";

    public IllegalDeliveryStateForApproval() {
        super(MESSAGE_UKR);
    }
}
