package com.goodspartner.service;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.RoutePoint;

public interface RouteService {

    void update(int id, RouteDto routeDto);

    void updatePoint(int routeId, String routePointId, RoutePoint routePoint);

}

