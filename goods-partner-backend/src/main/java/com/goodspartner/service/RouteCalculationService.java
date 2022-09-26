package com.goodspartner.service;

import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.service.dto.RouteMode;

import java.util.List;

public interface RouteCalculationService {

    List<Route> calculateRoutes(List<OrderExternal> orders, RouteMode coolerRequired);

}
