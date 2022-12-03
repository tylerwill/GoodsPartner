package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {
    UPDATE("UPDATE"),
    INFO("INFO");

    @Getter
    private final String type;
}
