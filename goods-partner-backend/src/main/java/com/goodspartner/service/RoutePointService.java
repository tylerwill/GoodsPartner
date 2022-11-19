package com.goodspartner.service;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

import java.util.List;

public interface RoutePointService {

    List<RoutePointDto> findByRouteId(int routeId);

    RoutePointActionResponse updateRoutePoint(long id, RoutePointAction action);
}
