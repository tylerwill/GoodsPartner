package com.goodspartner.mapper;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.AddressExternal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressExternalMapper {
    @Mapping(target = "orderAddress", source = "addressExternal.orderAddressId.orderAddress")
    @Mapping(target = "clientName", source = "addressExternal.orderAddressId.clientName")
    @Mapping(target = "status", source = "status.status")
    AddressExternalDto toAddressExternalDto(AddressExternal addressExternal);

    @Mapping(target = "orderAddressId.orderAddress", source = "orderAddress")
    @Mapping(target = "orderAddressId.clientName", source = "clientName")
    @Mapping(target = "status", source = "status")
    AddressExternal toAddressExternal(AddressExternalDto addressExternalDto);
}
