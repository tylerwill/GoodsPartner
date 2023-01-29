package com.goodspartner.service;

import com.goodspartner.entity.User;
import com.goodspartner.event.LiveEvent;

import java.util.function.Consumer;

public interface LiveEventService {

    void subscribe(Consumer<LiveEvent> event);

    void publishHeartBeat();

    void publishToDriver(LiveEvent event, User driver);

    void publishToAdminAndLogistician(LiveEvent event);
}
