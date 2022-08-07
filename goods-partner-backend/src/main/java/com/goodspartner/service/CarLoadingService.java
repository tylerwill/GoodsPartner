package com.goodspartner.service;

import com.goodspartner.dto.CarRoutesDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.factory.Store;

import java.util.List;

public interface CarLoadingService {
    List<CarRoutesDto> loadCars(Store store, List<RoutePointDto> routePoints);
}
