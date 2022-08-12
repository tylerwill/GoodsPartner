package com.goodspartner.service;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.web.controller.response.RoutesCalculation;

import java.time.LocalDate;
import java.util.List;

public interface RouteService {

    void add(RoutesCalculation.RouteDto routeDto);

    void update(int id, RoutesCalculation.RouteDto routeDto);

    List<RoutesCalculation.RouteDto> findAll();

    void updatePoint(int routeId, String routePointId, RoutePointDto routePoint);

    RoutesCalculation calculateRoutes(LocalDate date);

}

