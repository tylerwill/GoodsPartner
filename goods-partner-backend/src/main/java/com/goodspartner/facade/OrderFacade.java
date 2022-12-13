package com.goodspartner.facade;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;

public interface OrderFacade {

    void processOrdersForDeliveryAsync(Delivery delivery);

    OrderExternal update(long id, OrderDto orderDto);

}
