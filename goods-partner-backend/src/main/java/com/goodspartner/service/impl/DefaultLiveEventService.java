package com.goodspartner.service.impl;

import com.goodspartner.event.LiveEvent;
import com.goodspartner.service.LiveEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
@Slf4j
public class DefaultLiveEventService implements LiveEventService {

    private final List<Consumer<LiveEvent>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void subscribe(Consumer<LiveEvent> event) {
        listeners.add(event);
        log.info("New subscriber added, total count: {}", listeners.size());
    }

    @Override
    public void publish(LiveEvent event) {
        log.info("Processing event: {}", event);
        listeners.forEach(listener -> listener.accept(event));
    }
}
