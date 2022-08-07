package com.goodspartner.service;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Order;
import com.goodspartner.factory.Store;

import java.util.List;

public interface RouteService {
    List<RouteDto> calculateRoutes(List<Order> orders, Store store);
}
