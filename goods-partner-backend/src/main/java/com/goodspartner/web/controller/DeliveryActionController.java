package com.goodspartner.web.controller;

import com.goodspartner.action.DeliveryAction;
import com.goodspartner.action.RouteAction;
import com.goodspartner.action.RoutePointAction;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import com.goodspartner.web.controller.response.RouteActionResponse;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.RouteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/deliveries", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryActionController {

    private final DeliveryService deliveryService;
    private final RouteService routeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/{id}/{action}")
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

    /**
     * Delivery Routes manipulation
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping("/{id}/calculate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "Calculate routes by DeliveryDto",
            notes = "Return DeliveryDto",
            response = DeliveryDto.class)
    public DeliveryDto calculateRoutes(@RequestBody DeliveryDto deliveryDto) {
        return deliveryService.calculateDelivery(deliveryDto);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PutMapping("/{id}/routes/{routeId}/reorder")
    public void reorderRoutePoints(@PathVariable("id") UUID deliveryId, @PathVariable int routeId,
                                   @RequestBody LinkedList<RoutePoint> routePoints) {
        routeService.reorderRoutePoints(deliveryId, routeId, routePoints);
    }

}
