package com.goodspartner.dto;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class CarRouteComposition {

    private Car car;

    private List<RoutePoint> routePoints;

}
