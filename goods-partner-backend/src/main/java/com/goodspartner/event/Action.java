package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Action {
    private final ActionType type;
    private UUID deliveryId;

    public Action(ActionType type) {
        this.type = type;
    }
}