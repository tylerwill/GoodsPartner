package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    public abstract OrderDto toOrderDto(ODataOrderDto oDataOrderDto);

    public abstract List<OrderDto> toOrderDtosList(List<ODataOrderDto> oDataOrderDtos);

}
