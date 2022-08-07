package com.goodspartner.service;

import com.goodspartner.dto.CarRoutesDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;

import java.util.List;

public interface CarLoadingService {
    List<CarRoutesDto> loadCars(StoreDto storeDto, List<RoutePointDto> routePoints);
}
