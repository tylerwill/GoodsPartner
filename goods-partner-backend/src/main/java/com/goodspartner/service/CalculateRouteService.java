package com.goodspartner.service;

import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Order;
import com.goodspartner.web.controller.response.RoutesCalculation;

import java.util.List;

public interface CalculateRouteService {
    List<RoutesCalculation.RouteDto> calculateRoutes(List<Order> orders, StoreDto storeDto);
}
