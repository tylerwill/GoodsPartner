package com.goodspartner.service;

import com.goodspartner.event.LiveEvent;

import java.util.function.Consumer;

public interface LiveEventService {

    void subscribe(Consumer<LiveEvent> event);

    void publish(LiveEvent event);
}
