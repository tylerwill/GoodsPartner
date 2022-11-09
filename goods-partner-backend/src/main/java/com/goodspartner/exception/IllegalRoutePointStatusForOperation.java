package com.goodspartner.exception;

import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RoutePoint;

import java.util.List;

public class IllegalRoutePointStatusForOperation extends RuntimeException {

    private static final String ILLEGAL_STATUS_MESSAGE = "Unable to %s routePoint: %s with status: %s";

    public IllegalRoutePointStatusForOperation(List<RoutePointDto> routePointDtos, String action) {
        super(String.format(ILLEGAL_STATUS_MESSAGE, action, routePointDtos, "not pending"));
    }
}
