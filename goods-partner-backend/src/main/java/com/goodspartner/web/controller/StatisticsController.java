package com.goodspartner.web.controller;

import com.goodspartner.service.StatisticsService;
import com.goodspartner.web.controller.response.statistics.CarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.DailyCarStatisticsCalculation;
import com.goodspartner.web.controller.response.statistics.StatisticsCalculation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/statistics/deliveries")
    @ApiOperation(value = "Get statistics for date range",
            notes = "Return StatisticsCalculation object",
            response = StatisticsCalculation.class)

    public StatisticsCalculation getStatistics(
            @ApiParam(value = "date range start", required = true)
            @RequestParam String rangeStartDate,
            @ApiParam(value = "date range finish", required = true)
            @RequestParam String rangeFinishDate) {

        LocalDate startDate = LocalDate.parse(rangeStartDate);
        LocalDate finishDate = LocalDate.parse(rangeFinishDate);

        return statisticsService.getStatistics(startDate, finishDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/statistics/cars/{id}")
    @ApiOperation(value = "Get statistics for date range for selected car",
            notes = "Return CarStatisticsCalculation object",
            response = CarStatisticsCalculation.class)
    public CarStatisticsCalculation getCarStatistics(
            @ApiParam(value = "ID value for the car you need to calculate statistics", required = true)
            @PathVariable int id,
            @ApiParam(value = "Start date for the range you need to calculate statistics", required = true)
            @RequestParam String rangeStartDate,
            @ApiParam(value = "Finish date for the range you need to calculate statistics", required = true)
            @RequestParam String rangeFinishDate) {

        LocalDate startDate = LocalDate.parse(rangeStartDate);
        LocalDate finishDate = LocalDate.parse(rangeFinishDate);

        return statisticsService.getCarStatistics(startDate, finishDate, id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @GetMapping("/daily-statistics/cars/{id}")
    @ApiOperation(value = "Get statistics for date for selected car",
            notes = "Return DailyCarStatisticsCalculation object",
            response = CarStatisticsCalculation.class)
    public DailyCarStatisticsCalculation getCarDailyStatistics(
            @ApiParam(value = "ID value for the car you need to calculate statistics", required = true)
            @PathVariable int id,
            @ApiParam(value = "Date you need to calculate statistics for", required = true)
            @RequestParam String date) {

        return statisticsService.getDailyCarStatistics(LocalDate.parse(date), id);
    }
}
