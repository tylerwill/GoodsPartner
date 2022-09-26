package com.goodspartner.service;

import com.goodspartner.dto.VRPSolution;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.dto.StoreDto;

import java.util.List;

public interface VRPSolver {

    List<VRPSolution> optimize(List<Car> cars, StoreDto storeDto, List<RoutePoint> routePoints);

}
