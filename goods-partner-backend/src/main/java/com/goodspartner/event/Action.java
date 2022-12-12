package com.goodspartner.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Action {
    private final ActionType type;
    private final UUID deliveryId;
}