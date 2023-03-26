package com.goodspartner.event;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EventType {
    INFO,
    WARNING,
    SUCCESS,
    ERROR,
    HEARTBEAT
}
