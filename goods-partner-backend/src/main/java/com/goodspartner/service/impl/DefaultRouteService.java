package com.goodspartner.service.impl;

import com.goodspartner.action.RouteAction;
import com.goodspartner.action.RoutePointAction;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.IllegalRoutePointStatusForOperation;
import com.goodspartner.exception.IllegalRouteStatusForOperation;
import com.goodspartner.exception.RouteNotFoundException;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryStatus.COMPLETED;
import static com.goodspartner.entity.RoutePointStatus.PENDING;

@AllArgsConstructor
@Service
@Slf4j
public class DefaultRouteService implements RouteService {

    private final RouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;
    private final DefaultRouteCalculationService routeCalculationService;
    private final EventService eventService;
    private final RouteMapper routeMapper;

    @Override
    @Transactional
    public RouteActionResponse update(int routeId, RouteAction action) {

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        action.perform(route);

        processDeliveryStatus(route);

        routeRepository.save(route);

        eventService.publishRouteUpdated(route);

        return getRouteActionResponse(route);
    }

    private RouteActionResponse getRouteActionResponse(Route route) {
        RouteActionResponse routePointActionResponse = new RouteActionResponse();

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

    @Override
    @Transactional
    public RoutePointActionResponse updatePoint(int routeId, long routePointId, RoutePointAction action) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        List<RoutePoint> routePoints = route.getRoutePoints();

        RoutePoint routePoint = routePoints.stream()
                .filter(r -> r.getId() == routePointId)
                .findFirst()
                .orElseThrow(() -> new RoutePointNotFoundException(routePointId));

        action.perform(routePoint);

        processRouteStatus(route, routePoints);

        processDeliveryStatus(route);

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

    private void processRouteStatus(Route route, List<RoutePoint> routePoints) {
        if (isAllRoutePointsDone(routePoints)) {
            route.setStatus(RouteStatus.COMPLETED);
            route.setFinishTime(LocalDateTime.now());

            eventService.publishRouteStatusChangeAuto(route);

            log.info("Route ID {} was automatically close due to all RoutePoints are completed", route.getId());
        }
    }

    @Override
    public void reorderRoutePoints(UUID deliveryId, int routeId, LinkedList<RoutePointDto> routePointDtos) {

        validateDelivery(deliveryId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        validateRoute(route, deliveryId);

        if (isAllRoutePointsPending(routePointDtos)) {
            Route reorderedRoute = routeCalculationService.recalculateRoute(route, routePointDtos);
            routeRepository.save(reorderedRoute);
        } else {
            throw new IllegalRoutePointStatusForOperation(routePointDtos, "reorder");
        }
    }

    @Override
    public List<RouteDto> findRoutesByDeliveryAndCar(Delivery delivery, Car car) {
        return routeRepository.findByDeliveryAndCar(delivery, car)
                .stream()
                .map(routeMapper::routeToRouteDto)
                .toList();
    }

    private boolean isAllRoutePointsDone(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .filter(routePoint -> routePoint.getStatus().equals(PENDING))
                .findFirst()
                .isEmpty();
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
        if (!route.getDelivery().getId().equals(deliveryId)) {
            throw new RouteNotFoundException(deliveryId);
        }
        RouteStatus routeStatus = route.getStatus();
        if (routeStatus.equals(RouteStatus.COMPLETED) ||
                routeStatus.equals(RouteStatus.INPROGRESS)) {
            throw new IllegalRouteStatusForOperation(route, "reorder");
        }
    }
}
