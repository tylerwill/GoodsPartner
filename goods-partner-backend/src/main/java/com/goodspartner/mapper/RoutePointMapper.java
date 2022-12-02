package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class})

public interface RoutePointMapper {

    List<RoutePoint> mapToEntities(List<RoutePointDto> routePointDtos);

    List<RoutePointDto> mapToDtos(List<RoutePoint> routePoints);

    @Mapping(target = "mapPoint", source = "routePoint.addressExternal", qualifiedByName = "getMapPoint")
    RoutePointDto mapToDto(RoutePoint routePoint);

    @Named("getMapPoint")
    default MapPoint getMapPoint(AddressExternal addressExternal) {
        return MapPoint.builder()
                .address(addressExternal.getValidAddress())
                .latitude(addressExternal.getLatitude())
                .longitude(addressExternal.getLongitude())
                .status(addressExternal.getStatus())
                .build();
    }

    default List<MapPoint> getMapPoints(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(RoutePoint::getAddressExternal)
                .map(addressExternal -> MapPoint.builder()
                        .status(addressExternal.getStatus())
                        .address(addressExternal.getValidAddress())
                        .longitude(addressExternal.getLongitude())
                        .latitude(addressExternal.getLatitude())
                        .build())
                .collect(Collectors.toList());
    }
}