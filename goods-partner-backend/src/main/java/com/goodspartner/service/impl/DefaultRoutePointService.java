package com.goodspartner.service.impl;

import com.goodspartner.dto.Coordinates;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.AddressExternalNotFoundException;
import com.goodspartner.exception.DistanceOutOfLimitException;
import com.goodspartner.exception.IllegalRouteStateException;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.web.action.RoutePointAction;
import com.graphhopper.util.DistanceCalcEarth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.goodspartner.entity.RoutePointStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRoutePointService implements RoutePointService {
    private static final double DISTANCE_LIMIT = 500.00;
    private final RoutePointRepository routePointRepository;
    private final AddressExternalRepository addressExternalRepository;

    private final GraphhopperService graphhopperService;
    private final EventService eventService;

    @Override
    @Transactional
    public RoutePoint updateRoutePoint(long routePointId, RoutePointAction action) {
        RoutePoint routePoint = routePointRepository.findByRoutePointId(routePointId)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));

        Route route = routePoint.getRoute();
        if (!RouteStatus.INPROGRESS.equals(route.getStatus())) {
            throw new IllegalRouteStateException(RouteStatus.INPROGRESS.getStatus(), "update route point");
        }

        action.perform(routePoint);

        return routePoint;
    }

    @Override
    public List<OrderExternal> getRoutePointOrders(long routePointId) {
        return routePointRepository.findByIdWithOrders(routePointId)
                .map(RoutePoint::getOrders)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
    }

    /**
     * Original order.mappoint is not modified and it is expected
     */
    @Transactional
    @Override
    public void updateCoordinates(long routePointId, Coordinates coordinates) {
        // Updating specified routePoint
        RoutePoint routePoint = routePointRepository.findByRoutePointId(routePointId)
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));
        MapPoint mapPoint = routePoint.getMapPoint();
        validateCoordinatesDistance(mapPoint, coordinates);
        mapPoint.setLongitude(coordinates.getLongitude());
        mapPoint.setLatitude(coordinates.getLatitude());

        // Updating respective address
        OrderAddressId orderAddressId = OrderAddressId.builder()
                .orderAddress(routePoint.getAddress())
                .clientName(routePoint.getClientName())
                .build();

        AddressExternal addressExternal = addressExternalRepository.findById(orderAddressId)
                .orElseThrow(() -> new AddressExternalNotFoundException(orderAddressId));
        addressExternal.setLatitude(coordinates.getLatitude());
        addressExternal.setLongitude(coordinates.getLongitude());

        eventService.publishCoordinatesUpdated(routePoint);
    }

    private void validateCoordinatesDistance(MapPoint mapPoint, Coordinates coordinates) {
        DistanceCalcEarth distanceCalc = new DistanceCalcEarth();
        double distance = distanceCalc.calcDist(
                mapPoint.getLatitude(), mapPoint.getLongitude(),
                coordinates.getLatitude(), coordinates.getLongitude());

        if (distance > DISTANCE_LIMIT) {
            throw new DistanceOutOfLimitException(distance);
        }
    }


    @Override
    @Transactional
    public List<RoutePoint> actualizePendingRoutePoints(RoutePoint current, Route route) {
        List<RoutePoint> pendingRoutePoints = routePointRepository.findByRouteId(route.getId())
                .stream()
                .filter(routePoint -> PENDING.equals(routePoint.getStatus()))
                .collect(Collectors.toList());

        graphhopperService.routePointTimeActualize(current.getMapPoint(), pendingRoutePoints);

        return routePointRepository.saveAll(pendingRoutePoints);
    }
}
