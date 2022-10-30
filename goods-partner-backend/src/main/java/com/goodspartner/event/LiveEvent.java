package com.goodspartner.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LiveEvent {
    private String message;
    private EventType type;
    private Action action;

    public LiveEvent(String message, EventType type) {
        this.message = message;
        this.type = type;
    }
}
