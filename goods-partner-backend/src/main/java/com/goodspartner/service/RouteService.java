package com.goodspartner.service;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Route;
import com.goodspartner.web.action.RouteAction;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public interface RouteService {

    List<Route> findRelatedRoutesByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication);

    List<Route> findByDeliveryIdExtended(UUID deliveryId);

    List<Route> findByDeliveryId(UUID deliveryId);

    Route updateRoute(long routeId, RouteAction action);

    void reorderRoutePoints(long routeId, LinkedList<RoutePointDto> routePointDtos);

    Route findExtendedById(Long route);

}

