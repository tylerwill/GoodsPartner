package com.goodspartner.service;

import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Store;
import com.goodspartner.service.dto.VRPSolution;

import java.util.List;

public interface VRPSolver {

    VRPSolution optimize(List<Car> cars, Store store, List<RoutePoint> routePoints);

}
