package com.goodspartner.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RouteStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutesCalculation {
    private LocalDate date;
    private List<RouteDto> routes;
    private List<CarLoadDto> carLoadDetails;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RouteDto {

        private int id;
        private RouteStatus status;
        private double totalWeight;
        private int totalPoints;
        private int totalOrders;
        private double distance;
        private Duration estimatedTime;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalDateTime startTime;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalDateTime finishTime;
        private Duration spentTime;
        private String storeName;
        private String storeAddress;
        private List<RoutePointDto> routePoints;
        private CarDto car;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class CarLoadDto {
        private CarDto car;
        private List<OrderDto> orders;
    }
}