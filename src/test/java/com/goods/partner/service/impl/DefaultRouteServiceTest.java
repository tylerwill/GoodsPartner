package com.goods.partner.service.impl;

import com.goods.partner.dto.RoutePointDto;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

class DefaultRouteServiceTest {

    private final DirectionsRoute route = new DirectionsRoute();
    private final DirectionsLeg[] legs = new DirectionsLeg[4];
    private final List<RoutePointDto> routePoints = new ArrayList<>(3);
    private final DefaultRouteService routeService = new DefaultRouteService(null);

    @BeforeEach
    public void setUp() {
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
    }

    @Test
    @DisplayName("Check filling routPointDistantTime field of RoutePointDto from Route")
    public void testAddDurationTORoutingPointDto() {
        routeService.addRoutPointDistantTime(routePoints, route);
        for (int i = 0; i < routePoints.size(); i++) {
            assertEquals(java.time.Duration.ofSeconds(600 + i * 300L), routePoints.get(i).getRoutePointDistantTime());
        }
    }
}