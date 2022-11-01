package com.goodspartner.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryAuditEvent {

    private final UUID deliveryId;
    private final String action;

    public DeliveryAuditEvent(String action, UUID deliveryId) {
        this.action = action;
        this.deliveryId = deliveryId;
    }
}
