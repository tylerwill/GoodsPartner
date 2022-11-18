package com.goodspartner.facade.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.facade.DeliveryFacade;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.OrderExternalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultDeliveryFacade implements DeliveryFacade {

    private final DeliveryService deliveryService;
    private final OrderExternalService orderService;
    private final EventService eventService;

    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryDto add(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryService.add(deliveryDto);
        orderService.saveOrdersForDelivery(delivery); // Async
        eventService.publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, delivery.getId());
        return deliveryMapper.mapToDto(delivery);
    }


}
