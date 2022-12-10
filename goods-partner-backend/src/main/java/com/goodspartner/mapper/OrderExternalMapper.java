package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.OrderExternal;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderExternalMapper {

    @Mapping(target = "mapPoint", source = "addressExternal", qualifiedByName = "mapMapPoint")
    @Mapping(target = "address", source = "addressExternal.orderAddressId.orderAddress")
    @Mapping(target = "clientName", source = "addressExternal.orderAddressId.clientName")
    @Mapping(target = "deliveryId", source = "delivery.id")
    @Mapping(target = "frozen", source = "frozen")
    OrderDto toOrderDto(OrderExternal orderExternal);

    List<OrderDto> toOrderDtosList(List<OrderExternal> orderDtos);

    @Mapping(target = "addressExternal.orderAddressId.orderAddress", source = "address")
    @Mapping(target = "addressExternal.orderAddressId.clientName", source = "clientName")
    @Mapping(target = "addressExternal.validAddress", source = "mapPoint.address")
    @Mapping(target = "addressExternal.latitude", source = "mapPoint.latitude")
    @Mapping(target = "addressExternal.longitude", source = "mapPoint.longitude")
    @Mapping(target = "delivery.id", source = "deliveryId")
    @Mapping(target = "frozen", source = "frozen")
    OrderExternal toOrderExternal(OrderDto orderDto);

    List<OrderExternal> toOrderExternalList(List<OrderDto> orderDtos);

    @BeanMapping(ignoreByDefault = true)
    // Address related
    @Mapping(target = "addressExternal.validAddress", source = "mapPoint.address")
    @Mapping(target = "addressExternal.latitude", source = "mapPoint.latitude")
    @Mapping(target = "addressExternal.longitude", source = "mapPoint.longitude")
    // Other
    @Mapping(target = "deliveryType", source = "deliveryType")
    @Mapping(target = "deliveryStart", source = "deliveryStart")
    @Mapping(target = "deliveryFinish", source = "deliveryFinish")
    @Mapping(target = "excluded", source = "excluded")
    @Mapping(target = "dropped", source = "dropped")
    @Mapping(target = "frozen", source = "frozen")
    void update(@MappingTarget OrderExternal order, OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "excluded", ignore = true)
    @Mapping(target = "dropped", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "carLoad", ignore = true)
    @Mapping(target = "routePoint", ignore = true)
    OrderExternal copyNew(OrderExternal orderExternal);

    @Named("mapMapPoint")
    default MapPoint mapMapPoint(AddressExternal addressExternal) {
        return MapPoint.builder()
                .address(addressExternal.getValidAddress())
                .latitude(addressExternal.getLatitude())
                .longitude(addressExternal.getLongitude())
                .status(addressExternal.getStatus())
                .build();
    }
}