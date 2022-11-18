package com.goodspartner.service;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.event.DeliveryAuditEvent;

import java.util.List;
import java.util.UUID;

public interface DeliveryHistoryService {

    void add(DeliveryHistory deliveryHistory);

    List<DeliveryHistoryDto> findByDeliveryId(UUID id);

    void publish(DeliveryAuditEvent event);
}
