package com.goodspartner.service;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Order;

import java.util.List;

public interface RouteService {
    List<RouteDto> calculateRoutes(List<Order> orders, List<StoreDto> stores);
}
