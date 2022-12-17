package com.goodspartner.service;

import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.event.LiveEvent;

import java.util.UUID;

public interface EventService {

    void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID id);

    void publishRouteStatusChangeAuto(Route route);

    void publishDeliveryCompleted(Delivery delivery);

    void publishRouteUpdated(Route route);

    void publishRoutePointUpdated(RoutePoint routePoint, Route route);

    void publishOrdersStatus(DeliveryHistoryTemplate template, UUID id);

    void publishEvent(LiveEvent event);

    void publishCoordinatesUpdated(RoutePoint routePoint, AddressExternal addressExternal);
}
