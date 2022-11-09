package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class})

public interface RoutePointMapper {

    @Mapping(target = "id", ignore = true)
    RoutePoint toRoutePoint(RoutePointDto routePointDto);

    List<RoutePoint> toRoutePointList(List<RoutePointDto> routePointDtoList);

    @Mapping(target = "mapPoint", source = "routePoint.orders", qualifiedByName = "getMapPoint")
    RoutePointDto toRoutePointDtos(RoutePoint routePoint);

    List<RoutePointDto> toRoutePointDtoList(List<RoutePoint> routePoints);

    @Named("getMapPoint")
    default MapPoint getMapPoint(List<OrderExternal> orderExternals) {
        AddressExternal addressExternal = orderExternals.get(0).getAddressExternal();

        return MapPoint.builder()
                .address(addressExternal.getValidAddress())
                .latitude(addressExternal.getLatitude())
                .longitude(addressExternal.getLongitude())
                .status(KNOWN)
                .build();
    }

}