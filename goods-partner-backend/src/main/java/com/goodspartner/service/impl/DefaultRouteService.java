package com.goodspartner.service.impl;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.User;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.IllegalRoutePointStatusForOperation;
import com.goodspartner.exception.IllegalRouteStatusForOperation;
import com.goodspartner.exception.RouteNotFoundException;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.UserService;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    private final RouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;

    private final UserService userService;
    private final DefaultRouteCalculationService routeCalculationService;
    private final EventService eventService;

    @Transactional(readOnly = true)
    @Override
    public List<Route> findRelatedRoutesByDeliveryId(UUID deliveryId, OAuth2AuthenticationToken authentication) {
        return Optional.of(userService.findByAuthentication(authentication))
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(driver -> findByDeliveryAndDriver(deliveryId, driver))
                .orElseGet(() -> findByDeliveryIdExtended(deliveryId));
    }

    private List<Route> findByDeliveryAndDriver(UUID deliveryId, User driver) {
        Car car = carRepository.findCarByDriver(driver);
        return routeRepository.findByDeliveryIdAndCar(deliveryId, car);
    }

    @Override
    public List<Route> findByDeliveryIdExtended(UUID deliveryId) {
        return routeRepository.findByDeliveryIdExtended(deliveryId);
    }

    @Override
    public List<Route> findByDeliveryId(UUID deliveryId) {
        return routeRepository.findByDeliveryId(deliveryId);
    }

    @Override
    @Transactional
    public RouteActionResponse updateRoute(long routeId, RouteAction action) {

        Route route = routeRepository.findById(routeId) // TODO fetch with delivery?
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        action.perform(route);

        routeRepository.save(route);

        eventService.publishRouteUpdated(route);

        processDeliveryStatus(route);

        return getRouteActionResponse(route);
    }

    // MapStruct?
    private RouteActionResponse getRouteActionResponse(Route route) {
        RouteActionResponse routeActionResponse = new RouteActionResponse();

        // Route
        routeActionResponse.setRouteId(route.getId());
        routeActionResponse.setRouteStatus(route.getStatus());
        routeActionResponse.setRouteFinishTime(route.getFinishTime());

        // Delivery
        Delivery delivery = route.getDelivery();
        routeActionResponse.setDeliveryId(delivery.getId());
        routeActionResponse.setDeliveryStatus(delivery.getStatus());
        return routeActionResponse;
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
            throw new IllegalRoutePointStatusForOperation(routePointDtos, "reorder");
        }
    }

    private boolean isAllRoutesCompleted(Delivery delivery) {
        return delivery.getRoutes().stream()
                .filter(route -> !route.getStatus().equals(RouteStatus.COMPLETED))
                .findFirst()
                .isEmpty();
    }


    private void processDeliveryStatus(Route route) {
        Delivery delivery = route.getDelivery();
        if (isAllRoutesCompleted(delivery)) {
            delivery.setStatus(DeliveryStatus.COMPLETED);

            eventService.publishDeliveryCompleted(delivery);

            deliveryRepository.save(delivery);
            log.info("Delivery ID {} was automatically close due to all Routes are COMPLETED", route.getId());
        }
    }

    private boolean isAllRoutePointsPending(List<RoutePointDto> routePointDtos) {
        return routePointDtos.stream()
                .allMatch(routePoint -> routePoint.getStatus().equals(PENDING));
    }

    private void validateDelivery(UUID deliveryId) {
        Delivery savedDelivery = deliveryRepository.findById(deliveryId).
                orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        if (savedDelivery.getStatus().equals(COMPLETED)) {
            throw new IllegalDeliveryStatusForOperation(savedDelivery, "reorder route for");
        }
    }

    private void validateRoute(Route route, UUID deliveryId) {
        if (!(route.getDelivery().getId().equals(deliveryId))) {
            throw new RouteNotFoundException(deliveryId);
        }
        RouteStatus routeStatus = route.getStatus();
        if (routeStatus.equals(RouteStatus.COMPLETED) ||
                routeStatus.equals(RouteStatus.INPROGRESS)) {
            throw new IllegalRouteStatusForOperation(route, "reorder");
        }
    }
}
