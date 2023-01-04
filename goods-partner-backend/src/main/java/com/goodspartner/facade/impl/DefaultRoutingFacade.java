package com.goodspartner.facade.impl;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.facade.RoutingFacade;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.goodspartner.entity.RoutePointStatus.DONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRoutingFacade implements RoutingFacade {

    private final RouteService routeService;
    private final RoutePointService routePointService;
    private final EventService eventService;
    private final DeliveryService deliveryService;

    @Override
    public RouteActionResponse updateRoute(long routeId, RouteAction action) {

        Route route = routeService.updateRoute(routeId, action);

        eventService.publishRouteUpdated(route);
        if (RouteAction.START.equals(action)) {
            route.getRoutePoints().stream()
                    .filter(routePoint -> !routePoint.isMatchingExpectedDeliveryTime())
                    .findFirst()
                    .ifPresent(routePoint -> eventService.publishDeliveryTimeRangeWarning(route));
        }

        Delivery delivery = deliveryService.processDeliveryStatus(route);
        if (DeliveryStatus.COMPLETED.equals(delivery.getStatus())) {
            eventService.publishDeliveryCompleted(delivery);
        }

        return getRouteActionResponse(route, delivery);
    }

    @Override
    public RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action) {
        RoutePoint updatedRoutePoint = routePointService.updateRoutePoint(routePointId, action);

        Route route = routeService.findExtendedById(updatedRoutePoint.getRoute().getId());

        eventService.publishRoutePointUpdated(updatedRoutePoint, route);

        boolean allDone = route.getRoutePoints()
                .stream()
                .allMatch(pendingRoutePoint -> DONE.equals(pendingRoutePoint.getStatus()));

        if (allDone) { // No PENDING / SKIPPED point -> Route COMPLETE
            updateRoute(route.getId(), RouteAction.COMPLETE);
            log.info("Route ID {} was automatically close due to all RoutePoints are completed", route.getId());
        } else {
            List<RoutePoint> pendingRoutePoints = routePointService.actualizePendingRoutePoints(updatedRoutePoint, route);
            pendingRoutePoints.stream()
                    .filter(routePoint -> !routePoint.isMatchingExpectedDeliveryTime())
                    .findFirst()
                    .ifPresent(routePoint -> eventService.publishDeliveryTimeRangeWarning(route));
        }

        Delivery updatedDelivery = deliveryService.findById(route.getDelivery().getId());
        Route updatedRoute = routeService.findExtendedById(updatedRoutePoint.getRoute().getId());
        return getRoutePointActionResponse(updatedRoute, updatedRoutePoint, updatedDelivery);
    }

    // MapStruct?
    private RouteActionResponse getRouteActionResponse(Route route, Delivery delivery) {
        RouteActionResponse routeActionResponse = new RouteActionResponse();
        // Route
        routeActionResponse.setRouteId(route.getId());
        routeActionResponse.setRouteStatus(route.getStatus());
        routeActionResponse.setRouteFinishTime(route.getFinishTime());
        // Delivery
        routeActionResponse.setDeliveryId(delivery.getId());
        routeActionResponse.setDeliveryStatus(delivery.getStatus());
        return routeActionResponse;
    }

    private RoutePointActionResponse getRoutePointActionResponse(Route route, RoutePoint routePoint, Delivery delivery) {
        RoutePointActionResponse routePointActionResponse = new RoutePointActionResponse();
        // RoutePoint
        routePointActionResponse.setRoutePointId(routePoint.getId());
        routePointActionResponse.setRoutePointStatus(routePoint.getStatus());
        routePointActionResponse.setPointCompletedAt(routePoint.getCompletedAt());
        // Route
        routePointActionResponse.setRouteId(route.getId());
        routePointActionResponse.setRouteStatus(route.getStatus());
        routePointActionResponse.setRouteFinishTime(route.getFinishTime());
        // Delivery
        routePointActionResponse.setDeliveryId(delivery.getId());
        routePointActionResponse.setDeliveryStatus(delivery.getStatus());
        return routePointActionResponse;
    }
}
