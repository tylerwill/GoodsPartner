package com.goodspartner.service.impl;

import com.goodspartner.entity.Route;
import com.goodspartner.entity.User;
import com.goodspartner.event.Action;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.service.EventService;
import com.goodspartner.service.LiveEventService;
import com.goodspartner.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private final LiveEventService liveEventService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishForLogist(String eventMessage, EventType eventType, ActionType actionType, UUID deliveryId) {
        applicationEventPublisher.publishEvent(new DeliveryAuditEvent(eventMessage, deliveryId)); // History
        LiveEvent event = LiveEvent.builder()
                .message(eventMessage)
                .type(eventType)
                .action(new Action(actionType, deliveryId))
                .build();
        liveEventService.publishToAdminAndLogistician(event); // LiveEvent
    }

    @Override
    public void publishForDriverAndLogist(String eventMessage, EventType eventType, ActionType actionType, Route route) {
        UUID deliveryId = route.getDelivery().getId();
        applicationEventPublisher.publishEvent(new DeliveryAuditEvent(eventMessage, deliveryId)); // History

        User user = userService.findByRouteId(route.getId());
        LiveEvent event = LiveEvent.builder()
                .message(eventMessage)
                .type(eventType)
                .action(new Action(actionType, deliveryId))
                .build();
        liveEventService.publishToDriver(event, user);
    }


}
