package com.goodspartner.exception.delivery;

// Temp exception before localization
public class IllegalDeliveryStateForRouteReordering extends RuntimeException{

    private static final String MESSAGE_UKR = "Реорганізація маршрута неможлива для доставки в статусі - Завершена";

    public IllegalDeliveryStateForRouteReordering() {
        super(MESSAGE_UKR);
    }

}
