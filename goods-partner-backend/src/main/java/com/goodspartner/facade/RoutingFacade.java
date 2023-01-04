package com.goodspartner.facade;

import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

public interface RoutingFacade {

    RouteActionResponse updateRoute(long routeId, RouteAction action);

    RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action);
}
