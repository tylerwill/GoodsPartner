package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {
    DELIVERY_UPDATED("DELIVERY_UPDATED");

    @Getter
    private final String type;
}
