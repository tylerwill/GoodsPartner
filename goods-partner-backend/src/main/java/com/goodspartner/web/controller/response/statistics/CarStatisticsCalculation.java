package com.goodspartner.web.controller.response.statistics;

import com.goodspartner.dto.CarDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CarStatisticsCalculation {

    private int routeCount;
    private int orderCount;
    private double weight;
    private int fuelConsumption;
    private CarDto car;
}
