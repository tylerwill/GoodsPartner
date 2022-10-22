package com.goodspartner.service.dto;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class RoutingSolution {

    private Car car;

    private List<RoutePoint> routePoints;

}
