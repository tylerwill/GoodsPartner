package com.goodspartner.service.util;

import com.goodspartner.dto.OrderDto;

import java.util.List;

public interface ExternalOrderPostProcessor {
    void processOrderComments(List<OrderDto> orderDtos);
}