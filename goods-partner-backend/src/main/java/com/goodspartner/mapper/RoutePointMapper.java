package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.RoutePointDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static com.goodspartner.entity.AddressStatus.KNOWN;

@Mapper(componentModel = "spring",
        uses = {RouteMapper.class, OrderExternalMapper.class})

public interface RoutePointMapper {

    List<RoutePoint> toEntities(List<RoutePointDto> routePointDtos);

    List<RoutePointDto> toDtos(List<RoutePoint> routePoints);

    @Mapping(target = "mapPoint", source = "routePoint.orders", qualifiedByName = "getMapPoint")
    RoutePointDto toDto(RoutePoint routePoint);

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