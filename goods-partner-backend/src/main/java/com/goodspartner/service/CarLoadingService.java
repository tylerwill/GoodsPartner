package com.goodspartner.service;

import com.goodspartner.dto.CarRouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;

import java.util.List;

public interface CarLoadingService {

    List<CarRouteDto> loadCars(StoreDto storeDto, List<RoutePointDto> routePoints);

}
