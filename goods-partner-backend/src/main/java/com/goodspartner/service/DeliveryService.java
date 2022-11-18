package com.goodspartner.service;

import com.goodspartner.web.action.DeliveryAction;
import com.goodspartner.dto.CarDeliveryDto;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    /* --- CRUD --- */
    List<DeliveryDto> findAll();

    DeliveryDto findById(UUID id);

    Delivery add(DeliveryDto deliveryDto);

    DeliveryDto delete(UUID id);

    /* --- Data retrieval --- */

    DeliveryDto findByStatusAndDeliveryDate(DeliveryStatus status, LocalDate date);

    List<DeliveryDto> findByStatusAndDeliveryDateBetween(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo);

    /* --- Specific --- */

    DeliveryDto calculateDelivery(UUID id);

    DeliveryActionResponse approve(UUID id, DeliveryAction deliveryAction);

    /* --- Driver related --- */

    List<DeliveryDto> findAll(OAuth2AuthenticationToken authentication);

    CarDeliveryDto findById(UUID id, OAuth2AuthenticationToken authentication);
}
