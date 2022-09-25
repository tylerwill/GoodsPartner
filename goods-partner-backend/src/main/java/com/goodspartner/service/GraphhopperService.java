package com.goodspartner.service;

import com.goodspartner.dto.DistanceMatrix;
import com.goodspartner.dto.MapPoint;
import com.graphhopper.ResponsePath;

import java.util.List;

public interface GraphhopperService {
    DistanceMatrix getMatrix(List<MapPoint> mapPoints);

    ResponsePath getRoute(List<MapPoint> mapPoints);
}
