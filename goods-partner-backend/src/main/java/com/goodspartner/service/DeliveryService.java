package com.goodspartner.service;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    /* --- CRUD --- */
    List<Delivery> findAll(OAuth2AuthenticationToken authentication);

    Delivery findById(UUID id);

    Delivery add(DeliveryDto deliveryDto);

    Delivery delete(UUID id);

    /* --- Data retrieval --- */

    // TOTO revisit why DTO response required
    DeliveryDto findByStatusAndDeliveryDate(DeliveryStatus status, LocalDate date);

    List<DeliveryDto> findByStatusAndDeliveryDateBetween(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo);

    Delivery processDeliveryStatus(Route route);
}
