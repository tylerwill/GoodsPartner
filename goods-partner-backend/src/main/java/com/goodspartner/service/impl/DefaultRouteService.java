package com.goodspartner.service.impl;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
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
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class DefaultRouteService implements RouteService {

    private final RoutePointMapper routePointMapper;
    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional
    public void update(int id, RouteDto routeDto) {
        Route updateRoute = routeRepository.findById(id)
                .map(route -> routeMapper.update(route, routeDto))
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        processDeliveryStatus(updateRoute);

        routeRepository.save(updateRoute);
    }

    @Override
    @Transactional
    public void updatePoint(int routeId, String routePointId, RoutePoint routePoint) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        List<RoutePoint> routePoints = route.getRoutePoints();
        routePoints.forEach(point -> {
            if (point.getId().toString().equals(routePointId)) {
                routePointMapper.update(point, routePoint);
            }
        });

        route.setRoutePoints(routePoints);

        if (isAllRoutePointsDone(routePoints)) {
            route.setStatus(RouteStatus.COMPLETED);
            route.setFinishTime(LocalDateTime.now());
            log.info("Route ID {} was automatically close due to all RoutePoints are DONE", route.getId());

            processDeliveryStatus(route);
        }

        routeRepository.save(route);
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

            deliveryRepository.save(delivery);
            log.info("Delivery ID {} was automatically close due to all Routes are COMPLETED", route.getId());
        }
    }
}
