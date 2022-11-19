package com.goodspartner.service.impl;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.exception.RoutePointNotFoundException;
import com.goodspartner.mapper.RoutePointMapper;
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

import javax.transaction.Transactional;
import java.util.List;

import static com.goodspartner.entity.RoutePointStatus.PENDING;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRoutePointService implements RoutePointService {

    private final RoutePointRepository routePointRepository;
    private RouteService routeService;
    private RoutePointMapper routePointMapper;
    private final EventService eventService;

    @Override
    public List<RoutePointDto> findByRouteId(int routeId) {
        List<RoutePoint> routePoints = routePointRepository.findByRouteId(routeId);

        return routePointMapper.toDtos(routePoints);
    }

    @Override
    @Transactional
    public RoutePointActionResponse updateRoutePoint(long id, RoutePointAction action) {
        RoutePoint routePoint = routePointRepository.findById(id)
                .orElseThrow(() -> new RoutePointNotFoundException(id));

        action.perform(routePoint);

        Route route = routePoint.getRoute();
        Integer routeId = route.getId();

        List<RoutePointDto> routePointDtos = findByRouteId(routeId);

        eventService.publishRoutePointUpdated(routePoint, route);

        processRouteStatus(routePointDtos, routeId);

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

    private void processRouteStatus(List<RoutePointDto> routePointDtos, int routeId) {
        if (isAllRoutePointsDone(routePointDtos)) {
            routeService.updateRoute(routeId, RouteAction.COMPLETE);
            log.info("Route ID {} was automatically close due to all RoutePoints are completed", routeId);
        }
    }

    private boolean isAllRoutePointsDone(List<RoutePointDto> routePointDtos) {
        return routePointDtos.stream()
                .filter(routePointDto -> routePointDto.getStatus().equals(PENDING))
                .findFirst()
                .isEmpty();
    }
}
