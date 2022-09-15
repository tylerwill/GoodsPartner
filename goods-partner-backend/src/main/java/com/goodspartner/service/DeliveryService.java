package com.goodspartner.service;

import com.goodspartner.dto.DeliveryDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto add(DeliveryDto deliveryDto);

    DeliveryDto update(UUID id, DeliveryDto deliveryDto);

    DeliveryDto findById(UUID id);

    DeliveryDto findByDeliveryDate(LocalDate date);

    List<DeliveryDto> findAll();

    DeliveryDto delete(UUID id);
}
