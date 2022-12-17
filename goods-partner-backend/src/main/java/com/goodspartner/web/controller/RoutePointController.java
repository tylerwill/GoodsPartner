package com.goodspartner.web.controller;

import com.goodspartner.dto.Coordinates;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.web.action.RoutePointAction;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/route-points", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoutePointController {

    private final RoutePointService routePointService;

    private final OrderExternalMapper orderExternalMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @PostMapping("/{routePointId}/{action}")
    public RoutePointActionResponse apply(@PathVariable long routePointId,
                                          @PathVariable String action) {
        return routePointService.updateRoutePoint(routePointId, RoutePointAction.of(action));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping("/{routePointId}/orders")
    @ApiOperation(value = "Find Orders by RoutePoint ID",
            notes = "Provide an RoutePoint ID to look up related orders",
            response = List.class)
    public List<OrderDto> findByRoutePointId(@PathVariable(value = "routePointId") long routePointId) {
        return routePointService.getRoutePointOrders(routePointId)
                .stream()
                .map(orderExternalMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('DRIVER')")
    @PutMapping("/{routePointId}/coordinates")
    @ApiOperation(value = "Update coordinates by RoutePoint ID",
            notes = "Provide an RoutePoint ID and Coordinates for update client coordinates")
    public void updateCoordinates(@ApiParam(value = "ID value for the RoutePoint you need to retrieve", required = true)
                                  @PathVariable(value = "routePointId") long routePointId,
                                  @ApiParam(value = "Client Coordinates", type = "Coordinates", required = true)
                                  @RequestBody Coordinates coordinates) {
        routePointService.updateCoordinates(routePointId, coordinates);
    }

}
