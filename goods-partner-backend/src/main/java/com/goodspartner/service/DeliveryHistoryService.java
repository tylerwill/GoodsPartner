package com.goodspartner.service;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;

import java.util.List;
import java.util.UUID;

public interface DeliveryHistoryService {

    void add(DeliveryHistory deliveryHistory);

    List<DeliveryHistoryDto> findByDelivery(UUID id);

    void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID id);

    void publishRouteStatusChangeAuto(Route route);

    void publishDeliveryCompleted(Delivery delivery);

    void publishRouteUpdated(Route route);

    void publishRoutePointUpdated(RoutePoint routePoint, Route route);
}
