package com.goodspartner.service;

import com.goodspartner.dto.DeliveryDto;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto add(DeliveryDto deliveryDto);

    DeliveryDto update(UUID id, DeliveryDto deliveryDto);

    DeliveryDto findById(UUID id);

    List<DeliveryDto> findAll();

    DeliveryDto delete(UUID id);

    DeliveryDto calculateDelivery(UUID deliveryID);

    DeliveryDto reCalculateDelivery(UUID deliveryID);
}
