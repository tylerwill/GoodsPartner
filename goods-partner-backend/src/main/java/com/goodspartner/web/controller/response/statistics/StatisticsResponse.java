package com.goodspartner.web.controller.response.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Builder
@Getter
@Setter
public class StatisticsResponse {

    private int routeCount;
    private int orderCount;
    private double weight;
    private int fuelConsumption;
    private long averageDeliveryDuration;

    // Plots. Polymorphic value data
    private Map<LocalDate, Integer> routesForPeriodPerDay;
    private Map<LocalDate, Integer> ordersForPeriodPerDay;
    private Map<LocalDate, Integer> weightForPeriodPerDay;
    private Map<LocalDate, Integer> fuelConsumptionForPeriodPerDay;

}
