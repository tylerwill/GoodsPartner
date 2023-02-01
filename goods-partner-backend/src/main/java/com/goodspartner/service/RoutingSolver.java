package com.goodspartner.service;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Store;
import com.goodspartner.service.dto.TSPSolution;
import com.goodspartner.service.dto.VRPSolution;

import java.util.List;

public interface RoutingSolver {

    VRPSolution optimizeVRP(List<Car> cars, Store store, List<RoutePoint> routePoints);

    TSPSolution optimizeTSP(Car car, Store store, RoutePoint currentRoutePoint, List<RoutePoint> routePoints);
}
