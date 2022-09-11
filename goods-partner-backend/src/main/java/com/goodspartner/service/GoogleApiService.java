package com.goodspartner.service;

import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;

import java.util.List;

public interface GoogleApiService {
    DirectionsRoute getDirectionRoute(String startPoint, List<String> pointsAddresses);

    DistanceMatrix getDistanceMatrix(List<String> pointsAddresses);

    GeocodingResult[] getGeocodingResults(String address);
}
