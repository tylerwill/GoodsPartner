package com.goodspartner.service;

import com.goodspartner.entity.DeliveryHistory;

import java.util.List;
import java.util.UUID;

public interface DeliveryHistoryService {

    List<DeliveryHistory> findByDeliveryId(UUID id);

}
