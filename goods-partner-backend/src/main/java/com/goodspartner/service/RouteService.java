package com.goodspartner.service;

import com.goodspartner.action.RouteAction;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.web.controller.response.RouteActionResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public interface RouteService {

    List<RouteDto> findByDeliveryId(UUID deliveryId);

    RouteActionResponse updateRoute(int routeId, RouteAction action);

    void reorderRoutePoints(int id, LinkedList<RoutePointDto> routePointResponses);

    List<RouteDto> findRoutesByDeliveryAndCar(Delivery delivery, Car car);
}

