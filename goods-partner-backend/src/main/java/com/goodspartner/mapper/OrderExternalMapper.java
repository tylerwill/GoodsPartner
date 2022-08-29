package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.OrderExternal;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderExternalMapper {

    OrderExternal mapOrderDtoToOrderExternal(OrderDto orderDto);

    List<OrderExternal> mapOrderDtosToOrderExternal(List<OrderDto> orderDtos);
}