package com.goodspartner.service;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.VRPSolution;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;

import java.util.List;

public interface VRPSolver {

    List<VRPSolution> optimize(List<Car> cars, MapPoint storeMapPoint, List<RoutePoint> routePoints);

}
