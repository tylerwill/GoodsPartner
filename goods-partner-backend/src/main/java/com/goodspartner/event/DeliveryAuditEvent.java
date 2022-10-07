package com.goodspartner.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryAuditEvent {

    private final String name;
    private final UUID id;

    public DeliveryAuditEvent(String name, UUID id) {
        this.name = name;
        this.id = id;
    }
}
