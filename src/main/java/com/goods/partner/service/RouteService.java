package com.goods.partner.service;

import com.goods.partner.dto.RouteDto;
import com.goods.partner.dto.StoreDto;
import com.goods.partner.entity.Order;

import java.util.List;

public interface RouteService {
    List<RouteDto> calculateRoutes(List<Order> orders, List<StoreDto> stores);
}
