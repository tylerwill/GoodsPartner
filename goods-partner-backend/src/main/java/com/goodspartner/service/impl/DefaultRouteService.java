package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Route;
import com.goodspartner.exceptions.RouteNotFoundException;
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@AllArgsConstructor
@Service
public class DefaultRouteService implements RouteService {

    private final RoutePointMapper routePointMapper;
    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;

    @Override
    @Transactional
    public void update(int id, RouteDto routeDto) {
        Route updateRoute = routeRepository.findById(id)
                .map(route -> routeMapper.update(route, routeDto))
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));
        routeRepository.save(updateRoute);
    }

    @Override
    @Transactional
    public void updatePoint(int routeId, String routePointId, RoutePoint routePoint) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        List<RoutePoint> routePointList = route.getRoutePoints().stream()
                .filter(routePointDto -> routePointDto.getId().toString().equals(routePointId))
                .map(routePointDto -> routePointMapper.update(routePointDto, routePoint)).toList();

        route.setRoutePoints(routePointList);
        routeRepository.save(route);
    }

}
