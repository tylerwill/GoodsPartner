package com.goodspartner.service;

import com.goodspartner.action.RoutePointAction;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

public interface RoutePointService {
    RoutePointActionResponse updateRoutePoint(long id, RoutePointAction action);
}
