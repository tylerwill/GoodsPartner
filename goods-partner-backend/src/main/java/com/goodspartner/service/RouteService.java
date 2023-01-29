package com.goodspartner.service;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Route;
import com.goodspartner.web.action.RouteAction;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public interface RouteService {

    List<Route> findRelatedRoutesByDeliveryId(UUID deliveryId);

    List<Route> findByDeliveryIdExtended(UUID deliveryId);

    List<Route> findByDeliveryId(UUID deliveryId);

    Route updateRoute(long routeId, RouteAction action);

    void reorderRoutePoints(long routeId, LinkedList<RoutePointDto> routePointDtos);

    Route findExtendedById(Long route);

}

