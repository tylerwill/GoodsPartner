package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ODataOrderMapper {

    OrderDto toOrderDto(ODataOrderDto oDataOrderDto);

    List<OrderDto> toOrderDtosList(List<ODataOrderDto> oDataOrderDtos);

}
