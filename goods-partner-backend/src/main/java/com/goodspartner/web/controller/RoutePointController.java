package com.goodspartner.web.controller;

import com.goodspartner.action.RoutePointAction;
import com.goodspartner.service.RoutePointService;
import com.goodspartner.web.controller.response.RoutePointActionResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/route-points", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoutePointController {

    private final RoutePointService routePointService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @PostMapping("/{id}/{action}")
    public RoutePointActionResponse apply(@PathVariable long id,
                                          @PathVariable String action) {
        return routePointService.updateRoutePoint(id, RoutePointAction.of(action));
    }
}
