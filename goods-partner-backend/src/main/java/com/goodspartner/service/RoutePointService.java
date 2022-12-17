package com.goodspartner.service;

import com.goodspartner.dto.Coordinates;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

import java.util.List;

public interface RoutePointService {

    List<RoutePoint> findByRouteId(long routeId);

    RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action);

    List<OrderExternal> getRoutePointOrders(long routePointId);

    void updateCoordinates(long routePointId, Coordinates coordinates);
}
