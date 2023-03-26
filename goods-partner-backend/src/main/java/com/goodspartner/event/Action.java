package com.goodspartner.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Action {
    private final ActionType type;
    private final UUID deliveryId;
}