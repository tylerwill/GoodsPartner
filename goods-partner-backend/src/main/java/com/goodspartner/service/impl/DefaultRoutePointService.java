package com.goodspartner.service.impl;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.goodspartner.entity.RoutePointStatus.PENDING;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRoutePointService implements RoutePointService {

    private final RoutePointRepository routePointRepository;
    private final EventService eventService;
    private RouteService routeService;

    @Transactional(readOnly = true)
    @Override
    public List<RoutePoint> findByRouteId(int routeId) {
        return routePointRepository.findByRouteId(routeId);
    }

    @Override
    @Transactional
    public RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action) {
        RoutePoint routePoint = routePointRepository.findById(routePointId)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));

        action.perform(routePoint);

        Route route = routePoint.getRoute();
        Integer routeId = route.getId();

        eventService.publishRoutePointUpdated(routePoint, route);

        processRouteStatus(routeId);

        return getRoutePointActionResponse(route, routePoint);
    }

    private RoutePointActionResponse getRoutePointActionResponse(Route route, RoutePoint routePoint) {
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
        Delivery delivery = route.getDelivery();
        routePointActionResponse.setDeliveryId(delivery.getId());
        routePointActionResponse.setDeliveryStatus(delivery.getStatus());
        return routePointActionResponse;
    }

    private void processRouteStatus(int routeId) {
        List<RoutePoint> routePointDtos = findByRouteId(routeId);
        if (isAllRoutePointsDone(routePointDtos)) {
            routeService.updateRoute(routeId, RouteAction.COMPLETE);
            log.info("Route ID {} was automatically close due to all RoutePoints are completed", routeId);
        }
    }

    private boolean isAllRoutePointsDone(List<RoutePoint> routePointDtos) {
        return routePointDtos.stream()
                .filter(routePoint -> PENDING.equals(routePoint.getStatus()))
                .findFirst()
                .isEmpty();
    }
}
