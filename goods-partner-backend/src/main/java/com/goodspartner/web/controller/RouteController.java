package com.goodspartner.web.controller;

import com.goodspartner.dto.RouteDto;
import com.goodspartner.dto.RoutePointDto;
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

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/routes", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @ApiOperation(value = "Find Routes by delivery ID",
            notes = "Provide an delivery ID to look up related routes",
            response = List.class)
    public List<RouteDto> findByDeliveryId(@RequestParam UUID deliveryId) {
        return routeService.findByDeliveryId(deliveryId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PostMapping("/{id}/{action}")
    public RouteActionResponse apply(@PathVariable int id,
                                     @PathVariable String action) {
        return routeService.updateRoute(id, RouteAction.of(action));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PutMapping("/{id}/reorder")
    public void reorderRoutePoints(@PathVariable int id,
                                   @RequestBody LinkedList<RoutePointDto> routePointDtos) {
        routeService.reorderRoutePoints(id, routePointDtos);
    }
}