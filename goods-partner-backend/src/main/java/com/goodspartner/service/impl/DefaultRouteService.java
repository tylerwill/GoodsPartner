package com.goodspartner.service.impl;

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
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.RoutePointRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.goodspartner.entity.DeliveryStatus.COMPLETED;
import static com.goodspartner.entity.RoutePointStatus.PENDING;

@AllArgsConstructor
@Service
@Slf4j
public class DefaultRouteService implements RouteService {

    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;
    private final DeliveryRepository deliveryRepository;
    private final DefaultRouteCalculationService routeCalculationService;
    private final EventService eventService;
    private final RouteMapper routeMapper;

    @Override
    public List<RouteDto> findByDeliveryId(UUID deliveryId) {
        List<Route> routes = routeRepository.findByDeliveryId(deliveryId);

        List<Integer> routeIds = routes.stream()
                .mapToInt(Route::getId)
                .boxed()
                .toList();

        List<RoutePoint> routePoints = routePointRepository.findByMultipleRouteId(routeIds);

        Map<Integer, List<RoutePoint>> routePointsMap = routePoints.stream()
                .collect(Collectors.groupingBy(routePoint -> routePoint.getRoute().getId()));

        routes.forEach(route -> route.setRoutePoints(routePointsMap.get(route.getId())));

        return routeMapper.toDtos(routes);
    }

    @Override
    @Transactional
    public RouteActionResponse updateRoute(int routeId, RouteAction action) {

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
    public void reorderRoutePoints(int id, LinkedList<RoutePointDto> routePointDtos) {

        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));

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

    @Override
    public List<RouteDto> findRoutesByDeliveryAndCar(Delivery delivery, Car car) {
        return routeRepository.findByDeliveryAndCar(delivery, car)
                .stream()
                .map(routeMapper::mapToDto)
                .toList();
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
