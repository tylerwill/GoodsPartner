package com.goodspartner.service.impl;

import com.goodspartner.dto.Coordinates;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.DistanceOutOfLimitException;
import com.goodspartner.exception.IllegalRouteStateException;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import com.graphhopper.util.DistanceCalcEarth;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.goodspartner.entity.RoutePointStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRoutePointService implements RoutePointService {
    private static final double DISTANCE_LIMIT = 500.00;
    private final RoutePointRepository routePointRepository;
    private final EventService eventService;
    private final RouteService routeService;

    @Transactional(readOnly = true)
    @Override
    public List<RoutePoint> findByRouteId(long routeId) {
        return routePointRepository.findByRouteId(routeId);
    }

    @Override
    @Transactional
    public RoutePointActionResponse updateRoutePoint(long routePointId, RoutePointAction action) {
        RoutePoint routePoint = routePointRepository.findById(routePointId)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));

        Route route = routePoint.getRoute();
        if (!RouteStatus.INPROGRESS.equals(route.getStatus())) {
            throw new IllegalRouteStateException(RouteStatus.INPROGRESS.getStatus(), "update route point");
        }

        action.perform(routePoint);

        eventService.publishRoutePointUpdated(routePoint, route);
        processRouteStatus(route);
        return getRoutePointActionResponse(route, routePoint);
    }

    @Override
    public List<OrderExternal> getRoutePointOrders(long routePointId) {
        return routePointRepository.findByIdWithOrders(routePointId)
                .map(RoutePoint::getOrders)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
    }

    @Transactional
    @Override
    public void updateCoordinates(long routePointId, Coordinates coordinates) {
        RoutePoint routePoint = routePointRepository.findByRoutePointId(routePointId)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));

        AddressExternal addressExternal = routePoint.getAddressExternal();
        validateCoordinatesDistance(addressExternal.getLatitude(), addressExternal.getLongitude(), coordinates);
        addressExternal.setLongitude(coordinates.getLongitude());
        addressExternal.setLatitude(coordinates.getLatitude());

        routePointRepository.save(routePoint);
        eventService.publishCoordinatesUpdated(routePoint, addressExternal);
    }

    private void validateCoordinatesDistance(double fromLatitude, double fromLongitude, Coordinates coordinates) {
        DistanceCalcEarth distanceCalc = new DistanceCalcEarth();
        double distance = distanceCalc.calcDist(fromLatitude, fromLongitude, coordinates.getLatitude(), coordinates.getLongitude());

        if (distance > DISTANCE_LIMIT) {
            throw new DistanceOutOfLimitException(distance);
        }
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
        List<RoutePoint> routePointDtos = findByRouteId(route.getId());
        if (isAllRoutePointsProcessed(routePointDtos)) {
            routeService.updateRoute(route.getId(), RouteAction.COMPLETE);
            log.info("Route ID {} was automatically close due to all RoutePoints are completed", route.getId());
        }
    }

    private boolean isAllRoutePointsProcessed(List<RoutePoint> routePointDtos) {
        return routePointDtos.stream()
                .filter(routePoint -> PENDING.equals(routePoint.getStatus()))
                .findFirst()
                .isEmpty();
    }
}
