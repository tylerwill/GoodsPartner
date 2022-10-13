package com.goodspartner.service;

import com.goodspartner.action.RouteAction;
import com.goodspartner.action.RoutePointAction;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

import java.util.LinkedList;
import java.util.UUID;

public interface RouteService {

    RouteActionResponse update(int routeId, RouteAction action);

    RoutePointActionResponse updatePoint(int routeId, UUID routePointId, RoutePointAction action);

    void reorderRoutePoints(UUID deliveryId, int routeId, LinkedList<RoutePoint> routePoints);
}

