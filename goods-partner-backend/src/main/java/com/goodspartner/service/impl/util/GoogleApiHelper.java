package com.goodspartner.service.impl.util;

import com.goodspartner.dto.RoutePointDto;
import com.google.maps.model.DirectionsRoute;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleApiHelper {

    public double getRouteTotalDistance(DirectionsRoute route) {
        return BigDecimal.valueOf(Arrays.stream(route.legs).toList().stream()
                        .collect(Collectors.summarizingLong(leg -> leg.distance.inMeters)).getSum() / 1000d)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public long getRouteTotalTime(DirectionsRoute route) {
        return Arrays.stream(route.legs).toList().stream()
                .collect(Collectors.summarizingLong(leg -> leg.duration.inSeconds)).getSum();
    }

    public void addRoutPointDistantTime(List<RoutePointDto> routePoints, DirectionsRoute route) {
        //TODO: Rework on stream
        for (int i = 0; i < routePoints.size(); i++) {
            long duration = route.legs[i].duration.inSeconds;
            routePoints.get(i).setRoutePointDistantTime(Duration.ofSeconds(duration).toMinutes());
        }
    }
}
