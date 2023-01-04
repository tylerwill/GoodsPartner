package com.goodspartner.service;

import com.goodspartner.dto.Coordinates;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.web.action.RoutePointAction;

import java.util.List;

public interface RoutePointService {

    RoutePoint updateRoutePoint(long routePointId, RoutePointAction action);

    List<OrderExternal> getRoutePointOrders(long routePointId);

    void updateCoordinates(long routePointId, Coordinates coordinates);

    List<RoutePoint> actualizePendingRoutePoints(RoutePoint current, Route route);
}
