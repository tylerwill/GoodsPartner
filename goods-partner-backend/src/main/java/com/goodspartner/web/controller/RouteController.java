package com.goodspartner.web.controller;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.service.RouteService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteService routeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping("/calculate")
    public RoutesCalculation calculateRoutes(@RequestParam String date) {
        return routeService.calculateRoutes(LocalDate.parse(date));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping
    public List<RoutesCalculation.RouteDto> getRoutes() {
        return routeService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST')")
    @PostMapping
    public void addRoute(@RequestBody RoutesCalculation.RouteDto routeDto) {
        routeService.add(routeDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PutMapping("{id}")
    public void updateRoute(@PathVariable int id, @RequestBody RoutesCalculation.RouteDto routeDto) {
        routeService.update(id, routeDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PutMapping("{routeId}/route-points/{routePointID}")
    public void updateRoutePoint(@PathVariable int routeId, @PathVariable String routePointID, @RequestBody RoutePointDto routePoint) {
        routeService.updatePoint(routeId, routePointID, routePoint);
    }

}