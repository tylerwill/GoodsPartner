package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class})

public interface RoutePointMapper {

    List<RoutePoint> toRoutePointList(List<RoutePointDto> routePointDtos);

    List<RoutePointDto> toRoutePointDtosList(List<RoutePoint> routePoints);

    RoutePointDto toRoutePointDto(RoutePoint routePoint);

    default List<MapPoint> toMapPoints(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(RoutePoint::getMapPoint)
                .collect(Collectors.toList());
    }

}