package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {
    DELIVERY_CREATED("DELIVERY_CREATED"),
    DELIVERY_UPDATED("DELIVERY_UPDATED"),
    ROUTE_UPDATED("ROUTE_UPDATED"),
    ORDER_UPDATED("ORDER_UPDATED"),
    INFO("INFO");

    @Getter
    private final String type;
}
