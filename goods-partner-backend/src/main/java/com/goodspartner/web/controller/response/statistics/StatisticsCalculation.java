package com.goodspartner.web.controller.response.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StatisticsCalculation {

    private int routeCount;
    private int orderCount;
    private double weight;
    private int fuelConsumption;
    private long averageDeliveryDuration;

}
