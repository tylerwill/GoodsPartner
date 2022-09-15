package com.goodspartner.service;

import com.goodspartner.dto.DeliveryDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    void add(DeliveryDto deliveryDto);

    void update(UUID id, DeliveryDto deliveryDto);

    DeliveryDto findById(UUID id);

    DeliveryDto findByDeliveryDate(LocalDate date);

    List<DeliveryDto> findAll();

    void delete(UUID id);
}
