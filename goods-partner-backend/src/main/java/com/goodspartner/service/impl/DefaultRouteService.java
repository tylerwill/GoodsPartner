package com.goodspartner.service.impl;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.Route;
import com.goodspartner.exceptions.RouteNotFoundException;
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class DefaultRouteService implements RouteService {
    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;
    private final RoutePointMapper routePointMapper;

    @Override
    @Transactional
    public void add(RoutesCalculation.RouteDto routeDto) {
        Route route = routeMapper.routeDtoToRoute(routeDto);
        routeRepository.save(route);
    }

    @Override
    @Transactional
    public void update(int id, RoutesCalculation.RouteDto routeDto) {
        Route updateRoute = routeRepository.findById(id).map(route -> routeMapper.update(route, routeDto))
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));
        routeRepository.save(updateRoute);
    }

    @Override
    public List<RoutesCalculation.RouteDto> findAll() {
        return routeMapper.routesToRouteDtos(routeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public void updatePoint(int routeId, String routePointId, RoutePointDto routePoint) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        List<RoutePointDto> routePointDtoList = route.getRoutePoints().stream()
                .filter(routePointDto -> routePointDto.getId().toString().equals(routePointId))
                .map(routePointDto -> routePointMapper.update(routePointDto, routePoint)).toList();

        route.setRoutePoints(routePointDtoList);
        routeRepository.save(route);
    }
}
