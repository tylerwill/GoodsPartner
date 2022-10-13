package com.goodspartner.web.controller;

import com.goodspartner.action.DeliveryAction;
import com.goodspartner.action.RouteAction;
import com.goodspartner.action.RoutePointAction;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.RouteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/deliveries", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryActionController {

    private final DeliveryService deliveryService;
    private final RouteService routeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PutMapping("/{id}/{action}")
    @ApiOperation(value = "Approve Delivery",
            notes = "Provide an id to approve delivery")
    public DeliveryActionResponse approve(@ApiParam(value = "ID of Delivery to be approved", required = true)
                                              @PathVariable UUID id,
                                          @PathVariable String action) {
        return deliveryService.approve(id, DeliveryAction.of(action));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PostMapping("/{id}/routes/{routeId}/{action}")
    public RouteActionResponse updateRoute(@PathVariable int routeId,
                                           @PathVariable String action) {
        return routeService.update(routeId, RouteAction.of(action)); // TODO think about method name
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PostMapping("/{id}/routes/{routeId}/route-points/{routePointId}/{action}")
    public RoutePointActionResponse updateRoutePoint(@PathVariable int routeId,
                                                     @PathVariable UUID routePointId,
                                                     @PathVariable String action) {
        return routeService.updatePoint(routeId, routePointId, RoutePointAction.of(action)); // TODO think about method name
    }


}
