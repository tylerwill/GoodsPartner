package com.goodspartner.service;

import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;

import java.util.List;

public interface CalculateRouteService {

    List<Route> calculateRoutes(List<OrderExternal> orders, StoreDto storeDto);

}
