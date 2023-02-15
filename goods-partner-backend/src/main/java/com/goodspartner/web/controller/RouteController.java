package com.goodspartner.web.controller;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.facade.RoutingFacade;
import com.goodspartner.mapper.RouteMapper;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.action.RouteAction;
import com.goodspartner.web.controller.response.RouteActionResponse;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/routes", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {

    private final RouteService routeService;

    private final RoutingFacade routingFacade;

    private final RouteMapper routeMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @ApiOperation(value = "Find Routes by delivery ID",
            notes = "Provide an delivery ID to look up related routes",
            response = List.class)
    public List<RouteDto> findByDeliveryId(@RequestParam UUID deliveryId) {
        return routeService.findRelatedRoutesByDeliveryId(deliveryId)
                .stream()
                .map(routeMapper::toRouteDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @PostMapping("/{id}/{action}")
    public RouteActionResponse apply(@PathVariable long id,
                                     @PathVariable String action) {
        return routingFacade.updateRoute(id, RouteAction.of(action));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PutMapping("/{routeId}/reorder")
    public void reorderRoutePoints(@PathVariable long routeId,
                                   @RequestBody LinkedList<RoutePointDto> routePointDtos) {
        routeService.reorderRoutePoints(routeId, routePointDtos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PostMapping("/{routeId}/recalculate}")
    public void recalculate(@PathVariable long routeId) {
        routeService.recalculateRoute(routeId);
    }
}
