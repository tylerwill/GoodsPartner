package com.goodspartner.web.controller.response.statistics;

import com.goodspartner.dto.CarDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CarStatisticsResponse {

    //    private List<RouteDto> routes;
    private long totalTimeInRoutes;
    private int orderCount;
    private double weight;
    private int fuelConsumption;
    private int incompleteRoutePointsCount;
    private CarDto car;
}
