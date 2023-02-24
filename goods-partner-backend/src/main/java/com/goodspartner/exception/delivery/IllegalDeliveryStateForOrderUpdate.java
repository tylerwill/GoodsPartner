package com.goodspartner.exception.delivery;

// Temp exception before localization
public class IllegalDeliveryStateForOrderUpdate extends RuntimeException {

    private static final String MESSAGE_UKR = "Зміна замовлень можлива лише для доставки в статусі - Створена";

    public IllegalDeliveryStateForOrderUpdate() {
        super(MESSAGE_UKR);
    }
}
