package com.goods.partner.service;

import com.goods.partner.dto.CarLoadDto;
import com.goods.partner.dto.RoutePointDto;
import com.goods.partner.dto.StoreDto;

import java.util.List;

public interface CarLoadingService {
    List<CarLoadDto> loadCars(StoreDto store, List<RoutePointDto> routePoints);
}
