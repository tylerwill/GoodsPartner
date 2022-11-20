package com.goodspartner.facade;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.web.action.DeliveryAction;

import java.util.UUID;

public interface DeliveryFacade {

    Delivery add(DeliveryDto deliveryDto);

    Delivery approve(UUID deliveryId, DeliveryAction deliveryAction);

    Delivery calculateDelivery(UUID deliveryId);


}
