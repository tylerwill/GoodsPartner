package com.goodspartner.service.impl;


import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.RouteNotFoundException;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.goodspartner.entity.RoutePointStatus.PENDING;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRoutePointService implements RoutePointService {

    private final RoutePointRepository routePointRepository;
    private final RouteRepository routeRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public RoutePointActionResponse updateRoutePoint(long id, RoutePointAction action) {
        RoutePoint routePoint = routePointRepository.findById(id)
                .orElseThrow(() -> new RoutePointNotFoundException(id));

        action.perform(routePoint);

        Route route = routeRepository.findById(routePoint.getRoute().getId())
                .orElseThrow(() -> new RouteNotFoundException(routePoint.getRoute().getId()));

        processRouteStatus(route);

        //TODO complete logic for autocomplete delivery from routePoint
//        processDeliveryStatus(route);

        routeRepository.save(route);

        eventService.publishRoutePointUpdated(routePoint, route);

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

    private void processRouteStatus(Route route) {
        if (isAllRoutePointsDone(route.getRoutePoints())) {
            route.setStatus(RouteStatus.COMPLETED);
            route.setFinishTime(LocalDateTime.now());

            eventService.publishRouteStatusChangeAuto(route);

            log.info("Route ID {} was automatically close due to all RoutePoints are completed", route.getId());
        }
    }

    private boolean isAllRoutePointsDone(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .filter(routePoint -> routePoint.getStatus().equals(PENDING))
                .findFirst()
                .isEmpty();
    }
}
