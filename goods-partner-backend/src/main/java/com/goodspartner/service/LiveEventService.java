package com.goodspartner.service;

import com.goodspartner.entity.User;
import com.goodspartner.event.LiveEvent;

import java.util.UUID;
import java.util.function.Consumer;

public interface LiveEventService {

    void subscribe(Consumer<LiveEvent> event, UUID heartbeatId);

    void publishHeartBeat(UUID heartbeatId);

    void publishToDriver(LiveEvent event, User driver);

    void publishToAdminAndLogistician(LiveEvent event);
}
