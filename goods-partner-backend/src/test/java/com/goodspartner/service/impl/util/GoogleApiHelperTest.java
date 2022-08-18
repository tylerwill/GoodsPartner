package com.goodspartner.service.impl.util;

import com.goodspartner.dto.RoutePointDto;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class GoogleApiHelperTest {
    private final GoogleApiHelper googleApiHelper = new GoogleApiHelper();
    private final DirectionsLeg[] legs = new DirectionsLeg[4];
    private DirectionsRoute route;

    @BeforeEach
    public void before() {
        route = new DirectionsRoute();
    }

    @Test
    @DisplayName("Check getRouteTotalDistance Correctly Calculates The Total Distance Of The Route")
    public void testGetRouteTotalDistance() {

        // prepare
        int count = 2;
        for (int i = 0; i < legs.length; i++) {
            DirectionsLeg directionsLeg = new DirectionsLeg();
            directionsLeg.distance = new Distance();
            directionsLeg.distance.inMeters = 150L * count;
            legs[i] = directionsLeg;
            count++;
        }
        route.legs = legs;

        //when
        double actualRoadTotalDistance = googleApiHelper.getRouteTotalDistance(route);

        //then
        Assertions.assertEquals(2.100, actualRoadTotalDistance);
    }

    @Test
    @DisplayName("Check getRouteTotalTime Correctly Calculates The Total Time Of The Route ")
    public void testGetRouteTotalTime() {

        //prepare
        int count = 2;
        for (int i = 0; i < legs.length; i++) {
            DirectionsLeg directionsLeg = new DirectionsLeg();
            directionsLeg.duration = new Duration();
            directionsLeg.duration.inSeconds = 2700L * count;
            legs[i] = directionsLeg;
            count++;
        }
        route.legs = legs;

        //when
        long actualRouteTotalTime = googleApiHelper.getRouteTotalTime(route);

        //then
        Assertions.assertEquals(37800.0, actualRouteTotalTime);
    }

    @Test
    @DisplayName("Check Filling routPointDistantTime Field Of RoutePointDto From Route")
    public void testAddDurationTORoutingPointDto() {

        //prepare
        List<RoutePointDto> routePoints = new ArrayList<>(3);

        for (int i = 0; i < legs.length; i++) {
            DirectionsLeg directionsLeg = new DirectionsLeg();
            directionsLeg.duration = new Duration();
            directionsLeg.duration.inSeconds = 600 + i * 300L;
            legs[i] = directionsLeg;
        }
        route.legs = legs;

        for (int i = 0; i < legs.length - 1; i++) {
            routePoints.add(RoutePointDto.builder().clientId(i + 1).build());
        }

        //when
        googleApiHelper.addRoutPointDistantTime(routePoints, route);

        //then
        for (int i = 0; i < routePoints.size(); i++) {
            Assertions.assertEquals(java.time.Duration.ofSeconds(600 + i * 300L).toMinutes(),
                    routePoints.get(i).getRoutePointDistantTime());
        }
    }
}