package com.goodspartner.service.impl;

import com.goodspartner.dto.RouteDto;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.exceptions.IllegalDeliveryStatusForOperation;
import com.goodspartner.exceptions.IllegalRoutePointStatusForOperation;
import com.goodspartner.exceptions.IllegalRouteStatusForOperation;
import com.goodspartner.exceptions.RouteNotFoundException;
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.RouteService;
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

    private final RoutePointMapper routePointMapper;
    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;
    private final DefaultRouteCalculationService routeCalculationService;
    private final DefaultDeliveryHistoryService deliveryHistoryService;

    @Override
    @Transactional
    public void update(int id, RouteDto routeDto) {
        RouteStatus oldRouteStatus = routeRepository.findById(id).map(Route::getStatus)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        Route oldRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        Route updateRoute = routeMapper.update(oldRoute, routeDto);

        deliveryHistoryService.publishIfRouteUpdated(routeDto, oldRouteStatus, updateRoute);

        processDeliveryStatus(updateRoute);
        routeRepository.save(updateRoute);
    }

    @Override
    @Transactional
    public void updatePoint(int routeId, String routePointId, RoutePoint routePoint) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        List<RoutePoint> routePoints = route.getRoutePoints();

        RoutePoint point = routePoints.stream()
                .filter(r -> r.getId().toString().equals(routePointId))
                .findFirst().orElse(null);
        if(point != null){
            RoutePointStatus oldRoutePointStatus = point.getStatus();
            routePointMapper.update(point, routePoint);

            deliveryHistoryService.publishIfPointUpdated(routePoint, oldRoutePointStatus, route);
        }

        route.setRoutePoints(routePoints);

        if (isAllRoutePointsDone(routePoints)) {
            route.setStatus(RouteStatus.COMPLETED);
            route.setFinishTime(LocalDateTime.now());

            deliveryHistoryService.publishRouteStatusChangeAuto(RouteStatus.COMPLETED, route);

            log.info("Route ID {} was automatically close due to all RoutePoints are DONE", route.getId());

            processDeliveryStatus(route);
        }

        routeRepository.save(route);
    }

    @Override
    public void reorderRoutePoints(UUID deliveryId, int routeId, LinkedList<RoutePoint> routePoints) {

        validateDelivery(deliveryId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        validateRoute(route, deliveryId);

        if (isAllRoutePointsPending(routePoints)) {
            Route reorderedRoute = routeCalculationService.recalculateRoute(route, routePoints);
            routeRepository.save(reorderedRoute);
        } else {
            throw new IllegalRoutePointStatusForOperation(routePoints, "reorder");
        }
    }

    private boolean isAllRoutePointsDone(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .filter(routePointDto -> !routePointDto.getStatus().equals(RoutePointStatus.DONE))
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

            deliveryHistoryService.publishDeliveryCompleted(delivery);

            deliveryRepository.save(delivery);
            log.info("Delivery ID {} was automatically close due to all Routes are COMPLETED", route.getId());
        }
    }

    private boolean isAllRoutePointsPending(List<RoutePoint> routePoints) {
        return routePoints.stream()
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
                routeStatus.equals(RouteStatus.INPROGRESS) ||
                routeStatus.equals(RouteStatus.INCOMPLETE)) {
            throw new IllegalRouteStatusForOperation(route, "reorder");
        }
    }
}
