package com.goodspartner.service;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Store;
import com.goodspartner.service.dto.DistanceMatrix;
import com.graphhopper.ResponsePath;

import java.util.List;

public interface GraphhopperService {

    DistanceMatrix getMatrix(List<MapPoint> mapPoints);

    ResponsePath getRoute(List<MapPoint> mapPoints);

    void routePointTimeActualize(MapPoint mapPoint, List<RoutePoint> routePoints);

    void routePointTimeActualize(Store store, List<RoutePoint> routePoints);

    void checkDeliveryTimeRange(RoutePoint routePoint);
}
