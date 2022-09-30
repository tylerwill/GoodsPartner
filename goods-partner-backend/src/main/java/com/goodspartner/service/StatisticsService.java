package com.goodspartner.service;

import com.goodspartner.web.controller.response.statistics.CarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.DailyCarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.StatisticsCalculation;

import java.time.LocalDate;

public interface StatisticsService {

    StatisticsCalculation getStatistics(LocalDate startDate, LocalDate finishDate);

    CarStatisticsCalculation getCarStatistics(LocalDate startDate, LocalDate finishDate, int carId);

    DailyCarStatisticsCalculation getDailyCarStatistics(LocalDate date, int carId);
}
