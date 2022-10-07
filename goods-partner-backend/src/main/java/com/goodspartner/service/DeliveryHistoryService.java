package com.goodspartner.service;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;

import java.util.List;
import java.util.UUID;

public interface DeliveryHistoryService {

    void add(DeliveryHistory deliveryHistory);

    List<DeliveryHistoryDto> findByDelivery(UUID id);

    void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID id);

    void publishRouteStatusChangeAuto(RouteStatus routeStatus, Route route);

    void publishDeliveryCompleted(Delivery delivery);

    void publishIfRouteUpdated(RouteDto routeDto, RouteStatus status, Route route);

    void publishIfPointUpdated(RoutePoint routePoint, RoutePointStatus status, Route route);



}
