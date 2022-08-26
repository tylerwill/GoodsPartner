package com.goodspartner.service.dto;

import com.goodspartner.dto.OrderDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OrderValidationDto {

    List<OrderDto> validOrders;
    List<OrderDto> invalidOrders;

}
