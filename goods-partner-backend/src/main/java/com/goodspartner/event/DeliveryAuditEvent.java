package com.goodspartner.event;

import java.util.UUID;

public record DeliveryAuditEvent(String action, UUID deliveryId) {

}
