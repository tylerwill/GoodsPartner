package com.goodspartner.service;

import com.goodspartner.dto.CarLoadDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;

import java.util.List;

public interface CarLoadingService {
    List<CarLoadDto> loadCars(StoreDto store, List<RoutePointDto> routePoints);
}
