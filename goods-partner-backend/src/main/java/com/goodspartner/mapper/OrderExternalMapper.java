package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.OrderExternal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OrderExternalMapper {

    @Mapping(target = "addressExternal.orderAddressId.orderAddress", source = "address")
    @Mapping(target = "addressExternal.orderAddressId.clientName", source = "clientName")
    @Mapping(target = "addressExternal.validAddress", source = "mapPoint.address")
    @Mapping(target = "addressExternal.latitude", source = "mapPoint.latitude")
    @Mapping(target = "addressExternal.longitude", source = "mapPoint.longitude")
    @Mapping(target = "delivery.id", source = "deliveryID")
    public abstract OrderExternal mapOrderDtoToOrderExternal(OrderDto orderDto);

    @Mapping(target = "mapPoint", source = "addressExternal", qualifiedByName = "mapMapPoint")
    @Mapping(target = "address", source = "addressExternal.orderAddressId.orderAddress")
    @Mapping(target = "clientName", source = "addressExternal.orderAddressId.clientName")
    @Mapping(target = "deliveryID", source = "delivery.id")
    public abstract OrderDto mapOrderExternalToOrderDto(OrderExternal orderExternal);

    public abstract List<OrderExternal> mapOrderDtosToOrderExternal(List<OrderDto> orderDtos);

    public abstract List<OrderDto> mapExternalOrdersToOrderDtos(List<OrderExternal> externalOrders);

    @Named("mapMapPoint")
    MapPoint mapMapPoint(AddressExternal addressExternal) {
        return MapPoint.builder()
                .address(addressExternal.getOrderAddressId().getOrderAddress())
                .latitude(addressExternal.getLatitude())
                .longitude(addressExternal.getLongitude())
                .status(MapPoint.AddressStatus.KNOWN)
                .build();
    }
}