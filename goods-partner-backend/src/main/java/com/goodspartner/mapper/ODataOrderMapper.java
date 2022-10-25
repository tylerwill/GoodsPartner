package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalTime.class})
public interface ODataOrderMapper {

    // With Defaults // TODO move to properties and more graceful enrichment
    @Mapping(target = "deliveryStart", expression = "java(LocalTime.of(9, 0))")
    @Mapping(target = "deliveryFinish", expression = "java(LocalTime.of(18, 0))")
    @Mapping(target = "deliveryType", expression = "java(com.goodspartner.entity.DeliveryType.REGULAR)")
    OrderDto toOrderDto(ODataOrderDto oDataOrderDto);

    List<OrderDto> toOrderDtosList(List<ODataOrderDto> oDataOrderDtos);

}
