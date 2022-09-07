package com.goodspartner.service;

import com.goodspartner.dto.DistanceMatrix;
import com.goodspartner.dto.MapPoint;
import com.graphhopper.ResponsePath;

import java.util.List;

public interface GraphhopperService {
    public DistanceMatrix getMatrix(List<MapPoint> mapPoints);

    public ResponsePath getRoute(List<MapPoint> mapPoints);
}
