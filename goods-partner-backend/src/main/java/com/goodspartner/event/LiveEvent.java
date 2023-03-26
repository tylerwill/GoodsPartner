package com.goodspartner.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@EqualsAndHashCode
public class LiveEvent {
    private String message;
    private EventType type;
    private Action action;
}
