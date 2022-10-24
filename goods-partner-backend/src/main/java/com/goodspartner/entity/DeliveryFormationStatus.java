package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DeliveryFormationStatus {
    ORDERS_LOADING("ORDERS_LOADING"),
    ORDERS_LOADED("ORDERS_LOADED"),
    ROUTE_CALCULATION("ROUTE_CALCULATION"),
    COMPLETED ("COMPLETED");

    @Getter
    private final String status;
}
