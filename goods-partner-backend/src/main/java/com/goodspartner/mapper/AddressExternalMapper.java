package com.goodspartner.mapper;

import com.goodspartner.dto.AddressExternalDto;
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
    AddressExternalDto toAddressExternalDto(AddressExternal addressExternal);

    @Mapping(target = "orderAddressId.orderAddress", source = "orderAddress")
    @Mapping(target = "orderAddressId.clientName", source = "clientName")
    @Mapping(target = "validAddress", source = "mapPoint.address")
    @Mapping(target = "latitude", source = "mapPoint.latitude")
    @Mapping(target = "longitude", source = "mapPoint.longitude")
    @Mapping(target = "status", source = "mapPoint.status")
    AddressExternal toAddressExternal(AddressExternalDto addressExternalDto);

    @Mapping(target = "orderAddressId", ignore = true)
    @Mapping(target = "validAddress", source = "mapPoint.address")
    @Mapping(target = "latitude", source = "mapPoint.latitude")
    @Mapping(target = "longitude", source = "mapPoint.longitude")
    @Mapping(target = "status", source = "mapPoint.status")
    AddressExternal update(@MappingTarget AddressExternal addressExternal, AddressExternalDto addressExternalDto);
}
