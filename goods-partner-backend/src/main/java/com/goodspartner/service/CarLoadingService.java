package com.goodspartner.service;

import com.goodspartner.dto.CarRouteComposition;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.dto.StoreDto;

import java.util.List;

public interface CarLoadingService {

    List<CarRouteComposition> loadCars(StoreDto storeDto, List<RoutePoint> routePoints);

}
