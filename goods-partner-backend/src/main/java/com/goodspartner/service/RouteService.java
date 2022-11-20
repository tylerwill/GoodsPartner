package com.goodspartner.service;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Route;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public interface RouteService {

    List<Route> findRelatedRoutesByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication);

    List<Route> findByDeliveryIdExtended(UUID deliveryId);

    List<Route> findByDeliveryId(UUID deliveryId);

    RouteActionResponse updateRoute(int routeId, RouteAction action);

    void reorderRoutePoints(int id, LinkedList<RoutePointDto> routePointDtos);

}

