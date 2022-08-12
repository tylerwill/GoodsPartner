package com.goodspartner.service;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.StoreDto;

import com.goodspartner.web.controller.response.RoutesCalculation;

import java.util.List;

public interface CalculateRouteService {
    List<RoutesCalculation.RouteDto> calculateRoutes(List<OrderDto> orders, StoreDto storeDto);
}
