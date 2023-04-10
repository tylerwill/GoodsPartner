package com.goodspartner.service;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    /* --- CRUD --- */
    List<Delivery> findAll();

    Delivery findById(UUID id);

    Delivery add(DeliveryDto deliveryDto);

    void update(Delivery delivery);

    Delivery delete(UUID id);

    /* --- Data retrieval --- */

    // TOTO revisit why DTO response required
    DeliveryDto findByStatusAndDeliveryDate(DeliveryStatus status, LocalDate date);

    List<DeliveryDto> findByStatusAndDeliveryDateBetween(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo);

    /* --- Modification --- */
    Delivery processDeliveryStatus(Route route);

    Delivery cleanupDeliveryForOrdersSync(UUID deliveryId);
}
