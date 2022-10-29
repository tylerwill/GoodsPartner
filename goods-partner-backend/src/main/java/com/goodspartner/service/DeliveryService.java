package com.goodspartner.service;

import com.goodspartner.action.DeliveryAction;
import com.goodspartner.dto.CarDeliveryDto;
import com.goodspartner.web.controller.response.DeliveryActionResponse;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.DeliveryShortDto;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto add(DeliveryDto deliveryDto);

    DeliveryDto update(UUID id, DeliveryDto deliveryDto);

    DeliveryDto findById(UUID id);

    List<DeliveryShortDto> findAll();

    DeliveryDto delete(UUID id);

    DeliveryDto calculateDelivery(DeliveryDto deliveryDto);

    DeliveryActionResponse approve(UUID id, DeliveryAction deliveryAction);

    // --- Driver related ---

    List<DeliveryShortDto> findAll(OAuth2AuthenticationToken authentication);

    CarDeliveryDto findById(UUID id, OAuth2AuthenticationToken authentication);
}
