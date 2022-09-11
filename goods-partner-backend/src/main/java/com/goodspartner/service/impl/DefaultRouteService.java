package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Route;
import com.goodspartner.exceptions.RouteNotFoundException;
import com.goodspartner.mapper.CarDetailsMapper;
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.mapper.RoutePointMapper;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.OrderService;
import com.goodspartner.service.OrderValidationService;
import com.goodspartner.service.RouteService;
import com.goodspartner.service.StoreService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class DefaultRouteService implements RouteService {

    // Mappers
    private final CarDetailsMapper carDetailsMapper;
    private final RoutePointMapper routePointMapper;
    private final RouteMapper routeMapper;
    // Services
    private final CalculateRouteService calculateRouteService;
    private final RouteRepository routeRepository;
    private final OrderService orderService;

    private final StoreService storeFactory;

    private final OrderValidationService orderValidationService;


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

    // TODO fix me
    @Override
    @Transactional
    public void updatePoint(int routeId, String routePointId, RoutePointDto routePoint) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        List<RoutePointDto> routePointDtoList = route.getRoutePoints().stream()
                .filter(routePointDto -> routePointDto.getId().toString().equals(routePointId))
                .map(routePointDto -> routePointMapper.update(routePointDto, routePoint)).toList();

        route.setRoutePoints(routePointDtoList);
        routeRepository.save(route);
    }

    /**
     * TODO:
     * Order.addresses post processing and validation required. We should at least:
     * - trim the spaces and various trailing symbols
     * - trim the phone number by regexp, either 8 digit or with dash delimiters
     * - try to trim whatever in brackets, e.g: (Dream town) etc
     */
    @Override
    @Transactional
    public RoutesCalculation calculateRoutes(LocalDate date) {

        List<OrderDto> orders = orderService.findAllByShippingDate(date);
        // TODO: do we need to again validate the orders when we build the Route
        orderValidationService.enrichValidAddress(orders);

        StoreDto store = storeFactory.getMainStore();
        List<RoutesCalculation.RouteDto> routes = calculateRouteService.calculateRoutes(orders, store);

        List<RoutesCalculation.CarLoadDto> carsDetailsList = carDetailsMapper.map(routes, orders);

        RoutesCalculation routesCalculation = new RoutesCalculation();
        routesCalculation.setDate(date);
        routesCalculation.setRoutes(routes);
        routesCalculation.setCarLoadDetails(carsDetailsList);

        return routesCalculation;
    }

}
