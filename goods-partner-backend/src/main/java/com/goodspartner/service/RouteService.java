package com.goodspartner.service;

import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Order;
import com.goodspartner.response.RoutesCalculation;

import java.util.List;

public interface RouteService {
    List<RoutesCalculation.RouteDto> calculateRoutes(List<Order> orders, StoreDto storeDto);
}
