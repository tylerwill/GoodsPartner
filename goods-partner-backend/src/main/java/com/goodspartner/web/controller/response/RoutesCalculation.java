package com.goodspartner.web.controller.response;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RouteStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RoutesCalculation {
    private LocalDate date;
    private List<RouteDto> routes;
    private List<CarLoadDto> carLoadDetails;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RouteDto {
        private int id;
        private RouteStatus status;
        private double totalWeight;
        private int totalPoints;
        private int totalOrders;
        private double distance;
        private long estimatedTime;
        private LocalDateTime startTime;
        private LocalDateTime finishTime;
        private long spentTime;
        private String storeName;
        private String storeAddress;
        private boolean optimization;
        private List<RoutePointDto> routePoints;
        private CarDto car;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class CarLoadDto {
        private CarDto car;
        private List<OrderDto> orders;
    }
}