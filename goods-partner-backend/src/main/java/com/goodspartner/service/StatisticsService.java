package com.goodspartner.service;

import com.goodspartner.web.controller.response.statistics.CarStatisticsResponse;
import com.goodspartner.web.controller.response.statistics.DailyCarStatisticsResponse;
import com.goodspartner.web.controller.response.statistics.StatisticsResponse;

import java.time.LocalDate;

public interface StatisticsService {

    StatisticsResponse getStatistics(LocalDate startDate, LocalDate finishDate);

    CarStatisticsResponse getCarStatistics(LocalDate startDate, LocalDate finishDate, int carId);

    DailyCarStatisticsResponse getDailyCarStatistics(LocalDate date, int carId);
}
