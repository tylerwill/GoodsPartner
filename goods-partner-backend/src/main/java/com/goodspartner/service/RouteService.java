package com.goodspartner.service;

import com.goodspartner.action.RouteAction;
import com.goodspartner.action.RoutePointAction;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public interface RouteService {

    RouteActionResponse updateRoute(int routeId, RouteAction action);

    RoutePointActionResponse updateRoutePoint(int routeId, long routePointId, RoutePointAction action);

    void reorderRoutePoints(UUID deliveryId, int routeId, LinkedList<RoutePointDto> routePointDtos);

    List<RouteDto> findRoutesByDeliveryAndCar(Delivery delivery, Car car);
}

