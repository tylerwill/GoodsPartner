package com.goodspartner.service.google;

import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.service.GraphhopperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@AllArgsConstructor
public class GoogleSolverFactory {

    // Props
    protected final ClientRoutingProperties clientRoutingProperties;
    // Services
    protected final GraphhopperService graphhopperService;

    public GoogleTSPSolver getTSPSolver(LocalTime routeStartTime, LocalTime routeFinishTime) {
        return new GoogleTSPSolver(clientRoutingProperties, graphhopperService, routeStartTime, routeFinishTime);
    }

    public GoogleVRPSolver getVRPSolver() {
        return new GoogleVRPSolver(clientRoutingProperties, graphhopperService);
    }

}
