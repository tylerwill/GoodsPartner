package com.goodspartner.facade;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.web.controller.request.ExcludeOrderRequest;

import java.util.UUID;

public interface OrderFacade {

    void processOrdersForDeliveryAsync(Delivery delivery);

    OrderExternal update(long id, OrderDto orderDto);

    OrderExternal excludeOrder(long id, ExcludeOrderRequest excludeOrderRequest);

    void validateOrdersForDeliveryCalculation(UUID deliveryId);
}
