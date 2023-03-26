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
import static com.goodspartner.event.ActionType.DELIVERY_UPDATED;
import static com.goodspartner.event.ActionType.ROUTE_UPDATED;
import static com.goodspartner.event.EventMessageTemplate.DELIVERY_COMPLETED;
import static com.goodspartner.event.EventMessageTemplate.ROUTE_POINT_STATUS;
import static com.goodspartner.event.EventMessageTemplate.ROUTE_POINT_TIME_RANGE_WARNING;
import static com.goodspartner.event.EventMessageTemplate.ROUTE_START;
import static com.goodspartner.event.EventMessageTemplate.ROUTE_STATUS;
import static com.goodspartner.event.EventType.INFO;
import static com.goodspartner.event.EventType.WARNING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRoutingFacade implements RoutingFacade {

    private final RouteService routeService;
    private final RoutePointService routePointService;
    private final EventService eventService;
    private final DeliveryService deliveryService;

    @Override
    public RouteActionResponse startRoute(long routeId) {
        Route route = routeService.updateRoute(routeId, RouteAction.START);
        eventService.publishForDriverAndLogist(ROUTE_START.withRouteValues(route), INFO, ROUTE_UPDATED, route);

        route.getRoutePoints().stream()
                .filter(routePoint -> !routePoint.isMatchingExpectedDeliveryTime())
                .findFirst()
                .ifPresent(routePoint ->
                        eventService.publishForDriverAndLogist(ROUTE_POINT_TIME_RANGE_WARNING.withRouteValues(route), WARNING, ROUTE_UPDATED, route));

        return getRouteActionResponse(route, route.getDelivery());
    }

    @Override
    public RouteActionResponse completeRoute(long routeId) {
        Route route = routeService.updateRoute(routeId, RouteAction.COMPLETE);
        eventService.publishForDriverAndLogist(ROUTE_STATUS.withRouteValues(route), INFO, ROUTE_UPDATED, route);

        Delivery delivery = deliveryService.processDeliveryStatus(route);
        if (DeliveryStatus.COMPLETED.equals(delivery.getStatus())) {
            eventService.publishForLogist(DELIVERY_COMPLETED.getTemplate(), INFO, DELIVERY_UPDATED, delivery.getId());
        }

        return getRouteActionResponse(route, delivery);
    }

    @Override
    public RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action) {
        RoutePoint updatedRoutePoint = routePointService.updateRoutePoint(routePointId, action);

        Route route = routeService.findExtendedById(updatedRoutePoint.getRoute().getId());

        eventService.publishForDriverAndLogist(ROUTE_POINT_STATUS.withRoutePointValues(updatedRoutePoint), INFO, ROUTE_UPDATED, route);

        boolean allDone = route.getRoutePoints()
                .stream()
                .allMatch(pendingRoutePoint -> DONE.equals(pendingRoutePoint.getStatus()));

        if (allDone) { // No PENDING / SKIPPED point -> Route COMPLETE
            completeRoute(route.getId());
            log.info("Route ID {} was automatically close due to all RoutePoints are completed", route.getId());
        } else {
            List<RoutePoint> pendingRoutePoints = routePointService.actualizePendingRoutePoints(updatedRoutePoint, route);
            pendingRoutePoints.stream()
                    .filter(routePoint -> !routePoint.isMatchingExpectedDeliveryTime())
                    .findFirst()
                    .ifPresent(routePoint -> eventService.publishForDriverAndLogist(ROUTE_POINT_TIME_RANGE_WARNING.withRouteValues(route), WARNING, ROUTE_UPDATED, route));
        }

        Route updatedRoute = routeService.findExtendedById(updatedRoutePoint.getRoute().getId());
        return getRoutePointActionResponse(updatedRoute, updatedRoutePoint, updatedRoute.getDelivery());
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
