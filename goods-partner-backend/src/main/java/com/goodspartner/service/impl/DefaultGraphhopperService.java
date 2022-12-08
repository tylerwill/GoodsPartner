package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.GraphhopperProperties;
import com.goodspartner.service.dto.DistanceMatrix;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.service.GraphhopperService;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.shapes.GHPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DefaultGraphhopperService implements GraphhopperService {

    private final GraphhopperProperties properties;
    private final GraphHopper hopper;

    @Override
    public DistanceMatrix getMatrix(List<MapPoint> mapPoints) {
        Long[][] distanceMatrix = new Long[mapPoints.size()][mapPoints.size()];
        Long[][] durationMatrix = new Long[mapPoints.size()][mapPoints.size()];

        for (int i = 0; i < mapPoints.size(); i++) {
            for (int j = 0; j < mapPoints.size(); j++) {
                MapPoint startStop = mapPoints.get(i);
                MapPoint endStop = mapPoints.get(j);
                GHRequest request = new GHRequest(
                        startStop.getLatitude(), startStop.getLongitude(),
                        endStop.getLatitude(), endStop.getLongitude())
                        .setProfile(properties.getProfiles().getVehicle())
                        .setLocale(Locale.UK);

                ResponsePath path = getResponsePath(request);

                distanceMatrix[i][j] = (long) path.getDistance();
                durationMatrix[i][j] = Duration.ofMillis(path.getTime()).toMinutes();

            }
        }
        return DistanceMatrix.builder()
                .distance(distanceMatrix)
                .duration(durationMatrix)
                .build();
    }

    public ResponsePath getRoute(List<MapPoint> mapPoints) {
        GHRequest request = new GHRequest();
        mapPoints.forEach(mapPoint -> request.addPoint(new GHPoint(mapPoint.getLatitude(), mapPoint.getLongitude())));
        return getResponsePath(request);
    }

    private ResponsePath getResponsePath(GHRequest request) {
        request.setProfile(properties.getProfiles().getVehicle()).setLocale(Locale.UK);
        GHResponse response = hopper.route(request);

        if (response.hasErrors()) {
            throw new RuntimeException(response.getErrors().toString());
        }

        return response.getBest();
    }

}
