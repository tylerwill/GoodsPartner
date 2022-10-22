package com.goodspartner.service.dto;

import com.goodspartner.entity.RoutePoint;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VRPSolution {

    private List<RoutingSolution> routings;

    private List<RoutePoint> droppedPoints;

}
