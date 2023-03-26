package com.goodspartner.facade;

import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;

public interface RoutingFacade {

    RouteActionResponse startRoute(long routeId);

    RouteActionResponse completeRoute(long routeId);

    RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action);
}
