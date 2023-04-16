package com.goodspartner.mapper;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressExternalMapper {
    @Mapping(target = "orderAddress", source = "addressExternal.orderAddressId.orderAddress")
    @Mapping(target = "clientName", source = "addressExternal.orderAddressId.clientName")
    @Mapping(target = "mapPoint.address", source = "validAddress")
    @Mapping(target = "mapPoint.latitude", source = "latitude")
    @Mapping(target = "mapPoint.longitude", source = "longitude")
    @Mapping(target = "mapPoint.status", source = "status")
    @Mapping(target = "mapPoint.serviceTimeMinutes", source = "serviceTimeMinutes")
    AddressExternalDto toAddressExternalDto(AddressExternal addressExternal);

    @Mapping(target = "validAddress", source = "address")
    AddressExternal update(@MappingTarget AddressExternal addressExternal, MapPoint mapPoint);

    @Mapping(target = "orderAddressId.orderAddress", source = "address")
    @Mapping(target = "orderAddressId.clientName", source = "clientName")
    @Mapping(target = "validAddress", source = "mapPoint.address")
    @Mapping(target = "latitude", source = "mapPoint.latitude")
    @Mapping(target = "longitude", source = "mapPoint.longitude")
    @Mapping(target = "status", source = "mapPoint.status")
    @Mapping(target = "serviceTimeMinutes", source = "mapPoint.serviceTimeMinutes")
    AddressExternal mapToAddressExternal(OrderDto orderDto);
}
