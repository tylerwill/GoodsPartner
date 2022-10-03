package com.goodspartner.web.controller.response.statistics;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.RouteDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class CarStatisticsCalculation {

    private List<RouteDto> routes;
    private long totalTimeInRoutes;
    private int orderCount;
    private double weight;
    private int fuelConsumption;
    private int incompleteRoutePointsCount;
    private CarDto car;
}
