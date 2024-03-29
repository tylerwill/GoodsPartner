package com.goodspartner.service.impl;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.User;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.RouteNotFoundException;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForRouteReordering;
import com.goodspartner.exception.route.IllegalRoutePointStatusForReordering;
import com.goodspartner.exception.route.IllegalRouteStatusForReordering;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.GraphhopperService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.UserService;
import com.goodspartner.web.action.RouteAction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryStatus.COMPLETED;
import static com.goodspartner.entity.RoutePointStatus.PENDING;
import static com.goodspartner.entity.User.UserRole.DRIVER;

@AllArgsConstructor
@Service
@Slf4j
public class DefaultRouteService implements RouteService {

    private static final Sort DEFAULT_ROUTE_SORT = Sort.by(Sort.Direction.ASC, "id");

    private final RouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;
    private final UserService userService;
    private final DefaultRouteCalculationService routeCalculationService;
    private final GraphhopperService graphhopperService;

    @Transactional(readOnly = true)
    @Override
    public List<Route> findRelatedRoutesByDeliveryId(UUID deliveryId) {
        return Optional.of(userService.findByAuthentication())
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(driver -> findByDeliveryAndDriver(deliveryId, driver))
                .orElseGet(() -> findByDeliveryIdExtended(deliveryId));
    }

    private List<Route> findByDeliveryAndDriver(UUID deliveryId, User driver) {
        return carRepository.findCarByDriver(driver)
                .map(car -> routeRepository.findByDeliveryIdAndCar(deliveryId, car, DEFAULT_ROUTE_SORT))
                .orElseThrow(() -> new CarNotFoundException(driver));
    }

    @Override
    public List<Route> findByDeliveryIdExtended(UUID deliveryId) {
        return routeRepository.findByDeliveryIdExtended(deliveryId, DEFAULT_ROUTE_SORT);
    }

    @Override
    public List<Route> findByDeliveryId(UUID deliveryId) {
        return routeRepository.findByDeliveryId(deliveryId);
    }

    @Override
    @Transactional
    public Route updateRoute(long routeId, RouteAction action) {

        Route route = routeRepository.findExtendedById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        action.perform(route);

        if (RouteAction.START.equals(action)) {
            graphhopperService.routePointTimeActualize(route.getStore(), route.getRoutePoints());
        }

        routeRepository.save(route);

        return route;
    }

    @Override
    public void reorderRoutePoints(long routeId, LinkedList<RoutePointDto> routePointDtos) {

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        UUID deliveryId = route.getDelivery().getId();

        validateDelivery(deliveryId);

        validateRoute(route, deliveryId);

        if (isAllRoutePointsPending(routePointDtos)) {
            Route reorderedRoute = routeCalculationService.recalculateRoute(route, routePointDtos);
            routeRepository.save(reorderedRoute);
        } else {
            throw new IllegalRoutePointStatusForReordering();
        }
    }

    @Override
    public Route findExtendedById(Long routeId) {
        return routeRepository.findExtendedById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));
    }

    private boolean isAllRoutePointsPending(List<RoutePointDto> routePointDtos) {
        return routePointDtos.stream()
                .allMatch(routePoint -> routePoint.getStatus().equals(PENDING));
    }

    private void validateDelivery(UUID deliveryId) {
        Delivery savedDelivery = deliveryRepository.findById(deliveryId).
                orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        if (savedDelivery.getStatus().equals(COMPLETED)) {
            throw new IllegalDeliveryStateForRouteReordering();
        }
    }

    private void validateRoute(Route route, UUID deliveryId) {
        if (!(route.getDelivery().getId().equals(deliveryId))) {
            throw new RouteNotFoundException(deliveryId);
        }
        RouteStatus routeStatus = route.getStatus();
        if (routeStatus.equals(RouteStatus.COMPLETED)) {
            throw new IllegalRouteStatusForReordering();
        }
    }
}
