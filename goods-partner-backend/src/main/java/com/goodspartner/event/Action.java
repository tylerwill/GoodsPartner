package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class Action {
    private ActionType type;
    private UUID deliveryId;
}
