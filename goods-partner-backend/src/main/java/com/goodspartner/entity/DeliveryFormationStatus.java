package com.goodspartner.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeliveryFormationStatus {

    ORDERS_LOADING,
    ORDERS_LOADING_FAILED,
    ORDERS_LOADED,

    READY_FOR_CALCULATION,
    ROUTE_CALCULATION,
    ROUTE_CALCULATION_FAILED,
    CALCULATION_COMPLETED,

}
