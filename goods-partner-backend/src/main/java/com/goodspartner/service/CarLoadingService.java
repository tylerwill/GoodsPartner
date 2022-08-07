package com.goodspartner.service;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.dto.StoreDto;
import lombok.*;

import java.util.List;

public interface CarLoadingService {
    List<CarRoutesDto> loadCars(StoreDto storeDto, List<RoutePointDto> routePoints);

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    class CarRoutesDto {
        private CarDto car;
        private List<RoutePointDto> routePoints;
    }
}
