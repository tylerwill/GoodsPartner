package com.goodspartner.service;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.web.controller.response.RoutesCalculation;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    void add(RoutesCalculation.RouteDto routeDto);

    void update(int id, RoutesCalculation.RouteDto routeDto);

    List<RoutesCalculation.RouteDto> findAll();

    void updatePoint(int routeId, UUID routePointID, RoutePointDto routePoint);
}

