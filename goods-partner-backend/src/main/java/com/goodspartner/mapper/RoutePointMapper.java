package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

import static com.goodspartner.entity.AddressStatus.UNKNOWN;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class})

public interface RoutePointMapper {

    List<RoutePoint> toRoutePointList(List<RoutePointDto> routePointDtos);

    List<RoutePointDto> toRoutePointDtosList(List<RoutePoint> routePoints);

    RoutePointDto toRoutePointDto(RoutePoint routePoint);

    @Named("getMapPoint")
    default MapPoint getMapPoint(AddressExternal addressExternal) {
        return MapPoint.builder()
                .address(addressExternal.getValidAddress())
                .latitude(addressExternal.getLatitude())
                .longitude(addressExternal.getLongitude())
                .status(addressExternal.getStatus())
                .build();
    }

    default List<MapPoint> toMapPoints(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(RoutePoint::getMapPoint)
                .collect(Collectors.toList());
    }

    default MapPoint toMapPoint(RoutePoint routePoint) {
        MapPoint mapPoint = routePoint.getMapPoint();
        return MapPoint.builder()
                .status(mapPoint.getStatus())
                .address(mapPoint.getAddress())
                .longitude(mapPoint.getLongitude())
                .latitude(mapPoint.getLatitude())
                .build();
    }

    default MapPoint getUnknownMapPoint() {
        return MapPoint.builder()
                .status(UNKNOWN)
                .build();
    }
}