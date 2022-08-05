package com.goods.partner.service;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;

import java.util.List;

public interface GoogleApiService {
    DirectionsRoute getDirectionRoute(String startPoint, List<String> pointsAddresses);

    DistanceMatrix getDistanceMatrix(List<String> pointsAddresses);
}
