package com.goodspartner.service;

import com.goodspartner.entity.Route;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;

import java.util.UUID;

public interface EventService {
    void publishForLogist(String eventMessage, EventType eventType, ActionType actionType, UUID deliveryId);

    void publishForDriverAndLogist(String eventMessage, EventType eventType, ActionType actionType, Route route);
}
