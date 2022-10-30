package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EventType {
    INFO("INFO"),
    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    @Getter
    private final String type;
}
