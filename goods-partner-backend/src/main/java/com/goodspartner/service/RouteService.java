package com.goodspartner.service;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.RoutePoint;

import java.util.LinkedList;
import java.util.UUID;

public interface RouteService {

    void update(int id, RouteDto routeDto);

    void updatePoint(int routeId, String routePointId, RoutePoint routePoint);

    void reorderRoutePoints(UUID deliveryId, int routeId, LinkedList<RoutePoint> routePoints);
}

